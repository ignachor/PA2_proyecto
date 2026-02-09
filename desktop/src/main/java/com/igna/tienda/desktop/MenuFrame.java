package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.enums.CategoriaProducto;
import com.igna.tienda.infra.services.AdminServiceTx;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.infra.services.CarritoServiceTx;
import com.igna.tienda.infra.services.PedidoServiceTx;
import com.igna.tienda.infra.services.ProductoServiceTx;
import com.igna.tienda.infra.services.UsuarioServiceTx;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuFrame extends JFrame {

    private final AuthServiceTx authTx;
    private final UsuarioServiceTx usuarioTx;
    private final AdminServiceTx adminTx;
    private final ProductoServiceTx productoTx;
    private final CarritoServiceTx carritoTx;
    private final PedidoServiceTx pedidoTx;
    private Usuario usuarioActual;

    private final JTextField buscarField = ModernTheme.createTextField(26);
    private final JButton buscarBtn = ModernTheme.createSecondaryButton("Buscar");
    // Paso 1: combo de categorias usando enum + opcion Todas.
    private final JComboBox<CategoriaFiltroItem> categoriaCombo = new JComboBox<>(buildCategoriaModel());

    private final JTextArea detalleArea = ModernTheme.createTextArea(14, 36);

    private final JButton editarPerfilBtn = ModernTheme.createPrimaryButton("Editar Perfil");
    private final JButton agregarCarritoBtn = ModernTheme.createPrimaryButton("Agregar Producto al Carrito");
    private final JButton verCarritoBtn = ModernTheme.createSecondaryButton("Ver Carrito");
    private final JButton verPedidosBtn = ModernTheme.createSecondaryButton("Ver Pedidos");
    private final JButton cerrarSesionBtn = ModernTheme.createSecondaryButton("Cerrar Sesion");

    // Paso 2: se guarda el producto seleccionado para facilitar alta al carrito.
    private Producto productoSeleccionado;

    public MenuFrame(
            AuthServiceTx authTx,
            UsuarioServiceTx usuarioTx,
            AdminServiceTx adminTx,
            ProductoServiceTx productoTx,
            CarritoServiceTx carritoTx,
            PedidoServiceTx pedidoTx,
            Usuario usuarioActual
    ) {
        super("Tienda - Cliente");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;
        this.adminTx = adminTx;
        this.productoTx = productoTx;
        this.carritoTx = carritoTx;
        this.pedidoTx = pedidoTx;
        this.usuarioActual = usuarioActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(860, 640));
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(buildContent());
        wireEvents();
        cargarCatalogoInicial();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(ModernTheme.BG_PRIMARY);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(ModernTheme.BG_PRIMARY);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.BG_PRIMARY);
        JLabel title = ModernTheme.createTitleLabel("Tienda");
        header.add(title, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerRight.setBackground(ModernTheme.BG_PRIMARY);
        headerRight.add(editarPerfilBtn);
        headerRight.add(cerrarSesionBtn);
        header.add(headerRight, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setBackground(ModernTheme.BG_PRIMARY);
        searchPanel.add(ModernTheme.createLabel("Buscar productos"), BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(ModernTheme.BG_PRIMARY);
        searchRow.add(buscarField, BorderLayout.CENTER);
        searchRow.add(buscarBtn, BorderLayout.EAST);
        searchPanel.add(searchRow, BorderLayout.CENTER);

        JPanel categoriaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        categoriaPanel.setBackground(ModernTheme.BG_PRIMARY);
        categoriaCombo.setFont(ModernTheme.FONT_BODY);
        categoriaCombo.setPreferredSize(new Dimension(230, 40));
        categoriaPanel.add(categoriaCombo);

        top.add(header, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.CENTER);
        top.add(categoriaPanel, BorderLayout.SOUTH);

        detalleArea.setEditable(false);
        detalleArea.setText("Datos producto\n- dato_1\n- ...\n- dato_n");
        JScrollPane scroll = new JScrollPane(detalleArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        bottom.setBackground(ModernTheme.BG_PRIMARY);
        bottom.add(agregarCarritoBtn);
        bottom.add(verCarritoBtn);
        bottom.add(verPedidosBtn);

        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private void wireEvents() {
        buscarBtn.addActionListener(e -> buscarProductoPorNombre());
        buscarField.addActionListener(e -> buscarProductoPorNombre());
        categoriaCombo.addActionListener(e -> filtrarPorCategoria());
        agregarCarritoBtn.addActionListener(e -> agregarProductoAlCarrito());
        verCarritoBtn.addActionListener(e -> abrirCarrito());
        verPedidosBtn.addActionListener(e -> verPedidos());
        editarPerfilBtn.addActionListener(e -> openEditarPerfil());
        cerrarSesionBtn.addActionListener(e -> doLogout());
    }

    private void cargarCatalogoInicial() {
        try {
            // Paso inicial: mostrar catalogo activo cuando abre el menu de cliente.
            List<Producto> productos = productoTx.listarProductosActivos();
            detalleArea.setText(renderListaProductos(productos, "Todas"));
        } catch (RuntimeException ex) {
            detalleArea.setText("No se pudo cargar el catalogo: " + ex.getMessage());
        }
    }

    private void buscarProductoPorNombre() {
        String nombre = buscarField.getText();
        if (nombre == null || nombre.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa un nombre de producto.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Paso 1: busqueda por nombre para cliente (solo productos activos).
            Producto producto = productoTx.buscarProductoPorNombre(nombre.trim());
            productoSeleccionado = producto;

            if (producto == null) {
                detalleArea.setText("Producto no encontrado: " + nombre.trim());
                return;
            }

            detalleArea.setText(renderProducto(producto));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarPorCategoria() {
        CategoriaFiltroItem seleccion = (CategoriaFiltroItem) categoriaCombo.getSelectedItem();
        if (seleccion == null) {
            return;
        }

        try {
            // Paso 1: filtra por categoria seleccionada usando enum.
            List<Producto> productos;
            if (seleccion.esTodas()) {
                productos = productoTx.listarProductosActivos();
            } else {
                productos = productoTx.buscarProductosPorCategoria(seleccion.getCategoria());
            }

            // Paso 2: al mostrar listado se limpia seleccion puntual.
            productoSeleccionado = null;
            detalleArea.setText(renderListaProductos(productos, seleccion.getLabel()));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProductoAlCarrito() {
        try {
            // Paso 1: resolver producto a agregar (seleccionado o por nombre escrito).
            Producto producto = productoSeleccionado;
            if (producto == null) {
                String nombre = buscarField.getText();
                if (nombre == null || nombre.isBlank()) {
                    throw new IllegalArgumentException("Primero busca un producto por nombre");
                }
                producto = productoTx.buscarProductoPorNombre(nombre.trim());
            }
            if (producto == null) {
                throw new IllegalArgumentException("Producto no encontrado o no disponible");
            }

            // Paso 2: pedir cantidad a agregar.
            String cantidadStr = JOptionPane.showInputDialog(this,
                    "Cantidad a agregar:",
                    "Agregar al carrito",
                    JOptionPane.QUESTION_MESSAGE);
            if (cantidadStr == null) {
                return;
            }
            int cantidad = Integer.parseInt(cantidadStr.trim());
            if (cantidad <= 0) {
                throw new IllegalArgumentException("Cantidad invalida");
            }

            // Paso 3: ejecutar CU transaccional de carrito.
            carritoTx.agregarProductoAlCarrito(usuarioActual.getId(), producto.getId(), cantidad);
            productoSeleccionado = producto;
            detalleArea.setText(renderProducto(producto));

            JOptionPane.showMessageDialog(this,
                    "Producto agregado al carrito.",
                    "Carrito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cantidad invalida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirCarrito() {
        // Paso 1: abrir frame de carrito con tabla + total + boton pagar.
        CarritoUsuarioFrame frame = new CarritoUsuarioFrame(this, carritoTx, pedidoTx, usuarioActual);
        frame.setVisible(true);
    }

    private void verPedidos() {
        // Paso 1: abrir frame dedicado para visualizar pedidos y sus estados.
        PedidosUsuarioFrame frame = new PedidosUsuarioFrame(this, pedidoTx, usuarioActual);
        frame.setVisible(true);
    }

    private void openEditarPerfil() {
        EditarPerfilUsuario dialog = new EditarPerfilUsuario(this, usuarioTx, usuarioActual);
        dialog.setVisible(true);

        if (dialog.isUpdated()) {
            usuarioActual = dialog.getUsuarioActualizado();
        }
    }

    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Estas seguro de que deseas cerrar sesion?",
                "Confirmar cierre de sesion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame(authTx, usuarioTx, adminTx, productoTx, carritoTx, pedidoTx).setVisible(true);
        }
    }

    private String renderProducto(Producto p) {
        StringBuilder sb = new StringBuilder();
        sb.append("Producto seleccionado").append('\n');
        sb.append("ID: ").append(p.getId()).append('\n');
        sb.append("Nombre: ").append(p.getNombre()).append('\n');
        sb.append("Descripcion: ").append(p.getDescripcion()).append('\n');
        sb.append("Categoria: ").append(p.getCategoria() == null ? "-" : p.getCategoria().getDescripcion()).append('\n');
        sb.append("Precio unitario: ").append(String.format("%.2f", p.getPrecio())).append('\n');
        sb.append("Cantidad disponible: ").append(p.getCantidad()).append('\n');
        sb.append("Stock: ").append(p.getStock() ? "SI" : "NO");
        return sb.toString();
    }

    private String renderListaProductos(List<Producto> productos, String filtroLabel) {
        if (productos == null || productos.isEmpty()) {
            return "No hay productos para la categoria: " + filtroLabel;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Productos en categoria: ").append(filtroLabel).append('\n');
        sb.append("Cantidad encontrada: ").append(productos.size()).append("\n\n");

        for (Producto p : productos) {
            sb.append("- ").append(p.getNombre())
              .append(" | ").append(p.getCategoria() == null ? "-" : p.getCategoria().getDescripcion())
              .append(" | Precio: ").append(String.format("%.2f", p.getPrecio()))
              .append(" | Stock: ").append(p.getCantidad())
              .append('\n');
        }
        return sb.toString();
    }

    // Paso utilitario: construye el desplegable de categorias a partir del enum.
    private static DefaultComboBoxModel<CategoriaFiltroItem> buildCategoriaModel() {
        List<CategoriaFiltroItem> items = new ArrayList<>();
        items.add(CategoriaFiltroItem.todas());
        for (CategoriaProducto categoria : CategoriaProducto.values()) {
            items.add(CategoriaFiltroItem.deCategoria(categoria));
        }
        return new DefaultComboBoxModel<>(items.toArray(new CategoriaFiltroItem[0]));
    }

    private static final class CategoriaFiltroItem {
        private final String label;
        private final CategoriaProducto categoria;

        private CategoriaFiltroItem(String label, CategoriaProducto categoria) {
            this.label = label;
            this.categoria = categoria;
        }

        static CategoriaFiltroItem todas() {
            return new CategoriaFiltroItem("Todas", null);
        }

        static CategoriaFiltroItem deCategoria(CategoriaProducto categoria) {
            return new CategoriaFiltroItem(categoria.getDescripcion(), categoria);
        }

        boolean esTodas() {
            return categoria == null;
        }

        CategoriaProducto getCategoria() {
            return categoria;
        }

        String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}

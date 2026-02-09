package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Carrito;
import com.igna.tienda.core.domain.DetalleCarrito;
import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.CarritoServiceTx;
import com.igna.tienda.infra.services.PedidoServiceTx;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarritoUsuarioFrame extends JFrame {

    private final CarritoServiceTx carritoTx;
    private final PedidoServiceTx pedidoTx;
    private final Usuario usuarioActual;

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Nombre", "Categoria", "Cantidad", "Precio Unitario", "Precio Total"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(tableModel);
    private final JLabel totalLabel = ModernTheme.createSubtitleLabel("Total: 0.00");
    // Paso 0: snapshot de items para mapear cada fila seleccionada a su item real.
    private final List<DetalleCarrito> itemsActuales = new ArrayList<>();

    private final JButton editarCantidadBtn = ModernTheme.createSecondaryButton("Editar cantidad");
    private final JButton eliminarProductoBtn = ModernTheme.createSecondaryButton("Eliminar producto");
    private final JButton pagarBtn = ModernTheme.createAccentButton("Pagar");
    private final JButton refrescarBtn = ModernTheme.createSecondaryButton("Refrescar");
    private final JButton cerrarBtn = ModernTheme.createSecondaryButton("Cerrar");

    public CarritoUsuarioFrame(Frame owner, CarritoServiceTx carritoTx, PedidoServiceTx pedidoTx, Usuario usuarioActual) {
        super("Carrito");
        this.carritoTx = carritoTx;
        this.pedidoTx = pedidoTx;
        this.usuarioActual = usuarioActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setResizable(false);
        setLocationRelativeTo(owner);

        setContentPane(buildContent());
        wireEvents();
        cargarCarrito();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(ModernTheme.BG_PRIMARY);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.BG_PRIMARY);
        JLabel title = ModernTheme.createTitleLabel("CARRITO");
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(ModernTheme.BG_PRIMARY);
        right.add(editarCantidadBtn);
        right.add(eliminarProductoBtn);
        right.add(pagarBtn);
        right.add(refrescarBtn);
        header.add(right, BorderLayout.EAST);

        ModernTheme.styleTable(tabla);
        JScrollPane tableScroll = new JScrollPane(tabla);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(ModernTheme.BG_PRIMARY);
        footer.add(totalLabel, BorderLayout.EAST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setBackground(ModernTheme.BG_PRIMARY);
        acciones.add(cerrarBtn);
        footer.add(acciones, BorderLayout.WEST);

        root.add(header, BorderLayout.NORTH);
        root.add(tableScroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    private void wireEvents() {
        editarCantidadBtn.addActionListener(e -> editarCantidadSeleccionada());
        eliminarProductoBtn.addActionListener(e -> eliminarProductoSeleccionado());
        pagarBtn.addActionListener(e -> pagarCarrito());
        refrescarBtn.addActionListener(e -> cargarCarrito());
        cerrarBtn.addActionListener(e -> dispose());
    }

    private void cargarCarrito() {
        try {
            // Paso 1: obtener carrito actual del usuario.
            Carrito carrito = carritoTx.obtenerCarrito(usuarioActual.getId());
            tableModel.setRowCount(0);
            itemsActuales.clear();

            BigDecimal total = BigDecimal.ZERO;
            if (carrito != null) {
                List<DetalleCarrito> items = carrito.getItems();
                for (DetalleCarrito item : items) {
                    Producto producto = item.getProducto();
                    if (producto == null) {
                        continue;
                    }

                    // Paso 2: calcular precios por item (unitario y total).
                    BigDecimal precioUnitario = BigDecimal.valueOf(producto.getPrecio());
                    BigDecimal precioTotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
                    total = total.add(precioTotal);

                    tableModel.addRow(new Object[]{
                            producto.getNombre(),
                            producto.getCategoria() == null ? "-" : producto.getCategoria().getDescripcion(),
                            item.getCantidad(),
                            String.format("%.2f", precioUnitario),
                            String.format("%.2f", precioTotal)
                    });
                    itemsActuales.add(item);
                }
            }

            // Paso 3: mostrar suma final de todos los precios totales.
            totalLabel.setText("Total: " + String.format("%.2f", total));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarCantidadSeleccionada() {
        try {
            // Paso 1: validar que el usuario haya seleccionado una fila.
            int row = tabla.getSelectedRow();
            if (row < 0 || row >= itemsActuales.size()) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un producto del carrito para editar.",
                        "Carrito",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            DetalleCarrito item = itemsActuales.get(row);
            if (item.getProducto() == null || item.getProducto().getId() == null) {
                throw new IllegalArgumentException("No se pudo identificar el producto seleccionado");
            }

            // Paso 2: pedir nueva cantidad (0 elimina, >0 modifica).
            String nuevaCantidadStr = JOptionPane.showInputDialog(this,
                    "Nueva cantidad para " + item.getProducto().getNombre() + ":",
                    String.valueOf(item.getCantidad()));
            if (nuevaCantidadStr == null) {
                return;
            }

            int nuevaCantidad = Integer.parseInt(nuevaCantidadStr.trim());
            if (nuevaCantidad < 0) {
                throw new IllegalArgumentException("Cantidad invalida");
            }

            // Paso 3: ejecutar CU transaccional y refrescar.
            carritoTx.modificarCantidadProducto(usuarioActual.getId(), item.getProducto().getId(), nuevaCantidad);
            cargarCarrito();
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

    private void eliminarProductoSeleccionado() {
        try {
            // Paso 1: validar que exista una fila seleccionada.
            int row = tabla.getSelectedRow();
            if (row < 0 || row >= itemsActuales.size()) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un producto del carrito para eliminar.",
                        "Carrito",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            DetalleCarrito item = itemsActuales.get(row);
            if (item.getProducto() == null || item.getProducto().getId() == null) {
                throw new IllegalArgumentException("No se pudo identificar el producto seleccionado");
            }

            // Paso 2: confirmar eliminacion.
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Eliminar " + item.getProducto().getNombre() + " del carrito?",
                    "Confirmar eliminacion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Paso 3: ejecutar CU y refrescar tabla.
            carritoTx.eliminarProductoDelCarrito(usuarioActual.getId(), item.getProducto().getId());
            cargarCarrito();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pagarCarrito() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirmas finalizar la compra y generar el pedido?",
                    "Confirmar pago",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Paso 1: finalizar compra desde carrito y generar pedido.
            Pedido pedido = carritoTx.finalizarCompra(usuarioActual.getId());

            JOptionPane.showMessageDialog(this,
                    "Pedido generado correctamente. ID: " + pedido.getId() + " | Estado: " + pedido.getEstado(),
                    "Compra finalizada",
                    JOptionPane.INFORMATION_MESSAGE);

            // Paso 2: refrescar la vista del carrito luego de pagar.
            cargarCarrito();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

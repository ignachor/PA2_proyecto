package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.AdminServiceTx;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.infra.services.UsuarioServiceTx;

import javax.swing.*;
import java.awt.*;

public class MenuProductoAdminFrame extends JFrame {

    private final AuthServiceTx authTx;
    private final UsuarioServiceTx usuarioTx;
    private final AdminServiceTx adminTx;
    private final Usuario adminActual;
    

    private final JTextField buscarField = ModernTheme.createTextField(24);
    private final JButton buscarBtn = ModernTheme.createSecondaryButton("Buscar");

    private final JComboBox<String> categoriaCombo = new JComboBox<>(new String[] {
            "Todas",
            "Lácteos",
            "Bebidas",
            "Almacén",
            "Congelados",
            "Limpieza",
            "Higiene",
            "Otros"
    });

    private final JTextArea detalleArea = ModernTheme.createTextArea(14, 32);

    private final JButton agregarBtn = ModernTheme.createPrimaryButton("Agregar Producto");
    private final JButton eliminarBtn = ModernTheme.createSecondaryButton("Eliminar Producto");
    private final JButton modificarBtn = ModernTheme.createSecondaryButton("Modificar Producto");
    private final JButton listarBtn = ModernTheme.createSecondaryButton("Listar Productos");

    public MenuProductoAdminFrame(AuthServiceTx authTx, UsuarioServiceTx usuarioTx, AdminServiceTx adminTx, Usuario adminActual) {
        super("Menu Productos - Administrador");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;
        this.adminTx = adminTx;
        this.adminActual = adminActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(760, 520));
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(buildContent());
        wireEvents();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(ModernTheme.BG_PRIMARY);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.BG_PRIMARY);
        JLabel title = ModernTheme.createTitleLabel("MENÚ PRODUCTOS");
        header.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setBackground(ModernTheme.BG_PRIMARY);
        JLabel buscarLabel = ModernTheme.createLabel("Buscar productos");
        searchPanel.add(buscarLabel, BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(ModernTheme.BG_PRIMARY);
        searchRow.add(buscarField, BorderLayout.CENTER);
        searchRow.add(buscarBtn, BorderLayout.EAST);
        searchPanel.add(searchRow, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setBackground(ModernTheme.BG_PRIMARY);
        top.add(header, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.CENTER);

        JPanel categoriaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        categoriaPanel.setBackground(ModernTheme.BG_PRIMARY);
        categoriaCombo.setFont(ModernTheme.FONT_BODY);
        categoriaCombo.setPreferredSize(new Dimension(180, 40));
        categoriaPanel.add(categoriaCombo);
        top.add(categoriaPanel, BorderLayout.SOUTH);

        detalleArea.setEditable(false);
        detalleArea.setText("Datos producto\n- dato_1\n- ...\n- dato_n");
        JScrollPane scroll = new JScrollPane(detalleArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        buttonsRow.setBackground(ModernTheme.BG_PRIMARY);
        buttonsRow.add(agregarBtn);
        buttonsRow.add(eliminarBtn);
        buttonsRow.add(modificarBtn);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setBackground(ModernTheme.BG_PRIMARY);
        bottom.add(listarBtn);

        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(ModernTheme.BG_PRIMARY);
        southWrapper.add(buttonsRow, BorderLayout.NORTH);
        southWrapper.add(bottom, BorderLayout.SOUTH);
        root.add(southWrapper, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        buscarBtn.addActionListener(e -> buscarProducto());
        agregarBtn.addActionListener(e -> agregarProducto());
        eliminarBtn.addActionListener(e -> eliminarProducto());
        modificarBtn.addActionListener(e -> modificarProducto());
        listarBtn.addActionListener(e -> listarProductos());
    }

    private void buscarProducto() {
        String nombreProducto = buscarField.getText();
        if (nombreProducto == null || nombreProducto.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa el nombre del producto a buscar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Producto productoBuscado = adminTx.buscarProducto(nombreProducto.trim());
            if (productoBuscado == null) {
                detalleArea.setText("Producto no encontrado: " + nombreProducto.trim());
                return;
            }
            detalleArea.setText(renderProducto(productoBuscado));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProducto() {
        AgregarProductoAdmin agregarFrame = new AgregarProductoAdmin(this, adminTx);
        agregarFrame.setVisible(true);
    }

    private void eliminarProducto() {
        JOptionPane.showMessageDialog(this,
                "Eliminar producto (pendiente).",
                "Producto",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void modificarProducto() {
        JOptionPane.showMessageDialog(this,
                "Modificar producto (pendiente).",
                "Producto",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void listarProductos() {
        JOptionPane.showMessageDialog(this,
                "Listar productos (pendiente).",
                "Producto",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String renderProducto(Producto p) {
        StringBuilder sb = new StringBuilder();
        sb.append("Producto seleccionado").append('\n');
        sb.append("ID: ").append(p.getId()).append('\n');
        sb.append("Nombre: ").append(p.getNombre()).append('\n');
        sb.append("Descripcion: ").append(p.getDescripcion()).append('\n');
        sb.append("Categoria: ").append(p.getCategoria()).append('\n');
        sb.append("Precio: ").append(p.getPrecio()).append('\n');
        sb.append("Cantidad: ").append(p.getCantidad()).append('\n');
        sb.append("Cantidad minima: ").append(p.getCantidadMinimo()).append('\n');
        sb.append("Fecha vencimiento: ").append(p.getFechaVencimiento()).append('\n');
        sb.append("Stock: ").append(p.getStock() ? "SI" : "NO");
        return sb.toString();
    }
}

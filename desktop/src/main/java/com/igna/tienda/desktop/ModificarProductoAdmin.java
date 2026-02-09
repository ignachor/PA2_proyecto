package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.enums.CategoriaProducto;
import com.igna.tienda.infra.services.AdminServiceTx;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public class ModificarProductoAdmin extends JFrame {

    private final AdminServiceTx adminTx;
    private final Producto productoOriginal;
    private final Consumer<Producto> onProductoActualizado;

    private final JTextField nombreField = ModernTheme.createTextField(22);
    private final JTextArea descripcionArea = ModernTheme.createTextArea(4, 22);
    private final JComboBox<CategoriaProducto> categoriaCombo = new JComboBox<>(CategoriaProducto.values());
    private final JTextField precioField = ModernTheme.createTextField(22);
    private final JTextField cantidadField = ModernTheme.createTextField(22);
    private final JTextField cantidadMinimoField = ModernTheme.createTextField(22);
    private final JTextField fechaVencimientoField = ModernTheme.createTextField(22);
    // IMPLEMENTACION: control de estado para reactivar/desactivar el producto.
    private final JCheckBox stockCheck = new JCheckBox("Producto activo (en stock)");

    private final JButton guardarBtn = ModernTheme.createAccentButton("GUARDAR");
    private final JButton cancelarBtn = ModernTheme.createSecondaryButton("CANCELAR");

    public ModificarProductoAdmin(Frame owner, AdminServiceTx adminTx, Producto productoOriginal, Consumer<Producto> onProductoActualizado) {
        super("Modificar Producto");
        this.adminTx = Objects.requireNonNull(adminTx, "adminTx requerido");
        this.productoOriginal = Objects.requireNonNull(productoOriginal, "productoOriginal requerido");
        this.onProductoActualizado = onProductoActualizado == null ? p -> { } : onProductoActualizado;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(760, 640));
        setResizable(false);
        setContentPane(buildContent());
        wireEvents();
        cargarDatosProducto();
        setLocationRelativeTo(owner);
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ModernTheme.BG_PRIMARY);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 20));
        header.setBackground(ModernTheme.PRIMARY_DARK);
        JLabel title = ModernTheme.createTitleLabel("Modificar Producto");
        title.setForeground(Color.WHITE);
        header.add(title);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.BG_PRIMARY);
        formPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int y = 0;
        addRow(formPanel, c, y++, "Nombre", nombreField);

        JScrollPane descripcionScroll = new JScrollPane(descripcionArea);
        descripcionScroll.setPreferredSize(new Dimension(420, 110));
        addRow(formPanel, c, y++, "Descripcion", descripcionScroll);

        categoriaCombo.setFont(ModernTheme.FONT_BODY);
        categoriaCombo.setPreferredSize(new Dimension(420, 40));
        categoriaCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategoriaProducto categoria) {
                    setText(categoria.getDescripcion());
                }
                return this;
            }
        });
        addRow(formPanel, c, y++, "Categoria", categoriaCombo);

        addRow(formPanel, c, y++, "Precio", precioField);
        addRow(formPanel, c, y++, "Cantidad", cantidadField);
        addRow(formPanel, c, y++, "Cantidad minima", cantidadMinimoField);
        addRow(formPanel, c, y++, "Fecha vencimiento", fechaVencimientoField);

        // IMPLEMENTACION: campo visual para cambiar el boolean stock.
        stockCheck.setFont(ModernTheme.FONT_BODY);
        stockCheck.setBackground(ModernTheme.BG_PRIMARY);
        c.gridx = 1;
        c.gridy = y++;
        formPanel.add(stockCheck, c);

        JLabel hint = ModernTheme.createLabel("Si no es perecedero, deja 0");
        hint.setForeground(ModernTheme.TEXT_SECONDARY);
        c.gridx = 1;
        c.gridy = y;
        formPanel.add(hint, c);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(ModernTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));
        footer.add(cancelarBtn);
        footer.add(guardarBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, Component field) {
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0.0;
        c.gridwidth = 1;
        panel.add(ModernTheme.createLabel(label), c);

        c.gridx = 1;
        c.weightx = 1.0;
        panel.add(field, c);
    }

    private void wireEvents() {
        guardarBtn.addActionListener(e -> guardarCambios());
        cancelarBtn.addActionListener(e -> dispose());
        fechaVencimientoField.addActionListener(e -> guardarCambios());
    }

    private void cargarDatosProducto() {
        // IMPLEMENTACION: precarga de los datos actuales para facilitar la edicion.
        nombreField.setText(productoOriginal.getNombre());
        descripcionArea.setText(productoOriginal.getDescripcion());
        categoriaCombo.setSelectedItem(productoOriginal.getCategoria());
        precioField.setText(String.valueOf(productoOriginal.getPrecio()));
        cantidadField.setText(String.valueOf(productoOriginal.getCantidad()));
        cantidadMinimoField.setText(String.valueOf(productoOriginal.getCantidadMinimo()));
        fechaVencimientoField.setText(String.valueOf(productoOriginal.getFechaVencimiento()));
        stockCheck.setSelected(productoOriginal.getStock());
    }

    private void guardarCambios() {
        try {
            Producto modificado = construirProductoDesdeFormulario();
            setBusy(true);
            adminTx.modificarProducto(modificado);
            onProductoActualizado.accept(modificado);

            JOptionPane.showMessageDialog(this,
                    "Producto modificado correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Datos invalidos",
                    JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo modificar el producto: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            setBusy(false);
        }
    }

    private Producto construirProductoDesdeFormulario() {
        String nombre = requireText(nombreField, "Nombre");
        String descripcion = requireText(descripcionArea, "Descripcion");
        CategoriaProducto categoria = (CategoriaProducto) categoriaCombo.getSelectedItem();
        if (categoria == null) {
            throw new IllegalArgumentException("Selecciona una categoria");
        }

        double precio = parseDouble(precioField.getText(), "Precio");
        int cantidad = parseInt(cantidadField.getText(), "Cantidad");
        int cantidadMinima = parseInt(cantidadMinimoField.getText(), "Cantidad minima");
        int fechaVencimiento = parseInt(fechaVencimientoField.getText(), "Fecha vencimiento");
        boolean stock = stockCheck.isSelected();

        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        if (cantidadMinima < 0) {
            throw new IllegalArgumentException("La cantidad minima no puede ser negativa");
        }
        if (fechaVencimiento < 0) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser negativa");
        }

        // MODIFICACION: se envia id + stock para que el CU ModificarProducto actualice todo.
        return new Producto(
                productoOriginal.getId(),
                nombre,
                descripcion,
                categoria,
                precio,
                cantidad,
                cantidadMinima,
                fechaVencimiento,
                stock
        );
    }

    private String requireText(JTextComponent component, String fieldName) {
        String value = component.getText();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " es obligatorio");
        }
        return value.trim();
    }

    private double parseDouble(String raw, String fieldName) {
        String value = raw == null ? "" : raw.trim().replace(',', '.');
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " es obligatorio");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " invalido");
        }
    }

    private int parseInt(String raw, String fieldName) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " es obligatorio");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " invalido");
        }
    }

    private void setBusy(boolean busy) {
        guardarBtn.setEnabled(!busy);
        cancelarBtn.setEnabled(!busy);
        nombreField.setEnabled(!busy);
        descripcionArea.setEnabled(!busy);
        categoriaCombo.setEnabled(!busy);
        precioField.setEnabled(!busy);
        cantidadField.setEnabled(!busy);
        cantidadMinimoField.setEnabled(!busy);
        fechaVencimientoField.setEnabled(!busy);
        stockCheck.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}

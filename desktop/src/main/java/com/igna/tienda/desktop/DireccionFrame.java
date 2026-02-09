package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.value.Direccion;
import com.igna.tienda.infra.services.UsuarioServiceTx;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class DireccionFrame extends JDialog {

    private final UsuarioServiceTx usuarioTx;
    private Usuario usuarioActual;

    private JTextField calleField;
    private JTextField numeroField;
    private JTextField ciudadField;
    private JTextField provinciaField;
    private JTextField codPostalField;
    private JButton okBtn;
    private JButton cancelBtn;

    private boolean updated = false;

    public DireccionFrame(Frame owner, UsuarioServiceTx usuarioTx, Usuario usuarioActual) {
        super(owner, "Editar Dirección", true);
        this.usuarioTx = Objects.requireNonNull(usuarioTx, "usuarioTx requerido");
        this.usuarioActual = Objects.requireNonNull(usuarioActual, "usuarioActual requerido");

        setMinimumSize(new Dimension(550, 480));
        setLocationRelativeTo(owner);
        setResizable(false);
        setContentPane(buildContent());
        wireEvents();
        cargarDatosUsuario();
    }

    public boolean isUpdated() {
        return updated;
    }

    public Usuario getUsuarioActualizado() {
        return usuarioActual;
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ModernTheme.BG_PRIMARY);

        // Header
        JPanel header = new JPanel();
        header.setBackground(ModernTheme.ACCENT);
        header.setPreferredSize(new Dimension(550, 80));
        header.setLayout(new GridBagLayout());
        
        JLabel titleLabel = ModernTheme.createTitleLabel(" Editar Dirección");
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.BG_PRIMARY);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 10, 5);
        c.weightx = 1.0;

        int y = 0;

        // Calle
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        formPanel.add(ModernTheme.createLabel("Calle"), c);
        c.gridy = y + 1;
        calleField = ModernTheme.createTextField(20);
        formPanel.add(calleField, c);
        y += 2;

        // Número
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        formPanel.add(ModernTheme.createLabel("Número"), c);
        c.gridy = y + 1;
        numeroField = ModernTheme.createTextField(20);
        formPanel.add(numeroField, c);
        y += 2;

        // Ciudad
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        formPanel.add(ModernTheme.createLabel("Ciudad"), c);
        c.gridy = y + 1;
        ciudadField = ModernTheme.createTextField(20);
        formPanel.add(ciudadField, c);
        y += 2;

        // Provincia
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        formPanel.add(ModernTheme.createLabel("Provincia"), c);
        c.gridy = y + 1;
        provinciaField = ModernTheme.createTextField(20);
        formPanel.add(provinciaField, c);
        y += 2;

        // Código Postal
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        formPanel.add(ModernTheme.createLabel("Código Postal"), c);
        c.gridy = y + 1;
        codPostalField = ModernTheme.createTextField(20);
        formPanel.add(codPostalField, c);

        // Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setBackground(ModernTheme.BG_SECONDARY);
        buttons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));
        
        cancelBtn = ModernTheme.createSecondaryButton("CANCELAR");
        okBtn = ModernTheme.createAccentButton("GUARDAR");
        
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        okBtn.addActionListener(e -> doSave());
        cancelBtn.addActionListener(e -> dispose());
        codPostalField.addActionListener(e -> doSave());
    }

    private void cargarDatosUsuario() {
        Direccion d = usuarioActual.getDireccion();
        if (d != null) {
            calleField.setText(d.getCalle());
            numeroField.setText(d.getNumero());
            ciudadField.setText(d.getCiudad());
            provinciaField.setText(d.getProvincia());
            codPostalField.setText(d.getCodigoPostal());
        }
    }

    private void doSave() {
        Direccion nueva = new Direccion(
                calleField.getText().trim(),
                numeroField.getText().trim(),
                ciudadField.getText().trim(),
                provinciaField.getText().trim(),
                codPostalField.getText().trim()
        );

        setBusy(true);
        try {
            Usuario mod = new Usuario(
                    usuarioActual.getId(),
                    usuarioActual.getNombre(),
                    usuarioActual.getApellido(),
                    usuarioActual.getDni(),
                    usuarioActual.getEmail(),
                    nueva,
                    usuarioActual.getPassword(),
                    usuarioActual.getRol()
            );

            usuarioActual = usuarioTx.editarDireccion(mod);
            updated = true;

            JOptionPane.showMessageDialog(this,
                    "✓ Dirección actualizada correctamente",
                    "Actualización exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            setBusy(false);
        }
    }

    private void setBusy(boolean busy) {
        okBtn.setEnabled(!busy);
        cancelBtn.setEnabled(!busy);
        calleField.setEnabled(!busy);
        numeroField.setEnabled(!busy);
        ciudadField.setEnabled(!busy);
        provinciaField.setEnabled(!busy);
        codPostalField.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
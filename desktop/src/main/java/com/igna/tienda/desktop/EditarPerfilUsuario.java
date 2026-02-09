package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.UsuarioServiceTx;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class EditarPerfilUsuario extends JDialog {

    private final UsuarioServiceTx usuarioTx;
    private Usuario usuarioActual;

    private JTextField nombreField;
    private JTextField apellidoField;
    private JTextField dniField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton direccionBtn;
    private JButton okBtn;
    private JButton cancelBtn;

    private boolean updated = false;

    public EditarPerfilUsuario(Frame owner, UsuarioServiceTx usuarioTx, Usuario usuarioActual) {
        super(owner, "Editar Perfil", true);
        this.usuarioTx = Objects.requireNonNull(usuarioTx, "usuarioTx requerido");
        this.usuarioActual = Objects.requireNonNull(usuarioActual, "usuarioActual requerido");

        setMinimumSize(new Dimension(600, 550));
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
        header.setBackground(ModernTheme.PRIMARY);
        header.setPreferredSize(new Dimension(600, 80));
        header.setLayout(new GridBagLayout());
        
        JLabel titleLabel = ModernTheme.createTitleLabel("Editar Perfil");
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

        // Nombre
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        formPanel.add(ModernTheme.createLabel("Nombre *"), c);
        c.gridy = y + 1;
        nombreField = ModernTheme.createTextField(20);
        formPanel.add(nombreField, c);
        y += 2;

        // Apellido
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        formPanel.add(ModernTheme.createLabel("Apellido *"), c);
        c.gridy = y + 1;
        apellidoField = ModernTheme.createTextField(20);
        formPanel.add(apellidoField, c);
        y += 2;

        // DNI (no editable)
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        formPanel.add(ModernTheme.createLabel("DNI (no editable)"), c);
        c.gridy = y + 1;
        dniField = ModernTheme.createTextField(20);
        dniField.setEnabled(false);
        dniField.setBackground(new Color(240, 240, 240));
        formPanel.add(dniField, c);
        y += 2;

        // Email
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        formPanel.add(ModernTheme.createLabel("Email *"), c);
        c.gridy = y + 1;
        emailField = ModernTheme.createTextField(20);
        formPanel.add(emailField, c);
        y += 2;

        // Password (para cambiar email)
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        JLabel passLabel = ModernTheme.createLabel("Contraseña actual (requerida para cambiar email)");
        passLabel.setFont(ModernTheme.FONT_SMALL);
        formPanel.add(passLabel, c);
        c.gridy = y + 1;
        passwordField = ModernTheme.createPasswordField(20);
        formPanel.add(passwordField, c);
        y += 2;

        // Botón de dirección
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        c.insets = new Insets(20, 5, 10, 5);
        direccionBtn = ModernTheme.createAccentButton(" EDITAR DIRECCIÓN");
        direccionBtn.setPreferredSize(new Dimension(250, 40));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(ModernTheme.BG_PRIMARY);
        btnPanel.add(direccionBtn);
        formPanel.add(btnPanel, c);

        // Botones de acción
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setBackground(ModernTheme.BG_SECONDARY);
        buttons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));
        
        cancelBtn = ModernTheme.createSecondaryButton("CANCELAR");
        okBtn = ModernTheme.createPrimaryButton("GUARDAR CAMBIOS");
        
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
        direccionBtn.addActionListener(e -> openDireccion());
        passwordField.addActionListener(e -> doSave());
    }

    private void cargarDatosUsuario() {
        nombreField.setText(usuarioActual.getNombre());
        apellidoField.setText(usuarioActual.getApellido());
        dniField.setText(usuarioActual.getDni());
        emailField.setText(usuarioActual.getEmail());
    }

    private void doSave() {
        String nombre = nombreField.getText().trim();
        String apellido = apellidoField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor completa todos los campos obligatorios",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean emailChanged = !email.equals(usuarioActual.getEmail());
        
        setBusy(true);
        try {
            Usuario modDatos = new Usuario(
                    usuarioActual.getId(),
                    nombre,
                    apellido,
                    usuarioActual.getDni(),
                    usuarioActual.getEmail(),
                    usuarioActual.getDireccion(),
                    usuarioActual.getPassword(),
                    usuarioActual.getRol()
            );
            usuarioActual = usuarioTx.editarDatosPersonales(modDatos);

            if (emailChanged) {
                if (pass.isBlank()) {
                    JOptionPane.showMessageDialog(this,
                            "Para cambiar el email necesitas la contraseña actual",
                            "Contraseña requerida",
                            JOptionPane.WARNING_MESSAGE);
                    setBusy(false);
                    return;
                }

                Usuario modEmail = new Usuario(
                        usuarioActual.getId(),
                        nombre,
                        apellido,
                        usuarioActual.getDni(),
                        email,
                        usuarioActual.getDireccion(),
                        pass,
                        usuarioActual.getRol()
                );
                Usuario result = usuarioTx.editarEmail(modEmail);
                if (result != null) {
                    usuarioActual = result;
                }
            }

            updated = true;
            JOptionPane.showMessageDialog(this,
                    " Perfil actualizado correctamente",
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
        direccionBtn.setEnabled(!busy);
        okBtn.setEnabled(!busy);
        cancelBtn.setEnabled(!busy);
        nombreField.setEnabled(!busy);
        apellidoField.setEnabled(!busy);
        emailField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private void openDireccion() {
        DireccionFrame dialog = new DireccionFrame((Frame) this.getOwner(), usuarioTx, usuarioActual);
        dialog.setVisible(true);

        if (dialog.isUpdated()) {
            usuarioActual = dialog.getUsuarioActualizado();
        }
    }
}
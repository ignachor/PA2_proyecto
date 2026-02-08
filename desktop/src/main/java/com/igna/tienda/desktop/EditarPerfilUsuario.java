package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.UsuarioServiceTx;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class EditarPerfilUsuario extends JDialog {

    private final UsuarioServiceTx usuarioTx;
    private Usuario usuarioActual;

    private final JTextField nombreField = new JTextField(20);
    private final JTextField apellidoField = new JTextField(20);
    private final JTextField emailField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(20);

    private final JButton direccionBtn = new JButton("Editar direccion");
    private final JButton okBtn = new JButton("Guardar cambios");
    private final JButton cancelBtn = new JButton("Cancelar");

    private boolean updated = false;

    public EditarPerfilUsuario(Frame owner, UsuarioServiceTx usuarioTx, Usuario usuarioActual) {
        super(owner, "Editar perfil", true);
        this.usuarioTx = Objects.requireNonNull(usuarioTx, "usuarioTx requerido");
        this.usuarioActual = Objects.requireNonNull(usuarioActual, "usuarioActual requerido");

        setMinimumSize(new Dimension(460, 280));
        setLocationRelativeTo(owner);
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
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        int y = 0;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Nombre:"), c);
        c.gridx = 1;
        form.add(nombreField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Apellido:"), c);
        c.gridx = 1;
        form.add(apellidoField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Email:"), c);
        c.gridx = 1;
        form.add(emailField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Contrasena actual:"), c);
        c.gridx = 1;
        form.add(passwordField, c);

        JPanel hint = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hint.add(new JLabel("Solo requerida si cambias el email."));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(direccionBtn);
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(hint, BorderLayout.NORTH);
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
        emailField.setText(usuarioActual.getEmail());
        passwordField.setText("");
    }

    private void doSave() {
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());

        boolean emailChanged = email != null && !email.equals(usuarioActual.getEmail());

        setBusy(true);
        try {
            Usuario modDatos = new Usuario(
                    usuarioActual.getId(),
                    nombre,
                    apellido,
                    usuarioActual.getEmail(),
                    usuarioActual.getDireccion(),
                    usuarioActual.getPassword(),
                    usuarioActual.getRol()
            );
            usuarioActual = usuarioTx.editarDatosPersonales(modDatos);

            if (emailChanged) {
                if (pass == null || pass.isBlank()) {
                    JOptionPane.showMessageDialog(this,
                            "Para cambiar el email necesitas la contrasena actual.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Usuario modEmail = new Usuario(
                        usuarioActual.getId(),
                        nombre,
                        apellido,
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
                    "Perfil actualizado correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
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
        // Abrimos el dialogo de direccion desde el perfil y pasamos el usuario actual
        DireccionFrame dialog = new DireccionFrame((Frame) this.getOwner(), usuarioTx, usuarioActual);
        dialog.setVisible(true);

        // Si hubo cambios, actualizamos el usuario en memoria
        if (dialog.isUpdated()) {
            usuarioActual = dialog.getUsuarioActualizado();
        }
    }
}

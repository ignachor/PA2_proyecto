package com.igna.tienda.desktop;

import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.UsuarioServiceTx;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthServiceTx authTx;
    private final UsuarioServiceTx usuarioTx;

    private final JTextField emailField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JButton loginBtn = new JButton("Iniciar sesión");
    private final JButton registerBtn = new JButton("Registrarse");
    private final JLabel statusLabel = new JLabel(" ");

    public LoginFrame(AuthServiceTx authTx, UsuarioServiceTx usuarioTx) {
        super("Login - Tienda");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 220));
        setLocationRelativeTo(null);

        setContentPane(buildContent());
        wireEvents();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Email:"), c);
        c.gridx = 1;
        form.add(emailField, c);

        c.gridx = 0; c.gridy = 1;
        form.add(new JLabel("Contraseña:"), c);
        c.gridx = 1;
        form.add(passwordField, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(registerBtn);
        buttons.add(loginBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        statusLabel.setForeground(Color.DARK_GRAY);
        root.add(statusLabel, BorderLayout.NORTH);

        return root;
    }

    private void wireEvents() {
        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> openRegisterDialog());

        // Enter para loguear
        passwordField.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());

        setBusy(true);
        try {
            Usuario u = authTx.iniciarSesion(email, pass);
            statusLabel.setText("Login OK: " + u.getEmail() + " (rol=" + u.getRol() + ")");

            // 1) Con login OK abrimos el menu y le pasamos el usuario logueado
            // 2) Cerramos el login para que no quede abierto detras
            MenuFrame menu = new MenuFrame(authTx, usuarioTx, u);
            menu.setVisible(true);
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

    private void openRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this, authTx);
        dialog.setVisible(true);

        // si registró con éxito, precargá el email para login
        String registeredEmail = dialog.getRegisteredEmail();
        if (registeredEmail != null) {
            emailField.setText(registeredEmail);
            passwordField.setText("");
            statusLabel.setText("Registrado OK. Iniciá sesión.");
            passwordField.requestFocusInWindow();
        }
    }

    private void setBusy(boolean busy) {
        loginBtn.setEnabled(!busy);
        registerBtn.setEnabled(!busy);
        emailField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}

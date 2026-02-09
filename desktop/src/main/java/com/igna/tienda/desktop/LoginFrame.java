package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.infra.services.AdminServiceTx;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.infra.services.CarritoServiceTx;
import com.igna.tienda.infra.services.PedidoServiceTx;
import com.igna.tienda.infra.services.ProductoServiceTx;
import com.igna.tienda.infra.services.UsuarioServiceTx;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthServiceTx authTx;
    private final UsuarioServiceTx usuarioTx;
    private final AdminServiceTx adminTx;
    private final ProductoServiceTx productoTx;
    private final CarritoServiceTx carritoTx;
    private final PedidoServiceTx pedidoTx;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JLabel statusLabel;

    public LoginFrame(AuthServiceTx authTx, UsuarioServiceTx usuarioTx, AdminServiceTx adminTx) {
        this(authTx, usuarioTx, adminTx, null, null, null);
    }

    public LoginFrame(
            AuthServiceTx authTx,
            UsuarioServiceTx usuarioTx,
            AdminServiceTx adminTx,
            ProductoServiceTx productoTx,
            CarritoServiceTx carritoTx,
            PedidoServiceTx pedidoTx
    ) {
        super("Tienda - Iniciar Sesion");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;
        this.adminTx = adminTx;
        this.productoTx = productoTx;
        this.carritoTx = carritoTx;
        this.pedidoTx = pedidoTx;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 600));
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(buildContent());
        wireEvents();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ModernTheme.BG_PRIMARY);

        JPanel header = new JPanel();
        header.setBackground(ModernTheme.PRIMARY);
        header.setPreferredSize(new Dimension(500, 120));
        header.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("BIENVENIDO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(ModernTheme.BG_PRIMARY);
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        JLabel subtitle = ModernTheme.createSubtitleLabel("Inicia sesion en tu cuenta");
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(subtitle, gbc);

        gbc.gridy = 1;
        JLabel emailLabel = ModernTheme.createLabel("Correo electronico");
        centerPanel.add(emailLabel, gbc);

        gbc.gridy = 2;
        emailField = ModernTheme.createTextField(20);
        centerPanel.add(emailField, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 10, 0);
        JLabel passwordLabel = ModernTheme.createLabel("Contrasena");
        centerPanel.add(passwordLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 10, 0);
        passwordField = ModernTheme.createPasswordField(20);
        centerPanel.add(passwordField, gbc);

        gbc.gridy = 5;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(ModernTheme.FONT_SMALL);
        statusLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(statusLabel, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 10, 0);
        loginBtn = ModernTheme.createPrimaryButton("INICIAR SESION");
        loginBtn.setPreferredSize(new Dimension(300, 45));
        centerPanel.add(loginBtn, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 10, 0);
        registerBtn = ModernTheme.createSecondaryButton("CREAR CUENTA");
        registerBtn.setPreferredSize(new Dimension(300, 45));
        centerPanel.add(registerBtn, gbc);

        root.add(header, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);

        return root;
    }

    private void wireEvents() {
        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> openRegisterDialog());
        passwordField.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Por favor completa todos los campos");
            return;
        }

        setBusy(true);
        try {
            Usuario u = authTx.iniciarSesion(email, pass);

            if (u.getRol() == Rol.CLIENTE) {
                if (productoTx == null || carritoTx == null || pedidoTx == null) {
                    throw new IllegalStateException("Servicios de cliente no disponibles");
                }
                MenuFrame menu = new MenuFrame(authTx, usuarioTx, adminTx, productoTx, carritoTx, pedidoTx, u);
                menu.setVisible(true);
                dispose();
            } else {
                MenuAdminFrame admin = new MenuAdminFrame(authTx, usuarioTx, adminTx, productoTx, carritoTx, pedidoTx, u);
                admin.setVisible(true);
                dispose();
            }

            statusLabel.setText("Ingreso correcto");
            statusLabel.setForeground(ModernTheme.SUCCESS);
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        } finally {
            setBusy(false);
        }
    }

    private void openRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this, authTx);
        dialog.setVisible(true);

        String registeredEmail = dialog.getRegisteredEmail();
        if (registeredEmail != null) {
            emailField.setText(registeredEmail);
            passwordField.setText("");
            statusLabel.setText("Cuenta creada. Inicia sesion ahora");
            statusLabel.setForeground(ModernTheme.SUCCESS);
            passwordField.requestFocusInWindow();
        }
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setForeground(ModernTheme.ERROR);
    }

    private void setBusy(boolean busy) {
        loginBtn.setEnabled(!busy);
        registerBtn.setEnabled(!busy);
        emailField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}

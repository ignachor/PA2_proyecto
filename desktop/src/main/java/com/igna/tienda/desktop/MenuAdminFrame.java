package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.infra.services.UsuarioServiceTx;
import com.igna.tienda.infra.services.AdminServiceTx;

import javax.swing.*;
import java.awt.*;

public class MenuAdminFrame extends JFrame {
    private final AuthServiceTx authTx;
    private final UsuarioServiceTx usuarioTx;
    private final AdminServiceTx adminTx;
    private Usuario adminActual;

    private final JLabel tituloLabel = new JLabel("MENU ADMINISTRADOR");
    private final JLabel bienvenidaLabel = new JLabel();
    private final JButton usuariosBtn = new JButton("Usuarios");
    private final JButton productosBtn = new JButton("Productos");
    private final JButton salirBtn = new JButton("Salir");

    public MenuAdminFrame(AuthServiceTx authTx, UsuarioServiceTx usuarioTx, AdminServiceTx adminTx, Usuario adminActual) {
        super("Menu - Admin");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;
        this.adminTx = adminTx;
        this.adminActual = adminActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(640, 360));
        setLocationRelativeTo(null);

        setContentPane(buildContent());
        wireEvents();
        refreshUsuarioInfo();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        tituloLabel.setFont(tituloLabel.getFont().deriveFont(Font.BOLD, 16f));
        bienvenidaLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        left.add(tituloLabel);
        left.add(bienvenidaLabel);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        usuariosBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        productosBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(usuariosBtn);
        right.add(Box.createVerticalStrut(10));
        right.add(productosBtn);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.add(salirBtn);

        root.add(left, BorderLayout.CENTER);
        root.add(right, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        usuariosBtn.addActionListener(e -> openUsuarios());
        productosBtn.addActionListener(e -> openProductos());
        salirBtn.addActionListener(e -> doLogout());
    }

    private void refreshUsuarioInfo() {
        bienvenidaLabel.setText("Bienvenido " + adminActual.getNombre());
    }

    private void openUsuarios() {
        // Abrimos el menu de gestion de usuarios
        MenuUsuarioAdminFrame menuUsuarios = new MenuUsuarioAdminFrame(authTx, usuarioTx, adminTx, adminActual);
        menuUsuarios.setVisible(true);
    }

    private void openProductos() {
        // Punto de entrada para gestion de productos (placeholder)
        JOptionPane.showMessageDialog(this,
                "Abrir gestion de productos (pendiente).",
                "Productos",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void doLogout() {
        // Cerramos el menu admin y volvemos al login (nuevo frame)
        dispose();
        new LoginFrame(authTx, usuarioTx, adminTx).setVisible(true);
    }
}

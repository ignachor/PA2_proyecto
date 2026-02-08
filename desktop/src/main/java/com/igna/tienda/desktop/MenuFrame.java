package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.infra.services.UsuarioServiceTx;
import com.igna.tienda.infra.services.AdminServiceTx;

import javax.swing.*;
import java.awt.*;

public class MenuFrame extends JFrame {

    private final AuthServiceTx authTx;
    private final UsuarioServiceTx usuarioTx;
    private final AdminServiceTx adminTx;
    private Usuario usuarioActual;

    private final JLabel nombreLabel = new JLabel();
    private final JLabel emailLabel = new JLabel();
    private final JButton editarPerfilBtn = new JButton("Editar perfil");
    private final JButton cerrarSesionBtn = new JButton("Cerrar sesi\u00f3n");

    public MenuFrame(AuthServiceTx authTx, UsuarioServiceTx usuarioTx, AdminServiceTx adminTx, Usuario usuarioActual) {
        super("Men\u00fa - Tienda");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;
        this.adminTx = adminTx;
        this.usuarioActual = usuarioActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(420, 220));
        setLocationRelativeTo(null);

        setContentPane(buildContent());
        wireEvents();
        refreshUsuarioInfo();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel info = new JPanel(new GridLayout(0, 1, 6, 6));
        info.add(nombreLabel);
        info.add(emailLabel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(cerrarSesionBtn);
        buttons.add(editarPerfilBtn);

        root.add(info, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        editarPerfilBtn.addActionListener(e -> openEditarPerfil());
        cerrarSesionBtn.addActionListener(e -> doLogout());
    }

    private void refreshUsuarioInfo() {
        nombreLabel.setText("Usuario: " + usuarioActual.getNombre() + " " + usuarioActual.getApellido());
        emailLabel.setText("Email: " + usuarioActual.getEmail());
    }

    private void openEditarPerfil() {
        // 1) Desde el menu abrimos el dialogo de edicion
        // 2) Le pasamos este frame como owner para que quede modal
        // 3) Le pasamos el usuario actual y el service tx para guardar
        EditarPerfilUsuario dialog = new EditarPerfilUsuario(this, usuarioTx, usuarioActual);
        dialog.setVisible(true);

        // 4) Si se actualizo, tomamos el usuario actualizado y refrescamos la vista
        if (dialog.isUpdated()) {
            usuarioActual = dialog.getUsuarioActualizado();
            refreshUsuarioInfo();
        }
    }

    private void doLogout() {
        // Cerramos el menu y volvemos al login (nuevo frame)
        dispose();
        new LoginFrame(authTx, usuarioTx, adminTx).setVisible(true);
    }
}

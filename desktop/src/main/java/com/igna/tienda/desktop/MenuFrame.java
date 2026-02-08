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

    private JLabel nombreLabel;
    private JLabel emailLabel;
    private JButton editarPerfilBtn;
    private JButton cerrarSesionBtn;

    public MenuFrame(AuthServiceTx authTx, UsuarioServiceTx usuarioTx, AdminServiceTx adminTx, Usuario usuarioActual) {
        super("Mi Cuenta - Tienda");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;
        this.adminTx = adminTx;
        this.usuarioActual = usuarioActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 450));
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(buildContent());
        wireEvents();
        refreshUsuarioInfo();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ModernTheme.BG_PRIMARY);

        // Header con degradado
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.PRIMARY);
        header.setPreferredSize(new Dimension(600, 100));
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("MI PERFIL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        // Panel central con información del usuario
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(ModernTheme.BG_PRIMARY);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Card con info del usuario
        JPanel infoCard = new JPanel();
        infoCard.setBackground(Color.WHITE);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        infoCard.setMaximumSize(new Dimension(500, 200));

        // Ícono de usuario
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        nombreLabel = new JLabel();
        nombreLabel.setFont(ModernTheme.FONT_SUBTITLE);
        nombreLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        nombreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        emailLabel = new JLabel();
        emailLabel.setFont(ModernTheme.FONT_BODY);
        emailLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoCard.add(iconLabel);
        infoCard.add(Box.createVerticalStrut(15));
        infoCard.add(nombreLabel);
        infoCard.add(Box.createVerticalStrut(5));
        infoCard.add(emailLabel);

        centerPanel.add(infoCard);
        centerPanel.add(Box.createVerticalStrut(30));

        // Panel de acciones
        JPanel actionsPanel = new JPanel();
        actionsPanel.setBackground(ModernTheme.BG_PRIMARY);
        actionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionsPanel.setMaximumSize(new Dimension(500, 60));

        editarPerfilBtn = ModernTheme.createPrimaryButton("✏️ EDITAR PERFIL");
        editarPerfilBtn.setPreferredSize(new Dimension(200, 45));
        
        cerrarSesionBtn = ModernTheme.createSecondaryButton("🚪 CERRAR SESIÓN");
        cerrarSesionBtn.setPreferredSize(new Dimension(200, 45));

        actionsPanel.add(editarPerfilBtn);
        actionsPanel.add(cerrarSesionBtn);

        centerPanel.add(actionsPanel);

        root.add(header, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);

        return root;
    }

    private void wireEvents() {
        editarPerfilBtn.addActionListener(e -> openEditarPerfil());
        cerrarSesionBtn.addActionListener(e -> doLogout());
    }

    private void refreshUsuarioInfo() {
        nombreLabel.setText(usuarioActual.getNombre() + " " + usuarioActual.getApellido());
        emailLabel.setText(usuarioActual.getEmail());
    }

    private void openEditarPerfil() {
        EditarPerfilUsuario dialog = new EditarPerfilUsuario(this, usuarioTx, usuarioActual);
        dialog.setVisible(true);

        if (dialog.isUpdated()) {
            usuarioActual = dialog.getUsuarioActualizado();
            refreshUsuarioInfo();
        }
    }

    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas cerrar sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame(authTx, usuarioTx, adminTx).setVisible(true);
        }
    }
}
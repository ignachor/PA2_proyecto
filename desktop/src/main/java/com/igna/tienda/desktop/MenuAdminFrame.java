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

    private JLabel bienvenidaLabel;
    private JButton usuariosBtn;
    private JButton productosBtn;
    private JButton salirBtn;

    public MenuAdminFrame(AuthServiceTx authTx, UsuarioServiceTx usuarioTx, AdminServiceTx adminTx, Usuario adminActual) {
        super("Panel de Administración");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;
        this.adminTx = adminTx;
        this.adminActual = adminActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(buildContent());
        wireEvents();
        refreshUsuarioInfo();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ModernTheme.BG_PRIMARY);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.PRIMARY_DARK);
        header.setPreferredSize(new Dimension(700, 100));
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("⚙️ PANEL DE ADMINISTRACIÓN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        bienvenidaLabel = new JLabel();
        bienvenidaLabel.setFont(ModernTheme.FONT_BODY);
        bienvenidaLabel.setForeground(new Color(236, 240, 241));
        
        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(ModernTheme.PRIMARY_DARK);
        headerContent.add(titleLabel);
        headerContent.add(Box.createVerticalStrut(8));
        headerContent.add(bienvenidaLabel);
        
        header.add(headerContent, BorderLayout.WEST);

        // Panel central con opciones
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(ModernTheme.BG_PRIMARY);
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card de Usuarios
        gbc.gridy = 0;
        JPanel usuariosCard = createActionCard(
            "👥 Gestión de Usuarios",
            "Administra usuarios, activa/desactiva cuentas y visualiza información"
        );
        centerPanel.add(usuariosCard, gbc);

        gbc.gridy = 1;
        usuariosBtn = ModernTheme.createPrimaryButton("ABRIR GESTIÓN DE USUARIOS");
        usuariosBtn.setPreferredSize(new Dimension(400, 45));
        JPanel btnPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel1.setBackground(ModernTheme.BG_PRIMARY);
        btnPanel1.add(usuariosBtn);
        centerPanel.add(btnPanel1, gbc);

        // Card de Productos
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 0, 15, 0);
        JPanel productosCard = createActionCard(
            "📦 Gestión de Productos",
            "Administra el catálogo de productos (próximamente)"
        );
        centerPanel.add(productosCard, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(15, 0, 15, 0);
        productosBtn = ModernTheme.createSecondaryButton("ABRIR GESTIÓN DE PRODUCTOS");
        productosBtn.setPreferredSize(new Dimension(400, 45));
        productosBtn.setEnabled(false);
        JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel2.setBackground(ModernTheme.BG_PRIMARY);
        btnPanel2.add(productosBtn);
        centerPanel.add(btnPanel2, gbc);

        // Footer con botón salir
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(ModernTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));
        
        salirBtn = ModernTheme.createSecondaryButton("🚪 CERRAR SESIÓN");
        footer.add(salirBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private JPanel createActionCard(String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(500, 100));

        JLabel titleLabel = ModernTheme.createSubtitleLabel(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(ModernTheme.FONT_BODY);
        descLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descLabel);

        return card;
    }

    private void wireEvents() {
        usuariosBtn.addActionListener(e -> openUsuarios());
        productosBtn.addActionListener(e -> openProductos());
        salirBtn.addActionListener(e -> doLogout());
    }

    private void refreshUsuarioInfo() {
        bienvenidaLabel.setText("Administrador: " + adminActual.getNombre() + " " + adminActual.getApellido());
    }

    private void openUsuarios() {
        MenuUsuarioAdminFrame menuUsuarios = new MenuUsuarioAdminFrame(authTx, usuarioTx, adminTx, adminActual);
        menuUsuarios.setVisible(true);
    }

    private void openProductos() {
        JOptionPane.showMessageDialog(this,
                "La gestión de productos estará disponible próximamente",
                "Función en desarrollo",
                JOptionPane.INFORMATION_MESSAGE);
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
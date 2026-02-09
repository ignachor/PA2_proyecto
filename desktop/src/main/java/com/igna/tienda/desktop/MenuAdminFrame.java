package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.AdminServiceTx;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.infra.services.CarritoServiceTx;
import com.igna.tienda.infra.services.PedidoServiceTx;
import com.igna.tienda.infra.services.ProductoServiceTx;
import com.igna.tienda.infra.services.UsuarioServiceTx;

import javax.swing.*;
import java.awt.*;

public class MenuAdminFrame extends JFrame {
    private final AuthServiceTx authTx;
    private final UsuarioServiceTx usuarioTx;
    private final AdminServiceTx adminTx;
    private final ProductoServiceTx productoTx;
    private final CarritoServiceTx carritoTx;
    private final PedidoServiceTx pedidoTx;
    private Usuario adminActual;

    private JLabel bienvenidaLabel;
    private JButton usuariosBtn;
    private JButton productosBtn;
    private JButton salirBtn;

    public MenuAdminFrame(
            AuthServiceTx authTx,
            UsuarioServiceTx usuarioTx,
            AdminServiceTx adminTx,
            ProductoServiceTx productoTx,
            CarritoServiceTx carritoTx,
            PedidoServiceTx pedidoTx,
            Usuario adminActual
    ) {
        super("Panel de Administracion");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;
        this.adminTx = adminTx;
        this.productoTx = productoTx;
        this.carritoTx = carritoTx;
        this.pedidoTx = pedidoTx;
        this.adminActual = adminActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // Paso 0: aumenta el alto util para evitar recortes de tarjetas en pantallas con escalado.
        setMinimumSize(new Dimension(840, 620));
        setSize(new Dimension(900, 680));
        setLocationRelativeTo(null);
        setResizable(true);

        setContentPane(buildContent());
        wireEvents();
        refreshUsuarioInfo();
    }

    private JPanel buildContent() {
        // Paso 1: layout principal en tres zonas claras (header / contenido / footer).
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(ModernTheme.BG_PRIMARY);
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.PRIMARY_DARK);
        header.setPreferredSize(new Dimension(700, 110));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel("PANEL DE ADMINISTRACION");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
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

        // Paso 2: contenido central en 2 filas estables para mostrar siempre ambos modulos.
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 14));
        centerPanel.setBackground(ModernTheme.BG_PRIMARY);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));

        usuariosBtn = ModernTheme.createPrimaryButton("ABRIR GESTION DE USUARIOS");
        usuariosBtn.setPreferredSize(new Dimension(320, 42));
        productosBtn = ModernTheme.createSecondaryButton("ABRIR GESTION DE PRODUCTOS");
        productosBtn.setPreferredSize(new Dimension(320, 42));

        JPanel usuariosCard = createActionCard(
                "Gestion de Usuarios",
                "Administra usuarios, activa/desactiva cuentas y visualiza informacion.",
                usuariosBtn
        );
        JPanel productosCard = createActionCard(
                "Gestion de Productos",
                "Administra el catalogo y las operaciones de productos.",
                productosBtn
        );

        centerPanel.add(usuariosCard);
        centerPanel.add(productosCard);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(ModernTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));

        salirBtn = ModernTheme.createSecondaryButton("CERRAR SESION");
        footer.add(salirBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private JPanel createActionCard(String title, String description, JButton actionBtn) {
        // Paso 3: tarjeta por modulo con titulo/descripcion separados del boton.
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.WHITE);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = ModernTheme.createSubtitleLabel(title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><body style='width:620px'>" + description + "</body></html>");
        descLabel.setFont(ModernTheme.FONT_BODY);
        descLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(descLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(actionBtn);

        card.add(textPanel, BorderLayout.NORTH);
        card.add(buttonPanel, BorderLayout.SOUTH);

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
        MenuProductoAdminFrame menuProductos = new MenuProductoAdminFrame(authTx, usuarioTx, adminTx, adminActual);
        menuProductos.setVisible(true);
    }

    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Estas seguro de que deseas cerrar sesion?",
                "Confirmar cierre de sesion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame(authTx, usuarioTx, adminTx, productoTx, carritoTx, pedidoTx).setVisible(true);
        }
    }
}

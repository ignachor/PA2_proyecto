package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ListarUsuariosAdminFrame extends JFrame {

    private final DefaultTableModel model;
    private final JTable tabla;
    private JButton seleccionarBtn;
    private List<Usuario> usuarios;
    private final Consumer<Usuario> onSeleccionar;

    public ListarUsuariosAdminFrame() {
        this(null, null);
    }

    public ListarUsuariosAdminFrame(List<Usuario> usuarios) {
        this(usuarios, null);
    }

    public ListarUsuariosAdminFrame(List<Usuario> usuarios, Consumer<Usuario> onSeleccionar) {
        super("Lista Completa de Usuarios");
        this.onSeleccionar = onSeleccionar;
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setResizable(true);

        model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Apellido", "DNI", "Email", "Rol", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabla = new JTable(model);
        ModernTheme.styleTable(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setContentPane(buildContent());
        wireEvents();

        if (usuarios != null) {
            cargarUsuarios(usuarios);
        }
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ModernTheme.BG_PRIMARY);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.PRIMARY_DARK);
        header.setPreferredSize(new Dimension(1000, 90));
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("📋 LISTA DE USUARIOS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitle = new JLabel("Selecciona un usuario de la tabla");
        subtitle.setFont(ModernTheme.FONT_BODY);
        subtitle.setForeground(new Color(236, 240, 241));
        
        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(ModernTheme.PRIMARY_DARK);
        headerContent.add(titleLabel);
        headerContent.add(Box.createVerticalStrut(5));
        headerContent.add(subtitle);
        
        header.add(headerContent, BorderLayout.WEST);

        // Tabla con scroll
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        scroll.getViewport().setBackground(Color.WHITE);

        // Footer con botones
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(ModernTheme.BG_SECONDARY);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));
        
        seleccionarBtn = ModernTheme.createPrimaryButton("SELECCIONAR USUARIO");
        seleccionarBtn.setEnabled(false);
        
        footer.add(seleccionarBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            seleccionarBtn.setEnabled(tabla.getSelectedRow() >= 0);
        });

        seleccionarBtn.addActionListener(e -> seleccionarUsuario());
        
        // Doble click en tabla también selecciona
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    seleccionarUsuario();
                }
            }
        });
    }

    private void seleccionarUsuario() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow < 0) {
            seleccionarBtn.setEnabled(false);
            return;
        }
        
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        if (usuarios == null || modelRow < 0 || modelRow >= usuarios.size()) {
            return;
        }
        
        Usuario u = usuarios.get(modelRow);
        if (onSeleccionar != null) {
            onSeleccionar.accept(u);
        }
        dispose();
    }

    public void cargarUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
        model.setRowCount(0);
        
        if (usuarios == null || usuarios.isEmpty()) {
            return;
        }
        
        for (Usuario u : usuarios) {
            model.addRow(new Object[]{
                    u.getId(),
                    u.getNombre(),
                    u.getApellido(),
                    u.getDni(),
                    u.getEmail(),
                    u.getRol(),
                    u.esActivo() ? "✓ Activo" : "✗ Inactivo"
            });
        }
    }

    public JTable getTabla() {
        return tabla;
    }

    public JButton getSeleccionarBtn() {
        return seleccionarBtn;
    }
}
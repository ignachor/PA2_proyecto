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
    private final JButton seleccionarBtn = new JButton("Seleccionar");
    // Se mantiene la lista para poder mapear la fila seleccionada al Usuario original
    private List<Usuario> usuarios;
    // Callback al seleccionar un usuario (permite "volver" al menu anterior)
    private final Consumer<Usuario> onSeleccionar;

    public ListarUsuariosAdminFrame() {
        this(null, null);
    }

    public ListarUsuariosAdminFrame(List<Usuario> usuarios) {
        this(usuarios, null);
    }

    public ListarUsuariosAdminFrame(List<Usuario> usuarios, Consumer<Usuario> onSeleccionar) {
        super("Lista de Usuarios");
        this.onSeleccionar = onSeleccionar;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(860, 420));
        setLocationRelativeTo(null);

        model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Apellido", "DNI", "Email", "Rol", "Activo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(model);
        tabla.setRowHeight(24);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seleccionarBtn.setEnabled(false);

        setContentPane(buildContent());
        wireEvents();

        if (usuarios != null) {
            cargarUsuarios(usuarios);
        }
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("LISTA DE USUARIOS");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.WEST);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Usuarios"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.add(seleccionarBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        // Habilita/inhabilita el boton segun seleccion valida en la tabla
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            seleccionarBtn.setEnabled(tabla.getSelectedRow() >= 0);
        });

        // Al seleccionar, dispara el callback y cierra el frame
        seleccionarBtn.addActionListener(e -> seleccionarUsuario());
    }

    private void seleccionarUsuario() {
        // Convierte la fila seleccionada (vista) a indice del modelo
        int viewRow = tabla.getSelectedRow();
        if (viewRow < 0) {
            seleccionarBtn.setEnabled(false);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        // Verificacion basica de limites y lista
        if (usuarios == null || modelRow < 0 || modelRow >= usuarios.size()) {
            return;
        }
        Usuario u = usuarios.get(modelRow);
        // Hook al menu anterior
        if (onSeleccionar != null) {
            onSeleccionar.accept(u);
        }
        dispose();
    }

    public void cargarUsuarios(List<Usuario> usuarios) {
        // Refresca la tabla con la lista recibida
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
                    u.esActivo() ? "SI" : "NO"
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

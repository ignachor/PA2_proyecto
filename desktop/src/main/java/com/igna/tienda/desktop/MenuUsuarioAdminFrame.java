package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.infra.services.UsuarioServiceTx;
import com.igna.tienda.infra.services.AdminServiceTx;

import javax.swing.*;
import java.awt.*;

public class MenuUsuarioAdminFrame extends JFrame {

    private final AuthServiceTx authTx;
    private final UsuarioServiceTx usuarioTx;
    private final AdminServiceTx adminTx;
    private final Usuario adminActual;

    private final JTextField buscarField = new JTextField(26);
    private final JButton buscarBtn = new JButton("Buscar");

    private final JTextArea detalleArea = new JTextArea(10, 28);

    private final JButton activarBtn = new JButton("Activar Usuario");
    private final JButton desactivarBtn = new JButton("Desactivar Usuario");
    private final JButton verPedidosBtn = new JButton("Ver Pedidos");
    private final JButton listarBtn = new JButton("Listar Usuarios");

    public MenuUsuarioAdminFrame(AuthServiceTx authTx, UsuarioServiceTx usuarioTx, AdminServiceTx adminTx, Usuario adminActual) {
        super("Menu Usuario - Administrador");
        this.authTx = authTx;
        this.usuarioTx = usuarioTx;
        this.adminTx = adminTx;
        this.adminActual = adminActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(720, 420));
        setLocationRelativeTo(null);

        setContentPane(buildContent());
        wireEvents();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("MENU USUARIO");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        header.add(title);

        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.add(new JLabel("Buscar usuario"), BorderLayout.NORTH);
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.add(buscarField, BorderLayout.CENTER);
        searchRow.add(buscarBtn, BorderLayout.EAST);
        search.add(searchRow, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(header, BorderLayout.NORTH);
        top.add(search, BorderLayout.CENTER);

        detalleArea.setEditable(false);
        detalleArea.setText("Datos usuario y pedidos\n- dato_1\n- ...\n- dato_n");
        JScrollPane scroll = new JScrollPane(detalleArea);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        activarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        desactivarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        verPedidosBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(activarBtn);
        right.add(Box.createVerticalStrut(10));
        right.add(desactivarBtn);
        right.add(Box.createVerticalStrut(10));
        right.add(verPedidosBtn);

        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.add(scroll, BorderLayout.CENTER);
        center.add(right, BorderLayout.EAST);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottom.add(listarBtn);

        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        buscarBtn.addActionListener(e -> buscarUsuario());
        activarBtn.addActionListener(e -> activarUsuario());
        desactivarBtn.addActionListener(e -> desactivarUsuario());
        verPedidosBtn.addActionListener(e -> verPedidos());
        listarBtn.addActionListener(e -> listarUsuarios());
    }

    private void buscarUsuario() {
        // Placeholder: aca iria la busqueda real
        JOptionPane.showMessageDialog(this,
                "Buscar usuario: " + buscarField.getText(),
                "Buscar",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void activarUsuario() {
        String email = buscarField.getText();
        if (email == null || email.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa el email del usuario a activar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Estas seguro de activarlo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            adminTx.activarUsuarioPorEmail(email);
            detalleArea.setText("Usuario activado: " + email);
            JOptionPane.showMessageDialog(this,
                    "Usuario activado correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desactivarUsuario() {
        String email = buscarField.getText();
        if (email == null || email.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa el email del usuario a desactivar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Estas seguro de desactivarlo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            adminTx.desactivarUsuarioPorEmail(email);
            detalleArea.setText("Usuario desactivado: " + email);
            JOptionPane.showMessageDialog(this,
                    "Usuario desactivado correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verPedidos() {
        // IMPLEMENTACION: abre la nueva pantalla administrativa de pedidos.
        PedidosUsuarioAdminFrame frame = new PedidosUsuarioAdminFrame(this, adminTx);
        frame.setVisible(true);
    }

    private void listarUsuarios() {
        try {
            var usuarios = adminTx.listarUsuarios();
            // Abre el listado y define el callback de seleccion
            ListarUsuariosAdminFrame frame = new ListarUsuariosAdminFrame(usuarios, this::onUsuarioSeleccionado);
            frame.setVisible(true);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUsuarioSeleccionado(Usuario u) {
        if (u == null) return;
        // Autocompleta el email para facilitar la busqueda y muestra los datos
        buscarField.setText(u.getEmail());
        detalleArea.setText(renderUsuario(u));
        buscarField.requestFocusInWindow();
    }

    private String renderUsuario(Usuario u) {
        // Formatea los datos del usuario seleccionado para mostrar en el detalle
        StringBuilder sb = new StringBuilder();
        sb.append("Usuario seleccionado").append('\n');
        sb.append("ID: ").append(u.getId()).append('\n');
        sb.append("Nombre: ").append(u.getNombre()).append('\n');
        sb.append("Apellido: ").append(u.getApellido()).append('\n');
        sb.append("DNI: ").append(u.getDni()).append('\n');
        sb.append("Email: ").append(u.getEmail()).append('\n');
        sb.append("Rol: ").append(u.getRol()).append('\n');
        sb.append("Activo: ").append(u.esActivo() ? "SI" : "NO").append('\n');
        if (u.getDireccion() != null) {
            sb.append("Direccion: ")
              .append(u.getDireccion().getCalle()).append(' ')
              .append(u.getDireccion().getNumero()).append(", ")
              .append(u.getDireccion().getCiudad()).append(", ")
              .append(u.getDireccion().getProvincia()).append(" (")
              .append(u.getDireccion().getCodigoPostal()).append(')');
        }
        return sb.toString();
    }
}

package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.DetallePedido;
import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.enums.EstadoPedido;
import com.igna.tienda.infra.services.AdminServiceTx;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PedidosUsuarioAdminFrame extends JFrame {

    private final AdminServiceTx adminTx;
    private final List<Pedido> pedidosBase = new ArrayList<>();
    private final List<Pedido> pedidosFiltrados = new ArrayList<>();

    private final JTextField buscarField = ModernTheme.createTextField(26);
    private final JButton buscarBtn = ModernTheme.createSecondaryButton("Buscar");
    private final JButton limpiarBtn = ModernTheme.createSecondaryButton("Limpiar");
    private final JButton refrescarBtn = ModernTheme.createSecondaryButton("Refrescar");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Pedido", "Fecha", "Estado", "Nombre", "Apellido", "Email", "Total"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaPedidos = new JTable(tableModel);

    private final JComboBox<EstadoPedido> estadoCombo = new JComboBox<>(EstadoPedido.values());
    private final JButton actualizarEstadoBtn = ModernTheme.createAccentButton("Actualizar Estado");

    // IMPLEMENTACION: reduce altura del detalle para asegurar visibilidad del panel de cambio de estado.
    private final JTextArea detalleArea = ModernTheme.createTextArea(8, 40);
    private final JButton cerrarBtn = ModernTheme.createSecondaryButton("Cerrar");

    public PedidosUsuarioAdminFrame(Frame owner, AdminServiceTx adminTx) {
        super("Lista de Pedidos - Administrador");
        this.adminTx = adminTx;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setResizable(false);
        setLocationRelativeTo(owner);

        setContentPane(buildContent());
        wireEvents();
        cargarPedidosIniciales();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(ModernTheme.BG_PRIMARY);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setBackground(ModernTheme.BG_PRIMARY);

        JLabel title = ModernTheme.createTitleLabel("LISTA DE PEDIDOS");
        header.add(title, BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(ModernTheme.BG_PRIMARY);
        searchRow.add(buscarField, BorderLayout.CENTER);

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchButtons.setBackground(ModernTheme.BG_PRIMARY);
        searchButtons.add(buscarBtn);
        searchButtons.add(limpiarBtn);
        searchButtons.add(refrescarBtn);
        searchRow.add(searchButtons, BorderLayout.EAST);

        JPanel searchWrapper = new JPanel(new BorderLayout(0, 6));
        searchWrapper.setBackground(ModernTheme.BG_PRIMARY);
        searchWrapper.add(ModernTheme.createLabel("Buscar por nombre y apellido"), BorderLayout.NORTH);
        searchWrapper.add(searchRow, BorderLayout.CENTER);
        header.add(searchWrapper, BorderLayout.CENTER);

        ModernTheme.styleTable(tablaPedidos);
        // IMPLEMENTACION: evita selecciones multiples para operar siempre sobre un solo pedido.
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(tablaPedidos);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        tableScroll.setPreferredSize(new Dimension(1060, 300));

        JPanel estadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        estadoPanel.setBackground(ModernTheme.BG_PRIMARY);
        estadoPanel.add(ModernTheme.createLabel("Estado del pedido seleccionado:"));
        estadoCombo.setFont(ModernTheme.FONT_BODY);
        estadoCombo.setPreferredSize(new Dimension(200, 38));
        estadoPanel.add(estadoCombo);
        estadoPanel.add(actualizarEstadoBtn);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(ModernTheme.BG_PRIMARY);
        // IMPLEMENTACION: ubica el selector de estado en SOUTH para que siempre quede visible.
        center.add(tableScroll, BorderLayout.CENTER);
        center.add(estadoPanel, BorderLayout.SOUTH);

        detalleArea.setEditable(false);
        JScrollPane detalleScroll = new JScrollPane(detalleArea);
        detalleScroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setBackground(ModernTheme.BG_PRIMARY);
        footer.add(cerrarBtn);

        // IMPLEMENTACION: agrupa detalle + botonera en el sur para no pisar regiones BorderLayout.
        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.setBackground(ModernTheme.BG_PRIMARY);
        south.add(detalleScroll, BorderLayout.CENTER);
        south.add(footer, BorderLayout.SOUTH);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);
        return root;
    }

    private void wireEvents() {
        buscarBtn.addActionListener(e -> aplicarFiltroBusqueda());
        limpiarBtn.addActionListener(e -> limpiarFiltro());
        refrescarBtn.addActionListener(e -> cargarPedidosIniciales());
        actualizarEstadoBtn.addActionListener(e -> actualizarEstadoSeleccionado());
        cerrarBtn.addActionListener(e -> dispose());
        buscarField.addActionListener(e -> aplicarFiltroBusqueda());
        tablaPedidos.getSelectionModel().addListSelectionListener(this::onPedidoSeleccionado);
    }

    private void cargarPedidosIniciales() {
        try {
            // Paso 1: obtener todos los pedidos visibles para administrador.
            List<Pedido> pedidos = adminTx.listarTodosPedidos();
            pedidosBase.clear();
            if (pedidos != null) {
                pedidosBase.addAll(pedidos);
            }

            // Paso 2: mostrar listado completo (sin filtro inicial).
            pedidosFiltrados.clear();
            pedidosFiltrados.addAll(pedidosBase);
            renderTabla();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarFiltroBusqueda() {
        String filtro = buscarField.getText();
        String needle = filtro == null ? "" : filtro.trim().toLowerCase(Locale.ROOT);

        // Paso 1: filtrar pedidos por nombre y/o apellido del usuario.
        pedidosFiltrados.clear();
        if (needle.isEmpty()) {
            pedidosFiltrados.addAll(pedidosBase);
        } else {
            for (Pedido pedido : pedidosBase) {
                Usuario usuario = pedido.getUsuarioCliente();
                String nombre = usuario == null ? "" : safeLower(usuario.getNombre());
                String apellido = usuario == null ? "" : safeLower(usuario.getApellido());
                String completo = (nombre + " " + apellido).trim();

                if (nombre.contains(needle) || apellido.contains(needle) || completo.contains(needle)) {
                    // Paso 2: si hay nombres/apellidos repetidos, se incluyen todos los pedidos coincidentes.
                    pedidosFiltrados.add(pedido);
                }
            }
        }
        renderTabla();
    }

    private void limpiarFiltro() {
        buscarField.setText("");
        pedidosFiltrados.clear();
        pedidosFiltrados.addAll(pedidosBase);
        renderTabla();
    }

    private void renderTabla() {
        tableModel.setRowCount(0);

        for (Pedido pedido : pedidosFiltrados) {
            Usuario u = pedido.getUsuarioCliente();
            tableModel.addRow(new Object[]{
                    pedido.getId(),
                    pedido.getFechaCreacion(),
                    pedido.getEstado(),
                    u == null ? "-" : u.getNombre(),
                    u == null ? "-" : u.getApellido(),
                    u == null ? "-" : u.getEmail(),
                    String.format("%.2f", pedido.getTotal())
            });
        }

        // Paso 3: autoseleccionar primer pedido para mostrar detalle/estado.
        if (!pedidosFiltrados.isEmpty()) {
            tablaPedidos.setRowSelectionInterval(0, 0);
            mostrarDetallePedido(0);
        } else {
            detalleArea.setText("No hay pedidos para el filtro ingresado.");
        }
    }

    private void onPedidoSeleccionado(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }
        int row = tablaPedidos.getSelectedRow();
        if (row >= 0) {
            mostrarDetallePedido(row);
        }
    }

    private void mostrarDetallePedido(int row) {
        if (row < 0 || row >= pedidosFiltrados.size()) {
            detalleArea.setText("");
            return;
        }

        Pedido pedido = pedidosFiltrados.get(row);
        Usuario usuario = pedido.getUsuarioCliente();

        // Paso 1: sincronizar combo de estado con el pedido seleccionado.
        estadoCombo.setSelectedItem(pedido.getEstado());

        StringBuilder sb = new StringBuilder();
        sb.append("Pedido #").append(pedido.getId()).append('\n');
        sb.append("Fecha: ").append(pedido.getFechaCreacion()).append('\n');
        sb.append("Estado: ").append(pedido.getEstado()).append('\n');
        sb.append("Total: ").append(String.format("%.2f", pedido.getTotal())).append("\n\n");

        sb.append("Usuario:\n");
        if (usuario != null) {
            sb.append("- Nombre: ").append(usuario.getNombre()).append(' ').append(usuario.getApellido()).append('\n');
            sb.append("- Email: ").append(usuario.getEmail()).append('\n');
            sb.append("- DNI: ").append(usuario.getDni()).append('\n');
        } else {
            sb.append("- Sin usuario asociado\n");
        }

        sb.append("\nDetalle items:\n");
        for (DetallePedido item : pedido.getItems()) {
            Producto p = item.getProducto();
            String nombre = p == null ? "-" : p.getNombre();
            String categoria = (p == null || p.getCategoria() == null) ? "-" : p.getCategoria().getDescripcion();

            sb.append("- ").append(nombre)
              .append(" | Categoria: ").append(categoria)
              .append(" | Cantidad: ").append(item.getCantidad())
              .append(" | P.Unit: ").append(String.format("%.2f", item.getPrecioUnitario()))
              .append(" | P.Total: ").append(String.format("%.2f", item.getSubtotal()))
              .append('\n');
        }

        detalleArea.setText(sb.toString());
    }

    private void actualizarEstadoSeleccionado() {
        int row = tablaPedidos.getSelectedRow();
        if (row < 0 || row >= pedidosFiltrados.size()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un pedido para actualizar su estado.",
                    "Pedidos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pedido pedido = pedidosFiltrados.get(row);
        EstadoPedido nuevoEstado = (EstadoPedido) estadoCombo.getSelectedItem();
        if (nuevoEstado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un estado valido.",
                    "Pedidos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Paso 0: evita ejecutar el CU si el estado elegido ya coincide con el actual.
        if (nuevoEstado == pedido.getEstado()) {
            JOptionPane.showMessageDialog(this,
                    "El pedido ya se encuentra en estado: " + nuevoEstado,
                    "Pedidos",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Paso 0.1: confirma accion administrativa de cambio de estado.
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cambiar estado del pedido #" + pedido.getId() + " de " + pedido.getEstado() + " a " + nuevoEstado + "?",
                "Confirmar cambio de estado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Paso 1: persistir cambio de estado mediante CU admin.
            Pedido actualizado = adminTx.cambiarEstadoPedido(pedido.getId(), nuevoEstado);

            // Paso 2: reflejar cambio en ambas colecciones locales (base + filtrada).
            actualizarEstadoEnMemoria(actualizado.getId(), actualizado.getEstado());
            renderTabla();
            seleccionarPedidoPorId(actualizado.getId());

            JOptionPane.showMessageDialog(this,
                    "Estado actualizado correctamente.",
                    "Pedidos",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstadoEnMemoria(Long pedidoId, EstadoPedido nuevoEstado) {
        // Paso utilitario: mantiene sincronizadas listas base/filtrada tras actualizar estado.
        if (pedidoId == null || nuevoEstado == null) {
            return;
        }
        for (Pedido pedido : pedidosBase) {
            if (pedidoId.equals(pedido.getId())) {
                pedido.cambiarEstado(nuevoEstado);
            }
        }
        for (Pedido pedido : pedidosFiltrados) {
            if (pedidoId.equals(pedido.getId())) {
                pedido.cambiarEstado(nuevoEstado);
            }
        }
    }

    private void seleccionarPedidoPorId(Long pedidoId) {
        // Paso utilitario: recupera foco visual sobre el pedido actualizado dentro de la tabla.
        if (pedidoId == null) {
            return;
        }
        for (int i = 0; i < tablaPedidos.getRowCount(); i++) {
            Object value = tablaPedidos.getValueAt(i, 0);
            if (value instanceof Long && pedidoId.equals(value)) {
                tablaPedidos.setRowSelectionInterval(i, i);
                mostrarDetallePedido(i);
                return;
            }
        }
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}

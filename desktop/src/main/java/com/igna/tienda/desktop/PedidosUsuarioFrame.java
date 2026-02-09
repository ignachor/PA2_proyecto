package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.DetallePedido;
import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.services.PedidoServiceTx;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PedidosUsuarioFrame extends JFrame {

    private final PedidoServiceTx pedidoTx;
    private final Usuario usuarioActual;
    private final List<Pedido> pedidosActuales = new ArrayList<>();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Pedido", "Fecha", "Estado", "Total"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaPedidos = new JTable(tableModel);
    private final JTextArea detalleArea = ModernTheme.createTextArea(12, 36);

    private final JButton refrescarBtn = ModernTheme.createSecondaryButton("Refrescar");
    private final JButton cerrarBtn = ModernTheme.createSecondaryButton("Cerrar");

    public PedidosUsuarioFrame(Frame owner, PedidoServiceTx pedidoTx, Usuario usuarioActual) {
        super("Mis Pedidos");
        this.pedidoTx = pedidoTx;
        this.usuarioActual = usuarioActual;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(940, 620));
        setResizable(false);
        setLocationRelativeTo(owner);

        setContentPane(buildContent());
        wireEvents();
        cargarPedidos();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(ModernTheme.BG_PRIMARY);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernTheme.BG_PRIMARY);
        JLabel title = ModernTheme.createTitleLabel("Mis Pedidos");
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(ModernTheme.BG_PRIMARY);
        right.add(refrescarBtn);
        right.add(cerrarBtn);
        header.add(right, BorderLayout.EAST);

        ModernTheme.styleTable(tablaPedidos);
        JScrollPane tablaScroll = new JScrollPane(tablaPedidos);
        tablaScroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        tablaScroll.setPreferredSize(new Dimension(880, 220));

        detalleArea.setEditable(false);
        JScrollPane detalleScroll = new JScrollPane(detalleArea);
        detalleScroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(ModernTheme.BG_PRIMARY);
        center.add(tablaScroll, BorderLayout.NORTH);
        center.add(detalleScroll, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private void wireEvents() {
        refrescarBtn.addActionListener(e -> cargarPedidos());
        cerrarBtn.addActionListener(e -> dispose());
        tablaPedidos.getSelectionModel().addListSelectionListener(this::onPedidoSeleccionado);
    }

    private void cargarPedidos() {
        try {
            // Paso 1: listar pedidos del usuario actual.
            List<Pedido> pedidos = pedidoTx.listarPedidosPorCliente(usuarioActual.getId());
            pedidosActuales.clear();
            tableModel.setRowCount(0);

            if (pedidos != null) {
                for (Pedido pedido : pedidos) {
                    pedidosActuales.add(pedido);
                    tableModel.addRow(new Object[]{
                            pedido.getId(),
                            pedido.getFechaCreacion(),
                            pedido.getEstado(),
                            String.format("%.2f", pedido.getTotal())
                    });
                }
            }

            // Paso 2: mostrar detalle del primero si existe.
            if (!pedidosActuales.isEmpty()) {
                tablaPedidos.setRowSelectionInterval(0, 0);
                mostrarDetallePedido(0);
            } else {
                detalleArea.setText("No tienes pedidos registrados.");
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
        if (row < 0 || row >= pedidosActuales.size()) {
            detalleArea.setText("");
            return;
        }

        Pedido pedido = pedidosActuales.get(row);
        StringBuilder sb = new StringBuilder();
        // Paso 1: cabecera con estado de pedido.
        sb.append("Pedido #").append(pedido.getId()).append('\n');
        sb.append("Fecha: ").append(pedido.getFechaCreacion()).append('\n');
        sb.append("Estado: ").append(pedido.getEstado()).append('\n');
        sb.append("Total: ").append(String.format("%.2f", pedido.getTotal())).append("\n\n");
        sb.append("Detalle:\n");

        // Paso 2: detallar items del pedido.
        for (DetallePedido item : pedido.getItems()) {
            Producto producto = item.getProducto();
            String nombre = producto == null ? "-" : producto.getNombre();
            String categoria = (producto == null || producto.getCategoria() == null)
                    ? "-"
                    : producto.getCategoria().getDescripcion();

            sb.append("- ").append(nombre)
              .append(" | Categoria: ").append(categoria)
              .append(" | Cantidad: ").append(item.getCantidad())
              .append(" | P.Unit: ").append(String.format("%.2f", item.getPrecioUnitario()))
              .append(" | P.Total: ").append(String.format("%.2f", item.getSubtotal()))
              .append('\n');
        }

        detalleArea.setText(sb.toString());
    }
}

package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.value.Direccion;
import com.igna.tienda.infra.services.UsuarioServiceTx;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class DireccionFrame extends JDialog {

    private final UsuarioServiceTx usuarioTx;
    private Usuario usuarioActual;

    private final JTextField calleField = new JTextField(24);
    private final JTextField numeroField = new JTextField(24);
    private final JTextField ciudadField = new JTextField(24);
    private final JTextField provinciaField = new JTextField(24);
    private final JTextField codPostalField = new JTextField(24);

    private final JButton okBtn = new JButton("Guardar cambios");
    private final JButton cancelBtn = new JButton("Cancelar");

    private boolean updated = false;

    public DireccionFrame(Frame owner, UsuarioServiceTx usuarioTx, Usuario usuarioActual) {
        super(owner, "Editar direccion", true);
        this.usuarioTx = Objects.requireNonNull(usuarioTx, "usuarioTx requerido");
        this.usuarioActual = Objects.requireNonNull(usuarioActual, "usuarioActual requerido");

        setMinimumSize(new Dimension(520, 300));
        setLocationRelativeTo(owner);
        setContentPane(buildContent());
        wireEvents();
        cargarDatosUsuario();
    }

    public boolean isUpdated() {
        return updated;
    }

    public Usuario getUsuarioActualizado() {
        return usuarioActual;
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        int y = 0;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Calle:"), c);
        c.gridx = 1;
        form.add(calleField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Numero:"), c);
        c.gridx = 1;
        form.add(numeroField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Ciudad:"), c);
        c.gridx = 1;
        form.add(ciudadField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Provincia:"), c);
        c.gridx = 1;
        form.add(provinciaField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Codigo postal:"), c);
        c.gridx = 1;
        form.add(codPostalField, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        okBtn.addActionListener(e -> doSave());
        cancelBtn.addActionListener(e -> dispose());
        codPostalField.addActionListener(e -> doSave());
    }

    private void cargarDatosUsuario() {
        Direccion d = usuarioActual.getDireccion();
        if (d != null) {
            calleField.setText(d.getCalle());
            numeroField.setText(d.getNumero());
            ciudadField.setText(d.getCiudad());
            provinciaField.setText(d.getProvincia());
            codPostalField.setText(d.getCodigoPostal());
        }
    }

    private void doSave() {
        Direccion nueva = new Direccion(
                calleField.getText(),
                numeroField.getText(),
                ciudadField.getText(),
                provinciaField.getText(),
                codPostalField.getText()
        );

        setBusy(true);
        try {
            Usuario mod = new Usuario(
                    usuarioActual.getId(),
                    usuarioActual.getNombre(),
                    usuarioActual.getApellido(),
                    usuarioActual.getDni(),
                    usuarioActual.getEmail(),
                    nueva,
                    usuarioActual.getPassword(),
                    usuarioActual.getRol()
            );

            usuarioActual = usuarioTx.editarDireccion(mod);
            updated = true;

            JOptionPane.showMessageDialog(this,
                    "Direccion actualizada correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            setBusy(false);
        }
    }

    private void setBusy(boolean busy) {
        okBtn.setEnabled(!busy);
        cancelBtn.setEnabled(!busy);
        calleField.setEnabled(!busy);
        numeroField.setEnabled(!busy);
        ciudadField.setEnabled(!busy);
        provinciaField.setEnabled(!busy);
        codPostalField.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}

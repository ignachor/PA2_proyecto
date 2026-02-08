package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.core.domain.value.Direccion;
import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {

    private final AuthServiceTx authTx;

    private final JTextField nombreField = new JTextField(20);
    private final JTextField apellidoField = new JTextField(20);
    private final JTextField dniField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField calleField = new JTextField(20);
    private final JTextField numeroField = new JTextField(20);
    private final JTextField ciudadField = new JTextField(20);
    private final JTextField provinciaField = new JTextField(20);
    private final JTextField codPostalField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    private final JButton okBtn = new JButton("Crear cuenta");
    private final JButton cancelBtn = new JButton("Cancelar");

    private String registeredEmail = null;

    public RegisterDialog(Frame owner, AuthServiceTx authTx) {
        super(owner, "Registro", true);
        this.authTx = authTx;

        setMinimumSize(new Dimension(620, 460));
        setLocationRelativeTo(owner);
        setContentPane(buildContent());
        wireEvents();
    }

    public String getRegisteredEmail() {
        return registeredEmail;
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
        form.add(new JLabel("Nombre:"), c);
        c.gridx = 1;
        form.add(nombreField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Apellido:"), c);
        c.gridx = 1;
        form.add(apellidoField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("DNI:"), c);
        c.gridx = 1;
        form.add(dniField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Calle"), c);
        c.gridx = 1;
        form.add(calleField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Numero"), c);
        c.gridx = 1;
        form.add(numeroField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Ciudad"), c);
        c.gridx = 1;
        form.add(ciudadField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Provincia"), c);
        c.gridx = 1;
        form.add(provinciaField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Codigo Postal"), c);
        c.gridx = 1;
        form.add(codPostalField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Email:"), c);
        c.gridx = 1;
        form.add(emailField, c);
        y++;

        c.gridx = 0; c.gridy = y;
        form.add(new JLabel("Contraseña:"), c);
        c.gridx = 1;
        form.add(passwordField, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        okBtn.addActionListener(e -> doRegister());
        cancelBtn.addActionListener(e -> dispose());
        passwordField.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String dni = dniField.getText();
        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());
        Direccion direccion = new Direccion(
            calleField.getText(),
            numeroField.getText(),
            ciudadField.getText(),
            provinciaField.getText(),
            codPostalField.getText()
        );
        setBusy(true);
        try {
            // usa tu AuthService corregido (registrarCliente)
            authTx.registrar(nombre, apellido, dni, email, direccion, pass, Rol.CLIENTE);

            registeredEmail = email == null ? null : email.trim().toLowerCase();

            JOptionPane.showMessageDialog(this,
                    "Usuario creado correctamente.",
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
        nombreField.setEnabled(!busy);
        apellidoField.setEnabled(!busy);
        dniField.setEnabled(!busy);
        emailField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}

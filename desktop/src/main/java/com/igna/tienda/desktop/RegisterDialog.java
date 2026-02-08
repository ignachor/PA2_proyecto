package com.igna.tienda.desktop;

import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.core.domain.value.Direccion;
import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {

    private final AuthServiceTx authTx;

    private JTextField nombreField;
    private JTextField apellidoField;
    private JTextField dniField;
    private JTextField emailField;
    private JTextField calleField;
    private JTextField numeroField;
    private JTextField ciudadField;
    private JTextField provinciaField;
    private JTextField codPostalField;
    private JPasswordField passwordField;
    private JButton okBtn;
    private JButton cancelBtn;

    private String registeredEmail = null;

    public RegisterDialog(Frame owner, AuthServiceTx authTx) {
        super(owner, "Crear Nueva Cuenta", true);
        this.authTx = authTx;

        setMinimumSize(new Dimension(650, 700));
        setLocationRelativeTo(owner);
        setResizable(false);
        setContentPane(buildContent());
        wireEvents();
    }

    public String getRegisteredEmail() {
        return registeredEmail;
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ModernTheme.BG_PRIMARY);

        // Header
        JPanel header = new JPanel();
        header.setBackground(ModernTheme.PRIMARY);
        header.setPreferredSize(new Dimension(650, 80));
        header.setLayout(new GridBagLayout());
        
        JLabel titleLabel = ModernTheme.createTitleLabel("Registro de Usuario");
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);

        // Form panel con scroll
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(ModernTheme.BG_PRIMARY);
        formWrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ModernTheme.BG_PRIMARY);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 5, 8, 5);
        c.weightx = 1.0;

        int y = 0;

        // Sección: Datos Personales
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        JLabel seccionPersonal = ModernTheme.createSubtitleLabel("📋 Datos Personales");
        form.add(seccionPersonal, c);
        y++;

        // Nombre
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("Nombre *"), c);
        c.gridx = 1;
        nombreField = ModernTheme.createTextField(15);
        form.add(nombreField, c);
        y++;

        // Apellido
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("Apellido *"), c);
        c.gridx = 1;
        apellidoField = ModernTheme.createTextField(15);
        form.add(apellidoField, c);
        y++;

        // DNI
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("DNI *"), c);
        c.gridx = 1;
        dniField = ModernTheme.createTextField(15);
        form.add(dniField, c);
        y++;

        // Sección: Dirección
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        c.insets = new Insets(20, 5, 8, 5);
        JLabel seccionDireccion = ModernTheme.createSubtitleLabel("📍 Dirección");
        form.add(seccionDireccion, c);
        y++;
        c.insets = new Insets(8, 5, 8, 5);

        // Calle
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("Calle"), c);
        c.gridx = 1;
        calleField = ModernTheme.createTextField(15);
        form.add(calleField, c);
        y++;

        // Número
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("Número"), c);
        c.gridx = 1;
        numeroField = ModernTheme.createTextField(15);
        form.add(numeroField, c);
        y++;

        // Ciudad
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("Ciudad"), c);
        c.gridx = 1;
        ciudadField = ModernTheme.createTextField(15);
        form.add(ciudadField, c);
        y++;

        // Provincia
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("Provincia"), c);
        c.gridx = 1;
        provinciaField = ModernTheme.createTextField(15);
        form.add(provinciaField, c);
        y++;

        // Código Postal
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("Código Postal"), c);
        c.gridx = 1;
        codPostalField = ModernTheme.createTextField(15);
        form.add(codPostalField, c);
        y++;

        // Sección: Acceso
        c.gridx = 0; c.gridy = y; c.gridwidth = 2;
        c.insets = new Insets(20, 5, 8, 5);
        JLabel seccionAcceso = ModernTheme.createSubtitleLabel("🔐 Datos de Acceso");
        form.add(seccionAcceso, c);
        y++;
        c.insets = new Insets(8, 5, 8, 5);

        // Email
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("Email *"), c);
        c.gridx = 1;
        emailField = ModernTheme.createTextField(15);
        form.add(emailField, c);
        y++;

        // Contraseña
        c.gridx = 0; c.gridy = y;
        form.add(ModernTheme.createLabel("Contraseña *"), c);
        c.gridx = 1;
        passwordField = ModernTheme.createPasswordField(15);
        form.add(passwordField, c);

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        formWrapper.add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setBackground(ModernTheme.BG_SECONDARY);
        buttons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)));
        
        cancelBtn = ModernTheme.createSecondaryButton("CANCELAR");
        okBtn = ModernTheme.createPrimaryButton("CREAR CUENTA");
        
        buttons.add(cancelBtn);
        buttons.add(okBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(formWrapper, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private void wireEvents() {
        okBtn.addActionListener(e -> doRegister());
        cancelBtn.addActionListener(e -> dispose());
        passwordField.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        String nombre = nombreField.getText().trim();
        String apellido = apellidoField.getText().trim();
        String dni = dniField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        // Validaciones básicas
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || 
            email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor completa todos los campos obligatorios (*)",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Direccion direccion = new Direccion(
            calleField.getText().trim(),
            numeroField.getText().trim(),
            ciudadField.getText().trim(),
            provinciaField.getText().trim(),
            codPostalField.getText().trim()
        );

        setBusy(true);
        try {
            authTx.registrar(nombre, apellido, dni, email, direccion, pass, Rol.CLIENTE);
            registeredEmail = email.toLowerCase();

            JOptionPane.showMessageDialog(this,
                    "✓ Cuenta creada exitosamente\n\nYa puedes iniciar sesión",
                    "Registro Completado",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear la cuenta:\n" + ex.getMessage(),
                    "Error de Registro",
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
        calleField.setEnabled(!busy);
        numeroField.setEnabled(!busy);
        ciudadField.setEnabled(!busy);
        provinciaField.setEnabled(!busy);
        codPostalField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
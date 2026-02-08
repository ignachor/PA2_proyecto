package com.igna.tienda.desktop;

import javax.swing.*;
import java.awt.*;

/**
 * Tema moderno con paleta de colores celeste/azul
 * Diseño minimalista y profesional para toda la aplicación
 */
public class ModernTheme {
    
    // Paleta de colores principal - Tonos Celeste/Azul
    public static final Color PRIMARY = new Color(52, 152, 219);           // Azul vibrante
    public static final Color PRIMARY_DARK = new Color(41, 128, 185);      // Azul oscuro
    public static final Color PRIMARY_LIGHT = new Color(133, 193, 233);    // Celeste claro
    public static final Color ACCENT = new Color(26, 188, 156);            // Verde azulado (acentos)
    
    // Colores de fondo
    public static final Color BG_PRIMARY = new Color(236, 240, 241);       // Gris muy claro
    public static final Color BG_SECONDARY = Color.WHITE;                   // Blanco
    public static final Color BG_DARK = new Color(52, 73, 94);             // Azul grisáceo oscuro
    
    // Colores de texto
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);        // Azul oscuro
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);   // Gris medio
    public static final Color TEXT_LIGHT = Color.WHITE;
    
    // Colores de estado
    public static final Color SUCCESS = new Color(46, 204, 113);           // Verde
    public static final Color ERROR = new Color(231, 76, 60);              // Rojo
    public static final Color WARNING = new Color(241, 196, 15);           // Amarillo
    
    // Tipografía
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    
    /**
     * Crea un botón primario estilizado
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        
        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY);
            }
        });
        
        return btn;
    }
    
    /**
     * Crea un botón secundario estilizado
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setForeground(PRIMARY);
        btn.setBackground(BG_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(PRIMARY, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        
        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_LIGHT);
                btn.setForeground(TEXT_LIGHT);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BG_SECONDARY);
                btn.setForeground(PRIMARY);
            }
        });
        
        return btn;
    }
    
    /**
     * Crea un botón de acento (para acciones importantes secundarias)
     */
    public static JButton createAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(22, 160, 133));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT);
            }
        });
        
        return btn;
    }
    
    /**
     * Crea un campo de texto estilizado
     */
    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
        return field;
    }
    
    /**
     * Crea un campo de contraseña estilizado
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
        return field;
    }
    
    /**
     * Crea un panel con título
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }
    
    /**
     * Crea una etiqueta de título
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(PRIMARY_DARK);
        return label;
    }
    
    /**
     * Crea una etiqueta de subtítulo
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Crea una etiqueta normal
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Estiliza una tabla
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(35);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(189, 195, 199));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Estilo del header
        table.getTableHeader().setFont(FONT_BODY.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 40));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
    }
    
    /**
     * Crea un área de texto estilizada
     */
    public static JTextArea createTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setFont(FONT_BODY);
        area.setForeground(TEXT_PRIMARY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return area;
    }
}
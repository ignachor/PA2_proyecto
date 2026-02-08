package com.igna.tienda.desktop;

import com.igna.tienda.infra.services.AuthServiceTx;
import com.igna.tienda.infra.services.UsuarioServiceTx;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;

public class DesktopApp {

    public static void main(String[] args) {
      
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pa2PU");
        AuthServiceTx authTx = new AuthServiceTx(emf);
        UsuarioServiceTx usuarioTx = new UsuarioServiceTx(emf);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { emf.close(); } catch (Exception ignored) {}
            try { com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown(); }
            catch (Exception e) {
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            }
        }));

        SwingUtilities.invokeLater(() -> new LoginFrame(authTx, usuarioTx).setVisible(true));
    }
}

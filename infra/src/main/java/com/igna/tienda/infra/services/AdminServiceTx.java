package com.igna.tienda.infra.services;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.igna.tienda.core.services.AdminService;
import java.util.List;
public class AdminServiceTx {
    private final EntityManagerFactory emf;

    public AdminServiceTx(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //ACTIVAR USUARIO POR EMAIL
    public Usuario activarUsuarioPorEmail(String email) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var repo = new JpaUsuarioRepository(em);
            var adminCore = new AdminService(repo);
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email no proporcionado");
            }

            Usuario u = repo.buscarPorEmail(email.trim().toLowerCase());
            if (u == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            adminCore.ActivarUsuario(u);
            tx.commit();
            return u;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }




    //DESACTIVAR USUARIO POR EMAIL 
    public Usuario desactivarUsuarioPorEmail(String email) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var repo = new JpaUsuarioRepository(em);
            var adminCore = new AdminService(repo);
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email no proporcionado");
            }

            Usuario u = repo.buscarPorEmail(email.trim().toLowerCase());
            if (u == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            adminCore.DesactivarUsuario(u);
            tx.commit();
            return u;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    //LISTAR USUARIOS
    public List<Usuario> listarUsuarios() {
        EntityManager em = emf.createEntityManager();
        try {
            var repo = new JpaUsuarioRepository(em);
            var adminCore = new AdminService(repo);
            return adminCore.ListarUsuarios();
        } finally {
            em.close();
        }
    }
}

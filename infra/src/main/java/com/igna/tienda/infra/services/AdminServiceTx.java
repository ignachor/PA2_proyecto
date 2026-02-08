package com.igna.tienda.infra.services;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class AdminServiceTx {
    private final EntityManagerFactory emf;

    public AdminServiceTx(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Usuario desactivarUsuarioPorEmail(String email) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var repo = new JpaUsuarioRepository(em);

            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email no proporcionado");
            }

            Usuario u = repo.buscarPorEmail(email.trim().toLowerCase());
            if (u == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }

            u.desactivar();
            repo.guardar(u);
            tx.commit();
            return u;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

package com.igna.tienda.infra.services;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.enums.Rol;
import com.igna.tienda.core.services.AuthService;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.igna.tienda.core.domain.value.Direccion;
public class AuthServiceTx {

    private final EntityManagerFactory emf;

    public AuthServiceTx(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Usuario registrar(String nombre, String apellido, String dni, String email, Direccion direccion, String password, Rol rol) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();

            var repo = new JpaUsuarioRepository(em);
            var authCore = new AuthService(repo);

            Usuario u = authCore.registrar(nombre, apellido, dni, email, direccion, password, rol);

            tx.commit();
            return u;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Usuario iniciarSesion(String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            var repo = new JpaUsuarioRepository(em);
            var authCore = new AuthService(repo);

            return authCore.iniciarSesion(email, password);
        } finally {
            em.close();
        }
    }
}

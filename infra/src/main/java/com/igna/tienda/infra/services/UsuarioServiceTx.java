package com.igna.tienda.infra.services;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import jakarta.persistence.EntityManagerFactory;
import com.igna.tienda.core.services.UsuarioService;
import jakarta.persistence.EntityManager;


public class UsuarioServiceTx {
        private EntityManagerFactory emf;

        public UsuarioServiceTx(EntityManagerFactory emf) {
            this.emf = emf;
        }

        public Usuario editarDatosPersonales (Usuario modificarUsuario) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
    try {
            tx.begin();
            var repo = new JpaUsuarioRepository(em);
            var usuarioCore = new UsuarioService(repo);
            var result = usuarioCore.editarDatosPersonales(modificarUsuario);
            tx.commit();
            return result;
    } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

        public Usuario editarEmail(Usuario modificarUsuario) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var repo = new JpaUsuarioRepository(em);
            var usuarrioCore = new UsuarioService(repo);
            var result = usuarrioCore.editarEmail(modificarUsuario);        
            tx.commit();
            return result;    
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
        }

        public Usuario editarDireccion(Usuario modificarUsuario)
        {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var repo = new JpaUsuarioRepository(em);
            var usuarioCore = new UsuarioService(repo);
            var result = usuarioCore.editarDireccion(modificarUsuario);        
            tx.commit();
            return result;    
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
        }
}

package com.igna.tienda.infra.persistence.jpa;

import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.repositories.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityResult;
import jakarta.persistence.NoResultException;

import java.util.UUID;

public class JpaUsuarioRepository implements UsuarioRepository {
    private final EntityManager em;
    public JpaUsuarioRepository(EntityManager em){
        this.em = em;
    }
     @Override
     public Usuario buscarPorEmail(String email) {
        try {
            return em.createQuery("select u from Usuario u where lower(u.email) = :email", Usuario.class)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
     }

     public Usuario buscarPorId(UUID id) {
        try {
            return em.find(Usuario.class, id.toString());
        } catch (NoResultException e) {
            return null;
        }
     }

     public void guardar(Usuario usuario) {
        em.getTransaction().begin();
        em.merge(usuario);
        em.getTransaction().commit();
     }
}

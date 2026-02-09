package com.igna.tienda.infra.persistence.jpa;

import com.igna.tienda.core.domain.Carrito;
import com.igna.tienda.core.repositories.CarritoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.UUID;

public class JpaCarritoRepository implements CarritoRepository {
    private final EntityManager em;

    public JpaCarritoRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Carrito buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        try {
            // Paso 1: trae carrito + items + producto para usar el agregado completo en el CU.
            return em.createQuery(
                    "select distinct c from Carrito c " +
                    "left join fetch c.items i " +
                    "left join fetch i.producto " +
                    "where c.id = :id",
                    Carrito.class
            ).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar carrito por id", e);
        }
    }

    @Override
    public Carrito buscarPorClienteId(UUID clienteId) {
        if (clienteId == null) {
            return null;
        }
        try {
            // Paso 1: busca el carrito unico del cliente y carga items/productos relacionados.
            return em.createQuery(
                    "select distinct c from Carrito c " +
                    "left join fetch c.items i " +
                    "left join fetch i.producto " +
                    "where c.usuarioCliente.id = :clienteId",
                    Carrito.class
            ).setParameter("clienteId", clienteId.toString()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar carrito por cliente", e);
        }
    }

    @Override
    public Carrito guardar(Carrito carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("Carrito no proporcionado");
        }
        try {
            // Paso 1: para entidades nuevas usar persist evita copias internas de merge y dobles inserts.
            if (carrito.getId() == null) {
                em.persist(carrito);
                return carrito;
            }

            // Paso 2: para entidades existentes se mantiene merge (actualizacion).
            return em.merge(carrito);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar carrito", e);
        }
    }
}

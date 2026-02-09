package com.igna.tienda.infra.persistence.jpa;

import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.repositories.PedidoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.UUID;

public class JpaPedidoRepository implements PedidoRepository {
    private final EntityManager em;

    public JpaPedidoRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Pedido buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        try {
            // Paso 1: carga pedido con sus detalles para consulta/edicion de estado.
            return em.createQuery(
                    "select distinct p from Pedido p " +
                    "left join fetch p.usuarioCliente u " +
                    "left join fetch p.items i " +
                    "left join fetch i.producto " +
                    "where p.id = :id",
                    Pedido.class
            ).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar pedido por id", e);
        }
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no proporcionado");
        }
        try {
            // Paso 1: merge persiste alta/actualizacion del pedido.
            return em.merge(pedido);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar pedido", e);
        }
    }

    @Override
    public List<Pedido> listarPorClienteId(UUID clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente no proporcionado");
        }
        try {
            // Paso 1: consulta pedidos del cliente ordenados del mas reciente al mas antiguo.
            return em.createQuery(
                    "select distinct p from Pedido p " +
                    "left join fetch p.usuarioCliente u " +
                    "left join fetch p.items i " +
                    "left join fetch i.producto " +
                    "where p.usuarioCliente.id = :clienteId " +
                    "order by p.fechaCreacion desc",
                    Pedido.class
            ).setParameter("clienteId", clienteId.toString()).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar pedidos por cliente", e);
        }
    }

    @Override
    public List<Pedido> listarTodos() {
        try {
            // Paso 1: listado administrativo completo de pedidos.
            return em.createQuery(
                    "select distinct p from Pedido p " +
                    "left join fetch p.usuarioCliente u " +
                    "left join fetch p.items i " +
                    "left join fetch i.producto " +
                    "order by p.fechaCreacion desc",
                    Pedido.class
            ).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar todos los pedidos", e);
        }
    }
}

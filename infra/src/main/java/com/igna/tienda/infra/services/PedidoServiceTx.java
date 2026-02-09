package com.igna.tienda.infra.services;

import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.enums.EstadoPedido;
import com.igna.tienda.core.services.PedidoService;
import com.igna.tienda.infra.persistence.jpa.JpaPedidoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.UUID;

public class PedidoServiceTx {
    private final EntityManagerFactory emf;

    public PedidoServiceTx(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Pedido buscarPedidoPorId(Long pedidoId) {
        EntityManager em = emf.createEntityManager();
        try {
            // Paso 1: lectura de pedido para detalle/seguimiento.
            var pedidoRepo = new JpaPedidoRepository(em);
            var pedidoCore = new PedidoService(pedidoRepo);
            return pedidoCore.BuscarPedidoPorId(pedidoId);
        } finally {
            em.close();
        }
    }

    public List<Pedido> listarPedidosPorCliente(UUID clienteId) {
        EntityManager em = emf.createEntityManager();
        try {
            // Paso 1: lectura de pedidos del cliente.
            var pedidoRepo = new JpaPedidoRepository(em);
            var pedidoCore = new PedidoService(pedidoRepo);
            return pedidoCore.ListarPedidosPorCliente(clienteId);
        } finally {
            em.close();
        }
    }

    public List<Pedido> listarTodosLosPedidos() {
        EntityManager em = emf.createEntityManager();
        try {
            // Paso 1: lectura de pedidos global para administrador.
            var pedidoRepo = new JpaPedidoRepository(em);
            var pedidoCore = new PedidoService(pedidoRepo);
            return pedidoCore.ListarTodosLosPedidos();
        } finally {
            em.close();
        }
    }

    public Pedido cambiarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            // Paso 1: transaccion para cambiar estado del pedido.
            tx.begin();
            var pedidoRepo = new JpaPedidoRepository(em);
            var pedidoCore = new PedidoService(pedidoRepo);

            Pedido pedido = pedidoCore.CambiarEstadoPedido(pedidoId, nuevoEstado);
            tx.commit();
            return pedido;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

package com.igna.tienda.infra.services;

import com.igna.tienda.core.domain.Carrito;
import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.services.CarritoService;
import com.igna.tienda.infra.persistence.jpa.JpaCarritoRepository;
import com.igna.tienda.infra.persistence.jpa.JpaPedidoRepository;
import com.igna.tienda.infra.persistence.jpa.JpaProductoRepository;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.UUID;

public class CarritoServiceTx {
    private final EntityManagerFactory emf;

    public CarritoServiceTx(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Carrito obtenerCarrito(UUID clienteId) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            // Paso 1: se usa transaccion porque el CU puede crear carrito si no existe.
            tx.begin();
            var carritoRepo = new JpaCarritoRepository(em);
            var pedidoRepo = new JpaPedidoRepository(em);
            var productoRepo = new JpaProductoRepository(em);
            var usuarioRepo = new JpaUsuarioRepository(em);
            var carritoCore = new CarritoService(carritoRepo, pedidoRepo, productoRepo, usuarioRepo);

            Carrito carrito = carritoCore.ObtenerCarrito(clienteId);
            tx.commit();
            return carrito;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Carrito agregarProductoAlCarrito(UUID clienteId, Long productoId, int cantidad) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            // Paso 1: iniciar transaccion para alta/modificacion de carrito.
            tx.begin();
            var carritoRepo = new JpaCarritoRepository(em);
            var pedidoRepo = new JpaPedidoRepository(em);
            var productoRepo = new JpaProductoRepository(em);
            var usuarioRepo = new JpaUsuarioRepository(em);
            var carritoCore = new CarritoService(carritoRepo, pedidoRepo, productoRepo, usuarioRepo);

            Carrito carrito = carritoCore.AgregarProducto(clienteId, productoId, cantidad);
            tx.commit();
            return carrito;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Carrito modificarCantidadProducto(UUID clienteId, Long productoId, int nuevaCantidad) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            // Paso 1: iniciar transaccion para actualizar item del carrito.
            tx.begin();
            var carritoRepo = new JpaCarritoRepository(em);
            var pedidoRepo = new JpaPedidoRepository(em);
            var productoRepo = new JpaProductoRepository(em);
            var usuarioRepo = new JpaUsuarioRepository(em);
            var carritoCore = new CarritoService(carritoRepo, pedidoRepo, productoRepo, usuarioRepo);

            Carrito carrito = carritoCore.ModificarCantidadProducto(clienteId, productoId, nuevaCantidad);
            tx.commit();
            return carrito;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Carrito eliminarProductoDelCarrito(UUID clienteId, Long productoId) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            // Paso 1: iniciar transaccion para eliminar item del carrito.
            tx.begin();
            var carritoRepo = new JpaCarritoRepository(em);
            var pedidoRepo = new JpaPedidoRepository(em);
            var productoRepo = new JpaProductoRepository(em);
            var usuarioRepo = new JpaUsuarioRepository(em);
            var carritoCore = new CarritoService(carritoRepo, pedidoRepo, productoRepo, usuarioRepo);

            Carrito carrito = carritoCore.EliminarProducto(clienteId, productoId);
            tx.commit();
            return carrito;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Pedido finalizarCompra(UUID clienteId) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            // Paso 1: iniciar transaccion para convertir carrito en pedido.
            tx.begin();
            var carritoRepo = new JpaCarritoRepository(em);
            var pedidoRepo = new JpaPedidoRepository(em);
            var productoRepo = new JpaProductoRepository(em);
            var usuarioRepo = new JpaUsuarioRepository(em);
            var carritoCore = new CarritoService(carritoRepo, pedidoRepo, productoRepo, usuarioRepo);

            Pedido pedido = carritoCore.FinalizarCompra(clienteId);
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

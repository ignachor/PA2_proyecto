package com.igna.tienda.infra.services;

import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.Usuario;
import com.igna.tienda.core.domain.enums.CategoriaProducto;
import com.igna.tienda.core.domain.enums.EstadoPedido;
import com.igna.tienda.infra.persistence.jpa.JpaPedidoRepository;
import com.igna.tienda.infra.persistence.jpa.JpaProductoRepository;
import com.igna.tienda.infra.persistence.jpa.JpaUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.igna.tienda.core.services.AdminService;
import com.igna.tienda.core.services.PedidoService;
import com.igna.tienda.core.services.ProductoService;
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

    //AGREGAR PRODUCTOS 
    public void agregarProducto(Producto producto){
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var repo = new JpaProductoRepository(em);
            var productoCore = new ProductoService(repo);
            productoCore.AltaProducto(producto);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();

    }
}

    //DAR DE BAJA PRODUCTO
    public void darBajaProducto(Producto producto){
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var repo = new JpaProductoRepository(em);
            var productoCore = new ProductoService(repo);
            productoCore.BajaProducto(producto);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    //MODIFICAR PRODUCTO
    public void modificarProducto(Producto producto){
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var repo = new JpaProductoRepository(em);
            var productoCore = new ProductoService(repo);
            productoCore.ModificarProducto(producto);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    //LISTAR PRODUCTOS

    public List<Producto> listaProductos(){
        EntityManager em = emf.createEntityManager();
        try {
            var repo = new JpaProductoRepository(em);
            var productoCore = new ProductoService(repo);
            return productoCore.ListarProductos();
        } finally {
            em.close();
        }
    }

    //Buscar Productos 

    public Producto buscarProducto(String nombreBuscar){
        EntityManager em = emf.createEntityManager();
        try {
            var repo = new JpaProductoRepository(em);
            var productoCore = new ProductoService(repo);
            // Flujo de admin: busca aunque el producto este dado de baja.
            return productoCore.BuscarProductoAdmin(nombreBuscar);
        } finally {
            em.close();
        }
    }

    //Buscar Productos por Categoria

    public List<Producto> buscarProductosPorCategoria(CategoriaProducto categoriaProducto)
    {

        EntityManager em = emf.createEntityManager();
        try {
            var repo = new JpaProductoRepository(em);
            var productoCore = new ProductoService(repo);
            return productoCore.BuscarProductoCategoria(categoriaProducto);
        } finally {
            em.close();
        }
    }

    // LISTAR TODOS LOS PEDIDOS (ADMIN)
    public List<Pedido> listarTodosPedidos() {
        EntityManager em = emf.createEntityManager();
        try {
            var repo = new JpaPedidoRepository(em);
            var pedidoCore = new PedidoService(repo);
            return pedidoCore.ListarTodosLosPedidos();
        } finally {
            em.close();
        }
    }

    // CAMBIAR ESTADO DE PEDIDO (ADMIN)
    public Pedido cambiarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado) {
        EntityManager em = emf.createEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            var repo = new JpaPedidoRepository(em);
            var pedidoCore = new PedidoService(repo);
            Pedido pedidoActualizado = pedidoCore.CambiarEstadoPedido(pedidoId, nuevoEstado);
            tx.commit();
            return pedidoActualizado;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

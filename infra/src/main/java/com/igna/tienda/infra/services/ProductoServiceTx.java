package com.igna.tienda.infra.services;

import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.enums.CategoriaProducto;
import com.igna.tienda.core.services.ProductoService;
import com.igna.tienda.infra.persistence.jpa.JpaProductoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ProductoServiceTx {
    private final EntityManagerFactory emf;

    public ProductoServiceTx(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Producto buscarProductoPorNombre(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            // Paso 1: flujo de usuario; aplica filtro de producto activo.
            var repo = new JpaProductoRepository(em);
            var productoCore = new ProductoService(repo);
            return productoCore.BuscarProducto(nombre);
        } finally {
            em.close();
        }
    }

    public List<Producto> listarProductosActivos() {
        EntityManager em = emf.createEntityManager();
        try {
            // Paso 1: listado de catalogo visible para cliente.
            var repo = new JpaProductoRepository(em);
            var productoCore = new ProductoService(repo);
            return productoCore.ListarProductosActivos();
        } finally {
            em.close();
        }
    }

    public List<Producto> buscarProductosPorCategoria(CategoriaProducto categoriaProducto) {
        EntityManager em = emf.createEntityManager();
        try {
            // Paso 1: filtro por categoria en modo usuario (solo activos).
            var repo = new JpaProductoRepository(em);
            var productoCore = new ProductoService(repo);
            return productoCore.BuscarProductoCategoriaUsuario(categoriaProducto);
        } finally {
            em.close();
        }
    }
}

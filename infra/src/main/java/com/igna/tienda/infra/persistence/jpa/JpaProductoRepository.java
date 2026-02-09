package com.igna.tienda.infra.persistence.jpa;

import java.util.List;

import javax.management.RuntimeErrorException;

import com.igna.tienda.core.repositories.ProductoRepository;
import jakarta.persistence.EntityManager;

import com.igna.tienda.core.domain.Producto;
import com.igna.tienda.core.domain.enums.CategoriaProducto;

public class JpaProductoRepository implements ProductoRepository {
private final EntityManager em;
    public JpaProductoRepository(EntityManager em){
        this.em = em;
    }
    @Override
    public Producto buscarPorID(Long id) {

            try {
                return em.find(Producto.class, id);
            } catch (Exception e) {
                return null;
            }
    };



    @Override
    public Producto guardar(Producto producto) {

        try {
            em.merge(producto);
            return producto;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el producto", e);
        }
    };
    @Override
    public List<Producto> listarProductos() {

        try {
            return em.createQuery("select p from Producto p", Producto.class).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar los productos", e);
        }
    };

    @Override
    public Producto buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        try {
            List<Producto> resultados = em.createQuery(
                    "select p from Producto p where lower(trim(p.nombre)) = :nombre order by p.id desc",
                    Producto.class
            ).setParameter("nombre", nombre.trim().toLowerCase())
             .setMaxResults(1)
             .getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el producto por nombre", e);
        }
    }

    @Override 
    public List<Producto> buscarProductosPorCategoria(CategoriaProducto categoriaProducto)
    {
        try{
            return em.createQuery("select p from Producto p where p.categoria = :categoria", 
            Producto.class).setParameter("categoria", categoriaProducto).getResultList();
        }catch(Exception e){
            throw new RuntimeException("Error al buscar productos por categoria", e);
        }
    }
}

package com.igna.tienda.infra.persistence.jpa;

import java.util.List;
import com.igna.tienda.core.repositories.ProductoRepository;
import jakarta.persistence.EntityManager;

import com.igna.tienda.core.domain.Producto;

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
    public Producto buscarPorNombre(String nombre) {
        try {
            return em.find(Producto.class, nombre);
        } catch (Exception e) {
            return null;
        }
    }


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
}

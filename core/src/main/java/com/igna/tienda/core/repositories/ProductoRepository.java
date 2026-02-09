package com.igna.tienda.core.repositories;
import java.util.List;

import com.igna.tienda.core.domain.Producto;
public interface ProductoRepository {
    Producto buscarPorID(Long id);
    Producto buscarPorNombre(String nombre);
    Producto guardar(Producto producto);
    List<Producto> listarProductos();
}

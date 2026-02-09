package com.igna.tienda.core.repositories;

import com.igna.tienda.core.domain.Carrito;

import java.util.UUID;

public interface CarritoRepository {
    // CU carrito: obtener un carrito por su id.
    Carrito buscarPorId(Long id);

    // CU carrito: obtener carrito del cliente (si existe).
    Carrito buscarPorClienteId(UUID clienteId);

    // CU carrito: persistir cambios del carrito.
    Carrito guardar(Carrito carrito);
}

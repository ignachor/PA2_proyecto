package com.igna.tienda.core.repositories;

import com.igna.tienda.core.domain.Pedido;

import java.util.List;
import java.util.UUID;

public interface PedidoRepository {
    // CU pedidos: buscar pedido puntual por id.
    Pedido buscarPorId(Long id);

    // CU pedidos: persistir pedido.
    Pedido guardar(Pedido pedido);

    // CU pedidos: listar pedidos de un cliente.
    List<Pedido> listarPorClienteId(UUID clienteId);

    // CU pedidos: listar todos los pedidos (admin).
    List<Pedido> listarTodos();
}

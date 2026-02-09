package com.igna.tienda.core.services;

import com.igna.tienda.core.domain.Pedido;
import com.igna.tienda.core.domain.enums.EstadoPedido;
import com.igna.tienda.core.repositories.PedidoRepository;

import java.util.List;
import java.util.UUID;

public class PedidoService {
    private final PedidoRepository pedidoRepo;

    public PedidoService(PedidoRepository pedidoRepo) {
        this.pedidoRepo = pedidoRepo;
    }

    // CU: obtener pedido puntual por id.
    public Pedido BuscarPedidoPorId(Long pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido no proporcionado");
        }
        Pedido pedido = pedidoRepo.buscarPorId(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no encontrado");
        }
        return pedido;
    }

    // CU: listar pedidos del usuario logueado.
    public List<Pedido> ListarPedidosPorCliente(UUID clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente no proporcionado");
        }
        return pedidoRepo.listarPorClienteId(clienteId);
    }

    // CU admin: listar todos los pedidos.
    public List<Pedido> ListarTodosLosPedidos() {
        return pedidoRepo.listarTodos();
    }

    // CU admin: cambiar estado del pedido.
    public Pedido CambiarEstadoPedido(Long pedidoId, EstadoPedido nuevoEstado) {
        // Paso 1: validar datos de entrada.
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido no proporcionado");
        }
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("Estado no proporcionado");
        }

        // Paso 2: cargar pedido existente.
        Pedido pedido = pedidoRepo.buscarPorId(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no encontrado");
        }

        // Paso 3: aplicar cambio de estado y persistir.
        pedido.cambiarEstado(nuevoEstado);
        return pedidoRepo.guardar(pedido);
    }
}

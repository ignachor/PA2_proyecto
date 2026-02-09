# README Pedido

## Responsabilidad

`Pedido` representa una compra confirmada: cliente, items comprados, fecha de creacion y estado logistico/comercial.

## Reglas de negocio en la entidad

- Relacion `ManyToOne` a `Usuario` (`usuarioCliente`).
- Relacion `OneToMany` a `DetallePedido` (`items`).
- `fechaCreacion` se inicializa en `LocalDateTime.now()`.
- `estado` inicia en `EstadoPedido.PENDIENTE`.
- `addItem(...)` y `removeItem(...)` mantienen consistencia bidireccional.
- `getTotal()` suma subtotales de items.
- `cambiarEstado(...)` valida que el nuevo estado no sea nulo.

## Service (core)

`PedidoService`:

- `BuscarPedidoPorId(...)`
- `ListarPedidosPorCliente(...)`
- `ListarTodosLosPedidos()`
- `CambiarEstadoPedido(...)`

`CarritoService`:

- `FinalizarCompra(...)` crea y persiste `Pedido` desde el `Carrito`.

## Repository (core)

`PedidoRepository` define:

- `buscarPorId(Long id)`
- `guardar(Pedido pedido)`
- `listarPorClienteId(UUID clienteId)`
- `listarTodos()`

## JpaRepository (infra)

`JpaPedidoRepository`:

- Usa `left join fetch` para traer `usuarioCliente`, `items` y `producto` en una sola lectura.
- `guardar(...)` usa `merge`.
- `listarPorClienteId(...)` y `listarTodos()` ordenan por `fechaCreacion desc`.

## ServiceTx (infra)

`PedidoServiceTx`:

- Lecturas por id, por cliente y listado total.
- `cambiarEstadoPedido(...)` con transaccion.

`AdminServiceTx`:

- `listarTodosPedidos()`
- `cambiarEstadoPedido(...)`

`CarritoServiceTx`:

- `finalizarCompra(...)` genera el pedido transaccionalmente.

## Flujo end-to-end (cambio de estado admin)

1. Admin selecciona pedido desde UI.
2. UI llama `AdminServiceTx.cambiarEstadoPedido(id, estado)`.
3. `PedidoService.CambiarEstadoPedido(...)` valida entrada y existencia.
4. `Pedido.cambiarEstado(...)` aplica regla de dominio.
5. `JpaPedidoRepository.guardar(...)` persiste.
6. Commit y refresh de pantalla.



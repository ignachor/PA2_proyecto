# README EstadoPedido

## Responsabilidad

`EstadoPedido` define el ciclo de vida funcional de un pedido.

Valores actuales:

- `PENDIENTE`
- `PAGADO`
- `ENVIADO`
- `ENTREGADO`
- `CANCELADO`

## Service (core)

`PedidoService.CambiarEstadoPedido(...)` recibe este enum y lo aplica via `Pedido.cambiarEstado(...)`.

`CarritoService.FinalizarCompra(...)` crea pedidos en estado inicial (`PENDIENTE` por defecto de entidad).

## Repository (core)

No tiene repository propio.

Se persiste como campo de `Pedido` usando `PedidoRepository`.

## JpaRepository (infra)

`JpaPedidoRepository` persiste y consulta `Pedido`; el estado viaja como parte de la entidad.

## ServiceTx (infra)

- `PedidoServiceTx.cambiarEstadoPedido(...)`
- `AdminServiceTx.cambiarEstadoPedido(...)`

## Flujo end-to-end (ejemplo)

1. Admin selecciona pedido y estado nuevo en UI.
2. UI llama ServiceTx de admin/pedido.
3. Service valida pedido y estado.
4. Repository guarda pedido actualizado.
5. UI refresca tabla con nuevo estado.



# README DetallePedido

## Responsabilidad

`DetallePedido` representa una linea inmutable de compra dentro de un pedido: producto, cantidad y precio unitario al momento de la compra.

## Reglas de negocio en la entidad

- Relacion `ManyToOne` a `Pedido` y `Producto`.
- `cantidad` debe ser mayor a cero.
- `precioUnitario` debe ser no nulo y no negativo.
- `getSubtotal()` retorna `precioUnitario * cantidad`.
- El campo `precioUnitario` funciona como snapshot historico de precio.

## Service (core)

No existe `Service` exclusivo para `DetallePedido`.

Se construye desde `CarritoService.FinalizarCompra(...)`, donde:

- se valida stock de `Producto`
- se captura `precioUnitario` actual en `BigDecimal`
- se crea `new DetallePedido(...)`
- se agrega al pedido con `pedido.addItem(...)`

## Repository (core)

No existe `DetallePedidoRepository`.

La persistencia se realiza por cascada al guardar el agregado `Pedido`.

## JpaRepository (infra)

No existe `JpaDetallePedidoRepository`.

`JpaPedidoRepository` carga y persiste los detalles mediante la relacion `items`.

## ServiceTx (infra)

No existe `DetallePedidoServiceTx`.

La capa transaccional relevante es:

- `CarritoServiceTx.finalizarCompra(...)`
- `PedidoServiceTx` para lectura y cambio de estado del pedido

## Flujo end-to-end (ejemplo)

1. Cliente confirma pago.
2. `CarritoService.FinalizarCompra(...)` convierte cada `DetalleCarrito` en `DetallePedido`.
3. `Pedido` se persiste con `pedidoRepo.guardar(...)`.
4. Queda trazabilidad de precio historico por item.

## Extension recomendada

- Agregar descuentos/impuestos por linea si el dominio lo requiere.
- Guardar nombre y categoria como snapshot adicional para reportes historicos.

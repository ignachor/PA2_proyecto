# README DetalleCarrito

## Responsabilidad

`DetalleCarrito` representa una linea de carrito: referencia a producto + cantidad seleccionada por el cliente.

## Reglas de negocio en la entidad

- Relacion `ManyToOne` a `Carrito` y `Producto`.
- Restriccion unica en BD: `(carrito_id, producto_id)` para no duplicar lineas identicas.
- `cantidad` debe ser mayor a cero.
- `sumarCantidad(...)` incrementa la cantidad existente.
- `getSubtotal()` calcula `precioProducto * cantidad`.

## Service (core)

No existe `Service` dedicado para `DetalleCarrito`.

Se gestiona indirectamente desde:

- `CarritoService.AgregarProducto(...)`
- `CarritoService.ModificarCantidadProducto(...)`
- `CarritoService.EliminarProducto(...)`
- `CarritoService.FinalizarCompra(...)`

## Repository (core)

No existe `DetalleCarritoRepository`.

Las persistencias se realizan por cascada desde `CarritoRepository` a traves de:

- `CarritoRepository.guardar(Carrito carrito)`

## JpaRepository (infra)

No existe `JpaDetalleCarritoRepository`.

El manejo de filas se realiza via `JpaCarritoRepository` y las relaciones `cascade = CascadeType.ALL` + `orphanRemoval = true`.

## ServiceTx (infra)

No existe `DetalleCarritoServiceTx`.

Las operaciones transaccionales se exponen en `CarritoServiceTx`.

## Flujo end-to-end (ejemplo)

1. Cliente agrega producto.
2. `CarritoService` construye `new DetalleCarrito(producto, cantidad)`.
3. `Carrito.anadirProducto(...)` fusiona o agrega item.
4. `JpaCarritoRepository.guardar(...)` persiste la coleccion.

## Extension recomendada

- Guardar snapshot de precio unitario en carrito si se necesita congelar precio antes de pagar.
- Agregar validacion de stock maximo por item directamente en entidad si se quiere reforzar invariante.

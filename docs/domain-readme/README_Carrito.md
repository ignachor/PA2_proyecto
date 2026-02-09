# README Carrito

## Responsabilidad

`Carrito` es el agregado que concentra los productos que un cliente quiere comprar antes de generar un pedido.

## Reglas de negocio en la entidad

- Un carrito pertenece a un solo usuario (`OneToOne`, `cliente_id` unico).
- Contiene items `DetalleCarrito` con `orphanRemoval=true`.
- `anadirProducto(...)` fusiona cantidades si el producto ya existe en el carrito.
- `removerProducto(...)` corta relacion bidireccional.
- `vaciar()` elimina todos los items.
- `getTotal()` suma subtotales.

## Service (core)

`CarritoService`:

- `ObtenerCarrito(UUID clienteId)` crea carrito si no existe.
- `AgregarProducto(...)` valida stock/cantidad y agrega item.
- `ModificarCantidadProducto(...)` actualiza cantidad o elimina si llega `0`.
- `EliminarProducto(...)` remueve item.
- `FinalizarCompra(...)`:
  - crea `Pedido`
  - transforma items en `DetallePedido`
  - descuenta stock en `Producto`
  - persiste pedido
  - vacia carrito

## Repository (core)

`CarritoRepository` define:

- `buscarPorId(Long id)`
- `buscarPorClienteId(UUID clienteId)`
- `guardar(Carrito carrito)`

## JpaRepository (infra)

`JpaCarritoRepository`:

- Queries con `left join fetch` para cargar items y producto.
- `guardar(...)`:
  - usa `persist` si el carrito es nuevo (`id == null`)
  - usa `merge` si es existente
- Este enfoque evita inserts duplicados sobre `cliente_id` unico.

## ServiceTx (infra)

`CarritoServiceTx` encapsula frontera transaccional para todos los CU:

- `obtenerCarrito(...)`
- `agregarProductoAlCarrito(...)`
- `modificarCantidadProducto(...)`
- `eliminarProductoDelCarrito(...)`
- `finalizarCompra(...)`

En todos los casos arma repos (`JpaCarritoRepository`, `JpaPedidoRepository`, `JpaProductoRepository`, `JpaUsuarioRepository`) y ejecuta rollback ante excepcion.

## Flujo end-to-end (ejemplo)

1. Cliente pulsa "Agregar al carrito".
2. UI llama `CarritoServiceTx.agregarProductoAlCarrito(...)`.
3. `CarritoService.AgregarProducto(...)` valida producto y recupera/crea carrito.
4. `Carrito.anadirProducto(...)` aplica merge de linea por producto.
5. `JpaCarritoRepository.guardar(...)` persiste.
6. Commit de transaccion.

## Extension recomendada

- Manejar concurrencia optimista para evitar sobreventa en alta carga.
- Extraer politicas de stock a un servicio de inventario.
- Agregar eventos de dominio para auditoria de cambios en carrito.

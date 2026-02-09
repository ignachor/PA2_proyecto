# README Producto

## Responsabilidad

`Producto` modela un item del catalogo comercial: identificacion, categoria, precio, stock fisico y estado de visibilidad (`stock`).

## Reglas de negocio en la entidad

Campos relevantes:

- `nombre`, `descripcion`
- `categoria` (enum `CategoriaProducto` con converter)
- `precio`
- `cantidad` y `cantidadMinimo`
- `fechaVencimiento` (actualmente `int`)
- `stock` (activo/inactivo en busquedas de cliente)

Metodos de dominio:

- `hayStock()` / `noHayStock()`
- `descontarCantidad(int)` para compra
- `aumentarCantidad(int)` para reposicion
- `cambiarDatosProducto(...)` para edicion admin

## Service (core)

`ProductoService`:

- `AltaProducto(...)`: valida obligatorios y persiste.
- `BajaProducto(...)`: marca `stock=false`.
- `ModificarProducto(...)`: aplica cambios sobre entidad persistida.
- `ListarProductos()` y `ListarProductosActivos()`
- `BuscarProducto(...)` (cliente: solo activos)
- `BuscarProductoAdmin(...)` (admin: incluye dados de baja)
- `BuscarProductoCategoria(...)` y `BuscarProductoCategoriaUsuario(...)`

`CarritoService` tambien usa `Producto` para:

- validar stock/cantidad al agregar al carrito
- descontar stock en `FinalizarCompra(...)`

## Repository (core)

`ProductoRepository` define:

- `buscarPorID(Long id)`
- `buscarPorNombre(String nombre)`
- `buscarProductosPorCategoria(CategoriaProducto categoria)`
- `guardar(Producto producto)`
- `listarProductos()`

## JpaRepository (infra)

`JpaProductoRepository`:

- `buscarPorID(...)` con `find`.
- `buscarPorNombre(...)` con `lower(trim(nombre))` y `setMaxResults(1)`.
- `buscarProductosPorCategoria(...)` por igualdad de enum.
- `guardar(...)` via `merge`.
- `listarProductos()` con query simple.

## ServiceTx (infra)

`ProductoServiceTx` (flujo cliente):

- `buscarProductoPorNombre(...)`
- `listarProductosActivos()`
- `buscarProductosPorCategoria(...)`

`AdminServiceTx` (flujo admin):

- `agregarProducto(...)`
- `darBajaProducto(...)`
- `modificarProducto(...)`
- `listaProductos()`
- `buscarProducto(...)`
- `buscarProductosPorCategoria(...)`

## Flujo end-to-end (ejemplo)

1. Admin crea producto desde UI.
2. `AdminServiceTx.agregarProducto(...)` abre transaccion.
3. `ProductoService.AltaProducto(...)` valida reglas y normaliza stock.
4. `JpaProductoRepository.guardar(...)` inserta/actualiza.
5. Commit de transaccion.


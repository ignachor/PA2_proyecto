# README CategoriaProducto

## Responsabilidad

`CategoriaProducto` es el enum canonico de categorias de negocio para `Producto`.

Valores actuales:

- `ELECTRONICA`
- `HOGAR`
- `MODA`
- `ALIMENTOS_PERECEDEROS`
- `ALIMENTOS_NO_PERECEDEROS`

## Reglas de negocio en el enum

- Cada constante tiene `descripcion` valida (no nula ni vacia).
- `fromDescripcion(...)` permite resolver categoria por descripcion textual.

## Service (core)

Se utiliza principalmente en `ProductoService`:

- `AltaProducto(...)`
- `ModificarProducto(...)`
- `BuscarProductoCategoria(...)`
- `BuscarProductoCategoriaUsuario(...)`

Tambien se usa en `AdminServiceTx` y `ProductoServiceTx` para filtrar catalogo.

## Repository (core)

`ProductoRepository.buscarProductosPorCategoria(CategoriaProducto categoria)` usa este enum como criterio.

## JpaRepository (infra)

`JpaProductoRepository.buscarProductosPorCategoria(...)` compara por igualdad del campo `categoria`.

## ServiceTx (infra)

- `AdminServiceTx.buscarProductosPorCategoria(...)`
- `ProductoServiceTx.buscarProductosPorCategoria(...)`

## Flujo end-to-end (ejemplo)

1. UI selecciona categoria desde combo.
2. Se pasa enum `CategoriaProducto` al ServiceTx.
3. Service core delega en repository.
4. Jpa query retorna productos de esa categoria.

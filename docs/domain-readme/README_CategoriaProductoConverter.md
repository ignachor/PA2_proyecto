# README CategoriaProductoConverter

## Responsabilidad

`CategoriaProductoConverter` adapta la persistencia de `CategoriaProducto` para BD y compatibilidad con datos legacy.

## Reglas de conversion

`convertToDatabaseColumn(...)`:

- guarda `attribute.name()` si no es nulo.

`convertToEntityAttribute(...)`:

- intenta parseo directo por `valueOf`.
- fallback por `descripcion` del enum.
- fallback por `ordinal` numerico legacy.
- fallback por alias historicos:
  - `LACTEOS`, `BEBIDAS`, `CONGELADOS` -> `ALIMENTOS_PERECEDEROS`
  - `ALMACEN` -> `ALIMENTOS_NO_PERECEDEROS`
  - `LIMPIEZA`, `HIGIENE`, `HOGAR` -> `HOGAR`
  - `OTROS` -> `MODA`
  - `ELECTRONICA`, `ELECTRO`, `TECNOLOGIA` -> `ELECTRONICA`

## Service (core)

No tiene Service dedicado.

Afecta indirectamente a `ProductoService`, porque toda lectura/escritura de producto depende de esta conversion al hidratar entidad.

## Repository (core)

No tiene contrato propio de repository.

Opera como detalle tecnico del campo `Producto.categoria`.

## JpaRepository (infra)

Se ejecuta automaticamente cuando JPA lee/escribe `Producto` (campo con `@Convert(converter = CategoriaProductoConverter.class)`).

## ServiceTx (infra)

No tiene ServiceTx dedicado.

Impacta todos los flujos transaccionales que guardan o leen `Producto`:

- `AdminServiceTx`
- `ProductoServiceTx`
- `CarritoServiceTx` (lectura/actualizacion de producto en compra)

## Flujo end-to-end (ejemplo)

1. Se lee fila de `producto`.
2. JPA obtiene valor crudo de `categoria`.
3. Converter normaliza y mapea al enum.
4. Service recibe entidad consistente para reglas de negocio.

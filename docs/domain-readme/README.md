# Documentacion por Clase de Dominio

Este directorio contiene un README por cada clase de `core/domain`, con su trazabilidad en capas:

- Dominio (entidad/value/enum)
- Service (core)
- Repository (core)
- JpaRepository (infra/persistence/jpa)
- ServiceTx (infra/services)

## Indice

- `docs/domain-readme/README_Usuario.md`
- `docs/domain-readme/README_Producto.md`
- `docs/domain-readme/README_Carrito.md`
- `docs/domain-readme/README_DetalleCarrito.md`
- `docs/domain-readme/README_Pedido.md`
- `docs/domain-readme/README_DetallePedido.md`
- `docs/domain-readme/README_Direccion.md`
- `docs/domain-readme/README_CategoriaProducto.md`
- `docs/domain-readme/README_CategoriaProductoConverter.md`
- `docs/domain-readme/README_EstadoPedido.md`
- `docs/domain-readme/README_Rol.md`
- `docs/domain-readme/README_Flujos.md`

## Convenciones de lectura

- `Responsabilidad`: que modela la clase en negocio.
- `Reglas`: invariantes y validaciones.
- `Capas`: metodos que la usan en Service, Repository, Jpa y ServiceTx.
- `Flujo`: recorrido completo desde UI hasta BD.
- `Extension`: recomendaciones para evolucionar sin romper la arquitectura.

## Mapa rapido por agregado

- Usuario:
  - Service: `AuthService`, `UsuarioService`, `AdminService`
  - Repository: `UsuarioRepository`
  - Jpa: `JpaUsuarioRepository`
  - ServiceTx: `AuthServiceTx`, `UsuarioServiceTx`, `AdminServiceTx`
- Producto:
  - Service: `ProductoService`, `CarritoService` (consumo en compra)
  - Repository: `ProductoRepository`
  - Jpa: `JpaProductoRepository`
  - ServiceTx: `ProductoServiceTx`, `AdminServiceTx`, `CarritoServiceTx`
- Carrito:
  - Service: `CarritoService`
  - Repository: `CarritoRepository`
  - Jpa: `JpaCarritoRepository`
  - ServiceTx: `CarritoServiceTx`
- Pedido:
  - Service: `PedidoService`, `CarritoService` (creacion)
  - Repository: `PedidoRepository`
  - Jpa: `JpaPedidoRepository`
  - ServiceTx: `PedidoServiceTx`, `AdminServiceTx`, `CarritoServiceTx`

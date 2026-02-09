# Flujos End-to-End del Proyecto

Este documento resume como se conectan UI -> ServiceTx -> Service -> Repository -> JPA -> BD en los casos de uso principales.

## Flujo 1: Registro e inicio de sesion

1. UI captura datos.
2. `AuthServiceTx.registrar(...)` crea `EntityManager`, abre transaccion.
3. `AuthService.registrar(...)` valida email/dni/password y unicidad.
4. `JpaUsuarioRepository.guardar(...)` persiste usuario.
5. Commit y retorno.

Inicio de sesion:

1. UI llama `AuthServiceTx.iniciarSesion(...)`.
2. `AuthService.iniciarSesion(...)` valida credenciales y rol.
3. Si es cliente, valida `activo=true`.
4. Devuelve `Usuario` para routing de menu.

## Flujo 2: Gestion de productos (admin)

Alta:

1. UI admin arma `Producto`.
2. `AdminServiceTx.agregarProducto(...)` abre transaccion.
3. `ProductoService.AltaProducto(...)` valida obligatorios y stock inicial.
4. `JpaProductoRepository.guardar(...)` persiste.
5. Commit.

Baja/Modificacion:

1. UI busca producto por nombre.
2. `AdminServiceTx.buscarProducto(...)` usa flujo admin (incluye inactivos).
3. Se aplica `BajaProducto(...)` o `ModificarProducto(...)`.
4. Se persiste y confirma transaccion.

## Flujo 3: Catalogo cliente, carrito y compra

Busqueda de catalogo:

1. UI cliente usa `ProductoServiceTx`.
2. `ProductoService` filtra productos activos.
3. `JpaProductoRepository` consulta BD.

Carrito:

1. UI agrega/modifica/elimina item.
2. `CarritoServiceTx` abre transaccion.
3. `CarritoService` valida stock y cantidades.
4. `JpaCarritoRepository` persiste agregado carrito.

Finalizar compra:

1. UI confirma pago.
2. `CarritoService.FinalizarCompra(...)`:
   - valida carrito no vacio
   - crea `Pedido`
   - crea `DetallePedido` por item
   - descuenta stock de productos
   - guarda pedido
   - vacia carrito
3. Commit transaccional.

## Flujo 4: Gestion de pedidos (admin y cliente)

Consulta cliente:

1. UI cliente llama `PedidoServiceTx.listarPedidosPorCliente(...)`.
2. `PedidoService` delega en `PedidoRepository`.
3. `JpaPedidoRepository` devuelve pedidos con items.

Cambio de estado admin:

1. UI admin selecciona pedido + `EstadoPedido`.
2. `AdminServiceTx.cambiarEstadoPedido(...)` abre transaccion.
3. `PedidoService.CambiarEstadoPedido(...)` valida y aplica cambio.
4. `JpaPedidoRepository.guardar(...)` persiste.
5. Commit y refresh de UI.

## Nota tecnica importante

- Carrito usa `cliente_id` unico. Para entidades nuevas, `JpaCarritoRepository` usa `persist` (no `merge`) para evitar duplicados.
- Pedido y Carrito usan `left join fetch` para evitar problemas de carga perezosa al pintar UI desktop.

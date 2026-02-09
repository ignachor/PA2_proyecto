# Pedido README

## Objetivo
Documentar el flujo completo del módulo de pedidos para cliente, incluyendo el paso a paso técnico y funcional.

## Flujo funcional (paso a paso)
1. El cliente agrega productos al carrito desde `/tiendaCliente`.
2. En `/carrito`, presiona **Finalizar Compra**.
3. Se ejecuta `POST /carrito/finalizar`.
4. El sistema crea el pedido con los items del carrito.
5. Al finalizar correctamente, redirige a `/pedidos`.
6. En `/pedidos`, el cliente ve su historial de pedidos.
7. Puede abrir un pedido puntual en `/pedidos/{id}` para ver el detalle.

## Endpoints involucrados
- `POST /carrito/finalizar`
  - Crea pedido desde el carrito activo del cliente.
  - Controller: `CarritoController.finalizarCompra(...)`
- `GET /pedidos`
  - Lista pedidos del cliente autenticado.
  - Controller: `PedidoController.listarPedidos(...)`
- `GET /pedidos/{id}`
  - Muestra detalle de un pedido específico del cliente.
  - Controller: `PedidoController.verDetallePedido(...)`

## Vistas involucradas
- `web/src/main/resources/templates/pedidos.html`
  - Listado de pedidos (ID, fecha, estado, total, acciones).
- `web/src/main/resources/templates/pedido-detalle.html`
  - Resumen del pedido y detalle de productos.

## Control de acceso y seguridad
1. Solo usuarios con rol `CLIENTE` acceden a `/pedidos/**`.
2. En el detalle (`/pedidos/{id}`), se valida que el pedido pertenezca al usuario logueado.
3. Si no pertenece, se rechaza y se redirige al listado.

## Reglas de negocio aplicadas
1. Un pedido no se consulta si no existe (manejo de error en servicio/controlador).
2. El estado del pedido se muestra según `EstadoPedido` (`PENDIENTE`, `PAGADO`, `ENVIADO`, `ENTREGADO`, `CANCELADO`).
3. El total del pedido se calcula desde sus items (`pedido.getTotal()`).

## Paso a paso técnico de implementación
1. Se detectó error 500 por template faltante `pedidos`.
2. Se creó `pedidos.html` para listar pedidos del cliente.
3. Se creó `pedido-detalle.html` para mostrar el detalle por ID.
4. Se mantuvo la lógica del controller existente (`PedidoController`) sin romper contratos.
5. Se compiló el módulo `web` para validar que no queden errores de build.

## Cómo probar manualmente
1. Iniciar sesión como cliente.
2. Ir a `/tiendaCliente` y agregar productos al carrito.
3. Ir a `/carrito` y presionar **Finalizar Compra**.
4. Verificar redirección a `/pedidos`.
5. Abrir el detalle de un pedido desde el botón **Ver detalle**.

## Resultado esperado
- No debe aparecer `Error resolving template [pedidos]`.
- El usuario debe poder listar y consultar sus pedidos sin error 500.

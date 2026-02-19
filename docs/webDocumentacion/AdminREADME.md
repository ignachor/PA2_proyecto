# README - Controllers Admin

## Ubicacion
`web/src/main/java/com/igna/tienda/web/controllers/admin`

## Objetivo del modulo Admin
Implementar el apartado web de administracion con control por rol `ADMIN`, incluyendo:
- Menu administrador
- Gestion de usuarios
- Gestion de productos
- Gestion de pedidos

## Seguridad y acceso
La proteccion de rutas se aplica en `SecurityConfig`:
- Todas las rutas `/admin/**` requieren `ROLE_ADMIN`.
- Login redirige por rol:
  - `ADMIN` -> `/admin/menu`
  - `CLIENTE` -> `/tiendaCliente`

## Controllers implementados

### 1) `AdminMenuController`
- Ruta base: `/admin`
- Endpoints:
  - `GET /admin`
  - `GET /admin/`
  - `GET /admin/menu`
- Funcion:
  - Mostrar menu principal del administrador (`admin/menu`).
  - Cargar usuario autenticado en `usuarioAdmin`.

### 2) `AdminUsuarioController`
- Ruta base: `/admin/usuarios`

#### Funcionalidades
1. Busqueda de usuarios por nombre/apellido/email.
2. Vista principal con datos del usuario seleccionado.
3. Vista secundaria para listar y seleccionar usuarios.
4. Activar usuario (alta logica).
5. Desactivar usuario (baja logica).
6. Ver pedidos del usuario seleccionado.
7. Cambiar estado de pedidos del usuario.

#### Endpoints
- `GET /admin/usuarios`
  - Parametros:
    - `busqueda` (opcional)
    - `email` (opcional, usuario seleccionado)
    - `verPedidos` (opcional, default `false`)
- `GET /admin/usuarios/lista`
  - Parametros:
    - `busqueda` (opcional)
- `POST /admin/usuarios/activar`
  - Parametros:
    - `email`
    - `busqueda` (opcional)
- `POST /admin/usuarios/desactivar`
  - Parametros:
    - `email`
    - `busqueda` (opcional)
- `POST /admin/usuarios/pedidos/estado`
  - Parametros:
    - `pedidoId`
    - `nuevoEstado` (`EstadoPedido`)
    - `email`
    - `busqueda` (opcional)

#### Enums usados
- `Rol.values()` para mostrar roles en pantalla.
- `EstadoPedido.values()` para selector de cambio de estado.

#### Notas tecnicas
- Se encodean parametros en redirect (`email`, `busqueda`) con `UriUtils` para evitar errores en URLs.

### 3) `AdminProductoController`
- Ruta base: `/admin/productos`

#### Funcionalidades
1. Listar productos (con filtro por categoria).
2. Alta de producto.
3. Edicion de producto.
4. Baja de producto.

#### Endpoints
- `GET /admin/productos`
  - Parametros:
    - `categoria` (opcional, `CategoriaProducto`)
- `GET /admin/productos/agregar`
- `POST /admin/productos/agregar`
- `GET /admin/productos/editar/{id}`
- `POST /admin/productos/editar`
- `POST /admin/productos/baja/{id}`

#### Ajuste importante realizado
- Se removio dependencia a `adminService.buscarProductoPorId(...)` para evitar error de compilacion cuando se ejecuta solo el modulo `web`.
- Se implemento `obtenerProductoPorId(Long id)` dentro del controller usando `adminService.listaProductos()`.

### 4) `AdminPedidoController`
- Ruta base: `/admin/pedidos`

#### Funcionalidades
1. Listar todos los pedidos.
2. Filtrar pedidos por estado (`EstadoPedido`).
3. Ver detalle de pedido.
4. Cambiar estado de pedido.

#### Endpoints
- `GET /admin/pedidos`
  - Parametros:
    - `estado` (opcional, `EstadoPedido`)
- `GET /admin/pedidos/{id}`
- `POST /admin/pedidos/cambiar-estado`
  - Parametros:
    - `pedidoId`
    - `nuevoEstado` (`EstadoPedido`)

## Templates vinculados
Estos controllers renderizan templates en:
- `web/src/main/resources/templates/admin/menu.html`
- `web/src/main/resources/templates/admin/usuarios.html`
- `web/src/main/resources/templates/admin/usuarios-lista.html`
- `web/src/main/resources/templates/admin/productos.html`
- `web/src/main/resources/templates/admin/producto-form.html`
- `web/src/main/resources/templates/admin/pedidos.html`
- `web/src/main/resources/templates/admin/pedido-detalle.html`

## Flujo funcional principal
1. Login como admin.
2. Redireccion a `/admin/menu`.
3. Entrar a `Usuarios` o `Productos`.
4. En `Usuarios`:
   - Buscar usuario por nombre/apellido.
   - Listar y seleccionar usuario.
   - Activar/Desactivar usuario.
   - Ver pedidos del usuario.
   - Cambiar estado del pedido.

## Estado de compilacion
Compila correctamente en `web`:
```bash
mvn -q -DskipTests clean compile
```

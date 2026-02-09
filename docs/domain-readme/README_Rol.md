# README Rol

## Responsabilidad

`Rol` clasifica usuarios por permisos macro del sistema.

Valores:

- `ADMIN`
- `CLIENTE`

## Service (core)

`AuthService.iniciarSesion(...)` usa `Rol` para:

- validar estado activo solo en clientes.
- permitir acceso administrativo sin esa restriccion.

`Usuario` usa `rol` en:

- `esCliente()`
- `esAdmin()`

## Repository (core)

No existe repository de `Rol`.

Se persiste embebido dentro de `Usuario` via `UsuarioRepository`.

## JpaRepository (infra)

`JpaUsuarioRepository` gestiona lectura/escritura del usuario completo (incluyendo rol).

## ServiceTx (infra)

No existe `RolServiceTx`.

Se usa indirectamente desde:

- `AuthServiceTx` (registro/login)
- `AdminServiceTx` (operaciones administrativas)

## Flujo end-to-end (ejemplo)

1. Usuario inicia sesion.
2. `AuthService` valida credenciales.
3. Se evalua `usuario.getRol()`.
4. UI decide abrir menu de cliente o menu de admin.


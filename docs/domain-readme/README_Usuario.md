# README Usuario

## Responsabilidad

`Usuario` representa la identidad de una persona dentro del sistema (cliente o admin), sus datos personales, su direccion, su estado de activacion y sus relaciones con carrito y pedidos.

## Reglas de negocio en la entidad

- `id` se persiste como `String` (UUID serializado), pero el getter expone `UUID`.
- `email` es unico en BD (`usuario.email`).
- `rol` define permisos de alto nivel (`ADMIN` o `CLIENTE`).
- `activo` controla si un cliente puede iniciar sesion.
- Tiene relacion `OneToOne` con `Carrito` y `OneToMany` con `Pedido`.

Metodos clave de dominio:

- `activar()` / `desactivar()`
- `cambiarEmail(...)`
- `cambiarDatosPersonales(...)`
- `cambiarDireccion(...)`
- `crearCarrito(...)`
- `esCliente()` / `esAdmin()`

## Service (core)

`AuthService`:

- `registrar(...)`: valida email/password/dni, verifica duplicados y persiste.
- `iniciarSesion(...)`: valida credenciales y estado del usuario cliente.

`UsuarioService`:

- `editarDatosPersonales(...)`
- `editarEmail(...)`
- `editarDireccion(...)`

`AdminService`:

- `ActivarUsuario(...)`
- `DesactivarUsuario(...)`
- `BuscarUsuario(...)`
- `ListarUsuarios()`

## Repository (core)

`UsuarioRepository` define:

- `buscarPorEmail(String email)`
- `buscarPorId(UUID id)`
- `guardar(Usuario usuario)`
- `listarTodos()`

## JpaRepository (infra)

`JpaUsuarioRepository` implementa el contrato con JPA:

- `buscarPorEmail(...)` usa query por email normalizado.
- `buscarPorId(...)` usa `EntityManager.find`.
- `guardar(...)` usa `merge`.
- `listarTodos()` retorna lista completa.

## ServiceTx (infra)

`AuthServiceTx`:

- `registrar(...)` usa transaccion (begin/commit/rollback).
- `iniciarSesion(...)` lectura sin transaccion explicita.

`UsuarioServiceTx`:

- Envoltorio transaccional para edicion de datos/email/direccion.

`AdminServiceTx`:

- `activarUsuarioPorEmail(...)` y `desactivarUsuarioPorEmail(...)` con transaccion.
- `listarUsuarios()` como lectura.

## Flujo end-to-end (ejemplo)

1. UI solicita editar email.
2. `UsuarioServiceTx.editarEmail(...)` abre transaccion.
3. `UsuarioService.editarEmail(...)` valida password y unicidad.
4. `JpaUsuarioRepository.guardar(...)` persiste cambios.
5. Se confirma transaccion y retorna usuario actualizado.


# README Direccion

## Responsabilidad

`Direccion` es un `@Embeddable` usado por `Usuario` para modelar domicilio sin identidad propia.

## Reglas de negocio en la clase

- Contiene: `calle`, `numero`, `ciudad`, `provincia`, `codigoPostal`.
- Implementa `equals/hashCode` por valor.
- Tiene constructor protegido para JPA y constructor publico completo.

## Service (core)

No existe `Service` exclusivo para `Direccion`.

El flujo se maneja desde:

- `UsuarioService.editarDireccion(...)`
- `Usuario.cambiarDireccion(...)`

## Repository (core)

No existe `DireccionRepository`.

Se persiste como parte de `Usuario` via `UsuarioRepository.guardar(...)`.

## JpaRepository (infra)

No existe `JpaDireccionRepository`.

`JpaUsuarioRepository` persiste la direccion embebida al hacer `merge(usuario)`.

## ServiceTx (infra)

No existe `DireccionServiceTx`.

`UsuarioServiceTx.editarDireccion(...)` abre transaccion y delega a `UsuarioService`.

## Flujo end-to-end (ejemplo)

1. Cliente edita su domicilio.
2. UI crea `Usuario` con nueva `Direccion`.
3. `UsuarioServiceTx.editarDireccion(...)` valida datos y persiste usuario.
4. JPA actualiza columnas embebidas en tabla `usuario`.

## Extension recomendada

- Agregar validadores por pais/provincia para formato de codigo postal.
- Considerar tabla separada si se necesita historial de domicilios.

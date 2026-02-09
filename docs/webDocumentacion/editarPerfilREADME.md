# editarPerfilREADME

## Objetivo
Documentar el procedimiento implementado para que:
- `Mi Perfil` sea una vista solo lectura.
- `Editar Perfil` exista como vista separada.
- Las modificaciones se reflejen al volver a `Mi Perfil`.

## Problema inicial
1. El boton `Editar Perfil` estaba en `tiendaCliente`.
2. `perfil.html` mezclaba consulta y edicion en una sola pantalla.
3. Al cambiar email, el flujo llevaba a logout, lo que cortaba la experiencia de actualizacion inmediata.

## Solucion aplicada
Se separo la experiencia en dos pantallas y se ajusto el controller para mantener sesion coherente al cambiar email.

## Cambios realizados

### 1) Quitar boton Editar Perfil de Tienda
- Archivo modificado: `web/src/main/resources/templates/tiendaCliente.html`
- Cambio:
  - Se elimino el bloque del boton `Editar Perfil` del header de la tienda.
  - El header quedo solo con el titulo `Tienda`.

### 2) Convertir Mi Perfil en vista de solo lectura
- Archivo modificado: `web/src/main/resources/templates/perfil.html`
- Cambio:
  - Se reescribio la vista para mostrar datos del usuario sin formularios.
  - Se agrego boton interno `Editar perfil` que navega a `/perfil/editar`.
  - Se muestran:
    - nombre
    - apellido
    - dni
    - email
    - direccion completa (si existe)

### 3) Crear vista separada para edicion
- Archivo nuevo: `web/src/main/resources/templates/perfil-editar.html`
- Cambio:
  - Se creo pantalla con formularios para editar:
    - datos personales (`POST /perfil/datos`)
    - email (`POST /perfil/email`)
    - direccion (`POST /perfil/direccion`)
  - Se agrego boton `Volver a Mi Perfil`.

### 4) Ajustar PerfilController
- Archivo modificado: `web/src/main/java/com/igna/tienda/web/controllers/PerfilController.java`
- Cambios:
  1. Se mantuvo `GET /perfil` como vista de lectura (`perfil`).
  2. Se agrego `GET /perfil/editar` para vista de edicion (`perfil-editar`).
  3. Se mantuvieron los endpoints de actualizacion:
     - `POST /perfil/datos`
     - `POST /perfil/email`
     - `POST /perfil/direccion`
  4. Luego de cada POST, se redirige a `redirect:/perfil` para ver datos actualizados.

### 5) Sincronizar sesion al cambiar email
- Archivo modificado: `web/src/main/java/com/igna/tienda/web/controllers/PerfilController.java`
- Cambio:
  - Se implemento `actualizarAutenticacionConNuevoEmail(...)` para actualizar el `SecurityContext` con el nuevo email.
  - Resultado:
    - no se fuerza logout al cambiar email
    - el usuario sigue autenticado
    - `Mi Perfil` refleja el email actualizado inmediatamente

## Flujo final de usuario
1. El usuario entra a `Mi Perfil` (`/perfil`) y visualiza datos.
2. Presiona `Editar perfil`.
3. Se abre `/perfil/editar` con formularios.
4. Guarda cambios.
5. El sistema redirige a `/perfil`.
6. `Mi Perfil` muestra la informacion actualizada.

## Validacion tecnica
- Se compilo el modulo web con:
```bash
mvn -q -DskipTests compile
```
- Resultado: compilacion exitosa, sin errores.

## Archivos impactados
- `web/src/main/resources/templates/tiendaCliente.html`
- `web/src/main/resources/templates/perfil.html`
- `web/src/main/resources/templates/perfil-editar.html`
- `web/src/main/java/com/igna/tienda/web/controllers/PerfilController.java`

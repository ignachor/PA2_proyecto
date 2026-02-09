# Plan paso a paso: Web REST con Spring Boot (alineado a core/infra)

Objetivo: construir el apartado web con API REST básica en Spring Boot, reutilizando el mismo modelo de la aplicación DESKTOP y apoyándose en los módulos `core` e `infra`.

Este documento detalla cada paso con mayor profundidad para que la implementación sea consistente y fácil de seguir.

## Principios (obligatorios)
- Reutilizar entidades, value objects y reglas de negocio del `core` tal como están en DESKTOP.
- Encapsular persistencia y configuración en `infra`.
- En el módulo web, exponer controladores REST y DTOs sin duplicar lógica de negocio.
- Mantener separación clara entre capas: `web` solo orquesta, `core` decide, `infra` persiste.

## Paso 1: Revisión del modelo DESKTOP (análisis inicial)
1. Identificar entidades y relaciones del dominio:
   - Usuario, Carrito, Producto, Pedido, DetallePedido.
   - Estados posibles (por ejemplo, Pedido: NUEVO, PAGADO, ENVIADO, CANCELADO).
2. Revisar reglas de negocio existentes en `core`:
   - Validaciones de usuario (email único, formato, password).
   - Reglas de carrito (no duplicar productos, cantidades mínimas/máximas).
   - Reglas de pedido (no crear pedido sin items, stock disponible, estado inicial).
3. Mapear casos de uso existentes en DESKTOP:
   - Registro de usuarios.
   - Crear carrito / agregar productos / quitar productos.
   - Confirmar pedido y consultar historial.
4. Detectar gaps para el entorno web:
   - DTOs de entrada/salida.
   - Manejo de errores HTTP.
   - Seguridad (si aplica en esta fase o se deja para una segunda etapa).

## Paso 2: Estructura de módulos y paquetes (arquitectura)
1. Confirmar estructura de proyecto:
   - `core`: dominio y reglas de negocio.
   - `infra`: persistencia y configuración.
   - `web`: API REST y controladores.
2. Proponer paquetes internos:
   - `core.domain` (entidades, value objects).
   - `core.service` (casos de uso / servicios de aplicación).
   - `core.port` (interfaces de repositorio o gateways).
   - `infra.persistence` (entidades JPA, repositorios, mappers).
   - `infra.config` (datasource, JPA, properties).
   - `infra.adapter` (implementaciones de puertos).
   - `web.controller` (endpoints REST).
   - `web.dto` (request/response).
   - `web.mapper` (DTO <-> dominio).
3. Definir dependencias entre módulos:
   - `web` depende de `core`.
   - `infra` depende de `core`.
   - `core` no depende de ningún otro módulo.

## Paso 3: Persistencia y configuración (infra)
1. Configurar datasource y JPA:
   - Definir URL, usuario y password en `application.yml` o `application.properties`.
   - Configurar `spring.jpa.hibernate.ddl-auto` (usar `update` o `validate` según la estrategia).
2. Implementar entidades JPA:
   - Crear clases persistentes equivalentes al modelo del dominio.
   - Definir relaciones con `@OneToMany`, `@ManyToOne`, etc.
   - Ajustar nombres de columnas y constraints.
3. Implementar repositorios:
   - Interfaces que implementen `JpaRepository`.
   - Adaptadores en `infra.adapter` que cumplan los puertos de `core`.
4. Validar consistencia con el modelo DESKTOP:
   - Verificar que los campos del dominio existan en la base.
   - Revisar nullabilidad, tamaños y reglas.

## Paso 4: Contratos REST y DTOs (web)
1. Definir DTOs por caso de uso:
   - UsuarioRequest, UsuarioResponse.
   - CarritoRequest, CarritoResponse, ItemCarritoRequest.
   - PedidoRequest, PedidoResponse, DetallePedidoResponse.
2. Crear mappers:
   - Convertir DTOs a entidades de `core`.
   - Convertir entidades de `core` a DTOs de salida.
3. Evitar exponer entidades del dominio directamente:
   - Los DTOs controlan lo que se muestra y lo que se recibe.

## Paso 5: Definición de endpoints REST (contratos)
### Registro de usuarios
- `POST /api/usuarios` (registro)
  - Entrada: datos básicos del usuario.
  - Salida: usuario creado + id.
- `GET /api/usuarios/{id}` (consulta)
  - Salida: datos del usuario.

### Carrito de compras
- `POST /api/carritos`
  - Crea un carrito asociado a un usuario.
- `POST /api/carritos/{id}/items`
  - Agrega producto con cantidad.
- `DELETE /api/carritos/{id}/items/{itemId}`
  - Quita producto del carrito.
- `GET /api/carritos/{id}`
  - Devuelve carrito con items.

### Gestión de pedidos
- `POST /api/pedidos`
  - Crea un pedido a partir de un carrito.
- `GET /api/pedidos/{id}`
  - Devuelve detalle del pedido.
- `GET /api/pedidos?usuarioId=...`
  - Lista pedidos del usuario.

## Paso 6: Servicios de aplicación (core)
1. Crear servicios para cada caso de uso:
   - `UsuarioService.registrarUsuario()`
   - `CarritoService.crearCarrito()`
   - `CarritoService.agregarItem()`
   - `CarritoService.quitarItem()`
   - `PedidoService.crearPedido()`
2. Ubicar todas las validaciones en `core`:
   - Verificar integridad del dominio.
   - Controlar reglas de negocio antes de persistir.
3. Usar puertos para acceso a datos:
   - `UsuarioRepositoryPort`.
   - `CarritoRepositoryPort`.
   - `PedidoRepositoryPort`.

## Paso 7: Controladores REST (web)
1. Crear controladores por recurso:
   - `UsuarioController`, `CarritoController`, `PedidoController`.
2. Inyectar servicios del `core`:
   - Usar `@RequiredArgsConstructor` o constructor injection.
3. Definir métodos con anotaciones REST:
   - `@PostMapping`, `@GetMapping`, `@DeleteMapping`.
4. Validar entrada:
   - Usar `@Valid` y anotaciones de validación en DTOs.

## Paso 8: Manejo de errores y respuestas
1. Establecer respuestas HTTP consistentes:
   - `200 OK` para consultas.
   - `201 Created` para creaciones.
   - `400 Bad Request` para validaciones.
   - `404 Not Found` para recursos inexistentes.
2. Implementar `@ControllerAdvice`:
   - Mapear excepciones de dominio a respuestas JSON.
   - Incluir mensaje claro y código.

## Paso 9: Pruebas y validación
1. Pruebas unitarias en `core`:
   - Asegurar reglas del dominio.
2. Pruebas de persistencia en `infra`:
   - Probar repositorios y mapeos.
3. Pruebas de controladores en `web`:
   - Usar MockMvc para endpoints.
4. Pruebas de integración:
   - Validar flujos completos (registro -> carrito -> pedido).

## Paso 10: Integración con front-end
1. Documentar endpoints:
   - Estructura JSON de requests y responses.
2. Probar manualmente con Postman o Insomnia.
3. Confirmar que los flujos respeten el modelo DESKTOP.

## Paso 11: Entregables mínimos
1. API REST funcional con endpoints de usuario, carrito y pedidos.
2. Persistencia conectada y validada.
3. Documentación de endpoints y DTOs.
4. Alineación total con el modelo DESKTOP.

---

Este plan está diseñado para asegurar que el módulo web utilice exactamente el mismo modelo y reglas que la aplicación DESKTOP, evitando duplicaciones y manteniendo coherencia entre plataformas.

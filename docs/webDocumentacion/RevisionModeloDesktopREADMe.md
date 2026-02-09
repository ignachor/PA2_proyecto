# Revisión del modelo DESKTOP (Paso 1)

Este documento ejecuta el **Paso 1** del plan: revisar y relevar el modelo de la aplicación DESKTOP para asegurar que el módulo web reutilice exactamente el mismo dominio y reglas de negocio.

## 1) Identificar entidades y relaciones del dominio
Entidades principales identificadas:
- Usuario
- Carrito
- Producto
- Pedido
- DetallePedido

Relaciones esperadas (a validar en el modelo DESKTOP):
- Un `Usuario` puede tener uno o más `Carrito` (histórico) o un carrito activo.
- Un `Carrito` contiene múltiples `Producto` a través de `DetallePedido` o `ItemCarrito` (según nomenclatura actual).
- Un `Pedido` se genera desde un `Carrito` y contiene múltiples `DetallePedido`.
- Un `DetallePedido` referencia un `Producto` y la cantidad comprada.

Estados posibles (ejemplo):
- Pedido: `NUEVO`, `PAGADO`, `ENVIADO`, `CANCELADO`.

## 2) Revisar reglas de negocio existentes en `core`
Reglas clave que deben permanecer en el dominio:
- Usuario:
  - Email único y con formato válido.
  - Password con longitud mínima y/o complejidad.
- Carrito:
  - No duplicar productos (si se agrega uno existente, sumar cantidad).
  - Cantidades mínimas/máximas por producto.
- Pedido:
  - No crear pedido sin items.
  - Verificar stock disponible antes de confirmar.
  - Estado inicial coherente (por ejemplo `NUEVO`).

## 3) Mapear casos de uso existentes en DESKTOP
Casos de uso que deben reflejarse en web:
- Registro de usuarios.
- Crear carrito, agregar productos y quitar productos.
- Confirmar pedido y consultar historial de pedidos.

## 4) Detectar gaps para el entorno web
Aspectos específicos del entorno web que no suelen existir en desktop:
- DTOs de entrada y salida para no exponer el dominio directamente.
- Manejo de errores HTTP (códigos y mensajes consistentes).
- Seguridad y autenticación (si no está en esta fase, documentar alcance y planificar en fase 2).

---

**Resultado esperado:** con este relevamiento, el módulo web podrá reutilizar el modelo DESKTOP sin duplicar lógica y manteniendo consistencia en reglas de negocio.

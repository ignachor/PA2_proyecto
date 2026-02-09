# Buscar Productos - README

## Objetivo
Documentar la funcionalidad de búsqueda de productos en la vista `tiendaCliente`.

## Ubicación funcional
- Endpoint: `GET /tiendaCliente`
- Vista: `web/src/main/resources/templates/tiendaCliente.html`
- Lógica: `web/src/main/java/com/igna/tienda/web/controllers/ProductoController.java`

## Criterios de búsqueda implementados
1. Búsqueda por categoría:
- Se envía el parámetro `categoria`.
- Resultado: se muestran solo productos activos de esa categoría.

2. Búsqueda por nombre:
- Se envía el parámetro `busqueda`.
- Resultado: se muestran solo productos activos cuyo nombre contenga el texto buscado.
- La comparación es case-insensitive.

3. Búsqueda combinada (categoría + nombre):
- Se envían ambos parámetros.
- Resultado: intersección de filtros (cumplen categoría y nombre).

4. Sin filtros:
- Si `categoria` es nulo y `busqueda` está vacía, se muestran todos los productos activos.

## Comportamiento de la interfaz
- El selector de categoría ejecuta búsqueda automática (`onchange="this.form.submit()"`).
- El botón `Buscar` aplica la búsqueda por nombre (y mantiene categoría seleccionada si existe).
- El botón `Limpiar filtros` vuelve a `GET /tiendaCliente` sin parámetros.

## Ejemplos de URL
- Todos los productos:
`/tiendaCliente`

- Solo categoría ELECTRONICA:
`/tiendaCliente?categoria=ELECTRONICA`

- Solo nombre "xiaomi":
`/tiendaCliente?busqueda=xiaomi`

- Categoría + nombre:
`/tiendaCliente?categoria=ELECTRONICA&busqueda=asus`

## Notas técnicas
- La base de listado inicial es `listarProductosActivos()`.
- Los filtros se aplican en el controlador sobre la lista activa.
- Se mantiene compatibilidad con ruta anterior `/catalogo` mediante mapping múltiple.

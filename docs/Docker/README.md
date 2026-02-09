# Docker y docker-compose.yml

## ¿Qué es Docker Compose?
Docker Compose es una herramienta que permite definir y levantar varios contenedores como si fueran un solo “stack” de aplicación usando un archivo `docker-compose.yml`. En vez de crear contenedores a mano uno por uno, declaras todo en un archivo y ejecutas un único comando para tener el entorno completo listo.

## ¿Para qué sirve?
- Orquestar servicios relacionados (por ejemplo, una base de datos y una aplicación).
- Mantener configuraciones repetibles entre desarrolladores.
- Simplificar el arranque y apagado del entorno con pocos comandos.

## ¿Qué viene a solucionar?
En proyectos reales, configurar manualmente servicios suele causar diferencias entre equipos. Docker Compose ayuda a evitar inconsistencias entre entornos: todos usan la misma definición.

Ejemplo: si un desarrollador instala MySQL 8.0 en su PC y otro usa MySQL 8.4, pueden aparecer errores al ejecutar scripts o migraciones. Con Compose, ambos usarán exactamente la misma versión (en este proyecto `mysql:8.4`).

## Explicación del `docker-compose.yml` del proyecto
Archivo: `docker-compose.yml`

Este archivo define un servicio de base de datos MySQL con almacenamiento persistente.

### Sección `services`
Aquí se listan los contenedores que se van a crear.

- `db`:
  - **image: `mysql:8.4`**
    Indica que se usará la imagen oficial de MySQL versión 8.4.
  - **container_name: `pa2_mysql`**
    Nombre fijo para identificar el contenedor.
  - **environment**
    Variables de entorno que inicializan la base de datos:
    - `MYSQL_DATABASE: pa2_db` crea la base `pa2_db`.
    - `MYSQL_USER: pa2_user` y `MYSQL_PASSWORD: pa2_pass` crean un usuario no root.
    - `MYSQL_ROOT_PASSWORD: root_pass` define la contraseña del usuario root.
  - **ports: `"3306:3306"`**
    Expone el puerto 3306 del contenedor al 3306 de tu máquina. Esto permite que la aplicación o herramientas como MySQL Workbench se conecten localmente.
  - **volumes: `pa2_mysqldata:/var/lib/mysql`**
    Guarda los datos de MySQL en un volumen para que no se pierdan al apagar o recrear el contenedor.

### Sección `volumes`
- `pa2_mysqldata` define el volumen persistente usado por el servicio `db`.

## Resumen rápido
Con este `docker-compose.yml` puedes levantar una base MySQL lista para usar, con usuarios y base precreados y con datos persistentes. Así se evitan diferencias entre entornos y se acelera el inicio del desarrollo.

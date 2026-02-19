# Guia de instalacion en otra maquina

## Objetivo
Este documento explica todo lo que una maquina nueva debe instalar y configurar para ejecutar este repo sin fricciones.

## 1) Software que hay que descargar e instalar

### Obligatorio
1. `Git` (para clonar el repo).
2. `JDK 21` (recomendado Temurin u Oracle JDK).
3. `Apache Maven 3.9+` (este repo no incluye `mvnw`).
4. `MySQL 8.x` o `Docker Desktop + Docker Compose` para levantar la base.

### Recomendado
1. `Docker Desktop` (simplifica mucho la base de datos del proyecto).
2. IDE Java (`IntelliJ IDEA`, `Eclipse`, `VS Code` con extensiones Java).

## 2) Versiones usadas en este proyecto (referencia)
1. Java: `21.0.9`
2. Maven: `3.9.12`
3. Spring Boot (web): `3.2.2`
4. MySQL en compose: `8.4` (imagen `mysql:8.4`)

## 3) Verificaciones rapidas luego de instalar
Ejecutar en terminal:

```bash
git --version
java -version
mvn -v
docker --version
docker compose version
```

Si no usas Docker, en vez de los ultimos dos comandos valida tu MySQL local:

```bash
mysql --version
```

## 4) Clonar el repo
```bash
git clone <URL_DEL_REPO>
cd PA2_proyecto
```

## 5) Base de datos: opcion recomendada con Docker
Este proyecto ya trae `docker-compose.yml` listo para `pa2_db`.

```bash
docker compose up -d db
docker compose ps
```

Parametros definidos en `docker-compose.yml`:
1. DB: `pa2_db`
2. Usuario: `pa2_user`
3. Password: `pa2_pass`
4. Puerto: `3306`

## 6) Base de datos: opcion alternativa sin Docker
Si usas MySQL local, crea base y usuario equivalentes:

```sql
CREATE DATABASE pa2_db;
CREATE USER 'pa2_user'@'localhost' IDENTIFIED BY 'pa2_pass';
GRANT ALL PRIVILEGES ON pa2_db.* TO 'pa2_user'@'localhost';
FLUSH PRIVILEGES;
```

La app web usa estos valores en `web/src/main/resources/application.properties`.
La app desktop usa los mismos valores en `desktop/src/main/resources/META-INF/persistence.xml`.

## 7) Build inicial
Desde la raiz del repo:

```bash
mvn clean install -DskipTests
```

## 8) Ejecutar aplicacion web
Desde la raiz:

```bash
mvn clean spring-boot:run
```

URL:
1. Home: `http://localhost:8080/`
2. Login: `http://localhost:8080/login`
3. Registro: `http://localhost:8080/registro`

## 9) Ejecutar aplicacion desktop (opcional)
Desde la raiz:

```bash
mvn -pl desktop -am exec:java -Dexec.mainClass=com.igna.tienda.desktop.DesktopApp
```

Nota:
1. Desktop y web comparten la misma base de datos.
2. La maquina debe tener entorno grafico para Swing.

## 10) Datos iniciales y rol admin
Puntos importantes:
1. `datos.sql` y `pa2_db.sql` estan vacios.
2. No hay seed automatico de usuarios/productos.
3. El registro web crea usuarios con rol `CLIENTE`.

Si necesitas habilitar un admin rapido:
1. Registra un usuario normal.
2. Promocionalo en DB:

```sql
UPDATE usuario
SET rol = 'ADMIN'
WHERE email = 'tu_email@dominio.com';
```

## 11) Puertos que deben estar libres
1. `8080` para la app web.
2. `3306` para MySQL.

## 12) Problemas comunes y solucion

### Error: `No plugin found for prefix 'spring-boot'`
1. Asegurate de estar en la raiz del repo.
2. Ejecuta con el `pom.xml` actualizado de este proyecto.
3. Reintenta: `mvn clean spring-boot:run`.

### Error: `Port 8080 was already in use`
En PowerShell:

```powershell
Get-NetTCPConnection -LocalPort 8080 -State Listen
Get-Process -Id <PID>
Stop-Process -Id <PID> -Force
```

O correr en otro puerto:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Error MySQL: `Communications link failure`
1. Verifica que MySQL este levantado.
2. Verifica `host/puerto/user/password`.
3. Si usas Docker: `docker compose up -d db`.

### Error Java/Maven por version
1. Este repo compila modulos con `release 21` en el parent.
2. Usa JDK 21 y confirma `JAVA_HOME` apuntando a JDK 21.

## 13) Checklist final (todo OK)
1. `java -version` muestra Java 21.
2. `mvn -v` funciona.
3. DB levantada en `3306`.
4. `mvn clean install -DskipTests` termina bien.
5. `mvn clean spring-boot:run` levanta web en `8080`.
6. Se puede abrir `http://localhost:8080/`.

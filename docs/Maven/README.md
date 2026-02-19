# Ejecucion de Spring Boot desde la raiz

## Problema detectado
Al ejecutar en la raiz del proyecto:

```bash
mvn clean spring-boot:run
```

aparecia este error:

```text
No plugin found for prefix 'spring-boot'
```

## Causa del error
El proyecto raiz (`demo-maven`) es un agregador Maven (`packaging` = `pom`), y no tenia declarado el plugin de Spring Boot en su `pom.xml`.

El plugin `spring-boot-maven-plugin` estaba definido solo en el modulo `web`:

- `web/pom.xml`

Por eso, al ejecutar el comando en la raiz, Maven no podia resolver el prefijo `spring-boot`.

## Solucion aplicada (nueva configuracion)
Se agrego el plugin en el `pom.xml` raiz para que Maven pueda resolver `spring-boot` desde la raiz, pero configurado con `skip=true` para no intentar arrancar Spring Boot en modulos que no son web.

Archivo modificado:

- `pom.xml`

Configuracion agregada:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <version>3.2.2</version>
      <configuration>
        <skip>true</skip>
      </configuration>
    </plugin>
  </plugins>
  ...
</build>
```

### Que logra esta configuracion
- Permite usar `spring-boot:run` desde la raiz.
- Evita ejecutar Spring Boot en:
  - `demo-maven` (raiz)
  - `core`
  - `infra`
  - `desktop`
- El arranque real queda en el modulo `web`, donde ya existe su configuracion propia del plugin.

## Comando recomendado de arranque
Desde la raiz:

```bash
mvn clean spring-boot:run
```

## Segundo error encontrado durante las pruebas
Tambien se detecto este problema:

```text
Web server failed to start. Port 8080 was already in use.
```

Esto significa que habia otro proceso Java ocupando el puerto 8080.

## Como resolver conflicto de puerto 8080 (Windows PowerShell)
1. Ver que proceso usa el puerto:

```powershell
Get-NetTCPConnection -LocalPort 8080 -State Listen
```

2. Ver el proceso:

```powershell
Get-Process -Id <PID>
```

3. Detenerlo:

```powershell
Stop-Process -Id <PID> -Force
```

## Alternativa: correr en otro puerto
Si no queres liberar 8080, podes arrancar en otro puerto:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Resumen final
- Error principal: faltaba resolver el plugin `spring-boot` desde la raiz.
- Solucion: agregar el plugin en `pom.xml` raiz con `skip=true`.
- Estado final: ya se puede ejecutar desde la raiz y el modulo web arranca correctamente si el puerto esta libre.

---

## Tercer error posible (despues de corregir Maven): MySQL no disponible
En la validacion final desde la raiz aparecio:

```text
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
Connection refused
```

### Que significa
La aplicacion ya arranco correctamente desde Maven, pero no pudo conectarse a la base de datos MySQL.

### Como solucionarlo
Este proyecto ya trae `docker-compose.yml` con la DB esperada (`pa2_db`, `pa2_user`, `pa2_pass`).

1. Levantar MySQL:

```bash
docker compose up -d db
```

2. Confirmar que esta corriendo:

```bash
docker compose ps
```

3. Ejecutar nuevamente la app:

```bash
mvn clean spring-boot:run
```

### Configuracion de DB usada por la app
Archivo:

- `web/src/main/resources/application.properties`

Valores actuales:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pa2_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=pa2_user
spring.datasource.password=pa2_pass
```

Si esos datos cambian en Docker o en tu MySQL local, hay que mantenerlos sincronizados.

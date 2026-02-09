# Git y GitHub: Guía rápida y clara

Este documento es un tutorial básico y práctico para iniciar un repositorio, conectarlo con GitHub y manejar los comandos más comunes sin perder tecnicismo.

## 1) Creación de un repositorio local
Para inicializar un repositorio que vamos a versionar:

Comandos en la terminal (bash):
```bash
mkdir carpeta
cd carpeta
git init
```

## 2) Creación de un repositorio remoto
El repositorio remoto se crea desde GitHub (en la web).

### Identificar “de quién es el repositorio”
Cuando el repositorio remoto es tuyo, configura tu identidad local (solo una vez por máquina):
```bash
git config user.name "TU_NOMBRE_USUARIO"
git config user.email "TU_EMAIL"
```

Para autenticarte en GitHub, hoy se usa un **Access Token** en lugar de contraseña:
```bash
git config user.password "ACCESS_TOKEN"
```

Nota: este comando no es el método más recomendado actualmente para guardar credenciales, pero se mantiene aquí para que entiendas el concepto. Lo más común es usar el administrador de credenciales de Git o tokens con `git credential`.

### Conectar repositorio local con remoto
Para iniciar comunicación entre tu repositorio local y remoto:
```bash
git remote add origin "URL"
```

## 3) Subir cambios al repositorio remoto
### Flujo básico de cambios
```bash
git status
```

Si hay cambios sin añadir:
```bash
git add hola.txt
```

Esto agrega el archivo a la “cola de commit”, lo que permite revertir si hay un problema (ejemplo: si agregas el archivo equivocado, puedes sacarlo del stage con `git reset hola.txt`).

Luego haces commit:
```bash
git commit -m "DESCRIPCION_DEL_CAMBIO_HECHO"
```

Y subes al remoto:
```bash
git push -u origin master
```

Nota: en muchos proyectos la rama principal se llama `main`, no `master`.

La opción `-u` significa **upstream**, es decir, define la rama remota de referencia. Se usa una sola vez, y luego puedes hacer `git push` o `git pull` sin parámetros.

## 4) Git Clone y Git Pull
### Clonar un repositorio
En una carpeta:
```bash
git clone "URL"
```

### Traer cambios de una rama específica
```bash
git pull "apodo del repo remoto" "rama en la cual estamos trabajando"
```

Ejemplo:
```bash
git pull origin develop
```

## 5) Git: Branches
Comandos comunes:
```bash
git branch
```
Muestra las ramas del repo.

```bash
git branch "nombre"
```
Crea una rama nueva.

```bash
git branch -m "nombreViejo" "nombreNuevo"
```
Cambia el nombre de una rama.

```bash
git checkout "nombre de la rama"
```
Cambia de rama.

```bash
git branch -d "nombreRama"
```
Borra una rama (debes estar en otra rama para eliminarla).

Crear un archivo en una rama:
```bash
touch arch.tipoArchivo
```

### Regla importante al crear archivos
Siempre que crees archivos nuevos:
```bash
git add .
git commit -m "DESCRIPCION DE LOS CAMBIOS"
```

Si también quieres que existan en el remoto:
```bash
git push -u origin "ramaOrigen"
```

Con esto, los archivos quedan en tu rama local (si no haces `push`) o también en el remoto (si sí haces `push`). Luego podrás hacer merge a `main`.

## 6) Git Diff y Git Merge
### Ver diferencias entre ramas
```bash
git diff "rama1" "rama2"
```
Esto muestra qué tiene `rama2` que no tiene `rama1`.

### Unir ramas (merge)
```bash
git merge "ramaOrigen"
```
Para que funcione, debes estar parado en la rama destino:
```bash
git checkout ramaDestino
```

Ejemplo:
```bash
git checkout main
git merge develop
```

Luego del merge, en la rama destino:
```bash
git add .
git commit -m "MENSAJE"
git push -u origin main
```

Recuerda: el `commit` actualiza tu repositorio local, el `push` actualiza el repositorio remoto.

## 7) Extra útil: Estado e historial
Para ver cambios y commits:
```bash
git status
git log --oneline
para salir es con "q" (quit)
```

---

## 8) Flujo avanzado (Pull Requests, Rebase, Stash, Tags)
### Pull Requests (PR)
Un Pull Request es una solicitud para integrar cambios de una rama a otra (por ejemplo, `feature` -> `main`). Se crea en GitHub y permite revisión antes de mezclar cambios.

Flujo típico:
1. Crear rama y hacer cambios.
2. Hacer `push` de la rama al remoto.
3. Crear el PR en GitHub.
4. Revisar, aprobar y hacer merge.

### Rebase
`git rebase` reescribe el historial moviendo tus commits sobre la punta de otra rama. Es útil para mantener un historial lineal.

Ejemplo básico (traer cambios de `main` a tu rama):
```bash
git checkout mi-rama
git fetch origin
git rebase origin/main
```

Si aparecen conflictos, los resuelves y haces:
```bash
git add .
git rebase --continue
```

Ejemplo para entenderlo: si tu rama tiene 3 commits y `main` avanzó 2 commits, el rebase “mueve” tus 3 commits encima de esos 2 para evitar un merge extra.

### Stash
`git stash` guarda cambios temporales sin hacer commit. Útil cuando debes cambiar de rama rápido sin perder el trabajo.

```bash
git stash
git checkout otra-rama
```

Luego recuperas:
```bash
git stash pop
```

### Tags
Los tags marcan versiones específicas (por ejemplo, releases).

```bash
git tag v1.0.0
git push origin v1.0.0
```

Para listar tags:
```bash
git tag
```


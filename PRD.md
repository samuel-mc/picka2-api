# PRD — Local Postgres + Admin Seeder + Feed Query Hardening

## Contexto

El backend `pickados-api` actualmente requiere acceso a infraestructura remota (DB, secrets) para poder iniciar, lo que bloquea el onboarding de nuevos desarrolladores y dificulta pruebas reproducibles en local. Adicionalmente, las consultas de feed/timeline no aplican filtros consistentes sobre usuarios eliminados (soft-delete) ni respetan correctamente las reglas de visibilidad de posts (PUBLIC, FOLLOWERS_ONLY, PRIVATE), lo que produce contenido incorrecto en la respuesta de la API.

Este documento define los requisitos para resolver ambos problemas de forma independiente del entorno de producción.

## Objetivos

- Proveer una forma simple de levantar **Postgres local** para desarrollo sin dependencias externas.
- Garantizar que, en entorno local, exista un **usuario admin inicial** (y su rol) al arrancar la app, sin pasos manuales.
- Endurecer/estandarizar la lógica de feed/timeline para que:
  - No aparezcan contenidos de autores eliminados.
  - Se respeten reglas de visibilidad y relaciones de follow.

## No objetivos (fuera de alcance)

- Cambiar el modelo de autenticación/JWT o el sistema de cookies.
- Implementar UI o flujos del frontend.
- Diseñar un sistema completo de seeders/migraciones para todos los datos del sistema.
- Cambios de infraestructura de producción (se asume que ya existe un proveedor/DB remota).
- Implementar paginación o ranking del feed (ajenos a este PRD).

## Usuarios / stakeholders

| Rol | Necesidad principal |
|---|---|
| **Desarrolladores** | Levantar el stack local y probar endpoints sin secretos externos |
| **QA** | Reproducibilidad para validar escenarios de feed/visibilidad |
| **Admin/Operaciones** | Acceso inicial en dev/local para funciones administrativas (no en prod) |

## Dependencias

- **Requiere**: existencia de entidades `User`, `Role`, `Post`, `Follow` con su schema actual en DB.
- **Requiere**: soporte de perfiles Spring (`local`, `prod`) ya configurado en el proyecto.
- **Bloquea**: cualquier tarea de QA que valide feed/visibilidad con datos de usuarios eliminados.
- **No bloquea**: trabajo en autenticación, creación de posts u otras funcionalidades.

## Alcance

### A. Postgres local (Docker)

- Se agrega un `docker-compose.postgres.yml` que levanta:
  - **PostgreSQL 16**
  - Puerto expuesto **5432:5432**
  - DB por defecto: `pickados_db`
  - Usuario/Password por defecto: `postgres` / `postgres`
  - Volumen persistente: `pickados_pgdata`

### B. Admin seeding (solo perfil `local`)

- Al iniciar la app con el perfil **`local`**, el backend debe:
  - Asegurar que exista el rol **`ADMIN`** (si no existe, crearlo).
  - Crear un usuario admin por defecto si no existe (verificando por email **y** username).

**Credenciales y datos esperados (solo local):**

| Campo | Valor |
|---|---|
| Email | `admin@pickados.local` |
| Username | `admin` |
| Password | `123456` (almacenada hasheada con el encoder configurado) |
| Role | `ADMIN` |
| `active` | `true` |
| `deleted` | `false` |

Además, en **perfil `local`** se puede sembrar un usuario **tipster** solo para desarrollo y pruebas automatizadas (p. ej. TestSprite): `tipster` / `tipster@pickados.local` / `123456` con rol `TIPSTER` y perfil de tipster mínimo, porque `POST /posts` exige `ROLE_TIPSTER` en la configuración de seguridad.

### C. Consultas de repositorio: robustez con soft-delete y visibilidad

Las queries afectadas son las de **feed/timeline** (listado de posts del home, perfil de usuario y búsqueda). La lógica debe:

- Filtrar consistentemente registros con `deleted = false` (o `coalesce(deleted, false) = false` en queries nativas donde el campo puede ser `null`).
- En feed/timeline:
  - Excluir posts cuyo autor tenga `deleted = true`.
  - Respetar reglas de visibilidad del post:
    - **PUBLIC**: visible a todos los usuarios autenticados.
    - **FOLLOWERS_ONLY**: visible al autor y a sus followers confirmados.
    - **PRIVATE**: visible solo al autor.
  - En reposts: no incluir eventos originados por usuarios eliminados.

## Requisitos funcionales

### RF1 — Levantar Postgres local

- Debe ser posible ejecutar el compose y tener una instancia lista para conexiones locales con un único comando (`docker compose -f docker-compose.postgres.yml up -d`).
- La app debe conectarse exitosamente usando la configuración del perfil `local`.

### RF2 — Seeder admin en `local`

- El seeder **solo** debe correr bajo el perfil `local` (anotación `@Profile("local")`).
- El seeder es **idempotente**: si ya existe un usuario con ese email o username, no crea otro ni lanza error.
- El seeder crea el rol `ADMIN` si no existe en la tabla de roles.
- El seeder debe loguear el resultado de su ejecución (creado / ya existía).

### RF3 — Feed/timeline correcto con visibilidad + soft-delete

- Al listar feed/timeline desde cualquier endpoint expuesto:
  - No deben aparecer posts de autores con `deleted = true`.
  - No deben aparecer reposts de usuarios con `deleted = true`.
  - `FOLLOWERS_ONLY` requiere relación de follow activa entre solicitante y autor (cuando el solicitante no es el autor).
  - `PRIVATE` no debe aparecer a ningún tercero.

## Requisitos no funcionales

- **Seguridad**:
  - Las credenciales del admin seed son solo para local; no deben existir mecanismos que las creen automáticamente en `prod`.
  - Ningún secreto real (DB remota, mail, S3/R2) debe ser prerequisito para levantar local.
- **Reproducibilidad**:
  - Con `docker-compose.postgres.yml` + perfil `local`, un dev nuevo debe poder iniciar en menos de 10 minutos sin pasos manuales de DB/roles.
- **Observabilidad**:
  - Fallos de conexión a DB en local deben ser evidentes en logs estándar de Spring Boot.
  - El seeder debe emitir logs claros con el resultado de cada operación (rol creado, usuario creado, ya existía).

## Supuestos

- Existe soporte de perfiles Spring (`local`, `prod`) y configuración por environment.
- El proyecto usa JPA con entidades que tienen un flag booleano `deleted` para soft-delete en `users`.
- Existen tablas/relaciones para follows, reposts y reacciones en DB (requeridas por las queries nativas del feed).
- El `PasswordEncoder` ya está configurado como bean en el contexto de Spring.

## Métricas de éxito

- Un dev nuevo puede levantar el backend con DB en menos de 10 minutos siguiendo las instrucciones.
- QA puede reproducir escenarios de feed/visibilidad con datos consistentes sin intervención manual.
- Cero bugs reportados del tipo "usuario borrado aparece en feed" tras el deploy de los cambios.

## Criterios de aceptación

### Setup local

- [ ] `docker compose -f docker-compose.postgres.yml up -d` levanta Postgres 16 sin errores.
- [ ] La app con `spring.profiles.active=local` conecta a la DB local y arranca sin errores.

### Admin seeder

- [ ] Al iniciar por primera vez con perfil `local`:
  - [ ] Se crea el rol `ADMIN` en DB y se loguea `"Rol ADMIN creado"` (o similar).
  - [ ] Se crea el usuario `admin@pickados.local` / `admin` con `deleted=false, active=true`.
- [ ] Al reiniciar la app, no se crean duplicados; los logs reflejan `"ya existía"`.
- [ ] Con perfil distinto de `local`, el seeder **no** se ejecuta.

### Feed / visibilidad

- [ ] Un endpoint de feed no retorna posts de usuarios con `deleted=true`.
- [ ] Un endpoint de feed no retorna reposts originados por usuarios con `deleted=true`.
- [ ] Un post `FOLLOWERS_ONLY` no aparece en la respuesta de un usuario que no sigue al autor.
- [ ] Un post `PRIVATE` no aparece en la respuesta de ningún usuario que no sea el autor.
- [ ] Un post `PUBLIC` aparece correctamente para cualquier usuario autenticado.

## Rollout / implementación

### Desarrollo

1. Agregar `docker-compose.postgres.yml` en la raíz del proyecto.
2. Agregar/actualizar `application-local.properties` (o `.yml`) con la config de DB local.
3. Implementar el componente seeder con `@Profile("local")` y `@Component` / `ApplicationRunner`.
4. Actualizar/crear queries de repositorio con los filtros de soft-delete y visibilidad.
5. Documentar el proceso de setup local en el `README.md` del proyecto.

### Producción

- No habilitar el perfil `local`; el seeder no debe ejecutarse en ningún entorno no-local.
- Las queries endurecidas de feed son backward-compatible: no requieren migraciones adicionales.

## Riesgos y mitigaciones

| Riesgo | Probabilidad | Mitigación |
|---|---|---|
| Seeder se ejecuta accidentalmente en prod | Baja | Asegurar `@Profile("local")` y documentar que prod nunca use ese perfil |
| Inconsistencias entre queries JPQL y nativas con respecto a `deleted` | Media | Normalizar uso de `coalesce(deleted,false)` en nativas y `deleted=false` en JPQL; agregar tests de integración |
| Schema actual no tiene columna `deleted` en alguna entidad relevante | Baja | Verificar schema antes de implementar; si falta, agregar migración Flyway/Liquibase |
| Queries de feed nativas rompen si cambia el schema de `follows` o `posts` | Media | Revisar queries nativas contra schema actual antes de mergear |

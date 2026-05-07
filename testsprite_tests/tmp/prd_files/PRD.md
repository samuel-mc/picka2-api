# PRD — Local Postgres + Admin Seeder + Feed Query Hardening

## Contexto
El backend `pickados-api` necesita un entorno local reproducible y datos mínimos para poder desarrollar/probar (autenticación + funcionalidades de feed) sin depender de infraestructura remota. Además, algunas consultas de feed/timeline deben comportarse correctamente cuando existen usuarios “soft-deleted” (marcados como eliminados) y cuando aplica la visibilidad de posts (PUBLIC, FOLLOWERS_ONLY, PRIVATE).

Este documento define los requisitos del cambio.

## Objetivos
- Proveer una forma simple de levantar **Postgres local** para desarrollo.
- Garantizar que, en entorno local, exista un **usuario admin inicial** (y su rol) para acceder a funciones administrativas sin pasos manuales.
- Endurecer/estandarizar la lógica de feed/timeline para que:
  - No aparezcan contenidos de autores eliminados.
  - Se respeten reglas de visibilidad y relaciones de follow.

## No objetivos (fuera de alcance)
- Cambiar el modelo de autenticación/JWT o el sistema de cookies.
- Implementar UI o flujos del frontend.
- Diseñar un sistema completo de seeders/migraciones para todos los datos del sistema.
- Cambios de infraestructura de producción (se asume que ya existe un proveedor/DB remota).

## Usuarios / stakeholders
- **Desarrolladores**: necesitan levantar el stack local y probar endpoints.
- **QA**: necesita reproducibilidad para validar feed/visibilidad.
- **Admin/Operaciones**: necesitan un acceso inicial en dev/local (no en prod).

## Alcance
### A. Postgres local (Docker)
- Se agrega un `docker-compose.postgres.yml` que levanta:
  - **PostgreSQL 16**
  - Puerto expuesto **5432:5432**
  - DB por defecto: `pickados_db`
  - Usuario/Password por defecto: `postgres` / `postgres`
  - Volumen persistente: `pickados_pgdata`

### B. Admin seeding (solo perfil local)
- Al iniciar la app con el perfil **`local`**, el backend debe:
  - Asegurar que exista el rol **`ADMIN`** (si no existe, crearlo).
  - Crear un usuario admin por defecto si no existe (por email o username).

**Credenciales y datos esperados (solo local):**
- Email: `admin@pickados.local`
- Username: `admin`
- Password: `123456` (almacenada en DB con hashing/encoder configurado)
- Role: `ADMIN`
- Flags: `active = true`, `deleted = false`

### C. Consultas de repositorio: robustez con soft delete y feed
- La capa de repositorios debe ofrecer queries que:
  - Filtren consistentemente registros con `deleted = false` (o `coalesce(deleted,false) = false` donde aplique).
  - En feed/timeline:
    - Excluyan posts cuyo autor esté eliminado.
    - Respeten visibilidad:
      - PUBLIC: visible a todos.
      - FOLLOWERS_ONLY: visible al autor y a followers.
      - PRIVATE: visible solo al autor.
    - Cuando hay reposts (si aplica), no incluir eventos de usuarios eliminados.

## Requisitos funcionales
### RF1 — Levantar Postgres local
- Debe ser posible ejecutar el compose y tener una instancia lista para conexiones locales.

### RF2 — Seeder admin en `local`
- El seeder **solo** debe correr en perfil `local`.
- El seeder no debe duplicar datos:
  - Si ya existe un usuario con ese email o username, no debe crear otro.
- El seeder debe crear el rol `ADMIN` si no existe.

### RF3 — Feed/timeline correcto con visibilidad + soft delete
- Al listar feed/timeline:
  - No deben aparecer posts de autores eliminados.
  - No deben aparecer reposts de usuarios eliminados.
  - FOLLOWERS_ONLY debe requerir relación de follow (cuando el solicitante no sea el autor).
  - PRIVATE no debe aparecer a terceros.

## Requisitos no funcionales
- **Seguridad**:
  - Las credenciales del admin seed son solo para local; no deben existir mecanismos que las creen automáticamente en `prod`.
  - Ningún secreto real (DB remota, mail, S3/R2) debe ser requisito para levantar local.
- **Reproducibilidad**:
  - Con `docker-compose.postgres.yml` + perfil `local`, un dev debe poder iniciar sin pasos manuales de DB/roles.
- **Observabilidad**:
  - Fallos de conexión a DB en local deben ser evidentes en logs estándar de Spring Boot.

## Supuestos
- Existe soporte de perfiles Spring (por ejemplo `local`, `prod`) y configuración por environment.
- El proyecto usa JPA y entidades con un flag booleano `deleted` (soft delete) para `users`.
- Existen tablas/relaciones para follows/reposts/reacciones en DB (según queries nativas).

## Métricas de éxito
- Tiempo de onboarding dev para “correr backend con DB” disminuye (setup en minutos).
- QA puede validar feed/visibilidad con datos consistentes.
- Disminuyen bugs por “usuarios borrados aparecen en feed”.

## Criterios de aceptación (checklist)
- [ ] Se puede levantar Postgres local con el compose y conectar desde la app.
- [ ] Con `spring.profiles.active=local`, al iniciar por primera vez:
  - [ ] Se crea el rol `ADMIN` (si no existe).
  - [ ] Se crea el usuario `admin@pickados.local` / `admin` con `deleted=false`.
- [ ] Reiniciar la app no crea duplicados del admin/rol.
- [ ] En feed/timeline, no aparecen posts/reposts de usuarios con `deleted=true`.
- [ ] Visibilidad se respeta: PUBLIC/FOLLOWERS_ONLY/PRIVATE según reglas.

## Rollout / implementación
- **Desarrollo**:
  - Documentar el uso del compose (comando y variables) en un README futuro si el repo lo incorpora.
  - Activar perfil `local` para desarrollo local.
- **Producción**:
  - No habilitar `local`; el seeder no debe ejecutarse.

## Riesgos y mitigaciones
- **Riesgo**: que el seeder se ejecute accidentalmente en prod.
  - **Mitigación**: asegurar el `@Profile("local")` y evitar que prod use ese perfil.
- **Riesgo**: inconsistencias entre queries JPQL y nativas con respecto a `deleted`.
  - **Mitigación**: normalizar uso de `coalesce(deleted,false)` en nativas y `deleted=false` en entidades con valores no nulos.


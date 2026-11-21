# 1. Nombre del servicio y su bounded context

**Identificador del servicio:** `ms-usuarios` (`CADCHE-Users-Service`)

**Paquete arquitectónico:** `Paquete 1: Identidad`

**Puerto de escucha oficial:** `8120`

**Base de datos asignada:** `PostgreSQL 16` (`styleradar_usuarios`), aislada bajo el patrón *Database-per-Service*.

**Bounded Context:** Gestión de Perfiles de Usuario, Preferencias de Estilo, Tallas y Búsquedas Guardadas.

## Responsabilidades del dominio

Este microservicio encapsula exclusivamente la lógica de negocio correspondiente a la gestión de perfiles de usuario, preferencias de estilo, tallas y búsquedas guardadas.

| Qué hace este microservicio | Qué no hace este microservicio |
|---|---|
| Registra y consulta el perfil de usuarios compradores (`SR-RF-01`), validando unicidad de correo y aceptación de Habeas Data (`SR-RNF-02`). | No autentica usuarios, no firma tokens JWT ni valida códigos OTP. Estas responsabilidades corresponden a `ms-auth` (`:8081`). |
| Configura preferencias de estilo (`URBANO`, `CASUAL`, `FORMAL`, `DEPORTIVO`, `BOHEMIO`, `MINIMALISTA`, `CLASICO`, `VINTAGE`) con selección mínima de un estilo (`SR-RF-03`). | No gestiona almacenes aliados, verificación de RUT ni fundaciones aliadas. Estas responsabilidades corresponden a `ms-tiendas` (`:8083`). |
| Gestiona tallas habituales para ropa (`XS..XXL`) y calzado (`35..45`) (`SR-RF-04`, Deuda Técnica #9). | No administra el inventario de prendas del catálogo comercial ni envía notificaciones directamente. |
| Gestiona el feed personalizado (`SR-RF-05`), las recomendaciones basadas en perfil (`SR-RF-06`), las búsquedas guardadas con coordenadas geográficas (`SR-RF-28`, Deuda Técnica #15) y la administración de estados de cuenta (`ACTIVO`, `INACTIVO`, `BLOQUEADO`, `SR-RF-66`). | No accede directamente a las bases de datos de otros microservicios. |
| Garantiza persistencia ACID e idempotencia en su base de datos `styleradar_usuarios`. | |

## Arquetipos de moda y personajes de StyleRadar

| Antonia (Sastre) | Mateo (Calle) | Aura (Futuro) | Nora (Rebelde) | Tomás (Esencial) |
|---|---|---|---|---|
| **Sastre** — Tiendas y descubrimiento | **Calle** — Playlists y cultura urbana | **Futuro** — Probador virtual AI | **Rebelde** — Segunda vida y vintage | **Esencial** — Fundaciones y donaciones |

---

[Índice principal](../../README.md) · [Siguiente: 2. Equipo y responsables](02-equipo-responsables.md)

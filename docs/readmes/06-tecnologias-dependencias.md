# 6. Tecnologías y dependencias

El stack del microservicio `ms-usuarios` cumple con el estándar unificado de arquitectura de StyleRadar.

## 6.1. Stack tecnológico

| Capa / Componente | Tecnología | Versión | Justificación técnica |
|---|---|---|---|
| **Lenguaje Core** | Java LTS | `21 LTS` | Records inmutables para DTOs, tipado seguro y alto desempeño de ejecución. |
| **Framework Empresarial** | Spring Boot | `3.2.12` | Gestión declarativa de configuración, Spring Web, Spring Validation y Actuator. |
| **Base de Datos** | PostgreSQL con soporte JSONB | `16` | Integridad relacional ACID para perfiles y tallas, combinada con columnas JSONB indexadas con GIN para búsquedas guardadas. |
| **Caché en Memoria** | Redis | `7.x` | Almacenamiento en caché de perfiles y preferencias de estilo de usuarios activos. |
| **Migraciones de BD** | Flyway | `10.20.1` | Control de versiones reproducible del esquema SQL (`styleradar_usuarios`). |
| **Broker de Mensajería** | RabbitMQ | `3.12+` | Sincronización eventual de estados de cuenta y preferencias con `ms-auth` y `ms-busqueda`. |
| **Validación de Contratos** | Jakarta Bean Validation | `3.0.x` | Validación declarativa sin vulnerabilidades ReDoS (mitigación Deuda Técnica #15 en expresiones regulares). |
| **Mapeo de DTOs** | MapStruct + Lombok | `1.5.5.Final / 1.18.42` | Generación de mappers en tiempo de compilación sin sobrecarga por reflexión. |
| **Documentación API** | SpringDoc OpenAPI | `2.3.0` | Exposición automática del esquema OpenAPI 3.0 y consola interactiva `/swagger-ui.html`. |
| **Testing Core e Integración** | JUnit 5, Mockito, Testcontainers | `5.10.x / 1.19.8` | Pruebas unitarias de dominio, verificación de contrato OpenAPI y contenedores efímeros PostgreSQL. |

## 6.2. Dependencias externas

El microservicio utiliza integraciones y servicios externos para complementar sus responsabilidades:

| Dependencia | Tipo de comunicación | Propósito |
|---|---|---|
| PostgreSQL | JDBC | Persistencia de perfiles, preferencias, tallas y búsquedas guardadas. |
| Redis | Cliente Redis | Caché de perfiles y preferencias de usuarios activos. |
| RabbitMQ | AMQP | Publicación y consumo de eventos entre microservicios. |
| `ms-auth` | REST / eventos | Integración relacionada con identidad y estados de cuenta. |
| `ms-busqueda` | Eventos | Sincronización de preferencias y estados para búsquedas y recomendaciones. |

## 6.3. Análisis tecnológico

Las decisiones tecnológicas buscan cumplir los siguientes objetivos:

- Mantener una arquitectura desacoplada mediante puertos y adaptadores.
- Garantizar integridad y consistencia de los datos mediante PostgreSQL.
- Mejorar el rendimiento de consultas frecuentes mediante mecanismos de caché.
- Permitir comunicación asíncrona mediante RabbitMQ.
- Facilitar el mantenimiento del código mediante herramientas de generación de código y validación.
- Garantizar calidad mediante pruebas unitarias y de integración.

> El análisis formal exhaustivo basado en la Plantilla Maestra Oficial (Drivers de Calidad ISO/IEC 25010, Metodología MCDM/Pugh Matrix, Matrices de Decisión ADR-001 a ADR-005, Costos Cloud TCO/FinOps, Tech Radar, Matriz de Riesgos y Acta de Aprobación) se encuentra documentado en la [Sección 13: Análisis de Tecnologías del Servicio](13-analisis-tecnologias.md).

---

[← Anterior: 5. Eventos RabbitMQ](05-eventos-rabbitmq.md) · [Índice principal](../../README.md) · [Siguiente: 7. Ejecución local y Docker](07-ejecucion-local-docker.md)

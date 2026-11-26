# 5. Tecnologías, dependencias y análisis

El stack del microservicio `ms-usuarios` cumple con el estándar unificado de arquitectura de StyleRadar.

## 5.1. Stack tecnológico

| Capa / Componente | Tecnología | Versión | Justificación técnica |
|---|---|---|---|
| **Lenguaje Core** | Java LTS | `21 LTS` | Records inmutables para DTOs, tipado seguro y alto desempeño de ejecución. |
| **Framework Empresarial** | Spring Boot | `3.2.12` | Gestión declarativa de configuración, Spring Web, Spring Validation y Actuator. |
| **Base de Datos** | PostgreSQL con soporte JSONB | `16` | Integridad relacional ACID para perfiles y tallas, combinada con columnas JSONB indexadas con GIN para búsquedas guardadas. |
| **Caché en memoria** | `ConcurrentHashMap` | Implementación propia | Caché local de recomendaciones con vencimiento de cinco minutos; no requiere Redis. |
| **Migraciones de BD** | Flyway | `10.20.1` | Control de versiones reproducible del esquema SQL (`styleradar_usuarios`). |
| **Broker de Mensajería** | RabbitMQ | Configurado por ambiente | Publicación de eventos de usuarios, búsquedas, estados y cambios de contraseña. |
| **Validación de Contratos** | Jakarta Bean Validation | `3.0.x` | Validación declarativa sin vulnerabilidades ReDoS (mitigación Deuda Técnica #15 en expresiones regulares). |
| **Mapeo de DTOs** | MapStruct + Lombok | `1.5.5.Final / 1.18.42` | Generación de mappers en tiempo de compilación sin sobrecarga por reflexión. |
| **Documentación API** | SpringDoc OpenAPI | `2.3.0` | Exposición del esquema OpenAPI y consola interactiva `/swagger-ui/index.html`. |
| **Testing Core e Integración** | JUnit 5, Mockito, Testcontainers | `5.10.x / 1.19.8` | Pruebas unitarias de dominio, verificación de contrato OpenAPI y contenedores efímeros PostgreSQL. |

## 5.2. Dependencias externas

El microservicio utiliza integraciones y servicios externos para complementar sus responsabilidades:

| Dependencia | Tipo de comunicación | Propósito |
|---|---|---|
| PostgreSQL | JDBC | Persistencia de perfiles, preferencias, tallas y búsquedas guardadas. |
| Caché local | Memoria del proceso | Caché temporal de recomendaciones. Se pierde al reiniciar y no se comparte entre réplicas. |
| RabbitMQ | AMQP | Publicación asíncrona de eventos a otros microservicios. |
| `ms-auth` | HTTP interno / RabbitMQ | Verificación y recuperación de cuentas; notificación de cambios de estado y contraseña. |
| `ms-item-catalogo` | HTTP REST | Obtención de prendas para feed y recomendaciones; timeout configurado de 3 s conexión / 5 s lectura y respuesta degradada vacía ante fallas. |

## 5.3. Análisis tecnológico

Las decisiones tecnológicas buscan cumplir los siguientes objetivos:

- Mantener una arquitectura desacoplada mediante puertos y adaptadores.
- Garantizar integridad y consistencia de los datos mediante PostgreSQL.
- Evitar consultas repetidas de recomendaciones durante la vigencia de la caché local.
- Permitir comunicación asíncrona mediante RabbitMQ.
- Facilitar el mantenimiento del código mediante herramientas de generación de código y validación.
- Garantizar calidad mediante pruebas unitarias y de integración.

El servicio usa Java 21, Spring Boot, PostgreSQL, Spring Data JPA y Flyway para la API y persistencia; Spring Security valida JWT de ms-auth y Basic Auth en rutas internas. RabbitMQ transporta eventos y el catálogo se consulta por REST. Docker y GitHub Actions soportan empaquetado y despliegue en Azure.

No hay cliente Redis configurado en el proyecto. La caché de recomendaciones vive en memoria del proceso, por lo que no se comparte entre réplicas y se pierde al reiniciar. JaCoCo mide cobertura, SpotBugs analiza defectos potenciales y SonarCloud aplica las condiciones de calidad configuradas para cada análisis.

## 5.4. Tecnologías por responsabilidad

| Área | Tecnologías | Uso en el servicio |
|---|---|---|
| Lenguaje y construcción | Java 21, Maven | Implementación y automatización de compilación y pruebas. |
| API y seguridad | Spring Web, Spring Security, OAuth2 Resource Server, SpringDoc OpenAPI | Endpoints REST, autorización por roles, validación de JWT y documentación OpenAPI. |
| Persistencia | PostgreSQL, Spring Data JPA, Flyway | Persistencia por servicio y migraciones versionadas. |
| Integración | Spring AMQP / RabbitMQ, Spring `RestClient` | Publicación de eventos y consulta de prendas en ms-item-catalogo. |
| Calidad | JUnit 5, Mockito, Testcontainers, JaCoCo, SpotBugs, SonarCloud | Pruebas, medición de cobertura y análisis estático. |
| Entrega | Docker, Docker Compose, GitHub Actions, Azure Container Registry, Azure App Service | Empaquetado, CI y despliegues de QA/PROD. |

La selección mantiene el núcleo de dominio independiente de Spring y los protocolos mediante puertos y adaptadores. PostgreSQL ofrece persistencia relacional; RabbitMQ desacopla notificaciones de cambios; el cliente REST consulta catálogo con tiempos límite explícitos. No se documenta Redis, porque no forma parte de las dependencias activas del servicio.

---

[← Anterior: 4. Eventos RabbitMQ](04-eventos-rabbitmq.md) · [Índice principal](../../README.md) · [Siguiente: 6. Ejecución local y Docker](06-ejecucion-local-docker.md)

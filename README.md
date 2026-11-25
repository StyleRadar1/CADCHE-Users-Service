<!-- ==============================================================================
  STYLE RADAR
  Microservicio: MS-USUARIOS
  Documentación organizada en 13 secciones.
============================================================================== -->

<div align="center">

  <a href="https://github.com/StyleRadar1">
    <img src="https://raw.githubusercontent.com/JUANNICOLASA/Frontend-Style-Radar/main/styleradar-frontend-v3/assets/logo/styleradar-logo.svg" width="340" alt="StyleRadar Logo Oficial" />
  </a>

  <h1>STYLE<font color="#5600EF">RADAR</font> &nbsp;·&nbsp; <code>ms-usuarios</code></h1>

  <p align="center">
    <img src="https://img.shields.io/badge/BOUNDED_CONTEXT-GESTI%C3%93N_DE_USUARIOS-12063A?style=for-the-badge" alt="Bounded Context" />
    <img src="https://img.shields.io/badge/PUERTO_OFICIAL-8120-5600EF?style=for-the-badge" alt="Puerto oficial" />
    <img src="https://img.shields.io/badge/ARQUITECTURA-HEXAGONAL-16A34A?style=for-the-badge" alt="Arquitectura hexagonal" />
  </p>

  <p align="center">
    <strong><font color="#5600EF">Descubre tu estilo. En tu ciudad.</font></strong><br />
    <em>Gestión de perfiles, preferencias de estilo, tallas habituales y búsquedas guardadas para la plataforma StyleRadar.</em>
  </p>

  <p align="center">
    <img src="https://img.shields.io/badge/DEV_LOCAL-CONFIGURADO-16A34A?style=for-the-badge" alt="Desarrollo local" />
    <a href="https://styleradar-qa-app.azurewebsites.net/actuator/health"><img src="https://img.shields.io/badge/QA_HEALTH-UP-16A34A?style=for-the-badge" alt="QA health UP" /></a>
    <a href="https://styleradar-prod-app.azurewebsites.net/actuator/health"><img src="https://img.shields.io/badge/PROD_HEALTH-UP-16A34A?style=for-the-badge" alt="Producción health UP" /></a>
    <img src="https://img.shields.io/badge/CI_CD-GITHUB_ACTIONS-2088FF?style=for-the-badge&logo=githubactions&logoColor=white" alt="CI CD" />
  </p>

  <p align="center">
    <a href="https://styleradar-qa-app.azurewebsites.net/swagger-ui/index.html">Swagger QA</a> ·
    <a href="https://styleradar-prod-app.azurewebsites.net/swagger-ui/index.html">Swagger PROD</a>
  </p>

  <p align="center"><sub>Health check y Swagger respondieron HTTP 200 el 24/11/2026. Esto confirma disponibilidad de los endpoints, no la versión desplegada.</sub></p>

  <p align="center">
    <img src="https://img.shields.io/badge/TESTS-JUNIT_5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5" />
    <img src="https://img.shields.io/badge/JACOCO-COVERAGE_REPORT-6B7280?style=for-the-badge" alt="JaCoCo" />
  </p>

<h3>Tecnologías y Dependencias Principales</h3>

  <p align="center">
    <img src="https://img.shields.io/badge/JAVA-21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
    <img src="https://img.shields.io/badge/SPRING_BOOT-3.2.12-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3.2.12" />
    <img src="https://img.shields.io/badge/POSTGRESQL-RELATIONAL_DB-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
    <img src="https://img.shields.io/badge/FLYWAY-10.20.1-CC0200?style=for-the-badge" alt="Flyway 10.20.1" />
    <br />
    <img src="https://img.shields.io/badge/RABBITMQ-AMQP-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white" alt="RabbitMQ" />
    <img src="https://img.shields.io/badge/SPRING_SECURITY-OAUTH2-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security" />
    <img src="https://img.shields.io/badge/MAPSTRUCT-1.5.5-512BD4?style=for-the-badge" alt="MapStruct 1.5.5" />
    <img src="https://img.shields.io/badge/OPENAPI-SPRINGDOC_2.3.0-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="SpringDoc OpenAPI" />
  </p>

</div>

---

## <img src="https://img.shields.io/badge/ÍNDICE-5600EF?style=for-the-badge" height="20" align="absmiddle" /> ÍNDICE DE CONTENIDOS (13 SECCIONES)

| # | Sección | Documentación Detallada |
|:---:|---|---|
| 1 | Nombre del servicio y su bounded context | [01-nombre-servicio-bounded-context.md](docs/readmes/01-nombre-servicio-bounded-context.md) |
| 2 | Equipo y responsables | [02-equipo-responsables.md](docs/readmes/02-equipo-responsables.md) |
| 3 | Arquitectura hexagonal y diagramas | [03-arquitectura-diagramas.md](docs/readmes/03-arquitectura-diagramas.md) |
| 4 | Endpoints y enlace a Swagger | [04-endpoints-swagger.md](docs/readmes/04-endpoints-swagger.md) |
| 5 | Eventos que publica y consume (RabbitMQ) | [05-eventos-rabbitmq.md](docs/readmes/05-eventos-rabbitmq.md) |
| 6 | Tecnologías y dependencias | [06-tecnologias-dependencias.md](docs/readmes/06-tecnologias-dependencias.md) |
| 7 | Cómo ejecutar local y con Docker | [07-ejecucion-local-docker.md](docs/readmes/07-ejecucion-local-docker.md) |
| 8 | Variables de entorno | [08-variables-entorno.md](docs/readmes/08-variables-entorno.md) |
| 9 | Pruebas y cobertura | [09-pruebas-cobertura.md](docs/readmes/09-pruebas-cobertura.md) |
| 10 | CI/CD y pipeline | [10-cicd-pipeline.md](docs/readmes/10-cicd-pipeline.md) |
| 11 | Ramas y cómo contribuir | [11-ramas-contribuir.md](docs/readmes/11-ramas-contribuir.md) |
| 12 | Épica de Jira y requerimientos | [12-epica-jira-requerimientos.md](docs/readmes/12-epica-jira-requerimientos.md) |
| 13 | Análisis de tecnologías del servicio | [13-analisis-tecnologias.md](docs/readmes/13-analisis-tecnologias.md) |

---

<div align="center">

**STYLE RADAR · MS-USUARIOS**

<sub>CADCHE · Gestión de usuarios y preferencias · DOSW 2026</sub>

</div>

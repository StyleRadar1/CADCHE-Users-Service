# 4. Endpoints y enlace a Swagger

La documentación de la API de `ms-usuarios` utiliza OpenAPI 3.0 y permite consultar los endpoints disponibles en los diferentes entornos.

## 4.1. Enlaces por entorno

| Entorno | Estado documentado | URL base | Swagger UI | Health Check |
|---|---|---|---|---|
| **Producción (PROD)** | No confirmado | `https://styleradar-prod-app.azurewebsites.net` | [Swagger PROD](https://styleradar-prod-app.azurewebsites.net/swagger-ui/index.html) | [Health PROD](https://styleradar-prod-app.azurewebsites.net/actuator/health) |
| **Calidad (QA)** | Pendiente de validar | `https://styleradar-qa-app.azurewebsites.net` | [Swagger QA](https://styleradar-qa-app.azurewebsites.net/swagger-ui/index.html) | [Health QA](https://styleradar-qa-app.azurewebsites.net/actuator/health) |
| **Desarrollo local (DEV)** | Local | `http://localhost:8120` | [Swagger local](http://localhost:8120/swagger-ui.html) | [Health local](http://localhost:8120/actuator/health) |

**Especificación OpenAPI (JSON):**

- Local: `http://localhost:8120/v3/api-docs`
- QA y PROD: `/v3/api-docs` en los respectivos dominios.

> Estado documentado en el README original el 15/11/2026: el endpoint `/actuator/health` de PROD respondió HTTP 403 desde el entorno de revisión y QA no respondió antes del timeout. Esto no permite confirmar si los servicios están detenidos o restringidos por red.

## 4.2. Tabla de endpoints expuestos

| Método | Endpoint | Descripción | Rol requerido |
|---|---|---|---|
| `POST` | `/api/v1/users` | Registro de perfil de usuario comprador (`SR-RF-01`, `SR-RNF-02`). | Público |
| `GET` | `/api/v1/users/{id}` | Consulta del perfil propio por ID; un administrador puede consultar cualquier cuenta (`SR-RF-01`, `SR-RF-66`). | `COMPRADOR` o `ADMIN_STYLERADAR` |
| `PUT` | `/api/v1/users/{id}/style-preferences` | Actualización de preferencias de estilo (`SR-RF-03`). El ID debe corresponder al usuario autenticado. | `COMPRADOR` |
| `PUT` | `/api/v1/users/{id}/sizes` | Configuración de tallas habituales (`SR-RF-04`). El ID debe corresponder al usuario autenticado. | `COMPRADOR` |
| `GET` | `/api/v1/users/{id}/feed` | Feed paginado de prendas sugeridas (`SR-RF-05`). | `COMPRADOR` |
| `GET` | `/api/v1/users/{id}/recommendations` | Recomendaciones personalizadas (`SR-RF-06`). | `COMPRADOR` |
| `POST` | `/api/v1/users/{id}/saved-searches` | Registro de filtros y coordenadas de búsquedas frecuentes (`SR-RF-28`). | `COMPRADOR` |
| `GET` | `/api/v1/users/{id}/saved-searches` | Consulta de búsquedas guardadas. | `COMPRADOR` |
| `DELETE` | `/api/v1/users/{id}/saved-searches/{busquedaId}` | Eliminación de una búsqueda guardada. | `COMPRADOR` |
| `GET` | `/api/v1/admin/users` | Listado paginado y filtrable de usuarios (`SR-RF-66`). | `ADMIN_STYLERADAR` |
| `GET` | `/api/v1/admin/users/{id}` | Consulta administrativa del detalle de una cuenta. | `ADMIN_STYLERADAR` |
| `PATCH` | `/api/v1/admin/users/{id}/status` | Cambio de estado de cuenta con motivo (`SR-RF-66`). | `ADMIN_STYLERADAR` |
| `GET` | `/actuator/health` | Consulta del estado operativo del microservicio. | Público |

## 4.3. C

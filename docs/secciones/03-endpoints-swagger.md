# 3. API y Swagger

La API usa el prefijo `/api/v1`. Swagger UI y OpenAPI permiten consultar los esquemas desplegados.

## 3.1. Entornos

| Entorno | URL base | Swagger UI | Health |
|---|---|---|---|
| Local | `http://localhost:8120` | [`/swagger-ui/index.html`](http://localhost:8120/swagger-ui/index.html) | [`/actuator/health`](http://localhost:8120/actuator/health) |
| QA | `https://styleradar-qa-app.azurewebsites.net` | [Swagger QA](https://styleradar-qa-app.azurewebsites.net/swagger-ui/index.html) | [Health QA](https://styleradar-qa-app.azurewebsites.net/actuator/health) |
| PROD | `https://styleradar-prod-app.azurewebsites.net` | [Swagger PROD](https://styleradar-prod-app.azurewebsites.net/swagger-ui/index.html) | [Health PROD](https://styleradar-prod-app.azurewebsites.net/actuator/health) |

OpenAPI JSON: `/v3/api-docs` en cada URL base. El health check indica disponibilidad, no identifica la versión desplegada.

## 3.2. Rutas de usuarios

| Método | Ruta | Acceso |
|---|---|---|
| `POST` | `/api/v1/users` | Público; registro. `accountType` puede ser `BUYER` (predeterminado) o `STORE`; solo estos tipos pueden seleccionarse públicamente. |
| `GET` | `/api/v1/users/{id}` | JWT; el usuario consulta su cuenta y `ADMIN_STYLERADAR` puede consultar otras cuentas. |
| `PUT` | `/api/v1/users/{id}` | JWT del mismo usuario; actualiza `name`, `phone` y `neighborhood`. |
| `PUT` | `/api/v1/users/{id}/password` | JWT del mismo usuario; cambia contraseña validando la actual. |
| `PUT` | `/api/v1/users/{id}/style-preferences` | JWT de comprador o administrador. |
| `PUT` | `/api/v1/users/{id}/sizes` | JWT de comprador o administrador. |
| `GET` | `/api/v1/users/{id}/feed` | JWT de comprador o administrador. |
| `GET` | `/api/v1/users/{id}/recommendations` | JWT de comprador o administrador. |
| `POST` | `/api/v1/users/{id}/saved-searches` | JWT de comprador o administrador; guarda búsqueda. |
| `GET` | `/api/v1/users/{id}/saved-searches` | JWT de comprador o administrador; lista búsquedas guardadas. |
| `DELETE` | `/api/v1/users/{id}/saved-searches/{searchId}` | JWT de comprador o administrador; elimina búsqueda. |

Las contraseñas nuevas deben tener entre 8 y 72 caracteres e incluir mayúscula, minúscula, número y símbolo.

## 3.3. Rutas administrativas

Requieren rol `ADMIN_STYLERADAR`.

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/v1/admin/users` | Listado paginado con filtros. |
| `GET` | `/api/v1/admin/users/{id}` | Detalle de cuenta. |
| `PATCH` | `/api/v1/admin/users/{id}/status` | Cambia estado de cuenta y registra motivo. |
| `PATCH` | `/api/v1/admin/users/{id}/role` | Cambia rol; acepta valores ingleses y alias heredados en español. |
| `DELETE` | `/api/v1/admin/users/{id}` | Elimina cuenta. |

## 3.4. Rutas internas

Las rutas internas requieren Basic Auth de servicio.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/v1/internal/users/verify-credentials` | Verifica correo y contraseña para ms-auth. |
| `POST` | `/api/v1/internal/users/lookup` | Busca cuenta por correo para recuperación de contraseña. |
| `POST` | `/api/v1/internal/users/oauth2` | Crea o consulta cuenta asociada a Google. |
| `PATCH` | `/api/v1/internal/users/{id}/email-verification` | Marca el correo como verificado. |
| `PUT` | `/api/v1/internal/users/{id}/password` | Restablece contraseña tras la validación del flujo de recuperación. |
| `GET` | `/api/v1/internal/users/{userId}/measurements` | Consulta medidas del usuario para consumo interno. |

Se conservan rutas internas heredadas para verificación de credenciales y verificación de correo como compatibilidad; las rutas canónicas son las indicadas arriba. Las rutas heredadas no sustituyen la autenticación de servicio.

## 3.5. Formato de errores

Los errores siguen Problem Detail e incluyen `type`, `title`, `status`, `detail`, `instance`, `code` y `errors` cuando aplica. El `type` usa `https://api.styleradar.com/errors/<codigo-en-minusculas-con-guiones>`.

```json
{
  "type": "https://api.styleradar.com/errors/invalid-data",
  "title": "Bad Request",
  "status": 400,
  "detail": "La solicitud contiene datos inválidos",
  "instance": "/api/v1/users",
  "code": "INVALID_DATA",
  "errors": { "email": "El formato del email es inválido" }
}
```

---

[← Anterior: 2. Arquitectura y diagramas](02-arquitectura-diagramas.md) · [Índice](../../README.md) · [Siguiente: 4. Eventos](04-eventos-rabbitmq.md)

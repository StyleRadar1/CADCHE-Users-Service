# 4. Endpoints y enlace a Swagger

La documentación de la API de `ms-usuarios` utiliza OpenAPI 3.0 y permite consultar los endpoints disponibles en los diferentes entornos.

## 4.1. Enlaces por entorno

| Entorno | Verificación (24/11/2026) | URL base | Swagger UI | Health Check |
|---|---|---|---|---|
| **Producción (PROD)** | Health y Swagger responden HTTP 200 | `https://styleradar-prod-app.azurewebsites.net` | [Swagger PROD](https://styleradar-prod-app.azurewebsites.net/swagger-ui/index.html) | [Health PROD](https://styleradar-prod-app.azurewebsites.net/actuator/health) |
| **Calidad (QA)** | Health y Swagger responden HTTP 200 | `https://styleradar-qa-app.azurewebsites.net` | [Swagger QA](https://styleradar-qa-app.azurewebsites.net/swagger-ui/index.html) | [Health QA](https://styleradar-qa-app.azurewebsites.net/actuator/health) |
| **Desarrollo local (DEV)** | Local | `http://localhost:8120` | [Swagger local](http://localhost:8120/swagger-ui.html) | [Health local](http://localhost:8120/actuator/health) |

**Especificación OpenAPI (JSON):**

- Local: `http://localhost:8120/v3/api-docs`
- QA y PROD: `/v3/api-docs` en los respectivos dominios.

La verificación HTTP confirma que los endpoints responden desde el entorno de revisión; no identifica qué versión de la aplicación está desplegada.

## 4.2. Tabla de endpoints expuestos

| Método | Endpoint | Descripción | Rol requerido |
|---|---|---|---|
| `POST` | `/api/v1/users` | Registro de perfil de usuario comprador (`SR-RF-01`, `SR-RNF-02`). | Público |
| `GET` | `/api/v1/users/{id}` | Consulta del perfil propio por ID; un administrador puede consultar cualquier cuenta (`SR-RF-01`, `SR-RF-66`). | `COMPRADOR` o `ADMIN_STYLERADAR` |
| `PUT` | `/api/v1/users/{id}` | Actualiza nombre y, si se envían, teléfono y barrio; devuelve el perfil completo. El ID debe coincidir con el del token. | `COMPRADOR` |
| `PUT` | `/api/v1/users/{id}/style-preferences` | Actualización de preferencias de estilo (`SR-RF-03`). Compradores solo pueden modificar las propias; el admin puede usar cualquier ID. | `COMPRADOR` o `ADMIN_STYLERADAR` |
| `PUT` | `/api/v1/users/{id}/sizes` | Configuración de tallas habituales (`SR-RF-04`). Compradores solo pueden modificar las propias; el admin puede usar cualquier ID. | `COMPRADOR` o `ADMIN_STYLERADAR` |
| `GET` | `/api/v1/users/{id}/feed` | Feed paginado de prendas sugeridas (`SR-RF-05`). Compradores consultan el propio; el admin puede usar cualquier ID. | `COMPRADOR` o `ADMIN_STYLERADAR` |
| `GET` | `/api/v1/users/{id}/recommendations` | Recomendaciones personalizadas (`SR-RF-06`). Compradores consultan las propias; el admin puede usar cualquier ID. | `COMPRADOR` o `ADMIN_STYLERADAR` |
| `POST` | `/api/v1/users/{id}/saved-searches` | Registro de filtros y coordenadas de búsquedas frecuentes (`SR-RF-28`). | `COMPRADOR` o `ADMIN_STYLERADAR` |
| `GET` | `/api/v1/users/{id}/saved-searches` | Consulta de búsquedas guardadas. | `COMPRADOR` o `ADMIN_STYLERADAR` |
| `DELETE` | `/api/v1/users/{id}/saved-searches/{searchId}` | Eliminación de una búsqueda guardada. | `COMPRADOR` o `ADMIN_STYLERADAR` |
| `GET` | `/api/v1/admin/users` | Listado paginado y filtrable de usuarios (`SR-RF-66`). | `ADMIN_STYLERADAR` |
| `GET` | `/api/v1/admin/users/{id}` | Consulta administrativa del detalle de una cuenta. | `ADMIN_STYLERADAR` |
| `PATCH` | `/api/v1/admin/users/{id}/status` | Cambio de estado de cuenta con motivo (`SR-RF-66`). | `ADMIN_STYLERADAR` |
| `DELETE` | `/api/v1/admin/users/{id}` | Eliminación de cuenta (`SR-RF-115`); el front debe pedir confirmación antes de llamar. | `ADMIN_STYLERADAR` |
| `GET` | `/api/v1/internal/users/{userId}/measurements` | Consulta interna de medidas corporales. | Basic Auth de servicio |
| `GET` | `/actuator/health` | Consulta del estado operativo del microservicio. | Público |

### Actualizar datos personales (`PUT /api/v1/users/{id}`)

Requiere JWT de comprador y que `{id}` coincida con el `sub` del token. `name` es obligatorio; `phone` y `neighborhood` son opcionales. Si se omiten, se conservan los valores almacenados. La respuesta es el perfil completo, con la misma estructura que `GET /api/v1/users/{id}`.

```json
{
  "name": "Laura Gómez",
  "phone": "+573001234567",
  "neighborhood": "Chapinero"
}
```

Respuestas: `200` con perfil actualizado, `400` con errores por campo en `errors`, `401` con código `AUTHENTICATION_REQUIRED` si falta o es inválido el JWT, y `403` con código `ACCESS_DENIED` si el ID no corresponde al usuario autenticado.

### Formato de error

Los errores de la API usan ProblemDetail e incluyen `type`, `title`, `status`, `detail`, `instance`, `code` y `errors`. Los errores de validación usan el código `INVALID_DATA`; `errors` contiene los mensajes asociados a los campos cuando aplica.

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "La solicitud contiene datos inválidos",
  "instance": "/api/v1/users",
  "code": "INVALID_DATA",
  "errors": {
    "email": "El formato del email es inválido"
  },
  "message": "La solicitud contiene datos inválidos"
}
```

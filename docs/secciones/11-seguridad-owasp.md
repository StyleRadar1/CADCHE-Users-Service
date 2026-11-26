# 11. Seguridad y revisión OWASP

El documento fuente [SR_CADCHE_Usuarios_Seguridad_y_OWASP_2025.md](../security/SR_CADCHE_Usuarios_Seguridad_y_OWASP_2025.md) corresponde a una revisión estática fechada el 12/11/2026. No es una prueba de penetración ni valida por sí solo Azure, la red, PostgreSQL, RabbitMQ o los servicios externos. Los hallazgos de esta página se contrastaron con el código disponible el 25/11/2026; la evidencia del entorno debe verificarse por separado.

## 11.1. Controles implementados observados en el repositorio

| Área | Control observado |
|---|---|
| Autorización | Spring Security aplica roles en rutas administrativas, rutas internas y API de usuarios. Las rutas internas exigen rol de servicio mediante Basic Auth. |
| Tokens | La API valida JWT como OAuth2 Resource Server; ms-auth emite los tokens. |
| Contraseñas de cuentas | BCrypt y validación de longitud y complejidad para contraseñas registradas o cambiadas. |
| Validación de entrada | DTOs aplican Jakarta Bean Validation; las consultas de persistencia usan Spring Data JPA. |
| Errores | El manejador global evita devolver trazas de excepción y usa `code` en el formato de error de la API. |
| Trazabilidad | `CorrelationIdFilter` propaga `X-Correlation-Id` o `X-Request-Id` a MDC y a la respuesta, con sanitización del valor. |
| Cliente de catálogo | Timeouts configurados de 3 s para conexión y 5 s para lectura; feed y recomendaciones pueden degradar a lista vacía ante indisponibilidad. |
| Dependencias de Actions | Las acciones de GitHub usadas por los workflows están fijadas a SHA. |
| Contenedor | `Dockerfile` y `Dockerfile.runtime` corren con el usuario sin privilegios `appuser` y tienen `HEALTHCHECK`. |

## 11.2. Riesgos y verificaciones pendientes

La revisión estática señaló configuraciones que deben tratarse como deuda hasta corregirse y verificarse en despliegue:

- `application.yml` configura la contraseña Basic interna con `{noop}` y valores predeterminados `guest` para RabbitMQ; asegurar que los entornos siempre inyecten credenciales robustas y que no se use el perfil base de forma inadvertida.
- El perfil base habilita `show-sql`; los perfiles QA y PROD lo deshabilitan. Confirmar que QA/PROD activan su perfil y que los logs no exponen datos sensibles.
- CSRF se ignora para `/api/**`. Esta configuración depende de que la API use exclusivamente bearer JWT y no autenticación basada en cookies.
- La firma HS256 requiere un secreto de al menos 32 bytes, almacenado fuera del repositorio. Validación de issuer/audience, rotación de secretos, cifrado de conexiones y controles de red deben revisarse en los entornos.
- El compose local publica PostgreSQL y RabbitMQ en puertos del host (configurables en `.env`); usarlo solo en desarrollo local.
- El flujo no evidencia por sí solo controles de rate limiting, alertas centralizadas, SBOM o firma de imágenes. Tampoco se configura patrón Outbox ni confirmación de publicación RabbitMQ en el código revisado.
- La autorización por rol no sustituye las comprobaciones de propiedad del recurso; mantener y ejecutar pruebas negativas para IDs de otro usuario y operaciones administrativas.

Los puntos anteriores no afirman que una configuración de Azure esté expuesta o sea incorrecta: deben comprobarse con la configuración efectiva del entorno. Tampoco se deben marcar como resueltos sin cambio de código/configuración y evidencia.

## 11.3. Alcance de la revisión

El informe OWASP analiza estáticamente código, dependencias y configuración visibles al momento de la revisión. “No evidenciado” significa que el control no se pudo confirmar en ese material; no demuestra ausencia en infraestructura externa. La validación de runtime y pruebas dinámicas queda fuera del alcance del informe.

---

[← Anterior: 10. Contribución](10-ramas-contribuir.md) · [Índice](../../README.md) · [Siguiente: 12. Equipo y Jira](12-equipo-jira-requerimientos.md)

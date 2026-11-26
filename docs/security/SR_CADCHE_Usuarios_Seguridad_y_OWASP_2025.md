# Revisión de seguridad — OWASP Top 10:2025

**Servicio:** ms-usuarios — CADCHE / StyleRadar  

**Fecha de revisión:** 12 de noviembre de 2026  

**Alcance:** revisión estática del código, configuración, dependencias y pipeline presentes en la copia del repositorio analizada.  

**Referencia:** OWASP Top 10:2025 (https://top10.owasp.org/2025/es/).  

**Criterio de lectura:** “No evidenciado” significa que no se encontró una implementación o evidencia suficiente en los archivos revisados; no demuestra por sí solo que el control no exista en infraestructura externa. Esta revisión no sustituye un pentest, análisis dinámico ni auditoría de Azure, API Gateway, PostgreSQL o RabbitMQ.


> **Actualización (25/11/2026):** esta revisión es del 12/11/2026. Desde entonces ya se cerró el hallazgo de A02 sobre el contenedor: `Dockerfile` y `Dockerfile.runtime` corren con un usuario sin privilegios (`appuser`) y tienen `HEALTHCHECK`. También se agregó el identificador de correlación por solicitud (`CorrelationIdFilter`, A09). Los demás hallazgos se mantienen como están descritos.

## Resumen ejecutivo

La revisión encuentra controles positivos, como autorización por roles en Spring Security, validación de JWT, BCrypt para contraseñas de usuarios, validación de DTOs, consultas derivadas de Spring Data JPA y manejo centralizado de errores. Sin embargo, hay controles incompletos o no demostrados que deben cerrarse antes de considerar el servicio endurecido para producción.

| Categoría | ¿Aplica? | Estado observado | Acción principal |
|---|---|---|---|
| A01:2025 — Pérdida de Control de Acceso | Sí | Parcial / requiere verificación | Completar y probar autorización por propiedad del recurso; revisar endpoints que aún devuelven datos de ejemplo. |
| A02:2025 — Configuración de Seguridad Incorrecta | Sí | Pendiente | Retirar valores inseguros por defecto, desactivar SQL detallado en entornos no locales y ejecutar el contenedor como usuario no root. |
| A03:2025 — Fallas en la Cadena de Suministro de Software | Sí | Parcial | Mantener acciones fijadas por SHA y añadir escaneo de dependencias e imágenes; fijar versiones de imágenes base. |
| A04:2025 — Fallas Criptográficas | Sí | Parcial | Confirmar TLS y cifrado en reposo; gestionar claves fuera del repositorio y eliminar configuraciones de contraseña noop. |
| A05:2025 — Inyección | Sí | Sin hallazgo directo en la revisión estática | Mantener consultas parametrizadas y validación; añadir pruebas de seguridad de entradas y revisar futuras consultas dinámicas. |
| A06:2025 — Diseño Inseguro | Sí | Pendiente / no evidenciado | Definir controles contra abuso, límites de solicitudes y escenarios de amenaza para registro y verificación de credenciales. |
| A07:2025 — Fallas de Autenticación | Sí | Parcial | Añadir protección contra fuerza bruta y verificar políticas completas de JWT y credenciales de servicio. |
| A08:2025 — Fallas en la Integridad del Software o de los Datos | Sí | Parcial | Incorporar verificación/procedencia de artefactos y robustecer la publicación de eventos; validar datos recibidos de sistemas externos. |
| A09:2025 — Fallas en el Registro y Alertamiento de Seguridad | Sí | Pendiente | Registrar eventos relevantes de seguridad y configurar alertas; hoy se observan principalmente errores técnicos. |
| A10:2025 — Manejo Inadecuado de Condiciones Excepcionales | Sí | Parcial | Mantener respuestas genéricas y agregar timeouts, límites y pruebas de fallos en dependencias externas y mensajería. |

## Revisión por categoría

### A01:2025 — Pérdida de Control de Acceso

**¿Aplica?** Sí. El servicio expone operaciones de perfil, preferencias, tallas, búsquedas y administración de estados de cuenta.

**Estado y evidencia observada**

- SecurityConfig define rutas para COMPRADOR y ADMIN_STYLERADAR, y deniega rutas no contempladas mediante denyAll().
- Algunas operaciones de perfil obtienen el identificador desde el sub del JWT.
- No se encontró un mecanismo transversal de autorización por propiedad del recurso (ownership guard) en la copia revisada.
- UsuarioController.obtenerPerfil() y obtenerPorId() contienen respuestas de ejemplo; otros métodos de búsquedas también retornan valores de ejemplo. Por tanto, no se puede dar por verificada la autorización y consulta real de los recursos.
- GestionEstadoCuentaController toma el identificador del administrador desde Authentication.getName(). Debe confirmarse que el name del token siempre corresponde al identificador esperado y no a un correo u otro valor.

**Acción**

1. Implementar autorización por propiedad: un comprador solo puede leer o modificar recursos asociados al sub de su JWT.
1. Mantener las operaciones administrativas separadas y protegidas por rol.
1. Reemplazar respuestas de ejemplo por consultas reales y aplicar las mismas verificaciones de autorización.
1. Añadir pruebas negativas: usuario A intenta consultar, modificar o eliminar recursos del usuario B; un comprador intenta acceder a rutas administrativas.

**Prioridad:** Alta.

### A02:2025 — Configuración de Seguridad Incorrecta

**¿Aplica?** Sí. Hay configuración de aplicación, base de datos, broker, seguridad y contenedor.

**Estado y evidencia observada**

- application.yml define spring.security.user.password con prefijo {noop}. La configuración de seguridad usa un UserDetailsService propio y BCrypt, por lo que debe comprobarse si esta propiedad residual está activa o es innecesaria; no debe mantenerse como configuración de respaldo.
- application.yml tiene valores predeterminados guest/guest para RabbitMQ.
- application-dev.yml activa spring.jpa.show-sql: true; el perfil dev es el perfil predeterminado en application.yml.
- El Dockerfile no define un usuario no privilegiado (USER), por lo que la imagen ejecuta la aplicación con el usuario predeterminado de la imagen base.
- docker-compose.yml publica el puerto de PostgreSQL al host. Es práctico para desarrollo, pero no debe replicarse en despliegues productivos sin necesidad.
- La excepción CSRF para /api/** depende de que la autenticación JWT se envíe únicamente por Authorization y no por cookies. La excepción del endpoint interno usa HTTP Basic; debe estar limitada a comunicación de servicio y no quedar accesible desde navegadores.

**Acción**

1. Eliminar la propiedad spring.security.user.* si no se utiliza y retirar cualquier uso de {noop}.
1. Exigir credenciales de RabbitMQ en entornos no locales; no usar guest como valor de producción.
1. Desactivar show-sql en QA/producción y revisar que los logs no incluyan datos personales ni secretos.
1. Crear un usuario no root en la imagen Docker y usarlo con USER.
1. Revisar reglas de red/firewall para PostgreSQL y el endpoint /internal/api/v1/usuarios/verificar-credenciales.
1. Documentar y comprobar que JWT se transmite por Authorization y no mediante cookies antes de aceptar la excepción CSRF de /api/**.

**Prioridad:** Alta.

### A03:2025 — Fallas en la Cadena de Suministro de Software

**¿Aplica?** Sí. El servicio depende de Maven, imágenes Docker, GitHub Actions y artefactos publicados en Azure Container Registry.

**Estado y evidencia observada**

- Las acciones checkout, setup-java, upload-artifact y azure/login del workflow están fijadas a SHA completos, lo cual reduce el riesgo de cambios inesperados en etiquetas.
- El pom.xml declara dependencias directas y plugins, pero no se observa un paso de análisis de vulnerabilidades de dependencias (por ejemplo, OWASP Dependency-Check o escaneo equivalente).
- El Dockerfile usa etiquetas mutables (maven:3.9.9-eclipse-temurin-17 y eclipse-temurin:17-jre) sin digest.
- No se observa firma de imágenes ni generación/verificación de attestations o SBOM en el pipeline.

**Acción**

1. Añadir escaneo de dependencias Maven y establecer una política para vulnerabilidades críticas/altas.
1. Escanear la imagen Docker antes de publicarla.
1. Fijar imágenes base por digest y actualizar de forma controlada.
1. Generar SBOM y, si la plataforma lo permite, firmar la imagen y verificar su procedencia antes del despliegue.

**Prioridad:** Media-alta.

### A04:2025 — Fallas Criptográficas

**¿Aplica?** Sí. El servicio procesa contraseñas, tokens JWT y credenciales de conexión.

**Estado y evidencia observada**

- PasswordEncoderConfig utiliza BCryptPasswordEncoder.
- RegistrarUsuarioUseCaseImpl almacena la contraseña codificada mediante passwordEncoder.encode(...).
- JwtConfig valida la clave HMAC y exige al menos 32 bytes para HS256.
- La clave JWT se obtiene de configuración externa; en application-test.yml existe una clave fija de pruebas, aislada del perfil de test.
- En application.yml permanece una propiedad de contraseña {noop} que debe eliminarse si no se utiliza.
- En los archivos revisados no hay evidencia suficiente para confirmar TLS entre clientes y servicio, cifrado en reposo de PostgreSQL ni rotación de secretos.

**Acción**

1. Eliminar configuraciones noop y asegurar que todas las contraseñas persistidas pasan por BCrypt.
1. Guardar secretos en un gestor de secretos (por ejemplo, Azure Key Vault) y establecer rotación.
1. Verificar TLS para tráfico externo, conexiones a PostgreSQL y RabbitMQ.
1. Confirmar cifrado en reposo y controles de acceso a backups.
1. Mantener la clave de pruebas exclusivamente en test y evitar reutilizarla en otros entornos.

**Prioridad:** Alta.

### A05:2025 — Inyección

**¿Aplica?** Sí. El servicio recibe datos HTTP y consulta PostgreSQL y un catálogo externo.

**Estado y evidencia observada**

- Los repositorios revisados utilizan métodos derivados de Spring Data JPA (findByEmail, existsByEmail, countByRolAndEstado); no se observó SQL concatenado en esos repositorios.
- Los DTOs de entrada utilizan Jakarta Bean Validation, incluyendo límites de longitud, rangos numéricos, formato de correo y validaciones de campos.
- CatalogoRestAdapter construye parámetros de consulta mediante UriBuilder, en lugar de concatenar manualmente una URL.
- En la revisión estática realizada no se identificó una consulta SQL construida con entradas del usuario ni un hallazgo directo de inyección. Esto no descarta XSS, inyección en otros componentes ni fallos que solo aparezcan en ejecución.

**Acción**

1. Mantener consultas parametrizadas y evitar construir JPQL/SQL mediante concatenación.
1. Aplicar validación de entrada en todos los endpoints, incluidos los nuevos.
1. Añadir pruebas con caracteres especiales, payloads de inyección y límites de longitud.
1. Revisar el frontend y cualquier consumidor del contenido para codificación de salida contra XSS.

**Prioridad:** Media (mantener controles y pruebas).

### A06:2025 — Diseño Inseguro

**¿Aplica?** Sí. El servicio gestiona identidad de perfil, consentimiento, estado de cuenta y consultas de personalización.

**Estado y evidencia observada**

- Se valida la aceptación de Habeas Data en el DTO de registro.
- Existen reglas de dominio para estados de cuenta y validación de preferencias/tallas.
- No se encontró evidencia de límites de solicitudes, protección contra automatización o políticas de bloqueo/espera para intentos repetidos sobre el endpoint interno de verificación.
- No se encontró un documento de modelado de amenazas o una matriz de abuso para registro, consulta de perfiles y administración de cuentas.
- Algunas operaciones del controlador aún tienen respuestas de ejemplo, lo que impide confirmar el flujo completo de controles.

**Acción**

1. Documentar amenazas y abusos posibles para registro, enumeración de correos, verificación de credenciales y cambios de estado.
1. Definir límites de solicitudes en API Gateway o en el servicio.
1. Establecer políticas de bloqueo progresivo/espera y detección de automatización donde corresponda.
1. Añadir pruebas de abuso, autorización y consentimiento.

**Prioridad:** Alta.

### A07:2025 — Fallas de Autenticación

**¿Aplica?** Sí. Aunque la autenticación principal se delega a ms-auth, este servicio valida JWT y protege un endpoint interno con HTTP Basic.

**Estado y evidencia observada**

- Las rutas API utilizan Spring Security OAuth2 Resource Server y un JwtDecoder HS256.
- El endpoint interno de verificación requiere HTTP Basic y el rol SERVICE.
- La contraseña interna se codifica con BCrypt al construir el usuario en memoria.
- No se observa protección local contra intentos repetidos sobre el endpoint interno.
- JwtConfig configura la verificación criptográfica del token; no se observa configuración explícita de validación de issuer o audience.
- La seguridad de autenticación de usuario final depende también de ms-auth, por lo que debe revisarse en conjunto.

**Acción**

1. Configurar y validar issuer y audience esperados en los JWT, además de firma, expiración y algoritmo.
1. Limitar el endpoint interno por red y aplicar rate limiting.
1. Rotar las credenciales de servicio y evitar reutilizarlas entre entornos.
1. Revisar conjuntamente con ms-auth el ciclo de vida del token, bloqueo de cuentas y protección contra fuerza bruta.

**Prioridad:** Alta.

### A08:2025 — Fallas en la Integridad del Software o de los Datos

**¿Aplica?** Sí. El servicio consume datos de catálogo y publica eventos de cambio de estado.

**Estado y evidencia observada**

- El pipeline fija las acciones de GitHub por SHA, lo que aporta integridad a esa parte de la cadena de construcción.
- CatalogoRestAdapter transforma datos recibidos del catálogo y contempla campos nulos en algunas propiedades; no se observa una verificación criptográfica del origen o integridad de esos datos.
- RabbitMQUsuarioEstadoEventPublisher publica eventos directamente con RabbitTemplate; no se encontró evidencia de patrón Outbox, confirmaciones de publicación o manejo de duplicados en el código revisado.
- No se observa firma de artefactos ni verificación de procedencia de la imagen desplegada.

**Acción**

1. Validar estructura, rangos y campos obligatorios de las respuestas externas antes de utilizarlas.
1. Evaluar Outbox transaccional, publisher confirms e idempotencia para eventos de negocio.
1. Incorporar SBOM, firma y verificación de imágenes/artefactos.
1. Mantener validaciones de integridad de datos en límites entre microservicios.

**Prioridad:** Media-alta.

### A09:2025 — Fallas en el Registro y Alertamiento de Seguridad

**¿Aplica?** Sí. El servicio gestiona accesos, errores de autenticación, cambios de estado y operaciones con datos personales.

**Estado y evidencia observada**

- GlobalExceptionHandler registra errores de catálogo y errores inesperados.
- No se encontraron registros explícitos de eventos de seguridad como intentos de autenticación fallidos, accesos denegados, cambios administrativos de estado o anomalías de autorización.
- La exposición de Actuator en application.yml se limita a health e info, pero no se observa configuración de alertas ni integración de auditoría en el código revisado.
- No se encontró evidencia de correlación de eventos mediante identificador de solicitud.

**Acción**

1. Registrar eventos relevantes de seguridad: fallos de autenticación, denegaciones, cambios de estado y operaciones administrativas.
1. No registrar contraseñas, tokens, secretos ni datos personales innecesarios.
1. Enviar logs a una plataforma centralizada y configurar alertas para patrones anómalos.
1. Añadir un identificador de correlación por solicitud y conservar trazabilidad de cambios administrativos.

**Prioridad:** Media-alta.

### A10:2025 — Manejo Inadecuado de Condiciones Excepcionales

**¿Aplica?** Sí. El servicio depende de PostgreSQL, RabbitMQ y el catálogo externo.

**Estado y evidencia observada**

- GlobalExceptionHandler traduce errores de validación y dominio a respuestas HTTP, y devuelve un mensaje genérico ante excepciones inesperadas.
- Los errores no controlados se registran en el servidor sin devolver el stack trace al cliente.
- CatalogoRestAdapter utiliza RestClient; en el código revisado no se observa configuración explícita de timeouts de conexión/lectura ni política de reintentos acotados.
- La publicación RabbitMQ no muestra manejo local de fallos, confirmaciones o estrategia de recuperación.
- application.yml muestra ddl-auto: validate como valor predeterminado, lo que evita cambios automáticos del esquema, pero la disponibilidad de dependencias debe probarse en escenarios de fallo.

**Acción**

1. Configurar timeouts explícitos para conexiones HTTP y consultas externas.
1. Definir reintentos limitados con backoff y circuit breaker cuando sea apropiado; evitar reintentos infinitos.
1. Definir qué ocurre si RabbitMQ no está disponible al publicar un evento y cómo se recupera el evento.
1. Añadir pruebas de timeout, caída de catálogo, caída de broker, errores de base de datos y payloads inválidos.
1. Mantener respuestas externas genéricas y no revelar trazas ni detalles internos.

**Prioridad:** Media-alta.

## Plan de trabajo recomendado

1. Prioridad alta: cerrar A01, A02, A04, A06 y A07 (autorización por propiedad, configuración segura, secretos/TLS, protección contra abuso y controles de autenticación).
1. Prioridad media-alta: cerrar A03, A08, A09 y A10 (cadena de suministro, integridad de eventos, auditoría/alertas y resiliencia).
1. Mantenimiento: conservar las defensas actuales frente a A05 y ampliar las pruebas de entradas.

## Límites de esta revisión

Esta evaluación se realizó sobre los archivos disponibles en la copia del repositorio. No se verificaron:

- Reglas de red, WAF, API Gateway ni restricciones de acceso de Azure.
- Configuración real de TLS, Key Vault, cifrado en reposo, backups o permisos cloud.
- Políticas y configuración de ms-auth.
- Ejecución dinámica de pruebas de penetración o escáneres DAST.
- Configuración real del proyecto en SonarQube Cloud.

Por ello, los estados “parcial”, “pendiente” y “no evidenciado” deben cerrarse con pruebas o evidencia de infraestructura antes de declarar el control como implementado.

## Referencias

- OWASP Top 10:2025 — versión oficial en español (https://top10.owasp.org/2025/es/)
- OWASP Top 10:2025 — introducción y cambios (https://top10.owasp.org/2025/0x00_2025-Introduction/)
- Repositorio oficial OWASP Top 10 (https://github.com/OWASP/Top10)

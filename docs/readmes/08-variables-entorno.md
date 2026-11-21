# 8. Variables de entorno

El microservicio `ms-usuarios` utiliza variables de entorno para configurar la conexión con la base de datos, los servicios externos, RabbitMQ y los mecanismos de seguridad.

Estas variables permiten ejecutar el servicio en diferentes ambientes sin modificar directamente el código fuente.

## 8.1. Variables de configuración

| Variable | Descripción | Valor por defecto o referencia |
|---|---|---|
| `SERVER_PORT` | Puerto en el que se ejecuta el microservicio. | `8120` |
| `DB_URL` | URL de conexión a PostgreSQL. | `jdbc:postgresql://localhost:5432/styleradar_usuarios` |
| `DB_USERNAME` | Usuario de la base de datos. | `postgres` |
| `DB_PASSWORD` | Contraseña de PostgreSQL. | Configuración del ambiente |
| `INTERNAL_AUTH_USERNAME` | Usuario para la autenticación de solicitudes internas. | Configuración del ambiente |
| `INTERNAL_AUTH_PASSWORD` | Contraseña para la autenticación de solicitudes internas. | Configuración del ambiente |
| `JWT_SECRET_BASE64` | Secreto utilizado para validar tokens JWT. | Configuración del ambiente |
| `JWT_SECRET` | Variable alternativa para el secreto JWT. | Configuración del ambiente |
| `RABBITMQ_HOST` | Host del servidor RabbitMQ. | `localhost` |
| `RABBITMQ_PORT` | Puerto de conexión de RabbitMQ. | `5672` |
| `RABBITMQ_USERNAME` | Usuario de RabbitMQ. | `guest` |
| `RABBITMQ_PASSWORD` | Contraseña de RabbitMQ. | `guest` |
| `CATALOGO_API_BASE_URL` | URL base del microservicio de catálogo de artículos. | URL configurada para QA |

## 8.2. Configuración por ambiente

Los valores pueden cambiar dependiendo del ambiente en el que se ejecute el microservicio:

- **Local:** utiliza PostgreSQL y RabbitMQ disponibles en el entorno de desarrollo.
- **Docker:** utiliza las variables configuradas en `docker-compose.yml`.
- **QA y producción:** utiliza las variables de configuración definidas en el servicio de despliegue.

## 8.3. Seguridad

Las contraseñas y secretos no deben publicarse directamente en el repositorio.

Se recomienda configurar estos valores mediante variables de entorno y mantenerlos protegidos en los ambientes correspondientes.

## 8.4. Consideraciones

- La aplicación utiliza PostgreSQL como sistema de persistencia.
- RabbitMQ se configura mediante sus variables de conexión.
- El secreto JWT debe estar correctamente configurado para validar los tokens.
- La configuración de la base de datos debe coincidir con el ambiente donde se ejecuta el servicio.

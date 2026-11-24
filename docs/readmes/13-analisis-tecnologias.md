# 13. Análisis de tecnologías del servicio

El microservicio `ms-usuarios` utiliza diferentes tecnologías y herramientas para implementar su arquitectura, gestionar la información de los usuarios, exponer servicios REST y facilitar su despliegue.

## 13.1. Lenguaje y framework

| Tecnología | Versión | Análisis y propósito |
|---|---|---|
| Java | 21 LTS | Lenguaje principal del microservicio. Permite implementar la lógica de negocio y aprovechar las características del ecosistema Java. |
| Spring Boot | 3.2.12 | Facilita la configuración y construcción de la aplicación, además de proporcionar integración con diferentes componentes. |
| Maven | 3.9.9 (imagen de construcción Docker) | Gestiona las dependencias y automatiza la compilación y ejecución de pruebas. |

## 13.2. Persistencia de información

| Tecnología | Análisis y propósito |
|---|---|
| PostgreSQL | Base de datos relacional utilizada para almacenar la información de los usuarios. |
| Spring Data JPA | Facilita la comunicación entre la aplicación y la base de datos mediante repositorios. |
| Flyway | Permite gestionar las migraciones y los cambios en la estructura de la base de datos. |

Estas tecnologías permiten mantener organizada la información y controlar los cambios realizados en el esquema de persistencia.

## 13.3. Comunicación y seguridad

| Tecnología | Análisis y propósito |
|---|---|
| Spring Web | Permite construir los controladores y exponer los endpoints REST. |
| Spring AMQP | Facilita la comunicación asíncrona mediante RabbitMQ. |
| Spring Security | Proporciona mecanismos de seguridad para proteger los recursos del servicio. |
| OAuth2 Resource Server | Permite configurar la validación de tokens de acceso. |
| JWT | Se utiliza para trabajar con tokens de autenticación y autorización. |

La comunicación del microservicio combina solicitudes REST para operaciones que requieren respuestas directas y mensajería mediante RabbitMQ para eventos que pueden procesarse de manera asíncrona.

## 13.4. Herramientas de desarrollo

| Tecnología | Análisis y propósito |
|---|---|
| Lombok | Reduce código repetitivo mediante anotaciones. |
| MapStruct | Facilita la conversión entre objetos del dominio, DTO y otras estructuras. |
| SpringDoc OpenAPI | Genera la documentación de los endpoints y permite consultarlos mediante Swagger UI. |
| Spring Boot Actuator | Expone información de supervisión, incluyendo el estado de salud de la aplicación. |

## 13.5. Pruebas y calidad

| Tecnología | Análisis y propósito |
|---|---|
| JUnit 5 | Permite implementar y ejecutar pruebas automatizadas. |
| Mockito | Permite simular dependencias para probar componentes de manera aislada. |
| Testcontainers | Facilita las pruebas de integración utilizando servicios ejecutados en contenedores. |
| JaCoCo | Permite medir la cobertura del código mediante reportes. |
| SpotBugs | Analiza el código para identificar posibles errores. |
| SonarQube Cloud | Permite realizar análisis de calidad del código. |

Estas herramientas ayudan a detectar errores, validar el comportamiento del servicio y mantener una mejor calidad durante el desarrollo.

## 13.6. Contenedores y despliegue

| Tecnología | Análisis y propósito |
|---|---|
| Docker | Permite empaquetar el microservicio en una imagen para facilitar su ejecución. |
| Docker Compose | Facilita la ejecución local del microservicio junto con PostgreSQL. |
| GitHub Actions | Automatiza los procesos de integración continua y despliegue. |
| Azure Container Registry | Almacena las imágenes Docker generadas por el pipeline. |
| Azure App Service | Proporciona el entorno donde se despliega y ejecuta el microservicio. |

## 13.7. Análisis general

La selección de tecnologías permite construir un microservicio basado en Spring Boot y Java, con persistencia relacional mediante PostgreSQL y comunicación mediante REST y RabbitMQ.

La separación de responsabilidades se apoya en herramientas como Spring Data JPA, MapStruct y Spring Security. Por otro lado, Docker y GitHub Actions facilitan la ejecución y automatización del proceso de despliegue.

En conjunto, estas tecnologías proporcionan una base para desarrollar, probar, mantener y desplegar el microservicio `ms-usuarios`.


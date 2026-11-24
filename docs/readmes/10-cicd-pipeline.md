# 10. CI/CD y Pipeline

El microservicio `ms-usuarios` utiliza GitHub Actions para automatizar la integración continua, la ejecución de pruebas, el análisis de código, la construcción de imágenes Docker y el despliegue en Azure.

El pipeline se encuentra definido en:

`.github/workflows/ci.yml`

## 10.1. Integración continua (CI)

El flujo de integración continua se ejecuta cuando:

- Se realiza un `push` a cualquier rama.
- Se crea o actualiza un Pull Request dirigido a las ramas `develop` o `main`.

El proceso utiliza Ubuntu como entorno de ejecución y Java 21 con Maven.

Las principales actividades son:

1. Descargar el código del repositorio.
2. Configurar Java 21.
3. Ejecutar `mvn clean verify`.
4. Realizar análisis estático con SpotBugs.
5. Generar y guardar el reporte de cobertura JaCoCo.
6. Preparar los archivos necesarios para el análisis de SonarQube Cloud.

Para las pruebas, el pipeline inicia un servicio PostgreSQL 16.

## 10.2. Análisis de calidad

El pipeline utiliza las siguientes herramientas:

| Herramienta | Función |
|---|---|
| JUnit y Mockito | Ejecución de pruebas automatizadas. |
| JaCoCo | Generación del reporte de cobertura. |
| SpotBugs | Análisis estático para detectar posibles errores en el código. |
| SonarQube Cloud | Análisis de calidad y revisión del código. |

El análisis con SonarQube Cloud se ejecuta después de finalizar correctamente el trabajo de construcción y pruebas.

## 10.3. Construcción y publicación de imágenes Docker

Cuando se cumplen las condiciones configuradas para las ramas `develop` y `main`, el pipeline construye una imagen Docker del microservicio y la publica en Azure Container Registry (ACR).

Registro utilizado:

`styleradarusersacr.azurecr.io`

La imagen se publica utilizando el identificador del commit como etiqueta.

También se utilizan etiquetas adicionales:

| Rama o evento | Etiqueta |
|---|---|
| `develop` | `qa` |
| `main` | `prod` |
| Pull Request hacia `develop` | `qa` |

## 10.4. Despliegue en QA

El despliegue en QA se ejecuta cuando se realiza un `push` a `develop` o cuando se actualiza un Pull Request hacia esa rama desde el mismo repositorio.

El pipeline realiza las siguientes actividades:

1. Autenticarse en Azure.
2. Configurar la imagen Docker del microservicio en Azure App Service.
3. Configurar el puerto `8120`.
4. Activar Flyway mediante `FLYWAY_ENABLED=true`.
5. Establecer el perfil de Spring `qa`.
6. Reiniciar la aplicación.

El App Service configurado es:

`styleradar-qa-app`

## 10.5. Despliegue en producción

El despliegue en producción se ejecuta únicamente cuando se realiza un `push` a la rama `main`.

Antes del despliegue, el pipeline verifica que la variable `PROD_DATABASE_VERIFIED` tenga el valor `true`.

También comprueba que el App Service tenga configuradas las variables necesarias para la conexión a PostgreSQL y la seguridad:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `INTERNAL_AUTH_USERNAME`
- `INTERNAL_AUTH_PASSWORD`
- `JWT_SECRET_BASE64` o `JWT_SECRET`

Si alguna validación falla, el despliegue se detiene.

Cuando las validaciones son correctas, se configura la imagen Docker en Azure App Service, se activa Flyway y se reinicia la aplicación.

## 10.6. Secretos y variables de GitHub

El pipeline utiliza secretos y variables configurados en GitHub para autenticarse en Azure y acceder a la configuración

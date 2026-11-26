# 6. Cómo ejecutar local y con Docker

El microservicio `ms-usuarios` puede ejecutarse localmente mediante Maven o utilizando Docker Compose.

## 6.1. Requisitos previos

Antes de ejecutar el servicio se necesita:

| Herramienta | Uso |
|---|---|
| Java 21 | Ejecución de la aplicación. |
| Maven | Compilación y gestión de dependencias. |
| PostgreSQL | Persistencia de información. |
| Docker Desktop | Ejecución mediante contenedores. |
| RabbitMQ | Comunicación asíncrona con otros microservicios. |

## 6.2. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd CADCHE-Usuarios-Service
```

## 6.3. Configuración de variables de entorno

Antes de ejecutar la aplicación, configura las variables requeridas en el entorno o archivo `.env`.

Consulta la [Sección 7: Variables de entorno](07-variables-entorno.md).

No publiques credenciales, contraseñas ni secretos en el repositorio.

## 6.4. Ejecución local con Maven

Primero, compila el proyecto:

```bash
mvn clean install
```

Ejecuta las pruebas:

```bash
mvn test
```

Inicia la aplicación:

```bash
mvn spring-boot:run
```

El servicio utiliza por defecto el puerto `8120`.

```text
http://localhost:8120
```

Swagger UI:

```text
http://localhost:8120/swagger-ui/index.html
```

## 6.5. Ejecución con Docker Compose

El repositorio incluye un `Dockerfile` y un `docker-compose.yml` para construir y ejecutar la aplicación junto con PostgreSQL.

Crea un archivo `.env` en la raíz del proyecto con las variables requeridas.

Construye e inicia los contenedores:

```bash
docker compose up --build -d
```

Consulta los contenedores activos:

```bash
docker compose ps
```

Consulta los registros de la aplicación:

```bash
docker compose logs -f ms-usuarios
```

Detén los servicios:

```bash
docker compose down
```

Para eliminar también los volúmenes de datos:

```bash
docker compose down -v
```

**Advertencia:** el último comando elimina los datos persistidos en el volumen de PostgreSQL.

## 6.6. Servicios y puertos

| Servicio | Puerto del contenedor | Puerto local |
|---|---:|---:|
| `ms-usuarios` | `8120` | `8120` |
| `usuarios-db` | `5432` | `5432` |
| RabbitMQ | `5672` | Según configuración externa |

El `docker-compose.yml` del repositorio define los contenedores de `ms-usuarios` y PostgreSQL. RabbitMQ debe estar disponible y configurado por separado.

## 6.7. Verificación de funcionamiento

Comprueba el estado de la aplicación mediante Actuator:

```bash
curl http://localhost:8120/actuator/health
```

Respuesta esperada cuando la aplicación está disponible:

```json
{
  "status": "UP"
}
```

También puedes abrir Swagger UI:

```text
http://localhost:8120/swagger-ui/index.html
```

---

[← Anterior: 5. Tecnologías](05-tecnologias-dependencias.md) · [Índice principal](../../README.md) · [Siguiente: 7. Variables de entorno](07-variables-entorno.md)

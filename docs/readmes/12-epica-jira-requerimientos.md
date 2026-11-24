# 12. Épica de Jira y requerimientos

El seguimiento de los requerimientos, las historias de usuario, las tareas y las evidencias de entrega del microservicio `ms-usuarios` se realiza mediante Jira, dentro del proyecto CADCHE.

## 12.1. Enlaces de gestión

- **Épica y tablero Scrum de CADCHE:** [Abrir tablero Jira](https://mail-team-nyjtgqcj.atlassian.net/jira/software/projects/CADCHE/boards/75/backlog)

## 12.2. Requerimientos funcionales asignados

Los siguientes requerimientos funcionales están relacionados con las responsabilidades del microservicio:

| Código RF | Nombre del requerimiento | Actor principal | Criterio de aceptación clave |
|---|---|---|---|
| `SR-RF-01` | Registro y consulta de perfil de usuario comprador | `COMPRADOR` | Registra y consulta el perfil validando correo único y aceptación expresa de Habeas Data. |
| `SR-RF-03` | Selección de preferencias de estilo del comprador | `COMPRADOR` | Permite guardar al menos un estilo y rechaza listas vacías. |
| `SR-RF-04` | Configuración de tallas habituales por tipo de prenda | `COMPRADOR` | Permite configurar tallas diferenciadas para ropa (`XS` a `XXL`) y calzado (`35` a `45`). |
| `SR-RF-05` | Visualización de feed personalizado de prendas | `COMPRADOR` | Retorna un feed paginado de prendas sugeridas según las preferencias de estilo y tallas. |
| `SR-RF-06` | Recomendaciones personalizadas de prendas | `COMPRADOR` | Retorna recomendaciones calculadas según el puntaje de afinidad con el perfil y las búsquedas del comprador. |
| `SR-RF-28` | Gestión de búsquedas guardadas | `COMPRADOR` | Permite guardar, listar y eliminar búsquedas con filtros de prendas, precios y coordenadas geográficas. |
| `SR-RF-66` | Administración de estados de cuenta de usuario | `ADMIN_STYLERADAR` | Permite consultar usuarios y cambiar su estado a `ACTIVO`, `INACTIVO` o `BLOQUEADO`, registrando el motivo y la fecha. |

## 12.3. Evidencia del tablero Jira

En este espacio se debe agregar una captura del tablero Scrum donde se visualicen la épica, las historias de usuario y las tareas asignadas al microservicio durante el sprint.

**Ruta sugerida para guardar la evidencia:**

`docs/evidencias/jira-tablero-epica.png`

<!-- Cuando se tenga la captura, se puede agregar con:
![Tablero Jira](../evidencias/jira-tablero-epica.png)
-->

## 12.4. Otros enlaces relacionados

- **Swagger en producción:** [API PROD](https://styleradar-prod-app.azurewebsites.net/swagger-ui/index.html)
- **Swagger en QA:** [API QA](https://styleradar-qa-app.azurewebsites.net/swagger-ui/index.html)
- **Colección de pruebas Postman:** [Repositorio Postman](https://github.com/CatrachoINXS/StyleRadar-Backend/tree/main/postman)
- **Imagen de contenedor:** [Docker Hub](https://hub.docker.com/r/juanamunoz/styleradar)
- **Video de demostración:** [Google Drive](https://drive.google.com/file/d/1W6dklNVi8Bp8LQUx1dkGrkMlly0gRjd7/view?usp=sharing)


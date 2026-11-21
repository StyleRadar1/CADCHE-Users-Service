# 5. Eventos que publica y consume

La comunicación asíncrona entre microservicios se realiza mediante **RabbitMQ 3.12**, utilizando Topic Exchanges durables y mecanismos de mensajería para desacoplar los servicios.

## 5.1. Exchanges utilizados

| Exchange | Tipo | Durabilidad | Propósito |
|---|---|---|---|
| `styleradar.usuarios.events` / `styleradar.events` | `topic` | Durable | Comunicación de eventos relacionados con usuarios. |
| `styleradar.audit` | `fanout` | Durable | Distribución de eventos de auditoría. |
| `styleradar.dlx` | Dead Letter Exchange | Según configuración | Gestión de mensajes que no pudieron procesarse correctamente. |

## 5.2. Eventos publicados por ms-usuarios

| Routing Key | Microservicio receptor | Cuándo se publica |
|---|---|---|
| `usuario.perfil.actualizado` | `ms-busqueda` | Cuando el comprador actualiza sus preferencias de estilo o tallas por tipo de prenda (`SR-RF-03`, `SR-RF-04`), para recalcular el feed y las recomendaciones (`SR-RF-05`, `SR-RF-06`). |
| `usuario.estado.cambiado` | `ms-auth`, `ms-segunda-mano` | Cuando `ADMIN_STYLERADAR` cambia el estado de una cuenta a `ACTIVO`, `INACTIVO` o `BLOQUEADO` (`SR-RF-66`). En `ms-auth`, este evento permite revocar las sesiones del usuario. |

## 5.3. Eventos consumidos por ms-usuarios

| Routing Key | Cola | Microservicio emisor | Acción realizada |
|---|---|---|---|
| `auth.usuario.registrado` | `q.usuarios.registro` | `ms-auth` | Crea o sincroniza el perfil base del usuario después de su registro en el servicio de autenticación (`SR-RF-01`). |
| `tienda.rol.asignado` | `q.usuarios.rol` | `ms-tiendas` | Añade el rol `ADMIN_ALMACEN` o `REPRESENTANTE_FUNDACION` al perfil del usuario (`SR-RF-62`, `SR-RF-63`). |

## 5.4. Flujo de comunicación

### Publicación de eventos

1. `ms-usuarios` detecta un cambio en el perfil o estado de una cuenta.
2. Genera el evento correspondiente.
3. Publica el mensaje en RabbitMQ utilizando el exchange y routing key configurados.
4. RabbitMQ distribuye el mensaje a los microservicios que tienen una cola vinculada a esa routing key.
5. Los servicios receptores procesan el evento según su responsabilidad.

### Consumo de eventos

1. Otro microservicio publica un evento relacionado con usuarios.
2. RabbitMQ lo entrega a la cola correspondiente.
3. `ms-usuarios` recibe el mensaje.
4. El listener procesa el evento y ejecuta la operación correspondiente.

## 5.5. Ejemplo del evento usuario.estado.cambiado

El siguiente es el esquema JSON documentado para notificar un cambio de estado de cuenta:

```json
{
  "eventId": "018e3a2b-3333-7000-8000-000000000002",
  "eventType": "usuario.estado.cambiado",
  "timestamp": "2026-11-01T14:30:00Z",
  "producer": "ms-usuarios",
  "correlationId": "corr-usr-estado-0001",
  "data": {
    "usuarioId": 1,
    "correo": "camila.torres@correo.com",
    "estadoAnterior": "ACTIVO",
    "estadoNuevo": "BLOQUEADO",
    "motivo": "Incumplimiento reiterado de políticas de publicación",
    "version": 2
  }
}
```

## 5.6. Idempotencia y manejo de errores

Los eventos incorporan identificadores como `eventId` y `correlationId` para facilitar su trazabilidad y control de procesamiento.

El Dead Letter Exchange (`styleradar.dlx`) permite gestionar mensajes que no pueden procesarse correctamente, de acuerdo con la configuración de RabbitMQ.

---

[← Anterior: 4. Endpoints y Swagger](04-endpoints-swagger.md) · [Índice principal](../../README.md) · [Siguiente: 6. Tecnologías y dependencias](06-tecnologias-dependencias.md)

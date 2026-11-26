# 4. Eventos RabbitMQ

`ms-usuarios` publica eventos en el exchange `styleradar.exchange`. Los nombres de colas y su vinculación corresponden a los servicios consumidores y a la configuración del broker.

## 4.1. Eventos publicados

| Routing key | Cuándo se publica |
|---|---|
| `styleradar.usuarios.usuario.registrado` | Al registrar una cuenta nueva; el mensaje contiene `eventId`, `eventType`, `occurredAt` y datos no sensibles del evento. |
| `styleradar.usuarios.busqueda.sincronizada` | Al guardar o eliminar una búsqueda guardada. |
| `user.status.changed` | Al cambiar el estado de una cuenta; incluye `eventId`, `userId`, `previousStatus`, `newStatus`, `changedBy`, `reason` y `changedAt`. |
| `user.password.changed` | Tras cambiar o restablecer contraseña; incluye `eventId`, `eventType`, `userId` y `changedAt`. |
| `styleradar.usuarios.cuenta.eliminada` | Al eliminar una cuenta. |

Los eventos de estado y contraseña permiten que ms-auth invalide sesiones de acuerdo con su contrato. La emisión de eventos de sesión de usuario y de estado de fundaciones no está implementada por este servicio.

La publicación del evento de registro usa publisher confirms. Si RabbitMQ devuelve confirmación positiva y no reporta un mensaje sin ruta, el servicio escribe un log `Evento de registro confirmado por RabbitMQ y enrutado` con `eventId`, exchange y routing key, sin incluir datos personales. Ese log confirma aceptación y enrutamiento por el broker; no confirma que ms-estadisticas ya haya procesado el evento.

## 4.2. Consumo de eventos

En la revisión del código actual no se encontraron listeners `@RabbitListener` ni consumidores RabbitMQ. Por tanto, esta sección no anuncia eventos consumidos.

## 4.3. Ejemplo de cambio de estado

```json
{
  "eventId": "018e3a2b-3333-7000-8000-000000000002",
  "userId": 123,
  "previousStatus": "ACTIVE",
  "newStatus": "BLOCKED",
  "changedBy": 1,
  "reason": "Incumplimiento de políticas",
  "changedAt": "2026-11-25T14:30:00Z"
}
```

Los nombres y valores finales serializados provienen de los mensajes de integración del código; consulta el contrato acordado con el equipo consumidor antes de crear bindings o asumir nombres de colas.

---

[← Anterior: 3. API](03-endpoints-swagger.md) · [Índice](../../README.md) · [Siguiente: 5. Tecnologías](05-tecnologias-dependencias.md)

package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import java.time.Instant;

import edu.dosw.styleradar.usuarios.application.dto.serialization.ApiEnumSerializer;
import edu.dosw.styleradar.usuarios.domain.event.UsuarioEstadoCambiadoEvent;

record UsuarioEstadoCambiadoMessage(
        String eventId,
        Long userId,
        String previousStatus,
        String newStatus,
        Long changedBy,
        String reason,
        Instant changedAt) {

    static UsuarioEstadoCambiadoMessage desde(UsuarioEstadoCambiadoEvent evento) {
        return new UsuarioEstadoCambiadoMessage(
                evento.eventoId(),
                evento.usuarioId(),
                convertirEstado(evento.estadoAnterior()),
                convertirEstado(evento.nuevoEstado()),
                evento.administradorId(),
                evento.motivo(),
                evento.fechaCambio());
    }

    private static String convertirEstado(Object estado) {
        return estado == null ? null : ApiEnumSerializer.valueOf(estado);
    }
}

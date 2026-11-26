package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import java.time.Instant;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioContrasenaCambiadaEvent;

record UsuarioContrasenaCambiadaMessage(
        String eventId,
        String eventType,
        Long userId,
        Instant changedAt) {

    static UsuarioContrasenaCambiadaMessage desde(UsuarioContrasenaCambiadaEvent evento) {
        return new UsuarioContrasenaCambiadaMessage(
                evento.eventId(),
                "user.password.changed",
                evento.userId(),
                evento.changedAt());
    }
}

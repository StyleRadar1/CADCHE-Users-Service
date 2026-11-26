package edu.dosw.styleradar.usuarios.domain.event;

import java.time.Instant;
import java.util.UUID;

public record UsuarioContrasenaCambiadaEvent(
        String eventId,
        Long userId,
        Instant changedAt) {

    public static UsuarioContrasenaCambiadaEvent ahora(Long userId) {
        return new UsuarioContrasenaCambiadaEvent(
                UUID.randomUUID().toString(), userId, Instant.now());
    }
}

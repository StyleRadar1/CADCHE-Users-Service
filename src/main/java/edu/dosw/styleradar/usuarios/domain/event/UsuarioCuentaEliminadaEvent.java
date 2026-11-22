package edu.dosw.styleradar.usuarios.domain.event;

import java.time.Instant;

public record UsuarioCuentaEliminadaEvent(
        Long userId,
        Instant deletedAt
) {
}


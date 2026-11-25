package edu.dosw.styleradar.usuarios.domain.event;

import java.time.Instant;
import java.util.UUID;

import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;

public record UsuarioRegistradoEvent(
        String eventId,
        Instant occurredAt,
        Rol rol) {

    public static UsuarioRegistradoEvent desde(Usuario usuario) {
        return new UsuarioRegistradoEvent(
                UUID.randomUUID().toString(),
                usuario.getFechaRegistro(),
                usuario.getRol());
    }
}

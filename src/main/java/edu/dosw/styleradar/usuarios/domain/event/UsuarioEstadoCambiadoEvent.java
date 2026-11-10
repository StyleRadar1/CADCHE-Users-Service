package edu.dosw.styleradar.usuarios.domain.event;

import java.time.Instant;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;

public record UsuarioEstadoCambiadoEvent(
        Long usuarioId,
        EstadoCuenta estadoAnterior,
        EstadoCuenta estadoNuevo,
        Long administradorId,
        String motivo,
        Instant fechaCambio
) {
}

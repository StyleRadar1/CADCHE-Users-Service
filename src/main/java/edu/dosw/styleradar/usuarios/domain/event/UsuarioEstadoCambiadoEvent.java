package edu.dosw.styleradar.usuarios.domain.event;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;

public record UsuarioEstadoCambiadoEvent(
        String eventoId,
        Long usuarioId,
        String email,
        EstadoCuenta estadoAnterior,
        EstadoCuenta nuevoEstado,
        Long administradorId,
        String motivo,
        Instant fechaCambio
) {

    @JsonIgnore
    public EstadoCuenta estadoNuevo() {
        return nuevoEstado;
    }
}

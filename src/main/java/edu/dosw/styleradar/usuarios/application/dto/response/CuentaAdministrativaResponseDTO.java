package edu.dosw.styleradar.usuarios.application.dto.response;

import java.time.Instant;
import java.util.Set;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado operativo y auditoría administrativa de una cuenta de usuario (SR-RF-66)")
public record CuentaAdministrativaResponseDTO(
        @Schema(description = "Identificador único de la cuenta", example = "12")
        Long id,

        @Schema(description = "Nombre del titular de la cuenta", example = "Laura Gómez")
        String nombre,

        @Schema(description = "Correo electrónico registrado", example = "laura.gomez@styleradar.com")
        String email,

        @Schema(description = "Roles asignados a la cuenta", example = "[\"COMPRADOR\"]")
        Set<Rol> roles,

        @Schema(description = "Estado operativo efectivo de la cuenta", example = "ACTIVA")
        EstadoCuenta estadoCuenta,

        @Schema(description = "Fecha y hora UTC de registro original", example = "2026-10-15T10:00:00Z")
        Instant fechaRegistro,

        @Schema(description = "Fecha y hora UTC de la última decisión administrativa sobre el estado", example = "2026-11-02T18:30:00Z")
        Instant fechaCambioEstado,

        @Schema(description = "Identificador del administrador que ejecutó el último cambio de estado", example = "1")
        Long administradorCambioEstadoId,

        @Schema(description = "Motivo registrado en el último cambio de estado", example = "Revisión concluida")
        String motivoCambioEstado
) {
}


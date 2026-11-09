package edu.dosw.styleradar.usuarios.application.dto.response;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;

public record VerificarCredencialesResponseDTO(
        Long id,
        String email,
        Rol rol,
        EstadoCuenta estado
) {
}
package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;

public interface ObtenerUsuarioUseCase {

    UsuarioResponseDTO obtener(Long usuarioId);
}

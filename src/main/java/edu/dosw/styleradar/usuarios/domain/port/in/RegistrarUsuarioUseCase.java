package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;

public interface RegistrarUsuarioUseCase {

    UsuarioResponseDTO registrar(RegistrarUsuarioRequestDTO request);
}

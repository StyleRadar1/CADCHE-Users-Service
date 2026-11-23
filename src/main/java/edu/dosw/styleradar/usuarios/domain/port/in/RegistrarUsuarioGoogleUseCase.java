package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioGoogleRequestDTO;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;

public interface RegistrarUsuarioGoogleUseCase {

    Usuario ejecutar(RegistrarUsuarioGoogleRequestDTO request);
}

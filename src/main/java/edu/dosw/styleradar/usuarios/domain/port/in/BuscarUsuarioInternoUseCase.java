package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.domain.model.Usuario;

public interface BuscarUsuarioInternoUseCase {

    Usuario ejecutar(String email);
}

package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;

public interface CambiarRolUsuarioUseCase {

    Usuario ejecutar(Long usuarioId, Rol nuevoRol);
}

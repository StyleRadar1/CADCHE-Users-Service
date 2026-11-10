package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;

public interface CambiarEstadoCuentaUseCase {

    Usuario ejecutar(
            Long usuarioId,
            EstadoCuenta nuevoEstado,
            Long administradorId,
            String motivo);
}

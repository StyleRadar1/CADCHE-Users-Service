package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;

public interface ConsultarCuentasAdministrativasUseCase {

    PageResponseDTO<CuentaAdministrativaResponseDTO> ejecutar(
            EstadoCuenta estado, Rol rol, String q, int page, int size);
}

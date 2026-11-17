package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;

public interface ConsultarCuentaAdministrativaUseCase {

    CuentaAdministrativaResponseDTO ejecutar(Long id);
}

package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.application.dto.response.MedidasCorporalesResponseDTO;

public interface ObtenerMedidasCorporalesUseCase {

    MedidasCorporalesResponseDTO obtener(Long usuarioId);
}

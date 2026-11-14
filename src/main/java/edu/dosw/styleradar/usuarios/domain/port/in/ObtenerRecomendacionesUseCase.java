package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.application.dto.response.RecomendacionesResponseDTO;

public interface ObtenerRecomendacionesUseCase {

    RecomendacionesResponseDTO obtener(
            Long usuarioId,
            int page,
            int size
    );
}

package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;

public interface ObtenerFeedPersonalizadoUseCase {

    PageResponseDTO<CatalogoItemResponseDTO> obtener(
            Long usuarioId,
            int page,
            int size
    );
}


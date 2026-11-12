package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import java.util.List;

public interface CatalogoClientPort {

    List<CatalogoItemResponseDTO> obtenerPrendasDisponibles();
}

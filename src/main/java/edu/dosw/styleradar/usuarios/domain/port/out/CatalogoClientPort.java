package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CatalogoClientPort {

    List<CatalogoItemResponseDTO> obtenerPrendasDisponibles(
            Set<Estilo> estilos,
            Map<TipoPrenda, Set<String>> tallasPorTipo);
}

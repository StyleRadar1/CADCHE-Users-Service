package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import java.util.Map;
import java.util.Set;

public interface TallaRepositoryPort {

    Map<TipoPrenda, Set<String>> reemplazar(
            Long usuarioId,
            Map<TipoPrenda, Set<String>> tallas
    );

    Map<TipoPrenda, Set<String>> buscarPorUsuarioId(Long usuarioId);
}

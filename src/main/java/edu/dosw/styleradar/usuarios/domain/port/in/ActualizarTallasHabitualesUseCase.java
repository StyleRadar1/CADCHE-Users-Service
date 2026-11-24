package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;

import java.util.Map;
import java.util.Set;

public interface ActualizarTallasHabitualesUseCase {

    Map<TipoPrenda, Set<String>> actualizar(
            Long usuarioId,
            Map<TipoPrenda, Set<String>> tallas
    );
}
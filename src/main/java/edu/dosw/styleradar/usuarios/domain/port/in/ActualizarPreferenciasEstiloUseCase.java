package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.domain.model.Estilo;

import java.util.Set;

public interface ActualizarPreferenciasEstiloUseCase {

    Set<Estilo> actualizar(Long usuarioId, Set<Estilo> preferencias);
}

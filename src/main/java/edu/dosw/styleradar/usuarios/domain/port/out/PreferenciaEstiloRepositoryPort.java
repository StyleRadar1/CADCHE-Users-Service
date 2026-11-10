package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.model.Estilo;

import java.util.Set;

public interface PreferenciaEstiloRepositoryPort {

    Set<Estilo> reemplazar(Long usuarioId, Set<Estilo> preferencias);
}
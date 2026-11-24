package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarPreferenciasEstiloUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.PreferenciaEstiloRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ActualizarPreferenciasEstiloUseCaseImpl
        implements ActualizarPreferenciasEstiloUseCase {

    private final PreferenciaEstiloRepositoryPort repository;

    public ActualizarPreferenciasEstiloUseCaseImpl(
            PreferenciaEstiloRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Set<Estilo> actualizar(Long usuarioId, Set<Estilo> preferencias) {
        return repository.reemplazar(usuarioId, preferencias);
    }
}
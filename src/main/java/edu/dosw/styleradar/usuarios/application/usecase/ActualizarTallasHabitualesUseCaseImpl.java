package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarTallasHabitualesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.TallaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class ActualizarTallasHabitualesUseCaseImpl
        implements ActualizarTallasHabitualesUseCase {

    private final TallaRepositoryPort repository;

    public ActualizarTallasHabitualesUseCaseImpl(TallaRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Map<TipoPrenda, Set<String>> actualizar(
            Long usuarioId,
            Map<TipoPrenda, Set<String>> tallas) {

        return repository.reemplazar(usuarioId, tallas);
    }
}
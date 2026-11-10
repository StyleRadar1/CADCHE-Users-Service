package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.port.out.PreferenciaEstiloRepositoryPort;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.PreferenciaEstiloEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.PreferenciaEstiloJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PreferenciaEstiloPersistenceAdapter
        implements PreferenciaEstiloRepositoryPort {

    private final PreferenciaEstiloJpaRepository preferenciaRepository;
    private final UsuarioJpaRepository usuarioRepository;

    public PreferenciaEstiloPersistenceAdapter(
            PreferenciaEstiloJpaRepository preferenciaRepository,
            UsuarioJpaRepository usuarioRepository) {
        this.preferenciaRepository = preferenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Set<Estilo> reemplazar(Long usuarioId, Set<Estilo> preferencias) {

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        preferenciaRepository.deleteByUsuario_Id(usuarioId);
        preferenciaRepository.flush();

        Set<PreferenciaEstiloEntity> nuevas = preferencias.stream()
                .map(estilo -> new PreferenciaEstiloEntity(usuario, estilo))
                .collect(Collectors.toSet());

        preferenciaRepository.saveAllAndFlush(nuevas);

        return preferencias;
    }
}

package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.port.out.TallaRepositoryPort;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.TallaEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.TallaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TallaPersistenceAdapter implements TallaRepositoryPort {

    private final TallaJpaRepository tallaRepository;
    private final UsuarioJpaRepository usuarioRepository;

    public TallaPersistenceAdapter(
            TallaJpaRepository tallaRepository,
            UsuarioJpaRepository usuarioRepository) {
        this.tallaRepository = tallaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Map<TipoPrenda, Set<String>> reemplazar(
            Long usuarioId,
            Map<TipoPrenda, Set<String>> tallas) {

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        tallaRepository.deleteByUsuario_Id(usuarioId);
        tallaRepository.flush();

        Set<TallaEntity> nuevas = tallas.entrySet().stream()
                .flatMap(entrada -> entrada.getValue().stream()
                        .map(talla -> new TallaEntity(
                                usuario,
                                entrada.getKey(),
                                talla
                        )))
                .collect(Collectors.toSet());

        tallaRepository.saveAllAndFlush(nuevas);

        return tallas;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<TipoPrenda, Set<String>> buscarPorUsuarioId(Long usuarioId) {
        return tallaRepository.findByUsuario_Id(usuarioId)
                .stream()
                .collect(Collectors.groupingBy(
                        TallaEntity::getTipoPrenda,
                        Collectors.mapping(
                                TallaEntity::getTalla,
                                Collectors.toSet()
                        )
                ));
    }
}

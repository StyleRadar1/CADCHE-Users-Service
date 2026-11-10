package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public boolean existePorEmail(String email) {
        return usuarioJpaRepository.existsByEmail(email);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = convertirAEntity(usuario);

        try {
            UsuarioEntity guardado = usuarioJpaRepository.saveAndFlush(entity);
            return convertirADominio(guardado);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailYaRegistradoException(usuario.getEmail());
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioJpaRepository.findById(id)
                .map(this::convertirADominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioJpaRepository.findByEmail(email)
                .map(this::convertirADominio);
    }

    @Override
    public long contarAdministradoresActivos() {
        return usuarioJpaRepository.countByRolAndEstado(
                Rol.ADMIN_STYLERADAR,
                EstadoCuenta.ACTIVA);
    }

    @Override
    @Transactional
    public Optional<Usuario> cambiarEstado(
            Long usuarioId,
            EstadoCuenta nuevoEstado,
            Instant fechaCambio,
            Long administradorId,
            String motivo) {

        Optional<UsuarioEntity> resultado = usuarioJpaRepository.findById(usuarioId);

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        UsuarioEntity entity = resultado.get();
        entity.setEstado(nuevoEstado);
        entity.setFechaCambioEstado(fechaCambio);
        entity.setAdministradorCambioEstadoId(administradorId);
        entity.setMotivoCambioEstado(motivo);

        UsuarioEntity actualizado = usuarioJpaRepository.saveAndFlush(entity);

        return Optional.of(convertirADominio(actualizado));
    }

    private UsuarioEntity convertirAEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setNombre(usuario.getNombre());
        entity.setEmail(usuario.getEmail());
        entity.setTelefono(usuario.getTelefono());
        entity.setPasswordHash(usuario.getPasswordHash());
        entity.setAceptaHabeasData(usuario.getAceptaHabeasData());
        entity.setRol(usuario.getRol());
        entity.setEstado(usuario.getEstado());
        entity.setFechaRegistro(usuario.getFechaRegistro());
        return entity;
    }

    private Usuario convertirADominio(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getTelefono(),
                entity.getPasswordHash(),
                entity.getAceptaHabeasData(),
                entity.getRol(),
                entity.getEstado(),
                entity.getFechaRegistro()
        );
    }
}

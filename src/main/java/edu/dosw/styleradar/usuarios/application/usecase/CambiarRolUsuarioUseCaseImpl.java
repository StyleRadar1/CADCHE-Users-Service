package edu.dosw.styleradar.usuarios.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.dosw.styleradar.usuarios.domain.exception.CambioEstadoNoPermitidoException;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarRolUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

@Service
public class CambiarRolUsuarioUseCaseImpl implements CambiarRolUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public CambiarRolUsuarioUseCaseImpl(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Usuario ejecutar(Long usuarioId, Rol nuevoRol) {
        if (nuevoRol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        if (usuario.getRol() != nuevoRol
                && usuario.getRol() == Rol.ADMIN_STYLERADAR
                && usuario.getEstado() == EstadoCuenta.ACTIVA
                && usuarioRepository.contarAdministradoresActivos() <= 1) {
            throw new CambioEstadoNoPermitidoException(
                    "No se puede quitar el rol al último administrador activo",
                    "LAST_ACTIVE_ADMIN");
        }

        return usuarioRepository.actualizarRol(usuarioId, nuevoRol)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
    }
}

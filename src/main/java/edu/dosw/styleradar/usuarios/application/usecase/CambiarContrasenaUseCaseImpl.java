package edu.dosw.styleradar.usuarios.application.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.dosw.styleradar.usuarios.domain.exception.ContrasenaActualIncorrectaException;
import edu.dosw.styleradar.usuarios.domain.exception.CuentaNoActivaException;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.event.UsuarioContrasenaCambiadaEvent;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarContrasenaUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioContrasenaCambiadaEventPublisherPort;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

@Service
public class CambiarContrasenaUseCaseImpl implements CambiarContrasenaUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioContrasenaCambiadaEventPublisherPort eventPublisher;

    public CambiarContrasenaUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioContrasenaCambiadaEventPublisherPort eventPublisher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void ejecutar(Long usuarioId, String contrasenaActual, String contrasenaNueva) {
        var usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
        String passwordHash = usuario.getPasswordHash();

        if (passwordHash == null || !passwordEncoder.matches(contrasenaActual, passwordHash)) {
            throw new ContrasenaActualIncorrectaException();
        }

        actualizarPassword(usuarioId, contrasenaNueva);
    }

    @Override
    @Transactional
    public void restablecerInternamente(Long usuarioId, String contrasenaNueva) {
        var usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        if (usuario.getEstado() != EstadoCuenta.ACTIVA) {
            throw new CuentaNoActivaException("La cuenta no está activa");
        }

        actualizarPassword(usuarioId, contrasenaNueva);
    }

    private void actualizarPassword(Long usuarioId, String contrasenaNueva) {
        if (!usuarioRepository.actualizarPasswordHash(
                usuarioId, passwordEncoder.encode(contrasenaNueva))) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }
        eventPublisher.publicar(UsuarioContrasenaCambiadaEvent.ahora(usuarioId));
    }
}

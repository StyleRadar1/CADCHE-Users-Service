package edu.dosw.styleradar.usuarios.application.usecase;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioEstadoCambiadoEvent;
import edu.dosw.styleradar.usuarios.domain.exception.CambioEstadoNoPermitidoException;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarEstadoCuentaUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioEstadoEventPublisherPort;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

@Service
public class CambiarEstadoCuentaUseCaseImpl implements CambiarEstadoCuentaUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioEstadoEventPublisherPort eventoPublisher;

    public CambiarEstadoCuentaUseCaseImpl(
            UsuarioRepositoryPort usuarioRepositoryPort,
            UsuarioEstadoEventPublisherPort eventoPublisher) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.eventoPublisher = eventoPublisher;
    }

    @Override
    @Transactional
    public Usuario ejecutar(
            Long usuarioId,
            EstadoCuenta nuevoEstado,
            Long administradorId,
            String motivo) {

        Usuario usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        if (usuarioId.equals(administradorId)) {
            throw new CambioEstadoNoPermitidoException(
                    "Un administrador no puede cambiar el estado de su propia cuenta");
        }

        if (usuario.getEstado() == nuevoEstado) {
            throw new CambioEstadoNoPermitidoException(
                    "La cuenta ya tiene el estado solicitado");
        }

        if (usuario.getRol() == Rol.ADMIN_STYLERADAR
                && usuario.getEstado() == EstadoCuenta.ACTIVA
                && nuevoEstado != EstadoCuenta.ACTIVA
                && usuarioRepositoryPort.contarAdministradoresActivos() <= 1) {
            throw new CambioEstadoNoPermitidoException(
                    "No se puede cambiar el estado del último administrador activo");
        }

        EstadoCuenta estadoAnterior = usuario.getEstado();
        Instant fechaCambio = Instant.now();

        Usuario usuarioActualizado = usuarioRepositoryPort.cambiarEstado(
                        usuarioId,
                        nuevoEstado,
                        fechaCambio,
                        administradorId,
                        motivo)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        UsuarioEstadoCambiadoEvent evento = new UsuarioEstadoCambiadoEvent(
                usuarioId,
                estadoAnterior,
                nuevoEstado,
                administradorId,
                motivo,
                fechaCambio);

        eventoPublisher.publicar(evento);

        return usuarioActualizado;
    }
}

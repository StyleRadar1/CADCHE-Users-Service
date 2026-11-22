package edu.dosw.styleradar.usuarios.application.usecase;

import java.time.Instant;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioCuentaEliminadaEvent;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.port.in.EliminarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioCuentaEliminadaEventPublisherPort;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class EliminarUsuarioUseCaseImpl implements EliminarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioCuentaEliminadaEventPublisherPort eventoPublisher;

    public EliminarUsuarioUseCaseImpl(
            UsuarioRepositoryPort usuarioRepositoryPort,
            UsuarioCuentaEliminadaEventPublisherPort eventoPublisher) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.eventoPublisher = eventoPublisher;
    }

    @Override
    @Transactional
    public void ejecutar(Long usuarioId) {

        if (!usuarioRepositoryPort.eliminarPorId(usuarioId)) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }

        UsuarioCuentaEliminadaEvent evento =
                new UsuarioCuentaEliminadaEvent(usuarioId, Instant.now());

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        eventoPublisher.publicar(evento);
                    }
                });
    }
}

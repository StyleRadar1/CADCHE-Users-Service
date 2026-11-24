package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.port.in.MarcarCorreoVerificadoUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarcarCorreoVerificadoUseCaseImpl
        implements MarcarCorreoVerificadoUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public MarcarCorreoVerificadoUseCaseImpl(
            UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    @Transactional
    public void ejecutar(Long usuarioId) {

        usuarioRepositoryPort.marcarCorreoComoVerificado(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
    }
}

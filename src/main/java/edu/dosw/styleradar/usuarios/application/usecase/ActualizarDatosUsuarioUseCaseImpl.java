package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarDatosUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarDatosUsuarioUseCaseImpl implements ActualizarDatosUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public ActualizarDatosUsuarioUseCaseImpl(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void actualizar(Long usuarioId, String nombre, String telefono, String barrio) {
        usuarioRepository.actualizarDatosPersonales(usuarioId, nombre, telefono, barrio)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
    }
}

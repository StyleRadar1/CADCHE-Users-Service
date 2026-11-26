package edu.dosw.styleradar.usuarios.application.usecase;

import org.springframework.stereotype.Service;

import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoPorEmailException;
import edu.dosw.styleradar.usuarios.domain.model.Email;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.BuscarUsuarioInternoUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

@Service
public class BuscarUsuarioInternoUseCaseImpl implements BuscarUsuarioInternoUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public BuscarUsuarioInternoUseCaseImpl(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario ejecutar(String email) {
        String emailNormalizado = new Email(email).valor();
        return usuarioRepository.buscarPorEmail(emailNormalizado)
                .orElseThrow(UsuarioNoEncontradoPorEmailException::new);
    }
}

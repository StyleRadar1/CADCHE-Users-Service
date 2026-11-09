package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.VerificarCredencialesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificarCredencialesUseCaseImpl implements VerificarCredencialesUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public VerificarCredencialesUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Usuario> verificar(String email, String password) {

        Optional<Usuario> resultado = usuarioRepository.buscarPorEmail(email);

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = resultado.get();

        if (usuario.getEstado() != EstadoCuenta.ACTIVA) {
            return Optional.empty();
        }

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(usuario);
    }
}
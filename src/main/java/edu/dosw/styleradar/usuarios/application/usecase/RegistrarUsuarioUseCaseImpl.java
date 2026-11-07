package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrarUsuarioUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDTO registrar(RegistrarUsuarioRequestDTO request) {

        String email = request.getEmail().trim().toLowerCase();

        if (usuarioRepository.existePorEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        Usuario usuario = new Usuario(
                null,
                request.getNombre(),
                email,
                request.getTelefono(),
                passwordEncoder.encode(request.getPassword()),
                request.isAceptaHabeasData(),
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now()
        );

        Usuario guardado = usuarioRepository.guardar(usuario);

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(guardado.getId());
        response.setNombre(guardado.getNombre());
        response.setEmail(guardado.getEmail());
        response.setTelefono(guardado.getTelefono());
        response.setFechaRegistro(guardado.getFechaRegistro());
        response.setPreferenciasEstilo(new HashSet<>());
        response.setTallasHabituales(new HashSet<>());
        response.setTallasPorTipoPrenda(new HashMap<>());

        return response;
    }
}

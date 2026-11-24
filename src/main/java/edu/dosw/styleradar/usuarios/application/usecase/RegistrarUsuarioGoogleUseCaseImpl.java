package edu.dosw.styleradar.usuarios.application.usecase;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioGoogleRequestDTO;
import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.exception.IdentidadExternaConflictivaException;
import edu.dosw.styleradar.usuarios.domain.model.Email;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioGoogleUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

@Service
public class RegistrarUsuarioGoogleUseCaseImpl implements RegistrarUsuarioGoogleUseCase {

    private static final String GOOGLE = "GOOGLE";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrarUsuarioGoogleUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario ejecutar(RegistrarUsuarioGoogleRequestDTO request) {
        validar(request);

        String email = new Email(request.email()).valor();
        String nombre = request.name().trim();
        String providerId = request.providerId().trim();

        Optional<Usuario> existente = usuarioRepository.buscarPorEmail(email);
        if (existente.isPresent()) {
            return resolverUsuarioExistente(existente.get(), providerId);
        }

        Optional<Usuario> identidadExistente = usuarioRepository.buscarPorProveedorYId(GOOGLE, providerId);
        if (identidadExistente.isPresent()) {
            throw conflictoIdentidad();
        }

        Usuario nuevo = new Usuario(
                null,
                nombre,
                email,
                null,
                passwordEncoder.encode(generarPasswordNoUtilizable()),
                false,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now(),
                null,
                null,
                null,
                true,
                null,
                null,
                null,
                null,
                null,
                GOOGLE,
                providerId
        );

        try {
            return usuarioRepository.guardar(nuevo);
        } catch (EmailYaRegistradoException exception) {
            return resolverCreacionConcurrente(email, providerId, exception);
        }
    }

    private Usuario resolverUsuarioExistente(Usuario usuario, String providerId) {
        if (GOOGLE.equals(usuario.getProvider()) && providerId.equals(usuario.getProviderId())) {
            return usuario;
        }
        if (usuario.getProvider() != null || usuario.getProviderId() != null) {
            throw conflictoIdentidad();
        }

        Usuario vinculado = usuarioRepository.vincularIdentidadGoogle(usuario.getId(), providerId)
                .orElseThrow(() -> conflictoIdentidad());
        if (!GOOGLE.equals(vinculado.getProvider()) || !providerId.equals(vinculado.getProviderId())) {
            throw conflictoIdentidad();
        }
        return vinculado;
    }

    private Usuario resolverCreacionConcurrente(
            String email, String providerId, EmailYaRegistradoException exception) {
        Optional<Usuario> porEmail = usuarioRepository.buscarPorEmail(email);
        if (porEmail.isPresent()) {
            return resolverUsuarioExistente(porEmail.get(), providerId);
        }

        if (usuarioRepository.buscarPorProveedorYId(GOOGLE, providerId).isPresent()) {
            throw conflictoIdentidad();
        }
        throw exception;
    }

    private void validar(RegistrarUsuarioGoogleRequestDTO request) {
        if (request == null || request.email() == null || request.name() == null
                || request.provider() == null || request.providerId() == null
                || request.email().isBlank() || request.name().isBlank()
                || request.providerId().isBlank()) {
            throw new IllegalArgumentException("La solicitud de identidad Google está incompleta");
        }
        if (!GOOGLE.equals(request.provider())) {
            throw new IllegalArgumentException("El proveedor permitido es GOOGLE");
        }
        if (request.name().trim().length() > 100 || request.providerId().trim().length() > 255) {
            throw new IllegalArgumentException("La solicitud de identidad Google excede los límites permitidos");
        }
    }

    private String generarPasswordNoUtilizable() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private IdentidadExternaConflictivaException conflictoIdentidad() {
        return new IdentidadExternaConflictivaException(
                "La identidad de Google está vinculada a otra cuenta");
    }
}

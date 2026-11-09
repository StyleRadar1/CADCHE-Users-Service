package edu.dosw.styleradar.usuarios.application;

import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.application.usecase.RegistrarUsuarioUseCaseImpl;
import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrarUsuarioUseCaseTest {

    private UsuarioRepositoryPort usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private RegistrarUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepositoryPort.class);
        passwordEncoder = new BCryptPasswordEncoder();
        useCase = new RegistrarUsuarioUseCaseImpl(
                usuarioRepository,
                passwordEncoder
        );
    }

    @Test
    void debeRegistrarUsuarioCorrectamente() {

        RegistrarUsuarioRequestDTO request = crearRequest();

        when(usuarioRepository.existePorEmail("laura@gmail.com"))
                .thenReturn(false);

        when(usuarioRepository.guardar(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);

                    return new Usuario(
                            1L,
                            usuario.getNombre(),
                            usuario.getEmail(),
                            usuario.getTelefono(),
                            usuario.getPasswordHash(),
                            usuario.getAceptaHabeasData(),
                            usuario.getRol(),
                            usuario.getEstado(),
                            usuario.getFechaRegistro()
                    );
                });

        UsuarioResponseDTO response = useCase.registrar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laura Gómez", response.getNombre());
        assertEquals("laura@gmail.com", response.getEmail());

        verify(usuarioRepository).guardar(argThat(usuario -> {
            assertTrue(passwordEncoder.matches(
                    "StyleRadar2026*",
                    usuario.getPasswordHash()
            ));

            assertEquals(Boolean.TRUE, usuario.getAceptaHabeasData());
            assertEquals(Rol.COMPRADOR, usuario.getRol());
            assertEquals(EstadoCuenta.ACTIVA, usuario.getEstado());

            return true;
        }));
    }

    @Test
    void noDebeRegistrarUnCorreoExistente() {

        RegistrarUsuarioRequestDTO request = crearRequest();

        when(usuarioRepository.existePorEmail("laura@gmail.com"))
                .thenReturn(true);

        assertThrows(
                EmailYaRegistradoException.class,
                () -> useCase.registrar(request)
        );

        verify(usuarioRepository, never()).guardar(any(Usuario.class));
    }

    private RegistrarUsuarioRequestDTO crearRequest() {

        RegistrarUsuarioRequestDTO request =
                new RegistrarUsuarioRequestDTO();

        request.setNombre("Laura Gómez");
        request.setEmail(" Laura@GMAIL.COM ");
        request.setTelefono("+57 300 123 4567");
        request.setPassword("StyleRadar2026*");
        request.setAceptaHabeasData(true);

        return request;
    }
}

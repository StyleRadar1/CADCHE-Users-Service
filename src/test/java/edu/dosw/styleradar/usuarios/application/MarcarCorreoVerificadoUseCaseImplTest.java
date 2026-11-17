package edu.dosw.styleradar.usuarios.application;

import edu.dosw.styleradar.usuarios.application.usecase.MarcarCorreoVerificadoUseCaseImpl;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarcarCorreoVerificadoUseCaseImplTest {

    private UsuarioRepositoryPort usuarioRepositoryPort;
    private MarcarCorreoVerificadoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        usuarioRepositoryPort = mock(UsuarioRepositoryPort.class);
        useCase = new MarcarCorreoVerificadoUseCaseImpl(usuarioRepositoryPort);
    }

    @Test
    void debeMarcarCorreoComoVerificado() {

        Usuario usuario = new Usuario(
                144L,
                "Usuario de prueba",
                "usuario@gmail.com",
                "3001234567",
                "hash",
                true,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now(),
                null,
                null,
                null,
                true
        );

        when(usuarioRepositoryPort.marcarCorreoComoVerificado(144L))
                .thenReturn(Optional.of(usuario));

        assertDoesNotThrow(() -> useCase.ejecutar(144L));

        verify(usuarioRepositoryPort).marcarCorreoComoVerificado(144L);
    }

    @Test
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {

        when(usuarioRepositoryPort.marcarCorreoComoVerificado(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> useCase.ejecutar(999L)
        );

        verify(usuarioRepositoryPort).marcarCorreoComoVerificado(999L);
    }
}

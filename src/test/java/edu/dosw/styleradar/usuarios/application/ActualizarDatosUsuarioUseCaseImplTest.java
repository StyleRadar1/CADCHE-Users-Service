package edu.dosw.styleradar.usuarios.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.dosw.styleradar.usuarios.application.usecase.ActualizarDatosUsuarioUseCaseImpl;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

class ActualizarDatosUsuarioUseCaseImplTest {

    private UsuarioRepositoryPort usuarioRepository;
    private ActualizarDatosUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepositoryPort.class);
        useCase = new ActualizarDatosUsuarioUseCaseImpl(usuarioRepository);
    }

    @Test
    void actualizaDatosPersonalesCuandoExisteElUsuario() {
        when(usuarioRepository.actualizarDatosPersonales(
                12L, "Laura Gómez", "+573001234567", "Chapinero"))
                .thenReturn(Optional.of(mock(Usuario.class)));

        assertDoesNotThrow(() -> useCase.actualizar(
                12L, "Laura Gómez", "+573001234567", "Chapinero"));

        verify(usuarioRepository).actualizarDatosPersonales(
                12L, "Laura Gómez", "+573001234567", "Chapinero");
    }

    @Test
    void informaCuandoElUsuarioNoExiste() {
        when(usuarioRepository.actualizarDatosPersonales(
                404L, "Laura Gómez", null, null))
                .thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> useCase.actualizar(404L, "Laura Gómez", null, null));

        verify(usuarioRepository).actualizarDatosPersonales(404L, "Laura Gómez", null, null);
    }
}

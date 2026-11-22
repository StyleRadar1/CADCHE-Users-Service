package edu.dosw.styleradar.usuarios.application;

import edu.dosw.styleradar.usuarios.application.dto.response.MedidasCorporalesResponseDTO;
import edu.dosw.styleradar.usuarios.application.usecase.ObtenerMedidasCorporalesUseCaseImpl;
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

class ObtenerMedidasCorporalesUseCaseImplTest {

    private UsuarioRepositoryPort usuarioRepository;
    private ObtenerMedidasCorporalesUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepositoryPort.class);
        useCase = new ObtenerMedidasCorporalesUseCaseImpl(usuarioRepository);
    }

    @Test
    void debeObtenerLasCuatroMedidasCorporales() {

        Usuario usuario = new Usuario(
                144L,
                "Laura Gomez",
                "laura@gmail.com",
                "3001234567",
                "hash",
                true,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now(),
                null,
                null,
                null,
                true,
                "Chapinero",
                175.0,
                96.0,
                82.0,
                98.0
        );

        when(usuarioRepository.buscarPorId(144L))
                .thenReturn(Optional.of(usuario));

        MedidasCorporalesResponseDTO respuesta = useCase.obtener(144L);

        assertNotNull(respuesta);
        assertEquals(175.0, respuesta.getHeightCm());
        assertEquals(96.0, respuesta.getChestCm());
        assertEquals(82.0, respuesta.getWaistCm());
        assertEquals(98.0, respuesta.getHipCm());

        verify(usuarioRepository).buscarPorId(144L);
    }

    @Test
    void debeDevolverMedidasNulasCuandoElUsuarioNoLasTieneRegistradas() {

        Usuario usuario = new Usuario(
                144L,
                "Laura Gomez",
                "laura@gmail.com",
                "3001234567",
                "hash",
                true,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now(),
                null,
                null,
                null,
                true,
                "Chapinero",
                null,
                null,
                null,
                null
        );

        when(usuarioRepository.buscarPorId(144L))
                .thenReturn(Optional.of(usuario));

        MedidasCorporalesResponseDTO respuesta = useCase.obtener(144L);

        assertNotNull(respuesta);
        assertNull(respuesta.getHeightCm());
        assertNull(respuesta.getChestCm());
        assertNull(respuesta.getWaistCm());
        assertNull(respuesta.getHipCm());

        verify(usuarioRepository).buscarPorId(144L);
    }

    @Test
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {

        when(usuarioRepository.buscarPorId(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> useCase.obtener(999L)
        );

        verify(usuarioRepository).buscarPorId(999L);
    }
}

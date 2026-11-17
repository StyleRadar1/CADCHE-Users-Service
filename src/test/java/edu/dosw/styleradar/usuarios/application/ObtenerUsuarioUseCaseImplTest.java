package edu.dosw.styleradar.usuarios.application;

import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.application.usecase.ObtenerUsuarioUseCaseImpl;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.PreferenciaEstiloRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.TallaRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObtenerUsuarioUseCaseImplTest {

    private UsuarioRepositoryPort usuarioRepository;
    private PreferenciaEstiloRepositoryPort preferenciasRepository;
    private TallaRepositoryPort tallasRepository;
    private ObtenerUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepositoryPort.class);
        preferenciasRepository = mock(PreferenciaEstiloRepositoryPort.class);
        tallasRepository = mock(TallaRepositoryPort.class);

        useCase = new ObtenerUsuarioUseCaseImpl(
                usuarioRepository,
                preferenciasRepository,
                tallasRepository
        );
    }

    @Test
    void debeObtenerPerfilConNombreYCorreoVerificado() {

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
                "Chapinero"
        );

        when(usuarioRepository.buscarPorId(144L))
                .thenReturn(Optional.of(usuario));

        when(preferenciasRepository.buscarPorUsuarioId(144L))
                .thenReturn(Set.of());

        when(tallasRepository.buscarPorUsuarioId(144L))
                .thenReturn(Map.of());

        UsuarioResponseDTO respuesta = useCase.obtener(144L);

        assertNotNull(respuesta);
        assertEquals(144L, respuesta.getId());
        assertEquals("Laura Gomez", respuesta.getNombre());
        assertEquals("laura@gmail.com", respuesta.getEmail());
        assertEquals(Boolean.TRUE, respuesta.getCorreoVerificado());
        assertEquals("Chapinero", respuesta.getBarrio());

        verify(usuarioRepository).buscarPorId(144L);
        verify(preferenciasRepository).buscarPorUsuarioId(144L);
        verify(tallasRepository).buscarPorUsuarioId(144L);
    }

    @Test
    void debeDevolverCorreoNoVerificadoCuandoEsFalse() {

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
                false
        );

        when(usuarioRepository.buscarPorId(144L))
                .thenReturn(Optional.of(usuario));

        when(preferenciasRepository.buscarPorUsuarioId(144L))
                .thenReturn(Set.of());

        when(tallasRepository.buscarPorUsuarioId(144L))
                .thenReturn(Map.of());

        UsuarioResponseDTO respuesta = useCase.obtener(144L);

        assertEquals(Boolean.FALSE, respuesta.getCorreoVerificado());
    }

    @Test
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {

        when(usuarioRepository.buscarPorId(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> useCase.obtener(999L)
        );

        verifyNoInteractions(preferenciasRepository, tallasRepository);
    }
}

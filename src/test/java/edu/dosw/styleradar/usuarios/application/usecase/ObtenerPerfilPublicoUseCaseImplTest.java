package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.PublicProfileResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.PerfilPublicoUsuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerPerfilPublicoUseCaseImplTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    private ObtenerPerfilPublicoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerPerfilPublicoUseCaseImpl(usuarioRepositoryPort);
    }

    @Test
    void debeObtenerPerfilPublicoPorId() {
        PerfilPublicoUsuario perfil = new PerfilPublicoUsuario(7L, "Sofía López", EstadoCuenta.ACTIVA);
        when(usuarioRepositoryPort.buscarPerfilPublicoPorId(7L)).thenReturn(Optional.of(perfil));

        PublicProfileResponseDTO resultado = useCase.obtenerPorId(7L);

        assertNotNull(resultado);
        assertEquals(7L, resultado.getId());
        assertEquals("Sofía L.", resultado.getDisplayName());
        verify(usuarioRepositoryPort).buscarPerfilPublicoPorId(7L);
        verify(usuarioRepositoryPort, never()).buscarPorId(any());
    }

    @Test
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepositoryPort.buscarPerfilPublicoPorId(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> useCase.obtenerPorId(99L));
    }

    @Test
    void debeRetornarDisplayNameNuloParaCuentasBloqueadasEInactivas() {
        when(usuarioRepositoryPort.buscarPerfilPublicoPorId(8L))
                .thenReturn(Optional.of(new PerfilPublicoUsuario(8L, "Carlos Ruiz", EstadoCuenta.BLOQUEADA)));
        when(usuarioRepositoryPort.buscarPerfilPublicoPorId(9L))
                .thenReturn(Optional.of(new PerfilPublicoUsuario(9L, "Ana Mora", EstadoCuenta.INACTIVA)));

        PublicProfileResponseDTO bloqueado = useCase.obtenerPorId(8L);
        PublicProfileResponseDTO inactivo = useCase.obtenerPorId(9L);

        assertEquals(8L, bloqueado.getId());
        assertNull(bloqueado.getDisplayName());
        assertEquals(9L, inactivo.getId());
        assertNull(inactivo.getDisplayName());
    }

    @Test
    void debeObtenerPerfilesEnLoteConUnaSolaConsultaOmitiendoInexistentes() {
        List<Long> idsEsperados = List.of(1L, 2L, 3L);
        when(usuarioRepositoryPort.buscarPerfilesPublicosPorIds(idsEsperados)).thenReturn(List.of(
                new PerfilPublicoUsuario(2L, "Pedro Páramo", EstadoCuenta.ACTIVA),
                new PerfilPublicoUsuario(1L, "Laura Gómez", EstadoCuenta.ACTIVA)
        ));

        List<PublicProfileResponseDTO> resultados = useCase.obtenerPorLote(
                Arrays.asList(1L, null, -4L, 2L, 1L, 3L));

        assertEquals(2, resultados.size());
        assertEquals(1L, resultados.get(0).getId());
        assertEquals("Laura G.", resultados.get(0).getDisplayName());
        assertEquals(2L, resultados.get(1).getId());
        assertEquals("Pedro P.", resultados.get(1).getDisplayName());

        verify(usuarioRepositoryPort, times(1)).buscarPerfilesPublicosPorIds(idsEsperados);
        verify(usuarioRepositoryPort, never()).buscarPorId(any());
    }

    @Test
    void debeOcultarDisplayNameEnLoteParaCuentasNoActivas() {
        List<Long> ids = List.of(10L, 11L);
        when(usuarioRepositoryPort.buscarPerfilesPublicosPorIds(ids)).thenReturn(List.of(
                new PerfilPublicoUsuario(10L, "Usuario Bloqueado", EstadoCuenta.BLOQUEADA),
                new PerfilPublicoUsuario(11L, "Usuario Inactivo", EstadoCuenta.INACTIVA)
        ));

        List<PublicProfileResponseDTO> resultados = useCase.obtenerPorLote(ids);

        assertEquals(2, resultados.size());
        assertNull(resultados.get(0).getDisplayName());
        assertNull(resultados.get(1).getDisplayName());
    }

    @Test
    void debeDevolverListaVaciaCuandoLoteEsNuloVacioOSinIdsValidos() {
        assertTrue(useCase.obtenerPorLote(null).isEmpty());
        assertTrue(useCase.obtenerPorLote(List.of()).isEmpty());
        assertTrue(useCase.obtenerPorLote(Arrays.asList(null, 0L, -5L)).isEmpty());

        verifyNoInteractions(usuarioRepositoryPort);
    }
}

package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.PublicProfileResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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

    private Usuario crearUsuario(Long id, String nombre, EstadoCuenta estado) {
        return new Usuario(
                id,
                nombre,
                "test" + id + "@example.com",
                "3001234567",
                "hash",
                true,
                Rol.COMPRADOR,
                estado,
                Instant.now()
        );
    }

    @Test
    void debeObtenerPerfilPublicoPorId() {
        Usuario usuario = crearUsuario(7L, "Sofía López", EstadoCuenta.ACTIVA);
        when(usuarioRepositoryPort.buscarPorId(7L)).thenReturn(Optional.of(usuario));

        PublicProfileResponseDTO resultado = useCase.obtenerPorId(7L);

        assertNotNull(resultado);
        assertEquals(7L, resultado.getId());
        assertEquals("Sofía L.", resultado.getDisplayName());
    }

    @Test
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> useCase.obtenerPorId(99L));
    }

    @Test
    void debeRetornarDisplayNameNuloParaCuentasBloqueadas() {
        Usuario bloqueado = crearUsuario(8L, "Carlos Ruiz", EstadoCuenta.BLOQUEADA);
        when(usuarioRepositoryPort.buscarPorId(8L)).thenReturn(Optional.of(bloqueado));

        PublicProfileResponseDTO resultado = useCase.obtenerPorId(8L);
        assertEquals(8L, resultado.getId());
        assertNull(resultado.getDisplayName());
    }

    @Test
    void debeObtenerPerfilesEnLoteOmitiendoInexistentes() {
        Usuario u1 = crearUsuario(1L, "Laura Gómez", EstadoCuenta.ACTIVA);
        Usuario u2 = crearUsuario(2L, "Pedro Páramo", EstadoCuenta.ACTIVA);

        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(u1));
        when(usuarioRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(u2));
        when(usuarioRepositoryPort.buscarPorId(3L)).thenReturn(Optional.empty());

        List<PublicProfileResponseDTO> resultados = useCase.obtenerPorLote(List.of(1L, 2L, 3L));

        assertEquals(2, resultados.size());
        assertEquals("Laura G.", resultados.get(0).getDisplayName());
        assertEquals("Pedro P.", resultados.get(1).getDisplayName());
    }
}

package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioEstadoCambiadoEvent;
import edu.dosw.styleradar.usuarios.domain.exception.CambioEstadoNoPermitidoException;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioEstadoEventPublisherPort;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CambiarEstadoCuentaUseCaseImplTest {

    private UsuarioRepositoryPort usuarioRepository;
    private UsuarioEstadoEventPublisherPort eventoPublisher;
    private CambiarEstadoCuentaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepositoryPort.class);
        eventoPublisher = mock(UsuarioEstadoEventPublisherPort.class);

        useCase = new CambiarEstadoCuentaUseCaseImpl(
                usuarioRepository,
                eventoPublisher
        );
    }

    private Usuario crearUsuario(
            Long id,
            Rol rol,
            EstadoCuenta estado) {

        return new Usuario(
                id,
                "Julian",
                "julian@gmail.com",
                "3001234567",
                "hash",
                true,
                rol,
                estado,
                Instant.now()
        );
    }

    @Test
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.buscarPorId(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> useCase.ejecutar(
                        1L,
                        EstadoCuenta.BLOQUEADA,
                        10L,
                        "Incumplimiento"
                )
        );

        verify(usuarioRepository, never()).cambiarEstado(
                anyLong(), any(), any(), anyLong(), anyString()
        );

        verifyNoInteractions(eventoPublisher);
    }

    @Test
    void noDebePermitirCambiarEstadoDeLaPropiaCuenta() {
        Usuario administrador = crearUsuario(
                10L,
                Rol.ADMIN_STYLERADAR,
                EstadoCuenta.ACTIVA
        );

        when(usuarioRepository.buscarPorId(10L))
                .thenReturn(Optional.of(administrador));

        assertThrows(
                CambioEstadoNoPermitidoException.class,
                () -> useCase.ejecutar(
                        10L,
                        EstadoCuenta.BLOQUEADA,
                        10L,
                        "Prueba"
                )
        );

        verify(usuarioRepository, never()).cambiarEstado(
                anyLong(), any(), any(), anyLong(), anyString()
        );

        verifyNoInteractions(eventoPublisher);
    }

    @Test
    void noDebePermitirAsignarElMismoEstado() {
        Usuario usuario = crearUsuario(
                1L,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA
        );

        when(usuarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                CambioEstadoNoPermitidoException.class,
                () -> useCase.ejecutar(
                        1L,
                        EstadoCuenta.ACTIVA,
                        10L,
                        "Prueba"
                )
        );

        verify(usuarioRepository, never()).cambiarEstado(
                anyLong(), any(), any(), anyLong(), anyString()
        );

        verifyNoInteractions(eventoPublisher);
    }

    @Test
    void noDebeDesactivarAlUltimoAdministradorActivo() {
        Usuario administrador = crearUsuario(
                1L,
                Rol.ADMIN_STYLERADAR,
                EstadoCuenta.ACTIVA
        );

        when(usuarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(administrador));

        when(usuarioRepository.contarAdministradoresActivos())
                .thenReturn(1L);

        assertThrows(
                CambioEstadoNoPermitidoException.class,
                () -> useCase.ejecutar(
                        1L,
                        EstadoCuenta.INACTIVA,
                        10L,
                        "Prueba"
                )
        );

        verify(usuarioRepository).contarAdministradoresActivos();

        verify(usuarioRepository, never()).cambiarEstado(
                anyLong(), any(), any(), anyLong(), anyString()
        );

        verifyNoInteractions(eventoPublisher);
    }

    @Test
    void debeCambiarEstadoDeUsuarioYPublicarEvento() {
        Usuario usuario = crearUsuario(
                1L,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA
        );

        Usuario usuarioActualizado = crearUsuario(
                1L,
                Rol.COMPRADOR,
                EstadoCuenta.BLOQUEADA
        );

        when(usuarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.cambiarEstado(
                eq(1L),
                eq(EstadoCuenta.BLOQUEADA),
                any(Instant.class),
                eq(10L),
                eq("Incumplimiento")
        )).thenReturn(Optional.of(usuarioActualizado));

        Usuario resultado = useCase.ejecutar(
                1L,
                EstadoCuenta.BLOQUEADA,
                10L,
                "Incumplimiento"
        );

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(EstadoCuenta.BLOQUEADA, resultado.getEstado());

        ArgumentCaptor<UsuarioEstadoCambiadoEvent> captor =
                ArgumentCaptor.forClass(UsuarioEstadoCambiadoEvent.class);

        verify(eventoPublisher).publicar(captor.capture());

        UsuarioEstadoCambiadoEvent evento = captor.getValue();

        assertEquals(1L, evento.usuarioId());
        assertEquals(EstadoCuenta.ACTIVA, evento.estadoAnterior());
        assertEquals(EstadoCuenta.BLOQUEADA, evento.estadoNuevo());
        assertEquals(10L, evento.administradorId());
        assertEquals("Incumplimiento", evento.motivo());
        assertNotNull(evento.fechaCambio());
    }

    @Test
    void noDebeDesactivarAdministradorCuandoHayMasAdministradoresActivos() {
        Usuario administrador = crearUsuario(
                1L,
                Rol.ADMIN_STYLERADAR,
                EstadoCuenta.ACTIVA
        );

        Usuario administradorActualizado = crearUsuario(
                1L,
                Rol.ADMIN_STYLERADAR,
                EstadoCuenta.INACTIVA
        );

        when(usuarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(administrador));

        when(usuarioRepository.contarAdministradoresActivos())
                .thenReturn(2L);

        when(usuarioRepository.cambiarEstado(
                eq(1L),
                eq(EstadoCuenta.INACTIVA),
                any(Instant.class),
                eq(10L),
                eq("Cambio autorizado")
        )).thenReturn(Optional.of(administradorActualizado));

        Usuario resultado = useCase.ejecutar(
                1L,
                EstadoCuenta.INACTIVA,
                10L,
                "Cambio autorizado"
        );

        assertNotNull(resultado);
        assertEquals(EstadoCuenta.INACTIVA, resultado.getEstado());

        verify(usuarioRepository).contarAdministradoresActivos();
        verify(eventoPublisher).publicar(any(UsuarioEstadoCambiadoEvent.class));
    }

    @Test
    void debeLanzarExcepcionSiUsuarioDesapareceAlCambiarEstado() {
        Usuario usuario = crearUsuario(
                1L,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA
        );

        when(usuarioRepository.buscarPorId(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.cambiarEstado(
                eq(1L),
                eq(EstadoCuenta.BLOQUEADA),
                any(Instant.class),
                eq(10L),
                eq("Incumplimiento")
        )).thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> useCase.ejecutar(
                        1L,
                        EstadoCuenta.BLOQUEADA,
                        10L,
                        "Incumplimiento"
                )
        );

        verifyNoInteractions(eventoPublisher);
    }
}

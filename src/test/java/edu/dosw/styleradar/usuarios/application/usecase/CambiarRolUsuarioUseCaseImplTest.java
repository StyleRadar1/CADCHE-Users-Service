package edu.dosw.styleradar.usuarios.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.dosw.styleradar.usuarios.domain.exception.CambioEstadoNoPermitidoException;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

class CambiarRolUsuarioUseCaseImplTest {

    private UsuarioRepositoryPort repository;
    private CambiarRolUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(UsuarioRepositoryPort.class);
        useCase = new CambiarRolUsuarioUseCaseImpl(repository);
    }

    @Test
    void asignaRolDeTiendaYDevuelveLaCuentaActualizada() {
        Usuario actual = usuario(20L, Rol.COMPRADOR, EstadoCuenta.ACTIVA);
        Usuario actualizado = usuario(20L, Rol.ADMIN_ALMACEN, EstadoCuenta.ACTIVA);
        when(repository.buscarPorId(20L)).thenReturn(Optional.of(actual));
        when(repository.actualizarRol(20L, Rol.ADMIN_ALMACEN))
                .thenReturn(Optional.of(actualizado));

        Usuario result = useCase.ejecutar(20L, Rol.ADMIN_ALMACEN);

        assertEquals(Rol.ADMIN_ALMACEN, result.getRol());
        verify(repository).actualizarRol(20L, Rol.ADMIN_ALMACEN);
    }

    @Test
    void permiteAsignarElRolAdministrativoDelSistema() {
        Usuario actual = usuario(20L, Rol.COMPRADOR, EstadoCuenta.ACTIVA);
        Usuario actualizado = usuario(20L, Rol.ADMIN_STYLERADAR, EstadoCuenta.ACTIVA);
        when(repository.buscarPorId(20L)).thenReturn(Optional.of(actual));
        when(repository.actualizarRol(20L, Rol.ADMIN_STYLERADAR))
                .thenReturn(Optional.of(actualizado));

        Usuario result = useCase.ejecutar(20L, Rol.ADMIN_STYLERADAR);

        assertEquals(Rol.ADMIN_STYLERADAR, result.getRol());
        verify(repository).actualizarRol(20L, Rol.ADMIN_STYLERADAR);
    }

    @Test
    void rechazaUnRolNuloAntesDeConsultarElRepositorio() {
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(20L, null));
        verifyNoInteractions(repository);
    }

    @Test
    void informaSiLaCuentaNoExiste() {
        when(repository.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> useCase.ejecutar(404L, Rol.REPRESENTANTE_FUNDACION));
        verify(repository, never()).actualizarRol(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void protegeAlUltimoAdministradorActivo() {
        when(repository.buscarPorId(1L))
                .thenReturn(Optional.of(usuario(1L, Rol.ADMIN_STYLERADAR, EstadoCuenta.ACTIVA)));
        when(repository.contarAdministradoresActivos()).thenReturn(1L);

        CambioEstadoNoPermitidoException exception = assertThrows(
                CambioEstadoNoPermitidoException.class,
                () -> useCase.ejecutar(1L, Rol.COMPRADOR));

        assertEquals("LAST_ACTIVE_ADMIN", exception.getCode());
        verify(repository, never()).actualizarRol(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void permiteCambiarRolDeUnAdminCuandoQuedaOtroActivo() {
        Usuario actualizado = usuario(1L, Rol.REPRESENTANTE_FUNDACION, EstadoCuenta.ACTIVA);
        when(repository.buscarPorId(1L))
                .thenReturn(Optional.of(usuario(1L, Rol.ADMIN_STYLERADAR, EstadoCuenta.ACTIVA)));
        when(repository.contarAdministradoresActivos()).thenReturn(2L);
        when(repository.actualizarRol(1L, Rol.REPRESENTANTE_FUNDACION))
                .thenReturn(Optional.of(actualizado));

        Usuario result = useCase.ejecutar(1L, Rol.REPRESENTANTE_FUNDACION);

        assertEquals(Rol.REPRESENTANTE_FUNDACION, result.getRol());
        verify(repository).actualizarRol(1L, Rol.REPRESENTANTE_FUNDACION);
    }

    @Test
    void informaSiLaCuentaDesapareceDuranteElCambio() {
        when(repository.buscarPorId(20L))
                .thenReturn(Optional.of(usuario(20L, Rol.COMPRADOR, EstadoCuenta.ACTIVA)));
        when(repository.actualizarRol(20L, Rol.ADMIN_ALMACEN)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> useCase.ejecutar(20L, Rol.ADMIN_ALMACEN));
    }

    private Usuario usuario(Long id, Rol rol, EstadoCuenta estado) {
        return new Usuario(id, "Cuenta", "cuenta@example.com", null, "hash",
                true, rol, estado, Instant.parse("2026-11-20T10:00:00Z"));
    }
}

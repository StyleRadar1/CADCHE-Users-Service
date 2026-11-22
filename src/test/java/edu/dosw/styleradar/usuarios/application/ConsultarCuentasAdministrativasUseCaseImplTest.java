package edu.dosw.styleradar.usuarios.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.dosw.styleradar.usuarios.application.usecase.ConsultarCuentaAdministrativaUseCaseImpl;
import edu.dosw.styleradar.usuarios.application.usecase.ConsultarCuentasAdministrativasUseCaseImpl;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultarCuentasAdministrativasUseCaseImplTest {

    @Mock
    private UsuarioRepositoryPort repository;

    private ConsultarCuentasAdministrativasUseCaseImpl listar;
    private ConsultarCuentaAdministrativaUseCaseImpl consultar;

    @BeforeEach
    void setUp() {
        listar = new ConsultarCuentasAdministrativasUseCaseImpl(repository);
        consultar = new ConsultarCuentaAdministrativaUseCaseImpl(repository);
    }

    @Test
    void listaCuentasAplicandoEstadoRolTextoYPaginacion() {
        when(repository.buscarCuentas(EstadoCuenta.ACTIVA, Rol.COMPRADOR, "Laura", 1, 2))
                .thenReturn(List.of(usuario(5L, "Laura Gómez", "laura@example.com", "Chapinero")));
        when(repository.contarCuentas(EstadoCuenta.ACTIVA, Rol.COMPRADOR, "Laura"))
                .thenReturn(3L);

        var resultado = listar.ejecutar(EstadoCuenta.ACTIVA, Rol.COMPRADOR, " Laura ", 1, 2);

        assertEquals(1, resultado.getContent().size());
        assertEquals("Chapinero", resultado.getContent().get(0).barrio());
        assertEquals(3L, resultado.getTotalElements());
        assertEquals(2, resultado.getTotalPages());
        assertEquals(1, resultado.getPage());
        assertEquals(2, resultado.getSize());
        assertEquals(false, resultado.isFirst());
        assertEquals(true, resultado.isLast());
        verify(repository).buscarCuentas(EstadoCuenta.ACTIVA, Rol.COMPRADOR, "Laura", 1, 2);
        verify(repository).contarCuentas(EstadoCuenta.ACTIVA, Rol.COMPRADOR, "Laura");
    }

    @Test
    void listaCompletaMarcaPaginaVaciaComoPrimeraYUltima() {
        when(repository.buscarCuentas(null, null, "", 0, 20)).thenReturn(List.of());
        when(repository.contarCuentas(null, null, "")).thenReturn(0L);

        var resultado = listar.ejecutar(null, null, "   ", 0, 20);

        assertEquals(0, resultado.getTotalPages());
        assertEquals(true, resultado.isFirst());
        assertEquals(true, resultado.isLast());

        verify(repository).buscarCuentas(null, null, "", 0, 20);
        verify(repository).contarCuentas(null, null, "");
    }

    @Test
    void consultaDetalleIncluyeBarrioYEstadoEfectivo() {
        when(repository.buscarPorId(5L)).thenReturn(Optional.of(
                usuario(5L, "Laura Gómez", "laura@example.com", "Chapinero")));

        var resultado = consultar.ejecutar(5L);

        assertEquals(5L, resultado.id());
        assertEquals("Chapinero", resultado.barrio());
        assertEquals(EstadoCuenta.ACTIVA, resultado.estadoCuenta());
    }

    @Test
    void detalleInexistenteRetornaUsuarioNoEncontrado() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                UsuarioNoEncontradoException.class, () -> consultar.ejecutar(99L));
    }

    private Usuario usuario(Long id, String nombre, String email, String barrio) {
        return new Usuario(id, nombre, email, null, "hash", true,
                Rol.COMPRADOR, null, Instant.parse("2026-11-10T10:00:00Z"),
                null, null, null, false, barrio);
    }
}

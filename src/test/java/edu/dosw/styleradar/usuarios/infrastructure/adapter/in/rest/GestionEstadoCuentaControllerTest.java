package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.dosw.styleradar.usuarios.application.dto.request.CambioEstadoCuentaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.CuentasConsultaRequestDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarEstadoCuentaUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ConsultarCuentaAdministrativaUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ConsultarCuentasAdministrativasUseCase;
import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class GestionEstadoCuentaControllerTest {

    @Mock
    private CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase;
    @Mock
    private ConsultarCuentasAdministrativasUseCase consultarCuentasUseCase;
    @Mock
    private ConsultarCuentaAdministrativaUseCase consultarCuentaUseCase;

    private GestionEstadoCuentaController controller;

    @BeforeEach
    void setUp() {
        controller = new GestionEstadoCuentaController(
                cambiarEstadoCuentaUseCase, consultarCuentasUseCase, consultarCuentaUseCase);
    }

    @AfterEach
    void limpiarSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void consultarRetornaPaginaDeCuentasYCalculaSusIndicadores() {
        CuentasConsultaRequestDTO request = new CuentasConsultaRequestDTO();
        request.setEstado(EstadoCuenta.ACTIVA);
        request.setRol(Rol.COMPRADOR);
        request.setQ(" Laura ");
        request.setPage(0);
        request.setSize(2);
        var dto = respuesta(8L, EstadoCuenta.ACTIVA, "Chapinero");
        when(consultarCuentasUseCase.ejecutar(
                EstadoCuenta.ACTIVA, Rol.COMPRADOR, " Laura ", 0, 2))
                .thenReturn(new PageResponseDTO<>(List.of(dto), 0, 2, 3L, 2, true, false));

        var response = controller.consultar(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(2, response.getBody().getTotalPages());
        assertEquals(true, response.getBody().isFirst());
        assertEquals(false, response.getBody().isLast());
        assertEquals("Chapinero", response.getBody().getContent().get(0).barrio());
        verify(consultarCuentasUseCase).ejecutar(
                EstadoCuenta.ACTIVA, Rol.COMPRADOR, " Laura ", 0, 2);
    }

    @Test
    void consultarUnoRetornaLaCuentaYAsumeActivaCuandoElEstadoEsNulo() {
        when(consultarCuentaUseCase.ejecutar(8L))
                .thenReturn(respuesta(8L, EstadoCuenta.ACTIVA, "Suba"));

        var response = controller.consultarUno(8L);

        assertEquals(EstadoCuenta.ACTIVA, response.getBody().estadoCuenta());
        assertEquals(8L, response.getBody().id());
        assertEquals("Suba", response.getBody().barrio());
    }

    @Test
    void consultarUnoFallaSiLaCuentaNoExiste() {
        when(consultarCuentaUseCase.ejecutar(8L))
                .thenThrow(new UsuarioNoEncontradoException(8L));

        assertThrows(UsuarioNoEncontradoException.class, () -> controller.consultarUno(8L));
    }

    @Test
    void cambiarUsaElIdDelAdministradorAutenticado() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("99", "token"));
        CambioEstadoCuentaRequestDTO request = new CambioEstadoCuentaRequestDTO();
        request.setEstado(EstadoCuenta.BLOQUEADA);
        request.setMotivo("Revision");
        when(cambiarEstadoCuentaUseCase.ejecutar(8L, EstadoCuenta.BLOQUEADA, 99L, "Revision"))
                .thenReturn(usuario(8L, EstadoCuenta.BLOQUEADA));

        var response = controller.cambiar(8L, request);

        assertEquals(EstadoCuenta.BLOQUEADA, response.getBody().estadoCuenta());
        verify(cambiarEstadoCuentaUseCase).ejecutar(8L, EstadoCuenta.BLOQUEADA, 99L, "Revision");
    }

    private Usuario usuario(Long id, EstadoCuenta estado) {
        return new Usuario(id, "Comprador", "comprador@example.com", "3001234567", "hash",
                true, Rol.COMPRADOR, estado, Instant.parse("2026-11-01T12:00:00Z"));
    }

    private CuentaAdministrativaResponseDTO respuesta(
            Long id, EstadoCuenta estado, String barrio) {
        return new CuentaAdministrativaResponseDTO(id, "Comprador", "comprador@example.com",
                barrio, java.util.Set.of(Rol.COMPRADOR), estado,
                Instant.parse("2026-11-01T12:00:00Z"), null, null, null);
    }
}

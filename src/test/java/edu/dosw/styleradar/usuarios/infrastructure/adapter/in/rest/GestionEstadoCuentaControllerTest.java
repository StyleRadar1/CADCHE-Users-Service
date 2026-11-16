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
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
    private UsuarioRepositoryPort usuarioRepository;

    private GestionEstadoCuentaController controller;

    @BeforeEach
    void setUp() {
        controller = new GestionEstadoCuentaController(cambiarEstadoCuentaUseCase, usuarioRepository);
    }

    @AfterEach
    void limpiarSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void consultarRetornaPaginaDeCuentasYCalculaSusIndicadores() {
        CuentasConsultaRequestDTO request = new CuentasConsultaRequestDTO();
        request.setPage(0);
        request.setSize(2);
        when(usuarioRepository.buscarCuentas(null, 0, 2)).thenReturn(List.of(usuario(8L, EstadoCuenta.ACTIVA)));
        when(usuarioRepository.contarCuentas(null)).thenReturn(3L);

        var response = controller.consultar(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(2, response.getBody().getTotalPages());
        assertEquals(true, response.getBody().isFirst());
        assertEquals(false, response.getBody().isLast());
    }

    @Test
    void consultarUnoRetornaLaCuentaYAsumeActivaCuandoElEstadoEsNulo() {
        when(usuarioRepository.buscarPorId(8L)).thenReturn(Optional.of(usuario(8L, null)));

        var response = controller.consultarUno(8L);

        assertEquals(EstadoCuenta.ACTIVA, response.getBody().estadoCuenta());
        assertEquals(8L, response.getBody().id());
    }

    @Test
    void consultarUnoFallaSiLaCuentaNoExiste() {
        when(usuarioRepository.buscarPorId(8L)).thenReturn(Optional.empty());

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
}

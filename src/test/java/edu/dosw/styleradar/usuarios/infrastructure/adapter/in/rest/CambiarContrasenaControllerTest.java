package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.dosw.styleradar.usuarios.application.dto.request.CambiarContrasenaRequestDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarContrasenaUseCase;

class CambiarContrasenaControllerTest {

    private CambiarContrasenaUseCase useCase;
    private CambiarContrasenaController controller;

    @BeforeEach
    void setUp() {
        useCase = org.mockito.Mockito.mock(CambiarContrasenaUseCase.class);
        controller = new CambiarContrasenaController(useCase);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("7", "token"));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void cambiaLaContrasenaDelUsuarioAutenticado() {
        var response = controller.cambiar(7L,
                new CambiarContrasenaRequestDTO("Actual123", "Nueva1234"));

        assertEquals(204, response.getStatusCode().value());
        verify(useCase).ejecutar(7L, "Actual123", "Nueva1234");
    }

    @Test
    void rechazaCambiarLaContrasenaDeOtroUsuario() {
        assertThrows(AccessDeniedException.class, () -> controller.cambiar(8L,
                new CambiarContrasenaRequestDTO("Actual123", "Nueva1234")));

        verify(useCase, never()).ejecutar(8L, "Actual123", "Nueva1234");
    }
}

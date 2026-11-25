package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.dosw.styleradar.usuarios.application.dto.request.CambiarRolUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarRolUsuarioUseCase;

class CambioRolUsuarioControllerTest {

    private CambiarRolUsuarioUseCase useCase;
    private CambioRolUsuarioController controller;

    @BeforeEach
    void setUp() {
        useCase = Mockito.mock(CambiarRolUsuarioUseCase.class);
        controller = new CambioRolUsuarioController(useCase);
    }

    @Test
    void delegaElCambioDeRolYDevuelveLaCuenta() {
        Usuario user = new Usuario(25L, "Tienda QA", "tienda@example.com", null,
                "hash", true, Rol.ADMIN_ALMACEN, EstadoCuenta.ACTIVA,
                Instant.parse("2026-11-20T10:00:00Z"));
        when(useCase.ejecutar(25L, Rol.ADMIN_ALMACEN)).thenReturn(user);

        var response = controller.cambiarRol(
                25L, new CambiarRolUsuarioRequestDTO("ADMIN_ALMACEN"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals(25L, response.getBody().id());
        assertEquals(Set.of(Rol.ADMIN_ALMACEN), response.getBody().roles());
        verify(useCase).ejecutar(25L, Rol.ADMIN_ALMACEN);
    }
}

package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.request.VerificarCredencialesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.VerificarCredencialesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.VerificarCredencialesUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VerificarCredencialesControllerTest {

    private VerificarCredencialesUseCase verificarCredencialesUseCase;
    private VerificarCredencialesController controller;

    @BeforeEach
    void setUp() {
        verificarCredencialesUseCase =
                mock(VerificarCredencialesUseCase.class);

        controller = new VerificarCredencialesController(
                verificarCredencialesUseCase
        );
    }

    @Test
    void debeVerificarCredencialesCorrectas() {

        VerificarCredencialesRequestDTO request =
                new VerificarCredencialesRequestDTO(
                        "comprador@styleradar.com",
                        "password123"
                );

        Usuario usuario = new Usuario(
                144L,
                "Usuario de prueba",
                "comprador@styleradar.com",
                "3001234567",
                "hash-password",
                true,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now()
        );

        when(verificarCredencialesUseCase.verificar(
                request.email(),
                request.password()
        )).thenReturn(Optional.of(usuario));

        ResponseEntity<?> respuesta = controller.verificar(request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());

        VerificarCredencialesResponseDTO body =
                (VerificarCredencialesResponseDTO) respuesta.getBody();

        assertEquals(144L, body.id());
        assertEquals("comprador@styleradar.com", body.email());
        assertEquals(Rol.COMPRADOR, body.rol());
        assertEquals(EstadoCuenta.ACTIVA, body.estado());

        verify(verificarCredencialesUseCase).verificar(
                request.email(),
                request.password()
        );
    }

    @Test
    void debeRechazarCredencialesIncorrectas() {

        VerificarCredencialesRequestDTO request =
                new VerificarCredencialesRequestDTO(
                        "comprador@styleradar.com",
                        "password-incorrecta"
                );

        when(verificarCredencialesUseCase.verificar(
                request.email(),
                request.password()
        )).thenReturn(Optional.empty());

        ResponseEntity<?> respuesta = controller.verificar(request);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                respuesta.getStatusCode()
        );

        verify(verificarCredencialesUseCase).verificar(
                request.email(),
                request.password()
        );
    }
}

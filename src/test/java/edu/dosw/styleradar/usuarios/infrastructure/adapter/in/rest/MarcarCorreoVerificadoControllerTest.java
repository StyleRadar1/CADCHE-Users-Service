package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.domain.port.in.MarcarCorreoVerificadoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MarcarCorreoVerificadoControllerTest {

    private MarcarCorreoVerificadoUseCase useCase;
    private MarcarCorreoVerificadoController controller;

    @BeforeEach
    void setUp() {
        useCase = mock(MarcarCorreoVerificadoUseCase.class);
        controller = new MarcarCorreoVerificadoController(useCase);
    }

    @Test
    void debeMarcarCorreoYResponderSinContenido() {

        ResponseEntity<Void> respuesta =
                controller.marcarCorreoVerificado(144L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(useCase).ejecutar(144L);
    }
}

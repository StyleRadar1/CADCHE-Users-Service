package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.response.MedidasCorporalesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerMedidasCorporalesUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternalUsuarioControllerTest {

    private ObtenerMedidasCorporalesUseCase obtenerMedidasCorporalesUseCase;
    private InternalUserController controller;

    @BeforeEach
    void setUp() {
        obtenerMedidasCorporalesUseCase =
                mock(ObtenerMedidasCorporalesUseCase.class);

        controller = new InternalUserController(
                obtenerMedidasCorporalesUseCase
        );
    }

    @Test
    void debeObtenerLasCuatroMedidasCorporales() {

        MedidasCorporalesResponseDTO esperado =
                MedidasCorporalesResponseDTO.builder()
                        .heightCm(175.0)
                        .chestCm(96.0)
                        .waistCm(82.0)
                        .hipCm(98.0)
                        .build();

        when(obtenerMedidasCorporalesUseCase.obtener(144L))
                .thenReturn(esperado);

        ResponseEntity<MedidasCorporalesResponseDTO> respuesta =
                controller.getMeasurements(144L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());

        assertEquals(175.0, respuesta.getBody().getHeightCm());
        assertEquals(96.0, respuesta.getBody().getChestCm());
        assertEquals(82.0, respuesta.getBody().getWaistCm());
        assertEquals(98.0, respuesta.getBody().getHipCm());

        verify(obtenerMedidasCorporalesUseCase).obtener(144L);
    }

    @Test
    void debeDevolverMedidasNulasCuandoNoEstanRegistradas() {

        MedidasCorporalesResponseDTO esperado =
                MedidasCorporalesResponseDTO.builder()
                        .heightCm(null)
                        .chestCm(null)
                        .waistCm(null)
                        .hipCm(null)
                        .build();

        when(obtenerMedidasCorporalesUseCase.obtener(144L))
                .thenReturn(esperado);

        ResponseEntity<MedidasCorporalesResponseDTO> respuesta =
                controller.getMeasurements(144L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());

        assertNull(respuesta.getBody().getHeightCm());
        assertNull(respuesta.getBody().getChestCm());
        assertNull(respuesta.getBody().getWaistCm());
        assertNull(respuesta.getBody().getHipCm());

        verify(obtenerMedidasCorporalesUseCase).obtener(144L);
    }

    @Test
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {

        when(obtenerMedidasCorporalesUseCase.obtener(999L))
                .thenThrow(new UsuarioNoEncontradoException(999L));

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> controller.getMeasurements(999L)
        );

        verify(obtenerMedidasCorporalesUseCase).obtener(999L);
    }
}


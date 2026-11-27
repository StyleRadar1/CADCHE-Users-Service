package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.request.BatchPublicProfilesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.MedidasCorporalesResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PublicProfileResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerMedidasCorporalesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerPerfilPublicoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternalUsuarioControllerTest {

    private ObtenerMedidasCorporalesUseCase obtenerMedidasCorporalesUseCase;
    private ObtenerPerfilPublicoUseCase obtenerPerfilPublicoUseCase;
    private InternalUserController controller;

    @BeforeEach
    void setUp() {
        obtenerMedidasCorporalesUseCase = mock(ObtenerMedidasCorporalesUseCase.class);
        obtenerPerfilPublicoUseCase = mock(ObtenerPerfilPublicoUseCase.class);

        controller = new InternalUserController(
                obtenerMedidasCorporalesUseCase,
                obtenerPerfilPublicoUseCase
        );
    }

    @Test
    void debeObtenerLasCuatroMedidasCorporales() {
        MedidasCorporalesResponseDTO esperado = MedidasCorporalesResponseDTO.builder()
                .heightCm(175.0)
                .chestCm(96.0)
                .waistCm(82.0)
                .hipCm(98.0)
                .build();

        when(obtenerMedidasCorporalesUseCase.obtener(144L)).thenReturn(esperado);

        ResponseEntity<MedidasCorporalesResponseDTO> respuesta = controller.getMeasurements(144L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(175.0, respuesta.getBody().getHeightCm());
        verify(obtenerMedidasCorporalesUseCase).obtener(144L);
    }

    @Test
    void debeObtenerPerfilPublico() {
        PublicProfileResponseDTO perfil = PublicProfileResponseDTO.builder()
                .id(7L)
                .displayName("Sofía L.")
                .build();

        when(obtenerPerfilPublicoUseCase.obtenerPorId(7L)).thenReturn(perfil);

        ResponseEntity<PublicProfileResponseDTO> respuesta = controller.getPublicProfile(7L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(7L, respuesta.getBody().getId());
        assertEquals("Sofía L.", respuesta.getBody().getDisplayName());
        verify(obtenerPerfilPublicoUseCase).obtenerPorId(7L);
    }

    @Test
    void debeObtenerPerfilesPublicosEnLote() {
        List<PublicProfileResponseDTO> perfiles = List.of(
                new PublicProfileResponseDTO(1L, "Laura G."),
                new PublicProfileResponseDTO(2L, "Carlos M.")
        );

        when(obtenerPerfilPublicoUseCase.obtenerPorLote(List.of(1L, 2L))).thenReturn(perfiles);

        BatchPublicProfilesRequestDTO request = new BatchPublicProfilesRequestDTO(List.of(1L, 2L));
        ResponseEntity<List<PublicProfileResponseDTO>> respuesta = controller.getPublicProfiles(request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(2, respuesta.getBody().size());
        verify(obtenerPerfilPublicoUseCase).obtenerPorLote(List.of(1L, 2L));
    }

    @Test
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        when(obtenerMedidasCorporalesUseCase.obtener(999L))
                .thenThrow(new UsuarioNoEncontradoException(999L));

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> controller.getMeasurements(999L)
        );
    }
}

package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.request.PersonalizacionRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.RecomendacionesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerFeedPersonalizadoUseCase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonalizacionControllerTest {

    private ObtenerFeedPersonalizadoUseCase obtenerFeedPersonalizadoUseCase;
    private PersonalizacionController controller;

    @BeforeEach
    void setUp() {

        obtenerFeedPersonalizadoUseCase =
                mock(ObtenerFeedPersonalizadoUseCase.class);

        controller = new PersonalizacionController(
                obtenerFeedPersonalizadoUseCase
        );

        Jwt jwt = Jwt.withTokenValue("token-de-prueba")
                .header("alg", "HS256")
                .claim("sub", "144")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthenticationToken(jwt)
        );
    }

    @AfterEach
    void limpiarContextoSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void debeObtenerFeedDelUsuarioAutenticado() {

        PersonalizacionRequestDTO request =
                new PersonalizacionRequestDTO();

        PageResponseDTO<CatalogoItemResponseDTO> respuestaEsperada =
                new PageResponseDTO<>(
                        Collections.emptyList(),
                        0,
                        20,
                        0,
                        0,
                        true,
                        true
                );

        when(obtenerFeedPersonalizadoUseCase.obtener(144L, 0, 20))
                .thenReturn(respuestaEsperada);

        ResponseEntity<PageResponseDTO<CatalogoItemResponseDTO>> respuesta =
                controller.feed(request);

        assertEquals(200, respuesta.getStatusCode().value());
        assertSame(respuestaEsperada, respuesta.getBody());

        verify(obtenerFeedPersonalizadoUseCase)
                .obtener(144L, 0, 20);
    }

    @Test
    void debeObtenerRecomendacionesDelUsuarioAutenticado() {

        PersonalizacionRequestDTO request =
                new PersonalizacionRequestDTO();

        PageResponseDTO<CatalogoItemResponseDTO> pagina =
                new PageResponseDTO<>(
                        Collections.emptyList(),
                        0,
                        20,
                        0,
                        0,
                        true,
                        true
                );

        when(obtenerFeedPersonalizadoUseCase.obtener(144L, 0, 20))
                .thenReturn(pagina);

        ResponseEntity<RecomendacionesResponseDTO> respuesta =
                controller.recomendaciones(request);

        assertEquals(200, respuesta.getStatusCode().value());
        assertSame(pagina.getContent(), respuesta.getBody().getContent());
        assertEquals(
                false,
                respuesta.getBody().isGeneralSinPreferencias()
        );
        assertEquals(
                "Recommendations generated successfully",
                respuesta.getBody().getMensaje()
        );

        verify(obtenerFeedPersonalizadoUseCase)
                .obtener(144L, 0, 20);
    }
}

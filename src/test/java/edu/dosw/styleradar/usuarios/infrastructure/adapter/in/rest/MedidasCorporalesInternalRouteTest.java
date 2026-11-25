package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.response.MedidasCorporalesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerMedidasCorporalesUseCase;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.BusquedaGuardadaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.PreferenciaEstiloJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.TallaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "INTERNAL_AUTH_USERNAME=test-service",
        "INTERNAL_AUTH_PASSWORD=test-service-password"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MedidasCorporalesInternalRouteTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${INTERNAL_AUTH_USERNAME}")
    private String serviceUsername;

    @Value("${INTERNAL_AUTH_PASSWORD}")
    private String servicePassword;

    @MockBean
    private ObtenerMedidasCorporalesUseCase useCase;

    @MockBean
    private UsuarioJpaRepository usuarioJpaRepository;

    @MockBean
    private PreferenciaEstiloJpaRepository preferenciaEstiloJpaRepository;

    @MockBean
    private TallaJpaRepository tallaJpaRepository;

    @MockBean
    private BusquedaGuardadaJpaRepository busquedaGuardadaJpaRepository;

    @Test
    void debeConsultarMedidasConAutenticacionDeServicio() throws Exception {

        MedidasCorporalesResponseDTO medidas =
                MedidasCorporalesResponseDTO.builder()
                        .heightCm(175.0)
                        .chestCm(96.0)
                        .waistCm(82.0)
                        .hipCm(98.0)
                        .build();

        when(useCase.obtener(144L)).thenReturn(medidas);

        mockMvc.perform(get("/api/v1/internal/users/144/measurements")
                        .header(HttpHeaders.AUTHORIZATION, authorization()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.heightCm").value(175.0))
                .andExpect(jsonPath("$.chestCm").value(96.0))
                .andExpect(jsonPath("$.waistCm").value(82.0))
                .andExpect(jsonPath("$.hipCm").value(98.0));

        verify(useCase).obtener(144L);
    }

    @Test
    void debeRechazarConsultaSinAutenticacion() throws Exception {

        mockMvc.perform(get("/api/v1/internal/users/144/measurements"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(useCase);
    }

    @Test
    void debeRechazarConsultaConCredencialesIncorrectas() throws Exception {

        String credencialesIncorrectas =
                "Basic " + HttpHeaders.encodeBasicAuth(
                        "usuario-incorrecto",
                        "password-incorrecta",
                        null
                );

        mockMvc.perform(get("/api/v1/internal/users/144/measurements")
                        .header(HttpHeaders.AUTHORIZATION, credencialesIncorrectas))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(useCase);
    }

    @Test
    void debePermitirMedidasNulasConAutenticacionValida() throws Exception {

        MedidasCorporalesResponseDTO medidas =
                MedidasCorporalesResponseDTO.builder()
                        .heightCm(null)
                        .chestCm(null)
                        .waistCm(null)
                        .hipCm(null)
                        .build();

        when(useCase.obtener(144L)).thenReturn(medidas);

        mockMvc.perform(get("/api/v1/internal/users/144/measurements")
                        .header(HttpHeaders.AUTHORIZATION, authorization()))
                .andExpect(status().isOk());

        verify(useCase).obtener(144L);
    }

    private String authorization() {
        return "Basic " + HttpHeaders.encodeBasicAuth(
                serviceUsername,
                servicePassword,
                null
        );
    }
}

package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.response.PublicProfileResponseDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerMedidasCorporalesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerPerfilPublicoUseCase;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "INTERNAL_AUTH_USERNAME=test-service",
        "INTERNAL_AUTH_PASSWORD=test-service-password"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicProfileInternalRouteTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${INTERNAL_AUTH_USERNAME}")
    private String serviceUsername;

    @Value("${INTERNAL_AUTH_PASSWORD}")
    private String servicePassword;

    @MockBean
    private ObtenerPerfilPublicoUseCase useCase;

    @MockBean
    private ObtenerMedidasCorporalesUseCase medidasUseCase;

    @MockBean
    private UsuarioJpaRepository usuarioJpaRepository;

    @MockBean
    private PreferenciaEstiloJpaRepository preferenciaEstiloJpaRepository;

    @MockBean
    private TallaJpaRepository tallaJpaRepository;

    @MockBean
    private BusquedaGuardadaJpaRepository busquedaGuardadaJpaRepository;

    @Test
    void debeConsultarPerfilPublicoConAutenticacionDeServicio() throws Exception {
        PublicProfileResponseDTO perfil = PublicProfileResponseDTO.builder()
                .id(7L)
                .displayName("Sofía L.")
                .build();

        when(useCase.obtenerPorId(7L)).thenReturn(perfil);

        mockMvc.perform(get("/api/v1/internal/users/7/public-profile")
                        .header(HttpHeaders.AUTHORIZATION, authorization()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.displayName").value("Sofía L."))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.telefono").doesNotExist())
                .andExpect(jsonPath("$.barrio").doesNotExist());

        verify(useCase).obtenerPorId(7L);
    }

    @Test
    void debeRechazarConsultaPublicProfileSinAutenticacion() throws Exception {
        mockMvc.perform(get("/api/v1/internal/users/7/public-profile"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(useCase);
    }

    @Test
    void debeConsultarLoteDePerfilesPublicos() throws Exception {
        List<PublicProfileResponseDTO> perfiles = List.of(
                new PublicProfileResponseDTO(1L, "Laura G."),
                new PublicProfileResponseDTO(2L, "Carlos M.")
        );

        when(useCase.obtenerPorLote(List.of(1L, 2L))).thenReturn(perfiles);

        mockMvc.perform(post("/api/v1/internal/users/public-profiles")
                        .header(HttpHeaders.AUTHORIZATION, authorization())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\": [1, 2]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].displayName").value("Laura G."))
                .andExpect(jsonPath("$[1].displayName").value("Carlos M."));

        verify(useCase).obtenerPorLote(List.of(1L, 2L));
    }

    private String authorization() {
        return "Basic " + HttpHeaders.encodeBasicAuth(
                serviceUsername,
                servicePassword,
                null
        );
    }
}

package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.response.PublicProfileResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "INTERNAL_AUTH_USERNAME=test-service",
        "INTERNAL_AUTH_PASSWORD=test-service-password",
        "INTERNAL_PUBLIC_PROFILE_USERNAME=chat-directory-service",
        "INTERNAL_PUBLIC_PROFILE_PASSWORD=chat-directory-password"
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

    @Value("${INTERNAL_PUBLIC_PROFILE_USERNAME}")
    private String directoryUsername;

    @Value("${INTERNAL_PUBLIC_PROFILE_PASSWORD}")
    private String directoryPassword;

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
    void debeConsultarPerfilPublicoConCuentaDeMinimoPrivilegioSinDatosSensibles() throws Exception {
        PublicProfileResponseDTO perfil = PublicProfileResponseDTO.builder()
                .id(7L)
                .displayName("Sofía L.")
                .build();

        when(useCase.obtenerPorId(7L)).thenReturn(perfil);

        mockMvc.perform(get("/api/v1/internal/users/7/public-profile")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth(directoryUsername, directoryPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.displayName").value("Sofía L."))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.telefono").doesNotExist())
                .andExpect(jsonPath("$.barrio").doesNotExist())
                .andExpect(jsonPath("$.rol").doesNotExist())
                .andExpect(jsonPath("$.estado").doesNotExist());

        verify(useCase).obtenerPorId(7L);
    }

    @Test
    void debeConsultarPerfilPublicoConCuentaDeServicioPrincipal() throws Exception {
        when(useCase.obtenerPorId(7L))
                .thenReturn(new PublicProfileResponseDTO(7L, "Sofía L."));

        mockMvc.perform(get("/api/v1/internal/users/7/public-profile")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth(serviceUsername, servicePassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.displayName").value("Sofía L."));
    }

    @Test
    void debeResponder404CuandoUsuarioNoExiste() throws Exception {
        when(useCase.obtenerPorId(999L)).thenThrow(new UsuarioNoEncontradoException(999L));

        mockMvc.perform(get("/api/v1/internal/users/999/public-profile")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth(directoryUsername, directoryPassword)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void debeRechazarConsultaSinAutenticacionYConCredencialesInvalidas() throws Exception {
        mockMvc.perform(get("/api/v1/internal/users/7/public-profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));

        mockMvc.perform(get("/api/v1/internal/users/7/public-profile")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth("otro", "invalido")))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(useCase);
    }

    @Test
    void debeDenegarCon403CuandoCuentaDeDirectorioIntentaAccederAOtrosEndpointsInternos() throws Exception {
        String directoryAuth = basicAuth(directoryUsername, directoryPassword);

        mockMvc.perform(get("/api/v1/internal/users/7/measurements")
                        .header(HttpHeaders.AUTHORIZATION, directoryAuth))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        mockMvc.perform(put("/api/v1/internal/users/7/password")
                        .header(HttpHeaders.AUTHORIZATION, directoryAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\":\"NuevaClave2026*\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));

        verifyNoInteractions(medidasUseCase);
    }

    @Test
    void debeConsultarLoteDePerfilesPublicosConCuentaDeMinimoPrivilegio() throws Exception {
        List<PublicProfileResponseDTO> perfiles = List.of(
                new PublicProfileResponseDTO(1L, "Laura G."),
                new PublicProfileResponseDTO(2L, "Carlos M.")
        );

        when(useCase.obtenerPorLote(List.of(1L, 2L))).thenReturn(perfiles);

        mockMvc.perform(post("/api/v1/internal/users/public-profiles")
                        .header(HttpHeaders.AUTHORIZATION, basicAuth(directoryUsername, directoryPassword))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\": [1, 2]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].displayName").value("Laura G."))
                .andExpect(jsonPath("$[1].displayName").value("Carlos M."));

        verify(useCase).obtenerPorLote(List.of(1L, 2L));
    }

    @Test
    void debeRechazarCon400EntradasInvalidasEnIndividualYLote() throws Exception {
        String directoryAuth = basicAuth(directoryUsername, directoryPassword);

        mockMvc.perform(get("/api/v1/internal/users/-1/public-profile")
                        .header(HttpHeaders.AUTHORIZATION, directoryAuth))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_DATA"));

        mockMvc.perform(post("/api/v1/internal/users/public-profiles")
                        .header(HttpHeaders.AUTHORIZATION, directoryAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_DATA"));

        mockMvc.perform(post("/api/v1/internal/users/public-profiles")
                        .header(HttpHeaders.AUTHORIZATION, directoryAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\": [null]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_DATA"));

        mockMvc.perform(post("/api/v1/internal/users/public-profiles")
                        .header(HttpHeaders.AUTHORIZATION, directoryAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\": [-5]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_DATA"));

        String overLimit = IntStream.rangeClosed(1, 101)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        mockMvc.perform(post("/api/v1/internal/users/public-profiles")
                        .header(HttpHeaders.AUTHORIZATION, directoryAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ids\": [" + overLimit + "]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_DATA"));

        verifyNoInteractions(useCase);
    }

    private String basicAuth(String user, String pass) {
        return "Basic " + HttpHeaders.encodeBasicAuth(user, pass, null);
    }
}

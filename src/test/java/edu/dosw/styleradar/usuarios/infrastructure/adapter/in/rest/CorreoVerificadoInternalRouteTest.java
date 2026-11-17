package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.dosw.styleradar.usuarios.domain.port.in.MarcarCorreoVerificadoUseCase;
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

@SpringBootTest(properties = {
        "INTERNAL_AUTH_USERNAME=test-service",
        "INTERNAL_AUTH_PASSWORD=test-service-password"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorreoVerificadoInternalRouteTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${INTERNAL_AUTH_USERNAME}")
    private String serviceUsername;

    @Value("${INTERNAL_AUTH_PASSWORD}")
    private String servicePassword;

    @MockBean
    private MarcarCorreoVerificadoUseCase useCase;

    @MockBean
    private UsuarioJpaRepository usuarioJpaRepository;

    @MockBean
    private PreferenciaEstiloJpaRepository preferenciaEstiloJpaRepository;

    @MockBean
    private TallaJpaRepository tallaJpaRepository;

    @MockBean
    private BusquedaGuardadaJpaRepository busquedaGuardadaJpaRepository;

    @Test
    void rutaAcordadaPorMsAuthRequiereAutenticacionDeServicioYMarcaElCorreo() throws Exception {
        mockMvc.perform(patch("/api/v1/internal/usuarios/144/correo-verificado")
                        .header(HttpHeaders.AUTHORIZATION, authorization()))
                .andExpect(status().isNoContent());

        verify(useCase).ejecutar(144L);
    }

    @Test
    void rechazaSolicitudesSinAutenticacionYNoExponeRutasSinApiV1() throws Exception {
        mockMvc.perform(patch("/api/v1/internal/usuarios/145/correo-verificado")
                        .header(HttpHeaders.AUTHORIZATION, authorization()))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch("/internal/api/v1/usuarios/144/correo-verificado"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/v1/internal/usuarios/144/correo-verificado"))
                .andExpect(status().isUnauthorized());

        verify(useCase).ejecutar(145L);
    }

    private String authorization() {
        return "Basic " + HttpHeaders.encodeBasicAuth(serviceUsername, servicePassword, null);
    }
}

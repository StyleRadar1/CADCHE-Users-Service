package edu.dosw.styleradar.usuarios.infrastructure;

import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.PreferenciaEstiloJpaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.TallaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.BusquedaGuardadaJpaRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SwaggerOpenApiTest {

    @MockBean
    private TallaJpaRepository tallaJpaRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioJpaRepository usuarioJpaRepository;

    @MockBean
    private PreferenciaEstiloJpaRepository preferenciaEstiloJpaRepository;

    @MockBean
    private BusquedaGuardadaJpaRepository busquedaGuardadaJpaRepository;

    @Test
    void debeExponerInterfazSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void debeExponerEspecificacionOpenApiYEndpointsDeUsuarios() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servers[0].url").value("/"))
                .andExpect(jsonPath("$.paths['/api/v1/users/{id}'].put").exists())
                .andExpect(jsonPath("$.paths['/api/v1/admin/users/{id}'].delete").exists())
                .andExpect(jsonPath("$.paths['/api/v1/internal/users/verify-credentials'].post")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/v1/internal/users/{id}/email-verification'].patch")
                        .exists())
                .andExpect(jsonPath("$.paths['/internal/api/v1/usuarios/verificar-credenciales']")
                        .doesNotExist())
                .andExpect(jsonPath("$.paths['/api/v1/internal/usuarios/{id}/correo-verificado']")
                        .doesNotExist());
    }

    @Test
    void recomendacionesDebeRequerirSesion() throws Exception {
        mockMvc.perform(get("/api/v1/users/144/recommendations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminacionAdministrativaDebeRequerirSesion() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/v1/admin/users/144"))
                .andExpect(status().isUnauthorized());
    }
}

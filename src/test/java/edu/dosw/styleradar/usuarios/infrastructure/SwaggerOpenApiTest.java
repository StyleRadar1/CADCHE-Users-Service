package edu.dosw.styleradar.usuarios.infrastructure;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SwaggerOpenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debeExponerEspecificacionOpenApiYEndpointsDeUsuarios() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("(?s)"))))
                .andExpect(jsonPath("$.info.title").value("StyleRadar - Users Service API (ms-usuarios)"))
                .andExpect(jsonPath("$.paths['/api/v1/usuarios'].post").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/me'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/me/preferencias-estilo'].put").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/me/preferencias'].put").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/me/tallas-habituales'].put").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/me/busquedas-guardadas'].post").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/me/busquedas-guardadas'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/me/busquedas-guardadas/{busquedaId}'].delete").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/me/feed'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios/me/recomendaciones'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/admin/usuarios'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/admin/usuarios/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/admin/usuarios/{id}/estado'].patch").exists());
    }

    @Test
    void debeExponerInterfazSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}

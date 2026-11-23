package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioGoogleUseCase;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.BusquedaGuardadaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.PreferenciaEstiloJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.TallaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "INTERNAL_AUTH_USERNAME=test-service",
        "INTERNAL_AUTH_PASSWORD=test-service-password"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrarUsuarioGoogleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrarUsuarioGoogleUseCase useCase;

    @MockBean
    private UsuarioJpaRepository usuarioJpaRepository;

    @MockBean
    private PreferenciaEstiloJpaRepository preferenciaEstiloJpaRepository;

    @MockBean
    private TallaJpaRepository tallaJpaRepository;

    @MockBean
    private BusquedaGuardadaJpaRepository busquedaGuardadaJpaRepository;

    @Test
    void requiereAutenticacionInterna() throws Exception {
        mockMvc.perform(post("/api/v1/internal/users/oauth2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(useCase);
    }

    @Test
    void aceptaBasicAuthYDevuelveElContratoDeVerificacionDeCredenciales() throws Exception {
        when(useCase.ejecutar(any())).thenReturn(new Usuario(
                12L, "Usuario de Google", "usuario@gmail.com", null, "hash", false,
                Rol.COMPRADOR, EstadoCuenta.ACTIVA, Instant.parse("2026-11-22T10:00:00Z"),
                null, null, null, true, null, null, null, null, null,
                "GOOGLE", "google-sub-1"));

        mockMvc.perform(post("/api/v1/internal/users/oauth2")
                        .header(HttpHeaders.AUTHORIZATION, authorization())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.email").value("usuario@gmail.com"))
                .andExpect(jsonPath("$.rol").value("COMPRADOR"))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.correoVerificado").value(true))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.aMapWithSize(5)))
                .andExpect(jsonPath("$.provider").doesNotExist());

        verify(useCase).ejecutar(any());
    }

    @Test
    void validaQueElProveedorSeaGoogle() throws Exception {
        mockMvc.perform(post("/api/v1/internal/users/oauth2")
                        .header(HttpHeaders.AUTHORIZATION, authorization())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"usuario@gmail.com","name":"Usuario","provider":"FACEBOOK","providerId":"sub-1"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(useCase);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoCuenta.class, names = {"INACTIVA", "BLOQUEADA"})
    void devuelveHttp200ConElEstadoRealDeUnaCuentaNoActiva(EstadoCuenta estado) throws Exception {
        when(useCase.ejecutar(any())).thenReturn(new Usuario(
                12L, "Usuario de Google", "usuario@gmail.com", null, "hash", true,
                Rol.ADMIN_ALMACEN, estado, Instant.parse("2026-11-22T10:00:00Z"),
                null, null, null, false, null, null, null, null, null,
                "GOOGLE", "google-sub-1"));

        mockMvc.perform(post("/api/v1/internal/users/oauth2")
                        .header(HttpHeaders.AUTHORIZATION, authorization())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(estado.name()))
                .andExpect(jsonPath("$.rol").value("ADMIN_ALMACEN"))
                .andExpect(jsonPath("$.correoVerificado").value(false));
    }

    @Test
    void rechazaCorreoConFormatoInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/internal/users/oauth2")
                        .header(HttpHeaders.AUTHORIZATION, authorization())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"no-es-correo","name":"Usuario","provider":"GOOGLE","providerId":"sub-1"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(useCase);
    }

    private String requestJson() {
        return """
                {"email":"usuario@gmail.com","name":"Usuario de Google","provider":"GOOGLE","providerId":"google-sub-1"}
                """;
    }

    private String authorization() {
        return "Basic " + HttpHeaders.encodeBasicAuth("test-service", "test-service-password", null);
    }
}

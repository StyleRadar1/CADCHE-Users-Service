package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.port.in.ConsultarCuentaAdministrativaUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ConsultarCuentasAdministrativasUseCase;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.BusquedaGuardadaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.PreferenciaEstiloJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.TallaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GestionEstadoCuentaEndpointTest {

    private static final String SECRET_BASE64 =
            "VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdTZWNyZXRLZXlGb3JUZXN0czEyMzQ1Ng==";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultarCuentasAdministrativasUseCase listarUseCase;
    @MockBean
    private ConsultarCuentaAdministrativaUseCase detalleUseCase;
    @MockBean
    private UsuarioJpaRepository usuarioJpaRepository;
    @MockBean
    private PreferenciaEstiloJpaRepository preferenciaEstiloJpaRepository;
    @MockBean
    private TallaJpaRepository tallaJpaRepository;
    @MockBean
    private BusquedaGuardadaJpaRepository busquedaGuardadaJpaRepository;

    @Test
    void adminPuedeListarCuentasConFiltrosYBarrio() throws Exception {
        var cuenta = new CuentaAdministrativaResponseDTO(12L, "Laura Gómez",
                "laura@example.com", "Chapinero", Set.of(Rol.COMPRADOR),
                EstadoCuenta.ACTIVA, Instant.parse("2026-11-10T10:00:00Z"),
                null, null, null);
        when(listarUseCase.ejecutar(EstadoCuenta.ACTIVA, Rol.COMPRADOR, "laura", 0, 20))
                .thenReturn(new PageResponseDTO<>(List.of(cuenta), 0, 20, 1L, 1, true, true));

        mockMvc.perform(get("/api/v1/admin/usuarios")
                        .param("estado", "ACTIVA")
                        .param("rol", "COMPRADOR")
                        .param("q", "laura")
                        .param("page", "0")
                        .param("size", "20")
                        .header("Authorization", "Bearer " + token("ADMIN_STYLERADAR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Laura Gómez"))
                .andExpect(jsonPath("$.content[0].barrio").value("Chapinero"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void usuarioSinRolAdminRecibe403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/usuarios")
                        .header("Authorization", "Bearer " + token("COMPRADOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPuedeConsultarElDetalleDeUnaCuenta() throws Exception {
        var cuenta = new CuentaAdministrativaResponseDTO(12L, "Laura Gómez",
                "laura@example.com", "Chapinero", Set.of(Rol.COMPRADOR),
                EstadoCuenta.ACTIVA, Instant.parse("2026-11-10T10:00:00Z"),
                null, null, null);
        when(detalleUseCase.ejecutar(12L)).thenReturn(cuenta);

        mockMvc.perform(get("/api/v1/admin/usuarios/12")
                        .header("Authorization", "Bearer " + token("ADMIN_STYLERADAR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laura Gómez"))
                .andExpect(jsonPath("$.barrio").value("Chapinero"));
    }

    @Test
    void detalleInexistenteDevuelve404() throws Exception {
        when(detalleUseCase.ejecutar(999L)).thenThrow(new UsuarioNoEncontradoException(999L));

        mockMvc.perform(get("/api/v1/admin/usuarios/999")
                        .header("Authorization", "Bearer " + token("ADMIN_STYLERADAR")))
                .andExpect(status().isNotFound());
    }

    private String token(String role) throws Exception {
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        long now = Instant.now().getEpochSecond();
        String payload = base64Url("{\"sub\":\"900\",\"roles\":[\"" + role
                + "\"],\"iat\":" + now + ",\"exp\":" + (now + 120) + "}");
        String unsignedToken = header + "." + payload;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(Base64.getDecoder().decode(SECRET_BASE64), "HmacSHA256"));
        byte[] signature = mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8));
        return unsignedToken + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}

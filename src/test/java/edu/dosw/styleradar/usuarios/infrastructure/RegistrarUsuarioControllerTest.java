package edu.dosw.styleradar.usuarios.infrastructure;

import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrarUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @MockBean
    private UsuarioJpaRepository usuarioJpaRepository;

    @Test
    void debeRegistrarUsuarioYResponder201() throws Exception {

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(1L);
        response.setNombre("Laura Gómez");
        response.setEmail("laura@gmail.com");
        response.setTelefono("+57 300 123 4567");
        response.setFechaRegistro(Instant.now());

        when(registrarUsuarioUseCase.registrar(any()))
                .thenReturn(response);

        String request = """
                {
                    "nombre": "Laura Gómez",
                    "email": "laura@gmail.com",
                    "telefono": "+57 300 123 4567",
                    "password": "StyleRadar2026*",
                    "aceptaHabeasData": true
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("laura@gmail.com"));

        verify(registrarUsuarioUseCase).registrar(any());
    }

    @Test
    void noDebeRegistrarCorreoDuplicadoYResponder409() throws Exception {

        when(registrarUsuarioUseCase.registrar(any()))
                .thenThrow(new EmailYaRegistradoException("laura@gmail.com"));

        String request = """
                {
                    "nombre": "Laura Gómez",
                    "email": "laura@gmail.com",
                    "telefono": "+57 300 123 4567",
                    "password": "StyleRadar2026*",
                    "aceptaHabeasData": true
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("El correo electrónico laura@gmail.com ya se encuentra registrado"));
    }

    @Test
    void noDebeAceptarCorreoInvalidoYResponder400() throws Exception {

        String request = """
                {
                    "nombre": "Laura Gómez",
                    "email": "correo-invalido",
                    "telefono": "+57 300 123 4567",
                    "password": "StyleRadar2026*",
                    "aceptaHabeasData": true
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(registrarUsuarioUseCase, never()).registrar(any());
    }

    @Test
    void noDebeAceptarConsentimientoFalsoYResponder400() throws Exception {

        String request = """
                {
                    "nombre": "Laura Gómez",
                    "email": "laura@gmail.com",
                    "telefono": "+57 300 123 4567",
                    "password": "StyleRadar2026*",
                    "aceptaHabeasData": false
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(registrarUsuarioUseCase, never()).registrar(any());
    }

    @Test
    void noDebeAceptarConsentimientoAusenteYResponder400() throws Exception {

        String request = """
                {
                    "nombre": "Laura Gómez",
                    "email": "laura@gmail.com",
                    "telefono": "+57 300 123 4567",
                    "password": "StyleRadar2026*"
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verify(registrarUsuarioUseCase, never()).registrar(any());
    }
}

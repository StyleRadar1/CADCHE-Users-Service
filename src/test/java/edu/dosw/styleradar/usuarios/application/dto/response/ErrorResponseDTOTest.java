package edu.dosw.styleradar.usuarios.application.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ErrorResponseDTOTest {

    @Test
    void conservaAliasesInternosYValorPredeterminadoDelContrato() {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .title("Bad Request")
                .status(400)
                .detail("Datos inválidos")
                .instance("/api/v1/users")
                .code("INVALID_DATA")
                .errors(Map.of("email", "Formato inválido"))
                .build();

        assertEquals("about:blank", response.getType());
        assertEquals("Datos inválidos", response.getMessage());
        assertEquals("/api/v1/users", response.getPath());
        assertEquals(Map.of("email", "Formato inválido"), response.getValidationErrors());
        assertEquals("Bad Request", response.getError());
    }

    @Test
    void permiteReemplazarElTipoPredeterminado() {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .type("https://api.styleradar.com/errors/invalid-data")
                .build();

        assertEquals("https://api.styleradar.com/errors/invalid-data", response.getType());
        assertNull(response.getDetail());
    }
}

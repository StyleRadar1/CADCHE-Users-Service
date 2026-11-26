package edu.dosw.styleradar.usuarios.application.dto.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(name = "ApiError", description = "Problem response with stable code and validation errors")
public class ErrorResponseDTO {

    @Schema(example = "https://api.styleradar.com/errors/invalid-data")
    @Builder.Default
    String type = "https://api.styleradar.com/errors/internal-error";

    @Schema(example = "Bad Request")
    String title;

    @Schema(example = "400")
    int status;

    @Schema(example = "La solicitud contiene datos inválidos")
    String detail;

    @Schema(example = "/api/v1/users")
    String instance;

    @Schema(example = "INVALID_DATA")
    String code;

    @Schema(example = "{\"email\":\"El formato no es válido\"}")
    Map<String, String> errors;

    /** Alias internos para mantener compatibilidad con consumidores Java existentes. */
    public String getMessage() {
        return detail;
    }

    @JsonIgnore
    public String getPath() {
        return instance;
    }

    @JsonIgnore
    public Map<String, String> getValidationErrors() {
        return errors;
    }

    @JsonIgnore
    public String getError() {
        return title;
    }
}

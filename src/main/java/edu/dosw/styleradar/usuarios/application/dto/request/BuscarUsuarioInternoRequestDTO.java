package edu.dosw.styleradar.usuarios.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "InternalUserLookupRequest")
public record BuscarUsuarioInternoRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email es inválido")
        @JsonProperty("email")
        @Schema(example = "user@example.com")
        String email) {
}

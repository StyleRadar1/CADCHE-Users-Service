package edu.dosw.styleradar.usuarios.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "UpdateUserProfileRequest", description = "Editable user profile data")
public record ActualizarDatosUsuarioRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @JsonProperty("name")
        @JsonAlias("nombre")
        @Schema(example = "Laura Gómez", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Pattern(regexp = "^[+0-9\\-\\s()]{7,20}$", message = "El formato del teléfono es inválido")
        @JsonProperty("phone")
        @JsonAlias("telefono")
        @Schema(example = "+57 300 123 4567")
        String phone,

        @Size(max = 100, message = "El barrio no puede superar 100 caracteres")
        @JsonProperty("neighborhood")
        @JsonAlias("barrio")
        @Schema(example = "Chapinero")
        String neighborhood) {
}

package edu.dosw.styleradar.usuarios.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CambiarRolUsuarioRequestDTO(
        @NotBlank(message = "El rol es obligatorio")
        @Pattern(regexp = "^(COMPRADOR|ADMIN_ALMACEN|REPRESENTANTE_FUNDACION|ADMIN_STYLERADAR)$",
                message = "El rol indicado no es válido")
        @JsonProperty("role")
        @Schema(description = "Rol permitido para la cuenta", example = "ADMIN_ALMACEN")
        String role) {
}

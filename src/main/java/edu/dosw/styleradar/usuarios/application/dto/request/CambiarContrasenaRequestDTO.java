package edu.dosw.styleradar.usuarios.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import edu.dosw.styleradar.usuarios.application.validation.PasswordPolicy;

@Schema(name = "ChangePasswordRequest")
public record CambiarContrasenaRequestDTO(
        @NotBlank(message = "La contraseña actual es obligatoria")
        @JsonProperty("currentPassword")
        @Schema(description = "Contraseña actual", example = "CurrentPass2026*")
        String currentPassword,

        @NotBlank(message = "La contraseña nueva es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @JsonProperty("newPassword")
        @Pattern(regexp = PasswordPolicy.STRONG_PASSWORD_REGEX,
                message = "Debe incluir mayúscula, minúscula, número y símbolo")
        @Schema(description = "Nueva contraseña", example = "NewPass2026*")
        String newPassword) {
}

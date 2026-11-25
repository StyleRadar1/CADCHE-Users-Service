package edu.dosw.styleradar.usuarios.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.dosw.styleradar.usuarios.application.validation.PasswordPolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "InternalPasswordResetRequest")
public record RestablecerContrasenaInternaRequestDTO(
        @NotBlank(message = "La contraseña nueva es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(regexp = PasswordPolicy.STRONG_PASSWORD_REGEX,
                message = "Debe incluir mayúscula, minúscula, número y símbolo")
        @JsonProperty("newPassword")
        String newPassword) {
}

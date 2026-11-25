package edu.dosw.styleradar.usuarios.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.dosw.styleradar.usuarios.application.validation.CredentialComplexityPolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "InternalPasswordResetRequest")
public record RestablecerContrasenaInternaRequestDTO(
        @NotBlank(message = "La contraseña nueva es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(regexp = CredentialComplexityPolicy.COMPLEXITY_PATTERN,
                message = "Debe incluir mayúscula, minúscula, número y símbolo")
        @JsonProperty("newPassword")
        String newPassword) {
}

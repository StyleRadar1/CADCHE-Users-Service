package edu.dosw.styleradar.usuarios.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "GoogleUserRegistrationRequest", description = "Google identity validated by ms-auth")
public record RegistrarUsuarioGoogleRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email es inválido")
        @Size(max = 255, message = "El email no puede superar 255 caracteres")
        @Schema(example = "usuario@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Nombre del usuario", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @NotBlank(message = "El proveedor es obligatorio")
        @Pattern(regexp = "GOOGLE", message = "El proveedor permitido es GOOGLE")
        @Schema(example = "GOOGLE", requiredMode = Schema.RequiredMode.REQUIRED)
        String provider,

        @NotBlank(message = "El identificador del proveedor es obligatorio")
        @Size(max = 255, message = "El identificador no puede superar 255 caracteres")
        @Schema(example = "identificador-sub-de-google", requiredMode = Schema.RequiredMode.REQUIRED)
        String providerId) {
}

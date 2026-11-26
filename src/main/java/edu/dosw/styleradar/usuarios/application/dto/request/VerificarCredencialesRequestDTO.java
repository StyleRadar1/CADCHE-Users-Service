package edu.dosw.styleradar.usuarios.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@io.swagger.v3.oas.annotations.media.Schema(name = "VerifyCredentialsRequest")
public record VerificarCredencialesRequestDTO(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}

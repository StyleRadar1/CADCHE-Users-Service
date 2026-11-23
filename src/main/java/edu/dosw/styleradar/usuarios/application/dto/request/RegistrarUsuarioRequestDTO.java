package edu.dosw.styleradar.usuarios.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para registrar un usuario comprador")
public class RegistrarUsuarioRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100,
            message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre completo",
            example = "Laura Gómez",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    @JsonAlias("nombre")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email es inválido")
    @Schema(description = "Correo electrónico",
            example = "laura.gomez@styleradar.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("email")
    private String email;

    @Pattern(regexp = "^[+0-9\\-\\s()]{7,20}$",
            message = "El formato del teléfono es inválido")
    @Schema(description = "Número telefónico opcional",
            example = "+57 300 123 4567")
    @JsonProperty("phone")
    @JsonAlias("telefono")
    private String telefono;

    @Size(max = 100, message = "El barrio no puede superar 100 caracteres")
    @Schema(description = "Barrio de residencia", example = "Chapinero")
    @JsonProperty("neighborhood")
    @JsonAlias("barrio")
    private String barrio;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72,
            message = "La contraseña debe tener entre 8 y 72 caracteres")
    @Schema(description = "Contraseña de acceso",
            example = "StyleRadar2026*",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotNull(message = "Debe indicar si acepta el tratamiento de datos")
    @AssertTrue(message = "Debe aceptar el tratamiento de datos personales (Habeas Data)")
    @Schema(description = "Consentimiento expreso de tratamiento de datos personales",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("acceptsDataProcessing")
    @JsonAlias("aceptaHabeasData")
    private Boolean aceptaHabeasData;

    @Schema(description = "Estatura en centímetros", example = "175.0")
    private Double heightCm;

    @Schema(description = "Contorno de pecho en centímetros", example = "96.0")
    private Double chestCm;

    @Schema(description = "Contorno de cintura en centímetros", example = "82.0")
    private Double waistCm;

    @Schema(description = "Contorno de cadera en centímetros", example = "98.0")
    private Double hipCm;
}

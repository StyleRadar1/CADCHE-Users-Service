package edu.dosw.styleradar.usuarios.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Minimal and public user profile response for service-to-service communication. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Perfil público mínimo y seguro de un usuario")
public class PublicProfileResponseDTO {

    @Schema(description = "Identificador único del usuario", example = "7")
    private Long id;

    @Schema(description = "Nombre público corto del usuario (ej: Sofía L.)", example = "Sofía L.")
    private String displayName;
}

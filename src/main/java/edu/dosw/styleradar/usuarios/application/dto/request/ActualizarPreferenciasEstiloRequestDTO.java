package edu.dosw.styleradar.usuarios.application.dto.request;

import java.util.Set;

import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para actualizar las preferencias de estilo")
public class ActualizarPreferenciasEstiloRequestDTO {

    @NotEmpty(message = "Debe seleccionar al menos un estilo")
    @Schema(
            description = "Estilos preferidos del comprador",
            example = "[\"CASUAL\", \"URBANO\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Set<Estilo> preferencias;
}


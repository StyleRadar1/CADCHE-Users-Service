package edu.dosw.styleradar.usuarios.application.dto.request;

import java.util.Set;

import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para reemplazar las preferencias de estilo del comprador autenticado (SR-RF-03)")
public class ActualizarPreferenciasEstiloRequestDTO {

    @NotNull(message = "El conjunto de preferencias de estilo es obligatorio")
    @Schema(description = "Conjunto de estilos preferidos seleccionados por el comprador", example = "[\"CASUAL\",\"URBANO\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<Estilo> preferencias;
}


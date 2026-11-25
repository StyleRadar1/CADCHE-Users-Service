package edu.dosw.styleradar.usuarios.application.dto.request;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(name = "UpdateStylePreferencesRequest", description = "User style preferences")
public class ActualizarPreferenciasEstiloRequestDTO {

    @NotEmpty(message = "Debe seleccionar al menos un estilo")
    @Schema(
            description = "Estilos preferidos del comprador",
            example = "[\"CASUAL\", \"URBAN\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("styles")
    @JsonAlias({"stylePreferences", "preferencias", "preferenciasEstilo"})
    private Set<Estilo> preferencias;
}


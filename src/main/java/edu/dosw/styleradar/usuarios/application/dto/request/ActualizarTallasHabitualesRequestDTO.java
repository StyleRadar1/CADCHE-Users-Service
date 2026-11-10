package edu.dosw.styleradar.usuarios.application.dto.request;

import java.util.Map;
import java.util.Set;

import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para configurar las tallas habituales por tipo de prenda")
public class ActualizarTallasHabitualesRequestDTO {

    @Schema(description = "Tallas alfabéticas habituales", example = "[\"M\",\"L\"]")
    private Set<Talla> tallas;

    @NotNull(message = "Las tallas por tipo de prenda son obligatorias")
    @Schema(
            description = "Tallas agrupadas por tipo de prenda",
            example = "{\"SUPERIOR\":[\"M\"],\"INFERIOR\":[\"32\"],\"CALZADO\":[\"40\"]}",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Map<TipoPrenda, Set<String>> tallasPorTipoPrenda;

    @AssertTrue(message = "Una o más tallas no corresponden al tipo de prenda")
    public boolean isTallasValidas() {
        if (tallasPorTipoPrenda == null) {
            return true;
        }

        for (Map.Entry<TipoPrenda, Set<String>> entrada : tallasPorTipoPrenda.entrySet()) {
            if (entrada.getValue() == null || entrada.getValue().isEmpty()) {
                return false;
            }

            for (String talla : entrada.getValue()) {
                if (!Talla.esValidaPara(entrada.getKey(), talla)) {
                    return false;
                }
            }
        }

        return true;
    }
}

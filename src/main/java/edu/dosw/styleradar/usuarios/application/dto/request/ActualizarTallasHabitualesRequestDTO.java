package edu.dosw.styleradar.usuarios.application.dto.request;

import java.util.Map;
import java.util.Set;

import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
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
@Schema(description = "Solicitud para reemplazar las tallas habituales del comprador autenticado general y por tipo de prenda (SR-RF-04)")
public class ActualizarTallasHabitualesRequestDTO {

    @NotNull(message = "El conjunto de tallas habituales es obligatorio")
    @Schema(description = "Conjunto de tallas alfabéticas habituales registradas por el comprador", example = "[\"M\",\"L\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<Talla> tallas;

    @Schema(description = "Mapa opcional de tallas habituales clasificadas por tipo de prenda: esquemas alfabéticos (XS..XXL) para prendas superiores/inferiores y numérico (35..45) para calzado (SR-RF-04)",
            example = "{\"SUPERIOR\":[\"M\"],\"INFERIOR\":[\"L\"],\"CALZADO\":[\"40\"]}")
    private Map<TipoPrenda, Set<String>> tallasPorTipoPrenda;
}

package edu.dosw.styleradar.usuarios.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Medidas corporales del usuario expresadas en centímetros")
public class MedidasCorporalesResponseDTO {

    @Schema(description = "Estatura en centímetros", example = "175.0")
    private Double heightCm;

    @Schema(description = "Contorno de pecho en centímetros", example = "96.0")
    private Double chestCm;

    @Schema(description = "Contorno de cintura en centímetros", example = "82.0")
    private Double waistCm;

    @Schema(description = "Contorno de cadera en centímetros", example = "98.0")
    private Double hipCm;
}


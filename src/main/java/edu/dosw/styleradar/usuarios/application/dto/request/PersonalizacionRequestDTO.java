package edu.dosw.styleradar.usuarios.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Parámetros de paginación para consultar el feed personalizado y recomendaciones (SR-RF-05 / SR-RF-06)")
public class PersonalizacionRequestDTO {

    @NotNull
    @Min(0)
    @Schema(description = "Número de página de base cero", example = "0", defaultValue = "0")
    private Integer page = 0;

    @NotNull
    @Min(1)
    @Max(20)
    @Schema(
            description = "Cantidad de elementos por página (entre 1 y 20)",
            example = "20",
            defaultValue = "20"
    )
    private Integer size = 20;
}

package edu.dosw.styleradar.usuarios.application.dto.response;

import java.time.Instant;

import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Búsqueda guardada en el perfil del comprador con texto, filtros y ubicación (SR-RF-28)")
public class BusquedaGuardadaResponseDTO {

    @Schema(description = "Identificador único de la búsqueda guardada", example = "5")
    private Long id;

    @Schema(description = "Nombre descriptivo de la búsqueda", example = "Chaquetas negras casuales")
    private String nombre;

    @Schema(description = "Texto libre utilizado en la búsqueda", example = "chaqueta denim")
    private String query;

    @Schema(description = "Tipo de prenda filtrado", example = "SUPERIOR")
    private TipoPrenda tipo;

    @Schema(description = "Color filtrado", example = "Negro")
    private String color;

    @Schema(description = "Talla filtrada", example = "M")
    private Talla talla;

    @Schema(description = "Precio mínimo del rango en COP", example = "50000.0")
    private Double precioMin;

    @Schema(description = "Precio máximo del rango en COP", example = "180000.0")
    private Double precioMax;

    @Schema(description = "Marca filtrada", example = "Levi's")
    private String marca;

    @Schema(description = "Estilo filtrado", example = "CASUAL")
    private Estilo estilo;

    @Schema(description = "Latitud de la ubicación asociada a la búsqueda (SR-RF-28)", example = "4.6761")
    private Double latitud;

    @Schema(description = "Longitud de la ubicación asociada a la búsqueda (SR-RF-28)", example = "-74.0482")
    private Double longitud;

    @Schema(description = "Fecha y hora UTC en que se guardó la búsqueda", example = "2026-11-03T01:35:00Z")
    private Instant fechaCreacion;
}

package edu.dosw.styleradar.usuarios.application.dto.response;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.dosw.styleradar.usuarios.application.dto.BusquedaFiltrosDTO;
import edu.dosw.styleradar.usuarios.application.dto.serialization.ApiEnumSerializer;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@Schema(name = "SavedSearchResponse", description = "Saved search criteria and location")
public class BusquedaGuardadaResponseDTO extends BusquedaFiltrosDTO {

    @Schema(description = "Identificador único de la búsqueda guardada", example = "5")
    private Long id;

    @Schema(description = "Nombre descriptivo de la búsqueda", example = "Chaquetas negras casuales")
    @JsonProperty("name") @JsonAlias("nombre")
    private String nombre;

    @Schema(description = "Filtered garment type", example = "TOP")
    @JsonProperty("garmentType") @JsonAlias("tipo")
    @JsonSerialize(using = ApiEnumSerializer.class)
    private TipoPrenda tipo;

    @Schema(description = "Color filtrado", example = "Negro")
    private String color;

    @Schema(description = "Talla filtrada", example = "M")
    @JsonProperty("size") @JsonAlias("talla")
    private Talla talla;

    @Schema(description = "Precio mínimo del rango en COP", example = "50000.0")
    @JsonProperty("minPrice") @JsonAlias("precioMin")
    private Double precioMin;

    @Schema(description = "Precio máximo del rango en COP", example = "180000.0")
    @JsonProperty("maxPrice") @JsonAlias("precioMax")
    private Double precioMax;

    @Schema(description = "Latitud de la ubicación asociada a la búsqueda (SR-RF-28)", example = "4.6761")
    @JsonProperty("latitude") @JsonAlias("latitud")
    private Double latitud;

    @Schema(description = "Longitud de la ubicación asociada a la búsqueda (SR-RF-28)", example = "-74.0482")
    @JsonProperty("longitude") @JsonAlias("longitud")
    private Double longitud;

    @Schema(description = "Fecha y hora UTC en que se guardó la búsqueda", example = "2026-11-03T01:35:00Z")
    @JsonProperty("createdAt") @JsonAlias("fechaCreacion")
    private Instant fechaCreacion;
}

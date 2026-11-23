package edu.dosw.styleradar.usuarios.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.dosw.styleradar.usuarios.application.dto.serialization.ApiEnumSerializer;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** Shared public search fields used by saved-search request and response DTOs. */
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class BusquedaFiltrosDTO {

    @Schema(description = "Free-text search query", example = "chaqueta denim")
    @JsonProperty("query")
    @JsonAlias("searchText")
    protected String query;

    @Schema(description = "Filtered brand", example = "Levi's")
    @JsonProperty("brand")
    @JsonAlias("marca")
    protected String marca;

    @Schema(description = "Filtered style", example = "CASUAL")
    @JsonProperty("style")
    @JsonAlias("estilo")
    @JsonSerialize(using = ApiEnumSerializer.class)
    protected Estilo estilo;
}

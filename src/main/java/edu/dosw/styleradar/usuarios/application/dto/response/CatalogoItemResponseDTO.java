package edu.dosw.styleradar.usuarios.application.dto.response;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.dosw.styleradar.usuarios.application.dto.serialization.ApiEnumSerializer;
import edu.dosw.styleradar.usuarios.domain.model.EstadoItem;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
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
@Schema(description = "Ítem de catálogo proyectado dentro del feed personalizado o recomendaciones del usuario (SR-RF-05 / SR-RF-06)")
public class CatalogoItemResponseDTO {

    @Schema(description = "Identificador UUID de la publicación en el catálogo", example = "3f6c1a2e-8b1d-4c7a-9f2e-1a2b3c4d5e6f")
    @JsonProperty("itemId")
    private String itemId;

    @Schema(description = "Identificador de la prenda base", example = "45")
    @JsonProperty("garmentId") @JsonAlias("prendaId")
    private Long prendaId;

    @Schema(description = "NIT del almacén aliado propietario", example = "900123456-1")
    @JsonProperty("storeNit") @JsonAlias({"storeTaxId", "almacenNit"})
    private String almacenNit;

    @Schema(description = "Identificador del almacén propietario", example = "9a8b7c6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d")
    @JsonProperty("storeId") @JsonAlias("almacenId")
    private String almacenId;

    @Schema(description = "Nombre comercial de la prenda", example = "Chaqueta Denim Oversize")
    @JsonProperty("name") @JsonAlias("nombre")
    private String nombre;

    @Schema(description = "Descripción detallada de la prenda", example = "Chaqueta de mezclilla negra con corte urbano")
    @JsonProperty("description") @JsonAlias("descripcion")
    private String descripcion;

    @Schema(description = "Garment type or category", example = "TOP")
    @JsonProperty("garmentType") @JsonAlias("tipo")
    @JsonSerialize(using = ApiEnumSerializer.class)
    private TipoPrenda tipo;

    @Schema(description = "Marca de la prenda", example = "Levi's")
    @JsonProperty("brand") @JsonAlias("marca")
    private String marca;

    @Schema(description = "Color principal de la prenda", example = "Negro")
    private String color;

    @Schema(description = "Main style of the garment", example = "URBAN")
    @JsonProperty("style") @JsonAlias("estilo")
    @JsonSerialize(using = ApiEnumSerializer.class)
    private Estilo estilo;

    @Schema(description = "Precio vigente en COP", example = "159900.0")
    @JsonProperty("price") @JsonAlias("precio")
    private Double precio;

    @Schema(description = "Unidades disponibles agregadas", example = "8")
    @JsonProperty("stock")
    private Integer stock;

    @Schema(description = "Estado operativo del ítem en catálogo", example = "DISPONIBLE")
    @JsonProperty("status") @JsonAlias("estado")
    @JsonSerialize(using = ApiEnumSerializer.class)
    private EstadoItem estado;

    @Schema(description = "Fecha y hora UTC de publicación", example = "2026-11-01T14:00:00Z")
    @JsonProperty("publishedAt") @JsonAlias("fechaPublicacion")
    private Instant fechaPublicacion;

    @Schema(description = "Fotografías de la prenda")
    @JsonProperty("photos") @JsonAlias("fotografias")
    private List<String> fotografias;

    @Schema(description = "Tallas con disponibilidad activa", example = "[\"S\",\"M\",\"42\"]")
    @JsonProperty("availableSizes") @JsonAlias("tallasDisponibles")
    private List<String> tallasDisponibles;
}


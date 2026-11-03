package edu.dosw.styleradar.usuarios.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Criterios de búsqueda (texto, filtros y ubicación) para almacenar en el perfil del comprador (SR-RF-05 / SR-RF-28)")
public class GuardarBusquedaRequestDTO {

    @NotBlank(message = "El nombre de la búsqueda guardada es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    @Schema(description = "Nombre descriptivo asignado por el usuario a la búsqueda", example = "Chaquetas negras casuales", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Pattern(regexp = "^.*\\S.*$", message = "query no puede estar vacío")
    @Schema(description = "Texto libre de búsqueda", example = "chaqueta denim")
    private String query;

    @Schema(description = "Tipo o categoría de prenda filtrada", example = "SUPERIOR")
    private TipoPrenda tipo;

    @Pattern(regexp = "^.*\\S.*$", message = "color no puede estar vacío")
    @Schema(description = "Color filtrado en la búsqueda", example = "Negro")
    private String color;

    @Schema(description = "Talla filtrada en la búsqueda", example = "M")
    private Talla talla;

    @DecimalMin(value = "0.0", inclusive = true, message = "precioMin debe ser mayor o igual a 0")
    @Schema(description = "Precio mínimo del rango en COP", example = "50000.0")
    private Double precioMin;

    @DecimalMin(value = "0.0", inclusive = true, message = "precioMax debe ser mayor o igual a 0")
    @Schema(description = "Precio máximo del rango en COP", example = "180000.0")
    private Double precioMax;

    @Pattern(regexp = "^.*\\S.*$", message = "marca no puede estar vacía")
    @Schema(description = "Marca filtrada en la búsqueda", example = "Levi's")
    private String marca;

    @Schema(description = "Estilo filtrado en la búsqueda", example = "CASUAL")
    private Estilo estilo;

    @DecimalMin(value = "-90.0", message = "latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "latitud debe estar entre -90 y 90")
    @Schema(description = "Latitud de la ubicación utilizada en la búsqueda (SR-RF-28)", example = "4.6761")
    private Double latitud;

    @DecimalMin(value = "-180.0", message = "longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "longitud debe estar entre -180 y 180")
    @Schema(description = "Longitud de la ubicación utilizada en la búsqueda (SR-RF-28)", example = "-74.0482")
    private Double longitud;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "precioMin no puede ser mayor que precioMax")
    public boolean isRangoPrecioValido() {
        return precioMin == null || precioMax == null || precioMin <= precioMax;
    }
}

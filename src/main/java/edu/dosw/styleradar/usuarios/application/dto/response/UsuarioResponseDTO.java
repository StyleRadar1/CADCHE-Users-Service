package edu.dosw.styleradar.usuarios.application.dto.response;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

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
@Schema(description = "Representación del perfil de usuario comprador, sus preferencias de estilo y tallas habituales por tipo de prenda")
public class UsuarioResponseDTO {

    @Schema(description = "Identificador único del usuario", example = "12")
    private Long id;

    @Schema(description = "Nombre completo del usuario", example = "Laura Gómez")
    private String nombre;

    @Schema(description = "Correo electrónico registrado", example = "laura.gomez@styleradar.com")
    private String email;

    @Schema(description = "Número telefónico de contacto", example = "+57 300 123 4567")
    private String telefono;

    @Schema(description = "Barrio de residencia", example = "Chapinero")
    private String barrio;

    @Schema(description = "Indica si el correo electrónico fue verificado", example = "true")
    private Boolean correoVerificado;

    @Schema(description = "Fecha y hora UTC de registro en la plataforma", example = "2026-11-03T01:30:00Z")
    private Instant fechaRegistro;

    @Schema(description = "Preferencias de estilo configuradas por el usuario", example = "[\"CASUAL\",\"URBANO\"]")
    private Set<Estilo> preferenciasEstilo;

    @Schema(description = "Tallas habituales configuradas por el usuario", example = "[\"M\",\"L\"]")
    private Set<Talla> tallasHabituales;

    @Schema(description = "Tallas habituales agrupadas por tipo de prenda",
            example = "{\"SUPERIOR\":[\"M\"],\"INFERIOR\":[\"L\"],\"CALZADO\":[\"40\"]}")
    private Map<TipoPrenda, Set<String>> tallasPorTipoPrenda;
}

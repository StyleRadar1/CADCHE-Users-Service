package edu.dosw.styleradar.usuarios.application.dto.response;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.dosw.styleradar.usuarios.application.dto.serialization.ApiEnumSerializer;
import edu.dosw.styleradar.usuarios.application.dto.serialization.ApiEnumKeySerializer;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserResponse", description = "User profile, style preferences and usual sizes")
public class UsuarioResponseDTO {

    @Schema(description = "Identificador único del usuario", example = "12")
    private Long id;

    @Schema(description = "Nombre completo del usuario", example = "Laura Gómez")
    @JsonProperty("name")
    @JsonAlias("nombre")
    private String nombre;

    @Schema(description = "Correo electrónico registrado", example = "laura.gomez@styleradar.com")
    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    @JsonAlias("rol")
    @JsonSerialize(using = ApiEnumSerializer.class)
    private Rol rol;

    @JsonProperty("status")
    @JsonAlias("estado")
    @JsonSerialize(using = ApiEnumSerializer.class)
    private EstadoCuenta estado;

    @Schema(description = "Número telefónico de contacto", example = "+57 300 123 4567")
    @JsonProperty("phone")
    @JsonAlias("telefono")
    private String telefono;

    @Schema(description = "Barrio de residencia", example = "Chapinero")
    @JsonProperty("neighborhood")
    @JsonAlias("barrio")
    private String barrio;

    @Schema(description = "Indica si el correo electrónico fue verificado", example = "true")
    @JsonProperty("emailVerified")
    @JsonAlias("correoVerificado")
    private Boolean correoVerificado;

    @Schema(description = "Fecha y hora UTC de registro en la plataforma", example = "2026-11-03T01:30:00Z")
    @JsonProperty("registeredAt")
    @JsonAlias("fechaRegistro")
    private Instant fechaRegistro;

    @Schema(description = "Preferencias de estilo configuradas por el usuario", example = "[\"CASUAL\",\"URBANO\"]")
    @JsonProperty("stylePreferences")
    @JsonAlias("preferenciasEstilo")
    @JsonSerialize(contentUsing = ApiEnumSerializer.class)
    private Set<Estilo> preferenciasEstilo;

    @Schema(description = "Tallas habituales configuradas por el usuario", example = "[\"M\",\"L\"]")
    @JsonProperty("usualSizes")
    @JsonAlias("tallasHabituales")
    private Set<Talla> tallasHabituales;

    @Schema(description = "Tallas habituales agrupadas por tipo de prenda",
            example = "{\"SUPERIOR\":[\"M\"],\"INFERIOR\":[\"L\"],\"CALZADO\":[\"40\"]}")
    @JsonProperty("sizesByGarmentType")
    @JsonAlias("tallasPorTipoPrenda")
    @JsonSerialize(keyUsing = ApiEnumKeySerializer.class)
    private Map<TipoPrenda, Set<String>> tallasPorTipoPrenda;
}

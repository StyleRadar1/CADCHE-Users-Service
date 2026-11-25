package edu.dosw.styleradar.usuarios.application.dto.response;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.dosw.styleradar.usuarios.application.dto.serialization.ApiEnumSerializer;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AdminUserResponse", description = "Administrative account status and audit details")
public record CuentaAdministrativaResponseDTO(
        @Schema(description = "Identificador único de la cuenta", example = "12")
        Long id,

        @Schema(description = "Nombre del titular de la cuenta", example = "Laura Gómez")
        @JsonProperty("name") @JsonAlias("nombre") String nombre,

        @Schema(description = "Correo electrónico registrado", example = "laura.gomez@styleradar.com")
        String email,

        @Schema(description = "Barrio de residencia", example = "Chapinero")
        @JsonProperty("neighborhood") @JsonAlias("barrio") String barrio,

        @Schema(description = "Roles assigned to the account", example = "[\"BUYER\"]")
        @JsonProperty("roles") @JsonSerialize(contentUsing = ApiEnumSerializer.class) Set<Rol> roles,

        @Schema(description = "Effective account status", example = "ACTIVE")
        @JsonProperty("accountStatus") @JsonAlias({"status", "estadoCuenta"}) @JsonSerialize(using = ApiEnumSerializer.class) EstadoCuenta estadoCuenta,

        @Schema(description = "Fecha y hora UTC de registro original", example = "2026-10-15T10:00:00Z")
        @JsonProperty("registeredAt") @JsonAlias("fechaRegistro") Instant fechaRegistro,

        @Schema(description = "Fecha y hora UTC de la última decisión administrativa sobre el estado", example = "2026-11-02T18:30:00Z")
        @JsonProperty("statusChangedAt") @JsonAlias("fechaCambioEstado") Instant fechaCambioEstado,

        @Schema(description = "Identificador del administrador que ejecutó el último cambio de estado", example = "1")
        @JsonProperty("statusChangedBy") @JsonAlias("administradorCambioEstadoId") Long administradorCambioEstadoId,

        @Schema(description = "Motivo registrado en el último cambio de estado", example = "Revisión concluida")
        @JsonProperty("statusChangeReason") @JsonAlias("motivoCambioEstado") String motivoCambioEstado
) {
}


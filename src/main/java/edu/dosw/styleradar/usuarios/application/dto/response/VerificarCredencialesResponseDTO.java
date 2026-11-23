package edu.dosw.styleradar.usuarios.application.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.dosw.styleradar.usuarios.application.dto.serialization.ApiEnumSerializer;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;

public record VerificarCredencialesResponseDTO(
        Long id,
        String email,
        @JsonProperty("role") @JsonAlias("rol") @JsonSerialize(using = ApiEnumSerializer.class) Rol rol,
        @JsonProperty("status") @JsonAlias("estado") @JsonSerialize(using = ApiEnumSerializer.class) EstadoCuenta estado,
        @JsonProperty("emailVerified") @JsonAlias("correoVerificado") Boolean correoVerificado
) {
}

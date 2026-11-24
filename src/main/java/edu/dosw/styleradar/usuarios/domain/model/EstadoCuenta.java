package edu.dosw.styleradar.usuarios.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EstadoCuenta {
    ACTIVA,
    INACTIVA,
    BLOQUEADA;

    @JsonCreator
    public static EstadoCuenta fromContract(String value) {
        return valueOf(switch (value.toUpperCase(java.util.Locale.ROOT)) {
            case "ACTIVE" -> "ACTIVA";
            case "INACTIVE" -> "INACTIVA";
            case "BLOCKED" -> "BLOQUEADA";
            default -> value.toUpperCase(java.util.Locale.ROOT);
        });
    }
}


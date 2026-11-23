package edu.dosw.styleradar.usuarios.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Estilo {
    CASUAL,
    FORMAL,
    DEPORTIVO,
    URBANO,
    VINTAGE;

    @JsonCreator
    public static Estilo fromContract(String value) {
        return valueOf(switch (value.toUpperCase(java.util.Locale.ROOT)) {
            case "SPORTY" -> "DEPORTIVO";
            case "URBAN" -> "URBANO";
            default -> value.toUpperCase(java.util.Locale.ROOT);
        });
    }
}

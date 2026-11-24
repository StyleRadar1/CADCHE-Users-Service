package edu.dosw.styleradar.usuarios.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EstadoItem {
    DISPONIBLE,
    ULTIMAS_UNIDADES,
    NUEVA_PRENDA,
    AGOTADA;

    @JsonCreator
    public static EstadoItem fromContract(String value) {
        return valueOf(switch (value.toUpperCase(java.util.Locale.ROOT)) {
            case "AVAILABLE" -> "DISPONIBLE";
            case "LAST_UNITS" -> "ULTIMAS_UNIDADES";
            case "NEW_ITEM" -> "NUEVA_PRENDA";
            case "OUT_OF_STOCK" -> "AGOTADA";
            default -> value.toUpperCase(java.util.Locale.ROOT);
        });
    }
}


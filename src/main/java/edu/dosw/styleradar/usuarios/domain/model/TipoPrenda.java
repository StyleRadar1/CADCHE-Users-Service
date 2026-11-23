package edu.dosw.styleradar.usuarios.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoPrenda {
    SUPERIOR,
    INFERIOR,
    CALZADO;

    @JsonCreator
    public static TipoPrenda fromContract(String value) {
        return valueOf(switch (value.toUpperCase(java.util.Locale.ROOT)) {
            case "TOP" -> "SUPERIOR";
            case "BOTTOM" -> "INFERIOR";
            case "FOOTWEAR", "SHOES" -> "CALZADO";
            default -> value.toUpperCase(java.util.Locale.ROOT);
        });
    }
}


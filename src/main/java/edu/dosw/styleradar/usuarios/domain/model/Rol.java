package edu.dosw.styleradar.usuarios.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Rol {
    COMPRADOR,
    ADMIN_ALMACEN,
    REPRESENTANTE_FUNDACION,
    ADMIN_STYLERADAR;

    @JsonCreator
    public static Rol fromContract(String value) {
        return valueOf(switch (value.toUpperCase(java.util.Locale.ROOT)) {
            case "BUYER" -> "COMPRADOR";
            case "STORE_ADMIN" -> "ADMIN_ALMACEN";
            case "FOUNDATION_REPRESENTATIVE" -> "REPRESENTANTE_FUNDACION";
            case "STYLERADAR_ADMIN" -> "ADMIN_STYLERADAR";
            default -> value.toUpperCase(java.util.Locale.ROOT);
        });
    }
}


package edu.dosw.styleradar.usuarios.application.dto.serialization;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/** Serializa enums expuestos por DTOs HTTP con los valores del contrato público. */
public class ApiEnumSerializer extends JsonSerializer<Object> {

    private static final Map<String, String> VALUES = Map.ofEntries(
            Map.entry("COMPRADOR", "BUYER"),
            Map.entry("ADMIN_ALMACEN", "STORE_ADMIN"),
            Map.entry("REPRESENTANTE_FUNDACION", "FOUNDATION_REPRESENTATIVE"),
            Map.entry("ADMIN_STYLERADAR", "STYLERADAR_ADMIN"),
            Map.entry("ACTIVA", "ACTIVE"),
            Map.entry("INACTIVA", "INACTIVE"),
            Map.entry("BLOQUEADA", "BLOCKED"),
            Map.entry("DEPORTIVO", "SPORTY"),
            Map.entry("URBANO", "URBAN"),
            Map.entry("SUPERIOR", "TOP"),
            Map.entry("INFERIOR", "BOTTOM"),
            Map.entry("CALZADO", "FOOTWEAR"),
            Map.entry("DISPONIBLE", "AVAILABLE"),
            Map.entry("ULTIMAS_UNIDADES", "LAST_UNITS"),
            Map.entry("NUEVA_PRENDA", "NEW_ITEM"),
            Map.entry("AGOTADA", "OUT_OF_STOCK")
    );

    public static String valueOf(Object value) {
        String name = value instanceof Enum<?> enumValue
                ? enumValue.name()
                : value.toString();
        return VALUES.getOrDefault(name, name);
    }

    @Override
    public void serialize(Object value, JsonGenerator generator,
            SerializerProvider serializers) throws IOException {
        generator.writeString(valueOf(value));
    }
}

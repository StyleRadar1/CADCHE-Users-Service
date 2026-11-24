package edu.dosw.styleradar.usuarios.application.dto.serialization;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;

/** Serializa claves enum de mapas usando el vocabulario del contrato HTTP. */
public class ApiEnumKeySerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator generator,
            SerializerProvider serializers) throws IOException {
        generator.writeFieldName(ApiEnumSerializer.valueOf(value));
    }
}

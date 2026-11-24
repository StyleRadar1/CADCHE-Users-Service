package edu.dosw.styleradar.usuarios.application.dto.serialization;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import java.io.IOException;

public class TipoPrendaKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext context)
            throws IOException {
        return TipoPrenda.fromContract(key);
    }
}

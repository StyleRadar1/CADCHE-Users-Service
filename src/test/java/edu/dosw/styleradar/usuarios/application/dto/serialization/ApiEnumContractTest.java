package edu.dosw.styleradar.usuarios.application.dto.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonGenerator;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.EstadoItem;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import org.junit.jupiter.api.Test;

class ApiEnumContractTest {

    @Test
    void convierteValoresPublicosEnInglesAlModeloDeDominio() {
        assertEquals(Estilo.DEPORTIVO, Estilo.fromContract("SPORTY"));
        assertEquals(Estilo.URBANO, Estilo.fromContract("urban"));
        assertEquals(Estilo.CASUAL, Estilo.fromContract("CASUAL"));

        assertEquals(TipoPrenda.SUPERIOR, TipoPrenda.fromContract("TOP"));
        assertEquals(TipoPrenda.INFERIOR, TipoPrenda.fromContract("BOTTOM"));
        assertEquals(TipoPrenda.CALZADO, TipoPrenda.fromContract("FOOTWEAR"));
        assertEquals(TipoPrenda.CALZADO, TipoPrenda.fromContract("SHOES"));
        assertEquals(TipoPrenda.SUPERIOR, TipoPrenda.fromContract("SUPERIOR"));

        assertEquals(Rol.COMPRADOR, Rol.fromContract("BUYER"));
        assertEquals(Rol.ADMIN_ALMACEN, Rol.fromContract("STORE_ADMIN"));
        assertEquals(Rol.REPRESENTANTE_FUNDACION,
                Rol.fromContract("FOUNDATION_REPRESENTATIVE"));
        assertEquals(Rol.ADMIN_STYLERADAR, Rol.fromContract("STYLERADAR_ADMIN"));
        assertEquals(Rol.COMPRADOR, Rol.fromContract("COMPRADOR"));

        assertEquals(EstadoCuenta.ACTIVA, EstadoCuenta.fromContract("ACTIVE"));
        assertEquals(EstadoCuenta.INACTIVA, EstadoCuenta.fromContract("INACTIVE"));
        assertEquals(EstadoCuenta.BLOQUEADA, EstadoCuenta.fromContract("BLOCKED"));
        assertEquals(EstadoCuenta.ACTIVA, EstadoCuenta.fromContract("ACTIVA"));

        assertEquals(EstadoItem.DISPONIBLE, EstadoItem.fromContract("AVAILABLE"));
        assertEquals(EstadoItem.ULTIMAS_UNIDADES, EstadoItem.fromContract("LAST_UNITS"));
        assertEquals(EstadoItem.NUEVA_PRENDA, EstadoItem.fromContract("NEW_ITEM"));
        assertEquals(EstadoItem.AGOTADA, EstadoItem.fromContract("OUT_OF_STOCK"));
        assertEquals(EstadoItem.DISPONIBLE, EstadoItem.fromContract("DISPONIBLE"));
    }

    @Test
    void serializaEnumsYValoresDeRespaldoConElContratoHttp() throws Exception {
        ApiEnumSerializer serializer = new ApiEnumSerializer();
        JsonGenerator generator = mock(JsonGenerator.class);

        serializer.serialize(Estilo.DEPORTIVO, generator, null);
        verify(generator).writeString("SPORTY");
        assertEquals("BUYER", ApiEnumSerializer.valueOf(Rol.COMPRADOR));
        assertEquals("CUSTOM", ApiEnumSerializer.valueOf("CUSTOM"));

        ApiEnumKeySerializer keySerializer = new ApiEnumKeySerializer();
        keySerializer.serialize(TipoPrenda.SUPERIOR, generator, null);
        verify(generator).writeFieldName("TOP");

        assertEquals(TipoPrenda.CALZADO,
                new TipoPrendaKeyDeserializer().deserializeKey("FOOTWEAR", null));
    }
}

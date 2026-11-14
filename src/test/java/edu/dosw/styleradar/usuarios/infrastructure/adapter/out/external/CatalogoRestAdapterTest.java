package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.external;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CatalogoRestAdapterTest {

    @Test
    void consultaPorEstiloYTallaYUneLaMismaPrendaPorUuid() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        CatalogoRestAdapter adapter = new CatalogoRestAdapter(
                builder,
                "https://catalogo.test"
        );

        String respuesta = """
                [{
                  "id": "3f6c1a2e-8b1d-4c7a-9f2e-1a2b3c4d5e6f",
                  "almacenId": "9a8b7c6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d",
                  "nombre": "Camisa oxford blanca",
                  "tipo": "Camisa",
                  "color": "Blanco",
                  "marca": "Arturo Calle",
                  "estilo": "FORMAL",
                  "precio": 129900,
                  "fotografias": ["https://catalogo.test/foto1.jpg"],
                  "estado": "APROBADA",
                  "disponible": true,
                  "fechaPublicacion": "2026-11-10T15:32:00"
                }]
                """;

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/prendas?texto=FORMAL"))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));
        server.expect(requestTo(
                        "https://catalogo.test/api/v1/prendas?talla=M"))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas = adapter.obtenerPrendasDisponibles(
                Set.of(Estilo.FORMAL),
                Map.of(TipoPrenda.SUPERIOR, Set.of("M"))
        );

        server.verify();
        assertEquals(1, prendas.size());
        assertEquals("3f6c1a2e-8b1d-4c7a-9f2e-1a2b3c4d5e6f",
                prendas.get(0).getItemId());
        assertEquals(Estilo.FORMAL, prendas.get(0).getEstilo());
        assertEquals(TipoPrenda.SUPERIOR, prendas.get(0).getTipo());
        assertTrue(prendas.get(0).getTallasDisponibles().contains("M"));
        assertEquals("9a8b7c6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d",
                prendas.get(0).getAlmacenId());
    }
}

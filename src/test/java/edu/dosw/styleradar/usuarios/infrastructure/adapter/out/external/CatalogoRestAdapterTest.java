package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.external;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class CatalogoRestAdapterTest {

    private CatalogoRestAdapter crearAdapter(
            RestClient.Builder builder,
            String baseUrl) {
        return new CatalogoRestAdapter(builder, baseUrl);
    }

    @Test
    void consultaPorEstiloYTallaYUneLaMismaPrendaPorUuid() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

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
                        "https://catalogo.test/api/v1/garments?estilo=FORMAL"))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?talla=M"))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(
                        Set.of(Estilo.FORMAL),
                        Map.of(TipoPrenda.SUPERIOR, Set.of("M"))
                );

        server.verify();

        assertEquals(1, prendas.size());
        assertEquals(
                "3f6c1a2e-8b1d-4c7a-9f2e-1a2b3c4d5e6f",
                prendas.get(0).getItemId()
        );
        assertEquals(Estilo.FORMAL, prendas.get(0).getEstilo());
        assertEquals(TipoPrenda.SUPERIOR, prendas.get(0).getTipo());
        assertTrue(prendas.get(0).getTallasDisponibles().contains("M"));
        assertEquals(
                "9a8b7c6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d",
                prendas.get(0).getAlmacenId()
        );
    }

    @Test
    void consultaUnaTallaYLaAsociaAlTipoCorrecto() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        String respuesta = """
                [
                  {
                    "id": "11111111-1111-1111-1111-111111111111",
                    "nombre": "Camisa blanca",
                    "tipo": "Camisa",
                    "estilo": "CASUAL",
                    "precio": 50000,
                    "estado": "APROBADA",
                    "disponible": true
                  },
                  {
                    "id": "22222222-2222-2222-2222-222222222222",
                    "nombre": "Pantalon negro",
                    "tipo": "Pantalon",
                    "estilo": "CASUAL",
                    "precio": 80000,
                    "estado": "APROBADA",
                    "disponible": true
                  }
                ]
                """;

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?talla=M"))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(
                        Set.of(),
                        Map.of(
                                TipoPrenda.SUPERIOR, Set.of("M"),
                                TipoPrenda.INFERIOR, Set.of("M")
                        )
                );

        server.verify();

        assertEquals(2, prendas.size());

        CatalogoItemResponseDTO camisa = prendas.stream()
                .filter(prenda -> prenda.getTipo() == TipoPrenda.SUPERIOR)
                .findFirst()
                .orElseThrow();

        CatalogoItemResponseDTO pantalon = prendas.stream()
                .filter(prenda -> prenda.getTipo() == TipoPrenda.INFERIOR)
                .findFirst()
                .orElseThrow();

        assertTrue(camisa.getTallasDisponibles().contains("M"));
        assertTrue(pantalon.getTallasDisponibles().contains("M"));
    }

    @Test
    void ignoraTallasQueNoPertenecenALaListaUnificada() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?talla=M"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?talla=%C3%9Anica"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(
                        Set.of(),
                        Map.of(
                                TipoPrenda.SUPERIOR,
                                Set.of("m", "46", "unica", "TALLA-INVALIDA")
                        )
                );

        server.verify();
        assertTrue(prendas.isEmpty());
    }

    @Test
    void aceptaTallasNumericasDentroDelRangoYNormalizaTexto() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?talla=26"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?talla=XS"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(
                        Set.of(),
                        Map.of(
                                TipoPrenda.SUPERIOR,
                                Set.of("  xs  ", "26", "25", "46", "026")
                        )
                );

        server.verify();
        assertTrue(prendas.isEmpty());
    }

    @Test
    void ignoraPrendasSinIdONoDisponibles() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        String respuesta = """
                [
                  {
                    "id": null,
                    "tipo": "Camisa",
                    "disponible": true
                  },
                  {
                    "id": "22222222-2222-2222-2222-222222222222",
                    "tipo": "Camisa",
                    "disponible": false
                  },
                  {
                    "id": "33333333-3333-3333-3333-333333333333",
                    "tipo": "Camisa",
                    "disponible": null
                  }
                ]
                """;

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?estilo=CASUAL"))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(
                        Set.of(Estilo.CASUAL),
                        Map.of()
                );

        server.verify();
        assertTrue(prendas.isEmpty());
    }

    @Test
    void ignoraPrendasCuandoSuTipoNoCoincideConElEsperado() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        String respuesta = """
                [{
                  "id": "11111111-1111-1111-1111-111111111111",
                  "tipo": "Pantalon",
                  "disponible": true
                }]
                """;

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?talla=M"))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(
                        Set.of(),
                        Map.of(TipoPrenda.SUPERIOR, Set.of("M"))
                );

        server.verify();
        assertTrue(prendas.isEmpty());
    }

    @Test
    void permiteEstilosYTallasNulosSinRealizarConsultas() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(null, null);

        server.verify();
        assertTrue(prendas.isEmpty());
    }

    @Test
    void ignoraEstilosNulosYEntradasInvalidasDelMapaDeTallas() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        Set<Estilo> estilos = new HashSet<>();
        estilos.add(null);
        estilos.add(Estilo.CASUAL);

        Map<TipoPrenda, Set<String>> tallas = new HashMap<>();
        tallas.put(null, Set.of("M"));
        tallas.put(TipoPrenda.SUPERIOR, null);
        tallas.put(TipoPrenda.INFERIOR, new HashSet<>());

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?estilo=CASUAL"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(estilos, tallas);

        server.verify();
        assertTrue(prendas.isEmpty());
    }

    @Test
    void mapeaCamposOpcionalesNulosYEstiloInvalido() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        String respuesta = """
                [{
                  "id": "11111111-1111-1111-1111-111111111111",
                  "tipo": "Camisa",
                  "estilo": "ESTILO_DESCONOCIDO",
                  "fotografias": null,
                  "fechaPublicacion": null,
                  "disponible": true
                }]
                """;

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?estilo=CASUAL"))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(
                        Set.of(Estilo.CASUAL),
                        Map.of()
                );

        server.verify();

        assertEquals(1, prendas.size());
        assertNull(prendas.get(0).getEstilo());
        assertNull(prendas.get(0).getFechaPublicacion());
        assertTrue(prendas.get(0).getFotografias().isEmpty());
        assertEquals(TipoPrenda.SUPERIOR, prendas.get(0).getTipo());
    }

    @Test
    void respondeListaVaciaCuandoElCatalogoDevuelveCuerpoVacio() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?estilo=CASUAL"))
                .andRespond(withSuccess());

        List<CatalogoItemResponseDTO> prendas =
                adapter.obtenerPrendasDisponibles(
                        Set.of(Estilo.CASUAL),
                        Map.of()
                );

        server.verify();
        assertTrue(prendas.isEmpty());
    }

    @Test
    void degradaAListaVaciaCuandoCatalogoNoEstaDisponible() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?estilo=FORMAL"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        List<CatalogoItemResponseDTO> prendas = adapter.obtenerPrendasDisponibles(
                Set.of(Estilo.FORMAL),
                Map.of()
        );

        server.verify();
        assertTrue(prendas.isEmpty());
    }

    @Test
    void falloEnUnaConsultaIndividualNoDescartaResultadosDeOtrasConsultas() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CatalogoRestAdapter adapter =
                crearAdapter(builder, "https://catalogo.test");

        String respuesta = """
                [{
                  "id": "3f6c1a2e-8b1d-4c7a-9f2e-1a2b3c4d5e6f",
                  "almacenId": "9a8b7c6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d",
                  "nombre": "Camisa oxford blanca",
                  "tipo": "Camisa",
                  "estilo": "FORMAL",
                  "precio": 129900,
                  "disponible": true
                }]
                """;

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?estilo=FORMAL"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        server.expect(requestTo(
                        "https://catalogo.test/api/v1/garments?talla=M"))
                .andRespond(withSuccess(respuesta, MediaType.APPLICATION_JSON));

        List<CatalogoItemResponseDTO> prendas = adapter.obtenerPrendasDisponibles(
                Set.of(Estilo.FORMAL),
                Map.of(TipoPrenda.SUPERIOR, Set.of("M"))
        );

        server.verify();
        assertEquals(1, prendas.size());
        assertEquals("3f6c1a2e-8b1d-4c7a-9f2e-1a2b3c4d5e6f", prendas.get(0).getItemId());
    }

    @Test
    void constructorConTimeoutsDegradaAListaVaciaAnteErrorDeRed() {
        CatalogoRestAdapter adapter = new CatalogoRestAdapter(
                RestClient.builder(),
                "http://127.0.0.1:1",
                100,
                100);

        List<CatalogoItemResponseDTO> prendas = adapter.obtenerPrendasDisponibles(
                Set.of(Estilo.CASUAL),
                Map.of()
        );

        assertTrue(prendas.isEmpty());
    }
}

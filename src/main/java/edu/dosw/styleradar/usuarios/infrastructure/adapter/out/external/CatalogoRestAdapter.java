package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.external;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.EstadoItem;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.port.out.CatalogoClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.text.Normalizer;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
@Profile("!mock-catalogo")
public class CatalogoRestAdapter implements CatalogoClientPort {

    private final RestClient restClient;

    public CatalogoRestAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${styleradar.catalogo.base-url}") String baseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public List<CatalogoItemResponseDTO> obtenerPrendasDisponibles(
            Set<Estilo> estilos,
            Map<TipoPrenda, Set<String>> tallasPorTipo) {

        Map<String, CatalogoItemResponseDTO> prendasPorId =
                new LinkedHashMap<>();

        if (estilos != null) {
            estilos.stream()
                    .map(Enum::name)
                    .sorted()
                    .forEach(estilo -> agregarResultados(
                            prendasPorId,
                            consultar("texto", estilo),
                            null
                    ));
        }

        if (tallasPorTipo != null) {
            tallasPorTipo.values().stream()
                    .filter(tallas -> tallas != null)
                    .flatMap(Set::stream)
                    .map(String::trim)
                    .filter(talla -> !talla.isEmpty())
                    .distinct()
                    .sorted()
                    .forEach(talla -> agregarResultados(
                            prendasPorId,
                            consultar("talla", talla),
                            talla
                    ));
        }

        return List.copyOf(prendasPorId.values());
    }

    private List<CatalogoPrendaApiDTO> consultar(
            String nombreParametro,
            String valorParametro) {

        CatalogoPrendaApiDTO[] respuesta = restClient.get()
                .uri(uriBuilder -> construirUri(
                        uriBuilder,
                        nombreParametro,
                        valorParametro
                ))
                .retrieve()
                .body(CatalogoPrendaApiDTO[].class);

        return respuesta == null ? List.of() : List.of(respuesta);
    }

    private java.net.URI construirUri(
            UriBuilder uriBuilder,
            String nombreParametro,
            String valorParametro) {
        return uriBuilder.path("/api/v1/prendas")
                .queryParam(nombreParametro, valorParametro)
                .build();
    }

    private void agregarResultados(
            Map<String, CatalogoItemResponseDTO> prendasPorId,
            List<CatalogoPrendaApiDTO> resultados,
            String tallaConsultada) {

        for (CatalogoPrendaApiDTO prenda : resultados) {
            if (prenda.id() == null || !Boolean.TRUE.equals(prenda.disponible())) {
                continue;
            }

            CatalogoItemResponseDTO mapeada = mapear(prenda, tallaConsultada);
            CatalogoItemResponseDTO existente = prendasPorId.get(prenda.id());

            if (existente == null) {
                prendasPorId.put(prenda.id(), mapeada);
            } else if (tallaConsultada != null) {
                Set<String> tallas = new LinkedHashSet<>(
                        existente.getTallasDisponibles());
                tallas.add(tallaConsultada);
                existente.setTallasDisponibles(new ArrayList<>(tallas));
            }
        }
    }

    private CatalogoItemResponseDTO mapear(
            CatalogoPrendaApiDTO prenda,
            String tallaConsultada) {

        List<String> tallas = tallaConsultada == null
                ? new ArrayList<>()
                : new ArrayList<>(List.of(tallaConsultada));

        return CatalogoItemResponseDTO.builder()
                .itemId(prenda.id())
                .almacenId(prenda.almacenId())
                .nombre(prenda.nombre())
                .tipo(mapearTipo(prenda.tipo()))
                .color(prenda.color())
                .marca(prenda.marca())
                .estilo(mapearEstilo(prenda.estilo()))
                .precio(prenda.precio())
                .stock(1)
                .estado(EstadoItem.DISPONIBLE)
                .fechaPublicacion(prenda.fechaPublicacion() == null
                        ? null
                        : prenda.fechaPublicacion().toInstant(ZoneOffset.UTC))
                .fotografias(prenda.fotografias() == null
                        ? List.of()
                        : prenda.fotografias())
                .tallasDisponibles(tallas)
                .build();
    }

    private Estilo mapearEstilo(String estilo) {
        if (estilo == null || estilo.isBlank()) {
            return null;
        }

        try {
            return Estilo.valueOf(estilo.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException excepcion) {
            return null;
        }
    }

    private TipoPrenda mapearTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return null;
        }

        String normalizado = Normalizer.normalize(tipo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);

        if (contieneAlguno(normalizado,
                "calzado", "zapato", "zapatilla", "bota", "sandalia")) {
            return TipoPrenda.CALZADO;
        }

        if (contieneAlguno(normalizado,
                "inferior", "pantalon", "jean", "falda", "short", "bermuda")) {
            return TipoPrenda.INFERIOR;
        }

        if (contieneAlguno(normalizado,
                "superior", "camisa", "camiseta", "chaqueta", "blusa",
                "sueter", "sweater", "buzo", "abrigo")) {
            return TipoPrenda.SUPERIOR;
        }

        return null;
    }

    private boolean contieneAlguno(String texto, String... palabras) {
        for (String palabra : palabras) {
            if (texto.contains(palabra)) {
                return true;
            }
        }
        return false;
    }
}

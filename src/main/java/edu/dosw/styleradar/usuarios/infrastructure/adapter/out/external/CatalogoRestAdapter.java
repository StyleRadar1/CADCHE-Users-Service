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

    private static final Set<String> TALLAS_ALFABETICAS = Set.of(
            "XS", "S", "M", "L", "XL", "XXL"
    );

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
                    .filter(estilo -> estilo != null)
                    .map(Enum::name)
                    .sorted()
                    .forEach(estilo -> agregarResultados(
                            prendasPorId,
                            consultar("estilo", estilo),
                            null,
                            null
                    ));
        }

        Map<String, Set<TipoPrenda>> tiposPorTalla =
                agruparTallasPorTipo(tallasPorTipo);

        tiposPorTalla.forEach((talla, tipos) -> agregarResultados(
                prendasPorId,
                consultar("talla", talla),
                talla,
                tipos
        ));

        return List.copyOf(prendasPorId.values());
    }

    private Map<String, Set<TipoPrenda>> agruparTallasPorTipo(
            Map<TipoPrenda, Set<String>> tallasPorTipo) {

        Map<String, Set<TipoPrenda>> tiposPorTalla =
                new LinkedHashMap<>();

        if (tallasPorTipo == null) {
            return tiposPorTalla;
        }

        tallasPorTipo.forEach((tipo, tallas) -> {
            if (tipo == null || tallas == null) {
                return;
            }

            tallas.stream()
                    .map(this::normalizarTalla)
                    .filter(talla -> talla != null)
                    .distinct()
                    .sorted()
                    .forEach(talla -> tiposPorTalla
                            .computeIfAbsent(
                                    talla,
                                    clave -> new LinkedHashSet<>()
                            )
                            .add(tipo));
        });

        return tiposPorTalla;
    }

    private String normalizarTalla(String talla) {

        if (talla == null || talla.isBlank()) {
            return null;
        }

        String normalizada = talla.trim().toUpperCase(Locale.ROOT);

        if (TALLAS_ALFABETICAS.contains(normalizada)) {
            return normalizada;
        }

        if (normalizada.equals("UNICA")
                || normalizada.equals("ÚNICA")) {
            return "Única";
        }

        try {
            int numero = Integer.parseInt(normalizada);

            if (numero >= 26 && numero <= 45
                    && normalizada.equals(String.valueOf(numero))) {
                return normalizada;
            }
        } catch (NumberFormatException excepcion) {
            return null;
        }

        return null;
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
            String tallaConsultada,
            Set<TipoPrenda> tiposEsperados) {

        for (CatalogoPrendaApiDTO prenda : resultados) {

            if (prenda.id() == null
                    || !Boolean.TRUE.equals(prenda.disponible())) {
                continue;
            }

            CatalogoItemResponseDTO mapeada =
                    mapear(prenda, tallaConsultada);

            if (tiposEsperados != null
                    && !tiposEsperados.contains(mapeada.getTipo())) {
                continue;
            }

            CatalogoItemResponseDTO existente =
                    prendasPorId.get(prenda.id());

            if (existente == null) {
                prendasPorId.put(prenda.id(), mapeada);
            } else if (tallaConsultada != null) {

                Set<String> tallas = new LinkedHashSet<>(
                        existente.getTallasDisponibles()
                );

                tallas.add(tallaConsultada);

                existente.setTallasDisponibles(
                        new ArrayList<>(tallas)
                );
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
            return Estilo.valueOf(
                    estilo.trim().toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException excepcion) {
            return null;
        }
    }

    private TipoPrenda mapearTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            return null;
        }

        String normalizado = Normalizer.normalize(
                        tipo,
                        Normalizer.Form.NFD
                )
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

    private boolean contieneAlguno(
            String texto,
            String... palabras) {

        for (String palabra : palabras) {
            if (texto.contains(palabra)) {
                return true;
            }
        }

        return false;
    }
}

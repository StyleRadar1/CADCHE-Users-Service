package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.RecomendacionesResponseDTO;
import edu.dosw.styleradar.usuarios.application.service.RecomendacionesCache;
import edu.dosw.styleradar.usuarios.domain.exception.CatalogoNoDisponibleException;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerRecomendacionesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.CatalogoClientPort;
import edu.dosw.styleradar.usuarios.domain.port.out.PreferenciaEstiloRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.TallaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ObtenerRecomendacionesUseCaseImpl
        implements ObtenerRecomendacionesUseCase {

    private final PreferenciaEstiloRepositoryPort preferenciasRepository;
    private final TallaRepositoryPort tallasRepository;
    private final CatalogoClientPort catalogoClient;
    private final RecomendacionesCache recomendacionesCache;

    public ObtenerRecomendacionesUseCaseImpl(
            PreferenciaEstiloRepositoryPort preferenciasRepository,
            TallaRepositoryPort tallasRepository,
            CatalogoClientPort catalogoClient,
            RecomendacionesCache recomendacionesCache) {

        this.preferenciasRepository = preferenciasRepository;
        this.tallasRepository = tallasRepository;
        this.catalogoClient = catalogoClient;
        this.recomendacionesCache = recomendacionesCache;
    }

    @Override
    public RecomendacionesResponseDTO obtener(
            Long usuarioId,
            int page,
            int size) {

        Set<Estilo> estilos =
                preferenciasRepository.buscarPorUsuarioId(usuarioId);

        Map<TipoPrenda, Set<String>> tallas =
                tallasRepository.buscarPorUsuarioId(usuarioId);

        boolean perfilSinDatos =
                (estilos == null || estilos.isEmpty())
                        && (tallas == null || tallas.isEmpty());

        if (perfilSinDatos) {
            return crearRespuestaVacia(
                    page,
                    size,
                    true,
                    "No tienes preferencias de estilo ni tallas registradas. "
                            + "Actualiza tu perfil para recibir recomendaciones."
            );
        }

        String claveCache = usuarioId + "-" + estilos + "-" + tallas;

        List<CatalogoItemResponseDTO> recomendaciones =
                recomendacionesCache.obtener(claveCache)
                        .orElseGet(() -> {
                            List<CatalogoItemResponseDTO> calculadas =
                                    calcularRecomendaciones(estilos, tallas);

                            recomendacionesCache.guardar(
                                    claveCache,
                                    calculadas
                            );

                            return calculadas;
                        });

        return crearRespuesta(
                recomendaciones,
                page,
                size,
                false,
                "Recomendaciones generadas según tu perfil."
        );
    }

    private List<CatalogoItemResponseDTO> calcularRecomendaciones(
            Set<Estilo> estilos,
            Map<TipoPrenda, Set<String>> tallas) {

        List<CatalogoItemResponseDTO> prendasCatalogo;

        try {
            prendasCatalogo = catalogoClient.obtenerPrendasDisponibles(
                    estilos,
                    tallas
            );
        } catch (Exception exception) {
            throw new CatalogoNoDisponibleException(
                    "El catálogo no está disponible en este momento",
                    exception
            );
        }

        return prendasCatalogo.stream()
                .filter(prenda -> prenda.getStock() != null
                        && prenda.getStock() > 0)
                .map(prenda -> Map.entry(
                        prenda,
                        calcularPuntaje(prenda, estilos, tallas)
                ))
                .filter(resultado -> resultado.getValue() > 0)
                .sorted(Comparator
                        .comparingInt(
                                (Map.Entry<CatalogoItemResponseDTO, Integer> resultado) ->
                                        resultado.getValue()
                        )
                        .reversed()
                        .thenComparing(
                                resultado -> resultado.getKey().getFechaPublicacion(),
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                        .thenComparing(
                                resultado -> resultado.getKey().getItemId(),
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                .map(Map.Entry::getKey)
                .toList();
    }

    private int calcularPuntaje(
            CatalogoItemResponseDTO prenda,
            Set<Estilo> estilos,
            Map<TipoPrenda, Set<String>> tallas) {

        int puntaje = 0;

        if (estilos != null
                && prenda.getEstilo() != null
                && estilos.contains(prenda.getEstilo())) {
            puntaje += 2;
        }

        Set<String> tallasUsuario =
                tallas == null ? null : tallas.get(prenda.getTipo());

        if (tallasUsuario != null
                && prenda.getTallasDisponibles() != null) {

            boolean coincideTalla = prenda.getTallasDisponibles()
                    .stream()
                    .anyMatch(tallasUsuario::contains);

            if (coincideTalla) {
                puntaje += 3;
            }
        }

        return puntaje;
    }

    private RecomendacionesResponseDTO crearRespuesta(
            List<CatalogoItemResponseDTO> recomendaciones,
            int page,
            int size,
            boolean perfilSinDatos,
            String mensaje) {

        int desde = Math.min(page * size, recomendaciones.size());
        int hasta = Math.min(desde + size, recomendaciones.size());

        List<CatalogoItemResponseDTO> contenido =
                recomendaciones.subList(desde, hasta);

        int totalPaginas =
                (int) Math.ceil((double) recomendaciones.size() / size);

        PageResponseDTO<CatalogoItemResponseDTO> pagina =
                new PageResponseDTO<>(
                        contenido,
                        page,
                        size,
                        recomendaciones.size(),
                        totalPaginas,
                        page == 0,
                        page >= totalPaginas - 1
                );

        return new RecomendacionesResponseDTO(
                pagina,
                perfilSinDatos,
                mensaje
        );
    }

    private RecomendacionesResponseDTO crearRespuestaVacia(
            int page,
            int size,
            boolean perfilSinDatos,
            String mensaje) {

        return crearRespuesta(
                List.of(),
                page,
                size,
                perfilSinDatos,
                mensaje
        );
    }
}

package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerFeedPersonalizadoUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.CatalogoClientPort;
import edu.dosw.styleradar.usuarios.domain.port.out.PreferenciaEstiloRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.TallaRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.exception.CatalogoNoDisponibleException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ObtenerFeedPersonalizadoUseCaseImpl
        implements ObtenerFeedPersonalizadoUseCase {

    private final PreferenciaEstiloRepositoryPort preferenciasRepository;
    private final TallaRepositoryPort tallasRepository;
    private final CatalogoClientPort catalogoClient;

    public ObtenerFeedPersonalizadoUseCaseImpl(
            PreferenciaEstiloRepositoryPort preferenciasRepository,
            TallaRepositoryPort tallasRepository,
            CatalogoClientPort catalogoClient) {

        this.preferenciasRepository = preferenciasRepository;
        this.tallasRepository = tallasRepository;
        this.catalogoClient = catalogoClient;
    }

    @Override
    public PageResponseDTO<CatalogoItemResponseDTO> obtener(
            Long usuarioId, int page, int size) {

        Set<Estilo> estilos =
                preferenciasRepository.buscarPorUsuarioId(usuarioId);

        Map<TipoPrenda, Set<String>> tallas =
                tallasRepository.buscarPorUsuarioId(usuarioId);

        List<CatalogoItemResponseDTO> prendasCatalogo;

        try {
            prendasCatalogo = catalogoClient.obtenerPrendasDisponibles();
        } catch (Exception exception) {
            throw new CatalogoNoDisponibleException(
                    "El catálogo no está disponible en este momento",
                    exception
            );
        }

        List<CatalogoItemResponseDTO> prendas = prendasCatalogo.stream()
                .filter(prenda -> prenda.getStock() != null
                        && prenda.getStock() > 0)
                .sorted(Comparator
                        .comparingInt((CatalogoItemResponseDTO prenda) ->
                                calcularPuntaje(prenda, estilos, tallas))
                        .reversed()
                        .thenComparing(
                                CatalogoItemResponseDTO::getFechaPublicacion,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                        .thenComparing(
                                CatalogoItemResponseDTO::getItemId,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                .toList();

        int desde = Math.min(page * size, prendas.size());
        int hasta = Math.min(desde + size, prendas.size());

        List<CatalogoItemResponseDTO> contenido =
                prendas.subList(desde, hasta);

        int totalPaginas =
                (int) Math.ceil((double) prendas.size() / size);

        return new PageResponseDTO<>(
                contenido,
                page,
                size,
                prendas.size(),
                totalPaginas,
                page == 0,
                page >= totalPaginas - 1
        );
    }

    private int calcularPuntaje(
            CatalogoItemResponseDTO prenda,
            Set<Estilo> estilos,
            Map<TipoPrenda, Set<String>> tallas) {

        int puntaje = 0;

        if (prenda.getEstilo() != null
                && estilos.contains(prenda.getEstilo())) {
            puntaje += 2;
        }

        Set<String> tallasUsuario = tallas.get(prenda.getTipo());

        if (tallasUsuario != null
                && prenda.getTallasDisponibles() != null) {

            boolean coincideTalla = prenda.getTallasDisponibles()
                    .stream()
                    .map(Talla::name)
                    .anyMatch(tallasUsuario::contains);

            if (coincideTalla) {
                puntaje += 3;
            }
        }

        return puntaje;
    }
}

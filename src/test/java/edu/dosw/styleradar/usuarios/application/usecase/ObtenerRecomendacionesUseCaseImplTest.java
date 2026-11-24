package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.RecomendacionesResponseDTO;
import edu.dosw.styleradar.usuarios.application.service.RecomendacionesCache;
import edu.dosw.styleradar.usuarios.domain.model.EstadoItem;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.port.out.CatalogoClientPort;
import edu.dosw.styleradar.usuarios.domain.port.out.PreferenciaEstiloRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.TallaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObtenerRecomendacionesUseCaseImplTest {

    private PreferenciaEstiloRepositoryPort preferenciasRepository;
    private TallaRepositoryPort tallasRepository;
    private CatalogoClientPort catalogoClient;
    private RecomendacionesCache cache;

    private ObtenerRecomendacionesUseCaseImpl useCase;

    private final Long usuarioId = 107L;

    @BeforeEach
    void setUp() {
        preferenciasRepository = mock(PreferenciaEstiloRepositoryPort.class);
        tallasRepository = mock(TallaRepositoryPort.class);
        catalogoClient = mock(CatalogoClientPort.class);
        cache = new RecomendacionesCache();

        useCase = new ObtenerRecomendacionesUseCaseImpl(
                preferenciasRepository,
                tallasRepository,
                catalogoClient,
                cache
        );
    }

    @Test
    void debeRecomendarPrendasQueCoincidenConElPerfil() {
        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of(Estilo.URBANO));

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of(
                        TipoPrenda.SUPERIOR,
                        Set.of("M")
                ));

        CatalogoItemResponseDTO compatible = crearPrenda(
                1L, Estilo.URBANO, List.of(Talla.M), 5
        );

        CatalogoItemResponseDTO incompatible = crearPrenda(
                2L, Estilo.FORMAL, List.of(Talla.L), 5
        );

        when(catalogoClient.obtenerPrendasDisponibles(anySet(), anyMap()))
                .thenReturn(List.of(compatible, incompatible));

        RecomendacionesResponseDTO resultado =
                useCase.obtener(usuarioId, 0, 20);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("1", resultado.getContent().get(0).getItemId());
        assertFalse(resultado.isGeneralSinPreferencias());
    }

    @Test
    void debePriorizarCoincidenciaDeEstiloYTalla() {
        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of(Estilo.URBANO));

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of(
                        TipoPrenda.SUPERIOR,
                        Set.of("M")
                ));

        CatalogoItemResponseDTO soloEstilo = crearPrenda(
                1L, Estilo.URBANO, List.of(Talla.L), 5
        );

        CatalogoItemResponseDTO soloTalla = crearPrenda(
                2L, Estilo.FORMAL, List.of(Talla.M), 5
        );

        CatalogoItemResponseDTO ambasCoincidencias = crearPrenda(
                3L, Estilo.URBANO, List.of(Talla.M), 5
        );

        when(catalogoClient.obtenerPrendasDisponibles(anySet(), anyMap()))
                .thenReturn(List.of(
                        soloEstilo,
                        soloTalla,
                        ambasCoincidencias
                ));

        RecomendacionesResponseDTO resultado =
                useCase.obtener(usuarioId, 0, 20);

        assertEquals("3", resultado.getContent().get(0).getItemId());
        assertEquals("2", resultado.getContent().get(1).getItemId());
        assertEquals("1", resultado.getContent().get(2).getItemId());
    }

    @Test
    void debeDevolverListaVaciaCuandoElPerfilNoTieneDatos() {
        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of());

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of());

        RecomendacionesResponseDTO resultado =
                useCase.obtener(usuarioId, 0, 20);

        assertTrue(resultado.getContent().isEmpty());
        assertTrue(resultado.isGeneralSinPreferencias());
        assertNotNull(resultado.getMensaje());

        verify(catalogoClient, never()).obtenerPrendasDisponibles(anySet(), anyMap());
    }

    @Test
    void noDebeRecomendarPrendasSinStock() {
        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of(Estilo.URBANO));

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of());

        CatalogoItemResponseDTO disponible = crearPrenda(
                1L, Estilo.URBANO, List.of(Talla.M), 5
        );

        CatalogoItemResponseDTO agotada = crearPrenda(
                2L, Estilo.URBANO, List.of(Talla.M), 0
        );

        when(catalogoClient.obtenerPrendasDisponibles(anySet(), anyMap()))
                .thenReturn(List.of(disponible, agotada));

        RecomendacionesResponseDTO resultado =
                useCase.obtener(usuarioId, 0, 20);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("1", resultado.getContent().get(0).getItemId());
    }

    @Test
    void debeUtilizarCacheEnConsultasRepetidas() {
        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of(Estilo.URBANO));

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of());

        when(catalogoClient.obtenerPrendasDisponibles(anySet(), anyMap()))
                .thenReturn(List.of(
                        crearPrenda(1L, Estilo.URBANO, List.of(Talla.M), 5)
                ));

        useCase.obtener(usuarioId, 0, 20);
        useCase.obtener(usuarioId, 0, 20);

        verify(catalogoClient, times(1))
                .obtenerPrendasDisponibles(anySet(), anyMap());
    }

    private CatalogoItemResponseDTO crearPrenda(
            Long itemId,
            Estilo estilo,
            List<Talla> tallas,
            Integer stock) {

        return CatalogoItemResponseDTO.builder()
                .itemId(itemId.toString())
                .prendaId(itemId)
                .nombre("Prenda " + itemId)
                .tipo(TipoPrenda.SUPERIOR)
                .estilo(estilo)
                .precio(89900.0)
                .stock(stock)
                .estado(stock > 0 ? EstadoItem.DISPONIBLE : EstadoItem.AGOTADA)
                .fechaPublicacion(Instant.now())
                .tallasDisponibles(tallas.stream().map(Enum::name).toList())
                .build();
    }
}

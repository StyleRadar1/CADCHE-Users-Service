package edu.dosw.styleradar.usuarios.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.CatalogoNoDisponibleException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoItem;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.port.out.CatalogoClientPort;
import edu.dosw.styleradar.usuarios.domain.port.out.PreferenciaEstiloRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.TallaRepositoryPort;

class ObtenerFeedPersonalizadoUseCaseImplTest {

    private PreferenciaEstiloRepositoryPort preferenciasRepository;
    private TallaRepositoryPort tallasRepository;
    private CatalogoClientPort catalogoClient;

    private ObtenerFeedPersonalizadoUseCaseImpl useCase;

    private final Long usuarioId = 107L;

    @BeforeEach
    void setUp() {
        preferenciasRepository = mock(PreferenciaEstiloRepositoryPort.class);
        tallasRepository = mock(TallaRepositoryPort.class);
        catalogoClient = mock(CatalogoClientPort.class);

        useCase = new ObtenerFeedPersonalizadoUseCaseImpl(
                preferenciasRepository,
                tallasRepository,
                catalogoClient
        );
    }

    @Test
    void debePriorizarPrendasConCoincidenciaDeEstiloYTalla() {

        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of(Estilo.URBANO));

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of(
                        TipoPrenda.SUPERIOR,
                        Set.of("M")
                ));

        CatalogoItemResponseDTO prendaCompatible = crearPrenda(
                1L, Estilo.URBANO, List.of(Talla.M), 5,
                "2026-11-10T10:00:00Z"
        );

        CatalogoItemResponseDTO soloEstilo = crearPrenda(
                2L, Estilo.URBANO, List.of(Talla.L), 5,
                "2026-11-12T10:00:00Z"
        );

        CatalogoItemResponseDTO soloTalla = crearPrenda(
                3L, Estilo.CASUAL, List.of(Talla.M), 5,
                "2026-11-11T10:00:00Z"
        );

        when(catalogoClient.obtenerPrendasDisponibles())
                .thenReturn(List.of(
                        soloEstilo,
                        soloTalla,
                        prendaCompatible
                ));

        PageResponseDTO<CatalogoItemResponseDTO> resultado =
                useCase.obtener(usuarioId, 0, 20);

        assertEquals(3, resultado.getTotalElements());

        assertEquals(1L, resultado.getContent().get(0).getItemId());
        assertEquals(3L, resultado.getContent().get(1).getItemId());
        assertEquals(2L, resultado.getContent().get(2).getItemId());
    }

    @Test
    void debeDarMayorPuntajeALaCoincidenciaDeTallaQueAlEstilo() {

        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of(Estilo.URBANO));

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of(
                        TipoPrenda.SUPERIOR,
                        Set.of("M")
                ));

        CatalogoItemResponseDTO soloEstilo = crearPrenda(
                1L, Estilo.URBANO, List.of(Talla.L), 5,
                "2026-11-12T10:00:00Z"
        );

        CatalogoItemResponseDTO soloTalla = crearPrenda(
                2L, Estilo.CASUAL, List.of(Talla.M), 5,
                "2026-11-10T10:00:00Z"
        );

        when(catalogoClient.obtenerPrendasDisponibles())
                .thenReturn(List.of(soloEstilo, soloTalla));

        PageResponseDTO<CatalogoItemResponseDTO> resultado =
                useCase.obtener(usuarioId, 0, 20);

        assertEquals(2L, resultado.getContent().get(0).getItemId());
        assertEquals(1L, resultado.getContent().get(1).getItemId());
    }

    @Test
    void debeMostrarFeedGeneralCuandoNoHayPreferencias() {

        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of());

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of());

        CatalogoItemResponseDTO prendaAntigua = crearPrenda(
                1L, Estilo.URBANO, List.of(Talla.M), 5,
                "2026-11-08T10:00:00Z"
        );

        CatalogoItemResponseDTO prendaReciente = crearPrenda(
                2L, Estilo.CASUAL, List.of(Talla.L), 5,
                "2026-11-12T10:00:00Z"
        );

        when(catalogoClient.obtenerPrendasDisponibles())
                .thenReturn(List.of(prendaAntigua, prendaReciente));

        PageResponseDTO<CatalogoItemResponseDTO> resultado =
                useCase.obtener(usuarioId, 0, 20);

        assertEquals(2, resultado.getTotalElements());
        assertEquals(2L, resultado.getContent().get(0).getItemId());
        assertEquals(1L, resultado.getContent().get(1).getItemId());
    }

    @Test
    void noDebeMostrarPrendasSinStock() {

        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of());

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of());

        CatalogoItemResponseDTO disponible = crearPrenda(
                1L, Estilo.URBANO, List.of(Talla.M), 5,
                "2026-11-12T10:00:00Z"
        );

        CatalogoItemResponseDTO agotada = crearPrenda(
                2L, Estilo.CASUAL, List.of(Talla.M), 0,
                "2026-11-11T10:00:00Z"
        );

        when(catalogoClient.obtenerPrendasDisponibles())
                .thenReturn(List.of(disponible, agotada));

        PageResponseDTO<CatalogoItemResponseDTO> resultado =
                useCase.obtener(usuarioId, 0, 20);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(1L, resultado.getContent().get(0).getItemId());
    }

    @Test
    void debeOrdenarPorIdCuandoElPuntajeYLaFechaSonIguales() {

        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of());

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of());

        String fecha = "2026-11-12T10:00:00Z";

        CatalogoItemResponseDTO segundaPrenda = crearPrenda(
                2L, Estilo.URBANO, List.of(Talla.M), 5, fecha
        );

        CatalogoItemResponseDTO primeraPrenda = crearPrenda(
                1L, Estilo.CASUAL, List.of(Talla.L), 5, fecha
        );

        when(catalogoClient.obtenerPrendasDisponibles())
                .thenReturn(List.of(segundaPrenda, primeraPrenda));

        PageResponseDTO<CatalogoItemResponseDTO> resultado =
                useCase.obtener(usuarioId, 0, 20);

        assertEquals(1L, resultado.getContent().get(0).getItemId());
        assertEquals(2L, resultado.getContent().get(1).getItemId());
    }

    @Test
    void debeDevolverLaPaginaSolicitada() {

        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of());

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of());

        CatalogoItemResponseDTO primera = crearPrenda(
                1L, Estilo.URBANO, List.of(Talla.M), 5,
                "2026-11-12T10:00:00Z"
        );

        CatalogoItemResponseDTO segunda = crearPrenda(
                2L, Estilo.CASUAL, List.of(Talla.M), 5,
                "2026-11-11T10:00:00Z"
        );

        CatalogoItemResponseDTO tercera = crearPrenda(
                3L, Estilo.FORMAL, List.of(Talla.L), 5,
                "2026-11-10T10:00:00Z"
        );

        when(catalogoClient.obtenerPrendasDisponibles())
                .thenReturn(List.of(primera, segunda, tercera));

        PageResponseDTO<CatalogoItemResponseDTO> resultado =
                useCase.obtener(usuarioId, 1, 2);

        assertEquals(1, resultado.getPage());
        assertEquals(2, resultado.getSize());
        assertEquals(3, resultado.getTotalElements());
        assertEquals(2, resultado.getTotalPages());
        assertEquals(1, resultado.getContent().size());
        assertEquals(3L, resultado.getContent().get(0).getItemId());
        assertFalse(resultado.isFirst());
        assertTrue(resultado.isLast());
    }

    @Test
    void debeDevolverContenidoVacioCuandoLaPaginaNoExiste() {

        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of());

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of());

        when(catalogoClient.obtenerPrendasDisponibles())
                .thenReturn(List.of(
                        crearPrenda(
                                1L, Estilo.URBANO, List.of(Talla.M), 5,
                                "2026-11-12T10:00:00Z"
                        )
                ));

        PageResponseDTO<CatalogoItemResponseDTO> resultado =
                useCase.obtener(usuarioId, 5, 20);

        assertTrue(resultado.getContent().isEmpty());
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getTotalPages());
        assertTrue(resultado.isLast());
    }

    @Test
    void debeConvertirErrorDelCatalogoEnExcepcionEspecifica() {

        when(preferenciasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Set.of(Estilo.URBANO));

        when(tallasRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Map.of(
                        TipoPrenda.SUPERIOR,
                        Set.of("M")
                ));

        when(catalogoClient.obtenerPrendasDisponibles())
                .thenThrow(new RuntimeException("Catálogo no disponible"));

        CatalogoNoDisponibleException excepcion = assertThrows(
                CatalogoNoDisponibleException.class,
                () -> useCase.obtener(usuarioId, 0, 20)
        );

        assertEquals(
                "El catálogo no está disponible en este momento",
                excepcion.getMessage()
        );

        assertNotNull(excepcion.getCause());

        verify(catalogoClient).obtenerPrendasDisponibles();
    }

    private CatalogoItemResponseDTO crearPrenda(
            Long itemId,
            Estilo estilo,
            List<Talla> tallas,
            Integer stock,
            String fecha) {

        return CatalogoItemResponseDTO.builder()
                .itemId(itemId)
                .prendaId(itemId)
                .nombre("Prenda de prueba " + itemId)
                .tipo(TipoPrenda.SUPERIOR)
                .estilo(estilo)
                .precio(89900.0)
                .stock(stock)
                .estado(EstadoItem.DISPONIBLE)
                .fechaPublicacion(Instant.parse(fecha))
                .tallasDisponibles(tallas)
                .build();
    }
}

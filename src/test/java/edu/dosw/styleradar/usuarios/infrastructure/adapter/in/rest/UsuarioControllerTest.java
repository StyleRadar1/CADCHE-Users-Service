package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarPreferenciasEstiloRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarDatosUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarTallasHabitualesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.GuardarBusquedaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.BusquedaGuardadaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.event.BusquedaSincronizadaEvent;
import edu.dosw.styleradar.usuarios.domain.model.BusquedaGuardada;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarPreferenciasEstiloUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarDatosUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarTallasHabitualesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.BusquedaGuardadaRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.BusquedaSincronizadaEventPublisherPort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    private RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private ActualizarPreferenciasEstiloUseCase actualizarPreferenciasUseCase;
    private ActualizarDatosUsuarioUseCase actualizarDatosUsuarioUseCase;
    private ActualizarTallasHabitualesUseCase actualizarTallasUseCase;
    private ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private BusquedaGuardadaRepositoryPort busquedasRepository;
    private BusquedaSincronizadaEventPublisherPort busquedaEventPublisher;

    private UsuarioController controller;

    @BeforeEach
    void setUp() {

        registrarUsuarioUseCase = mock(RegistrarUsuarioUseCase.class);
        actualizarPreferenciasUseCase =
                mock(ActualizarPreferenciasEstiloUseCase.class);
        actualizarDatosUsuarioUseCase = mock(ActualizarDatosUsuarioUseCase.class);
        actualizarTallasUseCase =
                mock(ActualizarTallasHabitualesUseCase.class);
        obtenerUsuarioUseCase = mock(ObtenerUsuarioUseCase.class);
        busquedasRepository = mock(BusquedaGuardadaRepositoryPort.class);
        busquedaEventPublisher =
                mock(BusquedaSincronizadaEventPublisherPort.class);

        controller = new UsuarioController(
                registrarUsuarioUseCase,
                actualizarDatosUsuarioUseCase,
                actualizarPreferenciasUseCase,
                actualizarTallasUseCase,
                obtenerUsuarioUseCase,
                busquedasRepository,
                busquedaEventPublisher
        );

        Jwt jwt = Jwt.withTokenValue("token-de-prueba")
                .header("alg", "HS256")
                .claim("sub", "144")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthenticationToken(jwt)
        );
    }

    @Test
    void debeActualizarDatosPersonalesDelUsuarioAutenticado() {
        var request = new ActualizarDatosUsuarioRequestDTO(
                "Laura Gómez", "+57 300 123 4567", "Chapinero");
        when(obtenerUsuarioUseCase.obtener(144L)).thenReturn(crearRespuestaUsuario());

        var respuesta = controller.actualizarDatosPersonales(144L, request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(144L, respuesta.getBody().getId());
        verify(actualizarDatosUsuarioUseCase).actualizar(
                144L, "Laura Gómez", "+57 300 123 4567", "Chapinero");
    }

    @Test
    void rechazaActualizarDatosPersonalesDeOtroUsuario() {
        var request = new ActualizarDatosUsuarioRequestDTO(
                "Laura Gómez", null, null);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> controller.actualizarDatosPersonales(145L, request));
        verifyNoInteractions(actualizarDatosUsuarioUseCase);
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private UsuarioResponseDTO crearRespuestaUsuario() {

        return UsuarioResponseDTO.builder()
                .id(144L)
                .nombre("Laura Gomez")
                .email("laura@styleradar.com")
                .telefono("3001234567")
                .fechaRegistro(Instant.now())
                .preferenciasEstilo(Set.of(Estilo.CASUAL))
                .tallasHabituales(Set.of(Talla.M))
                .tallasPorTipoPrenda(Map.of(
                        TipoPrenda.SUPERIOR, Set.of("M")
                ))
                .build();
    }

    private BusquedaGuardada crearBusqueda(Long id) {
        return new BusquedaGuardada(
                id,
                144L,
                "Chaquetas casuales",
                "chaqueta",
                TipoPrenda.SUPERIOR,
                "Negro",
                Talla.M,
                null,
                null,
                null,
                Estilo.CASUAL,
                null,
                null,
                Instant.now()
        );
    }

    @Test
    void debeRegistrarUsuario() {

        RegistrarUsuarioRequestDTO request =
                new RegistrarUsuarioRequestDTO();

        UsuarioResponseDTO esperado = crearRespuestaUsuario();

        when(registrarUsuarioUseCase.registrar(request))
                .thenReturn(esperado);

        ResponseEntity<UsuarioResponseDTO> respuesta =
                controller.registrar(request);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertSame(esperado, respuesta.getBody());

        verify(registrarUsuarioUseCase).registrar(request);
    }

    @Test
    void debeObtenerPerfil() {

        UsuarioResponseDTO esperado = crearRespuestaUsuario();

        when(obtenerUsuarioUseCase.obtener(144L))
                .thenReturn(esperado);

        ResponseEntity<UsuarioResponseDTO> respuesta =
                controller.obtenerPorId(144L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(144L, respuesta.getBody().getId());

        assertTrue(respuesta.getBody()
                .getPreferenciasEstilo()
                .contains(Estilo.CASUAL));

        assertEquals(
                Set.of("M"),
                respuesta.getBody()
                        .getTallasPorTipoPrenda()
                        .get(TipoPrenda.SUPERIOR)
        );

        verify(obtenerUsuarioUseCase).obtener(144L);
    }

    @Test
    void debeObtenerUsuarioPorId() {

        UsuarioResponseDTO esperado = crearRespuestaUsuario();

        when(obtenerUsuarioUseCase.obtener(144L))
                .thenReturn(esperado);

        ResponseEntity<UsuarioResponseDTO> respuesta =
                controller.obtenerPorId(144L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(144L, respuesta.getBody().getId());
        assertEquals(
                "laura@styleradar.com",
                respuesta.getBody().getEmail()
        );

        verify(obtenerUsuarioUseCase).obtener(144L);
    }

    @Test
    void debeLanzarExcepcionSiElUsuarioAutenticadoNoExiste() {

        when(obtenerUsuarioUseCase.obtener(144L))
                .thenThrow(new UsuarioNoEncontradoException(144L));

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> controller.obtenerPorId(144L)
        );
    }

    @Test
    void rechazaConsultarElPerfilDeOtroUsuario() {
        assertThrows(
                org.springframework.security.access.AccessDeniedException.class,
                () -> controller.obtenerPorId(999L)
        );

        verifyNoInteractions(obtenerUsuarioUseCase);
    }

    @Test
    void debeActualizarPreferenciasDeEstilo() {

        ActualizarPreferenciasEstiloRequestDTO request =
                new ActualizarPreferenciasEstiloRequestDTO();

        request.setPreferencias(Set.of(
                Estilo.CASUAL,
                Estilo.URBANO
        ));

        when(actualizarPreferenciasUseCase.actualizar(
                144L,
                request.getPreferencias()
        )).thenReturn(request.getPreferencias());

        UsuarioResponseDTO esperado = crearRespuestaUsuario();

        when(obtenerUsuarioUseCase.obtener(144L))
                .thenReturn(esperado);

        ResponseEntity<UsuarioResponseDTO> respuesta =
                controller.actualizarPreferenciasEstilo(144L, request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(144L, respuesta.getBody().getId());

        verify(actualizarPreferenciasUseCase)
                .actualizar(144L, request.getPreferencias());

        verify(obtenerUsuarioUseCase).obtener(144L);
    }

    @Test
    void rechazaActualizarPreferenciasParaOtroUsuarioDeLaRuta() {
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> controller.actualizarPreferenciasEstilo(
                        145L, new ActualizarPreferenciasEstiloRequestDTO()));

        verifyNoInteractions(actualizarPreferenciasUseCase);
        verifyNoInteractions(obtenerUsuarioUseCase);
    }

    @Test
    void debeActualizarTallasHabituales() {

        ActualizarTallasHabitualesRequestDTO request =
                new ActualizarTallasHabitualesRequestDTO();

        request.setTallas(Set.of(Talla.M));

        request.setTallasPorTipoPrenda(Map.of(
                TipoPrenda.SUPERIOR, Set.of("M"),
                TipoPrenda.INFERIOR, Set.of("32"),
                TipoPrenda.CALZADO, Set.of("40")
        ));

        when(actualizarTallasUseCase.actualizar(
                144L,
                request.getTallasPorTipoPrenda()
        )).thenReturn(request.getTallasPorTipoPrenda());

        UsuarioResponseDTO esperado = crearRespuestaUsuario();

        when(obtenerUsuarioUseCase.obtener(144L))
                .thenReturn(esperado);

        ResponseEntity<UsuarioResponseDTO> respuesta =
                controller.actualizarTallasHabituales(144L, request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(144L, respuesta.getBody().getId());

        verify(actualizarTallasUseCase)
                .actualizar(144L, request.getTallasPorTipoPrenda());

        verify(obtenerUsuarioUseCase).obtener(144L);
    }

    @Test
    void debeGuardarBusqueda() {

        GuardarBusquedaRequestDTO request =
                new GuardarBusquedaRequestDTO();

        request.setNombre("Chaquetas casuales");
        request.setQuery("chaqueta");
        request.setTipo(TipoPrenda.SUPERIOR);
        request.setColor("Negro");
        request.setTalla(Talla.M);
        request.setEstilo(Estilo.CASUAL);

        when(busquedasRepository.guardar(any()))
                .thenReturn(crearBusqueda(10L));

        ResponseEntity<BusquedaGuardadaResponseDTO> respuesta =
                controller.guardarBusqueda(144L, request);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(10L, respuesta.getBody().getId());
        assertEquals(
                "Chaquetas casuales",
                respuesta.getBody().getNombre()
        );
        assertEquals(
                "chaqueta",
                respuesta.getBody().getQuery()
        );
        assertEquals(
                Estilo.CASUAL,
                respuesta.getBody().getEstilo()
        );

        assertNotNull(respuesta.getBody().getFechaCreacion());

        verify(busquedaEventPublisher).publicar(any());
    }

    @Test
    void debeActualizarBusquedaGuardadaYPublicarEvento() {

        BusquedaGuardada anterior = crearBusqueda(5L);

        GuardarBusquedaRequestDTO request =
                new GuardarBusquedaRequestDTO();

        request.setNombre("Chaquetas urbanas");
        request.setQuery("chaqueta urbana");
        request.setTipo(TipoPrenda.SUPERIOR);
        request.setColor("Azul");
        request.setTalla(Talla.M);
        request.setEstilo(Estilo.URBANO);

        BusquedaGuardada actualizada = new BusquedaGuardada(
                5L,
                144L,
                "Chaquetas urbanas",
                "chaqueta urbana",
                TipoPrenda.SUPERIOR,
                "Azul",
                Talla.M,
                null,
                null,
                null,
                Estilo.URBANO,
                null,
                null,
                anterior.fechaCreacion()
        );

        when(busquedasRepository.buscarPorIdYUsuarioId(5L, 144L))
                .thenReturn(Optional.of(anterior));

        when(busquedasRepository.guardar(any()))
                .thenReturn(actualizada);

        ResponseEntity<BusquedaGuardadaResponseDTO> respuesta =
                controller.actualizarBusquedaGuardada(144L, 5L, request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(5L, respuesta.getBody().getId());
        assertEquals("Chaquetas urbanas", respuesta.getBody().getNombre());
        assertEquals("chaqueta urbana", respuesta.getBody().getQuery());

        verify(busquedasRepository).buscarPorIdYUsuarioId(5L, 144L);
        verify(busquedasRepository).guardar(any());
        verify(busquedaEventPublisher).publicar(any());
    }

    @Test
    void debeRetornarNoEncontradoSiLaBusquedaNoPerteneceAlUsuario() {

        GuardarBusquedaRequestDTO request =
                new GuardarBusquedaRequestDTO();

        when(busquedasRepository.buscarPorIdYUsuarioId(99L, 144L))
                .thenReturn(Optional.empty());

        ResponseEntity<BusquedaGuardadaResponseDTO> respuesta =
                controller.actualizarBusquedaGuardada(144L, 99L, request);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());

        verify(busquedasRepository, never()).guardar(any());
        verify(busquedaEventPublisher, never()).publicar(any());
    }

    @Test
    void debeObtenerListaDeBusquedasGuardadas() {

        when(busquedasRepository.buscarPorUsuarioId(144L))
                .thenReturn(List.of());

        ResponseEntity<List<BusquedaGuardadaResponseDTO>> respuesta =
                controller.obtenerBusquedasGuardadas(144L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().isEmpty());

        verify(busquedasRepository).buscarPorUsuarioId(144L);
    }

    @Test
    void debeEliminarBusquedaGuardada() {

        BusquedaGuardada busqueda = crearBusqueda(5L);

        when(busquedasRepository.buscarPorIdYUsuarioId(5L, 144L))
                .thenReturn(Optional.of(busqueda));

        when(busquedasRepository.eliminarPorIdYUsuarioId(5L, 144L))
                .thenReturn(true);

        ResponseEntity<Void> respuesta =
                controller.eliminarBusquedaGuardada(144L, 5L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        assertNull(respuesta.getBody());

        verify(busquedasRepository)
                .eliminarPorIdYUsuarioId(5L, 144L);

        verify(busquedaEventPublisher).publicar(any());
    }
}

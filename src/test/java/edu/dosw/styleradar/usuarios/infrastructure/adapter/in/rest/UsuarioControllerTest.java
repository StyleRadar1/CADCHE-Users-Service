package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarPreferenciasEstiloRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarTallasHabitualesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.GuardarBusquedaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.BusquedaGuardadaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.BusquedaGuardada;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarPreferenciasEstiloUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarTallasHabitualesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.BusquedaGuardadaRepositoryPort;

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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    private RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private ActualizarPreferenciasEstiloUseCase actualizarPreferenciasUseCase;
    private ActualizarTallasHabitualesUseCase actualizarTallasUseCase;
    private ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private BusquedaGuardadaRepositoryPort busquedasRepository;

    private UsuarioController controller;

    @BeforeEach
    void setUp() {

        registrarUsuarioUseCase = mock(RegistrarUsuarioUseCase.class);

        actualizarPreferenciasUseCase =
                mock(ActualizarPreferenciasEstiloUseCase.class);

        actualizarTallasUseCase =
                mock(ActualizarTallasHabitualesUseCase.class);

        obtenerUsuarioUseCase = mock(ObtenerUsuarioUseCase.class);

        busquedasRepository = mock(BusquedaGuardadaRepositoryPort.class);

        controller = new UsuarioController(
                registrarUsuarioUseCase,
                actualizarPreferenciasUseCase,
                actualizarTallasUseCase,
                obtenerUsuarioUseCase,
                busquedasRepository
        );

        Jwt jwt = Jwt.withTokenValue("token-de-prueba")
                .header("alg", "HS256")
                .claim("sub", "144")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthenticationToken(jwt)
        );
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
                controller.obtenerPerfil();

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
    void debeLanzarExcepcionSiUsuarioNoExisteAlObtenerPerfil() {

        when(obtenerUsuarioUseCase.obtener(144L))
                .thenThrow(new UsuarioNoEncontradoException(144L));

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> controller.obtenerPerfil()
        );
    }

    @Test
    void debeLanzarExcepcionSiUsuarioNoExisteAlConsultarPorId() {

        when(obtenerUsuarioUseCase.obtener(999L))
                .thenThrow(new UsuarioNoEncontradoException(999L));

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> controller.obtenerPorId(999L)
        );
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
                controller.actualizarPreferenciasEstilo(request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(144L, respuesta.getBody().getId());

        verify(actualizarPreferenciasUseCase)
                .actualizar(144L, request.getPreferencias());

        verify(obtenerUsuarioUseCase).obtener(144L);
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
                controller.actualizarTallasHabituales(request);

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
                .thenReturn(new BusquedaGuardada(
                        10L,
                        144L,
                        request.getNombre(),
                        request.getQuery(),
                        request.getTipo(),
                        request.getColor(),
                        request.getTalla(),
                        request.getPrecioMin(),
                        request.getPrecioMax(),
                        request.getMarca(),
                        request.getEstilo(),
                        request.getLatitud(),
                        request.getLongitud(),
                        Instant.now()
                ));

        ResponseEntity<BusquedaGuardadaResponseDTO> respuesta =
                controller.guardarBusqueda(request);

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
    }

    @Test
    void debeObtenerListaDeBusquedasGuardadas() {

        when(busquedasRepository.buscarPorUsuarioId(144L))
                .thenReturn(List.of());

        ResponseEntity<List<BusquedaGuardadaResponseDTO>> respuesta =
                controller.obtenerBusquedasGuardadas();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().isEmpty());

        verify(busquedasRepository).buscarPorUsuarioId(144L);
    }

    @Test
    void debeEliminarBusquedaGuardada() {

        ResponseEntity<Void> respuesta =
                controller.eliminarBusquedaGuardada(5L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        assertNull(respuesta.getBody());

        verify(busquedasRepository)
                .eliminarPorIdYUsuarioId(5L, 144L);
    }
}

package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarPreferenciasEstiloRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarTallasHabitualesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.GuardarBusquedaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.BusquedaGuardadaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.*;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarPreferenciasEstiloUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarTallasHabitualesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

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
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    private RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private ActualizarPreferenciasEstiloUseCase actualizarPreferenciasUseCase;
    private ActualizarTallasHabitualesUseCase actualizarTallasUseCase;
    private UsuarioRepositoryPort usuarioRepository;
    private UsuarioController controller;

    @BeforeEach
    void setUp() {
        registrarUsuarioUseCase = mock(RegistrarUsuarioUseCase.class);
        actualizarPreferenciasUseCase =
                mock(ActualizarPreferenciasEstiloUseCase.class);
        actualizarTallasUseCase =
                mock(ActualizarTallasHabitualesUseCase.class);
        usuarioRepository = mock(UsuarioRepositoryPort.class);

        controller = new UsuarioController(
                registrarUsuarioUseCase,
                actualizarPreferenciasUseCase,
                actualizarTallasUseCase,
                usuarioRepository
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

    private Usuario crearUsuario() {
        return new Usuario(
                144L,
                "Laura Gomez",
                "laura@styleradar.com",
                "3001234567",
                "hash-password",
                true,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now()
        );
    }

    @Test
    void debeRegistrarUsuario() {
        RegistrarUsuarioRequestDTO request =
                new RegistrarUsuarioRequestDTO();

        UsuarioResponseDTO esperado = UsuarioResponseDTO.builder()
                .id(144L)
                .nombre("Laura Gomez")
                .email("laura@styleradar.com")
                .build();

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
        ResponseEntity<UsuarioResponseDTO> respuesta =
                controller.obtenerPerfil();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(12L, respuesta.getBody().getId());
        assertEquals("Laura Gómez", respuesta.getBody().getNombre());
        assertTrue(respuesta.getBody().getPreferenciasEstilo()
                .contains(Estilo.CASUAL));
        assertTrue(respuesta.getBody().getTallasHabituales()
                .contains(Talla.M));
    }

    @Test
    void debeObtenerUsuarioPorId() {
        ResponseEntity<UsuarioResponseDTO> respuesta =
                controller.obtenerPorId(144L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(144L, respuesta.getBody().getId());
        assertEquals("laura.gomez@styleradar.com",
                respuesta.getBody().getEmail());
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

        when(usuarioRepository.buscarPorId(144L))
                .thenReturn(Optional.of(crearUsuario()));

        ResponseEntity<UsuarioResponseDTO> respuesta =
                controller.actualizarPreferenciasEstilo(request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(144L, respuesta.getBody().getId());
        assertEquals(
                request.getPreferencias(),
                respuesta.getBody().getPreferenciasEstilo()
        );

        verify(actualizarPreferenciasUseCase)
                .actualizar(144L, request.getPreferencias());
        verify(usuarioRepository).buscarPorId(144L);
    }

    @Test
    void debeLanzarExcepcionSiUsuarioNoExisteAlActualizarPreferencias() {
        ActualizarPreferenciasEstiloRequestDTO request =
                new ActualizarPreferenciasEstiloRequestDTO();

        request.setPreferencias(Set.of(Estilo.CASUAL));

        when(actualizarPreferenciasUseCase.actualizar(
                144L,
                request.getPreferencias()
        )).thenReturn(request.getPreferencias());

        when(usuarioRepository.buscarPorId(144L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> controller.actualizarPreferenciasEstilo(request)
        );
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

        when(usuarioRepository.buscarPorId(144L))
                .thenReturn(Optional.of(crearUsuario()));

        ResponseEntity<UsuarioResponseDTO> respuesta =
                controller.actualizarTallasHabituales(request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(144L, respuesta.getBody().getId());
        assertEquals(
                request.getTallas(),
                respuesta.getBody().getTallasHabituales()
        );
        assertEquals(
                request.getTallasPorTipoPrenda(),
                respuesta.getBody().getTallasPorTipoPrenda()
        );

        verify(actualizarTallasUseCase)
                .actualizar(144L, request.getTallasPorTipoPrenda());
        verify(usuarioRepository).buscarPorId(144L);
    }

    @Test
    void debeLanzarExcepcionSiUsuarioNoExisteAlActualizarTallas() {
        ActualizarTallasHabitualesRequestDTO request =
                new ActualizarTallasHabitualesRequestDTO();

        request.setTallas(Set.of(Talla.M));
        request.setTallasPorTipoPrenda(
                Map.of(TipoPrenda.SUPERIOR, Set.of("M"))
        );

        when(actualizarTallasUseCase.actualizar(
                144L,
                request.getTallasPorTipoPrenda()
        )).thenReturn(request.getTallasPorTipoPrenda());

        when(usuarioRepository.buscarPorId(144L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> controller.actualizarTallasHabituales(request)
        );
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

        ResponseEntity<BusquedaGuardadaResponseDTO> respuesta =
                controller.guardarBusqueda(request);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(5L, respuesta.getBody().getId());
        assertEquals("Chaquetas casuales",
                respuesta.getBody().getNombre());
        assertEquals("chaqueta", respuesta.getBody().getQuery());
        assertEquals(Estilo.CASUAL, respuesta.getBody().getEstilo());
        assertNotNull(respuesta.getBody().getFechaCreacion());
    }

    @Test
    void debeObtenerListaDeBusquedasGuardadas() {
        ResponseEntity<List<BusquedaGuardadaResponseDTO>> respuesta =
                controller.obtenerBusquedasGuardadas();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().isEmpty());
    }

    @Test
    void debeEliminarBusquedaGuardada() {
        ResponseEntity<Void> respuesta =
                controller.eliminarBusquedaGuardada(5L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
    }
}

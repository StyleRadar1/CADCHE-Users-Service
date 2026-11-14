package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarPreferenciasEstiloRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarTallasHabitualesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.GuardarBusquedaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.BusquedaGuardadaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarPreferenciasEstiloUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarTallasHabitualesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.UsuarioApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController implements UsuarioApi {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final ActualizarPreferenciasEstiloUseCase actualizarPreferenciasUseCase;
    private final ActualizarTallasHabitualesUseCase actualizarTallasUseCase;
    private final UsuarioRepositoryPort usuarioRepository;

    public UsuarioController(
            RegistrarUsuarioUseCase registrarUsuarioUseCase,
            ActualizarPreferenciasEstiloUseCase actualizarPreferenciasUseCase,
            ActualizarTallasHabitualesUseCase actualizarTallasUseCase,
            UsuarioRepositoryPort usuarioRepository) {

        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.actualizarPreferenciasUseCase = actualizarPreferenciasUseCase;
        this.actualizarTallasUseCase = actualizarTallasUseCase;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @Valid @RequestBody RegistrarUsuarioRequestDTO request) {

        UsuarioResponseDTO usuario = registrarUsuarioUseCase.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil() {
        return ResponseEntity.ok(UsuarioResponseDTO.builder()
                .id(12L)
                .nombre("Laura Gómez")
                .email("laura.gomez@styleradar.com")
                .telefono("+57 300 123 4567")
                .fechaRegistro(Instant.now())
                .preferenciasEstilo(Set.of(Estilo.CASUAL, Estilo.URBANO))
                .tallasHabituales(Set.of(Talla.M))
                .tallasPorTipoPrenda(Map.of(
                        TipoPrenda.SUPERIOR, Set.of("M"),
                        TipoPrenda.INFERIOR, Set.of("L"),
                        TipoPrenda.CALZADO, Set.of("38")))
                .build());
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(UsuarioResponseDTO.builder()
                .id(id)
                .nombre("Laura Gómez")
                .email("laura.gomez@styleradar.com")
                .telefono("+57 300 123 4567")
                .fechaRegistro(Instant.now())
                .preferenciasEstilo(Set.of(Estilo.CASUAL))
                .tallasHabituales(Set.of(Talla.M))
                .tallasPorTipoPrenda(Map.of(
                        TipoPrenda.SUPERIOR, Set.of("M")))
                .build());
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> actualizarPreferenciasEstilo(
            @Valid @RequestBody ActualizarPreferenciasEstiloRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long usuarioId = Long.valueOf(jwt.getSubject());

        Set<Estilo> preferencias = actualizarPreferenciasUseCase.actualizar(
                usuarioId,
                request.getPreferencias()
        );

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        UsuarioResponseDTO respuesta = UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .fechaRegistro(usuario.getFechaRegistro())
                .preferenciasEstilo(preferencias)
                .tallasHabituales(Set.of())
                .tallasPorTipoPrenda(Map.of())
                .build();

        return ResponseEntity.ok(respuesta);
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> actualizarTallasHabituales(
            @Valid @RequestBody ActualizarTallasHabitualesRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long usuarioId = Long.valueOf(jwt.getSubject());

        Map<TipoPrenda, Set<String>> tallas =
                actualizarTallasUseCase.actualizar(
                        usuarioId,
                        request.getTallasPorTipoPrenda()
                );

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        UsuarioResponseDTO respuesta = UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .fechaRegistro(usuario.getFechaRegistro())
                .preferenciasEstilo(Set.of())
                .tallasHabituales(request.getTallas())
                .tallasPorTipoPrenda(tallas)
                .build();

        return ResponseEntity.ok(respuesta);
    }

    @Override
    public ResponseEntity<BusquedaGuardadaResponseDTO> guardarBusqueda(
            @Valid @RequestBody GuardarBusquedaRequestDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BusquedaGuardadaResponseDTO.builder()
                        .id(5L)
                        .nombre(request.getNombre())
                        .query(request.getQuery())
                        .tipo(request.getTipo())
                        .color(request.getColor())
                        .talla(request.getTalla())
                        .precioMin(request.getPrecioMin())
                        .precioMax(request.getPrecioMax())
                        .marca(request.getMarca())
                        .estilo(request.getEstilo())
                        .latitud(request.getLatitud())
                        .longitud(request.getLongitud())
                        .fechaCreacion(Instant.now())
                        .build());
    }

    @Override
    public ResponseEntity<List<BusquedaGuardadaResponseDTO>> obtenerBusquedasGuardadas() {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Void> eliminarBusquedaGuardada(
            @PathVariable("busquedaId") Long busquedaId) {
        return ResponseEntity.noContent().build();
    }
}

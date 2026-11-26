package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarPreferenciasEstiloRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarDatosUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarTallasHabitualesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.GuardarBusquedaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.BusquedaGuardadaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.domain.event.BusquedaSincronizadaEvent;
import edu.dosw.styleradar.usuarios.domain.model.BusquedaGuardada;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarPreferenciasEstiloUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarDatosUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarTallasHabitualesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.BusquedaGuardadaRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.BusquedaSincronizadaEventPublisherPort;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.UsuarioApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UsuarioController implements UsuarioApi {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final ActualizarDatosUsuarioUseCase actualizarDatosUsuarioUseCase;
    private final ActualizarPreferenciasEstiloUseCase actualizarPreferenciasUseCase;
    private final ActualizarTallasHabitualesUseCase actualizarTallasUseCase;
    private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private final BusquedaGuardadaRepositoryPort busquedasRepository;
    private final BusquedaSincronizadaEventPublisherPort busquedaEventPublisher;

    public UsuarioController(
            RegistrarUsuarioUseCase registrarUsuarioUseCase,
            ActualizarDatosUsuarioUseCase actualizarDatosUsuarioUseCase,
            ActualizarPreferenciasEstiloUseCase actualizarPreferenciasUseCase,
            ActualizarTallasHabitualesUseCase actualizarTallasUseCase,
            ObtenerUsuarioUseCase obtenerUsuarioUseCase,
            BusquedaGuardadaRepositoryPort busquedasRepository,
            BusquedaSincronizadaEventPublisherPort busquedaEventPublisher) {

        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.actualizarDatosUsuarioUseCase = actualizarDatosUsuarioUseCase;
        this.actualizarPreferenciasUseCase = actualizarPreferenciasUseCase;
        this.actualizarTallasUseCase = actualizarTallasUseCase;
        this.obtenerUsuarioUseCase = obtenerUsuarioUseCase;
        this.busquedasRepository = busquedasRepository;
        this.busquedaEventPublisher = busquedaEventPublisher;
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @Valid @RequestBody RegistrarUsuarioRequestDTO request) {

        UsuarioResponseDTO usuario = registrarUsuarioUseCase.registrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(
            @PathVariable("id") Long id) {

        validarConsultaUsuario(id);
        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarDatosPersonales(
            @PathVariable("id") Long id,
            @Valid @RequestBody ActualizarDatosUsuarioRequestDTO request) {

        Long usuarioId = usuarioAutenticadoId();
        validarUsuarioSolicitado(id, usuarioId);
        actualizarDatosUsuarioUseCase.actualizar(
                usuarioId,
                request.name(),
                request.phone(),
                request.neighborhood()
        );
        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(usuarioId));
    }

    @Override
    @PutMapping("/{id}/style-preferences")
    public ResponseEntity<UsuarioResponseDTO> actualizarPreferenciasEstilo(
            @PathVariable("id") Long id,
            @Valid @RequestBody ActualizarPreferenciasEstiloRequestDTO request) {

        Long usuarioId = usuarioObjetivo(id);

        actualizarPreferenciasUseCase.actualizar(
                usuarioId,
                request.getPreferencias()
        );

        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(usuarioId));
    }

    @Override
    @PutMapping("/{id}/sizes")
    public ResponseEntity<UsuarioResponseDTO> actualizarTallasHabituales(
            @PathVariable("id") Long id,
            @Valid @RequestBody ActualizarTallasHabitualesRequestDTO request) {

        Long usuarioId = usuarioObjetivo(id);

        actualizarTallasUseCase.actualizar(
                usuarioId,
                request.getTallasPorTipoPrenda()
        );

        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(usuarioId));
    }

    @Override
    @PostMapping("/{id}/saved-searches")
    public ResponseEntity<BusquedaGuardadaResponseDTO> guardarBusqueda(
            @PathVariable("id") Long id,
            @Valid @RequestBody GuardarBusquedaRequestDTO request) {

        Long usuarioId = usuarioObjetivo(id);

        BusquedaGuardada busqueda = new BusquedaGuardada(
                null,
                usuarioId,
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
        );

        BusquedaGuardada guardada = busquedasRepository.guardar(busqueda);

        publicarEventoBusqueda(guardada, "CREADA");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aRespuesta(guardada));
    }

    public ResponseEntity<BusquedaGuardadaResponseDTO> actualizarBusquedaGuardada(
            Long id,
            Long searchId,
            @Valid @RequestBody GuardarBusquedaRequestDTO request) {

        Long usuarioId = usuarioObjetivo(id);

        Optional<BusquedaGuardada> encontrada =
                busquedasRepository.buscarPorIdYUsuarioId(searchId, usuarioId);

        if (encontrada.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BusquedaGuardada anterior = encontrada.get();

        BusquedaGuardada actualizada = new BusquedaGuardada(
                anterior.id(),
                usuarioId,
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
                anterior.fechaCreacion()
        );

        BusquedaGuardada guardada = busquedasRepository.guardar(actualizada);

        publicarEventoBusqueda(guardada, "ACTUALIZADA");

        return ResponseEntity.ok(aRespuesta(guardada));
    }

    @Override
    @GetMapping("/{id}/saved-searches")
    public ResponseEntity<List<BusquedaGuardadaResponseDTO>> obtenerBusquedasGuardadas(
            @PathVariable("id") Long id) {

        Long usuarioId = usuarioObjetivo(id);

        return ResponseEntity.ok(
                busquedasRepository.buscarPorUsuarioId(usuarioId)
                        .stream()
                        .map(this::aRespuesta)
                        .toList()
        );
    }

    @DeleteMapping("/{id}/saved-searches/{searchId}")
    @Override
    public ResponseEntity<Void> eliminarBusquedaGuardada(
            @PathVariable("id") Long id,
            @PathVariable("searchId") Long searchId) {

        Long usuarioId = usuarioObjetivo(id);

        Optional<BusquedaGuardada> encontrada =
                busquedasRepository.buscarPorIdYUsuarioId(searchId, usuarioId);

        if (encontrada.isPresent()) {
            BusquedaGuardada busqueda = encontrada.get();

            boolean eliminada = busquedasRepository.eliminarPorIdYUsuarioId(
                    searchId,
                    usuarioId
            );

            if (eliminada) {
                publicarEventoBusqueda(busqueda, "ELIMINADA");
            }
        }

        return ResponseEntity.noContent().build();
    }

    private void publicarEventoBusqueda(
            BusquedaGuardada busqueda,
            String accion) {

        BusquedaSincronizadaEvent evento = BusquedaSincronizadaEvent.desde(
                busqueda.id(),
                busqueda.usuarioId(),
                busqueda.nombre(),
                busqueda.query(),
                busqueda.tipo(),
                busqueda.color(),
                busqueda.talla(),
                busqueda.precioMin(),
                busqueda.precioMax(),
                busqueda.marca(),
                busqueda.estilo(),
                accion
        );

        busquedaEventPublisher.publicar(evento);
    }

    private BusquedaGuardadaResponseDTO aRespuesta(BusquedaGuardada busqueda) {

        return BusquedaGuardadaResponseDTO.builder()
                .id(busqueda.id())
                .nombre(busqueda.nombre())
                .query(busqueda.query())
                .tipo(busqueda.tipo())
                .color(busqueda.color())
                .talla(busqueda.talla())
                .precioMin(busqueda.precioMin())
                .precioMax(busqueda.precioMax())
                .marca(busqueda.marca())
                .estilo(busqueda.estilo())
                .latitud(busqueda.latitud())
                .longitud(busqueda.longitud())
                .fechaCreacion(busqueda.fechaCreacion())
                .build();
    }

    private Long usuarioAutenticadoId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        return Long.valueOf(jwt.getSubject());
    }

    private void validarUsuarioSolicitado(Long idSolicitado, Long idAutenticado) {
        if (!idSolicitado.equals(idAutenticado)) {
            throw new AccessDeniedException("Solo puede consultar su propio perfil");
        }
    }

    private Long usuarioObjetivo(Long idSolicitado) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        boolean administrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN_STYLERADAR"
                        .equals(authority.getAuthority()));
        if (administrador) {
            return idSolicitado;
        }
        Long usuarioId = usuarioAutenticadoId();
        validarUsuarioSolicitado(idSolicitado, usuarioId);
        return usuarioId;
    }

    private void validarConsultaUsuario(Long idSolicitado) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        boolean administrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN_STYLERADAR"
                        .equals(authority.getAuthority()));

        if (!administrador
                && !idSolicitado.equals(usuarioAutenticadoId())) {
            throw new AccessDeniedException(
                    "Solo puede consultar su propio perfil"
            );
        }
    }
}

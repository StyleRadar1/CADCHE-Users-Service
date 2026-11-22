package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarPreferenciasEstiloRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarTallasHabitualesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.GuardarBusquedaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.BusquedaGuardadaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.domain.event.BusquedaSincronizadaEvent;
import edu.dosw.styleradar.usuarios.domain.model.BusquedaGuardada;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarPreferenciasEstiloUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ActualizarTallasHabitualesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.BusquedaGuardadaRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.BusquedaSincronizadaEventPublisherPort;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.UsuarioApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController implements UsuarioApi {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final ActualizarPreferenciasEstiloUseCase actualizarPreferenciasUseCase;
    private final ActualizarTallasHabitualesUseCase actualizarTallasUseCase;
    private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private final BusquedaGuardadaRepositoryPort busquedasRepository;
    private final BusquedaSincronizadaEventPublisherPort busquedaEventPublisher;

    public UsuarioController(
            RegistrarUsuarioUseCase registrarUsuarioUseCase,
            ActualizarPreferenciasEstiloUseCase actualizarPreferenciasUseCase,
            ActualizarTallasHabitualesUseCase actualizarTallasUseCase,
            ObtenerUsuarioUseCase obtenerUsuarioUseCase,
            BusquedaGuardadaRepositoryPort busquedasRepository,
            BusquedaSincronizadaEventPublisherPort busquedaEventPublisher) {

        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
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

    @Override
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil() {

        Long usuarioId = usuarioAutenticadoId();

        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(usuarioId));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(id));
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> actualizarPreferenciasEstilo(
            @Valid @RequestBody ActualizarPreferenciasEstiloRequestDTO request) {

        Long usuarioId = usuarioAutenticadoId();

        actualizarPreferenciasUseCase.actualizar(
                usuarioId,
                request.getPreferencias()
        );

        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(usuarioId));
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> actualizarTallasHabituales(
            @Valid @RequestBody ActualizarTallasHabitualesRequestDTO request) {

        Long usuarioId = usuarioAutenticadoId();

        actualizarTallasUseCase.actualizar(
                usuarioId,
                request.getTallasPorTipoPrenda()
        );

        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(usuarioId));
    }

    @Override
    public ResponseEntity<BusquedaGuardadaResponseDTO> guardarBusqueda(
            @Valid @RequestBody GuardarBusquedaRequestDTO request) {

        Long usuarioId = usuarioAutenticadoId();

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

    @PutMapping("/me/busquedas-guardadas/{busquedaId}")
    public ResponseEntity<BusquedaGuardadaResponseDTO> actualizarBusquedaGuardada(
            @PathVariable("busquedaId") Long busquedaId,
            @Valid @RequestBody GuardarBusquedaRequestDTO request) {

        Long usuarioId = usuarioAutenticadoId();

        Optional<BusquedaGuardada> encontrada =
                busquedasRepository.buscarPorIdYUsuarioId(busquedaId, usuarioId);

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
    public ResponseEntity<List<BusquedaGuardadaResponseDTO>> obtenerBusquedasGuardadas() {

        Long usuarioId = usuarioAutenticadoId();

        return ResponseEntity.ok(
                busquedasRepository.buscarPorUsuarioId(usuarioId)
                        .stream()
                        .map(this::aRespuesta)
                        .toList()
        );
    }

    @DeleteMapping("/me/busquedas-guardadas/{busquedaId}")
    @Override
    public ResponseEntity<Void> eliminarBusquedaGuardada(
            @PathVariable("busquedaId") Long busquedaId) {

        Long usuarioId = usuarioAutenticadoId();

        Optional<BusquedaGuardada> encontrada =
                busquedasRepository.buscarPorIdYUsuarioId(busquedaId, usuarioId);

        if (encontrada.isPresent()) {
            BusquedaGuardada busqueda = encontrada.get();

            boolean eliminada = busquedasRepository.eliminarPorIdYUsuarioId(
                    busquedaId,
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
}


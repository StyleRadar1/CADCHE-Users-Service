package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.UsuarioApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController implements UsuarioApi {

    @Override
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistrarUsuarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDTO.builder()
                .id(12L)
                .nombre(request.getNombre())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .fechaRegistro(Instant.now())
                .preferenciasEstilo(Set.of())
                .tallasHabituales(Set.of())
                .tallasPorTipoPrenda(Map.of())
                .build());
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
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponseDTO.builder()
                .id(id)
                .nombre("Laura Gómez")
                .email("laura.gomez@styleradar.com")
                .telefono("+57 300 123 4567")
                .fechaRegistro(Instant.now())
                .preferenciasEstilo(Set.of(Estilo.CASUAL))
                .tallasHabituales(Set.of(Talla.M))
                .tallasPorTipoPrenda(Map.of(TipoPrenda.SUPERIOR, Set.of("M")))
                .build());
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> actualizarPreferenciasEstilo(
            @Valid @RequestBody ActualizarPreferenciasEstiloRequestDTO request) {
        return ResponseEntity.ok(UsuarioResponseDTO.builder()
                .id(12L)
                .nombre("Laura Gómez")
                .email("laura.gomez@styleradar.com")
                .telefono("+57 300 123 4567")
                .fechaRegistro(Instant.now())
                .preferenciasEstilo(request.getPreferencias())
                .tallasHabituales(Set.of(Talla.M))
                .tallasPorTipoPrenda(Map.of(TipoPrenda.SUPERIOR, Set.of("M")))
                .build());
    }

    @Override
    public ResponseEntity<UsuarioResponseDTO> actualizarTallasHabituales(
            @Valid @RequestBody ActualizarTallasHabitualesRequestDTO request) {
        return ResponseEntity.ok(UsuarioResponseDTO.builder()
                .id(12L)
                .nombre("Laura Gómez")
                .email("laura.gomez@styleradar.com")
                .telefono("+57 300 123 4567")
                .fechaRegistro(Instant.now())
                .preferenciasEstilo(Set.of(Estilo.CASUAL))
                .tallasHabituales(request.getTallas())
                .tallasPorTipoPrenda(request.getTallasPorTipoPrenda() != null
                        ? request.getTallasPorTipoPrenda()
                        : Map.of())
                .build());
    }

    @Override
    public ResponseEntity<BusquedaGuardadaResponseDTO> guardarBusqueda(
            @Valid @RequestBody GuardarBusquedaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(BusquedaGuardadaResponseDTO.builder()
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
    public ResponseEntity<Void> eliminarBusquedaGuardada(@PathVariable Long busquedaId) {
        return ResponseEntity.noContent().build();
    }
}

package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.request.BatchPublicProfilesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.MedidasCorporalesResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PublicProfileResponseDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerMedidasCorporalesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerPerfilPublicoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal/users")
@Tag(name = "internal-user-controller", description = "Endpoints internos para comunicación servicio a servicio")
public class InternalUserController {

    private final ObtenerMedidasCorporalesUseCase obtenerMedidasCorporalesUseCase;
    private final ObtenerPerfilPublicoUseCase obtenerPerfilPublicoUseCase;

    public InternalUserController(
            ObtenerMedidasCorporalesUseCase obtenerMedidasCorporalesUseCase,
            ObtenerPerfilPublicoUseCase obtenerPerfilPublicoUseCase) {
        this.obtenerMedidasCorporalesUseCase = obtenerMedidasCorporalesUseCase;
        this.obtenerPerfilPublicoUseCase = obtenerPerfilPublicoUseCase;
    }

    @Operation(summary = "Obtener medidas corporales de un usuario", description = "Uso interno para servicio VirtualTryOn")
    @GetMapping("/{userId}/measurements")
    public ResponseEntity<MedidasCorporalesResponseDTO> getMeasurements(
            @PathVariable("userId") Long userId) {

        MedidasCorporalesResponseDTO measurements =
                obtenerMedidasCorporalesUseCase.obtener(userId);

        return ResponseEntity.ok(measurements);
    }

    @Operation(summary = "Obtener perfil público corto de un usuario",
            description = "Devuelve únicamente id y displayName seguro (ej: 'Sofía L.') sin datos sensibles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil público obtenido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}/public-profile")
    public ResponseEntity<PublicProfileResponseDTO> getPublicProfile(
            @PathVariable("userId") Long userId) {

        PublicProfileResponseDTO profile = obtenerPerfilPublicoUseCase.obtenerPorId(userId);
        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "Obtener perfiles públicos en lote",
            description = "Consulta masiva de nombres públicos (máx. 100). Los usuarios inexistentes son omitidos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de perfiles públicos obtenida")
    })
    @PostMapping("/public-profiles")
    public ResponseEntity<List<PublicProfileResponseDTO>> getPublicProfiles(
            @Valid @RequestBody BatchPublicProfilesRequestDTO request) {

        List<PublicProfileResponseDTO> profiles =
                obtenerPerfilPublicoUseCase.obtenerPorLote(request.getIds());
        return ResponseEntity.ok(profiles);
    }
}

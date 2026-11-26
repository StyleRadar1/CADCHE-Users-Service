package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.response.MedidasCorporalesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerMedidasCorporalesUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/users")
@Tag(name = "internal-user-controller")
public class InternalUserController {

    private final ObtenerMedidasCorporalesUseCase obtenerMedidasCorporalesUseCase;

    public InternalUserController(
            ObtenerMedidasCorporalesUseCase obtenerMedidasCorporalesUseCase) {
        this.obtenerMedidasCorporalesUseCase = obtenerMedidasCorporalesUseCase;
    }

    @GetMapping("/{userId}/measurements")
    public ResponseEntity<MedidasCorporalesResponseDTO> getMeasurements(
            @PathVariable("userId") Long userId) {

        MedidasCorporalesResponseDTO measurements =
                obtenerMedidasCorporalesUseCase.obtener(userId);

        return ResponseEntity.ok(measurements);
    }
}

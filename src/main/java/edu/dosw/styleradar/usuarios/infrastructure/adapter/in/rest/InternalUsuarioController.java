package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.response.MedidasCorporalesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerMedidasCorporalesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/usuarios")
public class InternalUsuarioController {

    private final ObtenerMedidasCorporalesUseCase obtenerMedidasCorporalesUseCase;

    public InternalUsuarioController(
            ObtenerMedidasCorporalesUseCase obtenerMedidasCorporalesUseCase) {
        this.obtenerMedidasCorporalesUseCase = obtenerMedidasCorporalesUseCase;
    }

    @GetMapping("/{userId}/medidas")
    public ResponseEntity<MedidasCorporalesResponseDTO> obtenerMedidas(
            @PathVariable("userId") Long userId) {

        MedidasCorporalesResponseDTO medidas =
                obtenerMedidasCorporalesUseCase.obtener(userId);

        return ResponseEntity.ok(medidas);
    }
}

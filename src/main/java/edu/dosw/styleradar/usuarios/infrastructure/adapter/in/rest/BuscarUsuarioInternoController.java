package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.BuscarUsuarioInternoRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.VerificarCredencialesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.BuscarUsuarioInternoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Internal users")
@SecurityRequirement(name = "serviceBasicAuth")
public class BuscarUsuarioInternoController {

    private final BuscarUsuarioInternoUseCase buscarUsuarioInternoUseCase;

    public BuscarUsuarioInternoController(BuscarUsuarioInternoUseCase buscarUsuarioInternoUseCase) {
        this.buscarUsuarioInternoUseCase = buscarUsuarioInternoUseCase;
    }

    @PostMapping("/api/v1/internal/users/lookup")
    @Operation(summary = "Find user by email for password recovery")
    public ResponseEntity<VerificarCredencialesResponseDTO> buscar(
            @Valid @RequestBody BuscarUsuarioInternoRequestDTO request) {
        Usuario usuario = buscarUsuarioInternoUseCase.ejecutar(request.email());
        return ResponseEntity.ok(new VerificarCredencialesResponseDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getEstado(),
                usuario.getCorreoVerificado()));
    }
}

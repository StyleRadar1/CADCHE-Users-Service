package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.RestablecerContrasenaInternaRequestDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarContrasenaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Internal users")
@SecurityRequirement(name = "serviceBasicAuth")
public class RestablecerContrasenaInternaController {

    private final CambiarContrasenaUseCase cambiarContrasenaUseCase;

    public RestablecerContrasenaInternaController(CambiarContrasenaUseCase cambiarContrasenaUseCase) {
        this.cambiarContrasenaUseCase = cambiarContrasenaUseCase;
    }

    @PutMapping("/api/v1/internal/users/{id}/password")
    @Operation(summary = "Reset a user's password after verified recovery")
    public ResponseEntity<Void> restablecer(
            @PathVariable("id") Long id,
            @Valid @RequestBody RestablecerContrasenaInternaRequestDTO request) {
        cambiarContrasenaUseCase.restablecerInternamente(id, request.newPassword());
        return ResponseEntity.noContent().build();
    }
}

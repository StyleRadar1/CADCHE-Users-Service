package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.CambiarContrasenaRequestDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarContrasenaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class CambiarContrasenaController {

    private final CambiarContrasenaUseCase cambiarContrasenaUseCase;

    public CambiarContrasenaController(CambiarContrasenaUseCase cambiarContrasenaUseCase) {
        this.cambiarContrasenaUseCase = cambiarContrasenaUseCase;
    }

    @PutMapping("/{id}/password")
    @Operation(
            summary = "Cambiar contraseña propia",
            description = "Requiere la contraseña actual y solo permite cambiar la cuenta del token.")
    public ResponseEntity<Void> cambiar(
            @PathVariable("id") Long id,
            @Valid @RequestBody CambiarContrasenaRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long usuarioAutenticado = Long.valueOf(authentication.getName());
        if (!id.equals(usuarioAutenticado)) {
            throw new AccessDeniedException("Solo puede cambiar su propia contraseña");
        }
        cambiarContrasenaUseCase.ejecutar(
                id, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}

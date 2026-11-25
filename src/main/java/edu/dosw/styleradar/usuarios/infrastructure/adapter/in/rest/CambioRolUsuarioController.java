package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.CambiarRolUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;
import edu.dosw.styleradar.usuarios.application.usecase.CuentaAdministrativaMapper;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarRolUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "User account status")
@SecurityRequirement(name = "bearerAuth")
public class CambioRolUsuarioController {

    private final CambiarRolUsuarioUseCase cambiarRolUsuarioUseCase;

    public CambioRolUsuarioController(CambiarRolUsuarioUseCase cambiarRolUsuarioUseCase) {
        this.cambiarRolUsuarioUseCase = cambiarRolUsuarioUseCase;
    }

    @PatchMapping("/{id}/role")
    @Operation(
            summary = "Cambiar rol de usuario",
            description = "Solo ADMIN_STYLERADAR. Permite COMPRADOR, ADMIN_ALMACEN, REPRESENTANTE_FUNDACION y ADMIN_STYLERADAR.")
    public ResponseEntity<CuentaAdministrativaResponseDTO> cambiarRol(
            @PathVariable("id") Long id,
            @Valid @RequestBody CambiarRolUsuarioRequestDTO request) {
        return ResponseEntity.ok(CuentaAdministrativaMapper.aRespuesta(
                cambiarRolUsuarioUseCase.ejecutar(id, Rol.fromContract(request.role()))));
    }
}

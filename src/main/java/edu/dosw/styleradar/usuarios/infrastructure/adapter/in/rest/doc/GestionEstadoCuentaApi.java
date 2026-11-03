package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import edu.dosw.styleradar.usuarios.application.dto.request.CambioEstadoCuentaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.CuentasConsultaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.ErrorResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Estados de cuentas", description = "Administración de estados de cuentas de usuarios (SR-RF-66). Requiere ROLE_ADMIN_STYLERADAR y cuenta activa")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación correcta"),
        @ApiResponse(responseCode = "400", description = "Request inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "JWT ausente, inválido o cuenta no activa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Requiere ROLE_ADMIN_STYLERADAR", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta inexistente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflicto concurrente; reintente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "422", description = "Autocambio o pérdida del último administrador operativo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error técnico", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
})
public interface GestionEstadoCuentaApi {

    @GetMapping
    @Operation(
            summary = "Consultar cuentas de usuario (SR-RF-66)",
            description = "ROLE_ADMIN_STYLERADAR. Filtro opcional ACTIVA, INACTIVA, BLOQUEADA. "
                    + "ACTIVA incluye NULL histórico efectivo, sin migrarlo. page=0, size=20, máximo 100; orden id ASC; paginación en BD.")
    ResponseEntity<PageResponseDTO<CuentaAdministrativaResponseDTO>> consultar(
            @Valid @ParameterObject @ModelAttribute CuentasConsultaRequestDTO request);

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar cuenta y última decisión (SR-RF-66)",
            description = "ROLE_ADMIN_STYLERADAR. Estado efectivo y última modificación, sin credenciales ni datos privados. "
                    + "NULL histórico se muestra ACTIVA; metadatos nulos no representan una decisión.")
    ResponseEntity<CuentaAdministrativaResponseDTO> consultarUno(
            @Parameter(description = "Identificador de la cuenta a consultar", example = "12") @PathVariable Long id);

    @PatchMapping("/{id}/estado")
    @Operation(
            summary = "Activar, inactivar o bloquear una cuenta (SR-RF-66)",
            description = "ROLE_ADMIN_STYLERADAR. ACTIVA, INACTIVA, BLOQUEADA; todas las transiciones entre estados distintos. "
                    + "BLOQUEADA exige motivo no vacío; máximo 1000 caracteres. Actor y fecha del servidor. "
                    + "No permite autocambio, ni siquiera idempotente. Conserva otro administrador operativo.")
    ResponseEntity<CuentaAdministrativaResponseDTO> cambiar(
            @Parameter(description = "Identificador de la cuenta a modificar", example = "12") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "BLOQUEADA", value = "{\"estado\":\"BLOQUEADA\",\"motivo\":\"Incumplimiento de las condiciones\"}"),
                            @ExampleObject(name = "INACTIVA", value = "{\"estado\":\"INACTIVA\"}"),
                            @ExampleObject(name = "ACTIVA", value = "{\"estado\":\"ACTIVA\",\"motivo\":\"Revisión concluida\"}")
                    }))
            @Valid @RequestBody CambioEstadoCuentaRequestDTO request);
}


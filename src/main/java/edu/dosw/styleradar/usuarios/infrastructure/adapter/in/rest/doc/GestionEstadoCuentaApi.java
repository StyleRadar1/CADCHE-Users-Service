package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@Tag(
        name = "User account status",
        description = "Administración de estados de cuentas de usuarios (SR-RF-66). "
                + "Requiere ROLE_ADMIN_STYLERADAR y cuenta activa"
)
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operación correcta"),
        @ApiResponse(
                responseCode = "400",
                description = "Request inválido",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "JWT ausente, inválido o cuenta no activa",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Requiere ROLE_ADMIN_STYLERADAR",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Cuenta inexistente",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Conflicto concurrente; reintente",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "422",
                description = "Autocambio o pérdida del último administrador operativo",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Error técnico",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        )
})
public interface GestionEstadoCuentaApi {

    @GetMapping
    @Operation(
            summary = "Consultar usuarios",
            description = "ROLE_ADMIN_STYLERADAR. Optional filters status, role and q "
                    + "(nombre o correo). ACTIVA incluye NULL histórico efectivo, sin migrarlo. "
                    + "page=0, size=20, entre 1 y 100; orden id ASC; paginación en BD. "
                    + "La respuesta incluye barrio cuando está informado."
    )
    ResponseEntity<PageResponseDTO<CuentaAdministrativaResponseDTO>> consultar(
            @Valid @ParameterObject @ModelAttribute CuentasConsultaRequestDTO request
    );

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar usuario y última decisión",
            description = "ROLE_ADMIN_STYLERADAR. Effective accountStatus and last change, "
                    + "sin credenciales ni datos privados. NULL histórico se muestra ACTIVA; "
                    + "metadatos nulos no representan una decisión."
    )
    ResponseEntity<CuentaAdministrativaResponseDTO> consultarUno(
            @Parameter(
                    description = "Identificador de la cuenta a consultar",
                    example = "12"
            )
            @PathVariable("id") Long id
    );

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Activate, deactivate or block a user account",
            description = "ROLE_ADMIN_STYLERADAR. ACTIVE, INACTIVE, BLOCKED; "
                    + "all transitions between distinct states. BLOCKED requires "
                    + "a non-empty reason; maximum 1000 characters. Actor and timestamp are server generated. "
                    + "No permite autocambio, ni siquiera idempotente. "
                    + "Conserva otro administrador operativo."
    )
    ResponseEntity<CuentaAdministrativaResponseDTO> cambiar(
            @Parameter(
                    description = "Identificador de la cuenta a modificar",
                    example = "12"
            )
            @PathVariable("id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(
                                    name = "BLOCKED",
                                    value = "{\"status\":\"BLOCKED\",\"reason\":\"Incumplimiento de las condiciones\"}"
                            ),
                            @ExampleObject(
                                    name = "INACTIVE",
                                    value = "{\"status\":\"INACTIVE\"}"
                            ),
                            @ExampleObject(
                                    name = "ACTIVE",
                                    value = "{\"status\":\"ACTIVE\",\"reason\":\"Revisión concluida\"}"
                            )
                    })
            )
            @Valid @RequestBody CambioEstadoCuentaRequestDTO request
    );

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user account (SR-RF-115)",
            description = "ROLE_ADMIN_STYLERADAR. Permanently deletes the user account. "
                    + "The front end must ask for confirmation before calling this endpoint."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User account deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    ResponseEntity<Void> eliminar(
            @Parameter(description = "User account identifier", example = "12")
            @PathVariable("id") Long id
    );
}

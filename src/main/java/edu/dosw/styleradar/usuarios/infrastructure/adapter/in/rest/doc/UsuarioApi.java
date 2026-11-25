package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarPreferenciasEstiloRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarDatosUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarTallasHabitualesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.GuardarBusquedaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.BusquedaGuardadaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.ErrorResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
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
        name = "Users",
        description = "User registration, profile, style preferences, usual sizes and saved searches"
)
public interface UsuarioApi {

    @Operation(
            summary = "Register buyer",
            description = "Public endpoint. Creates a buyer account with an encrypted BCrypt password, validates email uniqueness and records Habeas Data consent.",
            security = {},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegistrarUsuarioRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "RegisterUser",
                                    value = "{\"name\":\"Laura Gómez\",\"email\":\"laura.gomez@styleradar.com\",\"phone\":\"+57 300 123 4567\",\"password\":\"StyleRadar2026*\",\"dataPolicyAccepted\":true}"
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or missing Habeas Data consent",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Email is already in use",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping
    ResponseEntity<UsuarioResponseDTO> registrar(
            @Valid @RequestBody RegistrarUsuarioRequestDTO request
    );

    @Operation(
            summary = "Get user profile by ID",
            description = "Returns the authenticated user's profile when id matches the JWT subject. StyleRadar administrators may query any user.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing, invalid or expired JWT",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("/{id}")
    ResponseEntity<UsuarioResponseDTO> obtenerPorId(
            @Parameter(
                    description = "Unique user identifier",
                    example = "12"
            )
            @PathVariable("id") Long id
    );

    @Operation(
            summary = "Update personal details",
            description = "Buyer-only operation. The id must match the JWT subject. "
                    + "name is required; phone and neighborhood are optional and omitted values are preserved. "
                    + "Returns the full profile, matching GET /api/v1/users/{id}.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActualizarDatosUsuarioRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "UpdatePersonalDetails",
                                    value = "{\"name\":\"Laura Gómez\",\"phone\":\"+573001234567\",\"neighborhood\":\"Chapinero\"}"
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personal details updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data; validationErrors contains field-level errors",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Missing, invalid or expired JWT"),
            @ApiResponse(responseCode = "403", description = "Only the authenticated buyer can update their profile"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    ResponseEntity<UsuarioResponseDTO> actualizarDatosPersonales(
            @PathVariable("id") Long id,
            @Valid @RequestBody ActualizarDatosUsuarioRequestDTO request
    );

    @Operation(
            summary = "Update my style preferences",
            description = "Buyer-only operation. Replaces the authenticated buyer's style preferences.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActualizarPreferenciasEstiloRequestDTO.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Style preferences updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing, invalid or expired JWT",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only buyers can update style preferences",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping("/{id}/style-preferences")
    ResponseEntity<UsuarioResponseDTO> actualizarPreferenciasEstilo(
            @PathVariable("id") Long id,
            @Valid @RequestBody ActualizarPreferenciasEstiloRequestDTO request
    );

    @Operation(
            summary = "Update my usual sizes",
            description = "Buyer-only operation. Replaces the authenticated buyer's usual sizes by garment type.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActualizarTallasHabitualesRequestDTO.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usual sizes updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing, invalid or expired JWT",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only buyers can update usual sizes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping("/{id}/sizes")
    ResponseEntity<UsuarioResponseDTO> actualizarTallasHabituales(
            @PathVariable("id") Long id,
            @Valid @RequestBody ActualizarTallasHabitualesRequestDTO request
    );

    @Operation(
            summary = "Save a search",
            description = "Buyer-only operation. Stores search text, filters and geographic location for later use.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GuardarBusquedaRequestDTO.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Search saved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusquedaGuardadaResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing, invalid or expired JWT",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only buyers can save searches",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("/{id}/saved-searches")
    ResponseEntity<BusquedaGuardadaResponseDTO> guardarBusqueda(
            @PathVariable("id") Long id,
            @Valid @RequestBody GuardarBusquedaRequestDTO request
    );

    @Operation(
            summary = "Get my saved searches",
            description = "Buyer-only operation. Lists saved searches belonging to the authenticated buyer.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Saved searches retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusquedaGuardadaResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing, invalid or expired JWT",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only buyers can access saved searches",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("/{id}/saved-searches")
    ResponseEntity<List<BusquedaGuardadaResponseDTO>> obtenerBusquedasGuardadas(
            @PathVariable("id") Long id
    );

    @Operation(
            summary = "Delete a saved search",
            description = "Buyer-only operation. Deletes a saved search belonging to the authenticated buyer.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Saved search deleted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing, invalid or expired JWT",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only buyers can delete saved searches",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Saved search not found or does not belong to the user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @DeleteMapping("/{id}/saved-searches/{searchId}")
    ResponseEntity<Void> eliminarBusquedaGuardada(
            @PathVariable("id") Long id,
            @Parameter(
                    description = "Saved search identifier",
                    example = "5"
            )
            @PathVariable("searchId") Long searchId
    );
}

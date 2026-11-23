package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import edu.dosw.styleradar.usuarios.application.dto.request.PersonalizacionRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.ErrorResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.RecomendacionesResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
        name = "Personalization",
        description = "Personalized feed and clothing recommendations for the authenticated user (SR-RF-05, SR-RF-06)"
)
@SecurityRequirement(name = "bearerAuth")
public interface PersonalizacionApi {

    @Operation(
            summary = "Get my personalized feed (SR-RF-05)",
            description = "Prioritizes clothing items according to the buyer's style preferences and usual sizes. "
                    + "Ties are resolved by publication date descending and item ID ascending. "
                    + "When no preferences exist, the general available feed is returned. "
                    + "page=0 and size=20 by default; page>=0, size between 1 and 20."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated feed, including empty pages"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing, invalid JWT or inactive account",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found during a valid authenticated request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping({"/{id}/feed", "/me/feed"})
    ResponseEntity<PageResponseDTO<CatalogoItemResponseDTO>> feed(
            @org.springframework.web.bind.annotation.PathVariable(value = "id", required = false) Long id,
            @Valid @ParameterObject @ModelAttribute PersonalizacionRequestDTO request
    );

    @Operation(
            summary = "Get my clothing recommendations (SR-RF-06)",
            description = "Returns paginated clothing recommendations according to the buyer's profile. "
                    + "page=0 and size=20 by default; page>=0, size between 1 and 20."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated recommendations"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing, invalid JWT or inactive account",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found during a valid authenticated request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping({
            "/{id}/recommendations",
            "/me/recommendations",
            "/me/recomendaciones",
            "/recommendations",
            "/recomendaciones"
    })
    ResponseEntity<RecomendacionesResponseDTO> recomendaciones(
            @org.springframework.web.bind.annotation.PathVariable(value = "id", required = false) Long id,
            @Valid @ParameterObject @ModelAttribute PersonalizacionRequestDTO request
    );
}

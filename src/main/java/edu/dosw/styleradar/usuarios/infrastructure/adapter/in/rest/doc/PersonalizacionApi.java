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
        name = "Personalización",
        description = "Feed personalizado y recomendaciones de prendas del usuario autenticado (SR-RF-05, SR-RF-06)"
)
@SecurityRequirement(name = "bearerAuth")
public interface PersonalizacionApi {

    @Operation(
            summary = "Obtener mi feed personalizado (SR-RF-05)",
            description = "Prioriza prendas según las preferencias de estilo y tallas del comprador. "
                    + "Desempata por fecha de publicación descendente e ID ascendente. "
                    + "Sin preferencias retorna el feed general disponible. "
                    + "page=0 y size=20 por defecto; page>=0, size entre 1 y 20."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feed paginado, incluyendo páginas vacías"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paginación inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente, inválido o cuenta no activa",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario inexistente durante una consulta autenticada válida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/feed")
    ResponseEntity<PageResponseDTO<CatalogoItemResponseDTO>> feed(
            @Valid @ParameterObject @ModelAttribute PersonalizacionRequestDTO request
    );

    @Operation(
            summary = "Obtener mis recomendaciones de prendas (SR-RF-06)",
            description = "Devuelve recomendaciones paginadas según el perfil del comprador. "
                    + "page=0 y size=20 por defecto; page>=0, size entre 1 y 20."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recomendaciones paginadas"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paginación inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente, inválido o cuenta no activa",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario inexistente durante una consulta autenticada válida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/recomendaciones")
    ResponseEntity<RecomendacionesResponseDTO> recomendaciones(
            @Valid @ParameterObject @ModelAttribute PersonalizacionRequestDTO request
    );
}


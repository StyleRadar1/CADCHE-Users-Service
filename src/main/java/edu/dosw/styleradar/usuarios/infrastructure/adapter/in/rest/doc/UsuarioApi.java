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

@Tag(name = "Usuarios", description = "Registro con Habeas Data, perfil, preferencias de estilo, tallas habituales por tipo de prenda y búsquedas guardadas del usuario autenticado (SR-RF-01, SR-RF-03, SR-RF-04, SR-RF-28)")
public interface UsuarioApi {

    @Operation(
            summary = "Registrar usuario comprador (SR-RF-01 / SR-RNF-02)",
            description = "Público. Crea un nuevo usuario comprador en la plataforma con contraseña cifrada mediante BCrypt, validando correo único y registrando el consentimiento expreso de Habeas Data (Ley 1581).",
            security = {},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegistrarUsuarioRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "RegistrarUsuario",
                                    value = "{\"nombre\":\"Laura Gómez\",\"email\":\"laura.gomez@styleradar.com\",\"telefono\":\"+57 300 123 4567\",\"password\":\"StyleRadar2026*\",\"aceptaHabeasData\":true}"))))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "UsuarioCreado",
                                    value = "{\"id\":12,\"nombre\":\"Laura Gómez\",\"email\":\"laura.gomez@styleradar.com\",\"telefono\":\"+57 300 123 4567\",\"fechaRegistro\":\"2026-11-03T01:30:00Z\",\"preferenciasEstilo\":[],\"tallasHabituales\":[],\"tallasPorTipoPrenda\":{}}"))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o falta de aceptación de Habeas Data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "DatosInvalidos",
                                    value = "{\"timestamp\":\"2026-11-03T01:30:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"La solicitud contiene datos inválidos\",\"path\":\"/api/v1/usuarios\",\"validationErrors\":{\"email\":\"El formato del email es inválido\"}}"))),
            @ApiResponse(
                    responseCode = "422",
                    description = "El email ya está en uso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "EmailDuplicado",
                                    value = "{\"timestamp\":\"2026-11-03T01:30:00\",\"status\":422,\"error\":\"Unprocessable Entity\",\"message\":\"El correo electrónico ya se encuentra registrado\",\"path\":\"/api/v1/usuarios\",\"validationErrors\":{}}"))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistrarUsuarioRequestDTO request);

    @Operation(
            summary = "Consultar mi perfil",
            description = "ROLE_COMPRADOR. Retorna el perfil del usuario autenticado junto con sus preferencias de estilo y tallas habituales por tipo de prenda.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil consultado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "MiPerfil",
                                    value = "{\"id\":12,\"nombre\":\"Laura Gómez\",\"email\":\"laura.gomez@styleradar.com\",\"telefono\":\"+57 300 123 4567\",\"fechaRegistro\":\"2026-11-03T01:30:00Z\",\"preferenciasEstilo\":[\"CASUAL\",\"URBANO\"],\"tallasHabituales\":[\"M\"],\"tallasPorTipoPrenda\":{\"SUPERIOR\":[\"M\"],\"INFERIOR\":[\"L\"],\"CALZADO\":[\"38\"]}}"))),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente, inválido o expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Rol sin permiso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/me")
    ResponseEntity<UsuarioResponseDTO> obtenerPerfil();

    @Operation(
            summary = "Consultar usuario por ID",
            description = "ROLE_ADMIN_STYLERADAR. Retorna el perfil de cualquier usuario registrado por su identificador.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario consultado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente, inválido o expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Rol sin permiso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "UsuarioNoEncontrado",
                                    value = "{\"timestamp\":\"2026-11-03T01:30:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Usuario no encontrado\",\"path\":\"/api/v1/usuarios/999\",\"validationErrors\":{}}"))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    ResponseEntity<UsuarioResponseDTO> obtenerPorId(
            @Parameter(description = "Identificador único del usuario", example = "12") @PathVariable Long id);

    @Operation(
            summary = "Configurar mis preferencias de estilo (SR-RF-03)",
            description = "ROLE_COMPRADOR. Reemplaza las preferencias de estilo del usuario autenticado (CASUAL, FORMAL, DEPORTIVO, URBANO, VINTAGE).",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActualizarPreferenciasEstiloRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "PreferenciasEstilo",
                                    value = "{\"preferencias\":[\"CASUAL\",\"URBANO\",\"VINTAGE\"]}"))))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Preferencias de estilo actualizadas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente, inválido o expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Rol sin permiso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping({"/me/preferencias-estilo", "/me/preferencias"})
    ResponseEntity<UsuarioResponseDTO> actualizarPreferenciasEstilo(
            @Valid @RequestBody ActualizarPreferenciasEstiloRequestDTO request);

    @Operation(
            summary = "Configurar mis tallas habituales por tipo de prenda (SR-RF-04)",
            description = "ROLE_COMPRADOR. Reemplaza las tallas habituales del usuario autenticado tanto en conjunto general (XS..XXL) como discriminadas por tipo de prenda (SUPERIOR, INFERIOR, VESTIDO, CALZADO numérico 35..45).",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ActualizarTallasHabitualesRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "TallasHabitualesPorTipoPrenda",
                                    value = "{\"tallas\":[\"M\",\"L\"],\"tallasPorTipoPrenda\":{\"SUPERIOR\":[\"M\"],\"INFERIOR\":[\"L\"],\"CALZADO\":[\"40\"]}}"))))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tallas habituales actualizadas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente, inválido o expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Rol sin permiso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/me/tallas-habituales")
    ResponseEntity<UsuarioResponseDTO> actualizarTallasHabituales(
            @Valid @RequestBody ActualizarTallasHabitualesRequestDTO request);

    @Operation(
            summary = "Guardar búsqueda en mi perfil (SR-RF-28)",
            description = "ROLE_COMPRADOR. Almacena texto, filtros y ubicación geográfica (latitud/longitud) utilizados en una búsqueda para consultarla posteriormente o usarla como base para el feed personalizado y alertas.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GuardarBusquedaRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "GuardarBusqueda",
                                    value = "{\"nombre\":\"Chaquetas negras casuales\",\"query\":\"chaqueta denim\",\"tipo\":\"SUPERIOR\",\"color\":\"Negro\",\"talla\":\"M\",\"precioMin\":50000.0,\"precioMax\":180000.0,\"marca\":\"Levi's\",\"estilo\":\"CASUAL\",\"latitud\":4.6761,\"longitud\":-74.0482}"))))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Búsqueda guardada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusquedaGuardadaResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente, inválido o expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Rol sin permiso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/me/busquedas-guardadas")
    ResponseEntity<BusquedaGuardadaResponseDTO> guardarBusqueda(@Valid @RequestBody GuardarBusquedaRequestDTO request);

    @Operation(
            summary = "Consultar mis búsquedas guardadas (SR-RF-28)",
            description = "ROLE_COMPRADOR. Lista todas las búsquedas guardadas en el perfil del comprador autenticado.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsquedas guardadas consultadas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BusquedaGuardadaResponseDTO.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente, inválido o expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Rol sin permiso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/me/busquedas-guardadas")
    ResponseEntity<List<BusquedaGuardadaResponseDTO>> obtenerBusquedasGuardadas();

    @Operation(
            summary = "Eliminar mi búsqueda guardada (SR-RF-28)",
            description = "ROLE_COMPRADOR. Elimina una búsqueda guardada que pertenezca al comprador autenticado.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Búsqueda guardada eliminada"),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT ausente, inválido o expirado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Rol sin permiso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Búsqueda no encontrada o ajena",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/me/busquedas-guardadas/{busquedaId}")
    ResponseEntity<Void> eliminarBusquedaGuardada(
            @Parameter(description = "Identificador único de la búsqueda guardada", example = "5") @PathVariable Long busquedaId);
}

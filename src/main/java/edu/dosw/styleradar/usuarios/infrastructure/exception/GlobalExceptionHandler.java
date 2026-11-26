package edu.dosw.styleradar.usuarios.infrastructure.exception;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import edu.dosw.styleradar.usuarios.application.dto.response.ErrorResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.exception.ContrasenaActualIncorrectaException;
import edu.dosw.styleradar.usuarios.domain.exception.IdentidadExternaConflictivaException;
import edu.dosw.styleradar.usuarios.domain.exception.CambioEstadoNoPermitidoException;
import edu.dosw.styleradar.usuarios.domain.exception.CuentaNoActivaException;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoPorEmailException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import edu.dosw.styleradar.usuarios.domain.exception.CatalogoNoDisponibleException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        return handleValidationErrors(exception.getBindingResult(), request);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDTO> handleBindingValidation(
            BindException exception, HttpServletRequest request) {
        return handleValidationErrors(exception.getBindingResult(), request);
    }

    private ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            BindingResult bindingResult, HttpServletRequest request) {
        Map<String, String> validationErrors = new LinkedHashMap<>();

        bindingResult.getFieldErrors().forEach(error ->
                validationErrors.putIfAbsent(
                        nombreCampoApi(bindingResult.getTarget(), error.getField()),
                        error.getDefaultMessage()));

        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_DATA",
                "La solicitud contiene datos inválidos",
                request.getRequestURI(), validationErrors);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleInvalidHttpRequest(
            Exception exception, HttpServletRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_DATA",
                "La solicitud contiene parámetros inválidos",
                request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidData(
            IllegalArgumentException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_DATA",
                exception.getMessage(), request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request) {
        log.warn("Acceso denegado (403) en ruta {}", request.getRequestURI());
        return buildResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "Acceso denegado: no cuenta con permisos suficientes",
                request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
            NoResourceFoundException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND",
                "El recurso solicitado no existe",
                request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                "El método HTTP no está permitido para esta ruta",
                request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailDuplicado(
            EmailYaRegistradoException exception,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.CONFLICT, "EMAIL_ALREADY_REGISTERED",
                exception.getMessage(),
                request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(ContrasenaActualIncorrectaException.class)
    public ResponseEntity<ErrorResponseDTO> handleContrasenaActualIncorrecta(
            ContrasenaActualIncorrectaException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_CURRENT_PASSWORD",
                exception.getMessage(), request.getRequestURI(),
                Map.of("currentPassword", exception.getMessage()));
    }

    @ExceptionHandler(IdentidadExternaConflictivaException.class)
    public ResponseEntity<ErrorResponseDTO> handleIdentidadExternaConflictiva(
            IdentidadExternaConflictivaException exception,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.CONFLICT, "EXTERNAL_IDENTITY_CONFLICT",
                exception.getMessage(),
                request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(CuentaNoActivaException.class)
    public ResponseEntity<ErrorResponseDTO> handleCuentaNoActiva(
            CuentaNoActivaException exception,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.FORBIDDEN, "ACCOUNT_NOT_ACTIVE",
                exception.getMessage(),
                request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(CambioEstadoNoPermitidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleCambioEstadoNoPermitido(
            CambioEstadoNoPermitidoException exception,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.FORBIDDEN, exception.getCode(),
                exception.getMessage(),
                request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsuarioNoEncontrado(
            UsuarioNoEncontradoException exception,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                exception.getMessage(),
                request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(UsuarioNoEncontradoPorEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsuarioNoEncontradoPorEmail(
            UsuarioNoEncontradoPorEmailException exception,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                exception.getMessage(), request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(CatalogoNoDisponibleException.class)
    public ResponseEntity<ErrorResponseDTO> handleCatalogoNoDisponible(
            CatalogoNoDisponibleException exception,
            HttpServletRequest request) {

        log.error("No fue posible consultar el catálogo", exception);

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "CATALOG_UNAVAILABLE",
                exception.getMessage(),
                request.getRequestURI(),
                Map.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnexpected(
            Exception exception, HttpServletRequest request) {

        log.error("Error no controlado al procesar la ruta {}",
                request.getRequestURI(), exception);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Ocurrió un error interno. Intente nuevamente más tarde.",
                request.getRequestURI(), Map.of());
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(
            HttpStatus status,
            String code,
            String message,
            String path,
            Map<String, String> validationErrors) {

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .type(tipoUriError(code))
                .title(status.getReasonPhrase())
                .status(status.value())
                .code(code)
                .detail(message)
                .instance(path)
                .errors(validationErrors)
                .build();

        return ResponseEntity.status(status)
                .contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON)
                .body(errorResponse);
    }

    private String tipoUriError(String code) {
        String slug = code.toLowerCase(java.util.Locale.ROOT).replace('_', '-');
        return "https://api.styleradar.com/errors/" + slug;
    }

    private String nombreCampoApi(Object target, String nombreCampoJava) {
        if (target == null) {
            return nombreCampoJava;
        }

        String[] partes = nombreCampoJava.split("\\.");
        Class<?> tipoActual = target.getClass();

        for (int index = 0; index < partes.length; index++) {
            try {
                Field field = tipoActual.getDeclaredField(partes[index]);
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && !jsonProperty.value().isBlank()) {
                    partes[index] = jsonProperty.value();
                }
                tipoActual = field.getType();
            } catch (NoSuchFieldException exception) {
                break;
            }
        }

        return String.join(".", partes);
    }
}


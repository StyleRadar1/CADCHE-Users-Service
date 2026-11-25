package edu.dosw.styleradar.usuarios.infrastructure.exception;

import edu.dosw.styleradar.usuarios.application.dto.response.ErrorResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.ActualizarPreferenciasEstiloRequestDTO;
import edu.dosw.styleradar.usuarios.domain.exception.CambioEstadoNoPermitidoException;
import edu.dosw.styleradar.usuarios.domain.exception.CatalogoNoDisponibleException;
import edu.dosw.styleradar.usuarios.domain.exception.CuentaNoActivaException;
import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.exception.ContrasenaActualIncorrectaException;
import edu.dosw.styleradar.usuarios.domain.exception.IdentidadExternaConflictivaException;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/users");
    }

    @Test
    void debeManejarEmailDuplicado() {
        EmailYaRegistradoException exception =
                new EmailYaRegistradoException("test@email.com");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleEmailDuplicado(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("test@email.com"));
        assertEquals("/api/v1/users", response.getBody().getPath());
    }

    @Test
    void convierteDatosInvalidosEnProblemDetailConCodigo() {
        ResponseEntity<ErrorResponseDTO> response =
                handler.handleInvalidData(new IllegalArgumentException("Rol no permitido"), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_DATA", response.getBody().getCode());
        assertEquals("Rol no permitido", response.getBody().getDetail());
        assertTrue(response.getBody().getErrors().isEmpty());
    }

    @Test
    void devuelveCodigoCuandoLaContrasenaActualEsIncorrecta() {
        ResponseEntity<ErrorResponseDTO> response = handler.handleContrasenaActualIncorrecta(
                new ContrasenaActualIncorrectaException(), request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("INVALID_CURRENT_PASSWORD", response.getBody().getCode());
        assertEquals("/api/v1/users", response.getBody().getInstance());
        assertTrue(response.getBody().getErrors().containsKey("currentPassword"));
    }

    @Test
    void debeManejarIdentidadExternaConflictiva() {
        IdentidadExternaConflictivaException exception =
                new IdentidadExternaConflictivaException("Identidad Google conflictiva");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleIdentidadExternaConflictiva(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Identidad Google conflictiva", response.getBody().getMessage());
        assertEquals("/api/v1/users", response.getBody().getPath());
        assertTrue(response.getBody().getValidationErrors().isEmpty());
    }

    @Test
    void debeManejarCuentaNoActiva() {
        CuentaNoActivaException exception =
                new CuentaNoActivaException("La cuenta está inactiva");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleCuentaNoActiva(exception, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("La cuenta está inactiva",
                response.getBody().getMessage());
    }

    @Test
    void debeManejarCambioEstadoNoPermitido() {
        CambioEstadoNoPermitidoException exception =
                new CambioEstadoNoPermitidoException("Cambio no permitido");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleCambioEstadoNoPermitido(exception, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Cambio no permitido",
                response.getBody().getMessage());
    }

    @Test
    void debeManejarUsuarioNoEncontrado() {
        UsuarioNoEncontradoException exception =
                new UsuarioNoEncontradoException(10L);

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleUsuarioNoEncontrado(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("10"));
    }

    @Test
    void debeManejarCatalogoNoDisponible() {
        CatalogoNoDisponibleException exception =
                new CatalogoNoDisponibleException(
                        "Catálogo no disponible", new RuntimeException());

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleCatalogoNoDisponible(exception, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Catálogo no disponible",
                response.getBody().getMessage());
    }

    @Test
    void debeManejarErrorInesperado() {
        Exception exception = new Exception("Error interno");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleUnexpected(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(
                "Ocurrió un error interno. Intente nuevamente más tarde.",
                response.getBody().getMessage());
    }

    @Test
    void debeManejarMetodoHttpNoPermitido() {
        HttpRequestMethodNotSupportedException exception =
                new HttpRequestMethodNotSupportedException("POST");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleMethodNotSupported(exception, request);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    void debeManejarSolicitudNoLegible() {
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("JSON inválido");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleInvalidHttpRequest(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("La solicitud contiene parámetros inválidos",
                response.getBody().getMessage());
    }

    @Test
    void debeManejarTipoDeParametroIncorrecto() {
        MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException(
                        "abc", Long.class, "id", null,
                        new IllegalArgumentException());

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleInvalidHttpRequest(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void debeManejarTipoDeContenidoNoSoportado() {
        HttpMediaTypeNotSupportedException exception =
                new HttpMediaTypeNotSupportedException("Tipo no soportado");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleInvalidHttpRequest(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void debeManejarErroresDeBinding() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "usuario");

        bindingResult.addError(
                new FieldError("usuario", "email", "Email inválido"));

        BindException exception = new BindException(bindingResult);

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleBindingValidation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email inválido",
                response.getBody().getValidationErrors().get("email"));
    }

    @Test
    void convierteElNombreDeCampoDeValidacionAlContratoJson() {
        ActualizarPreferenciasEstiloRequestDTO target =
                new ActualizarPreferenciasEstiloRequestDTO();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "preferences");
        bindingResult.addError(new FieldError(
                "preferences", "preferencias", "Debe seleccionar un estilo"));

        ResponseEntity<ErrorResponseDTO> response = handler.handleBindingValidation(
                new BindException(bindingResult), request);

        assertEquals("Debe seleccionar un estilo",
                response.getBody().getValidationErrors().get("styles"));
    }
}

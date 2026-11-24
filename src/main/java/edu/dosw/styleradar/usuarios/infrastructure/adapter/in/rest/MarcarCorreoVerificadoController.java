package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.domain.port.in.MarcarCorreoVerificadoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarcarCorreoVerificadoController {

    private final MarcarCorreoVerificadoUseCase marcarCorreoVerificadoUseCase;

    public MarcarCorreoVerificadoController(
            MarcarCorreoVerificadoUseCase marcarCorreoVerificadoUseCase) {
        this.marcarCorreoVerificadoUseCase = marcarCorreoVerificadoUseCase;
    }

    @PatchMapping({
            "/api/v1/internal/users/{id}/email-verification",
            "/api/v1/internal/usuarios/{id}/correo-verificado"
    })
    public ResponseEntity<Void> marcarCorreoVerificado(
            @PathVariable("id") Long id) {

        marcarCorreoVerificadoUseCase.ejecutar(id);

        return ResponseEntity.noContent().build();
    }
}

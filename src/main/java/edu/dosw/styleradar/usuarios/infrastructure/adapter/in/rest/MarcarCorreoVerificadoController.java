package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.domain.port.in.MarcarCorreoVerificadoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({
        "/api/v1/internal/users",
        "/api/v1/internal/usuarios"
})
public class MarcarCorreoVerificadoController {

    private final MarcarCorreoVerificadoUseCase marcarCorreoVerificadoUseCase;

    public MarcarCorreoVerificadoController(
            MarcarCorreoVerificadoUseCase marcarCorreoVerificadoUseCase) {
        this.marcarCorreoVerificadoUseCase = marcarCorreoVerificadoUseCase;
    }

    @PatchMapping({
            "/{id}/email-verification",
            "/{id}/correo-verificado"
    })
    public ResponseEntity<Void> marcarCorreoVerificado(
            @PathVariable("id") Long id) {

        marcarCorreoVerificadoUseCase.ejecutar(id);

        return ResponseEntity.noContent().build();
    }
}

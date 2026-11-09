package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.request.VerificarCredencialesRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.VerificarCredencialesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.VerificarCredencialesUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/api/v1/usuarios")
public class VerificarCredencialesController {

    private final VerificarCredencialesUseCase verificarCredencialesUseCase;

    public VerificarCredencialesController(
            VerificarCredencialesUseCase verificarCredencialesUseCase) {
        this.verificarCredencialesUseCase = verificarCredencialesUseCase;
    }

    @PostMapping("/verificar-credenciales")
    public ResponseEntity<?> verificar(
            @Valid @RequestBody VerificarCredencialesRequestDTO request) {

        return verificarCredencialesUseCase
                .verificar(request.email(), request.password())
                .map(this::crearRespuesta)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private ResponseEntity<VerificarCredencialesResponseDTO> crearRespuesta(Usuario usuario) {

        VerificarCredencialesResponseDTO respuesta =
                new VerificarCredencialesResponseDTO(
                        usuario.getId(),
                        usuario.getEmail(),
                        usuario.getRol(),
                        usuario.getEstado()
                );

        return ResponseEntity.ok(respuesta);
    }
}
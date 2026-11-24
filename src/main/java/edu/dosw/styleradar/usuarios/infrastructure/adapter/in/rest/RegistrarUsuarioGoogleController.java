package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioGoogleRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.VerificarCredencialesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.RegistrarUsuarioGoogleUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/internal/users")
public class RegistrarUsuarioGoogleController {

    private final RegistrarUsuarioGoogleUseCase registrarUsuarioGoogleUseCase;

    public RegistrarUsuarioGoogleController(RegistrarUsuarioGoogleUseCase registrarUsuarioGoogleUseCase) {
        this.registrarUsuarioGoogleUseCase = registrarUsuarioGoogleUseCase;
    }

    @PostMapping("/oauth2")
    public ResponseEntity<VerificarCredencialesResponseDTO> registrarOConsultar(
            @Valid @RequestBody RegistrarUsuarioGoogleRequestDTO request) {
        Usuario usuario = registrarUsuarioGoogleUseCase.ejecutar(request);
        return ResponseEntity.ok(new VerificarCredencialesResponseDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getEstado(),
                usuario.getCorreoVerificado()));
    }
}

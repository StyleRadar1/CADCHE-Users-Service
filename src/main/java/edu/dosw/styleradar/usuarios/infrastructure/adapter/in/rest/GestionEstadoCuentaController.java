package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.CambioEstadoCuentaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.request.CuentasConsultaRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarEstadoCuentaUseCase;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.GestionEstadoCuentaApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
public class GestionEstadoCuentaController implements GestionEstadoCuentaApi {

    private final CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase;

    public GestionEstadoCuentaController(
            CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase) {
        this.cambiarEstadoCuentaUseCase = cambiarEstadoCuentaUseCase;
    }

    @InitBinder
    void validarEstado(WebDataBinder binder) {
        binder.registerCustomEditor(
                edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.class,
                new java.beans.PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) {
                        setValue(edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.valueOf(text));
                    }
                });
    }

    @Override
    public ResponseEntity<PageResponseDTO<CuentaAdministrativaResponseDTO>> consultar(
            @Valid @ModelAttribute CuentasConsultaRequestDTO request) {
        return ResponseEntity.ok(new PageResponseDTO<>(
                List.of(),
                request.getPage(),
                request.getSize(),
                0L,
                0,
                true,
                true));
    }

    @Override
    public ResponseEntity<CuentaAdministrativaResponseDTO> consultarUno(
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(new CuentaAdministrativaResponseDTO(
                id,
                null,
                null,
                Set.of(),
                null,
                null,
                null,
                null,
                null));
    }

    @Override
    public ResponseEntity<CuentaAdministrativaResponseDTO> cambiar(
            @PathVariable("id") Long id,
            @Valid @RequestBody CambioEstadoCuentaRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long administradorId = Long.valueOf(authentication.getName());

        Instant fechaCambio = Instant.now();

        Usuario usuario = cambiarEstadoCuentaUseCase.ejecutar(
                id,
                request.getEstado(),
                administradorId,
                request.getMotivo());

        CuentaAdministrativaResponseDTO response =
                new CuentaAdministrativaResponseDTO(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getEmail(),
                        Set.of(usuario.getRol()),
                        usuario.getEstado(),
                        usuario.getFechaRegistro(),
                        fechaCambio,
                        administradorId,
                        request.getMotivo());

        return ResponseEntity.ok(response);
    }
}


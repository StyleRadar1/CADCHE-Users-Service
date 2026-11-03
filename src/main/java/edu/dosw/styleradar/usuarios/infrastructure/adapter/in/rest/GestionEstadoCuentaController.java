package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
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
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.GestionEstadoCuentaApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
public class GestionEstadoCuentaController implements GestionEstadoCuentaApi {

    @InitBinder
    void validarEstado(WebDataBinder binder) {
        binder.registerCustomEditor(EstadoCuenta.class, new java.beans.PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(EstadoCuenta.valueOf(text));
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
    public ResponseEntity<CuentaAdministrativaResponseDTO> consultarUno(@PathVariable Long id) {
        return ResponseEntity.ok(new CuentaAdministrativaResponseDTO(
                id,
                "Laura Gómez",
                "laura.gomez@styleradar.com",
                Set.of(Rol.COMPRADOR),
                EstadoCuenta.ACTIVA,
                Instant.now(),
                null,
                null,
                null));
    }

    @Override
    public ResponseEntity<CuentaAdministrativaResponseDTO> cambiar(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoCuentaRequestDTO request) {
        return ResponseEntity.ok(new CuentaAdministrativaResponseDTO(
                id,
                "Laura Gómez",
                "laura.gomez@styleradar.com",
                Set.of(Rol.COMPRADOR),
                request.getEstado(),
                Instant.now(),
                Instant.now(),
                1L,
                request.getMotivo()));
    }
}


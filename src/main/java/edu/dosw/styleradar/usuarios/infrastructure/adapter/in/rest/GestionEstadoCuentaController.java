package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import edu.dosw.styleradar.usuarios.application.usecase.CuentaAdministrativaMapper;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarEstadoCuentaUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ConsultarCuentaAdministrativaUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ConsultarCuentasAdministrativasUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.EliminarUsuarioUseCase;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.GestionEstadoCuentaApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping({
        "/api/v1/admin/users",
        "/api/v1/admin/usuarios"
})
public class GestionEstadoCuentaController implements GestionEstadoCuentaApi {

    private final CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase;
    private final ConsultarCuentasAdministrativasUseCase consultarCuentasUseCase;
    private final ConsultarCuentaAdministrativaUseCase consultarCuentaUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;

    public GestionEstadoCuentaController(
            CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase,
            ConsultarCuentasAdministrativasUseCase consultarCuentasUseCase,
            ConsultarCuentaAdministrativaUseCase consultarCuentaUseCase,
            EliminarUsuarioUseCase eliminarUsuarioUseCase) {

        this.cambiarEstadoCuentaUseCase = cambiarEstadoCuentaUseCase;
        this.consultarCuentasUseCase = consultarCuentasUseCase;
        this.consultarCuentaUseCase = consultarCuentaUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
    }

    @InitBinder
    void validarEstado(WebDataBinder binder) {
        binder.registerCustomEditor(
                edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.class,
                new java.beans.PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) {
                        setValue(
                                edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.valueOf(text)
                        );
                    }
                }
        );
    }

    @GetMapping
    @Override
    public ResponseEntity<PageResponseDTO<CuentaAdministrativaResponseDTO>> consultar(
            @Valid @ModelAttribute CuentasConsultaRequestDTO request) {

        return ResponseEntity.ok(
                consultarCuentasUseCase.ejecutar(
                        request.getEstado(),
                        request.getRol(),
                        request.getQ(),
                        request.getPage(),
                        request.getSize()
                )
        );
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<CuentaAdministrativaResponseDTO> consultarUno(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(
                consultarCuentaUseCase.ejecutar(id)
        );
    }

    @PatchMapping({
            "/{id}/status",
            "/{id}/estado"
    })
    @Override
    public ResponseEntity<CuentaAdministrativaResponseDTO> cambiar(
            @PathVariable("id") Long id,
            @Valid @RequestBody CambioEstadoCuentaRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long administradorId = Long.valueOf(authentication.getName());

        Usuario usuario = cambiarEstadoCuentaUseCase.ejecutar(
                id,
                request.getEstado(),
                administradorId,
                request.getMotivo()
        );

        return ResponseEntity.ok(
                CuentaAdministrativaMapper.aRespuesta(usuario)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {

        eliminarUsuarioUseCase.ejecutar(id);

        return ResponseEntity.noContent().build();
    }
}

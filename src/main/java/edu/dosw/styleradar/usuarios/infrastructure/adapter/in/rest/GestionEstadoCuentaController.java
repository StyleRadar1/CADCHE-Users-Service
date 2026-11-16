package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
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
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.CambiarEstadoCuentaUseCase;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.GestionEstadoCuentaApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
public class GestionEstadoCuentaController implements GestionEstadoCuentaApi {

    private final CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase;
    private final UsuarioRepositoryPort usuarioRepository;

    public GestionEstadoCuentaController(
            CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase,
            UsuarioRepositoryPort usuarioRepository) {
        this.cambiarEstadoCuentaUseCase = cambiarEstadoCuentaUseCase;
        this.usuarioRepository = usuarioRepository;
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
        var cuentas = usuarioRepository.buscarCuentas(
                request.getEstado(), request.getPage(), request.getSize());
        long total = usuarioRepository.contarCuentas(request.getEstado());
        int paginas = (int) Math.ceil((double) total / request.getSize());
        return ResponseEntity.ok(new PageResponseDTO<>(
                cuentas.stream().map(this::aRespuesta).toList(),
                request.getPage(), request.getSize(), total, paginas,
                request.getPage() == 0, request.getPage() >= paginas - 1));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<CuentaAdministrativaResponseDTO> consultarUno(
            @PathVariable("id") Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
        return ResponseEntity.ok(aRespuesta(usuario));
    }

    @PatchMapping("/{id}/estado")
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
                request.getMotivo());

        return ResponseEntity.ok(aRespuesta(usuario));
    }

    private CuentaAdministrativaResponseDTO aRespuesta(Usuario usuario) {
        EstadoCuenta estado = usuario.getEstado() == null
                ? EstadoCuenta.ACTIVA : usuario.getEstado();
        return new CuentaAdministrativaResponseDTO(
                usuario.getId(), usuario.getNombre(), usuario.getEmail(),
                Set.of(usuario.getRol()), estado, usuario.getFechaRegistro(),
                usuario.getFechaCambioEstado(), usuario.getAdministradorCambioEstadoId(),
                usuario.getMotivoCambioEstado());
    }
}

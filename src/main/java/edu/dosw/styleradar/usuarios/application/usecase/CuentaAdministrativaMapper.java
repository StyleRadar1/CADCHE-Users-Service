package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import java.util.Set;

public final class CuentaAdministrativaMapper {

    private CuentaAdministrativaMapper() {
    }

    public static CuentaAdministrativaResponseDTO aRespuesta(Usuario usuario) {
        EstadoCuenta estado = usuario.getEstado() == null
                ? EstadoCuenta.ACTIVA : usuario.getEstado();
        return new CuentaAdministrativaResponseDTO(
                usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getBarrio(),
                Set.of(usuario.getRol()), estado, usuario.getFechaRegistro(),
                usuario.getFechaCambioEstado(), usuario.getAdministradorCambioEstadoId(),
                usuario.getMotivoCambioEstado());
    }
}

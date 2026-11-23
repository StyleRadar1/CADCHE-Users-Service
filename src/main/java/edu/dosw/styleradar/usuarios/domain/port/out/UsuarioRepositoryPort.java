package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.model.Rol;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {

    boolean existePorEmail(String email);

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorProveedorYId(String provider, String providerId);

    Optional<Usuario> vincularIdentidadGoogle(Long usuarioId, String providerId);

    List<Usuario> buscarCuentas(EstadoCuenta estado, Rol rol, String q, int page, int size);

    long contarCuentas(EstadoCuenta estado, Rol rol, String q);

    long contarAdministradoresActivos();

    Optional<Usuario> cambiarEstado(
            Long usuarioId,
            EstadoCuenta nuevoEstado,
            Instant fechaCambio,
            Long administradorId,
            String motivo);

    Optional<Usuario> marcarCorreoComoVerificado(Long usuarioId);

    boolean eliminarPorId(Long usuarioId);
}

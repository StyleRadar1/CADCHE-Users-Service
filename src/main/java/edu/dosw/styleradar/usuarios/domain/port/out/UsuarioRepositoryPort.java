package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;

import java.time.Instant;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepositoryPort {

    boolean existePorEmail(String email);

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    List<Usuario> buscarCuentas(EstadoCuenta estado, int page, int size);

    long contarCuentas(EstadoCuenta estado);

    long contarAdministradoresActivos();

    Optional<Usuario> cambiarEstado(
            Long usuarioId,
            EstadoCuenta nuevoEstado,
            Instant fechaCambio,
            Long administradorId,
            String motivo);
}

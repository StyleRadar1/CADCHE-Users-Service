package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.PublicProfileResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.PublicProfileFormatter;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerPerfilPublicoUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ObtenerPerfilPublicoUseCaseImpl implements ObtenerPerfilPublicoUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public ObtenerPerfilPublicoUseCaseImpl(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public PublicProfileResponseDTO obtenerPorId(Long userId) {
        Usuario usuario = usuarioRepositoryPort.buscarPorId(userId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(userId));

        // Blocked accounts return null displayName according to requirements
        String displayName = null;
        if (usuario.getEstado() != EstadoCuenta.BLOQUEADA) {
            displayName = PublicProfileFormatter.formatDisplayName(usuario.getNombre());
        }

        return PublicProfileResponseDTO.builder()
                .id(usuario.getId())
                .displayName(displayName)
                .build();
    }

    @Override
    public List<PublicProfileResponseDTO> obtenerPorLote(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        List<PublicProfileResponseDTO> resultados = new ArrayList<>();
        List<Long> idsUnicos = userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .limit(100)
                .toList();

        for (Long id : idsUnicos) {
            Optional<Usuario> usuarioOpt = usuarioRepositoryPort.buscarPorId(id);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                String displayName = null;
                if (usuario.getEstado() != EstadoCuenta.BLOQUEADA) {
                    displayName = PublicProfileFormatter.formatDisplayName(usuario.getNombre());
                }
                resultados.add(PublicProfileResponseDTO.builder()
                        .id(usuario.getId())
                        .displayName(displayName)
                        .build());
            }
        }

        return resultados;
    }
}

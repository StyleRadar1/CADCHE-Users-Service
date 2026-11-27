package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.PublicProfileResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.PerfilPublicoUsuario;
import edu.dosw.styleradar.usuarios.domain.model.PublicProfileFormatter;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerPerfilPublicoUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ObtenerPerfilPublicoUseCaseImpl implements ObtenerPerfilPublicoUseCase {

    private static final int MAX_BATCH_SIZE = 100;

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public ObtenerPerfilPublicoUseCaseImpl(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public PublicProfileResponseDTO obtenerPorId(Long userId) {
        PerfilPublicoUsuario perfil = usuarioRepositoryPort.buscarPerfilPublicoPorId(userId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(userId));

        return aRespuesta(perfil);
    }

    @Override
    public List<PublicProfileResponseDTO> obtenerPorLote(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        List<Long> idsUnicos = userIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .limit(MAX_BATCH_SIZE)
                .toList();

        if (idsUnicos.isEmpty()) {
            return List.of();
        }

        Map<Long, PerfilPublicoUsuario> porId = usuarioRepositoryPort
                .buscarPerfilesPublicosPorIds(idsUnicos)
                .stream()
                .collect(Collectors.toMap(
                        PerfilPublicoUsuario::id,
                        Function.identity(),
                        (primero, segundo) -> primero
                ));

        List<PublicProfileResponseDTO> resultados = new ArrayList<>(porId.size());
        for (Long id : idsUnicos) {
            PerfilPublicoUsuario perfil = porId.get(id);
            if (perfil != null) {
                resultados.add(aRespuesta(perfil));
            }
        }

        return resultados;
    }

    private PublicProfileResponseDTO aRespuesta(PerfilPublicoUsuario perfil) {
        // Fail-closed: only ACTIVE accounts expose a formatted displayName;
        // BLOCKED and INACTIVE accounts return null displayName.
        String displayName = perfil.estado() == EstadoCuenta.ACTIVA
                ? PublicProfileFormatter.formatDisplayName(perfil.nombre())
                : null;

        return PublicProfileResponseDTO.builder()
                .id(perfil.id())
                .displayName(displayName)
                .build();
    }
}

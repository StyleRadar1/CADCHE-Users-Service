package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.MedidasCorporalesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerMedidasCorporalesUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class ObtenerMedidasCorporalesUseCaseImpl
        implements ObtenerMedidasCorporalesUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public ObtenerMedidasCorporalesUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public MedidasCorporalesResponseDTO obtener(Long usuarioId) {

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        return MedidasCorporalesResponseDTO.builder()
                .heightCm(usuario.getHeightCm())
                .chestCm(usuario.getChestCm())
                .waistCm(usuario.getWaistCm())
                .hipCm(usuario.getHipCm())
                .build();
    }
}

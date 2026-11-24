package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.port.in.ConsultarCuentaAdministrativaUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class ConsultarCuentaAdministrativaUseCaseImpl
        implements ConsultarCuentaAdministrativaUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public ConsultarCuentaAdministrativaUseCaseImpl(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public CuentaAdministrativaResponseDTO ejecutar(Long id) {
        return usuarioRepository.buscarPorId(id)
                .map(CuentaAdministrativaMapper::aRespuesta)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }
}

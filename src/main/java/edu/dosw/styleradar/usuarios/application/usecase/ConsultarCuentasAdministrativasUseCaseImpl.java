package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.application.dto.response.CuentaAdministrativaResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.port.in.ConsultarCuentasAdministrativasUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConsultarCuentasAdministrativasUseCaseImpl
        implements ConsultarCuentasAdministrativasUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public ConsultarCuentasAdministrativasUseCaseImpl(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public PageResponseDTO<CuentaAdministrativaResponseDTO> ejecutar(
            EstadoCuenta estado, Rol rol, String q, int page, int size) {

        String filtro = q == null || q.isBlank() ? "" : q.strip();

        List<CuentaAdministrativaResponseDTO> cuentas = usuarioRepository
                .buscarCuentas(estado, rol, filtro, page, size)
                .stream()
                .map(CuentaAdministrativaMapper::aRespuesta)
                .toList();

        long total = usuarioRepository.contarCuentas(estado, rol, filtro);
        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResponseDTO<>(cuentas, page, size, total, totalPages,
                page == 0, page >= totalPages - 1);
    }
}

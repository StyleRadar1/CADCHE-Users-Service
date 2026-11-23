package edu.dosw.styleradar.usuarios.application.usecase;

import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edu.dosw.styleradar.usuarios.application.dto.response.UsuarioResponseDTO;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerUsuarioUseCase;
import edu.dosw.styleradar.usuarios.domain.port.out.PreferenciaEstiloRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.TallaRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

@Service
public class ObtenerUsuarioUseCaseImpl implements ObtenerUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PreferenciaEstiloRepositoryPort preferenciasRepository;
    private final TallaRepositoryPort tallasRepository;

    public ObtenerUsuarioUseCaseImpl(
            UsuarioRepositoryPort usuarioRepository,
            PreferenciaEstiloRepositoryPort preferenciasRepository,
            TallaRepositoryPort tallasRepository) {

        this.usuarioRepository = usuarioRepository;
        this.preferenciasRepository = preferenciasRepository;
        this.tallasRepository = tallasRepository;
    }

    @Override
    public UsuarioResponseDTO obtener(Long usuarioId) {

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        Set<Estilo> preferencias =
                preferenciasRepository.buscarPorUsuarioId(usuarioId);

        Map<TipoPrenda, Set<String>> tallas =
                tallasRepository.buscarPorUsuarioId(usuarioId);

        Set<Talla> tallasAlfabeticas =
                tallas.getOrDefault(TipoPrenda.SUPERIOR, Set.of())
                        .stream()
                        .map(valor -> {
                            try {
                                return Talla.valueOf(valor);
                            } catch (IllegalArgumentException exception) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .telefono(usuario.getTelefono())
                .barrio(usuario.getBarrio())
                .correoVerificado(usuario.getCorreoVerificado())
                .fechaRegistro(usuario.getFechaRegistro())
                .preferenciasEstilo(preferencias)
                .tallasHabituales(tallasAlfabeticas)
                .tallasPorTipoPrenda(tallas)
                .build();
    }
}

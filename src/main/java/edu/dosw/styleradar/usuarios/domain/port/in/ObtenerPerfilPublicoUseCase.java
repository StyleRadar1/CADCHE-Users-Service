package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.application.dto.response.PublicProfileResponseDTO;

import java.util.List;

/** Inbound port to retrieve safe public user profiles. */
public interface ObtenerPerfilPublicoUseCase {

    PublicProfileResponseDTO obtenerPorId(Long userId);

    List<PublicProfileResponseDTO> obtenerPorLote(List<Long> userIds);
}

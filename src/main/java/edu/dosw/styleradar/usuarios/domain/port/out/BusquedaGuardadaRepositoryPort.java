package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.model.BusquedaGuardada;
import java.util.List;

public interface BusquedaGuardadaRepositoryPort {
    BusquedaGuardada guardar(BusquedaGuardada busqueda);
    List<BusquedaGuardada> buscarPorUsuarioId(Long usuarioId);
    boolean eliminarPorIdYUsuarioId(Long busquedaId, Long usuarioId);
}

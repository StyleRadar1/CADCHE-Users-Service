package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.usuarios.domain.model.BusquedaGuardada;
import edu.dosw.styleradar.usuarios.domain.port.out.BusquedaGuardadaRepositoryPort;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.BusquedaGuardadaEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.BusquedaGuardadaJpaRepository;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BusquedaGuardadaPersistenceAdapter implements BusquedaGuardadaRepositoryPort {
    private final BusquedaGuardadaJpaRepository repository;

    public BusquedaGuardadaPersistenceAdapter(BusquedaGuardadaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public BusquedaGuardada guardar(BusquedaGuardada busqueda) {
        BusquedaGuardadaEntity entity = new BusquedaGuardadaEntity();
        entity.setUsuarioId(busqueda.usuarioId());
        entity.setNombre(busqueda.nombre());
        entity.setQuery(busqueda.query());
        entity.setTipo(busqueda.tipo());
        entity.setColor(busqueda.color());
        entity.setTalla(busqueda.talla());
        entity.setPrecioMin(busqueda.precioMin());
        entity.setPrecioMax(busqueda.precioMax());
        entity.setMarca(busqueda.marca());
        entity.setEstilo(busqueda.estilo());
        entity.setLatitud(busqueda.latitud());
        entity.setLongitud(busqueda.longitud());
        entity.setFechaCreacion(busqueda.fechaCreacion());
        return convertir(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusquedaGuardada> buscarPorUsuarioId(Long usuarioId) {
        return repository.findAllByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(this::convertir).toList();
    }

    @Override
    @Transactional
    public boolean eliminarPorIdYUsuarioId(Long busquedaId, Long usuarioId) {
        return repository.deleteByIdAndUsuarioId(busquedaId, usuarioId) > 0;
    }

    private BusquedaGuardada convertir(BusquedaGuardadaEntity entity) {
        return new BusquedaGuardada(entity.getId(), entity.getUsuarioId(), entity.getNombre(),
                entity.getQuery(), entity.getTipo(), entity.getColor(), entity.getTalla(),
                entity.getPrecioMin(), entity.getPrecioMax(), entity.getMarca(), entity.getEstilo(),
                entity.getLatitud(), entity.getLongitud(), entity.getFechaCreacion());
    }
}

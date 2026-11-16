package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository;

import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.BusquedaGuardadaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusquedaGuardadaJpaRepository extends JpaRepository<BusquedaGuardadaEntity, Long> {
    List<BusquedaGuardadaEntity> findAllByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    long deleteByIdAndUsuarioId(Long id, Long usuarioId);
}

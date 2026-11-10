package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository;

import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.PreferenciaEstiloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferenciaEstiloJpaRepository
        extends JpaRepository<PreferenciaEstiloEntity, Long> {

    List<PreferenciaEstiloEntity> findByUsuario_Id(Long usuarioId);

    void deleteByUsuario_Id(Long usuarioId);
}
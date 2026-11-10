package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository;

import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.TallaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TallaJpaRepository extends JpaRepository<TallaEntity, Long> {

    List<TallaEntity> findByUsuario_Id(Long usuarioId);

    void deleteByUsuario_Id(Long usuarioId);
}
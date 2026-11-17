package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByEmail(String email);

    Optional<UsuarioEntity> findByEmail(String email);

    long countByRolAndEstado(Rol rol, EstadoCuenta estado);

    @Query(value = "SELECT u FROM UsuarioEntity u WHERE (:estado IS NULL OR u.estado = :estado "
            + "OR (:estado = edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.ACTIVA "
            + "AND u.estado IS NULL)) "
            + "AND (:rol IS NULL OR u.rol = :rol) "
            + "AND (:q IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))) ORDER BY u.id ASC",
            countQuery = "SELECT COUNT(u) FROM UsuarioEntity u WHERE (:estado IS NULL OR u.estado = :estado "
                    + "OR (:estado = edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.ACTIVA "
                    + "AND u.estado IS NULL)) "
                    + "AND (:rol IS NULL OR u.rol = :rol) "
                    + "AND (:q IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%')) "
                    + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')))" )
    Page<UsuarioEntity> buscarCuentas(
            @Param("estado") EstadoCuenta estado,
            @Param("rol") Rol rol,
            @Param("q") String q,
            Pageable pageable);

    @Query("SELECT COUNT(u) FROM UsuarioEntity u WHERE (:estado IS NULL OR u.estado = :estado "
            + "OR (:estado = edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.ACTIVA "
            + "AND u.estado IS NULL)) "
            + "AND (:rol IS NULL OR u.rol = :rol) "
            + "AND (:q IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))) ")
    long contarCuentas(
            @Param("estado") EstadoCuenta estado,
            @Param("rol") Rol rol,
            @Param("q") String q);

}

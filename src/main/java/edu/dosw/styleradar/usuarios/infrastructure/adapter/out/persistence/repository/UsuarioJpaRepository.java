package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.PerfilPublicoUsuario;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByEmail(String email);

    Optional<UsuarioEntity> findByEmail(String email);

    Optional<UsuarioEntity> findByProviderAndProviderId(String provider, String providerId);

    @Query("SELECT new edu.dosw.styleradar.usuarios.domain.model.PerfilPublicoUsuario(u.id, u.nombre, u.estado) "
            + "FROM UsuarioEntity u WHERE u.id = :id")
    Optional<PerfilPublicoUsuario> buscarPerfilPublicoPorId(@Param("id") Long id);

    @Query("SELECT new edu.dosw.styleradar.usuarios.domain.model.PerfilPublicoUsuario(u.id, u.nombre, u.estado) "
            + "FROM UsuarioEntity u WHERE u.id IN :ids")
    List<PerfilPublicoUsuario> buscarPerfilesPublicosPorIds(@Param("ids") Collection<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UsuarioEntity u SET u.provider = 'GOOGLE', u.providerId = :providerId "
            + "WHERE u.id = :usuarioId AND u.provider IS NULL AND u.providerId IS NULL")
    int vincularGoogleSiNoTieneIdentidad(
            @Param("usuarioId") Long usuarioId,
            @Param("providerId") String providerId);

    long countByRolAndEstado(Rol rol, EstadoCuenta estado);

    @Query(value = "SELECT u FROM UsuarioEntity u WHERE (:estado IS NULL OR u.estado = :estado "
            + "OR (:estado = edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.ACTIVA "
            + "AND u.estado IS NULL)) "
            + "AND (:rol IS NULL OR u.rol = :rol) "
            + "AND (:q = '' OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))) ORDER BY u.id ASC",
            countQuery = "SELECT COUNT(u) FROM UsuarioEntity u WHERE (:estado IS NULL OR u.estado = :estado "
                    + "OR (:estado = edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.ACTIVA "
                    + "AND u.estado IS NULL)) "
                    + "AND (:rol IS NULL OR u.rol = :rol) "
                    + "AND (:q = '' OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%')) "
                    + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<UsuarioEntity> buscarCuentas(
            @Param("estado") EstadoCuenta estado,
            @Param("rol") Rol rol,
            @Param("q") String q,
            Pageable pageable);

    @Query("SELECT COUNT(u) FROM UsuarioEntity u WHERE (:estado IS NULL OR u.estado = :estado "
            + "OR (:estado = edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta.ACTIVA "
            + "AND u.estado IS NULL)) "
            + "AND (:rol IS NULL OR u.rol = :rol) "
            + "AND (:q = '' OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))) ")
    long contarCuentas(
            @Param("estado") EstadoCuenta estado,
            @Param("rol") Rol rol,
            @Param("q") String q);

}

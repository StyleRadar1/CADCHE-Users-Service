package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String barrio;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "acepta_habeas_data", nullable = false)
    private Boolean aceptaHabeasData;

    @Column(name = "correo_verificado", nullable = false)
    private Boolean correoVerificado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuenta estado;

    @Column(name = "fecha_registro", nullable = false)
    private Instant fechaRegistro;

    @Column(name = "fecha_cambio_estado")
    private Instant fechaCambioEstado;

    @Column(name = "administrador_cambio_estado_id")
    private Long administradorCambioEstadoId;

    @Column(name = "motivo_cambio_estado", length = 1000)
    private String motivoCambioEstado;

    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "chest_cm")
    private Double chestCm;

    @Column(name = "waist_cm")
    private Double waistCm;

    @Column(name = "hip_cm")
    private Double hipCm;

    public UsuarioEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Boolean getAceptaHabeasData() {
        return aceptaHabeasData;
    }

    public void setAceptaHabeasData(Boolean aceptaHabeasData) {
        this.aceptaHabeasData = aceptaHabeasData;
    }

    public Boolean getCorreoVerificado() {
        return correoVerificado;
    }

    public void setCorreoVerificado(Boolean correoVerificado) {
        this.correoVerificado = correoVerificado;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuenta estado) {
        this.estado = estado;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Instant fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Instant getFechaCambioEstado() {
        return fechaCambioEstado;
    }

    public void setFechaCambioEstado(Instant fechaCambioEstado) {
        this.fechaCambioEstado = fechaCambioEstado;
    }

    public Long getAdministradorCambioEstadoId() {
        return administradorCambioEstadoId;
    }

    public void setAdministradorCambioEstadoId(Long administradorCambioEstadoId) {
        this.administradorCambioEstadoId = administradorCambioEstadoId;
    }

    public String getMotivoCambioEstado() {
        return motivoCambioEstado;
    }

    public void setMotivoCambioEstado(String motivoCambioEstado) {
        this.motivoCambioEstado = motivoCambioEstado;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getChestCm() {
        return chestCm;
    }

    public void setChestCm(Double chestCm) {
        this.chestCm = chestCm;
    }

    public Double getWaistCm() {
        return waistCm;
    }

    public void setWaistCm(Double waistCm) {
        this.waistCm = waistCm;
    }

    public Double getHipCm() {
        return hipCm;
    }

    public void setHipCm(Double hipCm) {
        this.hipCm = hipCm;
    }
}

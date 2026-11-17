package edu.dosw.styleradar.usuarios.domain.model;

import java.time.Instant;

public class Usuario {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String barrio;
    private String passwordHash;
    private Boolean aceptaHabeasData;
    private Boolean correoVerificado;
    private Rol rol;
    private EstadoCuenta estado;
    private Instant fechaRegistro;
    private Instant fechaCambioEstado;
    private Long administradorCambioEstadoId;
    private String motivoCambioEstado;

    public Usuario(
            Long id,
            String nombre,
            String email,
            String telefono,
            String passwordHash,
            Boolean aceptaHabeasData,
            Rol rol,
            EstadoCuenta estado,
            Instant fechaRegistro) {
        this(id, nombre, email, telefono, passwordHash, aceptaHabeasData,
                rol, estado, fechaRegistro, null, null, null, false, null);
    }

    public Usuario(
            Long id, String nombre, String email, String telefono,
            String passwordHash, Boolean aceptaHabeasData, Rol rol,
            EstadoCuenta estado, Instant fechaRegistro, String barrio) {
        this(id, nombre, email, telefono, passwordHash, aceptaHabeasData,
                rol, estado, fechaRegistro, null, null, null, false, barrio);
    }

    public Usuario(
            Long id, String nombre, String email, String telefono,
            String passwordHash, Boolean aceptaHabeasData, Rol rol,
            EstadoCuenta estado, Instant fechaRegistro, Instant fechaCambioEstado,
            Long administradorCambioEstadoId, String motivoCambioEstado) {
        this(id, nombre, email, telefono, passwordHash, aceptaHabeasData,
                rol, estado, fechaRegistro, fechaCambioEstado,
                administradorCambioEstadoId, motivoCambioEstado, false, null);
    }

    public Usuario(
            Long id, String nombre, String email, String telefono,
            String passwordHash, Boolean aceptaHabeasData, Rol rol,
            EstadoCuenta estado, Instant fechaRegistro, Instant fechaCambioEstado,
            Long administradorCambioEstadoId, String motivoCambioEstado,
            Boolean correoVerificado) {
        this(id, nombre, email, telefono, passwordHash, aceptaHabeasData, rol,
                estado, fechaRegistro, fechaCambioEstado, administradorCambioEstadoId,
                motivoCambioEstado, correoVerificado, null);
    }

    public Usuario(
            Long id, String nombre, String email, String telefono,
            String passwordHash, Boolean aceptaHabeasData, Rol rol,
            EstadoCuenta estado, Instant fechaRegistro, Instant fechaCambioEstado,
            Long administradorCambioEstadoId, String motivoCambioEstado,
            Boolean correoVerificado, String barrio) {

        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.barrio = barrio;
        this.passwordHash = passwordHash;
        this.aceptaHabeasData = aceptaHabeasData;
        this.correoVerificado = correoVerificado;
        this.rol = rol;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.fechaCambioEstado = fechaCambioEstado;
        this.administradorCambioEstadoId = administradorCambioEstadoId;
        this.motivoCambioEstado = motivoCambioEstado;
    }

    public void cambiarEstado(EstadoCuenta nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }

        this.estado = nuevoEstado;
    }

    public void marcarCorreoComoVerificado() {
        this.correoVerificado = true;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getBarrio() {
        return barrio;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Boolean getAceptaHabeasData() {
        return aceptaHabeasData;
    }

    public Boolean getCorreoVerificado() {
        return correoVerificado;
    }

    public Rol getRol() {
        return rol;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }

    public Instant getFechaCambioEstado() {
        return fechaCambioEstado;
    }

    public Long getAdministradorCambioEstadoId() {
        return administradorCambioEstadoId;
    }

    public String getMotivoCambioEstado() {
        return motivoCambioEstado;
    }
}



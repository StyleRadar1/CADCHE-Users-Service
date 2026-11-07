package edu.dosw.styleradar.usuarios.domain.model;

import java.time.Instant;

public class Usuario {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String passwordHash;
    private Boolean aceptaHabeasData;
    private Rol rol;
    private EstadoCuenta estado;
    private Instant fechaRegistro;

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
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.passwordHash = passwordHash;
        this.aceptaHabeasData = aceptaHabeasData;
        this.rol = rol;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public Boolean getAceptaHabeasData() {
        return aceptaHabeasData;
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
}


package edu.dosw.styleradar.usuarios.domain.model;

/**
 * Minimal domain projection used to resolve public display names without loading PII
 * (email, phone, neighborhood, password hash, measurements) from the database.
 */
public record PerfilPublicoUsuario(Long id, String nombre, EstadoCuenta estado) {
}

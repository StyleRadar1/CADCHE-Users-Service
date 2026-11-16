package edu.dosw.styleradar.usuarios.domain.model;

import java.time.Instant;

public record BusquedaGuardada(
        Long id,
        Long usuarioId,
        String nombre,
        String query,
        TipoPrenda tipo,
        String color,
        Talla talla,
        Double precioMin,
        Double precioMax,
        String marca,
        Estilo estilo,
        Double latitud,
        Double longitud,
        Instant fechaCreacion) {
}

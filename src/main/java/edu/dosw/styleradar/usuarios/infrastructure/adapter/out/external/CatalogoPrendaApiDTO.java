package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogoPrendaApiDTO(
        String id,
        String almacenId,
        String nombre,
        String tipo,
        String color,
        String marca,
        String estilo,
        Double precio,
        List<String> fotografias,
        String estado,
        Boolean disponible,
        LocalDateTime fechaPublicacion) {
}

package edu.dosw.styleradar.usuarios.domain.event;

import java.time.Instant;

import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;

public record BusquedaSincronizadaEvent(
        String eventoId,
        Long busquedaId,
        Long usuarioId,
        String nombre,
        String textoLibre,
        String categoria,
        String color,
        String talla,
        Double precioMin,
        Double precioMax,
        String marca,
        String estilo,
        String localidad,
        String accion,
        Instant fechaEvento
) {

    public static BusquedaSincronizadaEvent desde(
            Long busquedaId,
            Long usuarioId,
            String nombre,
            String textoLibre,
            TipoPrenda categoria,
            String color,
            Talla talla,
            Double precioMin,
            Double precioMax,
            String marca,
            Estilo estilo,
            String accion) {

        return new BusquedaSincronizadaEvent(
                java.util.UUID.randomUUID().toString(),
                busquedaId,
                usuarioId,
                nombre,
                textoLibre,
                categoria == null ? null : categoria.name(),
                color,
                talla == null ? null : talla.name(),
                precioMin,
                precioMax,
                marca,
                estilo == null ? null : estilo.name(),
                null,
                accion,
                Instant.now()
        );
    }
}

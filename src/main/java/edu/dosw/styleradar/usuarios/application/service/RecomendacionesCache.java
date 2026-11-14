package edu.dosw.styleradar.usuarios.application.service;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RecomendacionesCache {

    private static final long DURACION_CACHE_SEGUNDOS = 300;

    private final ConcurrentHashMap<String, CacheEntry> cache =
            new ConcurrentHashMap<>();

    public Optional<List<CatalogoItemResponseDTO>> obtener(String clave) {
        CacheEntry entrada = cache.get(clave);

        if (entrada == null) {
            return Optional.empty();
        }

        if (Instant.now().isAfter(entrada.expiracion())) {
            cache.remove(clave, entrada);
            return Optional.empty();
        }

        return Optional.of(entrada.recomendaciones());
    }

    public void guardar(
            String clave,
            List<CatalogoItemResponseDTO> recomendaciones) {

        cache.put(
                clave,
                new CacheEntry(
                        List.copyOf(recomendaciones),
                        Instant.now().plusSeconds(DURACION_CACHE_SEGUNDOS)
                )
        );

        limpiarExpiradas();
    }

    private void limpiarExpiradas() {
        Instant ahora = Instant.now();

        cache.entrySet().removeIf(
                entrada -> ahora.isAfter(entrada.getValue().expiracion())
        );
    }

    private record CacheEntry(
            List<CatalogoItemResponseDTO> recomendaciones,
            Instant expiracion
    ) {
    }
}


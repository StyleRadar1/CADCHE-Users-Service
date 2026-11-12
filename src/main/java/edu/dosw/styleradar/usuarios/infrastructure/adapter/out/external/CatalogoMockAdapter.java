package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.external;

import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.domain.model.EstadoItem;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.port.out.CatalogoClientPort;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class CatalogoMockAdapter implements CatalogoClientPort {

    @Override
    public List<CatalogoItemResponseDTO> obtenerPrendasDisponibles() {

        return List.of(
                crearPrenda(101L, "Chaqueta urbana", Estilo.URBANO,
                        List.of(Talla.S, Talla.M, Talla.L), 8, 5),

                crearPrenda(102L, "Camiseta casual", Estilo.CASUAL,
                        List.of(Talla.M, Talla.L), 12, 4),

                crearPrenda(103L, "Camisa formal", Estilo.FORMAL,
                        List.of(Talla.S, Talla.M), 4, 3),

                crearPrenda(104L, "Camiseta deportiva", Estilo.DEPORTIVO,
                        List.of(Talla.XS, Talla.S, Talla.M), 6, 2),

                crearPrenda(105L, "Chaqueta vintage", Estilo.VINTAGE,
                        List.of(Talla.S, Talla.M), 3, 1),

                crearPrenda(106L, "Camiseta urbana", Estilo.URBANO,
                        List.of(Talla.XS, Talla.S, Talla.M), 10, 0)
        );
    }

    private CatalogoItemResponseDTO crearPrenda(
            Long id,
            String nombre,
            Estilo estilo,
            List<Talla> tallas,
            Integer stock,
            int diasAntiguedad) {

        return CatalogoItemResponseDTO.builder()
                .itemId(id)
                .prendaId(id)
                .nombre(nombre)
                .descripcion("Prenda de prueba para el feed de StyleRadar")
                .tipo(TipoPrenda.SUPERIOR)
                .marca("StyleRadar")
                .color("Varios")
                .estilo(estilo)
                .precio(89900.0)
                .stock(stock)
                .estado(stock > 0 ? EstadoItem.DISPONIBLE : EstadoItem.AGOTADA)
                .fechaPublicacion(
                        Instant.now().minusSeconds(diasAntiguedad * 86400L)
                )
                .tallasDisponibles(tallas)
                .build();
    }
}


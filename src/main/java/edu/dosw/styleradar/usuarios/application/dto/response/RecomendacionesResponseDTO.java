package edu.dosw.styleradar.usuarios.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Respuesta de recomendaciones de prendas según el perfil del comprador")
public class RecomendacionesResponseDTO
        extends PageResponseDTO<CatalogoItemResponseDTO> {

    @Schema(description = "Indica si el usuario no tiene preferencias ni tallas configuradas")
    private final boolean generalSinPreferencias;

    @Schema(description = "Mensaje informativo sobre las recomendaciones")
    private final String mensaje;

    public RecomendacionesResponseDTO(
            PageResponseDTO<CatalogoItemResponseDTO> pagina,
            boolean generalSinPreferencias,
            String mensaje) {

        super(
                pagina.getContent(),
                pagina.getPage(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast()
        );

        this.generalSinPreferencias = generalSinPreferencias;
        this.mensaje = mensaje;
    }
}

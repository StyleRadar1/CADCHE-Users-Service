package edu.dosw.styleradar.usuarios.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Respuesta paginada de recomendaciones de prendas según el perfil del comprador (SR-RF-06)")
public class RecomendacionesResponseDTO extends PageResponseDTO<CatalogoItemResponseDTO> {

    @Schema(description = "true indica una selección general reciente porque no hay estilos ni tallas configurados", example = "false")
    private final boolean generalSinPreferencias;

    public RecomendacionesResponseDTO(PageResponseDTO<CatalogoItemResponseDTO> pagina, boolean generalSinPreferencias) {
        super(pagina.getContent(), pagina.getPage(), pagina.getSize(), pagina.getTotalElements(),
                pagina.getTotalPages(), pagina.isFirst(), pagina.isLast());
        this.generalSinPreferencias = generalSinPreferencias;
    }
}


package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.PersonalizacionRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.RecomendacionesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerFeedPersonalizadoUseCase;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.PersonalizacionApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios/me")
public class PersonalizacionController implements PersonalizacionApi {

    private final ObtenerFeedPersonalizadoUseCase obtenerFeedPersonalizadoUseCase;

    public PersonalizacionController(
            ObtenerFeedPersonalizadoUseCase obtenerFeedPersonalizadoUseCase) {
        this.obtenerFeedPersonalizadoUseCase = obtenerFeedPersonalizadoUseCase;
    }

    @Override
    public ResponseEntity<PageResponseDTO<CatalogoItemResponseDTO>> feed(
            @Valid @ModelAttribute PersonalizacionRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long usuarioId = Long.valueOf(jwt.getSubject());

        PageResponseDTO<CatalogoItemResponseDTO> respuesta =
                obtenerFeedPersonalizadoUseCase.obtener(
                        usuarioId,
                        request.getPage(),
                        request.getSize()
                );

        return ResponseEntity.ok(respuesta);
    }

    @Override
    public ResponseEntity<RecomendacionesResponseDTO> recomendaciones(
            @Valid @ModelAttribute PersonalizacionRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long usuarioId = Long.valueOf(jwt.getSubject());

        PageResponseDTO<CatalogoItemResponseDTO> pagina =
                obtenerFeedPersonalizadoUseCase.obtener(
                        usuarioId,
                        request.getPage(),
                        request.getSize()
                );

        return ResponseEntity.ok(
                new RecomendacionesResponseDTO(pagina, false)
        );
    }
}


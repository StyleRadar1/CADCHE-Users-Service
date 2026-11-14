package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import edu.dosw.styleradar.usuarios.application.dto.request.PersonalizacionRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.RecomendacionesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerFeedPersonalizadoUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerRecomendacionesUseCase;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.PersonalizacionApi;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios/me")
public class PersonalizacionController implements PersonalizacionApi {

    private final ObtenerFeedPersonalizadoUseCase obtenerFeedPersonalizadoUseCase;
    private final ObtenerRecomendacionesUseCase obtenerRecomendacionesUseCase;

    public PersonalizacionController(
            ObtenerFeedPersonalizadoUseCase obtenerFeedPersonalizadoUseCase,
            ObtenerRecomendacionesUseCase obtenerRecomendacionesUseCase) {

        this.obtenerFeedPersonalizadoUseCase = obtenerFeedPersonalizadoUseCase;
        this.obtenerRecomendacionesUseCase = obtenerRecomendacionesUseCase;
    }

    @Override
    public ResponseEntity<PageResponseDTO<CatalogoItemResponseDTO>> feed(
            @Valid @ModelAttribute PersonalizacionRequestDTO request) {

        Long usuarioId = obtenerUsuarioId();

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

        Long usuarioId = obtenerUsuarioId();

        RecomendacionesResponseDTO respuesta =
                obtenerRecomendacionesUseCase.obtener(
                        usuarioId,
                        request.getPage(),
                        request.getSize()
                );

        return ResponseEntity.ok(respuesta);
    }

    private Long obtenerUsuarioId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        return Long.valueOf(jwt.getSubject());
    }
}



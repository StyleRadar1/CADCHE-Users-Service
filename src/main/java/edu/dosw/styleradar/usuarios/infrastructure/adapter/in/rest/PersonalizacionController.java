package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.PersonalizacionRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.RecomendacionesResponseDTO;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerFeedPersonalizadoUseCase;
import edu.dosw.styleradar.usuarios.domain.port.in.ObtenerRecomendacionesUseCase;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.PersonalizacionApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
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
    @GetMapping("/{id}/feed")
    public ResponseEntity<PageResponseDTO<CatalogoItemResponseDTO>> feed(
            @org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @Valid @ModelAttribute PersonalizacionRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long usuarioId = Long.valueOf(jwt.getSubject());
        validarUsuarioSolicitado(id, usuarioId);

        PageResponseDTO<CatalogoItemResponseDTO> respuesta =
                obtenerFeedPersonalizadoUseCase.obtener(
                        usuarioId,
                        request.getPage(),
                        request.getSize()
                );

        return ResponseEntity.ok(respuesta);
    }

    @Override
    @GetMapping("/{id}/recommendations")
    public ResponseEntity<RecomendacionesResponseDTO> recomendaciones(
            @org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @Valid @ModelAttribute PersonalizacionRequestDTO request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long usuarioId = Long.valueOf(jwt.getSubject());
        validarUsuarioSolicitado(id, usuarioId);

        return ResponseEntity.ok(obtenerRecomendacionesUseCase.obtener(
                usuarioId,
                request.getPage(),
                request.getSize()
        ));
    }

    private void validarUsuarioSolicitado(Long idSolicitado, Long idAutenticado) {
        if (!idSolicitado.equals(idAutenticado)) {
            throw new AccessDeniedException("Solo puede consultar sus propias recomendaciones");
        }
    }
}

package edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.dosw.styleradar.usuarios.application.dto.request.PersonalizacionRequestDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.CatalogoItemResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.PageResponseDTO;
import edu.dosw.styleradar.usuarios.application.dto.response.RecomendacionesResponseDTO;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.in.rest.doc.PersonalizacionApi;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios/me")
public class PersonalizacionController implements PersonalizacionApi {

    @Override
    public ResponseEntity<PageResponseDTO<CatalogoItemResponseDTO>> feed(
            @Valid @ModelAttribute PersonalizacionRequestDTO request) {
        return ResponseEntity.ok(new PageResponseDTO<>(
                List.of(),
                request.getPage(),
                request.getSize(),
                0L,
                0,
                true,
                true));
    }

    @Override
    public ResponseEntity<RecomendacionesResponseDTO> recomendaciones(
            @Valid @ModelAttribute PersonalizacionRequestDTO request) {
        var page = new PageResponseDTO<CatalogoItemResponseDTO>(
                List.of(),
                request.getPage(),
                request.getSize(),
                0L,
                0,
                true,
                true);
        return ResponseEntity.ok(new RecomendacionesResponseDTO(page, false));
    }
}


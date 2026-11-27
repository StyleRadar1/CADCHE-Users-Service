package edu.dosw.styleradar.usuarios.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Request payload for batch querying public user profiles. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de consulta de perfiles públicos en lote")
public class BatchPublicProfilesRequestDTO {

    @NotEmpty(message = "La lista de ids no puede estar vacía")
    @Size(max = 100, message = "No se pueden consultar más de 100 usuarios en una sola solicitud")
    @Schema(description = "Lista de identificadores de usuario positivos (máx. 100)", example = "[7, 12, 31]")
    private List<@NotNull(message = "Ningún identificador de usuario puede ser nulo")
            @Positive(message = "Cada identificador de usuario debe ser positivo") Long> ids;
}

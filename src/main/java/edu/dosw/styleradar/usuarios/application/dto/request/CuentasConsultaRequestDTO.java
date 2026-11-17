package edu.dosw.styleradar.usuarios.application.dto.request;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Filtros y paginación para la consulta administrativa de cuentas de usuario (SR-RF-66)")
public class CuentasConsultaRequestDTO {

    @Schema(description = "Filtro opcional por estado efectivo de la cuenta", example = "ACTIVA")
    private EstadoCuenta estado;

    @Schema(description = "Filtro opcional por rol", example = "COMPRADOR")
    private Rol rol;

    @Size(max = 100)
    @Schema(description = "Búsqueda parcial por nombre o correo", example = "laura@styleradar.com")
    private String q;

    @NotNull
    @Min(0)
    @Schema(description = "Número de página de base cero", example = "0", defaultValue = "0")
    private Integer page = 0;

    @NotNull
    @Min(1)
    @Max(100)
    @Schema(description = "Cantidad de cuentas por página (máximo 100)", example = "20", defaultValue = "20")
    private Integer size = 20;
}


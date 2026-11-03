package edu.dosw.styleradar.usuarios.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Solicitud administrativa para cambiar el estado de una cuenta de usuario (SR-RF-66)")
public class CambioEstadoCuentaRequestDTO {

    @NotNull
    @Schema(description = "Nuevo estado operativo de la cuenta", example = "BLOQUEADA", requiredMode = Schema.RequiredMode.REQUIRED)
    private EstadoCuenta estado;

    @Size(max = 1000)
    @Schema(description = "Motivo de la decisión administrativa; obligatorio cuando el estado es BLOQUEADA (máximo 1000 caracteres)", example = "Incumplimiento reiterado de los términos de uso")
    private String motivo;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "El bloqueo exige un motivo no vacío")
    public boolean isMotivoValido() {
        return estado != EstadoCuenta.BLOQUEADA || (motivo != null && !motivo.isBlank());
    }

    @JsonAnySetter
    public void rechazarCampoDesconocido(String campo, Object valor) {
        throw new IllegalArgumentException("Campo no permitido en el cambio de estado de cuenta");
    }
}


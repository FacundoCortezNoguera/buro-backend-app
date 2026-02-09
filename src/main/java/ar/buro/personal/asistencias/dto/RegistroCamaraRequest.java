package ar.buro.personal.asistencias.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class RegistroCamaraRequest {

    @NotNull(message = "El ID del empleado es requerido")
    private Long empleadoId;

    @NotNull(message = "La hora es requerida")
    private LocalTime hora;
}

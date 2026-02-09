package ar.buro.personal.asistencias.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class MarcarPresenteRequest {

    @NotNull(message = "El ID del empleado es requerido")
    private Long empleadoId;

    @NotNull(message = "La hora de llegada es requerida")
    private LocalTime horaLlegada;

    private LocalTime horaEsperada;  // Opcional, por defecto 22:00

    private String observaciones;
}

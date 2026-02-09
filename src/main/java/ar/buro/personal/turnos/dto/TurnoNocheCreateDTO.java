package ar.buro.personal.turnos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class TurnoNocheCreateDTO {

    @NotNull(message = "El empleado es requerido")
    private Long empleadoId;

    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private BigDecimal horasTrabajadas;
    private BigDecimal montoCalculado;
    private String observaciones;
}

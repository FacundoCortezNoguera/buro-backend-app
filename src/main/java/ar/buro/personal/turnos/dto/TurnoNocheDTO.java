package ar.buro.personal.turnos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoNocheDTO {

    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private String empleadoCargo;
    private LocalDate fecha;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private BigDecimal horasTrabajadas;
    private BigDecimal montoCalculado;
    private String observaciones;
}

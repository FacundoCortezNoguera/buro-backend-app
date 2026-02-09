package ar.buro.personal.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoReporteHoraDTO {

    private Long empleadoId;
    private String nombre;
    private String cargo;

    // Mapa de fecha -> horas trabajadas ese día
    private Map<LocalDate, BigDecimal> horasPorDia;

    // Mapa de fecha -> monto ganado ese día
    private Map<LocalDate, BigDecimal> montosPorDia;

    // Totales
    private BigDecimal totalHoras;
    private BigDecimal descuento;
    private BigDecimal totalAPagar;
}

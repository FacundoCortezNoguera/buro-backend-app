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
public class EmpleadoReporteSemanalDTO {

    private Long empleadoId;
    private String nombre;
    private String cargo;

    // Mapa de fecha -> monto para cada día que trabajó
    private Map<LocalDate, BigDecimal> montosPorDia;

    // Descuento (si aplica)
    private BigDecimal descuento;

    // Total a pagar (suma de días - descuento)
    private BigDecimal totalAPagar;
}

package ar.buro.personal.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteHoraDTO {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // Lista de fechas del período (para las columnas)
    private List<LocalDate> diasDelPeriodo;

    // Empleados agrupados por cargo
    private Map<String, List<EmpleadoReporteHoraDTO>> empleadosPorCargo;

    // Totales por día (horas)
    private Map<LocalDate, BigDecimal> totalHorasPorDia;

    // Totales por día (monto)
    private Map<LocalDate, BigDecimal> totalMontosPorDia;

    // Totales generales
    private BigDecimal totalHoras;
    private BigDecimal totalGeneral;

    // Cantidad de empleados
    private int cantidadEmpleados;
}

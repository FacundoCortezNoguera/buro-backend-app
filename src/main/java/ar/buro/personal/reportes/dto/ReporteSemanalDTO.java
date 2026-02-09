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
public class ReporteSemanalDTO {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // Lista de fechas del período (para las columnas)
    private List<LocalDate> diasDelPeriodo;

    // Empleados agrupados por cargo
    private Map<String, List<EmpleadoReporteSemanalDTO>> empleadosPorCargo;

    // Totales por día
    private Map<LocalDate, BigDecimal> totalesPorDia;

    // Total general
    private BigDecimal totalGeneral;

    // Cantidad de empleados
    private int cantidadEmpleados;
}

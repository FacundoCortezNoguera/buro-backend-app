package ar.buro.personal.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenDTO {

    private String periodo; // SEMANA, MES, AÑO
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // Financiero
    private BigDecimal totalGastadoSueldos;
    private BigDecimal totalPagado;
    private BigDecimal totalPendiente;
    private int cantidadReportes;

    // Asistencias
    private int totalAsistencias;
    private int asistenciasPuntuales;
    private int asistenciasTarde;
    private int asistenciasTemprano;
    private double porcentajePuntualidad;

    // Empleados
    private int empleadosActivos;
    private int empleadosPorDia;
    private int empleadosPorHora;
}

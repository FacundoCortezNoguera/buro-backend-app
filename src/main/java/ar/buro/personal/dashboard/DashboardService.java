package ar.buro.personal.dashboard;

import ar.buro.personal.asistencias.AsistenciaRepository;
import ar.buro.personal.asistencias.EstadoAsistencia;
import ar.buro.personal.dashboard.dto.AlertaEmpleadoDTO;
import ar.buro.personal.dashboard.dto.DashboardResumenDTO;
import ar.buro.personal.dashboard.dto.EmpleadoRankingDTO;
import ar.buro.personal.empleados.Empleado;
import ar.buro.personal.empleados.EmpleadoRepository;
import ar.buro.personal.empleados.TipoPago;
import ar.buro.personal.pagos.PagoDiarioRepository;
import ar.buro.personal.reportes.ReporteRepository;
import ar.buro.personal.reportes.TipoReporte;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final AsistenciaRepository asistenciaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PagoDiarioRepository pagoDiarioRepository;
    private final ReporteRepository reporteRepository;

    /**
     * Obtiene el resumen del dashboard para un período dado
     */
    public DashboardResumenDTO getResumen(String periodo) {
        LocalDate[] fechas = calcularFechasPeriodo(periodo);
        LocalDate desde = fechas[0];
        LocalDate hasta = fechas[1];

        return buildResumen(periodo, desde, hasta);
    }

    /**
     * Obtiene el top de empleados por puntualidad
     */
    public List<EmpleadoRankingDTO> getTopEmpleadosPorPuntualidad(int limit, LocalDate desde, LocalDate hasta) {
        List<EmpleadoRankingDTO> rankings = buildEmpleadoRankings(desde, hasta);

        return rankings.stream()
                .filter(r -> r.getDiasTrabajados() > 0)
                .sorted((a, b) -> Double.compare(b.getPorcentajePuntualidad(), a.getPorcentajePuntualidad()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los empleados con más tardanzas
     */
    public List<EmpleadoRankingDTO> getEmpleadosMasTardanzas(int limit, LocalDate desde, LocalDate hasta) {
        List<EmpleadoRankingDTO> rankings = buildEmpleadoRankings(desde, hasta);

        return rankings.stream()
                .filter(r -> r.getAsistenciasTarde() > 0)
                .sorted((a, b) -> Integer.compare(b.getAsistenciasTarde(), a.getAsistenciasTarde()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el top de empleados por días trabajados
     */
    public List<EmpleadoRankingDTO> getTopEmpleadosPorDiasTrabajados(int limit, LocalDate desde, LocalDate hasta) {
        List<EmpleadoRankingDTO> rankings = buildEmpleadoRankings(desde, hasta);

        return rankings.stream()
                .filter(r -> r.getDiasTrabajados() > 0)
                .sorted((a, b) -> Integer.compare(b.getDiasTrabajados(), a.getDiasTrabajados()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las alertas de empleados problemáticos
     */
    public List<AlertaEmpleadoDTO> getAlertas(LocalDate desde, LocalDate hasta) {
        List<AlertaEmpleadoDTO> alertas = new ArrayList<>();

        // Obtener estadísticas por empleado
        Map<Long, Integer> tardanzasPorEmpleado = new HashMap<>();
        Map<Long, LocalDate> ultimaTardanza = new HashMap<>();
        Map<Long, Double> promedioMinutos = new HashMap<>();

        // Contar tardanzas por empleado
        List<Object[]> estadisticasEstado = asistenciaRepository.countByEmpleadoAndEstado(desde, hasta);
        for (Object[] row : estadisticasEstado) {
            Long empleadoId = (Long) row[0];
            EstadoAsistencia estado = (EstadoAsistencia) row[1];
            Long count = (Long) row[2];

            if (estado == EstadoAsistencia.TARDE) {
                tardanzasPorEmpleado.put(empleadoId, count.intValue());
            }
        }

        // Obtener última tardanza por empleado
        List<Object[]> ultimasTardanzas = asistenciaRepository.findLastDateByEmpleadoAndEstado(desde, hasta, EstadoAsistencia.TARDE);
        for (Object[] row : ultimasTardanzas) {
            Long empleadoId = (Long) row[0];
            LocalDate fecha = (LocalDate) row[1];
            ultimaTardanza.put(empleadoId, fecha);
        }

        // Obtener promedio de minutos de diferencia por empleado
        List<Object[]> promedios = asistenciaRepository.avgMinutosDiferenciaByEmpleado(desde, hasta);
        for (Object[] row : promedios) {
            Long empleadoId = (Long) row[0];
            Double avg = (Double) row[1];
            if (avg != null) {
                promedioMinutos.put(empleadoId, avg);
            }
        }

        // Obtener empleados activos
        Map<Long, Empleado> empleadosMap = empleadoRepository.findByActivoTrue().stream()
                .collect(Collectors.toMap(Empleado::getId, e -> e));

        // Generar alertas por tardanzas frecuentes (más de 3)
        for (Map.Entry<Long, Integer> entry : tardanzasPorEmpleado.entrySet()) {
            if (entry.getValue() > 3) {
                Empleado emp = empleadosMap.get(entry.getKey());
                if (emp != null) {
                    alertas.add(AlertaEmpleadoDTO.builder()
                            .empleadoId(emp.getId())
                            .nombreCompleto(emp.getNombreCompleto())
                            .cargo(emp.getCargo())
                            .tipoAlerta("TARDANZAS_FRECUENTES")
                            .descripcion(entry.getValue() + " llegadas tarde en el período")
                            .valor(entry.getValue())
                            .fechaUltimaIncidencia(ultimaTardanza.get(entry.getKey()))
                            .build());
                }
            }
        }

        // Generar alertas por bajo rendimiento (promedio > 15 minutos tarde)
        for (Map.Entry<Long, Double> entry : promedioMinutos.entrySet()) {
            if (entry.getValue() > 15) {
                Empleado emp = empleadosMap.get(entry.getKey());
                if (emp != null && !tardanzasPorEmpleado.containsKey(entry.getKey())) {
                    // Solo si no tiene ya alerta de tardanzas frecuentes
                    alertas.add(AlertaEmpleadoDTO.builder()
                            .empleadoId(emp.getId())
                            .nombreCompleto(emp.getNombreCompleto())
                            .cargo(emp.getCargo())
                            .tipoAlerta("BAJO_RENDIMIENTO")
                            .descripcion("Promedio de llegada: " + Math.round(entry.getValue()) + " minutos tarde")
                            .valor(entry.getValue().intValue())
                            .fechaUltimaIncidencia(ultimaTardanza.getOrDefault(entry.getKey(), hasta))
                            .build());
                }
            }
        }

        // Ordenar alertas por valor descendente
        alertas.sort((a, b) -> Integer.compare(b.getValor(), a.getValor()));

        return alertas;
    }

    /**
     * Obtiene el comparativo entre el período actual y el anterior
     */
    public Map<String, DashboardResumenDTO> getComparativo(String periodo) {
        LocalDate[] fechasActual = calcularFechasPeriodo(periodo);
        LocalDate[] fechasAnterior = calcularFechasPeriodoAnterior(periodo, fechasActual[0], fechasActual[1]);

        Map<String, DashboardResumenDTO> comparativo = new HashMap<>();
        comparativo.put("actual", buildResumen(periodo, fechasActual[0], fechasActual[1]));
        comparativo.put("anterior", buildResumen(periodo, fechasAnterior[0], fechasAnterior[1]));

        return comparativo;
    }

    // ========== Métodos privados ==========

    private DashboardResumenDTO buildResumen(String periodo, LocalDate desde, LocalDate hasta) {
        // Datos financieros
        BigDecimal totalGastado = pagoDiarioRepository.sumTotalByFechaBetween(desde, hasta);
        BigDecimal totalPagado = pagoDiarioRepository.sumPagadoByFechaBetween(desde, hasta);
        BigDecimal totalPendiente = pagoDiarioRepository.sumPendienteByFechaBetween(desde, hasta);
        int cantidadReportes = reporteRepository.findByFechaReporteBetween(desde, hasta).size();

        // Datos de asistencia
        long totalAsistencias = asistenciaRepository.countByFechaBetween(desde, hasta);
        long puntuales = asistenciaRepository.countByFechaBetweenAndEstado(desde, hasta, EstadoAsistencia.PUNTUAL);
        long tarde = asistenciaRepository.countByFechaBetweenAndEstado(desde, hasta, EstadoAsistencia.TARDE);
        long temprano = asistenciaRepository.countByFechaBetweenAndEstado(desde, hasta, EstadoAsistencia.TEMPRANO);

        double porcentajePuntualidad = totalAsistencias > 0
                ? ((double) (puntuales + temprano) / totalAsistencias) * 100
                : 0.0;

        // Datos de empleados
        long empleadosActivos = empleadoRepository.countByActivoTrue();
        long empleadosPorDia = empleadoRepository.countByActivoTrueAndTipoPago(TipoPago.DIA);
        long empleadosPorHora = empleadoRepository.countByActivoTrueAndTipoPago(TipoPago.HORA);

        return DashboardResumenDTO.builder()
                .periodo(periodo)
                .fechaDesde(desde)
                .fechaHasta(hasta)
                .totalGastadoSueldos(totalGastado != null ? totalGastado : BigDecimal.ZERO)
                .totalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO)
                .totalPendiente(totalPendiente != null ? totalPendiente : BigDecimal.ZERO)
                .cantidadReportes(cantidadReportes)
                .totalAsistencias((int) totalAsistencias)
                .asistenciasPuntuales((int) puntuales)
                .asistenciasTarde((int) tarde)
                .asistenciasTemprano((int) temprano)
                .porcentajePuntualidad(Math.round(porcentajePuntualidad * 10.0) / 10.0)
                .empleadosActivos((int) empleadosActivos)
                .empleadosPorDia((int) empleadosPorDia)
                .empleadosPorHora((int) empleadosPorHora)
                .build();
    }

    private List<EmpleadoRankingDTO> buildEmpleadoRankings(LocalDate desde, LocalDate hasta) {
        // Obtener todos los empleados activos
        List<Empleado> empleados = empleadoRepository.findByActivoTrue();
        Map<Long, Empleado> empleadosMap = empleados.stream()
                .collect(Collectors.toMap(Empleado::getId, e -> e));

        // Estadísticas de asistencia por empleado
        Map<Long, Map<EstadoAsistencia, Integer>> asistenciasPorEmpleado = new HashMap<>();
        List<Object[]> estadisticasEstado = asistenciaRepository.countByEmpleadoAndEstado(desde, hasta);
        for (Object[] row : estadisticasEstado) {
            Long empleadoId = (Long) row[0];
            EstadoAsistencia estado = (EstadoAsistencia) row[1];
            Long count = (Long) row[2];

            asistenciasPorEmpleado.computeIfAbsent(empleadoId, k -> new HashMap<>())
                    .put(estado, count.intValue());
        }

        // Total de asistencias por empleado
        Map<Long, Integer> totalAsistencias = new HashMap<>();
        List<Object[]> countAsistencias = asistenciaRepository.countAsistenciasByEmpleado(desde, hasta);
        for (Object[] row : countAsistencias) {
            Long empleadoId = (Long) row[0];
            Long count = (Long) row[1];
            totalAsistencias.put(empleadoId, count.intValue());
        }

        // Promedio de minutos por empleado
        Map<Long, Double> promedioMinutos = new HashMap<>();
        List<Object[]> promedios = asistenciaRepository.avgMinutosDiferenciaByEmpleado(desde, hasta);
        for (Object[] row : promedios) {
            Long empleadoId = (Long) row[0];
            Double avg = (Double) row[1];
            if (avg != null) {
                promedioMinutos.put(empleadoId, avg);
            }
        }

        // Total cobrado por empleado
        Map<Long, BigDecimal> totalCobrado = new HashMap<>();
        List<Object[]> montos = pagoDiarioRepository.sumMontoByEmpleado(desde, hasta);
        for (Object[] row : montos) {
            Long empleadoId = (Long) row[0];
            BigDecimal monto = (BigDecimal) row[1];
            totalCobrado.put(empleadoId, monto);
        }

        // Construir rankings
        List<EmpleadoRankingDTO> rankings = new ArrayList<>();
        for (Empleado emp : empleados) {
            Map<EstadoAsistencia, Integer> estados = asistenciasPorEmpleado.getOrDefault(emp.getId(), new HashMap<>());
            int puntuales = estados.getOrDefault(EstadoAsistencia.PUNTUAL, 0);
            int temprano = estados.getOrDefault(EstadoAsistencia.TEMPRANO, 0);
            int tarde = estados.getOrDefault(EstadoAsistencia.TARDE, 0);
            int diasTrabajados = totalAsistencias.getOrDefault(emp.getId(), 0);

            double porcentajePuntualidad = diasTrabajados > 0
                    ? ((double) (puntuales + temprano) / diasTrabajados) * 100
                    : 0.0;

            rankings.add(EmpleadoRankingDTO.builder()
                    .empleadoId(emp.getId())
                    .nombreCompleto(emp.getNombreCompleto())
                    .cargo(emp.getCargo())
                    .tipoPago(emp.getTipoPago().name())
                    .diasTrabajados(diasTrabajados)
                    .totalCobrado(totalCobrado.getOrDefault(emp.getId(), BigDecimal.ZERO))
                    .asistenciasPuntuales(puntuales + temprano)
                    .asistenciasTarde(tarde)
                    .porcentajePuntualidad(Math.round(porcentajePuntualidad * 10.0) / 10.0)
                    .promedioMinutosDiferencia(promedioMinutos.getOrDefault(emp.getId(), 0.0))
                    .build());
        }

        return rankings;
    }

    private LocalDate[] calcularFechasPeriodo(String periodo) {
        LocalDate hoy = LocalDate.now();
        LocalDate desde;
        LocalDate hasta;

        switch (periodo.toUpperCase()) {
            case "SEMANA":
                desde = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                hasta = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case "MES":
                desde = hoy.withDayOfMonth(1);
                hasta = hoy.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "AÑO":
                desde = hoy.withDayOfYear(1);
                hasta = hoy.with(TemporalAdjusters.lastDayOfYear());
                break;
            default:
                // Por defecto, semana actual
                desde = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                hasta = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }

        return new LocalDate[]{desde, hasta};
    }

    private LocalDate[] calcularFechasPeriodoAnterior(String periodo, LocalDate desdeActual, LocalDate hastaActual) {
        LocalDate desde;
        LocalDate hasta;

        switch (periodo.toUpperCase()) {
            case "SEMANA":
                desde = desdeActual.minusWeeks(1);
                hasta = hastaActual.minusWeeks(1);
                break;
            case "MES":
                desde = desdeActual.minusMonths(1);
                hasta = desde.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "AÑO":
                desde = desdeActual.minusYears(1);
                hasta = hastaActual.minusYears(1);
                break;
            default:
                desde = desdeActual.minusWeeks(1);
                hasta = hastaActual.minusWeeks(1);
        }

        return new LocalDate[]{desde, hasta};
    }
}

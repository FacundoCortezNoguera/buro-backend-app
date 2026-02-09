package ar.buro.personal.dashboard;

import ar.buro.personal.dashboard.dto.AlertaEmpleadoDTO;
import ar.buro.personal.dashboard.dto.DashboardResumenDTO;
import ar.buro.personal.dashboard.dto.EmpleadoRankingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'DUENO')")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Obtiene el resumen del dashboard para un período dado
     * GET /api/dashboard/resumen?periodo=SEMANA|MES|AÑO
     */
    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> getResumen(
            @RequestParam(defaultValue = "SEMANA") String periodo) {
        return ResponseEntity.ok(dashboardService.getResumen(periodo));
    }

    /**
     * Obtiene el ranking de empleados por puntualidad
     * GET /api/dashboard/ranking/puntualidad?limit=10&desde=&hasta=
     */
    @GetMapping("/ranking/puntualidad")
    public ResponseEntity<List<EmpleadoRankingDTO>> getRankingPuntualidad(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LocalDate[] fechas = calcularFechasDefault(desde, hasta);
        return ResponseEntity.ok(dashboardService.getTopEmpleadosPorPuntualidad(limit, fechas[0], fechas[1]));
    }

    /**
     * Obtiene el ranking de empleados con más tardanzas
     * GET /api/dashboard/ranking/tardanzas?limit=10&desde=&hasta=
     */
    @GetMapping("/ranking/tardanzas")
    public ResponseEntity<List<EmpleadoRankingDTO>> getRankingTardanzas(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LocalDate[] fechas = calcularFechasDefault(desde, hasta);
        return ResponseEntity.ok(dashboardService.getEmpleadosMasTardanzas(limit, fechas[0], fechas[1]));
    }

    /**
     * Obtiene el ranking de empleados por días trabajados
     * GET /api/dashboard/ranking/dias-trabajados?limit=10&desde=&hasta=
     */
    @GetMapping("/ranking/dias-trabajados")
    public ResponseEntity<List<EmpleadoRankingDTO>> getRankingDiasTrabajados(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LocalDate[] fechas = calcularFechasDefault(desde, hasta);
        return ResponseEntity.ok(dashboardService.getTopEmpleadosPorDiasTrabajados(limit, fechas[0], fechas[1]));
    }

    /**
     * Obtiene las alertas de empleados problemáticos
     * GET /api/dashboard/alertas?desde=&hasta=
     */
    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaEmpleadoDTO>> getAlertas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LocalDate[] fechas = calcularFechasDefault(desde, hasta);
        return ResponseEntity.ok(dashboardService.getAlertas(fechas[0], fechas[1]));
    }

    /**
     * Obtiene el comparativo entre el período actual y el anterior
     * GET /api/dashboard/comparativo?periodo=SEMANA|MES|AÑO
     */
    @GetMapping("/comparativo")
    public ResponseEntity<Map<String, DashboardResumenDTO>> getComparativo(
            @RequestParam(defaultValue = "SEMANA") String periodo) {
        return ResponseEntity.ok(dashboardService.getComparativo(periodo));
    }

    /**
     * Calcula fechas por defecto si no se proporcionan (último mes)
     */
    private LocalDate[] calcularFechasDefault(LocalDate desde, LocalDate hasta) {
        if (hasta == null) {
            hasta = LocalDate.now();
        }
        if (desde == null) {
            desde = hasta.minusMonths(1);
        }
        return new LocalDate[]{desde, hasta};
    }
}

package ar.buro.personal.reportes;

import ar.buro.personal.reportes.dto.ReporteDTO;
import com.lowagie.text.DocumentException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DUENO', 'RRHH')")
    public ResponseEntity<List<ReporteDTO>> getAll() {
        return ResponseEntity.ok(reporteService.findAll());
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DUENO', 'RRHH')")
    public ResponseEntity<List<ReporteDTO>> getByTipo(@PathVariable TipoReporte tipo) {
        return ResponseEntity.ok(reporteService.findByTipo(tipo));
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'DUENO', 'RRHH')")
    public ResponseEntity<List<ReporteDTO>> getByPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return ResponseEntity.ok(reporteService.findByPeriodo(desde, hasta));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DUENO', 'RRHH')")
    public ResponseEntity<ReporteDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.findById(id));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'DUENO', 'RRHH')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        try {
            byte[] pdfContent = reporteService.getPdfContent(id);
            ReporteDTO reporte = reporteService.findById(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", reporte.getArchivoNombre());
            headers.setContentLength(pdfContent.length);

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cierre-noche")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'SUPERVISOR')")
    public ResponseEntity<ReporteDTO> generarCierreNoche(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        try {
            ReporteDTO reporte = reporteService.generarReporteCierreNoche(fecha);
            return ResponseEntity.status(HttpStatus.CREATED).body(reporte);
        } catch (DocumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Genera el reporte semanal con formato de tabla por días y agrupado por cargo.
     * Si desde/hasta no se especifican, usa desde el último reporte hasta hoy.
     */
    @PostMapping("/semanal")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'SUPERVISOR')")
    public ResponseEntity<ReporteDTO> generarReporteSemanal(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        try {
            ReporteDTO reporte = reporteService.generarReporteSemanal(desde, hasta);
            return ResponseEntity.status(HttpStatus.CREATED).body(reporte);
        } catch (DocumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Genera el reporte para empleados que cobran por hora.
     * Si desde/hasta no se especifican, usa desde el último reporte de hora hasta hoy.
     */
    @PostMapping("/hora")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'SUPERVISOR')")
    public ResponseEntity<ReporteDTO> generarReporteHora(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        try {
            ReporteDTO reporte = reporteService.generarReporteHora(desde, hasta);
            return ResponseEntity.status(HttpStatus.CREATED).body(reporte);
        } catch (DocumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/regenerar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<ReporteDTO> regenerarReporte(@PathVariable Long id) {
        try {
            ReporteDTO reporte = reporteService.regenerarReporte(id);
            return ResponseEntity.ok(reporte);
        } catch (DocumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/gastos-periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'DUENO')")
    public ResponseEntity<Map<String, Object>> getGastosPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        BigDecimal total = reporteService.getTotalGastosPeriodo(desde, hasta);
        List<ReporteDTO> reportes = reporteService.findByPeriodo(desde, hasta);

        return ResponseEntity.ok(Map.of(
                "totalGastos", total,
                "cantidadReportes", reportes.size(),
                "desde", desde,
                "hasta", hasta
        ));
    }

    @PostMapping("/{id}/enviar-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'SUPERVISOR')")
    public ResponseEntity<ReporteDTO> enviarPorEmail(
            @PathVariable Long id,
            @RequestParam(required = false) String email
    ) {
        try {
            ReporteDTO reporte = reporteService.enviarReportePorEmail(id, email);
            return ResponseEntity.ok(reporte);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/email-configurado")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<Map<String, Object>> getEmailConfigurado() {
        List<String> emails = reporteService.getEmailsDestinatarios();
        return ResponseEntity.ok(Map.of(
                "emails", emails,
                "email", emails.isEmpty() ? "" : String.join(", ", emails)
        ));
    }
}

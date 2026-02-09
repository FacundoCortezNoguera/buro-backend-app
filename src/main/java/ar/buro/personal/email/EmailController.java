package ar.buro.personal.email;

import ar.buro.personal.email.dto.CierreNocheEmailDTO;
import ar.buro.personal.email.dto.EmailRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return Map.of(
            "enabled", emailService.isEmailEnabled(),
            "message", emailService.isEmailEnabled()
                ? "Servicio de email activo"
                : "Servicio de email en modo simulación"
        );
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        try {
            if (request.isHtml()) {
                emailService.sendHtmlEmail(request.getTo(), request.getSubject(), request.getBody());
            } else {
                emailService.sendSimpleEmail(request.getTo(), request.getSubject(), request.getBody());
            }
            return ResponseEntity.ok(Map.of("message", "Email enviado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error enviando email: " + e.getMessage()));
        }
    }

    @PostMapping("/cierre-noche")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> sendCierreNocheReport(@Valid @RequestBody CierreNocheEmailDTO request) {
        try {
            String htmlReport = buildCierreNocheHtml(request);
            emailService.sendCierreNocheReport(request.getTo(), request.getFecha(), htmlReport);
            return ResponseEntity.ok(Map.of("message", "Reporte de cierre enviado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error enviando reporte: " + e.getMessage()));
        }
    }

    private String buildCierreNocheHtml(CierreNocheEmailDTO request) {
        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    h1 { color: #1f2937; }
                    table { border-collapse: collapse; width: 100%; margin-top: 20px; }
                    th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                    th { background-color: #4f46e5; color: white; }
                    tr:nth-child(even) { background-color: #f3f4f6; }
                    .total { font-weight: bold; font-size: 1.2em; margin-top: 20px; }
                    .pagado { color: #059669; }
                    .pendiente { color: #dc2626; }
                </style>
            </head>
            <body>
            """);

        html.append("<h1>Reporte Cierre de Noche - ").append(request.getFecha()).append("</h1>");

        html.append("""
            <table>
                <tr>
                    <th>Empleado</th>
                    <th>Cargo</th>
                    <th>Entrada</th>
                    <th>Salida</th>
                    <th>Monto</th>
                    <th>Estado</th>
                </tr>
            """);

        if (request.getEmpleados() != null) {
            for (CierreNocheEmailDTO.EmpleadoResumenDTO emp : request.getEmpleados()) {
                html.append("<tr>");
                html.append("<td>").append(emp.getNombre()).append("</td>");
                html.append("<td>").append(emp.getCargo()).append("</td>");
                html.append("<td>").append(emp.getHoraEntrada() != null ? emp.getHoraEntrada() : "-").append("</td>");
                html.append("<td>").append(emp.getHoraSalida() != null ? emp.getHoraSalida() : "-").append("</td>");
                html.append("<td>$").append(emp.getMonto()).append("</td>");
                html.append("<td class=\"").append(emp.isPagado() ? "pagado" : "pendiente").append("\">")
                    .append(emp.isPagado() ? "Pagado" : "Pendiente").append("</td>");
                html.append("</tr>");
            }
        }

        html.append("</table>");

        html.append("<p class=\"total\">Total: $").append(request.getTotalPagos()).append("</p>");

        if (request.getObservaciones() != null && !request.getObservaciones().isEmpty()) {
            html.append("<p><strong>Observaciones:</strong> ").append(request.getObservaciones()).append("</p>");
        }

        html.append("""
            <hr>
            <p style="color: #6b7280; font-size: 0.9em;">
                Este es un mensaje automático generado por BURO Sistema de Gestión.
            </p>
            </body>
            </html>
            """);

        return html.toString();
    }
}

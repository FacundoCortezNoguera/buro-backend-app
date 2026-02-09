package ar.buro.personal.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@buro.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendSimpleEmail(String to, String subject, String body) {
        if (!emailEnabled || mailSender == null) {
            logger.info("Email deshabilitado. Simularía envío a: {} - Asunto: {}", to, subject);
            logger.debug("Contenido: {}", body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("Email enviado a: {} - Asunto: {}", to, subject);
        } catch (Exception e) {
            logger.error("Error enviando email a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error enviando email", e);
        }
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        if (!emailEnabled || mailSender == null) {
            logger.info("Email deshabilitado. Simularía envío HTML a: {} - Asunto: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            logger.info("Email HTML enviado a: {} - Asunto: {}", to, subject);
        } catch (MessagingException e) {
            logger.error("Error enviando email HTML a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error enviando email HTML", e);
        }
    }

    public void sendCierreNocheReport(String to, String fecha, String reportHtml) {
        String subject = "Reporte Cierre de Noche - " + fecha;
        sendHtmlEmail(to, subject, reportHtml);
    }

    @Async
    public void sendEmailWithPdfAttachment(String to, String subject, String htmlBody,
                                           byte[] pdfContent, String pdfFileName) {
        if (!emailEnabled || mailSender == null) {
            logger.info("Email deshabilitado. Simularía envío con adjunto PDF a: {} - Asunto: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            // Adjuntar PDF
            helper.addAttachment(pdfFileName, new ByteArrayResource(pdfContent), "application/pdf");

            mailSender.send(message);
            logger.info("Email con PDF adjunto enviado a: {} - Asunto: {} - Archivo: {}", to, subject, pdfFileName);
        } catch (MessagingException e) {
            logger.error("Error enviando email con PDF a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error enviando email con PDF adjunto", e);
        }
    }

    public void sendCierreNocheReportWithPdf(String to, String fecha, String resumenHtml,
                                              byte[] pdfContent, String pdfFileName) {
        String subject = "Reporte Cierre de Noche - " + fecha + " - BURO";

        String htmlBody = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; color: #333; }
                    .header { background: #4F46E5; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .footer { background: #f3f4f6; padding: 15px; text-align: center; color: #6b7280; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>BURO - Cierre de Noche</h1>
                    <p>%s</p>
                </div>
                <div class="content">
                    <p>Adjunto encontrarás el reporte de cierre de noche en formato PDF.</p>
                    %s
                </div>
                <div class="footer">
                    <p>Este es un mensaje automático del Sistema BURO.</p>
                    <p>No responder a este email.</p>
                </div>
            </body>
            </html>
            """, fecha, resumenHtml);

        sendEmailWithPdfAttachment(to, subject, htmlBody, pdfContent, pdfFileName);
    }

    public void sendPagoNotification(String to, String empleadoNombre, String monto, String fecha) {
        String subject = "Notificación de Pago - BURO";
        String body = String.format("""
            Hola %s,

            Se ha registrado un pago a tu nombre:

            Fecha: %s
            Monto: $%s

            Saludos,
            BURO Sistema de Gestión
            """, empleadoNombre, fecha, monto);

        sendSimpleEmail(to, subject, body);
    }

    @Async
    public void sendCierreNocheReportWithPdfToMultiple(
            List<String> recipients, String fecha, String resumenHtml,
            byte[] pdfContent, String pdfFileName) {

        if (recipients == null || recipients.isEmpty()) {
            logger.warn("No hay destinatarios para enviar el reporte de cierre de noche");
            return;
        }

        for (String to : recipients) {
            try {
                sendCierreNocheReportWithPdf(to, fecha, resumenHtml, pdfContent, pdfFileName);
            } catch (Exception e) {
                logger.error("Error enviando reporte a {}: {}", to, e.getMessage());
            }
        }
    }

    @Async
    public void sendCambioEmpleadoNotification(
            String to, String empleadoNombre, String tipoCambio,
            String campoModificado, String valorAnterior, String valorNuevo,
            String descripcion, String approveUrl, String token) {

        String rejectUrl = approveUrl.replace("/aprobar/", "/rechazar/");

        String subject = "Aprobación requerida: " + tipoCambio + " - " + empleadoNombre;

        String htmlBody = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; color: #333; margin: 0; }
                    .header { background: #4F46E5; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .detail-card { background: #f9fafb; padding: 15px; border-radius: 8px; margin: 15px 0; }
                    .detail-row { margin: 8px 0; }
                    .label { font-weight: bold; color: #374151; }
                    .old-value { color: #ef4444; text-decoration: line-through; }
                    .new-value { color: #22c55e; font-weight: bold; }
                    .buttons { margin: 25px 0; text-align: center; }
                    .btn { display: inline-block; padding: 12px 30px; border-radius: 6px; text-decoration: none; font-weight: bold; margin: 0 10px; }
                    .btn-approve { background: #22c55e; color: white; }
                    .btn-reject { background: #ef4444; color: white; }
                    .footer { background: #f3f4f6; padding: 15px; text-align: center; color: #6b7280; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>BURO - Aprobación de Cambio</h1>
                    <p>%s</p>
                </div>
                <div class="content">
                    <p>Se ha solicitado el siguiente cambio que requiere su aprobación:</p>
                    <div class="detail-card">
                        <div class="detail-row"><span class="label">Empleado:</span> %s</div>
                        <div class="detail-row"><span class="label">Tipo de cambio:</span> %s</div>
                        <div class="detail-row"><span class="label">Campo:</span> %s</div>
                        <div class="detail-row"><span class="label">Valor anterior:</span> <span class="old-value">%s</span></div>
                        <div class="detail-row"><span class="label">Valor nuevo:</span> <span class="new-value">%s</span></div>
                        %s
                    </div>
                    <div class="buttons">
                        <a href="%s" class="btn btn-approve">Aprobar</a>
                        <a href="%s" class="btn btn-reject">Rechazar</a>
                    </div>
                    <p style="color: #6b7280; font-size: 12px; text-align: center;">
                        Este enlace expira en 48 horas.
                    </p>
                </div>
                <div class="footer">
                    <p>Este es un mensaje automático del Sistema BURO.</p>
                </div>
            </body>
            </html>
            """,
                tipoCambio,
                empleadoNombre,
                tipoCambio,
                campoModificado,
                valorAnterior != null ? valorAnterior : "N/A",
                valorNuevo,
                descripcion != null && !descripcion.isBlank()
                        ? "<div class=\"detail-row\"><span class=\"label\">Descripción:</span> " + descripcion + "</div>"
                        : "",
                approveUrl,
                rejectUrl
        );

        sendHtmlEmail(to, subject, htmlBody);
    }

    public boolean isEmailEnabled() {
        return emailEnabled && mailSender != null;
    }
}

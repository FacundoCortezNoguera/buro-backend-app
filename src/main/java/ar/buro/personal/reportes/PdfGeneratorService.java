package ar.buro.personal.reportes;

import ar.buro.personal.config.ConfiguracionRepository;
import ar.buro.personal.reportes.dto.EmpleadoReporteHoraDTO;
import ar.buro.personal.reportes.dto.EmpleadoReporteSemanalDTO;
import ar.buro.personal.reportes.dto.ReporteHoraDTO;
import ar.buro.personal.reportes.dto.ReporteSemanalDTO;
import ar.buro.personal.turnos.dto.TurnoNocheDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PdfGeneratorService {

    private final ConfiguracionRepository configuracionRepository;

    @Value("${app.reportes.path:./reportes}")
    private String reportesPath;

    public PdfGeneratorService(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    public byte[] generarReporteCierreNoche(
            LocalDate fecha,
            List<TurnoNocheDTO> turnos,
            BigDecimal totalNoche
    ) throws DocumentException, IOException {

        String nombreNegocio = configuracionRepository.findByClave("NOMBRE_NEGOCIO")
                .map(c -> c.getValor())
                .orElse("BURO");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.DARK_GRAY);
        Paragraph title = new Paragraph(nombreNegocio + " - Cierre de Noche", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        // Fecha
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Color.GRAY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Paragraph fechaParagraph = new Paragraph("Fecha: " + fecha.format(formatter), dateFont);
        fechaParagraph.setAlignment(Element.ALIGN_CENTER);
        fechaParagraph.setSpacingAfter(20);
        document.add(fechaParagraph);

        // Tabla de empleados
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 2, 2, 2, 2});

        // Headers
        addTableHeader(table, "Empleado");
        addTableHeader(table, "Cargo");
        addTableHeader(table, "Entrada");
        addTableHeader(table, "Salida");
        addTableHeader(table, "Monto");

        // Rows
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (TurnoNocheDTO turno : turnos) {
            addTableCell(table, turno.getEmpleadoNombre());
            addTableCell(table, turno.getEmpleadoCargo() != null ? turno.getEmpleadoCargo() : "-");
            addTableCell(table, turno.getHoraEntrada() != null ?
                    turno.getHoraEntrada().format(timeFormatter) : "-");
            addTableCell(table, turno.getHoraSalida() != null ?
                    turno.getHoraSalida().format(timeFormatter) : "-");
            addTableCell(table, "$" + formatMonto(turno.getMontoCalculado()));
        }

        document.add(table);

        // Total
        document.add(new Paragraph("\n"));
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        Paragraph totalParagraph = new Paragraph(
                "TOTAL DE LA NOCHE: $" + formatMonto(totalNoche),
                totalFont
        );
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        totalParagraph.setSpacingBefore(10);
        document.add(totalParagraph);

        // Resumen
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.GRAY);
        Paragraph summary = new Paragraph(
                "Empleados que trabajaron: " + turnos.size(),
                summaryFont
        );
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        // Footer
        document.add(new Paragraph("\n\n"));
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.LIGHT_GRAY);
        Paragraph footer = new Paragraph(
                "Generado automáticamente por Sistema BURO - " +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        return baos.toByteArray();
    }

    /**
     * Genera el reporte semanal con formato similar al de la imagen de referencia:
     * - Período (desde/hasta)
     * - Tabla agrupada por cargo con columnas por día
     * - Totales por día y por empleado
     */
    public byte[] generarReporteSemanal(ReporteSemanalDTO datos) throws DocumentException, IOException {
        String nombreNegocio = configuracionRepository.findByClave("NOMBRE_NEGOCIO")
                .map(c -> c.getValor())
                .orElse("BURO");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Usar página horizontal para que quepan más columnas
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, baos);

        document.open();

        // Colores del diseño
        Color primaryColor = new Color(124, 58, 237);  // Violeta (#7c3aed)
        Color headerBg = new Color(75, 85, 99);        // Gray-600
        Color lightBg = new Color(249, 250, 251);      // Gray-50
        Color textDark = new Color(17, 24, 39);        // Gray-900

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
        Paragraph title = new Paragraph(nombreNegocio + " - Reporte de Pagos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);

        // Período
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE dd/MM/yyyy", new Locale("es", "AR"));
        Font periodoFont = FontFactory.getFont(FontFactory.HELVETICA, 11, textDark);

        Paragraph periodoDesde = new Paragraph("Fecha desde: " + datos.getFechaDesde().format(formatter), periodoFont);
        Paragraph periodoHasta = new Paragraph("Fecha hasta: " + datos.getFechaHasta().format(formatter), periodoFont);
        periodoDesde.setSpacingAfter(2);
        periodoHasta.setSpacingAfter(15);
        document.add(periodoDesde);
        document.add(periodoHasta);

        // Calcular número de columnas: PUESTO + NOMBRE + días + DESC + A PAGAR
        int numDias = datos.getDiasDelPeriodo().size();
        int numColumnas = 2 + numDias + 2; // puesto, nombre, días..., desc, a pagar

        PdfPTable table = new PdfPTable(numColumnas);
        table.setWidthPercentage(100);

        // Configurar anchos de columna
        float[] widths = new float[numColumnas];
        widths[0] = 2f;  // PUESTO
        widths[1] = 3f;  // NOMBRE
        for (int i = 0; i < numDias; i++) {
            widths[2 + i] = 1.2f;  // Días
        }
        widths[numColumnas - 2] = 1.2f;  // DESC
        widths[numColumnas - 1] = 1.5f;  // A PAGAR
        table.setWidths(widths);

        // Headers
        addHeaderCell(table, "PUESTO", headerBg);
        addHeaderCell(table, "NOMBRE Y APELLIDO", headerBg);

        // Headers de días (abreviados)
        DateTimeFormatter diaFormatter = DateTimeFormatter.ofPattern("EEE", new Locale("es", "AR"));
        for (LocalDate dia : datos.getDiasDelPeriodo()) {
            String diaAbrev = dia.format(diaFormatter).toUpperCase().substring(0, 3);
            addHeaderCell(table, diaAbrev, headerBg);
        }

        addHeaderCell(table, "DESC", headerBg);
        addHeaderCell(table, "A PAGAR", headerBg);

        // Filas por cargo
        boolean alternateBg = false;
        for (Map.Entry<String, List<EmpleadoReporteSemanalDTO>> entry : datos.getEmpleadosPorCargo().entrySet()) {
            String cargo = entry.getKey();
            List<EmpleadoReporteSemanalDTO> empleados = entry.getValue();

            for (EmpleadoReporteSemanalDTO emp : empleados) {
                Color rowBg = alternateBg ? lightBg : Color.WHITE;

                // Cargo
                addDataCell(table, cargo, rowBg, Element.ALIGN_LEFT, false);
                // Nombre
                addDataCell(table, emp.getNombre(), rowBg, Element.ALIGN_LEFT, false);

                // Montos por día
                for (LocalDate dia : datos.getDiasDelPeriodo()) {
                    BigDecimal monto = emp.getMontosPorDia() != null ?
                            emp.getMontosPorDia().get(dia) : null;
                    String montoStr = monto != null ? formatMonto(monto) : "";
                    addDataCell(table, montoStr, rowBg, Element.ALIGN_RIGHT, false);
                }

                // Descuento
                String descStr = emp.getDescuento() != null && emp.getDescuento().compareTo(BigDecimal.ZERO) > 0 ?
                        formatMonto(emp.getDescuento()) : "";
                addDataCell(table, descStr, rowBg, Element.ALIGN_RIGHT, false);

                // Total a pagar
                String totalStr = emp.getTotalAPagar() != null ? formatMonto(emp.getTotalAPagar()) : "0";
                addDataCell(table, totalStr, rowBg, Element.ALIGN_RIGHT, true);

                alternateBg = !alternateBg;
            }
        }

        // Fila de totales
        Color totalsBg = new Color(229, 231, 235); // Gray-200
        addTotalsCell(table, "Totales", totalsBg, 2);

        for (LocalDate dia : datos.getDiasDelPeriodo()) {
            BigDecimal totalDia = datos.getTotalesPorDia() != null ?
                    datos.getTotalesPorDia().get(dia) : BigDecimal.ZERO;
            String totalStr = totalDia != null ? formatMonto(totalDia) : "0";
            addDataCell(table, totalStr, totalsBg, Element.ALIGN_RIGHT, true);
        }

        addDataCell(table, "0", totalsBg, Element.ALIGN_RIGHT, true); // DESC total
        addDataCell(table, formatMonto(datos.getTotalGeneral()), totalsBg, Element.ALIGN_RIGHT, true);

        document.add(table);

        // Footer con resumen
        document.add(new Paragraph("\n"));
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph summary = new Paragraph(
                "Total empleados: " + datos.getCantidadEmpleados() + " | " +
                "Total a pagar: $" + formatMonto(datos.getTotalGeneral()),
                summaryFont
        );
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        // Footer de generación
        document.add(new Paragraph("\n"));
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.LIGHT_GRAY);
        Paragraph footer = new Paragraph(
                "Generado por Sistema BURO - " +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        return baos.toByteArray();
    }

    /**
     * Genera el reporte para empleados por hora.
     * Muestra horas trabajadas y monto por cada día.
     */
    public byte[] generarReporteHora(ReporteHoraDTO datos) throws DocumentException, IOException {
        String nombreNegocio = configuracionRepository.findByClave("NOMBRE_NEGOCIO")
                .map(c -> c.getValor())
                .orElse("BURO");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, baos);

        document.open();

        // Colores del diseño - verde para diferenciar
        Color primaryColor = new Color(16, 185, 129);   // Emerald-500
        Color headerBg = new Color(6, 95, 70);          // Emerald-800
        Color lightBg = new Color(236, 253, 245);       // Emerald-50
        Color textDark = new Color(17, 24, 39);         // Gray-900
        Color horasColor = new Color(107, 114, 128);    // Gray-500 para las horas

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
        Paragraph title = new Paragraph(nombreNegocio + " - Reporte de Pagos por Hora", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15);
        document.add(title);

        // Período
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE dd/MM/yyyy", new Locale("es", "AR"));
        Font periodoFont = FontFactory.getFont(FontFactory.HELVETICA, 11, textDark);

        Paragraph periodoDesde = new Paragraph("Fecha desde: " + datos.getFechaDesde().format(formatter), periodoFont);
        Paragraph periodoHasta = new Paragraph("Fecha hasta: " + datos.getFechaHasta().format(formatter), periodoFont);
        periodoDesde.setSpacingAfter(2);
        periodoHasta.setSpacingAfter(15);
        document.add(periodoDesde);
        document.add(periodoHasta);

        // Columnas: PUESTO, NOMBRE, [días...], TOTAL HS, DESC, A PAGAR
        int numDias = datos.getDiasDelPeriodo().size();
        int numColumnas = 2 + numDias + 3; // +3 por TOTAL HS, DESC, A PAGAR

        PdfPTable table = new PdfPTable(numColumnas);
        table.setWidthPercentage(100);

        // Configurar anchos
        float[] widths = new float[numColumnas];
        widths[0] = 2f;   // PUESTO
        widths[1] = 3f;   // NOMBRE
        for (int i = 0; i < numDias; i++) {
            widths[2 + i] = 1.5f;  // Días (más ancho para mostrar hs + $)
        }
        widths[numColumnas - 3] = 1.2f;  // TOTAL HS
        widths[numColumnas - 2] = 1f;    // DESC
        widths[numColumnas - 1] = 1.5f;  // A PAGAR
        table.setWidths(widths);

        // Headers
        addHeaderCell(table, "PUESTO", headerBg);
        addHeaderCell(table, "NOMBRE Y APELLIDO", headerBg);

        DateTimeFormatter diaFormatter = DateTimeFormatter.ofPattern("EEE", new Locale("es", "AR"));
        for (LocalDate dia : datos.getDiasDelPeriodo()) {
            String diaAbrev = dia.format(diaFormatter).toUpperCase().substring(0, 3);
            addHeaderCell(table, diaAbrev, headerBg);
        }

        addHeaderCell(table, "TOT HS", headerBg);
        addHeaderCell(table, "DESC", headerBg);
        addHeaderCell(table, "A PAGAR", headerBg);

        // Filas por cargo
        boolean alternateBg = false;
        for (Map.Entry<String, List<EmpleadoReporteHoraDTO>> entry : datos.getEmpleadosPorCargo().entrySet()) {
            String cargo = entry.getKey();
            List<EmpleadoReporteHoraDTO> empleados = entry.getValue();

            for (EmpleadoReporteHoraDTO emp : empleados) {
                Color rowBg = alternateBg ? lightBg : Color.WHITE;

                addDataCell(table, cargo, rowBg, Element.ALIGN_LEFT, false);
                addDataCell(table, emp.getNombre(), rowBg, Element.ALIGN_LEFT, false);

                // Celdas por día: muestran HORAS + MONTO
                for (LocalDate dia : datos.getDiasDelPeriodo()) {
                    BigDecimal horas = emp.getHorasPorDia() != null ?
                            emp.getHorasPorDia().get(dia) : null;
                    BigDecimal monto = emp.getMontosPorDia() != null ?
                            emp.getMontosPorDia().get(dia) : null;

                    if (horas != null && monto != null) {
                        // Mostrar horas y monto en la misma celda
                        String cellText = horas.intValue() + "hs\n$" + formatMonto(monto);
                        addHoraMontoCell(table, cellText, rowBg, horasColor);
                    } else {
                        addDataCell(table, "", rowBg, Element.ALIGN_CENTER, false);
                    }
                }

                // Total horas
                String totalHsStr = emp.getTotalHoras() != null ? emp.getTotalHoras().intValue() + "hs" : "0hs";
                addDataCell(table, totalHsStr, rowBg, Element.ALIGN_CENTER, true);

                // Descuento
                String descStr = emp.getDescuento() != null && emp.getDescuento().compareTo(BigDecimal.ZERO) > 0 ?
                        formatMonto(emp.getDescuento()) : "";
                addDataCell(table, descStr, rowBg, Element.ALIGN_RIGHT, false);

                // Total a pagar
                String totalStr = emp.getTotalAPagar() != null ? formatMonto(emp.getTotalAPagar()) : "0";
                addDataCell(table, totalStr, rowBg, Element.ALIGN_RIGHT, true);

                alternateBg = !alternateBg;
            }
        }

        // Fila de totales
        Color totalsBg = new Color(209, 250, 229); // Emerald-100
        addTotalsCell(table, "Totales", totalsBg, 2);

        for (LocalDate dia : datos.getDiasDelPeriodo()) {
            BigDecimal horasDia = datos.getTotalHorasPorDia() != null ?
                    datos.getTotalHorasPorDia().get(dia) : BigDecimal.ZERO;
            BigDecimal montoDia = datos.getTotalMontosPorDia() != null ?
                    datos.getTotalMontosPorDia().get(dia) : BigDecimal.ZERO;

            if (horasDia != null && horasDia.compareTo(BigDecimal.ZERO) > 0) {
                String cellText = horasDia.intValue() + "hs\n$" + formatMonto(montoDia);
                addHoraMontoCell(table, cellText, totalsBg, Color.DARK_GRAY);
            } else {
                addDataCell(table, "", totalsBg, Element.ALIGN_CENTER, false);
            }
        }

        // Total horas general
        String totalHsGeneral = datos.getTotalHoras() != null ? datos.getTotalHoras().intValue() + "hs" : "0hs";
        addDataCell(table, totalHsGeneral, totalsBg, Element.ALIGN_CENTER, true);

        addDataCell(table, "0", totalsBg, Element.ALIGN_RIGHT, true);
        addDataCell(table, formatMonto(datos.getTotalGeneral()), totalsBg, Element.ALIGN_RIGHT, true);

        document.add(table);

        // Footer con resumen
        document.add(new Paragraph("\n"));
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph summary = new Paragraph(
                "Total empleados: " + datos.getCantidadEmpleados() + " | " +
                "Total horas: " + (datos.getTotalHoras() != null ? datos.getTotalHoras().intValue() : 0) + "hs | " +
                "Total a pagar: $" + formatMonto(datos.getTotalGeneral()),
                summaryFont
        );
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        // Footer de generación
        document.add(new Paragraph("\n"));
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.LIGHT_GRAY);
        Paragraph footer = new Paragraph(
                "Generado por Sistema BURO - " +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        return baos.toByteArray();
    }

    /**
     * Celda especial para mostrar horas y monto en dos líneas
     */
    private void addHoraMontoCell(PdfPTable table, String text, Color bgColor, Color textColor) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 7, textColor);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(4);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addHeaderCell(PdfPTable table, String text, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addDataCell(PdfPTable table, String text, Color bgColor, int align, boolean bold) {
        Font font = bold ?
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.DARK_GRAY) :
                FontFactory.getFont(FontFactory.HELVETICA, 8, Color.DARK_GRAY);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addTotalsCell(PdfPTable table, String text, Color bgColor, int colspan) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    public String guardarPdf(byte[] pdfContent, String nombreArchivo) throws IOException {
        // Crear directorio si no existe
        File dir = new File(reportesPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fullPath = reportesPath + "/" + nombreArchivo;
        try (FileOutputStream fos = new FileOutputStream(fullPath)) {
            fos.write(pdfContent);
        }

        return fullPath;
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE)));
        cell.setBackgroundColor(new Color(75, 85, 99)); // Gray-600
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY)));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private String formatMonto(BigDecimal monto) {
        if (monto == null) return "0";
        return String.format("%,.0f", monto);
    }
}

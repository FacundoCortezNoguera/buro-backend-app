package ar.buro.personal.reportes;

import ar.buro.personal.asistencias.Asistencia;
import ar.buro.personal.asistencias.AsistenciaRepository;
import ar.buro.personal.email.EmailService;
import ar.buro.personal.empleados.Empleado;
import ar.buro.personal.empleados.TipoPago;
import ar.buro.personal.notificaciones.DestinatarioService;
import ar.buro.personal.reportes.dto.EmpleadoReporteHoraDTO;
import ar.buro.personal.reportes.dto.EmpleadoReporteSemanalDTO;
import ar.buro.personal.reportes.dto.ReporteDTO;
import ar.buro.personal.reportes.dto.ReporteHoraDTO;
import ar.buro.personal.reportes.dto.ReporteSemanalDTO;
import ar.buro.personal.turnos.dto.TurnoNocheDTO;
import com.lowagie.text.DocumentException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final AsistenciaRepository asistenciaRepository;
    private final DestinatarioService destinatarioService;
    private final EmailService emailService;

    public ReporteService(
            ReporteRepository reporteRepository,
            PdfGeneratorService pdfGeneratorService,
            AsistenciaRepository asistenciaRepository,
            DestinatarioService destinatarioService,
            EmailService emailService
    ) {
        this.reporteRepository = reporteRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.asistenciaRepository = asistenciaRepository;
        this.destinatarioService = destinatarioService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> findAll() {
        return reporteRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> findByTipo(TipoReporte tipo) {
        return reporteRepository.findByTipoOrderByFechaReporteDesc(tipo).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> findByPeriodo(LocalDate desde, LocalDate hasta) {
        return reporteRepository.findByFechaReporteBetween(desde, hasta).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReporteDTO findById(Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reporte no encontrado"));
        return toDTO(reporte);
    }

    @Transactional(readOnly = true)
    public byte[] getPdfContent(Long id) throws IOException {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reporte no encontrado"));

        if (reporte.getArchivoPath() == null) {
            throw new IllegalStateException("El reporte no tiene archivo asociado");
        }

        java.io.File file = new java.io.File(reporte.getArchivoPath());
        if (!file.exists()) {
            throw new IOException("Archivo no encontrado: " + reporte.getArchivoPath());
        }

        return java.nio.file.Files.readAllBytes(file.toPath());
    }

    public ReporteDTO generarReporteCierreNoche(LocalDate fecha) throws DocumentException, IOException {
        // Verificar si ya existe un reporte para esa fecha
        var existente = reporteRepository.findByTipoAndFechaReporte(TipoReporte.CIERRE_NOCHE, fecha);
        if (existente.isPresent()) {
            // Regenerar el reporte existente
            return regenerarReporte(existente.get().getId());
        }

        // Obtener asistencias de la fecha y convertir a TurnoNocheDTO
        List<Asistencia> asistencias = asistenciaRepository.findByFechaWithEmpleado(fecha);

        if (asistencias.isEmpty()) {
            throw new IllegalStateException("No hay asistencias registradas para la fecha: " + fecha);
        }

        List<TurnoNocheDTO> turnos = asistencias.stream()
                .map(this::asistenciaToTurnoDTO)
                .collect(Collectors.toList());

        // Calcular total
        BigDecimal totalNoche = turnos.stream()
                .map(TurnoNocheDTO::getMontoCalculado)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Generar PDF
        byte[] pdfContent = pdfGeneratorService.generarReporteCierreNoche(fecha, turnos, totalNoche);

        // Guardar archivo
        String nombreArchivo = "cierre_noche_" + fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        String archivoPath = pdfGeneratorService.guardarPdf(pdfContent, nombreArchivo);

        // Crear registro de reporte
        Reporte reporte = new Reporte(
                TipoReporte.CIERRE_NOCHE,
                fecha,
                "Cierre de Noche - " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        reporte.setArchivoNombre(nombreArchivo);
        reporte.setArchivoPath(archivoPath);
        reporte.setTotalMonto(totalNoche);
        reporte.setCantidadEmpleados(turnos.size());
        reporte.setDescripcion("Reporte de cierre de noche con " + turnos.size() + " empleados");

        reporte = reporteRepository.save(reporte);

        return toDTO(reporte);
    }

    /**
     * Genera el reporte semanal con formato de tabla por días y agrupado por cargo.
     * Si desde es null, usa la fecha del último reporte + 1 día.
     * Si hasta es null, usa la fecha actual.
     */
    public ReporteDTO generarReporteSemanal(LocalDate desde, LocalDate hasta) throws DocumentException, IOException {
        LocalDate hoy = LocalDate.now();

        // Si no se especifica desde, usar día siguiente al último reporte
        if (desde == null) {
            desde = reporteRepository.findByTipoOrderByFechaReporteDesc(TipoReporte.CIERRE_NOCHE)
                    .stream()
                    .findFirst()
                    .map(r -> r.getFechaReporte().plusDays(1))
                    .orElse(hoy.minusDays(7));
        }

        // Si no se especifica hasta, usar hoy
        if (hasta == null) {
            hasta = hoy;
        }

        // Obtener asistencias del período
        List<Asistencia> asistencias = asistenciaRepository.findByFechaBetweenWithEmpleado(desde, hasta);

        if (asistencias.isEmpty()) {
            throw new IllegalStateException("No hay asistencias registradas para el período: " + desde + " a " + hasta);
        }

        // Construir lista de días del período
        List<LocalDate> diasDelPeriodo = new ArrayList<>();
        LocalDate dia = desde;
        while (!dia.isAfter(hasta)) {
            diasDelPeriodo.add(dia);
            dia = dia.plusDays(1);
        }

        // Agrupar asistencias por empleado
        Map<Long, List<Asistencia>> asistenciasPorEmpleado = asistencias.stream()
                .collect(Collectors.groupingBy(a -> a.getEmpleado().getId()));

        // Construir DTOs de empleados
        List<EmpleadoReporteSemanalDTO> empleadosDTO = new ArrayList<>();
        for (Map.Entry<Long, List<Asistencia>> entry : asistenciasPorEmpleado.entrySet()) {
            List<Asistencia> asisEmpleado = entry.getValue();
            Empleado empleado = asisEmpleado.get(0).getEmpleado();

            // Calcular montos por día
            Map<LocalDate, BigDecimal> montosPorDia = new HashMap<>();
            BigDecimal totalEmpleado = BigDecimal.ZERO;

            for (Asistencia asis : asisEmpleado) {
                BigDecimal monto = calcularMontoAsistencia(asis);
                montosPorDia.put(asis.getFecha(), monto);
                totalEmpleado = totalEmpleado.add(monto);
            }

            empleadosDTO.add(EmpleadoReporteSemanalDTO.builder()
                    .empleadoId(empleado.getId())
                    .nombre(empleado.getNombreCompleto())
                    .cargo(empleado.getCargo() != null ? empleado.getCargo() : "Sin cargo")
                    .montosPorDia(montosPorDia)
                    .descuento(BigDecimal.ZERO) // Por ahora sin descuentos
                    .totalAPagar(totalEmpleado)
                    .build());
        }

        // Agrupar por cargo y ordenar
        Map<String, List<EmpleadoReporteSemanalDTO>> empleadosPorCargo = empleadosDTO.stream()
                .sorted(Comparator.comparing(EmpleadoReporteSemanalDTO::getCargo)
                        .thenComparing(EmpleadoReporteSemanalDTO::getNombre))
                .collect(Collectors.groupingBy(
                        EmpleadoReporteSemanalDTO::getCargo,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Calcular totales por día
        Map<LocalDate, BigDecimal> totalesPorDia = new HashMap<>();
        for (LocalDate d : diasDelPeriodo) {
            BigDecimal totalDia = empleadosDTO.stream()
                    .map(e -> e.getMontosPorDia().getOrDefault(d, BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalesPorDia.put(d, totalDia);
        }

        // Total general
        BigDecimal totalGeneral = empleadosDTO.stream()
                .map(EmpleadoReporteSemanalDTO::getTotalAPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Construir DTO del reporte
        ReporteSemanalDTO reporteData = ReporteSemanalDTO.builder()
                .fechaDesde(desde)
                .fechaHasta(hasta)
                .diasDelPeriodo(diasDelPeriodo)
                .empleadosPorCargo(empleadosPorCargo)
                .totalesPorDia(totalesPorDia)
                .totalGeneral(totalGeneral)
                .cantidadEmpleados(empleadosDTO.size())
                .build();

        // Generar PDF
        byte[] pdfContent = pdfGeneratorService.generarReporteSemanal(reporteData);

        // Guardar archivo
        String nombreArchivo = "reporte_semanal_" + desde.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                + "_" + hasta.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        String archivoPath = pdfGeneratorService.guardarPdf(pdfContent, nombreArchivo);

        // Crear registro de reporte (usamos la fecha hasta como fecha del reporte)
        Reporte reporte = new Reporte(
                TipoReporte.CIERRE_NOCHE,
                hasta,
                "Reporte Semanal - " + desde.format(DateTimeFormatter.ofPattern("dd/MM"))
                        + " al " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        reporte.setArchivoNombre(nombreArchivo);
        reporte.setArchivoPath(archivoPath);
        reporte.setTotalMonto(totalGeneral);
        reporte.setCantidadEmpleados(empleadosDTO.size());
        reporte.setDescripcion("Reporte semanal con " + empleadosDTO.size() + " empleados");

        reporte = reporteRepository.save(reporte);

        return toDTO(reporte);
    }

    private BigDecimal calcularMontoAsistencia(Asistencia asistencia) {
        Empleado empleado = asistencia.getEmpleado();

        BigDecimal horasTrabajadas = BigDecimal.valueOf(
                empleado.getHorasPorDia() != null ? empleado.getHorasPorDia() : 8
        );

        BigDecimal montoBase;
        if (empleado.getTipoPago() == TipoPago.HORA) {
            BigDecimal tarifaHora = empleado.getCobraPorHora() != null
                    ? empleado.getCobraPorHora()
                    : BigDecimal.ZERO;
            montoBase = tarifaHora.multiply(horasTrabajadas);
        } else {
            montoBase = empleado.getCobraPorDia() != null
                    ? empleado.getCobraPorDia()
                    : BigDecimal.ZERO;
        }

        // Aplicar ajustes
        BigDecimal montoFinal = montoBase;
        if (empleado.getAjustePorcentaje() != null && empleado.getAjustePorcentaje().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal ajuste = montoBase.multiply(empleado.getAjustePorcentaje())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            montoFinal = montoFinal.add(ajuste);
        }
        if (empleado.getAjusteMonto() != null) {
            montoFinal = montoFinal.add(empleado.getAjusteMonto());
        }

        return montoFinal;
    }

    /**
     * Genera el reporte para empleados que cobran por hora.
     * Incluye horas trabajadas y monto por cada día.
     */
    public ReporteDTO generarReporteHora(LocalDate desde, LocalDate hasta) throws DocumentException, IOException {
        LocalDate hoy = LocalDate.now();

        // Si no se especifica desde, usar día siguiente al último reporte de hora
        if (desde == null) {
            desde = reporteRepository.findByTipoOrderByFechaReporteDesc(TipoReporte.CIERRE_HORA)
                    .stream()
                    .findFirst()
                    .map(r -> r.getFechaReporte().plusDays(1))
                    .orElse(hoy.minusDays(14));
        }

        if (hasta == null) {
            hasta = hoy;
        }

        // Obtener asistencias del período
        List<Asistencia> todasAsistencias = asistenciaRepository.findByFechaBetweenWithEmpleado(desde, hasta);

        // Filtrar solo empleados que cobran por HORA
        List<Asistencia> asistencias = todasAsistencias.stream()
                .filter(a -> a.getEmpleado().getTipoPago() == TipoPago.HORA)
                .collect(Collectors.toList());

        if (asistencias.isEmpty()) {
            throw new IllegalStateException("No hay asistencias de empleados por hora para el período: " + desde + " a " + hasta);
        }

        // Construir lista de días del período
        List<LocalDate> diasDelPeriodo = new ArrayList<>();
        LocalDate dia = desde;
        while (!dia.isAfter(hasta)) {
            diasDelPeriodo.add(dia);
            dia = dia.plusDays(1);
        }

        // Agrupar asistencias por empleado
        Map<Long, List<Asistencia>> asistenciasPorEmpleado = asistencias.stream()
                .collect(Collectors.groupingBy(a -> a.getEmpleado().getId()));

        // Construir DTOs de empleados con horas y montos
        List<EmpleadoReporteHoraDTO> empleadosDTO = new ArrayList<>();
        for (Map.Entry<Long, List<Asistencia>> entry : asistenciasPorEmpleado.entrySet()) {
            List<Asistencia> asisEmpleado = entry.getValue();
            Empleado empleado = asisEmpleado.get(0).getEmpleado();

            Map<LocalDate, BigDecimal> horasPorDia = new HashMap<>();
            Map<LocalDate, BigDecimal> montosPorDia = new HashMap<>();
            BigDecimal totalHoras = BigDecimal.ZERO;
            BigDecimal totalMonto = BigDecimal.ZERO;

            for (Asistencia asis : asisEmpleado) {
                // Horas trabajadas (del empleado o por defecto 8)
                BigDecimal horas = BigDecimal.valueOf(
                        empleado.getHorasPorDia() != null ? empleado.getHorasPorDia() : 8
                );
                horasPorDia.put(asis.getFecha(), horas);
                totalHoras = totalHoras.add(horas);

                // Monto (tarifa por hora * horas)
                BigDecimal monto = calcularMontoAsistencia(asis);
                montosPorDia.put(asis.getFecha(), monto);
                totalMonto = totalMonto.add(monto);
            }

            empleadosDTO.add(EmpleadoReporteHoraDTO.builder()
                    .empleadoId(empleado.getId())
                    .nombre(empleado.getNombreCompleto())
                    .cargo(empleado.getCargo() != null ? empleado.getCargo() : "Sin cargo")
                    .horasPorDia(horasPorDia)
                    .montosPorDia(montosPorDia)
                    .totalHoras(totalHoras)
                    .descuento(BigDecimal.ZERO)
                    .totalAPagar(totalMonto)
                    .build());
        }

        // Agrupar por cargo y ordenar
        Map<String, List<EmpleadoReporteHoraDTO>> empleadosPorCargo = empleadosDTO.stream()
                .sorted(Comparator.comparing(EmpleadoReporteHoraDTO::getCargo)
                        .thenComparing(EmpleadoReporteHoraDTO::getNombre))
                .collect(Collectors.groupingBy(
                        EmpleadoReporteHoraDTO::getCargo,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Calcular totales por día (horas y montos)
        Map<LocalDate, BigDecimal> totalHorasPorDia = new HashMap<>();
        Map<LocalDate, BigDecimal> totalMontosPorDia = new HashMap<>();
        for (LocalDate d : diasDelPeriodo) {
            BigDecimal horasDia = empleadosDTO.stream()
                    .map(e -> e.getHorasPorDia().getOrDefault(d, BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalHorasPorDia.put(d, horasDia);

            BigDecimal montoDia = empleadosDTO.stream()
                    .map(e -> e.getMontosPorDia().getOrDefault(d, BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalMontosPorDia.put(d, montoDia);
        }

        // Totales generales
        BigDecimal totalHoras = empleadosDTO.stream()
                .map(EmpleadoReporteHoraDTO::getTotalHoras)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGeneral = empleadosDTO.stream()
                .map(EmpleadoReporteHoraDTO::getTotalAPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Construir DTO del reporte
        ReporteHoraDTO reporteData = ReporteHoraDTO.builder()
                .fechaDesde(desde)
                .fechaHasta(hasta)
                .diasDelPeriodo(diasDelPeriodo)
                .empleadosPorCargo(empleadosPorCargo)
                .totalHorasPorDia(totalHorasPorDia)
                .totalMontosPorDia(totalMontosPorDia)
                .totalHoras(totalHoras)
                .totalGeneral(totalGeneral)
                .cantidadEmpleados(empleadosDTO.size())
                .build();

        // Generar PDF
        byte[] pdfContent = pdfGeneratorService.generarReporteHora(reporteData);

        // Guardar archivo
        String nombreArchivo = "reporte_hora_" + desde.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                + "_" + hasta.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        String archivoPath = pdfGeneratorService.guardarPdf(pdfContent, nombreArchivo);

        // Crear registro de reporte
        Reporte reporte = new Reporte(
                TipoReporte.CIERRE_HORA,
                hasta,
                "Reporte Por Hora - " + desde.format(DateTimeFormatter.ofPattern("dd/MM"))
                        + " al " + hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        reporte.setArchivoNombre(nombreArchivo);
        reporte.setArchivoPath(archivoPath);
        reporte.setTotalMonto(totalGeneral);
        reporte.setCantidadEmpleados(empleadosDTO.size());
        reporte.setDescripcion("Reporte de empleados por hora con " + empleadosDTO.size() + " empleados - " +
                totalHoras + " horas totales");

        reporte = reporteRepository.save(reporte);

        return toDTO(reporte);
    }

    public ReporteDTO regenerarReporte(Long id) throws DocumentException, IOException {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reporte no encontrado"));

        if (reporte.getTipo() != TipoReporte.CIERRE_NOCHE) {
            throw new IllegalArgumentException("Solo se pueden regenerar reportes de cierre de noche");
        }

        // Obtener asistencias actualizadas y convertir a TurnoNocheDTO
        List<Asistencia> asistencias = asistenciaRepository.findByFechaWithEmpleado(reporte.getFechaReporte());
        List<TurnoNocheDTO> turnos = asistencias.stream()
                .map(this::asistenciaToTurnoDTO)
                .collect(Collectors.toList());

        // Calcular total
        BigDecimal totalNoche = turnos.stream()
                .map(TurnoNocheDTO::getMontoCalculado)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Regenerar PDF
        byte[] pdfContent = pdfGeneratorService.generarReporteCierreNoche(
                reporte.getFechaReporte(), turnos, totalNoche);

        // Guardar archivo (sobrescribe el anterior)
        pdfGeneratorService.guardarPdf(pdfContent, reporte.getArchivoNombre());

        // Actualizar registro
        reporte.setTotalMonto(totalNoche);
        reporte.setCantidadEmpleados(turnos.size());
        reporte.setDescripcion("Reporte de cierre de noche con " + turnos.size() + " empleados (regenerado)");

        reporte = reporteRepository.save(reporte);

        return toDTO(reporte);
    }

    public ReporteDTO marcarComoEnviado(Long id, String emailDestino) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reporte no encontrado"));

        reporte.setEnviado(true);
        reporte.setEmailDestino(emailDestino);
        reporte.setFechaEnvio(LocalDateTime.now());

        reporte = reporteRepository.save(reporte);
        return toDTO(reporte);
    }

    public ReporteDTO enviarReportePorEmail(Long id, String emailOverride) throws IOException {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reporte no encontrado"));

        // Obtener contenido PDF
        byte[] pdfContent = getPdfContent(id);

        // Generar resumen HTML
        String resumenHtml = generarResumenHtml(reporte);

        // Fecha formateada
        String fechaFormateada = reporte.getFechaReporte()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        if (emailOverride != null && !emailOverride.isBlank()) {
            // Envío manual a un email específico
            emailService.sendCierreNocheReportWithPdf(
                    emailOverride, fechaFormateada, resumenHtml, pdfContent, reporte.getArchivoNombre());
            return marcarComoEnviado(id, emailOverride);
        }

        // Enviar a todos los destinatarios suscriptos a cierre de noche
        List<String> destinatarios = destinatarioService.getEmailsForCierreNoche();

        if (destinatarios.isEmpty()) {
            throw new IllegalStateException("No hay destinatarios configurados para reportes de cierre de noche");
        }

        emailService.sendCierreNocheReportWithPdfToMultiple(
                destinatarios, fechaFormateada, resumenHtml, pdfContent, reporte.getArchivoNombre());

        String emailsConcat = String.join(", ", destinatarios);
        return marcarComoEnviado(id, emailsConcat);
    }

    private String generarResumenHtml(Reporte reporte) {
        return String.format("""
            <div style="background: #f9fafb; padding: 15px; border-radius: 8px; margin: 10px 0;">
                <h3 style="margin: 0 0 10px; color: #374151;">Resumen</h3>
                <p><strong>Total de la noche:</strong> $%s</p>
                <p><strong>Empleados que trabajaron:</strong> %d</p>
            </div>
            """,
                reporte.getTotalMonto() != null ?
                        String.format("%,.0f", reporte.getTotalMonto()) : "0",
                reporte.getCantidadEmpleados() != null ?
                        reporte.getCantidadEmpleados() : 0
        );
    }

    @Transactional(readOnly = true)
    public List<String> getEmailsDestinatarios() {
        return destinatarioService.getEmailsForCierreNoche();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalGastosPeriodo(LocalDate desde, LocalDate hasta) {
        return reporteRepository.sumTotalMontoByTipoAndPeriodo(TipoReporte.CIERRE_NOCHE, desde, hasta);
    }

    private ReporteDTO toDTO(Reporte reporte) {
        return ReporteDTO.builder()
                .id(reporte.getId())
                .tipo(reporte.getTipo())
                .fechaReporte(reporte.getFechaReporte())
                .titulo(reporte.getTitulo())
                .descripcion(reporte.getDescripcion())
                .archivoNombre(reporte.getArchivoNombre())
                .totalMonto(reporte.getTotalMonto())
                .cantidadEmpleados(reporte.getCantidadEmpleados())
                .enviado(reporte.getEnviado())
                .emailDestino(reporte.getEmailDestino())
                .fechaEnvio(reporte.getFechaEnvio())
                .createdAt(reporte.getCreatedAt())
                .build();
    }

    private TurnoNocheDTO asistenciaToTurnoDTO(Asistencia asistencia) {
        Empleado empleado = asistencia.getEmpleado();

        // Calcular horas trabajadas (asumiendo jornada completa si no hay hora de salida)
        BigDecimal horasTrabajadas = BigDecimal.valueOf(
                empleado.getHorasPorDia() != null ? empleado.getHorasPorDia() : 8
        );

        // Calcular monto según tipo de pago
        BigDecimal montoBase;
        if (empleado.getTipoPago() == TipoPago.HORA) {
            BigDecimal tarifaHora = empleado.getCobraPorHora() != null
                    ? empleado.getCobraPorHora()
                    : BigDecimal.ZERO;
            montoBase = tarifaHora.multiply(horasTrabajadas);
        } else {
            montoBase = empleado.getCobraPorDia() != null
                    ? empleado.getCobraPorDia()
                    : BigDecimal.ZERO;
        }

        // Aplicar ajustes
        BigDecimal montoFinal = montoBase;
        if (empleado.getAjustePorcentaje() != null && empleado.getAjustePorcentaje().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal ajuste = montoBase.multiply(empleado.getAjustePorcentaje())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            montoFinal = montoFinal.add(ajuste);
        }
        if (empleado.getAjusteMonto() != null) {
            montoFinal = montoFinal.add(empleado.getAjusteMonto());
        }

        return TurnoNocheDTO.builder()
                .id(asistencia.getId())
                .empleadoId(empleado.getId())
                .empleadoNombre(empleado.getNombreCompleto())
                .empleadoCargo(empleado.getCargo())
                .fecha(asistencia.getFecha())
                .horaEntrada(asistencia.getHoraLlegada())
                .horaSalida(null) // No tenemos hora de salida en asistencia
                .horasTrabajadas(horasTrabajadas)
                .montoCalculado(montoFinal)
                .observaciones(asistencia.getObservaciones())
                .build();
    }
}

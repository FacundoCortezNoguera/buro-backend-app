package ar.buro.personal.pagos;

import ar.buro.personal.asistencias.Asistencia;
import ar.buro.personal.asistencias.AsistenciaRepository;
import ar.buro.personal.cambios.CambioEmpleado;
import ar.buro.personal.cambios.CambioEmpleadoRepository;
import ar.buro.personal.cambios.EstadoCambio;
import ar.buro.personal.empleados.Empleado;
import ar.buro.personal.empleados.EmpleadoRepository;
import ar.buro.personal.empleados.TipoPago;
import ar.buro.personal.pagos.dto.*;
import ar.buro.personal.reportes.ReporteRepository;
import ar.buro.personal.reportes.TipoReporte;
import ar.buro.personal.turnos.TurnoNoche;
import ar.buro.personal.turnos.TurnoNocheRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PagoDiarioService {

    private final PagoDiarioRepository pagoDiarioRepository;
    private final TurnoNocheRepository turnoNocheRepository;
    private final EmpleadoRepository empleadoRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final ReporteRepository reporteRepository;
    private final CambioEmpleadoRepository cambioEmpleadoRepository;

    public PagoDiarioService(
            PagoDiarioRepository pagoDiarioRepository,
            TurnoNocheRepository turnoNocheRepository,
            EmpleadoRepository empleadoRepository,
            AsistenciaRepository asistenciaRepository,
            ReporteRepository reporteRepository,
            CambioEmpleadoRepository cambioEmpleadoRepository) {
        this.pagoDiarioRepository = pagoDiarioRepository;
        this.turnoNocheRepository = turnoNocheRepository;
        this.empleadoRepository = empleadoRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.reporteRepository = reporteRepository;
        this.cambioEmpleadoRepository = cambioEmpleadoRepository;
    }

    @Transactional(readOnly = true)
    public List<PagoDiarioDTO> findByFechaRange(LocalDate desde, LocalDate hasta) {
        return pagoDiarioRepository.findByFechaBetweenWithEmpleado(desde, hasta).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PagoDiarioDTO> findPendientes() {
        return pagoDiarioRepository.findPendientesWithEmpleado().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PagoDiarioDTO marcarPagado(Long id) {
        PagoDiario pago = pagoDiarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));

        pago.setPagado(true);
        pago.setFechaPago(LocalDate.now());
        pago = pagoDiarioRepository.save(pago);
        return toDTO(pago);
    }

    public PagoDiarioDTO marcarNoPagado(Long id) {
        PagoDiario pago = pagoDiarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado: " + id));

        pago.setPagado(false);
        pago.setFechaPago(null);
        pago = pagoDiarioRepository.save(pago);
        return toDTO(pago);
    }

    public CalcularSalarioResponseDTO calcularSalarios(CalcularSalarioRequestDTO request) {
        List<TurnoNoche> turnos;

        if (request.getEmpleadoIds() != null && !request.getEmpleadoIds().isEmpty()) {
            // Filter by specific employees
            turnos = new ArrayList<>();
            for (Long empleadoId : request.getEmpleadoIds()) {
                turnos.addAll(turnoNocheRepository.findByEmpleadoAndFechaBetween(
                        empleadoId, request.getDesde(), request.getHasta()));
            }
        } else {
            turnos = turnoNocheRepository.findByFechaBetweenWithEmpleado(request.getDesde(), request.getHasta());
        }

        int created = 0;
        int updated = 0;
        BigDecimal total = BigDecimal.ZERO;
        List<PagoDiario> pagosGuardados = new ArrayList<>();

        for (TurnoNoche turno : turnos) {
            if (turno.getMontoCalculado() == null || turno.getMontoCalculado().compareTo(BigDecimal.ZERO) == 0) {
                continue; // Skip turnos without calculated amount
            }

            // Check if a pago already exists for this turno
            List<PagoDiario> existingPagos = pagoDiarioRepository.findByTurnoNocheId(turno.getId());
            PagoDiario pago;

            if (existingPagos.isEmpty()) {
                // Create new pago
                pago = new PagoDiario();
                pago.setEmpleado(turno.getEmpleado());
                pago.setFecha(turno.getFecha());
                pago.setTurnoNoche(turno);
                pago.setPagado(false);
                created++;
            } else {
                // Update existing
                pago = existingPagos.get(0);
                if (!pago.getPagado()) {
                    updated++;
                }
            }

            pago.setConcepto("Turno noche " + turno.getFecha());
            pago.setMonto(turno.getMontoCalculado());

            pagosGuardados.add(pagoDiarioRepository.save(pago));
            total = total.add(turno.getMontoCalculado());
        }

        return CalcularSalarioResponseDTO.builder()
                .pagosCreados(created)
                .pagosActualizados(updated)
                .totalMonto(total)
                .pagos(pagosGuardados.stream().map(this::toDTO).collect(Collectors.toList()))
                .build();
    }

    public String enviarEmailCierreNoche(LocalDate fecha, String destinatario, String asunto) {
        // Stub implementation - in a real app, this would send an email
        List<TurnoNoche> turnos = turnoNocheRepository.findByFechaWithEmpleado(fecha);

        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de cierre de noche - ").append(fecha).append("\n\n");

        BigDecimal totalNoche = BigDecimal.ZERO;
        for (TurnoNoche turno : turnos) {
            sb.append("- ").append(turno.getEmpleado().getNombreCompleto());
            if (turno.getHorasTrabajadas() != null) {
                sb.append(": ").append(turno.getHorasTrabajadas()).append(" horas");
            }
            if (turno.getMontoCalculado() != null) {
                sb.append(" - $").append(turno.getMontoCalculado());
                totalNoche = totalNoche.add(turno.getMontoCalculado());
            }
            sb.append("\n");
        }

        sb.append("\nTotal: $").append(totalNoche);

        // Log the email that would be sent
        System.out.println("=== EMAIL STUB ===");
        System.out.println("To: " + (destinatario != null ? destinatario : "admin@buro.ar"));
        System.out.println("Subject: " + (asunto != null ? asunto : "Cierre de noche " + fecha));
        System.out.println(sb);
        System.out.println("=================");

        return "Email preparado para enviar (stub). Destinatario: " +
               (destinatario != null ? destinatario : "admin@buro.ar") +
               ". Total turnos: " + turnos.size() + ". Total monto: $" + totalNoche;
    }

    private PagoDiarioDTO toDTO(PagoDiario pago) {
        return PagoDiarioDTO.builder()
                .id(pago.getId())
                .empleadoId(pago.getEmpleado().getId())
                .empleadoNombre(pago.getEmpleado().getNombreCompleto())
                .empleadoCargo(pago.getEmpleado().getCargo())
                .fecha(pago.getFecha())
                .concepto(pago.getConcepto())
                .monto(pago.getMonto())
                .pagado(pago.getPagado())
                .fechaPago(pago.getFechaPago())
                .turnoNocheId(pago.getTurnoNoche() != null ? pago.getTurnoNoche().getId() : null)
                .observaciones(pago.getObservaciones())
                .build();
    }

    /**
     * Obtiene el resumen de pagos para empleados por DIA.
     * Calcula desde el último reporte CIERRE_NOCHE hasta hoy.
     */
    @Transactional(readOnly = true)
    public ResumenPagosDTO getResumenPagosDia() {
        return getResumenPagosPorTipo(TipoPago.DIA, TipoReporte.CIERRE_NOCHE, 7);
    }

    /**
     * Obtiene el resumen de pagos para empleados por HORA.
     * Calcula desde el último reporte CIERRE_HORA hasta hoy.
     */
    @Transactional(readOnly = true)
    public ResumenPagosDTO getResumenPagosHora() {
        return getResumenPagosPorTipo(TipoPago.HORA, TipoReporte.CIERRE_HORA, 14);
    }

    /**
     * Método genérico para obtener resumen de pagos filtrado por tipo.
     */
    private ResumenPagosDTO getResumenPagosPorTipo(TipoPago tipoPago, TipoReporte tipoReporte, int diasDefault) {
        LocalDate hoy = LocalDate.now();

        // Obtener fecha del último reporte del tipo correspondiente
        LocalDate fechaDesde = reporteRepository.findByTipoOrderByFechaReporteDesc(tipoReporte)
                .stream()
                .findFirst()
                .map(r -> r.getFechaReporte().plusDays(1))
                .orElse(hoy.minusDays(diasDefault));

        // Obtener asistencias del período - FILTRAR POR TIPO DE PAGO
        List<Asistencia> asistencias = asistenciaRepository.findByFechaBetweenWithEmpleado(fechaDesde, hoy)
                .stream()
                .filter(a -> a.getEmpleado().getTipoPago() == tipoPago)
                .toList();

        // Obtener pagos pendientes de períodos anteriores - FILTRAR POR TIPO DE PAGO
        List<PagoDiario> pagosPendientesAnteriores = pagoDiarioRepository.findPendientesWithEmpleado()
                .stream()
                .filter(p -> p.getFecha().isBefore(fechaDesde))
                .filter(p -> p.getEmpleado().getTipoPago() == tipoPago)
                .toList();

        // Obtener cambios de cargo del período - FILTRAR POR TIPO DE PAGO
        List<CambioEmpleado> cambiosCargo = cambioEmpleadoRepository
                .findByEstadoOrderByCreatedAtDesc(EstadoCambio.ACEPTADO)
                .stream()
                .filter(c -> c.getEmpleado().getTipoPago() == tipoPago)
                .filter(c -> c.getCreatedAt().toLocalDate().isAfter(fechaDesde.minusDays(1))
                        && c.getCreatedAt().toLocalDate().isBefore(hoy.plusDays(1)))
                .toList();

        // Agrupar asistencias por empleado
        Map<Long, List<Asistencia>> asistenciasPorEmpleado = new HashMap<>();
        for (Asistencia a : asistencias) {
            asistenciasPorEmpleado.computeIfAbsent(a.getEmpleado().getId(), k -> new ArrayList<>()).add(a);
        }

        // Agrupar pendientes anteriores por empleado
        Map<Long, List<PagoDiario>> pendientesPorEmpleado = new HashMap<>();
        for (PagoDiario p : pagosPendientesAnteriores) {
            pendientesPorEmpleado.computeIfAbsent(p.getEmpleado().getId(), k -> new ArrayList<>()).add(p);
        }

        // Agrupar cambios por empleado
        Map<Long, List<CambioEmpleado>> cambiosPorEmpleado = new HashMap<>();
        for (CambioEmpleado c : cambiosCargo) {
            cambiosPorEmpleado.computeIfAbsent(c.getEmpleado().getId(), k -> new ArrayList<>()).add(c);
        }

        // Obtener todos los empleados involucrados
        Set<Long> empleadoIds = new HashSet<>();
        empleadoIds.addAll(asistenciasPorEmpleado.keySet());
        empleadoIds.addAll(pendientesPorEmpleado.keySet());

        // Construir resumen por empleado
        List<EmpleadoPagoResumenDTO> empleadosResumen = new ArrayList<>();
        BigDecimal totalPeriodo = BigDecimal.ZERO;
        BigDecimal totalPendienteAnterior = BigDecimal.ZERO;

        for (Long empleadoId : empleadoIds) {
            List<Asistencia> asistenciasEmp = asistenciasPorEmpleado.getOrDefault(empleadoId, Collections.emptyList());
            List<PagoDiario> pendientesEmp = pendientesPorEmpleado.getOrDefault(empleadoId, Collections.emptyList());
            List<CambioEmpleado> cambiosEmp = cambiosPorEmpleado.getOrDefault(empleadoId, Collections.emptyList());

            if (asistenciasEmp.isEmpty() && pendientesEmp.isEmpty()) continue;

            Empleado empleado = asistenciasEmp.isEmpty()
                    ? pendientesEmp.get(0).getEmpleado()
                    : asistenciasEmp.get(0).getEmpleado();

            EmpleadoPagoResumenDTO resumen = buildEmpleadoResumen(
                    empleado, asistenciasEmp, pendientesEmp, cambiosEmp, fechaDesde, hoy);

            empleadosResumen.add(resumen);
            totalPeriodo = totalPeriodo.add(resumen.getTotalPeriodo());
            totalPendienteAnterior = totalPendienteAnterior.add(resumen.getTotalPendienteAnterior());
        }

        // Ordenar por nombre
        empleadosResumen.sort(Comparator.comparing(EmpleadoPagoResumenDTO::getEmpleadoNombre));

        return ResumenPagosDTO.builder()
                .fechaDesde(fechaDesde)
                .fechaHasta(hoy)
                .totalPeriodo(totalPeriodo)
                .totalPendienteAnterior(totalPendienteAnterior)
                .totalGeneral(totalPeriodo.add(totalPendienteAnterior))
                .empleados(empleadosResumen)
                .build();
    }

    private EmpleadoPagoResumenDTO buildEmpleadoResumen(
            Empleado empleado,
            List<Asistencia> asistencias,
            List<PagoDiario> pendientesAnteriores,
            List<CambioEmpleado> cambios,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {

        // Calcular monto base por día
        BigDecimal montoBaseDia = calcularMontoBaseDia(empleado);

        // Construir desglose por día
        List<PagoDiaDetalleDTO> diasPeriodo = new ArrayList<>();
        BigDecimal totalPeriodo = BigDecimal.ZERO;

        for (Asistencia asistencia : asistencias) {
            BigDecimal montoExtras = BigDecimal.ZERO;

            // Buscar si hay pago existente para esta fecha
            List<PagoDiario> pagosExistentes = pagoDiarioRepository
                    .findByEmpleadoIdAndFecha(empleado.getId(), asistencia.getFecha());
            PagoDiario pagoExistente = pagosExistentes.isEmpty() ? null : pagosExistentes.get(0);

            BigDecimal montoTotal = montoBaseDia.add(montoExtras);
            totalPeriodo = totalPeriodo.add(montoTotal);

            diasPeriodo.add(PagoDiaDetalleDTO.builder()
                    .pagoId(pagoExistente != null ? pagoExistente.getId() : null)
                    .fecha(asistencia.getFecha())
                    .montoBase(montoBaseDia)
                    .montoExtras(montoExtras)
                    .montoTotal(montoTotal)
                    .pagado(pagoExistente != null && pagoExistente.getPagado())
                    .horaLlegada(asistencia.getHoraLlegada().toString())
                    .estado(asistencia.getEstado().name())
                    .build());
        }

        // Calcular total pendiente anterior
        BigDecimal totalPendienteAnterior = pendientesAnteriores.stream()
                .map(PagoDiario::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Construir extras
        List<ExtraPagoDTO> extras = new ArrayList<>();

        // Agregar cambios de cargo como extras
        for (CambioEmpleado cambio : cambios) {
            if ("cargo".equals(cambio.getCampoModificado())) {
                extras.add(ExtraPagoDTO.builder()
                        .tipo("CAMBIO_CARGO")
                        .descripcion("Cambio de " + cambio.getValorAnterior() + " a " + cambio.getValorNuevo())
                        .monto(BigDecimal.ZERO) // Los cambios de cargo no tienen monto extra por defecto
                        .fecha(cambio.getCreatedAt().toLocalDate())
                        .build());
            }
        }

        return EmpleadoPagoResumenDTO.builder()
                .empleadoId(empleado.getId())
                .empleadoNombre(empleado.getNombreCompleto())
                .cargo(empleado.getCargo())
                .tipoPago(empleado.getTipoPago().name())
                .totalPeriodo(totalPeriodo)
                .totalPendienteAnterior(totalPendienteAnterior)
                .totalACobrar(totalPeriodo.add(totalPendienteAnterior))
                .diasTrabajados(asistencias.size())
                .diasPendientesAnteriores(pendientesAnteriores.size())
                .diasPeriodo(diasPeriodo)
                .extras(extras)
                .build();
    }

    private BigDecimal calcularMontoBaseDia(Empleado empleado) {
        BigDecimal montoBase;

        if (empleado.getTipoPago() == TipoPago.DIA) {
            montoBase = empleado.getCobraPorDia() != null ? empleado.getCobraPorDia() : BigDecimal.ZERO;
        } else {
            // Por hora: horasPorDia * cobraPorHora
            BigDecimal porHora = empleado.getCobraPorHora() != null ? empleado.getCobraPorHora() : BigDecimal.ZERO;
            int horas = empleado.getHorasPorDia() != null ? empleado.getHorasPorDia() : 8;
            montoBase = porHora.multiply(BigDecimal.valueOf(horas));
        }

        // Aplicar ajustes
        if (empleado.getAjustePorcentaje() != null && empleado.getAjustePorcentaje().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal ajuste = montoBase.multiply(empleado.getAjustePorcentaje()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            montoBase = montoBase.add(ajuste);
        }

        if (empleado.getAjusteMonto() != null && empleado.getAjusteMonto().compareTo(BigDecimal.ZERO) != 0) {
            montoBase = montoBase.add(empleado.getAjusteMonto());
        }

        return montoBase;
    }

    /**
     * Marca todos los pagos de un empleado en el período como pagados.
     */
    public void marcarEmpleadoPagado(Long empleadoId, LocalDate desde, LocalDate hasta) {
        // Crear pagos para asistencias que no tienen pago
        List<Asistencia> asistencias = asistenciaRepository.findByFechaBetweenWithEmpleado(desde, hasta)
                .stream()
                .filter(a -> a.getEmpleado().getId().equals(empleadoId))
                .toList();

        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

        BigDecimal montoBaseDia = calcularMontoBaseDia(empleado);

        for (Asistencia asistencia : asistencias) {
            List<PagoDiario> existentes = pagoDiarioRepository.findByEmpleadoIdAndFecha(empleadoId, asistencia.getFecha());

            PagoDiario pago;
            if (existentes.isEmpty()) {
                pago = new PagoDiario();
                pago.setEmpleado(empleado);
                pago.setFecha(asistencia.getFecha());
                pago.setConcepto("Turno " + asistencia.getFecha());
                pago.setMonto(montoBaseDia);
            } else {
                pago = existentes.get(0);
            }

            pago.setPagado(true);
            pago.setFechaPago(LocalDate.now());
            pagoDiarioRepository.save(pago);
        }

        // También marcar pendientes anteriores como pagados
        pagoDiarioRepository.findPendientesWithEmpleado()
                .stream()
                .filter(p -> p.getEmpleado().getId().equals(empleadoId))
                .forEach(p -> {
                    p.setPagado(true);
                    p.setFechaPago(LocalDate.now());
                    pagoDiarioRepository.save(p);
                });
    }
}

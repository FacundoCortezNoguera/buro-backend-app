package ar.buro.personal.cambios;

import ar.buro.personal.cambios.dto.CambioEmpleadoCreateDTO;
import ar.buro.personal.cambios.dto.CambioEmpleadoDTO;
import ar.buro.personal.email.EmailService;
import ar.buro.personal.empleados.Empleado;
import ar.buro.personal.empleados.EmpleadoRepository;
import ar.buro.personal.notificaciones.DestinatarioService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CambioEmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(CambioEmpleadoService.class);

    private final CambioEmpleadoRepository cambioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final DestinatarioService destinatarioService;
    private final EmailService emailService;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public CambioEmpleadoService(
            CambioEmpleadoRepository cambioRepository,
            EmpleadoRepository empleadoRepository,
            DestinatarioService destinatarioService,
            EmailService emailService
    ) {
        this.cambioRepository = cambioRepository;
        this.empleadoRepository = empleadoRepository;
        this.destinatarioService = destinatarioService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<CambioEmpleadoDTO> findAll() {
        return cambioRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CambioEmpleadoDTO> findPendientes() {
        return cambioRepository.findByEstadoOrderByCreatedAtDesc(EstadoCambio.PENDIENTE).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CambioEmpleadoDTO findById(Long id) {
        CambioEmpleado cambio = cambioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cambio no encontrado"));
        return toDTO(cambio);
    }

    @Transactional(readOnly = true)
    public CambioEmpleadoDTO findByToken(String token) {
        CambioEmpleado cambio = cambioRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Cambio no encontrado o token inválido"));
        return toDTO(cambio);
    }

    public CambioEmpleadoDTO crearCambio(CambioEmpleadoCreateDTO dto, String username) {
        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

        CambioEmpleado cambio = new CambioEmpleado();
        cambio.setEmpleado(empleado);
        cambio.setTipoCambio(dto.getTipoCambio());
        cambio.setCampoModificado(dto.getCampoModificado());
        cambio.setValorAnterior(dto.getValorAnterior());
        cambio.setValorNuevo(dto.getValorNuevo());
        cambio.setDescripcion(dto.getDescripcion());
        cambio.setEstado(EstadoCambio.PENDIENTE);
        cambio.setSolicitadoPor(username);

        // Generar token con expiración de 48 horas
        cambio.setToken(UUID.randomUUID().toString());
        cambio.setTokenExpiracion(LocalDateTime.now().plusHours(48));

        cambio = cambioRepository.save(cambio);

        // Enviar notificación por email
        enviarNotificacion(cambio, empleado);

        return toDTO(cambio);
    }

    public CambioEmpleadoDTO aprobarPorToken(String token) {
        CambioEmpleado cambio = cambioRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token inválido"));

        if (cambio.getEstado() != EstadoCambio.PENDIENTE) {
            throw new IllegalStateException("Este cambio ya fue " + cambio.getEstado().name().toLowerCase());
        }

        if (cambio.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            cambio.setEstado(EstadoCambio.EXPIRADO);
            cambioRepository.save(cambio);
            throw new IllegalStateException("El token ha expirado");
        }

        // Aplicar el cambio al empleado
        aplicarCambio(cambio);

        cambio.setEstado(EstadoCambio.ACEPTADO);
        cambio.setAprobadoPor("Aprobado via email");
        cambio.setFechaAprobacion(LocalDateTime.now());
        cambio = cambioRepository.save(cambio);

        return toDTO(cambio);
    }

    public CambioEmpleadoDTO rechazarPorToken(String token) {
        CambioEmpleado cambio = cambioRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token inválido"));

        if (cambio.getEstado() != EstadoCambio.PENDIENTE) {
            throw new IllegalStateException("Este cambio ya fue " + cambio.getEstado().name().toLowerCase());
        }

        cambio.setEstado(EstadoCambio.RECHAZADO);
        cambio.setAprobadoPor("Rechazado via email");
        cambio.setFechaAprobacion(LocalDateTime.now());
        cambio = cambioRepository.save(cambio);

        return toDTO(cambio);
    }

    private void aplicarCambio(CambioEmpleado cambio) {
        Empleado empleado = cambio.getEmpleado();
        String campo = cambio.getCampoModificado();
        String nuevoValor = cambio.getValorNuevo();

        switch (campo) {
            case "cargo" -> empleado.setCargo(nuevoValor);
            case "cobraPorHora" -> empleado.setCobraPorHora(new BigDecimal(nuevoValor));
            case "cobraPorDia" -> empleado.setCobraPorDia(new BigDecimal(nuevoValor));
            case "ajustePorcentaje" -> empleado.setAjustePorcentaje(new BigDecimal(nuevoValor));
            case "ajusteMonto" -> empleado.setAjusteMonto(new BigDecimal(nuevoValor));
            default -> throw new IllegalArgumentException("Campo no soportado para cambio: " + campo);
        }

        empleadoRepository.save(empleado);
        logger.info("Cambio aplicado al empleado {}: {} = {}", empleado.getId(), campo, nuevoValor);
    }

    private void enviarNotificacion(CambioEmpleado cambio, Empleado empleado) {
        List<String> destinatarios = destinatarioService.getEmailsForCambiosEmpleados();

        if (destinatarios.isEmpty()) {
            logger.warn("No hay destinatarios configurados para cambios de empleados");
            return;
        }

        String approveUrl = frontendUrl + "/cambios/aprobar/" + cambio.getToken();
        String nombreEmpleado = empleado.getNombre() + " " + empleado.getApellido();
        String tipoLabel = getTipoCambioLabel(cambio.getTipoCambio());

        for (String email : destinatarios) {
            emailService.sendCambioEmpleadoNotification(
                    email, nombreEmpleado, tipoLabel,
                    cambio.getCampoModificado(),
                    cambio.getValorAnterior(),
                    cambio.getValorNuevo(),
                    cambio.getDescripcion(),
                    approveUrl,
                    cambio.getToken()
            );
        }
    }

    private String getTipoCambioLabel(TipoCambio tipo) {
        return switch (tipo) {
            case CAMBIO_CARGO -> "Cambio de Cargo";
            case AUMENTO_SUELDO -> "Aumento de Sueldo";
            case CAMBIO_TARIFA -> "Cambio de Tarifa";
        };
    }

    private CambioEmpleadoDTO toDTO(CambioEmpleado cambio) {
        Empleado emp = cambio.getEmpleado();
        return CambioEmpleadoDTO.builder()
                .id(cambio.getId())
                .empleadoId(emp.getId())
                .empleadoNombre(emp.getNombre() + " " + emp.getApellido())
                .tipoCambio(cambio.getTipoCambio())
                .campoModificado(cambio.getCampoModificado())
                .valorAnterior(cambio.getValorAnterior())
                .valorNuevo(cambio.getValorNuevo())
                .descripcion(cambio.getDescripcion())
                .estado(cambio.getEstado())
                .solicitadoPor(cambio.getSolicitadoPor())
                .aprobadoPor(cambio.getAprobadoPor())
                .fechaAprobacion(cambio.getFechaAprobacion())
                .createdAt(cambio.getCreatedAt())
                .build();
    }
}

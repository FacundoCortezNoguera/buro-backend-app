package ar.buro.personal.turnos;

import ar.buro.personal.empleados.Empleado;
import ar.buro.personal.empleados.EmpleadoRepository;
import ar.buro.personal.empleados.TipoPago;
import ar.buro.personal.turnos.dto.TurnoNocheBulkDTO;
import ar.buro.personal.turnos.dto.TurnoNocheCreateDTO;
import ar.buro.personal.turnos.dto.TurnoNocheDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TurnoNocheService {

    private final TurnoNocheRepository turnoNocheRepository;
    private final EmpleadoRepository empleadoRepository;

    public TurnoNocheService(TurnoNocheRepository turnoNocheRepository, EmpleadoRepository empleadoRepository) {
        this.turnoNocheRepository = turnoNocheRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(readOnly = true)
    public List<TurnoNocheDTO> findByFecha(LocalDate fecha) {
        return turnoNocheRepository.findByFechaWithEmpleado(fecha).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TurnoNocheDTO> findByFechaRange(LocalDate desde, LocalDate hasta) {
        return turnoNocheRepository.findByFechaBetweenWithEmpleado(desde, hasta).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TurnoNocheDTO> saveTurnosForFecha(LocalDate fecha, List<TurnoNocheCreateDTO> turnosDto) {
        List<TurnoNoche> turnosGuardados = new ArrayList<>();

        for (TurnoNocheCreateDTO dto : turnosDto) {
            Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                    .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado: " + dto.getEmpleadoId()));

            // Find existing or create new
            TurnoNoche turno = turnoNocheRepository.findByEmpleadoIdAndFecha(dto.getEmpleadoId(), fecha)
                    .orElse(new TurnoNoche());

            turno.setEmpleado(empleado);
            turno.setFecha(fecha);
            turno.setHoraEntrada(dto.getHoraEntrada());
            turno.setHoraSalida(dto.getHoraSalida());
            turno.setHorasTrabajadas(dto.getHorasTrabajadas());
            turno.setObservaciones(dto.getObservaciones());

            // Calculate amount if not provided
            BigDecimal monto = dto.getMontoCalculado();
            if (monto == null && dto.getHorasTrabajadas() != null) {
                monto = calcularMonto(empleado, dto.getHorasTrabajadas());
            }
            turno.setMontoCalculado(monto);

            turnosGuardados.add(turnoNocheRepository.save(turno));
        }

        return turnosGuardados.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TurnoNocheDTO> saveTurnosBulk(TurnoNocheBulkDTO bulkDto) {
        return saveTurnosForFecha(bulkDto.getFecha(), bulkDto.getTurnos());
    }

    private BigDecimal calcularMonto(Empleado empleado, BigDecimal horas) {
        if (empleado.getTipoPago() == TipoPago.HORA && empleado.getCobraPorHora() != null) {
            return horas.multiply(empleado.getCobraPorHora());
        } else if (empleado.getTipoPago() == TipoPago.DIA && empleado.getCobraPorDia() != null) {
            // If worked full day (or more), pay full day rate
            int horasPorDia = empleado.getHorasPorDia() != null ? empleado.getHorasPorDia() : 8;
            if (horas.compareTo(BigDecimal.valueOf(horasPorDia)) >= 0) {
                return empleado.getCobraPorDia();
            }
            // Proportional for partial day
            return empleado.getCobraPorDia()
                    .multiply(horas)
                    .divide(BigDecimal.valueOf(horasPorDia), 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private TurnoNocheDTO toDTO(TurnoNoche turno) {
        return TurnoNocheDTO.builder()
                .id(turno.getId())
                .empleadoId(turno.getEmpleado().getId())
                .empleadoNombre(turno.getEmpleado().getNombreCompleto())
                .empleadoCargo(turno.getEmpleado().getCargo())
                .fecha(turno.getFecha())
                .horaEntrada(turno.getHoraEntrada())
                .horaSalida(turno.getHoraSalida())
                .horasTrabajadas(turno.getHorasTrabajadas())
                .montoCalculado(turno.getMontoCalculado())
                .observaciones(turno.getObservaciones())
                .build();
    }
}

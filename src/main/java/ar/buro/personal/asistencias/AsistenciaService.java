package ar.buro.personal.asistencias;

import ar.buro.personal.asistencias.dto.AsistenciaDTO;
import ar.buro.personal.asistencias.dto.MarcarPresenteRequest;
import ar.buro.personal.asistencias.dto.RegistroCamaraRequest;
import ar.buro.personal.empleados.Empleado;
import ar.buro.personal.empleados.EmpleadoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final EmpleadoRepository empleadoRepository;

    @Value("${app.camara.hora-esperada:22:00}")
    private String horaEsperadaConfig;

    public AsistenciaService(AsistenciaRepository asistenciaRepository, EmpleadoRepository empleadoRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    public AsistenciaDTO registrarDesdeCamara(RegistroCamaraRequest request) {
        LocalDate hoy = LocalDate.now();

        // Verificar si ya existe asistencia para hoy
        if (asistenciaRepository.existsByEmpleadoIdAndFecha(request.getEmpleadoId(), hoy)) {
            throw new IllegalStateException("El empleado ya tiene registrada su asistencia para hoy");
        }

        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + request.getEmpleadoId()));

        Asistencia asistencia = new Asistencia(empleado, hoy, request.getHora());

        LocalTime horaEsperada = LocalTime.parse(horaEsperadaConfig);
        asistencia.setHoraEsperada(horaEsperada);
        asistencia.calcularEstado();

        asistencia.setRegistradoPor("CAMARA");
        asistencia.setObservaciones("Registro automático desde cámara");

        asistencia = asistenciaRepository.save(asistencia);

        return toDTO(asistencia);
    }

    public AsistenciaDTO marcarPresente(MarcarPresenteRequest request, String registradoPor) {
        LocalDate hoy = LocalDate.now();

        // Verificar si ya existe asistencia para hoy
        if (asistenciaRepository.existsByEmpleadoIdAndFecha(request.getEmpleadoId(), hoy)) {
            throw new IllegalStateException("El empleado ya tiene registrada su asistencia para hoy");
        }

        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

        Asistencia asistencia = new Asistencia(empleado, hoy, request.getHoraLlegada());

        if (request.getHoraEsperada() != null) {
            asistencia.setHoraEsperada(request.getHoraEsperada());
            asistencia.calcularEstado();
        }

        asistencia.setRegistradoPor(registradoPor);
        asistencia.setObservaciones(request.getObservaciones());

        asistencia = asistenciaRepository.save(asistencia);

        return toDTO(asistencia);
    }

    public AsistenciaDTO actualizarAsistencia(Long id, LocalTime horaLlegada, String observaciones) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asistencia no encontrada"));

        asistencia.setHoraLlegada(horaLlegada);
        asistencia.setObservaciones(observaciones);
        asistencia.calcularEstado();

        asistencia = asistenciaRepository.save(asistencia);

        return toDTO(asistencia);
    }

    @Transactional(readOnly = true)
    public List<AsistenciaDTO> getAsistenciasHoy() {
        return getAsistenciasByFecha(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<AsistenciaDTO> getAsistenciasByFecha(LocalDate fecha) {
        return asistenciaRepository.findByFechaWithEmpleado(fecha).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AsistenciaDTO getAsistenciaEmpleadoHoy(Long empleadoId) {
        return asistenciaRepository.findByEmpleadoIdAndFecha(empleadoId, LocalDate.now())
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<AsistenciaDTO> getHistorialEmpleado(Long empleadoId) {
        return asistenciaRepository.findByEmpleadoIdOrderByFechaDesc(empleadoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void eliminarAsistencia(Long id) {
        if (!asistenciaRepository.existsById(id)) {
            throw new EntityNotFoundException("Asistencia no encontrada");
        }
        asistenciaRepository.deleteById(id);
    }

    private AsistenciaDTO toDTO(Asistencia asistencia) {
        Empleado emp = asistencia.getEmpleado();

        return AsistenciaDTO.builder()
                .id(asistencia.getId())
                .empleadoId(emp.getId())
                .empleadoNombre(emp.getNombreCompleto())
                .empleadoCargo(emp.getCargo())
                .fecha(asistencia.getFecha())
                .horaLlegada(asistencia.getHoraLlegada())
                .horaEsperada(asistencia.getHoraEsperada())
                .minutosDiferencia(asistencia.getMinutosDiferencia())
                .estado(asistencia.getEstado())
                .registradoPor(asistencia.getRegistradoPor())
                .observaciones(asistencia.getObservaciones())
                .build();
    }
}

package ar.buro.personal.notificaciones;

import ar.buro.personal.notificaciones.dto.DestinatarioCreateDTO;
import ar.buro.personal.notificaciones.dto.DestinatarioDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DestinatarioService {

    private final DestinatarioRepository destinatarioRepository;

    public DestinatarioService(DestinatarioRepository destinatarioRepository) {
        this.destinatarioRepository = destinatarioRepository;
    }

    @Transactional(readOnly = true)
    public List<DestinatarioDTO> findAll() {
        return destinatarioRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DestinatarioDTO findById(Long id) {
        DestinatarioNotificacion d = destinatarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destinatario no encontrado con id: " + id));
        return toDTO(d);
    }

    public DestinatarioDTO create(DestinatarioCreateDTO dto) {
        if (destinatarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un destinatario con ese email");
        }

        DestinatarioNotificacion d = new DestinatarioNotificacion();
        d.setNombre(dto.getNombre());
        d.setEmail(dto.getEmail());
        d.setActivo(true);
        d.setRecibeCierreNoche(dto.getRecibeCierreNoche() != null ? dto.getRecibeCierreNoche() : false);
        d.setRecibeReportesMensuales(dto.getRecibeReportesMensuales() != null ? dto.getRecibeReportesMensuales() : false);
        d.setRecibeCambiosEmpleados(dto.getRecibeCambiosEmpleados() != null ? dto.getRecibeCambiosEmpleados() : false);

        d = destinatarioRepository.save(d);
        return toDTO(d);
    }

    public DestinatarioDTO update(Long id, DestinatarioCreateDTO dto) {
        DestinatarioNotificacion d = destinatarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destinatario no encontrado con id: " + id));

        if (!d.getEmail().equals(dto.getEmail()) && destinatarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un destinatario con ese email");
        }

        d.setNombre(dto.getNombre());
        d.setEmail(dto.getEmail());
        d.setRecibeCierreNoche(dto.getRecibeCierreNoche() != null ? dto.getRecibeCierreNoche() : false);
        d.setRecibeReportesMensuales(dto.getRecibeReportesMensuales() != null ? dto.getRecibeReportesMensuales() : false);
        d.setRecibeCambiosEmpleados(dto.getRecibeCambiosEmpleados() != null ? dto.getRecibeCambiosEmpleados() : false);

        d = destinatarioRepository.save(d);
        return toDTO(d);
    }

    public DestinatarioDTO toggleActivo(Long id) {
        DestinatarioNotificacion d = destinatarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destinatario no encontrado con id: " + id));
        d.setActivo(!d.getActivo());
        d = destinatarioRepository.save(d);
        return toDTO(d);
    }

    public void delete(Long id) {
        if (!destinatarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Destinatario no encontrado con id: " + id);
        }
        destinatarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> getEmailsForCierreNoche() {
        return destinatarioRepository.findByActivoTrueAndRecibeCierreNocheTrue()
                .stream().map(DestinatarioNotificacion::getEmail).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getEmailsForReportesMensuales() {
        return destinatarioRepository.findByActivoTrueAndRecibeReportesMensualesTrue()
                .stream().map(DestinatarioNotificacion::getEmail).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getEmailsForCambiosEmpleados() {
        return destinatarioRepository.findByActivoTrueAndRecibeCambiosEmpleadosTrue()
                .stream().map(DestinatarioNotificacion::getEmail).collect(Collectors.toList());
    }

    private DestinatarioDTO toDTO(DestinatarioNotificacion d) {
        return DestinatarioDTO.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .email(d.getEmail())
                .activo(d.getActivo())
                .recibeCierreNoche(d.getRecibeCierreNoche())
                .recibeReportesMensuales(d.getRecibeReportesMensuales())
                .recibeCambiosEmpleados(d.getRecibeCambiosEmpleados())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}

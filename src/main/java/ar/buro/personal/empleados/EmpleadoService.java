package ar.buro.personal.empleados;

import ar.buro.personal.empleados.dto.EmpleadoCreateDTO;
import ar.buro.personal.empleados.dto.EmpleadoDTO;
import ar.buro.personal.users.Role;
import ar.buro.personal.users.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final RoleRepository roleRepository;
    private final EntityManager entityManager;

    public EmpleadoService(EmpleadoRepository empleadoRepository, RoleRepository roleRepository, EntityManager entityManager) {
        this.empleadoRepository = empleadoRepository;
        this.roleRepository = roleRepository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<EmpleadoDTO> findAll() {
        return empleadoRepository.findAllWithDiasTrabajo().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmpleadoDTO> findAllActivos() {
        return empleadoRepository.findAllActivosWithDiasTrabajo().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpleadoDTO findById(Long id) {
        Empleado empleado = empleadoRepository.findByIdWithDiasTrabajo(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con id: " + id));
        return toDTO(empleado);
    }

    public EmpleadoDTO create(EmpleadoCreateDTO dto) {
        if (empleadoRepository.existsByDocumentoNumero(dto.getDocumentoNumero())) {
            throw new IllegalArgumentException("Ya existe un empleado con ese número de documento");
        }

        Empleado empleado = new Empleado();
        updateEmpleadoFromDTO(empleado, dto, true);
        empleado.setActivo(true);

        empleado = empleadoRepository.save(empleado);
        return toDTO(empleado);
    }

    public EmpleadoDTO update(Long id, EmpleadoCreateDTO dto) {
        Empleado empleado = empleadoRepository.findByIdWithDiasTrabajo(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con id: " + id));

        // Check if documento changed and if new documento already exists
        if (!empleado.getDocumentoNumero().equals(dto.getDocumentoNumero())
            && empleadoRepository.existsByDocumentoNumero(dto.getDocumentoNumero())) {
            throw new IllegalArgumentException("Ya existe un empleado con ese número de documento");
        }

        // Clear dias trabajo and flush to execute DELETEs before INSERTs
        empleado.clearDiasTrabajo();
        entityManager.flush();

        updateEmpleadoFromDTO(empleado, dto, false);
        empleado = empleadoRepository.save(empleado);
        return toDTO(empleado);
    }

    public EmpleadoDTO toggleActivo(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con id: " + id));

        empleado.setActivo(!empleado.getActivo());
        empleado = empleadoRepository.save(empleado);
        return toDTO(empleado);
    }

    public void delete(Long id) {
        if (!empleadoRepository.existsById(id)) {
            throw new EntityNotFoundException("Empleado no encontrado con id: " + id);
        }
        empleadoRepository.deleteById(id);
    }

    private void updateEmpleadoFromDTO(Empleado empleado, EmpleadoCreateDTO dto, boolean clearDias) {
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setDocumentoTipo(dto.getDocumentoTipo());
        empleado.setDocumentoNumero(dto.getDocumentoNumero());
        empleado.setTipoPago(dto.getTipoPago());
        empleado.setTelefono(dto.getTelefono());
        empleado.setCargo(dto.getCargo());
        empleado.setCobraPorHora(dto.getCobraPorHora());
        empleado.setCobraPorDia(dto.getCobraPorDia());
        empleado.setHorasPorDia(dto.getHorasPorDia() != null ? dto.getHorasPorDia() : 8);
        empleado.setAjustePorcentaje(dto.getAjustePorcentaje());
        empleado.setAjusteMonto(dto.getAjusteMonto());

        if (dto.getRolCode() != null) {
            Role rol = roleRepository.findByCode(dto.getRolCode())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + dto.getRolCode()));
            empleado.setRol(rol);
        }

        // Update work days
        if (clearDias) {
            empleado.clearDiasTrabajo();
        }
        if (dto.getDiasTrabajo() != null) {
            for (DiaSemana dia : dto.getDiasTrabajo()) {
                empleado.addDiaTrabajo(dia);
            }
        }
    }

    private EmpleadoDTO toDTO(Empleado empleado) {
        Set<DiaSemana> dias = empleado.getDiasTrabajo().stream()
                .map(EmpleadoDiaTrabajo::getDiaSemana)
                .collect(Collectors.toSet());

        return EmpleadoDTO.builder()
                .id(empleado.getId())
                .nombre(empleado.getNombre())
                .apellido(empleado.getApellido())
                .nombreCompleto(empleado.getNombreCompleto())
                .documentoTipo(empleado.getDocumentoTipo())
                .documentoNumero(empleado.getDocumentoNumero())
                .rolCode(empleado.getRol() != null ? empleado.getRol().getCode() : null)
                .rolDescription(empleado.getRol() != null ? empleado.getRol().getDescription() : null)
                .tipoPago(empleado.getTipoPago())
                .activo(empleado.getActivo())
                .fechaAlta(empleado.getFechaAlta())
                .telefono(empleado.getTelefono())
                .cargo(empleado.getCargo())
                .cobraPorHora(empleado.getCobraPorHora())
                .cobraPorDia(empleado.getCobraPorDia())
                .horasPorDia(empleado.getHorasPorDia())
                .ajustePorcentaje(empleado.getAjustePorcentaje())
                .ajusteMonto(empleado.getAjusteMonto())
                .diasTrabajo(dias)
                .build();
    }
}

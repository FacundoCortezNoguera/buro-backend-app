package ar.buro.personal.empleados;

import ar.buro.personal.empleados.dto.EmpleadoCreateDTO;
import ar.buro.personal.empleados.dto.EmpleadoDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> findAll(
            @RequestParam(required = false, defaultValue = "false") boolean soloActivos) {
        List<EmpleadoDTO> empleados = soloActivos
                ? empleadoService.findAllActivos()
                : empleadoService.findAll();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> findById(@PathVariable Long id) {
        EmpleadoDTO empleado = empleadoService.findById(id);
        return ResponseEntity.ok(empleado);
    }

    @PostMapping
    public ResponseEntity<EmpleadoDTO> create(@Valid @RequestBody EmpleadoCreateDTO dto) {
        EmpleadoDTO created = empleadoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EmpleadoCreateDTO dto) {
        EmpleadoDTO updated = empleadoService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<EmpleadoDTO> toggleActivo(@PathVariable Long id) {
        EmpleadoDTO updated = empleadoService.toggleActivo(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        empleadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

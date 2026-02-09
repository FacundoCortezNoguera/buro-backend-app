package ar.buro.personal.cambios;

import ar.buro.personal.cambios.dto.CambioEmpleadoCreateDTO;
import ar.buro.personal.cambios.dto.CambioEmpleadoDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cambios")
public class CambioEmpleadoController {

    private final CambioEmpleadoService cambioService;

    public CambioEmpleadoController(CambioEmpleadoService cambioService) {
        this.cambioService = cambioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<List<CambioEmpleadoDTO>> getAll() {
        return ResponseEntity.ok(cambioService.findAll());
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<List<CambioEmpleadoDTO>> getPendientes() {
        return ResponseEntity.ok(cambioService.findPendientes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<CambioEmpleadoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cambioService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<CambioEmpleadoDTO> crear(
            @Valid @RequestBody CambioEmpleadoCreateDTO dto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(cambioService.crearCambio(dto, username));
    }

    // --- Endpoints públicos (sin auth, acceso por token) ---

    @GetMapping("/aprobar/{token}")
    public ResponseEntity<CambioEmpleadoDTO> getByToken(@PathVariable String token) {
        return ResponseEntity.ok(cambioService.findByToken(token));
    }

    @PostMapping("/aprobar/{token}")
    public ResponseEntity<CambioEmpleadoDTO> aprobar(@PathVariable String token) {
        return ResponseEntity.ok(cambioService.aprobarPorToken(token));
    }

    @PostMapping("/rechazar/{token}")
    public ResponseEntity<CambioEmpleadoDTO> rechazar(@PathVariable String token) {
        return ResponseEntity.ok(cambioService.rechazarPorToken(token));
    }
}

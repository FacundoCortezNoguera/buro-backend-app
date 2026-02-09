package ar.buro.personal.notificaciones;

import ar.buro.personal.notificaciones.dto.DestinatarioCreateDTO;
import ar.buro.personal.notificaciones.dto.DestinatarioDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinatarios-notificacion")
public class DestinatarioController {

    private final DestinatarioService destinatarioService;

    public DestinatarioController(DestinatarioService destinatarioService) {
        this.destinatarioService = destinatarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DestinatarioDTO>> getAll() {
        return ResponseEntity.ok(destinatarioService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinatarioDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(destinatarioService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinatarioDTO> create(@Valid @RequestBody DestinatarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(destinatarioService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinatarioDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DestinatarioCreateDTO dto) {
        return ResponseEntity.ok(destinatarioService.update(id, dto));
    }

    @PatchMapping("/{id}/toggle-activo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinatarioDTO> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(destinatarioService.toggleActivo(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        destinatarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

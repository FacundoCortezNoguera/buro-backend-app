package ar.buro.personal.tarifas;

import ar.buro.personal.tarifas.dto.TarifaCargoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaCargoController {

    private final TarifaCargoService tarifaCargoService;

    public TarifaCargoController(TarifaCargoService tarifaCargoService) {
        this.tarifaCargoService = tarifaCargoService;
    }

    @GetMapping
    public List<TarifaCargoDTO> findAll() {
        return tarifaCargoService.findAll();
    }

    @GetMapping("/{id}")
    public TarifaCargoDTO findById(@PathVariable Long id) {
        return tarifaCargoService.findById(id);
    }

    @GetMapping("/cargo/{cargo}")
    public TarifaCargoDTO findByCargo(@PathVariable String cargo) {
        return tarifaCargoService.findByCargo(cargo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TarifaCargoDTO update(@PathVariable Long id, @RequestBody TarifaCargoDTO dto) {
        return tarifaCargoService.update(id, dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TarifaCargoDTO> create(@RequestBody TarifaCargoDTO dto) {
        return ResponseEntity.ok(tarifaCargoService.create(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tarifaCargoService.delete(id);
        return ResponseEntity.ok().build();
    }
}

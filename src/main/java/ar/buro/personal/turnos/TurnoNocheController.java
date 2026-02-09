package ar.buro.personal.turnos;

import ar.buro.personal.turnos.dto.TurnoNocheBulkDTO;
import ar.buro.personal.turnos.dto.TurnoNocheDTO;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turnos-noche")
public class TurnoNocheController {

    private final TurnoNocheService turnoNocheService;

    public TurnoNocheController(TurnoNocheService turnoNocheService) {
        this.turnoNocheService = turnoNocheService;
    }

    @GetMapping("/{fecha}")
    public ResponseEntity<List<TurnoNocheDTO>> getByFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<TurnoNocheDTO> turnos = turnoNocheService.findByFecha(fecha);
        return ResponseEntity.ok(turnos);
    }

    @GetMapping
    public ResponseEntity<List<TurnoNocheDTO>> getByFechaRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<TurnoNocheDTO> turnos = turnoNocheService.findByFechaRange(desde, hasta);
        return ResponseEntity.ok(turnos);
    }

    @PostMapping("/{fecha}")
    public ResponseEntity<List<TurnoNocheDTO>> saveForFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Valid @RequestBody TurnoNocheBulkDTO bulkDto) {
        bulkDto.setFecha(fecha); // Override with path variable
        List<TurnoNocheDTO> saved = turnoNocheService.saveTurnosBulk(bulkDto);
        return ResponseEntity.ok(saved);
    }
}

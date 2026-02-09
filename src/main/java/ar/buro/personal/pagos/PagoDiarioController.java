package ar.buro.personal.pagos;

import ar.buro.personal.pagos.dto.*;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PagoDiarioController {

    private final PagoDiarioService pagoDiarioService;

    public PagoDiarioController(PagoDiarioService pagoDiarioService) {
        this.pagoDiarioService = pagoDiarioService;
    }

    @GetMapping("/pagos-diarios")
    public ResponseEntity<List<PagoDiarioDTO>> getPagosByFechaRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<PagoDiarioDTO> pagos = pagoDiarioService.findByFechaRange(desde, hasta);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/pagos-diarios/pendientes")
    public ResponseEntity<List<PagoDiarioDTO>> getPagosPendientes() {
        List<PagoDiarioDTO> pagos = pagoDiarioService.findPendientes();
        return ResponseEntity.ok(pagos);
    }

    @PutMapping("/pagos-diarios/{id}/marcar-pagado")
    public ResponseEntity<PagoDiarioDTO> marcarPagado(@PathVariable Long id) {
        PagoDiarioDTO pago = pagoDiarioService.marcarPagado(id);
        return ResponseEntity.ok(pago);
    }

    @PutMapping("/pagos-diarios/{id}/marcar-no-pagado")
    public ResponseEntity<PagoDiarioDTO> marcarNoPagado(@PathVariable Long id) {
        PagoDiarioDTO pago = pagoDiarioService.marcarNoPagado(id);
        return ResponseEntity.ok(pago);
    }

    @PostMapping("/pagos-diarios/calcular-salario")
    public ResponseEntity<CalcularSalarioResponseDTO> calcularSalarios(
            @Valid @RequestBody CalcularSalarioRequestDTO request) {
        CalcularSalarioResponseDTO response = pagoDiarioService.calcularSalarios(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cierre-noche/enviar-email")
    public ResponseEntity<Map<String, String>> enviarEmailCierreNoche(
            @Valid @RequestBody EnviarEmailRequestDTO request) {
        String resultado = pagoDiarioService.enviarEmailCierreNoche(
                request.getFecha(),
                request.getDestinatario(),
                request.getAsunto());
        return ResponseEntity.ok(Map.of("message", resultado));
    }

    @GetMapping("/pagos-diarios/resumen-dia")
    public ResponseEntity<ResumenPagosDTO> getResumenPagosDia() {
        ResumenPagosDTO resumen = pagoDiarioService.getResumenPagosDia();
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/pagos-diarios/resumen-hora")
    public ResponseEntity<ResumenPagosDTO> getResumenPagosHora() {
        ResumenPagosDTO resumen = pagoDiarioService.getResumenPagosHora();
        return ResponseEntity.ok(resumen);
    }

    @PostMapping("/pagos-diarios/empleado/{empleadoId}/marcar-pagado")
    public ResponseEntity<Map<String, String>> marcarEmpleadoPagado(
            @PathVariable Long empleadoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        pagoDiarioService.marcarEmpleadoPagado(empleadoId, desde, hasta);
        return ResponseEntity.ok(Map.of("message", "Pagos marcados como pagados"));
    }
}

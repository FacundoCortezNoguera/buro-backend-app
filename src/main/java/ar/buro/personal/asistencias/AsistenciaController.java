package ar.buro.personal.asistencias;

import ar.buro.personal.asistencias.dto.AsistenciaDTO;
import ar.buro.personal.asistencias.dto.MarcarPresenteRequest;
import ar.buro.personal.asistencias.dto.RegistroCamaraRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @Value("${app.camara.api-key:}")
    private String camaraApiKey;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    /**
     * Endpoint para la cámara Anviz - No requiere JWT, usa API Key
     */
    @PostMapping("/camara")
    public ResponseEntity<?> registrarDesdeCamara(
            @Valid @RequestBody RegistroCamaraRequest request,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey
    ) {
        // Validar API Key
        if (camaraApiKey.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "API Key de cámara no configurada"));
        }

        if (apiKey == null || !apiKey.equals(camaraApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "API Key inválida"));
        }

        try {
            AsistenciaDTO asistencia = asistenciaService.registrarDesdeCamara(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(asistencia);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/marcar-presente")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'SUPERVISOR')")
    public ResponseEntity<AsistenciaDTO> marcarPresente(
            @Valid @RequestBody MarcarPresenteRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        AsistenciaDTO asistencia = asistenciaService.marcarPresente(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(asistencia);
    }

    @GetMapping("/hoy")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AsistenciaDTO>> getAsistenciasHoy() {
        return ResponseEntity.ok(asistenciaService.getAsistenciasHoy());
    }

    @GetMapping("/fecha/{fecha}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AsistenciaDTO>> getAsistenciasByFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(asistenciaService.getAsistenciasByFecha(fecha));
    }

    @GetMapping("/empleado/{empleadoId}/hoy")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AsistenciaDTO> getAsistenciaEmpleadoHoy(@PathVariable Long empleadoId) {
        AsistenciaDTO asistencia = asistenciaService.getAsistenciaEmpleadoHoy(empleadoId);
        if (asistencia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(asistencia);
    }

    @GetMapping("/empleado/{empleadoId}/historial")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AsistenciaDTO>> getHistorialEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(asistenciaService.getHistorialEmpleado(empleadoId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<AsistenciaDTO> actualizarAsistencia(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        LocalTime horaLlegada = LocalTime.parse((String) body.get("horaLlegada"));
        String observaciones = (String) body.get("observaciones");

        AsistenciaDTO asistencia = asistenciaService.actualizarAsistencia(id, horaLlegada, observaciones);
        return ResponseEntity.ok(asistencia);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarAsistencia(@PathVariable Long id) {
        asistenciaService.eliminarAsistencia(id);
        return ResponseEntity.noContent().build();
    }
}

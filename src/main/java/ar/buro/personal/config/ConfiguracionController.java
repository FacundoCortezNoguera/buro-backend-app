package ar.buro.personal.config;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    public ConfiguracionController(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getAllConfiguraciones() {
        return ResponseEntity.ok(configuracionService.getAllConfiguraciones());
    }

    @GetMapping("/{clave}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getConfiguracion(@PathVariable String clave) {
        String valor = configuracionService.getConfiguracion(clave);
        if (valor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("clave", clave, "valor", valor));
    }

    @PutMapping("/{clave}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> updateConfiguracion(
            @PathVariable String clave,
            @RequestBody Map<String, String> body
    ) {
        String valor = body.get("valor");
        if (valor == null) {
            return ResponseEntity.badRequest().build();
        }

        ConfiguracionSistema config = configuracionService.updateConfiguracion(clave, valor);
        return ResponseEntity.ok(Map.of(
                "clave", config.getClave(),
                "valor", config.getValor()
        ));
    }

    // Endpoints específicos para configuraciones comunes

    @GetMapping("/email-reportes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getEmailReportes() {
        String email = configuracionService.getEmailReportes();
        return ResponseEntity.ok(Map.of("email", email != null ? email : ""));
    }

    @PutMapping("/email-reportes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> setEmailReportes(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null) {
            return ResponseEntity.badRequest().build();
        }

        configuracionService.setEmailReportes(email);
        return ResponseEntity.ok(Map.of("email", email, "mensaje", "Email actualizado correctamente"));
    }

    @GetMapping("/nombre-negocio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getNombreNegocio() {
        String nombre = configuracionService.getNombreNegocio();
        return ResponseEntity.ok(Map.of("nombre", nombre));
    }

    @PutMapping("/nombre-negocio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> setNombreNegocio(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        if (nombre == null || nombre.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        configuracionService.setNombreNegocio(nombre);
        return ResponseEntity.ok(Map.of("nombre", nombre, "mensaje", "Nombre actualizado correctamente"));
    }
}

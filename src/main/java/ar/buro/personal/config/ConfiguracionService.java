package ar.buro.personal.config;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionService(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, String> getAllConfiguraciones() {
        List<ConfiguracionSistema> configs = configuracionRepository.findAll();
        Map<String, String> result = new HashMap<>();
        for (ConfiguracionSistema config : configs) {
            result.put(config.getClave(), config.getValor());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public String getConfiguracion(String clave) {
        return configuracionRepository.findByClave(clave)
                .map(ConfiguracionSistema::getValor)
                .orElse(null);
    }

    public ConfiguracionSistema updateConfiguracion(String clave, String valor) {
        ConfiguracionSistema config = configuracionRepository.findByClave(clave)
                .orElseThrow(() -> new EntityNotFoundException("Configuración no encontrada: " + clave));

        config.setValor(valor);
        return configuracionRepository.save(config);
    }

    public ConfiguracionSistema createOrUpdateConfiguracion(String clave, String valor, String descripcion) {
        ConfiguracionSistema config = configuracionRepository.findByClave(clave)
                .orElse(new ConfiguracionSistema(clave, valor, descripcion));

        config.setValor(valor);
        if (descripcion != null && !descripcion.isBlank()) {
            config.setDescripcion(descripcion);
        }

        return configuracionRepository.save(config);
    }

    // Métodos específicos para configuraciones comunes
    public String getEmailReportes() {
        return getConfiguracion("EMAIL_REPORTES");
    }

    public void setEmailReportes(String email) {
        createOrUpdateConfiguracion("EMAIL_REPORTES", email, "Email donde se envían los reportes de cierre de noche");
    }

    public String getNombreNegocio() {
        String nombre = getConfiguracion("NOMBRE_NEGOCIO");
        return nombre != null ? nombre : "BURO";
    }

    public void setNombreNegocio(String nombre) {
        createOrUpdateConfiguracion("NOMBRE_NEGOCIO", nombre, "Nombre del negocio para los reportes");
    }
}

package ar.buro.personal.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaEmpleadoDTO {

    private Long empleadoId;
    private String nombreCompleto;
    private String cargo;
    private String tipoAlerta; // TARDANZAS_FRECUENTES, AUSENCIAS, BAJO_RENDIMIENTO
    private String descripcion;
    private int valor; // cantidad de incidencias
    private LocalDate fechaUltimaIncidencia;
}

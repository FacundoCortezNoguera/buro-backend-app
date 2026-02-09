package ar.buro.personal.notificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinatarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private Boolean activo;
    private Boolean recibeCierreNoche;
    private Boolean recibeReportesMensuales;
    private Boolean recibeCambiosEmpleados;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

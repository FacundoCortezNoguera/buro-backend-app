package ar.buro.personal.cambios.dto;

import ar.buro.personal.cambios.EstadoCambio;
import ar.buro.personal.cambios.TipoCambio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambioEmpleadoDTO {
    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private TipoCambio tipoCambio;
    private String campoModificado;
    private String valorAnterior;
    private String valorNuevo;
    private String descripcion;
    private EstadoCambio estado;
    private String solicitadoPor;
    private String aprobadoPor;
    private LocalDateTime fechaAprobacion;
    private LocalDateTime createdAt;
}

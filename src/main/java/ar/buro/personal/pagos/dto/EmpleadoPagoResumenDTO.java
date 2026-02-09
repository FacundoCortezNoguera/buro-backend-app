package ar.buro.personal.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoPagoResumenDTO {

    private Long empleadoId;
    private String empleadoNombre;
    private String cargo;
    private String tipoPago; // HORA o DIA

    // Totales
    private BigDecimal totalPeriodo;
    private BigDecimal totalPendienteAnterior;
    private BigDecimal totalACobrar;

    // Contadores
    private int diasTrabajados;
    private int diasPendientesAnteriores;

    // Desglose por día del período actual
    private List<PagoDiaDetalleDTO> diasPeriodo;

    // Extras
    private List<ExtraPagoDTO> extras;
}

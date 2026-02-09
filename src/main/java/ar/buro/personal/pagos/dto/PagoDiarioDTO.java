package ar.buro.personal.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoDiarioDTO {

    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private String empleadoCargo;
    private LocalDate fecha;
    private String concepto;
    private BigDecimal monto;
    private Boolean pagado;
    private LocalDate fechaPago;
    private Long turnoNocheId;
    private String observaciones;
}

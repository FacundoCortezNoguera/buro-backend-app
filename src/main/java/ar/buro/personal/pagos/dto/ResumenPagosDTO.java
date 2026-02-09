package ar.buro.personal.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPagosDTO {

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private BigDecimal totalPeriodo;
    private BigDecimal totalPendienteAnterior;
    private BigDecimal totalGeneral;
    private List<EmpleadoPagoResumenDTO> empleados;
}

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
public class PagoDiaDetalleDTO {

    private Long pagoId; // null si no está creado aún
    private LocalDate fecha;
    private BigDecimal montoBase;
    private BigDecimal montoExtras;
    private BigDecimal montoTotal;
    private boolean pagado;
    private String horaLlegada;
    private String estado; // PUNTUAL, TARDE, TEMPRANO
}

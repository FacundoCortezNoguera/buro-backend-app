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
public class ExtraPagoDTO {

    private String tipo; // CENA, CAMBIO_CARGO, AJUSTE
    private String descripcion;
    private BigDecimal monto;
    private LocalDate fecha;
}

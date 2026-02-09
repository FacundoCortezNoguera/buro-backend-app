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
public class CalcularSalarioResponseDTO {

    private int pagosCreados;
    private int pagosActualizados;
    private BigDecimal totalMonto;
    private List<PagoDiarioDTO> pagos;
}

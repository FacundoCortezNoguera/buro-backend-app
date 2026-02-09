package ar.buro.personal.tarifas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TarifaCargoDTO {
    private Long id;
    private String cargo;
    private String tipoPago;  // HORA o DIA
    private BigDecimal montoPorHora;
    private BigDecimal montoPorDia;
}

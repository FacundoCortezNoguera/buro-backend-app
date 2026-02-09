package ar.buro.personal.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoRankingDTO {

    private Long empleadoId;
    private String nombreCompleto;
    private String cargo;
    private String tipoPago;

    // Metricas
    private int diasTrabajados;
    private BigDecimal totalCobrado;
    private int asistenciasPuntuales;
    private int asistenciasTarde;
    private double porcentajePuntualidad;
    private double promedioMinutosDiferencia;
}

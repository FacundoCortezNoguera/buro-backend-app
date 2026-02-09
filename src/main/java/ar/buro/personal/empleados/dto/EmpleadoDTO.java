package ar.buro.personal.empleados.dto;

import ar.buro.personal.empleados.DiaSemana;
import ar.buro.personal.empleados.TipoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String documentoTipo;
    private String documentoNumero;
    private String rolCode;
    private String rolDescription;
    private TipoPago tipoPago;
    private Boolean activo;
    private LocalDate fechaAlta;
    private String telefono;
    private String cargo;
    private BigDecimal cobraPorHora;
    private BigDecimal cobraPorDia;
    private Integer horasPorDia;
    private BigDecimal ajustePorcentaje;
    private BigDecimal ajusteMonto;
    private Set<DiaSemana> diasTrabajo;
}

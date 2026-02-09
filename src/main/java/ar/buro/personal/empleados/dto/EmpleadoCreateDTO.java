package ar.buro.personal.empleados.dto;

import ar.buro.personal.empleados.DiaSemana;
import ar.buro.personal.empleados.TipoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class EmpleadoCreateDTO {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    private String apellido;

    private String documentoTipo;

    @NotBlank(message = "El número de documento es requerido")
    private String documentoNumero;

    private String rolCode;

    @NotNull(message = "El tipo de pago es requerido")
    private TipoPago tipoPago;

    private String telefono;

    private String cargo;

    private BigDecimal cobraPorHora;

    private BigDecimal cobraPorDia;

    private Integer horasPorDia;

    private BigDecimal ajustePorcentaje;

    private BigDecimal ajusteMonto;

    private Set<DiaSemana> diasTrabajo;
}

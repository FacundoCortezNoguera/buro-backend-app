package ar.buro.personal.cambios.dto;

import ar.buro.personal.cambios.TipoCambio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambioEmpleadoCreateDTO {

    @NotNull(message = "El ID del empleado es requerido")
    private Long empleadoId;

    @NotNull(message = "El tipo de cambio es requerido")
    private TipoCambio tipoCambio;

    @NotBlank(message = "El campo modificado es requerido")
    private String campoModificado;

    private String valorAnterior;

    @NotBlank(message = "El valor nuevo es requerido")
    private String valorNuevo;

    private String descripcion;
}

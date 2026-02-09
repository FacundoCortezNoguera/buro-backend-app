package ar.buro.personal.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CierreNocheEmailDTO {
    @NotBlank(message = "Destinatario es requerido")
    @Email(message = "Email inválido")
    private String to;

    @NotBlank(message = "Fecha es requerida")
    private String fecha;

    private List<EmpleadoResumenDTO> empleados;
    private String totalPagos;
    private String observaciones;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EmpleadoResumenDTO {
        private String nombre;
        private String cargo;
        private String horaEntrada;
        private String horaSalida;
        private String monto;
        private boolean pagado;
    }
}

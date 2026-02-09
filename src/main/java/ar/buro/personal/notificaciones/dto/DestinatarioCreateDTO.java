package ar.buro.personal.notificaciones.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DestinatarioCreateDTO {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El email es requerido")
    @Email(message = "Email inválido")
    private String email;

    private Boolean recibeCierreNoche = false;
    private Boolean recibeReportesMensuales = false;
    private Boolean recibeCambiosEmpleados = false;
}

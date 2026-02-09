package ar.buro.personal.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "El username es requerido")
    private String username;

    @NotBlank(message = "La password es requerida")
    private String password;
}

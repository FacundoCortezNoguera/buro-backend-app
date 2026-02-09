package ar.buro.personal.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioCreateDTO {
    @NotBlank(message = "Username es requerido")
    private String username;

    @NotBlank(message = "Password es requerido")
    private String password;

    @NotBlank(message = "Nombre es requerido")
    private String nombre;

    @NotBlank(message = "Role es requerido")
    private String role;
}

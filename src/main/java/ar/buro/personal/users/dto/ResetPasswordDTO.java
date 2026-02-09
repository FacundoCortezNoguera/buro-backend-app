package ar.buro.personal.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordDTO {
    @NotBlank(message = "Nueva contraseña es requerida")
    private String newPassword;
}

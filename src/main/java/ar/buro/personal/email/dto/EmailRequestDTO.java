package ar.buro.personal.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailRequestDTO {
    @NotBlank(message = "Destinatario es requerido")
    @Email(message = "Email inválido")
    private String to;

    @NotBlank(message = "Asunto es requerido")
    private String subject;

    @NotBlank(message = "Contenido es requerido")
    private String body;

    private boolean html = false;
}

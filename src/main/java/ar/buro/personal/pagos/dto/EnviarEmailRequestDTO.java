package ar.buro.personal.pagos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnviarEmailRequestDTO {

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha;

    private String destinatario;
    private String asunto;
}

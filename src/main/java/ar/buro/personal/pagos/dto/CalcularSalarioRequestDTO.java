package ar.buro.personal.pagos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CalcularSalarioRequestDTO {

    @NotNull(message = "La fecha desde es requerida")
    private LocalDate desde;

    @NotNull(message = "La fecha hasta es requerida")
    private LocalDate hasta;

    private List<Long> empleadoIds; // Optional: if null, process all employees
}

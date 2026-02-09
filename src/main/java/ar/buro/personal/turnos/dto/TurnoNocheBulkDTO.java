package ar.buro.personal.turnos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TurnoNocheBulkDTO {

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha;

    @Valid
    @NotNull(message = "Los turnos son requeridos")
    private List<TurnoNocheCreateDTO> turnos;
}

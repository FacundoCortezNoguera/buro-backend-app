package ar.buro.personal.asistencias.dto;

import ar.buro.personal.asistencias.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaDTO {

    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private String empleadoCargo;
    private LocalDate fecha;
    private LocalTime horaLlegada;
    private LocalTime horaEsperada;
    private Integer minutosDiferencia;
    private EstadoAsistencia estado;
    private String registradoPor;
    private String observaciones;
}

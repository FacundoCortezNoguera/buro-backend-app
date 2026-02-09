package ar.buro.personal.reportes.dto;

import ar.buro.personal.reportes.TipoReporte;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {

    private Long id;
    private TipoReporte tipo;
    private LocalDate fechaReporte;
    private String titulo;
    private String descripcion;
    private String archivoNombre;
    private BigDecimal totalMonto;
    private Integer cantidadEmpleados;
    private Boolean enviado;
    private String emailDestino;
    private LocalDateTime fechaEnvio;
    private LocalDateTime createdAt;
}

package ar.buro.personal.reportes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Getter
@Setter
@NoArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoReporte tipo;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDate fechaReporte;

    @Column(length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "archivo_nombre", length = 255)
    private String archivoNombre;

    @Column(name = "archivo_path", length = 500)
    private String archivoPath;

    @Column(name = "total_monto", precision = 12, scale = 2)
    private BigDecimal totalMonto;

    @Column(name = "cantidad_empleados")
    private Integer cantidadEmpleados;

    @Column(nullable = false)
    private Boolean enviado = false;

    @Column(name = "email_destino", length = 255)
    private String emailDestino;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Reporte(TipoReporte tipo, LocalDate fechaReporte, String titulo) {
        this.tipo = tipo;
        this.fechaReporte = fechaReporte;
        this.titulo = titulo;
    }
}

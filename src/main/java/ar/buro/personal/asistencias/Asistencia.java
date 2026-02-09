package ar.buro.personal.asistencias;

import ar.buro.personal.empleados.Empleado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "asistencias", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"empleado_id", "fecha"})
})
@Getter
@Setter
@NoArgsConstructor
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_llegada", nullable = false)
    private LocalTime horaLlegada;

    @Column(name = "hora_esperada")
    private LocalTime horaEsperada = LocalTime.of(22, 0);

    @Column(name = "minutos_diferencia")
    private Integer minutosDiferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAsistencia estado;

    @Column(name = "registrado_por", length = 60)
    private String registradoPor;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Asistencia(Empleado empleado, LocalDate fecha, LocalTime horaLlegada) {
        this.empleado = empleado;
        this.fecha = fecha;
        this.horaLlegada = horaLlegada;
        calcularEstado();
    }

    public void calcularEstado() {
        if (horaLlegada == null || horaEsperada == null) {
            this.estado = EstadoAsistencia.PUNTUAL;
            this.minutosDiferencia = 0;
            return;
        }

        // Calcular diferencia en minutos
        int minutosLlegada = horaLlegada.getHour() * 60 + horaLlegada.getMinute();
        int minutosEsperada = horaEsperada.getHour() * 60 + horaEsperada.getMinute();

        this.minutosDiferencia = minutosLlegada - minutosEsperada;

        // Tolerancia de 5 minutos para considerar puntual
        if (minutosDiferencia <= -5) {
            this.estado = EstadoAsistencia.TEMPRANO;
        } else if (minutosDiferencia <= 5) {
            this.estado = EstadoAsistencia.PUNTUAL;
        } else {
            this.estado = EstadoAsistencia.TARDE;
        }
    }
}

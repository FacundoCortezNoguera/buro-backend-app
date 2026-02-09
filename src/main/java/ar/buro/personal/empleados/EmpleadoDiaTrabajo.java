package ar.buro.personal.empleados;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empleado_dias_trabajo",
       uniqueConstraints = @UniqueConstraint(columnNames = {"empleado_id", "dia_semana"}))
@Getter
@Setter
@NoArgsConstructor
public class EmpleadoDiaTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    private DiaSemana diaSemana;

    public EmpleadoDiaTrabajo(Empleado empleado, DiaSemana diaSemana) {
        this.empleado = empleado;
        this.diaSemana = diaSemana;
    }
}

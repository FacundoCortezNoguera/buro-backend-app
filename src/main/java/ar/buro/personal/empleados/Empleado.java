package ar.buro.personal.empleados;

import ar.buro.personal.users.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "empleados")
@Getter
@Setter
@NoArgsConstructor
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String apellido;

    @Column(name = "documento_tipo", length = 10)
    private String documentoTipo;

    @Column(name = "documento_numero", nullable = false, length = 20)
    private String documentoNumero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Role rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 10)
    private TipoPago tipoPago;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta = LocalDate.now();

    @Column(length = 20)
    private String telefono;

    @Column(length = 50)
    private String cargo;

    @Column(name = "cobra_por_hora", precision = 10, scale = 2)
    private BigDecimal cobraPorHora;

    @Column(name = "cobra_por_dia", precision = 10, scale = 2)
    private BigDecimal cobraPorDia;

    @Column(name = "horas_por_dia")
    private Integer horasPorDia = 8;

    @Column(name = "ajuste_porcentaje", precision = 5, scale = 2)
    private BigDecimal ajustePorcentaje = BigDecimal.ZERO;

    @Column(name = "ajuste_monto", precision = 10, scale = 2)
    private BigDecimal ajusteMonto = BigDecimal.ZERO;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmpleadoDiaTrabajo> diasTrabajo = new HashSet<>();

    public void addDiaTrabajo(DiaSemana dia) {
        EmpleadoDiaTrabajo diaTrabajo = new EmpleadoDiaTrabajo(this, dia);
        diasTrabajo.add(diaTrabajo);
    }

    public void clearDiasTrabajo() {
        diasTrabajo.clear();
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}

package ar.buro.personal.cambios;

import ar.buro.personal.empleados.Empleado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cambios_empleado")
@Getter
@Setter
@NoArgsConstructor
public class CambioEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "tipo_cambio", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoCambio tipoCambio;

    @Column(name = "campo_modificado", length = 100)
    private String campoModificado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoCambio estado = EstadoCambio.PENDIENTE;

    @Column(length = 255, unique = true)
    private String token;

    @Column(name = "token_expiracion")
    private LocalDateTime tokenExpiracion;

    @Column(name = "solicitado_por", length = 100)
    private String solicitadoPor;

    @Column(name = "aprobado_por", length = 100)
    private String aprobadoPor;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

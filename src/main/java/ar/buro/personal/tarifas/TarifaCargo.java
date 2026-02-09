package ar.buro.personal.tarifas;

import ar.buro.personal.empleados.TipoPago;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarifas_cargo")
@Getter
@Setter
@NoArgsConstructor
public class TarifaCargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 10)
    private TipoPago tipoPago = TipoPago.DIA;

    @Column(name = "monto_por_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPorHora = BigDecimal.ZERO;

    @Column(name = "monto_por_dia", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPorDia = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public TarifaCargo(String cargo, TipoPago tipoPago, BigDecimal montoPorHora, BigDecimal montoPorDia) {
        this.cargo = cargo;
        this.tipoPago = tipoPago;
        this.montoPorHora = montoPorHora;
        this.montoPorDia = montoPorDia;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

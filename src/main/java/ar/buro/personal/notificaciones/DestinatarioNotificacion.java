package ar.buro.personal.notificaciones;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "destinatarios_notificacion")
@Getter
@Setter
@NoArgsConstructor
public class DestinatarioNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "recibe_cierre_noche", nullable = false)
    private Boolean recibeCierreNoche = false;

    @Column(name = "recibe_reportes_mensuales", nullable = false)
    private Boolean recibeReportesMensuales = false;

    @Column(name = "recibe_cambios_empleados", nullable = false)
    private Boolean recibeCambiosEmpleados = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

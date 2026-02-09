package ar.buro.personal.notificaciones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinatarioRepository extends JpaRepository<DestinatarioNotificacion, Long> {

    Optional<DestinatarioNotificacion> findByEmail(String email);

    boolean existsByEmail(String email);

    List<DestinatarioNotificacion> findByActivoTrue();

    List<DestinatarioNotificacion> findByActivoTrueAndRecibeCierreNocheTrue();

    List<DestinatarioNotificacion> findByActivoTrueAndRecibeReportesMensualesTrue();

    List<DestinatarioNotificacion> findByActivoTrueAndRecibeCambiosEmpleadosTrue();
}

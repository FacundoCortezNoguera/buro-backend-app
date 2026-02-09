package ar.buro.personal.cambios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CambioEmpleadoRepository extends JpaRepository<CambioEmpleado, Long> {

    Optional<CambioEmpleado> findByToken(String token);

    List<CambioEmpleado> findByEstadoOrderByCreatedAtDesc(EstadoCambio estado);

    List<CambioEmpleado> findAllByOrderByCreatedAtDesc();
}

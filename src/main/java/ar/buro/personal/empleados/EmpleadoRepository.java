package ar.buro.personal.empleados;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    List<Empleado> findByActivoTrue();

    List<Empleado> findByActivoFalse();

    @Query("SELECT e FROM Empleado e LEFT JOIN FETCH e.diasTrabajo WHERE e.id = :id")
    Optional<Empleado> findByIdWithDiasTrabajo(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Empleado e LEFT JOIN FETCH e.diasTrabajo WHERE e.activo = true")
    List<Empleado> findAllActivosWithDiasTrabajo();

    @Query("SELECT DISTINCT e FROM Empleado e LEFT JOIN FETCH e.diasTrabajo")
    List<Empleado> findAllWithDiasTrabajo();

    boolean existsByDocumentoNumero(String documentoNumero);

    Optional<Empleado> findByDocumentoNumero(String documentoNumero);

    // Dashboard queries
    long countByActivoTrueAndTipoPago(TipoPago tipoPago);

    long countByActivoTrue();
}

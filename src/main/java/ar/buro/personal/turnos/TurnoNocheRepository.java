package ar.buro.personal.turnos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoNocheRepository extends JpaRepository<TurnoNoche, Long> {

    @Query("SELECT t FROM TurnoNoche t JOIN FETCH t.empleado WHERE t.fecha = :fecha")
    List<TurnoNoche> findByFechaWithEmpleado(@Param("fecha") LocalDate fecha);

    List<TurnoNoche> findByFecha(LocalDate fecha);

    @Query("SELECT t FROM TurnoNoche t JOIN FETCH t.empleado WHERE t.fecha BETWEEN :desde AND :hasta")
    List<TurnoNoche> findByFechaBetweenWithEmpleado(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    Optional<TurnoNoche> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    @Query("SELECT t FROM TurnoNoche t JOIN FETCH t.empleado WHERE t.empleado.id = :empleadoId AND t.fecha BETWEEN :desde AND :hasta")
    List<TurnoNoche> findByEmpleadoAndFechaBetween(
            @Param("empleadoId") Long empleadoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);
}

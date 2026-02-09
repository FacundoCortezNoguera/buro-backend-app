package ar.buro.personal.asistencias;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findByFechaOrderByHoraLlegadaAsc(LocalDate fecha);

    Optional<Asistencia> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    boolean existsByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    @Query("SELECT a FROM Asistencia a JOIN FETCH a.empleado WHERE a.fecha = :fecha ORDER BY a.horaLlegada ASC")
    List<Asistencia> findByFechaWithEmpleado(@Param("fecha") LocalDate fecha);

    @Query("SELECT a FROM Asistencia a JOIN FETCH a.empleado WHERE a.empleado.id = :empleadoId ORDER BY a.fecha DESC")
    List<Asistencia> findByEmpleadoIdOrderByFechaDesc(@Param("empleadoId") Long empleadoId);

    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.empleado.id = :empleadoId AND a.estado = :estado")
    long countByEmpleadoIdAndEstado(@Param("empleadoId") Long empleadoId, @Param("estado") EstadoAsistencia estado);

    @Query("SELECT a FROM Asistencia a JOIN FETCH a.empleado WHERE a.fecha BETWEEN :desde AND :hasta ORDER BY a.empleado.id, a.fecha")
    List<Asistencia> findByFechaBetweenWithEmpleado(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    // Dashboard queries
    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.fecha BETWEEN :desde AND :hasta AND a.estado = :estado")
    long countByFechaBetweenAndEstado(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta, @Param("estado") EstadoAsistencia estado);

    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.fecha BETWEEN :desde AND :hasta")
    long countByFechaBetween(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT a.empleado.id, a.estado, COUNT(a) FROM Asistencia a " +
           "WHERE a.fecha BETWEEN :desde AND :hasta GROUP BY a.empleado.id, a.estado")
    List<Object[]> countByEmpleadoAndEstado(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT a.empleado.id, AVG(a.minutosDiferencia) FROM Asistencia a " +
           "WHERE a.fecha BETWEEN :desde AND :hasta GROUP BY a.empleado.id")
    List<Object[]> avgMinutosDiferenciaByEmpleado(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT a.empleado.id, COUNT(a) FROM Asistencia a " +
           "WHERE a.fecha BETWEEN :desde AND :hasta GROUP BY a.empleado.id")
    List<Object[]> countAsistenciasByEmpleado(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT a.empleado.id, MAX(a.fecha) FROM Asistencia a " +
           "WHERE a.fecha BETWEEN :desde AND :hasta AND a.estado = :estado GROUP BY a.empleado.id")
    List<Object[]> findLastDateByEmpleadoAndEstado(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta, @Param("estado") EstadoAsistencia estado);
}

package ar.buro.personal.pagos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoDiarioRepository extends JpaRepository<PagoDiario, Long> {

    @Query("SELECT p FROM PagoDiario p JOIN FETCH p.empleado WHERE p.fecha BETWEEN :desde AND :hasta ORDER BY p.fecha DESC, p.empleado.apellido")
    List<PagoDiario> findByFechaBetweenWithEmpleado(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    @Query("SELECT p FROM PagoDiario p JOIN FETCH p.empleado WHERE p.pagado = false ORDER BY p.fecha DESC")
    List<PagoDiario> findPendientesWithEmpleado();

    @Query("SELECT p FROM PagoDiario p JOIN FETCH p.empleado WHERE p.empleado.id = :empleadoId AND p.fecha BETWEEN :desde AND :hasta")
    List<PagoDiario> findByEmpleadoAndFechaBetween(
            @Param("empleadoId") Long empleadoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    List<PagoDiario> findByTurnoNocheId(Long turnoNocheId);

    @Query("SELECT p FROM PagoDiario p WHERE p.empleado.id = :empleadoId AND p.pagado = false")
    List<PagoDiario> findPendientesByEmpleado(@Param("empleadoId") Long empleadoId);

    @Query("SELECT p FROM PagoDiario p WHERE p.empleado.id = :empleadoId AND p.fecha = :fecha")
    List<PagoDiario> findByEmpleadoIdAndFecha(@Param("empleadoId") Long empleadoId, @Param("fecha") LocalDate fecha);

    // Dashboard queries
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoDiario p WHERE p.fecha BETWEEN :desde AND :hasta")
    java.math.BigDecimal sumTotalByFechaBetween(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoDiario p WHERE p.fecha BETWEEN :desde AND :hasta AND p.pagado = true")
    java.math.BigDecimal sumPagadoByFechaBetween(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoDiario p WHERE p.fecha BETWEEN :desde AND :hasta AND p.pagado = false")
    java.math.BigDecimal sumPendienteByFechaBetween(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT p.empleado.id, COALESCE(SUM(p.monto), 0), COUNT(p) FROM PagoDiario p " +
           "WHERE p.fecha BETWEEN :desde AND :hasta GROUP BY p.empleado.id")
    List<Object[]> sumMontoByEmpleado(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}

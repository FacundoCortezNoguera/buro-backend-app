package ar.buro.personal.reportes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByTipoOrderByFechaReporteDesc(TipoReporte tipo);

    List<Reporte> findAllByOrderByCreatedAtDesc();

    Optional<Reporte> findByTipoAndFechaReporte(TipoReporte tipo, LocalDate fechaReporte);

    @Query("SELECT r FROM Reporte r WHERE r.fechaReporte BETWEEN :desde AND :hasta ORDER BY r.fechaReporte DESC")
    List<Reporte> findByFechaReporteBetween(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT r FROM Reporte r WHERE r.tipo = :tipo AND r.fechaReporte BETWEEN :desde AND :hasta ORDER BY r.fechaReporte DESC")
    List<Reporte> findByTipoAndFechaReporteBetween(
            @Param("tipo") TipoReporte tipo,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("SELECT COALESCE(SUM(r.totalMonto), 0) FROM Reporte r WHERE r.tipo = :tipo AND r.fechaReporte BETWEEN :desde AND :hasta")
    java.math.BigDecimal sumTotalMontoByTipoAndPeriodo(
            @Param("tipo") TipoReporte tipo,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );
}

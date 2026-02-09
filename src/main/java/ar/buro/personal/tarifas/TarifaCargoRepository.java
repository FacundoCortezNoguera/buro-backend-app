package ar.buro.personal.tarifas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifaCargoRepository extends JpaRepository<TarifaCargo, Long> {
    Optional<TarifaCargo> findByCargo(String cargo);
    boolean existsByCargo(String cargo);
}

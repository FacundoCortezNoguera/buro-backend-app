package ar.buro.personal.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionRepository extends JpaRepository<ConfiguracionSistema, Long> {

    Optional<ConfiguracionSistema> findByClave(String clave);

    boolean existsByClave(String clave);
}

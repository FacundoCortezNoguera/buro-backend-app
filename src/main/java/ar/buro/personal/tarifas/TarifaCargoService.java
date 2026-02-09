package ar.buro.personal.tarifas;

import ar.buro.personal.empleados.TipoPago;
import ar.buro.personal.tarifas.dto.TarifaCargoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TarifaCargoService {

    private final TarifaCargoRepository tarifaCargoRepository;

    public TarifaCargoService(TarifaCargoRepository tarifaCargoRepository) {
        this.tarifaCargoRepository = tarifaCargoRepository;
    }

    public List<TarifaCargoDTO> findAll() {
        return tarifaCargoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public TarifaCargoDTO findById(Long id) {
        return tarifaCargoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada: " + id));
    }

    public TarifaCargoDTO findByCargo(String cargo) {
        return tarifaCargoRepository.findByCargo(cargo)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada para cargo: " + cargo));
    }

    @Transactional
    public TarifaCargoDTO update(Long id, TarifaCargoDTO dto) {
        TarifaCargo tarifa = tarifaCargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada: " + id));

        if (dto.getCargo() != null) {
            tarifa.setCargo(dto.getCargo());
        }
        if (dto.getTipoPago() != null) {
            tarifa.setTipoPago(TipoPago.valueOf(dto.getTipoPago()));
        }
        tarifa.setMontoPorHora(dto.getMontoPorHora());
        tarifa.setMontoPorDia(dto.getMontoPorDia());

        return toDTO(tarifaCargoRepository.save(tarifa));
    }

    @Transactional
    public TarifaCargoDTO create(TarifaCargoDTO dto) {
        if (tarifaCargoRepository.existsByCargo(dto.getCargo())) {
            throw new RuntimeException("Ya existe una tarifa para el cargo: " + dto.getCargo());
        }

        TarifaCargo tarifa = new TarifaCargo(
                dto.getCargo(),
                TipoPago.valueOf(dto.getTipoPago()),
                dto.getMontoPorHora(),
                dto.getMontoPorDia()
        );

        return toDTO(tarifaCargoRepository.save(tarifa));
    }

    @Transactional
    public void delete(Long id) {
        tarifaCargoRepository.deleteById(id);
    }

    private TarifaCargoDTO toDTO(TarifaCargo tarifa) {
        return new TarifaCargoDTO(
                tarifa.getId(),
                tarifa.getCargo(),
                tarifa.getTipoPago().name(),
                tarifa.getMontoPorHora(),
                tarifa.getMontoPorDia()
        );
    }
}

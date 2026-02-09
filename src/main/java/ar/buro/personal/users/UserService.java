package ar.buro.personal.users;

import ar.buro.personal.users.dto.UsuarioCreateDTO;
import ar.buro.personal.users.dto.UsuarioDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public UsuarioDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    @Transactional
    public UsuarioDTO create(UsuarioCreateDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con ese username: " + dto.getUsername());
        }

        Role role = roleRepository.findByCode(dto.getRole())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + dto.getRole()));

        User user = new User(
                dto.getUsername(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getNombre(),
                role
        );

        return toDTO(userRepository.save(user));
    }

    @Transactional
    public UsuarioDTO toggleEnabled(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        user.setEnabled(!user.getEnabled());
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public UsuarioDTO update(Long id, UsuarioDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        if (dto.getNombre() != null) {
            user.setNombre(dto.getNombre());
        }

        if (dto.getRole() != null) {
            Role role = roleRepository.findByCode(dto.getRole())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + dto.getRole()));
            user.setRole(role);
        }

        return toDTO(userRepository.save(user));
    }

    private UsuarioDTO toDTO(User user) {
        return new UsuarioDTO(
                user.getId(),
                user.getUsername(),
                user.getNombre(),
                user.getRole().getCode(),
                user.getEnabled()
        );
    }
}

package ar.buro.personal.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET /api/debug/users - Lista todos los usuarios
    @GetMapping
    public List<Map<String, Object>> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "nombre", u.getNombre(),
                        "role", u.getRole().getCode(),
                        "enabled", u.getEnabled(),
                        "passwordHash", u.getPasswordHash()
                ))
                .toList();
    }

    // POST /api/debug/users/{username}/reset-password
    // Body: { "newPassword": "admin123" }
    @PostMapping("/{username}/reset-password")
    public Map<String, String> resetPassword(
            @PathVariable String username,
            @RequestBody Map<String, String> body) {

        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isBlank()) {
            return Map.of("error", "newPassword is required");
        }

        return userRepository.findByUsername(username)
                .map(user -> {
                    String hash = passwordEncoder.encode(newPassword);
                    user.setPasswordHash(hash);
                    userRepository.save(user);
                    return Map.of(
                            "message", "Password updated for " + username,
                            "newHash", hash
                    );
                })
                .orElse(Map.of("error", "User not found: " + username));
    }
}

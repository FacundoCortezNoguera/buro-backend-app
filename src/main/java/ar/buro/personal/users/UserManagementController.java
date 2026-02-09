package ar.buro.personal.users;

import ar.buro.personal.users.dto.ResetPasswordDTO;
import ar.buro.personal.users.dto.UsuarioCreateDTO;
import ar.buro.personal.users.dto.UsuarioDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UsuarioDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UsuarioDTO findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@Valid @RequestBody UsuarioCreateDTO dto) {
        return ResponseEntity.ok(userService.create(dto));
    }

    @PutMapping("/{id}")
    public UsuarioDTO update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return userService.update(id, dto);
    }

    @PatchMapping("/{id}/toggle-enabled")
    public UsuarioDTO toggleEnabled(@PathVariable Long id) {
        return userService.toggleEnabled(id);
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(id, dto.getNewPassword());
        return ResponseEntity.ok().build();
    }
}

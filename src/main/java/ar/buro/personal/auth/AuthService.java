package ar.buro.personal.auth;

import ar.buro.personal.auth.dto.LoginRequestDTO;
import ar.buro.personal.auth.dto.LoginResponseDTO;
import ar.buro.personal.config.security.JwtService;
import ar.buro.personal.users.User;
import ar.buro.personal.users.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long expirationMs;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${jwt.expiration-ms:28800000}") long expirationMs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.expirationMs = expirationMs;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!user.getEnabled()) {
            throw new BadCredentialsException("Usuario deshabilitado");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String token = jwtService.generate(user);

        return LoginResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .nombre(user.getNombre())
                .role(user.getRole().getCode())
                .expiresIn(expirationMs)
                .build();
    }
}

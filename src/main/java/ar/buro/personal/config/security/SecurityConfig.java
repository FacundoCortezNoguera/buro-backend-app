package ar.buro.personal.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/debug/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Cambios de empleado - aprobación pública via token
                .requestMatchers("/api/cambios/aprobar/**").permitAll()
                .requestMatchers("/api/cambios/rechazar/**").permitAll()

                // Cámara Anviz - usa API Key propia
                .requestMatchers("/api/asistencias/camara").permitAll()

                // Empleados endpoints
                .requestMatchers(HttpMethod.GET, "/api/empleados/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/empleados").hasAnyRole("ADMIN", "RRHH")
                .requestMatchers(HttpMethod.PUT, "/api/empleados/**").hasAnyRole("ADMIN", "RRHH")
                .requestMatchers(HttpMethod.PATCH, "/api/empleados/**").hasAnyRole("ADMIN", "RRHH")
                .requestMatchers(HttpMethod.DELETE, "/api/empleados/**").hasRole("ADMIN")

                // Turnos endpoints
                .requestMatchers(HttpMethod.GET, "/api/turnos-noche/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/turnos-noche/**").hasAnyRole("ADMIN", "RRHH", "SUPERVISOR")

                // Pagos endpoints
                .requestMatchers(HttpMethod.GET, "/api/pagos-diarios/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/pagos-diarios/*/marcar-pagado").hasAnyRole("ADMIN", "CAJA")
                .requestMatchers(HttpMethod.POST, "/api/pagos-diarios/calcular-salario").hasAnyRole("ADMIN", "RRHH")

                // Cierre noche
                .requestMatchers(HttpMethod.POST, "/api/cierre-noche/**").hasAnyRole("ADMIN", "SUPERVISOR")

                // Tarifas endpoints
                .requestMatchers(HttpMethod.GET, "/api/tarifas/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/tarifas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/tarifas/**").hasRole("ADMIN")

                // Usuarios management endpoints
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // Email endpoints
                .requestMatchers(HttpMethod.GET, "/api/email/status").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/email/**").hasAnyRole("ADMIN", "SUPERVISOR")

                // Dashboard endpoints
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "DUENO")

                // Default: require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

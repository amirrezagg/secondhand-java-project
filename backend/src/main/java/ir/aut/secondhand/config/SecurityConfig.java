package ir.aut.secondhand.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ir.aut.secondhand.security.JwtRequestFilter;

/**
 * Security configuration for the secondhand application.
 * Configures JWT-based stateless authentication, CSRF protection, and authorization rules.
 * Defines public access to authentication endpoints and advertisements, restricts admin endpoints,
 * and requires authentication for all other requests.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain with JWT authentication, session management, and authorization rules.
     * <p>
     * Security settings:
     * <ul>
     *   <li>CSRF protection disabled (appropriate for stateless API)</li>
     *   <li>Stateless session management (no server-side session storage)</li>
     *   <li>JWT filter registered to authenticate requests</li>
     *   <li>Public access to registration, login, and error endpoints</li>
     *   <li>Public read-only access to advertisements</li>
     *   <li>Admin-only access to /api/admin endpoints</li>
     *   <li>All other requests require authentication</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless API
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register", "/api/users/login", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/advertisements/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        // Register JWT filter before the standard authentication filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

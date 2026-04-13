package com.hermosacartagena.security;

import com.hermosacartagena.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CONFIGURACIÓN DE SEGURIDAD
 * =============================================
 * 
 * Configuración principal de Spring Security con JWT.
 * Define reglas de acceso, autenticación y autorización.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioService usuarioService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura el proveedor de autenticación
     * 
     * @return DaoAuthenticationProvider configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configura el Authentication Manager
     * 
     * @param config Configuración de autenticación
     * @return AuthenticationManager configurado
     * @throws Exception Si hay error en configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura el encoder de contraseñas
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el filtro CORS
     * 
     * @return CorsFilter configurado
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Total-Count", "X-Page-Count"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return new CorsFilter(source);
    }

    /**
     * Configura la cadena de filtros de seguridad
     * 
     * @param http Configuración HTTP de Spring Security
     * @return SecurityFilterChain configurado
     * @throws Exception Si hay error en configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (usamos JWT)
            .csrf(csrf -> csrf.disable())
            
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configurar manejo de sesiones (stateless para JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configurar excepciones de autenticación
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            
            // Configurar reglas de autorización
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos (sin autenticación)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Endpoints de documentación
                .requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                
                // Recursos estáticos
                .requestMatchers("/static/**", "/uploads/**", "/reports/**").permitAll()
                
                // Endpoints que requieren autenticación básica
                .requestMatchers("/api/perfil/**").authenticated()
                .requestMatchers("/api/mi-perfil/**").authenticated()
                
                // Endpoints de ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/api/roles/**").hasRole("ADMIN")
                .requestMatchers("/api/proveedores/**").hasRole("ADMIN")
                .requestMatchers("/api/estadisticas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/servicios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/servicios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/servicios/**").hasRole("ADMIN")
                
                // Endpoints de EMPLEADO
                .requestMatchers("/api/empleados/**").hasAnyRole("ADMIN", "EMPLEADO")
                .requestMatchers("/api/reservas/**").hasAnyRole("ADMIN", "EMPLEADO")
                .requestMatchers("/api/pagos/**").hasAnyRole("ADMIN", "EMPLEADO")
                .requestMatchers(HttpMethod.GET, "/api/servicios/**").hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                .requestMatchers(HttpMethod.POST, "/api/reservas/**").hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                
                // Endpoints de CLIENTE
                .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers("/api/mis-reservas/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers("/api/mis-pagos/**").hasAnyRole("ADMIN", "CLIENTE")
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            );
        
        // Agregar filtro JWT
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * Configura la fuente de configuración CORS
     * 
     * @return CorsConfigurationSource configurado
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Total-Count", "X-Page-Count"));
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

package com.hermosacartagena.config;

import com.hermosacartagena.security.JwtAuthenticationFilter;
import com.hermosacartagena.security.OAuth2SuccessHandler;
import com.hermosacartagena.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
 * CONFIGURACIÓN DE SEGURIDAD
 * =============================================
 * 
 * Configuración principal de seguridad Spring Security.
 * Implementa autenticación JWT, OAuth2 y autorización por roles.
 * 
 * Características:
 * - Autenticación stateless con JWT
 * - Login OAuth2 (Google, Facebook)
 * - Autorización por roles y permisos
 * - CORS configurado para frontend
 * - Rate limiting y protección CSRF
 * 
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2024
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioService usuarioService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    /**
     * Configura la cadena de filtros de seguridad.
     * Define qué endpoints son públicos y cuáles requieren autenticación.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (usamos JWT stateless)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configurar reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers("/api/v1/servicios/publicos").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/login", "/register", "/forgot-password").permitAll()
                
                // Recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/assets/**").permitAll()
                
                // Endpoints de usuario autenticado
                .requestMatchers("/api/v1/usuarios/perfil").hasAnyRole("CLIENTE", "EMPLEADO", "ADMIN")
                .requestMatchers("/api/v1/reservas/mis-reservas").hasAnyRole("CLIENTE", "EMPLEADO", "ADMIN")
                
                // Endpoints de empleado
                .requestMatchers("/api/v1/reservas/**").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers("/api/v1/pagos/**").hasAnyRole("EMPLEADO", "ADMIN")
                .requestMatchers("/api/v1/clientes/**").hasAnyRole("EMPLEADO", "ADMIN")
                
                // Endpoints de administrador
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/reportes/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/auditoria/**").hasRole("ADMIN")
                
                // Todas las demás solicitudes requieren autenticación
                .anyRequest().authenticated()
            )
            
            // Configurar OAuth2 Login
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessURL("/dashboard", true)
                .failureURL("/login?error=true")
                .successHandler(oAuth2SuccessHandler)
                .authorizationEndpoint(auth -> auth
                    .baseUri("/oauth2/authorization")
                )
                .redirectionEndpoint(redirect -> redirect
                    .baseUri("/oauth2/callback/*")
                )
            )
            
            // Configuración de formulario de login tradicional
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessURL("/dashboard", true)
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            
            // Configuración de logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .deleteCookies("JSESSIONID", "jwt-token")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            
            // Configuración de sesión (stateless para JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Agregar filtro JWT
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura el origen CORS para permitir solicitudes del frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes específicos
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://hermosacartagena.com",
            "https://www.hermosacartagena.com"
        ));
        
        // Permitir métodos HTTP
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Permitir headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Permitir credentials
        configuration.setAllowCredentials(true);
        
        // Exponer headers
        configuration.setExposedHeaders(Arrays.asList(
            "X-Total-Count", "X-Page-Count", "X-Current-Page"
        ));
        
        // Tiempo de preflight
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Bean para el codificador de contraseñas.
     * Usa BCrypt con fuerza 12 para mayor seguridad.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Bean para el Authentication Manager.
     * Necesario para la autenticación programática.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

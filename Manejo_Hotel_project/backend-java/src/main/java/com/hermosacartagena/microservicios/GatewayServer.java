package com.hermosacartagena.microservicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * SERVIDOR GATEWAY (API GATEWAY)
 * =============================================
 * 
 * Gateway principal para enrutar peticiones a los microservicios.
 * Implementa balanceo de carga, seguridad y monitoreo.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableWebSecurity
public class GatewayServer {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServer.class, args);
    }

    /**
     * Configuración de rutas del Gateway
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Rutas al microservicio de autenticación
            .route("auth-service", r -> r.path("/api/auth/**")
                .uri("lb://auth-service")
                .filters(f -> f.filter(JwtAuthenticationFilter.class)))
            
            // Rutas al microservicio de usuarios
            .route("usuarios-service", r -> r.path("/api/usuarios/**")
                .uri("lb://usuarios-service")
                .filters(f -> f.filter(JwtAuthenticationFilter.class)
                    .circuitBreaker(config -> config.setName("usuariosCB")))))
            
            // Rutas al microservicio de servicios
            .route("servicios-service", r -> r.path("/api/servicios/**")
                .uri("lb://servicios-service")
                .filters(f -> f.filter(JwtAuthenticationFilter.class)
                    .circuitBreaker(config -> config.setName("serviciosCB")))))
            
            // Rutas al microservicio de reservas
            .route("reservas-service", r -> r.path("/api/reservas/**")
                .uri("lb://reservas-service")
                .filters(f -> f.filter(JwtAuthenticationFilter.class)
                    .circuitBreaker(config -> config.setName("reservasCB")))))
            
            // Rutas al microservicio de pagos
            .route("pagos-service", r -> r.path("/api/pagos/**")
                .uri("lb://pagos-service")
                .filters(f -> f.filter(JwtAuthenticationFilter.class)
                    .circuitBreaker(config -> config.setName("pagosCB")))))
            
            // Rutas al microservicio de reportes
            .route("reportes-service", r -> r.path("/api/reportes/**")
                .uri("lb://reportes-service")
                .filters(f -> f.filter(JwtAuthenticationFilter.class)
                    .circuitBreaker(config -> config.setName("reportesCB")))))
            
            // Rutas de salud y monitoreo (públicas)
            .route("health-check", r -> r.path("/health/**")
                .uri("lb://gateway"))
            
            .route("actuator", r -> r.path("/actuator/**")
                .uri("lb://gateway"))
            
            // Redirección Swagger UI
            .route("swagger", r -> r.path("/swagger-ui/**")
                .uri("lb://gateway"))
            
            .build();
    }

    /**
     * Configuración de seguridad del Gateway
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Permitir rutas públicas
                .requestMatchers("/health/**", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Rutas de autenticación públicas
                .requestMatchers("/api/auth/**").permitAll()
                // Todas las demás requieren autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

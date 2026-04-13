package com.hermosacartagena.microservicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * SERVIDOR DE DESCUBRIMIENTO DE SERVICIOS (EUREKA)
 * =============================================
 * 
 * Servidor Eureka para descubrimiento de microservicios.
 * Permite que los microservicios se registren y se descubran entre sí.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableEurekaServer
@EnableWebSecurity
public class ServicioDiscoveryServer {

    public static void main(String[] args) {
        SpringApplication.run(ServicioDiscoveryServer.class, args);
    }

    /**
     * Configuración de seguridad para el servidor Eureka
     * Permite acceso sin autenticación para el dashboard
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            );
        return http.build();
    }
}

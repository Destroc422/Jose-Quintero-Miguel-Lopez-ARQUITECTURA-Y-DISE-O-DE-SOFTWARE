package com.hermosacartagena.microservicios.servicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * MICROSERVICIO DE SERVICIOS TURÍSTICOS
 * =============================================
 * 
 * Microservicio especializado en gestión de servicios turísticos.
 * Implementa comunicación con otros microservicios vía REST.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServicioMicroservicioApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicioMicroservicioApplication.class, args);
    }

    /**
     * Bean para comunicación REST entre microservicios
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

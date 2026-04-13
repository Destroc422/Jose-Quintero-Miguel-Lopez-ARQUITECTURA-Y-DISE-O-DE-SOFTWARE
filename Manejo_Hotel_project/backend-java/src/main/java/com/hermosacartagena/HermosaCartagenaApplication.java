package com.hermosacartagena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CLASE PRINCIPAL DE LA APLICACIÓN
 * =============================================
 * 
 * Esta es la clase principal que inicia la aplicación Spring Boot.
 * Configura los aspectos principales del sistema incluyendo:
 * - Escaneo de componentes
 * - Habilitación de caché
 * - Procesamiento asíncrono
 * - Programación de tareas
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class HermosaCartagenaApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(HermosaCartagenaApplication.class, args);
    }
    
}

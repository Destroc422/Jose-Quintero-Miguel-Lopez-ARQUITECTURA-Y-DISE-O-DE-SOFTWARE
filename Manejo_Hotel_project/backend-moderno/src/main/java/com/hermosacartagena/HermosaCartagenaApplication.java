package com.hermosacartagena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
 * APLICACIÓN PRINCIPAL
 * =============================================
 * 
 * Clase principal de inicio de la aplicación Spring Boot.
 * Configura y habilita las funcionalidades principales del sistema.
 * 
 * Características habilitadas:
 * - Spring Boot con configuración automática
 * - Caching con Redis para rendimiento
 * - Procesamiento asíncrono para operaciones pesadas
 * - Programación de tareas automáticas
 * 
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2024
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class HermosaCartagenaApplication {

    /**
     * Método principal de inicio de la aplicación.
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(HermosaCartagenaApplication.class, args);
        System.out.println("\n" +
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║           🌴 HERMOSA CARTAGENA - SISTEMA TURÍSTICO 🌴         ║\n" +
            "║                                                              ║\n" +
            "║  Versión: 3.0.0 - Arquitectura Moderna Unificada             ║\n" +
            "║  Status: 🟢 Sistema operativo y listo para uso              ║\n" +
            "║                                                              ║\n" +
            "║  🚀 Características principales:                              ║\n" +
            "║     • Spring Boot 3.2 + Spring Security                     ║\n" +
            "║     • APIs REST con JSON estándar                           ║\n" +
            "║     • Autenticación OAuth2 + JWT                           ║\n" +
            "║     • Caching Redis para alto rendimiento                  ║\n" +
            "║     • Base de datos MySQL 8.0 optimizada                    ║\n" +
            "║                                                              ║\n" +
            "║  📊 APIs disponibles en: http://localhost:8080               ║\n" +
            "║  📚 Documentación: http://localhost:8080/swagger-ui.html    ║\n" +
            "║                                                              ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n"
        );
    }
}

package com.hermosacartagena.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CONFIGURACIÓN DE BASE DE DATOS
 * =============================================
 * 
 * Esta clase configura todos los aspectos relacionados con la base de datos:
 * - DataSource optimizado con HikariCP
 * - Auditoría automática de entidades
 * - Configuración de transacciones
 * - Configuración de repositorios JPA
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.hermosacartagena.repository")
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
public class DatabaseConfig {

    /**
     * Configura el DataSource principal con HikariCP
     * Optimizado para alto rendimiento y manejo de conexiones
     * 
     * @return DataSource configurado con HikariCP
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource dataSource() {
        return new HikariDataSource();
    }

    /**
     * Configura el auditor para JPA Auditing
     * Permite registrar automáticamente quién crea/modifica las entidades
     * 
     * @return AuditorAware implementación
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * Implementación de AuditorAware para JPA Auditing
     * Obtiene el usuario actual del contexto de seguridad
     */
    public static class AuditorAwareImpl implements AuditorAware<String> {

        /**
         * Retorna el nombre de usuario actual para auditoría
         * 
         * @return Optional con el nombre del usuario actual
         */
        @Override
        public Optional<String> getCurrentAuditor() {
            // Por ahora retornamos "system" como auditoría por defecto
            // En la implementación con Spring Security, obtendremos el usuario del contexto
            return Optional.of("system");
        }
    }
}

package com.hermosacartagena.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CONFIGURACIÓN WEB MVC
 * =============================================
 * 
 * Esta clase configura aspectos importantes del MVC de Spring:
 * - CORS para permitir peticiones desde el frontend
 * - Manejo de recursos estáticos
 * - Configuración de content negotiation
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura CORS (Cross-Origin Resource Sharing)
     * Permite peticiones desde el frontend en diferentes dominios
     * 
     * @param registry Registro de configuración CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:3000",    // React development
                    "http://localhost:8080",    // Backend
                    "http://localhost:4200",    // Angular development
                    "http://127.0.0.1:5500",   // Live Server
                    "https://hermosacartagena.com" // Producción
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "X-Total-Count", "X-Page-Count")
                .allowCredentials(true)
                .maxAge(3600);
        
        // Configuración para Swagger/OpenAPI
        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        
        registry.addMapping("/api-docs/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    /**
     * Configura el manejo de recursos estáticos
     * Permite servir archivos como imágenes, documentos, etc.
     * 
     * @param registry Registro de manejadores de recursos
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Recursos estáticos del frontend
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // Archivos subidos
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        
        // Reportes generados
        registry.addResourceHandler("/reports/**")
                .addResourceLocations("file:reports/");
        
        // Swagger UI
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/");
        
        // Favicon y otros recursos comunes
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/");
        
        registry.addResourceHandler("/robots.txt")
                .addResourceLocations("classpath:/static/");
    }
}

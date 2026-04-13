package com.hermosacartagena.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * PUNTO DE ENTRADA DE AUTENTICACIÓN JWT
 * =============================================
 * 
 * Maneja respuestas de error para autenticación no autorizada.
 * Retorna respuestas JSON consistentes para errores de autenticación.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Maneja el inicio de autenticación fallida
     * 
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param authException Excepción de autenticación
     * @throws IOException Si hay error de I/O
     * @throws ServletException Si hay error de servlet
     */
    @Override
    public void commence(HttpServletRequest request, 
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        log.error("Error de autenticación no autorizada: {}", authException.getMessage());
        
        // Configurar respuesta
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Crear cuerpo de respuesta
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "No está autorizado para acceder a este recurso. Por favor, inicie sesión.");
        body.put("path", request.getServletPath());
        body.put("code", "AUTH_001");
        
        // Escribir respuesta
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}

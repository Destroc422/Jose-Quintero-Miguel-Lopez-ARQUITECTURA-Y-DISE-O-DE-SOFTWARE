package com.hermosacartagena.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
 * DTO DE SOLICITUD DE LOGIN
 * =============================================
 * 
 * DTO para solicitudes de autenticación.
 * Reemplaza completamente el protocolo XML por JSON estándar.
 * 
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2024
 */
@Data
@Schema(description = "Solicitud de autenticación de usuario")
public class LoginRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario", example = "jquintero", required = true)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    private String password;

    @Schema(description = "Recordar sesión", example = "true")
    private boolean rememberMe = false;

    @Schema(description = "Token CSRF para protección", example = "abc123def456")
    private String csrfToken;
}

package com.hermosacartagena.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
 * DTO DE RESPUESTA DE LOGIN
 * =============================================
 * 
 * DTO para respuestas de autenticación exitosa.
 * Reemplaza el complejo protocolo XML por JSON simple y estándar.
 * 
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación exitosa")
public class LoginResponse {

    @Schema(description = "Token JWT de acceso", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Token de refresco", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Tiempo de expiración en segundos", example = "3600")
    private Long expiresIn;

    @Schema(description = "Fecha y hora de expiración")
    private LocalDateTime expiresAt;

    @Schema(description = "Información del usuario autenticado")
    private UsuarioInfo usuario;

    @Schema(description = "Permisos del usuario")
    private List<String> permisos;

    @Schema(description = "Mensaje de bienvenida", example = "¡Bienvenido al sistema!")
    private String message;

    @Schema(description = "Indica si es primer login", example = "false")
    private boolean primerLogin = false;

    @Schema(description = "URL de redirección post-login", example = "/dashboard")
    private String redirectUrl;

    /**
     * Clase interna para información del usuario.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Información básica del usuario")
    public static class UsuarioInfo {

        @Schema(description = "ID del usuario", example = "123")
        private Long id;

        @Schema(description = "Nombre de usuario", example = "jquintero")
        private String username;

        @Schema(description = "Nombre completo", example = "Jose Quintero")
        private String nombreCompleto;

        @Schema(description = "Email del usuario", example = "jquintero@hermosacartagena.com")
        private String email;

        @Schema(description = "Rol del usuario", example = "ADMIN")
        private String rol;

        @Schema(description = "URL del avatar", example = "/images/avatars/user123.jpg")
        private String avatar;

        @Schema(description = "Teléfono del usuario", example = "+57 5 660 0000")
        private String telefono;

        @Schema(description = "Último login", example = "2024-04-27T10:30:00")
        private LocalDateTime ultimoLogin;
    }
}

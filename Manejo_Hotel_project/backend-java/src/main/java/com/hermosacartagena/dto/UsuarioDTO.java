package com.hermosacartagena.dto;

import com.hermosacartagena.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * DTO USUARIO
 * =============================================
 * 
 * Data Transfer Object para la entidad Usuario.
 * Se utiliza para transferencia de datos entre capas y validación.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long idUsuario;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
    private String usuario;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre completo debe tener entre 3 y 255 caracteres")
    private String nombreCompleto;

    @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres")
    private String telefono;

    @Size(max = 500, message = "La dirección no debe exceder 500 caracteres")
    private String direccion;

    private Long idRol;
    private String nombreRol;

    private LocalDateTime fechaUltimoAcceso;
    private Integer intentosFallidos;
    private LocalDateTime cuentaBloqueadaHasta;
    private String estado;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Constructor para creación de usuario
     */
    public UsuarioDTO(String usuario, String email, String password, String nombreCompleto, Long idRol) {
        this.usuario = usuario;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.idRol = idRol;
    }

    /**
     * Constructor para actualización (sin contraseña)
     */
    public UsuarioDTO(Long idUsuario, String usuario, String email, String nombreCompleto, Long idRol) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.idRol = idRol;
    }

    /**
     * Verifica si el usuario está activo
     * 
     * @return true si está activo
     */
    public boolean estaActivo() {
        return "activo".equals(this.estado);
    }

    /**
     * Verifica si la cuenta está bloqueada
     * 
     * @return true si está bloqueada
     */
    public boolean estaBloqueado() {
        return cuentaBloqueadaHasta != null && cuentaBloqueadaHasta.isAfter(LocalDateTime.now());
    }

    /**
     * Obtiene información resumida del usuario
     * 
     * @return String con información básica
     */
    public String getInformacionResumida() {
        return String.format("%s (%s) - %s", nombreCompleto, usuario, nombreRol);
    }

    /**
     * DTO para respuesta de login
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private String refreshToken;
        private Long expiresIn;
        private UsuarioDTO usuario;
        private String mensaje;
    }

    /**
     * DTO para cambio de contraseña
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CambioPassword {
        @NotBlank(message = "La contraseña actual es obligatoria")
        private String passwordActual;

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 6, max = 255, message = "La nueva contraseña debe tener entre 6 y 255 caracteres")
        private String passwordNuevo;

        @NotBlank(message = "La confirmación de contraseña es obligatoria")
        private String passwordConfirmacion;
    }

    /**
     * DTO para reset de contraseña
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPassword {
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        private String email;
    }

    /**
     * DTO para actualización de perfil
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActualizarPerfil {
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 255, message = "El nombre completo debe tener entre 3 y 255 caracteres")
        private String nombreCompleto;

        @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres")
        private String telefono;

        @Size(max = 500, message = "La dirección no debe exceder 500 caracteres")
        private String direccion;
    }

    /**
     * Convierte el DTO a entidad Usuario
     * 
     * @return Entidad Usuario
     */
    public Usuario toEntity() {
        return Usuario.builder()
                .idUsuario(idUsuario)
                .usuario(usuario)
                .password(password)
                .nombreCompleto(nombreCompleto)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .estado(estado)
                .intentosFallidos(intentosFallidos)
                .build();
    }
}

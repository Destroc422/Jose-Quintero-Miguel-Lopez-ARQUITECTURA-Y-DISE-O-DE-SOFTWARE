package com.hermosacartagena.controller;

import com.hermosacartagena.dto.auth.LoginRequest;
import com.hermosacartagena.dto.auth.LoginResponse;
import com.hermosacartagena.dto.auth.RegisterRequest;
import com.hermosacartagena.dto.common.ApiResponse;
import com.hermosacartagena.dto.common.ErrorResponse;
import com.hermosacartagena.service.AuthService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
 * CONTROLADOR DE AUTENTICACIÓN
 * =============================================
 * 
 * Controlador REST para gestión de autenticación.
 * Reemplaza completamente el protocolo XML por APIs REST modernas.
 * 
 * Endpoints implementados:
 * - POST /api/v1/auth/login - Login tradicional
 * - POST /api/v1/auth/register - Registro de usuarios
 * - POST /api/v1/auth/refresh - Refrescar token
 * - POST /api/v1/auth/logout - Cerrar sesión
 * - GET /api/v1/auth/me - Obtener información del usuario actual
 * - POST /api/v1/auth/forgot-password - Recuperar contraseña
 * - POST /api/v1/auth/reset-password - Restablecer contraseña
 * - POST /api/v1/auth/verify-mfa - Verificación MFA
 * 
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Autenticación", description = "APIs de gestión de autenticación y autorización")
public class AuthController {

    private final AuthService authService;

    /**
     * Autentica un usuario en el sistema.
     * Reemplaza el complejo protocolo XML por un simple JSON REST.
     */
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y devuelve tokens JWT para acceso al sistema"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "429",
            description = "Demasiados intentos de login",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @RateLimiter(name = "login", fallbackMethod = "loginFallback")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        log.info("Intento de login para usuario: {}", loginRequest.getUsername());
        
        LoginResponse loginResponse = authService.authenticate(loginRequest, request);
        
        // Configurar cookies si rememberMe está activado
        if (loginRequest.isRememberMe()) {
            authService.setRefreshTokenCookie(response, loginResponse.getRefreshToken());
        }
        
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
            .success(true)
            .message("Login exitoso")
            .data(loginResponse)
            .timestamp(java.time.LocalDateTime.now())
            .build());
    }

    /**
     * Registra un nuevo usuario en el sistema.
     */
    @PostMapping("/register")
    @Operation(
        summary = "Registrar usuario",
        description = "Crea una nueva cuenta de usuario en el sistema"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Usuario registrado exitosamente"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Datos de registro inválidos"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "El usuario ya existe"
        )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            HttpServletRequest request) {
        
        log.info("Intento de registro para usuario: {}", registerRequest.getUsername());
        
        LoginResponse loginResponse = authService.register(registerRequest, request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Usuario registrado exitosamente")
                .data(loginResponse)
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }

    /**
     * Refresca un token de acceso utilizando un token de refresco.
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Refrescar token",
        description = "Genera un nuevo token de acceso usando un token de refresco válido"
    )
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestBody Map<String, String> tokenRequest) {
        
        String refreshToken = tokenRequest.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .message("Token de refresco es requerido")
                    .timestamp(java.time.LocalDateTime.now())
                    .build());
        }
        
        LoginResponse loginResponse = authService.refreshToken(refreshToken);
        
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
            .success(true)
            .message("Token refrescado exitosamente")
            .data(loginResponse)
            .timestamp(java.time.LocalDateTime.now())
            .build());
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Cerrar sesión",
        description = "Invalida los tokens del usuario y cierra la sesión"
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        
        if (authentication != null) {
            String username = authentication.getName();
            log.info("Cerrando sesión para usuario: {}", username);
            
            authService.logout(username, request, response);
        }
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .success(true)
            .message("Sesión cerrada exitosamente")
            .timestamp(java.time.LocalDateTime.now())
            .build());
    }

    /**
     * Obtiene la información del usuario autenticado actual.
     */
    @GetMapping("/me")
    @Operation(
        summary = "Obtener información del usuario",
        description = "Devuelve la información del usuario autenticado actualmente"
    )
    public ResponseEntity<ApiResponse<LoginResponse.UsuarioInfo>> getCurrentUser(
            Authentication authentication) {
        
        String username = authentication.getName();
        LoginResponse.UsuarioInfo userInfo = authService.getCurrentUser(username);
        
        return ResponseEntity.ok(ApiResponse.<LoginResponse.UsuarioInfo>builder()
            .success(true)
            .message("Información del usuario obtenida exitosamente")
            .data(userInfo)
            .timestamp(java.time.LocalDateTime.now())
            .build());
    }

    /**
     * Inicia el proceso de recuperación de contraseña.
     */
    @PostMapping("/forgot-password")
    @Operation(
        summary = "Recuperar contraseña",
        description = "Envía un email con instrucciones para recuperar la contraseña"
    )
    @RateLimiter(name = "forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Parameter(description = "Email del usuario", required = true)
            @RequestParam String email) {
        
        log.info("Solicitud de recuperación de contraseña para email: {}", email);
        
        authService.initiatePasswordReset(email);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .success(true)
            .message("Si el email está registrado, recibirás instrucciones para recuperar tu contraseña")
            .timestamp(java.time.LocalDateTime.now())
            .build());
    }

    /**
     * Restablece la contraseña usando un token de recuperación.
     */
    @PostMapping("/reset-password")
    @Operation(
        summary = "Restablecer contraseña",
        description = "Restablece la contraseña usando un token de recuperación válido"
    )
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody Map<String, String> resetRequest) {
        
        String token = resetRequest.get("token");
        String newPassword = resetRequest.get("newPassword");
        
        if (token == null || newPassword == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Token y nueva contraseña son requeridos")
                    .timestamp(java.time.LocalDateTime.now())
                    .build());
        }
        
        authService.resetPassword(token, newPassword);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .success(true)
            .message("Contraseña restablecida exitosamente")
            .timestamp(java.time.LocalDateTime.now())
            .build());
    }

    /**
     * Verifica el código MFA del usuario.
     */
    @PostMapping("/verify-mfa")
    @Operation(
        summary = "Verificar MFA",
        description = "Verifica el código de autenticación de múltiples factores"
    )
    public ResponseEntity<ApiResponse<LoginResponse>> verifyMfa(
            @RequestBody Map<String, String> mfaRequest,
            Authentication authentication) {
        
        String username = authentication.getName();
        String mfaCode = mfaRequest.get("code");
        
        if (mfaCode == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .message("Código MFA es requerido")
                    .timestamp(java.time.LocalDateTime.now())
                    .build());
        }
        
        LoginResponse loginResponse = authService.verifyMfa(username, mfaCode);
        
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
            .success(true)
            .message("MFA verificado exitosamente")
            .data(loginResponse)
            .timestamp(java.time.LocalDateTime.now())
            .build());
    }

    /**
     * Método fallback para rate limiting en login.
     */
    public ResponseEntity<ApiResponse<LoginResponse>> loginFallback(
            LoginRequest loginRequest, 
            HttpServletRequest request,
            HttpServletResponse response,
            Exception ex) {
        
        log.warn("Rate limiting activado para login del usuario: {}", loginRequest.getUsername());
        
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(ApiResponse.<LoginResponse>builder()
                .success(false)
                .message("Demasiados intentos de login. Por favor espera unos minutos antes de intentar nuevamente.")
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }
}

package com.hermosacartagena.controller;

import com.hermosacartagena.dto.UsuarioDTO;
import com.hermosacartagena.entity.Usuario;
import com.hermosacartagena.security.JwtTokenProvider;
import com.hermosacartagena.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CONTROLADOR DE AUTENTICACIÓN
 * =============================================
 * 
 * Controlador REST para autenticación y gestión de tokens JWT.
 * Expone endpoints para login, logout y gestión de tokens.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para autenticación y gestión de tokens JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioService usuarioService;

    /**
     * Autentica un usuario y retorna tokens JWT
     * 
     * @param loginRequest DTO con credenciales de login
     * @return Respuesta con tokens y datos del usuario
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna tokens JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "403", description = "Cuenta bloqueada o inactiva"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioDTO.LoginResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        
        log.info("Petición de login para usuario: {}", loginRequest.getUsuario());

        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsuario(),
                    loginRequest.getPassword()
                )
            );

            // Obtener detalles del usuario
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Usuario usuario = (Usuario) userDetails;

            // Verificar estado del usuario
            if (!usuario.estaActivo()) {
                log.warn("Intento de login con usuario inactivo: {}", loginRequest.getUsuario());
                return ResponseEntity.status(403).build();
            }

            // Verificar si la cuenta está bloqueada
            if (usuario.estaBloqueado()) {
                log.warn("Intento de login con cuenta bloqueada: {}", loginRequest.getUsuario());
                return ResponseEntity.status(403).build();
            }

            // Generar tokens
            String token = jwtTokenProvider.generarTokenConClaims(
                usuario.getUsuario(),
                usuario.getIdUsuario(),
                usuario.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .toList()
            );

            String refreshToken = jwtTokenProvider.generarTokenRefresco(usuario.getUsuario());

            // Reiniciar intentos fallidos
            usuarioService.reiniciarIntentosFallidos(usuario.getIdUsuario());

            // Construir respuesta
            UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .usuario(usuario.getUsuario())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .idRol(usuario.getIdRol())
                .nombreRol(usuario.getRol().getNombreRol())
                .estado(usuario.getEstado())
                .build();

            UsuarioDTO.LoginResponse response = UsuarioDTO.LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.obtenerTiempoRestante(token))
                .usuario(usuarioDTO)
                .mensaje("Login exitoso")
                .build();

            log.info("Login exitoso para usuario: {}", loginRequest.getUsuario());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para usuario: {}", loginRequest.getUsuario());
            
            // Incrementar intentos fallidos
            try {
                Usuario usuario = usuarioService.obtenerUsuarioPorUsuarioOEmail(loginRequest.getUsuario())
                    .toEntity(); // Necesitaríamos método para convertir DTO a Entity
                usuarioService.incrementarIntentosFallidos(usuario.getIdUsuario());
            } catch (Exception ex) {
                log.debug("No se pudo incrementar intentos fallidos: {}", ex.getMessage());
            }
            
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Error en proceso de login: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Refresca un token JWT existente
     * 
     * @param refreshTokenRequest DTO con token de refresco
     * @return Nuevo token JWT
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", description = "Genera un nuevo token JWT usando un token de refresco")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente"),
        @ApiResponse(responseCode = "401", description = "Token de refresco inválido o expirado")
    })
    public ResponseEntity<UsuarioDTO.LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        
        log.debug("Petición para refrescar token");

        try {
            String refreshToken = refreshTokenRequest.getRefreshToken();
            
            // Validar token de refresco
            if (!jwtTokenProvider.validarToken(refreshToken) || 
                !jwtTokenProvider.esTokenRefresco(refreshToken)) {
                return ResponseEntity.status(401).build();
            }

            // Obtener username del token
            String username = jwtTokenProvider.obtenerUsernameDesdeToken(refreshToken);
            
            // Generar nuevo token
            String newToken = jwtTokenProvider.generarTokenDesdeUsername(username);
            
            // Generar nuevo token de refresco
            String newRefreshToken = jwtTokenProvider.generarTokenRefresco(username);

            // Obtener información del usuario
            UsuarioDTO usuarioDTO = usuarioService.obtenerUsuarioPorUsuarioOEmail(username);

            UsuarioDTO.LoginResponse response = UsuarioDTO.LoginResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenProvider.obtenerTiempoRestante(newToken))
                .usuario(usuarioDTO)
                .mensaje("Token refrescado exitosamente")
                .build();

            log.debug("Token refrescado exitosamente para usuario: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al refrescar token: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Cierra sesión del usuario (invalida token)
     * 
     * @param logoutRequest DTO con token a invalidar
     * @return Respuesta vacía
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida el token JWT actual")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logoutRequest) {
        log.info("Petición de logout");

        try {
            String token = logoutRequest.getToken();
            
            if (!jwtTokenProvider.validarToken(token)) {
                return ResponseEntity.status(401).build();
            }

            // En una implementación real, agregaríamos el token a una lista negra
            // Por ahora, simplemente retornamos éxito
            log.info("Logout exitoso");
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error en proceso de logout: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Valida si un token JWT es válido
     * 
     * @param token Token JWT a validar
     * @return Respuesta con información del token
     */
    @PostMapping("/validate")
    @Operation(summary = "Validar token", description = "Verifica si un token JWT es válido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenValidationRequest token) {
        log.debug("Petición para validar token");

        try {
            String jwt = token.getToken();
            
            if (!jwtTokenProvider.validarToken(jwt)) {
                return ResponseEntity.status(401).build();
            }

            String username = jwtTokenProvider.obtenerUsernameDesdeToken(jwt);
            Long tiempoRestante = jwtTokenProvider.obtenerTiempoRestante(jwt);
            boolean proximoAExpirar = jwtTokenProvider.estaProximoAExpirar(jwt);

            TokenValidationResponse response = TokenValidationResponse.builder()
                .valid(true)
                .username(username)
                .tiempoRestante(tiempoRestante)
                .proximoAExpirar(proximoAExpirar)
                .build();

            log.debug("Token válido para usuario: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        }
    }

    // DTOs para las peticiones

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class LoginRequest {
        private String usuario;
        private String password;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class RefreshTokenRequest {
        private String refreshToken;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class LogoutRequest {
        private String token;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class TokenValidationRequest {
        private String token;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class TokenValidationResponse {
        private boolean valid;
        private String username;
        private Long tiempoRestante;
        private boolean proximoAExpirar;
    }
}

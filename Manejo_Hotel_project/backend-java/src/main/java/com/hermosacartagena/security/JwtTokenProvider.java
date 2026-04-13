package com.hermosacartagena.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * PROVEEDOR DE TOKENS JWT
 * =============================================
 * 
 * Componente para generación, validación y manejo de tokens JWT.
 * Implementa operaciones criptográficas seguras para autenticación.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${hermosa-cartagena.security.jwt.secret}")
    private String jwtSecret;

    @Value("${hermosa-cartagena.security.jwt.expiration}")
    private int jwtExpirationInSeconds;

    @Value("${hermosa-cartagena.security.jwt.refresh-expiration}")
    private int refreshExpirationInSeconds;

    @Value("${hermosa-cartagena.security.jwt.issuer}")
    private String jwtIssuer;

    @Value("${hermosa-cartagena.security.jwt.audience}")
    private String jwtAudience;

    /**
     * Genera un token JWT para el usuario autenticado
     * 
     * @param authentication Autenticación de Spring Security
     * @return Token JWT generado
     */
    public String generarToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        return generarTokenDesdeUsername(userDetails.getUsername());
    }

    /**
     * Genera un token JWT desde un username
     * 
     * @param username Nombre de usuario
     * @return Token JWT generado
     */
    public String generarTokenDesdeUsername(String username) {
        Instant ahora = Instant.now();
        Instant fechaExpiracion = ahora.plus(jwtExpirationInSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience)
                .setIssuedAt(Date.from(ahora))
                .setExpiration(Date.from(fechaExpiracion))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Genera un token de refresco
     * 
     * @param username Nombre de usuario
     * @return Token de refresco
     */
    public String generarTokenRefresco(String username) {
        Instant ahora = Instant.now();
        Instant fechaExpiracion = ahora.plus(refreshExpirationInSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience + "-refresh")
                .setIssuedAt(Date.from(ahora))
                .setExpiration(Date.from(fechaExpiracion))
                .claim("type", "refresh")
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Genera un token con claims personalizados
     * 
     * @param username Nombre de usuario
     * @param idUsuario ID del usuario
     * @param roles Roles del usuario
     * @return Token JWT con claims
     */
    public String generarTokenConClaims(String username, Long idUsuario, List<String> roles) {
        Instant ahora = Instant.now();
        Instant fechaExpiracion = ahora.plus(jwtExpirationInSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience)
                .setIssuedAt(Date.from(ahora))
                .setExpiration(Date.from(fechaExpiracion))
                .claim("userId", idUsuario)
                .claim("roles", roles)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Obtiene el username desde un token JWT
     * 
     * @param token Token JWT
     * @return Username extraído
     */
    public String obtenerUsernameDesdeToken(String token) {
        return getClaimDesdeToken(token, Claims::getSubject);
    }

    /**
     * Obtiene el ID del usuario desde un token JWT
     * 
     * @param token Token JWT
     * @return ID del usuario
     */
    public Long obtenerIdUsuarioDesdeToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("userId", Long.class);
    }

    /**
     * Obtiene los roles desde un token JWT
     * 
     * @param token Token JWT
     * @return Lista de roles
     */
    @SuppressWarnings("unchecked")
    public List<String> obtenerRolesDesdeToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("roles", List.class);
    }

    /**
     * Obtiene la fecha de expiración desde un token JWT
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date obtenerFechaExpiracionDesdeToken(String token) {
        return getClaimDesdeToken(token, Claims::getExpiration);
    }

    /**
     * Verifica si un token JWT es de refresco
     * 
     * @param token Token JWT
     * @return true si es token de refresco
     */
    public boolean esTokenRefresco(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida un token JWT
     * 
     * @param token Token JWT a validar
     * @return true si el token es válido
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            
            return true;
        } catch (SecurityException ex) {
            log.error("Firma JWT inválida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Token JWT malformado: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT no soportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Claims de token JWT vacíos: {}", ex.getMessage());
        }
        
        return false;
    }

    /**
     * Verifica si un token está próximo a expirar (dentro de 5 minutos)
     * 
     * @param token Token JWT
     * @return true si está próximo a expirar
     */
    public boolean estaProximoAExpirar(String token) {
        try {
            Date fechaExpiracion = obtenerFechaExpiracionDesdeToken(token);
            Instant ahora = Instant.now();
            Instant limiteExpiracion = ahora.plus(5, ChronoUnit.MINUTES);
            
            return fechaExpiracion.toInstant().isBefore(limiteExpiracion);
        } catch (Exception e) {
            log.warn("Error al verificar expiración del token: {}", e.getMessage());
            return true; // Si hay error, considerarlo como expirado
        }
    }

    /**
     * Obtiene el tiempo restante de vida del token en segundos
     * 
     * @param token Token JWT
     * @return Segundos restantes o -1 si está expirado
     */
    public long obtenerTiempoRestante(String token) {
        try {
            Date fechaExpiracion = obtenerFechaExpiracionDesdeToken(token);
            long tiempoRestante = (fechaExpiracion.getTime() - System.currentTimeMillis()) / 1000;
            
            return Math.max(0, tiempoRestante);
        } catch (Exception e) {
            log.warn("Error al obtener tiempo restante del token: {}", e.getMessage());
            return -1;
        }
    }

    /**
     * Refresca un token existente
     * 
     * @param tokenRefresco Token de refresco
     * @return Nuevo token JWT
     * @throws JwtException Si el token de refresco es inválido
     */
    public String refrescarToken(String tokenRefresco) throws JwtException {
        if (!validarToken(tokenRefresco) || !esTokenRefresco(tokenRefresco)) {
            throw new JwtException("Token de refresco inválido");
        }

        String username = obtenerUsernameDesdeToken(tokenRefresco);
        return generarTokenDesdeUsername(username);
    }

    /**
     * Obtiene un claim específico desde un token JWT
     * 
     * @param token Token JWT
     * @param claimsResolver Función para resolver el claim
     * @param <T> Tipo del claim
     * @return Valor del claim
     */
    public <T> T getClaimDesdeToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claimsResolver.apply(claims);
    }

    /**
     * Obtiene la clave de firma para tokens JWT
     * 
     * @return SecretKey para firma
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token para reset de contraseña
     * 
     * @param username Nombre de usuario
     * @return Token de reset de contraseña
     */
    public String generarTokenResetPassword(String username) {
        Instant ahora = Instant.now();
        Instant fechaExpiracion = ahora.plus(1, ChronoUnit.HOURS); // Válido por 1 hora

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience + "-reset")
                .setIssuedAt(Date.from(ahora))
                .setExpiration(Date.from(fechaExpiracion))
                .claim("type", "reset-password")
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Valida un token de reset de contraseña
     * 
     * @param token Token de reset
     * @return true si es válido
     */
    public boolean validarTokenResetPassword(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return "reset-password".equals(claims.get("type"));
        } catch (Exception e) {
            log.warn("Token de reset inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene información del token para debugging
     * 
     * @param token Token JWT
     * @return Información del token
     */
    public String obtenerInformacionToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return String.format(
                "Token Info - Subject: %s, Issuer: %s, Audience: %s, Issued: %s, Expires: %s, Roles: %s",
                claims.getSubject(),
                claims.getIssuer(),
                claims.getAudience(),
                claims.getIssuedAt(),
                claims.getExpiration(),
                claims.get("roles", List.class)
            );
        } catch (Exception e) {
            return "Token inválido o expirado: " + e.getMessage();
        }
    }
}

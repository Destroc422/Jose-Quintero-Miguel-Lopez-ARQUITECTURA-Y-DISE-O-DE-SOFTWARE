package com.hermosacartagena.security;

import com.hermosacartagena.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * FILTRO DE AUTENTICACIÓN JWT
 * =============================================
 * 
 * Filtro que procesa tokens JWT en cada petición.
 * Valida el token y establece el contexto de autenticación.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final UsuarioService usuarioService;

    /**
     * Filtra cada petición para validar el token JWT
     * 
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param filterChain Cadena de filtros
     * @throws ServletException Si hay error de servlet
     * @throws IOException Si hay error de I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Obtener token JWT de la petición
            String jwt = obtenerTokenDesdeRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validarToken(jwt)) {
                // Obtener username del token
                String username = jwtTokenProvider.obtenerUsernameDesdeToken(jwt);
                
                // Cargar detalles del usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Crear autenticación
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Establecer en contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Actualizar último acceso
                try {
                    Long usuarioId = jwtTokenProvider.obtenerIdUsuarioDesdeToken(jwt);
                    usuarioService.actualizarUltimoAcceso(usuarioId);
                } catch (Exception e) {
                    log.warn("No se pudo actualizar último acceso: {}", e.getMessage());
                }
                
                log.debug("Autenticación JWT establecida para usuario: {}", username);
            }
        } catch (Exception ex) {
            log.error("No se pudo establecer autenticación JWT: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Obtiene el token JWT desde la petición HTTP
     * 
     * @param request Petición HTTP
     * @return Token JWT o null si no existe
     */
    private String obtenerTokenDesdeRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}

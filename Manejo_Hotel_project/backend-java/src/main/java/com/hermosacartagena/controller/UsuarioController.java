package com.hermosacartagena.controller;

import com.hermosacartagena.dto.UsuarioDTO;
import com.hermosacartagena.service.UsuarioService;
import com.hermosacartagena.exception.ResourceNotFoundException;
import com.hermosacartagena.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CONTROLADOR USUARIO
 * =============================================
 * 
 * Controlador REST para gestión de usuarios.
 * Expone endpoints para operaciones CRUD y business logic.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Crea un nuevo usuario
     * 
     * @param usuarioDTO DTO con datos del usuario
     * @return UsuarioDTO creado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos"),
        @ApiResponse(responseCode = "403", description = "No autorizado"),
        @ApiResponse(responseCode = "409", description = "El usuario o email ya existe")
    })
    public ResponseEntity<UsuarioDTO> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        log.info("Petición para crear usuario: {}", usuarioDTO.getUsuario());
        
        UsuarioDTO usuarioCreado = usuarioService.crearUsuario(usuarioDTO);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioCreado);
    }

    /**
     * Actualiza un usuario existente
     * 
     * @param id ID del usuario a actualizar
     * @param usuarioDTO DTO con datos actualizados
     * @return UsuarioDTO actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Actualizar usuario", description = "Actualiza datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos"),
        @ApiResponse(responseCode = "403", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        
        log.info("Petición para actualizar usuario con ID: {}", id);
        
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
        
        return ResponseEntity.ok(usuarioActualizado);
    }

    /**
     * Obtiene un usuario por su ID
     * 
     * @param id ID del usuario
     * @return UsuarioDTO encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene información de un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        
        log.debug("Petición para obtener usuario con ID: {}", id);
        
        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(id);
        
        return ResponseEntity.ok(usuario);
    }

    /**
     * Lista todos los usuarios paginados
     * 
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo de ordenación (default: idUsuario)
     * @param sortDir Dirección de ordenación (default: ASC)
     * @return Página de usuarios
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios", description = "Lista todos los usuarios con paginación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios listados exitosamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<Page<UsuarioDTO>> listarTodosLosUsuarios(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenación") @RequestParam(defaultValue = "idUsuario") String sortBy,
            @Parameter(description = "Dirección de ordenación") @RequestParam(defaultValue = "ASC") String sortDir) {
        
        log.debug("Petición para listar usuarios - página: {}, tamaño: {}", page, size);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<UsuarioDTO> usuarios = usuarioService.listarTodosLosUsuarios(pageable);
        
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca usuarios por texto
     * 
     * @param texto Texto a buscar
     * @param page Número de página (default: 0)
     * @param size Tamaño de página (default: 10)
     * @param sortBy Campo de ordenación (default: idUsuario)
     * @param sortDir Dirección de ordenación (default: ASC)
     * @return Página de usuarios que coinciden
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar usuarios", description = "Busca usuarios por texto en nombre, email o usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda completada"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<Page<UsuarioDTO>> buscarUsuarios(
            @Parameter(description = "Texto a buscar") @RequestParam String texto,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenación") @RequestParam(defaultValue = "idUsuario") String sortBy,
            @Parameter(description = "Dirección de ordenación") @RequestParam(defaultValue = "ASC") String sortDir) {
        
        log.debug("Petición para buscar usuarios con texto: {}", texto);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<UsuarioDTO> usuarios = usuarioService.buscarUsuarios(texto, pageable);
        
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Lista usuarios por rol
     * 
     * @param idRol ID del rol
     * @return Lista de usuarios del rol
     */
    @GetMapping("/rol/{idRol}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios por rol", description = "Obtiene todos los usuarios de un rol específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios listados exitosamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<List<UsuarioDTO>> listarUsuariosPorRol(
            @Parameter(description = "ID del rol") @PathVariable Long idRol) {
        
        log.debug("Petición para listar usuarios del rol: {}", idRol);
        
        List<UsuarioDTO> usuarios = usuarioService.listarUsuariosPorRol(idRol);
        
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Lista usuarios activos
     * 
     * @return Lista de usuarios activos
     */
    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios activos", description = "Obtiene todos los usuarios activos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios activos listados"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<UsuarioDTO>> listarUsuariosActivos() {
        log.debug("Petición para listar usuarios activos");
        
        List<UsuarioDTO> usuarios = usuarioService.listarUsuariosActivos();
        
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Cambia el estado de un usuario
     * 
     * @param id ID del usuario
     * @param estado Nuevo estado
     * @return UsuarioDTO actualizado
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar estado de usuario", description = "Activa o desactiva un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "403", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioDTO> cambiarEstado(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Parameter(description = "Nuevo estado (activo/inactivo)") @RequestParam String estado) {
        
        log.info("Petición para cambiar estado del usuario {} a: {}", id, estado);
        
        UsuarioDTO usuarioActualizado = usuarioService.cambiarEstado(id, estado);
        
        return ResponseEntity.ok(usuarioActualizado);
    }

    /**
     * Reinicia intentos fallidos de un usuario
     * 
     * @param id ID del usuario
     * @return Respuesta vacía
     */
    @PatchMapping("/{id}/reiniciar-intentos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reiniciar intentos fallidos", description = "Reinicia el contador de intentos fallidos de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Intentos reiniciados"),
        @ApiResponse(responseCode = "403", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> reiniciarIntentosFallidos(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        
        log.info("Petición para reiniciar intentos fallidos del usuario: {}", id);
        
        usuarioService.reiniciarIntentosFallidos(id);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Bloquea cuenta de usuario
     * 
     * @param id ID del usuario
     * @param minutos Minutos de bloqueo
     * @return Respuesta vacía
     */
    @PatchMapping("/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bloquear cuenta", description = "Bloquea la cuenta de un usuario por un tiempo específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta bloqueada"),
        @ApiResponse(responseCode = "403", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> bloquearCuenta(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Parameter(description = "Minutos de bloqueo") @RequestParam(defaultValue = "30") int minutos) {
        
        log.info("Petición para bloquear cuenta del usuario {} por {} minutos", id, minutos);
        
        usuarioService.bloquearCuenta(id, minutos);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Elimina un usuario (eliminación lógica)
     * 
     * @param id ID del usuario
     * @return Respuesta vacía
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario (desactivación lógica)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "403", description = "No autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        
        log.info("Petición para eliminar usuario con ID: {}", id);
        
        usuarioService.eliminarUsuario(id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene estadísticas de usuarios
     * 
     * @return Estadísticas de usuarios
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener estadísticas", description = "Obtiene estadísticas generales de usuarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<Object[]> obtenerEstadisticas() {
        log.debug("Petición para obtener estadísticas de usuarios");
        
        Object[] estadisticas = usuarioService.obtenerEstadisticas();
        
        return ResponseEntity.ok(estadisticas);
    }

    // ==================== ENDPOINTS PARA USUARIOS AUTENTICADOS ====================

    /**
     * Obtiene perfil del usuario autenticado
     * 
     * @return Perfil del usuario
     */
    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener perfil", description = "Obtiene información del perfil del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil obtenido"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UsuarioDTO> obtenerPerfil() {
        // Esta implementación requerirá obtener el ID del usuario autenticado
        // desde el contexto de seguridad de Spring
        // Por ahora, retornamos un placeholder
        throw new UnsupportedOperationException("Implementación pendiente - requiere contexto de seguridad");
    }

    /**
     * Actualiza perfil del usuario autenticado
     * 
     * @param perfilDTO DTO con datos del perfil
     * @return Perfil actualizado
     */
    @PutMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Actualizar perfil", description = "Actualiza información del perfil del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UsuarioDTO> actualizarPerfil(
            @Valid @RequestBody UsuarioDTO.ActualizarPerfil perfilDTO) {
        // Esta implementación requerirá obtener el ID del usuario autenticado
        // desde el contexto de seguridad de Spring
        throw new UnsupportedOperationException("Implementación pendiente - requiere contexto de seguridad");
    }

    // ==================== MANEJO DE ERRORES ====================

    /**
     * Manejador de excepciones para ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Manejador de excepciones para BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        log.warn("Error de negocio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Manejador de excepciones genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Error inesperado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor. Por favor, contacte al administrador.");
    }
}

package com.hermosacartagena.repository;

import com.hermosacartagena.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * REPOSITORIO USUARIO
 * =============================================
 * 
 * Repositorio Spring Data JPA para la entidad Usuario.
 * Proporciona métodos CRUD personalizados y consultas específicas
 * para gestión de usuarios y autenticación.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario
     * 
     * @param usuario Nombre de usuario a buscar
     * @return Optional con el usuario encontrado
     */
    Optional<Usuario> findByUsuario(String usuario);

    /**
     * Busca un usuario por su email
     * 
     * @param email Email del usuario a buscar
     * @return Optional con el usuario encontrado
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por nombre de usuario o email
     * 
     * @param usuario Nombre de usuario o email
     * @return Optional con el usuario encontrado
     */
    @Query("SELECT u FROM Usuario u WHERE u.usuario = :usuario OR u.email = :usuario")
    Optional<Usuario> findByUsuarioOrEmail(@Param("usuario") String usuario);

    /**
     * Verifica si existe un usuario por su nombre de usuario
     * 
     * @param usuario Nombre de usuario a verificar
     * @return true si existe
     */
    boolean existsByUsuario(String usuario);

    /**
     * Verifica si existe un usuario por su email
     * 
     * @param email Email a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario por nombre de usuario o email
     * 
     * @param usuario Nombre de usuario o email
     * @return true si existe
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.usuario = :usuario OR u.email = :usuario")
    boolean existsByUsuarioOrEmail(@Param("usuario") String usuario);

    /**
     * Busca usuarios por rol
     * 
     * @param idRol ID del rol
     * @return Lista de usuarios con el rol especificado
     */
    List<Usuario> findByIdRol(Long idRol);

    /**
     * Busca usuarios por estado
     * 
     * @param estado Estado del usuario (activo, inactivo)
     * @return Lista de usuarios con el estado especificado
     */
    List<Usuario> findByEstado(String estado);

    /**
     * Busca usuarios activos
     * 
     * @return Lista de usuarios activos
     */
    @Query("SELECT u FROM Usuario u WHERE u.estado = 'activo' ORDER BY u.nombreCompleto")
    List<Usuario> findActivosOrdenadosPorNombre();

    /**
     * Busca usuarios por rol y estado
     * 
     * @param idRol ID del rol
     * @param estado Estado del usuario
     * @return Lista de usuarios con rol y estado especificados
     */
    @Query("SELECT u FROM Usuario u WHERE u.idRol = :idRol AND u.estado = :estado ORDER BY u.nombreCompleto")
    List<Usuario> findByIdRolAndEstado(@Param("idRol") Long idRol, @Param("estado") String estado);

    /**
     * Busca usuarios que iniciaron sesión recientemente
     * 
     * @param fecha Fecha límite para último acceso
     * @return Lista de usuarios activos recientemente
     */
    @Query("SELECT u FROM Usuario u WHERE u.fechaUltimoAcceso >= :fecha AND u.estado = 'activo' ORDER BY u.fechaUltimoAcceso DESC")
    List<Usuario> findUsuariosActivosRecientemente(@Param("fecha") LocalDateTime fecha);

    /**
     * Busca usuarios con cuentas bloqueadas
     * 
     * @return Lista de usuarios con cuentas bloqueadas
     */
    @Query("SELECT u FROM Usuario u WHERE u.cuentaBloqueadaHasta > CURRENT_TIMESTAMP")
    List<Usuario> findUsuariosConCuentasBloqueadas();

    /**
     * Busca usuarios con intentos fallidos
     * 
     * @param intentosMinimos Mínimo de intentos fallidos
     * @return Lista de usuarios con intentos fallidos
     */
    @Query("SELECT u FROM Usuario u WHERE u.intentosFallidos >= :intentosMinimos ORDER BY u.intentosFallidos DESC")
    List<Usuario> findUsuariosConIntentosFallidos(@Param("intentosMinimos") Integer intentosMinimos);

    /**
     * Busca usuarios por texto en nombre completo o email (paginado)
     * 
     * @param texto Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de usuarios que coinciden
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(u.usuario) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Usuario> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    /**
     * Obtiene estadísticas de usuarios por rol y estado
     * 
     * @return Lista de arrays con [id_rol, nombre_rol, estado, cantidad]
     */
    @Query("SELECT u.idRol, r.nombreRol, u.estado, COUNT(*) " +
           "FROM Usuario u JOIN u.rol r " +
           "GROUP BY u.idRol, r.nombreRol, u.estado " +
           "ORDER BY r.nombreRol, u.estado")
    List<Object[]> obtenerEstadisticasPorRolYEstado();

    /**
     * Obtiene estadísticas generales de usuarios
     * 
     * @return Array con [total, activos, inactivos, bloqueados, admin, empleados, clientes]
     */
    @Query("SELECT " +
           "COUNT(*) as total, " +
           "SUM(CASE WHEN u.estado = 'activo' THEN 1 ELSE 0 END) as activos, " +
           "SUM(CASE WHEN u.estado = 'inactivo' THEN 1 ELSE 0 END) as inactivos, " +
           "SUM(CASE WHEN u.cuentaBloqueadaHasta > CURRENT_TIMESTAMP THEN 1 ELSE 0 END) as bloqueados, " +
           "SUM(CASE WHEN r.nombreRol = 'ADMIN' THEN 1 ELSE 0 END) as admin, " +
           "SUM(CASE WHEN r.nombreRol = 'EMPLEADO' THEN 1 ELSE 0 END) as empleados, " +
           "SUM(CASE WHEN r.nombreRol = 'CLIENTE' THEN 1 ELSE 0 END) as clientes " +
           "FROM Usuario u JOIN u.rol r")
    Object[] obtenerEstadisticasGenerales();

    /**
     * Actualiza el último acceso de un usuario
     * 
     * @param idUsuario ID del usuario
     * @param fechaAcceso Nueva fecha de último acceso
     */
    @Modifying
    @Query("UPDATE Usuario u SET u.fechaUltimoAcceso = :fechaAcceso WHERE u.idUsuario = :idUsuario")
    void actualizarUltimoAcceso(@Param("idUsuario") Long idUsuario, @Param("fechaAcceso") LocalDateTime fechaAcceso);

    /**
     * Reinicia intentos fallidos de un usuario
     * 
     * @param idUsuario ID del usuario
     */
    @Modifying
    @Query("UPDATE Usuario u SET u.intentosFallidos = 0, u.cuentaBloqueadaHasta = NULL WHERE u.idUsuario = :idUsuario")
    void reiniciarIntentosFallidos(@Param("idUsuario") Long idUsuario);

    /**
     * Incrementa intentos fallidos de un usuario
     * 
     * @param idUsuario ID del usuario
     */
    @Modifying
    @Query("UPDATE Usuario u SET u.intentosFallidos = COALESCE(u.intentosFallidos, 0) + 1 WHERE u.idUsuario = :idUsuario")
    void incrementarIntentosFallidos(@Param("idUsuario") Long idUsuario);

    /**
     * Bloquea cuenta de usuario hasta una fecha específica
     * 
     * @param idUsuario ID del usuario
     * @param fechaBloqueo Fecha hasta la cual estará bloqueada
     */
    @Modifying
    @Query("UPDATE Usuario u SET u.cuentaBloqueadaHasta = :fechaBloqueo WHERE u.idUsuario = :idUsuario")
    void bloquearCuentaHasta(@Param("idUsuario") Long idUsuario, @Param("fechaBloqueo") LocalDateTime fechaBloqueo);

    /**
     * Cambia el estado de un usuario
     * 
     * @param idUsuario ID del usuario
     * @param nuevoEstado Nuevo estado
     */
    @Modifying
    @Query("UPDATE Usuario u SET u.estado = :nuevoEstado WHERE u.idUsuario = :idUsuario")
    void cambiarEstado(@Param("idUsuario") Long idUsuario, @Param("nuevoEstado") String nuevoEstado);

    /**
     * Busca usuarios que no han iniciado sesión en un período específico
     * 
     * @param fechaLimite Fecha límite de último acceso
     * @return Lista de usuarios inactivos
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "(u.fechaUltimoAcceso IS NULL OR u.fechaUltimoAcceso < :fechaLimite) AND u.estado = 'activo'")
    List<Usuario> findUsuariosInactivosDesde(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Obtiene usuarios con perfil completo (con cliente o empleado asociado)
     * 
     * @return Lista de usuarios con perfil completo
     */
    @Query("SELECT u FROM Usuario u WHERE u.cliente IS NOT NULL OR u.empleado IS NOT NULL")
    List<Usuario> findUsuariosConPerfilCompleto();

    /**
     * Busca administradores activos
     * 
     * @return Lista de administradores activos
     */
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombreRol = 'ADMIN' AND u.estado = 'active'")
    List<Usuario> findAdministradoresActivos();

    /**
     * Verifica si un email ya está en uso por otro usuario
     * 
     * @param email Email a verificar
     * @param idUsuarioExcluir ID de usuario a excluir de la búsqueda
     * @return true si el email está en uso por otro usuario
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.email = :email AND u.idUsuario != :idUsuarioExcluir")
    boolean emailEnUsoPorOtroUsuario(@Param("email") String email, @Param("idUsuarioExcluir") Long idUsuarioExcluir);

    /**
     * Obtiene usuarios para reporte de actividad
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Lista de usuarios activos en el período
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "u.fechaUltimoAcceso BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY u.fechaUltimoAcceso DESC")
    List<Usuario> findUsuariosActivosEnPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                             @Param("fechaFin") LocalDateTime fechaFin);
}

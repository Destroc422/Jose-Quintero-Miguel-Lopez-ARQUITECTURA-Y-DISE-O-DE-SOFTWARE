package com.hermosacartagena.repository;

import com.hermosacartagena.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * REPOSITORIO ROL
 * =============================================
 * 
 * Repositorio Spring Data JPA para la entidad Rol.
 * Proporciona métodos CRUD personalizados y consultas específicas.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre
     * 
     * @param nombreRol Nombre del rol a buscar
     * @return Optional con el rol encontrado
     */
    Optional<Rol> findByNombreRol(String nombreRol);

    /**
     * Verifica si existe un rol por su nombre
     * 
     * @param nombreRol Nombre del rol a verificar
     * @return true si existe
     */
    boolean existsByNombreRol(String nombreRol);

    /**
     * Busca roles por estado
     * 
     * @param estado Estado del rol (activo, inactivo)
     * @return Lista de roles con el estado especificado
     */
    List<Rol> findByEstado(String estado);

    /**
     * Busca roles activos ordenados por nombre
     * 
     * @return Lista de roles activos ordenados alfabéticamente
     */
    @Query("SELECT r FROM Rol r WHERE r.estado = 'activo' ORDER BY r.nombreRol")
    List<Rol> findActivosOrdenadosPorNombre();

    /**
     * Obtiene estadísticas de roles por estado
     * 
     * @return Array con conteo [total, activos, inactivos]
     */
    @Query("SELECT " +
            "COUNT(*) as total, " +
            "SUM(CASE WHEN r.estado = 'activo' THEN 1 ELSE 0 END) as activos, " +
            "SUM(CASE WHEN r.estado = 'inactivo' THEN 1 ELSE 0 END) as inactivos " +
            "FROM Rol r")
    Object[] obtenerEstadisticasPorEstado();

    /**
     * Busca roles con usuarios asociados
     * 
     * @param cantidadMinimaUsuarios Cantidad mínima de usuarios
     * @return Lista de roles con la cantidad mínima de usuarios
     */
    @Query("SELECT r FROM Rol r WHERE SIZE(r.usuarios) >= :cantidadMinimaUsuarios ORDER BY SIZE(r.usuarios) DESC")
    List<Rol> findByCantidadUsuariosMinima(@Param("cantidadMinimaUsuarios") int cantidadMinimaUsuarios);

    /**
     * Obtiene el rol más utilizado (con más usuarios)
     * 
     * @return Optional con el rol más utilizado
     */
    @Query("SELECT r FROM Rol r WHERE SIZE(r.usuarios) = (SELECT MAX(SIZE(r2.usuarios)) FROM Rol r2)")
    Optional<Rol> findRolMasUtilizado();

    /**
     * Busca roles por nombre que contenga el texto especificado (case insensitive)
     * 
     * @param texto Texto a buscar en el nombre del rol
     * @return Lista de roles que coinciden con la búsqueda
     */
    @Query("SELECT r FROM Rol r WHERE LOWER(r.nombreRol) LIKE LOWER(CONCAT('%', :texto, '%')) ORDER BY r.nombreRol")
    List<Rol> findByNombreRolContainingIgnoreCase(@Param("texto") String texto);

    /**
     * Obtiene todos los roles con la cantidad de usuarios asociados
     * 
     * @return Lista de arrays con [rol, cantidad_usuarios]
     */
    @Query("SELECT r, SIZE(r.usuarios) FROM Rol r ORDER BY SIZE(r.usuarios) DESC")
    List<Object[]> findAllWithCantidadUsuarios();

    /**
     * Verifica si un rol tiene usuarios asociados
     * 
     * @param idRol ID del rol a verificar
     * @return true si tiene usuarios asociados
     */
    @Query("SELECT CASE WHEN SIZE(r.usuarios) > 0 THEN true ELSE false END FROM Rol r WHERE r.idRol = :idRol")
    boolean tieneUsuariosAsociados(@Param("idRol") Long idRol);

    /**
     * Obtiene roles disponibles para asignación (activos y no del tipo especificado)
     * 
     * @param excluirNombreRol Nombre de rol a excluir
     * @return Lista de roles disponibles
     */
    @Query("SELECT r FROM Rol r WHERE r.estado = 'activo' AND r.nombreRol != :excluirNombreRol ORDER BY r.nombreRol")
    List<Rol> findDisponiblesParaAsignacion(@Param("excluirNombreRol") String excluirNombreRol);
}

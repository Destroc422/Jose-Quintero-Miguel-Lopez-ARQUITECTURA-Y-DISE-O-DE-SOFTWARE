package com.hermosacartagena.repository;

import com.hermosacartagena.entity.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * REPOSITORIO SERVICIO
 * =============================================
 * 
 * Repositorio Spring Data JPA para la entidad Servicio.
 * Proporciona métodos CRUD personalizados y consultas específicas
 * para gestión de servicios turísticos.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    /**
     * Busca servicios por proveedor
     * 
     * @param idProveedor ID del proveedor
     * @return Lista de servicios del proveedor
     */
    List<Servicio> findByIdProveedor(Long idProveedor);

    /**
     * Busca servicios por tipo
     * 
     * @param tipoServicio Tipo de servicio
     * @return Lista de servicios del tipo especificado
     */
    List<Servicio> findByTipoServicio(String tipoServicio);

    /**
     * Busca servicios por estado
     * 
     * @param estado Estado del servicio
     * @return Lista de servicios con el estado especificado
     */
    List<Servicio> findByEstado(String estado);

    /**
     * Busca servicios activos
     * 
     * @return Lista de servicios activos ordenados por nombre
     */
    @Query("SELECT s FROM Servicio s WHERE s.estado = 'activo' ORDER BY s.nombreServicio")
    List<Servicio> findActivosOrdenadosPorNombre();

    /**
     * Busca servicios destacados
     * 
     * @return Lista de servicios destacados
     */
    @Query("SELECT s FROM Servicio s WHERE s.destacado = true AND s.estado = 'activo' ORDER BY s.nombreServicio")
    List<Servicio> findDestacadosActivos();

    /**
     * Busca servicios por rango de precios
     * 
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de servicios en el rango de precios
     */
    @Query("SELECT s FROM Servicio s WHERE s.precioBase BETWEEN :precioMin AND :precioMax AND s.estado = 'activo' ORDER BY s.precioBase")
    List<Servicio> findByRangoPrecio(@Param("precioMin") BigDecimal precioMin, @Param("precioMax") BigDecimal precioMax);

    /**
     * Busca servicios por capacidad mínima
     * 
     * @param capacidadMinima Capacidad mínima requerida
     * @return Lista de servicios con capacidad mínima o mayor
     */
    @Query("SELECT s FROM Servicio s WHERE s.capacidadMaxima >= :capacidadMinima AND s.estado = 'activo' ORDER BY s.capacidadMaxima DESC")
    List<Servicio> findByCapacidadMinima(@Param("capacidadMinima") Integer capacidadMinima);

    /**
     * Busca servicios por ubicación (contiene texto)
     * 
     * @param ubicacion Texto a buscar en ubicación
     * @return Lista de servicios que coinciden con la ubicación
     */
    @Query("SELECT s FROM Servicio s WHERE LOWER(s.ubicacion) LIKE LOWER(CONCAT('%', :ubicacion, '%')) AND s.estado = 'activo' ORDER BY s.ubicacion")
    List<Servicio> findByUbicacionContaining(@Param("ubicacion") String ubicacion);

    /**
     * Busca servicios por texto en nombre o descripción
     * 
     * @param texto Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de servicios que coinciden
     */
    @Query("SELECT s FROM Servicio s WHERE " +
           "(LOWER(s.nombreServicio) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(s.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(s.ubicacion) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
           "AND s.estado = 'active'")
    Page<Servicio> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    /**
     * Obtiene servicios disponibles para un rango de fechas y cantidad de personas
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param cantidadPersonas Cantidad de personas
     * @return Lista de servicios disponibles
     */
    @Query("SELECT s FROM Servicio s WHERE " +
           "s.estado = 'activo' AND " +
           "s.capacidadMaxima >= :cantidadPersonas AND " +
           "(s.disponibleDesde IS NULL OR s.disponibleDesde <= :fechaInicio) AND " +
           "(s.disponibleHasta IS NULL OR s.disponibleHasta >= :fechaFin) " +
           "ORDER BY s.nombreServicio")
    List<Servicio> findDisponiblesParaFechasYPersonas(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                      @Param("fechaFin") LocalDateTime fechaFin,
                                                      @Param("cantidadPersonas") Integer cantidadPersonas);

    /**
     * Obtiene servicios populares (con más reservas)
     * 
     * @param limite Límite de resultados
     * @return Lista de servicios populares
     */
    @Query("SELECT s FROM Servicio s WHERE s.estado = 'activo' " +
           "ORDER BY s.totalReservas DESC, s.calificacionPromedio DESC")
    List<Servicio> findServiciosPopulares(Pageable pageable);

    /**
     * Obtiene servicios mejor calificados
     * 
     * @param limite Límite de resultados
     * @return Lista de servicios mejor calificados
     */
    @Query("SELECT s FROM Servicio s WHERE s.estado = 'activo' AND s.calificacionPromedio > 0 " +
           "ORDER BY s.calificacionPromedio DESC, s.totalReservas DESC")
    List<Servicio> findServiciosMejorCalificados(Pageable pageable);

    /**
     * Busca servicios por proveedor y estado
     * 
     * @param idProveedor ID del proveedor
     * @param estado Estado del servicio
     * @return Lista de servicios del proveedor con estado específico
     */
    @Query("SELECT s FROM Servicio s WHERE s.idProveedor = :idProveedor AND s.estado = :estado ORDER BY s.nombreServicio")
    List<Servicio> findByIdProveedorAndEstado(@Param("idProveedor") Long idProveedor, @Param("estado") String estado);

    /**
     * Obtiene estadísticas de servicios por tipo
     * 
     * @return Lista de arrays con [tipo_servicio, total, activos, inactivos]
     */
    @Query("SELECT s.tipoServicio, COUNT(*), " +
           "SUM(CASE WHEN s.estado = 'activo' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN s.estado = 'inactivo' THEN 1 ELSE 0 END) " +
           "FROM Servicio s GROUP BY s.tipoServicio ORDER BY COUNT(*) DESC")
    List<Object[]> obtenerEstadisticasPorTipo();

    /**
     * Obtiene estadísticas generales de servicios
     * 
     * @return Array con [total, activos, inactivos, destacados, precio_promedio, calificacion_promedio]
     */
    @Query("SELECT " +
           "COUNT(*) as total, " +
           "SUM(CASE WHEN s.estado = 'activo' THEN 1 ELSE 0 END) as activos, " +
           "SUM(CASE WHEN s.estado = 'inactivo' THEN 1 ELSE 0 END) as inactivos, " +
           "SUM(CASE WHEN s.destacado = true THEN 1 ELSE 0 END) as destacados, " +
           "AVG(s.precioBase) as precio_promedio, " +
           "AVG(s.calificacionPromedio) as calificacion_promedio " +
           "FROM Servicio s")
    Object[] obtenerEstadisticasGenerales();

    /**
     * Actualiza la calificación promedio de un servicio
     * 
     * @param idServicio ID del servicio
     * @param nuevaCalificacion Nueva calificación promedio
     */
    @Modifying
    @Query("UPDATE Servicio s SET s.calificacionPromedio = :nuevaCalificacion WHERE s.idServicio = :idServicio")
    void actualizarCalificacionPromedio(@Param("idServicio") Long idServicio, @Param("nuevaCalificacion") BigDecimal nuevaCalificacion);

    /**
     * Incrementa el contador de reservas de un servicio
     * 
     * @param idServicio ID del servicio
     */
    @Modifying
    @Query("UPDATE Servicio s SET s.totalReservas = COALESCE(s.totalReservas, 0) + 1 WHERE s.idServicio = :idServicio")
    void incrementarTotalReservas(@Param("idServicio") Long idServicio);

    /**
     * Cambia el estado de un servicio
     * 
     * @param idServicio ID del servicio
     * @param nuevoEstado Nuevo estado
     */
    @Modifying
    @Query("UPDATE Servicio s SET s.estado = :nuevoEstado WHERE s.idServicio = :idServicio")
    void cambiarEstado(@Param("idServicio") Long idServicio, @Param("nuevoEstado") String nuevoEstado);

    /**
     * Marca o desmarca un servicio como destacado
     * 
     * @param idServicio ID del servicio
     * @param destacado Valor a establecer
     */
    @Modifying
    @Query("UPDATE Servicio s SET s.destacado = :destacado WHERE s.idServicio = :idServicio")
    void actualizarDestacado(@Param("idServicio") Long idServicio, @Param("destacado") Boolean destacado);

    /**
     * Actualiza la disponibilidad de un servicio
     * 
     * @param idServicio ID del servicio
     * @param disponibleDesde Fecha de inicio de disponibilidad
     * @param disponibleHasta Fecha de fin de disponibilidad
     */
    @Modifying
    @Query("UPDATE Servicio s SET s.disponibleDesde = :disponibleDesde, s.disponibleHasta = :disponibleHasta WHERE s.idServicio = :idServicio")
    void actualizarDisponibilidad(@Param("idServicio") Long idServicio, 
                                  @Param("disponibleDesde") LocalDateTime disponibleDesde,
                                  @Param("disponibleHasta") LocalDateTime disponibleHasta);

    /**
     * Busca servicios con reservas en un período específico
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Lista de servicios con reservas en el período
     */
    @Query("SELECT DISTINCT s FROM Servicio s " +
           "JOIN s.reservas r " +
           "WHERE r.fechaInicioServicio BETWEEN :fechaInicio AND :fechaFin " +
           "AND r.estadoReserva != 'cancelada' " +
           "ORDER BY s.nombreServicio")
    List<Servicio> findServiciosConReservasEnPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                    @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Obtiene servicios sin reservas recientes
     * 
     * @param diasAtrás Días hacia atrás para considerar "reciente"
     * @return Lista de servicios sin reservas recientes
     */
    @Query("SELECT s FROM Servicio s " +
           "WHERE s.estado = 'activo' AND " +
           "NOT EXISTS (SELECT 1 FROM Reserva r WHERE r.servicio.idServicio = s.idServicio " +
           "AND r.fechaReserva >= :fechaLimite)")
    List<Servicio> findServiciosSinReservasRecientes(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Busca servicios por etiquetas
     * 
     * @param etiqueta Etiqueta a buscar
     * @return Lista de servicios con la etiqueta especificada
     */
    @Query("SELECT s FROM Servicio s WHERE s.etiquetas LIKE CONCAT('%', :etiqueta, '%') AND s.estado = 'activo'")
    List<Servicio> findByEtiqueta(@Param("etiqueta") String etiqueta);

    /**
     * Obtiene servicios con precio promedio por tipo
     * 
     * @return Lista de arrays con [tipo_servicio, precio_promedio, cantidad_servicios]
     */
    @Query("SELECT s.tipoServicio, AVG(s.precioBase), COUNT(*) " +
           "FROM Servicio s WHERE s.estado = 'activo' " +
           "GROUP BY s.tipoServicio " +
           "ORDER BY AVG(s.precioBase)")
    List<Object[]> obtenerPrecioPromedioPorTipo();

    /**
     * Verifica si un servicio tiene reservas activas
     * 
     * @param idServicio ID del servicio
     * @return true si tiene reservas activas
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reserva r WHERE r.servicio.idServicio = :idServicio " +
           "AND r.estadoReserva IN ('pendiente', 'confirmada', 'pagada')")
    boolean tieneReservasActivas(@Param("idServicio") Long idServicio);

    /**
     * Obtiene servicios para reporte de ocupación
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de arrays con [servicio, total_reservas, capacidad_total, ocupacion_porcentaje]
     */
    @Query("SELECT s, COUNT(r), s.capacidadMaxima, " +
           "(COUNT(r) * 100.0 / s.capacidadMaxima) " +
           "FROM Servicio s LEFT JOIN s.reservas r " +
           "WHERE r.fechaInicioServicio BETWEEN :fechaInicio AND :fechaFin " +
           "AND r.estadoReserva != 'cancelada' " +
           "GROUP BY s.idServicio " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> obtenerReporteOcupacion(@Param("fechaInicio") LocalDateTime fechaInicio,
                                          @Param("fechaFin") LocalDateTime fechaFin);
}

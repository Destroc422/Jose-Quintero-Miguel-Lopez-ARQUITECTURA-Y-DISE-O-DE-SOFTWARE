package com.hermosacartagena.repository;

import com.hermosacartagena.entity.Reserva;
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
 * REPOSITORIO RESERVA
 * =============================================
 * 
 * Repositorio Spring Data JPA para la entidad Reserva.
 * Proporciona métodos CRUD personalizados y consultas específicas
 * para gestión de reservas de servicios turísticos.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * Busca una reserva por su código único
     * 
     * @param codigoReserva Código de la reserva
     * @return Optional con la reserva encontrada
     */
    Optional<Reserva> findByCodigoReserva(String codigoReserva);

    /**
     * Verifica si existe una reserva por su código
     * 
     * @param codigoReserva Código de la reserva
     * @return true si existe
     */
    boolean existsByCodigoReserva(String codigoReserva);

    /**
     * Busca reservas por cliente
     * 
     * @param idCliente ID del cliente
     * @return Lista de reservas del cliente
     */
    List<Reserva> findByIdCliente(Long idCliente);

    /**
     * Busca reservas por servicio
     * 
     * @param idServicio ID del servicio
     * @return Lista de reservas del servicio
     */
    List<Reserva> findByIdServicio(Long idServicio);

    /**
     * Busca reservas por empleado
     * 
     * @param idEmpleado ID del empleado
     * @return Lista de reservas atendidas por el empleado
     */
    List<Reserva> findByIdEmpleado(Long idEmpleado);

    /**
     * Busca reservas por estado
     * 
     * @param estadoReserva Estado de la reserva
     * @return Lista de reservas con el estado especificado
     */
    List<Reserva> findByEstadoReserva(String estadoReserva);

    /**
     * Busca reservas activas (no canceladas)
     * 
     * @return Lista de reservas activas
     */
    @Query("SELECT r FROM Reserva r WHERE r.cancelada = false ORDER BY r.fechaInicioServicio DESC")
    List<Reserva> findActivas();

    /**
     * Busca reservas confirmadas
     * 
     * @return Lista de reservas confirmadas
     */
    @Query("SELECT r FROM Reserva r WHERE r.confirmada = true AND r.cancelada = false ORDER BY r.fechaInicioServicio")
    List<Reserva> findConfirmadas();

    /**
     * Busca reservas pendientes de pago
     * 
     * @return Lista de reservas pendientes de pago
     */
    @Query("SELECT r FROM Reserva r WHERE r.estadoReserva = 'pendiente_pago' AND r.cancelada = false ORDER BY r.fechaReserva")
    List<Reserva> findPendientesPago();

    /**
     * Busca reservas canceladas
     * 
     * @return Lista de reservas canceladas
     */
    @Query("SELECT r FROM Reserva r WHERE r.cancelada = true ORDER BY r.fechaCancelacion DESC")
    List<Reserva> findCanceladas();

    /**
     * Busca reservas por cliente y estado
     * 
     * @param idCliente ID del cliente
     * @param estadoReserva Estado de la reserva
     * @return Lista de reservas del cliente con estado específico
     */
    @Query("SELECT r FROM Reserva r WHERE r.idCliente = :idCliente AND r.estadoReserva = :estadoReserva ORDER BY r.fechaInicioServicio DESC")
    List<Reserva> findByIdClienteAndEstadoReserva(@Param("idCliente") Long idCliente, 
                                                  @Param("estadoReserva") String estadoReserva);

    /**
     * Busca reservas en un rango de fechas
     * 
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de reservas en el rango de fechas
     */
    @Query("SELECT r FROM Reserva r WHERE r.fechaInicioServicio BETWEEN :fechaInicio AND :fechaFin " +
           "AND r.cancelada = false ORDER BY r.fechaInicioServicio")
    List<Reserva> findByRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                   @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca reservas próximas (próximos 7 días)
     * 
     * @return Lista de reservas próximas
     */
    @Query("SELECT r FROM Reserva r WHERE r.fechaInicioServicio BETWEEN CURRENT_TIMESTAMP AND :proximaSemana " +
           "AND r.cancelada = false ORDER BY r.fechaInicioServicio")
    List<Reserva> findReservasProximas(@Param("proximaSemana") LocalDateTime proximaSemana);

    /**
     * Busca reservas en progreso actualmente
     * 
     * @return Lista de reservas en progreso
     */
    @Query("SELECT r FROM Reserva r WHERE CURRENT_TIMESTAMP BETWEEN r.fechaInicioServicio AND r.fechaFinServicio " +
           "AND r.cancelada = false")
    List<Reserva> findReservasEnProgreso();

    /**
     * Busca reservas ya finalizadas
     * 
     * @return Lista de reservas finalizadas
     */
    @Query("SELECT r FROM Reserva r WHERE r.fechaFinServicio < CURRENT_TIMESTAMP AND r.cancelada = false " +
           "ORDER BY r.fechaFinServicio DESC")
    List<Reserva> findReservasFinalizadas();

    /**
     * Busca reservas por texto en código o nombre de cliente/servicio
     * 
     * @param texto Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de reservas que coinciden
     */
    @Query("SELECT r FROM Reserva r WHERE " +
           "LOWER(r.codigoReserva) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(r.cliente.usuario.nombreCompleto) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(r.servicio.nombreServicio) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Reserva> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

    /**
     * Obtiene estadísticas de reservas por estado
     * 
     * @return Lista de arrays con [estado, cantidad, monto_total]
     */
    @Query("SELECT r.estadoReserva, COUNT(*), SUM(r.precioTotal) " +
           "FROM Reserva r GROUP BY r.estadoReserva ORDER BY COUNT(*) DESC")
    List<Object[]> obtenerEstadisticasPorEstado();

    /**
     * Obtiene estadísticas generales de reservas
     * 
     * @return Array con [total, activas, canceladas, confirmadas, pagadas, pendientes_pago, monto_total]
     */
    @Query("SELECT " +
           "COUNT(*) as total, " +
           "SUM(CASE WHEN r.cancelada = false THEN 1 ELSE 0 END) as activas, " +
           "SUM(CASE WHEN r.cancelada = true THEN 1 ELSE 0 END) as canceladas, " +
           "SUM(CASE WHEN r.confirmada = true THEN 1 ELSE 0 END) as confirmadas, " +
           "SUM(CASE WHEN r.estadoReserva = 'pagada' THEN 1 ELSE 0 END) as pagadas, " +
           "SUM(CASE WHEN r.estadoReserva = 'pendiente_pago' THEN 1 ELSE 0 END) as pendientes_pago, " +
           "SUM(r.precioTotal) as monto_total " +
           "FROM Reserva r")
    Object[] obtenerEstadisticasGenerales();

    /**
     * Obtiene reservas por mes y año
     * 
     * @param año Año a consultar
     * @return Lista de arrays con [mes, cantidad, monto_total]
     */
    @Query("SELECT MONTH(r.fechaInicioServicio), COUNT(*), SUM(r.precioTotal) " +
           "FROM Reserva r WHERE YEAR(r.fechaInicioServicio) = :año AND r.cancelada = false " +
           "GROUP BY MONTH(r.fechaInicioServicio) ORDER BY MONTH(r.fechaInicioServicio)")
    List<Object[]> obtenerReservasPorMes(@Param("año") int año);

    /**
     * Obtiene reservas por servicio en un período
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Lista de arrays con [id_servicio, nombre_servicio, cantidad, monto_total]
     */
    @Query("SELECT s.idServicio, s.nombreServicio, COUNT(r), SUM(r.precioTotal) " +
           "FROM Reserva r JOIN r.servicio s " +
           "WHERE r.fechaInicioServicio BETWEEN :fechaInicio AND :fechaFin AND r.cancelada = false " +
           "GROUP BY s.idServicio, s.nombreServicio " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> obtenerReservasPorServicioEnPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Confirma una reserva
     * 
     * @param idReserva ID de la reserva
     * @param fechaConfirmacion Fecha de confirmación
     */
    @Modifying
    @Query("UPDATE Reserva r SET r.confirmada = true, r.fechaConfirmacion = :fechaConfirmacion, " +
           "r.estadoReserva = 'confirmada' WHERE r.idReserva = :idReserva")
    void confirmarReserva(@Param("idReserva") Long idReserva, @Param("fechaConfirmacion") LocalDateTime fechaConfirmacion);

    /**
     * Cancela una reserva
     * 
     * @param idReserva ID de la reserva
     * @param fechaCancelacion Fecha de cancelación
     * @param motivoCancelacion Motivo de la cancelación
     */
    @Modifying
    @Query("UPDATE Reserva r SET r.cancelada = true, r.fechaCancelacion = :fechaCancelacion, " +
           "r.motivoCancelacion = :motivoCancelacion, r.estadoReserva = 'cancelada' WHERE r.idReserva = :idReserva")
    void cancelarReserva(@Param("idReserva") Long idReserva, 
                        @Param("fechaCancelacion") LocalDateTime fechaCancelacion,
                        @Param("motivoCancelacion") String motivoCancelacion);

    /**
     * Marca una reserva como pagada
     * 
     * @param idReserva ID de la reserva
     */
    @Modifying
    @Query("UPDATE Reserva r SET r.estadoReserva = 'pagada' WHERE r.idReserva = :idReserva")
    void marcarComoPagada(@Param("idReserva") Long idReserva);

    /**
     * Asigna un empleado a una reserva
     * 
     * @param idReserva ID de la reserva
     * @param idEmpleado ID del empleado
     */
    @Modifying
    @Query("UPDATE Reserva r SET r.idEmpleado = :idEmpleado WHERE r.idReserva = :idReserva")
    void asignarEmpleado(@Param("idReserva") Long idReserva, @Param("idEmpleado") Long idEmpleado);

    /**
     * Actualiza el método de pago de una reserva
     * 
     * @param idReserva ID de la reserva
     * @param metodoPago Método de pago
     * @param referenciaPago Referencia de pago
     */
    @Modifying
    @Query("UPDATE Reserva r SET r.metodoPago = :metodoPago, r.referenciaPago = :referenciaPago " +
           "WHERE r.idReserva = :idReserva")
    void actualizarMetodoPago(@Param("idReserva") Long idReserva, 
                             @Param("metodoPago") String metodoPago,
                             @Param("referenciaPago") String referenciaPago);

    /**
     * Obtiene reservas para reporte de ingresos
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Lista de arrays con [fecha, cantidad_reservas, monto_total]
     */
    @Query("SELECT DATE(r.fechaInicioServicio), COUNT(*), SUM(r.precioTotal) " +
           "FROM Reserva r WHERE r.fechaInicioServicio BETWEEN :fechaInicio AND :fechaFin " +
           "AND r.cancelada = false " +
           "GROUP BY DATE(r.fechaInicioServicio) " +
           "ORDER BY DATE(r.fechaInicioServicio)")
    List<Object[]> obtenerReporteIngresosDiarios(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca reservas con conflictos de disponibilidad
     * 
     * @param idServicio ID del servicio
     * @param fechaInicio Fecha de inicio a verificar
     * @param fechaFin Fecha de fin a verificar
     * @param cantidadPersonas Cantidad de personas
     * @return Lista de reservas con conflictos
     */
    @Query("SELECT r FROM Reserva r WHERE r.idServicio = :idServicio " +
           "AND r.fechaInicioServicio < :fechaFin AND r.fechaFinServicio > :fechaInicio " +
           "AND r.cancelada = false " +
           "ORDER BY r.fechaInicioServicio")
    List<Reserva> findReservasConflictoDisponibilidad(@Param("idServicio") Long idServicio,
                                                    @Param("fechaInicio") LocalDateTime fechaInicio,
                                                    @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Obtiene reservas por cliente con pagos pendientes
     * 
     * @param idCliente ID del cliente
     * @return Lista de reservas con pagos pendientes
     */
    @Query("SELECT r FROM Reserva r WHERE r.idCliente = :idCliente " +
           "AND r.estadoReserva = 'pendiente_pago' AND r.cancelada = false " +
           "ORDER BY r.fechaInicioServicio")
    List<Reserva> findReservasConPagosPendientesPorCliente(@Param("idCliente") Long idCliente);

    /**
     * Verifica disponibilidad de un servicio en un rango de fechas
     * 
     * @param idServicio ID del servicio
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param cantidadPersonas Cantidad de personas
     * @return true si hay disponibilidad
     */
    @Query("SELECT CASE WHEN COUNT(r) = 0 THEN true ELSE false END " +
           "FROM Reserva r WHERE r.idServicio = :idServicio " +
           "AND r.fechaInicioServicio < :fechaFin AND r.fechaFinServicio > :fechaInicio " +
           "AND r.cancelada = false")
    boolean verificarDisponibilidad(@Param("idServicio") Long idServicio,
                                   @Param("fechaInicio") LocalDateTime fechaInicio,
                                   @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Obtiene reservas para notificaciones automáticas
     * 
     * @param fechaNotificacion Fecha para enviar notificaciones
     * @param horasAntes Horas antes del servicio para notificar
     * @return Lista de reservas que necesitan notificación
     */
    @Query("SELECT r FROM Reserva r WHERE " +
           "DATE_ADD(r.fechaInicioServicio, HOUR, -:horasAntes) <= :fechaNotificacion " +
           "AND r.fechaInicioServicio > :fechaNotificacion " +
           "AND r.confirmada = true AND r.cancelada = false")
    List<Reserva> findReservasParaNotificacion(@Param("fechaNotificacion") LocalDateTime fechaNotificacion,
                                               @Param("horasAntes") int horasAntes);

    /**
     * Obtiene reservas por empleado en un período
     * 
     * @param idEmpleado ID del empleado
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Lista de reservas atendidas por el empleado
     */
    @Query("SELECT r FROM Reserva r WHERE r.idEmpleado = :idEmpleado " +
           "AND r.fechaInicioServicio BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY r.fechaInicioServicio")
    List<Reserva> findByIdEmpleadoAndRangoFechas(@Param("idEmpleado") Long idEmpleado,
                                                @Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin);
}

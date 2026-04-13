package com.hermosacartagena.stub;

import com.hermosacartagena.model.ServicioTuristico;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * INTERFAZ SERVICIO STUB (REMOTE)
 * =============================================
 * 
 * Esta interfaz define los métodos remotos que serán
 * implementados por el Stub del servicio.
 * 
 * @author Sistema de Gestión Turística
 * @version 1.0
 */
public interface ServicioStub extends Remote {
    
    /**
     * Obtiene todos los servicios turísticos disponibles
     * @return Lista de servicios turísticos
     * @throws RemoteException Si hay error en la comunicación
     */
    List<ServicioTuristico> obtenerTodosLosServicios() throws RemoteException;
    
    /**
     * Busca servicios por tipo
     * @param tipoServicio Tipo de servicio a buscar
     * @return Lista de servicios del tipo especificado
     * @throws RemoteException Si hay error en la comunicación
     */
    List<ServicioTuristico> buscarServiciosPorTipo(String tipoServicio) throws RemoteException;
    
    /**
     * Busca servicios por nombre o descripción
     * @param terminoBusqueda Término de búsqueda
     * @return Lista de servicios que coinciden con la búsqueda
     * @throws RemoteException Si hay error en la comunicación
     */
    List<ServicioTuristico> buscarServiciosPorNombre(String terminoBusqueda) throws RemoteException;
    
    /**
     * Obtiene un servicio específico por su ID
     * @param idServicio ID del servicio a obtener
     * @return Servicio turístico encontrado
     * @throws RemoteException Si hay error en la comunicación
     */
    ServicioTuristico obtenerServicioPorId(int idServicio) throws RemoteException;
    
    /**
     * Crea un nuevo servicio turístico
     * @param servicio Servicio turístico a crear
     * @return true si se creó exitosamente
     * @throws RemoteException Si hay error en la comunicación
     */
    boolean crearServicio(ServicioTuristico servicio) throws RemoteException;
    
    /**
     * Actualiza un servicio turístico existente
     * @param servicio Servicio turístico con los datos actualizados
     * @return true si se actualizó exitosamente
     * @throws RemoteException Si hay error en la comunicación
     */
    boolean actualizarServicio(ServicioTuristico servicio) throws RemoteException;
    
    /**
     * Elimina (desactiva) un servicio turístico
     * @param idServicio ID del servicio a eliminar
     * @return true si se eliminó exitosamente
     * @throws RemoteException Si hay error en la comunicación
     */
    boolean eliminarServicio(int idServicio) throws RemoteException;
    
    /**
     * Verifica la disponibilidad de un servicio para una fecha y cantidad de personas
     * @param idServicio ID del servicio
     * @param fecha Fecha del servicio (formato YYYY-MM-DD)
     * @param cantidadPersonas Número de personas
     * @return true si hay disponibilidad
     * @throws RemoteException Si hay error en la comunicación
     */
    boolean verificarDisponibilidad(int idServicio, String fecha, int cantidadPersonas) throws RemoteException;
    
    /**
     * Obtiene servicios disponibles para un rango de fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param cantidadPersonas Número de personas
     * @return Lista de servicios disponibles
     * @throws RemoteException Si hay error en la comunicación
     */
    List<ServicioTuristico> obtenerServiciosDisponibles(String fechaInicio, String fechaFin, int cantidadPersonas) throws RemoteException;
    
    /**
     * Obtiene los servicios más populares
     * @param limite Límite de resultados
     * @return Lista de servicios populares
     * @throws RemoteException Si hay error en la comunicación
     */
    List<ServicioTuristico> obtenerServiciosPopulares(int limite) throws RemoteException;
    
    /**
     * Obtiene estadísticas de servicios
     * @return Array con estadísticas [total, activos, inactivos, tours, hospedaje, transporte, alimentacion, actividades]
     * @throws RemoteException Si hay error en la comunicación
     */
    int[] obtenerEstadisticasServicios() throws RemoteException;
    
    /**
     * Prueba la conexión con el servidor
     * @return "OK" si la conexión es exitosa
     * @throws RemoteException Si hay error en la comunicación
     */
    String probarConexion() throws RemoteException;
}

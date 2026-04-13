package com.hermosacartagena.server;

import com.hermosacartagena.model.ServicioTuristico;
import com.hermosacartagena.stub.ServicioStub;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * SERVIDOR DE SERVICIOS (RMI)
 * =============================================
 * 
 * Este servidor implementa la interfaz ServicioStub y
 * proporciona servicios remotos para la gestión de servicios turísticos.
 * 
 * @author Sistema de Gestión Turística
 * @version 1.0
 */
public class ServidorServicios extends UnicastRemoteObject implements ServicioStub {
    
    private static final long serialVersionUID = 1L;
    
    // Configuración de la base de datos
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hermosa_cartagena";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Puerto RMI
    private static final int RMI_PORT = 1099;
    
    /**
     * Constructor del servidor
     * @throws RemoteException Si hay error en la creación del objeto remoto
     */
    public ServidorServicios() throws RemoteException {
        super();
        System.out.println("Servidor de servicios inicializado");
    }
    
    /**
     * Método principal para iniciar el servidor
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        try {
            System.out.println("Iniciando servidor de servicios turísticos...");
            
            // Crear y registrar el servidor RMI
            ServidorServicios servidor = new ServidorServicios();
            
            // Crear el registro RMI
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            
            // Registrar el servicio remoto
            registry.rebind("ServicioTuristico", servidor);
            
            System.out.println("Servidor RMI iniciado en el puerto " + RMI_PORT);
            System.out.println("Servicio 'ServicioTuristico' registrado y disponible");
            System.out.println("Esperando solicitudes de clientes...");
            
            // Mantener el servidor en ejecución
            while (true) {
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene conexión a la base de datos
     * @return Conexión a la base de datos
     * @throws SQLException Si hay error en la conexión
     */
    private Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado", e);
        }
    }
    
    /**
     * Cierra la conexión a la base de datos
     * @param conn Conexión a cerrar
     */
    private void cerrarConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto ServicioTuristico
     * @param rs ResultSet con los datos
     * @return Objeto ServicioTuristico
     * @throws SQLException Si hay error en el mapeo
     */
    private ServicioTuristico mapearServicio(ResultSet rs) throws SQLException {
        ServicioTuristico servicio = new ServicioTuristico();
        servicio.setIdServicio(rs.getInt("id_servicio"));
        servicio.setIdProveedor(rs.getInt("id_proveedor"));
        servicio.setNombreServicio(rs.getString("nombre_servicio"));
        servicio.setDescripcion(rs.getString("descripcion"));
        servicio.setTipoServicio(rs.getString("tipo_servicio"));
        servicio.setPrecioBase(rs.getDouble("precio_base"));
        servicio.setDuracionHoras(rs.getDouble("duracion_horas"));
        servicio.setCapacidadMaxima(rs.getInt("capacidad_maxima"));
        servicio.setUbicacion(rs.getString("ubicacion"));
        servicio.setRequisitos(rs.getString("requisitos"));
        servicio.setIncluye(rs.getString("incluye"));
        servicio.setNoIncluye(rs.getString("no_incluye"));
        servicio.setImagenes(rs.getString("imagenes"));
        servicio.setEstado(rs.getString("estado"));
        
        // Mapear fechas
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            servicio.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        
        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            servicio.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }
        
        // Mapear datos del proveedor si están disponibles
        try {
            servicio.setNombreProveedor(rs.getString("nombre_empresa"));
            servicio.setContactoPrincipal(rs.getString("contacto_principal"));
            servicio.setTelefonoProveedor(rs.getString("telefono_proveedor"));
        } catch (SQLException e) {
            // Estos campos pueden no estar disponibles en todas las consultas
        }
        
        return servicio;
    }
    
    @Override
    public List<ServicioTuristico> obtenerTodosLosServicios() throws RemoteException {
        List<ServicioTuristico> servicios = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = obtenerConexion();
            String sql = "SELECT s.*, p.nombre_empresa, p.contacto_principal, p.telefono as telefono_proveedor " +
                        "FROM servicios s " +
                        "JOIN proveedores p ON s.id_proveedor = p.id_proveedor " +
                        "ORDER BY s.nombre_servicio";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("Se obtuvieron " + servicios.size() + " servicios");
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los servicios: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return servicios;
    }
    
    @Override
    public List<ServicioTuristico> buscarServiciosPorTipo(String tipoServicio) throws RemoteException {
        List<ServicioTuristico> servicios = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = obtenerConexion();
            String sql = "SELECT s.*, p.nombre_empresa, p.contacto_principal, p.telefono as telefono_proveedor " +
                        "FROM servicios s " +
                        "JOIN proveedores p ON s.id_proveedor = p.id_proveedor " +
                        "WHERE s.tipo_servicio = ? AND s.estado = 'activo' " +
                        "ORDER BY s.nombre_servicio";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipoServicio);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("Se encontraron " + servicios.size() + " servicios del tipo: " + tipoServicio);
            
        } catch (SQLException e) {
            System.err.println("Error al buscar servicios por tipo: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return servicios;
    }
    
    @Override
    public List<ServicioTuristico> buscarServiciosPorNombre(String terminoBusqueda) throws RemoteException {
        List<ServicioTuristico> servicios = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = obtenerConexion();
            String sql = "SELECT s.*, p.nombre_empresa, p.contacto_principal, p.telefono as telefono_proveedor " +
                        "FROM servicios s " +
                        "JOIN proveedores p ON s.id_proveedor = p.id_proveedor " +
                        "WHERE (s.nombre_servicio LIKE ? OR s.descripcion LIKE ? OR s.ubicacion LIKE ?) " +
                        "AND s.estado = 'activo' " +
                        "ORDER BY s.nombre_servicio";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            String searchParam = "%" + terminoBusqueda + "%";
            stmt.setString(1, searchParam);
            stmt.setString(2, searchParam);
            stmt.setString(3, searchParam);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("Se encontraron " + servicios.size() + " servicios con: " + terminoBusqueda);
            
        } catch (SQLException e) {
            System.err.println("Error al buscar servicios por nombre: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return servicios;
    }
    
    @Override
    public ServicioTuristico obtenerServicioPorId(int idServicio) throws RemoteException {
        ServicioTuristico servicio = null;
        Connection conn = null;
        
        try {
            conn = obtenerConexion();
            String sql = "SELECT s.*, p.nombre_empresa, p.contacto_principal, p.telefono as telefono_proveedor " +
                        "FROM servicios s " +
                        "JOIN proveedores p ON s.id_proveedor = p.id_proveedor " +
                        "WHERE s.id_servicio = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idServicio);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                servicio = mapearServicio(rs);
            }
            
            rs.close();
            stmt.close();
            
            if (servicio != null) {
                System.out.println("Se obtuvo el servicio: " + servicio.getNombreServicio());
            } else {
                System.out.println("No se encontró el servicio con ID: " + idServicio);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener servicio por ID: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return servicio;
    }
    
    @Override
    public boolean crearServicio(ServicioTuristico servicio) throws RemoteException {
        if (!servicio.validar()) {
            throw new RemoteException("Datos del servicio inválidos");
        }
        
        Connection conn = null;
        boolean exito = false;
        
        try {
            conn = obtenerConexion();
            conn.setAutoCommit(false);
            
            String sql = "INSERT INTO servicios " +
                        "(id_proveedor, nombre_servicio, descripcion, tipo_servicio, precio_base, " +
                        "duracion_horas, capacidad_maxima, ubicacion, requisitos, incluye, no_incluye, " +
                        "imagenes, estado) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, servicio.getIdProveedor());
            stmt.setString(2, servicio.getNombreServicio());
            stmt.setString(3, servicio.getDescripcion());
            stmt.setString(4, servicio.getTipoServicio());
            stmt.setDouble(5, servicio.getPrecioBase());
            stmt.setDouble(6, servicio.getDuracionHoras());
            stmt.setInt(7, servicio.getCapacidadMaxima());
            stmt.setString(8, servicio.getUbicacion());
            stmt.setString(9, servicio.getRequisitos());
            stmt.setString(10, servicio.getIncluye());
            stmt.setString(11, servicio.getNoIncluye());
            stmt.setString(12, servicio.getImagenes());
            stmt.setString(13, servicio.getEstado());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    servicio.setIdServicio(rs.getInt(1));
                }
                rs.close();
                
                conn.commit();
                exito = true;
                
                System.out.println("Servicio creado exitosamente: " + servicio.getNombreServicio());
            } else {
                conn.rollback();
                System.out.println("No se pudo crear el servicio");
            }
            
            stmt.close();
            
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            
            System.err.println("Error al crear servicio: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return exito;
    }
    
    @Override
    public boolean actualizarServicio(ServicioTuristico servicio) throws RemoteException {
        if (!servicio.validar()) {
            throw new RemoteException("Datos del servicio inválidos");
        }
        
        Connection conn = null;
        boolean exito = false;
        
        try {
            conn = obtenerConexion();
            conn.setAutoCommit(false);
            
            String sql = "UPDATE servicios SET " +
                        "id_proveedor = ?, nombre_servicio = ?, descripcion = ?, tipo_servicio = ?, " +
                        "precio_base = ?, duracion_horas = ?, capacidad_maxima = ?, ubicacion = ?, " +
                        "requisitos = ?, incluye = ?, no_incluye = ?, imagenes = ?, estado = ?, " +
                        "fecha_actualizacion = CURRENT_TIMESTAMP " +
                        "WHERE id_servicio = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, servicio.getIdProveedor());
            stmt.setString(2, servicio.getNombreServicio());
            stmt.setString(3, servicio.getDescripcion());
            stmt.setString(4, servicio.getTipoServicio());
            stmt.setDouble(5, servicio.getPrecioBase());
            stmt.setDouble(6, servicio.getDuracionHoras());
            stmt.setInt(7, servicio.getCapacidadMaxima());
            stmt.setString(8, servicio.getUbicacion());
            stmt.setString(9, servicio.getRequisitos());
            stmt.setString(10, servicio.getIncluye());
            stmt.setString(11, servicio.getNoIncluye());
            stmt.setString(12, servicio.getImagenes());
            stmt.setString(13, servicio.getEstado());
            stmt.setInt(14, servicio.getIdServicio());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                conn.commit();
                exito = true;
                
                System.out.println("Servicio actualizado exitosamente: " + servicio.getNombreServicio());
            } else {
                conn.rollback();
                System.out.println("No se encontró el servicio para actualizar");
            }
            
            stmt.close();
            
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            
            System.err.println("Error al actualizar servicio: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return exito;
    }
    
    @Override
    public boolean eliminarServicio(int idServicio) throws RemoteException {
        Connection conn = null;
        boolean exito = false;
        
        try {
            conn = obtenerConexion();
            conn.setAutoCommit(false);
            
            String sql = "UPDATE servicios SET estado = 'inactivo', fecha_actualizacion = CURRENT_TIMESTAMP " +
                        "WHERE id_servicio = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idServicio);
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                conn.commit();
                exito = true;
                
                System.out.println("Servicio eliminado (desactivado) exitosamente. ID: " + idServicio);
            } else {
                conn.rollback();
                System.out.println("No se encontró el servicio para eliminar");
            }
            
            stmt.close();
            
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            
            System.err.println("Error al eliminar servicio: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return exito;
    }
    
    @Override
    public boolean verificarDisponibilidad(int idServicio, String fecha, int cantidadPersonas) throws RemoteException {
        Connection conn = null;
        boolean disponible = false;
        
        try {
            conn = obtenerConexion();
            
            // Obtener capacidad del servicio
            String sqlCapacidad = "SELECT capacidad_maxima FROM servicios WHERE id_servicio = ? AND estado = 'activo'";
            PreparedStatement stmtCapacidad = conn.prepareStatement(sqlCapacidad);
            stmtCapacidad.setInt(1, idServicio);
            
            ResultSet rsCapacidad = stmtCapacidad.executeQuery();
            
            if (rsCapacidad.next()) {
                int capacidad = rsCapacidad.getInt("capacidad_maxima");
                
                // Contar reservas existentes para esa fecha
                String sqlReservas = "SELECT COALESCE(SUM(cantidad_personas), 0) as total_reservado " +
                                    "FROM reservas " +
                                    "WHERE id_servicio = ? AND fecha_inicio_servicio = ? " +
                                    "AND estado_reserva IN ('pendiente', 'confirmada', 'pagada')";
                
                PreparedStatement stmtReservas = conn.prepareStatement(sqlReservas);
                stmtReservas.setInt(1, idServicio);
                stmtReservas.setString(2, fecha);
                
                ResultSet rsReservas = stmtReservas.executeQuery();
                
                if (rsReservas.next()) {
                    int totalReservado = rsReservas.getInt("total_reservado");
                    disponible = (totalReservado + cantidadPersonas) <= capacidad;
                }
                
                rsReservas.close();
                stmtReservas.close();
            }
            
            rsCapacidad.close();
            stmtCapacidad.close();
            
            System.out.println("Disponibilidad verificada - Servicio ID: " + idServicio + 
                             ", Fecha: " + fecha + ", Personas: " + cantidadPersonas + 
                             ", Disponible: " + disponible);
            
        } catch (SQLException e) {
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return disponible;
    }
    
    @Override
    public List<ServicioTuristico> obtenerServiciosDisponibles(String fechaInicio, String fechaFin, int cantidadPersonas) throws RemoteException {
        List<ServicioTuristico> servicios = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = obtenerConexion();
            
            String sql = "SELECT s.*, p.nombre_empresa, p.contacto_principal, p.telefono as telefono_proveedor " +
                        "FROM servicios s " +
                        "JOIN proveedores p ON s.id_proveedor = p.id_proveedor " +
                        "WHERE s.estado = 'activo' " +
                        "AND s.capacidad_maxima >= ? " +
                        "AND s.id_servicio NOT IN (" +
                        "    SELECT DISTINCT r.id_servicio " +
                        "    FROM reservas r " +
                        "    WHERE r.fecha_inicio_servicio BETWEEN ? AND ? " +
                        "    AND r.estado_reserva IN ('pendiente', 'confirmada', 'pagada')" +
                        "    AND (r.cantidad_personas + ?) > s.capacidad_maxima" +
                        ") " +
                        "ORDER BY s.nombre_servicio";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cantidadPersonas);
            stmt.setString(2, fechaInicio);
            stmt.setString(3, fechaFin);
            stmt.setInt(4, cantidadPersonas);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("Se encontraron " + servicios.size() + " servicios disponibles para " + 
                             cantidadPersonas + " personas entre " + fechaInicio + " y " + fechaFin);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener servicios disponibles: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return servicios;
    }
    
    @Override
    public List<ServicioTuristico> obtenerServiciosPopulares(int limite) throws RemoteException {
        List<ServicioTuristico> servicios = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = obtenerConexion();
            
            String sql = "SELECT s.*, p.nombre_empresa, p.contacto_principal, p.telefono as telefono_proveedor, " +
                        "COUNT(r.id_reserva) as total_reservas " +
                        "FROM servicios s " +
                        "JOIN proveedores p ON s.id_proveedor = p.id_proveedor " +
                        "LEFT JOIN reservas r ON s.id_servicio = r.id_servicio " +
                        "WHERE s.estado = 'activo' " +
                        "GROUP BY s.id_servicio, s.nombre_servicio, p.nombre_empresa " +
                        "HAVING total_reservas > 0 " +
                        "ORDER BY total_reservas DESC, s.nombre_servicio " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limite);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("Se obtuvieron " + servicios.size() + " servicios populares (límite: " + limite + ")");
            
        } catch (SQLException e) {
            System.err.println("Error al obtener servicios populares: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return servicios;
    }
    
    @Override
    public int[] obtenerEstadisticasServicios() throws RemoteException {
        int[] estadisticas = new int[8]; // [total, activos, inactivos, tours, hospedaje, transporte, alimentacion, actividades]
        Connection conn = null;
        
        try {
            conn = obtenerConexion();
            
            String sql = "SELECT " +
                        "COUNT(*) as total_servicios, " +
                        "SUM(CASE WHEN estado = 'activo' THEN 1 ELSE 0 END) as servicios_activos, " +
                        "SUM(CASE WHEN estado = 'inactivo' THEN 1 ELSE 0 END) as servicios_inactivos, " +
                        "SUM(CASE WHEN tipo_servicio = 'tour' THEN 1 ELSE 0 END) as tours, " +
                        "SUM(CASE WHEN tipo_servicio = 'hospedaje' THEN 1 ELSE 0 END) as hospedaje, " +
                        "SUM(CASE WHEN tipo_servicio = 'transporte' THEN 1 ELSE 0 END) as transporte, " +
                        "SUM(CASE WHEN tipo_servicio = 'alimentacion' THEN 1 ELSE 0 END) as alimentacion, " +
                        "SUM(CASE WHEN tipo_servicio = 'actividad' THEN 1 ELSE 0 END) as actividades " +
                        "FROM servicios";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                estadisticas[0] = rs.getInt("total_servicios");
                estadisticas[1] = rs.getInt("servicios_activos");
                estadisticas[2] = rs.getInt("servicios_inactivos");
                estadisticas[3] = rs.getInt("tours");
                estadisticas[4] = rs.getInt("hospedaje");
                estadisticas[5] = rs.getInt("transporte");
                estadisticas[6] = rs.getInt("alimentacion");
                estadisticas[7] = rs.getInt("actividades");
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("Estadísticas de servicios obtenidas");
            
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            throw new RemoteException("Error de base de datos: " + e.getMessage(), e);
        } finally {
            cerrarConexion(conn);
        }
        
        return estadisticas;
    }
    
    @Override
    public String probarConexion() throws RemoteException {
        try {
            Connection conn = obtenerConexion();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            
            if (rs.next()) {
                rs.close();
                stmt.close();
                cerrarConexion(conn);
                
                System.out.println("Conexión probada exitosamente");
                return "OK";
            }
            
        } catch (SQLException e) {
            System.err.println("Error al probar conexión: " + e.getMessage());
            throw new RemoteException("Error de conexión a base de datos: " + e.getMessage(), e);
        }
        
        return "ERROR";
    }
}

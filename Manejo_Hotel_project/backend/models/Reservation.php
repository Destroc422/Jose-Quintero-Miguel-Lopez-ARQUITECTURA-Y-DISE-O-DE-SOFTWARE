<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * MODELO: RESERVATION
 * =============================================
 * 
 * Este modelo maneja todas las operaciones relacionadas
 * con reservas de servicios turísticos.
 */

require_once __DIR__ . '/../config/database.php';

class Reservation {
    private $conn;
    private $table_name = "reservas";
    
    // Propiedades de la reserva
    public $id_reserva;
    public $id_cliente;
    public $id_servicio;
    public $id_empleado;
    public $codigo_reserva;
    public $fecha_reserva;
    public $fecha_inicio_servicio;
    public $fecha_fin_servicio;
    public $cantidad_personas;
    public $precio_unitario;
    public $precio_total;
    public $estado_reserva;
    public $notas;
    public $fecha_confirmacion;
    public $fecha_cancelacion;
    public $motivo_cancelacion;
    
    // Propiedades adicionales para relaciones
    public $nombre_cliente;
    public $email_cliente;
    public $nombre_servicio;
    public $nombre_empleado;
    public $nombre_proveedor;
    
    /**
     * Constructor - Establece conexión a la base de datos
     */
    public function __construct() {
        $database = new Database();
        $this->conn = $database->getConnection();
    }
    
    /**
     * Crear nueva reserva
     * @return bool
     */
    public function create() {
        try {
            // Generar código de reserva único
            $this->codigo_reserva = $this->generateReservationCode();
            
            // Verificar disponibilidad antes de crear
            if (!$this->checkAvailability()) {
                return false;
            }
            
            $query = "INSERT INTO " . $this->table_name . "
                     (id_cliente, id_servicio, id_empleado, codigo_reserva, fecha_inicio_servicio,
                      fecha_fin_servicio, cantidad_personas, precio_unitario, precio_total, 
                      estado_reserva, notas)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            $stmt = $this->conn->prepare($query);
            
            // Sanitizar y bind parameters
            $this->notas = sanitizeInput($this->notas);
            
            $stmt->bindParam(1, $this->id_cliente);
            $stmt->bindParam(2, $this->id_servicio);
            $stmt->bindParam(3, $this->id_empleado);
            $stmt->bindParam(4, $this->codigo_reserva);
            $stmt->bindParam(5, $this->fecha_inicio_servicio);
            $stmt->bindParam(6, $this->fecha_fin_servicio);
            $stmt->bindParam(7, $this->cantidad_personas);
            $stmt->bindParam(8, $this->precio_unitario);
            $stmt->bindParam(9, $this->precio_total);
            $stmt->bindParam(10, $this->estado_reserva);
            $stmt->bindParam(11, $this->notas);
            
            if ($stmt->execute()) {
                $this->id_reserva = $this->conn->lastInsertId();
                
                // Registrar en historial
                $this->logOperation('creacion', 'Reserva creada: ' . $this->codigo_reserva);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al crear reserva: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Leer todas las reservas
     * @return array
     */
    public function readAll() {
        try {
            $query = "SELECT r.*, 
                            c.nombre_completo as nombre_cliente,
                            c.email as email_cliente,
                            s.nombre_servicio,
                            p.nombre_empresa as nombre_proveedor,
                            e.nombre_completo as nombre_empleado
                     FROM " . $this->table_name . " r
                     JOIN clientes c ON r.id_cliente = c.id_cliente
                     JOIN servicios s ON r.id_servicio = s.id_servicio
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     LEFT JOIN empleados e ON r.id_empleado = e.id_empleado
                     ORDER BY r.fecha_reserva DESC";
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al leer reservas: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Leer reservas de un cliente
     * @param int $id_cliente ID del cliente
     * @return array
     */
    public function readByClient($id_cliente) {
        try {
            $query = "SELECT r.*, 
                            s.nombre_servicio,
                            p.nombre_empresa as nombre_proveedor,
                            e.nombre_completo as nombre_empleado
                     FROM " . $this->table_name . " r
                     JOIN servicios s ON r.id_servicio = s.id_servicio
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     LEFT JOIN empleados e ON r.id_empleado = e.id_empleado
                     WHERE r.id_cliente = ?
                     ORDER BY r.fecha_inicio_servicio DESC";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $id_cliente);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al leer reservas del cliente: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Leer una reserva específica
     * @param int $id_reserva ID de la reserva
     * @return array|null
     */
    public function readOne($id_reserva) {
        try {
            $query = "SELECT r.*, 
                            c.nombre_completo as nombre_cliente,
                            c.email as email_cliente,
                            c.telefono as telefono_cliente,
                            s.nombre_servicio,
                            s.descripcion as descripcion_servicio,
                            s.ubicacion,
                            p.nombre_empresa as nombre_proveedor,
                            p.contacto_principal,
                            p.telefono as telefono_proveedor,
                            e.nombre_completo as nombre_empleado
                     FROM " . $this->table_name . " r
                     JOIN clientes c ON r.id_cliente = c.id_cliente
                     JOIN servicios s ON r.id_servicio = s.id_servicio
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     LEFT JOIN empleados e ON r.id_empleado = e.id_empleado
                     WHERE r.id_reserva = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $id_reserva);
            $stmt->execute();
            
            return $stmt->fetch(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al leer reserva: " . $exception->getMessage());
            return null;
        }
    }
    
    /**
     * Actualizar reserva
     * @return bool
     */
    public function update() {
        try {
            $query = "UPDATE " . $this->table_name . "
                     SET id_servicio = ?, id_empleado = ?, fecha_inicio_servicio = ?,
                         fecha_fin_servicio = ?, cantidad_personas = ?, precio_unitario = ?,
                         precio_total = ?, estado_reserva = ?, notas = ?
                     WHERE id_reserva = ?";
            
            $stmt = $this->conn->prepare($query);
            
            // Sanitizar datos
            $this->notas = sanitizeInput($this->notas);
            
            $stmt->bindParam(1, $this->id_servicio);
            $stmt->bindParam(2, $this->id_empleado);
            $stmt->bindParam(3, $this->fecha_inicio_servicio);
            $stmt->bindParam(4, $this->fecha_fin_servicio);
            $stmt->bindParam(5, $this->cantidad_personas);
            $stmt->bindParam(6, $this->precio_unitario);
            $stmt->bindParam(7, $this->precio_total);
            $stmt->bindParam(8, $this->estado_reserva);
            $stmt->bindParam(9, $this->notas);
            $stmt->bindParam(10, $this->id_reserva);
            
            if ($stmt->execute()) {
                // Registrar en historial
                $this->logOperation('actualizacion', 'Reserva actualizada: ' . $this->codigo_reserva);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al actualizar reserva: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Confirmar reserva
     * @return bool
     */
    public function confirm() {
        try {
            $query = "UPDATE " . $this->table_name . "
                     SET estado_reserva = 'confirmada', fecha_confirmacion = CURRENT_TIMESTAMP
                     WHERE id_reserva = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $this->id_reserva);
            
            if ($stmt->execute()) {
                // Registrar en historial
                $this->logOperation('actualizacion', 'Reserva confirmada: ' . $this->codigo_reserva);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al confirmar reserva: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Cancelar reserva
     * @param string $motivo_cancelacion Motivo de cancelación
     * @return bool
     */
    public function cancel($motivo_cancelacion) {
        try {
            $query = "UPDATE " . $this->table_name . "
                     SET estado_reserva = 'cancelada', 
                         fecha_cancelacion = CURRENT_TIMESTAMP,
                         motivo_cancelacion = ?
                     WHERE id_reserva = ?";
            
            $stmt = $this->conn->prepare($query);
            
            $this->motivo_cancelacion = sanitizeInput($motivo_cancelacion);
            
            $stmt->bindParam(1, $this->motivo_cancelacion);
            $stmt->bindParam(2, $this->id_reserva);
            
            if ($stmt->execute()) {
                // Registrar en historial
                $this->logOperation('cancelacion', 'Reserva cancelada: ' . $this->codigo_reserva . ' - ' . $this->motivo_cancelacion);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al cancelar reserva: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar reserva (lógica)
     * @return bool
     */
    public function delete() {
        try {
            $query = "UPDATE " . $this->table_name . "
                     SET estado_reserva = 'cancelada'
                     WHERE id_reserva = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $this->id_reserva);
            
            if ($stmt->execute()) {
                // Registrar en historial
                $this->logOperation('eliminacion', 'Reserva eliminada: ' . $this->codigo_reserva);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al eliminar reserva: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Buscar reservas por código
     * @param string $codigo_reserva Código de reserva
     * @return array|null
     */
    public function findByCode($codigo_reserva) {
        try {
            $query = "SELECT r.*, 
                            c.nombre_completo as nombre_cliente,
                            c.email as email_cliente,
                            s.nombre_servicio,
                            p.nombre_empresa as nombre_proveedor,
                            e.nombre_completo as nombre_empleado
                     FROM " . $this->table_name . " r
                     JOIN clientes c ON r.id_cliente = c.id_cliente
                     JOIN servicios s ON r.id_servicio = s.id_servicio
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     LEFT JOIN empleados e ON r.id_empleado = e.id_empleado
                     WHERE r.codigo_reserva = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $codigo_reserva);
            $stmt->execute();
            
            return $stmt->fetch(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al buscar reserva por código: " . $exception->getMessage());
            return null;
        }
    }
    
    /**
     * Obtener reservas por estado
     * @param string $estado Estado de la reserva
     * @return array
     */
    public function getByStatus($estado) {
        try {
            $query = "SELECT r.*, 
                            c.nombre_completo as nombre_cliente,
                            s.nombre_servicio,
                            p.nombre_empresa as nombre_proveedor,
                            e.nombre_completo as nombre_empleado
                     FROM " . $this->table_name . " r
                     JOIN clientes c ON r.id_cliente = c.id_cliente
                     JOIN servicios s ON r.id_servicio = s.id_servicio
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     LEFT JOIN empleados e ON r.id_empleado = e.id_empleado
                     WHERE r.estado_reserva = ?
                     ORDER BY r.fecha_inicio_servicio";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $estado);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener reservas por estado: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Obtener reservas por rango de fechas
     * @param string $fecha_inicio Fecha de inicio
     * @param string $fecha_fin Fecha de fin
     * @return array
     */
    public function getByDateRange($fecha_inicio, $fecha_fin) {
        try {
            $query = "SELECT r.*, 
                            c.nombre_completo as nombre_cliente,
                            s.nombre_servicio,
                            p.nombre_empresa as nombre_proveedor,
                            e.nombre_completo as nombre_empleado
                     FROM " . $this->table_name . " r
                     JOIN clientes c ON r.id_cliente = c.id_cliente
                     JOIN servicios s ON r.id_servicio = s.id_servicio
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     LEFT JOIN empleados e ON r.id_empleado = e.id_empleado
                     WHERE r.fecha_inicio_servicio BETWEEN ? AND ?
                     ORDER BY r.fecha_inicio_servicio";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $fecha_inicio);
            $stmt->bindParam(2, $fecha_fin);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener reservas por rango de fechas: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Obtener estadísticas de reservas
     * @return array
     */
    public function getReservationStats() {
        try {
            $query = "SELECT 
                        COUNT(*) as total_reservas,
                        SUM(CASE WHEN estado_reserva = 'pendiente' THEN 1 ELSE 0 END) as pendientes,
                        SUM(CASE WHEN estado_reserva = 'confirmada' THEN 1 ELSE 0 END) as confirmadas,
                        SUM(CASE WHEN estado_reserva = 'pagada' THEN 1 ELSE 0 END) as pagadas,
                        SUM(CASE WHEN estado_reserva = 'cancelada' THEN 1 ELSE 0 END) as canceladas,
                        SUM(CASE WHEN estado_reserva = 'completada' THEN 1 ELSE 0 END) as completadas,
                        SUM(precio_total) as ingresos_totales,
                        AVG(precio_total) as promedio_reserva
                     FROM " . $this->table_name;
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            
            return $stmt->fetch(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener estadísticas de reservas: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Generar código de reserva único
     * @return string
     */
    private function generateReservationCode() {
        do {
            $codigo = 'RES' . date('Y') . str_pad(mt_rand(1, 99999), 5, '0', STR_PAD_LEFT);
            
            $query = "SELECT id_reserva FROM " . $this->table_name . " WHERE codigo_reserva = ?";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $codigo);
            $stmt->execute();
            
        } while ($stmt->rowCount() > 0);
        
        return $codigo;
    }
    
    /**
     * Verificar disponibilidad para la reserva
     * @return bool
     */
    private function checkAvailability() {
        try {
            $query = "SELECT capacidad_maxima 
                     FROM servicios 
                     WHERE id_servicio = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $this->id_servicio);
            $stmt->execute();
            
            $servicio = $stmt->fetch(PDO::FETCH_ASSOC);
            
            if (!$servicio) {
                return false;
            }
            
            // Contar reservas existentes para esa fecha
            $query = "SELECT SUM(cantidad_personas) as total_reservado
                     FROM " . $this->table_name . "
                     WHERE id_servicio = ? AND fecha_inicio_servicio = ?
                     AND estado_reserva IN ('pendiente', 'confirmada', 'pagada')
                     AND id_reserva != ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $this->id_servicio);
            $stmt->bindParam(2, $this->fecha_inicio_servicio);
            $stmt->bindParam(3, $this->id_reserva);
            $stmt->execute();
            
            $reservas = $stmt->fetch(PDO::FETCH_ASSOC);
            $total_reservado = $reservas['total_reservado'] ?? 0;
            
            return ($total_reservado + $this->cantidad_personas) <= $servicio['capacidad_maxima'];
            
        } catch(PDOException $exception) {
            error_log("Error al verificar disponibilidad: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Registrar operación en el historial
     * @param string $tipo_operacion Tipo de operación
     * @param string $descripcion Descripción de la operación
     * @return bool
     */
    private function logOperation($tipo_operacion, $descripcion) {
        try {
            $query = "INSERT INTO historial_operaciones
                     (id_usuario, id_reserva, tipo_operacion, descripcion_operacion, tabla_afectada, id_registro_afectado, ip_address)
                     VALUES (?, ?, ?, 'reservas', ?, ?)";
            
            $stmt = $this->conn->prepare($query);
            
            $user_id = $_SESSION['user_id'] ?? 0;
            $ip_address = $_SERVER['REMOTE_ADDR'] ?? 'unknown';
            
            $stmt->bindParam(1, $user_id);
            $stmt->bindParam(2, $this->id_reserva);
            $stmt->bindParam(3, $tipo_operacion);
            $stmt->bindParam(4, $descripcion);
            $stmt->bindParam(5, $this->id_reserva);
            $stmt->bindParam(6, $ip_address);
            
            return $stmt->execute();
            
        } catch(PDOException $exception) {
            error_log("Error al registrar operación: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Validar datos de la reserva antes de guardar
     * @return array Array de errores
     */
    public function validate() {
        $errors = [];
        
        // Validar cliente
        if (empty($this->id_cliente)) {
            $errors[] = "Debe seleccionar un cliente";
        }
        
        // Validar servicio
        if (empty($this->id_servicio)) {
            $errors[] = "Debe seleccionar un servicio";
        }
        
        // Validar fecha de inicio
        if (empty($this->fecha_inicio_servicio)) {
            $errors[] = "La fecha de inicio del servicio es obligatoria";
        } elseif (strtotime($this->fecha_inicio_servicio) < strtotime(date('Y-m-d'))) {
            $errors[] = "La fecha de inicio no puede ser anterior a hoy";
        }
        
        // Validar cantidad de personas
        if (empty($this->cantidad_personas) || $this->cantidad_personas <= 0) {
            $errors[] = "La cantidad de personas debe ser mayor a 0";
        }
        
        // Validar precio
        if (empty($this->precio_unitario) || $this->precio_unitario <= 0) {
            $errors[] = "El precio unitario debe ser mayor a 0";
        }
        
        if (empty($this->precio_total) || $this->precio_total <= 0) {
            $errors[] = "El precio total debe ser mayor a 0";
        }
        
        // Validar estado
        if (empty($this->estado_reserva) || !in_array($this->estado_reserva, ['pendiente', 'confirmada', 'pagada', 'cancelada', 'completada'])) {
            $errors[] = "Debe seleccionar un estado válido";
        }
        
        return $errors;
    }
    
    /**
     * Obtener reservas próximas (próximos 7 días)
     * @return array
     */
    public function getUpcomingReservations() {
        try {
            $query = "SELECT r.*, 
                            c.nombre_completo as nombre_cliente,
                            s.nombre_servicio,
                            p.nombre_empresa as nombre_proveedor
                     FROM " . $this->table_name . " r
                     JOIN clientes c ON r.id_cliente = c.id_cliente
                     JOIN servicios s ON r.id_servicio = s.id_servicio
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     WHERE r.fecha_inicio_servicio BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
                     AND r.estado_reserva IN ('confirmada', 'pagada')
                     ORDER BY r.fecha_inicio_servicio";
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener reservas próximas: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Calcular precio total basado en cantidad de personas y precio unitario
     * @return float
     */
    public function calculateTotalPrice() {
        return $this->precio_unitario * $this->cantidad_personas;
    }
}
?>

<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * MODELO: SERVICE
 * =============================================
 * 
 * Este modelo maneja todas las operaciones relacionadas
 * con servicios turísticos del sistema.
 */

require_once __DIR__ . '/../config/database.php';

class Service {
    private $conn;
    private $table_name = "servicios";
    
    // Propiedades del servicio
    public $id_servicio;
    public $id_proveedor;
    public $nombre_servicio;
    public $descripcion;
    public $tipo_servicio;
    public $precio_base;
    public $duracion_horas;
    public $capacidad_maxima;
    public $ubicacion;
    public $requisitos;
    public $incluye;
    public $no_incluye;
    public $imagenes;
    public $estado;
    public $fecha_creacion;
    public $fecha_actualizacion;
    
    // Propiedades adicionales para relaciones
    public $nombre_proveedor;
    public $contacto_proveedor;
    public $telefono_proveedor;
    
    /**
     * Constructor - Establece conexión a la base de datos
     */
    public function __construct() {
        $database = new Database();
        $this->conn = $database->getConnection();
    }
    
    /**
     * Crear nuevo servicio
     * @return bool
     */
    public function create() {
        try {
            $query = "INSERT INTO " . $this->table_name . "
                     (id_proveedor, nombre_servicio, descripcion, tipo_servicio, precio_base, 
                      duracion_horas, capacidad_maxima, ubicacion, requisitos, incluye, no_incluye, 
                      imagenes, estado)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            $stmt = $this->conn->prepare($query);
            
            // Sanitizar y bind parameters
            $this->nombre_servicio = sanitizeInput($this->nombre_servicio);
            $this->descripcion = sanitizeInput($this->descripcion);
            $this->ubicacion = sanitizeInput($this->ubicacion);
            $this->requisitos = sanitizeInput($this->requisitos);
            $this->incluye = sanitizeInput($this->incluye);
            $this->no_incluye = sanitizeInput($this->no_incluye);
            
            $stmt->bindParam(1, $this->id_proveedor);
            $stmt->bindParam(2, $this->nombre_servicio);
            $stmt->bindParam(3, $this->descripcion);
            $stmt->bindParam(4, $this->tipo_servicio);
            $stmt->bindParam(5, $this->precio_base);
            $stmt->bindParam(6, $this->duracion_horas);
            $stmt->bindParam(7, $this->capacidad_maxima);
            $stmt->bindParam(8, $this->ubicacion);
            $stmt->bindParam(9, $this->requisitos);
            $stmt->bindParam(10, $this->incluye);
            $stmt->bindParam(11, $this->no_incluye);
            $stmt->bindParam(12, $this->imagenes);
            $stmt->bindParam(13, $this->estado);
            
            if ($stmt->execute()) {
                $this->id_servicio = $this->conn->lastInsertId();
                
                // Registrar en historial
                $this->logOperation('creacion', 'Servicio creado: ' . $this->nombre_servicio);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al crear servicio: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Leer todos los servicios
     * @return array
     */
    public function readAll() {
        try {
            $query = "SELECT s.*, p.nombre_empresa, p.contacto_principal, p.telefono as telefono_proveedor
                     FROM " . $this->table_name . " s
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     ORDER BY s.fecha_creacion DESC";
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al leer servicios: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Leer servicios activos
     * @return array
     */
    public function readActive() {
        try {
            $query = "SELECT s.*, p.nombre_empresa, p.contacto_principal, p.telefono as telefono_proveedor
                     FROM " . $this->table_name . " s
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     WHERE s.estado = 'activo'
                     ORDER BY s.nombre_servicio";
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al leer servicios activos: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Leer un servicio específico
     * @param int $id_servicio ID del servicio
     * @return array|null
     */
    public function readOne($id_servicio) {
        try {
            $query = "SELECT s.*, p.nombre_empresa, p.contacto_principal, p.telefono as telefono_proveedor
                     FROM " . $this->table_name . " s
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     WHERE s.id_servicio = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $id_servicio);
            $stmt->execute();
            
            return $stmt->fetch(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al leer servicio: " . $exception->getMessage());
            return null;
        }
    }
    
    /**
     * Actualizar servicio
     * @return bool
     */
    public function update() {
        try {
            $query = "UPDATE " . $this->table_name . "
                     SET id_proveedor = ?, nombre_servicio = ?, descripcion = ?, tipo_servicio = ?,
                         precio_base = ?, duracion_horas = ?, capacidad_maxima = ?, ubicacion = ?,
                         requisitos = ?, incluye = ?, no_incluye = ?, imagenes = ?, estado = ?
                     WHERE id_servicio = ?";
            
            $stmt = $this->conn->prepare($query);
            
            // Sanitizar datos
            $this->nombre_servicio = sanitizeInput($this->nombre_servicio);
            $this->descripcion = sanitizeInput($this->descripcion);
            $this->ubicacion = sanitizeInput($this->ubicacion);
            $this->requisitos = sanitizeInput($this->requisitos);
            $this->incluye = sanitizeInput($this->incluye);
            $this->no_incluye = sanitizeInput($this->no_incluye);
            
            $stmt->bindParam(1, $this->id_proveedor);
            $stmt->bindParam(2, $this->nombre_servicio);
            $stmt->bindParam(3, $this->descripcion);
            $stmt->bindParam(4, $this->tipo_servicio);
            $stmt->bindParam(5, $this->precio_base);
            $stmt->bindParam(6, $this->duracion_horas);
            $stmt->bindParam(7, $this->capacidad_maxima);
            $stmt->bindParam(8, $this->ubicacion);
            $stmt->bindParam(9, $this->requisitos);
            $stmt->bindParam(10, $this->incluye);
            $stmt->bindParam(11, $this->no_incluye);
            $stmt->bindParam(12, $this->imagenes);
            $stmt->bindParam(13, $this->estado);
            $stmt->bindParam(14, $this->id_servicio);
            
            if ($stmt->execute()) {
                // Registrar en historial
                $this->logOperation('actualizacion', 'Servicio actualizado: ' . $this->nombre_servicio);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al actualizar servicio: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar servicio (cambiar estado a inactivo)
     * @return bool
     */
    public function delete() {
        try {
            $query = "UPDATE " . $this->table_name . "
                     SET estado = 'inactivo'
                     WHERE id_servicio = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $this->id_servicio);
            
            if ($stmt->execute()) {
                // Registrar en historial
                $this->logOperation('eliminacion', 'Servicio eliminado: ' . $this->nombre_servicio);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al eliminar servicio: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Obtener servicios por tipo
     * @param string $tipo_servicio Tipo de servicio
     * @return array
     */
    public function getByType($tipo_servicio) {
        try {
            $query = "SELECT s.*, p.nombre_empresa
                     FROM " . $this->table_name . " s
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     WHERE s.tipo_servicio = ? AND s.estado = 'activo'
                     ORDER BY s.nombre_servicio";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $tipo_servicio);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener servicios por tipo: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Buscar servicios por nombre o descripción
     * @param string $search Término de búsqueda
     * @return array
     */
    public function searchServices($search) {
        try {
            $search_param = "%" . sanitizeInput($search) . "%";
            
            $query = "SELECT s.*, p.nombre_empresa
                     FROM " . $this->table_name . " s
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     WHERE (s.nombre_servicio LIKE ? OR s.descripcion LIKE ? OR s.ubicacion LIKE ?)
                     AND s.estado = 'activo'
                     ORDER BY s.nombre_servicio";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $search_param);
            $stmt->bindParam(2, $search_param);
            $stmt->bindParam(3, $search_param);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al buscar servicios: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Obtener servicios por proveedor
     * @param int $id_proveedor ID del proveedor
     * @return array
     */
    public function getByProvider($id_proveedor) {
        try {
            $query = "SELECT s.*, p.nombre_empresa
                     FROM " . $this->table_name . " s
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     WHERE s.id_proveedor = ? AND s.estado = 'activo'
                     ORDER BY s.nombre_servicio";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $id_proveedor);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener servicios por proveedor: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Obtener servicios populares (más reservados)
     * @param int $limit Límite de resultados
     * @return array
     */
    public function getPopularServices($limit = 10) {
        try {
            $query = "SELECT s.*, p.nombre_empresa, COUNT(r.id_reserva) as total_reservas
                     FROM " . $this->table_name . " s
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     LEFT JOIN reservas r ON s.id_servicio = r.id_servicio
                     WHERE s.estado = 'activo'
                     GROUP BY s.id_servicio, s.nombre_servicio, p.nombre_empresa
                     HAVING total_reservas > 0
                     ORDER BY total_reservas DESC, s.nombre_servicio
                     LIMIT ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $limit, PDO::PARAM_INT);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener servicios populares: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Verificar disponibilidad de servicio para una fecha
     * @param int $id_servicio ID del servicio
     * @param string $fecha Fecha (YYYY-MM-DD)
     * @param int $cantidad_personas Número de personas
     * @return bool
     */
    public function checkAvailability($id_servicio, $fecha, $cantidad_personas) {
        try {
            // Obtener capacidad del servicio
            $query = "SELECT capacidad_maxima FROM " . $this->table_name . " WHERE id_servicio = ?";
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $id_servicio);
            $stmt->execute();
            
            $servicio = $stmt->fetch(PDO::FETCH_ASSOC);
            
            if (!$servicio) {
                return false;
            }
            
            // Contar reservas existentes para esa fecha
            $query = "SELECT SUM(cantidad_personas) as total_reservado
                     FROM reservas
                     WHERE id_servicio = ? AND fecha_inicio_servicio = ?
                     AND estado_reserva IN ('pendiente', 'confirmada', 'pagada')";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $id_servicio);
            $stmt->bindParam(2, $fecha);
            $stmt->execute();
            
            $reservas = $stmt->fetch(PDO::FETCH_ASSOC);
            $total_reservado = $reservas['total_reservado'] ?? 0;
            
            return ($total_reservado + $cantidad_personas) <= $servicio['capacidad_maxima'];
            
        } catch(PDOException $exception) {
            error_log("Error al verificar disponibilidad: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Obtener estadísticas de servicios
     * @return array
     */
    public function getServiceStats() {
        try {
            $query = "SELECT 
                        COUNT(*) as total_servicios,
                        SUM(CASE WHEN estado = 'activo' THEN 1 ELSE 0 END) as servicios_activos,
                        SUM(CASE WHEN estado = 'inactivo' THEN 1 ELSE 0 END) as servicios_inactivos,
                        SUM(CASE WHEN tipo_servicio = 'tour' THEN 1 ELSE 0 END) as tours,
                        SUM(CASE WHEN tipo_servicio = 'hospedaje' THEN 1 ELSE 0 END) as hospedaje,
                        SUM(CASE WHEN tipo_servicio = 'transporte' THEN 1 ELSE 0 END) as transporte,
                        SUM(CASE WHEN tipo_servicio = 'alimentacion' THEN 1 ELSE 0 END) as alimentacion,
                        SUM(CASE WHEN tipo_servicio = 'actividad' THEN 1 ELSE 0 END) as actividades,
                        AVG(precio_base) as precio_promedio
                     FROM " . $this->table_name;
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            
            return $stmt->fetch(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener estadísticas de servicios: " . $exception->getMessage());
            return [];
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
                     (id_usuario, tipo_operacion, descripcion_operacion, tabla_afectada, id_registro_afectado, ip_address)
                     VALUES (?, ?, ?, 'servicios', ?, ?)";
            
            $stmt = $this->conn->prepare($query);
            
            $user_id = $_SESSION['user_id'] ?? 0;
            $ip_address = $_SERVER['REMOTE_ADDR'] ?? 'unknown';
            
            $stmt->bindParam(1, $user_id);
            $stmt->bindParam(2, $tipo_operacion);
            $stmt->bindParam(3, $descripcion);
            $stmt->bindParam(4, $this->id_servicio);
            $stmt->bindParam(5, $ip_address);
            
            return $stmt->execute();
            
        } catch(PDOException $exception) {
            error_log("Error al registrar operación: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Validar datos del servicio antes de guardar
     * @return array Array de errores
     */
    public function validate() {
        $errors = [];
        
        // Validar nombre del servicio
        if (empty($this->nombre_servicio)) {
            $errors[] = "El nombre del servicio es obligatorio";
        } elseif (strlen($this->nombre_servicio) < 5) {
            $errors[] = "El nombre del servicio debe tener al menos 5 caracteres";
        }
        
        // Validar proveedor
        if (empty($this->id_proveedor)) {
            $errors[] = "Debe seleccionar un proveedor";
        }
        
        // Validar tipo de servicio
        if (empty($this->tipo_servicio) || !in_array($this->tipo_servicio, ['tour', 'hospedaje', 'transporte', 'alimentacion', 'actividad'])) {
            $errors[] = "Debe seleccionar un tipo de servicio válido";
        }
        
        // Validar precio
        if (empty($this->precio_base) || $this->precio_base <= 0) {
            $errors[] = "El precio base debe ser mayor a 0";
        }
        
        // Validar duración
        if (empty($this->duracion_horas) || $this->duracion_horas <= 0) {
            $errors[] = "La duración debe ser mayor a 0 horas";
        }
        
        // Validar capacidad
        if (empty($this->capacidad_maxima) || $this->capacidad_maxima <= 0) {
            $errors[] = "La capacidad máxima debe ser mayor a 0";
        }
        
        // Validar ubicación
        if (empty($this->ubicacion)) {
            $errors[] = "La ubicación es obligatoria";
        }
        
        return $errors;
    }
    
    /**
     * Obtener servicios con disponibilidad para un rango de fechas
     * @param string $fecha_inicio Fecha de inicio
     * @param string $fecha_fin Fecha de fin
     * @param int $cantidad_personas Número de personas
     * @return array
     */
    public function getAvailableServices($fecha_inicio, $fecha_fin, $cantidad_personas) {
        try {
            $query = "SELECT s.*, p.nombre_empresa
                     FROM " . $this->table_name . " s
                     JOIN proveedores p ON s.id_proveedor = p.id_proveedor
                     WHERE s.estado = 'activo'
                     AND s.capacidad_maxima >= ?
                     AND s.id_servicio NOT IN (
                         SELECT DISTINCT r.id_servicio
                         FROM reservas r
                         WHERE r.fecha_inicio_servicio BETWEEN ? AND ?
                         AND r.estado_reserva IN ('pendiente', 'confirmada', 'pagada')
                         AND (r.cantidad_personas + ?) > s.capacidad_maxima
                     )
                     ORDER BY s.nombre_servicio";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $cantidad_personas);
            $stmt->bindParam(2, $fecha_inicio);
            $stmt->bindParam(3, $fecha_fin);
            $stmt->bindParam(4, $cantidad_personas);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener servicios disponibles: " . $exception->getMessage());
            return [];
        }
    }
}
?>

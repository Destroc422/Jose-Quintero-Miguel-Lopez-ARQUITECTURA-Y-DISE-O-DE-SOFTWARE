<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * MODELO: USER
 * =============================================
 * 
 * Este modelo maneja todas las operaciones relacionadas
 * con usuarios del sistema: autenticación, CRUD, etc.
 */

require_once __DIR__ . '/../config/database.php';

class User {
    private $conn;
    private $table_name = "usuarios";
    
    // Propiedades del usuario
    public $id_usuario;
    public $id_rol;
    public $username;
    public $password;
    public $email;
    public $nombre_completo;
    public $telefono;
    public $fecha_creacion;
    public $ultimo_login;
    public $estado;
    
    // Propiedades adicionales para relaciones
    public $nombre_rol;
    
    /**
     * Constructor - Establece conexión a la base de datos
     */
    public function __construct() {
        $database = new Database();
        $this->conn = $database->getConnection();
    }
    
    /**
     * Autenticar usuario
     * @param string $username Nombre de usuario
     * @param string $password Contraseña
     * @return bool
     */
    public function login($username, $password) {
        try {
            // Consulta preparada para evitar SQL injection
            $query = "SELECT u.*, r.nombre_rol 
                     FROM " . $this->table_name . " u
                     JOIN roles r ON u.id_rol = r.id_rol
                     WHERE u.username = ? AND u.estado = 'activo'";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $username);
            $stmt->execute();
            
            if ($stmt->rowCount() > 0) {
                $row = $stmt->fetch(PDO::FETCH_ASSOC);
                
                // Verificar contraseña
                if (verifyPassword($password, $row['password'])) {
                    // Asignar propiedades del usuario
                    $this->id_usuario = $row['id_usuario'];
                    $this->id_rol = $row['id_rol'];
                    $this->username = $row['username'];
                    $this->email = $row['email'];
                    $this->nombre_completo = $row['nombre_completo'];
                    $this->telefono = $row['telefono'];
                    $this->estado = $row['estado'];
                    $this->nombre_rol = $row['nombre_rol'];
                    
                    // Actualizar último login
                    $this->updateLastLogin();
                    
                    // Registrar en historial
                    $this->logOperation('login', 'Usuario inició sesión en el sistema');
                    
                    return true;
                }
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error en login: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Crear nuevo usuario
     * @return bool
     */
    public function create() {
        try {
            // Verificar si el username o email ya existen
            if ($this->userExists() || $this->emailExists()) {
                return false;
            }
            
            // Hash de la contraseña
            $hashed_password = hashPassword($this->password);
            
            $query = "INSERT INTO " . $this->table_name . "
                     (id_rol, username, password, email, nombre_completo, telefono)
                     VALUES (?, ?, ?, ?, ?, ?)";
            
            $stmt = $this->conn->prepare($query);
            
            // Sanitizar y bind parameters
            $this->username = sanitizeInput($this->username);
            $this->email = sanitizeInput($this->email);
            $this->nombre_completo = sanitizeInput($this->nombre_completo);
            $this->telefono = sanitizeInput($this->telefono);
            
            $stmt->bindParam(1, $this->id_rol);
            $stmt->bindParam(2, $this->username);
            $stmt->bindParam(3, $hashed_password);
            $stmt->bindParam(4, $this->email);
            $stmt->bindParam(5, $this->nombre_completo);
            $stmt->bindParam(6, $this->telefono);
            
            if ($stmt->execute()) {
                $this->id_usuario = $this->conn->lastInsertId();
                
                // Registrar en historial
                $this->logOperation('creacion', 'Usuario creado: ' . $this->username);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al crear usuario: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Leer todos los usuarios
     * @return array
     */
    public function readAll() {
        try {
            $query = "SELECT u.*, r.nombre_rol
                     FROM " . $this->table_name . " u
                     JOIN roles r ON u.id_rol = r.id_rol
                     ORDER BY u.fecha_creacion DESC";
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al leer usuarios: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Leer un usuario específico
     * @param int $id_usuario ID del usuario
     * @return array|null
     */
    public function readOne($id_usuario) {
        try {
            $query = "SELECT u.*, r.nombre_rol
                     FROM " . $this->table_name . " u
                     JOIN roles r ON u.id_rol = r.id_rol
                     WHERE u.id_usuario = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $id_usuario);
            $stmt->execute();
            
            return $stmt->fetch(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al leer usuario: " . $exception->getMessage());
            return null;
        }
    }
    
    /**
     * Actualizar usuario
     * @return bool
     */
    public function update() {
        try {
            $query = "UPDATE " . $this->table_name . "
                     SET id_rol = ?, email = ?, nombre_completo = ?, telefono = ?, estado = ?
                     WHERE id_usuario = ?";
            
            $stmt = $this->conn->prepare($query);
            
            // Sanitizar datos
            $this->email = sanitizeInput($this->email);
            $this->nombre_completo = sanitizeInput($this->nombre_completo);
            $this->telefono = sanitizeInput($this->telefono);
            
            $stmt->bindParam(1, $this->id_rol);
            $stmt->bindParam(2, $this->email);
            $stmt->bindParam(3, $this->nombre_completo);
            $stmt->bindParam(4, $this->telefono);
            $stmt->bindParam(5, $this->estado);
            $stmt->bindParam(6, $this->id_usuario);
            
            if ($stmt->execute()) {
                // Registrar en historial
                $this->logOperation('actualizacion', 'Usuario actualizado: ' . $this->username);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al actualizar usuario: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Cambiar contraseña de usuario
     * @param string $new_password Nueva contraseña
     * @return bool
     */
    public function changePassword($new_password) {
        try {
            $hashed_password = hashPassword($new_password);
            
            $query = "UPDATE " . $this->table_name . "
                     SET password = ?
                     WHERE id_usuario = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $hashed_password);
            $stmt->bindParam(2, $this->id_usuario);
            
            if ($stmt->execute()) {
                // Registrar en historial
                $this->logOperation('actualizacion', 'Contraseña cambiada para usuario: ' . $this->username);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al cambiar contraseña: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar usuario (cambiar estado a inactivo)
     * @return bool
     */
    public function delete() {
        try {
            $query = "UPDATE " . $this->table_name . "
                     SET estado = 'inactivo'
                     WHERE id_usuario = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $this->id_usuario);
            
            if ($stmt->execute()) {
                // Registrar en historial
                $this->logOperation('eliminacion', 'Usuario eliminado: ' . $this->username);
                
                return true;
            }
            
            return false;
            
        } catch(PDOException $exception) {
            error_log("Error al eliminar usuario: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Verificar si el username ya existe
     * @return bool
     */
    public function userExists() {
        try {
            $query = "SELECT id_usuario FROM " . $this->table_name . "
                     WHERE username = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $this->username);
            $stmt->execute();
            
            return $stmt->rowCount() > 0;
            
        } catch(PDOException $exception) {
            error_log("Error al verificar username: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Verificar si el email ya existe
     * @return bool
     */
    public function emailExists() {
        try {
            $query = "SELECT id_usuario FROM " . $this->table_name . "
                     WHERE email = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $this->email);
            $stmt->execute();
            
            return $stmt->rowCount() > 0;
            
        } catch(PDOException $exception) {
            error_log("Error al verificar email: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Actualizar último login del usuario
     * @return bool
     */
    private function updateLastLogin() {
        try {
            $query = "UPDATE " . $this->table_name . "
                     SET ultimo_login = CURRENT_TIMESTAMP
                     WHERE id_usuario = ?";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $this->id_usuario);
            
            return $stmt->execute();
            
        } catch(PDOException $exception) {
            error_log("Error al actualizar último login: " . $exception->getMessage());
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
                     (id_usuario, tipo_operacion, descripcion_operacion, tabla_afectada, id_registro_afectado, ip_address)
                     VALUES (?, ?, ?, 'usuarios', ?, ?)";
            
            $stmt = $this->conn->prepare($query);
            
            $ip_address = $_SERVER['REMOTE_ADDR'] ?? 'unknown';
            
            $stmt->bindParam(1, $this->id_usuario);
            $stmt->bindParam(2, $tipo_operacion);
            $stmt->bindParam(3, $descripcion);
            $stmt->bindParam(4, $this->id_usuario);
            $stmt->bindParam(5, $ip_address);
            
            return $stmt->execute();
            
        } catch(PDOException $exception) {
            error_log("Error al registrar operación: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Obtener usuarios por rol
     * @param int $id_rol ID del rol
     * @return array
     */
    public function getUsersByRole($id_rol) {
        try {
            $query = "SELECT u.*, r.nombre_rol
                     FROM " . $this->table_name . " u
                     JOIN roles r ON u.id_rol = r.id_rol
                     WHERE u.id_rol = ? AND u.estado = 'activo'
                     ORDER BY u.nombre_completo";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $id_rol);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener usuarios por rol: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Buscar usuarios por nombre o email
     * @param string $search Término de búsqueda
     * @return array
     */
    public function searchUsers($search) {
        try {
            $search_param = "%" . sanitizeInput($search) . "%";
            
            $query = "SELECT u.*, r.nombre_rol
                     FROM " . $this->table_name . " u
                     JOIN roles r ON u.id_rol = r.id_rol
                     WHERE (u.nombre_completo LIKE ? OR u.email LIKE ? OR u.username LIKE ?)
                     AND u.estado = 'activo'
                     ORDER BY u.nombre_completo";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(1, $search_param);
            $stmt->bindParam(2, $search_param);
            $stmt->bindParam(3, $search_param);
            $stmt->execute();
            
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al buscar usuarios: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Obtener estadísticas de usuarios
     * @return array
     */
    public function getUserStats() {
        try {
            $query = "SELECT 
                        COUNT(*) as total_usuarios,
                        SUM(CASE WHEN estado = 'activo' THEN 1 ELSE 0 END) as usuarios_activos,
                        SUM(CASE WHEN estado = 'inactivo' THEN 1 ELSE 0 END) as usuarios_inactivos,
                        SUM(CASE WHEN id_rol = 1 THEN 1 ELSE 0 END) as administradores,
                        SUM(CASE WHEN id_rol = 2 THEN 1 ELSE 0 END) as empleados,
                        SUM(CASE WHEN id_rol = 3 THEN 1 ELSE 0 END) as clientes
                     FROM " . $this->table_name;
            
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            
            return $stmt->fetch(PDO::FETCH_ASSOC);
            
        } catch(PDOException $exception) {
            error_log("Error al obtener estadísticas: " . $exception->getMessage());
            return [];
        }
    }
    
    /**
     * Validar datos del usuario antes de guardar
     * @return array Array de errores
     */
    public function validate() {
        $errors = [];
        
        // Validar username
        if (empty($this->username)) {
            $errors[] = "El nombre de usuario es obligatorio";
        } elseif (strlen($this->username) < 3) {
            $errors[] = "El nombre de usuario debe tener al menos 3 caracteres";
        } elseif (!preg_match('/^[a-zA-Z0-9_]+$/', $this->username)) {
            $errors[] = "El nombre de usuario solo puede contener letras, números y guiones bajos";
        }
        
        // Validar email
        if (empty($this->email)) {
            $errors[] = "El email es obligatorio";
        } elseif (!validateEmail($this->email)) {
            $errors[] = "El formato del email no es válido";
        }
        
        // Validar nombre completo
        if (empty($this->nombre_completo)) {
            $errors[] = "El nombre completo es obligatorio";
        } elseif (strlen($this->nombre_completo) < 5) {
            $errors[] = "El nombre completo debe tener al menos 5 caracteres";
        }
        
        // Validar rol
        if (empty($this->id_rol) || !in_array($this->id_rol, [1, 2, 3])) {
            $errors[] = "Debe seleccionar un rol válido";
        }
        
        // Validar contraseña (solo para nuevos usuarios)
        if (empty($this->id_usuario) && empty($this->password)) {
            $errors[] = "La contraseña es obligatoria";
        } elseif (!empty($this->password) && strlen($this->password) < 6) {
            $errors[] = "La contraseña debe tener al menos 6 caracteres";
        }
        
        return $errors;
    }
}
?>

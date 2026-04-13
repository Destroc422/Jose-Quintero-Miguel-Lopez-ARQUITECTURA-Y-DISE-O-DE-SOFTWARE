<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CONFIGURACIÓN DE BASE DE DATOS
 * =============================================
 * 
 * Este archivo contiene la configuración de conexión
 * a la base de datos MySQL del sistema.
 */

class Database {
    // Parámetros de conexión
    private $host = "localhost";
    private $db_name = "hermosa_cartagena";
    private $username = "root"; // Cambiar según configuración
    private $password = ""; // Cambiar según configuración
    private $charset = "utf8mb4";
    
    // Conexión PDO
    public $conn;
    
    /**
     * Constructor - Establece la conexión a la base de datos
     */
    public function __construct() {
        try {
            // Configuración DSN para MySQL
            $dsn = "mysql:host=" . $this->host . ";dbname=" . $this->db_name . ";charset=" . $this->charset;
            
            // Opciones de conexión PDO
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES => false,
                PDO::ATTR_PERSISTENT => true // Conexión persistente para mejor rendimiento
            ];
            
            // Crear conexión PDO
            $this->conn = new PDO($dsn, $this->username, $this->password, $options);
            
        } catch(PDOException $exception) {
            // Error en la conexión
            echo "Error de conexión: " . $exception->getMessage();
            die();
        }
    }
    
    /**
     * Obtener la conexión activa
     * @return PDO Objeto de conexión PDO
     */
    public function getConnection() {
        return $this->conn;
    }
    
    /**
     * Iniciar transacción
     * @return bool
     */
    public function beginTransaction() {
        return $this->conn->beginTransaction();
    }
    
    /**
     * Confirmar transacción
     * @return bool
     */
    public function commit() {
        return $this->conn->commit();
    }
    
    /**
     * Revertir transacción
     * @return bool
     */
    public function rollback() {
        return $this->conn->rollback();
    }
    
    /**
     * Cerrar conexión
     */
    public function closeConnection() {
        $this->conn = null;
    }
    
    /**
     * Ejecutar consulta preparada
     * @param string $sql Consulta SQL
     * @param array $params Parámetros de la consulta
     * @return PDOStatement
     */
    public function prepareAndExecute($sql, $params = []) {
        try {
            $stmt = $this->conn->prepare($sql);
            $stmt->execute($params);
            return $stmt;
        } catch(PDOException $exception) {
            echo "Error en consulta: " . $exception->getMessage();
            die();
        }
    }
    
    /**
     * Obtener último ID insertado
     * @return string
     */
    public function lastInsertId() {
        return $this->conn->lastInsertId();
    }
    
    /**
     * Verificar si la conexión está activa
     * @return bool
     */
    public function isConnected() {
        return $this->conn !== null;
    }
    
    /**
     * Obtener información del servidor MySQL
     * @return array
     */
    public function getServerInfo() {
        return [
            'server_version' => $this->conn->getAttribute(PDO::ATTR_SERVER_VERSION),
            'client_version' => $this->conn->getAttribute(PDO::ATTR_CLIENT_VERSION),
            'connection_status' => $this->conn->getAttribute(PDO::ATTR_CONNECTION_STATUS)
        ];
    }
}

/**
 * Clase para manejo de errores y logging
 */
class DatabaseLogger {
    private $logFile;
    
    public function __construct() {
        $this->logFile = __DIR__ . '/../logs/database.log';
        $this->ensureLogDirectory();
    }
    
    private function ensureLogDirectory() {
        $logDir = dirname($this->logFile);
        if (!is_dir($logDir)) {
            mkdir($logDir, 0777, true);
        }
    }
    
    public function logError($message, $exception = null) {
        $timestamp = date('Y-m-d H:i:s');
        $logMessage = "[$timestamp] ERROR: $message";
        
        if ($exception) {
            $logMessage .= "\nException: " . $exception->getMessage();
            $logMessage .= "\nFile: " . $exception->getFile();
            $logMessage .= "\nLine: " . $exception->getLine();
        }
        
        $logMessage .= "\n" . str_repeat("-", 80) . "\n";
        
        file_put_contents($this->logFile, $logMessage, FILE_APPEND | LOCK_EX);
    }
    
    public function logQuery($sql, $params = [], $executionTime = null) {
        $timestamp = date('Y-m-d H:i:s');
        $logMessage = "[$timestamp] QUERY: $sql";
        
        if (!empty($params)) {
            $logMessage .= "\nPARAMS: " . json_encode($params);
        }
        
        if ($executionTime !== null) {
            $logMessage .= "\nEXECUTION TIME: {$executionTime}ms";
        }
        
        $logMessage .= "\n" . str_repeat("-", 80) . "\n";
        
        file_put_contents($this->logFile, $logMessage, FILE_APPEND | LOCK_EX);
    }
}

/**
 * Función para obtener instancia única de base de datos (Singleton)
 */
function getDatabase() {
    static $instance = null;
    
    if ($instance === null) {
        $instance = new Database();
    }
    
    return $instance;
}

/**
 * Función para sanitizar datos de entrada
 * @param mixed $data Datos a sanitizar
 * @return mixed Datos sanitizados
 */
function sanitizeInput($data) {
    if (is_array($data)) {
        return array_map('sanitizeInput', $data);
    }
    
    return htmlspecialchars(strip_tags(trim($data)), ENT_QUOTES, 'UTF-8');
}

/**
 * Función para validar email
 * @param string $email Email a validar
 * @return bool
 */
function validateEmail($email) {
    return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
}

/**
 * Función para generar hash de contraseña
 * @param string $password Contraseña en texto plano
 * @return string Hash de la contraseña
 */
function hashPassword($password) {
    return hash('sha256', $password . 'hermosa_cartagena_salt');
}

/**
 * Función para verificar contraseña
 * @param string $password Contraseña en texto plano
 * @param string $hash Hash almacenado
 * @return bool
 */
function verifyPassword($password, $hash) {
    return hash('sha256', $password . 'hermosa_cartagena_salt') === $hash;
}

/**
 * Función para generar token CSRF
 * @return string Token CSRF
 */
function generateCSRFToken() {
    if (session_status() == PHP_SESSION_NONE) {
        session_start();
    }
    
    if (empty($_SESSION['csrf_token'])) {
        $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
    }
    
    return $_SESSION['csrf_token'];
}

/**
 * Función para verificar token CSRF
 * @param string $token Token a verificar
 * @return bool
 */
function verifyCSRFToken($token) {
    if (session_status() == PHP_SESSION_NONE) {
        session_start();
    }
    
    return isset($_SESSION['csrf_token']) && hash_equals($_SESSION['csrf_token'], $token);
}
?>

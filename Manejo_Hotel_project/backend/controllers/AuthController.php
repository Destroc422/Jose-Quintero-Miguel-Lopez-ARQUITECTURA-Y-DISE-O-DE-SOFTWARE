<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CONTROLADOR: AUTHENTICATION
 * =============================================
 * 
 * Este controlador maneja la autenticación de usuarios,
 * sesiones y seguridad del sistema.
 */

require_once __DIR__ . '/../models/User.php';
require_once __DIR__ . '/../config/database.php';

class AuthController {
    private $user;
    
    /**
     * Constructor
     */
    public function __construct() {
        $this->user = new User();
        
        // Iniciar sesión si no está activa
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }
    }
    
    /**
     * Procesar login de usuario
     * @return array Resultado del login
     */
    public function login() {
        // Validar método HTTP
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            return ['success' => false, 'message' => 'Método no permitido'];
        }
        
        // Validar CSRF token
        if (!verifyCSRFToken($_POST['csrf_token'] ?? '')) {
            return ['success' => false, 'message' => 'Token de seguridad inválido'];
        }
        
        // Obtener y sanitizar datos
        $username = sanitizeInput($_POST['username'] ?? '');
        $password = $_POST['password'] ?? '';
        
        // Validación básica
        if (empty($username) || empty($password)) {
            return ['success' => false, 'message' => 'Por favor ingrese usuario y contraseña'];
        }
        
        // Intentar autenticación
        if ($this->user->login($username, $password)) {
            // Establecer variables de sesión
            $_SESSION['user_id'] = $this->user->id_usuario;
            $_SESSION['username'] = $this->user->username;
            $_SESSION['nombre_completo'] = $this->user->nombre_completo;
            $_SESSION['email'] = $this->user->email;
            $_SESSION['rol'] = $this->user->nombre_rol;
            $_SESSION['id_rol'] = $this->user->id_rol;
            $_SESSION['login_time'] = time();
            $_SESSION['last_activity'] = time();
            
            // Regenerar ID de sesión para seguridad
            session_regenerate_id(true);
            
            return [
                'success' => true,
                'message' => 'Login exitoso',
                'redirect' => $this->getRedirectUrl(),
                'user' => [
                    'id' => $this->user->id_usuario,
                    'username' => $this->user->username,
                    'nombre' => $this->user->nombre_completo,
                    'rol' => $this->user->nombre_rol
                ]
            ];
        } else {
            return ['success' => false, 'message' => 'Usuario o contraseña incorrectos'];
        }
    }
    
    /**
     * Cerrar sesión del usuario
     * @return array Resultado del logout
     */
    public function logout() {
        // Registrar logout en historial
        if (isset($_SESSION['user_id'])) {
            $this->user->id_usuario = $_SESSION['user_id'];
            $this->user->username = $_SESSION['username'];
            $this->logOperation('logout', 'Usuario cerró sesión');
        }
        
        // Destruir sesión
        session_unset();
        session_destroy();
        
        // Eliminar cookie de sesión
        if (isset($_COOKIE[session_name()])) {
            setcookie(session_name(), '', time() - 3600, '/');
        }
        
        return ['success' => true, 'message' => 'Sesión cerrada correctamente'];
    }
    
    /**
     * Verificar si el usuario está autenticado
     * @return bool
     */
    public function isAuthenticated() {
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }
        
        // Verificar si existe sesión y no ha expirado
        if (isset($_SESSION['user_id']) && isset($_SESSION['last_activity'])) {
            // Tiempo de expiración: 30 minutos de inactividad
            $timeout = 30 * 60;
            
            if (time() - $_SESSION['last_activity'] < $timeout) {
                // Actualizar última actividad
                $_SESSION['last_activity'] = time();
                return true;
            } else {
                // Sesión expirada, cerrar
                $this->logout();
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Verificar si el usuario tiene un rol específico
     * @param string $required_rol Rol requerido
     * @return bool
     */
    public function hasRole($required_rol) {
        if (!$this->isAuthenticated()) {
            return false;
        }
        
        return ($_SESSION['rol'] === $required_rol);
    }
    
    /**
     * Verificar si el usuario tiene permiso de administrador
     * @return bool
     */
    public function isAdmin() {
        return $this->hasRole('administrador');
    }
    
    /**
     * Verificar si el usuario es empleado
     * @return bool
     */
    public function isEmployee() {
        return $this->hasRole('empleado');
    }
    
    /**
     * Verificar si el usuario es cliente
     * @return bool
     */
    public function isClient() {
        return $this->hasRole('cliente');
    }
    
    /**
     * Obtener URL de redirección según rol
     * @return string
     */
    private function getRedirectUrl() {
        switch ($_SESSION['rol']) {
            case 'administrador':
                return '../views/admin/dashboard.php';
            case 'empleado':
                return '../views/employee/dashboard.php';
            case 'cliente':
                return '../views/client/dashboard.php';
            default:
                return '../views/login.php';
        }
    }
    
    /**
     * Proteger página (requiere autenticación)
     * @param string $required_rol Rol requerido (opcional)
     * @return void
     */
    public function requireAuth($required_rol = null) {
        if (!$this->isAuthenticated()) {
            // Redirigir a login
            header('Location: ../views/login.php?error=session_expired');
            exit();
        }
        
        // Verificar rol si se especifica
        if ($required_rol && !$this->hasRole($required_rol)) {
            // Redirigir a página de acceso denegado
            header('Location: ../views/access_denied.php');
            exit();
        }
    }
    
    /**
     * Obtener información del usuario actual
     * @return array|null
     */
    public function getCurrentUser() {
        if (!$this->isAuthenticated()) {
            return null;
        }
        
        return [
            'id' => $_SESSION['user_id'],
            'username' => $_SESSION['username'],
            'nombre_completo' => $_SESSION['nombre_completo'],
            'email' => $_SESSION['email'],
            'rol' => $_SESSION['rol'],
            'id_rol' => $_SESSION['id_rol'],
            'login_time' => $_SESSION['login_time'] ?? 0,
            'last_activity' => $_SESSION['last_activity'] ?? 0
        ];
    }
    
    /**
     * Actualizar última actividad
     * @return void
     */
    public function updateLastActivity() {
        if ($this->isAuthenticated()) {
            $_SESSION['last_activity'] = time();
        }
    }
    
    /**
     * Verificar si la sesión está a punto de expirar
     * @return bool
     */
    public function isSessionExpiringSoon() {
        if (!$this->isAuthenticated()) {
            return false;
        }
        
        $timeout = 30 * 60; // 30 minutos
        $warning_time = 5 * 60; // 5 minutos antes de expirar
        
        return (time() - $_SESSION['last_activity']) > ($timeout - $warning_time);
    }
    
    /**
     * Obtener tiempo restante de sesión
     * @return int Tiempo en segundos
     */
    public function getSessionTimeRemaining() {
        if (!$this->isAuthenticated()) {
            return 0;
        }
        
        $timeout = 30 * 60;
        $elapsed = time() - $_SESSION['last_activity'];
        
        return max(0, $timeout - $elapsed);
    }
    
    /**
     * Registrar operación en historial
     * @param string $tipo_operacion Tipo de operación
     * @param string $descripcion Descripción
     * @return bool
     */
    private function logOperation($tipo_operacion, $descripcion) {
        try {
            $database = new Database();
            $conn = $database->getConnection();
            
            $query = "INSERT INTO historial_operaciones
                     (id_usuario, tipo_operacion, descripcion_operacion, tabla_afectada, ip_address)
                     VALUES (?, ?, ?, 'usuarios', ?)";
            
            $stmt = $conn->prepare($query);
            
            $ip_address = $_SERVER['REMOTE_ADDR'] ?? 'unknown';
            
            $stmt->bindParam(1, $_SESSION['user_id']);
            $stmt->bindParam(2, $tipo_operacion);
            $stmt->bindParam(3, $descripcion);
            $stmt->bindParam(4, $ip_address);
            
            return $stmt->execute();
            
        } catch(PDOException $exception) {
            error_log("Error al registrar operación: " . $exception->getMessage());
            return false;
        }
    }
    
    /**
     * Validar y sanitizar datos de login
     * @param array $data Datos del formulario
     * @return array Datos validados
     */
    public function validateLoginData($data) {
        $errors = [];
        
        // Validar username
        if (empty($data['username'])) {
            $errors['username'] = 'El nombre de usuario es obligatorio';
        } elseif (strlen($data['username']) < 3) {
            $errors['username'] = 'El nombre de usuario debe tener al menos 3 caracteres';
        }
        
        // Validar password
        if (empty($data['password'])) {
            $errors['password'] = 'La contraseña es obligatoria';
        } elseif (strlen($data['password']) < 6) {
            $errors['password'] = 'La contraseña debe tener al menos 6 caracteres';
        }
        
        return [
            'errors' => $errors,
            'data' => [
                'username' => sanitizeInput($data['username'] ?? ''),
                'password' => $data['password'] ?? ''
            ]
        ];
    }
    
    /**
     * Generar respuesta JSON para AJAX
     * @param array $response Datos de respuesta
     * @return void
     */
    public function jsonResponse($response) {
        header('Content-Type: application/json');
        echo json_encode($response);
        exit();
    }
    
    /**
     * Manejar solicitudes AJAX
     * @return void
     */
    public function handleAjaxRequest() {
        $action = $_GET['action'] ?? '';
        
        switch ($action) {
            case 'login':
                $result = $this->login();
                $this->jsonResponse($result);
                break;
                
            case 'logout':
                $result = $this->logout();
                $this->jsonResponse($result);
                break;
                
            case 'check_session':
                $result = [
                    'authenticated' => $this->isAuthenticated(),
                    'user' => $this->getCurrentUser(),
                    'time_remaining' => $this->getSessionTimeRemaining(),
                    'expiring_soon' => $this->isSessionExpiringSoon()
                ];
                $this->jsonResponse($result);
                break;
                
            case 'refresh_session':
                $this->updateLastActivity();
                $result = [
                    'success' => true,
                    'time_remaining' => $this->getSessionTimeRemaining()
                ];
                $this->jsonResponse($result);
                break;
                
            default:
                $this->jsonResponse(['success' => false, 'message' => 'Acción no válida']);
                break;
        }
    }
    
    /**
     * Obtener configuración de seguridad
     * @return array
     */
    public function getSecurityConfig() {
        return [
            'session_timeout' => 30 * 60, // 30 minutos
            'warning_time' => 5 * 60, // 5 minutos
            'max_login_attempts' => 5,
            'lockout_time' => 15 * 60, // 15 minutos
            'password_min_length' => 6,
            'require_csrf' => true
        ];
    }
}
?>

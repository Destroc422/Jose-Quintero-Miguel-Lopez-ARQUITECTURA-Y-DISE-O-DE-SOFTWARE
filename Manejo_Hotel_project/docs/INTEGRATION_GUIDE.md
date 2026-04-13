# Guía de Integración del Sistema

## Overview

Este documento describe cómo integrar todas las componentes del Sistema de Gestión Turística "Hermosa Cartagena" para un funcionamiento completo y cohesivo.

## Arquitectura de Integración

```
                    +-------------------+
                    |   Cliente Web     |
                    |  (Browser/HTML)   |
                    +---------+---------+
                              |
                    +---------v---------+
                    |   Servidor Web    |
                    |    (Apache/Nginx) |
                    +---------+---------+
                              |
                    +---------v---------+
                    |   Backend PHP     |
                    +---------+---------+
                              |
                    +---------v---------+
                    |   Base de Datos   |
                    |    (MySQL)        |
                    +-------------------+

                    +-------------------+
                    |  Cliente Java RMI  |
                    +---------+---------+
                              |
                    +---------v---------+
                    |  Servidor RMI     |
                    +---------+---------+
                              |
                    +---------v---------+
                    |   Base de Datos   |
                    |    (MySQL)        |
                    +-------------------+
```

## Pasos de Integración

### 1. Configuración del Entorno

#### Base de Datos
```bash
# 1. Crear base de datos
mysql -u root -p -e "CREATE DATABASE hermosa_cartagena CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;"

# 2. Importar estructura
mysql -u root -p hermosa_cartagena < database/01_create_database.sql

# 3. Importar datos de prueba
mysql -u root -p hermosa_cartagena < database/02_insert_datos_prueba.sql

# 4. Verificar instalación
mysql -u root -p hermosa_cartagena -e "SHOW TABLES;"
```

#### Configuración PHP
```php
// backend/config/database.php
private $host = "localhost";
private $db_name = "hermosa_cartagena";
private $username = "root";
private $password = "tu_contraseña";
```

#### Configuración Java
```bash
# 1. Verificar Java 11+
java -version

# 2. Configurar CLASSPATH con MySQL JDBC
export CLASSPATH=$CLASSPATH:/path/to/mysql-connector-java.jar

# 3. Compilar sistema distribuido
cd java
ant compile
ant jar
```

### 2. Integración Frontend-Backend

#### Conexión del Login
```javascript
// assets/js/auth.js
async function handleLogin() {
    const formData = new FormData(loginForm);
    
    const response = await fetch('../backend/api/auth.php?action=login', {
        method: 'POST',
        body: formData,
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    });
    
    const result = await response.json();
    
    if (result.success) {
        window.location.href = result.redirect;
    }
}
```

#### API Endpoints
```php
// backend/api/auth.php
require_once '../controllers/AuthController.php';

$auth = new AuthController();
$action = $_GET['action'] ?? '';

switch ($action) {
    case 'login':
        $result = $auth->login();
        break;
    case 'logout':
        $result = $auth->logout();
        break;
    case 'check_session':
        $result = ['authenticated' => $auth->isAuthenticated()];
        break;
}

header('Content-Type: application/json');
echo json_encode($result);
```

### 3. Integración Sistema Distribuido

#### Iniciar Servidor RMI
```bash
cd java
ant run-server
```

#### Conexión del Cliente
```java
// java/src/com/hermosacartagena/client/ClienteServicios.java
private boolean conectarServidor() {
    try {
        Registry registry = LocateRegistry.getRegistry(SERVER_HOST, RMI_PORT);
        servicioStub = (ServicioStub) registry.lookup(SERVICE_NAME);
        return true;
    } catch (Exception e) {
        System.err.println("Error al conectar: " + e.getMessage());
        return false;
    }
}
```

#### Sincronización de Datos
```java
// El sistema Java puede sincronizar datos con la base de datos MySQL
// compartida con el sistema PHP, asegurando consistencia
```

### 4. Configuración de Seguridad

#### Tokens CSRF
```php
// backend/config/database.php
function generateCSRFToken() {
    if (session_status() == PHP_SESSION_NONE) {
        session_start();
    }
    
    if (empty($_SESSION['csrf_token'])) {
        $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
    }
    
    return $_SESSION['csrf_token'];
}
```

#### Políticas de Seguridad Java
```java
// dist/server.policy
grant {
    permission java.net.SocketPermission "localhost:1024-", "listen,accept";
    permission java.io.FilePermission "<<ALL FILES>>", "read,write,delete";
    permission java.rmi.RMISecurityManager;
};
```

### 5. Configuración del Servidor Web

#### Apache (.htaccess)
```apache
# Activar rewrite engine
RewriteEngine On

# Redirigir todas las peticiones a index.php
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule ^(.*)$ index.php [QSA,L]

# Configuración de seguridad
<Files "*.php">
    Require all granted
</Files>

<Files "config/*">
    Require all denied
</Files>
```

#### Configuración Virtual Host
```apache
<VirtualHost *:80>
    ServerName hermosacartagena.local
    DocumentRoot "/path/to/Manejo_Hotel_project/views"
    
    <Directory "/path/to/Manejo_Hotel_project/views">
        AllowOverride All
        Require all granted
    </Directory>
    
    ErrorLog ${APACHE_LOG_DIR}/hermosacartagena_error.log
    CustomLog ${APACHE_LOG_DIR}/hermosacartagena_access.log combined
</VirtualHost>
```

### 6. Pruebas de Integración

#### Prueba 1: Autenticación
```bash
# 1. Acceder al login
curl -X POST http://localhost/login.php \
  -d "username=admin&password=admin123&csrf_token=token"

# 2. Verificar respuesta JSON
# {"success": true, "redirect": "views/admin/dashboard.php"}
```

#### Prueba 2: API de Servicios
```bash
# Obtener todos los servicios
curl -X GET http://localhost/backend/api/services.php

# Buscar servicios
curl -X GET "http://localhost/backend/api/services.php?action=search&type=tour"
```

#### Prueba 3: Sistema Distribuido
```bash
# 1. Iniciar servidor
cd java && ant run-server

# 2. Probar conexión
ant run-client
# Seleccionar opción 12 (probar conexión)
# Debe mostrar "OK"
```

### 7. Monitoreo y Logging

#### Logs de PHP
```php
// backend/config/database.php
class DatabaseLogger {
    public function logError($message, $exception = null) {
        $timestamp = date('Y-m-d H:i:s');
        $logMessage = "[$timestamp] ERROR: $message";
        
        file_put_contents('../logs/error.log', $logMessage, FILE_APPEND);
    }
}
```

#### Logs de Java
```java
// java/src/com/hermosacartagena/server/ServidorServicios.java
private void logOperation(String operation, String details) {
    System.out.println("[" + LocalDateTime.now() + "] " + operation + ": " + details);
}
```

### 8. Optimización de Rendimiento

#### Caching en PHP
```php
// backend/config/database.php
class Database {
    private static $instance = null;
    
    public static function getInstance() {
        if (self::$instance === null) {
            self::$instance = new Database();
        }
        return self::$instance;
    }
}
```

#### Pool de Conexiones Java
```java
// Implementar pool de conexiones para mejor rendimiento
private Connection obtenerConexion() throws SQLException {
    // Implementar pool de conexiones
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
}
```

### 9. Manejo de Errores

#### Errores de Conexión
```php
try {
    $conn = new PDO($dsn, $username, $password);
} catch (PDOException $e) {
    error_log("Error de conexión: " . $e->getMessage());
    header('HTTP/1.1 500 Internal Server Error');
    echo json_encode(['success' => false, 'message' => 'Error de conexión']);
}
```

#### Errores RMI
```java
try {
    servicioStub = (ServicioStub) registry.lookup(SERVICE_NAME);
} catch (RemoteException e) {
    System.err.println("Error de comunicación RMI: " + e.getMessage());
    // Implementar reintentos o fallback
}
```

### 10. Deploy en Producción

#### Checklist de Deploy
- [ ] Base de datos configurada y con datos
- [ ] Configuración PHP actualizada
- [ ] Permisos de archivos correctos
- [ ] Servidor web configurado
- [ ] SSL/TLS configurado
- [ ] Logs configurados
- [ ] Sistema distribuido probado
- [ ] Backup de base de datos realizado

#### Comandos de Deploy
```bash
# 1. Backup base de datos
mysqldump -u root -p hermosa_cartagena > backup_$(date +%Y%m%d).sql

# 2. Actualizar archivos
rsync -av --exclude='.git' ./ /var/www/hermosacartagena/

# 3. Configurar permisos
chown -R www-data:www-data /var/www/hermosacartagena
chmod -R 755 /var/www/hermosacartagena
chmod -R 644 /var/www/hermosacartagena/*.php

# 4. Reiniciar servicios
systemctl restart apache2
systemctl restart mysql

# 5. Verificar funcionamiento
curl -I http://localhost
```

## Troubleshooting de Integración

### Problemas Comunes

#### 1. Error de Conexión a BD
```
Error: SQLSTATE[HY000] [1045] Access denied for user
```
**Solución**: Verificar credenciales en `backend/config/database.php`

#### 2. Error de RMI
```
java.rmi.ConnectException: Connection refused
```
**Solución**: Asegurar que el servidor RMI esté corriendo en el puerto 1099

#### 3. Error de CSRF
```
Token de seguridad inválido
```
**Solución**: Verificar que el token se genere y valide correctamente

#### 4. Error de Sesión
```
Sesión expirada
```
**Solución**: Ajustar tiempo de timeout en `assets/js/auth.js`

### Herramientas de Debug

#### PHP
```bash
# Habilitar errores PHP
ini_set('display_errors', 1);
error_reporting(E_ALL);

# Logs de errores
tail -f /var/log/apache2/error.log
```

#### Java
```bash
# Ejecutar con debug
ant debug-server

# Logs del servidor
tail -f logs/servidor.log
```

#### MySQL
```bash
# Verificar conexión
mysql -u root -p -e "SELECT 1;"

# Verificar procesos
SHOW PROCESSLIST;
```

## Validación Final

### Checklist de Integración Completa

#### Frontend
- [ ] Login funciona correctamente
- [ ] Redirección según rol funciona
- [ ] Interfaces responsive
- [ ] Validación de formularios
- [ ] Manejo de errores

#### Backend
- [ ] API endpoints funcionan
- [ ] Autenticación segura
- [ ] CRUD operaciones funcionan
- [ ] Logs de auditoría
- [ ] Manejo de excepciones

#### Sistema Distribuido
- [ ] Servidor RMI inicia
- [ ] Cliente conecta
- [ ] Operaciones remotas funcionan
- [ ] Serialización correcta
- [ ] Manejo de errores

#### Base de Datos
- [ ] Todas las tablas creadas
- [ ] Datos de prueba insertados
- [ ] Índices funcionando
- [ ] Triggers activos
- [ ] Vistas accesibles

#### Seguridad
- [ ] Tokens CSRF implementados
- [ ] Contraseñas hasheadas
- [ ] Validación de entrada
- [ ] Políticas de seguridad
- [ ] Permisos configurados

---

## Conclusión

Esta guía proporciona los pasos necesarios para integrar completamente todas las componentes del sistema. Siguiendo estos pasos, se logrará una arquitectura robusta y funcional que atienda todos los requisitos del proyecto "Hermosa Cartagena".

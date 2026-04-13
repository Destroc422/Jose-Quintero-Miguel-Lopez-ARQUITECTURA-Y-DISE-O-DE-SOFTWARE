# Sistema de Gestión Turística "Hermosa Cartagena"

## Descripción del Proyecto

Sistema completo de gestión turística para la empresa "Hermosa Cartagena" que permite optimizar la administración, automatizar reservas y pagos, controlar usuarios con roles y permisos, y mejorar la experiencia del cliente.

## Arquitectura del Sistema

### Frontend (Cliente-Servidor Web)
- **HTML5, CSS3, Bootstrap 5**
- **JavaScript Vanilla**
- **Interfaces responsivas según rol**
- **Login con validación y seguridad**

### Backend (Web)
- **PHP 8+ con PDO**
- **Sistema de autenticación completo**
- **CRUD para todas las entidades**
- **Validación de formularios y sesiones**
- **Manejo de errores y logging**

### Sistema Distribuido (Java RMI)
- **Java 11+ con RMI**
- **Sockets y serialización de objetos**
- **Stub para servicios remotos**
- **Servidor con conexión JDBC**
- **Cliente con menú interactivo**

### Base de Datos
- **MySQL 8+**
- **9 tablas relacionales**
- **Índices optimizados**
- **Vistas y triggers**
- **Procedimientos almacenados**

## Estructura del Proyecto

```
Manejo_Hotel_project/
|
|-- database/                    # Scripts de base de datos
|   |-- 01_create_database.sql  # Creación de tablas
|   |-- 02_insert_datos_prueba.sql # Datos de prueba
|
|-- backend/                     # Backend PHP
|   |-- config/
|   |   |-- database.php        # Configuración PDO
|   |-- controllers/
|   |   |-- AuthController.php  # Autenticación
|   |   |-- ValidationController.php # Validaciones
|   |-- models/
|   |   |-- User.php            # Modelo Usuario
|   |   |-- Service.php          # Modelo Servicio
|   |   |-- Reservation.php     # Modelo Reserva
|   |-- api/                     # Endpoints API
|
|-- views/                       # Frontend
|   |-- login.php               # Página de login
|   |-- admin/                  # Interfaces administrador
|   |   |-- dashboard.php
|   |-- employee/               # Interfaces empleado
|   |   |-- dashboard.php
|   |-- client/                 # Interfaces cliente
|   |   |-- dashboard.php
|
|-- assets/                      # Recursos estáticos
|   |-- css/
|   |   |-- style.css           # Estilos personalizados
|   |-- js/
|   |   |-- auth.js             # JavaScript autenticación
|
|-- java/                        # Sistema distribuido
|   |-- src/
|   |   |-- com/hermosacartagena/
|   |       |-- model/
|   |       |   |-- ServicioTuristico.java
|   |       |-- stub/
|   |       |   |-- ServicioStub.java
|   |       |-- server/
|   |       |   |-- ServidorServicios.java
|   |       |-- client/
|   |           |-- ClienteServicios.java
|   |-- build.xml                # Script Ant
```

## Instalación y Configuración

### Requisitos Previos

1. **Servidor Web**: Apache o Nginx
2. **PHP**: Versión 8.0 o superior
3. **MySQL**: Versión 8.0 o superior
4. **Java**: Versión 11 o superior
5. **Apache Ant**: Para compilar el sistema Java

### Base de Datos

1. Crear la base de datos:
```bash
mysql -u root -p < database/01_create_database.sql
```

2. Insertar datos de prueba:
```bash
mysql -u root -p hermosa_cartagena < database/02_insert_datos_prueba.sql
```

### Configuración PHP

1. Configurar el archivo `backend/config/database.php`:
```php
private $host = "localhost";
private $db_name = "hermosa_cartagena";
private $username = "root";
private $password = "tu_password";
```

2. Configurar el servidor web para que apunte al directorio `views/`

### Sistema Distribuido Java

1. Compilar con Ant:
```bash
cd java
ant compile
```

2. Crear JARs:
```bash
ant jar
```

3. Ejecutar el servidor:
```bash
ant run-server
```

4. Ejecutar el cliente (en otra terminal):
```bash
ant run-client
```

## Funcionalidades del Sistema

### Autenticación y Roles
- **Administrador**: Acceso completo al sistema
- **Empleado**: Gestión de reservas y pagos
- **Cliente**: Reservas y consulta de servicios

### Gestión de Servicios
- Crear, leer, actualizar y eliminar servicios
- Búsqueda por tipo y nombre
- Verificación de disponibilidad
- Servicios populares y estadísticas

### Gestión de Reservas
- Creación de reservas automáticas
- Confirmación y cancelación
- Verificación de disponibilidad en tiempo real
- Historial completo

### Gestión de Pagos
- Procesamiento de pagos múltiples métodos
- Soporte para monedas nacionales e internacionales
- Registro de transacciones
- Estados de pago

### Sistema Distribuido
- Comunicación RMI entre cliente y servidor
- Serialización de objetos ServicioTuristico
- Operaciones CRUD remotas
- Manejo de transacciones

## Seguridad Implementada

### Frontend
- Tokens CSRF
- Validación de formularios
- Sanitización de datos
- Sesiones seguras con timeout

### Backend
- Prepared statements (SQL injection prevention)
- Hash de contraseñas (SHA-256)
- Validación de datos de entrada
- Logging de operaciones

### Sistema Distribuido
- Políticas de seguridad Java
- Validación de objetos serializados
- Manejo seguro de excepciones
- Control de acceso remoto

## API Endpoints

### Autenticación
- `POST /backend/api/auth.php?action=login` - Iniciar sesión
- `POST /backend/api/auth.php?action=logout` - Cerrar sesión
- `GET /backend/api/auth.php?action=check_session` - Verificar sesión

### Servicios
- `GET /backend/api/services.php` - Listar servicios
- `GET /backend/api/services.php?action=search` - Buscar servicios
- `POST /backend/api/services.php?action=create` - Crear servicio

### Reservas
- `GET /backend/api/reservations.php` - Listar reservas
- `POST /backend/api/reservations.php?action=create` - Crear reserva
- `PUT /backend/api/reservations.php?action=update` - Actualizar reserva

## Métodos Remotos (Java RMI)

### ServicioStub
- `obtenerTodosLosServicios()` - Listar todos los servicios
- `buscarServiciosPorTipo(tipo)` - Buscar por tipo
- `crearServicio(servicio)` - Crear nuevo servicio
- `actualizarServicio(servicio)` - Actualizar servicio
- `verificarDisponibilidad(id, fecha, personas)` - Verificar disponibilidad

## Base de Datos - Esquema

### Tablas Principales
1. **usuarios** - Usuarios del sistema
2. **roles** - Roles de usuario
3. **clientes** - Información de clientes
4. **empleados** - Información de empleados
5. **proveedores** - Proveedores de servicios
6. **servicios** - Servicios turísticos
7. **reservas** - Reservas de servicios
8. **pagos** - Pagos de reservas
9. **historial_operaciones** - Auditoría

### Vistas Útiles
- `vista_clientes_completa` - Información completa de clientes
- `vista_empleados_completa` - Información completa de empleados
- `vista_servicios_proveedores` - Servicios con proveedores
- `vista_reservas_completa` - Reservas con información relacionada

## Pruebas del Sistema

### Pruebas Unitarias
- Validación de modelos PHP
- Métodos de utilidad
- Lógica de negocio

### Pruebas de Integración
- Conexión a base de datos
- Operaciones CRUD
- Autenticación

### Pruebas del Sistema Distribuido
- Conexión RMI
- Serialización de objetos
- Operaciones remotas

## Despliegue

### Producción
1. Configurar servidor web Apache/Nginx
2. Configurar PHP con extensiones necesarias
3. Configurar MySQL con usuario dedicado
4. Establecer permisos de archivos
5. Configurar SSL/TLS
6. Optimizar rendimiento (caching, etc.)

### Sistema Distribuido
1. Configurar RMI registry
2. Establecer políticas de seguridad
3. Configurar firewall para puertos RMI
4. Monitorear rendimiento

## Mantenimiento

### Tareas Regulares
- Backups de base de datos
- Actualización de dependencias
- Monitoreo de logs
- Limpieza de archivos temporales
- Optimización de base de datos

### Monitoreo
- Logs de errores
- Métricas de rendimiento
- Uso de recursos
- Disponibilidad del servicio

## Troubleshooting Común

### Problemas de Conexión
- Verificar configuración de base de datos
- Revisar permisos de usuario MySQL
- Comprobar que el servidor web esté corriendo

### Problemas de Sesión
- Limpiar cookies del navegador
- Verificar configuración de PHP session
- Revisar tiempo de timeout

### Problemas RMI
- Verificar que el servidor esté corriendo
- Comprobar políticas de seguridad
- Revisar configuración de red

## Contribución

1. Fork del proyecto
2. Crear rama de feature
3. Implementar cambios
4. Agregar pruebas
5. Enviar Pull Request

## Licencia

Este proyecto es propiedad de "Hermosa Cartagena" y está protegido por derechos de autor.

## Contacto

- **Empresa**: Hermosa Cartagena
- **Email**: info@hermosacartagena.com
- **Teléfono**: +57 5 660 0000
- **Dirección**: Calle de la Inquisición #38-45, Cartagena, Colombia

---

## Versión

**Versión Actual**: 1.0.0  
**Fecha de Lanzamiento**: Diciembre 2024  
**Última Actualización**: Diciembre 2024

## Tecnologías Utilizadas

- **Frontend**: HTML5, CSS3, Bootstrap 5, JavaScript ES6+
- **Backend**: PHP 8.2, MySQL 8.0, Apache 2.4
- **Sistema Distribuido**: Java 11, RMI, JDBC, Ant
- **Herramientas**: Git, Visual Studio Code, MySQL Workbench
- **Testing**: PHPUnit (PHP), JUnit (Java)

---

*Este sistema ha sido desarrollado como solución integral para la gestión turística, optimizando procesos y mejorando la experiencia tanto para administradores como para clientes.*

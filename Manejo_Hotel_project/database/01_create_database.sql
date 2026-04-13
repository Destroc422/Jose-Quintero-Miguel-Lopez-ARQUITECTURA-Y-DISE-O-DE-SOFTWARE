-- =============================================
-- SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
-- FASE 1: CREACIÓN DE BASE DE DATOS Y TABLAS
-- =============================================

-- Eliminar base de datos si existe para limpiar instalación
DROP DATABASE IF EXISTS hermosa_cartagena;

-- Crear base de datos principal
CREATE DATABASE hermosa_cartagena 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_spanish_ci;

-- Seleccionar la base de datos para usarla
USE hermosa_cartagena;

-- =============================================
-- TABLA DE ROLES DEL SISTEMA
-- =============================================
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('activo', 'inactivo') DEFAULT 'activo'
);

-- =============================================
-- TABLA DE USUARIOS DEL SISTEMA
-- =============================================
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Almacenará hash SHA-256
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_login TIMESTAMP NULL,
    estado ENUM('activo', 'inactivo', 'bloqueado') DEFAULT 'activo',
    
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- =============================================
-- TABLA DE CLIENTES
-- =============================================
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    tipo_documento ENUM('CC', 'CE', 'PPN', 'TI') NOT NULL,
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento DATE,
    nacionalidad VARCHAR(50) DEFAULT 'Colombiana',
    direccion VARCHAR(255),
    ciudad VARCHAR(100),
    preferencias_contacto ENUM('email', 'telefono', 'whatsapp') DEFAULT 'whatsapp',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);

-- =============================================
-- TABLA DE EMPLEADOS
-- =============================================
CREATE TABLE empleados (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    codigo_empleado VARCHAR(20) NOT NULL UNIQUE,
    cargo VARCHAR(100) NOT NULL,
    departamento VARCHAR(50),
    salario DECIMAL(10,2),
    fecha_contratacion DATE,
    tipo_contrato ENUM('tiempo_completo', 'medio_tiempo', 'por_horas') DEFAULT 'tiempo_completo',
    
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);

-- =============================================
-- TABLA DE PROVEEDORES
-- =============================================
CREATE TABLE proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre_empresa VARCHAR(100) NOT NULL,
    nit VARCHAR(20) NOT NULL UNIQUE,
    contacto_principal VARCHAR(100),
    email_contacto VARCHAR(100),
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    ciudad VARCHAR(50),
    tipo_servicio ENUM('transporte', 'hospedaje', 'alimentacion', 'guia_turistico', 'actividades') NOT NULL,
    estado ENUM('activo', 'inactivo') DEFAULT 'activo',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- TABLA DE SERVICIOS TURÍSTICOS
-- =============================================
CREATE TABLE servicios (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor INT NOT NULL,
    nombre_servicio VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_servicio ENUM('tour', 'hospedaje', 'transporte', 'alimentacion', 'actividad') NOT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    duracion_horas DECIMAL(4,2),
    capacidad_maxima INT,
    ubicacion VARCHAR(255),
    requisitos VARCHAR(500),
    incluye VARCHAR(500),
    no_incluye VARCHAR(500),
    imagenes JSON, -- Almacenar rutas de imágenes
    estado ENUM('activo', 'inactivo', 'temporada') DEFAULT 'activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- =============================================
-- TABLA DE RESERVAS
-- =============================================
CREATE TABLE reservas (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_servicio INT NOT NULL,
    id_empleado INT NULL, -- Empleado que gestiona la reserva
    codigo_reserva VARCHAR(20) NOT NULL UNIQUE,
    fecha_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio_servicio DATE NOT NULL,
    fecha_fin_servicio DATE,
    cantidad_personas INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    precio_total DECIMAL(10,2) NOT NULL,
    estado_reserva ENUM('pendiente', 'confirmada', 'pagada', 'cancelada', 'completada') DEFAULT 'pendiente',
    notas TEXT,
    fecha_confirmacion TIMESTAMP NULL,
    fecha_cancelacion TIMESTAMP NULL,
    motivo_cancelacion VARCHAR(255),
    
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado) ON DELETE SET NULL ON UPDATE CASCADE
);

-- =============================================
-- TABLA DE PAGOS
-- =============================================
CREATE TABLE pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva INT NOT NULL,
    id_empleado INT NULL, -- Empleado que procesa el pago
    monto_pago DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM('efectivo', 'transferencia', 'tarjeta_credito', 'tarjeta_debito', 'paypal', 'nequi', 'daviplata') NOT NULL,
    tipo_pago ENUM('completo', 'abono') NOT NULL,
    banco VARCHAR(50) NULL,
    numero_referencia VARCHAR(100) NULL,
    estado_pago ENUM('pendiente', 'procesando', 'aprobado', 'rechazado', 'reembolsado') DEFAULT 'pendiente',
    moneda ENUM('COP', 'USD', 'EUR') DEFAULT 'COP',
    tasa_cambio DECIMAL(10,4) DEFAULT 1.0000,
    comision_pago DECIMAL(10,2) DEFAULT 0.00,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_aprobacion TIMESTAMP NULL,
    evidencia_pago VARCHAR(255), -- Ruta a archivo de comprobante
    
    FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado) ON DELETE SET NULL ON UPDATE CASCADE
);

-- =============================================
-- TABLA DE HISTORIAL DE OPERACIONES
-- =============================================
CREATE TABLE historial_operaciones (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_reserva INT NULL,
    id_pago INT NULL,
    tipo_operacion ENUM('creacion', 'actualizacion', 'eliminacion', 'login', 'logout', 'pago', 'cancelacion') NOT NULL,
    descripcion_operacion TEXT NOT NULL,
    tabla_afectada VARCHAR(50),
    id_registro_afectado INT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    fecha_operacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (id_pago) REFERENCES pagos(id_pago) ON DELETE SET NULL ON UPDATE CASCADE
);

-- =============================================
-- TABLA DE CONFIGURACIÓN DEL SISTEMA
-- =============================================
CREATE TABLE configuracion_sistema (
    id_config INT AUTO_INCREMENT PRIMARY KEY,
    parametro VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT NOT NULL,
    descripcion VARCHAR(255),
    tipo_dato ENUM('texto', 'numero', 'booleano', 'json') DEFAULT 'texto',
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================
-- ÍNDICES PARA OPTIMIZAR CONSULTAS
-- =============================================

-- Índices para tabla usuarios
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_estado ON usuarios(estado);

-- Índices para tabla clientes
CREATE INDEX idx_clientes_documento ON clientes(numero_documento);
CREATE INDEX idx_clientes_usuario ON clientes(id_usuario);

-- Índices para tabla empleados
CREATE INDEX idx_empleados_codigo ON empleados(codigo_empleado);
CREATE INDEX idx_empleados_cargo ON empleados(cargo);

-- Índices para tabla servicios
CREATE INDEX idx_servicios_tipo ON servicios(tipo_servicio);
CREATE INDEX idx_servicios_proveedor ON servicios(id_proveedor);
CREATE INDEX idx_servicios_estado ON servicios(estado);
CREATE INDEX idx_servicios_precio ON servicios(precio_base);

-- Índices para tabla reservas
CREATE INDEX idx_reservas_cliente ON reservas(id_cliente);
CREATE INDEX idx_reservas_servicio ON reservas(id_servicio);
CREATE INDEX idx_reservas_empleado ON reservas(id_empleado);
CREATE INDEX idx_reservas_estado ON reservas(estado_reserva);
CREATE INDEX idx_reservas_fecha ON reservas(fecha_inicio_servicio);
CREATE INDEX idx_reservas_codigo ON reservas(codigo_reserva);

-- Índices para tabla pagos
CREATE INDEX idx_pagos_reserva ON pagos(id_reserva);
CREATE INDEX idx_pagos_estado ON pagos(estado_pago);
CREATE INDEX idx_pagos_fecha ON pagos(fecha_pago);
CREATE INDEX idx_pagos_metodo ON pagos(metodo_pago);

-- Índices para tabla historial
CREATE INDEX idx_historial_usuario ON historial_operaciones(id_usuario);
CREATE INDEX idx_historial_tipo ON historial_operaciones(tipo_operacion);
CREATE INDEX idx_historial_fecha ON historial_operaciones(fecha_operacion);

-- =============================================
-- VISTAS ÚTILES PARA CONSULTAS FRECUENTES
-- =============================================

-- Vista para información completa de clientes
CREATE VIEW vista_clientes_completa AS
SELECT 
    c.id_cliente,
    c.tipo_documento,
    c.numero_documento,
    c.fecha_nacimiento,
    c.nacionalidad,
    c.direccion,
    c.ciudad,
    c.preferencias_contacto,
    u.nombre_completo,
    u.email,
    u.telefono,
    u.fecha_creacion,
    u.estado
FROM clientes c
JOIN usuarios u ON c.id_usuario = u.id_usuario;

-- Vista para información completa de empleados
CREATE VIEW vista_empleados_completa AS
SELECT 
    e.id_empleado,
    e.codigo_empleado,
    e.cargo,
    e.departamento,
    e.salario,
    e.fecha_contratacion,
    e.tipo_contrato,
    u.nombre_completo,
    u.email,
    u.telefono,
    u.estado
FROM empleados e
JOIN usuarios u ON e.id_usuario = u.id_usuario;

-- Vista para servicios con información de proveedores
CREATE VIEW vista_servicios_proveedores AS
SELECT 
    s.id_servicio,
    s.nombre_servicio,
    s.descripcion,
    s.tipo_servicio,
    s.precio_base,
    s.duracion_horas,
    s.capacidad_maxima,
    s.ubicacion,
    s.estado,
    p.nombre_empresa,
    p.contacto_principal,
    p.telefono as telefono_proveedor
FROM servicios s
JOIN proveedores p ON s.id_proveedor = p.id_proveedor;

-- Vista para reservas con información completa
CREATE VIEW vista_reservas_completa AS
SELECT 
    r.id_reserva,
    r.codigo_reserva,
    r.fecha_reserva,
    r.fecha_inicio_servicio,
    r.fecha_fin_servicio,
    r.cantidad_personas,
    r.precio_total,
    r.estado_reserva,
    c.nombre_completo as nombre_cliente,
    c.email as email_cliente,
    s.nombre_servicio,
    e.nombre_completo as nombre_empleado
FROM reservas r
JOIN vista_clientes_completa c ON r.id_cliente = c.id_cliente
JOIN servicios s ON r.id_servicio = s.id_servicio
LEFT JOIN vista_empleados_completa e ON r.id_empleado = e.id_empleado;

-- =============================================
-- TRIGGERS PARA AUDITORÍA
-- =============================================

-- Trigger para registrar cambios en usuarios
DELIMITER //
CREATE TRIGGER tr_usuarios_insert
AFTER INSERT ON usuarios
FOR EACH ROW
BEGIN
    INSERT INTO historial_operaciones (id_usuario, tipo_operacion, descripcion_operacion, tabla_afectada, id_registro_afectado)
    VALUES (NEW.id_usuario, 'creacion', CONCAT('Usuario creado: ', NEW.username), 'usuarios', NEW.id_usuario);
END//
DELIMITER ;

-- Trigger para registrar cambios en reservas
DELIMITER //
CREATE TRIGGER tr_reservas_insert
AFTER INSERT ON reservas
FOR EACH ROW
BEGIN
    INSERT INTO historial_operaciones (id_usuario, id_reserva, tipo_operacion, descripcion_operacion, tabla_afectada, id_registro_afectado)
    VALUES (NEW.id_cliente, NEW.id_reserva, 'creacion', CONCAT('Reserva creada: ', NEW.codigo_reserva), 'reservas', NEW.id_reserva);
END//
DELIMITER ;

-- Trigger para registrar pagos
DELIMITER //
CREATE TRIGGER tr_pagos_insert
AFTER INSERT ON pagos
FOR EACH ROW
BEGIN
    INSERT INTO historial_operaciones (id_usuario, id_pago, id_reserva, tipo_operacion, descripcion_operacion, tabla_afectada, id_registro_afectado)
    VALUES (
        (SELECT id_cliente FROM reservas WHERE id_reserva = NEW.id_reserva), 
        NEW.id_pago, 
        NEW.id_reserva, 
        'pago', 
        CONCAT('Pago registrado: $', NEW.monto_pago, ' - ', NEW.metodo_pago), 
        'pagos', 
        NEW.id_pago
    );
END//
DELIMITER ;

-- =============================================
-- COMENTARIOS FINALES
-- =============================================
-- Base de datos creada exitosamente con:
-- - 9 tablas principales con relaciones properas
-- - Índices optimizados para rendimiento
-- - Vistas para consultas frecuentes
-- - Triggers para auditoría automática
-- - Soporte para UTF-8 caracteres españoles
-- - Enumeraciones para integridad de datos

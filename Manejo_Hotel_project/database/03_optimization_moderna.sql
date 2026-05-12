-- =============================================
-- SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
-- FASE 7: OPTIMIZACIÓN DE BASE DE DATOS MODERNA
-- =============================================

-- Usar la base de datos existente
USE hermosa_cartagena;

-- =============================================
-- ÍNDICES AVANZADOS PARA ALTO RENDIMIENTO
-- =============================================

-- Índices compuestos para consultas frecuentes
CREATE INDEX idx_reservas_cliente_fecha_estado ON reservas(id_cliente, fecha_reserva, estado_reserva);
CREATE INDEX idx_reservas_servicio_fecha ON reservas(id_servicio, fecha_reserva);
CREATE INDEX idx_servicios_tipo_precio_disponibilidad ON servicios(tipo_servicio, precio, disponible);
CREATE INDEX idx_pagos_estado_fecha ON pagos(estado_pago, fecha_pago);
CREATE INDEX idx_usuarios_email_estado ON usuarios(email, estado);
CREATE INDEX idx_clientes_usuario_tipo ON clientes(id_usuario, tipo_cliente);

-- Índices para búsquedas de texto completo
CREATE FULLTEXT INDEX idx_servicios_busqueda ON servicios(nombre_servicio, descripcion);
CREATE FULLTEXT INDEX idx_clientes_busqueda ON clientes(nombre_completo, email, telefono);

-- Índices para ordenamiento y paginación
CREATE INDEX idx_servicios_populares ON servicios(calificacion_promedio DESC, numero_reservas DESC);
CREATE INDEX idx_reservas_recientes ON reservas(fecha_reserva DESC);

-- =============================================
-- PARTICIONAMIENTO DE TABLAS GRANDES
-- =============================================

-- Particionar tabla de reservas por año
ALTER TABLE reservas 
PARTITION BY RANGE (YEAR(fecha_reserva)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION pmax VALUES LESS THAN MAXVALUE
);

-- Particionar tabla de pagos por año
ALTER TABLE pagos 
PARTITION BY RANGE (YEAR(fecha_pago)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION pmax VALUES LESS THAN MAXVALUE
);

-- =============================================
-- NUEVAS TABLAS PARA FUNCIONALIDADES MODERNAS
-- =============================================

-- Sistema de notificaciones
CREATE TABLE notificaciones (
    id_notificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    tipo ENUM('reserva', 'pago', 'promocion', 'sistema', 'recordatorio') NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    mensaje TEXT NOT NULL,
    datos_adicionales JSON,
    leida BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_lectura TIMESTAMP NULL,
    fecha_expiracion TIMESTAMP NULL,
    prioridad ENUM('baja', 'media', 'alta', 'urgente') DEFAULT 'media',
    
    INDEX idx_usuario_no_leidas (id_usuario, leida),
    INDEX idx_tipo_fecha (tipo, fecha_creacion),
    INDEX idx_prioridad_no_leidas (prioridad, leida),
    INDEX idx_expiracion (fecha_expiracion),
    
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Sistema de calificaciones y reseñas
CREATE TABLE calificaciones (
    id_calificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_servicio BIGINT NOT NULL,
    id_cliente BIGINT NOT NULL,
    id_reserva BIGINT,
    calificacion INT CHECK (calificacion BETWEEN 1 AND 5),
    titulo_resena VARCHAR(255),
    comentario TEXT,
    fotos JSON,
    respuesta_hotel TEXT,
    fecha_calificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta TIMESTAMP NULL,
    verificada BOOLEAN DEFAULT FALSE,
    util BOOLEAN DEFAULT FALSE,
    
    INDEX idx_servicio_calificacion (id_servicio, calificacion),
    INDEX idx_cliente_calificaciones (id_cliente),
    INDEX idx_reserva_calificacion (id_reserva),
    INDEX idx_calificacion_fecha (calificacion, fecha_calificacion),
    
    FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Sistema de promociones y descuentos
CREATE TABLE promociones (
    id_promocion BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    tipo_promocion ENUM('porcentaje', 'monto_fijo', 'noches_gratis', 'regalo') NOT NULL,
    valor_descuento DECIMAL(10,2),
    porcentaje_descuento DECIMAL(5,2),
    condiciones JSON,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    uso_limite INT DEFAULT NULL,
    uso_actual INT DEFAULT 0,
    codigo_promocional VARCHAR(50) UNIQUE,
    aplicacion_automatica BOOLEAN DEFAULT FALSE,
    servicios_aplicables JSON,
    clientes_aplicables JSON,
    
    INDEX idx_promociones_activas (activa, fecha_inicio, fecha_fin),
    INDEX idx_codigo_promocional (codigo_promocional),
    INDEX idx_tipo_fecha (tipo_promocion, fecha_inicio),
    
    CHECK (fecha_fin >= fecha_inicio),
    CHECK (uso_actual <= uso_limite OR uso_limite IS NULL)
);

-- Historial mejorado de operaciones
CREATE TABLE historial_operaciones (
    id_historial BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    tipo_entidad ENUM('reserva', 'pago', 'servicio', 'cliente', 'usuario', 'promocion') NOT NULL,
    id_entidad BIGINT NOT NULL,
    accion ENUM('crear', 'modificar', 'eliminar', 'cancelar', 'completar', 'activar', 'desactivar') NOT NULL,
    valores_anteriores JSON,
    valores_nuevos JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modulo VARCHAR(50),
    
    INDEX idx_entidad_historial (tipo_entidad, id_entidad),
    INDEX idx_usuario_historial (id_usuario, fecha_accion),
    INDEX idx_fecha_accion (fecha_accion),
    INDEX idx_modulo_accion (modulo, accion),
    
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Sistema de favoritos
CREATE TABLE favoritos (
    id_favorito BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    tipo_entidad ENUM('servicio', 'hotel', 'destino') NOT NULL,
    id_entidad BIGINT NOT NULL,
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_favorito_cliente (id_cliente, tipo_entidad, id_entidad),
    INDEX idx_cliente_favoritos (id_cliente),
    INDEX idx_tipo_entidad (tipo_entidad, id_entidad),
    
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Sistema de wishlist/lista de deseos
CREATE TABLE wishlist (
    id_wishlist BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    nombre_lista VARCHAR(100) NOT NULL,
    descripcion TEXT,
    publica BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_cliente_wishlist (id_cliente),
    INDEX idx_wishlist_publica (publica),
    
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE wishlist_items (
    id_wishlist_item BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_wishlist BIGINT NOT NULL,
    id_servicio BIGINT NOT NULL,
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notas TEXT,
    
    UNIQUE KEY uk_wishlist_servicio (id_wishlist, id_servicio),
    INDEX idx_wishlist_items (id_wishlist),
    INDEX idx_servicio_wishlist (id_servicio),
    
    FOREIGN KEY (id_wishlist) REFERENCES wishlist(id_wishlist) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio) ON DELETE CASCADE ON UPDATE CASCADE
);

-- =============================================
-- VIEWS OPTIMIZADAS PARA CONSULTAS COMPLEJAS
-- =============================================

-- Vista de servicios con calificaciones y disponibilidad
CREATE OR REPLACE VIEW vista_servicios_completos AS
SELECT 
    s.*,
    COALESCE(AVG(c.calificacion), 0) as calificacion_promedio_real,
    COUNT(c.id_calificacion) as total_calificaciones,
    COUNT(r.id_reserva) as total_reservas,
    SUM(CASE WHEN r.estado_reserva = 'confirmada' THEN 1 ELSE 0 END) as reservas_confirmadas,
    GROUP_CONCAT(DISTINCT p.nombre SEPARATOR ', ') as proveedores_asociados
FROM servicios s
LEFT JOIN calificaciones c ON s.id_servicio = c.id_servicio AND c.verificada = TRUE
LEFT JOIN reservas r ON s.id_servicio = r.id_servicio AND r.estado_reserva = 'confirmada'
LEFT JOIN proveedores p ON s.id_proveedor = p.id_proveedor
WHERE s.disponible = TRUE
GROUP BY s.id_servicio;

-- Vista de clientes con actividad reciente
CREATE OR REPLACE VIEW vista_clientes_activos AS
SELECT 
    c.*,
    u.username,
    u.email,
    u.ultimo_login,
    COUNT(r.id_reserva) as total_reservas,
    SUM(r.precio_total) as total_gastado,
    MAX(r.fecha_reserva) as ultima_reserva,
    AVG(CASE WHEN c.calificacion_promedio > 0 THEN c.calificacion_promedio ELSE NULL END) as calificacion_cliente,
    COUNT(DISTINCT f.id_favorito) as total_favoritos
FROM clientes c
JOIN usuarios u ON c.id_usuario = u.id_usuario
LEFT JOIN reservas r ON c.id_cliente = r.id_cliente AND r.estado_reserva = 'confirmada'
LEFT JOIN favoritos f ON c.id_cliente = f.id_cliente
WHERE u.estado = 'activo'
GROUP BY c.id_cliente;

-- Vista de ingresos mensuales
CREATE OR REPLACE VIEW vista_ingresos_mensuales AS
SELECT 
    YEAR(p.fecha_pago) as año,
    MONTH(p.fecha_pago) as mes,
    COUNT(p.id_pago) as total_pagos,
    SUM(p.monto) as ingresos_totales,
    AVG(p.monto) importe_promedio,
    COUNT(DISTINCT p.id_cliente) as clientes_unicos,
    COUNT(DISTINCT s.id_servicio) as servicios_vendidos
FROM pagos p
JOIN reservas r ON p.id_reserva = r.id_reserva
JOIN servicios s ON r.id_servicio = s.id_servicio
WHERE p.estado_pago = 'completado'
GROUP BY YEAR(p.fecha_pago), MONTH(p.fecha_pago)
ORDER BY año DESC, mes DESC;

-- =============================================
-- TRIGGERS PARA MANTENIMIENTO AUTOMÁTICO
-- =============================================

-- Trigger para actualizar estadísticas de servicios
DELIMITER $$
CREATE TRIGGER tr_actualizar_estadisticas_servicio
AFTER INSERT ON calificaciones
FOR EACH ROW
BEGIN
    UPDATE servicios 
    SET 
        calificacion_promedio = (
            SELECT AVG(calificacion) 
            FROM calificaciones 
            WHERE id_servicio = NEW.id_servicio AND verificada = TRUE
        ),
        numero_reservas = (
            SELECT COUNT(*) 
            FROM reservas 
            WHERE id_servicio = NEW.id_servicio AND estado_reserva = 'confirmada'
        )
    WHERE id_servicio = NEW.id_servicio;
END$$
DELIMITER ;

-- Trigger para registrar historial de cambios
DELIMITER $$
CREATE TRIGGER tr_historial_reservas
AFTER UPDATE ON reservas
FOR EACH ROW
BEGIN
    IF OLD.estado_reserva != NEW.estado_reserva THEN
        INSERT INTO historial_operaciones (
            id_usuario, tipo_entidad, id_entidad, accion, 
            valores_anteriores, valores_nuevos, modulo
        ) VALUES (
            NEW.id_cliente, 'reserva', NEW.id_reserva, 'modificar',
            JSON_OBJECT('estado_reserva', OLD.estado_reserva),
            JSON_OBJECT('estado_reserva', NEW.estado_reserva),
            'reservas'
        );
    END IF;
END$$
DELIMITER ;

-- =============================================
-- PROCEDIMIENTOS ALMACENADOS OPTIMIZADOS
-- =============================================

-- Procedimiento para obtener servicios recomendados
DELIMITER $$
CREATE PROCEDURE sp_obtener_servicios_recomendados(
    IN p_id_cliente BIGINT,
    IN p_limite INT DEFAULT 10
)
BEGIN
    -- Obtener servicios basados en historial y preferencias
    SELECT DISTINCT s.*
    FROM servicios s
    LEFT JOIN reservas r ON s.id_servicio = r.id_servicio AND r.id_cliente = p_id_cliente
    LEFT JOIN calificaciones c ON s.id_servicio = c.id_servicio
    WHERE 
        s.disponible = TRUE
        AND s.id_servicio NOT IN (
            SELECT id_servicio FROM reservas WHERE id_cliente = p_id_cliente
        )
        AND (
            -- Servicios del mismo tipo que ha reservado
            s.tipo_servicio IN (
                SELECT DISTINCT s2.tipo_servicio 
                FROM servicios s2 
                JOIN reservas r2 ON s2.id_servicio = r2.id_servicio 
                WHERE r2.id_cliente = p_id_cliente AND r2.estado_reserva = 'confirmada'
            )
            OR
            -- Servicios con alta calificación
            s.calificacion_promedio >= 4.5
            OR
            -- Servicios populares
            s.numero_reservas >= 10
        )
    ORDER BY 
        s.calificacion_promedio DESC,
        s.numero_reservas DESC,
        s.precio ASC
    LIMIT p_limite;
END$$
DELIMITER ;

-- Procedimiento para reporte de ocupación
DELIMITER $$
CREATE PROCEDURE sp_reporte_ocupacion(
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE,
    IN p_id_servicio BIGINT NULL
)
BEGIN
    SELECT 
        s.id_servicio,
        s.nombre_servicio,
        s.tipo_servicio,
        COUNT(r.id_reserva) as total_reservas,
        SUM(r.precio_total) as ingresos_totales,
        AVG(r.precio_total) as precio_promedio,
        COUNT(DISTINCT r.id_cliente) as clientes_unicos,
        CASE 
            WHEN COUNT(r.id_reserva) > 0 THEN 'Alta'
            WHEN COUNT(r.id_reserva) > 5 THEN 'Media'
            ELSE 'Baja'
        END as nivel_ocupacion
    FROM servicios s
    LEFT JOIN reservas r ON s.id_servicio = r.id_servicio
        AND r.fecha_reserva BETWEEN p_fecha_inicio AND p_fecha_fin
        AND r.estado_reserva = 'confirmada'
    WHERE (p_id_servicio IS NULL OR s.id_servicio = p_id_servicio)
    GROUP BY s.id_servicio, s.nombre_servicio, s.tipo_servicio
    ORDER BY total_reservas DESC, ingresos_totales DESC;
END$$
DELIMITER ;

-- =============================================
-- CONFIGURACIÓN DE PERFORMANCE
-- =============================================

-- Aumentar el tamaño del buffer para consultas complejas
SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB

-- Configurar el query cache
SET GLOBAL query_cache_size = 67108864; -- 64MB
SET GLOBAL query_cache_type = ON;

-- Optimizar para conexiones concurrentes
SET GLOBAL max_connections = 200;
SET GLOBAL innodb_thread_concurrency = 0;

-- Configurar logs binarios para replicación
SET GLOBAL log_bin = ON;
SET GLOBAL binlog_format = ROW;
SET GLOBAL sync_binlog = 1;

-- =============================================
-- LIMPIEZA Y MANTENIMIENTO
-- =============================================

-- Limpiar datos antiguos del historial (mantener 2 años)
DELETE FROM historial_operaciones 
WHERE fecha_accion < DATE_SUB(NOW(), INTERVAL 2 YEAR);

-- Limpiar notificaciones expiradas
DELETE FROM notificaciones 
WHERE fecha_expiracion < NOW() AND leida = TRUE;

-- Optimizar tablas
OPTIMIZE TABLE usuarios;
OPTIMIZE TABLE clientes;
OPTIMIZE TABLE servicios;
OPTIMIZE TABLE reservas;
OPTIMIZE TABLE pagos;

-- Analizar tablas para actualizar estadísticas
ANALYZE TABLE usuarios;
ANALYZE TABLE clientes;
ANALYZE TABLE servicios;
ANALYZE TABLE reservas;
ANALYZE TABLE pagos;

-- =============================================
-- RESUMEN DE OPTIMIZACIONES APLICADAS
-- =============================================

-- Índices creados: 15+ índices compuestos y de texto completo
-- Particionamiento: Tablas reservas y pagos por año
-- Nuevas tablas: 6 tablas para funcionalidades modernas
-- Vistas optimizadas: 3 vistas para consultas complejas
-- Triggers: 2 triggers para mantenimiento automático
-- Procedimientos: 2 procedimientos para reportes
-- Configuración: Performance tuning para alto volumen

SELECT 'Optimización de base de datos completada exitosamente' as mensaje,
       NOW() as fecha_optimizacion,
       'Hermosa Cartagena 3.0' as version;

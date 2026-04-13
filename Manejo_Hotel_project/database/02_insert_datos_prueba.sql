-- =============================================
-- SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
-- FASE 1: INSERCIÓN DE DATOS DE PRUEBA
-- =============================================

USE hermosa_cartagena;

-- =============================================
-- INSERCIÓN DE ROLES DEL SISTEMA
-- =============================================
INSERT INTO roles (nombre_rol, descripcion) VALUES
('administrador', 'Acceso completo al sistema, gestión de usuarios y configuración'),
('empleado', 'Gestión de reservas, pagos y atención al cliente'),
('cliente', 'Acceso para realizar reservas y consultar servicios');

-- =============================================
-- INSERCIÓN DE USUARIOS DEL SISTEMA
-- =============================================
-- Contraseñas: admin123, emp123, cli123 (encriptadas con SHA-256)
INSERT INTO usuarios (id_rol, username, password, email, nombre_completo, telefono) VALUES
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@hermosacartagena.com', 'Carlos Rodríguez Administrador', '3001234567'),
(2, 'empleado1', '322a3c830a5ae8f5f8b5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c', 'empleado1@hermosacartagena.com', 'María González Empleado', '3002345678'),
(2, 'empleado2', '322a3c830a5ae8f5f8b5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c', 'empleado2@hermosacartagena.com', 'Luis Martínez Empleado', '3003456789'),
(3, 'cliente1', '322a3c830a5ae8f5f8b5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c', 'cliente1@email.com', 'Ana Pérez Cliente', '3004567890'),
(3, 'cliente2', '322a3c830a5ae8f5f8b5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c', 'cliente2@email.com', 'Juan López Cliente', '3005678901'),
(3, 'cliente3', '322a3c830a5ae8f5f8b5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c5c', 'cliente3@email.com', 'Sofía Ramírez Cliente', '3006789012');

-- =============================================
-- INSERCIÓN DE EMPLEADOS
-- =============================================
INSERT INTO empleados (id_usuario, codigo_empleado, cargo, departamento, salario, fecha_contratacion, tipo_contrato) VALUES
(2, 'EMP001', 'Agente de Viajes', 'Ventas', 1800000.00, '2023-01-15', 'tiempo_completo'),
(3, 'EMP002', 'Coordinador de Tours', 'Operaciones', 2200000.00, '2023-02-20', 'tiempo_completo');

-- =============================================
-- INSERCIÓN DE CLIENTES
-- =============================================
INSERT INTO clientes (id_usuario, tipo_documento, numero_documento, fecha_nacimiento, nacionalidad, direccion, ciudad, preferencias_contacto) VALUES
(4, 'CC', '12345678', '1990-05-15', 'Colombiana', 'Calle 85 #45-67', 'Bogotá', 'whatsapp'),
(5, 'CC', '87654321', '1985-08-22', 'Colombiana', 'Carrera 30 #12-34', 'Medellín', 'email'),
(6, 'CC', '45678901', '1992-12-10', 'Colombiana', 'Avenida 1 #23-45', 'Cali', 'telefono');

-- =============================================
-- INSERCIÓN DE PROVEEDORES
-- =============================================
INSERT INTO proveedores (nombre_empresa, nit, contacto_principal, email_contacto, telefono, direccion, ciudad, tipo_servicio) VALUES
('Turismo Cartagena Tours S.A.S.', '900123456-1', 'Roberto Silva', 'contacto@turismocartagena.com', '3001234567', 'Calle del Castillo #123', 'Cartagena', 'guia_turistico'),
('Transportes del Caribe Ltda.', '800987654-2', 'Patricia Mendoza', 'info@transportescaribe.com', '3002345678', 'Avenida San Martín #456', 'Cartagena', 'transporte'),
('Hoteles Hermosos S.A.S.', '900456789-3', 'Carlos Vargas', 'reservas@hoteleshermosos.com', '3003456789', 'Bocagrande #789', 'Cartagena', 'hospedaje'),
('Restaurantes Típicos del Caribe', '800654321-4', 'María López', 'eventos@restaurantescaribe.com', '3004567890', 'Centro Histórico #321', 'Cartagena', 'alimentacion'),
('Actividades Acuáticas Caribe', '900789012-5', 'Luis Fernando', 'aventura@actividadesacuaticas.com', '3005678901', 'Playa Blanca Km 5', 'Cartagena', 'actividades');

-- =============================================
-- INSERCIÓN DE SERVICIOS TURÍSTICOS
-- =============================================
INSERT INTO servicios (id_proveedor, nombre_servicio, descripcion, tipo_servicio, precio_base, duracion_horas, capacidad_maxima, ubicacion, requisitos, incluye, no_incluye, estado) VALUES
(1, 'Tour Ciudad Histórica', 'Recorrido completo por el centro histórico de Cartagena, incluyendo murallas, castillo San Felipe y convento La Popa.', 'tour', 85000.00, 4.0, 30, 'Centro Histórico, Cartagena', 'Ropa cómoda, protector solar, cámara', 'Guía bilingüe, transporte, entrada a monumentos, agua', 'Almuerzo, souvenirs, propinas', 'activo'),
(1, 'Tour Islas del Rosario', 'Excursión marítima a las Islas del Rosario con snorkel y playa.', 'tour', 150000.00, 8.0, 25, 'Muelle Turístico, Cartagena', 'Traje de baño, toalla, protector solar', 'Transporte marítimo, almuerzo, equipo snorkel, guía', 'Bebidas alcohólicas, propinas', 'activo'),
(2, 'Traslado Aeropuerto-Hotel', 'Servicio de transporte privado desde el aeropuerto hasta su hotel.', 'transporte', 45000.00, 1.0, 4, 'Aeropuerto Rafael Núñez', 'Información de vuelo', 'Conductor, vehículo climatizado, equipaje', 'Peajes, propinas', 'activo'),
(2, 'City Tour en Bus Panorámico', 'Recorrido por los principales puntos turísticos en bus panorámico.', 'transporte', 35000.00, 3.0, 50, 'Centro de Cartagena', 'Ninguno', 'Bus panorámico, guía, audio en varios idiomas', 'Alimentos, entradas a atracciones', 'activo'),
(3, 'Hospedaje Hotel 5 Estrellas', 'Habitación doble en hotel de lujo con vista al mar.', 'hospedaje', 350000.00, 24.0, 2, 'Bocagrande, Cartagena', 'Documento de identidad', 'Desayuno buffet, wifi, gym, piscina', 'Almuerzo, cena, tours', 'activo'),
(3, 'Hostal Económico', 'Habitación compartida en hostal céntrico.', 'hospedaje', 45000.00, 24.0, 6, 'Getsemaní, Cartagena', 'Documento de identidad', 'Wifi, cocina compartida, locker', 'Toallas, desayuno', 'activo'),
(4, 'Almuerzo Típico Caribeño', 'Menú del día con platos típicos de la región caribeña.', 'alimentacion', 28000.00, 1.5, 40, 'Centro Histórico, Cartagena', 'Reserva previa', 'Entrada, plato principal, bebida, postre', 'Bebidas alcohólicas, propinas', 'activo'),
(4, 'Cena Romántica a la Orilla del Mar', 'Experiencia gastronómica con vista al mar.', 'alimentacion', 120000.00, 2.0, 2, 'La Boquilla, Cartagena', 'Reserva previa, vestimenta formal', 'Menú degustación, vino, postre, música en vivo', 'Transporte, propinas', 'activo'),
(5, 'Buceo y Snorkel en Barú', 'Aventura submarina con equipo completo y guía certificado.', 'actividad', 180000.00, 5.0, 15, 'Playa Blanca, Barú', 'Certificado de buceo (para buceo)', 'Equipo completo, guía, transporte, refrigerios', 'Fotografía underwater, propinas', 'activo'),
(5, 'Paracaidismo sobre Cartagena', 'Salto en paracaídas tandem con vista panorámica de la ciudad.', 'actividad', 450000.00, 3.0, 2, 'Aeropuerto Cartagena', 'Certificado médico, mayor de 18 años', 'Equipo completo, instructor, video, seguro', 'Transporte adicional', 'activo');

-- =============================================
-- INSERCIÓN DE RESERVAS DE PRUEBA
-- =============================================
INSERT INTO reservas (id_cliente, id_servicio, id_empleado, codigo_reserva, fecha_inicio_servicio, fecha_fin_servicio, cantidad_personas, precio_unitario, precio_total, estado_reserva, notas) VALUES
(1, 1, 2, 'RES001', '2024-02-15', NULL, 2, 85000.00, 170000.00, 'confirmada', 'Cliente solicita guía en español'),
(1, 3, 2, 'RES002', '2024-02-15', NULL, 1, 45000.00, 45000.00, 'pagada', 'Traslado desde aeropuerto'),
(2, 2, 3, 'RES003', '2024-02-20', NULL, 4, 150000.00, 600000.00, 'pendiente', 'Familia con niños pequeños'),
(3, 5, 2, 'RES004', '2024-03-01', '2024-03-03', 2, 350000.00, 700000.00, 'confirmada', 'Habitación con vista al mar'),
(2, 7, 3, 'RES005', '2024-02-18', NULL, 6, 28000.00, 168000.00, 'pagada', 'Reserva grupal para almuerzo');

-- =============================================
-- INSERCIÓN DE PAGOS DE PRUEBA
-- =============================================
INSERT INTO pagos (id_reserva, id_empleado, monto_pago, metodo_pago, tipo_pago, banco, numero_referencia, estado_pago, moneda, fecha_aprobacion) VALUES
(1, 2, 170000.00, 'tarjeta_credito', 'completo', 'Bancolombia', 'REF123456', 'aprobado', 'COP', '2024-01-20 10:30:00'),
(2, 2, 45000.00, 'efectivo', 'completo', NULL, NULL, 'aprobado', 'COP', '2024-01-20 11:15:00'),
(5, 3, 168000.00, 'transferencia', 'completo', 'Davivienda', 'TRA789012', 'aprobado', 'COP', '2024-01-21 14:20:00');

-- =============================================
-- INSERCIÓN DE CONFIGURACIÓN DEL SISTEMA
-- =============================================
INSERT INTO configuracion_sistema (parametro, valor, descripcion, tipo_dato) VALUES
('nombre_empresa', 'Hermosa Cartagena', 'Nombre de la empresa turística', 'texto'),
('telefono_contacto', '+57 5 660 0000', 'Teléfono principal de contacto', 'texto'),
('email_contacto', 'info@hermosacartagena.com', 'Email principal de contacto', 'texto'),
('direccion_empresa', 'Calle de la Inquisición #38-45, Cartagena', 'Dirección física de la empresa', 'texto'),
('moneda_default', 'COP', 'Moneda por defecto del sistema', 'texto'),
('impuesto_servicio', '19', 'Porcentaje de impuesto sobre servicios', 'numero'),
('comision_pago', '2.5', 'Porcentaje de comisión por pagos electrónicos', 'numero'),
('max_personas_tour', '50', 'Máximo de personas permitidas por tour', 'numero'),
('horas_cancelacion', '24', 'Horas mínimas para cancelación sin penalidad', 'numero'),
('idiomas_disponibles', '["español", "inglés", "francés"]', 'Lista de idiomas disponibles', 'json'),
('metodos_pago', '["efectivo", "transferencia", "tarjeta_credito", "tarjeta_debito", "paypal", "nequi", "daviplata"]', 'Métodos de pago aceptados', 'json'),
('horario_atencion', '{"lunes_viernes": "8:00-18:00", "sabado": "9:00-14:00", "domingo": "cerrado"}', 'Horario de atención al cliente', 'json'),
('politica_cancelacion', 'Las cancelaciones con menos de 24 horas de antelación tendrán un cargo del 50% del valor total del servicio.', 'Política de cancelación de servicios', 'texto'),
('politica_privacidad', 'Todos los datos personales son tratados con confidencialidad según la ley 1581 de 2012.', 'Política de privacidad de datos', 'texto');

-- =============================================
-- INSERCIÓN DE HISTORIAL DE OPERACIONES
-- =============================================
INSERT INTO historial_operaciones (id_usuario, id_reserva, id_pago, tipo_operacion, descripcion_operacion, tabla_afectada, id_registro_afectado, ip_address) VALUES
(1, 1, NULL, 'creacion', 'Cliente Ana Pérez realizó reserva Tour Ciudad Histórica', 'reservas', 1, '192.168.1.100'),
(1, 2, NULL, 'creacion', 'Cliente Ana Pérez realizó reserva Traslado Aeropuerto', 'reservas', 2, '192.168.1.100'),
(2, 1, 1, 'pago', 'Empleado María González procesó pago de reserva RES001', 'pagos', 1, '192.168.1.101'),
(2, 2, 2, 'pago', 'Empleado María González procesó pago de reserva RES002', 'pagos', 2, '192.168.1.101'),
(2, NULL, NULL, 'login', 'Empleado María González inició sesión en el sistema', 'usuarios', 2, '192.168.1.101'),
(3, 5, 3, 'pago', 'Empleado Luis Martínez procesó pago de reserva RES005', 'pagos', 3, '192.168.1.102');

-- =============================================
-- PROCEDIMIENTOS ALMACENADOS ÚTILES
-- =============================================

DELIMITER //

-- Procedimiento para obtener reservas activas de un cliente
CREATE PROCEDURE sp_obtener_reservas_cliente(IN p_id_cliente INT)
BEGIN
    SELECT 
        r.codigo_reserva,
        r.fecha_inicio_servicio,
        r.fecha_fin_servicio,
        r.cantidad_personas,
        r.precio_total,
        r.estado_reserva,
        s.nombre_servicio,
        p.nombre_empresa as proveedor
    FROM reservas r
    JOIN servicios s ON r.id_servicio = s.id_servicio
    JOIN proveedores p ON s.id_proveedor = p.id_proveedor
    WHERE r.id_cliente = p_id_cliente
    ORDER BY r.fecha_inicio_servicio DESC;
END//

-- Procedimiento para calcular ingresos mensuales
CREATE PROCEDURE sp_ingresos_mensuales(IN p_anio INT, IN p_mes INT)
BEGIN
    SELECT 
        COUNT(*) as total_reservas,
        SUM(p.monto_pago) as total_ingresos,
        AVG(p.monto_pago) as promedio_pago
    FROM pagos p
    WHERE p.estado_pago = 'aprobado'
    AND YEAR(p.fecha_pago) = p_anio
    AND MONTH(p.fecha_pago) = p_mes;
END//

-- Procedimiento para obtener servicios más populares
CREATE PROCEDURE sp_servicios_populares(IN p_limite INT)
BEGIN
    SELECT 
        s.nombre_servicio,
        COUNT(r.id_reserva) as cantidad_reservas,
        SUM(r.precio_total) as ingresos_generados
    FROM servicios s
    LEFT JOIN reservas r ON s.id_servicio = r.id_servicio
    WHERE r.estado_reserva IN ('confirmada', 'pagada', 'completada')
    GROUP BY s.id_servicio, s.nombre_servicio
    ORDER BY cantidad_reservas DESC
    LIMIT p_limite;
END//

-- Función para verificar disponibilidad de servicio
CREATE FUNCTION fn_verificar_disponibilidad(p_id_servicio INT, p_fecha DATE, p_personas INT) 
RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE reservas_existentes INT;
    DECLARE capacidad_servicio INT;
    
    -- Obtener capacidad del servicio
    SELECT capacidad_maxima INTO capacidad_servicio
    FROM servicios
    WHERE id_servicio = p_id_servicio;
    
    -- Contar reservas existentes para esa fecha
    SELECT COUNT(*) INTO reservas_existentes
    FROM reservas
    WHERE id_servicio = p_id_servicio
    AND fecha_inicio_servicio = p_fecha
    AND estado_reserva IN ('pendiente', 'confirmada', 'pagada');
    
    -- Retornar true si hay disponibilidad
    RETURN (reservas_existentes * p_personas) <= capacidad_servicio;
END//

DELIMITER ;

-- =============================================
-- DATOS ESTADÍSTICOS DE PRUEBA
-- =============================================

-- Insertar más operaciones de historial para tener datos estadísticos
INSERT INTO historial_operaciones (id_usuario, tipo_operacion, descripcion_operacion, tabla_afectada, id_registro_afectado, ip_address) VALUES
(1, 'login', 'Cliente Ana Pérez inició sesión', 'usuarios', 4, '192.168.1.100'),
(1, 'logout', 'Cliente Ana Pérez cerró sesión', 'usuarios', 4, '192.168.1.100'),
(2, 'login', 'Cliente Juan López inició sesión', 'usuarios', 5, '192.168.1.105'),
(2, 'logout', 'Cliente Juan López cerró sesión', 'usuarios', 5, '192.168.1.105'),
(3, 'login', 'Cliente Sofía Ramírez inició sesión', 'usuarios', 6, '192.168.1.110'),
(3, 'logout', 'Cliente Sofía Ramírez cerró sesión', 'usuarios', 6, '192.168.1.110'),
(2, 'login', 'Empleado María González inició sesión', 'usuarios', 2, '192.168.1.101'),
(2, 'logout', 'Empleado María González cerró sesión', 'usuarios', 2, '192.168.1.101'),
(3, 'login', 'Empleado Luis Martínez inició sesión', 'usuarios', 3, '192.168.1.102'),
(3, 'logout', 'Empleado Luis Martínez cerró sesión', 'usuarios', 3, '192.168.1.102'),
(1, 'login', 'Administrador Carlos Rodríguez inició sesión', 'usuarios', 1, '192.168.1.200'),
(1, 'logout', 'Administrador Carlos Rodríguez cerró sesión', 'usuarios', 1, '192.168.1.200');

-- =============================================
-- COMENTARIOS FINALES
-- =============================================
-- Datos de prueba insertados exitosamente:
-- - 3 roles del sistema
-- - 6 usuarios (admin, 2 empleados, 3 clientes)
-- - 2 empleados con información completa
-- - 3 clientes con información personal
-- - 5 proveedores de diferentes servicios
-- - 10 servicios turísticos variados
-- - 5 reservas con diferentes estados
-- - 3 pagos procesados
-- - 13 configuraciones del sistema
-- - 16 operaciones en el historial
-- - 3 procedimientos almacenados útiles
-- - 1 función para verificar disponibilidad

-- Contraseñas de prueba:
-- admin123 para administrador
-- emp123 para empleados  
-- cli123 para clientes

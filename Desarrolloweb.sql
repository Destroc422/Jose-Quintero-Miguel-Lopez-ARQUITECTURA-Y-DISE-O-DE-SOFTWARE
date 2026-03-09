-- Base de datos funcional para un sistema de gestión de hotel

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS hotel_management;
USE hotel_management;

-- Tabla de roles
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Tabla de permisos
CREATE TABLE permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Tabla de relación roles-permisos
CREATE TABLE role_permissions (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Tabla de usuarios (personal del hotel)
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Debe ser hash en producción
    email VARCHAR(100) NOT NULL UNIQUE,
    role_id INT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    hire_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Tabla de huéspedes
CREATE TABLE guests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    id_number VARCHAR(20) UNIQUE, -- DNI o pasaporte
    date_of_birth DATE,
    nationality VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de tipos de habitación
CREATE TABLE room_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    price_per_night DECIMAL(10,2) NOT NULL,
    capacity INT NOT NULL,
    amenities TEXT
);

-- Tabla de habitaciones
CREATE TABLE rooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type_id INT,
    status ENUM('available', 'occupied', 'maintenance', 'cleaning') DEFAULT 'available',
    floor INT,
    description TEXT,
    FOREIGN KEY (room_type_id) REFERENCES room_types(id)
);

-- Tabla de reservas
CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    guest_id INT,
    room_id INT,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INT DEFAULT 1,
    total_price DECIMAL(10,2),
    status ENUM('confirmed', 'checked_in', 'checked_out', 'cancelled') DEFAULT 'confirmed',
    special_requests TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Tabla de pagos
CREATE TABLE payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('cash', 'credit_card', 'debit_card', 'bank_transfer', 'paypal') NOT NULL,
    status ENUM('pending', 'completed', 'failed', 'refunded') DEFAULT 'pending',
    transaction_id VARCHAR(100),
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- Tabla de servicios adicionales
CREATE TABLE services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category ENUM('food', 'drink', 'spa', 'laundry', 'transport', 'other') DEFAULT 'other'
);

-- Tabla de servicios utilizados en reservas
CREATE TABLE booking_services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT,
    service_id INT,
    quantity INT DEFAULT 1,
    date_used DATE,
    total_price DECIMAL(10,2),
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Tabla de facturas
CREATE TABLE invoices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT,
    total_amount DECIMAL(10,2) NOT NULL,
    issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    status ENUM('unpaid', 'paid', 'overdue') DEFAULT 'unpaid',
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- Tabla de turnos del personal
CREATE TABLE staff_shifts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    shift_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insertar roles (8 roles)
INSERT INTO roles (name, description) VALUES
('Admin', 'Administrador con acceso completo al sistema'),
('General_Manager', 'Gerente general con control operativo'),
('Front_DeskJ_Manager', 'Gerente de recepción'),
('Receptionist', 'Recepcionista para check-in/check-out'),
('Housekeeper', 'Personal de limpieza'),
('Chef', 'Chef del restaurante'),
('Security_Guard', 'Guardia de seguridad'),
('Maintenance_Technician', 'Técnico de mantenimiento');

-- Insertar permisos
INSERT INTO permissions (name, description) VALUES
('create_booking', 'Crear nuevas reservas'),
('view_bookings', 'Ver todas las reservas'),
('edit_booking', 'Editar reservas existentes'),
('cancel_booking', 'Cancelar reservas'),
('view_guests', 'Ver información de huéspedes'),
('edit_guests', 'Editar información de huéspedes'),
('manage_rooms', 'Gestionar habitaciones y tipos'),
('view_reports', 'Ver reportes financieros'),
('manage_staff', 'Gestionar personal'),
('manage_roles', 'Gestionar roles y permisos'),
('process_payments', 'Procesar pagos'),
('view_services', 'Ver servicios adicionales'),
('manage_services', 'Gestionar servicios adicionales'),
('view_shifts', 'Ver turnos del personal'),
('manage_shifts', 'Gestionar turnos del personal');

-- Asignar permisos a roles
-- Admin: todos los permisos
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'Admin';

-- General Manager: la mayoría excepto manage_roles
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'General_Manager' AND p.name NOT IN ('manage_roles');

-- Front Desk Manager: permisos relacionados con reservas y huéspedes
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Front_DeskJ_Manager' AND p.name IN ('create_booking', 'view_bookings', 'edit_booking', 'cancel_booking', 'view_guests', 'edit_guests', 'process_payments');

-- Receptionist: similares al Front Desk Manager pero menos edición
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Receptionist' AND p.name IN ('create_booking', 'view_bookings', 'edit_booking', 'view_guests', 'process_payments');

-- Housekeeper: ver habitaciones y reservas
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Housekeeper' AND p.name IN ('view_bookings', 'manage_rooms');

-- Chef: gestionar servicios de comida
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Chef' AND p.name IN ('view_services', 'manage_services');

-- Security Guard: ver reservas y turnos
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Security_Guard' AND p.name IN ('view_bookings', 'view_guests', 'view_shifts');

-- Maintenance Technician: gestionar habitaciones
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'Maintenance_Technician' AND p.name IN ('manage_rooms');

-- Insertar tipos de habitación de ejemplo
INSERT INTO room_types (name, description, price_per_night, capacity, amenities) VALUES
('Single', 'Habitación_individual', 50.00, 1, 'WiFi, TV, baño privado'),
('Double', 'Habitación_doble', 80.00, 2, 'WiFi, TV, baño privado, balcón'),
('Suite', 'Suite_de_lujo', 150.00, 4, 'WiFi, TV, baño privado, jacuzzi, minibar'),
('Family', 'Habitación_familiar', 120.00, 4, 'WiFi, TV, baño privado, cocina pequeña');

-- Insertar habitaciones de ejemplo
INSERT INTO rooms (room_number, room_type_id, status, floor, description) VALUES
('101', 1, 'available', 1, 'Vista a la calle'),
('102', 1, 'available', 1, 'Vista al jardín'),
('201', 2, 'available', 2, 'Vista a la piscina'),
('202', 2, 'maintenance', 2, 'Necesita reparación'),
('301', 3, 'available', 3, 'Suite ejecutiva'),
('401', 4, 'available', 4, 'Habitación familiar grande');

-- Insertar servicios de ejemplo
INSERT INTO services (name, description, price, category) VALUES
('Desayuno continental', 'Desayuno buffet', 15.00, 'food'),
('Almuerzo', 'Menú_día', 25.00, 'food'),
('Cena', 'Menú_gourmet', 40.00, 'food'),
('Masaje', 'Masaje_por_hora', 60.00, 'spa'),
('Lavandería', 'Lavado_planchado_por_kg', 5.00, 'laundry'),
('Transporte_aeropuerto', 'Traslado_taxi', 30.00, 'transport');

-- Usuario admin de ejemplo (password: admin123 - en producción usar hash)
INSERT INTO users (username, password, email, role_id, first_name, last_name, phone, hire_date) VALUES
('admin', 'admin123', 'admin@hotel.com', 1, 'Admin', 'User', '123456789', '2023-01-01');

-- Huésped de ejemplo
INSERT INTO guests (first_name, last_name, email, phone, address, id_number, date_of_birth, nationality) VALUES
('Juan', 'Pérez', 'juan.perez@email.com', '987654321', 'Calle Principal 123', '12345678', '1985-05-15', 'Español');

-- Reserva de ejemplo
INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, number_of_guests, total_price, status) VALUES
(1, 1, '2024-03-10', '2024-03-12', 1, 100.00, 'confirmed');

-- Pago de ejemplo
INSERT INTO payments (booking_id, amount, payment_method, status) VALUES
(1, 100.00, 'credit_card', 'completed');

-- Servicio usado en reserva
INSERT INTO booking_services (booking_id, service_id, quantity, date_used, total_price) VALUES
(1, 1, 2, '2024-03-10', 30.00);

-- Factura de ejemplo
INSERT INTO invoices (booking_id, total_amount, due_date, status) VALUES
(1, 130.00, '2024-03-15', 'paid');

-- Turno de ejemplo
INSERT INTO staff_shifts (user_id, shift_date, start_time, end_time, notes) VALUES
(1, '2024-03-10', '08:00:00', '16:00:00', 'Turno de mañana');

-- Script de esquema mínimo para el proyecto Manejo_Hotel
CREATE DATABASE IF NOT EXISTS hotel DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE hotel;

CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario VARCHAR(50) NOT NULL UNIQUE,
  clave VARCHAR(100) NOT NULL,
  rol VARCHAR(20) NOT NULL
);

CREATE TABLE habitaciones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  numero VARCHAR(10) NOT NULL UNIQUE,
  tipo VARCHAR(50),
  precio DECIMAL(10,2),
  estado VARCHAR(20)
);

CREATE TABLE reservas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  habitacion_id INT,
  usuario_id INT,
  entrada DATE,
  salida DATE,
  estado VARCHAR(20),
  FOREIGN KEY (habitacion_id) REFERENCES habitaciones(id) ON DELETE SET NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Datos iniciales
INSERT INTO usuarios (usuario, clave, rol) VALUES ('admin', 'admin123', 'admin');
INSERT INTO usuarios (usuario, clave, rol) VALUES ('cliente', 'cliente123', 'cliente');

INSERT INTO habitaciones (numero, tipo, precio, estado) VALUES ('101','Sencilla',50,'Disponible');
INSERT INTO habitaciones (numero, tipo, precio, estado) VALUES ('102','Doble',80,'Ocupada');
INSERT INTO habitaciones (numero, tipo, precio, estado) VALUES ('201','Suite',150,'Disponible');

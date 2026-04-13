# API Documentation - Hermosa Cartagena

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Base URL](#base-url)
4. [Common Headers](#common-headers)
5. [Error Handling](#error-handling)
6. [Endpoints](#endpoints)
   - [Authentication](#authentication-endpoints)
   - [Users](#users-endpoints)
   - [Services](#services-endpoints)
   - [Reservations](#reservations-endpoints)
   - [Payments](#payments-endpoints)
   - [Reports](#reports-endpoints)
7. [Data Models](#data-models)
8. [Rate Limiting](#rate-limiting)
9. [Versioning](#versioning)

## Overview

The Hermosa Cartagena API provides RESTful endpoints for managing a complete tourism management system. The API follows REST principles and uses JSON for data exchange.

### Key Features

- JWT-based authentication
- Role-based access control (ADMIN, EMPLEADO, CLIENTE)
- RESTful design with proper HTTP methods
- Comprehensive error handling
- Pagination support
- Data validation
- Audit trails

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. All protected endpoints require a valid JWT token in the `Authorization` header.

### Login Endpoint

```http
POST /api/auth/login
Content-Type: application/json

{
  "usuario": "admin",
  "password": "password123"
}
```

### Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "usuario": {
    "idUsuario": 1,
    "usuario": "admin",
    "email": "admin@test.com",
    "nombreCompleto": "Administrator",
    "nombreRol": "ADMIN",
    "estado": "activo"
  },
  "mensaje": "Login exitoso"
}
```

### Using the Token

Include the JWT token in the `Authorization` header for all protected requests:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Base URL

```
Development: http://localhost:8080/api
Production: https://api.hermosacartagena.com/api
```

## Common Headers

| Header | Description | Required |
|--------|-------------|----------|
| `Authorization` | JWT token for authentication | Yes (protected endpoints) |
| `Content-Type` | Media type of the request body | Yes (POST/PUT/PATCH) |
| `Accept` | Media type of the response | Optional |

## Error Handling

The API returns appropriate HTTP status codes and error responses:

### Standard Error Response

```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/usuarios",
  "code": "VALIDATION_001",
  "details": {
    "field": "email",
    "message": "Email is required"
  }
}
```

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Request successful, no content returned |
| 400 | Bad Request - Invalid request data |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource conflict |
| 422 | Unprocessable Entity - Validation failed |
| 500 | Internal Server Error - Server error |

## Endpoints

### Authentication Endpoints

#### Login
```http
POST /api/auth/login
```

**Request Body:**
```json
{
  "usuario": "string",
  "password": "string"
}
```

**Response:** [LoginResponse](#loginresponse)

#### Refresh Token
```http
POST /api/auth/refresh
```

**Request Body:**
```json
{
  "refreshToken": "string"
}
```

#### Logout
```http
POST /api/auth/logout
```

**Request Body:**
```json
{
  "token": "string"
}
```

#### Validate Token
```http
POST /api/auth/validate
```

**Request Body:**
```json
{
  "token": "string"
}
```

#### Reset Password
```http
POST /api/auth/reset-password
```

**Request Body:**
```json
{
  "email": "string"
}
```

### Users Endpoints

#### Get All Users
```http
GET /api/usuarios?page=0&size=10&sortBy=idUsuario&sortDir=ASC
```

**Query Parameters:**
- `page` (int): Page number (default: 0)
- `size` (int): Page size (default: 10)
- `sortBy` (string): Sort field (default: idUsuario)
- `sortDir` (string): Sort direction (ASC/DESC, default: ASC)

**Response:** [PaginatedResponse](#paginatedresponse) of [UsuarioDTO](#usuariodto)

**Permissions:** ADMIN

#### Get User by ID
```http
GET /api/usuarios/{id}
```

**Response:** [UsuarioDTO](#usuariodto)

**Permissions:** ADMIN or own user

#### Create User
```http
POST /api/usuarios
```

**Request Body:** [UsuarioDTO](#usuariodto)

**Response:** [UsuarioDTO](#usuariodto)

**Permissions:** ADMIN

#### Update User
```http
PUT /api/usuarios/{id}
```

**Request Body:** [UsuarioDTO](#usuariodto)

**Response:** [UsuarioDTO](#usuariodto)

**Permissions:** ADMIN or own user

#### Delete User
```http
DELETE /api/usuarios/{id}
```

**Response:** 204 No Content

**Permissions:** ADMIN

#### Change User Status
```http
PATCH /api/usuarios/{id}/estado?estado={estado}
```

**Query Parameters:**
- `estado` (string): New status (activo/inactivo)

**Response:** [UsuarioDTO](#usuariodto)

**Permissions:** ADMIN

#### Reset Failed Attempts
```http
PATCH /api/usuarios/{id}/reiniciar-intentos
```

**Response:** 200 OK

**Permissions:** ADMIN

#### Block User Account
```http
PATCH /api/usuarios/{id}/bloquear?minutos={minutos}
```

**Query Parameters:**
- `minutos` (int): Block duration in minutes (default: 30)

**Response:** 200 OK

**Permissions:** ADMIN

#### Search Users
```http
GET /api/usuarios/buscar?texto={texto}&page=0&size=10
```

**Query Parameters:**
- `texto` (string): Search text
- `page` (int): Page number (default: 0)
- `size` (int): Page size (default: 10)

**Response:** [PaginatedResponse](#paginatedresponse) of [UsuarioDTO](#usuariodto)

**Permissions:** ADMIN

#### Get Users by Role
```http
GET /api/usuarios/rol/{idRol}
```

**Response:** Array of [UsuarioDTO](#usuariodto)

**Permissions:** ADMIN

#### Get Active Users
```http
GET /api/usuarios/activos
```

**Response:** Array of [UsuarioDTO](#usuariodto)

**Permissions:** ADMIN

#### Get User Statistics
```http
GET /api/usuarios/estadisticas
```

**Response:** Array of statistics
```json
[
  100,  // Total users
  80,   // Active users
  20,   // Inactive users
  5,    // Blocked users
  1500.50,  // Average monthly spending
  4.5   // Average rating
]
```

**Permissions:** ADMIN

### Services Endpoints

#### Get All Services
```http
GET /api/servicios?page=0&size=10&sortBy=nombreServicio&sortDir=ASC
```

**Response:** [PaginatedResponse](#paginatedresponse) of [ServicioDTO](#serviciodto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE

#### Get Service by ID
```http
GET /api/servicios/{id}
```

**Response:** [ServicioDTO](#serviciodto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE

#### Create Service
```http
POST /api/servicios
```

**Request Body:** [ServicioDTO](#serviciodto)

**Response:** [ServicioDTO](#serviciodto)

**Permissions:** ADMIN

#### Update Service
```http
PUT /api/servicios/{id}
```

**Request Body:** [ServicioDTO](#serviciodto)

**Response:** [ServicioDTO](#serviciodto)

**Permissions:** ADMIN

#### Delete Service
```http
DELETE /api/servicios/{id}
```

**Response:** 204 No Content

**Permissions:** ADMIN

#### Search Services
```http
GET /api/servicios/buscar?texto={texto}&tipo={tipo}&precioMin={precioMin}&precioMax={precioMax}
```

**Query Parameters:**
- `texto` (string): Search text
- `tipo` (string): Service type (tour/hospedaje/transporte/alimentacion/actividad)
- `precioMin` (decimal): Minimum price
- `precioMax` (decimal): Maximum price

**Response:** [PaginatedResponse](#paginatedresponse) of [ServicioDTO](#serviciodto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE

#### Get Featured Services
```http
GET /api/servicios/destacados
```

**Response:** Array of [ServicioDTO](#serviciodto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE

#### Get Popular Services
```http
GET /api/servicios/populares?limit=10
```

**Query Parameters:**
- `limit` (int): Maximum number of results (default: 10)

**Response:** Array of [ServicioDTO](#serviciodto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE

#### Check Service Availability
```http
POST /api/servicios/disponibilidad
```

**Request Body:**
```json
{
  "idServicio": 1,
  "fechaInicio": "2024-01-01T10:00:00",
  "fechaFin": "2024-01-01T12:00:00",
  "cantidadPersonas": 4
}
```

**Response:**
```json
{
  "disponible": true,
  "mensaje": "Service is available",
  "capacidadDisponible": 8,
  "fechaSugerida": null
}
```

**Permissions:** ADMIN, EMPLEADO, CLIENTE

### Reservations Endpoints

#### Get All Reservations
```http
GET /api/reservas?page=0&size=10&sortBy=fechaInicioServicio&sortDir=DESC
```

**Response:** [PaginatedResponse](#paginatedresponse) of [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO

#### Get Reservation by ID
```http
GET /api/reservas/{id}
```

**Response:** [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE (own reservations)

#### Get Reservation by Code
```http
GET /api/reservas/codigo/{codigo}
```

**Response:** [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE (own reservations)

#### Create Reservation
```http
POST /api/reservas
```

**Request Body:** [ReservaDTO](#reservadto)

**Response:** [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE

#### Update Reservation
```http
PUT /api/reservas/{id}
```

**Request Body:** [ReservaDTO](#reservadto)

**Response:** [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO

#### Cancel Reservation
```http
PATCH /api/reservas/{id}/cancelar
```

**Request Body:**
```json
{
  "motivo": "string"
}
```

**Response:** [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE (own reservations)

#### Confirm Reservation
```http
PATCH /api/reservas/{id}/confirmar
```

**Response:** [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO

#### Mark as Paid
```http
PATCH /api/reservas/{id}/pagar
```

**Response:** [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO

#### Search Reservations
```http
GET /api/reservas/buscar?texto={texto}&page=0&size=10
```

**Response:** [PaginatedResponse](#paginatedresponse) of [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO

#### Get Reservations by Customer
```http
GET /api/reservas/cliente/{idCliente}
```

**Response:** Array of [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE (own reservations)

#### Get Upcoming Reservations
```http
GET /api/reservas/proximas?dias=7
```

**Query Parameters:**
- `dias` (int): Days ahead (default: 7)

**Response:** Array of [ReservaDTO](#reservadto)

**Permissions:** ADMIN, EMPLEADO

### Payments Endpoints

#### Get All Payments
```http
GET /api/pagos?page=0&size=10&sortBy=fechaPago&sortDir=DESC
```

**Response:** [PaginatedResponse](#paginatedresponse) of [PagoDTO](#pagodto)

**Permissions:** ADMIN, EMPLEADO

#### Get Payment by ID
```http
GET /api/pagos/{id}
```

**Response:** [PagoDTO](#pagodto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE (own payments)

#### Create Payment
```http
POST /api/pagos
```

**Request Body:** [PagoDTO](#pagodto)

**Response:** [PagoDTO](#pagodto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE

#### Process Payment
```http
POST /api/pagos/{id}/procesar
```

**Request Body:**
```json
{
  "metodoPago": "tarjeta_credito",
  "datosPago": {
    "numeroTarjeta": "4111111111111111",
    "cvv": "123",
    "fechaExpiracion": "12/25",
    "titular": "John Doe"
  }
}
```

**Response:** [PagoDTO](#pagodto)

**Permissions:** ADMIN, EMPLEADO

#### Refund Payment
```http
POST /api/pagos/{id}/reembolsar
```

**Request Body:**
```json
{
  "motivo": "string"
}
```

**Response:** [PagoDTO](#pagodto)

**Permissions:** ADMIN, EMPLEADO

#### Get Payments by Reservation
```http
GET /api/pagos/reserva/{idReserva}
```

**Response:** Array of [PagoDTO](#pagodto)

**Permissions:** ADMIN, EMPLEADO, CLIENTE (own payments)

### Reports Endpoints

#### Generate Income Report
```http
GET /api/reportes/ingresos?fechaInicio=2024-01-01&fechaFin=2024-01-31
```

**Query Parameters:**
- `fechaInicio` (date): Start date
- `fechaFin` (date): End date

**Response:** Income report data

**Permissions:** ADMIN

#### Generate Occupancy Report
```http
GET /api/reportes/ocupacion?fechaInicio=2024-01-01&fechaFin=2024-01-31
```

**Response:** Occupancy report data

**Permissions:** ADMIN

#### Export Report to PDF
```http
GET /api/reportes/{tipo}/pdf?fechaInicio=2024-01-01&fechaFin=2024-01-31
```

**Query Parameters:**
- `tipo` (string): Report type (ingresos/ocupacion/servicios/clientes)
- `fechaInicio` (date): Start date
- `fechaFin` (date): End date

**Response:** PDF file

**Permissions:** ADMIN

## Data Models

### UsuarioDTO

```json
{
  "idUsuario": 1,
  "usuario": "admin",
  "email": "admin@test.com",
  "nombreCompleto": "Administrator",
  "telefono": "123456789",
  "direccion": "123 Main St",
  "idRol": 1,
  "nombreRol": "ADMIN",
  "estado": "activo",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### ServicioDTO

```json
{
  "idServicio": 1,
  "idProveedor": 1,
  "nombreServicio": "City Tour",
  "descripcion": "Guided tour of the historic city",
  "tipoServicio": "tour",
  "precioBase": 50.00,
  "duracionHoras": 2.5,
  "capacidadMaxima": 20,
  "ubicacion": "Cartagena Historic Center",
  "requisitos": "Comfortable walking shoes",
  "incluye": "Guide, entrance fees",
  "noIncluye": "Lunch, tips",
  "imagenes": "image1.jpg,image2.jpg",
  "videoPromocional": "https://youtube.com/watch?v=...",
  "calificacionPromedio": 4.5,
  "totalReservas": 150,
  "disponibleDesde": "2024-01-01T00:00:00",
  "disponibleHasta": "2024-12-31T23:59:59",
  "estado": "activo",
  "etiquetas": "historic,culture,walking",
  "destacado": true
}
```

### ReservaDTO

```json
{
  "idReserva": 1,
  "codigoReserva": "RES-2024-001",
  "idCliente": 1,
  "idServicio": 1,
  "idEmpleado": null,
  "fechaReserva": "2024-01-01T10:00:00",
  "fechaInicioServicio": "2024-01-15T09:00:00",
  "fechaFinServicio": "2024-01-15T11:30:00",
  "cantidadPersonas": 2,
  "precioUnitario": 50.00,
  "precioTotal": 100.00,
  "descuentoAplicado": 0.00,
  "metodoPago": "tarjeta_credito",
  "referenciaPago": "REF-123456",
  "estadoReserva": "confirmada",
  "confirmada": true,
  "fechaConfirmacion": "2024-01-01T10:30:00",
  "cancelada": false,
  "fechaCancelacion": null,
  "motivoCancelacion": null,
  "notas": "Special requirements: wheelchair accessible"
}
```

### PagoDTO

```json
{
  "idPago": 1,
  "idReserva": 1,
  "monto": 100.00,
  "moneda": "USD",
  "metodoPago": "tarjeta_credito",
  "referencia": "REF-123456",
  "estado": "completado",
  "fechaPago": "2024-01-01T10:30:00",
  "fechaConfirmacion": "2024-01-01T10:35:00",
  "fechaReembolso": null,
  "motivoReembolso": null,
  "notas": "Paid with credit card ending in 1234"
}
```

### PaginatedResponse

```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false
}
```

### LoginResponse

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "usuario": {...},
  "mensaje": "Login exitoso"
}
```

## Rate Limiting

The API implements rate limiting to prevent abuse:

- **Authentication endpoints**: 5 requests per minute per IP
- **General endpoints**: 100 requests per minute per authenticated user
- **Report endpoints**: 10 requests per minute per authenticated user

Rate limit headers are included in responses:

```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1640995200
```

## Versioning

The API uses URL versioning. The current version is v1:

```
https://api.hermosacartagena.com/api/v1/...
```

Previous versions are maintained for backward compatibility:

```
https://api.hermosacartagena.com/api/v1/...  (Current)
https://api.hermosacartagena.com/api/v2/...  (Future)
```

## SDK and Client Libraries

Official SDKs are available for:

- JavaScript/TypeScript
- Java
- Python
- PHP

Installation examples:

```bash
# JavaScript
npm install hermosa-cartagena-sdk

# Java
<dependency>
    <groupId>com.hermosacartagena</groupId>
    <artifactId>hermosa-cartagena-sdk</artifactId>
    <version>2.0.0</version>
</dependency>

# Python
pip install hermosa-cartagena-sdk

# PHP
composer require hermosa-cartagena/sdk
```

## Support

For API support and questions:

- **Documentation**: https://docs.hermosacartagena.com/api
- **Status Page**: https://status.hermosacartagena.com
- **Support Email**: api-support@hermosacartagena.com
- **GitHub Issues**: https://github.com/hermosa-cartagena/api/issues

## Changelog

### v2.0.0 (2024-01-01)
- Complete API rewrite with Spring Boot
- JWT authentication implementation
- Microservices architecture
- Enhanced security features
- New reporting endpoints

### v1.0.0 (2023-06-01)
- Initial API release
- Basic CRUD operations
- PHP backend implementation
- Simple authentication

---

*Last updated: January 1, 2024*

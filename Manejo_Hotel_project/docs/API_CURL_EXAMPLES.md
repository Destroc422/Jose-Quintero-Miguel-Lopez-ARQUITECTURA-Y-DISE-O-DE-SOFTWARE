# API Documentation - cURL Examples

## 📋 Introduction

This document provides comprehensive cURL examples for testing and integrating with the Hermosa Cartagena Hotel Management System REST APIs. All examples include authentication, error handling, and response parsing.

---

## 🔐 Authentication Setup

### Get JWT Token
```bash
# Login and get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@hermosacartagena.com",
    "password": "admin123"
  }' \
  | jq -r '.token'
```

### Store Token in Variable
```bash
# Store token for subsequent requests
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@hermosacartagena.com",
    "password": "admin123"
  }' | jq -r '.token')

echo "Token: $TOKEN"
```

---

## 👥 User Management APIs

### Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCompleto": "Juan Pérez",
    "email": "juan.perez@email.com",
    "password": "password123",
    "telefono": "+57 5 6641234",
    "direccion": "Calle 31 #2-45, Bocagrande",
    "tipoDocumento": "CC",
    "numeroDocumento": "123456789"
  }'
```

### Get Current User Profile
```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Update User Profile
```bash
curl -X PUT http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCompleto": "Juan Pérez García",
    "telefono": "+57 5 6641235",
    "direccion": "Carrera 1 #45-67, Centro Histórico"
  }'
```

### Change Password
```bash
curl -X POST http://localhost:8080/api/auth/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "password123",
    "newPassword": "newpassword456"
  }'
```

---

## 🏨 Client Management APIs

### Get All Clients (Paginated)
```bash
curl -X GET "http://localhost:8080/api/clientes?page=0&size=10&sort=nombreCompleto,asc" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Get Client by ID
```bash
curl -X GET http://localhost:8080/api/clientes/1001 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Create New Client
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCompleto": "María García",
    "email": "maria.garcia@email.com",
    "telefono": "+57 5 6641236",
    "direccion": "Avenida San Martín #123-45",
    "tipoDocumento": "CC",
    "numeroDocumento": "987654321"
  }'
```

### Update Client
```bash
curl -X PUT http://localhost:8080/api/clientes/1002 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCompleto": "María García López",
    "telefono": "+57 5 6641237",
    "direccion": "Calle 30 #50-60, Getsemaní"
  }'
```

### Delete Client
```bash
curl -X DELETE http://localhost:8080/api/clientes/1003 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Search Clients
```bash
curl -X GET "http://localhost:8080/api/clientes/search?q=María&tipoDocumento=CC" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

---

## 🛏️ Room Management APIs

### Get All Rooms
```bash
curl -X GET "http://localhost:8080/api/habitaciones?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Get Available Rooms
```bash
curl -X GET "http://localhost:8080/api/habitaciones/disponibles?fechaInicio=2024-06-15&fechaFin=2024-06-18&capacidad=2" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Get Room by ID
```bash
curl -X GET http://localhost:8080/api/habitaciones/205 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Create New Room
```bash
curl -X POST http://localhost:8080/api/habitaciones \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "numero": "301",
    "tipo": "Suite Deluxe",
    "capacidad": 2,
    "precioPorNoche": 280.00,
    "descripcion": "Suite con vista al mar, king size, balcón privado",
    "estado": "disponible"
  }'
```

### Update Room
```bash
curl -X PUT http://localhost:8080/api/habitaciones/205 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "precioPorNoche": 295.00,
    "descripcion": "Suite con vista al mar, king size, balcón privado, renovada recientemente"
  }'
```

### Update Room Status
```bash
curl -X PATCH http://localhost:8080/api/habitaciones/205/estado \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "estado": "mantenimiento"
  }'
```

---

## 📅 Reservation Management APIs

### Get All Reservations
```bash
curl -X GET "http://localhost:8080/api/reservas?page=0&size=10&sort=fechaCheckIn,desc" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Get Reservations by Client
```bash
curl -X GET "http://localhost:8080/api/reservas/cliente/1001?page=0&size=5" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Create New Reservation
```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idCliente": 1001,
    "idHabitacion": 205,
    "fechaCheckIn": "2024-06-15",
    "fechaCheckOut": "2024-06-18",
    "observaciones": "Cliente VIP, preferencia piso alto"
  }'
```

### Get Reservation by ID
```bash
curl -X GET http://localhost:8080/api/reservas/5001 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Update Reservation
```bash
curl -X PUT http://localhost:8080/api/reservas/5001 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "observaciones": "Cliente VIP, preferencia piso alto, necesita cuna extra"
  }'
```

### Cancel Reservation
```bash
curl -X POST http://localhost:8080/api/reservas/5001/cancelar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "motivoCancelacion": "Cancelación por emergencia familiar"
  }'
```

### Confirm Reservation
```bash
curl -X POST http://localhost:8080/api/reservas/5001/confirmar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

---

## 💳 Payment Processing APIs

### Process Payment
```bash
curl -X POST http://localhost:8080/api/pagos/procesar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idReserva": 5001,
    "monto": 840.00,
    "metodoPago": "TARJETA_CREDITO",
    "datosTarjeta": {
      "numero": "4111111111111111",
      "titular": "Juan Pérez",
      "expiracion": "12/25",
      "cvv": "123"
    }
  }'
```

### Get Payment History
```bash
curl -X GET "http://localhost:8080/api/pagos/historico?idCliente=1001&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Get Payment by ID
```bash
curl -X GET http://localhost:8080/api/pagos/7001 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Refund Payment
```bash
curl -X POST http://localhost:8080/api/pagos/7001/reembolsar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Cancelación de reserva con reembolso completo",
    "monto": 840.00
  }'
```

---

## 🛎️ Services Management APIs

### Get All Services
```bash
curl -X GET "http://localhost:8080/api/servicios?page=0&size=20&categoria=RESTAURANTE" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Get Available Services
```bash
curl -X GET "http://localhost:8080/api/servicios/disponibles?fecha=2024-06-15" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Create Service Request
```bash
curl -X POST http://localhost:8080/api/servicios/solicitar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idServicio": 3001,
    "idReserva": 5001,
    "fechaSolicitud": "2024-06-15T14:00:00",
    "cantidad": 2,
    "observaciones": "Preferencia mesa cerca de la ventana"
  }'
```

---

## 📊 Reports and Analytics APIs

### Get Occupancy Report
```bash
curl -X GET "http://localhost:8080/api/reportes/ocupacion?fechaInicio=2024-06-01&fechaFin=2024-06-30" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Get Revenue Report
```bash
curl -X GET "http://localhost:8080/api/reportes/ingresos?fechaInicio=2024-06-01&fechaFin=2024-06-30&agruparPor=DIA" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Get Client Statistics
```bash
curl -X GET "http://localhost:8080/api/reportes/clientes?periodo=ULTIMO_MES" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Export Report to PDF
```bash
curl -X POST http://localhost:8080/api/reportes/exportar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tipoReporte": "INGRESOS",
    "fechaInicio": "2024-06-01",
    "fechaFin": "2024-06-30",
    "formato": "PDF"
  }' \
  --output reporte_ingresos_junio.pdf
```

---

## 🔍 Advanced Search and Filtering

### Complex Search Example
```bash
curl -X GET "http://localhost:8080/api/clientes/search?nombre=Juan&email=@gmail.com&estado=activo&page=0&size=5&sort=nombreCompleto,asc" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Filter by Date Range
```bash
curl -X GET "http://localhost:8080/api/reservas/search?fechaInicio=2024-06-01&fechaFin=2024-06-30&estado=confirmada" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Filter by Multiple Criteria
```bash
curl -X GET "http://localhost:8080/api/habitaciones/search?tipo=SUITE&capacidadMinima=2&precioMaximo=300&estado=disponible" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

---

## 🚨 Error Handling Examples

### Handle Authentication Error
```bash
# Invalid token example
curl -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer invalid_token" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n"
```

### Handle Validation Error
```bash
# Invalid data example
curl -X POST http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreCompleto": "",
    "email": "invalid-email",
    "password": "123"
  }' \
  -w "\nHTTP Status: %{http_code}\n"
```

### Handle Not Found Error
```bash
# Non-existent resource
curl -X GET http://localhost:8080/api/clientes/99999 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n"
```

---

## 📝 File Upload Examples

### Upload Client Photo
```bash
curl -X POST http://localhost:8080/api/clientes/1001/foto \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/client_photo.jpg" \
  -F "descripcion=Foto de perfil del cliente"
```

### Upload Room Images
```bash
curl -X POST http://localhost:8080/api/habitaciones/205/imagenes \
  -H "Authorization: Bearer $TOKEN" \
  -F "files=@/path/to/room1.jpg" \
  -F "files=@/path/to/room2.jpg" \
  -F "files=@/path/to/room3.jpg"
```

---

## 🔧 Utility Scripts

### Batch Operations Script
```bash
#!/bin/bash
# batch_operations.sh

# Get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@hermosacartagena.com","password":"admin123"}' | jq -r '.token')

# Create multiple clients
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/clientes \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"nombreCompleto\": \"Cliente Test $i\",
      \"email\": \"cliente$i@test.com\",
      \"telefono\": \"+57 5 664123$i\",
      \"direccion\": \"Dirección Test $i\",
      \"tipoDocumento\": \"CC\",
      \"numeroDocumento\": \"12345678$i\"
    }"
  echo "Created client $i"
done
```

### Health Check Script
```bash
#!/bin/bash
# health_check.sh

# Check API health
HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/health)

if [ $HEALTH -eq 200 ]; then
  echo "✅ API is healthy"
else
  echo "❌ API is down (HTTP $HEALTH)"
  exit 1
fi

# Check database connectivity
DB_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/health/database)

if [ $DB_HEALTH -eq 200 ]; then
  echo "✅ Database is connected"
else
  echo "❌ Database connection failed (HTTP $DB_HEALTH)"
  exit 1
fi
```

---

## 📊 Performance Testing

### Load Testing Script
```bash
#!/bin/bash
# load_test.sh

CONCURRENT_USERS=10
REQUESTS_PER_USER=100
BASE_URL="http://localhost:8080"

# Get token once
TOKEN=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@hermosacartagena.com","password":"admin123"}' | jq -r '.token')

echo "Starting load test with $CONCURRENT_USERS concurrent users..."

# Run concurrent requests
for i in $(seq 1 $CONCURRENT_USERS); do
  (
    for j in $(seq 1 $REQUESTS_PER_USER); do
      curl -s -o /dev/null -w "%{http_code}" \
        -H "Authorization: Bearer $TOKEN" \
        $BASE_URL/api/clientes &
      sleep 0.1
    done
  ) &
done

wait
echo "Load test completed"
```

---

## 🔍 Debugging Tips

### Verbose Output
```bash
# Enable verbose curl output for debugging
curl -v -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Save Response to File
```bash
# Save response for inspection
curl -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -o response.json
```

### Pretty Print JSON Response
```bash
# Pretty print JSON response
curl -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'
```

### Measure Response Time
```bash
# Measure response time
curl -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -w "\nTime: %{time_total}s\nSize: %{size_download} bytes\n"
```

---

## 📚 Common Response Formats

### Success Response
```json
{
  "success": true,
  "data": {
    "id": 1001,
    "nombreCompleto": "Juan Pérez",
    "email": "juan.perez@email.com"
  },
  "message": "Operación exitosa",
  "timestamp": "2024-06-12T10:30:00Z"
}
```

### Error Response
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Datos de entrada inválidos",
    "details": [
      {
        "field": "email",
        "message": "El email es requerido"
      }
    ]
  },
  "timestamp": "2024-06-12T10:30:00Z"
}
```

### Paginated Response
```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 10,
    "totalElements": 150,
    "totalPages": 15,
    "first": true,
    "last": false
  }
}
```

---

## 🎯 Best Practices

1. **Always include authentication headers** for protected endpoints
2. **Use appropriate HTTP methods** (GET, POST, PUT, DELETE, PATCH)
3. **Handle HTTP status codes** properly (200, 201, 400, 401, 404, 500)
4. **Validate input data** before sending requests
5. **Use JSON format** for request/response bodies
6. **Implement retry logic** for network failures
7. **Log requests and responses** for debugging
8. **Use environment variables** for sensitive data
9. **Rate limit requests** to avoid overwhelming the server
10. **Test error scenarios** to ensure robustness

---

## 📞 Support

For API support and questions:
- **Email**: api-support@hermosacartagena.com
- **Documentation**: https://docs.hermosacartagena.com/api
- **Status Page**: https://status.hermosacartagena.com
- **GitHub Issues**: https://github.com/hermosacartagena/api/issues

**Last Updated**: June 12, 2024  
**API Version**: v1.0.0  
**Base URL**: http://localhost:8080/api

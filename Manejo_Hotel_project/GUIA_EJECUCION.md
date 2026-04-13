# GUÍA DE EJECUCIÓN - SISTEMA "HERMOSA CARTAGENA" V2.0

## REQUISITOS PREVIOS

### Software Necesario
- **Java 17+** (OpenJDK o Oracle JDK)
- **Maven 3.9+**
- **MySQL 8.0+**
- **Node.js 18+** (opcional, para frontend)
- **Git**

### Hardware Recomendado
- **RAM**: 8GB mínimo, 16GB recomendado
- **CPU**: 4 cores mínimo, 8 cores recomendado
- **Disco**: 10GB espacio libre

---

## PASO 1: CONFIGURACIÓN DE LA BASE DE DATOS

### 1.1 Instalar y Configurar MySQL
```bash
# Windows (usando MySQL Installer)
# Descargar desde: https://dev.mysql.com/downloads/installer/

# Linux (Ubuntu/Debian)
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation

# macOS (usando Homebrew)
brew install mysql
brew services start mysql
```

### 1.2 Crear Base de Datos
```sql
-- Conectar a MySQL
mysql -u root -p

-- Crear base de datos
CREATE DATABASE hermosa_cartagena CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario para la aplicación
CREATE USER 'hermosa_user'@'localhost' IDENTIFIED BY 'tu_password_seguro';
GRANT ALL PRIVILEGES ON hermosa_cartagena.* TO 'hermosa_user'@'localhost';
FLUSH PRIVILEGES;

-- Salir
EXIT;
```

### 1.3 Ejecutar Scripts SQL
```bash
# Navegar al directorio del proyecto
cd Manejo_Hotel_project/database

# Ejecutar scripts en orden correcto
mysql -u hermosa_user -p hermosa_cartagena < 01_create_database.sql
mysql -u hermosa_user -p hermosa_cartagena < 02_insert_datos_prueba.sql

# Verificar que todo esté creado
mysql -u hermosa_user -p hermosa_cartagena -e "SHOW TABLES;"
```

---

## PASO 2: COMPILAR EL BACKEND SPRING BOOT

### 2.1 Navegar al Directorio del Backend
```bash
cd Manejo_Hotel_project/backend-java
```

### 2.2 Configurar Archivo application.yml
Editar `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hermosa_cartagena
    username: hermosa_user
    password: tu_password_seguro
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: validate  # Usar 'update' para desarrollo
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        
hermosa-cartagena:
  security:
    jwt:
      secret: tu_secreto_jwt_muy_largo_y_seguro_aqui
      expiration: 3600  # 1 hora
      refresh-expiration: 2592000  # 30 días
```

### 2.3 Compilar y Ejecutar Tests
```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar todos los tests
mvn test

# Generar reporte de cobertura
mvn jacoco:report

# Verificar que todo compile correctamente
mvn clean package -DskipTests
```

---

## PASO 3: EJECUTAR EL SISTEMA

### OPCIÓN A: EJECUCIÓN MONOLÍTICA (Recomendado para desarrollo)

### 3.1 Iniciar la Aplicación Principal
```bash
# Desde el directorio backend-java
mvn spring-boot:run

# O ejecutar el JAR compilado
java -jar target/hermosa-cartagena-backend-2.0.0.jar
```

### 3.2 Verificar que la API esté funcionando
```bash
# En otra terminal, probar la API
curl http://localhost:8080/api/actuator/health

# Debería responder: {"status":"UP"}
```

### OPCIÓN B: EJECUCIÓN CON MICROSERVICIOS (Avanzado)

### 3.1 Iniciar Eureka Discovery Server
```bash
# Crear un nuevo módulo para Eureka o usar configuración existente
# Desde el directorio del proyecto
mvn spring-boot:run -pl discovery-server

# Verificar en: http://localhost:8761
```

### 3.2 Iniciar API Gateway
```bash
mvn spring-boot:run -pl gateway-server

# Verificar en: http://localhost:8080
```

### 3.3 Iniciar Microservicios (en terminales separadas)
```bash
# Terminal 1: Auth Service
mvn spring-boot:run -pl auth-service

# Terminal 2: Usuarios Service
mvn spring-boot:run -pl usuarios-service

# Terminal 3: Servicios Service
mvn spring-boot:run -pl servicios-service

# Terminal 4: Reservas Service
mvn spring-boot:run -pl reservas-service

# Terminal 5: Pagos Service
mvn spring-boot:run -pl pagos-service

# Terminal 6: Reportes Service
mvn spring-boot:run -pl reportes-service
```

---

## PASO 4: CONFIGURAR EL FRONTEND

### 4.1 Usar Servidor Web Simple
```bash
# Navegar al directorio frontend
cd Manejo_Hotel_project/frontend

# Opción 1: Usar Python
python -m http.server 3000

# Opción 2: Usar Node.js con live-server
npx live-server --port=3000

# Opción 3: Usar PHP (si está instalado)
php -S localhost:3000
```

### 4.2 Configurar el Frontend
Editar `frontend/js/api.js` para apuntar al backend correcto:
```javascript
const API_CONFIG = {
    baseURL: 'http://localhost:8080/api',  // Para monolítico
    // baseURL: 'http://localhost:8080/api',  // Para microservicios
    timeout: 30000,
    retries: 3,
    retryDelay: 1000
};
```

---

## PASO 5: ACCEDER AL SISTEMA

### 5.1 URLs de Acceso
- **Frontend**: http://localhost:3000
- **API Backend**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/api/actuator/health

### 5.2 Credenciales de Prueba
```json
{
  "admin": {
    "usuario": "admin",
    "password": "admin123"
  },
  "empleado": {
    "usuario": "empleado1",
    "password": "emp123"
  },
  "cliente": {
    "usuario": "cliente1",
    "password": "cli123"
  }
}
```

### 5.3 Primer Login
1. Abrir http://localhost:3000 en el navegador
2. Iniciar sesión con: `admin` / `admin123`
3. Verificar el dashboard de administrador
4. Explorar las diferentes funcionalidades

---

## PASO 6: VERIFICACIÓN Y TESTING

### 6.1 Verificar Endpoints Principales
```bash
# Health check
curl http://localhost:8080/api/actuator/health

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usuario":"admin","password":"admin123"}'

# Obtener usuarios (requiere token)
curl -H "Authorization: Bearer TU_TOKEN_JWT" \
  http://localhost:8080/api/usuarios
```

### 6.2 Verificar Base de Datos
```sql
mysql -u hermosa_user -p hermosa_cartagena

-- Verificar tablas creadas
SHOW TABLES;

-- Verificar datos de prueba
SELECT COUNT(*) FROM usuarios;
SELECT COUNT(*) FROM servicios;
SELECT COUNT(*) FROM reservas;
```

### 6.3 Verificar Logs
```bash
# Los logs de Spring Boot aparecen en la consola
# También se guardan en logs/application.log

# Verificar errores
tail -f logs/application.log | grep ERROR
```

---

## TROUBLESHOOTING COMÚN

### Problema: "Connection refused" en MySQL
**Solución:**
```bash
# Verificar que MySQL esté corriendo
sudo systemctl status mysql  # Linux
brew services list | grep mysql  # macOS

# Reiniciar MySQL si es necesario
sudo systemctl restart mysql
brew services restart mysql
```

### Problema: "Java version not supported"
**Solución:**
```bash
# Verificar versión de Java
java -version

# Instalar Java 17 si no está disponible
# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# macOS
brew install openjdk@17

# Windows
# Descargar desde https://adoptium.net/
```

### Problema: "Maven compilation failed"
**Solución:**
```bash
# Limpiar y recompilar
mvn clean
mvn compile

# Verificar dependencias
mvn dependency:tree

# Si hay problemas de dependencias, forzar actualización
mvn clean compile -U
```

### Problema: "Port already in use"
**Solución:**
```bash
# Encontrar proceso usando el puerto
netstat -tulpn | grep :8080  # Linux
lsof -i :8080  # macOS

# Matar el proceso
kill -9 PID_DEL_PROCESO

# O cambiar el puerto en application.yml
server:
  port: 8081
```

### Problema: "JWT token invalid"
**Solución:**
```bash
# Verificar configuración del secreto JWT
# Asegurarse que el secreto sea el mismo en frontend y backend

# Generar nuevo token si es necesario
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usuario":"admin","password":"admin123"}'
```

---

## CONFIGURACIÓN AVANZADA

### Docker (Opcional)
```dockerfile
# Dockerfile para el backend
FROM openjdk:17-jre-slim
COPY target/hermosa-cartagena-backend-2.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Construir imagen
docker build -t hermosa-cartagena:2.0.0 .

# Ejecutar contenedor
docker run -p 8080:8080 hermosa-cartagena:2.0.0
```

### Kubernetes (Opcional)
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: hermosa-cartagena
spec:
  replicas: 3
  selector:
    matchLabels:
      app: hermosa-cartagena
  template:
    metadata:
      labels:
        app: hermosa-cartagena
    spec:
      containers:
      - name: hermosa-cartagena
        image: hermosa-cartagena:2.0.0
        ports:
        - containerPort: 8080
```

---

## MONITOREO Y MANTENIMIENTO

### Logs y Métricas
- **Logs**: `logs/application.log`
- **Health**: `/api/actuator/health`
- **Metrics**: `/api/actuator/metrics`
- **Info**: `/api/actuator/info`

### Backup de Base de Datos
```bash
# Backup completo
mysqldump -u hermosa_user -p hermosa_cartagena > backup_$(date +%Y%m%d).sql

# Restaurar
mysql -u hermosa_user -p hermosa_cartagena < backup_20240101.sql
```

---

## PRÓXIMOS PASOS DESPUÉS DE LA EJECUCIÓN

1. **Explorar el sistema**: Probar todas las funcionalidades
2. **Revisar la documentación**: `docs/API_DOCUMENTATION.md`
3. **Ejecutar tests**: `mvn test`
4. **Personalizar configuración**: Ajustar a tu entorno
5. **Desarrollar nuevas features**: Usar la arquitectura existente

---

## SOPORTE

Si encuentras algún problema:

1. **Revisar los logs** del sistema
2. **Verificar la configuración** de base de datos
3. **Consultar la documentación** técnica
4. **Revisar el troubleshooting** común
5. **Contactar soporte** si el problema persiste

---

**¡Listo! El sistema "Hermosa Cartagena" V2.0 debería estar funcionando correctamente.**

*Última actualización: 13 de abril de 2026*

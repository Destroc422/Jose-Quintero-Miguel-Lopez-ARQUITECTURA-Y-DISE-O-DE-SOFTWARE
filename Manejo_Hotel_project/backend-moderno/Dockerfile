# =============================================
# SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
# DOCKERFILE - ARQUITECTURA MODERNA UNIFICADA
# =============================================

# Etapa 1: Build con Maven
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar archivos de configuración
COPY pom.xml .
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime con OpenJDK
FROM openjdk:17-jdk-slim

# Instalar utilidades necesarias
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN groupadd -r hermosa && useradd -r -g hermosa hermosa

# Establecer directorio de trabajo
WORKDIR /app

# Copiar JAR desde la etapa de build
COPY --from=builder /app/target/hermosa-cartagena-moderno-*.jar app.jar

# Crear directorios necesarios
RUN mkdir -p /app/logs /app/temp && \
    chown -R hermosa:hermosa /app

# Cambiar a usuario no-root
USER hermosa

# Exponer puertos
EXPOSE 8080 8081

# Variables de entorno
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV MANAGEMENT_SERVER_PORT=8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels para metadata
LABEL maintainer="Sistema Hermosa Cartagena" \
      version="3.0.0" \
      description="Sistema de Gestión Turística Moderno" \
      architecture="spring-boot-microservice" \
      vendor="Hermosa Cartagena Tourism"

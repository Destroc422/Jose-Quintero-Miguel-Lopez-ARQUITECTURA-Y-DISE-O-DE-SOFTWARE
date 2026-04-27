<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>
    
    <!-- Plantilla principal -->
    <xsl:template match="/protocol">
        <html>
            <head>
                <title>Respuesta del Sistema Hotelero</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { background: #667eea; color: white; padding: 15px; border-radius: 5px; }
                    .success { background: #d4edda; border: 1px solid #c3e6cb; padding: 10px; border-radius: 5px; margin: 10px 0; }
                    .error { background: #f8d7da; border: 1px solid #f5c6cb; padding: 10px; border-radius: 5px; margin: 10px 0; }
                    .info { background: #d1ecf1; border: 1px solid #bee5eb; padding: 10px; border-radius: 5px; margin: 10px 0; }
                    .data-section { margin: 15px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; }
                    .user-info { background: #f8f9fa; padding: 10px; border-radius: 5px; }
                    .hotel-card { border: 1px solid #ddd; margin: 10px 0; padding: 15px; border-radius: 5px; }
                    .room-item { background: #fff; padding: 10px; margin: 5px 0; border-left: 3px solid #667eea; }
                    .booking-item { background: #e9ecef; padding: 10px; margin: 5px 0; border-radius: 3px; }
                    table { width: 100%; border-collapse: collapse; margin: 10px 0; }
                    th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }
                    th { background: #f8f9fa; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Sistema Hotelero - Respuesta XML</h1>
                    <p>Timestamp: <xsl:value-of select="@timestamp"/></p>
                </div>
                
                <!-- Procesar respuesta -->
                <xsl:apply-templates select="response"/>
                
                <!-- Procesar error -->
                <xsl:apply-templates select="error"/>
            </body>
        </html>
    </xsl:template>
    
    <!-- Plantilla para respuestas -->
    <xsl:template match="response">
        <div class="info">
            <h3>Información de la Respuesta</h3>
            <p><strong>ID de Solicitud:</strong> <xsl:value-of select="@request_id"/></p>
            <p><strong>ID del Servidor:</strong> <xsl:value-of select="@server_id"/></p>
            <xsl:if test="@processing_time">
                <p><strong>Tiempo de Procesamiento:</strong> <xsl:value-of select="@processing_time"/>s</p>
            </xsl:if>
            <p><strong>Estado:</strong> <xsl:value-of select="status"/></p>
        </div>
        
        <!-- Mostrar mensaje de éxito o error -->
        <xsl:choose>
            <xsl:when test="status &lt; 300">
                <div class="success">
                    <h3>✅ Operación Exitosa</h3>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <div class="error">
                    <h3>❌ Error en la Operación</h3>
                </div>
            </xsl:otherwise>
        </xsl:choose>
        
        <!-- Procesar datos -->
        <xsl:apply-templates select="data"/>
    </xsl:template>
    
    <!-- Plantilla para errores -->
    <xsl:template match="error">
        <div class="error">
            <h3>❌ Error del Servidor</h3>
            <p><strong>Código:</strong> <xsl:value-of select="code"/></p>
            <p><strong>Mensaje:</strong> <xsl:value-of select="message"/></p>
            <xsl:if test="details">
                <h4>Detalles:</h4>
                <ul>
                    <xsl:for-each select="details/detail">
                        <li>
                            <xsl:if test="@field">
                                <strong><xsl:value-of select="@field"/>:</strong>
                            </xsl:if>
                            <xsl:value-of select="."/>
                        </li>
                    </xsl:for-each>
                </ul>
            </xsl:if>
        </div>
    </xsl:template>
    
    <!-- Plantilla para datos -->
    <xsl:template match="data">
        <div class="data-section">
            <h3>Datos de Respuesta</h3>
            
            <!-- Procesar usuario -->
            <xsl:apply-templates select="user"/>
            
            <!-- Procesar mensaje -->
            <xsl:apply-templates select="message"/>
            
            <!-- Procesar hoteles -->
            <xsl:apply-templates select="hotels"/>
            
            <!-- Procesar habitaciones -->
            <xsl:apply-templates select="rooms"/>
            
            <!-- Procesar reservas -->
            <xsl:apply-templates select="bookings"/>
        </div>
    </xsl:template>
    
    <!-- Plantilla para usuario -->
    <xsl:template match="user">
        <div class="user-info">
            <h4>👤 Información del Usuario</h4>
            <table>
                <tr><td><strong>ID:</strong></td><td><xsl:value-of select="id"/></td></tr>
                <tr><td><strong>Usuario:</strong></td><td><xsl:value-of select="username"/></td></tr>
                <tr><td><strong>Email:</strong></td><td><xsl:value-of select="email"/></td></tr>
                <tr><td><strong>Nombre:</strong></td><td><xsl:value-of select="name"/></td></tr>
                <tr><td><strong>Rol:</strong></td><td><xsl:value-of select="role"/></td></tr>
                <tr><td><strong>Creado:</strong></td><td><xsl:value-of select="created_at"/></td></tr>
            </table>
        </div>
    </xsl:template>
    
    <!-- Plantilla para mensaje -->
    <xsl:template match="message">
        <div class="info">
            <h4>💬 Mensaje</h4>
            <p><xsl:value-of select="."/></p>
        </div>
    </xsl:template>
    
    <!-- Plantilla para hoteles -->
    <xsl:template match="hotels">
        <h4>🏨 Lista de Hoteles</h4>
        <xsl:for-each select="hotel">
            <div class="hotel-card">
                <h5><xsl:value-of select="name"/></h5>
                <p><strong>ID:</strong> <xsl:value-of select="id"/></p>
                <p><strong>Descripción:</strong> <xsl:value-of select="description"/></p>
                <p><strong>Ubicación:</strong> <xsl:value-of select="location"/></p>
                <p><strong>Calificación:</strong> ⭐ <xsl:value-of select="rating"/></p>
                
                <!-- Mostrar habitaciones del hotel -->
                <xsl:if test="rooms">
                    <h6>Habitaciones Disponibles:</h6>
                    <xsl:apply-templates select="rooms"/>
                </xsl:if>
            </div>
        </xsl:for-each>
    </xsl:template>
    
    <!-- Plantilla para habitaciones -->
    <xsl:template match="rooms">
        <div class="rooms-section">
            <xsl:for-each select="room">
                <div class="room-item">
                    <strong>Habitación <xsl:value-of select="number"/></strong> - <xsl:value-of select="type"/>
                    <br/>
                    <small>
                        Capacidad: <xsl:value-of select="capacity"/> personas | 
                        Precio: $<xsl:value-of select="price_per_night"/>/noche | 
                        Estado: <xsl:choose>
                            <xsl:when test="available = 'true'">✅ Disponible</xsl:when>
                            <xsl:otherwise>❌ Ocupada</xsl:otherwise>
                        </xsl:choose>
                    </small>
                </div>
            </xsl:for-each>
        </div>
    </xsl:template>
    
    <!-- Plantilla para reservas -->
    <xsl:template match="bookings">
        <h4>📅 Reservas</h4>
        <xsl:for-each select="booking">
            <div class="booking-item">
                <strong>Reserva <xsl:value-of select="id"/></strong>
                <br/>
                <small>
                    Check-in: <xsl:value-of select="check_in"/> | 
                    Check-out: <xsl:value-of select="check_out"/> | 
                    Total: $<xsl:value-of select="total_price"/> | 
                    Estado: <xsl:value-of select="status"/>
                </small>
            </div>
        </xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>

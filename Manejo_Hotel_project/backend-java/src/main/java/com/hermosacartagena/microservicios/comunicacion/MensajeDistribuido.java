package com.hermosacartagena.microservicios.comunicacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * MENSAJE DISTRIBUIDO
 * =============================================
 * 
 * Clase base para comunicación entre microservicios.
 * Implementa un protocolo de mensajería estandarizado.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDistribuido implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único del mensaje
     */
    private String idMensaje;

    /**
     * Tipo de mensaje (REQUEST, RESPONSE, EVENT, ERROR)
     */
    private String tipoMensaje;

    /**
     * Nombre de la operación o acción
     */
    private String operacion;

    /**
     * Microservicio origen
     */
    private String servicioOrigen;

    /**
     * Microservicio destino
     */
    private String servicioDestino;

    /**
     * Datos del payload (serializado)
     */
    private Map<String, Object> payload;

    /**
     * Headers adicionales
     */
    private Map<String, String> headers;

    /**
     * Timestamp de creación
     */
    private LocalDateTime timestamp;

    /**
     * Timestamp de procesamiento
     */
    private LocalDateTime timestampProcesamiento;

    /**
     * Estado del mensaje (PENDIENTE, PROCESANDO, COMPLETADO, ERROR)
     */
    private String estado;

    /**
     * Código de error si aplica
     */
    private String codigoError;

    /**
     * Mensaje de error si aplica
     */
    private String mensajeError;

    /**
     * Correlación ID para seguimiento de operaciones
     */
    private String correlacionId;

    /**
     * Número de intentos de procesamiento
     */
    private Integer intentos;

    /**
     * Timeout en milisegundos
     */
    private Long timeout;

    /**
     * Prioridad del mensaje (LOW, NORMAL, HIGH, URGENT)
     */
    private String prioridad;

    /**
     * Constructor básico para REQUEST
     */
    public MensajeDistribuido(String operacion, String servicioOrigen, String servicioDestino, 
                              Map<String, Object> payload) {
        this.idMensaje = UUID.randomUUID().toString();
        this.tipoMensaje = "REQUEST";
        this.operacion = operacion;
        this.servicioOrigen = servicioOrigen;
        this.servicioDestino = servicioDestino;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.intentos = 0;
        this.prioridad = "NORMAL";
        this.correlacionId = UUID.randomUUID().toString();
    }

    /**
     * Constructor para RESPONSE
     */
    public MensajeDistribuido(MensajeDistribuido request, Map<String, Object> payload) {
        this.idMensaje = UUID.randomUUID().toString();
        this.tipoMensaje = "RESPONSE";
        this.operacion = request.getOperacion();
        this.servicioOrigen = request.getServicioDestino();
        this.servicioDestino = request.getServicioOrigen();
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
        this.estado = "COMPLETADO";
        this.correlacionId = request.getCorrelacionId();
        this.prioridad = request.getPrioridad();
    }

    /**
     * Constructor para ERROR
     */
    public MensajeDistribuido(MensajeDistribuido request, String codigoError, String mensajeError) {
        this.idMensaje = UUID.randomUUID().toString();
        this.tipoMensaje = "ERROR";
        this.operacion = request.getOperacion();
        this.servicioOrigen = request.getServicioDestino();
        this.servicioDestino = request.getServicioOrigen();
        this.codigoError = codigoError;
        this.mensajeError = mensajeError;
        this.timestamp = LocalDateTime.now();
        this.estado = "ERROR";
        this.correlacionId = request.getCorrelacionId();
        this.prioridad = request.getPrioridad();
    }

    /**
     * Constructor para EVENT
     */
    public MensajeDistribuido(String evento, String servicioOrigen, Map<String, Object> eventData) {
        this.idMensaje = UUID.randomUUID().toString();
        this.tipoMensaje = "EVENT";
        this.operacion = evento;
        this.servicioOrigen = servicioOrigen;
        this.servicioDestino = "BROADCAST";
        this.payload = eventData;
        this.timestamp = LocalDateTime.now();
        this.estado = "COMPLETADO";
        this.prioridad = "NORMAL";
        this.correlacionId = UUID.randomUUID().toString();
    }

    // Métodos de utilidad

    /**
     * Verifica si el mensaje es de tipo REQUEST
     */
    public boolean esRequest() {
        return "REQUEST".equals(tipoMensaje);
    }

    /**
     * Verifica si el mensaje es de tipo RESPONSE
     */
    public boolean esResponse() {
        return "RESPONSE".equals(tipoMensaje);
    }

    /**
     * Verifica si el mensaje es de tipo EVENT
     */
    public boolean esEvent() {
        return "EVENT".equals(tipoMensaje);
    }

    /**
     * Verifica si el mensaje es de tipo ERROR
     */
    public boolean esError() {
        return "ERROR".equals(tipoMensaje);
    }

    /**
     * Verifica si el mensaje está completado
     */
    public boolean estaCompletado() {
        return "COMPLETADO".equals(estado);
    }

    /**
     * Verifica si el mensaje tiene error
     */
    public boolean tieneError() {
        return "ERROR".equals(estado);
    }

    /**
     * Verifica si el mensaje está pendiente
     */
    public boolean estaPendiente() {
        return "PENDIENTE".equals(estado);
    }

    /**
     * Verifica si el mensaje está procesando
     */
    public boolean estaProcesando() {
        return "PROCESANDO".equals(estado);
    }

    /**
     * Marca como procesando
     */
    public void marcarComoProcesando() {
        this.estado = "PROCESANDO";
        this.timestampProcesamiento = LocalDateTime.now();
    }

    /**
     * Marca como completado
     */
    public void marcarComoCompletado() {
        this.estado = "COMPLETADO";
        if (this.timestampProcesamiento == null) {
            this.timestampProcesamiento = LocalDateTime.now();
        }
    }

    /**
     * Marca como error
     */
    public void marcarComoError(String codigoError, String mensajeError) {
        this.estado = "ERROR";
        this.codigoError = codigoError;
        this.mensajeError = mensajeError;
        if (this.timestampProcesamiento == null) {
            this.timestampProcesamiento = LocalDateTime.now();
        }
    }

    /**
     * Incrementa el contador de intentos
     */
    public void incrementarIntentos() {
        this.intentos = (this.intentos == null) ? 1 : this.intentos + 1;
    }

    /**
     * Obtiene el tiempo de procesamiento en milisegundos
     */
    public Long getTiempoProcesamiento() {
        if (timestampProcesamiento == null || timestamp == null) {
            return null;
        }
        return java.time.Duration.between(timestamp, timestampProcesamiento).toMillis();
    }

    /**
     * Verifica si el mensaje ha expirado
     */
    public boolean haExpirado() {
        if (timeout == null) {
            return false;
        }
        
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime expiracion = timestamp.plusNanos(timeout * 1_000_000);
        
        return ahora.isAfter(expiracion);
    }

    /**
     * Obtiene un valor del payload
     */
    @SuppressWarnings("unchecked")
    public <T> T getPayloadValue(String key, Class<T> type) {
        if (payload == null || !payload.containsKey(key)) {
            return null;
        }
        
        Object value = payload.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        
        return null;
    }

    /**
     * Agrega un valor al payload
     */
    public void addPayloadValue(String key, Object value) {
        if (this.payload == null) {
            this.payload = new java.util.HashMap<>();
        }
        this.payload.put(key, value);
    }

    /**
     * Obtiene un header
     */
    public String getHeader(String key) {
        if (headers == null) {
            return null;
        }
        return headers.get(key);
    }

    /**
     * Agrega un header
     */
    public void addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new java.util.HashMap<>();
        }
        this.headers.put(key, value);
    }

    /**
     * Crea una respuesta para este mensaje
     */
    public MensajeDistribuido crearRespuesta(Map<String, Object> payload) {
        return new MensajeDistribuido(this, payload);
    }

    /**
     * Crea una respuesta de error para este mensaje
     */
    public MensajeDistribuido crearRespuestaError(String codigoError, String mensajeError) {
        return new MensajeDistribuido(this, codigoError, mensajeError);
    }

    /**
     * Representación en texto para logging
     */
    @Override
    public String toString() {
        return String.format(
            "Mensaje[id=%s, tipo=%s, operacion=%s, origen=%s, destino=%s, estado=%s, correlacion=%s]",
            idMensaje, tipoMensaje, operacion, servicioOrigen, servicioDestino, estado, correlacionId
        );
    }

    /**
     * Enum para tipos de mensaje
     */
    public enum TipoMensaje {
        REQUEST, RESPONSE, EVENT, ERROR
    }

    /**
     * Enum para estados
     */
    public enum EstadoMensaje {
        PENDIENTE, PROCESANDO, COMPLETADO, ERROR
    }

    /**
     * Enum para prioridades
     */
    public enum Prioridad {
        LOW(1), NORMAL(2), HIGH(3), URGENT(4);

        private final int nivel;

        Prioridad(int nivel) {
            this.nivel = nivel;
        }

        public int getNivel() {
            return nivel;
        }
    }
}

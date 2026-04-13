package com.hermosacartagena.microservicios.comunicacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CLIENTE DE COMUNICACIÓN DISTRIBUIDA
 * =============================================
 * 
 * Cliente para comunicación entre microservicios.
 * Implementa envío síncrono y asíncrono de mensajes.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteComunicacion {

    private final RestTemplate restTemplate;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Value("${hermosa-cartagena.microservicios.timeout:30000}")
    private long timeoutPorDefecto;

    @Value("${hermosa-cartagena.microservicios.nombre:servicio}")
    private String nombreServicio;

    @Value("${hermosa-cartagena.microservicios.reintentos:3}")
    private int maxReintentos;

    /**
     * Envía un mensaje síncrono a otro microservicio
     * 
     * @param mensaje Mensaje a enviar
     * @return Respuesta del microservicio
     * @throws ComunicacionException Si hay error en la comunicación
     */
    public MensajeDistribuido enviarMensaje(MensajeDistribuido mensaje) throws ComunicacionException {
        return enviarMensaje(mensaje, timeoutPorDefecto);
    }

    /**
     * Envía un mensaje síncrono con timeout específico
     * 
     * @param mensaje Mensaje a enviar
     * @param timeout Timeout en milisegundos
     * @return Respuesta del microservicio
     * @throws ComunicacionException Si hay error en la comunicación
     */
    public MensajeDistribuido enviarMensaje(MensajeDistribuido mensaje, long timeout) 
            throws ComunicacionException {
        
        try {
            log.debug("Enviando mensaje: {} -> {}", mensaje.getServicioOrigen(), mensaje.getServicioDestino());
            
            // Validar mensaje
            validarMensaje(mensaje);
            
            // Configurar timeout
            mensaje.setTimeout(timeout);
            
            // Construir URL del microservicio destino
            String url = construirUrlDestino(mensaje);
            
            // Preparar headers HTTP
            HttpHeaders headers = prepararHeaders(mensaje);
            
            // Crear entidad HTTP
            HttpEntity<MensajeDistribuido> entity = new HttpEntity<>(mensaje, headers);
            
            // Enviar mensaje con reintentos
            MensajeDistribuido respuesta = enviarConReintentos(url, entity, mensaje);
            
            log.debug("Respuesta recibida: {}", respuesta.getIdMensaje());
            
            return respuesta;
            
        } catch (Exception e) {
            log.error("Error enviando mensaje: {}", e.getMessage(), e);
            throw new ComunicacionException("Error en comunicación: " + e.getMessage(), e);
        }
    }

    /**
     * Envía un mensaje asíncrono
     * 
     * @param mensaje Mensaje a enviar
     * @return CompletableFuture con la respuesta
     */
    public CompletableFuture<MensajeDistribuido> enviarMensajeAsincrono(MensajeDistribuido mensaje) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return enviarMensaje(mensaje);
            } catch (ComunicacionException e) {
                log.error("Error en envío asíncrono: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    /**
     * Envía un mensaje asíncrono con callback
     * 
     * @param mensaje Mensaje a enviar
     * @param callback Callback para manejar respuesta
     */
    public void enviarMensajeAsincrono(MensajeDistribuido mensaje, 
                                      java.util.function.Consumer<MensajeDistribuido> callback) {
        enviarMensajeAsincrono(mensaje)
            .thenAccept(callback)
            .exceptionally(throwable -> {
                log.error("Error en callback asíncrono: {}", throwable.getMessage());
                return null;
            });
    }

    /**
     * Publica un evento (broadcast)
     * 
     * @param evento Nombre del evento
     * @param datos Datos del evento
     * @return MensajeDistribuido del evento
     */
    public MensajeDistribuido publicarEvento(String evento, Map<String, Object> datos) {
        MensajeDistribuido mensajeEvento = new MensajeDistribuido(evento, nombreServicio, datos);
        
        try {
            // Enviar a todos los microservicios suscritos
            enviarEventoAMicroservicios(mensajeEvento);
            
            log.info("Evento publicado: {}", evento);
            return mensajeEvento;
            
        } catch (Exception e) {
            log.error("Error publicando evento: {}", e.getMessage());
            throw new ComunicacionException("Error publicando evento: " + e.getMessage(), e);
        }
    }

    /**
     * Envía mensaje con reintentos
     */
    private MensajeDistribuido enviarConReintentos(String url, HttpEntity<MensajeDistribuido> entity, 
                                                  MensajeDistribuido mensajeOriginal) {
        
        MensajeDistribuido respuesta = null;
        Exception ultimaExcepcion = null;
        
        for (int intento = 1; intento <= maxReintentos; intento++) {
            try {
                // Marcar como procesando
                mensajeOriginal.marcarComoProcesando();
                mensajeOriginal.incrementarIntentos();
                
                // Enviar petición
                ResponseEntity<MensajeDistribuido> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, MensajeDistribuido.class
                );
                
                respuesta = response.getBody();
                
                if (respuesta != null) {
                    mensajeOriginal.marcarComoCompletado();
                    break;
                }
                
            } catch (HttpClientErrorException e) {
                ultimaExcepcion = e;
                log.warn("Intento {} fallido (HTTP {}): {}", intento, e.getStatusCode(), e.getMessage());
                
                // Si es error de cliente (4xx), no reintentar
                if (e.getStatusCode().is4xxClientError()) {
                    break;
                }
                
            } catch (ResourceAccessException e) {
                ultimaExcepcion = e;
                log.warn("Intento {} fallido (conexión): {}", intento, e.getMessage());
                
            } catch (Exception e) {
                ultimaExcepcion = e;
                log.warn("Intento {} fallido: {}", intento, e.getMessage());
            }
            
            // Esperar antes de reintentar
            if (intento < maxReintentos) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000 * intento); // Backoff exponencial
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        if (respuesta == null) {
            mensajeOriginal.marcarComoError("COM_ERROR", ultimaExcepcion.getMessage());
            throw new ComunicacionException("No se pudo enviar mensaje después de " + maxReintentos + " intentos", ultimaExcepcion);
        }
        
        return respuesta;
    }

    /**
     * Construye la URL del microservicio destino
     */
    private String construirUrlDestino(MensajeDistribuido mensaje) {
        String servicioDestino = mensaje.getServicioDestino();
        
        // Obtener configuración del microservicio
        String urlBase = obtenerUrlBaseServicio(servicioDestino);
        
        return urlBase + "/api/comunicacion/mensaje";
    }

    /**
     * Obtiene la URL base de un microservicio
     */
    private String obtenerUrlBaseServicio(String nombreServicio) {
        // En una implementación real, esto podría usar Eureka Discovery
        // Por ahora, usamos configuración por defecto
        
        Map<String, String> urlsServicios = new HashMap<>();
        urlsServicios.put("usuarios-service", "http://localhost:8081");
        urlsServicios.put("servicios-service", "http://localhost:8082");
        urlsServicios.put("reservas-service", "http://localhost:8083");
        urlsServicios.put("pagos-service", "http://localhost:8084");
        urlsServicios.put("reportes-service", "http://localhost:8085");
        urlsServicios.put("auth-service", "http://localhost:8086");
        
        return urlsServicios.getOrDefault(nombreServicio, "http://localhost:8080");
    }

    /**
     * Prepara headers HTTP para el mensaje
     */
    private HttpHeaders prepararHeaders(MensajeDistribuido mensaje) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Message-ID", mensaje.getIdMensaje());
        headers.set("X-Service-Origin", mensaje.getServicioOrigen());
        headers.set("X-Service-Destination", mensaje.getServicioDestino());
        headers.set("X-Correlation-ID", mensaje.getCorrelacionId());
        headers.set("X-Message-Type", mensaje.getTipoMensaje());
        headers.set("X-Operation", mensaje.getOperacion());
        headers.set("X-Timestamp", mensaje.getTimestamp().toString());
        
        // Agregar headers adicionales del mensaje
        if (mensaje.getHeaders() != null) {
            mensaje.getHeaders().forEach(headers::add);
        }
        
        return headers;
    }

    /**
     * Valida el mensaje antes de enviarlo
     */
    private void validarMensaje(MensajeDistribuido mensaje) throws ComunicacionException {
        if (mensaje.getIdMensaje() == null || mensaje.getIdMensaje().trim().isEmpty()) {
            throw new ComunicacionException("ID del mensaje es requerido");
        }
        
        if (mensaje.getServicioOrigen() == null || mensaje.getServicioOrigen().trim().isEmpty()) {
            throw new ComunicacionException("Servicio origen es requerido");
        }
        
        if (mensaje.getServicioDestino() == null || mensaje.getServicioDestino().trim().isEmpty()) {
            throw new ComunicacionException("Servicio destino es requerido");
        }
        
        if (mensaje.getOperacion() == null || mensaje.getOperacion().trim().isEmpty()) {
            throw new ComunicacionException("Operación es requerida");
        }
        
        if (mensaje.getTimestamp() == null) {
            mensaje.setTimestamp(LocalDateTime.now());
        }
        
        if (mensaje.getCorrelacionId() == null || mensaje.getCorrelacionId().trim().isEmpty()) {
            mensaje.setCorrelacionId(java.util.UUID.randomUUID().toString());
        }
    }

    /**
     * Envía evento a todos los microservicios suscritos
     */
    private void enviarEventoAMicroservicios(MensajeDistribuido evento) {
        String[] servicios = {
            "usuarios-service", "servicios-service", "reservas-service", 
            "pagos-service", "reportes-service", "auth-service"
        };
        
        for (String servicio : servicios) {
            if (!servicio.equals(nombreServicio)) { // No enviar a sí mismo
                try {
                    MensajeDistribuido mensajeDestino = new MensajeDistribuido(
                        evento.getOperacion(), 
                        evento.getServicioOrigen(), 
                        servicio, 
                        evento.getPayload()
                    );
                    mensajeDestino.setTipoMensaje("EVENT");
                    mensajeDestino.setCorrelacionId(evento.getCorrelacionId());
                    
                    // Enviar asíncronamente para no bloquear
                    enviarMensajeAsincrono(mensajeDestino, respuesta -> {
                        if (respuesta.tieneError()) {
                            log.warn("Error en evento {}: {}", evento.getOperacion(), respuesta.getMensajeError());
                        }
                    });
                    
                } catch (Exception e) {
                    log.warn("Error enviando evento a {}: {}", servicio, e.getMessage());
                }
            }
        }
    }

    /**
     * Cierra el executor service
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

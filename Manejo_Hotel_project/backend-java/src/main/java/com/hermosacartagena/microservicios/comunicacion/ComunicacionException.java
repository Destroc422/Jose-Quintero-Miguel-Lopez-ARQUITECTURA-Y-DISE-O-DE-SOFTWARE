package com.hermosacartagena.microservicios.comunicacion;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * EXCEPCIÓN DE COMUNICACIÓN DISTRIBUIDA
 * =============================================
 * 
 * Excepción específica para errores en comunicación
 * entre microservicios.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
public class ComunicacionException extends Exception {

    private String codigoError;
    private String servicioOrigen;
    private String servicioDestino;
    private String idMensaje;

    public ComunicacionException(String message) {
        super(message);
    }

    public ComunicacionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComunicacionException(String codigoError, String message, String servicioOrigen, 
                               String servicioDestino, String idMensaje) {
        super(message);
        this.codigoError = codigoError;
        this.servicioOrigen = servicioOrigen;
        this.servicioDestino = servicioDestino;
        this.idMensaje = idMensaje;
    }

    public ComunicacionException(String codigoError, String message, String servicioOrigen, 
                               String servicioDestino, String idMensaje, Throwable cause) {
        super(message, cause);
        this.codigoError = codigoError;
        this.servicioOrigen = servicioOrigen;
        this.servicioDestino = servicioDestino;
        this.idMensaje = idMensaje;
    }

    // Getters y setters
    public String getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(String codigoError) {
        this.codigoError = codigoError;
    }

    public String getServicioOrigen() {
        return servicioOrigen;
    }

    public void setServicioOrigen(String servicioOrigen) {
        this.servicioOrigen = servicioOrigen;
    }

    public String getServicioDestino() {
        return servicioDestino;
    }

    public void setServicioDestino(String servicioDestino) {
        this.servicioDestino = servicioDestino;
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    @Override
    public String toString() {
        return String.format(
            "ComunicacionException{codigo='%s', origen='%s', destino='%s', mensaje='%s'}",
            codigoError, servicioOrigen, servicioDestino, getMessage()
        );
    }
}

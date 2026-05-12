package com.hermosacartagena.exception;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * EXCEPCIÓN: ERROR DE COMUNICACIÓN
 * =============================================
 * 
 * Excepción lanzada cuando ocurre un error en la comunicación
 * entre microservicios o servicios externos.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
public class ComunicacionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ComunicacionException(String message) {
        super(message);
    }

    public ComunicacionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComunicacionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }
}

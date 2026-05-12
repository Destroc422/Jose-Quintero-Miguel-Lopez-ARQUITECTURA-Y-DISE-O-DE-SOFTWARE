package com.hermosacartagena.exception;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * EXCEPCIÓN: RECURSO NO ENCONTRADO
 * =============================================
 * 
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s con ID %s no encontrado", resourceName, resourceId));
    }
}

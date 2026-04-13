package com.hermosacartagena.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * ENTIDAD BASE DE AUDITORÍA
 * =============================================
 * 
 * Esta clase abstracta proporciona campos de auditoría
 * para todas las entidades del sistema.
 * Implementa JPA Auditing para tracking automático.
 * 
 * Campos incluidos:
 * - createdAt: Fecha y hora de creación
 * - updatedAt: Fecha y hora de última modificación
 * - createdBy: Usuario que creó el registro
 * - updatedBy: Usuario que modificó el registro
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class Auditoria {

    /**
     * Fecha y hora de creación del registro
     * Se actualiza automáticamente cuando se crea la entidad
     */
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de última modificación del registro
     * Se actualiza automáticamente cuando se modifica la entidad
     */
    @LastModifiedDate
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Usuario que creó el registro
     * Se obtiene del contexto de seguridad de Spring
     */
    @CreatedBy
    @Column(name = "creado_por", length = 100, nullable = false, updatable = false)
    private String createdBy;

    /**
     * Usuario que modificó por última vez el registro
     * Se obtiene del contexto de seguridad de Spring
     */
    @LastModifiedBy
    @Column(name = "actualizado_por", length = 100, nullable = false)
    private String updatedBy;

    /**
     * Método para obtener la fecha de creación en formato compatible con la BD existente
     * 
     * @return LocalDateTime con la fecha de creación
     */
    public LocalDateTime getFechaCreacion() {
        return createdAt;
    }

    /**
     * Método para establecer la fecha de creación
     * 
     * @param fechaCreacion Fecha de creación a establecer
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.createdAt = fechaCreacion;
    }

    /**
     * Método para obtener la fecha de actualización en formato compatible con la BD existente
     * 
     * @return LocalDateTime con la fecha de actualización
     */
    public LocalDateTime getFechaActualizacion() {
        return updatedAt;
    }

    /**
     * Método para establecer la fecha de actualización
     * 
     * @param fechaActualizacion Fecha de actualización a establecer
     */
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.updatedAt = fechaActualizacion;
    }

    /**
     * Método para obtener el creador en formato compatible con la BD existente
     * 
     * @return String con el nombre del creador
     */
    public String getCreadoPor() {
        return createdBy;
    }

    /**
     * Método para establecer el creador
     * 
     * @param creadoPor Nombre del usuario creador
     */
    public void setCreadoPor(String creadoPor) {
        this.createdBy = creadoPor;
    }

    /**
     * Método para obtener el actualizador en formato compatible con la BD existente
     * 
     * @return String con el nombre del actualizador
     */
    public String getActualizadoPor() {
        return updatedBy;
    }

    /**
     * Método para establecer el actualizador
     * 
     * @param actualizadoPor Nombre del usuario actualizador
     */
    public void setActualizadoPor(String actualizadoPor) {
        this.updatedBy = actualizadoPor;
    }
}

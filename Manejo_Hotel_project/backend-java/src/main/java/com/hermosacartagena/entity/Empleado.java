package com.hermosacartagena.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * ENTIDAD EMPLEADO
 * =============================================
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Entity
@Table(name = "empleados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empleado extends Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Long idEmpleado;

    @Column(name = "id_usuario", nullable = false, unique = true)
    private Long idUsuario;

    @Column(name = "codigo_empleado", nullable = false, unique = true, length = 20)
    private String codigoEmpleado;

    @Column(name = "cargo", nullable = false, length = 100)
    private String cargo;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Column(name = "salario", precision = 10, scale = 2)
    private Double salario;

    @Column(name = "fecha_contratacion")
    private LocalDateTime fechaContratacion;

    @Column(name = "tipo_contrato", length = 50)
    private String tipoContrato;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}

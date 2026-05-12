package com.hermosacartagena.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * ENTIDAD CLIENTE
 * =============================================
 * 
 * Esta entidad representa los clientes del sistema.
 * Extiende información básica del usuario con datos específicos de cliente.
 * 
 * Contiene información adicional:
 * - Datos de identificación
 * - Preferencias y lealtad
 * - Información de contacto adicional
 * - Historial de reservas
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Entity
@Table(name = "clientes", indexes = {
    @Index(name = "idx_cliente_documento", columnList = "tipo_documento, numero_documento"),
    @Index(name = "idx_cliente_usuario", columnList = "id_usuario"),
    @Index(name = "idx_cliente_lealtad", columnList = "puntos_lealtad")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_usuario", nullable = false, unique = true)
    private Long idUsuario;

    @Column(name = "tipo_documento", length = 20, nullable = false)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 50, nullable = false, unique = true)
    private String numeroDocumento;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "nacionalidad", length = 100)
    private String nacionalidad;

    @Column(name = "preferencias_contacto", length = 500)
    private String preferenciasContacto;

    @Column(name = "puntos_lealtad", columnDefinition = "INT DEFAULT 0")
    private Integer puntosLealtad;

    @Column(name = "nivel_lealtad", length = 20)
    private String nivelLealtad;

    @Column(name = "historial_viajes", columnDefinition = "TEXT")
    private String historialViajes;

    @Column(name = "preferencias_servicios", columnDefinition = "TEXT")
    private String preferenciasServicios;

    @Column(name = "notas_adicionales", columnDefinition = "TEXT")
    private String notasAdicionales;

    // Relaciones
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos;

    /**
     * Constructor básico
     */
    public Cliente(Long idUsuario, String tipoDocumento, String numeroDocumento) {
        this.idUsuario = idUsuario;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.puntosLealtad = 0;
        this.nivelLealtad = "BRONCE";
    }

    /**
     * Calcula la edad del cliente
     * 
     * @return Edad actual o null si no hay fecha de nacimiento
     */
    public Integer getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        return LocalDate.now().minusYears(fechaNacimiento.getYear()).getYear() - 
               (fechaNacimiento.isAfter(LocalDate.now().minusYears(LocalDate.now().getYear() - fechaNacimiento.getYear())) ? 1 : 0);
    }

    /**
     * Agrega puntos de lealtad
     * 
     * @param puntos Puntos a agregar
     */
    public void agregarPuntosLealtad(Integer puntos) {
        this.puntosLealtad = (this.puntosLealtad == null) ? puntos : this.puntosLealtad + puntos;
        actualizarNivelLealtad();
    }

    /**
     * Canjear puntos de lealtad
     * 
     * @param puntos Puntos a canjear
     * @return true si se pudieron canjear los puntos
     */
    public boolean canjearPuntosLealtad(Integer puntos) {
        if (this.puntosLealtad == null || this.puntosLealtad < puntos) {
            return false;
        }
        this.puntosLealtad -= puntos;
        actualizarNivelLealtad();
        return true;
    }

    /**
     * Actualiza el nivel de lealtad según los puntos acumulados
     */
    private void actualizarNivelLealtad() {
        if (this.puntosLealtad == null) {
            this.nivelLealtad = "BRONCE";
            return;
        }

        if (this.puntosLealtad >= 1000) {
            this.nivelLealtad = "PLATINO";
        } else if (this.puntosLealtad >= 500) {
            this.nivelLealtad = "ORO";
        } else if (this.puntosLealtad >= 100) {
            this.nivelLealtad = "PLATA";
        } else {
            this.nivelLealtad = "BRONCE";
        }
    }

    /**
     * Obtiene el total de reservas del cliente
     * 
     * @return Número total de reservas
     */
    public int getTotalReservas() {
        return reservas != null ? reservas.size() : 0;
    }

    /**
     * Obtiene el total gastado por el cliente
     * 
     * @return Sumatoria total de pagos
     */
    public Double getTotalGastado() {
        if (pagos == null || pagos.isEmpty()) {
            return 0.0;
        }
        return pagos.stream()
                .filter(p -> p.getMonto() != null && "completado".equals(p.getEstado()))
                .mapToDouble(p -> p.getMonto().doubleValue())
                .sum();
    }

    /**
     * Verifica si el cliente es VIP
     * 
     * @return true si es nivel ORO o PLATINO
     */
    public boolean esVip() {
        return "ORO".equals(nivelLealtad) || "PLATINO".equals(nivelLealtad);
    }

    /**
     * Obtiene el descuento aplicable según nivel de lealtad
     * 
     * @return Porcentaje de descuento (0.0 a 1.0)
     */
    public Double getDescuentoAplicable() {
        switch (nivelLealtad) {
            case "BRONCE":
                return 0.0;
            case "PLATA":
                return 0.05; // 5%
            case "ORO":
                return 0.10; // 10%
            case "PLATINO":
                return 0.15; // 15%
            default:
                return 0.0;
        }
    }

    /**
     * Obtiene información completa del cliente
     * 
     * @return String con información detallada
     */
    public String getInformacionCompleta() {
        StringBuilder info = new StringBuilder();
        info.append("Cliente: ").append(usuario != null ? usuario.getNombreCompleto() : "N/A");
        info.append(" | Documento: ").append(tipoDocumento).append(" ").append(numeroDocumento);
        info.append(" | Nivel: ").append(nivelLealtad);
        info.append(" | Puntos: ").append(puntosLealtad);
        info.append(" | Reservas: ").append(getTotalReservas());
        
        if (esVip()) {
            info.append(" [VIP]");
        }
        
        return info.toString();
    }

    /**
     * Método toString personalizado
     * 
     * @return Representación en texto del cliente
     */
    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", nivelLealtad='" + nivelLealtad + '\'' +
                ", puntosLealtad=" + puntosLealtad +
                '}';
    }
}

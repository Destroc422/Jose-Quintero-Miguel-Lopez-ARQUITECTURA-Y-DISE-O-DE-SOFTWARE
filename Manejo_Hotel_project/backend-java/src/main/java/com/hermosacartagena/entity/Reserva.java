package com.hermosacartagena.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * ENTIDAD RESERVA
 * =============================================
 * 
 * Esta entidad representa las reservas de servicios turísticos.
 * Es una entidad central que conecta clientes, servicios y pagos.
 * 
 * Contiene información completa de la reserva:
 * - Datos de identificación y seguimiento
 * - Información temporal y de capacidad
 * - Estado y confirmación
 * - Relación con pagos y servicios
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Entity
@Table(name = "reservas", indexes = {
    @Index(name = "idx_reserva_codigo", columnList = "codigo_reserva"),
    @Index(name = "idx_reserva_cliente", columnList = "id_cliente"),
    @Index(name = "idx_reserva_servicio", columnList = "id_servicio"),
    @Index(name = "idx_reserva_estado", columnList = "estado_reserva"),
    @Index(name = "idx_reserva_fecha", columnList = "fecha_inicio_servicio"),
    @Index(name = "idx_reserva_empleado", columnList = "id_empleado")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva extends Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "codigo_reserva", length = 50, nullable = false, unique = true)
    private String codigoReserva;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "id_servicio", nullable = false)
    private Long idServicio;

    @Column(name = "id_empleado")
    private Long idEmpleado;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_inicio_servicio", nullable = false)
    private LocalDateTime fechaInicioServicio;

    @Column(name = "fecha_fin_servicio")
    private LocalDateTime fechaFinServicio;

    @Column(name = "cantidad_personas", nullable = false)
    private Integer cantidadPersonas;

    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "precio_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioTotal;

    @Column(name = "descuento_aplicado", precision = 5, scale = 2, columnDefinition = "DECIMAL(5,2) DEFAULT 0.00")
    private BigDecimal descuentoAplicado;

    @Column(name = "monto_descuento", precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal montoDescuento;

    @Column(name = "estado_reserva", length = 20, nullable = false)
    private String estadoReserva;

    @Column(name = "confirmada", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean confirmada;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "cancelada", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean cancelada;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @Column(name = "notas_adicionales", columnDefinition = "TEXT")
    private String notasAdicionales;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", insertable = false, updatable = false)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", insertable = false, updatable = false)
    private Empleado empleado;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos;

    /**
     * Constructor básico
     */
    public Reserva(String codigoReserva, Long idCliente, Long idServicio, 
                   LocalDateTime fechaInicioServicio, Integer cantidadPersonas, 
                   BigDecimal precioUnitario) {
        this.codigoReserva = codigoReserva;
        this.idCliente = idCliente;
        this.idServicio = idServicio;
        this.fechaReserva = LocalDateTime.now();
        this.fechaInicioServicio = fechaInicioServicio;
        this.cantidadPersonas = cantidadPersonas;
        this.precioUnitario = precioUnitario;
        this.estadoReserva = "pendiente";
        this.confirmada = false;
        this.cancelada = false;
        this.descuentoAplicado = BigDecimal.ZERO;
        this.montoDescuento = BigDecimal.ZERO;
        
        // Calcular precio total
        this.precioTotal = precioUnitario.multiply(new BigDecimal(cantidadPersonas));
    }

    // Métodos de negocio

    /**
     * Verifica si la reserva está activa (no cancelada)
     * 
     * @return true si la reserva está activa
     */
    public boolean estaActiva() {
        return !Boolean.TRUE.equals(cancelada);
    }

    /**
     * Verifica si la reserva está confirmada
     * 
     * @return true si está confirmada
     */
    public boolean estaConfirmada() {
        return Boolean.TRUE.equals(confirmada);
    }

    /**
     * Verifica si la reserva está pagada
     * 
     * @return true si está pagada
     */
    public boolean estaPagada() {
        return "pagada".equals(estadoReserva);
    }

    /**
     * Verifica si la reserva está pendiente de pago
     * 
     * @return true si está pendiente de pago
     */
    public boolean estaPendientePago() {
        return "pendiente_pago".equals(estadoReserva);
    }

    /**
     * Confirma la reserva
     */
    public void confirmar() {
        this.confirmada = true;
        this.fechaConfirmacion = LocalDateTime.now();
        this.estadoReserva = "confirmada";
    }

    /**
     * Cancela la reserva
     * 
     * @param motivo Motivo de cancelación
     */
    public void cancelar(String motivo) {
        this.cancelada = true;
        this.fechaCancelacion = LocalDateTime.now();
        this.motivoCancelacion = motivo;
        this.estadoReserva = "cancelada";
    }

    /**
     * Marca como pagada
     */
    public void marcarComoPagada() {
        this.estadoReserva = "pagada";
        if (!this.estaConfirmada()) {
            this.confirmar();
        }
    }

    /**
     * Aplica un descuento a la reserva
     * 
     * @param porcentajeDescuento Porcentaje de descuento (0.0 a 1.0)
     */
    public void aplicarDescuento(BigDecimal porcentajeDescuento) {
        if (porcentajeDescuento == null || porcentajeDescuento.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        this.descuentoAplicado = porcentajeDescuento;
        BigDecimal precioOriginal = precioUnitario.multiply(new BigDecimal(cantidadPersonas));
        this.montoDescuento = precioOriginal.multiply(porcentajeDescuento);
        this.precioTotal = precioOriginal.subtract(this.montoDescuento);
    }

    /**
     * Calcula el monto total pagado
     * 
     * @return Sumatoria de pagos completados
     */
    public BigDecimal getMontoPagado() {
        if (pagos == null || pagos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return pagos.stream()
                .filter(p -> "completado".equals(p.getEstado()) && p.getMonto() != null)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el saldo pendiente
     * 
     * @return Monto restante por pagar
     */
    public BigDecimal getSaldoPendiente() {
        BigDecimal pagado = getMontoPagado();
        return precioTotal.subtract(pagado);
    }

    /**
     * Verifica si la reserva está completamente pagada
     * 
     * @return true si está pagada completamente
     */
    public boolean estaCompletamentePagada() {
        return getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * Verifica si la reserva está próxima (dentro de 24 horas)
     * 
     * @return true si está próxima a iniciarse
     */
    public boolean estaProxima() {
        if (fechaInicioServicio == null) {
            return false;
        }
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime en24Horas = ahora.plusHours(24);
        return fechaInicioServicio.isAfter(ahora) && fechaInicioServicio.isBefore(en24Horas);
    }

    /**
     * Verifica si la reserva está en progreso
     * 
     * @return true si está en progreso actualmente
     */
    public boolean estaEnProgreso() {
        if (fechaInicioServicio == null || fechaFinServicio == null) {
            return false;
        }
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(fechaInicioServicio) && ahora.isBefore(fechaFinServicio);
    }

    /**
     * Verifica si la reserva ya pasó
     * 
     * @return true si la reserva ya finalizó
     */
    public boolean estaFinalizada() {
        if (fechaFinServicio == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(fechaFinServicio);
    }

    /**
     * Obtiene el estado formateado para mostrar
     * 
     * @return String con estado formateado
     */
    public String getEstadoFormateado() {
        switch (estadoReserva) {
            case "pendiente":
                return "Pendiente";
            case "confirmada":
                return "Confirmada";
            case "pagada":
                return "Pagada";
            case "pendiente_pago":
                return "Pendiente de Pago";
            case "cancelada":
                return "Cancelada";
            case "completada":
                return "Completada";
            default:
                return estadoReserva;
        }
    }

    /**
     * Obtiene el precio total formateado
     * 
     * @return String con precio formateado
     */
    public String getPrecioTotalFormateado() {
        return String.format("$%,.2f", precioTotal);
    }

    /**
     * Obtiene el monto de descuento formateado
     * 
     * @return String con descuento formateado
     */
    public String getMontoDescuentoFormateado() {
        if (montoDescuento == null || montoDescuento.compareTo(BigDecimal.ZERO) == 0) {
            return "$0.00";
        }
        return String.format("$%,.2f", montoDescuento);
    }

    /**
     * Obtiene información completa de la reserva
     * 
     * @return String con información detallada
     */
    public String getInformacionCompleta() {
        StringBuilder info = new StringBuilder();
        info.append("Reserva: ").append(codigoReserva);
        info.append(" | Cliente: ").append(cliente != null ? cliente.getUsuario().getNombreCompleto() : "N/A");
        info.append(" | Servicio: ").append(servicio != null ? servicio.getNombreServicio() : "N/A");
        info.append(" | Fecha: ").append(fechaInicioServicio.toLocalDate());
        info.append(" | Personas: ").append(cantidadPersonas);
        info.append(" | Total: ").append(getPrecioTotalFormateado());
        info.append(" | Estado: ").append(getEstadoFormateado());
        
        if (estaConfirmada()) {
            info.append(" [CONFIRMADA]");
        }
        
        if (estaPagada()) {
            info.append(" [PAGADA]");
        }
        
        if (cancelada) {
            info.append(" [CANCELADA]");
        }
        
        return info.toString();
    }

    /**
     * Genera un código de reserva único
     * 
     * @return String con código generado
     */
    public static String generarCodigoReserva() {
        return "HC-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    /**
     * Método toString personalizado
     * 
     * @return Representación en texto de la reserva
     */
    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", codigoReserva='" + codigoReserva + '\'' +
                ", estadoReserva='" + estadoReserva + '\'' +
                ", cantidadPersonas=" + cantidadPersonas +
                ", precioTotal=" + precioTotal +
                '}';
    }
}

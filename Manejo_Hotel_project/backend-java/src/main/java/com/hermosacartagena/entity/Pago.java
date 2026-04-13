package com.hermosacartagena.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * ENTIDAD PAGO
 * =============================================
 * 
 * Esta entidad representa los pagos realizados por las reservas.
 * Soporta múltiples métodos de pago y monedas.
 * 
 * Contiene información completa del pago:
 * - Datos de transacción
 * - Montos y divisas
 * - Estado y confirmación
 * - Relación con reservas y clientes
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Entity
@Table(name = "pagos", indexes = {
    @Index(name = "idx_pago_reserva", columnList = "id_reserva"),
    @Index(name = "idx_pago_cliente", columnList = "id_cliente"),
    @Index(name = "idx_pago_estado", columnList = "estado"),
    @Index(name = "idx_pago_fecha", columnList = "fecha_pago"),
    @Index(name = "idx_pago_referencia", columnList = "referencia_pago")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago extends Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @Column(name = "id_reserva", nullable = false)
    private Long idReserva;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "moneda", length = 10, nullable = false)
    private String moneda;

    @Column(name = "tipo_cambio", precision = 10, scale = 6, columnDefinition = "DECIMAL(10,6) DEFAULT 1.000000")
    private BigDecimal tipoCambio;

    @Column(name = "monto_original", precision = 10, scale = 2)
    private BigDecimal montoOriginal;

    @Column(name = "moneda_original", length = 10)
    private String monedaOriginal;

    @Column(name = "metodo_pago", length = 50, nullable = false)
    private String metodoPago;

    @Column(name = "tipo_tarjeta", length = 50)
    private String tipoTarjeta;

    @Column(name = "numero_tarjeta", length = 20)
    private String numeroTarjeta;

    @Column(name = "titular_tarjeta", length = 255)
    private String titularTarjeta;

    @Column(name = "banco_emisor", length = 100)
    private String bancoEmisor;

    @Column(name = "referencia_pago", length = 100, unique = true)
    private String referenciaPago;

    @Column(name = "codigo_autorizacion", length = 50)
    private String codigoAutorizacion;

    @Column(name = "respuesta_gateway", columnDefinition = "TEXT")
    private String respuestaGateway;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "fecha_reembolso")
    private LocalDateTime fechaReembolso;

    @Column(name = "motivo_reembolso", length = 500)
    private String motivoReembolso;

    @Column(name = "notas_adicionales", columnDefinition = "TEXT")
    private String notasAdicionales;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", insertable = false, updatable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private Cliente cliente;

    /**
     * Constructor básico
     */
    public Pago(Long idReserva, Long idCliente, BigDecimal monto, String moneda, String metodoPago) {
        this.idReserva = idReserva;
        this.idCliente = idCliente;
        this.fechaPago = LocalDateTime.now();
        this.monto = monto;
        this.moneda = moneda;
        this.metodoPago = metodoPago;
        this.estado = "pendiente";
        this.tipoCambio = BigDecimal.ONE;
    }

    /**
     * Constructor pago internacional
     */
    public Pago(Long idReserva, Long idCliente, BigDecimal monto, String moneda, 
                BigDecimal montoOriginal, String monedaOriginal, BigDecimal tipoCambio, 
                String metodoPago) {
        this(idReserva, idCliente, monto, moneda, metodoPago);
        this.montoOriginal = montoOriginal;
        this.monedaOriginal = monedaOriginal;
        this.tipoCambio = tipoCambio;
    }

    // Métodos de negocio

    /**
     * Verifica si el pago está completado
     * 
     * @return true si está completado
     */
    public boolean estaCompletado() {
        return "completado".equals(estado);
    }

    /**
     * Verifica si el pago está pendiente
     * 
     * @return true si está pendiente
     */
    public boolean estaPendiente() {
        return "pendiente".equals(estado);
    }

    /**
     * Verifica si el pago está fallido
     * 
     * @return true si está fallido
     */
    public boolean estaFallido() {
        return "fallido".equals(estado);
    }

    /**
     * Verifica si el pago fue reembolsado
     * 
     * @return true si fue reembolsado
     */
    public boolean estaReembolsado() {
        return "reembolsado".equals(estado);
    }

    /**
     * Confirma el pago
     * 
     * @param codigoAutorizacion Código de autorización del gateway
     * @param respuesta Respuesta del gateway de pago
     */
    public void confirmar(String codigoAutorizacion, String respuesta) {
        this.estado = "completado";
        this.fechaConfirmacion = LocalDateTime.now();
        this.codigoAutorizacion = codigoAutorizacion;
        this.respuestaGateway = respuesta;
    }

    /**
     * Marca el pago como fallido
     * 
     * @param motivo Motivo del fallo
     */
    public void marcarComoFallido(String motivo) {
        this.estado = "fallido";
        this.respuestaGateway = motivo;
    }

    /**
     * Procesa el reembolso del pago
     * 
     * @param motivo Motivo del reembolso
     */
    public void procesarReembolso(String motivo) {
        this.estado = "reembolsado";
        this.fechaReembolso = LocalDateTime.now();
        this.motivoReembolso = motivo;
    }

    /**
     * Verifica si es un pago internacional
     * 
     * @return true si es internacional
     */
    public boolean esInternacional() {
        return monedaOriginal != null && !moneda.equals(monedaOriginal);
    }

    /**
     * Verifica si es pago con tarjeta
     * 
     * @return true si es con tarjeta
     */
    public boolean esPagoConTarjeta() {
        return "tarjeta_credito".equals(metodoPago) || 
               "tarjeta_debito".equals(metodoPago) || 
               "tarjeta".equals(metodoPago);
    }

    /**
     * Verifica si es pago electrónico
     * 
     * @return true si es pago electrónico
     */
    public boolean esPagoElectronico() {
        return "paypal".equals(metodoPago) || 
               "stripe".equals(metodoPago) || 
               "mercadopago".equals(metodoPago) || 
               "pse".equals(metodoPago);
    }

    /**
     * Genera referencia de pago única
     * 
     * @return String con referencia generada
     */
    public static String generarReferenciaPago() {
        return "PAY-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    /**
     * Enmascara el número de tarjeta para mostrar
     * 
     * @return String con tarjeta enmascarada
     */
    public String getNumeroTarjetaEnmascarado() {
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) {
            return "****";
        }
        
        int longitud = numeroTarjeta.length();
        String ultimos4 = numeroTarjeta.substring(longitud - 4);
        StringBuilder enmascarado = new StringBuilder();
        
        for (int i = 0; i < longitud - 4; i++) {
            enmascarado.append("*");
            if (i > 0 && i % 4 == 0) {
                enmascarado.append(" ");
            }
        }
        enmascarado.append(ultimos4);
        
        return enmascarado.toString();
    }

    /**
     * Obtiene el método de pago formateado
     * 
     * @return String con método formateado
     */
    public String getMetodoPagoFormateado() {
        switch (metodoPago) {
            case "tarjeta_credito":
                return "Tarjeta de Crédito";
            case "tarjeta_debito":
                return "Tarjeta de Débito";
            case "transferencia":
                return "Transferencia Bancaria";
            case "efectivo":
                return "Efectivo";
            case "paypal":
                return "PayPal";
            case "stripe":
                return "Stripe";
            case "pse":
                return "PSE";
            case "mercadopago":
                return "Mercado Pago";
            default:
                return metodoPago;
        }
    }

    /**
     * Obtiene el estado formateado
     * 
     * @return String con estado formateado
     */
    public String getEstadoFormateado() {
        switch (estado) {
            case "pendiente":
                return "Pendiente";
            case "completado":
                return "Completado";
            case "fallido":
                return "Fallido";
            case "reembolsado":
                return "Reembolsado";
            case "cancelado":
                return "Cancelado";
            default:
                return estado;
        }
    }

    /**
     * Obtiene el monto formateado
     * 
     * @return String con monto formateado
     */
    public String getMontoFormateado() {
        return String.format("%s %,.2f", moneda, monto);
    }

    /**
     * Obtiene información completa del pago
     * 
     * @return String con información detallada
     */
    public String getInformacionCompleta() {
        StringBuilder info = new StringBuilder();
        info.append("Pago: ").append(referenciaPago != null ? referenciaPago : "N/A");
        info.append(" | Monto: ").append(getMontoFormateado());
        info.append(" | Método: ").append(getMetodoPagoFormateado());
        info.append(" | Estado: ").append(getEstadoFormateado());
        info.append(" | Fecha: ").append(fechaPago.toLocalDate());
        
        if (esInternacional()) {
            info.append(" | Original: ").append(String.format("%s %,.2f", monedaOriginal, montoOriginal));
        }
        
        if (esPagoConTarjeta() && numeroTarjeta != null) {
            info.append(" | Tarjeta: ").append(getNumeroTarjetaEnmascarado());
        }
        
        if (estaCompletado()) {
            info.append(" [CONFIRMADO]");
        }
        
        if (estaReembolsado()) {
            info.append(" [REEMBOLSADO]");
        }
        
        return info.toString();
    }

    /**
     * Método toString personalizado
     * 
     * @return Representación en texto del pago
     */
    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", referenciaPago='" + referenciaPago + '\'' +
                ", monto=" + monto +
                ", moneda='" + moneda + '\'' +
                ", metodoPago='" + metodoPago + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}

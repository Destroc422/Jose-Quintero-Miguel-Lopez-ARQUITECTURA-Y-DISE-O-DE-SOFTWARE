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
 * ENTIDAD SERVICIO
 * =============================================
 * 
 * Esta entidad representa los servicios turísticos ofrecidos.
 * Es la entidad central del sistema con relaciones múltiples.
 * 
 * Contiene información completa del servicio:
 * - Datos básicos y descripción
 * - Información de precios y capacidad
 * - Relación con proveedor
 * - Gestión de disponibilidad
 * - Multimedia y detalles adicionales
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Entity
@Table(name = "servicios", indexes = {
    @Index(name = "idx_servicio_tipo", columnList = "tipo_servicio"),
    @Index(name = "idx_servicio_proveedor", columnList = "id_proveedor"),
    @Index(name = "idx_servicio_estado", columnList = "estado"),
    @Index(name = "idx_servicio_precio", columnList = "precio_base"),
    @Index(name = "idx_servicio_ubicacion", columnList = "ubicacion")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servicio extends Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Long idServicio;

    @Column(name = "id_proveedor", nullable = false)
    private Long idProveedor;

    @Column(name = "nombre_servicio", length = 255, nullable = false)
    private String nombreServicio;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "tipo_servicio", length = 50, nullable = false)
    private String tipoServicio;

    @Column(name = "precio_base", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioBase;

    @Column(name = "duracion_horas", precision = 5, scale = 2, nullable = false)
    private BigDecimal duracionHoras;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;

    @Column(name = "ubicacion", length = 255, nullable = false)
    private String ubicacion;

    @Column(name = "requisitos", columnDefinition = "TEXT")
    private String requisitos;

    @Column(name = "incluye", columnDefinition = "TEXT")
    private String incluye;

    @Column(name = "no_incluye", columnDefinition = "TEXT")
    private String noIncluye;

    @Column(name = "imagenes", columnDefinition = "TEXT")
    private String imagenes;

    @Column(name = "video_promocional", length = 500)
    private String videoPromocional;

    @Column(name = "calificacion_promedio", precision = 3, scale = 2, columnDefinition = "DECIMAL(3,2) DEFAULT 0.00")
    private BigDecimal calificacionPromedio;

    @Column(name = "total_reservas", columnDefinition = "INT DEFAULT 0")
    private Integer totalReservas;

    @Column(name = "disponible_desde")
    private LocalDateTime disponibleDesde;

    @Column(name = "disponible_hasta")
    private LocalDateTime disponibleHasta;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "etiquetas", length = 500)
    private String etiquetas;

    @Column(name = "destacado", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean destacado;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", insertable = false, updatable = false)
    private Proveedor proveedor;

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reserva> reservas;

    /**
     * Constructor básico
     */
    public Servicio(Long idProveedor, String nombreServicio, String descripcion, 
                   String tipoServicio, BigDecimal precioBase, BigDecimal duracionHoras, 
                   Integer capacidadMaxima, String ubicacion) {
        this.idProveedor = idProveedor;
        this.nombreServicio = nombreServicio;
        this.descripcion = descripcion;
        this.tipoServicio = tipoServicio;
        this.precioBase = precioBase;
        this.duracionHoras = duracionHoras;
        this.capacidadMaxima = capacidadMaxima;
        this.ubicacion = ubicacion;
        this.estado = "activo";
        this.totalReservas = 0;
        this.calificacionPromedio = BigDecimal.ZERO;
        this.destacado = false;
    }

    // Métodos de negocio

    /**
     * Verifica si el servicio está activo
     * 
     * @return true si el servicio está activo
     */
    public boolean estaActivo() {
        return "activo".equals(this.estado);
    }

    /**
     * Verifica si el servicio está disponible en una fecha específica
     * 
     * @param fecha Fecha a verificar
     * @return true si está disponible
     */
    public boolean estaDisponible(LocalDateTime fecha) {
        if (!estaActivo()) {
            return false;
        }

        if (disponibleDesde != null && fecha.isBefore(disponibleDesde)) {
            return false;
        }

        if (disponibleHasta != null && fecha.isAfter(disponibleHasta)) {
            return false;
        }

        return true;
    }

    /**
     * Verifica si hay capacidad para una cantidad de personas
     * 
     * @param cantidadPersonas Número de personas
     * @return true si hay capacidad suficiente
     */
    public boolean tieneCapacidad(Integer cantidadPersonas) {
        return cantidadPersonas != null && cantidadPersonas <= this.capacidadMaxima;
    }

    /**
     * Calcula el precio total para una cantidad de personas
     * 
     * @param cantidadPersonas Número de personas
     * @return Precio total calculado
     */
    public BigDecimal calcularPrecioTotal(Integer cantidadPersonas) {
        if (cantidadPersonas == null || cantidadPersonas <= 0) {
            return BigDecimal.ZERO;
        }
        return precioBase.multiply(new BigDecimal(cantidadPersonas));
    }

    /**
     * Calcula el precio con descuento para clientes VIP
     * 
     * @param cantidadPersonas Número de personas
     * @param descuento Porcentaje de descuento (0.0 a 1.0)
     * @return Precio con descuento aplicado
     */
    public BigDecimal calcularPrecioConDescuento(Integer cantidadPersonas, BigDecimal descuento) {
        BigDecimal precioTotal = calcularPrecioTotal(cantidadPersonas);
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal montoDescuento = precioTotal.multiply(descuento);
            return precioTotal.subtract(montoDescuento);
        }
        return precioTotal;
    }

    /**
     * Actualiza la calificación promedio del servicio
     * 
     * @param nuevaCalificacion Nueva calificación a agregar
     */
    public void actualizarCalificacion(BigDecimal nuevaCalificacion) {
        if (nuevaCalificacion == null) {
            return;
        }

        if (this.totalReservas == null || this.totalReservas == 0) {
            this.calificacionPromedio = nuevaCalificacion;
        } else {
            // Fórmula para actualizar promedio: (promedio_actual * total + nueva_calificacion) / (total + 1)
            BigDecimal sumaActual = this.calificacionPromedio.multiply(new BigDecimal(this.totalReservas));
            BigDecimal nuevaSuma = sumaActual.add(nuevaCalificacion);
            BigDecimal nuevoPromedio = nuevaSuma.divide(new BigDecimal(this.totalReservas + 1), 2, BigDecimal.ROUND_HALF_UP);
            this.calificacionPromedio = nuevoPromedio;
        }
        this.totalReservas++;
    }

    /**
     * Activa el servicio
     */
    public void activar() {
        this.estado = "activo";
    }

    /**
     * Desactiva el servicio
     */
    public void desactivar() {
        this.estado = "inactivo";
    }

    /**
     * Marca como temporada alta
     */
    public void marcarTemporadaAlta() {
        this.estado = "temporada_alta";
    }

    /**
     * Verifica si está en temporada alta
     * 
     * @return true si está en temporada alta
     */
    public boolean esTemporadaAlta() {
        return "temporada_alta".equals(this.estado);
    }

    /**
     * Destaca el servicio
     */
    public void destacar() {
        this.destacado = true;
    }

    /**
     * Quita el destacado del servicio
     */
    public void quitarDestacado() {
        this.destacado = false;
    }

    /**
     * Obtiene el precio formateado
     * 
     * @return String con precio formateado
     */
    public String getPrecioFormateado() {
        return String.format("$%,.2f", precioBase);
    }

    /**
     * Obtiene la descripción corta (primeros 100 caracteres)
     * 
     * @return String con descripción corta
     */
    public String getDescripcionCorta() {
        if (descripcion == null) {
            return "";
        }
        return descripcion.length() > 100 ? descripcion.substring(0, 97) + "..." : descripcion;
    }

    /**
     * Obtiene la lista de imágenes como array
     * 
     * @return Array de URLs de imágenes
     */
    public String[] getImagenesArray() {
        if (imagenes == null || imagenes.trim().isEmpty()) {
            return new String[0];
        }
        return imagenes.split(",");
    }

    /**
     * Obtiene la primera imagen del servicio
     * 
     * @return URL de la primera imagen o null
     */
    public String getImagenPrincipal() {
        String[] imagenesArray = getImagenesArray();
        return imagenesArray.length > 0 ? imagenesArray[0] : null;
    }

    /**
     * Agrega una imagen al servicio
     * 
     * @param urlImagen URL de la imagen a agregar
     */
    public void agregarImagen(String urlImagen) {
        if (urlImagen == null || urlImagen.trim().isEmpty()) {
            return;
        }

        if (this.imagenes == null || this.imagenes.trim().isEmpty()) {
            this.imagenes = urlImagen;
        } else {
            this.imagenes += "," + urlImagen;
        }
    }

    /**
     * Verifica si el tipo de servicio es válido
     * 
     * @return true si el tipo es válido
     */
    public boolean esTipoValido() {
        return "tour".equals(tipoServicio) || 
               "hospedaje".equals(tipoServicio) || 
               "transporte".equals(tipoServicio) || 
               "alimentacion".equals(tipoServicio) || 
               "actividad".equals(tipoServicio);
    }

    /**
     * Obtiene el tipo de servicio formateado para mostrar
     * 
     * @return String con tipo formateado
     */
    public String getTipoFormateado() {
        switch (tipoServicio) {
            case "tour":
                return "Tour Turístico";
            case "hospedaje":
                return "Hospedaje";
            case "transporte":
                return "Transporte";
            case "alimentacion":
                return "Alimentación";
            case "actividad":
                return "Actividad";
            default:
                return tipoServicio;
        }
    }

    /**
     * Obtiene información completa del servicio
     * 
     * @return String con información detallada
     */
    public String getInformacionCompleta() {
        StringBuilder info = new StringBuilder();
        info.append("Servicio: ").append(nombreServicio);
        info.append(" | Tipo: ").append(getTipoFormateado());
        info.append(" | Precio: ").append(getPrecioFormateado());
        info.append(" | Duración: ").append(duracionHoras).append(" horas");
        info.append(" | Capacidad: ").append(capacidadMaxima).append(" personas");
        info.append(" | Ubicación: ").append(ubicacion);
        
        if (calificacionPromedio != null && calificacionPromedio.compareTo(BigDecimal.ZERO) > 0) {
            info.append(" | Calificación: ").append(String.format("%.1f", calificacionPromedio)).append("/5.0");
        }
        
        if (destacado) {
            info.append(" [DESTACADO]");
        }
        
        return info.toString();
    }

    /**
     * Método toString personalizado
     * 
     * @return Representación en texto del servicio
     */
    @Override
    public String toString() {
        return "Servicio{" +
                "idServicio=" + idServicio +
                ", nombreServicio='" + nombreServicio + '\'' +
                ", tipoServicio='" + tipoServicio + '\'' +
                ", precioBase=" + precioBase +
                ", capacidadMaxima=" + capacidadMaxima +
                ", estado='" + estado + '\'' +
                '}';
    }
}

package com.hermosacartagena.dto;

import jakarta.validation.constraints.*;
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
 * DTO SERVICIO
 * =============================================
 * 
 * Data Transfer Object para la entidad Servicio.
 * Se utiliza para transferencia de datos entre capas y validación.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {

    private Long idServicio;

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long idProveedor;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre del servicio debe tener entre 3 y 255 caracteres")
    private String nombreServicio;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String descripcion;

    @NotBlank(message = "El tipo de servicio es obligatorio")
    @Pattern(regexp = "^(tour|hospedaje|transporte|alimentacion|actividad)$", 
             message = "El tipo de servicio debe ser: tour, hospedaje, transporte, alimentacion o actividad")
    private String tipoServicio;

    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio base debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio base debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal precioBase;

    @NotNull(message = "La duración es obligatoria")
    @DecimalMin(value = "0.5", message = "La duración debe ser mayor a 0.5 horas")
    @Digits(integer = 3, fraction = 2, message = "La duración debe tener máximo 3 dígitos enteros y 2 decimales")
    private BigDecimal duracionHoras;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 1, message = "La capacidad máxima debe ser mayor a 0")
    private Integer capacidadMaxima;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(min = 3, max = 255, message = "La ubicación debe tener entre 3 y 255 caracteres")
    private String ubicacion;

    @Size(max = 1000, message = "Los requisitos no deben exceder 1000 caracteres")
    private String requisitos;

    @Size(max = 1000, message = "El campo 'incluye' no debe exceder 1000 caracteres")
    private String incluye;

    @Size(max = 1000, message = "El campo 'no incluye' no debe exceder 1000 caracteres")
    private String noIncluye;

    private String imagenes;
    private String videoPromocional;
    private BigDecimal calificacionPromedio;
    private Integer totalReservas;
    private LocalDateTime disponibleDesde;
    private LocalDateTime disponibleHasta;
    private String estado;
    private String etiquetas;
    private Boolean destacado;

    // Campos adicionales para respuestas
    private String nombreProveedor;
    private String contactoProveedor;
    private String telefonoProveedor;
    private String imagenPrincipal;
    private String tipoFormateado;
    private String precioFormateado;
    private String descripcionCorta;
    private Boolean tieneDisponibilidad;
    private List<String> imagenesArray;

    /**
     * Constructor básico
     */
    public ServicioDTO(Long idProveedor, String nombreServicio, String descripcion, 
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
        this.destacado = false;
    }

    /**
     * Verifica si el servicio está activo
     * 
     * @return true si está activo
     */
    public boolean estaActivo() {
        return "activo".equals(this.estado);
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
     * Calcula el precio total para una cantidad de personas
     * 
     * @param cantidadPersonas Número de personas
     * @return Precio total calculado
     */
    public BigDecimal calcularPrecioTotal(Integer cantidadPersonas) {
        if (cantidadPersonas == null || cantidadPersonas <= 0 || precioBase == null) {
            return BigDecimal.ZERO;
        }
        return precioBase.multiply(new BigDecimal(cantidadPersonas));
    }

    /**
     * Obtiene el precio formateado
     * 
     * @return String con precio formateado
     */
    public String getPrecioFormateado() {
        if (precioBase == null) {
            return "$0.00";
        }
        return String.format("$%,.2f", precioBase);
    }

    /**
     * Obtiene la descripción corta
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
     * Obtiene el tipo formateado para mostrar
     * 
     * @return String con tipo formateado
     */
    public String getTipoFormateado() {
        if (tipoServicio == null) {
            return "";
        }
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
     * Obtiene la imagen principal
     * 
     * @return URL de la primera imagen
     */
    public String getImagenPrincipal() {
        if (imagenes == null || imagenes.trim().isEmpty()) {
            return null;
        }
        String[] imagenesArray = imagenes.split(",");
        return imagenesArray.length > 0 ? imagenesArray[0] : null;
    }

    /**
     * Obtiene el array de imágenes
     * 
     * @return Array de URLs de imágenes
     */
    public List<String> getImagenesArray() {
        if (imagenes == null || imagenes.trim().isEmpty()) {
            return List.of();
        }
        return List.of(imagenes.split(","));
    }

    /**
     * DTO para búsqueda de servicios
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusquedaServicios {
        private String texto;
        private String tipoServicio;
        private BigDecimal precioMin;
        private BigDecimal precioMax;
        private Integer capacidadMinima;
        private String ubicacion;
        private Boolean destacado;
        private String estado;
        private Integer page;
        private Integer size;
        private String sortBy;
        private String sortDir;
    }

    /**
     * DTO para disponibilidad de servicio
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisponibilidadServicio {
        @NotNull(message = "El ID del servicio es obligatorio")
        private Long idServicio;

        @NotNull(message = "La fecha de inicio es obligatoria")
        private LocalDateTime fechaInicio;

        private LocalDateTime fechaFin;

        @NotNull(message = "La cantidad de personas es obligatoria")
        @Min(value = 1, message = "La cantidad de personas debe ser mayor a 0")
        private Integer cantidadPersonas;
    }

    /**
     * DTO para respuesta de disponibilidad
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RespuestaDisponibilidad {
        private boolean disponible;
        private String mensaje;
        private Integer capacidadDisponible;
        private LocalDateTime fechaSugerida;
    }

    /**
     * DTO para estadísticas de servicios
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadisticasServicios {
        private Long total;
        private Long activos;
        private Long inactivos;
        private Long destacados;
        private BigDecimal precioPromedio;
        private BigDecimal calificacionPromedio;
        private Long tours;
        private Long hospedaje;
        private Long transporte;
        private Long alimentacion;
        private Long actividades;
    }
}

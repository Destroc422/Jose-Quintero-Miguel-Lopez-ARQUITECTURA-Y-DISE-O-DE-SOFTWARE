package com.hermosacartagena.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CLASE SERVICIO TURÍSTICO (SERIALIZABLE)
 * =============================================
 * 
 * Esta clase representa un objeto serializable de servicio turístico
 * para el sistema distribuido con sockets.
 * 
 * @author Sistema de Gestión Turística
 * @version 1.0
 */
public class ServicioTuristico implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Atributos del servicio turístico
    private int idServicio;
    private int idProveedor;
    private String nombreServicio;
    private String descripcion;
    private String tipoServicio;
    private double precioBase;
    private double duracionHoras;
    private int capacidadMaxima;
    private String ubicacion;
    private String requisitos;
    private String incluye;
    private String noIncluye;
    private String imagenes;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Atributos adicionales para relaciones
    private String nombreProveedor;
    private String contactoPrincipal;
    private String telefonoProveedor;
    
    /**
     * Constructor por defecto
     */
    public ServicioTuristico() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "activo";
    }
    
    /**
     * Constructor con parámetros básicos
     * @param idProveedor ID del proveedor
     * @param nombreServicio Nombre del servicio
     * @param descripcion Descripción del servicio
     * @param tipoServicio Tipo de servicio
     * @param precioBase Precio base
     * @param duracionHoras Duración en horas
     * @param capacidadMaxima Capacidad máxima
     * @param ubicación Ubicación
     */
    public ServicioTuristico(int idProveedor, String nombreServicio, String descripcion, 
                           String tipoServicio, double precioBase, double duracionHoras, 
                           int capacidadMaxima, String ubicacion) {
        this();
        this.idProveedor = idProveedor;
        this.nombreServicio = nombreServicio;
        this.descripcion = descripcion;
        this.tipoServicio = tipoServicio;
        this.precioBase = precioBase;
        this.duracionHoras = duracionHoras;
        this.capacidadMaxima = capacidadMaxima;
        this.ubicacion = ubicacion;
    }
    
    // Getters y Setters
    
    public int getIdServicio() {
        return idServicio;
    }
    
    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }
    
    public int getIdProveedor() {
        return idProveedor;
    }
    
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }
    
    public String getNombreServicio() {
        return nombreServicio;
    }
    
    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getTipoServicio() {
        return tipoServicio;
    }
    
    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }
    
    public double getPrecioBase() {
        return precioBase;
    }
    
    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }
    
    public double getDuracionHoras() {
        return duracionHoras;
    }
    
    public void setDuracionHoras(double duracionHoras) {
        this.duracionHoras = duracionHoras;
    }
    
    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }
    
    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    public String getRequisitos() {
        return requisitos;
    }
    
    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }
    
    public String getIncluye() {
        return incluye;
    }
    
    public void setIncluye(String incluye) {
        this.incluye = incluye;
    }
    
    public String getNoIncluye() {
        return noIncluye;
    }
    
    public void setNoIncluye(String noIncluye) {
        this.noIncluye = noIncluye;
    }
    
    public String getImagenes() {
        return imagenes;
    }
    
    public void setImagenes(String imagenes) {
        this.imagenes = imagenes;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public String getNombreProveedor() {
        return nombreProveedor;
    }
    
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }
    
    public String getContactoPrincipal() {
        return contactoPrincipal;
    }
    
    public void setContactoPrincipal(String contactoPrincipal) {
        this.contactoPrincipal = contactoPrincipal;
    }
    
    public String getTelefonoProveedor() {
        return telefonoProveedor;
    }
    
    public void setTelefonoProveedor(String telefonoProveedor) {
        this.telefonoProveedor = telefonoProveedor;
    }
    
    /**
     * Calcula el precio total para una cantidad de personas
     * @param cantidadPersonas Número de personas
     * @return Precio total
     */
    public double calcularPrecioTotal(int cantidadPersonas) {
        return this.precioBase * cantidadPersonas;
    }
    
    /**
     * Verifica si hay disponibilidad para una cantidad de personas
     * @param cantidadPersonas Número de personas
     * @return true si hay disponibilidad
     */
    public boolean verificarDisponibilidad(int cantidadPersonas) {
        return cantidadPersonas <= this.capacidadMaxima;
    }
    
    /**
     * Obtiene una descripción corta del servicio
     * @return Descripción corta (máximo 100 caracteres)
     */
    public String getDescripcionCorta() {
        if (descripcion == null) {
            return "";
        }
        return descripcion.length() > 100 ? descripcion.substring(0, 97) + "..." : descripcion;
    }
    
    /**
     * Verifica si el servicio está activo
     * @return true si está activo
     */
    public boolean estaActivo() {
        return "activo".equals(this.estado);
    }
    
    /**
     * Activa el servicio
     */
    public void activar() {
        this.estado = "activo";
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Desactiva el servicio
     */
    public void desactivar() {
        this.estado = "inactivo";
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Valida los datos del servicio
     * @return true si los datos son válidos
     */
    public boolean validar() {
        // Validar campos obligatorios
        if (nombreServicio == null || nombreServicio.trim().isEmpty()) {
            return false;
        }
        
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return false;
        }
        
        if (tipoServicio == null || tipoServicio.trim().isEmpty()) {
            return false;
        }
        
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            return false;
        }
        
        // Validar valores numéricos
        if (precioBase <= 0) {
            return false;
        }
        
        if (duracionHoras <= 0) {
            return false;
        }
        
        if (capacidadMaxima <= 0) {
            return false;
        }
        
        // Validar tipo de servicio
        if (!esTipoServicioValido(tipoServicio)) {
            return false;
        }
        
        // Validar estado
        if (!esEstadoValido(estado)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Verifica si el tipo de servicio es válido
     * @param tipoServicio Tipo de servicio a verificar
     * @return true si es válido
     */
    private boolean esTipoServicioValido(String tipoServicio) {
        return "tour".equals(tipoServicio) || 
               "hospedaje".equals(tipoServicio) || 
               "transporte".equals(tipoServicio) || 
               "alimentacion".equals(tipoServicio) || 
               "actividad".equals(tipoServicio);
    }
    
    /**
     * Verifica si el estado es válido
     * @param estado Estado a verificar
     * @return true si es válido
     */
    private boolean esEstadoValido(String estado) {
        return "activo".equals(estado) || 
               "inactivo".equals(estado) || 
               "temporada".equals(estado);
    }
    
    /**
     * Genera una representación en texto del servicio
     * @return Representación en texto
     */
    @Override
    public String toString() {
        return "ServicioTuristico{" +
               "idServicio=" + idServicio +
               ", nombreServicio='" + nombreServicio + '\'' +
               ", tipoServicio='" + tipoServicio + '\'' +
               ", precioBase=" + precioBase +
               ", capacidadMaxima=" + capacidadMaxima +
               ", ubicacion='" + ubicacion + '\'' +
               ", estado='" + estado + '\'' +
               '}';
    }
    
    /**
     * Compara dos servicios por ID
     * @param obj Objeto a comparar
     * @return true si son iguales
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        ServicioTuristico that = (ServicioTuristico) obj;
        return idServicio == that.idServicio;
    }
    
    /**
     * Genera hash code basado en el ID
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(idServicio);
    }
    
    /**
     * Crea una copia del servicio
     * @return Copia del servicio
     */
    public ServicioTuristico clonar() {
        ServicioTuristico clon = new ServicioTuristico();
        clon.idServicio = this.idServicio;
        clon.idProveedor = this.idProveedor;
        clon.nombreServicio = this.nombreServicio;
        clon.descripcion = this.descripcion;
        clon.tipoServicio = this.tipoServicio;
        clon.precioBase = this.precioBase;
        clon.duracionHoras = this.duracionHoras;
        clon.capacidadMaxima = this.capacidadMaxima;
        clon.ubicacion = this.ubicacion;
        clon.requisitos = this.requisitos;
        clon.incluye = this.incluye;
        clon.noIncluye = this.noIncluye;
        clon.imagenes = this.imagenes;
        clon.estado = this.estado;
        clon.fechaCreacion = this.fechaCreacion;
        clon.fechaActualizacion = this.fechaActualizacion;
        clon.nombreProveedor = this.nombreProveedor;
        clon.contactoPrincipal = this.contactoPrincipal;
        clon.telefonoProveedor = this.telefonoProveedor;
        
        return clon;
    }
    
    /**
     * Convierte el servicio a formato JSON (simplificado)
     * @return Representación JSON
     */
    public String toJson() {
        return "{" +
               "\"idServicio\":" + idServicio + "," +
               "\"nombreServicio\":\"" + nombreServicio + "\"," +
               "\"descripcion\":\"" + descripcion + "\"," +
               "\"tipoServicio\":\"" + tipoServicio + "\"," +
               "\"precioBase\":" + precioBase + "," +
               "\"duracionHoras\":" + duracionHoras + "," +
               "\"capacidadMaxima\":" + capacidadMaxima + "," +
               "\"ubicacion\":\"" + ubicacion + "\"," +
               "\"estado\":\"" + estado + "\"" +
               "}";
    }
}

package com.hermosacartagena.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * ENTIDAD ROL
 * =============================================
 * 
 * Esta entidad representa los roles del sistema.
 * Implementa GrantedAuthority para integración con Spring Security.
 * 
 * Roles disponibles:
 * - ADMIN: Acceso completo al sistema
 * - EMPLEADO: Gestión de reservas y pagos
 * - CLIENTE: Acceso a servicios y reservas personales
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol extends Auditoria implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "nombre_rol", length = 50, nullable = false, unique = true)
    private String nombreRol;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Usuario> usuarios;

    /**
     * Constructor con nombre de rol
     * 
     * @param nombreRol Nombre del rol
     */
    public Rol(String nombreRol) {
        this.nombreRol = nombreRol;
        this.estado = "activo";
    }

    /**
     * Constructor completo
     * 
     * @param nombreRol Nombre del rol
     * @param descripcion Descripción del rol
     */
    public Rol(String nombreRol, String descripcion) {
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
        this.estado = "activo";
    }

    /**
     * Implementación de GrantedAuthority para Spring Security
     * Retorna el nombre del rol con prefijo ROLE_
     * 
     * @return String con la autoridad del rol
     */
    @Override
    public String getAuthority() {
        return "ROLE_" + nombreRol.toUpperCase();
    }

    /**
     * Verifica si el rol está activo
     * 
     * @return true si el rol está activo
     */
    public boolean estaActivo() {
        return "activo".equals(this.estado);
    }

    /**
     * Activa el rol
     */
    public void activar() {
        this.estado = "activo";
    }

    /**
     * Desactiva el rol
     */
    public void desactivar() {
        this.estado = "inactivo";
    }

    /**
     * Obtiene la cantidad de usuarios asociados
     * 
     * @return Número de usuarios con este rol
     */
    public int getCantidadUsuarios() {
        return usuarios != null ? usuarios.size() : 0;
    }

    /**
     * Obtiene los nombres de usuarios asociados
     * 
     * @return Lista de nombres de usuarios
     */
    public List<String> getNombresUsuarios() {
        if (usuarios == null) {
            return List.of();
        }
        return usuarios.stream()
                .filter(u -> u.getUsuario() != null)
                .map(Usuario::getUsuario)
                .collect(Collectors.toList());
    }

    /**
     * Método toString personalizado
     * 
     * @return Representación en texto del rol
     */
    @Override
    public String toString() {
        return "Rol{" +
                "idRol=" + idRol +
                ", nombreRol='" + nombreRol + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}

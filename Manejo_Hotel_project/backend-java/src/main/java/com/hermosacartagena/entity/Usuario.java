package com.hermosacartagena.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * ENTIDAD USUARIO
 * =============================================
 * 
 * Esta entidad representa los usuarios del sistema.
 * Implementa UserDetails para integración con Spring Security.
 * 
 * Contiene información de autenticación y autorización:
 * - Credenciales de login
 * - Información personal
 * - Estado y fecha de último acceso
 * - Relación con rol y entidades específicas (cliente/empleado)
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_usuario_email", columnList = "email"),
    @Index(name = "idx_usuario_estado", columnList = "estado"),
    @Index(name = "idx_usuario_rol", columnList = "id_rol")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends Auditoria implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "id_rol", nullable = false)
    private Long idRol;

    @Column(name = "usuario", length = 100, nullable = false, unique = true)
    private String usuario;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "nombre_completo", length = 255, nullable = false)
    private String nombreCompleto;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion", length = 500)
    private String direccion;

    @Column(name = "fecha_ultimo_acceso")
    private LocalDateTime fechaUltimoAcceso;

    @Column(name = "intentos_fallidos", columnDefinition = "INT DEFAULT 0")
    private Integer intentosFallidos;

    @Column(name = "cuenta_bloqueada_hasta")
    private LocalDateTime cuentaBloqueadaHasta;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", insertable = false, updatable = false)
    private Rol rol;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cliente cliente;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Empleado empleado;

    /**
     * Constructor básico
     */
    public Usuario(String usuario, String email, String password, String nombreCompleto) {
        this.usuario = usuario;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.estado = "activo";
        this.intentosFallidos = 0;
    }

    // Implementación de UserDetails para Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = Set.of(
            new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol().toUpperCase())
        );
        
        // Agregar permisos específicos según el rol
        if ("ADMIN".equals(rol.getNombreRol())) {
            authorities.add(new SimpleGrantedAuthority("PERMISO_ADMIN"));
        }
        
        return authorities;
    }

    @Override
    public String getUsername() {
        return usuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // La cuenta está bloqueada si está bloqueada hasta una fecha futura
        if (cuentaBloqueadaHasta != null) {
            return cuentaBloqueadaHasta.isBefore(LocalDateTime.now());
        }
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "activo".equals(estado);
    }

    // Métodos de negocio

    /**
     * Verifica si el usuario está activo
     * 
     * @return true si el usuario está activo
     */
    public boolean estaActivo() {
        return "activo".equals(this.estado);
    }

    /**
     * Verifica si la cuenta está bloqueada
     * 
     * @return true si la cuenta está bloqueada
     */
    public boolean estaBloqueado() {
        if (cuentaBloqueadaHasta != null) {
            return cuentaBloqueadaHasta.isAfter(LocalDateTime.now());
        }
        return false;
    }

    /**
     * Incrementa el contador de intentos fallidos
     */
    public void incrementarIntentosFallidos() {
        this.intentosFallidos = (this.intentosFallidos == null) ? 1 : this.intentosFallidos + 1;
        
        // Bloquear cuenta después de 5 intentos fallidos por 30 minutos
        if (this.intentosFallidos >= 5) {
            this.cuentaBloqueadaHasta = LocalDateTime.now().plusMinutes(30);
        }
    }

    /**
     * Reinicia los intentos fallidos y desbloquea la cuenta
     */
    public void reiniciarIntentosFallidos() {
        this.intentosFallidos = 0;
        this.cuentaBloqueadaHasta = null;
        this.fechaUltimoAcceso = LocalDateTime.now();
    }

    /**
     * Activa el usuario
     */
    public void activar() {
        this.estado = "activo";
        this.reiniciarIntentosFallidos();
    }

    /**
     * Desactiva el usuario
     */
    public void desactivar() {
        this.estado = "inactivo";
    }

    /**
     * Bloquea el usuario por un tiempo específico
     * 
     * @param minutos Minutos de bloqueo
     */
    public void bloquearCuenta(int minutos) {
        this.cuentaBloqueadaHasta = LocalDateTime.now().plusMinutes(minutos);
    }

    /**
     * Verifica si el usuario es administrador
     * 
     * @return true si es administrador
     */
    public boolean esAdministrador() {
        return rol != null && "ADMIN".equals(rol.getNombreRol());
    }

    /**
     * Verifica si el usuario es empleado
     * 
     * @return true si es empleado
     */
    public boolean esEmpleado() {
        return rol != null && "EMPLEADO".equals(rol.getNombreRol());
    }

    /**
     * Verifica si el usuario es cliente
     * 
     * @return true si es cliente
     */
    public boolean esCliente() {
        return rol != null && "CLIENTE".equals(rol.getNombreRol());
    }

    /**
     * Obtiene el perfil del usuario (cliente/empleado)
     * 
     * @return Objeto del perfil específico
     */
    public Object getPerfil() {
        if (cliente != null) {
            return cliente;
        } else if (empleado != null) {
            return empleado;
        }
        return null;
    }

    /**
     * Obtiene información resumida del usuario
     * 
     * @return String con información básica
     */
    public String getInformacionResumida() {
        return String.format("%s (%s) - %s", 
                nombreCompleto, 
                usuario, 
                rol != null ? rol.getNombreRol() : "Sin rol");
    }

    /**
     * Método toString personalizado (sin password por seguridad)
     * 
     * @return Representación en texto del usuario
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", usuario='" + usuario + '\'' +
                ", email='" + email + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", estado='" + estado + '\'' +
                ", rol=" + (rol != null ? rol.getNombreRol() : "null") +
                '}';
    }
}

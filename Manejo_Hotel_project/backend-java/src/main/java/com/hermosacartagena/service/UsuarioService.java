package com.hermosacartagena.service;

import com.hermosacartagena.dto.UsuarioDTO;
import com.hermosacartagena.entity.Usuario;
import com.hermosacartagena.entity.Rol;
import com.hermosacartagena.repository.UsuarioRepository;
import com.hermosacartagena.repository.RolRepository;
import com.hermosacartagena.exception.ResourceNotFoundException;
import com.hermosacartagena.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * SERVICIO USUARIO
 * =============================================
 * 
 * Servicio de negocio para gestión de usuarios.
 * Implementa toda la lógica de negocio relacionada con usuarios.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea un nuevo usuario
     * 
     * @param usuarioDTO DTO con datos del usuario
     * @return UsuarioDTO creado
     * @throws BusinessException Si hay errores de negocio
     */
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        log.info("Creando nuevo usuario: {}", usuarioDTO.getUsuario());

        // Validaciones de negocio
        validarDatosUsuario(usuarioDTO, true);

        // Verificar si ya existe el usuario o email
        if (usuarioRepository.existsByUsuarioOrEmail(usuarioDTO.getUsuario())) {
            throw new BusinessException("El nombre de usuario o email ya está en uso");
        }

        // Obtener rol
        Rol rol = rolRepository.findById(usuarioDTO.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + usuarioDTO.getIdRol()));

        if (!rol.estaActivo()) {
            throw new BusinessException("El rol especificado no está activo");
        }

        // Crear entidad
        Usuario usuario = Usuario.builder()
                .usuario(usuarioDTO.getUsuario())
                .email(usuarioDTO.getEmail())
                .password(passwordEncoder.encode(usuarioDTO.getPassword()))
                .nombreCompleto(usuarioDTO.getNombreCompleto())
                .telefono(usuarioDTO.getTelefono())
                .direccion(usuarioDTO.getDireccion())
                .idRol(usuarioDTO.getIdRol())
                .estado("activo")
                .intentosFallidos(0)
                .build();

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getIdUsuario());

        return convertirADTO(usuarioGuardado);
    }

    /**
     * Actualiza un usuario existente
     * 
     * @param id ID del usuario a actualizar
     * @param usuarioDTO DTO con datos actualizados
     * @return UsuarioDTO actualizado
     * @throws ResourceNotFoundException Si no existe el usuario
     * @throws BusinessException Si hay errores de negocio
     */
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Validaciones de negocio
        validarDatosUsuario(usuarioDTO, false);

        // Verificar si el email o usuario ya está en uso por otro usuario
        if (usuarioRepository.emailEnUsoPorOtroUsuario(usuarioDTO.getEmail(), id)) {
            throw new BusinessException("El email ya está en uso por otro usuario");
        }

        // Verificar rol
        if (!usuarioDTO.getIdRol().equals(usuarioExistente.getIdRol())) {
            Rol rol = rolRepository.findById(usuarioDTO.getIdRol())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + usuarioDTO.getIdRol()));
            
            if (!rol.estaActivo()) {
                throw new BusinessException("El rol especificado no está activo");
            }
        }

        // Actualizar campos
        usuarioExistente.setUsuario(usuarioDTO.getUsuario());
        usuarioExistente.setEmail(usuarioDTO.getEmail());
        usuarioExistente.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuarioExistente.setTelefono(usuarioDTO.getTelefono());
        usuarioExistente.setDireccion(usuarioDTO.getDireccion());
        usuarioExistente.setIdRol(usuarioDTO.getIdRol());

        // Actualizar contraseña si se proporcionó
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
        log.info("Usuario actualizado exitosamente");

        return convertirADTO(usuarioActualizado);
    }

    /**
     * Obtiene un usuario por su ID
     * 
     * @param id ID del usuario
     * @return UsuarioDTO encontrado
     * @throws ResourceNotFoundException Si no existe el usuario
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        return convertirADTO(usuario);
    }

    /**
     * Obtiene un usuario por nombre de usuario o email
     * 
     * @param usuarioOEmail Nombre de usuario o email
     * @return UsuarioDTO encontrado
     * @throws ResourceNotFoundException Si no existe el usuario
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorUsuarioOEmail(String usuarioOEmail) {
        Usuario usuario = usuarioRepository.findByUsuarioOrEmail(usuarioOEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioOEmail));
        
        return convertirADTO(usuario);
    }

    /**
     * Lista todos los usuarios paginados
     * 
     * @param pageable Configuración de paginación
     * @return Página de usuarios
     */
    @Transactional(readOnly = true)
    public Page<UsuarioDTO> listarTodosLosUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    /**
     * Busca usuarios por texto
     * 
     * @param texto Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de usuarios que coinciden
     */
    @Transactional(readOnly = true)
    public Page<UsuarioDTO> buscarUsuarios(String texto, Pageable pageable) {
        return usuarioRepository.buscarPorTexto(texto, pageable)
                .map(this::convertirADTO);
    }

    /**
     * Lista usuarios por rol
     * 
     * @param idRol ID del rol
     * @return Lista de usuarios del rol
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuariosPorRol(Long idRol) {
        return usuarioRepository.findByIdRol(idRol).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista usuarios activos
     * 
     * @return Lista de usuarios activos
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuariosActivos() {
        return usuarioRepository.findActivosOrdenadosPorNombre().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Cambia el estado de un usuario
     * 
     * @param id ID del usuario
     * @param nuevoEstado Nuevo estado
     * @return UsuarioDTO actualizado
     */
    public UsuarioDTO cambiarEstado(Long id, String nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        if (!"activo".equals(nuevoEstado) && !"inactivo".equals(nuevoEstado)) {
            throw new BusinessException("Estado no válido. Debe ser 'activo' o 'inactivo'");
        }

        usuarioRepository.cambiarEstado(id, nuevoEstado);
        log.info("Estado del usuario {} cambiado a: {}", id, nuevoEstado);

        // Recargar para obtener datos actualizados
        Usuario usuarioActualizado = usuarioRepository.findById(id).orElse(usuario);
        return convertirADTO(usuarioActualizado);
    }

    
    /**
     * Bloquea cuenta de usuario
     * 
     * @param id ID del usuario
     * @param minutos Minutos de bloqueo
     */
    public void bloquearCuenta(Long id, int minutos) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }

        LocalDateTime fechaBloqueo = LocalDateTime.now().plusMinutes(minutos);
        usuarioRepository.bloquearCuentaHasta(id, fechaBloqueo);
        log.info("Cuenta del usuario {} bloqueada por {} minutos", id, minutos);
    }

    /**
     * Actualiza último acceso de usuario
     * 
     * @param id ID del usuario
     */
    public void actualizarUltimoAcceso(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }

        usuarioRepository.actualizarUltimoAcceso(id, LocalDateTime.now());
    }

    /**
     * Elimina un usuario (eliminación lógica)
     * 
     * @param id ID del usuario
     * @throws ResourceNotFoundException Si no existe el usuario
     */
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Eliminación lógica - cambiar estado a inactivo
        usuarioRepository.cambiarEstado(id, "inactivo");
        log.info("Usuario eliminado lógicamente (inactivado) con ID: {}", id);
    }

    /**
     * Obtiene estadísticas de usuarios
     * 
     * @return Array con estadísticas
     */
    @Transactional(readOnly = true)
    public Object[] obtenerEstadisticas() {
        return usuarioRepository.obtenerEstadisticasGenerales();
    }

    /**
     * Incrementa el contador de intentos fallidos de login
     * 
     * @param idUsuario ID del usuario
     */
    @Transactional
    public void incrementarIntentosFallidos(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", idUsuario));
        
        int intentosActuales = usuario.getIntentosFallidos() != null ? usuario.getIntentosFallidos() : 0;
        usuario.setIntentosFallidos(intentosActuales + 1);
        
        // Bloquear cuenta después de 5 intentos fallidos
        if (usuario.getIntentosFallidos() >= 5) {
            usuario.setEstado("bloqueado");
            log.warn("Usuario bloqueado por exceder intentos fallidos: {}", idUsuario);
        }
        
        usuarioRepository.save(usuario);
        log.info("Incrementados intentos fallidos para usuario {}: {}", idUsuario, usuario.getIntentosFallidos());
    }

    /**
     * Reinicia intentos fallidos del usuario (usado en login exitoso)
     * 
     * @param idUsuario ID del usuario
     */
    @Transactional
    public void reiniciarIntentosFallidos(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", idUsuario));
        
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
        log.debug("Reiniciados intentos fallidos para usuario: {}", idUsuario);
    }

    /**
     * Valida datos del usuario
     * 
     * @param usuarioDTO DTO a validar
     * @param esCreacion Si es para creación
     * @throws BusinessException Si hay errores de validación
     */
    private void validarDatosUsuario(UsuarioDTO usuarioDTO, boolean esCreacion) {
        if (esCreacion && (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().trim().isEmpty())) {
            throw new BusinessException("La contraseña es obligatoria para crear un usuario");
        }

        if (!usuarioDTO.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            throw new BusinessException("La contraseña debe tener al menos 6 caracteres, una mayúscula, una minúscula y un número");
        }
    }

    /**
     * Convierte entidad a DTO
     * 
     * @param usuario Entidad Usuario
     * @return UsuarioDTO
     */
    private UsuarioDTO convertirADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .usuario(usuario.getUsuario())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .idRol(usuario.getIdRol())
                .nombreRol(usuario.getRol() != null ? usuario.getRol().getNombreRol() : null)
                .fechaUltimoAcceso(usuario.getFechaUltimoAcceso())
                .intentosFallidos(usuario.getIntentosFallidos())
                .cuentaBloqueadaHasta(usuario.getCuentaBloqueadaHasta())
                .estado(usuario.getEstado())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .createdBy(usuario.getCreatedBy())
                .updatedBy(usuario.getUpdatedBy())
                .build();
    }
}

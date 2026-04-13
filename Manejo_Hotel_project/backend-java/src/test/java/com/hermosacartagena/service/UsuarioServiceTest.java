package com.hermosacartagena.service;

import com.hermosacartagena.dto.UsuarioDTO;
import com.hermosacartagena.entity.Usuario;
import com.hermosacartagena.entity.Rol;
import com.hermosacartagena.repository.UsuarioRepository;
import com.hermosacartagena.repository.RolRepository;
import com.hermosacartagena.exception.ResourceNotFoundException;
import com.hermosacartagena.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * PRUEBAS UNITARIAS - USUARIO SERVICE
 * =============================================
 * 
 * Tests unitarios para la clase UsuarioService.
 * Verifica la lógica de negocio y manejo de errores.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioTest;
    private Rol rolTest;
    private UsuarioDTO usuarioDTOTest;

    @BeforeEach
    void setUp() {
        // Setup de datos de prueba
        rolTest = Rol.builder()
                .idRol(1L)
                .nombreRol("ADMIN")
                .descripcion("Administrador del sistema")
                .estado("activo")
                .build();

        usuarioTest = Usuario.builder()
                .idUsuario(1L)
                .usuario("admin")
                .email("admin@test.com")
                .password("encodedPassword")
                .nombreCompleto("Administrador Test")
                .telefono("123456789")
                .direccion("Dirección Test")
                .idRol(1L)
                .rol(rolTest)
                .estado("activo")
                .intentosFallidos(0)
                .createdAt(LocalDateTime.now())
                .build();

        usuarioDTOTest = UsuarioDTO.builder()
                .usuario("admin")
                .email("admin@test.com")
                .password("password123")
                .nombreCompleto("Administrador Test")
                .telefono("123456789")
                .direccion("Dirección Test")
                .idRol(1L)
                .build();
    }

    @Test
    void testCrearUsuario_Exitoso() throws Exception {
        // Given
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolTest));
        when(usuarioRepository.existsByUsuarioOrEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);

        // When
        UsuarioDTO resultado = usuarioService.crearUsuario(usuarioDTOTest);

        // Then
        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsuario());
        assertEquals("admin@test.com", resultado.getEmail());
        assertEquals("Administrador Test", resultado.getNombreCompleto());
        assertEquals(1L, resultado.getIdRol());

        verify(rolRepository).findById(1L);
        verify(usuarioRepository).existsByUsuarioOrEmail("admin");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuario_EmailYaExiste_LanzaException() {
        // Given
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolTest));
        when(usuarioRepository.existsByUsuarioOrEmail(anyString())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> usuarioService.crearUsuario(usuarioDTOTest));

        assertEquals("El nombre de usuario o email ya está en uso", exception.getMessage());

        verify(rolRepository).findById(1L);
        verify(usuarioRepository).existsByUsuarioOrEmail("admin");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuario_RolNoExiste_LanzaException() {
        // Given
        when(rolRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> usuarioService.crearUsuario(usuarioDTOTest));

        assertEquals("Rol no encontrado con ID: 1", exception.getMessage());

        verify(rolRepository).findById(1L);
        verify(usuarioRepository, never()).existsByUsuarioOrEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuario_RolInactivo_LanzaException() {
        // Given
        rolTest.setEstado("inactivo");
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolTest));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> usuarioService.crearUsuario(usuarioDTOTest));

        assertEquals("El rol especificado no está activo", exception.getMessage());

        verify(rolRepository).findById(1L);
        verify(usuarioRepository, never()).existsByUsuarioOrEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_Exitoso() throws Exception {
        // Given
        UsuarioDTO updateDTO = UsuarioDTO.builder()
                .usuario("adminUpdated")
                .email("adminUpdated@test.com")
                .nombreCompleto("Admin Updated")
                .telefono("987654321")
                .direccion("New Address")
                .idRol(1L)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolTest));
        when(usuarioRepository.emailEnUsoPorOtroUsuario(anyString(), eq(1L))).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);

        // When
        UsuarioDTO resultado = usuarioService.actualizarUsuario(1L, updateDTO);

        // Then
        assertNotNull(resultado);
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_NoExiste_LanzaException() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> usuarioService.actualizarUsuario(1L, usuarioDTOTest));

        assertEquals("Usuario no encontrado con ID: 1", exception.getMessage());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testObtenerUsuarioPorId_Exitoso() throws Exception {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));

        // When
        UsuarioDTO resultado = usuarioService.obtenerUsuarioPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsuario());
        assertEquals("admin@test.com", resultado.getEmail());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void testObtenerUsuarioPorId_NoExiste_LanzaException() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> usuarioService.obtenerUsuarioPorId(1L));

        assertEquals("Usuario no encontrado con ID: 1", exception.getMessage());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void testObtenerUsuarioPorUsuarioOEmail_Exitoso() throws Exception {
        // Given
        when(usuarioRepository.findByUsuarioOrEmail("admin")).thenReturn(Optional.of(usuarioTest));

        // When
        UsuarioDTO resultado = usuarioService.obtenerUsuarioPorUsuarioOEmail("admin");

        // Then
        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsuario());

        verify(usuarioRepository).findByUsuarioOrEmail("admin");
    }

    @Test
    void testObtenerUsuarioPorUsuarioOEmail_NoExiste_LanzaException() {
        // Given
        when(usuarioRepository.findByUsuarioOrEmail("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> usuarioService.obtenerUsuarioPorUsuarioOEmail("nonexistent"));

        assertEquals("Usuario no encontrado: nonexistent", exception.getMessage());

        verify(usuarioRepository).findByUsuarioOrEmail("nonexistent");
    }

    @Test
    void testListarTodosLosUsuarios_Exitoso() {
        // Given
        List<Usuario> usuarios = Arrays.asList(usuarioTest);
        Page<Usuario> pageUsuarios = new PageImpl<>(usuarios);
        Pageable pageable = PageRequest.of(0, 10);

        when(usuarioRepository.findAll(pageable)).thenReturn(pageUsuarios);

        // When
        Page<UsuarioDTO> resultado = usuarioService.listarTodosLosUsuarios(pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("admin", resultado.getContent().get(0).getUsuario());

        verify(usuarioRepository).findAll(pageable);
    }

    @Test
    void testBuscarUsuarios_Exitoso() {
        // Given
        List<Usuario> usuarios = Arrays.asList(usuarioTest);
        Page<Usuario> pageUsuarios = new PageImpl<>(usuarios);
        Pageable pageable = PageRequest.of(0, 10);

        when(usuarioRepository.buscarPorTexto("admin", pageable)).thenReturn(pageUsuarios);

        // When
        Page<UsuarioDTO> resultado = usuarioService.buscarUsuarios("admin", pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

        verify(usuarioRepository).buscarPorTexto("admin", pageable);
    }

    @Test
    void testListarUsuariosPorRol_Exitoso() {
        // Given
        List<Usuario> usuarios = Arrays.asList(usuarioTest);
        when(usuarioRepository.findByIdRol(1L)).thenReturn(usuarios);

        // When
        List<UsuarioDTO> resultado = usuarioService.listarUsuariosPorRol(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("admin", resultado.get(0).getUsuario());

        verify(usuarioRepository).findByIdRol(1L);
    }

    @Test
    void testListarUsuariosActivos_Exitoso() {
        // Given
        List<Usuario> usuarios = Arrays.asList(usuarioTest);
        when(usuarioRepository.findActivosOrdenadosPorNombre()).thenReturn(usuarios);

        // When
        List<UsuarioDTO> resultado = usuarioService.listarUsuariosActivos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("admin", resultado.get(0).getUsuario());

        verify(usuarioRepository).findActivosOrdenadosPorNombre();
    }

    @Test
    void testCambiarEstado_Exitoso() throws Exception {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));

        // When
        UsuarioDTO resultado = usuarioService.cambiarEstado(1L, "inactivo");

        // Then
        assertNotNull(resultado);
        verify(usuarioRepository, times(2)).findById(1L);
        verify(usuarioRepository).cambiarEstado(1L, "inactivo");
    }

    @Test
    void testCambiarEstado_NoExiste_LanzaException() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> usuarioService.cambiarEstado(1L, "inactivo"));

        assertEquals("Usuario no encontrado con ID: 1", exception.getMessage());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository, never()).cambiarEstado(anyLong(), anyString());
    }

    @Test
    void testCambiarEstado_EstadoInvalido_LanzaException() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
                () -> usuarioService.cambiarEstado(1L, "invalid"));

        assertEquals("Estado no válido. Debe ser 'activo' o 'inactivo'", exception.getMessage());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository, never()).cambiarEstado(anyLong(), anyString());
    }

    @Test
    void testReiniciarIntentosFallidos_Exitoso() throws Exception {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // When
        usuarioService.reiniciarIntentosFallidos(1L);

        // Then
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).reiniciarIntentosFallidos(1L);
    }

    @Test
    void testReiniciarIntentosFallidos_NoExiste_LanzaException() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> usuarioService.reiniciarIntentosFallidos(1L));

        assertEquals("Usuario no encontrado con ID: 1", exception.getMessage());

        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository, never()).reiniciarIntentosFallidos(anyLong());
    }

    @Test
    void testBloquearCuenta_Exitoso() throws Exception {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // When
        usuarioService.bloquearCuenta(1L, 30);

        // Then
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).bloquearCuentaHasta(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void testBloquearCuenta_NoExiste_LanzaException() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> usuarioService.bloquearCuenta(1L, 30));

        assertEquals("Usuario no encontrado con ID: 1", exception.getMessage());

        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository, never()).bloquearCuentaHasta(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void testActualizarUltimoAcceso_Exitoso() throws Exception {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // When
        usuarioService.actualizarUltimoAcceso(1L);

        // Then
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).actualizarUltimoAcceso(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void testActualizarUltimoAcceso_NoExiste_LanzaException() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> usuarioService.actualizarUltimoAcceso(1L));

        assertEquals("Usuario no encontrado con ID: 1", exception.getMessage());

        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository, never()).actualizarUltimoAcceso(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void testEliminarUsuario_Exitoso() throws Exception {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));

        // When
        usuarioService.eliminarUsuario(1L);

        // Then
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).cambiarEstado(1L, "inactivo");
    }

    @Test
    void testEliminarUsuario_NoExiste_LanzaException() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> usuarioService.eliminarUsuario(1L));

        assertEquals("Usuario no encontrado con ID: 1", exception.getMessage());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository, never()).cambiarEstado(anyLong(), anyString());
    }

    @Test
    void testObtenerEstadisticas_Exitoso() {
        // Given
        Object[] estadisticas = {100L, 80L, 20L, 5L, 1500.50, 4.5};
        when(usuarioRepository.obtenerEstadisticasGenerales()).thenReturn(estadisticas);

        // When
        Object[] resultado = usuarioService.obtenerEstadisticas();

        // Then
        assertNotNull(resultado);
        assertEquals(100L, resultado[0]);
        assertEquals(80L, resultado[1]);

        verify(usuarioRepository).obtenerEstadisticasGenerales();
    }
}

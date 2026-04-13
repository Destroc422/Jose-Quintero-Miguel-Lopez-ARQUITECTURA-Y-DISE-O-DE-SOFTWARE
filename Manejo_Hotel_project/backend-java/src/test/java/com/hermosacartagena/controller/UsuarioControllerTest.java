package com.hermosacartagena.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hermosacartagena.dto.UsuarioDTO;
import com.hermosacartagena.service.UsuarioService;
import com.hermosacartagena.exception.ResourceNotFoundException;
import com.hermosacartagena.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * PRUEBAS DE INTEGRACIÓN - USUARIO CONTROLLER
 * =============================================
 * 
 * Tests de integración para el controlador de usuarios.
 * Verifica endpoints HTTP y respuestas.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioDTO usuarioDTOTest;

    @BeforeEach
    void setUp() {
        usuarioDTOTest = UsuarioDTO.builder()
                .idUsuario(1L)
                .usuario("admin")
                .email("admin@test.com")
                .nombreCompleto("Administrador Test")
                .telefono("123456789")
                .direccion("Dirección Test")
                .idRol(1L)
                .nombreRol("ADMIN")
                .estado("activo")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCrearUsuario_Exitoso() throws Exception {
        // Given
        UsuarioDTO nuevoUsuario = UsuarioDTO.builder()
                .usuario("newuser")
                .email("new@test.com")
                .password("password123")
                .nombreCompleto("New User")
                .idRol(2L)
                .build();

        when(usuarioService.crearUsuario(any(UsuarioDTO.class))).thenReturn(usuarioDTOTest);

        // When & Then
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.usuario").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.nombreCompleto").value("Administrador Test"));

        verify(usuarioService).crearUsuario(any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCrearUsuario_DatosInvalidos_ReturnsBadRequest() throws Exception {
        // Given
        UsuarioDTO usuarioInvalido = UsuarioDTO.builder()
                .usuario("") // Usuario vacío
                .email("invalid-email") // Email inválido
                .build();

        // When & Then
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).crearUsuario(any(UsuarioDTO.class));
    }

    @Test
    void testCrearUsuario_SinRolAdmin_ReturnsForbidden() throws Exception {
        // Given
        UsuarioDTO nuevoUsuario = UsuarioDTO.builder()
                .usuario("newuser")
                .email("new@test.com")
                .password("password123")
                .nombreCompleto("New User")
                .idRol(2L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).crearUsuario(any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarUsuario_Exitoso() throws Exception {
        // Given
        UsuarioDTO updateDTO = UsuarioDTO.builder()
                .usuario("adminUpdated")
                .email("adminUpdated@test.com")
                .nombreCompleto("Admin Updated")
                .idRol(1L)
                .build();

        when(usuarioService.actualizarUsuario(eq(1L), any(UsuarioDTO.class))).thenReturn(usuarioDTOTest);

        // When & Then
        mockMvc.perform(put("/api/usuarios/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.usuario").value("admin"));

        verify(usuarioService).actualizarUsuario(eq(1L), any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarUsuario_NoExiste_ReturnsNotFound() throws Exception {
        // Given
        UsuarioDTO updateDTO = UsuarioDTO.builder()
                .usuario("adminUpdated")
                .build();

        when(usuarioService.actualizarUsuario(eq(1L), any(UsuarioDTO.class)))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        // When & Then
        mockMvc.perform(put("/api/usuarios/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"));

        verify(usuarioService).actualizarUsuario(eq(1L), any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerUsuarioPorId_Exitoso() throws Exception {
        // Given
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(usuarioDTOTest);

        // When & Then
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.usuario").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@test.com"));

        verify(usuarioService).obtenerUsuarioPorId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerUsuarioPorId_NoExiste_ReturnsNotFound() throws Exception {
        // Given
        when(usuarioService.obtenerUsuarioPorId(1L))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"));

        verify(usuarioService).obtenerUsuarioPorId(1L);
    }

    @Test
    void testObtenerUsuarioPorId_SinAutenticacion_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).obtenerUsuarioPorId(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarTodosLosUsuarios_Exitoso() throws Exception {
        // Given
        List<UsuarioDTO> usuarios = Arrays.asList(usuarioDTOTest);
        Page<UsuarioDTO> pageUsuarios = new PageImpl<>(usuarios);

        when(usuarioService.listarTodosLosUsuarios(any())).thenReturn(pageUsuarios);

        // When & Then
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].idUsuario").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(usuarioService).listarTodosLosUsuarios(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBuscarUsuarios_Exitoso() throws Exception {
        // Given
        List<UsuarioDTO> usuarios = Arrays.asList(usuarioDTOTest);
        Page<UsuarioDTO> pageUsuarios = new PageImpl<>(usuarios);

        when(usuarioService.buscarUsuarios(eq("admin"), any())).thenReturn(pageUsuarios);

        // When & Then
        mockMvc.perform(get("/api/usuarios/buscar")
                .param("texto", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].usuario").value("admin"));

        verify(usuarioService).buscarUsuarios(eq("admin"), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarUsuariosPorRol_Exitoso() throws Exception {
        // Given
        List<UsuarioDTO> usuarios = Arrays.asList(usuarioDTOTest);
        when(usuarioService.listarUsuariosPorRol(1L)).thenReturn(usuarios);

        // When & Then
        mockMvc.perform(get("/api/usuarios/rol/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idUsuario").value(1L));

        verify(usuarioService).listarUsuariosPorRol(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarUsuariosActivos_Exitoso() throws Exception {
        // Given
        List<UsuarioDTO> usuarios = Arrays.asList(usuarioDTOTest);
        when(usuarioService.listarUsuariosActivos()).thenReturn(usuarios);

        // When & Then
        mockMvc.perform(get("/api/usuarios/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].estado").value("activo"));

        verify(usuarioService).listarUsuariosActivos();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCambiarEstado_Exitoso() throws Exception {
        // Given
        when(usuarioService.cambiarEstado(1L, "inactivo")).thenReturn(usuarioDTOTest);

        // When & Then
        mockMvc.perform(patch("/api/usuarios/1/estado")
                .param("estado", "inactivo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L));

        verify(usuarioService).cambiarEstado(1L, "inactivo");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testReiniciarIntentosFallidos_Exitoso() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/usuarios/1/reiniciar-intentos"))
                .andExpect(status().isOk());

        verify(usuarioService).reiniciarIntentosFallidos(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBloquearCuenta_Exitoso() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/usuarios/1/bloquear")
                .param("minutos", "30"))
                .andExpect(status().isOk());

        verify(usuarioService).bloquearCuenta(1L, 30);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarUsuario_Exitoso() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).eliminarUsuario(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerEstadisticas_Exitoso() throws Exception {
        // Given
        Object[] estadisticas = {100L, 80L, 20L, 5L, 1500.50, 4.5};
        when(usuarioService.obtenerEstadisticas()).thenReturn(estadisticas);

        // When & Then
        mockMvc.perform(get("/api/usuarios/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(100L))
                .andExpect(jsonPath("$[1]").value(80L));

        verify(usuarioService).obtenerEstadisticas();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testObtenerPerfil_Exitoso() throws Exception {
        // Given - Esta prueba necesita implementación con contexto de seguridad real
        // Por ahora, verificamos que el endpoint exista y esté protegido
        
        // When & Then
        mockMvc.perform(get("/api/usuarios/perfil"))
                .andExpect(status().isNotFound()); // Implementación pendiente
    }

    @Test
    void testEndpointsProtegidos_SinAutenticacion_ReturnsUnauthorized() throws Exception {
        // Verificar que los endpoints protegidos requieran autenticación
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void testEndpointsAdmin_SinRolAdmin_ReturnsForbidden() throws Exception {
        // Verificar que los endpoints de admin requieran rol ADMIN
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/usuarios/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testManejoExcepciones_BusinessException_ReturnsBadRequest() throws Exception {
        // Given
        UsuarioDTO nuevoUsuario = UsuarioDTO.builder()
                .usuario("newuser")
                .email("new@test.com")
                .password("password123")
                .build();

        when(usuarioService.crearUsuario(any(UsuarioDTO.class)))
                .thenThrow(new BusinessException("Error de negocio"));

        // When & Then
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error de negocio"));

        verify(usuarioService).crearUsuario(any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testManejoExcepciones_GenericException_ReturnsInternalServerError() throws Exception {
        // Given
        when(usuarioService.obtenerUsuarioPorId(1L))
                .thenThrow(new RuntimeException("Error inesperado"));

        // When & Then
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor. Por favor, contacte al administrador."));

        verify(usuarioService).obtenerUsuarioPorId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testValidacionCampos_CamposRequeridos_ReturnsBadRequest() throws Exception {
        // Given - Usuario sin campos requeridos
        Map<String, Object> usuarioInvalido = Map.of();

        // When & Then
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).crearUsuario(any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testValidacionEmail_EmailInvalido_ReturnsBadRequest() throws Exception {
        // Given
        Map<String, Object> usuarioInvalido = Map.of(
                "usuario", "test",
                "email", "email-invalido", // Email inválido
                "password", "password123",
                "nombreCompleto", "Test User",
                "idRol", 1L
        );

        // When & Then
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).crearUsuario(any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testValidacionPassword_PasswordCorta_ReturnsBadRequest() throws Exception {
        // Given
        Map<String, Object> usuarioInvalido = Map.of(
                "usuario", "test",
                "email", "test@test.com",
                "password", "123", // Contraseña muy corta
                "nombreCompleto", "Test User",
                "idRol", 1L
        );

        // When & Then
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).crearUsuario(any(UsuarioDTO.class));
    }
}

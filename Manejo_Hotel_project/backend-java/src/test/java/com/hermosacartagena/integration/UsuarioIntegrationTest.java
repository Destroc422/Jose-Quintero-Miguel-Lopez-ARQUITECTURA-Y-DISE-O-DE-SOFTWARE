package com.hermosacartagena.integration;

import com.hermosacartagena.dto.UsuarioDTO;
import com.hermosacartagena.entity.Usuario;
import com.hermosacartagena.entity.Rol;
import com.hermosacartagena.repository.UsuarioRepository;
import com.hermosacartagena.repository.RolRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * PRUEBAS DE INTEGRACIÓN - USUARIO
 * =============================================
 * 
 * Tests de integración completos para el módulo de usuarios.
 * Verifica la interacción entre todas las capas.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UsuarioIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Rol rolAdmin;
    private Rol rolCliente;
    private Usuario usuarioAdmin;

    @BeforeEach
    void setUp() {
        // Configurar MockMvc con seguridad
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Crear roles de prueba
        rolAdmin = Rol.builder()
                .nombreRol("ADMIN")
                .descripcion("Administrador del sistema")
                .estado("activo")
                .build();
        rolAdmin = rolRepository.save(rolAdmin);

        rolCliente = Rol.builder()
                .nombreRol("CLIENTE")
                .descripcion("Cliente del sistema")
                .estado("activo")
                .build();
        rolCliente = rolRepository.save(rolCliente);

        // Crear usuario administrador de prueba
        usuarioAdmin = Usuario.builder()
                .usuario("admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .nombreCompleto("Administrador Test")
                .telefono("123456789")
                .direccion("Dirección Test")
                .idRol(rolAdmin.getIdRol())
                .estado("activo")
                .intentosFallidos(0)
                .build();
        usuarioAdmin = usuarioRepository.save(usuarioAdmin);
    }

    @Test
    void testFlujoCompletoUsuario_CreacionActualizacionEliminacion() throws Exception {
        // 1. Crear nuevo usuario
        UsuarioDTO nuevoUsuario = UsuarioDTO.builder()
                .usuario("newuser")
                .email("newuser@test.com")
                .password("password123")
                .nombreCompleto("New User")
                .telefono("987654321")
                .direccion("New Address")
                .idRol(rolCliente.getIdRol())
                .build();

        String responseContent = mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.nombreCompleto").value("New User"))
                .andExpect(jsonPath("$.estado").value("activo"))
                .andReturn().getResponse().getContentAsString();

        // Extraer ID del usuario creado
        Long usuarioId = objectMapper.readTree(responseContent).get("idUsuario").asLong();

        // 2. Obtener usuario por ID
        mockMvc.perform(get("/api/usuarios/" + usuarioId)
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(usuarioId))
                .andExpect(jsonPath("$.usuario").value("newuser"));

        // 3. Actualizar usuario
        UsuarioDTO updateDTO = UsuarioDTO.builder()
                .usuario("newuserUpdated")
                .email("newuserupdated@test.com")
                .nombreCompleto("New User Updated")
                .telefono("111111111")
                .direccion("Updated Address")
                .idRol(rolCliente.getIdRol())
                .build();

        mockMvc.perform(put("/api/usuarios/" + usuarioId)
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("newuserUpdated"))
                .andExpect(jsonPath("$.email").value("newuserupdated@test.com"));

        // 4. Cambiar estado a inactivo
        mockMvc.perform(patch("/api/usuarios/" + usuarioId + "/estado")
                .param("estado", "inactivo")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("inactivo"));

        // 5. Eliminar usuario (eliminación lógica)
        mockMvc.perform(delete("/api/usuarios/" + usuarioId)
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isNoContent());

        // 6. Verificar que el usuario está inactivo
        mockMvc.perform(get("/api/usuarios/" + usuarioId)
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("inactivo"));
    }

    @Test
    void testListadoUsuarios_PaginacionYFiltros() throws Exception {
        // Crear usuarios adicionales para pruebas
        for (int i = 1; i <= 5; i++) {
            Usuario usuario = Usuario.builder()
                    .usuario("user" + i)
                    .email("user" + i + "@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .nombreCompleto("User " + i)
                    .idRol(rolCliente.getIdRol())
                    .estado("activo")
                    .build();
            usuarioRepository.save(usuario);
        }

        // 1. Listar todos los usuarios
        mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(6)) // 5 nuevos + 1 admin
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        // 2. Listar con paginación personalizada
        mockMvc.perform(get("/api/usuarios")
                .param("page", "0")
                .param("size", "3")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").value(3));

        // 3. Buscar usuarios por texto
        mockMvc.perform(get("/api/usuarios/buscar")
                .param("texto", "user")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(5)); // Solo los "userX"

        // 4. Listar usuarios por rol
        mockMvc.perform(get("/api/usuarios/rol/" + rolCliente.getIdRol())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.nombreRol == 'CLIENTE')]")).isNotEmpty();

        // 5. Listar usuarios activos
        mockMvc.perform(get("/api/usuarios/activos")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.estado == 'activo')]").isNotEmpty());
    }

    @Test
    void testOperacionesAdministrativas() throws Exception {
        // Obtener un usuario para pruebas
        Usuario usuarioTest = Usuario.builder()
                .usuario("testuser")
                .email("test@test.com")
                .password(passwordEncoder.encode("password123"))
                .nombreCompleto("Test User")
                .idRol(rolCliente.getIdRol())
                .estado("activo")
                .build();
        usuarioTest = usuarioRepository.save(usuarioTest);

        // 1. Reiniciar intentos fallidos
        mockMvc.perform(patch("/api/usuarios/" + usuarioTest.getIdUsuario() + "/reiniciar-intentos")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk());

        // 2. Bloquear cuenta
        mockMvc.perform(patch("/api/usuarios/" + usuarioTest.getIdUsuario() + "/bloquear")
                .param("minutos", "30")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk());

        // 3. Obtener estadísticas
        mockMvc.perform(get("/api/usuarios/estadisticas")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isNumber());
    }

    @Test
    void testValidacionesYRestricciones() throws Exception {
        // 1. Intentar crear usuario sin autenticación
        UsuarioDTO usuario = UsuarioDTO.builder()
                .usuario("unauthorized")
                .email("unauthorized@test.com")
                .password("password123")
                .nombreCompleto("Unauthorized User")
                .idRol(rolCliente.getIdRol())
                .build();

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isUnauthorized());

        // 2. Intentar crear usuario con rol CLIENTE (debe ser prohibido)
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated());

        // Crear usuario cliente para pruebas de autorización
        Usuario usuarioCliente = Usuario.builder()
                .usuario("client")
                .email("client@test.com")
                .password(passwordEncoder.encode("client123"))
                .nombreCompleto("Client User")
                .idRol(rolCliente.getIdRol())
                .estado("activo")
                .build();
        usuarioCliente = usuarioRepository.save(usuarioCliente);

        // 3. Intentar acceder a endpoints de admin como cliente
        mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("client:client123".getBytes())))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("client:client123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isForbidden());

        // 4. Validación de datos obligatorios
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        // 5. Validación de email
        UsuarioDTO usuarioEmailInvalido = UsuarioDTO.builder()
                .usuario("test")
                .email("email-invalido")
                .password("password123")
                .nombreCompleto("Test User")
                .idRol(rolCliente.getIdRol())
                .build();

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioEmailInvalido)))
                .andExpect(status().isBadRequest());

        // 6. Validación de contraseña
        UsuarioDTO usuarioPasswordInvalido = UsuarioDTO.builder()
                .usuario("test")
                .email("test@test.com")
                .password("123") // Contraseña muy corta
                .nombreCompleto("Test User")
                .idRol(rolCliente.getIdRol())
                .build();

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioPasswordInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testManejoErroresYCasosBorde() throws Exception {
        // 1. Intentar obtener usuario que no existe
        mockMvc.perform(get("/api/usuarios/99999")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes())))
                .andExpect(status().isNotFound());

        // 2. Intentar actualizar usuario que no existe
        UsuarioDTO updateDTO = UsuarioDTO.builder()
                .usuario("nonexistent")
                .build();

        mockMvc.perform(put("/api/usuarios/99999")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        // 3. Intentar crear usuario con email duplicado
        UsuarioDTO usuarioDuplicado = UsuarioDTO.builder()
                .usuario("duplicate")
                .email("admin@test.com") // Email ya existe
                .password("password123")
                .nombreCompleto("Duplicate User")
                .idRol(rolCliente.getIdRol())
                .build();

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDuplicado)))
                .andExpect(status().isBadRequest());

        // 4. Intentar crear usuario con rol que no existe
        UsuarioDTO usuarioRolInvalido = UsuarioDTO.builder()
                .usuario("invalidrol")
                .email("invalidrol@test.com")
                .password("password123")
                .nombreCompleto("Invalid Role User")
                .idRol(99999L) // Rol no existe
                .build();

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioRolInvalido)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSeguridadYAutenticacion() throws Exception {
        // 1. Login con credenciales correctas
        String loginRequest = String.format(
                "{\"usuario\":\"admin\",\"password\":\"admin123\"}"
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.usuario.usuario").value("admin"))
                .andExpect(jsonPath("$.usuario.nombreRol").value("ADMIN"));

        // 2. Login con credenciales incorrectas
        String loginInvalido = String.format(
                "{\"usuario\":\"admin\",\"password\":\"wrongpassword\"}"
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginInvalido))
                .andExpect(status().isUnauthorized());

        // 3. Acceso a endpoint protegido sin token
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());

        // 4. Acceso a endpoint protegido con token inválido
        mockMvc.perform(get("/api/usuarios")
                .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testPersistenciaYConsistenciaDatos() throws Exception {
        // 1. Crear usuario
        UsuarioDTO nuevoUsuario = UsuarioDTO.builder()
                .usuario("persisttest")
                .email("persist@test.com")
                .password("password123")
                .nombreCompleto("Persistence Test")
                .idRol(rolCliente.getIdRol())
                .build();

        String response = mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long usuarioId = objectMapper.readTree(response).get("idUsuario").asLong();

        // 2. Verificar persistencia en base de datos
        Usuario usuarioBD = usuarioRepository.findById(usuarioId).orElse(null);
        assertNotNull(usuarioBD);
        assertEquals("persisttest", usuarioBD.getUsuario());
        assertEquals("persist@test.com", usuarioBD.getEmail());
        assertEquals("Persistence Test", usuarioBD.getNombreCompleto());
        assertTrue(passwordEncoder.matches("password123", usuarioBD.getPassword()));
        assertNotNull(usuarioBD.getCreatedAt());
        assertNotNull(usuarioBD.getUpdatedAt());

        // 3. Verificar que la contraseña esté encriptada
        assertNotEquals("password123", usuarioBD.getPassword());

        // 4. Verificar auditoría
        assertNotNull(usuarioBD.getCreatedBy());
        assertNotNull(usuarioBD.getUpdatedBy());

        // 5. Actualizar y verificar cambios
        mockMvc.perform(put("/api/usuarios/" + usuarioId)
                .with(csrf())
                .header("Authorization", "Basic " + java.util.Base64.getEncoder()
                        .encodeToString("admin:admin123".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombreCompleto\":\"Updated Name\"}"))
                .andExpect(status().isOk());

        Usuario usuarioActualizado = usuarioRepository.findById(usuarioId).orElse(null);
        assertNotNull(usuarioActualizado);
        assertEquals("Updated Name", usuarioActualizado.getNombreCompleto());
        assertTrue(usuarioActualizado.getUpdatedAt().isAfter(usuarioActualizado.getCreatedAt()));
    }
}

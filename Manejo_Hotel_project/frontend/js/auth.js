/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * JAVASCRIPT - AUTHENTICATION
 * =============================================
 * 
 * Módulo de autenticación para manejo de tokens JWT,
 * login, logout y gestión de sesiones.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */

// Configuración de la API
const API_BASE_URL = 'http://localhost:8080/api';

// Estado de autenticación
let authToken = null;
let refreshToken = null;
let currentUser = null;
let sessionTimeout = null;
let sessionWarningTimeout = null;

/**
 * Inicializa el módulo de autenticación
 */
function initAuth() {
    // Cargar tokens desde localStorage
    authToken = localStorage.getItem('token');
    refreshToken = localStorage.getItem('refreshToken');
    currentUser = JSON.parse(localStorage.getItem('user') || '{}');
    
    // Configurar interceptores para API
    setupApiInterceptors();
    
    // Iniciar monitoreo de sesión
    if (authToken) {
        startSessionMonitoring();
    }
}

/**
 * Configura interceptores para las peticiones API
 */
function setupApiInterceptors() {
    // Interceptor para agregar token a las peticiones
    const originalFetch = window.fetch;
    window.fetch = function(url, options = {}) {
        // Agregar token a headers si existe
        if (authToken && !url.includes('/auth/')) {
            options.headers = {
                ...options.headers,
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            };
        }
        
        return originalFetch(url, options)
            .then(response => {
                // Manejar 401 Unauthorized
                if (response.status === 401) {
                    handleUnauthorized();
                }
                return response;
            })
            .catch(error => {
                console.error('Error en petición API:', error);
                throw error;
            });
    };
}

/**
 * Maneja respuesta 401 Unauthorized
 */
function handleUnauthorized() {
    // Intentar refrescar el token
    if (refreshToken) {
        refreshAccessToken()
            .then(newToken => {
                authToken = newToken;
                localStorage.setItem('token', newToken);
                // Recargar la página para reintentar la petición
                window.location.reload();
            })
            .catch(error => {
                console.error('Error refrescando token:', error);
                logout();
            });
    } else {
        logout();
    }
}

/**
 * Inicia sesión de usuario
 */
async function login(username, password) {
    try {
        showLoading();
        
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                usuario: username,
                password: password
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            // Guardar tokens y usuario
            authToken = data.token;
            refreshToken = data.refreshToken;
            currentUser = data.usuario;
            
            localStorage.setItem('token', authToken);
            localStorage.setItem('refreshToken', refreshToken);
            localStorage.setItem('user', JSON.stringify(currentUser));
            
            // Iniciar monitoreo de sesión
            startSessionMonitoring();
            
            // Mostrar aplicación principal
            showMainApp();
            
            // Mostrar notificación de bienvenida
            showNotification('success', `¡Bienvenido, ${currentUser.nombreCompleto}!`);
            
            return { success: true, data };
        } else {
            throw new Error(data.message || 'Error en el login');
        }
    } catch (error) {
        console.error('Error en login:', error);
        showNotification('error', error.message || 'Error al iniciar sesión');
        return { success: false, error: error.message };
    } finally {
        hideLoading();
    }
}

/**
 * Cierra sesión del usuario
 */
async function logout() {
    try {
        // Invalidar token en el servidor
        if (authToken) {
            await fetch(`${API_BASE_URL}/auth/logout`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    token: authToken
                })
            });
        }
    } catch (error) {
        console.error('Error al invalidar token:', error);
    } finally {
        // Limpiar localStorage
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        
        // Limpiar variables
        authToken = null;
        refreshToken = null;
        currentUser = null;
        
        // Detener monitoreo de sesión
        stopSessionMonitoring();
        
        // Mostrar pantalla de login
        showLoginScreen();
        
        showNotification('info', 'Sesión cerrada correctamente');
    }
}

/**
 * Refresca el token de acceso
 */
async function refreshAccessToken() {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                refreshToken: refreshToken
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            authToken = data.token;
            refreshToken = data.refreshToken;
            
            localStorage.setItem('token', authToken);
            localStorage.setItem('refreshToken', refreshToken);
            
            return authToken;
        } else {
            throw new Error('Error refrescando token');
        }
    } catch (error) {
        console.error('Error refrescando token:', error);
        throw error;
    }
}

/**
 * Valida si el token actual es válido
 */
function validateToken() {
    if (!authToken) {
        return false;
    }
    
    try {
        // Decodificar payload del JWT
        const payload = JSON.parse(atob(authToken.split('.')[1]));
        const currentTime = Date.now() / 1000;
        
        return payload.exp > currentTime;
    } catch (error) {
        console.error('Error validando token:', error);
        return false;
    }
}

/**
 * Obtiene el tiempo restante del token en segundos
 */
function getTokenRemainingTime() {
    if (!authToken) {
        return 0;
    }
    
    try {
        const payload = JSON.parse(atob(authToken.split('.')[1]));
        const currentTime = Date.now() / 1000;
        return Math.max(0, payload.exp - currentTime);
    } catch (error) {
        console.error('Error obteniendo tiempo restante:', error);
        return 0;
    }
}

/**
 * Inicia el monitoreo de la sesión
 */
function startSessionMonitoring() {
    // Detener monitoreo existente
    stopSessionMonitoring();
    
    // Verificar token cada 30 segundos
    sessionTimeout = setInterval(() => {
        if (!validateToken()) {
            console.log('Token expirado, cerrando sesión');
            logout();
        }
    }, 30000);
    
    // Advertencia de expiración (5 minutos antes)
    const remainingTime = getTokenRemainingTime();
    if (remainingTime > 0 && remainingTime <= 300) { // 5 minutos
        showSessionWarning(remainingTime);
    } else if (remainingTime > 300) {
        // Programar advertencia para 5 minutos antes de expirar
        const warningTime = (remainingTime - 300) * 1000;
        sessionWarningTimeout = setTimeout(() => {
            showSessionWarning(300);
        }, warningTime);
    }
}

/**
 * Detiene el monitoreo de la sesión
 */
function stopSessionMonitoring() {
    if (sessionTimeout) {
        clearInterval(sessionTimeout);
        sessionTimeout = null;
    }
    
    if (sessionWarningTimeout) {
        clearTimeout(sessionWarningTimeout);
        sessionWarningTimeout = null;
    }
}

/**
 * Muestra advertencia de expiración de sesión
 */
function showSessionWarning(remainingSeconds) {
    const modal = new bootstrap.Modal(document.getElementById('sessionTimeoutModal'));
    
    // Actualizar contador
    const timeElement = document.getElementById('sessionTimeRemaining');
    let timeLeft = remainingSeconds;
    
    const updateCounter = () => {
        if (timeLeft <= 0) {
            modal.hide();
            logout();
            return;
        }
        
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        timeElement.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;
        timeLeft--;
    };
    
    updateCounter();
    const counterInterval = setInterval(updateCounter, 1000);
    
    // Limpiar interval cuando se cierra el modal
    document.getElementById('sessionTimeoutModal').addEventListener('hidden.bs.modal', () => {
        clearInterval(counterInterval);
    }, { once: true });
    
    modal.show();
}

/**
 * Extiende la sesión actual
 */
async function extendSession() {
    try {
        await refreshAccessToken();
        
        // Cerrar modal de advertencia
        const modal = bootstrap.Modal.getInstance(document.getElementById('sessionTimeoutModal'));
        modal.hide();
        
        // Reiniciar monitoreo
        startSessionMonitoring();
        
        showNotification('success', 'Sesión extendida exitosamente');
    } catch (error) {
        console.error('Error extendiendo sesión:', error);
        showNotification('error', 'No se pudo extender la sesión');
    }
}

/**
 * Recupera contraseña
 */
async function resetPassword(email) {
    try {
        showLoading();
        
        const response = await fetch(`${API_BASE_URL}/auth/reset-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showNotification('success', 'Se ha enviado un email de recuperación a tu correo');
            
            // Cerrar modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('resetPasswordModal'));
            modal.hide();
            
            // Limpiar formulario
            document.getElementById('resetEmail').value = '';
        } else {
            throw new Error(data.message || 'Error al recuperar contraseña');
        }
    } catch (error) {
        console.error('Error recuperando contraseña:', error);
        showNotification('error', error.message || 'Error al recuperar contraseña');
    } finally {
        hideLoading();
    }
}

/**
 * Verifica si el usuario tiene un rol específico
 */
function hasRole(role) {
    return currentUser && currentUser.nombreRol === role;
}

/**
 * Verifica si el usuario tiene alguno de los roles especificados
 */
function hasAnyRole(roles) {
    return currentUser && roles.includes(currentUser.nombreRol);
}

/**
 * Verifica si el usuario está autenticado
 */
function isAuthenticated() {
    return authToken && validateToken();
}

/**
 * Obtiene el usuario actual
 */
function getCurrentUser() {
    return currentUser;
}

/**
 * Obtiene el token de acceso actual
 */
function getAuthToken() {
    return authToken;
}

/**
 * Maneja el evento de login del formulario
 */
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            if (!username || !password) {
                showNotification('error', 'Por favor, complete todos los campos');
                return;
            }
            
            // Mostrar estado de carga
            const loginBtn = document.getElementById('loginBtn');
            const btnText = loginBtn.querySelector('.btn-text');
            const btnLoading = loginBtn.querySelector('.btn-loading');
            
            btnText.style.display = 'none';
            btnLoading.style.display = 'block';
            loginBtn.disabled = true;
            
            // Realizar login
            const result = await login(username, password);
            
            // Restaurar botón
            btnText.style.display = 'block';
            btnLoading.style.display = 'none';
            loginBtn.disabled = false;
            
            if (!result.success) {
                // Limpiar contraseña en caso de error
                document.getElementById('password').value = '';
            }
        });
    }
    
    // Manejar formulario de reset de contraseña
    const resetForm = document.getElementById('resetPasswordForm');
    if (resetForm) {
        resetForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('resetEmail').value;
            resetPassword(email);
        });
    }
    
    // Inicializar autenticación
    initAuth();
});

// Exportar funciones para uso global
window.Auth = {
    login,
    logout,
    validateToken,
    hasRole,
    hasAnyRole,
    isAuthenticated,
    getCurrentUser,
    getAuthToken,
    resetPassword,
    extendSession
};

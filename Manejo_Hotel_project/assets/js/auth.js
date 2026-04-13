/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * JAVASCRIPT: AUTHENTICATION
 * =============================================
 * 
 * Este archivo maneja la autenticación del lado del cliente,
 * validación de formularios y comunicación con el backend.
 */

// Configuración global
const CONFIG = {
    API_BASE_URL: '../backend/api/',
    SESSION_TIMEOUT: 30 * 60 * 1000, // 30 minutos
    WARNING_TIMEOUT: 5 * 60 * 1000, // 5 minutos antes de expirar
    CHECK_INTERVAL: 30 * 1000 // Verificar cada 30 segundos
};

// Estado de la aplicación
let sessionTimer = null;
let warningTimer = null;
let checkInterval = null;

/**
 * Inicializar el sistema de autenticación
 */
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar formulario de login
    initLoginForm();
    
    // Verificar sesión activa
    checkSessionStatus();
    
    // Iniciar monitoreo de sesión
    startSessionMonitoring();
});

/**
 * Inicializar formulario de login
 */
function initLoginForm() {
    const loginForm = document.getElementById('loginForm');
    if (!loginForm) return;
    
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        handleLogin();
    });
    
    // Validación en tiempo real
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    
    usernameInput.addEventListener('input', validateUsername);
    passwordInput.addEventListener('input', validatePassword);
    
    // Prevenir envío con Enter sin validar
    loginForm.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleLogin();
        }
    });
}

/**
 * Manejar proceso de login
 */
async function handleLogin() {
    const form = document.getElementById('loginForm');
    const formData = new FormData(form);
    const loginBtn = document.getElementById('loginBtn');
    const loginText = document.getElementById('loginText');
    
    // Validar formulario
    if (!validateLoginForm()) {
        return;
    }
    
    // Deshabilitar botón y mostrar loading
    loginBtn.disabled = true;
    loginText.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Iniciando sesión...';
    
    try {
        // Enviar datos al backend
        const response = await fetch(CONFIG.API_BASE_URL + 'auth.php?action=login', {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        
        const result = await response.json();
        
        if (result.success) {
            // Login exitoso
            showAlert('success', result.message || 'Login exitoso');
            
            // Redirigir según rol
            setTimeout(() => {
                window.location.href = result.redirect || 'dashboard.php';
            }, 1000);
            
        } else {
            // Error en login
            showAlert('error', result.message || 'Error en el login');
            
            // Rehabilitar botón
            loginBtn.disabled = false;
            loginText.textContent = 'Iniciar Sesión';
            
            // Enfocar campo apropiado
            if (result.message && result.message.toLowerCase().includes('usuario')) {
                document.getElementById('username').focus();
            } else {
                document.getElementById('password').focus();
            }
        }
        
    } catch (error) {
        console.error('Error en login:', error);
        showAlert('error', 'Error de conexión. Por favor intenta nuevamente.');
        
        // Rehabilitar botón
        loginBtn.disabled = false;
        loginText.textContent = 'Iniciar Sesión';
    }
}

/**
 * Validar formulario de login
 */
function validateLoginForm() {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    
    let isValid = true;
    
    // Validar username
    if (!username) {
        showFieldError('username', 'El nombre de usuario es obligatorio');
        isValid = false;
    } else if (username.length < 3) {
        showFieldError('username', 'El nombre de usuario debe tener al menos 3 caracteres');
        isValid = false;
    } else {
        clearFieldError('username');
    }
    
    // Validar password
    if (!password) {
        showFieldError('password', 'La contraseña es obligatoria');
        isValid = false;
    } else if (password.length < 6) {
        showFieldError('password', 'La contraseña debe tener al menos 6 caracteres');
        isValid = false;
    } else {
        clearFieldError('password');
    }
    
    return isValid;
}

/**
 * Validar username en tiempo real
 */
function validateUsername() {
    const username = this.value.trim();
    
    if (!username) {
        showFieldError('username', 'El nombre de usuario es obligatorio');
    } else if (username.length < 3) {
        showFieldError('username', 'El nombre de usuario debe tener al menos 3 caracteres');
    } else if (!/^[a-zA-Z0-9_]+$/.test(username)) {
        showFieldError('username', 'Solo puede contener letras, números y guiones bajos');
    } else {
        clearFieldError('username');
    }
}

/**
 * Validar password en tiempo real
 */
function validatePassword() {
    const password = this.value;
    
    if (!password) {
        showFieldError('password', 'La contraseña es obligatoria');
    } else if (password.length < 6) {
        showFieldError('password', 'La contraseña debe tener al menos 6 caracteres');
    } else {
        clearFieldError('password');
    }
}

/**
 * Mostrar error de campo
 */
function showFieldError(fieldId, message) {
    const field = document.getElementById(fieldId);
    const feedback = field.nextElementSibling || document.createElement('div');
    
    if (!feedback.classList.contains('invalid-feedback')) {
        feedback.className = 'invalid-feedback';
        field.parentNode.appendChild(feedback);
    }
    
    feedback.textContent = message;
    field.classList.add('is-invalid');
    field.classList.remove('is-valid');
}

/**
 * Limpiar error de campo
 */
function clearFieldError(fieldId) {
    const field = document.getElementById(fieldId);
    const feedback = field.nextElementSibling;
    
    if (feedback && feedback.classList.contains('invalid-feedback')) {
        feedback.remove();
    }
    
    field.classList.remove('is-invalid');
    field.classList.add('is-valid');
}

/**
 * Mostrar alerta
 */
function showAlert(type, message) {
    const alertContainer = document.getElementById('alertContainer');
    const alertClass = type === 'error' ? 'danger' : type;
    const icon = type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-triangle' : 'info-circle';
    
    const alertHtml = `
        <div class="alert alert-${alertClass} alert-dismissible fade show" role="alert">
            <i class="fas fa-${icon} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    alertContainer.innerHTML = alertHtml;
    
    // Auto-dismiss después de 5 segundos
    setTimeout(() => {
        const alert = alertContainer.querySelector('.alert');
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    }, 5000);
}

/**
 * Verificar estado de la sesión
 */
async function checkSessionStatus() {
    try {
        const response = await fetch(CONFIG.API_BASE_URL + 'auth.php?action=check_session', {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        
        const result = await response.json();
        
        if (!result.authenticated) {
            // Redirigir a login si no está autenticado
            if (window.location.pathname !== '/login.php') {
                window.location.href = 'login.php?session_expired=true';
            }
        } else {
            // Actualizar información de sesión
            updateSessionInfo(result.user);
            
            // Verificar tiempo restante
            if (result.time_remaining < CONFIG.WARNING_TIMEOUT) {
                showSessionWarning(result.time_remaining);
            }
        }
        
    } catch (error) {
        console.error('Error verificando sesión:', error);
    }
}

/**
 * Actualizar información de sesión en la UI
 */
function updateSessionInfo(user) {
    // Actualizar nombre de usuario en la interfaz
    const userNameElements = document.querySelectorAll('.user-name');
    userNameElements.forEach(element => {
        element.textContent = user.nombre_completo || user.username;
    });
    
    // Actualizar rol
    const userRoleElements = document.querySelectorAll('.user-role');
    userRoleElements.forEach(element => {
        element.textContent = user.rol || '';
    });
    
    // Actualizar avatar
    const userAvatarElements = document.querySelectorAll('.user-avatar');
    userAvatarElements.forEach(element => {
        const initials = (user.nombre_completo || user.username)
            .split(' ')
            .map(word => word[0])
            .join('')
            .toUpperCase()
            .slice(0, 2);
        element.textContent = initials;
    });
}

/**
 * Iniciar monitoreo de sesión
 */
function startSessionMonitoring() {
    // Limpiar timers existentes
    stopSessionMonitoring();
    
    // Configurar timer de sesión
    sessionTimer = setTimeout(() => {
        handleSessionTimeout();
    }, CONFIG.SESSION_TIMEOUT);
    
    // Configurar timer de advertencia
    warningTimer = setTimeout(() => {
        showSessionWarning(CONFIG.WARNING_TIMEOUT);
    }, CONFIG.SESSION_TIMEOUT - CONFIG.WARNING_TIMEOUT);
    
    // Configurar verificación periódica
    checkInterval = setInterval(() => {
        checkSessionStatus();
    }, CONFIG.CHECK_INTERVAL);
    
    // Escuchar actividad del usuario
    document.addEventListener('mousemove', resetSessionTimer);
    document.addEventListener('keypress', resetSessionTimer);
    document.addEventListener('scroll', resetSessionTimer);
    document.addEventListener('click', resetSessionTimer);
}

/**
 * Detener monitoreo de sesión
 */
function stopSessionMonitoring() {
    if (sessionTimer) clearTimeout(sessionTimer);
    if (warningTimer) clearTimeout(warningTimer);
    if (checkInterval) clearInterval(checkInterval);
}

/**
 * Reiniciar timer de sesión
 */
function resetSessionTimer() {
    stopSessionMonitoring();
    startSessionMonitoring();
}

/**
 * Manejar timeout de sesión
 */
function handleSessionTimeout() {
    showAlert('warning', 'Tu sesión ha expirado. Serás redirigido al login.');
    
    setTimeout(() => {
        logout();
    }, 3000);
}

/**
 * Mostrar advertencia de sesión
 */
function showSessionWarning(timeRemaining) {
    const minutes = Math.floor(timeRemaining / 60000);
    const seconds = Math.floor((timeRemaining % 60000) / 1000);
    
    const message = `Tu sesión expirará en ${minutes}:${seconds.toString().padStart(2, '0')}. ¿Deseas extenderla?`;
    
    // Crear modal de advertencia
    const modalHtml = `
        <div class="modal fade" id="sessionWarningModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="fas fa-exclamation-triangle text-warning me-2"></i>
                            Sesión por expirar
                        </h5>
                    </div>
                    <div class="modal-body">
                        <p>${message}</p>
                        <div class="progress">
                            <div class="progress-bar bg-warning" role="progressbar" 
                                 style="width: ${(timeRemaining / CONFIG.WARNING_TIMEOUT) * 100}%">
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" onclick="extendSession()">
                            Extender sesión
                        </button>
                        <button type="button" class="btn btn-secondary" onclick="logout()">
                            Cerrar sesión
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Agregar modal al DOM
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    
    // Mostrar modal
    const modal = new bootstrap.Modal(document.getElementById('sessionWarningModal'));
    modal.show();
    
    // Actualizar barra de progreso
    const progressBar = document.querySelector('#sessionWarningModal .progress-bar');
    const progressInterval = setInterval(() => {
        const remaining = timeRemaining - (Date.now() - modal._element.dataset.startTime);
        if (remaining <= 0) {
            clearInterval(progressInterval);
            modal.hide();
            handleSessionTimeout();
        } else {
            const percentage = (remaining / CONFIG.WARNING_TIMEOUT) * 100;
            progressBar.style.width = percentage + '%';
        }
    }, 1000);
    
    modal._element.dataset.startTime = Date.now();
    
    // Limpiar modal al cerrar
    document.getElementById('sessionWarningModal').addEventListener('hidden.bs.modal', function() {
        clearInterval(progressInterval);
        this.remove();
    });
}

/**
 * Extender sesión
 */
async function extendSession() {
    try {
        const response = await fetch(CONFIG.API_BASE_URL + 'auth.php?action=refresh_session', {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        
        const result = await response.json();
        
        if (result.success) {
            // Cerrar modal de advertencia
            const modal = bootstrap.Modal.getInstance(document.getElementById('sessionWarningModal'));
            if (modal) modal.hide();
            
            // Reiniciar timer
            resetSessionTimer();
            
            showAlert('success', 'Sesión extendida exitosamente');
        } else {
            showAlert('error', 'Error al extender la sesión');
        }
        
    } catch (error) {
        console.error('Error extendiendo sesión:', error);
        showAlert('error', 'Error de conexión');
    }
}

/**
 * Cerrar sesión
 */
async function logout() {
    try {
        await fetch(CONFIG.API_BASE_URL + 'auth.php?action=logout', {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
    } catch (error) {
        console.error('Error en logout:', error);
    }
    
    // Detener monitoreo
    stopSessionMonitoring();
    
    // Redirigir a login
    window.location.href = 'login.php?logout=success';
}

/**
 * Verificar si el usuario tiene un rol específico
 */
function hasRole(requiredRole) {
    // Esta función debe ser implementada según la información de sesión
    // Por ahora, retorna true para demostración
    return true;
}

/**
 * Proteger rutas según rol
 */
function protectRoute(requiredRole) {
    if (!hasRole(requiredRole)) {
        window.location.href = 'access_denied.php';
        return false;
    }
    return true;
}

/**
 * Formatear tiempo restante
 */
function formatTimeRemaining(milliseconds) {
    const minutes = Math.floor(milliseconds / 60000);
    const seconds = Math.floor((milliseconds % 60000) / 1000);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
}

/**
 * Detectar inactividad del usuario
 */
let inactivityTimer;
const INACTIVITY_TIMEOUT = 15 * 60 * 1000; // 15 minutos

function resetInactivityTimer() {
    clearTimeout(inactivityTimer);
    inactivityTimer = setTimeout(() => {
        showAlert('warning', 'Has estado inactivo por mucho tiempo. ¿Deseas continuar?');
    }, INACTIVITY_TIMEOUT);
}

// Iniciar detector de inactividad
document.addEventListener('DOMContentLoaded', function() {
    ['mousemove', 'keypress', 'scroll', 'click'].forEach(event => {
        document.addEventListener(event, resetInactivityTimer);
    });
    resetInactivityTimer();
});

// Exportar funciones para uso global
window.Auth = {
    login: handleLogin,
    logout: logout,
    checkSession: checkSessionStatus,
    extendSession: extendSession,
    hasRole: hasRole,
    protectRoute: protectRoute
};

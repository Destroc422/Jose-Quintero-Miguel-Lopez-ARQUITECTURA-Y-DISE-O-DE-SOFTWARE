/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * JAVASCRIPT - API CLIENT
 * =============================================
 * 
 * Módulo para comunicación con la API REST del backend.
 * Proporciona métodos para todas las operaciones CRUD.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */

// Configuración base de la API
const API_CONFIG = {
    baseURL: 'http://localhost:8080/api',
    timeout: 30000, // 30 segundos
    retries: 3,
    retryDelay: 1000
};

/**
 * Cliente API principal
 */
class ApiClient {
    constructor() {
        this.baseURL = API_CONFIG.baseURL;
        this.timeout = API_CONFIG.timeout;
    }

    /**
     * Realiza una petición HTTP con manejo de errores y reintentos
     */
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            timeout: this.timeout,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                const error = await response.json().catch(() => ({}));
                throw new ApiError(error.message || `HTTP ${response.status}`, response.status, error);
            }

            return await response.json();
        } catch (error) {
            console.error('API Request Error:', error);
            throw error;
        }
    }

    /**
     * Petición GET
     */
    async get(endpoint, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const url = queryString ? `${endpoint}?${queryString}` : endpoint;
        return this.request(url);
    }

    /**
     * Petición POST
     */
    async post(endpoint, data = {}) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    /**
     * Petición PUT
     */
    async put(endpoint, data = {}) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    /**
     * Petición PATCH
     */
    async patch(endpoint, data = {}) {
        return this.request(endpoint, {
            method: 'PATCH',
            body: JSON.stringify(data)
        });
    }

    /**
     * Petición DELETE
     */
    async delete(endpoint) {
        return this.request(endpoint, {
            method: 'DELETE'
        });
    }
}

/**
 * Clase para manejo de errores de API
 */
class ApiError extends Error {
    constructor(message, status, data = {}) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.data = data;
    }
}

// Instancia del cliente API
const apiClient = new ApiClient();

/**
 * API de Autenticación
 */
const AuthAPI = {
    /**
     * Iniciar sesión
     */
    async login(credentials) {
        return apiClient.post('/auth/login', credentials);
    },

    /**
     * Refrescar token
     */
    async refreshToken(refreshToken) {
        return apiClient.post('/auth/refresh', { refreshToken });
    },

    /**
     * Cerrar sesión
     */
    async logout(token) {
        return apiClient.post('/auth/logout', { token });
    },

    /**
     * Validar token
     */
    async validateToken(token) {
        return apiClient.post('/auth/validate', { token });
    },

    /**
     * Recuperar contraseña
     */
    async resetPassword(email) {
        return apiClient.post('/auth/reset-password', { email });
    }
};

/**
 * API de Usuarios
 */
const UsuariosAPI = {
    /**
     * Obtener todos los usuarios
     */
    async getAll(params = {}) {
        return apiClient.get('/usuarios', params);
    },

    /**
     * Obtener usuario por ID
     */
    async getById(id) {
        return apiClient.get(`/usuarios/${id}`);
    },

    /**
     * Crear usuario
     */
    async create(usuario) {
        return apiClient.post('/usuarios', usuario);
    },

    /**
     * Actualizar usuario
     */
    async update(id, usuario) {
        return apiClient.put(`/usuarios/${id}`, usuario);
    },

    /**
     * Eliminar usuario
     */
    async delete(id) {
        return apiClient.delete(`/usuarios/${id}`);
    },

    /**
     * Cambiar estado de usuario
     */
    async cambiarEstado(id, estado) {
        return apiClient.patch(`/usuarios/${id}/estado`, null, {
            params: { estado }
        });
    },

    /**
     * Reiniciar intentos fallidos
     */
    async reiniciarIntentos(id) {
        return apiClient.patch(`/usuarios/${id}/reiniciar-intentos`);
    },

    /**
     * Bloquear cuenta
     */
    async bloquearCuenta(id, minutos = 30) {
        return apiClient.patch(`/usuarios/${id}/bloquear`, null, {
            params: { minutos }
        });
    },

    /**
     * Buscar usuarios
     */
    async buscar(texto, params = {}) {
        return apiClient.get('/usuarios/buscar', { texto, ...params });
    },

    /**
     * Obtener estadísticas
     */
    async getEstadisticas() {
        return apiClient.get('/usuarios/estadisticas');
    }
};

/**
 * API de Servicios
 */
const ServiciosAPI = {
    /**
     * Obtener todos los servicios
     */
    async getAll(params = {}) {
        return apiClient.get('/servicios', params);
    },

    /**
     * Obtener servicio por ID
     */
    async getById(id) {
        return apiClient.get(`/servicios/${id}`);
    },

    /**
     * Crear servicio
     */
    async create(servicio) {
        return apiClient.post('/servicios', servicio);
    },

    /**
     * Actualizar servicio
     */
    async update(id, servicio) {
        return apiClient.put(`/servicios/${id}`, servicio);
    },

    /**
     * Eliminar servicio
     */
    async delete(id) {
        return apiClient.delete(`/servicios/${id}`);
    },

    /**
     * Buscar servicios
     */
    async buscar(params = {}) {
        return apiClient.get('/servicios/buscar', params);
    },

    /**
     * Obtener servicios destacados
     */
    async getDestacados() {
        return apiClient.get('/servicios/destacados');
    },

    /**
     * Obtener servicios populares
     */
    async getPopulares(limite = 10) {
        return apiClient.get('/servicios/populares', { limit: limite });
    },

    /**
     * Verificar disponibilidad
     */
    async verificarDisponibilidad(idServicio, fechaInicio, fechaFin, cantidadPersonas) {
        return apiClient.post('/servicios/disponibilidad', {
            idServicio,
            fechaInicio,
            fechaFin,
            cantidadPersonas
        });
    },

    /**
     * Obtener estadísticas
     */
    async getEstadisticas() {
        return apiClient.get('/servicios/estadisticas');
    }
};

/**
 * API de Reservas
 */
const ReservasAPI = {
    /**
     * Obtener todas las reservas
     */
    async getAll(params = {}) {
        return apiClient.get('/reservas', params);
    },

    /**
     * Obtener reserva por ID
     */
    async getById(id) {
        return apiClient.get(`/reservas/${id}`);
    },

    /**
     * Obtener reserva por código
     */
    async getByCodigo(codigo) {
        return apiClient.get('/reservas/codigo/' + codigo);
    },

    /**
     * Crear reserva
     */
    async create(reserva) {
        return apiClient.post('/reservas', reserva);
    },

    /**
     * Actualizar reserva
     */
    async update(id, reserva) {
        return apiClient.put(`/reservas/${id}`, reserva);
    },

    /**
     * Cancelar reserva
     */
    async cancelar(id, motivo) {
        return apiClient.patch(`/reservas/${id}/cancelar`, { motivo });
    },

    /**
     * Confirmar reserva
     */
    async confirmar(id) {
        return apiClient.patch(`/reservas/${id}/confirmar`);
    },

    /**
     * Marcar como pagada
     */
    async marcarComoPagada(id) {
        return apiClient.patch(`/reservas/${id}/pagar`);
    },

    /**
     * Buscar reservas
     */
    async buscar(texto, params = {}) {
        return apiClient.get('/reservas/buscar', { texto, ...params });
    },

    /**
     * Obtener reservas por cliente
     */
    async getByCliente(idCliente, params = {}) {
        return apiClient.get('/reservas/cliente/' + idCliente, params);
    },

    /**
     * Obtener reservas por servicio
     */
    async getByServicio(idServicio, params = {}) {
        return apiClient.get('/reservas/servicio/' + idServicio, params);
    },

    /**
     * Obtener reservas próximas
     */
    async getProximas(dias = 7) {
        return apiClient.get('/reservas/proximas', { dias });
    },

    /**
     * Obtener estadísticas
     */
    async getEstadisticas() {
        return apiClient.get('/reservas/estadisticas');
    }
};

/**
 * API de Pagos
 */
const PagosAPI = {
    /**
     * Obtener todos los pagos
     */
    async getAll(params = {}) {
        return apiClient.get('/pagos', params);
    },

    /**
     * Obtener pago por ID
     */
    async getById(id) {
        return apiClient.get(`/pagos/${id}`);
    },

    /**
     * Crear pago
     */
    async create(pago) {
        return apiClient.post('/pagos', pago);
    },

    /**
     * Actualizar pago
     */
    async update(id, pago) {
        return apiClient.put(`/pagos/${id}`, pago);
    },

    /**
     * Procesar pago
     */
    async procesar(id, metodoPago, datosPago) {
        return apiClient.post(`/pagos/${id}/procesar`, {
            metodoPago,
            ...datosPago
        });
    },

    /**
     * Reembolsar pago
     */
    async reembolsar(id, motivo) {
        return apiClient.post(`/pagos/${id}/reembolsar`, { motivo });
    },

    /**
     * Buscar pagos
     */
    async buscar(texto, params = {}) {
        return apiClient.get('/pagos/buscar', { texto, ...params });
    },

    /**
     * Obtener pagos por reserva
     */
    async getByReserva(idReserva, params = {}) {
        return apiClient.get('/pagos/reserva/' + idReserva, params);
    },

    /**
     * Obtener pagos por cliente
     */
    async getByCliente(idCliente, params = {}) {
        return apiClient.get('/pagos/cliente/' + idCliente, params);
    },

    /**
     * Obtener estadísticas
     */
    async getEstadisticas() {
        return apiClient.get('/pagos/estadisticas');
    }
};

/**
 * API de Reportes
 */
const ReportesAPI = {
    /**
     * Generar reporte de ingresos
     */
    async ingresos(fechaInicio, fechaFin) {
        return apiClient.get('/reportes/ingresos', {
            fechaInicio,
            fechaFin
        });
    },

    /**
     * Generar reporte de ocupación
     */
    async ocupacion(fechaInicio, fechaFin) {
        return apiClient.get('/reportes/ocupacion', {
            fechaInicio,
            fechaFin
        });
    },

    /**
     * Generar reporte de servicios populares
     */
    async serviciosPopulares(fechaInicio, fechaFin, limite = 10) {
        return apiClient.get('/reportes/servicios-populares', {
            fechaInicio,
            fechaFin,
            limite
        });
    },

    /**
     * Generar reporte de clientes
     */
    async clientes(params = {}) {
        return apiClient.get('/reportes/clientes', params);
    },

    /**
     * Exportar reporte a PDF
     */
    async exportarPDF(tipo, params = {}) {
        const response = await apiClient.get(`/reportes/${tipo}/pdf`, params);
        
        // Crear blob y descargar
        const blob = new Blob([response], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `reporte-${tipo}-${new Date().toISOString().split('T')[0]}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    }
};

/**
 * Utilidades para manejo de respuestas
 */
const ResponseUtils = {
    /**
     * Maneja respuesta de API genérica
     */
    handleResponse(response, successCallback, errorCallback) {
        if (response.success !== false) {
            if (successCallback) successCallback(response);
        } else {
            if (errorCallback) errorCallback(response.message || 'Error en la operación');
        }
    },

    /**
     * Maneja error de API
     */
    handleError(error, errorCallback) {
        console.error('API Error:', error);
        
        let message = 'Error en la operación';
        if (error instanceof ApiError) {
            message = error.message;
        } else if (error.message) {
            message = error.message;
        }
        
        if (errorCallback) errorCallback(message);
    },

    /**
     * Extrae datos paginados
     */
    extractPaginatedData(response) {
        return {
            content: response.content || response.data || [],
            totalElements: response.totalElements || response.total || 0,
            totalPages: response.totalPages || response.totalPages || 0,
            size: response.size || response.pageSize || 10,
            number: response.number || response.pageNumber || 0,
            first: response.first,
            last: response.last
        };
    }
};

/**
 * Cache simple para respuestas de API
 */
class ApiCache {
    constructor(maxSize = 50, ttl = 300000) { // 5 minutos por defecto
        this.cache = new Map();
        this.maxSize = maxSize;
        this.ttl = ttl;
    }

    set(key, value) {
        // Eliminar entrada más antigua si se excede el tamaño
        if (this.cache.size >= this.maxSize) {
            const firstKey = this.cache.keys().next().value;
            this.cache.delete(firstKey);
        }

        this.cache.set(key, {
            value,
            timestamp: Date.now()
        });
    }

    get(key) {
        const item = this.cache.get(key);
        
        if (!item) {
            return null;
        }

        // Verificar TTL
        if (Date.now() - item.timestamp > this.ttl) {
            this.cache.delete(key);
            return null;
        }

        return item.value;
    }

    clear() {
        this.cache.clear();
    }

    delete(key) {
        this.cache.delete(key);
    }
}

// Instancia de cache
const apiCache = new ApiCache();

/**
 * API con cache
 */
const CachedAPI = {
    /**
     * GET con cache
     */
    async get(endpoint, params = {}, useCache = true) {
        const cacheKey = `GET:${endpoint}:${JSON.stringify(params)}`;
        
        if (useCache) {
            const cached = apiCache.get(cacheKey);
            if (cached) {
                return cached;
            }
        }

        const response = await apiClient.get(endpoint, params);
        
        if (useCache) {
            apiCache.set(cacheKey, response);
        }

        return response;
    },

    /**
     * Invalidar cache para un endpoint
     */
    invalidateCache(endpoint) {
        // Eliminar todas las entradas que coincidan con el endpoint
        for (const key of apiCache.cache.keys()) {
            if (key.startsWith(endpoint)) {
                apiCache.delete(key);
            }
        }
    },

    /**
     * Limpiar todo el cache
     */
    clearCache() {
        apiCache.clear();
    }
};

// Exportar APIs para uso global
window.API = {
    Auth: AuthAPI,
    Usuarios: UsuariosAPI,
    Servicios: ServiciosAPI,
    Reservas: ReservasAPI,
    Pagos: PagosAPI,
    Reportes: ReportesAPI,
    Utils: ResponseUtils,
    Cache: CachedAPI
};

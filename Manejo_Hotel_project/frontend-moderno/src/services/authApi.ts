import axios, { AxiosInstance, AxiosResponse } from 'axios';

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
 * AUTH API SERVICE
 * =============================================
 * 
 * Servicio API para autenticación.
 * Reemplaza completamente el protocolo XML por REST moderno.
 * 
 * Características:
 * - Cliente Axios con interceptores
 * - Manejo automático de tokens
 * - Refresh automático
 * - Manejo de errores centralizado
 * - Cancelación de requests
 * 
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2024
 */

// Interfaces para las respuestas de la API
export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  expiresAt: string;
  usuario: {
    id: number;
    username: string;
    nombreCompleto: string;
    email: string;
    rol: string;
    avatar?: string;
    telefono?: string;
    ultimoLogin?: string;
  };
  permisos: string[];
  message: string;
  primerLogin: boolean;
  redirectUrl: string;
}

export interface LoginRequest {
  username: string;
  password: string;
  rememberMe?: boolean;
  csrfToken?: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  nombreCompleto: string;
  telefono?: string;
}

// Configuración del cliente Axios
class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1',
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Interceptor para agregar token de autorización
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('hermosa-cartagena-auth-storage');
        if (token) {
          try {
            const authData = JSON.parse(token);
            if (authData.state?.accessToken) {
              config.headers.Authorization = `Bearer ${authData.state.accessToken}`;
            }
          } catch (error) {
            console.error('Error parsing auth token:', error);
          }
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Interceptor para manejar respuestas y errores
    this.client.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        // Si el error es 401 (Unauthorized) y no hemos intentado refrescar
        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            await this.refreshAccessToken();
            // Reintentar la solicitud original
            return this.client(originalRequest);
          } catch (refreshError) {
            // Si el refresh falla, limpiar y redirigir a login
            this.clearAuthData();
            window.location.href = '/login';
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      }
    );
  }

  private async refreshAccessToken(): Promise<void> {
    const token = localStorage.getItem('hermosa-cartagena-auth-storage');
    if (!token) {
      throw new Error('No refresh token available');
    }

    try {
      const authData = JSON.parse(token);
      const refreshToken = authData.state?.refreshToken;

      if (!refreshToken) {
        throw new Error('No refresh token in storage');
      }

      const response = await axios.post<ApiResponse<LoginResponse>>(
        `${this.client.defaults.baseURL}/auth/refresh`,
        { refreshToken }
      );

      if (response.data.success) {
        // Actualizar tokens en localStorage
        const newAuthData = {
          ...authData,
          state: {
            ...authData.state,
            accessToken: response.data.data.accessToken,
            refreshToken: response.data.data.refreshToken,
          },
        };
        localStorage.setItem('hermosa-cartagena-auth-storage', JSON.stringify(newAuthData));
      }
    } catch (error) {
      throw error;
    }
  }

  private clearAuthData(): void {
    localStorage.removeItem('hermosa-cartagena-auth-storage');
  }

  // Métodos públicos de la API
  async login(credentials: LoginRequest): Promise<ApiResponse<LoginResponse>> {
    try {
      const response: AxiosResponse<ApiResponse<LoginResponse>> = await this.client.post(
        '/auth/login',
        credentials
      );
      return response.data;
    } catch (error: any) {
      throw this.handleError(error);
    }
  }

  async register(userData: RegisterRequest): Promise<ApiResponse<LoginResponse>> {
    try {
      const response: AxiosResponse<ApiResponse<LoginResponse>> = await this.client.post(
        '/auth/register',
        userData
      );
      return response.data;
    } catch (error: any) {
      throw this.handleError(error);
    }
  }

  async logout(): Promise<ApiResponse<void>> {
    try {
      const response: AxiosResponse<ApiResponse<void>> = await this.client.post('/auth/logout');
      return response.data;
    } catch (error: any) {
      throw this.handleError(error);
    }
  }

  async refreshToken(request: RefreshTokenRequest): Promise<ApiResponse<LoginResponse>> {
    try {
      const response: AxiosResponse<ApiResponse<LoginResponse>> = await this.client.post(
        '/auth/refresh',
        request
      );
      return response.data;
    } catch (error: any) {
      throw this.handleError(error);
    }
  }

  async getCurrentUser(): Promise<ApiResponse<LoginResponse['usuario']>> {
    try {
      const response: AxiosResponse<ApiResponse<LoginResponse['usuario']>> = await this.client.get(
        '/auth/me'
      );
      return response.data;
    } catch (error: any) {
      throw this.handleError(error);
    }
  }

  async updateProfile(userData: Partial<LoginResponse['usuario']>): Promise<ApiResponse<void>> {
    try {
      const response: AxiosResponse<ApiResponse<void>> = await this.client.put(
        '/auth/profile',
        userData
      );
      return response.data;
    } catch (error: any) {
      throw this.handleError(error);
    }
  }

  async forgotPassword(email: string): Promise<ApiResponse<void>> {
    try {
      const response: AxiosResponse<ApiResponse<void>> = await this.client.post(
        '/auth/forgot-password',
        null,
        { params: { email } }
      );
      return response.data;
    } catch (error: any) {
      throw this.handleError(error);
    }
  }

  async resetPassword(token: string, newPassword: string): Promise<ApiResponse<void>> {
    try {
      const response: AxiosResponse<ApiResponse<void>> = await this.client.post(
        '/auth/reset-password',
        { token, newPassword }
      );
      return response.data;
    } catch (error: any) {
      throw this.handleError(error);
    }
  }

  async verifyMfa(code: string): Promise<ApiResponse<LoginResponse>> {
    try {
      const response: AxiosResponse<ApiResponse<LoginResponse>> = await this.client.post(
        '/auth/verify-mfa',
        { code }
      );
      return response.data;
    } catch (error: any) {
      throw this.handleError(error);
    }
  }

  private handleError(error: any): Error {
    if (error.response) {
      // Error del servidor
      const message = error.response.data?.message || error.response.statusText;
      return new Error(message);
    } else if (error.request) {
      // Error de red
      return new Error('Error de conexión. Verifica tu conexión a internet.');
    } else {
      // Error de configuración
      return new Error('Error en la configuración de la aplicación.');
    }
  }
}

// Exportar instancia única del cliente API
export const authApi = new ApiClient();

// Exportar tipos para uso en componentes
export type {
  ApiResponse,
  LoginResponse,
  LoginRequest,
  RefreshTokenRequest,
  RegisterRequest,
};

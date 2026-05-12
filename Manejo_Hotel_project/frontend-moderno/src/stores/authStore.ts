import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { authApi } from '../services/authApi';

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
 * AUTH STORE - ZUSTAND STATE MANAGEMENT
 * =============================================
 * 
 * Store de Zustand para gestión de autenticación.
 * Reemplaza completamente el sistema XML por estado moderno.
 * 
 * Características:
 * - Estado persistente en localStorage
 * - Gestión de tokens JWT
 * - Información de usuario
 * - Loading states
 * - Manejo de errores
 * 
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2024
 */

export interface User {
  id: number;
  username: string;
  nombreCompleto: string;
  email: string;
  rol: string;
  avatar?: string;
  telefono?: string;
  ultimoLogin?: string;
}

export interface LoginCredentials {
  username: string;
  password: string;
  rememberMe?: boolean;
}

export interface AuthState {
  // Estado
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  
  // Acciones
  login: (credentials: LoginCredentials) => Promise<any>;
  logout: () => void;
  refreshToken: () => Promise<void>;
  updateProfile: (userData: Partial<User>) => Promise<void>;
  clearError: () => void;
  setLoading: (loading: boolean) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      // Estado inicial
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // Login
      login: async (credentials: LoginCredentials) => {
        try {
          set({ isLoading: true, error: null });
          
          // Llamada a API REST (reemplaza XML)
          const response = await authApi.login(credentials);
          
          if (response.success) {
            const { accessToken, refreshToken, usuario } = response.data;
            
            set({
              user: usuario,
              accessToken,
              refreshToken,
              isAuthenticated: true,
              isLoading: false,
              error: null,
            });
            
            return response;
          } else {
            throw new Error(response.message || 'Error en el login');
          }
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || error.message || 'Error de conexión';
          set({
            user: null,
            accessToken: null,
            refreshToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: errorMessage,
          });
          throw error;
        }
      },

      // Logout
      logout: () => {
        try {
          // Llamar API de logout si hay token
          const { accessToken } = get();
          if (accessToken) {
            authApi.logout().catch(console.error);
          }
        } catch (error) {
          console.error('Error en logout API:', error);
        } finally {
          // Limpiar estado
          set({
            user: null,
            accessToken: null,
            refreshToken: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          });
        }
      },

      // Refresh Token
      refreshToken: async () => {
        try {
          const { refreshToken } = get();
          if (!refreshToken) {
            throw new Error('No hay refresh token disponible');
          }

          const response = await authApi.refreshToken({ refreshToken });
          
          if (response.success) {
            const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data;
            
            set({
              accessToken: newAccessToken,
              refreshToken: newRefreshToken,
            });
          } else {
            throw new Error('Error al refrescar token');
          }
        } catch (error) {
          // Si falla el refresh, hacer logout
          get().logout();
          throw error;
        }
      },

      // Update Profile
      updateProfile: async (userData: Partial<User>) => {
        try {
          set({ isLoading: true, error: null });
          
          const response = await authApi.updateProfile(userData);
          
          if (response.success) {
            set(state => ({
              user: state.user ? { ...state.user, ...userData } : null,
              isLoading: false,
            }));
          } else {
            throw new Error(response.message || 'Error al actualizar perfil');
          }
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || error.message || 'Error al actualizar perfil';
          set({
            isLoading: false,
            error: errorMessage,
          });
          throw error;
        }
      },

      // Clear Error
      clearError: () => set({ error: null }),

      // Set Loading
      setLoading: (loading: boolean) => set({ isLoading: loading }),
    }),
    {
      name: 'hermosa-cartagena-auth',
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

// Selectores para componentes
export const useAuth = () => useAuthStore();
export const useUser = () => useAuthStore(state => state.user);
export const useIsAuthenticated = () => useAuthStore(state => state.isAuthenticated);
export const useAuthLoading = () => useAuthStore(state => state.isLoading);
export const useAuthError = () => useAuthStore(state => state.error);

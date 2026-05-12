import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../stores/authStore';
import { toast } from 'react-hot-toast';
import { EyeIcon, EyeSlashIcon } from '@heroicons/react/24/outline';
import { GoogleIcon, FacebookIcon } from '../icons/SocialIcons';

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
 * COMPONENTE LOGIN FORM
 * =============================================
 * 
 * Componente moderno de login que reemplaza completamente
 * el sistema XML anterior por una interfaz React moderna.
 * 
 * Características:
 * - Formulario con validación en tiempo real
 * - Integración con APIs REST JSON
 * - Login social (Google, Facebook)
 * - Indicadores de carga y errores
 * - Diseño responsive con Tailwind CSS
 * 
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2024
 */

interface LoginFormData {
  username: string;
  password: string;
  rememberMe: boolean;
}

interface LoginFormProps {
  onSuccess?: () => void;
  onError?: (error: string) => void;
}

export const LoginForm: React.FC<LoginFormProps> = ({ onSuccess, onError }) => {
  const navigate = useNavigate();
  const { login, isLoading } = useAuthStore();
  const [showPassword, setShowPassword] = useState(false);
  const [isSocialLoading, setIsSocialLoading] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    setError,
    clearErrors
  } = useForm<LoginFormData>({
    mode: 'onChange',
    defaultValues: {
      username: '',
      password: '',
      rememberMe: false
    }
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      clearErrors();
      
      // Llamada a la API REST (reemplaza completamente el XML)
      const response = await login({
        username: data.username,
        password: data.password,
        rememberMe: data.rememberMe
      });

      if (response.success) {
        toast.success('¡Bienvenido al sistema!');
        onSuccess?.();
        navigate('/dashboard');
      } else {
        const errorMessage = response.message || 'Error en el login';
        setError('root', { message: errorMessage });
        onError?.(errorMessage);
        toast.error(errorMessage);
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Error de conexión';
      setError('root', { message: errorMessage });
      onError?.(errorMessage);
      toast.error(errorMessage);
    }
  };

  const handleSocialLogin = async (provider: 'google' | 'facebook') => {
    try {
      setIsSocialLoading(provider);
      
      // Redirección a OAuth2 (reemplaza el sistema XML)
      const oauthUrl = provider === 'google' 
        ? '/oauth2/authorization/google'
        : '/oauth2/authorization/facebook';
      
      window.location.href = oauthUrl;
    } catch (error) {
      toast.error(`Error al iniciar sesión con ${provider}`);
      setIsSocialLoading(null);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-600 to-purple-700 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        {/* Header */}
        <div>
          <div className="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-white/10 backdrop-blur-sm">
            <svg className="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2v2a2 2 0 104 0v-2a2 2 0 012-2h.5a2.5 2.5 0 002.5-2.5V3.935a2.5 2.5 0 00-2.5-2.5h-5A2.5 2.5 0 008 3.935z" />
            </svg>
          </div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-white">
            Hermosa Cartagena
          </h2>
          <p className="mt-2 text-center text-sm text-blue-100">
            Sistema de Gestión Turística Moderno
          </p>
        </div>

        {/* Form */}
        <form className="mt-8 space-y-6 bg-white/10 backdrop-blur-sm p-8 rounded-2xl shadow-2xl" onSubmit={handleSubmit(onSubmit)}>
          <div className="space-y-4">
            {/* Username */}
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-blue-100">
                Usuario
              </label>
              <div className="mt-1 relative">
                <input
                  {...register('username', {
                    required: 'El usuario es obligatorio',
                    minLength: {
                      value: 3,
                      message: 'El usuario debe tener al menos 3 caracteres'
                    }
                  })}
                  type="text"
                  autoComplete="username"
                  className={`block w-full px-4 py-3 border rounded-lg shadow-sm placeholder-blue-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white/20 text-white placeholder-blue-200 ${
                    errors.username ? 'border-red-500' : 'border-blue-400/30'
                  }`}
                  placeholder="Ingresa tu usuario"
                />
                {errors.username && (
                  <p className="mt-1 text-sm text-red-400">{errors.username.message}</p>
                )}
              </div>
            </div>

            {/* Password */}
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-blue-100">
                Contraseña
              </label>
              <div className="mt-1 relative">
                <input
                  {...register('password', {
                    required: 'La contraseña es obligatoria',
                    minLength: {
                      value: 6,
                      message: 'La contraseña debe tener al menos 6 caracteres'
                    }
                  })}
                  type={showPassword ? 'text' : 'password'}
                  autoComplete="current-password"
                  className={`block w-full px-4 py-3 pr-12 border rounded-lg shadow-sm placeholder-blue-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white/20 text-white placeholder-blue-200 ${
                    errors.password ? 'border-red-500' : 'border-blue-400/30'
                  }`}
                  placeholder="Ingresa tu contraseña"
                />
                <button
                  type="button"
                  className="absolute inset-y-0 right-0 pr-3 flex items-center"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeSlashIcon className="h-5 w-5 text-blue-300" />
                  ) : (
                    <EyeIcon className="h-5 w-5 text-blue-300" />
                  )}
                </button>
                {errors.password && (
                  <p className="mt-1 text-sm text-red-400">{errors.password.message}</p>
                )}
              </div>
            </div>

            {/* Remember Me */}
            <div className="flex items-center">
              <input
                {...register('rememberMe')}
                type="checkbox"
                className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-blue-400/30 rounded bg-white/20"
              />
              <label htmlFor="remember-me" className="ml-2 block text-sm text-blue-100">
                Recordarme
              </label>
            </div>
          </div>

          {/* Error General */}
          {errors.root && (
            <div className="rounded-md bg-red-500/20 p-4 backdrop-blur-sm">
              <p className="text-sm text-red-200">{errors.root.message}</p>
            </div>
          )}

          {/* Submit Button */}
          <div>
            <button
              type="submit"
              disabled={isLoading || !isValid}
              className="group relative w-full flex justify-center py-3 px-4 border border-transparent rounded-lg text-white bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
            >
              {isLoading ? (
                <>
                  <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Iniciando sesión...
                </>
              ) : (
                'Iniciar Sesión'
              )}
            </button>
          </div>

          {/* Divider */}
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-blue-400/30"></div>
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-2 bg-transparent text-blue-100">O continúa con</span>
            </div>
          </div>

          {/* Social Login */}
          <div className="grid grid-cols-2 gap-3">
            <button
              type="button"
              onClick={() => handleSocialLogin('google')}
              disabled={isSocialLoading === 'google'}
              className="w-full inline-flex justify-center py-2 px-4 border border-blue-400/30 rounded-lg shadow-sm text-sm font-medium text-blue-100 bg-white/10 backdrop-blur-sm hover:bg-white/20 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 transition-all duration-200"
            >
              {isSocialLoading === 'google' ? (
                <svg className="animate-spin -ml-1 mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              ) : (
                <GoogleIcon className="h-5 w-5" />
              )}
              <span className="ml-2">Google</span>
            </button>

            <button
              type="button"
              onClick={() => handleSocialLogin('facebook')}
              disabled={isSocialLoading === 'facebook'}
              className="w-full inline-flex justify-center py-2 px-4 border border-blue-400/30 rounded-lg shadow-sm text-sm font-medium text-blue-100 bg-white/10 backdrop-blur-sm hover:bg-white/20 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 transition-all duration-200"
            >
              {isSocialLoading === 'facebook' ? (
                <svg className="animate-spin -ml-1 mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              ) : (
                <FacebookIcon className="h-5 w-5" />
              )}
              <span className="ml-2">Facebook</span>
            </button>
          </div>

          {/* Links */}
          <div className="text-center space-y-2">
            <a
              href="/forgot-password"
              className="text-sm text-blue-100 hover:text-white transition-colors duration-200"
            >
              ¿Olvidaste tu contraseña?
            </a>
            <div>
              <span className="text-sm text-blue-100">¿No tienes cuenta? </span>
              <a
                href="/register"
                className="text-sm font-medium text-white hover:text-blue-200 transition-colors duration-200"
              >
                Regístrate aquí
              </a>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginForm;

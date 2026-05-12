package com.hermosacartagena.uddi;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CLIENTE DE DESCUBRIMIENTO DE SERVICIOS (UDDI)
 * =============================================
 *
 * Cliente que simula la consulta a un registro UDDI para descubrir
 * dinámicamente los endpoints de los servicios sin configuraciones estáticas.
 *
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2026
 */
public class ClienteDescubrimiento {
    
    // Simulación del registro UDDI (en producción sería una conexión real)
    private static final Map<String, String> registroUDDI = new HashMap<>();
    
    static {
        // Inicializar el registro simulado con los servicios disponibles
        registroUDDI.put("hotel-management", "http://localhost:8080/hermosacartagena/ws");
        registroUDDI.put("reservation-service", "http://localhost:8080/hermosacartagena/reservations");
        registroUDDI.put("catalog-service", "http://localhost:8080/hermosacartagena/catalog");
        
        // Endpoints seguros
        registroUDDI.put("hotel-management-secure", "https://api.hermosacartagena.com/ws");
        registroUDDI.put("reservation-service-secure", "https://api.hermosacartagena.com/reservations");
        registroUDDI.put("catalog-service-secure", "https://api.hermosacartagena.com/catalog");
    }
    
    /**
     * Descubre el endpoint de un servicio por su nombre lógico
     * 
     * @param nombreServicio Nombre lógico del servicio
     * @return URL del endpoint del servicio
     * @throws ServicioNoEncontradoException Si el servicio no está registrado
     */
    public URL descubrirServicio(String nombreServicio) throws ServicioNoEncontradoException {
        String endpoint = registroUDDI.get(nombreServicio);
        
        if (endpoint == null) {
            throw new ServicioNoEncontradoException(
                "Servicio '" + nombreServicio + "' no encontrado en el registro UDDI"
            );
        }
        
        try {
            return new URL(endpoint);
        } catch (Exception e) {
            throw new ServicioNoEncontradoException(
                "Endpoint inválido para servicio '" + nombreServicio + "': " + endpoint
            );
        }
    }
    
    /**
     * Descubre el endpoint preferido (seguro si está disponible, sino el estándar)
     * 
     * @param nombreServicio Nombre lógico del servicio
     * @return URL del endpoint preferido
     * @throws ServicioNoEncontradoException Si no hay endpoints disponibles
     */
    public URL descubrirServicioPreferido(String nombreServicio) throws ServicioNoEncontradoException {
        // Intentar primero el endpoint seguro
        String nombreSeguro = nombreServicio + "-secure";
        String endpointSeguro = registroUDDI.get(nombreSeguro);
        
        if (endpointSeguro != null) {
            try {
                return new URL(endpointSeguro);
            } catch (Exception e) {
                // Si el endpoint seguro falla, continuar con el estándar
            }
        }
        
        // Usar el endpoint estándar como fallback
        return descubrirServicio(nombreServicio);
    }
    
    /**
     * Lista todos los servicios disponibles en el registro
     * 
     * @return Map con nombre del servicio y su endpoint
     */
    public Map<String, String> listarServiciosDisponibles() {
        return new HashMap<>(registroUDDI);
    }
    
    /**
     * Verifica si un servicio está disponible en el registro
     * 
     * @param nombreServicio Nombre del servicio a verificar
     * @return true si el servicio está disponible
     */
    public boolean servicioDisponible(String nombreServicio) {
        return registroUDDI.containsKey(nombreServicio);
    }
    
    /**
     * Obtiene el WSDL de un servicio descubierto
     * 
     * @param nombreServicio Nombre del servicio
     * @return URL del WSDL del servicio
     * @throws ServicioNoEncontradoException Si el servicio no está disponible
     */
    public URL obtenerWSDL(String nombreServicio) throws ServicioNoEncontradoException {
        URL endpoint = descubrirServicio(nombreServicio);
        try {
            return new URL(endpoint.toString() + "?wsdl");
        } catch (Exception e) {
            throw new ServicioNoEncontradoException(
                "No se puede construir la URL del WSDL para: " + nombreServicio
            );
        }
    }
    
    /**
     * Registra un nuevo servicio en el UDDI (simulación)
     * 
     * @param nombreServicio Nombre lógico del servicio
     * @param endpoint URL del endpoint
     */
    public void registrarServicio(String nombreServicio, String endpoint) {
        registroUDDI.put(nombreServicio, endpoint);
        System.out.println("Servicio registrado: " + nombreServicio + " -> " + endpoint);
    }
    
    /**
     * Elimina un servicio del registro UDDI (simulación)
     * 
     * @param nombreServicio Nombre del servicio a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarServicio(String nombreServicio) {
        String eliminado = registroUDDI.remove(nombreServicio);
        if (eliminado != null) {
            System.out.println("Servicio eliminado: " + nombreServicio);
            return true;
        }
        return false;
    }
    
    /**
     * Método principal para demostrar el descubrimiento dinámico
     */
    public static void main(String[] args) {
        ClienteDescubrimiento cliente = new ClienteDescubrimiento();
        
        System.out.println("=== CLIENTE DE DESCUBRIMIENTO UDDI ===");
        System.out.println();
        
        // Listar servicios disponibles
        System.out.println("Servicios disponibles en el registro UDDI:");
        Map<String, String> servicios = cliente.listarServiciosDisponibles();
        servicios.forEach((nombre, endpoint) -> 
            System.out.println("  " + nombre + " -> " + endpoint)
        );
        System.out.println();
        
        // Descubrir servicio específico
        try {
            System.out.println("Descubriendo servicio de gestión hotelera...");
            URL endpoint = cliente.descubrirServicioPreferido("hotel-management");
            System.out.println("Endpoint encontrado: " + endpoint);
            
            // Obtener WSDL
            URL wsdl = cliente.obtenerWSDL("hotel-management");
            System.out.println("WSDL disponible en: " + wsdl);
            
        } catch (ServicioNoEncontradoException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        System.out.println();
        System.out.println("=== FIN DE LA DEMOSTRACIÓN ===");
    }
}

/**
 * Excepción personalizada para servicios no encontrados
 */
class ServicioNoEncontradoException extends Exception {
    public ServicioNoEncontradoException(String message) {
        super(message);
    }
}

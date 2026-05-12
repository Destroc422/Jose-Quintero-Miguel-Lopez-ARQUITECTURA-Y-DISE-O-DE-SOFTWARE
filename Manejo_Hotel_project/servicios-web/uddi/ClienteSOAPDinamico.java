package com.hermosacartagena.soap;

import com.hermosacartagena.uddi.ClienteDescubrimiento;
import com.hermosacartagena.uddi.ServicioNoEncontradoException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CLIENTE SOAP CON DESCUBRIMIENTO DINÁMICO
 * =============================================
 *
 * Cliente SOAP que descubre dinámicamente los servicios
 * mediante UDDI sin configuraciones estáticas.
 *
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2026
 */
public class ClienteSOAPDinamico {
    
    private ClienteDescubrimiento clienteUDDI;
    private String authToken;
    
    public ClienteSOAPDinamico() {
        this.clienteUDDI = new ClienteDescubrimiento();
        this.authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
    }
    
    /**
     * Realiza una llamada SOAP dinámica descubriendo el servicio
     * 
     * @param nombreServicio Nombre lógico del servicio
     * @param soapRequest Mensaje SOAP a enviar
     * @return Respuesta SOAP del servidor
     * @throws Exception Si hay error en la comunicación
     */
    public String invocarServicioDinamico(String nombreServicio, String soapRequest) throws Exception {
        // 1. Descubrir el endpoint dinámicamente
        URL endpoint = clienteUDDI.descubrirServicioPreferido(nombreServicio);
        System.out.println("Endpoint descubierto: " + endpoint);
        
        // 2. Realizar la llamada SOAP
        return realizarLlamadaSOAP(endpoint, soapRequest);
    }
    
    /**
     * Realiza la llamada HTTP SOAP al endpoint
     */
    private String realizarLlamadaSOAP(URL endpoint, String soapRequest) throws Exception {
        HttpURLConnection connection = null;
        
        try {
            // Configurar conexión HTTP
            connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction", "\"http://hermosacartagena.com/ws/operacion\"");
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            
            // Enviar la solicitud SOAP
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = soapRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Procesar la respuesta
            int responseCode = connection.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return leerRespuesta(connection);
            } else {
                String errorResponse = leerError(connection);
                throw new Exception("Error en la llamada SOAP: " + responseCode + " - " + errorResponse);
            }
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Lee la respuesta exitosa del servidor
     */
    private String leerRespuesta(HttpURLConnection connection) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
    
    /**
     * Lee la respuesta de error del servidor
     */
    private String leerError(HttpURLConnection connection) throws IOException {
        InputStream errorStream = connection.getErrorStream();
        if (errorStream != null) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
                
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        }
        return "Error sin detalles";
    }
    
    /**
     * Ejemplo de registro de cliente usando descubrimiento dinámico
     */
    public void registrarClienteDinamico() {
        try {
            // Construir mensaje SOAP para registrar cliente
            String soapRequest = construirMensajeRegistrarCliente();
            
            // Invocar el servicio dinámicamente
            String respuesta = invocarServicioDinamico("hotel-management", soapRequest);
            
            System.out.println("Respuesta SOAP recibida:");
            System.out.println(respuesta);
            
            // Aquí se procesaría la respuesta SOAP
            
        } catch (ServicioNoEncontradoException e) {
            System.err.println("Servicio no encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al invocar servicio: " + e.getMessage());
        }
    }
    
    /**
     * Construye el mensaje SOAP para registrar un cliente
     */
    private String construirMensajeRegistrarCliente() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
               "               xmlns:tns=\"http://hermosacartagena.com/ws\">\n" +
               "    <soap:Header>\n" +
               "        <tns:Autenticacion>\n" +
               "            <tns:usuario>cliente@ejemplo.com</tns:usuario>\n" +
               "            <tns:token>" + authToken + "</tns:token>\n" +
               "            <tns:timestamp>2026-05-11T14:30:00Z</tns:timestamp>\n" +
               "        </tns:Autenticacion>\n" +
               "    </soap:Header>\n" +
               "    <soap:Body>\n" +
               "        <tns:registrarClienteRequest>\n" +
               "            <tns:cliente>\n" +
               "                <tns:nombre>María</tns:nombre>\n" +
               "                <tns:apellido>García</tns:apellido>\n" +
               "                <tns:email>maria.garcia@email.com</tns:email>\n" +
               "                <tns:telefono>+57 5 6641234</tns:telefono>\n" +
               "                <tns:direccion>Carrera 1 #45-67, Centro Histórico</tns:direccion>\n" +
               "                <tns:tipoDocumento>CC</tns:tipoDocumento>\n" +
               "                <tns:numeroDocumento>987654321</tns:numeroDocumento>\n" +
               "                <tns:puntosLealtad>0</tns:puntosLealtad>\n" +
               "                <tns:estado>activo</tns:estado>\n" +
               "            </tns:cliente>\n" +
               "        </tns:registrarClienteRequest>\n" +
               "    </soap:Body>\n" +
               "</soap:Envelope>";
    }
    
    /**
     * Ejemplo de consulta de habitaciones disponibles
     */
    public void consultarHabitacionesDisponibles() {
        try {
            String soapRequest = construirMensajeConsultarHabitaciones();
            String respuesta = invocarServicioDinamico("catalog-service", soapRequest);
            
            System.out.println("Respuesta de habitaciones disponibles:");
            System.out.println(respuesta);
            
        } catch (Exception e) {
            System.err.println("Error al consultar habitaciones: " + e.getMessage());
        }
    }
    
    /**
     * Construye el mensaje SOAP para consultar habitaciones disponibles
     */
    private String construirMensajeConsultarHabitaciones() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
               "               xmlns:tns=\"http://hermosacartagena.com/ws\">\n" +
               "    <soap:Header>\n" +
               "        <tns:Autenticacion>\n" +
               "            <tns:usuario>guest@hermosacartagena.com</tns:usuario>\n" +
               "            <tns:token>" + authToken + "</tns:token>\n" +
               "            <tns:timestamp>2026-05-11T14:40:00Z</tns:timestamp>\n" +
               "        </tns:Autenticacion>\n" +
               "    </soap:Header>\n" +
               "    <soap:Body>\n" +
               "        <tns:listarHabitacionesDisponiblesRequest>\n" +
               "            <tns:fechaInicio>2026-06-15</tns:fechaInicio>\n" +
               "            <tns:fechaFin>2026-06-18</tns:fechaFin>\n" +
               "            <tns:capacidad>2</tns:capacidad>\n" +
               "        </tns:listarHabitacionesDisponiblesRequest>\n" +
               "    </soap:Body>\n" +
               "</soap:Envelope>";
    }
    
    /**
     * Método principal para demostrar el cliente dinámico
     */
    public static void main(String[] args) {
        ClienteSOAPDinamico cliente = new ClienteSOAPDinamico();
        
        System.out.println("=== CLIENTE SOAP DINÁMICO ===");
        System.out.println();
        
        // Demostrar registro de cliente
        System.out.println("1. Registrando cliente dinámicamente...");
        cliente.registrarClienteDinamico();
        System.out.println();
        
        // Demostrar consulta de habitaciones
        System.out.println("2. Consultando habitaciones disponibles...");
        cliente.consultarHabitacionesDisponibles();
        System.out.println();
        
        System.out.println("=== FIN DE LA DEMOSTRACIÓN ===");
    }
}

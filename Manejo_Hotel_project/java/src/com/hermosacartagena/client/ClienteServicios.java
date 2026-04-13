package com.hermosacartagena.client;

import com.hermosacartagena.model.ServicioTuristico;
import com.hermosacartagena.stub.ServicioStub;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CLIENTE DE SERVICIOS (RMI)
 * =============================================
 * 
 * Este cliente se conecta al servidor RMI para gestionar
 * servicios turísticos de forma remota.
 * 
 * @author Sistema de Gestión Turística
 * @version 1.0
 */
public class ClienteServicios {
    
    // Configuración del cliente
    private static final String SERVER_HOST = "localhost";
    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "ServicioTuristico";
    
    // Referencia al stub remoto
    private ServicioStub servicioStub;
    
    // Scanner para entrada de usuario
    private Scanner scanner;
    
    /**
     * Constructor del cliente
     */
    public ClienteServicios() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Método principal para iniciar el cliente
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        ClienteServicios cliente = new ClienteServicios();
        
        try {
            System.out.println("=== CLIENTE DE SERVICIOS TURÍSTICOS ===");
            System.out.println("Iniciando conexión con el servidor...");
            
            // Conectar al servidor
            if (cliente.conectarServidor()) {
                System.out.println("Conexión establecida exitosamente");
                
                // Probar conexión
                cliente.probarConexion();
                
                // Mostrar menú principal
                cliente.mostrarMenuPrincipal();
            } else {
                System.err.println("No se pudo establecer conexión con el servidor");
            }
            
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cliente.scanner != null) {
                cliente.scanner.close();
            }
        }
    }
    
    /**
     * Conecta con el servidor RMI
     * @return true si la conexión fue exitosa
     */
    private boolean conectarServidor() {
        try {
            System.out.println("Buscando registro RMI en " + SERVER_HOST + ":" + RMI_PORT);
            
            // Obtener el registro RMI
            Registry registry = LocateRegistry.getRegistry(SERVER_HOST, RMI_PORT);
            
            // Buscar el servicio remoto
            servicioStub = (ServicioStub) registry.lookup(SERVICE_NAME);
            
            System.out.println("Servicio remoto encontrado: " + SERVICE_NAME);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Prueba la conexión con el servidor
     */
    private void probarConexion() {
        try {
            String resultado = servicioStub.probarConexion();
            System.out.println("Prueba de conexión: " + resultado);
            
            if ("OK".equals(resultado)) {
                System.out.println("¡La conexión con el servidor funciona correctamente!");
            } else {
                System.err.println("Advertencia: La conexión puede tener problemas");
            }
            
        } catch (Exception e) {
            System.err.println("Error al probar conexión: " + e.getMessage());
        }
    }
    
    /**
     * Muestra el menú principal de la aplicación
     */
    private void mostrarMenuPrincipal() {
        boolean salir = false;
        
        while (!salir) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Listar todos los servicios");
            System.out.println("2. Buscar servicios por tipo");
            System.out.println("3. Buscar servicios por nombre");
            System.out.println("4. Obtener servicio por ID");
            System.out.println("5. Crear nuevo servicio");
            System.out.println("6. Actualizar servicio");
            System.out.println("7. Eliminar servicio");
            System.out.println("8. Verificar disponibilidad");
            System.out.println("9. Obtener servicios disponibles");
            System.out.println("10. Ver servicios populares");
            System.out.println("11. Ver estadísticas");
            System.out.println("12. Probar conexión");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                
                switch (opcion) {
                    case 1:
                        listarTodosLosServicios();
                        break;
                    case 2:
                        buscarServiciosPorTipo();
                        break;
                    case 3:
                        buscarServiciosPorNombre();
                        break;
                    case 4:
                        obtenerServicioPorId();
                        break;
                    case 5:
                        crearServicio();
                        break;
                    case 6:
                        actualizarServicio();
                        break;
                    case 7:
                        eliminarServicio();
                        break;
                    case 8:
                        verificarDisponibilidad();
                        break;
                    case 9:
                        obtenerServiciosDisponibles();
                        break;
                    case 10:
                        verServiciosPopulares();
                        break;
                    case 11:
                        verEstadisticas();
                        break;
                    case 12:
                        probarConexion();
                        break;
                    case 0:
                        salir = true;
                        System.out.println("Cerrando cliente...");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido.");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            
            if (!salir) {
                System.out.print("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
        }
    }
    
    /**
     * Lista todos los servicios turísticos
     */
    private void listarTodosLosServicios() {
        try {
            System.out.println("\n=== LISTA DE TODOS LOS SERVICIOS ===");
            
            List<ServicioTuristico> servicios = servicioStub.obtenerTodosLosServicios();
            
            if (servicios.isEmpty()) {
                System.out.println("No hay servicios disponibles.");
            } else {
                System.out.println("Se encontraron " + servicios.size() + " servicios:\n");
                
                for (ServicioTuristico servicio : servicios) {
                    mostrarServicio(servicio);
                    System.out.println("---");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al listar servicios: " + e.getMessage());
        }
    }
    
    /**
     * Busca servicios por tipo
     */
    private void buscarServiciosPorTipo() {
        try {
            System.out.println("\n=== BUSCAR SERVICIOS POR TIPO ===");
            System.out.println("Tipos disponibles: tour, hospedaje, transporte, alimentacion, actividad");
            System.out.print("Ingrese el tipo de servicio: ");
            String tipo = scanner.nextLine().trim();
            
            if (tipo.isEmpty()) {
                System.out.println("El tipo de servicio no puede estar vacío.");
                return;
            }
            
            List<ServicioTuristico> servicios = servicioStub.buscarServiciosPorTipo(tipo);
            
            if (servicios.isEmpty()) {
                System.out.println("No se encontraron servicios del tipo: " + tipo);
            } else {
                System.out.println("Se encontraron " + servicios.size() + " servicios del tipo '" + tipo + "':\n");
                
                for (ServicioTuristico servicio : servicios) {
                    mostrarServicio(servicio);
                    System.out.println("---");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al buscar servicios por tipo: " + e.getMessage());
        }
    }
    
    /**
     * Busca servicios por nombre
     */
    private void buscarServiciosPorNombre() {
        try {
            System.out.println("\n=== BUSCAR SERVICIOS POR NOMBRE ===");
            System.out.print("Ingrese término de búsqueda: ");
            String termino = scanner.nextLine().trim();
            
            if (termino.isEmpty()) {
                System.out.println("El término de búsqueda no puede estar vacío.");
                return;
            }
            
            List<ServicioTuristico> servicios = servicioStub.buscarServiciosPorNombre(termino);
            
            if (servicios.isEmpty()) {
                System.out.println("No se encontraron servicios con: " + termino);
            } else {
                System.out.println("Se encontraron " + servicios.size() + " servicios con '" + termino + "':\n");
                
                for (ServicioTuristico servicio : servicios) {
                    mostrarServicio(servicio);
                    System.out.println("---");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error al buscar servicios por nombre: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene un servicio por su ID
     */
    private void obtenerServicioPorId() {
        try {
            System.out.println("\n=== OBTENER SERVICIO POR ID ===");
            System.out.print("Ingrese el ID del servicio: ");
            
            int id = Integer.parseInt(scanner.nextLine());
            
            ServicioTuristico servicio = servicioStub.obtenerServicioPorId(id);
            
            if (servicio == null) {
                System.out.println("No se encontró un servicio con ID: " + id);
            } else {
                System.out.println("Servicio encontrado:\n");
                mostrarServicio(servicio);
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingrese un número válido para el ID.");
        } catch (Exception e) {
            System.err.println("Error al obtener servicio por ID: " + e.getMessage());
        }
    }
    
    /**
     * Crea un nuevo servicio
     */
    private void crearServicio() {
        try {
            System.out.println("\n=== CREAR NUEVO SERVICIO ===");
            
            ServicioTuristico servicio = new ServicioTuristico();
            
            // Solicitar datos del servicio
            System.out.print("ID del Proveedor: ");
            servicio.setIdProveedor(Integer.parseInt(scanner.nextLine()));
            
            System.out.print("Nombre del Servicio: ");
            servicio.setNombreServicio(scanner.nextLine().trim());
            
            System.out.print("Descripción: ");
            servicio.setDescripcion(scanner.nextLine().trim());
            
            System.out.print("Tipo de Servicio (tour/hospedaje/transporte/alimentacion/actividad): ");
            servicio.setTipoServicio(scanner.nextLine().trim());
            
            System.out.print("Precio Base: ");
            servicio.setPrecioBase(Double.parseDouble(scanner.nextLine()));
            
            System.out.print("Duración (horas): ");
            servicio.setDuracionHoras(Double.parseDouble(scanner.nextLine()));
            
            System.out.print("Capacidad Máxima: ");
            servicio.setCapacidadMaxima(Integer.parseInt(scanner.nextLine()));
            
            System.out.print("Ubicación: ");
            servicio.setUbicacion(scanner.nextLine().trim());
            
            System.out.print("Requisitos (opcional): ");
            String requisitos = scanner.nextLine().trim();
            servicio.setRequisitos(requisitos.isEmpty() ? null : requisitos);
            
            System.out.print("¿Qué incluye? (opcional): ");
            String incluye = scanner.nextLine().trim();
            servicio.setIncluye(incluye.isEmpty() ? null : incluye);
            
            System.out.print("¿Qué no incluye? (opcional): ");
            String noIncluye = scanner.nextLine().trim();
            servicio.setNoIncluye(noIncluye.isEmpty() ? null : noIncluye);
            
            // Validar y crear
            if (!servicio.validar()) {
                System.out.println("Error: Los datos del servicio no son válidos.");
                return;
            }
            
            boolean exito = servicioStub.crearServicio(servicio);
            
            if (exito) {
                System.out.println("¡Servicio creado exitosamente!");
                System.out.println("ID asignado: " + servicio.getIdServicio());
            } else {
                System.out.println("No se pudo crear el servicio.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un número válido para los campos numéricos.");
        } catch (Exception e) {
            System.err.println("Error al crear servicio: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza un servicio existente
     */
    private void actualizarServicio() {
        try {
            System.out.println("\n=== ACTUALIZAR SERVICIO ===");
            
            System.out.print("Ingrese el ID del servicio a actualizar: ");
            int id = Integer.parseInt(scanner.nextLine());
            
            // Primero obtener el servicio existente
            ServicioTuristico servicio = servicioStub.obtenerServicioPorId(id);
            
            if (servicio == null) {
                System.out.println("No se encontró un servicio con ID: " + id);
                return;
            }
            
            System.out.println("Servicio actual:");
            mostrarServicio(servicio);
            
            System.out.println("\nIngrese los nuevos datos (deje en blanco para mantener el valor actual):");
            
            // Solicitar nuevos datos
            System.out.print("Nombre del Servicio [" + servicio.getNombreServicio() + "]: ");
            String nombre = scanner.nextLine().trim();
            if (!nombre.isEmpty()) {
                servicio.setNombreServicio(nombre);
            }
            
            System.out.print("Descripción [" + servicio.getDescripcion() + "]: ");
            String descripcion = scanner.nextLine().trim();
            if (!descripcion.isEmpty()) {
                servicio.setDescripcion(descripcion);
            }
            
            System.out.print("Precio Base [" + servicio.getPrecioBase() + "]: ");
            String precioStr = scanner.nextLine().trim();
            if (!precioStr.isEmpty()) {
                servicio.setPrecioBase(Double.parseDouble(precioStr));
            }
            
            System.out.print("Capacidad Máxima [" + servicio.getCapacidadMaxima() + "]: ");
            String capacidadStr = scanner.nextLine().trim();
            if (!capacidadStr.isEmpty()) {
                servicio.setCapacidadMaxima(Integer.parseInt(capacidadStr));
            }
            
            System.out.print("Ubicación [" + servicio.getUbicacion() + "]: ");
            String ubicacion = scanner.nextLine().trim();
            if (!ubicacion.isEmpty()) {
                servicio.setUbicacion(ubicacion);
            }
            
            System.out.print("Estado (activo/inactivo) [" + servicio.getEstado() + "]: ");
            String estado = scanner.nextLine().trim();
            if (!estado.isEmpty()) {
                servicio.setEstado(estado);
            }
            
            // Validar y actualizar
            if (!servicio.validar()) {
                System.out.println("Error: Los datos del servicio no son válidos.");
                return;
            }
            
            boolean exito = servicioStub.actualizarServicio(servicio);
            
            if (exito) {
                System.out.println("¡Servicio actualizado exitosamente!");
            } else {
                System.out.println("No se pudo actualizar el servicio.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un número válido para los campos numéricos.");
        } catch (Exception e) {
            System.err.println("Error al actualizar servicio: " + e.getMessage());
        }
    }
    
    /**
     * Elimina (desactiva) un servicio
     */
    private void eliminarServicio() {
        try {
            System.out.println("\n=== ELIMINAR SERVICIO ===");
            
            System.out.print("Ingrese el ID del servicio a eliminar: ");
            int id = Integer.parseInt(scanner.nextLine());
            
            // Mostrar información del servicio antes de eliminar
            ServicioTuristico servicio = servicioStub.obtenerServicioPorId(id);
            
            if (servicio == null) {
                System.out.println("No se encontró un servicio con ID: " + id);
                return;
            }
            
            System.out.println("Servicio a eliminar:");
            mostrarServicio(servicio);
            
            System.out.print("\n¿Está seguro de que desea eliminar este servicio? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();
            
            if ("S".equals(confirmacion)) {
                boolean exito = servicioStub.eliminarServicio(id);
                
                if (exito) {
                    System.out.println("¡Servicio eliminado exitosamente!");
                } else {
                    System.out.println("No se pudo eliminar el servicio.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un número válido para el ID.");
        } catch (Exception e) {
            System.err.println("Error al eliminar servicio: " + e.getMessage());
        }
    }
    
    /**
     * Verifica la disponibilidad de un servicio
     */
    private void verificarDisponibilidad() {
        try {
            System.out.println("\n=== VERIFICAR DISPONIBILIDAD ===");
            
            System.out.print("ID del Servicio: ");
            int idServicio = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Fecha (YYYY-MM-DD): ");
            String fecha = scanner.nextLine().trim();
            
            System.out.print("Cantidad de Personas: ");
            int cantidadPersonas = Integer.parseInt(scanner.nextLine());
            
            boolean disponible = servicioStub.verificarDisponibilidad(idServicio, fecha, cantidadPersonas);
            
            if (disponible) {
                System.out.println("¡El servicio está disponible para " + cantidadPersonas + 
                                 " personas el " + fecha + "!");
            } else {
                System.out.println("El servicio NO está disponible para " + cantidadPersonas + 
                                 " personas el " + fecha + ".");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese números válidos para ID y cantidad de personas.");
        } catch (Exception e) {
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene servicios disponibles para un rango de fechas
     */
    private void obtenerServiciosDisponibles() {
        try {
            System.out.println("\n=== SERVICIOS DISPONIBLES ===");
            
            System.out.print("Fecha de Inicio (YYYY-MM-DD): ");
            String fechaInicio = scanner.nextLine().trim();
            
            System.out.print("Fecha de Fin (YYYY-MM-DD): ");
            String fechaFin = scanner.nextLine().trim();
            
            System.out.print("Cantidad de Personas: ");
            int cantidadPersonas = Integer.parseInt(scanner.nextLine());
            
            List<ServicioTuristico> servicios = servicioStub.obtenerServiciosDisponibles(
                fechaInicio, fechaFin, cantidadPersonas);
            
            if (servicios.isEmpty()) {
                System.out.println("No hay servicios disponibles para " + cantidadPersonas + 
                                 " personas entre " + fechaInicio + " y " + fechaFin + ".");
            } else {
                System.out.println("Se encontraron " + servicios.size() + " servicios disponibles:\n");
                
                for (ServicioTuristico servicio : servicios) {
                    mostrarServicio(servicio);
                    System.out.println("---");
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un número válido para la cantidad de personas.");
        } catch (Exception e) {
            System.err.println("Error al obtener servicios disponibles: " + e.getMessage());
        }
    }
    
    /**
     * Muestra los servicios más populares
     */
    private void verServiciosPopulares() {
        try {
            System.out.println("\n=== SERVICIOS POPULARES ===");
            
            System.out.print("Límite de resultados (default 10): ");
            String limiteStr = scanner.nextLine().trim();
            int limite = limiteStr.isEmpty() ? 10 : Integer.parseInt(limiteStr);
            
            List<ServicioTuristico> servicios = servicioStub.obtenerServiciosPopulares(limite);
            
            if (servicios.isEmpty()) {
                System.out.println("No hay servicios populares disponibles.");
            } else {
                System.out.println("Top " + servicios.size() + " servicios más populares:\n");
                
                int ranking = 1;
                for (ServicioTuristico servicio : servicios) {
                    System.out.println("Ranking #" + ranking++);
                    mostrarServicio(servicio);
                    System.out.println("---");
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un número válido para el límite.");
        } catch (Exception e) {
            System.err.println("Error al obtener servicios populares: " + e.getMessage());
        }
    }
    
    /**
     * Muestra estadísticas de servicios
     */
    private void verEstadisticas() {
        try {
            System.out.println("\n=== ESTADÍSTICAS DE SERVICIOS ===");
            
            int[] estadisticas = servicioStub.obtenerEstadisticasServicios();
            
            System.out.println("Total de servicios: " + estadisticas[0]);
            System.out.println("Servicios activos: " + estadisticas[1]);
            System.out.println("Servicios inactivos: " + estadisticas[2]);
            System.out.println("Tours: " + estadisticas[3]);
            System.out.println("Hospedaje: " + estadisticas[4]);
            System.out.println("Transporte: " + estadisticas[5]);
            System.out.println("Alimentación: " + estadisticas[6]);
            System.out.println("Actividades: " + estadisticas[7]);
            
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
        }
    }
    
    /**
     * Muestra la información de un servicio de forma formateada
     * @param servicio Servicio a mostrar
     */
    private void mostrarServicio(ServicioTuristico servicio) {
        System.out.println("ID: " + servicio.getIdServicio());
        System.out.println("Nombre: " + servicio.getNombreServicio());
        System.out.println("Tipo: " + servicio.getTipoServicio());
        System.out.println("Descripción: " + servicio.getDescripcion());
        System.out.println("Precio Base: $" + servicio.getPrecioBase());
        System.out.println("Duración: " + servicio.getDuracionHoras() + " horas");
        System.out.println("Capacidad Máxima: " + servicio.getCapacidadMaxima() + " personas");
        System.out.println("Ubicación: " + servicio.getUbicacion());
        
        if (servicio.getRequisitos() != null && !servicio.getRequisitos().isEmpty()) {
            System.out.println("Requisitos: " + servicio.getRequisitos());
        }
        
        if (servicio.getIncluye() != null && !servicio.getIncluye().isEmpty()) {
            System.out.println("Incluye: " + servicio.getIncluye());
        }
        
        if (servicio.getNoIncluye() != null && !servicio.getNoIncluye().isEmpty()) {
            System.out.println("No incluye: " + servicio.getNoIncluye());
        }
        
        System.out.println("Estado: " + servicio.getEstado());
        System.out.println("Fecha de Creación: " + servicio.getFechaCreacion());
        
        if (servicio.getNombreProveedor() != null) {
            System.out.println("Proveedor: " + servicio.getNombreProveedor());
            if (servicio.getContactoPrincipal() != null) {
                System.out.println("Contacto: " + servicio.getContactoPrincipal());
            }
            if (servicio.getTelefonoProveedor() != null) {
                System.out.println("Teléfono: " + servicio.getTelefonoProveedor());
            }
        }
    }
}

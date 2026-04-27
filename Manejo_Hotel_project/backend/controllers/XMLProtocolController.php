<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CONTROLADOR: PROTOCOLO XML
 * =============================================
 * 
 * Este controlador maneja el protocolo de comunicación XML
 * para el intercambio de información entre cliente y servidor.
 */

require_once __DIR__ . '/../models/User.php';
require_once __DIR__ . '/../config/database.php';

class XMLProtocolController {
    private $user;
    private $xsdPath;
    private $dtdPath;
    
    /**
     * Constructor
     */
    public function __construct() {
        $this->user = new User();
        $this->xsdPath = __DIR__ . '/../../xml_protocol/protocol.xsd';
        $this->dtdPath = __DIR__ . '/../../xml_protocol/protocol.dtd';
        
        // Iniciar sesión si no está activa
        if (session_status() == PHP_SESSION_NONE) {
            session_start();
        }
    }
    
    /**
     * Procesar solicitud XML
     * @param string $xmlString Solicitud XML recibida
     * @return string Respuesta XML
     */
    public function processRequest($xmlString) {
        try {
            // Validar estructura XML
            if (!$this->validateXML($xmlString)) {
                return $this->createErrorResponse(400, 'Estructura XML inválida', 'El mensaje no cumple con el protocolo definido');
            }
            
            // Cargar XML
            $xml = simplexml_load_string($xmlString);
            if (!$xml) {
                return $this->createErrorResponse(400, 'Error al parsear XML', 'No se pudo interpretar el mensaje XML');
            }
            
            // Extraer información de la solicitud
            $request = $xml->request;
            $requestId = (string)$request['id'];
            $clientId = (string)$request['client_id'];
            $operation = (string)$request->operation;
            $parameters = [];
            
            // Extraer parámetros usando XPath
            if ($request->parameters) {
                foreach ($request->parameters->parameter as $param) {
                    $paramName = (string)$param['name'];
                    $paramValue = (string)$param;
                    $paramType = (string)$param['type'] ?? 'string';
                    
                    // Convertir tipo de dato
                    switch ($paramType) {
                        case 'integer':
                            $paramValue = (int)$paramValue;
                            break;
                        case 'boolean':
                            $paramValue = filter_var($paramValue, FILTER_VALIDATE_BOOLEAN);
                            break;
                        case 'date':
                            $paramValue = new DateTime($paramValue);
                            break;
                    }
                    
                    $parameters[$paramName] = $paramValue;
                }
            }
            
            // Ejecutar operación correspondiente
            $startTime = microtime(true);
            $result = $this->executeOperation($operation, $parameters);
            $processingTime = round(microtime(true) - $startTime, 3);
            
            // Generar respuesta XML
            return $this->createResponse($requestId, $result, $processingTime);
            
        } catch (Exception $e) {
            return $this->createErrorResponse(500, 'Error interno del servidor', $e->getMessage());
        }
    }
    
    /**
     * Validar XML contra XSD y DTD
     * @param string $xmlString XML a validar
     * @return bool
     */
    private function validateXML($xmlString) {
        // Validar contra XSD
        $dom = new DOMDocument();
        $dom->loadXML($xmlString);
        
        if (!$dom->schemaValidate($this->xsdPath)) {
            return false;
        }
        
        // Validar contra DTD
        $internalErrors = libxml_use_internal_errors(true);
        $dom = new DOMDocument();
        $dom->loadXML($xmlString);
        
        if (!$dom->validate()) {
            libxml_use_internal_errors($internalErrors);
            return false;
        }
        
        libxml_use_internal_errors($internalErrors);
        return true;
    }
    
    /**
     * Ejecutar operación solicitada
     * @param string $operation Operación a ejecutar
     * @param array $parameters Parámetros de la operación
     * @return array Resultado de la operación
     */
    private function executeOperation($operation, $parameters) {
        switch ($operation) {
            case 'login':
                return $this->handleLogin($parameters);
            case 'register':
                return $this->handleRegister($parameters);
            case 'logout':
                return $this->handleLogout($parameters);
            case 'get_hotels':
                return $this->handleGetHotels($parameters);
            case 'get_rooms':
                return $this->handleGetRooms($parameters);
            case 'book_room':
                return $this->handleBookRoom($parameters);
            case 'cancel_booking':
                return $this->handleCancelBooking($parameters);
            case 'get_user_data':
                return $this->handleGetUserData($parameters);
            case 'update_profile':
                return $this->handleUpdateProfile($parameters);
            default:
                return ['success' => false, 'message' => 'Operación no reconocida: ' . $operation];
        }
    }
    
    /**
     * Manejar login
     */
    private function handleLogin($parameters) {
        $username = $parameters['username'] ?? '';
        $password = $parameters['password'] ?? '';
        $remember = $parameters['remember'] ?? false;
        
        if (empty($username) || empty($password)) {
            return ['success' => false, 'message' => 'Usuario y contraseña son requeridos'];
        }
        
        $loginResult = $this->user->login($username, $password);
        
        if ($loginResult) {
            if ($remember) {
                // Configurar cookie de recordarme
                $token = bin2hex(random_bytes(32));
                setcookie('remember_token', $token, time() + (30 * 24 * 60 * 60), '/');
            }
            
            // Establecer sesión
            $_SESSION['user_id'] = $this->user->id_usuario;
            $_SESSION['username'] = $this->user->username;
            $_SESSION['role'] = $this->user->nombre_rol;
            
            return [
                'success' => true,
                'user' => [
                    'id' => $this->user->id_usuario,
                    'username' => $this->user->username,
                    'email' => $this->user->email,
                    'name' => $this->user->nombre_completo,
                    'role' => $this->user->nombre_rol,
                    'created_at' => $this->user->fecha_creacion
                ],
                'message' => 'Login exitoso'
            ];
        } else {
            return [
                'success' => false,
                'message' => 'Credenciales inválidas'
            ];
        }
    }
    
    /**
     * Manejar registro
     */
    private function handleRegister($parameters) {
        $username = $parameters['username'] ?? '';
        $email = $parameters['email'] ?? '';
        $password = $parameters['password'] ?? '';
        $name = $parameters['name'] ?? '';
        
        if (empty($username) || empty($email) || empty($password) || empty($name)) {
            return ['success' => false, 'message' => 'Todos los campos son requeridos'];
        }
        
        // Asignar propiedades al modelo
        $this->user->username = $username;
        $this->user->email = $email;
        $this->user->password = $password;
        $this->user->nombre_completo = $name;
        $this->user->id_rol = 2; // Rol por defecto: cliente
        
        $result = $this->user->create();
        
        if ($result) {
            return [
                'success' => true,
                'message' => 'Usuario registrado correctamente',
                'user' => [
                    'id' => $this->user->id_usuario,
                    'username' => $this->user->username,
                    'email' => $this->user->email,
                    'name' => $this->user->nombre_completo,
                    'role' => 'cliente'
                ]
            ];
        } else {
            return ['success' => false, 'message' => 'Error al registrar usuario'];
        }
    }
    
    /**
     * Manejar logout
     */
    private function handleLogout($parameters) {
        session_destroy();
        setcookie('remember_token', '', time() - 3600, '/');
        
        return ['success' => true, 'message' => 'Sesión cerrada correctamente'];
    }
    
    /**
     * Manejar obtención de hoteles
     */
    private function handleGetHotels($parameters) {
        // Simulación de datos de hoteles
        $hotels = [
            [
                'id' => '1',
                'name' => 'Hotel Hermosa Cartagena',
                'description' => 'Hotel de lujo en el centro histórico',
                'location' => 'Cartagena, Colombia',
                'rating' => 4.8,
                'rooms' => [
                    [
                        'id' => '101',
                        'number' => '101',
                        'type' => 'Suite',
                        'capacity' => 2,
                        'price_per_night' => 150.00,
                        'available' => true
                    ],
                    [
                        'id' => '102',
                        'number' => '102',
                        'type' => 'Doble',
                        'capacity' => 4,
                        'price_per_night' => 120.00,
                        'available' => false
                    ]
                ]
            ]
        ];
        
        return ['success' => true, 'hotels' => $hotels];
    }
    
    /**
     * Manejar obtención de habitaciones
     */
    private function handleGetRooms($parameters) {
        $hotelId = $parameters['hotel_id'] ?? '';
        
        if (empty($hotelId)) {
            return ['success' => false, 'message' => 'ID del hotel es requerido'];
        }
        
        // Simulación de datos
        $rooms = [
            [
                'id' => '101',
                'number' => '101',
                'type' => 'Suite',
                'capacity' => 2,
                'price_per_night' => 150.00,
                'available' => true
            ],
            [
                'id' => '102',
                'number' => '102',
                'type' => 'Doble',
                'capacity' => 4,
                'price_per_night' => 120.00,
                'available' => false
            ]
        ];
        
        return ['success' => true, 'rooms' => $rooms];
    }
    
    /**
     * Manejar reserva de habitación
     */
    private function handleBookRoom($parameters) {
        $roomId = $parameters['room_id'] ?? '';
        $checkIn = $parameters['check_in'] ?? '';
        $checkOut = $parameters['check_out'] ?? '';
        
        if (empty($roomId) || empty($checkIn) || empty($checkOut)) {
            return ['success' => false, 'message' => 'Todos los campos son requeridos'];
        }
        
        // Simulación de reserva
        $booking = [
            'id' => 'BK' . time(),
            'room_id' => $roomId,
            'check_in' => $checkIn,
            'check_out' => $checkOut,
            'total_price' => 300.00,
            'status' => 'confirmed'
        ];
        
        return ['success' => true, 'booking' => $booking];
    }
    
    /**
     * Manejar cancelación de reserva
     */
    private function handleCancelBooking($parameters) {
        $bookingId = $parameters['booking_id'] ?? '';
        
        if (empty($bookingId)) {
            return ['success' => false, 'message' => 'ID de reserva es requerido'];
        }
        
        return ['success' => true, 'message' => 'Reserva cancelada correctamente'];
    }
    
    /**
     * Manejar obtención de datos de usuario
     */
    private function handleGetUserData($parameters) {
        if (!isset($_SESSION['user_id'])) {
            return ['success' => false, 'message' => 'No autenticado'];
        }
        
        // Obtener datos del usuario desde la sesión
        $userData = [
            'id' => $_SESSION['user_id'],
            'username' => $_SESSION['username'],
            'role' => $_SESSION['role'],
            'email' => $this->user->email,
            'name' => $this->user->nombre_completo
        ];
        
        return ['success' => true, 'user' => $userData];
    }
    
    /**
     * Manejar actualización de perfil
     */
    private function handleUpdateProfile($parameters) {
        if (!isset($_SESSION['user_id'])) {
            return ['success' => false, 'message' => 'No autenticado'];
        }
        
        $name = $parameters['name'] ?? '';
        $email = $parameters['email'] ?? '';
        
        if (empty($name) || empty($email)) {
            return ['success' => false, 'message' => 'Nombre y email son requeridos'];
        }
        
        // Actualizar propiedades del usuario
        $this->user->nombre_completo = $name;
        $this->user->email = $email;
        
        // Usar el método update existente en el modelo User
        $result = $this->user->update();
        
        if ($result) {
            return ['success' => true, 'message' => 'Perfil actualizado correctamente'];
        } else {
            return ['success' => false, 'message' => 'Error al actualizar perfil'];
        }
    }
    
    /**
     * Crear respuesta XML
     * @param string $requestId ID de la solicitud
     * @param array $result Resultado de la operación
     * @param float $processingTime Tiempo de procesamiento
     * @return string Respuesta XML
     */
    private function createResponse($requestId, $result, $processingTime) {
        $timestamp = date('c');
        $statusCode = $result['success'] ? 200 : 400;
        
        $xml = new SimpleXMLElement('<protocol version="1.0" timestamp="' . $timestamp . '"/>');
        
        $response = $xml->addChild('response');
        $response->addAttribute('request_id', $requestId);
        $response->addAttribute('server_id', 'hotel_server_001');
        $response->addAttribute('processing_time', $processingTime);
        
        $response->addChild('status', $statusCode);
        
        if ($result['success']) {
            $data = $response->addChild('data');
            $this->addDataToXML($data, $result);
        } else {
            $data = $response->addChild('data');
            $data->addChild('message', htmlspecialchars($result['message']));
        }
        
        return $xml->asXML();
    }
    
    /**
     * Agregar datos al XML
     * @param SimpleXMLElement $parent Elemento padre
     * @param array $data Datos a agregar
     */
    private function addDataToXML($parent, $data) {
        foreach ($data as $key => $value) {
            if ($key === 'success') continue;
            
            if (is_array($value)) {
                if ($key === 'user') {
                    $user = $parent->addChild('user');
                    foreach ($value as $field => $fieldValue) {
                        $user->addChild($field, htmlspecialchars($fieldValue));
                    }
                } elseif ($key === 'hotels') {
                    $hotels = $parent->addChild('hotels');
                    foreach ($value as $hotel) {
                        $hotelElement = $hotels->addChild('hotel');
                        foreach ($hotel as $hotelField => $hotelValue) {
                            if ($hotelField === 'rooms') {
                                $rooms = $hotelElement->addChild('rooms');
                                foreach ($hotelValue as $room) {
                                    $roomElement = $rooms->addChild('room');
                                    foreach ($room as $roomField => $roomValue) {
                                        $roomElement->addChild($roomField, htmlspecialchars($roomValue));
                                    }
                                }
                            } else {
                                $hotelElement->addChild($hotelField, htmlspecialchars($hotelValue));
                            }
                        }
                    }
                } elseif ($key === 'rooms') {
                    $rooms = $parent->addChild('rooms');
                    foreach ($value as $room) {
                        $roomElement = $rooms->addChild('room');
                        foreach ($room as $field => $fieldValue) {
                            $roomElement->addChild($field, htmlspecialchars($fieldValue));
                        }
                    }
                } elseif ($key === 'booking') {
                    $booking = $parent->addChild('booking');
                    foreach ($value as $field => $fieldValue) {
                        $booking->addChild($field, htmlspecialchars($fieldValue));
                    }
                }
            } else {
                $parent->addChild($key, htmlspecialchars($value));
            }
        }
    }
    
    /**
     * Crear respuesta de error XML
     * @param int $code Código de error
     * @param string $message Mensaje de error
     * @param string $details Detalles adicionales
     * @return string Respuesta de error XML
     */
    private function createErrorResponse($code, $message, $details = '') {
        $timestamp = date('c');
        
        $xml = new SimpleXMLElement('<protocol version="1.0" timestamp="' . $timestamp . '"/>');
        
        $error = $xml->addChild('error');
        $error->addChild('code', $code);
        $error->addChild('message', htmlspecialchars($message));
        
        if (!empty($details)) {
            $detailsElement = $error->addChild('details');
            $detail = $detailsElement->addChild('detail', htmlspecialchars($details));
        }
        
        return $xml->asXML();
    }
}

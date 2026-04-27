<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * ENDPOINT XML
 * =============================================
 * 
 * Este endpoint maneja todas las solicitudes XML del cliente
 * y devuelve respuestas en formato XML según el protocolo definido.
 */

// Configurar headers para XML
header('Content-Type: application/xml; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Manejar preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit(0);
}

// Solo permitir POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo '<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE protocol SYSTEM "xml_protocol/protocol.dtd">
<protocol version="1.0" timestamp="' . date('c') . '">
    <error>
        <code>405</code>
        <message>Método no permitido</message>
        <details>
            <detail>Solo se permiten solicitudes POST</detail>
        </details>
    </error>
</protocol>';
    exit;
}

// Obtener el XML del request
$xmlInput = file_get_contents('php://input');

if (empty($xmlInput)) {
    http_response_code(400);
    echo '<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE protocol SYSTEM "xml_protocol/protocol.dtd">
<protocol version="1.0" timestamp="' . date('c') . '">
    <error>
        <code>400</code>
        <message>XML vacío o no proporcionado</message>
        <details>
            <detail>Se requiere un mensaje XML en el cuerpo de la solicitud</detail>
        </details>
    </error>
</protocol>';
    exit;
}

// Incluir el controlador del protocolo XML
require_once __DIR__ . '/controllers/XMLProtocolController.php';

// Procesar la solicitud XML
try {
    $controller = new XMLProtocolController();
    $response = $controller->processRequest($xmlInput);
    
    // Enviar respuesta
    echo $response;
    
} catch (Exception $e) {
    http_response_code(500);
    echo '<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE protocol SYSTEM "xml_protocol/protocol.dtd">
<protocol version="1.0" timestamp="' . date('c') . '">
    <error>
        <code>500</code>
        <message>Error interno del servidor</message>
        <details>
            <detail>' . htmlspecialchars($e->getMessage()) . '</detail>
        </details>
    </error>
</protocol>';
}
?>

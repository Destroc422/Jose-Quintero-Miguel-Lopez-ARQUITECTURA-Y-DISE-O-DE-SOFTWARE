/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * DEMO XML PROTOCOL
 * =============================================
 */

// Inicializar componentes
const xmlClient = new XMLClient();
const xmlTransformer = new XMLTransformer();
let currentRequest = '';
let currentResponse = '';

// Generar XML de ejemplo según operación seleccionada
function generateExampleXML() {
    const operation = document.getElementById('operationSelect').value;
    const examples = {
        'login': {
            username: 'jquintero',
            password: 'password123',
            remember: true
        },
        'register': {
            username: 'nuevo_usuario',
            email: 'usuario@hotel.com',
            password: 'password123',
            name: 'Usuario Nuevo'
        },
        'get_hotels': {},
        'get_rooms': {
            hotel_id: '1'
        },
        'book_room': {
            room_id: '101',
            check_in: '2026-05-01',
            check_out: '2026-05-03'
        },
        'get_user_data': {}
    };
    
    return xmlClient.createRequest(operation, examples[operation]);
}

// Ejecutar operación demo
async function executeOperation() {
    const operation = document.getElementById('operationSelect').value;
    
    // Generar y mostrar XML de solicitud
    currentRequest = generateExampleXML();
    document.getElementById('xmlRequest').textContent = formatXML(currentRequest);
    
    // Simular respuesta (en producción sería real)
    try {
        const response = await simulateResponse(operation);
        currentResponse = response;
        document.getElementById('xmlResponse').textContent = formatXML(response);
        
        // Mostrar indicador de éxito
        showNotification('success', 'Operación ejecutada correctamente');
    } catch (error) {
        showNotification('error', 'Error en la operación: ' + error.message);
    }
}

// Simular respuesta del servidor
async function simulateResponse(operation) {
    const responses = {
        'login': '<?xml version="1.0" encoding="UTF-8"?>' +
            '\n<!DOCTYPE protocol SYSTEM "../xml_protocol/protocol.dtd">' +
            '\n<protocol version="1.0" timestamp="2026-04-27T14:30:02Z">' +
            '\n    <response request_id="req_001" server_id="hotel_server_001" processing_time="0.125">' +
            '\n        <status>200</status>' +
            '\n        <data>' +
            '\n            <user>' +
            '\n                <id>123</id>' +
            '\n                <username>jquintero</username>' +
            '\n                <email>jquintero@hotel.com</email>' +
            '\n                <name>Jose Quintero</name>' +
            '\n                <role>admin</role>' +
            '\n                <created_at>2026-01-15T10:30:00Z</created_at>' +
            '\n            </user>' +
            '\n            <message>Login exitoso</message>' +
            '\n        </data>' +
            '\n    </response>' +
            '\n</protocol>',
        'get_hotels': '<?xml version="1.0" encoding="UTF-8"?>' +
            '\n<!DOCTYPE protocol SYSTEM "../xml_protocol/protocol.dtd">' +
            '\n<protocol version="1.0" timestamp="2026-04-27T14:30:02Z">' +
            '\n    <response request_id="req_002" server_id="hotel_server_001" processing_time="0.089">' +
            '\n        <status>200</status>' +
            '\n        <data>' +
            '\n            <hotels>' +
            '\n                <hotel>' +
            '\n                    <id>1</id>' +
            '\n                    <name>Hotel Hermosa Cartagena</name>' +
            '\n                    <description>Hotel de lujo en el centro histórico</description>' +
            '\n                    <location>Cartagena, Colombia</location>' +
            '\n                    <rating>4.8</rating>' +
            '\n                </hotel>' +
            '\n            </hotels>' +
            '\n        </data>' +
            '\n    </response>' +
            '\n</protocol>'
    };
    
    // Simular delay de red
    await new Promise(resolve => setTimeout(resolve, 500));
    
    return responses[operation] || responses['login'];
}

// Formatear XML para visualización
function formatXML(xmlString) {
    try {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, 'text/xml');
        const serializer = new XMLSerializer();
        let formatted = serializer.serializeToString(xmlDoc);
        
        // Simple indentation
        formatted = formatted.replace(/></g, '>\n<');
        formatted = formatted.replace(/(\s+)(<)/g, '$2');
        
        return formatted;
    } catch (error) {
        return xmlString;
    }
}

// Validar XML
function validateXML() {
    if (!currentRequest || !currentResponse) {
        showNotification('warning', 'Ejecuta una operación primero');
        return;
    }
    
    const validation = xmlTransformer.validateXML(currentResponse);
    
    if (validation.valid) {
        showNotification('success', 'XML válido y bien formado');
    } else {
        showNotification('error', 'Error de validación: ' + validation.error);
    }
}

// Transformar a HTML
async function transformToHTML() {
    if (!currentResponse) {
        showNotification('warning', 'Ejecuta una operación primero');
        return;
    }
    
    try {
        await xmlTransformer.previewTransformation(currentResponse, '../xml_protocol/transformations/response_to_html.xsl');
        showNotification('success', 'Transformación completada');
    } catch (error) {
        showNotification('error', 'Error en transformación: ' + error.message);
    }
}

// Extraer con XPath
function extractWithXPath() {
    if (!currentResponse) {
        showNotification('warning', 'Ejecuta una operación primero');
        return;
    }
    
    try {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(currentResponse, 'text/xml');
        
        // Ejemplos de XPath
        const examples = [
            '//protocol/response/status',
            '//protocol/response/data/message',
            '//protocol/response/data/user/username',
            '//protocol/@timestamp'
        ];
        
        let results = 'Resultados XPath:\n\n';
        examples.forEach(xpath => {
            try {
                const result = xmlDoc.evaluate(xpath, xmlDoc, null, XPathResult.STRING_TYPE, null);
                results += `${xpath}: ${result.stringValue}\n`;
            } catch (error) {
                results += `${xpath}: Error - ${error.message}\n`;
            }
        });
        
        alert(results);
        showNotification('success', 'Extracción XPath completada');
    } catch (error) {
        showNotification('error', 'Error en extracción: ' + error.message);
    }
}

// Limpiar demo
function clearDemo() {
    currentRequest = '';
    currentResponse = '';
    document.getElementById('xmlRequest').textContent = 'Selecciona una operación para generar el XML...';
    document.getElementById('xmlResponse').textContent = 'Esperando respuesta del servidor...';
}

// Mostrar notificación
function showNotification(type, message) {
    const alertClass = type === 'error' ? 'danger' : type;
    const icon = type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-triangle' : 'info-circle';
    
    const notification = `
        <div class="alert alert-${alertClass} alert-dismissible fade show position-fixed" 
             style="top: 20px; right: 20px; z-index: 9999; min-width: 300px;" role="alert">
            <i class="fas fa-${icon} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', notification);
    
    // Auto-remover después de 5 segundos
    setTimeout(() => {
        const alert = document.querySelector('.alert:last-of-type');
        if (alert) {
            alert.remove();
        }
    }, 5000);
}

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Generar XML inicial
    document.getElementById('xmlRequest').textContent = formatXML(generateExampleXML());
    
    // Auto-generar al cambiar operación
    document.getElementById('operationSelect').addEventListener('change', function() {
        document.getElementById('xmlRequest').textContent = formatXML(generateExampleXML());
    });
});

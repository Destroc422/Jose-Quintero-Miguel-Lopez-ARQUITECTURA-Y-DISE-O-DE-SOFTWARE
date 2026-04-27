/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CLIENTE XML PROTOCOL
 * =============================================
 * 
 * Este módulo maneja la comunicación XML con el servidor
 * utilizando el protocolo definido para el sistema distribuido.
 */

class XMLClient {
    constructor(endpointUrl = '../backend/xml_endpoint.php') {
        this.endpointUrl = endpointUrl;
        this.requestCounter = 0;
        this.clientId = 'web_client_' + Math.random().toString(36).substr(2, 9);
    }

    /**
     * Generar ID único para solicitudes
     */
    generateRequestId() {
        return 'req_' + String(++this.requestCounter).padStart(3, '0');
    }

    /**
     * Crear solicitud XML
     * @param {string} operation Operación a ejecutar
     * @param {Object} parameters Parámetros de la operación
     * @returns {string} XML de solicitud
     */
    createRequest(operation, parameters = {}) {
        const requestId = this.generateRequestId();
        const timestamp = new Date().toISOString();
        
        let xml = `<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE protocol SYSTEM "../xml_protocol/protocol.dtd">
<protocol version="1.0" timestamp="${timestamp}">
    <request id="${requestId}" client_id="${this.clientId}">
        <operation>${operation}</operation>`;
        
        if (Object.keys(parameters).length > 0) {
            xml += '\n        <parameters>';
            for (const [key, value] of Object.entries(parameters)) {
                const type = this.getParameterType(value);
                xml += `\n            <parameter name="${key}" type="${type}">${this.escapeXml(value)}</parameter>`;
            }
            xml += '\n        </parameters>';
        }
        
        xml += `\n    </request>
</protocol>`;
        
        return xml;
    }

    /**
     * Determinar tipo de parámetro
     * @param {*} value Valor a evaluar
     * @returns {string} Tipo del parámetro
     */
    getParameterType(value) {
        if (typeof value === 'boolean') return 'boolean';
        if (typeof value === 'number') return 'integer';
        if (value instanceof Date) return 'date';
        return 'string';
    }

    /**
     * Escapar caracteres XML
     * @param {string} text Texto a escapar
     * @returns {string} Texto escapado
     */
    escapeXml(text) {
        return String(text)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    /**
     * Enviar solicitud XML al servidor
     * @param {string} operation Operación a ejecutar
     * @param {Object} parameters Parámetros de la operación
     * @returns {Promise} Promesa con la respuesta
     */
    async sendRequest(operation, parameters = {}) {
        const xmlRequest = this.createRequest(operation, parameters);
        
        try {
            const response = await fetch(this.endpointUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/xml',
                    'Accept': 'application/xml'
                },
                body: xmlRequest
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const xmlResponse = await response.text();
            return this.parseResponse(xmlResponse);

        } catch (error) {
            console.error('Error en comunicación XML:', error);
            throw error;
        }
    }

    /**
     * Parsear respuesta XML usando XPath
     * @param {string} xmlString XML de respuesta
     * @returns {Object} Respuesta parseada
     */
    parseResponse(xmlString) {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, 'text/xml');

        // Verificar si hay errores de parseo
        const parseError = xmlDoc.querySelector('parsererror');
        if (parseError) {
            throw new Error('Error al parsear XML: ' + parseError.textContent);
        }

        // Usar XPath para extraer información
        const nsResolver = xmlDoc.createNSResolver(xmlDoc.documentElement);

        // Determinar si es respuesta o error
        const responseElement = xmlDoc.evaluate('//protocol/response', xmlDoc, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        const errorElement = xmlDoc.evaluate('//protocol/error', xmlDoc, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);

        if (responseElement && responseElement.singleNodeValue) {
            return this.extractResponseData(responseElement.singleNodeValue, xmlDoc, nsResolver);
        } else if (errorElement && errorElement.singleNodeValue) {
            return this.extractErrorData(errorElement.singleNodeValue, xmlDoc, nsResolver);
        } else {
            throw new Error('Respuesta XML no válida: no se encontró response o error');
        }
    }

    /**
     * Extraer datos de respuesta usando XPath
     * @param {Element} responseElement Elemento de respuesta
     * @param {Document} xmlDoc Documento XML
     * @param {XPathNSResolver} nsResolver Resolvedor de namespaces
     * @returns {Object} Datos extraídos
     */
    extractResponseData(responseElement, xmlDoc, nsResolver) {
        const requestId = responseElement.getAttribute('request_id');
        const serverId = responseElement.getAttribute('server_id');
        const processingTime = responseElement.getAttribute('processing_time');

        // Extraer estado
        const statusNode = xmlDoc.evaluate('./status', responseElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        const status = statusNode ? parseInt(statusNode.singleNodeValue.textContent) : null;

        // Extraer datos
        const dataNode = xmlDoc.evaluate('./data', responseElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        let data = {};

        if (dataNode && dataNode.singleNodeValue) {
            data = this.extractDataContent(dataNode.singleNodeValue, xmlDoc, nsResolver);
        }

        return {
            type: 'response',
            requestId,
            serverId,
            processingTime,
            status,
            data,
            success: status >= 200 && status < 300
        };
    }

    /**
     * Extraer datos de error usando XPath
     * @param {Element} errorElement Elemento de error
     * @param {Document} xmlDoc Documento XML
     * @param {XPathNSResolver} nsResolver Resolvedor de namespaces
     * @returns {Object} Datos de error
     */
    extractErrorData(errorElement, xmlDoc, nsResolver) {
        const requestId = errorElement.getAttribute('request_id');

        // Extraer código de error
        const codeNode = xmlDoc.evaluate('./code', errorElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        const code = codeNode ? parseInt(codeNode.singleNodeValue.textContent) : null;

        // Extraer mensaje de error
        const messageNode = xmlDoc.evaluate('./message', errorElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        const message = messageNode ? messageNode.singleNodeValue.textContent : '';

        // Extraer detalles
        const detailsNode = xmlDoc.evaluate('./details', errorElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        let details = [];

        if (detailsNode && detailsNode.singleNodeValue) {
            const detailNodes = xmlDoc.evaluate('./details/detail', errorElement, nsResolver, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
            for (let i = 0; i < detailNodes.snapshotLength; i++) {
                const detailNode = detailNodes.snapshotItem(i);
                details.push({
                    field: detailNode.getAttribute('field'),
                    message: detailNode.textContent
                });
            }
        }

        return {
            type: 'error',
            requestId,
            code,
            message,
            details,
            success: false
        };
    }

    /**
     * Extraer contenido de datos usando XPath
     * @param {Element} dataElement Elemento de datos
     * @param {Document} xmlDoc Documento XML
     * @param {XPathNSResolver} nsResolver Resolvedor de namespaces
     * @returns {Object} Datos extraídos
     */
    extractDataContent(dataElement, xmlDoc, nsResolver) {
        const data = {};

        // Extraer usuario
        const userNode = xmlDoc.evaluate('./user', dataElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        if (userNode && userNode.singleNodeValue) {
            data.user = this.extractElementData(userNode.singleNodeValue, ['id', 'username', 'email', 'name', 'role', 'created_at']);
        }

        // Extraer mensaje
        const messageNode = xmlDoc.evaluate('./message', dataElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        if (messageNode && messageNode.singleNodeValue) {
            data.message = messageNode.singleNodeValue.textContent;
        }

        // Extraer hoteles
        const hotelsNode = xmlDoc.evaluate('./hotels', dataElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        if (hotelsNode && hotelsNode.singleNodeValue) {
            data.hotels = this.extractHotelsData(hotelsNode.singleNodeValue, xmlDoc, nsResolver);
        }

        // Extraer habitaciones
        const roomsNode = xmlDoc.evaluate('./rooms', dataElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        if (roomsNode && roomsNode.singleNodeValue) {
            data.rooms = this.extractRoomsData(roomsNode.singleNodeValue, xmlDoc, nsResolver);
        }

        // Extraer reservas
        const bookingsNode = xmlDoc.evaluate('./bookings', dataElement, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
        if (bookingsNode && bookingsNode.singleNodeValue) {
            data.bookings = this.extractBookingsData(bookingsNode.singleNodeValue, xmlDoc, nsResolver);
        }

        return data;
    }

    /**
     * Extraer datos de un elemento
     * @param {Element} element Elemento a extraer
     * @param {Array} fields Campos a extraer
     * @returns {Object} Datos extraídos
     */
    extractElementData(element, fields) {
        const data = {};
        for (const field of fields) {
            const fieldNode = element.querySelector(field);
            if (fieldNode) {
                data[field] = fieldNode.textContent;
            }
        }
        return data;
    }

    /**
     * Extraer datos de hoteles
     * @param {Element} hotelsElement Elemento de hoteles
     * @param {Document} xmlDoc Documento XML
     * @param {XPathNSResolver} nsResolver Resolvedor de namespaces
     * @returns {Array} Lista de hoteles
     */
    extractHotelsData(hotelsElement, xmlDoc, nsResolver) {
        const hotels = [];
        const hotelNodes = xmlDoc.evaluate('./hotel', hotelsElement, nsResolver, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
        
        for (let i = 0; i < hotelNodes.snapshotLength; i++) {
            const hotelNode = hotelNodes.snapshotItem(i);
            const hotel = this.extractElementData(hotelNode, ['id', 'name', 'description', 'location', 'rating']);
            
            // Extraer habitaciones del hotel
            const roomsNode = xmlDoc.evaluate('./rooms', hotelNode, nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
            if (roomsNode && roomsNode.singleNodeValue) {
                hotel.rooms = this.extractRoomsData(roomsNode.singleNodeValue, xmlDoc, nsResolver);
            }
            
            hotels.push(hotel);
        }
        
        return hotels;
    }

    /**
     * Extraer datos de habitaciones
     * @param {Element} roomsElement Elemento de habitaciones
     * @param {Document} xmlDoc Documento XML
     * @param {XPathNSResolver} nsResolver Resolvedor de namespaces
     * @returns {Array} Lista de habitaciones
     */
    extractRoomsData(roomsElement, xmlDoc, nsResolver) {
        const rooms = [];
        const roomNodes = xmlDoc.evaluate('./room', roomsElement, nsResolver, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
        
        for (let i = 0; i < roomNodes.snapshotLength; i++) {
            const roomNode = roomNodes.snapshotItem(i);
            const room = this.extractElementData(roomNode, ['id', 'number', 'type', 'capacity', 'price_per_night', 'available']);
            rooms.push(room);
        }
        
        return rooms;
    }

    /**
     * Extraer datos de reservas
     * @param {Element} bookingsElement Elemento de reservas
     * @param {Document} xmlDoc Documento XML
     * @param {XPathNSResolver} nsResolver Resolvedor de namespaces
     * @returns {Array} Lista de reservas
     */
    extractBookingsData(bookingsElement, xmlDoc, nsResolver) {
        const bookings = [];
        const bookingNodes = xmlDoc.evaluate('./booking', bookingsElement, nsResolver, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
        
        for (let i = 0; i < bookingNodes.snapshotLength; i++) {
            const bookingNode = bookingNodes.snapshotItem(i);
            const booking = this.extractElementData(bookingNode, ['id', 'user_id', 'room_id', 'check_in', 'check_out', 'total_price', 'status']);
            bookings.push(booking);
        }
        
        return bookings;
    }

    /**
     * Métodos de conveniencia para operaciones comunes
     */
    async login(username, password, remember = false) {
        return await this.sendRequest('login', { username, password, remember });
    }

    async register(username, email, password, name) {
        return await this.sendRequest('register', { username, email, password, name });
    }

    async logout() {
        return await this.sendRequest('logout');
    }

    async getHotels() {
        return await this.sendRequest('get_hotels');
    }

    async getRooms(hotelId) {
        return await this.sendRequest('get_rooms', { hotel_id: hotelId });
    }

    async bookRoom(roomId, checkIn, checkOut) {
        return await this.sendRequest('book_room', { room_id: roomId, check_in: checkIn, check_out: checkOut });
    }

    async cancelBooking(bookingId) {
        return await this.sendRequest('cancel_booking', { booking_id: bookingId });
    }

    async getUserData() {
        return await this.sendRequest('get_user_data');
    }

    async updateProfile(name, email) {
        return await this.sendRequest('update_profile', { name, email });
    }
}

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * TRANSFORMADOR XSLT
 * =============================================
 * 
 * Este módulo maneja la transformación de respuestas XML
 * a otros formatos utilizando hojas de estilo XSLT.
 */

class XMLTransformer {
    constructor() {
        this.xsltProcessor = new XSLTProcessor();
    }

    /**
     * Cargar hoja de estilo XSLT
     * @param {string} xslPath Ruta al archivo XSL
     * @returns {Promise} Promesa con el procesador XSLT cargado
     */
    async loadStylesheet(xslPath) {
        try {
            const response = await fetch(xslPath);
            const xslText = await response.text();
            
            const parser = new DOMParser();
            const xslDoc = parser.parseFromString(xslText, 'text/xml');
            
            this.xsltProcessor.importStylesheet(xslDoc);
            return this.xsltProcessor;
        } catch (error) {
            console.error('Error cargando hoja de estilo XSLT:', error);
            throw error;
        }
    }

    /**
     * Transformar XML a HTML
     * @param {string} xmlString XML a transformar
     * @param {string} xslPath Ruta al archivo XSL (opcional si ya está cargado)
     * @returns {Promise} Promesa con el HTML transformado
     */
    async transformToHTML(xmlString, xslPath = null) {
        try {
            // Cargar hoja de estilo si se proporciona
            if (xslPath) {
                await this.loadStylesheet(xslPath);
            }

            // Parsear XML
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlString, 'text/xml');

            // Verificar errores de parseo
            const parseError = xmlDoc.querySelector('parsererror');
            if (parseError) {
                throw new Error('Error al parsear XML: ' + parseError.textContent);
            }

            // Realizar transformación
            const transformedDoc = this.xsltProcessor.transformToDocument(xmlDoc);
            
            // Obtener HTML resultante
            const htmlString = new XMLSerializer().serializeToString(transformedDoc);
            
            return htmlString;
        } catch (error) {
            console.error('Error en transformación XSLT:', error);
            throw error;
        }
    }

    /**
     * Transformar XML a texto plano
     * @param {string} xmlString XML a transformar
     * @returns {string} Texto plano extraído
     */
    transformToText(xmlString) {
        try {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlString, 'text/xml');

            // Extraer texto usando XPath
            const textContent = xmlDoc.evaluate('//text()', xmlDoc, null, XPathResult.STRING_TYPE, null);
            
            return textContent.stringValue.trim();
        } catch (error) {
            console.error('Error extrayendo texto de XML:', error);
            return '';
        }
    }

    /**
     * Transformar XML a JSON
     * @param {string} xmlString XML a transformar
     * @returns {Object} Objeto JSON
     */
    transformToJSON(xmlString) {
        try {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlString, 'text/xml');

            // Función recursiva para convertir elementos a objetos
            const elementToObject = (element) => {
                const obj = {};
                
                // Atributos
                for (const attr of element.attributes) {
                    obj['@' + attr.name] = attr.value;
                }
                
                // Hijos
                for (const child of element.children) {
                    const childObj = elementToObject(child);
                    
                    if (obj[child.tagName]) {
                        // Si ya existe, convertir en array
                        if (!Array.isArray(obj[child.tagName])) {
                            obj[child.tagName] = [obj[child.tagName]];
                        }
                        obj[child.tagName].push(childObj);
                    } else {
                        obj[child.tagName] = childObj;
                    }
                }
                
                // Texto si no hay hijos
                if (element.children.length === 0 && element.textContent) {
                    return element.textContent;
                }
                
                return obj;
            };

            return elementToObject(xmlDoc.documentElement);
        } catch (error) {
            console.error('Error convirtiendo XML a JSON:', error);
            return null;
        }
    }

    /**
     * Crear vista previa de transformación en una ventana nueva
     * @param {string} xmlString XML a transformar
     * @param {string} xslPath Ruta al archivo XSL
     */
    async previewTransformation(xmlString, xslPath) {
        try {
            const html = await this.transformToHTML(xmlString, xslPath);
            
            // Abrir nueva ventana con la transformación
            const newWindow = window.open('', '_blank', 'width=800,height=600,scrollbars=yes');
            newWindow.document.write(html);
            newWindow.document.close();
        } catch (error) {
            console.error('Error creando vista previa:', error);
            alert('Error al crear vista previa: ' + error.message);
        }
    }

    /**
     * Validar XML contra DTD
     * @param {string} xmlString XML a validar
     * @returns {Object} Resultado de validación
     */
    validateXML(xmlString) {
        try {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlString, 'text/xml');
            
            const parseError = xmlDoc.querySelector('parsererror');
            
            return {
                valid: !parseError,
                error: parseError ? parseError.textContent : null
            };
        } catch (error) {
            return {
                valid: false,
                error: error.message
            };
        }
    }

    /**
     * Formatear XML para visualización
     * @param {string} xmlString XML a formatear
     * @returns {string} XML formateado
     */
    formatXML(xmlString) {
        try {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlString, 'text/xml');
            
            const serializer = new XMLSerializer();
            let formatted = serializer.serializeToString(xmlDoc);
            
            // Aplicar indentación básica
            formatted = formatted.replace(/></g, '>\n<');
            
            return formatted;
        } catch (error) {
            console.error('Error formateando XML:', error);
            return xmlString;
        }
    }
}

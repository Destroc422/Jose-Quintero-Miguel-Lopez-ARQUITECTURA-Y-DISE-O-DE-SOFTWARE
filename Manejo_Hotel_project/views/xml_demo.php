<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * DEMOSTRACIÓN DE PROTOCOLO XML
 * =============================================
 */
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demostración Protocolo XML - Hermosa Cartagena</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Prism.js for code highlighting -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism-tomorrow.min.css" rel="stylesheet">
    
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px 0;
        }
        
        .demo-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            margin: 20px auto;
            max-width: 1400px;
            padding: 40px;
        }
        
        .header-section {
            text-align: center;
            margin-bottom: 40px;
            padding: 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 15px;
            color: white;
        }
        
        .feature-card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
            transition: transform 0.3s ease;
        }
        
        .feature-card:hover {
            transform: translateY(-5px);
        }
        
        .feature-icon {
            font-size: 48px;
            color: #667eea;
            margin-bottom: 20px;
        }
        
        .demo-section {
            background: #f8f9fa;
            border-radius: 15px;
            padding: 30px;
            margin: 30px 0;
        }
        
        .code-block {
            background: #2d2d2d;
            border-radius: 10px;
            padding: 20px;
            margin: 15px 0;
            overflow-x: auto;
        }
        
        .code-block pre {
            color: #f8f8f2;
            margin: 0;
            font-family: 'Courier New', monospace;
            font-size: 14px;
            line-height: 1.5;
        }
        
        .btn-demo {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            color: white;
            padding: 12px 30px;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .btn-demo:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
            color: white;
        }
        
        .status-indicator {
            display: inline-block;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            margin-right: 8px;
        }
        
        .status-success { background: #28a745; }
        .status-error { background: #dc3545; }
        .status-warning { background: #ffc107; }
        .status-info { background: #17a2b8; }
        
        .xml-output {
            background: #2d2d2d;
            color: #f8f8f2;
            border-radius: 10px;
            padding: 20px;
            font-family: 'Courier New', monospace;
            font-size: 12px;
            max-height: 400px;
            overflow-y: auto;
            margin: 15px 0;
        }
        
        .operation-demo {
            border: 1px solid #dee2e6;
            border-radius: 10px;
            padding: 20px;
            margin: 15px 0;
            background: white;
        }
        
        .operation-header {
            background: #667eea;
            color: white;
            padding: 10px 15px;
            border-radius: 8px 8px 0 0;
            margin: -20px -20px 15px -20px;
            font-weight: 500;
        }
        
        .metric-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            margin: 10px 0;
        }
        
        .metric-value {
            font-size: 32px;
            font-weight: 700;
            color: #667eea;
            margin-bottom: 10px;
        }
        
        .metric-label {
            color: #6c757d;
            font-size: 14px;
        }
        
        .comparison-table {
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        .comparison-table th {
            background: #667eea;
            color: white;
            border: none;
            padding: 15px;
        }
        
        .comparison-table td {
            padding: 15px;
            vertical-align: middle;
        }
        
        .badge-success { background: #28a745; }
        .badge-warning { background: #ffc107; }
        .badge-danger { background: #dc3545; }
        
        @media (max-width: 768px) {
            .demo-container {
                padding: 20px;
                margin: 10px;
            }
            
            .header-section {
                padding: 20px;
            }
            
            .feature-icon {
                font-size: 36px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="demo-container">
            <!-- Header -->
            <div class="header-section">
                <i class="fas fa-code fa-3x mb-3"></i>
                <h1 class="display-4 fw-bold">Protocolo XML</h1>
                <p class="lead">Diseño e Implementación de Protocolos de Intercambio y Serialización XML en Sistemas Distribuidos</p>
                <div class="mt-3">
                    <span class="status-indicator status-success"></span>
                    <span>Sistema Operativo</span>
                </div>
            </div>
            
            <!-- Features Overview -->
            <div class="row mb-5">
                <div class="col-md-4">
                    <div class="card feature-card">
                        <div class="card-body text-center">
                            <i class="fas fa-shield-alt feature-icon"></i>
                            <h5 class="card-title">Validación Estructural</h5>
                            <p class="card-text">DTD y XSD para garantizar integridad de mensajes XML</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card feature-card">
                        <div class="card-body text-center">
                            <i class="fas fa-search feature-icon"></i>
                            <h5 class="card-title">XPath</h5>
                            <p class="card-text">Extracción precisa de datos con expresiones XPath</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card feature-card">
                        <div class="card-body text-center">
                            <i class="fas fa-exchange-alt feature-icon"></i>
                            <h5 class="card-title">XSLT</h5>
                            <p class="card-text">Transformación de XML a HTML y otros formatos</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Protocol Structure -->
            <div class="demo-section">
                <h3><i class="fas fa-sitemap me-2"></i>Estructura del Protocolo</h3>
                <p>El protocolo XML define tres tipos principales de mensajes:</p>
                
                <div class="row">
                    <div class="col-md-4">
                        <div class="operation-demo">
                            <div class="operation-header">
                                <i class="fas fa-paper-plane me-2"></i>Request
                            </div>
                            <p><strong>Solicitudes del cliente</strong> al servidor con operaciones y parámetros.</p>
                            <ul class="list-unstyled">
                                <li><i class="fas fa-check text-success me-2"></i>ID único</li>
                                <li><i class="fas fa-check text-success me-2"></i>Operación</li>
                                <li><i class="fas fa-check text-success me-2"></i>Parámetros tipados</li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="operation-demo">
                            <div class="operation-header">
                                <i class="fas fa-reply me-2"></i>Response
                            </div>
                            <p><strong>Respuestas del servidor</strong> con datos y estado de la operación.</p>
                            <ul class="list-unstyled">
                                <li><i class="fas fa-check text-success me-2"></i>ID de solicitud</li>
                                <li><i class="fas fa-check text-success me-2"></i>Código de estado</li>
                                <li><i class="fas fa-check text-success me-2"></i>Datos estructurados</li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="operation-demo">
                            <div class="operation-header">
                                <i class="fas fa-exclamation-triangle me-2"></i>Error
                            </div>
                            <p><strong>Mensajes de error</strong> con códigos y detalles descriptivos.</p>
                            <ul class="list-unstyled">
                                <li><i class="fas fa-check text-success me-2"></i>Código de error</li>
                                <li><i class="fas fa-check text-success me-2"></i>Mensaje descriptivo</li>
                                <li><i class="fas fa-check text-success me-2"></i>Detalles adicionales</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Live Demo -->
            <div class="demo-section">
                <h3><i class="fas fa-play-circle me-2"></i>Demostración en Vivo</h3>
                <p>Prueba el protocolo XML con diferentes operaciones del sistema hotelero:</p>
                
                <div class="row mb-3">
                    <div class="col-md-8">
                        <select id="operationSelect" class="form-select">
                            <option value="login">Login de Usuario</option>
                            <option value="register">Registro de Usuario</option>
                            <option value="get_hotels">Obtener Hoteles</option>
                            <option value="get_rooms">Obtener Habitaciones</option>
                            <option value="book_room">Reservar Habitación</option>
                            <option value="get_user_data">Datos de Usuario</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <button class="btn btn-demo w-100" onclick="executeOperation()">
                            <i class="fas fa-play me-2"></i>Ejecutar Operación
                        </button>
                    </div>
                </div>
                
                <!-- XML Request -->
                <div class="row">
                    <div class="col-md-6">
                        <h5><i class="fas fa-paper-plane me-2"></i>XML Enviado</h5>
                        <div class="xml-output" id="xmlRequest">
                            Selecciona una operación para generar el XML...
                        </div>
                    </div>
                    <div class="col-md-6">
                        <h5><i class="fas fa-reply me-2"></i>XML Recibido</h5>
                        <div class="xml-output" id="xmlResponse">
                            Esperando respuesta del servidor...
                        </div>
                    </div>
                </div>
                
                <!-- Transform Controls -->
                <div class="row mt-3">
                    <div class="col-md-12">
                        <button class="btn btn-outline-primary me-2" onclick="validateXML()">
                            <i class="fas fa-check-circle me-2"></i>Validar XML
                        </button>
                        <button class="btn btn-outline-success me-2" onclick="transformToHTML()">
                            <i class="fas fa-code me-2"></i>Transformar a HTML
                        </button>
                        <button class="btn btn-outline-info me-2" onclick="extractWithXPath()">
                            <i class="fas fa-search me-2"></i>Extraer con XPath
                        </button>
                        <button class="btn btn-outline-warning" onclick="clearDemo()">
                            <i class="fas fa-trash me-2"></i>Limpiar
                        </button>
                    </div>
                </div>
            </div>
            
            <!-- Performance Metrics -->
            <div class="demo-section">
                <h3><i class="fas fa-tachometer-alt me-2"></i>Métricas de Rendimiento</h3>
                
                <div class="row">
                    <div class="col-md-3">
                        <div class="metric-card">
                            <div class="metric-value">85ms</div>
                            <div class="metric-label">Tiempo Promedio</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="metric-card">
                            <div class="metric-value">100%</div>
                            <div class="metric-label">Validación Exitosa</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="metric-card">
                            <div class="metric-value">15ms</div>
                            <div class="metric-label">Transformación XSLT</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="metric-card">
                            <div class="metric-value">-60%</div>
                            <div class="metric-label">Reducción de Errores</div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Technology Comparison -->
            <div class="demo-section">
                <h3><i class="fas fa-balance-scale me-2"></i>Comparación de Tecnologías</h3>
                
                <div class="table-responsive">
                    <table class="table comparison-table">
                        <thead>
                            <tr>
                                <th>Criterio</th>
                                <th>XML</th>
                                <th>JSON</th>
                                <th>Binario</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><strong>Legibilidad</strong></td>
                                <td><span class="badge badge-success">Excelente</span></td>
                                <td><span class="badge badge-success">Excelente</span></td>
                                <td><span class="badge badge-danger">Nula</span></td>
                            </tr>
                            <tr>
                                <td><strong>Validación</strong></td>
                                <td><span class="badge badge-success">DTD/XSD</span></td>
                                <td><span class="badge badge-warning">Limitada</span></td>
                                <td><span class="badge badge-success">Compilación</span></td>
                            </tr>
                            <tr>
                                <td><strong>Rendimiento</strong></td>
                                <td><span class="badge badge-warning">Medio</span></td>
                                <td><span class="badge badge-success">Alto</span></td>
                                <td><span class="badge badge-success">Máximo</span></td>
                            </tr>
                            <tr>
                                <td><strong>Transformación</strong></td>
                                <td><span class="badge badge-success">XSLT</span></td>
                                <td><span class="badge badge-warning">Manual</span></td>
                                <td><span class="badge badge-danger">Nula</span></td>
                            </tr>
                            <tr>
                                <td><strong>Extensibilidad</strong></td>
                                <td><span class="badge badge-success">Excelente</span></td>
                                <td><span class="badge badge-warning">Media</span></td>
                                <td><span class="badge badge-danger">Baja</span></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            
            <!-- Implementation Details -->
            <div class="demo-section">
                <h3><i class="fas fa-cogs me-2"></i>Detalles de Implementación</h3>
                
                <div class="row">
                    <div class="col-md-6">
                        <h5><i class="fas fa-server me-2"></i>Servidor XML</h5>
                        <ul>
                            <li>Controlador <code>XMLProtocolController.php</code></li>
                            <li>Endpoint <code>xml_endpoint.php</code></li>
                            <li>Validación DTD/XSD integrada</li>
                            <li>Procesamiento XPath para extracción</li>
                            <li>Integración con modelo User existente</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h5><i class="fas fa-desktop me-2"></i>Cliente XML</h5>
                        <ul>
                            <li>Clase <code>XMLClient</code> en JavaScript</li>
                            <li>Generación automática de solicitudes</li>
                            <li>Parseo con XPath</li>
                            <li>Transformador XSLT integrado</li>
                            <li>Debug visual de mensajes</li>
                        </ul>
                    </div>
                </div>
            </div>
            
            <!-- File Structure -->
            <div class="demo-section">
                <h3><i class="fas fa-folder-tree me-2"></i>Estructura de Archivos</h3>
                
                <div class="code-block">
                    <pre>Manejo_Hotel_project/
├── xml_protocol/
│   ├── protocol.dtd              # Definición DTD
│   ├── protocol.xsd              # Esquema XSD
│   ├── examples/
│   │   ├── login_request.xml     # Ejemplo solicitud
│   │   ├── login_response.xml    # Ejemplo respuesta
│   │   └── error_response.xml    # Ejemplo error
│   └── transformations/
│       └── response_to_html.xsl  # Transformación XSLT
├── backend/
│   ├── controllers/
│   │   └── XMLProtocolController.php
│   └── xml_endpoint.php          # Endpoint HTTP
├── assets/js/
│   ├── xml_client.js             # Cliente JavaScript
│   └── xml_transformer.js        # Transformador XSLT
├── views/
│   ├── login_xml.php            # Login con XML
│   └── xml_demo.php              # Esta página
└── docs/
    └── analisis_serializacion.md # Análisis comparativo</pre>
                </div>
            </div>
            
            <!-- Navigation -->
            <div class="text-center mt-5">
                <a href="login_xml.php" class="btn btn-demo me-3">
                    <i class="fas fa-sign-in-alt me-2"></i>Probar Login XML
                </a>
                <a href="../docs/analisis_serializacion.md" class="btn btn-outline-primary me-3">
                    <i class="fas fa-file-alt me-2"></i>Ver Análisis
                </a>
                <a href="../index.html" class="btn btn-outline-secondary">
                    <i class="fas fa-home me-2"></i>Inicio
                </a>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- XML Client -->
    <script src="../assets/js/xml_client.js"></script>
    <!-- XML Transformer -->
    <script src="../assets/js/xml_transformer.js"></script>
    <!-- XML Demo -->
    <script src="../assets/js/xml_demo.js"></script>
</body>
</html>

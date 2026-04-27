<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * LOGIN CON PROTOCOLO XML
 * =============================================
 */

// Funciones auxiliares (simuladas para demostración)
function generateCSRFToken() {
    return bin2hex(random_bytes(32));
}
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login XML - Hermosa Cartagena</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="../assets/css/style.css" rel="stylesheet">
    
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .login-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            max-width: 1200px;
            width: 100%;
            margin: 20px;
        }
        
        .login-image {
            background: url('https://images.unsplash.com/photo-1558036117-15e6194d9863?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80') center/cover;
            min-height: 600px;
            position: relative;
        }
        
        .login-image::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.4);
        }
        
        .login-image .overlay-content {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            text-align: center;
            color: white;
            z-index: 1;
        }
        
        .login-form {
            padding: 60px 50px;
        }
        
        .brand-logo {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .brand-logo i {
            font-size: 48px;
            color: #667eea;
            margin-bottom: 10px;
        }
        
        .brand-logo h2 {
            color: #333;
            font-weight: 600;
            margin: 0;
        }
        
        .brand-logo p {
            color: #666;
            margin: 5px 0 0;
        }
        
        .xml-badge {
            background: #28a745;
            color: white;
            padding: 5px 10px;
            border-radius: 15px;
            font-size: 12px;
            font-weight: 600;
            margin-left: 10px;
        }
        
        .form-floating {
            margin-bottom: 20px;
        }
        
        .form-control {
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 12px 15px;
            font-size: 16px;
            transition: all 0.3s ease;
        }
        
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        
        .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 12px;
            font-size: 16px;
            font-weight: 500;
            color: white;
            width: 100%;
            transition: all 0.3s ease;
        }
        
        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
        }
        
        .btn-login:disabled {
            opacity: 0.6;
            transform: none;
        }
        
        .alert {
            border-radius: 10px;
            border: none;
            padding: 15px;
            margin-bottom: 20px;
        }
        
        .spinner-border {
            width: 20px;
            height: 20px;
            margin-right: 10px;
        }
        
        .password-toggle {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            color: #666;
            z-index: 10;
        }
        
        .password-toggle:hover {
            color: #667eea;
        }
        
        .remember-forgot {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }
        
        .form-check {
            margin-bottom: 0;
        }
        
        .form-check-input:checked {
            background-color: #667eea;
            border-color: #667eea;
        }
        
        .forgot-link {
            color: #667eea;
            text-decoration: none;
            font-size: 14px;
            transition: color 0.3s ease;
        }
        
        .forgot-link:hover {
            color: #764ba2;
            text-decoration: underline;
        }
        
        .xml-debug {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 10px;
            padding: 15px;
            margin-top: 20px;
            font-family: 'Courier New', monospace;
            font-size: 12px;
            max-height: 200px;
            overflow-y: auto;
        }
        
        .xml-debug h6 {
            color: #495057;
            margin-bottom: 10px;
        }
        
        .xml-toggle {
            background: #6c757d;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 12px;
            cursor: pointer;
            margin-bottom: 10px;
        }
        
        .xml-toggle:hover {
            background: #5a6268;
        }
        
        .hidden {
            display: none;
        }
        
        @media (max-width: 768px) {
            .login-container {
                margin: 10px;
            }
            
            .login-image {
                min-height: 200px;
            }
            
            .login-form {
                padding: 40px 30px;
            }
            
            .brand-logo h2 {
                font-size: 24px;
            }
            
            .remember-forgot {
                flex-direction: column;
                gap: 15px;
                align-items: flex-start;
            }
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="row g-0">
            <!-- Imagen lateral -->
            <div class="col-lg-6 d-none d-lg-block">
                <div class="login-image">
                    <div class="overlay-content">
                        <i class="fas fa-umbrella-beach fa-4x mb-4"></i>
                        <h1 class="display-4 fw-bold mb-3">Hermosa Cartagena</h1>
                        <p class="lead">Tu aventura turística comienza aquí</p>
                        <div class="mt-4">
                            <i class="fas fa-map-marked-alt me-3"></i>
                            <i class="fas fa-ship me-3"></i>
                            <i class="fas fa-hotel me-3"></i>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Formulario de login -->
            <div class="col-lg-6">
                <div class="login-form">
                    <!-- Logo y branding -->
                    <div class="brand-logo">
                        <i class="fas fa-umbrella-beach"></i>
                        <h2>Bienvenido <span class="xml-badge">XML</span></h2>
                        <p>Inicia sesión con protocolo XML</p>
                    </div>
                    
                    <!-- Alertas -->
                    <div id="alertContainer"></div>
                    
                    <!-- Formulario -->
                    <form id="loginForm">
                        <!-- Username -->
                        <div class="form-floating">
                            <input type="text" class="form-control" id="username" name="username" 
                                   placeholder="Usuario" required autocomplete="username">
                            <label for="username">
                                <i class="fas fa-user me-2"></i>Usuario
                            </label>
                        </div>
                        
                        <!-- Password -->
                        <div class="form-floating position-relative">
                            <input type="password" class="form-control" id="password" name="password" 
                                   placeholder="Contraseña" required autocomplete="current-password">
                            <label for="password">
                                <i class="fas fa-lock me-2"></i>Contraseña
                            </label>
                            <span class="password-toggle" onclick="togglePassword()">
                                <i class="fas fa-eye" id="passwordIcon"></i>
                            </span>
                        </div>
                        
                        <!-- Remember me y forgot password -->
                        <div class="remember-forgot">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="remember" name="remember">
                                <label class="form-check-label" for="remember">
                                    Recordarme
                                </label>
                            </div>
                            <a href="#" class="forgot-link" onclick="showForgotPassword()">
                                ¿Olvidaste tu contraseña?
                            </a>
                        </div>
                        
                        <!-- Botón de login -->
                        <button type="submit" class="btn btn-login" id="loginBtn">
                            <span id="loginText">Iniciar Sesión con XML</span>
                        </button>
                    </form>
                    
                    <!-- Debug XML -->
                    <button class="xml-toggle" onclick="toggleXmlDebug()">
                        <i class="fas fa-code me-2"></i>Mostrar/Ocultar Debug XML
                    </button>
                    
                    <div id="xmlDebug" class="xml-debug hidden">
                        <h6><i class="fas fa-paper-plane me-2"></i>XML Enviado:</h6>
                        <pre id="xmlRequest"></pre>
                        <hr>
                        <h6><i class="fas fa-reply me-2"></i>XML Recibido:</h6>
                        <pre id="xmlResponse"></pre>
                    </div>
                    
                    <!-- Divider -->
                    <div class="divider">
                        <span>O continúa con</span>
                    </div>
                    
                    <!-- Social login -->
                    <div class="social-login">
                        <a href="#" class="social-btn" onclick="socialLogin('google')">
                            <i class="fab fa-google"></i>
                            Google
                        </a>
                        <a href="#" class="social-btn" onclick="socialLogin('facebook')">
                            <i class="fab fa-facebook-f"></i>
                            Facebook
                        </a>
                    </div>
                    
                    <!-- Register link -->
                    <div class="register-link">
                        ¿No tienes una cuenta? 
                        <a href="#" onclick="showRegister()">Regístrate aquí</a>
                    </div>
                </div>
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
    
    <script>
        // Inicializar cliente XML
        const xmlClient = new XMLClient();
        const xmlTransformer = new XMLTransformer();
        
        // Toggle password visibility
        function togglePassword() {
            const passwordInput = document.getElementById('password');
            const passwordIcon = document.getElementById('passwordIcon');
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                passwordIcon.classList.remove('fa-eye');
                passwordIcon.classList.add('fa-eye-slash');
            } else {
                passwordInput.type = 'password';
                passwordIcon.classList.remove('fa-eye-slash');
                passwordIcon.classList.add('fa-eye');
            }
        }
        
        // Toggle XML debug
        function toggleXmlDebug() {
            const debugDiv = document.getElementById('xmlDebug');
            debugDiv.classList.toggle('hidden');
        }
        
        // Show forgot password modal
        function showForgotPassword() {
            showAlert('info', 'Función de recuperación de contraseña en desarrollo');
        }
        
        // Show register form
        function showRegister() {
            showAlert('info', 'Redirigiendo a página de registro...');
        }
        
        // Social login
        function socialLogin(provider) {
            showAlert('info', `Login con ${provider} en desarrollo`);
        }
        
        // Show alert
        function showAlert(type, message) {
            const alertContainer = document.getElementById('alertContainer');
            const alertClass = type === 'error' ? 'danger' : type;
            const icon = type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-triangle' : 'info-circle';
            
            const alertHtml = `
                <div class="alert alert-${alertClass} alert-dismissible fade show" role="alert">
                    <i class="fas fa-${icon} me-2"></i>
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            `;
            
            alertContainer.innerHTML = alertHtml;
        }
        
        // Format XML for display
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
        
        // Handle form submission
        document.getElementById('loginForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const remember = document.getElementById('remember').checked;
            
            // Validación básica
            if (!username || !password) {
                showAlert('error', 'Por favor completa todos los campos');
                return;
            }
            
            // Mostrar loading
            const loginBtn = document.getElementById('loginBtn');
            const loginText = document.getElementById('loginText');
            loginBtn.disabled = true;
            loginText.innerHTML = '<span class="spinner-border"></span>Procesando...';
            
            try {
                // Crear y mostrar XML de solicitud
                const xmlRequest = xmlClient.createRequest('login', { username, password, remember });
                document.getElementById('xmlRequest').textContent = formatXML(xmlRequest);
                
                // Enviar solicitud XML
                const response = await xmlClient.sendRequest('login', { username, password, remember });
                
                // Mostrar XML de respuesta
                const xmlResponse = await fetch('../backend/xml_endpoint.php', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/xml' },
                    body: xmlRequest
                }).then(res => res.text());
                
                document.getElementById('xmlResponse').textContent = formatXML(xmlResponse);
                
                // Procesar respuesta
                if (response.success) {
                    showAlert('success', response.data.message || 'Login exitoso');
                    
                    // Opcional: mostrar vista previa de transformación
                    if (confirm('¿Desea ver una vista previa de la respuesta XML transformada?')) {
                        await xmlTransformer.previewTransformation(xmlResponse, '../xml_protocol/transformations/response_to_html.xsl');
                    }
                    
                    // Redirigir después de un tiempo
                    setTimeout(() => {
                        window.location.href = '../views/dashboard.php';
                    }, 2000);
                } else {
                    showAlert('error', response.message || 'Error en el login');
                }
                
            } catch (error) {
                console.error('Error en login XML:', error);
                showAlert('error', 'Error de comunicación: ' + error.message);
            } finally {
                // Restaurar botón
                loginBtn.disabled = false;
                loginText.innerHTML = 'Iniciar Sesión con XML';
            }
        });
        
        // Initialize
        document.addEventListener('DOMContentLoaded', function() {
            // Focus on username field
            document.getElementById('username').focus();
            
            // Check for URL parameters
            const urlParams = new URLSearchParams(window.location.search);
            const error = urlParams.get('error');
            const message = urlParams.get('message');
            
            if (error) {
                showAlert('error', decodeURIComponent(message || 'Error en el login'));
            }
            
            if (urlParams.get('logout') === 'success') {
                showAlert('success', 'Sesión cerrada correctamente');
            }
        });
    </script>
</body>
</html>

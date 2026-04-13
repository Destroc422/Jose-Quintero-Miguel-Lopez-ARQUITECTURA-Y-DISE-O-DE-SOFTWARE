<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * VISTA: ADMIN DASHBOARD
 * =============================================
 * 
 * Panel de administración para el rol de administrador
 */

require_once __DIR__ . '/../../backend/controllers/AuthController.php';

// Verificar autenticación y rol
$auth = new AuthController();
$auth->requireAuth('administrador');

// Obtener información del usuario actual
$currentUser = $auth->getCurrentUser();
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Administración - Hermosa Cartagena</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="../../assets/css/style.css" rel="stylesheet">
    
    <style>
        .dashboard-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
            border-radius: 0 0 1rem 1rem;
        }
        
        .stat-card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            border-left: 4px solid transparent;
            height: 100%;
        }
        
        .stat-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
        }
        
        .stat-card.primary {
            border-left-color: #667eea;
        }
        
        .stat-card.success {
            border-left-color: #28a745;
        }
        
        .stat-card.warning {
            border-left-color: #ffc107;
        }
        
        .stat-card.danger {
            border-left-color: #dc3545;
        }
        
        .stat-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            margin-bottom: 1rem;
        }
        
        .stat-icon.primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .stat-icon.success {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
        }
        
        .stat-icon.warning {
            background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
            color: white;
        }
        
        .stat-icon.danger {
            background: linear-gradient(135deg, #dc3545 0%, #e91e63 100%);
            color: white;
        }
        
        .stat-number {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }
        
        .stat-label {
            color: #6c757d;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .chart-container {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .chart-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
        }
        
        .chart-title {
            font-size: 1.25rem;
            font-weight: 600;
            color: #333;
        }
        
        .recent-activity {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        
        .activity-item {
            display: flex;
            align-items: start;
            gap: 1rem;
            padding: 1rem 0;
            border-bottom: 1px solid #f1f3f5;
        }
        
        .activity-item:last-child {
            border-bottom: none;
        }
        
        .activity-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 0.9rem;
            flex-shrink: 0;
        }
        
        .activity-content {
            flex: 1;
        }
        
        .activity-title {
            font-weight: 500;
            margin-bottom: 0.25rem;
        }
        
        .activity-time {
            font-size: 0.8rem;
            color: #6c757d;
        }
        
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }
        
        .quick-action-card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            text-align: center;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            text-decoration: none;
            color: inherit;
        }
        
        .quick-action-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
            color: inherit;
            text-decoration: none;
        }
        
        .quick-action-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            margin: 0 auto 1rem;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .quick-action-title {
            font-weight: 600;
            margin-bottom: 0.5rem;
        }
        
        .quick-action-desc {
            font-size: 0.9rem;
            color: #6c757d;
        }
        
        .welcome-section {
            background: white;
            border-radius: 1rem;
            padding: 2rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .welcome-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 2rem;
        }
        
        .welcome-text h1 {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        
        .welcome-text p {
            color: #6c757d;
            margin-bottom: 0;
        }
        
        .welcome-time {
            text-align: right;
        }
        
        .current-time {
            font-size: 2rem;
            font-weight: 700;
            color: #667eea;
        }
        
        .current-date {
            color: #6c757d;
        }
        
        @media (max-width: 768px) {
            .welcome-content {
                flex-direction: column;
                text-align: center;
            }
            
            .welcome-time {
                text-align: center;
            }
            
            .quick-actions {
                grid-template-columns: 1fr;
            }
            
            .stat-number {
                font-size: 2rem;
            }
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <nav class="sidebar">
        <div class="sidebar-header">
            <i class="fas fa-umbrella-beach fa-2x mb-2"></i>
            <h4>Hermosa Cartagena</h4>
            <small>Panel Administración</small>
        </div>
        
        <div class="sidebar-menu">
            <div class="sidebar-item">
                <a href="#" class="sidebar-link active">
                    <i class="fas fa-tachometer-alt"></i>
                    <span>Dashboard</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="users.php" class="sidebar-link">
                    <i class="fas fa-users"></i>
                    <span>Usuarios</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="services.php" class="sidebar-link">
                    <i class="fas fa-concierge-bell"></i>
                    <span>Servicios</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="reservations.php" class="sidebar-link">
                    <i class="fas fa-calendar-check"></i>
                    <span>Reservas</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="payments.php" class="sidebar-link">
                    <i class="fas fa-credit-card"></i>
                    <span>Pagos</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="providers.php" class="sidebar-link">
                    <i class="fas fa-handshake"></i>
                    <span>Proveedores</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="reports.php" class="sidebar-link">
                    <i class="fas fa-chart-bar"></i>
                    <span>Reportes</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="settings.php" class="sidebar-link">
                    <i class="fas fa-cog"></i>
                    <span>Configuración</span>
                </a>
            </div>
            
            <hr class="my-3">
            
            <div class="sidebar-item">
                <a href="#" class="sidebar-link" onclick="logout()">
                    <i class="fas fa-sign-out-alt"></i>
                    <span>Cerrar Sesión</span>
                </a>
            </div>
        </div>
    </nav>
    
    <!-- Main Content -->
    <div class="main-content">
        <!-- Welcome Section -->
        <div class="welcome-section">
            <div class="welcome-content">
                <div class="welcome-text">
                    <h1>Bienvenido, <?php echo htmlspecialchars($currentUser['nombre_completo']); ?></h1>
                    <p>Panel de Administración - Gestión Turística Hermosa Cartagena</p>
                </div>
                <div class="welcome-time">
                    <div class="current-time" id="currentTime">00:00:00</div>
                    <div class="current-date" id="currentDate">Cargando...</div>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div class="quick-actions">
            <a href="users.php?action=create" class="quick-action-card">
                <div class="quick-action-icon">
                    <i class="fas fa-user-plus"></i>
                </div>
                <div class="quick-action-title">Nuevo Usuario</div>
                <div class="quick-action-desc">Crear cuenta de usuario</div>
            </a>
            
            <a href="services.php?action=create" class="quick-action-card">
                <div class="quick-action-icon">
                    <i class="fas fa-plus-circle"></i>
                </div>
                <div class="quick-action-title">Nuevo Servicio</div>
                <div class="quick-action-desc">Agregar servicio turístico</div>
            </a>
            
            <a href="reservations.php?action=create" class="quick-action-card">
                <div class="quick-action-icon">
                    <i class="fas fa-calendar-plus"></i>
                </div>
                <div class="quick-action-title">Nueva Reserva</div>
                <div class="quick-action-desc">Crear reserva manual</div>
            </a>
            
            <a href="reports.php" class="quick-action-card">
                <div class="quick-action-icon">
                    <i class="fas fa-file-alt"></i>
                </div>
                <div class="quick-action-title">Generar Reporte</div>
                <div class="quick-action-desc">Ver informes y estadísticas</div>
            </a>
        </div>
        
        <!-- Statistics Cards -->
        <div class="row mb-4">
            <div class="col-lg-3 col-md-6 mb-4">
                <div class="stat-card primary">
                    <div class="stat-icon primary">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stat-number" id="totalUsers">0</div>
                    <div class="stat-label">Total Usuarios</div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 mb-4">
                <div class="stat-card success">
                    <div class="stat-icon success">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                    <div class="stat-number" id="totalReservations">0</div>
                    <div class="stat-label">Reservas Activas</div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 mb-4">
                <div class="stat-card warning">
                    <div class="stat-icon warning">
                        <i class="fas fa-concierge-bell"></i>
                    </div>
                    <div class="stat-number" id="totalServices">0</div>
                    <div class="stat-label">Servicios</div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 mb-4">
                <div class="stat-card danger">
                    <div class="stat-icon danger">
                        <i class="fas fa-money-bill-wave"></i>
                    </div>
                    <div class="stat-number" id="totalRevenue">$0</div>
                    <div class="stat-label">Ingresos Mensuales</div>
                </div>
            </div>
        </div>
        
        <!-- Charts Section -->
        <div class="row">
            <div class="col-lg-8 mb-4">
                <div class="chart-container">
                    <div class="chart-header">
                        <h3 class="chart-title">Reservas Mensuales</h3>
                        <div class="btn-group" role="group">
                            <button type="button" class="btn btn-sm btn-outline-primary" onclick="updateChart('month')">Mes</button>
                            <button type="button" class="btn btn-sm btn-outline-primary" onclick="updateChart('year')">Año</button>
                        </div>
                    </div>
                    <canvas id="reservationsChart" height="100"></canvas>
                </div>
            </div>
            
            <div class="col-lg-4 mb-4">
                <div class="chart-container">
                    <div class="chart-header">
                        <h3 class="chart-title">Distribución de Servicios</h3>
                    </div>
                    <canvas id="servicesChart" height="200"></canvas>
                </div>
            </div>
        </div>
        
        <!-- Recent Activity -->
        <div class="row">
            <div class="col-lg-6 mb-4">
                <div class="recent-activity">
                    <div class="chart-header">
                        <h3 class="chart-title">Actividad Reciente</h3>
                        <a href="history.php" class="btn btn-sm btn-outline-primary">Ver Todo</a>
                    </div>
                    <div id="recentActivity">
                        <!-- Activity items will be loaded here -->
                    </div>
                </div>
            </div>
            
            <div class="col-lg-6 mb-4">
                <div class="recent-activity">
                    <div class="chart-header">
                        <h3 class="chart-title">Reservas Próximas</h3>
                        <a href="reservations.php" class="btn btn-sm btn-outline-primary">Ver Todas</a>
                    </div>
                    <div id="upcomingReservations">
                        <!-- Upcoming reservations will be loaded here -->
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- Custom JS -->
    <script src="../../assets/js/auth.js"></script>
    
    <script>
        // Update current time
        function updateDateTime() {
            const now = new Date();
            const timeElement = document.getElementById('currentTime');
            const dateElement = document.getElementById('currentDate');
            
            const timeString = now.toLocaleTimeString('es-CO', { 
                hour: '2-digit', 
                minute: '2-digit', 
                second: '2-digit' 
            });
            
            const dateString = now.toLocaleDateString('es-CO', { 
                weekday: 'long', 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric' 
            });
            
            timeElement.textContent = timeString;
            dateElement.textContent = dateString.charAt(0).toUpperCase() + dateString.slice(1);
        }
        
        // Load dashboard statistics
        async function loadStatistics() {
            try {
                const response = await fetch('../../backend/api/dashboard.php?action=stats');
                const data = await response.json();
                
                if (data.success) {
                    document.getElementById('totalUsers').textContent = data.stats.total_users || 0;
                    document.getElementById('totalReservations').textContent = data.stats.active_reservations || 0;
                    document.getElementById('totalServices').textContent = data.stats.total_services || 0;
                    document.getElementById('totalRevenue').textContent = '$' + (data.stats.monthly_revenue || 0).toLocaleString();
                }
            } catch (error) {
                console.error('Error loading statistics:', error);
            }
        }
        
        // Load recent activity
        async function loadRecentActivity() {
            try {
                const response = await fetch('../../backend/api/dashboard.php?action=activity');
                const data = await response.json();
                
                if (data.success) {
                    const container = document.getElementById('recentActivity');
                    container.innerHTML = '';
                    
                    data.activities.forEach(activity => {
                        const activityHtml = `
                            <div class="activity-item">
                                <div class="activity-icon bg-primary text-white">
                                    <i class="fas fa-${getActivityIcon(activity.tipo_operacion)}"></i>
                                </div>
                                <div class="activity-content">
                                    <div class="activity-title">${activity.descripcion_operacion}</div>
                                    <div class="activity-time">${formatTime(activity.fecha_operacion)}</div>
                                </div>
                            </div>
                        `;
                        container.innerHTML += activityHtml;
                    });
                }
            } catch (error) {
                console.error('Error loading recent activity:', error);
            }
        }
        
        // Load upcoming reservations
        async function loadUpcomingReservations() {
            try {
                const response = await fetch('../../backend/api/dashboard.php?action=upcoming');
                const data = await response.json();
                
                if (data.success) {
                    const container = document.getElementById('upcomingReservations');
                    container.innerHTML = '';
                    
                    data.reservations.forEach(reservation => {
                        const reservationHtml = `
                            <div class="activity-item">
                                <div class="activity-icon bg-success text-white">
                                    <i class="fas fa-calendar"></i>
                                </div>
                                <div class="activity-content">
                                    <div class="activity-title">${reservation.nombre_servicio}</div>
                                    <div class="activity-time">
                                        ${reservation.nombre_cliente} - ${formatDate(reservation.fecha_inicio_servicio)}
                                    </div>
                                </div>
                            </div>
                        `;
                        container.innerHTML += reservationHtml;
                    });
                }
            } catch (error) {
                console.error('Error loading upcoming reservations:', error);
            }
        }
        
        // Initialize charts
        function initCharts() {
            // Reservations Chart
            const reservationsCtx = document.getElementById('reservationsChart').getContext('2d');
            window.reservationsChart = new Chart(reservationsCtx, {
                type: 'line',
                data: {
                    labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
                    datasets: [{
                        label: 'Reservas',
                        data: [12, 19, 3, 5, 2, 3],
                        borderColor: '#667eea',
                        backgroundColor: 'rgba(102, 126, 234, 0.1)',
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
            
            // Services Chart
            const servicesCtx = document.getElementById('servicesChart').getContext('2d');
            window.servicesChart = new Chart(servicesCtx, {
                type: 'doughnut',
                data: {
                    labels: ['Tours', 'Hospedaje', 'Transporte', 'Alimentación', 'Actividades'],
                    datasets: [{
                        data: [30, 20, 25, 15, 10],
                        backgroundColor: [
                            '#667eea',
                            '#764ba2',
                            '#f093fb',
                            '#28a745',
                            '#ffc107'
                        ]
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
        }
        
        // Helper functions
        function getActivityIcon(type) {
            const icons = {
                'creacion': 'plus',
                'actualizacion': 'edit',
                'eliminacion': 'trash',
                'login': 'sign-in-alt',
                'logout': 'sign-out-alt',
                'pago': 'credit-card',
                'cancelacion': 'times'
            };
            return icons[type] || 'info-circle';
        }
        
        function formatTime(dateString) {
            const date = new Date(dateString);
            const now = new Date();
            const diff = now - date;
            const minutes = Math.floor(diff / 60000);
            const hours = Math.floor(diff / 3600000);
            const days = Math.floor(diff / 86400000);
            
            if (minutes < 1) return 'Ahora';
            if (minutes < 60) return `Hace ${minutes} minutos`;
            if (hours < 24) return `Hace ${hours} horas`;
            return `Hace ${days} días`;
        }
        
        function formatDate(dateString) {
            const date = new Date(dateString);
            return date.toLocaleDateString('es-CO', {
                day: 'numeric',
                month: 'short',
                year: 'numeric'
            });
        }
        
        // Update chart based on period
        function updateChart(period) {
            // Implement chart update logic
            console.log('Updating chart for period:', period);
        }
        
        // Initialize dashboard
        document.addEventListener('DOMContentLoaded', function() {
            updateDateTime();
            setInterval(updateDateTime, 1000);
            
            loadStatistics();
            loadRecentActivity();
            loadUpcomingReservations();
            initCharts();
            
            // Refresh data every 5 minutes
            setInterval(() => {
                loadStatistics();
                loadRecentActivity();
                loadUpcomingReservations();
            }, 300000);
        });
        
        // Logout function
        function logout() {
            if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
                Auth.logout();
            }
        }
    </script>
</body>
</html>

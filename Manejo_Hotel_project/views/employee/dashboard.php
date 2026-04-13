<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * VISTA: EMPLOYEE DASHBOARD
 * =============================================
 * 
 * Panel de control para el rol de empleado
 */

require_once __DIR__ . '/../../backend/controllers/AuthController.php';

// Verificar autenticación y rol
$auth = new AuthController();
$auth->requireAuth('empleado');

// Obtener información del usuario actual
$currentUser = $auth->getCurrentUser();
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Empleado - Hermosa Cartagena</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="../../assets/css/style.css" rel="stylesheet">
    
    <style>
        .employee-header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
            border-radius: 0 0 1rem 1rem;
        }
        
        .task-card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            border-left: 4px solid transparent;
            height: 100%;
            cursor: pointer;
        }
        
        .task-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
        }
        
        .task-card.pending {
            border-left-color: #ffc107;
        }
        
        .task-card.urgent {
            border-left-color: #dc3545;
        }
        
        .task-card.completed {
            border-left-color: #28a745;
        }
        
        .task-priority {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: 500;
            margin-bottom: 1rem;
        }
        
        .priority-high {
            background: rgba(220, 53, 69, 0.1);
            color: #dc3545;
        }
        
        .priority-medium {
            background: rgba(255, 193, 7, 0.1);
            color: #ffc107;
        }
        
        .priority-low {
            background: rgba(40, 167, 69, 0.1);
            color: #28a745;
        }
        
        .reservation-card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 1rem;
            transition: all 0.3s ease;
        }
        
        .reservation-card:hover {
            transform: translateX(4px);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
        }
        
        .reservation-status {
            display: inline-block;
            padding: 0.375rem 0.75rem;
            border-radius: 0.5rem;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        .status-pending {
            background: rgba(255, 193, 7, 0.1);
            color: #ffc107;
        }
        
        .status-confirmed {
            background: rgba(40, 167, 69, 0.1);
            color: #28a745;
        }
        
        .status-paid {
            background: rgba(23, 162, 184, 0.1);
            color: #17a2b8;
        }
        
        .quick-action-btn {
            width: 100%;
            padding: 1rem;
            border: none;
            border-radius: 0.75rem;
            font-weight: 500;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.75rem;
            margin-bottom: 1rem;
        }
        
        .quick-action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
        }
        
        .btn-reserve {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
        }
        
        .btn-payment {
            background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
            color: white;
        }
        
        .btn-service {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .performance-metric {
            text-align: center;
            padding: 1.5rem;
            background: white;
            border-radius: 1rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
        }
        
        .performance-metric:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
        }
        
        .metric-value {
            font-size: 2.5rem;
            font-weight: 700;
            color: #28a745;
            margin-bottom: 0.5rem;
        }
        
        .metric-label {
            color: #6c757d;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .calendar-widget {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        
        .calendar-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }
        
        .calendar-grid {
            display: grid;
            grid-template-columns: repeat(7, 1fr);
            gap: 0.5rem;
            text-align: center;
        }
        
        .calendar-day {
            padding: 0.5rem;
            border-radius: 0.5rem;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .calendar-day:hover {
            background: rgba(40, 167, 69, 0.1);
        }
        
        .calendar-day.has-events {
            background: rgba(40, 167, 69, 0.2);
            color: #28a745;
            font-weight: 600;
        }
        
        .calendar-day.today {
            background: #28a745;
            color: white;
        }
        
        .notification-item {
            display: flex;
            align-items: start;
            gap: 1rem;
            padding: 1rem 0;
            border-bottom: 1px solid #f1f3f5;
        }
        
        .notification-item:last-child {
            border-bottom: none;
        }
        
        .notification-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }
        
        .notification-icon.new {
            background: rgba(40, 167, 69, 0.1);
            color: #28a745;
        }
        
        .notification-icon.warning {
            background: rgba(255, 193, 7, 0.1);
            color: #ffc107;
        }
        
        .notification-icon.info {
            background: rgba(102, 126, 234, 0.1);
            color: #667eea;
        }
        
        .notification-content {
            flex: 1;
        }
        
        .notification-title {
            font-weight: 500;
            margin-bottom: 0.25rem;
        }
        
        .notification-time {
            font-size: 0.8rem;
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
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        
        .welcome-text p {
            color: #6c757d;
            margin-bottom: 0;
        }
        
        .shift-info {
            text-align: right;
        }
        
        .current-shift {
            font-size: 1.25rem;
            font-weight: 600;
            color: #28a745;
        }
        
        .shift-time {
            color: #6c757d;
        }
        
        @media (max-width: 768px) {
            .welcome-content {
                flex-direction: column;
                text-align: center;
            }
            
            .shift-info {
                text-align: center;
            }
            
            .quick-action-btn {
                font-size: 0.9rem;
            }
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <nav class="sidebar">
        <div class="sidebar-header" style="background: linear-gradient(135deg, #28a745 0%, #20c997 100%);">
            <i class="fas fa-user-tie fa-2x mb-2"></i>
            <h4>Panel Empleado</h4>
            <small>Hermosa Cartagena</small>
        </div>
        
        <div class="sidebar-menu">
            <div class="sidebar-item">
                <a href="#" class="sidebar-link active">
                    <i class="fas fa-tachometer-alt"></i>
                    <span>Dashboard</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="reservations.php" class="sidebar-link">
                    <i class="fas fa-calendar-check"></i>
                    <span>Mis Reservas</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="payments.php" class="sidebar-link">
                    <i class="fas fa-credit-card"></i>
                    <span>Gestión de Pagos</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="services.php" class="sidebar-link">
                    <i class="fas fa-concierge-bell"></i>
                    <span>Servicios</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="customers.php" class="sidebar-link">
                    <i class="fas fa-users"></i>
                    <span>Clientes</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="schedule.php" class="sidebar-link">
                    <i class="fas fa-calendar-alt"></i>
                    <span>Mi Horario</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="reports.php" class="sidebar-link">
                    <i class="fas fa-chart-line"></i>
                    <span>Mis Reportes</span>
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
                    <p>Panel de Control de Empleado - Gestión Turística Hermosa Cartagena</p>
                </div>
                <div class="shift-info">
                    <div class="current-shift">Turno: Mañana</div>
                    <div class="shift-time">08:00 - 16:00</div>
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div class="row mb-4">
            <div class="col-md-4 mb-3">
                <button class="quick-action-btn btn-reserve" onclick="createReservation()">
                    <i class="fas fa-calendar-plus"></i>
                    <span>Nueva Reserva</span>
                </button>
            </div>
            <div class="col-md-4 mb-3">
                <button class="quick-action-btn btn-payment" onclick="processPayment()">
                    <i class="fas fa-money-check-alt"></i>
                    <span>Procesar Pago</span>
                </button>
            </div>
            <div class="col-md-4 mb-3">
                <button class="quick-action-btn btn-service" onclick="manageServices()">
                    <i class="fas fa-tasks"></i>
                    <span>Gestionar Servicios</span>
                </button>
            </div>
        </div>
        
        <!-- Performance Metrics -->
        <div class="row mb-4">
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="performance-metric">
                    <div class="metric-value" id="todayReservations">0</div>
                    <div class="metric-label">Reservas Hoy</div>
                </div>
            </div>
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="performance-metric">
                    <div class="metric-value" id="pendingPayments">0</div>
                    <div class="metric-label">Pagos Pendientes</div>
                </div>
            </div>
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="performance-metric">
                    <div class="metric-value" id="completedTasks">0</div>
                    <div class="metric-label">Tareas Completadas</div>
                </div>
            </div>
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="performance-metric">
                    <div class="metric-value" id="customerRating">0</div>
                    <div class="metric-label">Rating Clientes</div>
                </div>
            </div>
        </div>
        
        <!-- Main Content Grid -->
        <div class="row">
            <!-- Today's Reservations -->
            <div class="col-lg-8 mb-4">
                <div class="card">
                    <div class="card-header" style="background: linear-gradient(135deg, #28a745 0%, #20c997 100%); color: white;">
                        <h5 class="mb-0">
                            <i class="fas fa-calendar-day me-2"></i>
                            Reservas del Día
                        </h5>
                    </div>
                    <div class="card-body">
                        <div id="todayReservationsList">
                            <!-- Reservations will be loaded here -->
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Calendar & Notifications -->
            <div class="col-lg-4 mb-4">
                <!-- Calendar Widget -->
                <div class="calendar-widget mb-4">
                    <div class="calendar-header">
                        <h6 class="mb-0">Calendario</h6>
                        <div class="btn-group btn-group-sm" role="group">
                            <button type="button" class="btn btn-outline-secondary" onclick="changeMonth(-1)">
                                <i class="fas fa-chevron-left"></i>
                            </button>
                            <button type="button" class="btn btn-outline-secondary" onclick="changeMonth(1)">
                                <i class="fas fa-chevron-right"></i>
                            </button>
                        </div>
                    </div>
                    <div class="calendar-grid" id="calendarGrid">
                        <!-- Calendar will be generated here -->
                    </div>
                </div>
                
                <!-- Notifications -->
                <div class="card">
                    <div class="card-header" style="background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%); color: white;">
                        <h6 class="mb-0">
                            <i class="fas fa-bell me-2"></i>
                            Notificaciones
                        </h6>
                    </div>
                    <div class="card-body">
                        <div id="notificationsList">
                            <!-- Notifications will be loaded here -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Pending Tasks -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                        <h5 class="mb-0">
                            <i class="fas fa-tasks me-2"></i>
                            Tareas Pendientes
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row" id="tasksList">
                            <!-- Tasks will be loaded here -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Custom JS -->
    <script src="../../assets/js/auth.js"></script>
    
    <script>
        // Load employee dashboard data
        async function loadDashboardData() {
            try {
                const response = await fetch('../../backend/api/employee_dashboard.php?action=dashboard');
                const data = await response.json();
                
                if (data.success) {
                    // Update metrics
                    document.getElementById('todayReservations').textContent = data.metrics.today_reservations || 0;
                    document.getElementById('pendingPayments').textContent = data.metrics.pending_payments || 0;
                    document.getElementById('completedTasks').textContent = data.metrics.completed_tasks || 0;
                    document.getElementById('customerRating').textContent = data.metrics.customer_rating || '0.0';
                    
                    // Load reservations
                    loadTodayReservations(data.reservations);
                    
                    // Load tasks
                    loadTasks(data.tasks);
                    
                    // Load notifications
                    loadNotifications(data.notifications);
                }
            } catch (error) {
                console.error('Error loading dashboard data:', error);
            }
        }
        
        // Load today's reservations
        function loadTodayReservations(reservations) {
            const container = document.getElementById('todayReservationsList');
            container.innerHTML = '';
            
            if (!reservations || reservations.length === 0) {
                container.innerHTML = '<p class="text-muted text-center">No hay reservas para hoy</p>';
                return;
            }
            
            reservations.forEach(reservation => {
                const reservationHtml = `
                    <div class="reservation-card">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="mb-1">${reservation.nombre_servicio}</h6>
                                <p class="mb-1 text-muted">
                                    <i class="fas fa-user me-1"></i> ${reservation.nombre_cliente}
                                </p>
                                <p class="mb-1 text-muted">
                                    <i class="fas fa-clock me-1"></i> ${reservation.hora_inicio}
                                </p>
                                <p class="mb-0">
                                    <span class="reservation-status status-${reservation.estado_reserva}">
                                        ${getStatusLabel(reservation.estado_reserva)}
                                    </span>
                                </p>
                            </div>
                            <div class="text-end">
                                <button class="btn btn-sm btn-primary" onclick="viewReservation(${reservation.id_reserva})">
                                    <i class="fas fa-eye"></i>
                                </button>
                                ${reservation.estado_reserva === 'pendiente' ? `
                                    <button class="btn btn-sm btn-success ms-1" onclick="confirmReservation(${reservation.id_reserva})">
                                        <i class="fas fa-check"></i>
                                    </button>
                                ` : ''}
                            </div>
                        </div>
                    </div>
                `;
                container.innerHTML += reservationHtml;
            });
        }
        
        // Load tasks
        function loadTasks(tasks) {
            const container = document.getElementById('tasksList');
            container.innerHTML = '';
            
            if (!tasks || tasks.length === 0) {
                container.innerHTML = '<div class="col-12"><p class="text-muted text-center">No hay tareas pendientes</p></div>';
                return;
            }
            
            tasks.forEach(task => {
                const taskHtml = `
                    <div class="col-md-6 col-lg-4 mb-3">
                        <div class="task-card ${task.priority}" onclick="viewTask(${task.id_tarea})">
                            <span class="task-priority priority-${task.priority}">
                                ${getPriorityLabel(task.priority)}
                            </span>
                            <h6 class="mb-2">${task.titulo}</h6>
                            <p class="mb-2 text-muted small">${task.descripcion}</p>
                            <div class="d-flex justify-content-between align-items-center">
                                <small class="text-muted">
                                    <i class="fas fa-clock me-1"></i>
                                    ${formatTime(task.fecha_creacion)}
                                </small>
                                <button class="btn btn-sm btn-outline-success" onclick="completeTask(event, ${task.id_tarea})">
                                    <i class="fas fa-check"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                `;
                container.innerHTML += taskHtml;
            });
        }
        
        // Load notifications
        function loadNotifications(notifications) {
            const container = document.getElementById('notificationsList');
            container.innerHTML = '';
            
            if (!notifications || notifications.length === 0) {
                container.innerHTML = '<p class="text-muted text-center">No hay notificaciones nuevas</p>';
                return;
            }
            
            notifications.forEach(notification => {
                const notificationHtml = `
                    <div class="notification-item">
                        <div class="notification-icon ${notification.type}">
                            <i class="fas fa-${getNotificationIcon(notification.type)}"></i>
                        </div>
                        <div class="notification-content">
                            <div class="notification-title">${notification.titulo}</div>
                            <div class="notification-time">${formatTime(notification.fecha_creacion)}</div>
                        </div>
                    </div>
                `;
                container.innerHTML += notificationHtml;
            });
        }
        
        // Generate calendar
        function generateCalendar() {
            const grid = document.getElementById('calendarGrid');
            const now = new Date();
            const year = now.getFullYear();
            const month = now.getMonth();
            const firstDay = new Date(year, month, 1).getDay();
            const daysInMonth = new Date(year, month + 1, 0).getDate();
            const today = now.getDate();
            
            // Day headers
            const dayHeaders = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
            grid.innerHTML = '';
            
            dayHeaders.forEach(day => {
                const header = document.createElement('div');
                header.className = 'small fw-bold text-muted';
                header.textContent = day;
                grid.appendChild(header);
            });
            
            // Empty cells before first day
            for (let i = 0; i < firstDay; i++) {
                grid.appendChild(document.createElement('div'));
            }
            
            // Days of month
            for (let day = 1; day <= daysInMonth; day++) {
                const dayElement = document.createElement('div');
                dayElement.className = 'calendar-day';
                dayElement.textContent = day;
                
                if (day === today) {
                    dayElement.classList.add('today');
                }
                
                // Simulate some days with events
                if ([5, 12, 18, 25].includes(day)) {
                    dayElement.classList.add('has-events');
                }
                
                dayElement.onclick = () => selectDate(year, month, day);
                grid.appendChild(dayElement);
            }
        }
        
        // Helper functions
        function getStatusLabel(status) {
            const labels = {
                'pendiente': 'Pendiente',
                'confirmada': 'Confirmada',
                'pagada': 'Pagada',
                'cancelada': 'Cancelada',
                'completada': 'Completada'
            };
            return labels[status] || status;
        }
        
        function getPriorityLabel(priority) {
            const labels = {
                'high': 'Alta',
                'medium': 'Media',
                'low': 'Baja'
            };
            return labels[priority] || priority;
        }
        
        function getNotificationIcon(type) {
            const icons = {
                'new': 'bell',
                'warning': 'exclamation-triangle',
                'info': 'info-circle'
            };
            return icons[type] || 'bell';
        }
        
        function formatTime(dateString) {
            const date = new Date(dateString);
            const now = new Date();
            const diff = now - date;
            const minutes = Math.floor(diff / 60000);
            const hours = Math.floor(diff / 3600000);
            const days = Math.floor(diff / 86400000);
            
            if (minutes < 1) return 'Ahora';
            if (minutes < 60) return `Hace ${minutes} min`;
            if (hours < 24) return `Hace ${hours} h`;
            return `Hace ${days} d`;
        }
        
        // Action functions
        function createReservation() {
            window.location.href = 'reservations.php?action=create';
        }
        
        function processPayment() {
            window.location.href = 'payments.php';
        }
        
        function manageServices() {
            window.location.href = 'services.php';
        }
        
        function viewReservation(id) {
            window.location.href = `reservations.php?action=view&id=${id}`;
        }
        
        function confirmReservation(id) {
            if (confirm('¿Deseas confirmar esta reserva?')) {
                // Implement confirmation logic
                console.log('Confirming reservation:', id);
            }
        }
        
        function viewTask(id) {
            // Implement task view logic
            console.log('Viewing task:', id);
        }
        
        function completeTask(event, id) {
            event.stopPropagation();
            if (confirm('¿Deseas marcar esta tarea como completada?')) {
                // Implement task completion logic
                console.log('Completing task:', id);
            }
        }
        
        function selectDate(year, month, day) {
            console.log('Selected date:', year, month, day);
        }
        
        function changeMonth(direction) {
            // Implement month change logic
            console.log('Changing month:', direction);
        }
        
        // Initialize dashboard
        document.addEventListener('DOMContentLoaded', function() {
            loadDashboardData();
            generateCalendar();
            
            // Refresh data every 2 minutes
            setInterval(loadDashboardData, 120000);
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

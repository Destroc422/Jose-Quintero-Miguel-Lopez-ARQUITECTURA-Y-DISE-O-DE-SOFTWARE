/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * JAVASCRIPT - DASHBOARD
 * =============================================
 * 
 * Módulo para manejo de dashboards dinámicos según rol.
 * Carga diferentes vistas y componentes para cada tipo de usuario.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */

// Estado del dashboard
let currentPage = 'dashboard';
let currentCharts = {};
let dashboardData = {};

/**
 * Inicializa el dashboard
 */
function initDashboard() {
    // Configurar navegación
    setupNavigation();
    
    // Cargar página inicial
    loadPage('dashboard');
    
    // Configurar actualizaciones automáticas
    setupAutoRefresh();
}

/**
 * Configura la navegación del dashboard
 */
function setupNavigation() {
    // Manejar clicks en el menú lateral
    document.querySelectorAll('.sidebar-link[data-page]').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.getAttribute('data-page');
            loadPage(page);
        });
    });
    
    // Manejar breadcrumbs
    updateBreadcrumb('Dashboard');
}

/**
 * Carga una página específica del dashboard
 */
async function loadPage(page) {
    try {
        showLoading();
        
        currentPage = page;
        
        // Actualizar navegación activa
        updateActiveNavigation(page);
        
        // Actualizar breadcrumb
        updateBreadcrumb(getPageTitle(page));
        
        // Limpiar charts existentes
        clearCharts();
        
        // Cargar contenido según página
        const content = await loadPageContent(page);
        
        // Insertar contenido
        document.getElementById('pageContent').innerHTML = content;
        
        // Inicializar componentes específicos de la página
        initializePageComponents(page);
        
        // Cargar datos específicas
        await loadPageData(page);
        
        hideLoading();
        
    } catch (error) {
        console.error('Error cargando página:', error);
        showNotification('error', 'Error al cargar la página');
        hideLoading();
    }
}

/**
 * Obtiene el título de la página
 */
function getPageTitle(page) {
    const titles = {
        'dashboard': 'Dashboard',
        'reservas': 'Reservas',
        'servicios': 'Servicios',
        'pagos': 'Pagos',
        'usuarios': 'Usuarios',
        'proveedores': 'Proveedores',
        'reportes': 'Reportes',
        'configuracion': 'Configuración',
        'perfil': 'Mi Perfil',
        'historial': 'Historial',
        'favoritos': 'Favoritos'
    };
    
    return titles[page] || page;
}

/**
 * Actualiza la navegación activa
 */
function updateActiveNavigation(page) {
    // Remover clase active de todos los enlaces
    document.querySelectorAll('.sidebar-link').forEach(link => {
        link.classList.remove('active');
    });
    
    // Agregar clase active al enlace actual
    const activeLink = document.querySelector(`.sidebar-link[data-page="${page}"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }
}

/**
 * Actualiza el breadcrumb
 */
function updateBreadcrumb(title) {
    document.getElementById('breadcrumb').textContent = title;
}

/**
 * Limpia los charts existentes
 */
function clearCharts() {
    Object.values(currentCharts).forEach(chart => {
        if (chart) {
            chart.destroy();
        }
    });
    currentCharts = {};
}

/**
 * Carga el contenido de una página
 */
async function loadPageContent(page) {
    const userRole = getCurrentUser().nombreRol;
    
    switch (page) {
        case 'dashboard':
            return await loadDashboardContent(userRole);
        case 'reservas':
            return await loadReservasContent(userRole);
        case 'servicios':
            return await loadServiciosContent(userRole);
        case 'pagos':
            return await loadPagosContent(userRole);
        case 'usuarios':
            return await loadUsuariosContent(userRole);
        case 'perfil':
            return await loadPerfilContent(userRole);
        default:
            return `<div class="text-center">
                <h3>Página en construcción</h3>
                <p>La página ${page} está siendo desarrollada.</p>
            </div>`;
    }
}

/**
 * Carga contenido del dashboard según rol
 */
async function loadDashboardContent(role) {
    switch (role) {
        case 'ADMIN':
            return await loadAdminDashboard();
        case 'EMPLEADO':
            return await loadEmpleadoDashboard();
        case 'CLIENTE':
            return await loadClienteDashboard();
        default:
            return '<div class="text-center"><h3>Rol no reconocido</h3></div>';
    }
}

/**
 * Dashboard de Administrador
 */
async function loadAdminDashboard() {
    return `
        <div class="dashboard-header">
            <h1>Panel de Administración</h1>
            <p class="text-muted">Gestión completa del sistema</p>
        </div>
        
        <!-- Estadísticas Principales -->
        <div class="row mb-4">
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="stat-card primary">
                    <div class="stat-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number" id="totalUsuarios">0</div>
                        <div class="stat-label">Usuarios Totales</div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="stat-card success">
                    <div class="stat-icon">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number" id="totalReservas">0</div>
                        <div class="stat-label">Reservas Activas</div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="stat-card warning">
                    <div class="stat-icon">
                        <i class="fas fa-concierge-bell"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number" id="totalServicios">0</div>
                        <div class="stat-label">Servicios</div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="stat-card info">
                    <div class="stat-icon">
                        <i class="fas fa-money-bill-wave"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number" id="ingresosMensuales">$0</div>
                        <div class="stat-label">Ingresos Mensuales</div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Gráficos -->
        <div class="row mb-4">
            <div class="col-lg-8 mb-3">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Reservas Mensuales</h5>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-outline-primary" onclick="updateReservasChart('month')">Mes</button>
                            <button class="btn btn-outline-primary" onclick="updateReservasChart('year')">Año</button>
                        </div>
                    </div>
                    <div class="card-body">
                        <canvas id="reservasChart" height="100"></canvas>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-4 mb-3">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Distribución de Servicios</h5>
                    </div>
                    <div class="card-body">
                        <canvas id="serviciosChart" height="200"></canvas>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Actividad Reciente -->
        <div class="row">
            <div class="col-lg-6 mb-3">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Actividad Reciente</h5>
                        <a href="#" class="btn btn-sm btn-outline-primary">Ver Todo</a>
                    </div>
                    <div class="card-body">
                        <div id="actividadReciente">
                            <!-- Se cargará dinámicamente -->
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-6 mb-3">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Reservas Próximas</h5>
                        <a href="#" class="btn btn-sm btn-outline-primary">Ver Todas</a>
                    </div>
                    <div class="card-body">
                        <div id="reservasProximas">
                            <!-- Se cargará dinámicamente -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * Dashboard de Empleado
 */
async function loadEmpleadoDashboard() {
    return `
        <div class="dashboard-header">
            <h1>Mi Panel</h1>
            <p class="text-muted">Gestión de reservas y servicios</p>
        </div>
        
        <!-- Estadísticas de Empleado -->
        <div class="row mb-4">
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="stat-card success">
                    <div class="stat-icon">
                        <i class="fas fa-calendar-day"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number" id="reservasHoy">0</div>
                        <div class="stat-label">Reservas Hoy</div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="stat-card warning">
                    <div class="stat-icon">
                        <i class="fas fa-credit-card"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number" id="pagosPendientes">0</div>
                        <div class="stat-label">Pagos Pendientes</div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="stat-card info">
                    <div class="stat-icon">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number" id="tareasCompletadas">0</div>
                        <div class="stat-label">Tareas Completadas</div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 mb-3">
                <div class="stat-card primary">
                    <div class="stat-icon">
                        <i class="fas fa-star"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number" id="ratingPromedio">0.0</div>
                        <div class="stat-label">Rating Clientes</div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Acciones Rápidas -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Acciones Rápidas</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-3 mb-2">
                                <button class="btn btn-success w-100" onclick="showCreateReservaModal()">
                                    <i class="fas fa-plus-circle me-2"></i>Nueva Reserva
                                </button>
                            </div>
                            <div class="col-md-3 mb-2">
                                <button class="btn btn-info w-100" onclick="showProcessPaymentModal()">
                                    <i class="fas fa-money-check-alt me-2"></i>Procesar Pago
                                </button>
                            </div>
                            <div class="col-md-3 mb-2">
                                <button class="btn btn-primary w-100" onclick="navigateToServices()">
                                    <i class="fas fa-list me-2"></i>Gestionar Servicios
                                </button>
                            </div>
                            <div class="col-md-3 mb-2">
                                <button class="btn btn-warning w-100" onclick="navigateToSchedule()">
                                    <i class="fas fa-calendar me-2"></i>Mi Horario
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Reservas del Día -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Reservas del Día</h5>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-outline-primary active">Hoy</button>
                            <button class="btn btn-outline-primary">Semana</button>
                            <button class="btn btn-outline-primary">Mes</button>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover" id="reservasDiaTable">
                                <thead>
                                    <tr>
                                        <th>Código</th>
                                        <th>Cliente</th>
                                        <th>Servicio</th>
                                        <th>Horario</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- Se cargará dinámicamente -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * Dashboard de Cliente
 */
async function loadClienteDashboard() {
    return `
        <div class="dashboard-header">
            <h1>Mi Panel</h1>
            <p class="text-muted">Explora y reserva servicios turísticos</p>
        </div>
        
        <!-- Perfil del Cliente -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h4 class="mb-1">¡Bienvenido, ${getCurrentUser().nombreCompleto}!</h4>
                                <p class="text-muted mb-0">Descubre las maravillas de Cartagena con nosotros</p>
                            </div>
                            <div class="col-md-4 text-end">
                                <div class="user-stats">
                                    <div class="stat-item">
                                        <div class="stat-value" id="totalReservasCliente">0</div>
                                        <div class="stat-label">Reservas Totales</div>
                                    </div>
                                    <div class="stat-item">
                                        <div class="stat-value" id="puntosLealtad">0</div>
                                        <div class="stat-label">Puntos Lealtad</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Servicios Destacados -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Servicios Destacados</h5>
                        <button class="btn btn-outline-primary" onclick="navigateToServices()">
                            Explorar Todos <i class="fas fa-arrow-right ms-1"></i>
                        </button>
                    </div>
                    <div class="card-body">
                        <div class="row" id="serviciosDestacados">
                            <!-- Se cargará dinámicamente -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Búsqueda y Filtros -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Buscar Servicios</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <div class="form-floating">
                                    <input type="text" class="form-control" id="busquedaServicios" 
                                           placeholder="¿Qué estás buscando?">
                                    <label for="busquedaServicios">Buscar servicios</label>
                                </div>
                            </div>
                            <div class="col-md-3 mb-3">
                                <div class="form-floating">
                                    <select class="form-select" id="filtroTipo">
                                        <option value="">Todos los tipos</option>
                                        <option value="tour">Tours</option>
                                        <option value="hospedaje">Hospedaje</option>
                                        <option value="transporte">Transporte</option>
                                        <option value="alimentacion">Alimentación</option>
                                        <option value="actividad">Actividades</option>
                                    </select>
                                    <label for="filtroTipo">Tipo de servicio</label>
                                </div>
                            </div>
                            <div class="col-md-3 mb-3">
                                <div class="form-floating">
                                    <input type="number" class="form-control" id="filtroPersonas" 
                                           placeholder="Cantidad">
                                    <label for="filtroPersonas">Personas</label>
                                </div>
                            </div>
                            <div class="col-md-2 mb-3">
                                <button class="btn btn-primary w-100 h-100" onclick="buscarServicios()">
                                    <i class="fas fa-search me-2"></i>Buscar
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Resultados de Búsqueda -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Resultados</h5>
                        <div class="d-flex align-items-center">
                            <span class="badge bg-primary me-2" id="resultadosCount">0</span>
                            <span class="text-muted">servicios encontrados</span>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="row" id="resultadosServicios">
                            <!-- Se cargará dinámicamente -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Mis Reservas Recientes -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Mis Reservas Recientes</h5>
                        <a href="#" class="btn btn-sm btn-outline-primary">Ver Todas</a>
                    </div>
                    <div class="card-body">
                        <div class="row" id="reservasRecientes">
                            <!-- Se cargará dinámicamente -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * Inicializa componentes específicos de la página
 */
function initializePageComponents(page) {
    switch (page) {
        case 'dashboard':
            initializeDashboardCharts();
            break;
        case 'reservas':
            initializeReservasTable();
            break;
        case 'servicios':
            initializeServiciosGrid();
            break;
        case 'pagos':
            initializePagosTable();
            break;
    }
}

/**
 * Inicializa los charts del dashboard
 */
function initializeDashboardCharts() {
    // Chart de Reservas Mensuales
    const reservasCtx = document.getElementById('reservasChart');
    if (reservasCtx) {
        currentCharts.reservas = new Chart(reservasCtx, {
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
    }
    
    // Chart de Servicios
    const serviciosCtx = document.getElementById('serviciosChart');
    if (serviciosCtx) {
        currentCharts.servicios = new Chart(serviciosCtx, {
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
}

/**
 * Carga datos específicos de la página
 */
async function loadPageData(page) {
    try {
        switch (page) {
            case 'dashboard':
            case 'dashboard':
                await loadDashboardData();
                break;
            case 'reservas':
                await loadReservasData();
                break;
            case 'servicios':
                await loadServiciosData();
                break;
            case 'pagos':
                await loadPagosData();
                break;
        }
    } catch (error) {
        console.error('Error cargando datos de página:', error);
        showNotification('error', 'Error al cargar los datos');
    }
}

/**
 * Carga datos del dashboard
 */
async function loadDashboardData() {
    const userRole = getCurrentUser().nombreRol;
    
    try {
        // Cargar estadísticas generales
        const [usuariosStats, reservasStats, serviciosStats] = await Promise.all([
            API.Usuarios.getEstadisticas(),
            API.Reservas.getEstadisticas(),
            API.Servicios.getEstadisticas()
        ]);
        
        // Actualizar estadísticas en el DOM
        updateDashboardStats(usuariosStats, reservasStats, serviciosStats);
        
        // Cargar datos específicos según rol
        if (userRole === 'ADMIN') {
            await loadAdminDashboardData();
        } else if (userRole === 'EMPLEADO') {
            await loadEmpleadoDashboardData();
        } else if (userRole === 'CLIENTE') {
            await loadClienteDashboardData();
        }
        
    } catch (error) {
        console.error('Error cargando datos del dashboard:', error);
    }
}

/**
 * Actualiza las estadísticas del dashboard
 */
function updateDashboardStats(usuariosStats, reservasStats, serviciosStats) {
    // Actualizar estadísticas de usuarios
    if (document.getElementById('totalUsuarios')) {
        document.getElementById('totalUsuarios').textContent = usuariosStats[0] || 0;
    }
    
    // Actualizar estadísticas de reservas
    if (document.getElementById('totalReservas')) {
        document.getElementById('totalReservas').textContent = reservasStats[1] || 0;
    }
    
    // Actualizar estadísticas de servicios
    if (document.getElementById('totalServicios')) {
        document.getElementById('totalServicios').textContent = serviciosStats[0] || 0;
    }
    
    // Actualizar ingresos mensuales
    if (document.getElementById('ingresosMensuales')) {
        const ingresos = reservasStats[6] || 0;
        document.getElementById('ingresosMensuales').textContent = 
            '$' + ingresos.toLocaleString();
    }
}

/**
 * Configura actualizaciones automáticas
 */
function setupAutoRefresh() {
    // Actualizar dashboard cada 5 minutos
    setInterval(() => {
        if (currentPage === 'dashboard') {
            loadPageData('dashboard');
        }
    }, 300000);
    
    // Actualizar notificaciones cada minuto
    setInterval(() => {
        checkNotifications();
    }, 60000);
}

/**
 * Verifica notificaciones nuevas
 */
async function checkNotifications() {
    try {
        // Implementar verificación de notificaciones
        const response = await API.Utils.get('/notificaciones');
        
        if (response && response.length > 0) {
            updateNotificationBadge(response.length);
            updateNotificationsList(response);
        }
    } catch (error) {
        console.error('Error verificando notificaciones:', error);
    }
}

/**
 * Actualiza el badge de notificaciones
 */
function updateNotificationBadge(count) {
    const badge = document.getElementById('notificationBadge');
    if (badge) {
        badge.textContent = count;
        badge.style.display = count > 0 ? 'block' : 'none';
    }
}

/**
 * Actualiza la lista de notificaciones
 */
function updateNotificationsList(notifications) {
    const list = document.getElementById('notificationsList');
    if (!list) return;
    
    list.innerHTML = '';
    
    notifications.forEach(notification => {
        const item = createNotificationItem(notification);
        list.appendChild(item);
    });
}

/**
 * Crea un elemento de notificación
 */
function createNotificationItem(notification) {
    const div = document.createElement('div');
    div.className = 'notification-item';
    div.innerHTML = `
        <div class="notification-icon ${notification.type}">
            <i class="fas fa-${getNotificationIcon(notification.type)}"></i>
        </div>
        <div class="notification-content">
            <div class="notification-title">${notification.title}</div>
            <div class="notification-time">${formatTime(notification.createdAt)}</div>
        </div>
    `;
    return div;
}

/**
 * Obtiene el ícono para el tipo de notificación
 */
function getNotificationIcon(type) {
    const icons = {
        'success': 'check-circle',
        'warning': 'exclamation-triangle',
        'error': 'times-circle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

/**
 * Formatea tiempo relativo
 */
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

// Inicializar dashboard cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('pageContent')) {
        initDashboard();
    }
});

// Exportar funciones para uso global
window.Dashboard = {
    loadPage,
    refreshData: () => loadPageData(currentPage),
    updateCharts: () => {
        if (currentPage === 'dashboard') {
            initializeDashboardCharts();
        }
    }
};

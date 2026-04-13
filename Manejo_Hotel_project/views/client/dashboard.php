<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * VISTA: CLIENT DASHBOARD
 * =============================================
 * 
 * Panel de control para el rol de cliente
 */

require_once __DIR__ . '/../../backend/controllers/AuthController.php';

// Verificar autenticación y rol
$auth = new AuthController();
$auth->requireAuth('cliente');

// Obtener información del usuario actual
$currentUser = $auth->getCurrentUser();
?>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Panel - Hermosa Cartagena</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="../../assets/css/style.css" rel="stylesheet">
    
    <style>
        .client-header {
            background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
            border-radius: 0 0 1rem 1rem;
        }
        
        .service-card {
            background: white;
            border-radius: 1rem;
            overflow: hidden;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            height: 100%;
            cursor: pointer;
        }
        
        .service-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
        }
        
        .service-image {
            height: 200px;
            background-size: cover;
            background-position: center;
            position: relative;
        }
        
        .service-image::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(to bottom, transparent, rgba(0, 0, 0, 0.5));
        }
        
        .service-badge {
            position: absolute;
            top: 1rem;
            right: 1rem;
            background: rgba(255, 255, 255, 0.9);
            color: #17a2b8;
            padding: 0.25rem 0.75rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        .service-content {
            padding: 1.5rem;
        }
        
        .service-title {
            font-size: 1.25rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: #333;
        }
        
        .service-description {
            color: #6c757d;
            margin-bottom: 1rem;
            font-size: 0.9rem;
            line-height: 1.5;
        }
        
        .service-details {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }
        
        .service-price {
            font-size: 1.5rem;
            font-weight: 700;
            color: #17a2b8;
        }
        
        .service-duration {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        .service-features {
            margin-bottom: 1rem;
        }
        
        .service-feature {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 0.25rem;
            font-size: 0.9rem;
            color: #6c757d;
        }
        
        .service-feature i {
            color: #28a745;
        }
        
        .reservation-card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 1rem;
            transition: all 0.3s ease;
            border-left: 4px solid transparent;
        }
        
        .reservation-card:hover {
            transform: translateX(4px);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
        }
        
        .reservation-card.pending {
            border-left-color: #ffc107;
        }
        
        .reservation-card.confirmed {
            border-left-color: #28a745;
        }
        
        .reservation-card.paid {
            border-left-color: #17a2b8;
        }
        
        .reservation-card.completed {
            border-left-color: #6c757d;
        }
        
        .reservation-header {
            display: flex;
            justify-content: space-between;
            align-items: start;
            margin-bottom: 1rem;
        }
        
        .reservation-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #333;
        }
        
        .reservation-status {
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
        
        .status-completed {
            background: rgba(108, 117, 125, 0.1);
            color: #6c757d;
        }
        
        .reservation-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .reservation-detail {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-size: 0.9rem;
            color: #6c757d;
        }
        
        .reservation-detail i {
            color: #17a2b8;
        }
        
        .search-filters {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .filter-group {
            margin-bottom: 1rem;
        }
        
        .filter-label {
            font-weight: 500;
            margin-bottom: 0.5rem;
            color: #333;
        }
        
        .price-range {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .price-input {
            flex: 1;
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
            background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        
        .welcome-text p {
            color: #6c757d;
            margin-bottom: 0;
        }
        
        .user-stats {
            text-align: right;
        }
        
        .stat-item {
            margin-bottom: 0.5rem;
        }
        
        .stat-value {
            font-size: 1.25rem;
            font-weight: 600;
            color: #17a2b8;
        }
        
        .stat-label {
            font-size: 0.9rem;
            color: #6c757d;
        }
        
        .quick-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }
        
        .quick-stat-card {
            background: white;
            border-radius: 1rem;
            padding: 1.5rem;
            text-align: center;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
        }
        
        .quick-stat-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 15px rgba(0, 0, 0, 0.15);
        }
        
        .quick-stat-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            margin: 0 auto 1rem;
            background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
            color: white;
        }
        
        .quick-stat-value {
            font-size: 2rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .quick-stat-label {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        .featured-services {
            background: linear-gradient(135deg, rgba(23, 162, 184, 0.1), rgba(19, 132, 150, 0.1));
            border-radius: 1rem;
            padding: 2rem;
            margin-bottom: 2rem;
        }
        
        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }
        
        .section-title {
            font-size: 1.75rem;
            font-weight: 700;
            color: #333;
        }
        
        .btn-explore {
            background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
            color: white;
            border: none;
            border-radius: 0.5rem;
            padding: 0.75rem 1.5rem;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .btn-explore:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 15px rgba(23, 162, 184, 0.3);
            color: white;
        }
        
        @media (max-width: 768px) {
            .welcome-content {
                flex-direction: column;
                text-align: center;
            }
            
            .user-stats {
                text-align: center;
            }
            
            .service-details {
                grid-template-columns: 1fr;
            }
            
            .reservation-details {
                grid-template-columns: 1fr;
            }
            
            .section-header {
                flex-direction: column;
                gap: 1rem;
                text-align: center;
            }
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <nav class="sidebar">
        <div class="sidebar-header" style="background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);">
            <i class="fas fa-user fa-2x mb-2"></i>
            <h4>Mi Panel</h4>
            <small>Hermosa Cartagena</small>
        </div>
        
        <div class="sidebar-menu">
            <div class="sidebar-item">
                <a href="#" class="sidebar-link active">
                    <i class="fas fa-home"></i>
                    <span>Inicio</span>
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
                    <span>Mis Reservas</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="payments.php" class="sidebar-link">
                    <i class="fas fa-credit-card"></i>
                    <span>Historial de Pagos</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="profile.php" class="sidebar-link">
                    <i class="fas fa-user-cog"></i>
                    <span>Mi Perfil</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="favorites.php" class="sidebar-link">
                    <i class="fas fa-heart"></i>
                    <span>Favoritos</span>
                </a>
            </div>
            
            <div class="sidebar-item">
                <a href="reviews.php" class="sidebar-link">
                    <i class="fas fa-star"></i>
                    <span>Mis Reseñas</span>
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
                    <h1>¡Bienvenido, <?php echo htmlspecialchars($currentUser['nombre_completo']); ?>!</h1>
                    <p>Descubre las maravillas de Cartagena con nosotros</p>
                </div>
                <div class="user-stats">
                    <div class="stat-item">
                        <div class="stat-value" id="totalReservations">0</div>
                        <div class="stat-label">Reservas Totales</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value" id="loyaltyPoints">0</div>
                        <div class="stat-label">Puntos de Lealtad</div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Quick Stats -->
        <div class="quick-stats">
            <div class="quick-stat-card">
                <div class="quick-stat-icon">
                    <i class="fas fa-calendar-check"></i>
                </div>
                <div class="quick-stat-value" id="activeReservations">0</div>
                <div class="quick-stat-label">Reservas Activas</div>
            </div>
            
            <div class="quick-stat-card">
                <div class="quick-stat-icon">
                    <i class="fas fa-money-bill-wave"></i>
                </div>
                <div class="quick-stat-value" id="totalSpent">$0</div>
                <div class="quick-stat-label">Total Gastado</div>
            </div>
            
            <div class="quick-stat-card">
                <div class="quick-stat-icon">
                    <i class="fas fa-star"></i>
                </div>
                <div class="quick-stat-value" id="averageRating">0.0</div>
                <div class="quick-stat-label">Mi Rating</div>
            </div>
            
            <div class="quick-stat-card">
                <div class="quick-stat-icon">
                    <i class="fas fa-gift"></i>
                </div>
                <div class="quick-stat-value" id="availableRewards">0</div>
                <div class="quick-stat-label">Recompensas</div>
            </div>
        </div>
        
        <!-- Featured Services -->
        <div class="featured-services">
            <div class="section-header">
                <h2 class="section-title">Servicios Destacados</h2>
                <button class="btn btn-explore" onclick="exploreServices()">
                    Explorar Todos <i class="fas fa-arrow-right ms-2"></i>
                </button>
            </div>
            
            <div class="row" id="featuredServices">
                <!-- Featured services will be loaded here -->
            </div>
        </div>
        
        <!-- Search and Filters -->
        <div class="search-filters">
            <h5 class="mb-3">Buscar Servicios</h5>
            <div class="row">
                <div class="col-md-4">
                    <div class="filter-group">
                        <label class="filter-label">Buscar</label>
                        <input type="text" class="form-control" id="searchInput" placeholder="¿Qué estás buscando?">
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="filter-group">
                        <label class="filter-label">Tipo de Servicio</label>
                        <select class="form-select" id="serviceType">
                            <option value="">Todos los tipos</option>
                            <option value="tour">Tours</option>
                            <option value="hospedaje">Hospedaje</option>
                            <option value="transporte">Transporte</option>
                            <option value="alimentacion">Alimentación</option>
                            <option value="actividad">Actividades</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="filter-group">
                        <label class="filter-label">Rango de Precio</label>
                        <div class="price-range">
                            <input type="number" class="form-control price-input" id="minPrice" placeholder="Min">
                            <span>-</span>
                            <input type="number" class="form-control price-input" id="maxPrice" placeholder="Max">
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="filter-group">
                        <label class="filter-label">&nbsp;</label>
                        <button class="btn btn-primary w-100" onclick="searchServices()">
                            <i class="fas fa-search me-2"></i>Buscar
                        </button>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Search Results -->
        <div class="row mb-4" id="searchResults">
            <!-- Search results will be loaded here -->
        </div>
        
        <!-- Recent Reservations -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header" style="background: linear-gradient(135deg, #17a2b8 0%, #138496 100%); color: white;">
                        <div class="d-flex justify-content-between align-items-center">
                            <h5 class="mb-0">
                                <i class="fas fa-history me-2"></i>
                                Mis Reservas Recientes
                            </h5>
                            <a href="reservations.php" class="btn btn-sm btn-light">
                                Ver Todas <i class="fas fa-arrow-right ms-1"></i>
                            </a>
                        </div>
                    </div>
                    <div class="card-body">
                        <div id="recentReservations">
                            <!-- Recent reservations will be loaded here -->
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
        // Load client dashboard data
        async function loadDashboardData() {
            try {
                const response = await fetch('../../backend/api/client_dashboard.php?action=dashboard');
                const data = await response.json();
                
                if (data.success) {
                    // Update stats
                    document.getElementById('totalReservations').textContent = data.stats.total_reservations || 0;
                    document.getElementById('loyaltyPoints').textContent = data.stats.loyalty_points || 0;
                    document.getElementById('activeReservations').textContent = data.stats.active_reservations || 0;
                    document.getElementById('totalSpent').textContent = '$' + (data.stats.total_spent || 0).toLocaleString();
                    document.getElementById('averageRating').textContent = (data.stats.average_rating || 0).toFixed(1);
                    document.getElementById('availableRewards').textContent = data.stats.available_rewards || 0;
                    
                    // Load featured services
                    loadFeaturedServices(data.featured_services);
                    
                    // Load recent reservations
                    loadRecentReservations(data.recent_reservations);
                    
                    // Load search results with all services
                    loadSearchResults(data.all_services);
                }
            } catch (error) {
                console.error('Error loading dashboard data:', error);
            }
        }
        
        // Load featured services
        function loadFeaturedServices(services) {
            const container = document.getElementById('featuredServices');
            container.innerHTML = '';
            
            if (!services || services.length === 0) {
                container.innerHTML = '<div class="col-12"><p class="text-muted text-center">No hay servicios destacados disponibles</p></div>';
                return;
            }
            
            services.forEach(service => {
                const serviceHtml = `
                    <div class="col-md-6 col-lg-4 mb-4">
                        <div class="service-card" onclick="viewService(${service.id_servicio})">
                            <div class="service-image" style="background-image: url('https://picsum.photos/seed/service${service.id_servicio}/400/200.jpg');">
                                <span class="service-badge">${getServiceTypeLabel(service.tipo_servicio)}</span>
                            </div>
                            <div class="service-content">
                                <h5 class="service-title">${service.nombre_servicio}</h5>
                                <p class="service-description">${service.descripcion}</p>
                                <div class="service-details">
                                    <div class="service-price">$${service.precio_base.toLocaleString()}</div>
                                    <div class="service-duration">
                                        <i class="fas fa-clock me-1"></i>${service.duracion_horas}h
                                    </div>
                                </div>
                                <div class="service-features">
                                    <div class="service-feature">
                                        <i class="fas fa-users"></i>
                                        <span>Capacidad: ${service.capacidad_maxima} personas</span>
                                    </div>
                                    <div class="service-feature">
                                        <i class="fas fa-map-marker-alt"></i>
                                        <span>${service.ubicacion}</span>
                                    </div>
                                </div>
                                <button class="btn btn-primary w-100">
                                    <i class="fas fa-calendar-plus me-2"></i>Reservar Ahora
                                </button>
                            </div>
                        </div>
                    </div>
                `;
                container.innerHTML += serviceHtml;
            });
        }
        
        // Load recent reservations
        function loadRecentReservations(reservations) {
            const container = document.getElementById('recentReservations');
            container.innerHTML = '';
            
            if (!reservations || reservations.length === 0) {
                container.innerHTML = '<p class="text-muted text-center">No tienes reservas recientes</p>';
                return;
            }
            
            reservations.forEach(reservation => {
                const reservationHtml = `
                    <div class="reservation-card ${reservation.estado_reserva}">
                        <div class="reservation-header">
                            <div>
                                <div class="reservation-title">${reservation.nombre_servicio}</div>
                                <small class="text-muted">Código: ${reservation.codigo_reserva}</small>
                            </div>
                            <span class="reservation-status status-${reservation.estado_reserva}">
                                ${getStatusLabel(reservation.estado_reserva)}
                            </span>
                        </div>
                        <div class="reservation-details">
                            <div class="reservation-detail">
                                <i class="fas fa-calendar"></i>
                                <span>${formatDate(reservation.fecha_inicio_servicio)}</span>
                            </div>
                            <div class="reservation-detail">
                                <i class="fas fa-users"></i>
                                <span>${reservation.cantidad_personas} personas</span>
                            </div>
                            <div class="reservation-detail">
                                <i class="fas fa-money-bill-wave"></i>
                                <span>$${reservation.precio_total.toLocaleString()}</span>
                            </div>
                        </div>
                        <div class="d-flex gap-2">
                            <button class="btn btn-sm btn-outline-primary" onclick="viewReservation(${reservation.id_reserva})">
                                <i class="fas fa-eye me-1"></i>Ver Detalles
                            </button>
                            ${reservation.estado_reserva === 'confirmada' ? `
                                <button class="btn btn-sm btn-success" onclick="makePayment(${reservation.id_reserva})">
                                    <i class="fas fa-credit-card me-1"></i>Pagar
                                </button>
                            ` : ''}
                        </div>
                    </div>
                `;
                container.innerHTML += reservationHtml;
            });
        }
        
        // Load search results
        function loadSearchResults(services) {
            const container = document.getElementById('searchResults');
            container.innerHTML = '';
            
            if (!services || services.length === 0) {
                container.innerHTML = '<div class="col-12"><p class="text-muted text-center">No se encontraron servicios</p></div>';
                return;
            }
            
            services.forEach(service => {
                const serviceHtml = `
                    <div class="col-md-6 col-lg-4 mb-4">
                        <div class="service-card" onclick="viewService(${service.id_servicio})">
                            <div class="service-image" style="background-image: url('https://picsum.photos/seed/service${service.id_servicio}/400/200.jpg');">
                                <span class="service-badge">${getServiceTypeLabel(service.tipo_servicio)}</span>
                            </div>
                            <div class="service-content">
                                <h5 class="service-title">${service.nombre_servicio}</h5>
                                <p class="service-description">${service.descripcion}</p>
                                <div class="service-details">
                                    <div class="service-price">$${service.precio_base.toLocaleString()}</div>
                                    <div class="service-duration">
                                        <i class="fas fa-clock me-1"></i>${service.duracion_horas}h
                                    </div>
                                </div>
                                <button class="btn btn-primary w-100">
                                    <i class="fas fa-calendar-plus me-2"></i>Reservar Ahora
                                </button>
                            </div>
                        </div>
                    </div>
                `;
                container.innerHTML += serviceHtml;
            });
        }
        
        // Search services
        async function searchServices() {
            const searchTerm = document.getElementById('searchInput').value;
            const serviceType = document.getElementById('serviceType').value;
            const minPrice = document.getElementById('minPrice').value;
            const maxPrice = document.getElementById('maxPrice').value;
            
            try {
                const params = new URLSearchParams({
                    action: 'search',
                    search: searchTerm,
                    type: serviceType,
                    min_price: minPrice,
                    max_price: maxPrice
                });
                
                const response = await fetch(`../../backend/api/services.php?${params}`);
                const data = await response.json();
                
                if (data.success) {
                    loadSearchResults(data.services);
                } else {
                    document.getElementById('searchResults').innerHTML = '<div class="col-12"><p class="text-muted text-center">Error en la búsqueda</p></div>';
                }
            } catch (error) {
                console.error('Error searching services:', error);
            }
        }
        
        // Helper functions
        function getServiceTypeLabel(type) {
            const labels = {
                'tour': 'Tour',
                'hospedaje': 'Hospedaje',
                'transporte': 'Transporte',
                'alimentacion': 'Alimentación',
                'actividad': 'Actividad'
            };
            return labels[type] || type;
        }
        
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
        
        function formatDate(dateString) {
            const date = new Date(dateString);
            return date.toLocaleDateString('es-CO', {
                day: 'numeric',
                month: 'short',
                year: 'numeric'
            });
        }
        
        // Action functions
        function exploreServices() {
            window.location.href = 'services.php';
        }
        
        function viewService(id) {
            window.location.href = `services.php?action=view&id=${id}`;
        }
        
        function viewReservation(id) {
            window.location.href = `reservations.php?action=view&id=${id}`;
        }
        
        function makePayment(id) {
            window.location.href = `payments.php?reservation_id=${id}`;
        }
        
        // Initialize dashboard
        document.addEventListener('DOMContentLoaded', function() {
            loadDashboardData();
            
            // Add search on Enter key
            document.getElementById('searchInput').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    searchServices();
                }
            });
            
            // Auto-search on filter change
            ['serviceType', 'minPrice', 'maxPrice'].forEach(id => {
                document.getElementById(id).addEventListener('change', searchServices);
            });
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

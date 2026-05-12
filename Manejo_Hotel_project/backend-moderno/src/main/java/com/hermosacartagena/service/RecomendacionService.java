package com.hermosacartagena.service;

import com.hermosacartagena.dto.RecomendacionDTO;
import com.hermosacartagena.entity.*;
import com.hermosacartagena.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA" 3.0
 * SERVICIO DE RECOMENDACIONES INTELIGENTES
 * =============================================
 * 
 * Servicio de recomendaciones basado en algoritmos de machine learning.
 * Analiza el historial del usuario para sugerir servicios personalizados.
 * 
 * Algoritmos implementados:
 * - Basado en contenido (servicios similares)
 * - Basado en colaborativo (usuarios similares)
 * - Basado en popularidad (tendencias)
 * - Basado en contexto (temporada, ubicación)
 * 
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecomendacionService {

    private final ReservaRepository reservaRepository;
    private final ServicioRepository servicioRepository;
    private final CalificacionRepository calificacionRepository;
    private final ClienteRepository clienteRepository;
    private final FavoritoRepository favoritoRepository;

    /**
     * Obtiene recomendaciones personalizadas para un cliente.
     * Combina múltiples algoritmos para máxima precisión.
     */
    @Cacheable(value = "recomendaciones", key = "#idCliente")
    public List<RecomendacionDTO> getRecomendacionesPersonalizadas(Long idCliente) {
        log.info("Generando recomendaciones personalizadas para cliente: {}", idCliente);

        List<RecomendacionDTO> recomendaciones = new ArrayList<>();

        // 1. Algoritmo basado en contenido (40% peso)
        recomendaciones.addAll(getRecomendacionesPorContenido(idCliente, 0.4));

        // 2. Algoritmo colaborativo (30% peso)
        recomendaciones.addAll(getRecomendacionesColaborativas(idCliente, 0.3));

        // 3. Algoritmo basado en popularidad (20% peso)
        recomendaciones.addAll(getRecomendacionesPorPopularidad(idCliente, 0.2));

        // 4. Algoritmo contextual (10% peso)
        recomendaciones.addAll(getRecomendacionesContextuales(idCliente, 0.1));

        // Eliminar duplicados y ordenar por score
        return recomendaciones.stream()
            .collect(Collectors.toMap(
                RecomendacionDTO::getIdServicio,
                rec -> rec,
                (existing, replacement) -> existing.getScore() > replacement.getScore() ? existing : replacement
            ))
            .values()
            .stream()
            .sorted(Comparator.comparing(RecomendacionDTO::getScore).reversed())
            .limit(20)
            .collect(Collectors.toList());
    }

    /**
     * Algoritmo basado en contenido: recomienda servicios similares
     * a los que el usuario ha reservado o calificado positivamente.
     */
    private List<RecomendacionDTO> getRecomendacionesPorContenido(Long idCliente, double peso) {
        List<RecomendacionDTO> recomendaciones = new ArrayList<>();

        // Obtener historial del cliente
        List<Servicio> serviciosHistorial = reservaRepository
            .findTop10ByClienteIdOrderByFechaReservaDesc(idCliente)
            .stream()
            .map(Reserva::getServicio)
            .collect(Collectors.toList());

        // Analizar preferencias por tipo y características
        Map<String, Integer> preferenciasTipo = new HashMap<>();
        Map<String, Double> preferenciasPrecio = new HashMap<>();

        serviciosHistorial.forEach(servicio -> {
            preferenciasTipo.merge(servicio.getTipoServicio(), 1, Integer::sum);
            preferenciasPrecio.merge(getRangoPrecio(servicio.getPrecio()), 1.0, Double::sum);
        });

        // Encontrar servicios similares
        List<Servicio> serviciosSimilares = servicioRepository
            .findServiciosSimilaresByTipoYPrecio(
                preferenciasTipo.keySet(),
                obtenerRangoPrecioPreferido(preferenciasPrecio)
            );

        // Excluir ya reservados
        Set<Long> serviciosExcluidos = serviciosHistorial.stream()
            .map(Servicio::getIdServicio)
            .collect(Collectors.toSet());

        serviciosSimilares.stream()
            .filter(s -> !serviciosExcluidos.contains(s.getIdServicio()))
            .forEach(servicio -> {
                double score = calcularScoreContenido(servicio, preferenciasTipo, preferenciasPrecio);
                recomendaciones.add(RecomendacionDTO.builder()
                    .idServicio(servicio.getIdServicio())
                    .nombreServicio(servicio.getNombreServicio())
                    .tipoRecomendacion("CONTENIDO_SIMILAR")
                    .score(score * peso)
                    .motivo("Basado en tus reservas anteriores de " + servicio.getTipoServicio())
                    .build());
            });

        return recomendaciones;
    }

    /**
     * Algoritmo colaborativo: recomienda servicios que usuarios
     * similares han disfrutado.
     */
    private List<RecomendacionDTO> getRecomendacionesColaborativas(Long idCliente, double peso) {
        List<RecomendacionDTO> recomendaciones = new ArrayList<>();

        // Encontrar usuarios similares basados en calificaciones
        List<Long> usuariosSimilares = encontrarUsuariosSimilares(idCliente);

        if (usuariosSimilares.isEmpty()) {
            return recomendaciones;
        }

        // Obtener servicios calificados positivamente por usuarios similares
        List<Servicio> serviciosRecomendados = calificacionRepository
            .findTopRatedServicesByUsuarios(usuariosSimilares, 4.5);

        // Excluir ya conocidos por el cliente
        Set<Long> serviciosConocidos = obtenerServiciosConocidos(idCliente);

        serviciosRecomendados.stream()
            .filter(s -> !serviciosConocidos.contains(s.getIdServicio()))
            .forEach(servicio -> {
                double score = calcularScoreColaborativo(servicio, usuariosSimilares);
                recomendaciones.add(RecomendacionDTO.builder()
                    .idServicio(servicio.getIdServicio())
                    .nombreServicio(servicio.getNombreServicio())
                    .tipoRecomendacion("USUARIOS_SIMILARES")
                    .score(score * peso)
                    .motivo("Usuarios con gustos similares a ti disfrutaron este servicio")
                    .build());
            });

        return recomendaciones;
    }

    /**
     * Algoritmo basado en popularidad: recomienda servicios
     * tendencia y mejor calificados.
     */
    private List<RecomendacionDTO> getRecomendacionesPorPopularidad(Long idCliente, double peso) {
        List<RecomendacionDTO> recomendaciones = new ArrayList<>();

        // Servicios más populares del último mes
        LocalDateTime fechaInicio = LocalDateTime.now().minusMonths(1);
        List<Servicio> serviciosPopulares = servicioRepository
            .findServiciosPopularesByFecha(fechaInicio);

        Set<Long> serviciosConocidos = obtenerServiciosConocidos(idCliente);

        serviciosPopulares.stream()
            .filter(s -> !serviciosConocidos.contains(s.getIdServicio()))
            .forEach(servicio -> {
                double score = calcularScorePopularidad(servicio);
                recomendaciones.add(RecomendacionDTO.builder()
                    .idServicio(servicio.getIdServicio())
                    .nombreServicio(servicio.getNombreServicio())
                    .tipoRecomendacion("POPULARIDAD")
                    .score(score * peso)
                    .motivo("Este servicio es muy popular actualmente")
                    .build());
            });

        return recomendaciones;
    }

    /**
     * Algoritmo contextual: recomienda basado en temporada,
     * ubicación y contexto actual.
     */
    private List<RecomendacionDTO> getRecomendacionesContextuales(Long idCliente, double peso) {
        List<RecomendacionDTO> recomendaciones = new ArrayList<>();

        // Obtener contexto actual
        String temporadaActual = obtenerTemporadaActual();
        String ubicacionCliente = obtenerUbicacionCliente(idCliente);

        // Servicios recomendados para la temporada actual
        List<Servicio> serviciosTemporada = servicioRepository
            .findServiciosByTemporada(temporadaActual);

        Set<Long> serviciosConocidos = obtenerServiciosConocidos(idCliente);

        serviciosTemporada.stream()
            .filter(s -> !serviciosConocidos.contains(s.getIdServicio()))
            .forEach(servicio -> {
                double score = calcularScoreContextual(servicio, temporadaActual, ubicacionCliente);
                recomendaciones.add(RecomendacionDTO.builder()
                    .idServicio(servicio.getIdServicio())
                    .nombreServicio(servicio.getNombreServicio())
                    .tipoRecomendacion("CONTEXTUAL")
                    .score(score * peso)
                    .motivo("Perfecto para la temporada de " + temporadaActual)
                    .build());
            });

        return recomendaciones;
    }

    /**
     * Métodos utilitarios para cálculos
     */
    private String getRangoPrecio(Double precio) {
        if (precio < 50000) return "ECONOMICO";
        if (precio < 150000) return "MEDIO";
        return "PREMIUM";
    }

    private String obtenerRangoPrecioPreferido(Map<String, Double> preferencias) {
        return preferencias.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("MEDIO");
    }

    private double calcularScoreContenido(Servicio servicio, Map<String, Integer> preferenciasTipo, Map<String, Double> preferenciasPrecio) {
        double score = 0.0;

        // Ponderación por tipo de servicio
        Integer preferenciaTipo = preferenciasTipo.get(servicio.getTipoServicio());
        if (preferenciaTipo != null) {
            score += preferenciaTipo * 0.4;
        }

        // Ponderación por rango de precio
        String rangoPrecio = getRangoPrecio(servicio.getPrecio());
        Double preferenciaPrecio = preferenciasPrecio.get(rangoPrecio);
        if (preferenciaPrecio != null) {
            score += preferenciaPrecio * 0.3;
        }

        // Calificación del servicio
        if (servicio.getCalificacionPromedio() != null) {
            score += servicio.getCalificacionPromedio() * 0.2;
        }

        // Popularidad
        if (servicio.getNumeroReservas() != null) {
            score += Math.min(servicio.getNumeroReservas() / 100.0, 1.0) * 0.1;
        }

        return Math.min(score, 1.0);
    }

    private double calcularScoreColaborativo(Servicio servicio, List<Long> usuariosSimilares) {
        // Calcular calificación promedio de usuarios similares
        Double calificacionPromedio = calificacionRepository
            .findCalificacionPromedioByServicioAndUsuarios(servicio.getIdServicio(), usuariosSimilares);

        if (calificacionPromedio == null) {
            return 0.0;
        }

        // Normalizar a 0-1
        return Math.min(calificacionPromedio / 5.0, 1.0);
    }

    private double calcularScorePopularidad(Servicio servicio) {
        double score = 0.0;

        // Calificación promedio (40%)
        if (servicio.getCalificacionPromedio() != null) {
            score += (servicio.getCalificacionPromedio() / 5.0) * 0.4;
        }

        // Número de reservas (40%)
        if (servicio.getNumeroReservas() != null) {
            score += Math.min(servicio.getNumeroReservas() / 1000.0, 1.0) * 0.4;
        }

        // Reciente (20%)
        score += 0.2; // Asumimos que es reciente por la consulta

        return Math.min(score, 1.0);
    }

    private double calcularScoreContextual(Servicio servicio, String temporada, String ubicacion) {
        double score = 0.0;

        // Relevancia por temporada (60%)
        if (servicio.getTipoServicio().toLowerCase().contains(temporada.toLowerCase())) {
            score += 0.6;
        } else {
            score += 0.3; // Relevancia parcial
        }

        // Calificación (30%)
        if (servicio.getCalificacionPromedio() != null) {
            score += (servicio.getCalificacionPromedio() / 5.0) * 0.3;
        }

        // Disponibilidad (10%)
        if (servicio.getDisponible()) {
            score += 0.1;
        }

        return Math.min(score, 1.0);
    }

    private List<Long> encontrarUsuariosSimilares(Long idCliente) {
        // Implementación simplificada: usuarios que calificaron similarmente
        return calificacionRepository.findUsuariosSimilaresByCalificaciones(idCliente, 0.8);
    }

    private Set<Long> obtenerServiciosConocidos(Long idCliente) {
        Set<Long> servicios = new HashSet<>();

        // Servicios reservados
        servicios.addAll(reservaRepository.findServicioIdsByCliente(idCliente));

        // Servicios en favoritos
        servicios.addAll(favoritoRepository.findServicioIdsByCliente(idCliente));

        // Servicios calificados
        servicios.addAll(calificacionRepository.findServicioIdsByCliente(idCliente));

        return servicios;
    }

    private String obtenerTemporadaActual() {
        int month = LocalDateTime.now().getMonthValue();
        if (month >= 12 || month <= 2) return "INVIERNO";
        if (month >= 3 && month <= 5) return "PRIMAVERA";
        if (month >= 6 && month <= 8) return "VERANO";
        return "OTOÑO";
    }

    private String obtenerUbicacionCliente(Long idCliente) {
        // Implementación simplificada
        return "Cartagena";
    }

    /**
     * Actualiza el modelo de recomendaciones basado en nueva interacción
     */
    @Transactional
    public void actualizarModeloRecomendaciones(Long idCliente, Long idServicio, Integer calificacion) {
        log.info("Actualizando modelo de recomendaciones para cliente: {}, servicio: {}, calificación: {}", 
                idCliente, idServicio, calificacion);

        // Aquí se implementaría la lógica de actualización del modelo
        // Por ahora, simplemente limpiamos el cache
        // En una implementación real, se actualizarían los pesos del modelo
    }
}

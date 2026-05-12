package com.hermosacartagena.service;

import com.hermosacartagena.dto.ReservaDTO;
import com.hermosacartagena.entity.Reserva;
import com.hermosacartagena.entity.Cliente;
import com.hermosacartagena.entity.Habitacion;
import com.hermosacartagena.repository.ReservaRepository;
import com.hermosacartagena.repository.ClienteRepository;
import com.hermosacartagena.repository.HabitacionRepository;
import com.hermosacartagena.exception.ResourceNotFoundException;
import com.hermosacartagena.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * PRUEBAS UNITARIAS - RESERVA SERVICE
 * =============================================
 * 
 * Tests unitarios para la clase ReservaService.
 * Verifica la lógica de negocio y manejo de errores.
 * 
 * @author Sistema de Gestión Turística
 * @version 2.0.0
 * @since 2024
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Cliente clienteMock;
    private Habitacion habitacionMock;
    private Reserva reservaMock;
    private ReservaDTO reservaDTOMock;

    @BeforeEach
    void setUp() {
        // Setup cliente mock
        clienteMock = new Cliente();
        clienteMock.setIdCliente(1001L);
        clienteMock.setNombreCompleto("Juan Pérez");
        clienteMock.setEmail("juan.perez@email.com");

        // Setup habitación mock
        habitacionMock = new Habitacion();
        habitacionMock.setIdHabitacion(205L);
        habitacionMock.setNumero("205");
        habitacionMock.setTipo("Suite Deluxe");
        habitacionMock.setCapacidad(2);
        habitacionMock.setPrecioPorNoche(280.00);
        habitacionMock.setEstado("disponible");

        // Setup reserva mock
        reservaMock = new Reserva();
        reservaMock.setIdReserva(5001L);
        reservaMock.setCliente(clienteMock);
        reservaMock.setHabitacion(habitacionMock);
        reservaMock.setFechaCheckIn(LocalDate.of(2024, 6, 15));
        reservaMock.setFechaCheckOut(LocalDate.of(2024, 6, 18));
        reservaMock.setEstado("pendiente");
        reservaMock.setFechaCreacion(LocalDateTime.now());

        // Setup DTO mock
        reservaDTOMock = new ReservaDTO();
        reservaDTOMock.setIdReserva(5001L);
        reservaDTOMock.setIdCliente(1001L);
        reservaDTOMock.setIdHabitacion(205L);
        reservaDTOMock.setFechaCheckIn(LocalDate.of(2024, 6, 15));
        reservaDTOMock.setFechaCheckOut(LocalDate.of(2024, 6, 18));
        reservaDTOMock.setEstado("pendiente");
    }

    @Test
    void testCrearReserva_Exito() {
        // Given
        when(clienteRepository.findById(1001L)).thenReturn(Optional.of(clienteMock));
        when(habitacionRepository.findById(205L)).thenReturn(Optional.of(habitacionMock));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        // When
        Reserva resultado = reservaService.crearReserva(reservaDTOMock);

        // Then
        assertNotNull(resultado);
        assertEquals(5001L, resultado.getIdReserva());
        assertEquals("pendiente", resultado.getEstado());
        assertEquals(clienteMock, resultado.getCliente());
        assertEquals(habitacionMock, resultado.getHabitacion());
        
        verify(clienteRepository).findById(1001L);
        verify(habitacionRepository).findById(205L);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void testCrearReserva_ClienteNoExiste_LanzaExcepcion() {
        // Given
        when(clienteRepository.findById(1001L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reservaService.crearReserva(reservaDTOMock);
        });

        verify(clienteRepository).findById(1001L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void testCrearReserva_HabitacionNoExiste_LanzaExcepcion() {
        // Given
        when(clienteRepository.findById(1001L)).thenReturn(Optional.of(clienteMock));
        when(habitacionRepository.findById(205L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reservaService.crearReserva(reservaDTOMock);
        });

        verify(clienteRepository).findById(1001L);
        verify(habitacionRepository).findById(205L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void testCrearReserva_HabitacionNoDisponible_LanzaExcepcion() {
        // Given
        habitacionMock.setEstado("ocupada");
        when(clienteRepository.findById(1001L)).thenReturn(Optional.of(clienteMock));
        when(habitacionRepository.findById(205L)).thenReturn(Optional.of(habitacionMock));

        // When & Then
        assertThrows(BusinessException.class, () -> {
            reservaService.crearReserva(reservaDTOMock);
        });

        verify(clienteRepository).findById(1001L);
        verify(habitacionRepository).findById(205L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void testCrearReserva_FechasInvalidas_LanzaExcepcion() {
        // Given
        reservaDTOMock.setFechaCheckIn(LocalDate.of(2024, 6, 18));
        reservaDTOMock.setFechaCheckOut(LocalDate.of(2024, 6, 15)); // Checkout antes que check-in
        when(clienteRepository.findById(1001L)).thenReturn(Optional.of(clienteMock));
        when(habitacionRepository.findById(205L)).thenReturn(Optional.of(habitacionMock));

        // When & Then
        assertThrows(BusinessException.class, () -> {
            reservaService.crearReserva(reservaDTOMock);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void testObtenerReservaPorId_Exito() {
        // Given
        when(reservaRepository.findById(5001L)).thenReturn(Optional.of(reservaMock));

        // When
        Optional<Reserva> resultado = reservaService.obtenerReservaPorId(5001L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(5001L, resultado.get().getIdReserva());
        assertEquals("pendiente", resultado.get().getEstado());
        
        verify(reservaRepository).findById(5001L);
    }

    @Test
    void testObtenerReservaPorId_NoExiste() {
        // Given
        when(reservaRepository.findById(5001L)).thenReturn(Optional.empty());

        // When
        Optional<Reserva> resultado = reservaService.obtenerReservaPorId(5001L);

        // Then
        assertFalse(resultado.isPresent());
        verify(reservaRepository).findById(5001L);
    }

    @Test
    void testActualizarReserva_Exito() {
        // Given
        ReservaDTO reservaActualizada = new ReservaDTO();
        reservaActualizada.setIdReserva(5001L);
        reservaActualizada.setEstado("confirmada");
        reservaActualizada.setObservaciones("Cliente VIP");

        when(reservaRepository.findById(5001L)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        // When
        Reserva resultado = reservaService.actualizarReserva(5001L, reservaActualizada);

        // Then
        assertNotNull(resultado);
        assertEquals(5001L, resultado.getIdReserva());
        assertEquals("confirmada", resultado.getEstado());
        
        verify(reservaRepository).findById(5001L);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void testActualizarReserva_NoExiste_LanzaExcepcion() {
        // Given
        when(reservaRepository.findById(5001L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reservaService.actualizarReserva(5001L, new ReservaDTO());
        });

        verify(reservaRepository).findById(5001L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void testCancelarReserva_Exito() {
        // Given
        when(reservaRepository.findById(5001L)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        // When
        Reserva resultado = reservaService.cancelarReserva(5001L, "Cancelación por cliente");

        // Then
        assertNotNull(resultado);
        assertEquals(5001L, resultado.getIdReserva());
        assertEquals("cancelada", resultado.getEstado());
        
        verify(reservaRepository).findById(5001L);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void testConfirmarReserva_Exito() {
        // Given
        when(reservaRepository.findById(5001L)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        // When
        Reserva resultado = reservaService.confirmarReserva(5001L);

        // Then
        assertNotNull(resultado);
        assertEquals(5001L, resultado.getIdReserva());
        assertEquals("confirmada", resultado.getEstado());
        
        verify(reservaRepository).findById(5001L);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void testObtenerReservasPorCliente_Exito() {
        // Given
        List<Reserva> reservas = Arrays.asList(reservaMock);
        Page<Reserva> pageReservas = new PageImpl<>(reservas);
        Pageable pageable = PageRequest.of(0, 10);

        when(reservaRepository.findByClienteIdCliente(1001L, pageable)).thenReturn(pageReservas);

        // When
        Page<Reserva> resultado = reservaService.obtenerReservasPorCliente(1001L, pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(5001L, resultado.getContent().get(0).getIdReserva());
        
        verify(reservaRepository).findByClienteIdCliente(1001L, pageable);
    }

    @Test
    void testObtenerReservasPorFechas_Exito() {
        // Given
        LocalDate fechaInicio = LocalDate.of(2024, 6, 1);
        LocalDate fechaFin = LocalDate.of(2024, 6, 30);
        List<Reserva> reservas = Arrays.asList(reservaMock);
        Page<Reserva> pageReservas = new PageImpl<>(reservas);
        Pageable pageable = PageRequest.of(0, 10);

        when(reservaRepository.findByFechaCheckInBetween(fechaInicio, fechaFin, pageable))
            .thenReturn(pageReservas);

        // When
        Page<Reserva> resultado = reservaService.obtenerReservasPorFechas(fechaInicio, fechaFin, pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        
        verify(reservaRepository).findByFechaCheckInBetween(fechaInicio, fechaFin, pageable);
    }

    @Test
    void testObtenerReservasPorEstado_Exito() {
        // Given
        String estado = "pendiente";
        List<Reserva> reservas = Arrays.asList(reservaMock);
        Page<Reserva> pageReservas = new PageImpl<>(reservas);
        Pageable pageable = PageRequest.of(0, 10);

        when(reservaRepository.findByEstado(estado, pageable)).thenReturn(pageReservas);

        // When
        Page<Reserva> resultado = reservaService.obtenerReservasPorEstado(estado, pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("pendiente", resultado.getContent().get(0).getEstado());
        
        verify(reservaRepository).findByEstado(estado, pageable);
    }

    @Test
    void testVerificarDisponibilidadHabitacion_Disponible() {
        // Given
        LocalDate fechaInicio = LocalDate.of(2024, 6, 15);
        LocalDate fechaFin = LocalDate.of(2024, 6, 18);
        when(reservaRepository.countByHabitacionIdHabitacionAndFechaCheckInBetween(
            205L, fechaInicio, fechaFin)).thenReturn(0);

        // When
        boolean disponible = reservaService.verificarDisponibilidadHabitacion(205L, fechaInicio, fechaFin);

        // Then
        assertTrue(disponible);
        verify(reservaRepository).countByHabitacionIdHabitacionAndFechaCheckInBetween(205L, fechaInicio, fechaFin);
    }

    @Test
    void testVerificarDisponibilidadHabitacion_NoDisponible() {
        // Given
        LocalDate fechaInicio = LocalDate.of(2024, 6, 15);
        LocalDate fechaFin = LocalDate.of(2024, 6, 18);
        when(reservaRepository.countByHabitacionIdHabitacionAndFechaCheckInBetween(
            205L, fechaInicio, fechaFin)).thenReturn(1);

        // When
        boolean disponible = reservaService.verificarDisponibilidadHabitacion(205L, fechaInicio, fechaFin);

        // Then
        assertFalse(disponible);
        verify(reservaRepository).countByHabitacionIdHabitacionAndFechaCheckInBetween(205L, fechaInicio, fechaFin);
    }

    @Test
    void testCalcularTotalReserva_Exito() {
        // Given
        int noches = 3; // 15, 16, 17 de junio
        double precioEsperado = habitacionMock.getPrecioPorNoche() * noches;

        // When
        double total = reservaService.calcularTotalReserva(reservaMock);

        // Then
        assertEquals(precioEsperado, total, 0.01);
    }

    @Test
    void testObtenerReservasActivas_Exito() {
        // Given
        List<Reserva> reservas = Arrays.asList(reservaMock);
        when(reservaRepository.findByEstadoIn(Arrays.asList("pendiente", "confirmada")))
            .thenReturn(reservas);

        // When
        List<Reserva> resultado = reservaService.obtenerReservasActivas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5001L, resultado.get(0).getIdReserva());
        
        verify(reservaRepository).findByEstadoIn(Arrays.asList("pendiente", "confirmada"));
    }
}

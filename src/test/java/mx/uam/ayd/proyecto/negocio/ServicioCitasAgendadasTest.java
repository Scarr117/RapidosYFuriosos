package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;

/**
 * Pruebas unitarias para ServicioCitasAgendadas (HU-01).
 */
@ExtendWith(MockitoExtension.class)
class ServicioCitasAgendadasTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private ServicioCitasAgendadas servicioCitasAgendadas;

    private Cita cita1;
    private Cita cita2;

    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setTelefono("5512345678");

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setMarca("Nissan");
        vehiculo.setModelo("Versa");
        vehiculo.setPlacas("ABC-123");

        cita1 = new Cita();
        cita1.setIdCita(1L);
        cita1.setFecha(LocalDate.now());
        cita1.setHora(LocalTime.of(12, 0));
        cita1.setCliente(cliente);
        cita1.setVehiculo(vehiculo);

        cita2 = new Cita();
        cita2.setIdCita(2L);
        cita2.setFecha(LocalDate.now());
        cita2.setHora(LocalTime.of(9, 0));
        cita2.setCliente(cliente);
        cita2.setVehiculo(vehiculo);
    }

    @Test
    @DisplayName("HU-01: Obtiene las citas de hoy ordenadas por hora")
    void obtenerCitasHoy_Exito() {
        when(citaRepository.findByFecha(LocalDate.now())).thenReturn(Arrays.asList(cita1, cita2));

        List<Cita> resultado = servicioCitasAgendadas.obtenerCitasHoy();

        assertEquals(2, resultado.size());
        assertEquals(LocalTime.of(9, 0), resultado.get(0).getHora());
        assertEquals(LocalTime.of(12, 0), resultado.get(1).getHora());
        verify(citaRepository, times(1)).findByFecha(LocalDate.now());
    }

    @Test
    @DisplayName("HU-01: Obtiene las citas de la semana actual")
    void obtenerCitasSemanales_Exito() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.with(DayOfWeek.MONDAY);
        LocalDate fin = hoy.with(DayOfWeek.SUNDAY);

        when(citaRepository.findByFechaBetween(inicio, fin)).thenReturn(Arrays.asList(cita1, cita2));

        List<Cita> resultado = servicioCitasAgendadas.obtenerCitasSemanales();

        assertEquals(2, resultado.size());
        verify(citaRepository, times(1)).findByFechaBetween(inicio, fin);
    }

    @Test
    @DisplayName("HU-01: Obtiene todas las citas registradas")
    void obtenerCitasTotales_Exito() {
        when(citaRepository.findAll()).thenReturn(Arrays.asList(cita1, cita2));

        List<Cita> resultado = servicioCitasAgendadas.obtenerCitasTotales();

        assertEquals(2, resultado.size());
        verify(citaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("HU-01: Busca citas por nombre de cliente, placas y teléfono")
    void obtenerCitasAtributo_Exito() {
        when(citaRepository.findByClienteNombreContainingIgnoreCase("Juan")).thenReturn(Arrays.asList(cita1));
        when(citaRepository.findByVehiculoPlacasContainingIgnoreCase("ABC-123")).thenReturn(Arrays.asList(cita1));
        when(citaRepository.findByClienteTelefonoContaining("5512345678")).thenReturn(Arrays.asList(cita1));

        List<Cita> porCliente = servicioCitasAgendadas.obtenerCitasAtributo("cliente", "Juan");
        List<Cita> porPlacas = servicioCitasAgendadas.obtenerCitasAtributo("placas", "ABC-123");
        List<Cita> porTelefono = servicioCitasAgendadas.obtenerCitasAtributo("telefono", "55-1234-5678");

        assertEquals(1, porCliente.size());
        assertEquals(1, porPlacas.size());
        assertEquals(1, porTelefono.size());

        verify(citaRepository).findByClienteNombreContainingIgnoreCase("Juan");
        verify(citaRepository).findByVehiculoPlacasContainingIgnoreCase("ABC-123");
        verify(citaRepository).findByClienteTelefonoContaining("5512345678");
    }

    @Test
    @DisplayName("HU-01: Si el atributo está vacío devuelve todas las citas")
    void obtenerCitasAtributo_Vacio() {
        when(citaRepository.findAll()).thenReturn(Arrays.asList(cita1, cita2));

        List<Cita> resultado = servicioCitasAgendadas.obtenerCitasAtributo("cliente", "   ");

        assertEquals(2, resultado.size());
        verify(citaRepository, times(1)).findAll();
    }
}

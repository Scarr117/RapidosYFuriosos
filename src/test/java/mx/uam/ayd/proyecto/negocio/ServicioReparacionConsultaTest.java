package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.ReparacionRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Reparacion;

/**
 * Pruebas de ServicioReparacion para la HU-14.
 */
@ExtendWith(MockitoExtension.class)
class ServicioReparacionConsultaTest {

    @Mock
    private ReparacionRepository reparacionRepository;

    @InjectMocks
    private ServicioReparacion servicioReparacion;

    @Test
    @DisplayName("HU-14: obtenerReparaciones regresa las reparaciones que entrega el repositorio")
    void testObtenerReparaciones() {
        List<Reparacion> lista = new ArrayList<>();
        lista.add(new Reparacion());
        lista.add(new Reparacion());

        when(reparacionRepository.findEnTallerConVehiculo(anyList())).thenReturn(lista);

        List<Reparacion> resultado = servicioReparacion.obtenerReparaciones();

        assertEquals(2, resultado.size());
        verify(reparacionRepository, times(1)).findEnTallerConVehiculo(anyList());
    }

    @Test
    @DisplayName("HU-14: sin reparaciones regresa una lista vacía")
    void testObtenerReparacionesVacio() {
        when(reparacionRepository.findEnTallerConVehiculo(anyList())).thenReturn(new ArrayList<>());

        List<Reparacion> resultado = servicioReparacion.obtenerReparaciones();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("HU-14: solo se consultan los estatus vigentes; las entregadas quedan fuera")
    @SuppressWarnings("unchecked")
    void testObtenerReparacionesExcluyeEntregadas() {
        when(reparacionRepository.findEnTallerConVehiculo(anyList())).thenReturn(new ArrayList<>());

        servicioReparacion.obtenerReparaciones();

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(reparacionRepository).findEnTallerConVehiculo(captor.capture());

        List<String> estatus = captor.getValue();
        assertTrue(estatus.contains("En reparación"));
        assertTrue(estatus.contains("En espera"));
        assertTrue(estatus.contains("Listo para entrega"));
        assertFalse(estatus.contains("Entregado"));
    }
}

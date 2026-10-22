package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mx.uam.ayd.proyecto.datos.ReparacionRepository;
import mx.uam.ayd.proyecto.datos.ReparacionRepository.VehiculosPendientesDTO;
import mx.uam.ayd.proyecto.negocio.modelo.Reparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;
import mx.uam.ayd.proyecto.negocio.modelo.Cotizacion;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;

class ServicioReparacionHU42Test {

    @Mock
    private ReparacionRepository reparacionRepository;

    @InjectMocks
    private ServicioReparacion servicioReparacion;

    private Reparacion reparacionPrueba;

    private Vehiculo vehiculoPrueba;

    private Cotizacion cotizacionPrueba;

    private Cita citaPrueba;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Preparamos nuestro objeto base para las pruebas de marcado
        reparacionPrueba = new Reparacion();

        vehiculoPrueba = new Vehiculo();
        vehiculoPrueba.setPlacas("XYZ-123");

        citaPrueba = new Cita();
        citaPrueba.setVehiculo(vehiculoPrueba);

        cotizacionPrueba = new Cotizacion();
        cotizacionPrueba.setCita(citaPrueba);
    }

// --- Pruebas para crearNuevaReparacionConFallas ---

    @Test
    @DisplayName("Debería lanzar excepción si las fallas son nulas o vacías")
    void testCrearReparacionFallasNulasOVacias() {
        assertThrows(IllegalArgumentException.class, 
            () -> servicioReparacion.crearNuevaReparacionConFallas(null, 6, cotizacionPrueba),
            "Debería rechazar fallas nulas");

        assertThrows(IllegalArgumentException.class, 
            () -> servicioReparacion.crearNuevaReparacionConFallas("   ", 6, cotizacionPrueba),
            "Debería rechazar fallas vacías o con puros espacios");
    }

    @Test
    @DisplayName("Debería lanzar excepción si la cotización es nula")
    void testCrearReparacionCotizacionNula() {
        assertThrows(IllegalArgumentException.class, 
            () -> servicioReparacion.crearNuevaReparacionConFallas("Falla motor", 6, null),
            "Debería rechazar una cotización nula");
    }
    
    @Test
    @DisplayName("Debería lanzar excepción si la garantía es negativa")
    void testCrearReparacionGarantiaNegativa() {
        assertThrows(IllegalArgumentException.class, 
            () -> servicioReparacion.crearNuevaReparacionConFallas("Falla motor", -1, cotizacionPrueba),
            "Debería rechazar meses de garantía negativos");
    }

    @Test
    @DisplayName("Debería lanzar excepción si la cotización ya tiene una reparación activa")
    void testCrearReparacionVehiculoConReparacionActiva() {
        // Given: Simulamos que el repositorio responde que SÍ existe una reparación activa para esta cotización
        when(reparacionRepository.existsByCotizacionAndEstatusServicioIn(eq(cotizacionPrueba), anyList()))
            .thenReturn(true);

        // When & Then: Verificamos que se bloquee el flujo
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, 
            () -> servicioReparacion.crearNuevaReparacionConFallas("Frenos", 6, cotizacionPrueba));
            
        // Verifica que el mensaje de error coincida con el que lanzas en tu servicio real
        assertTrue(excepcion.getMessage().contains("ya tiene")); 
        
        // Verificamos que nunca se intentó guardar nada
        verify(reparacionRepository, never()).save(any(Reparacion.class));
    }

    @Test
    @DisplayName("Debería crear y guardar la nueva reparación exitosamente")
    void testCrearReparacionExito() {
        // Given: Simulamos que NO hay reparaciones activas para esta cotización
        when(reparacionRepository.existsByCotizacionAndEstatusServicioIn(eq(cotizacionPrueba), anyList()))
            .thenReturn(false);

        // When: Ejecutamos el método pasándole las fallas, la garantía y la cotización válida
        servicioReparacion.crearNuevaReparacionConFallas("Falta aceite, Balatas", 6, cotizacionPrueba);
        
        // Then: Verificamos que el repositorio haya llamado al método save exactamente 1 vez
        verify(reparacionRepository, times(1)).save(any(Reparacion.class));
    }



    // --- Pruebas para obtenerVehiculosParaEntrega (HU-42) ---

    @Test
    @DisplayName("Debería retornar la lista de vehículos pendientes para entrega (HU-42)")
    void testObtenerVehiculosParaEntrega() {
        // Given: Como VehiculosPendientesDTO es una interfaz (proyección de Spring), creamos un mock de ella
        VehiculosPendientesDTO dtoMock = mock(VehiculosPendientesDTO.class);
        List<VehiculosPendientesDTO> listaEsperada = Arrays.asList(dtoMock);
        
        when(reparacionRepository.findVehiculosActivos()).thenReturn(listaEsperada);

        // When: Llamamos al servicio
        List<VehiculosPendientesDTO> resultado = servicioReparacion.obtenerVehiculosParaEntrega();

        // Then: Verificamos que devuelva la lista correctamente
        assertFalse(resultado.isEmpty(), "La lista devuelta no debería estar vacía");
        assertEquals(1, resultado.size(), "Debería retornar exactamente un elemento");
        verify(reparacionRepository, times(1)).findVehiculosActivos();
    }

    // --- Pruebas para marcarEntregado (HU-42) ---

    @Test
    @DisplayName("Debería marcar como entregado y guardar si el estatus es 'Listo para entrega' (HU-42)")
    void testMarcarEntregadoExito() {
        // Given: Simulamos una reparación que YA pasó el control de calidad
        reparacionPrueba.setEstatusServicio("Listo para entrega");
        when(reparacionRepository.findById(1)).thenReturn(Optional.of(reparacionPrueba));

        // When: Intentamos marcarla como entregada
        boolean resultado = servicioReparacion.marcarEntregado(1);

        // Then: Debe retornar true, cambiar el estatus a "Entregado" y guardar en BD
        assertTrue(resultado, "Debería retornar true al marcar como entregado exitosamente");
        assertEquals("Entregado", reparacionPrueba.getEstatusServicio(), "El estatus interno debe cambiar a 'Entregado'");
        verify(reparacionRepository, times(1)).save(reparacionPrueba);
    }

    @Test
    @DisplayName("No debería marcar como entregado si el estatus no es 'Listo para entrega' (HU-42)")
    void testMarcarEntregadoFalloEstatusIncorrecto() {
        // Given: Simulamos una reparación que sigue en revisión
        reparacionPrueba.setEstatusServicio("En espera"); 
        when(reparacionRepository.findById(2)).thenReturn(Optional.of(reparacionPrueba));

        // When: Intentamos marcarla como entregada
        boolean resultado = servicioReparacion.marcarEntregado(2);

        // Then: Debe dar false, no alterar el estado, y NO llamar a save()
        assertFalse(resultado, "Debería retornar false si el estatus actual no permite la entrega");
        assertEquals("En espera", reparacionPrueba.getEstatusServicio(), "El estatus original no debe ser modificado");
        verify(reparacionRepository, never()).save(any(Reparacion.class));
    }

    @Test
    @DisplayName("No debería hacer nada si el ID de reparación no existe (HU-42)")
    void testMarcarEntregadoFalloIdInexistente() {
        // Given: Simulamos que el repositorio no encuentra la reparación
        when(reparacionRepository.findById(99)).thenReturn(Optional.empty());

        // When: Intentamos procesar un ID fantasma
        boolean resultado = servicioReparacion.marcarEntregado(99);

        // Then: Debe dar false y no guardar nada
        assertFalse(resultado, "Debería retornar false si el ID de la reparación no existe en la base de datos");
        verify(reparacionRepository, never()).save(any(Reparacion.class));
    }
}
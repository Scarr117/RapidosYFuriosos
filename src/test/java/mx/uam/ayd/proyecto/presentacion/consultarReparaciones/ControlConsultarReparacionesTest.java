package mx.uam.ayd.proyecto.presentacion.consultarReparaciones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.negocio.ServicioReparacion;
import mx.uam.ayd.proyecto.negocio.modelo.EstadoReparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Reparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;

/**
 * Pruebas del controlador de la HU-14 (consultar reparaciones)
 */
@ExtendWith(MockitoExtension.class)
class ControlConsultarReparacionesTest {

    @Mock
    private ServicioReparacion servicioReparacion;

    @Mock
    private VentanaConsultarReparaciones ventana;

    @InjectMocks
    private ControlConsultarReparaciones control;

    @Captor
    private ArgumentCaptor<Map<EstadoReparacion, List<Reparacion>>> captorListas;

    @Captor
    private ArgumentCaptor<String> captorTexto;

    private Reparacion creaReparacion(int id, String estatus, String placas) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlacas(placas);
        vehiculo.setMarca("Nissan");
        vehiculo.setModelo("Versa");

        Reparacion reparacion = new Reparacion();
        reparacion.setIdReparacion(id);
        reparacion.setEstatusServicio(estatus);
        reparacion.setVehiculo(vehiculo);
        reparacion.setFechaInicio(LocalDateTime.of(2026, 10, 20, 9, 30));
        return reparacion;
    }

    // Escenario 1: Visualización de reparaciones

    @Test
    @DisplayName("Escenario 1: se muestran las reparaciones en curso, en espera y listas; las entregadas no")
    void testMuestraSoloReparacionesDelTaller() {
        Reparacion enCurso = creaReparacion(1, "En reparación", "AAA-111");
        Reparacion enEspera = creaReparacion(2, "En espera", "BBB-222");
        Reparacion listo = creaReparacion(3, "Listo para entrega", "CCC-333");
        Reparacion entregado = creaReparacion(4, "Entregado", "DDD-444");

        when(servicioReparacion.obtenerReparaciones()).thenReturn(List.of(enCurso, enEspera, listo, entregado));

        control.solicitarReparaciones();

        verify(ventana).muestraReparaciones(captorListas.capture());
        verify(ventana, never()).muestraTallerLibre(any());

        Map<EstadoReparacion, List<Reparacion>> mostradas = captorListas.getValue();
        int total = mostradas.values().stream().mapToInt(List::size).sum();
        assertEquals(3, total);
        assertFalse(mostradas.values().stream().anyMatch(lista -> lista.contains(entregado)));
    }

    // Escenario 2: Identificación del vehículo

    @Test
    @DisplayName("Escenario 2: cada reparación muestra marca, modelo y placas del vehículo")
    void testIdentificaVehiculo() {
        Reparacion reparacion = creaReparacion(1, "En espera", "ABC-123");

        String descripcion = control.descripcionVehiculo(reparacion);
        String detalle = control.construyeDetalle(reparacion);

        assertTrue(descripcion.contains("ABC-123"));
        assertTrue(descripcion.contains("Nissan"));
        assertTrue(descripcion.contains("Versa"));
        assertTrue(detalle.contains("ABC-123"));
    }

    // Escenario 3: Visualización del estado

    @Test
    @DisplayName("Escenario 3: cada reparación queda en el grupo de su estado")
    void testClasificaPorEstado() {
        Reparacion reparando = creaReparacion(1, "En reparación", "AAA-111");
        Reparacion revisando = creaReparacion(2, "En revisión", "BBB-222");
        Reparacion espera = creaReparacion(3, "En espera", "CCC-333");
        Reparacion listo = creaReparacion(4, "Listo para entrega", "DDD-444");

        Map<EstadoReparacion, List<Reparacion>> clasificadas = control
                .clasificaPorEstado(List.of(reparando, revisando, espera, listo));

        assertEquals(List.of(reparando, revisando), clasificadas.get(EstadoReparacion.EN_CURSO));
        assertEquals(List.of(espera), clasificadas.get(EstadoReparacion.EN_ESPERA));
        assertEquals(List.of(listo), clasificadas.get(EstadoReparacion.LISTO));
    }

    @Test
    @DisplayName("Escenario 3: el estado se muestra con su etiqueta en la información de la reparación")
    void testDetalleMuestraEstado() {
        assertTrue(control.construyeDetalle(creaReparacion(1, "Listo para entrega", "ABC-123"))
                .contains("Estado: Listo para entrega"));
        assertTrue(control.construyeDetalle(creaReparacion(2, "En reparación", "ABC-123"))
                .contains("Estado: En curso"));
        assertTrue(control.construyeDetalle(creaReparacion(3, "En espera", "ABC-123"))
                .contains("Estado: En espera"));
    }

    @Test
    @DisplayName("Escenario 3: el mapa siempre trae los tres estados, aunque alguno no tenga reparaciones")
    void testClasificaSiempreTraeLosTresEstados() {
        Map<EstadoReparacion, List<Reparacion>> clasificadas = control
                .clasificaPorEstado(List.of(creaReparacion(1, "En espera", "ABC-123")));

        assertEquals(3, clasificadas.size());
        assertTrue(clasificadas.get(EstadoReparacion.EN_CURSO).isEmpty());
        assertTrue(clasificadas.get(EstadoReparacion.LISTO).isEmpty());
    }

    // Escenario 4: Consulta de información

    @Test
    @DisplayName("Escenario 4: al seleccionar una reparación se muestra su información registrada")
    void testSeleccionaReparacion() {
        Reparacion reparacion = creaReparacion(7, "En espera", "ABC-123");
        reparacion.setObservacionesTecnicas("Cambio de balatas");
        reparacion.setGarantiaMeses(3);
        reparacion.setCondicionesGarantia("Solo piezas instaladas");

        control.seleccionaReparacion(reparacion);

        verify(ventana).muestraDetalle(captorTexto.capture());
        String detalle = captorTexto.getValue();
        assertTrue(detalle.contains("Reparación #7"));
        assertTrue(detalle.contains("Cambio de balatas"));
        assertTrue(detalle.contains("3 meses"));
        assertTrue(detalle.contains("Solo piezas instaladas"));
        assertTrue(detalle.contains("20/10/2026 09:30"));
    }

    @Test
    @DisplayName("Escenario 4: si no hay observaciones ni fecha de fin se indica claramente")
    void testDetalleConDatosFaltantes() {
        Reparacion reparacion = creaReparacion(1, "En reparación", "ABC-123");
        reparacion.setObservacionesTecnicas(null);
        reparacion.setFechaFin(null);

        String detalle = control.construyeDetalle(reparacion);

        assertTrue(detalle.contains("Sin observaciones registradas"));
        assertTrue(detalle.contains("Aún sin fecha de término"));
    }

    @Test
    @DisplayName("Escenario 4: una selección nula no hace nada")
    void testSeleccionNula() {
        control.seleccionaReparacion(null);

        verifyNoInteractions(ventana);
    }

    // Escenario 5: Lista vacía

    @Test
    @DisplayName("Escenario 5: sin reparaciones se muestra el mensaje de taller libre")
    void testTallerLibre() {
        when(servicioReparacion.obtenerReparaciones()).thenReturn(List.of());

        control.solicitarReparaciones();

        verify(ventana).muestraTallerLibre(ControlConsultarReparaciones.MENSAJE_TALLER_LIBRE);
        verify(ventana, never()).muestraReparaciones(any());
    }

    @Test
    @DisplayName("Escenario 5: si solo hay reparaciones ya entregadas también se muestra taller libre")
    void testTallerLibreSoloEntregadas() {
        when(servicioReparacion.obtenerReparaciones()).thenReturn(List.of(creaReparacion(1, "Entregado", "ABC-123")));

        control.solicitarReparaciones();

        verify(ventana).muestraTallerLibre(ControlConsultarReparaciones.MENSAJE_TALLER_LIBRE);
        verify(ventana, never()).muestraReparaciones(any());
    }

    // Escenario 6: Información actualizada

    @Test
    @DisplayName("Escenario 6: al consultar de nuevo se muestran los estados registrados en ese momento")
    void testInformacionActualizada() {
        Reparacion antes = creaReparacion(1, "En reparación", "ABC-123");
        Reparacion despues = creaReparacion(1, "Listo para entrega", "ABC-123");

        when(servicioReparacion.obtenerReparaciones()).thenReturn(List.of(antes)).thenReturn(List.of(despues));

        control.solicitarReparaciones();
        control.solicitarReparaciones();

        verify(servicioReparacion, times(2)).obtenerReparaciones();
        verify(ventana, times(2)).muestraReparaciones(captorListas.capture());

        List<Map<EstadoReparacion, List<Reparacion>>> consultas = captorListas.getAllValues();

        Map<EstadoReparacion, List<Reparacion>> primera = consultas.get(0);
        assertEquals(1, primera.get(EstadoReparacion.EN_CURSO).size());
        assertTrue(primera.get(EstadoReparacion.LISTO).isEmpty());

        Map<EstadoReparacion, List<Reparacion>> segunda = consultas.get(1);
        assertTrue(segunda.get(EstadoReparacion.EN_CURSO).isEmpty());
        assertEquals(1, segunda.get(EstadoReparacion.LISTO).size());
    }
}

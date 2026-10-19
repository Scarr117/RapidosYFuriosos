package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mx.uam.ayd.proyecto.datos.CotizacionRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Cotizacion;
import mx.uam.ayd.proyecto.negocio.modelo.Refaccion;

class ServicioCotizacionTest {

    @Mock
    private CotizacionRepository cotizacionRepository;

    @InjectMocks
    private ServicioCotizacion servicioCotizacion;

    private Cita citaPrueba;
    private Refaccion refaccionPrueba;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        citaPrueba = new Cita();
        
        refaccionPrueba = new Refaccion();
        refaccionPrueba.setPrecio(100.0f); 
    }

    // --- Pruebas para crearCotizacionBorrador ---

    @Test
    @DisplayName("Debería retornar false si se intenta crear un borrador con cita nula")
    void testCrearCotizacionBorradorCitaNula() {
        boolean resultado = servicioCotizacion.crearCotizacionBorrador(null);
        assertFalse(resultado, "No debe permitir crear un borrador sin una cita válida");
    }

    @Test
    @DisplayName("Debería crear el borrador en memoria exitosamente")
    void testCrearCotizacionBorradorExito() {
        boolean resultado = servicioCotizacion.crearCotizacionBorrador(citaPrueba);
        assertTrue(resultado, "Debería retornar true al crear el borrador exitosamente");
    }

    // --- Pruebas para agregarRefaccionACotizacionBorrador ---

    @Test
    @DisplayName("No debería agregar refacción si no hay un borrador activo")
    void testAgregarRefaccionSinBorrador() {
        boolean resultado = servicioCotizacion.agregarRefaccionACotizacionBorrador(refaccionPrueba, 2);
        assertFalse(resultado, "Debe retornar false porque this.cotizacion es nulo");
    }

    @Test
    @DisplayName("No debería agregar refacción con datos inválidos (Nulo o cantidad 0)")
    void testAgregarRefaccionDatosInvalidos() {
        servicioCotizacion.crearCotizacionBorrador(citaPrueba);
        
        assertFalse(servicioCotizacion.agregarRefaccionACotizacionBorrador(null, 2), "No debe aceptar refacción nula");
        assertFalse(servicioCotizacion.agregarRefaccionACotizacionBorrador(refaccionPrueba, 0), "No debe aceptar cantidades menores o iguales a 0");
    }

    @Test
    @DisplayName("Debería agregar la refacción al concepto del borrador exitosamente")
    void testAgregarRefaccionExito() {
        servicioCotizacion.crearCotizacionBorrador(citaPrueba);
        
        boolean resultado = servicioCotizacion.agregarRefaccionACotizacionBorrador(refaccionPrueba, 2);
        assertTrue(resultado, "Debería agregar el concepto y retornar true");
    }

    // --- Pruebas para capturarDatosServicio (ACTUALIZADAS) ---

    @Test
    @DisplayName("No debería capturar datos si no hay borrador activo")
    void testCapturarDatosSinBorrador() {
        // Ahora solo pasamos el float
        boolean resultado = servicioCotizacion.capturarDatosServicio(500.0f);
        assertFalse(resultado, "Debe retornar false porque this.cotizacion es nulo");
    }

    @Test
    @DisplayName("Debería capturar datos exitosamente y manejar costos negativos")
    void testCapturarDatosExitoYValidacion() {
        servicioCotizacion.crearCotizacionBorrador(citaPrueba);
        
        // Ahora solo pasamos el float (enviamos negativo para probar la validación del ELSE)
        boolean resultado = servicioCotizacion.capturarDatosServicio(-150.0f);
        assertTrue(resultado, "Debería retornar true tras guardar los datos");
    }

    // --- Pruebas para finalizarCotizacion (ACTUALIZADAS) ---

    @Test
    @DisplayName("No debería finalizar la cotización si no hay un borrador activo")
    void testFinalizarCotizacionSinBorrador() {
        boolean resultado = servicioCotizacion.finalizarCotizacion("Diagnóstico de prueba para el test");
        assertFalse(resultado, "Debe retornar false si intenta finalizar sin haber creado el borrador");
    }

    @Test
    @DisplayName("Debería calcular totales correctamente, guardar y limpiar el borrador")
    void testFinalizarCotizacionExitoCalculosYGuardado() {
        servicioCotizacion.crearCotizacionBorrador(citaPrueba);
        servicioCotizacion.agregarRefaccionACotizacionBorrador(refaccionPrueba, 2); // 2 piezas de $100 = $200
        
        // Capturamos el servicio solo con el flotante
        servicioCotizacion.capturarDatosServicio(500.0f); // Mano de obra = $500
        
        boolean resultado = servicioCotizacion.finalizarCotizacion("Diagnóstico de prueba para el test");
        assertTrue(resultado, "Debe retornar true al finalizar exitosamente");
        
        ArgumentCaptor<Cotizacion> captor = ArgumentCaptor.forClass(Cotizacion.class);
        verify(cotizacionRepository, times(1)).save(captor.capture());
        
        Cotizacion cotizacionGuardada = captor.getValue();
        
        // Verificamos matemáticas: Subtotal (200 + 500 = 700). IVA (700 * 0.16 = 112). Total = 812.0
        assertEquals(812.0f, cotizacionGuardada.getCostoTotal(), 0.01, "El cálculo del IVA y total no es correcto");
        
        // Comprobamos que el borrador es null intentando meter un costo
        assertFalse(servicioCotizacion.capturarDatosServicio(100f), "El borrador debió ser destruido (null)");
    }
}
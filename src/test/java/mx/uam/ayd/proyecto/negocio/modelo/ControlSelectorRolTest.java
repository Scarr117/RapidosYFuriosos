package mx.uam.ayd.proyecto.presentacion.inicio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipal;
import mx.uam.ayd.proyecto.presentacion.cliente.principal.ControlPrincipalCliente;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ControlSelectorRolTest {

    // Simulamos Mock las vistas y controladores de los que depende el selector
    @Mock
    private VistaSelectorRol vista;

    @Mock
    private ControlPrincipal controlPrincipalMecanico;

    @Mock
    private ControlPrincipalCliente controlPrincipalCliente;

    // Inyectamos los mocks en nuestro controlador real
    @InjectMocks
    private ControlSelectorRol controlSelectorRol;

    @Test
    void pruebaInicia_MuestraLaVista() {
        // Ejecución
        controlSelectorRol.inicia();

        // Verificación Se debió llamar al método muestra() pasándose a sí mismo como parámetro
        verify(vista, times(1)).muestra(controlSelectorRol);
    }

    @Test
    void pruebaIngresarComoMecanico_IniciaMecanicoYCierraSelector() {
        // Ejecución
        controlSelectorRol.ingresarComoMecanico();

        // Verificación Se arranca la vista del mecánico y se cierra el selector
        verify(controlPrincipalMecanico, times(1)).inicia();
        verify(vista, times(1)).cierra();

        // Aseguramos que NO se llamó por accidente a la vista del cliente
        verify(controlPrincipalCliente, times(0)).inicia();
    }

    @Test
    void pruebaIngresarComoCliente_IniciaClienteYCierraSelector() {
        // Ejecución
        controlSelectorRol.ingresarComoCliente();

        // Verificación: Se arranca la vista del cliente y se cierra el selector
        verify(controlPrincipalCliente, times(1)).inicia();
        verify(vista, times(1)).cierra();

        // Aseguramos que NO se llamó por accidente a la vista del mecánico
        verify(controlPrincipalMecanico, times(0)).inicia();
    }
}
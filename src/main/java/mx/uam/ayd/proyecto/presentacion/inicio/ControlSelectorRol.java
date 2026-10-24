package mx.uam.ayd.proyecto.presentacion.inicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.presentacion.principal.ControlPrincipal;
import mx.uam.ayd.proyecto.presentacion.cliente.principal.ControlPrincipalCliente;

@Component
public class ControlSelectorRol {

    @Autowired
    private VistaSelectorRol vista;

    @Autowired
    private ControlPrincipal controlPrincipalMecanico;

    @Autowired
    private ControlPrincipalCliente controlPrincipalCliente;

    public void inicia() {
        vista.muestra(this);
    }

    public void ingresarComoMecanico() {
        controlPrincipalMecanico.inicia();
        vista.cierra();
    }

    public void ingresarComoCliente() {
        controlPrincipalCliente.inicia();
        vista.cierra();
    }
}
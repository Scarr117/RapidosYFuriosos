package mx.uam.ayd.proyecto.presentacion.cliente.principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.inicio.ControlSelectorRol;

@Component
public class ControlPrincipalCliente {

    @Autowired
    private VistaPrincipalCliente vista;

    @Lazy
    @Autowired
    private ControlSelectorRol controlSelectorRol;

    public void inicia() {
        vista.muestra(this);
    }

    /**
     * Este método será llamado por la vista cuando el usuario presione la X roja.
     */
    public void regresarASelector() {
        controlSelectorRol.inicia();
    }
}
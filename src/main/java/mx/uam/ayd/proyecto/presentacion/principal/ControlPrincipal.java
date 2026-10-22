package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.listarInventario.ControlInventario;
import mx.uam.ayd.proyecto.presentacion.registrarPieza.ControlRegistrarPieza;
import mx.uam.ayd.proyecto.presentacion.registrarReparacion.ControlReparacion;
import mx.uam.ayd.proyecto.presentacion.consultarDistribuidores.ControlConsultarDistribuidores;
import mx.uam.ayd.proyecto.presentacion.consultarEntregas.ControlVehiculosEntrega;
import mx.uam.ayd.proyecto.presentacion.registrarServicio.ControlRegistrarServicio;
import mx.uam.ayd.proyecto.presentacion.pedidos.ControladorPedidos;
import mx.uam.ayd.proyecto.presentacion.registrarCotizacion.ControlCotizacion;
import mx.uam.ayd.proyecto.presentacion.registrarCita.ControlRegistrarCita;
// INICIO Modificación realizada por Erik para la HU-34 (Notificación de Atraso)
import mx.uam.ayd.proyecto.presentacion.informeAtrasos.ControlInformeAtrasos;
// FIN Modificación Erik HU-34
import mx.uam.ayd.proyecto.presentacion.registrarReparacion.ControlReparacion;

/**
 * Esta clase lleva el flujo de control de la ventana principal
 * 
 * @author humbertocervantes
 *
 */
@Component
public class ControlPrincipal {

    private final ControlRegistrarPieza controlRegistrarPieza;
    private final ControlConsultarDistribuidores controlConsultarDistribuidores;
    private final ControlRegistrarServicio controlRegistrarServicio;
    private final VentanaPrincipal ventana;
    private final ControladorPedidos controladorPedidos;
    private final ControlInventario controlInventario;
    private final ControlVehiculosEntrega controlVehiculoEntrega;
    private final ControlCotizacion controlCotizacion;
    private final ControlRegistrarCita controlRegistrarCita;
    // INICIO Modificación realizada por Erik para la HU-34
    private final ControlInformeAtrasos controlInformeAtrasos;
    // FIN Modificación Erik HU-34
    //HU-06 Reparacion
    private final ControlReparacion controlReparacion; 


    @Autowired
    public ControlPrincipal(
            ControlRegistrarPieza controlRegistrarPieza,
            ControlConsultarDistribuidores controlConsultarDistribuidores,
            ControlRegistrarServicio controlRegistrarServicio,
            ControladorPedidos controladorPedidos,
            ControlInventario controlInventario,
            ControlVehiculosEntrega controlVehiculosEntrega,
            ControlCotizacion controlCotizacion,
            ControlRegistrarCita controlRegistrarCita,
            // INICIO Modificación realizada por Erik para la HU-34
            ControlInformeAtrasos controlInformeAtrasos,
            // FIN Modificación Erik HU-34
            ControlReparacion controlReparacion,
            VentanaPrincipal ventana) {
        this.controlRegistrarPieza = controlRegistrarPieza;
        this.controlConsultarDistribuidores = controlConsultarDistribuidores;
        this.controlRegistrarServicio = controlRegistrarServicio;
        this.ventana = ventana;
        this.controladorPedidos = controladorPedidos;
        this.controlInventario = controlInventario;
        this.controlVehiculoEntrega = controlVehiculosEntrega;
        this.controlCotizacion = controlCotizacion;
        this.controlRegistrarCita = controlRegistrarCita;
        // INICIO Modificación realizada por Erik para la HU-34
        this.controlInformeAtrasos = controlInformeAtrasos;
        // FIN Modificación Erik HU-34
        this.controlReparacion = controlReparacion;
    }
    
    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control principal y la ventana principal
     */
    @PostConstruct
    public void init() {
        ventana.setControlPrincipal(this);
    }
    
    /**
     * Inicia el flujo de control de la ventana principal
     * 
     */
    public void inicia() {
        ventana.muestra();
    }
    
    /**
     * Método que arranca la historia de usuario "registrar pieza" (HU-31)
     *
     */
    public void registrarPieza() {
        controlRegistrarPieza.inicia();
    }

    /**
     * Método que arranca la historia de usuario "consultar distribuidores" (HU-25)
     *
     */
    public void consultarDistribuidores() {
        controlConsultarDistribuidores.inicia();
    }

        /**
     * Método que arranca la historia de usuario "inventario de piezas (HU-12)
     *
     */
    public void consultarInventario() {
        controlInventario.inicia();
    }

    /**
     * Método que arranca la historia de usuario "regitrar cotizacion" (HU-14)
     *
     */
    public void registrarCotizacion() {
        controlCotizacion.iniciar();
    }

    /**
     * Método que arranca la historia de usuario "historial de servicio" (HU-29)
     *
     */
    public void registrarServicio() {
        controlRegistrarServicio.inicia();
    }
    public void iniciaVentanaPedidos() { 
        controladorPedidos.inicia(); 
    }
    
    /**
     * Método que arranca la historia de usuario "lista de vehiculos por entregar" (HU-42)
     *
     */
    public void consultarEntregas() {
        controlVehiculoEntrega.inicia();
    }
    
    public void registrarCita() {
        controlRegistrarCita.inicia();
    }

    // INICIO Modificación realizada por Erik para la HU-34
    /**
     * Método que arranca la historia de usuario "Notificación de Atrasos" (HU-34)
     */
    public void informeAtrasos() {
        controlInformeAtrasos.inicia();
    }
    // FIN Modificación Erik HU-34
    public void registrarReparacion() {
        controlReparacion.inicia();
    }
}
package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // <-- IMPORT NUEVO
import org.springframework.stereotype.Component;
import mx.uam.ayd.proyecto.presentacion.consultarReparaciones.ControlConsultarReparaciones;
import mx.uam.ayd.proyecto.presentacion.listarInventario.ControlInventario;
import mx.uam.ayd.proyecto.presentacion.registrarPieza.ControlRegistrarPieza;
import mx.uam.ayd.proyecto.presentacion.registrarReparacion.ControlReparacion;
import mx.uam.ayd.proyecto.presentacion.consultarDistribuidores.ControlConsultarDistribuidores;
import mx.uam.ayd.proyecto.presentacion.consultarEntregas.ControlVehiculosEntrega;
import mx.uam.ayd.proyecto.presentacion.registrarServicio.ControlRegistrarServicio;
import mx.uam.ayd.proyecto.presentacion.pedidos.ControladorPedidos;
import mx.uam.ayd.proyecto.presentacion.registrarCotizacion.ControlCotizacion;
import mx.uam.ayd.proyecto.presentacion.registrarCita.ControlRegistrarCita;
import mx.uam.ayd.proyecto.presentacion.informeAtrasos.ControlInformeAtrasos;
import mx.uam.ayd.proyecto.presentacion.registrarReparacion.ControlReparacion;
import mx.uam.ayd.proyecto.presentacion.citasAgendadas.ControlCitasAgendadas;
import mx.uam.ayd.proyecto.presentacion.inicio.ControlSelectorRol;

/**
 * Esta clase lleva el flujo de control de la ventana principal
 * 
 * @author humbertocervantes
 *
 */
@Component
public class ControlPrincipal {

    private final ControlConsultarReparaciones controlConsultarReparaciones;
    private final ControlRegistrarPieza controlRegistrarPieza;
    private final ControlConsultarDistribuidores controlConsultarDistribuidores;
    private final ControlRegistrarServicio controlRegistrarServicio;
    private final VentanaPrincipal ventana;
    private final ControladorPedidos controladorPedidos;
    private final ControlInventario controlInventario;
    private final ControlVehiculosEntrega controlVehiculoEntrega;
    private final ControlCotizacion controlCotizacion;
    private final ControlRegistrarCita controlRegistrarCita;
    private final ControlInformeAtrasos controlInformeAtrasos;
    //HU-06 Reparacion 
    private final ControlReparacion controlReparacion;
    
    //HU-01 Citas Agendadas
    private final ControlCitasAgendadas controlCitasAgendadas;

    //HU-04
    @Lazy
    @Autowired
    private ControlSelectorRol controlSelectorRol;

    @Autowired
    public ControlPrincipal(
            ControlConsultarReparaciones controlConsultarReparaciones,
            ControlRegistrarPieza controlRegistrarPieza,
            ControlConsultarDistribuidores controlConsultarDistribuidores,
            ControlRegistrarServicio controlRegistrarServicio,
            ControladorPedidos controladorPedidos,
            ControlInventario controlInventario,
            ControlVehiculosEntrega controlVehiculosEntrega,
            ControlCotizacion controlCotizacion,
            ControlRegistrarCita controlRegistrarCita,
            ControlInformeAtrasos controlInformeAtrasos,
            ControlReparacion controlReparacion,
            ControlCitasAgendadas controlCitasAgendadas,
            VentanaPrincipal ventana) {
        this.controlConsultarReparaciones = controlConsultarReparaciones;
        this.controlRegistrarPieza = controlRegistrarPieza;
        this.controlConsultarDistribuidores = controlConsultarDistribuidores;
        this.controlRegistrarServicio = controlRegistrarServicio;
        this.ventana = ventana;
        this.controladorPedidos = controladorPedidos;
        this.controlInventario = controlInventario;
        this.controlVehiculoEntrega = controlVehiculosEntrega;
        this.controlCotizacion = controlCotizacion;
        this.controlRegistrarCita = controlRegistrarCita;
        this.controlInformeAtrasos = controlInformeAtrasos;
        this.controlReparacion = controlReparacion;
        this.controlCitasAgendadas = controlCitasAgendadas;
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
    /**
     * Método que arranca la historia de usuario "Notificación de Atrasos" (HU-34)
     */
    public void informeAtrasos() {
        controlInformeAtrasos.inicia();
    }

    // --- NUEVO MÉTODO PARA CERRAR LA VENTANA ---
    /**
     * Este método será llamado por la ventana cuando se presione la cruz roja
     */
    public void regresarASelector() {
        controlSelectorRol.inicia();
    }
    public void registrarReparacion() {
        controlReparacion.inicia();
    }

    /**
     * Método que arranca la historia de usuario "Citas agendadas" (HU-01)
     */
    public void citasAgendadas() {
        controlCitasAgendadas.inicia();
    }

    public void consultarReparaciones() {
        controlConsultarReparaciones.inicia();
    }
}
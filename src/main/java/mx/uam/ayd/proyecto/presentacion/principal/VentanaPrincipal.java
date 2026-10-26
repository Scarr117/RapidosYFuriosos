package mx.uam.ayd.proyecto.presentacion.principal;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Ventana principal usando JavaFX con FXML
 *
 */
@Component
public class VentanaPrincipal {

    private Stage stage;
    private ControlPrincipal control;
    private boolean initialized = false;

    /**
     * Constructor without UI initialization
     */
    public VentanaPrincipal() {
        // Don't initialize JavaFX components in constructor
    }

    /**
     * Initialize UI components on the JavaFX application thread
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }

        // Create UI only if we're on JavaFX thread
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Mi Aplicación");

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-principal.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            stage.setOnCloseRequest(event -> {
                event.consume();
                stage.close();
                if (control != null) {
                    control.regresarASelector(); // Volver al selector de perfiles
                }
            });

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setControlPrincipal(ControlPrincipal control) {
        this.control = control;
    }
    /**
     * Muestra la ventana y establece el control
     *
     * @param control El controlador asociado a esta ventana
     */
    public void muestra() {
        //this.control = control;

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra());
            return;
        }

        initializeUI();
        stage.show();
    }

    // FXML Event Handlers

    @FXML
    private void handleConsultarReparaciones() {
        if (control != null) {
            control.consultarReparaciones();
        }
    }

    @FXML
    private void handleConsultarInventario() {
        if (control != null) {
            control.consultarInventario();
        }
    }

    @FXML
    private void handleRegistrarPieza() {
        if (control != null) {
            control.registrarPieza();
        }
    }

    @FXML
    private void handleConsultarDistribuidores() {
        if (control != null) {
            control.consultarDistribuidores();
        }
    }

    @FXML
    private void handleRegistrarServicio() {
        if (control != null) {
            control.registrarServicio();
        }
    }
    @FXML
    private void abrirVentanaPedidos() {
        // Le dice al control principal que es hora de abrir la HU-30
        control.iniciaVentanaPedidos();
    }

    @FXML
    private void handleVehiculosEntrega() {
        if (control != null){
            control.consultarEntregas();
        }
    }

    @FXML
    private void handleRegistarCotizacion() {
        if (control != null){
            // Le dice al control principal que es hora de abrir la HU-14
            control.registrarCotizacion();
        }
    }
    @FXML
    private void handleRegistrarCita() {
        if (control != null) {
            control.registrarCita();
        }
    }

    // INICIO Modificación realizada por Erik para la HU-34
    @FXML
    private void handleInformeAtrasos() {
        if (control != null) {
            control.informeAtrasos();
        }
    }
    // FIN Modificación Erik HU-34

    @FXML 
    private void handleRegistrarReparacion() {
        if (control != null) {
            control.registrarReparacion();
        }
    }

    @FXML
    private void handleCitasAgendadas() {
        if (control != null) {
            control.citasAgendadas();
        }
    }
}
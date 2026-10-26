package mx.uam.ayd.proyecto.presentacion.consultarReparaciones;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.EstadoReparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Reparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;

/**
 * Ventana de la HU-14 (consultar reparaciones) usando JavaFX con FXML.
 *
 * Muestra tres listas, una por estado (en curso, en espera y listas para
 * entrega), y un panel con la información de la reparación seleccionada.
 * Si el taller no tiene vehículos, muestra un mensaje en lugar de las listas.
 */
@Component
public class VentanaConsultarReparaciones {

    private Stage stage;
    private ControlConsultarReparaciones control;

    @FXML
    private Label labelMensaje;

    @FXML
    private VBox contenedorListas;

    @FXML
    private Label labelEnCurso;

    @FXML
    private Label labelEnEspera;

    @FXML
    private Label labelListo;

    @FXML
    private TableView<Reparacion> tableEnCurso;

    @FXML
    private TableView<Reparacion> tableEnEspera;

    @FXML
    private TableView<Reparacion> tableListo;

    @FXML
    private TextArea textAreaDetalle;

    private boolean initialized = false;

    /**
     * Constructor without UI initialization
     */
    public VentanaConsultarReparaciones() {
        // Don't initialize JavaFX components in constructor
    }

    /**
     * Initialize UI components on the JavaFX application thread
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Reparaciones");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-consultar-reparaciones.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 700, 680);
            stage.setScene(scene);

            List<TableView<Reparacion>> tablas = List.of(tableEnCurso, tableEnEspera, tableListo);
            for (TableView<Reparacion> tabla : tablas) {
                configuraTabla(tabla, tablas);
            }

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar el archivo FXML: " + e.getMessage(), e);
        }
    }

    /**
     * Agrega las columnas a una tabla y hace que, al seleccionar una reparación,
     * se deseleccione en las otras tablas y se muestre su información.
     */
    private void configuraTabla(TableView<Reparacion> tabla, List<TableView<Reparacion>> todas) {

        tabla.getColumns().add(creaColumna("Placas", 100, r -> {
            Vehiculo v = r.getVehiculo();
            return v == null ? "" : v.getPlacas();
        }));
        tabla.getColumns().add(creaColumna("Marca", 100, r -> {
            Vehiculo v = r.getVehiculo();
            return v == null ? "" : v.getMarca();
        }));
        tabla.getColumns().add(creaColumna("Modelo", 100, r -> {
            Vehiculo v = r.getVehiculo();
            return v == null ? "" : v.getModelo();
        }));
        tabla.getColumns().add(creaColumna("Cliente", 170, r -> {
            Vehiculo v = r.getVehiculo();
            return (v == null || v.getCliente() == null) ? "" : v.getCliente().getNombre();
        }));
        tabla.getColumns().add(creaColumna("Inicio", 130,
                r -> ControlConsultarReparaciones.formateaFecha(r.getFechaInicio(), "")));

        tabla.setPlaceholder(new Label("Sin vehículos en este estado"));

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada == null) {
                return;
            }
            for (TableView<Reparacion> otra : todas) {
                if (otra != tabla) {
                    otra.getSelectionModel().clearSelection();
                }
            }
            if (control != null) {
                control.seleccionaReparacion(seleccionada);
            }
        });
    }

    private TableColumn<Reparacion, String> creaColumna(String titulo, double ancho,
                                                        Function<Reparacion, String> extractor) {
        TableColumn<Reparacion, String> columna = new TableColumn<>(titulo);
        columna.setPrefWidth(ancho);
        columna.setCellValueFactory(celda -> new SimpleStringProperty(extractor.apply(celda.getValue())));
        return columna;
    }

    /**
     * Establece el controlador asociado a esta ventana
     *
     * @param control El controlador asociado
     */
    public void setControlConsultarReparaciones(ControlConsultarReparaciones control) {
        this.control = control;
    }

    /**
     * Muestra las reparaciones clasificadas por estado
     *
     * @param reparaciones mapa estado → reparaciones de ese estado
     */
    public void muestraReparaciones(Map<EstadoReparacion, List<Reparacion>> reparaciones) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraReparaciones(reparaciones));
            return;
        }

        initializeUI();

        labelMensaje.setVisible(false);
        labelMensaje.setManaged(false);
        contenedorListas.setVisible(true);
        contenedorListas.setManaged(true);

        llenaLista(EstadoReparacion.EN_CURSO, tableEnCurso, labelEnCurso, reparaciones);
        llenaLista(EstadoReparacion.EN_ESPERA, tableEnEspera, labelEnEspera, reparaciones);
        llenaLista(EstadoReparacion.LISTO, tableListo, labelListo, reparaciones);

        textAreaDetalle.setText(ControlConsultarReparaciones.MENSAJE_SIN_SELECCION);

        stage.show();
    }

    private void llenaLista(EstadoReparacion estado, TableView<Reparacion> tabla, Label etiqueta,
                            Map<EstadoReparacion, List<Reparacion>> reparaciones) {
        List<Reparacion> lista = reparaciones.getOrDefault(estado, List.of());
        tabla.setItems(FXCollections.observableArrayList(lista));
        etiqueta.setText(estado.getEtiqueta() + " (" + lista.size() + ")");
    }

    /**
     * Muestra un mensaje en lugar de las listas cuando no hay vehículos en el taller
     *
     * @param mensaje el mensaje a mostrar
     */
    public void muestraTallerLibre(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraTallerLibre(mensaje));
            return;
        }

        initializeUI();

        labelMensaje.setText(mensaje);
        labelMensaje.setVisible(true);
        labelMensaje.setManaged(true);
        contenedorListas.setVisible(false);
        contenedorListas.setManaged(false);

        stage.show();
    }

    /**
     * Muestra la información de la reparación seleccionada
     *
     * @param texto el detalle ya formateado
     */
    public void muestraDetalle(String texto) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDetalle(texto));
            return;
        }

        textAreaDetalle.setText(texto);
    }

    // FXML Event Handlers

    @FXML
    private void handleActualizar() {
        if (control != null) {
            control.solicitarReparaciones();
        }
    }

    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        }
    }
}

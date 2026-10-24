package mx.uam.ayd.proyecto.presentacion.citasAgendadas;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;

/**
 * Ventana para visualizar y buscar las citas agendadas del día, de la semana y totales (HU-01).
 */
@Component
public class VentanaCitasAgendadas {

    private static final String ESTILO_ACTIVO = "-fx-background-color: #e8f5e9; -fx-border-color: #27ae60; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-text-fill: #1e8449; -fx-font-weight: bold; -fx-padding: 6px 14px;";
    private static final String ESTILO_INACTIVO = "-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-text-fill: #555555; -fx-padding: 6px 14px;";

    private static final Locale LOCALE_ES = new Locale("es", "MX");
    private static final DateTimeFormatter FORMATO_HORA_12 = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
    private static final DateTimeFormatter FORMATO_DIA_SEMANA = DateTimeFormatter.ofPattern("EEE dd MMM", LOCALE_ES);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HOY_LARGO = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", LOCALE_ES);
    private static final DateTimeFormatter FORMATO_MES_ANIO = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", LOCALE_ES);

    private Stage stage;
    private ControlCitasAgendadas control;
    private boolean initialized = false;
    private String pestaniaActual = "HOY";

    @FXML private TextField txtBuscarCliente;
    @FXML private TextField txtBuscarPlacas;
    @FXML private TextField txtBuscarTelefono;

    @FXML private Button btnCitasHoy;
    @FXML private Button btnCitasSemana;
    @FXML private Button btnTodasCitas;
    @FXML private Label lblInfoFecha;

    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, Long> colId;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colCliente;
    @FXML private TableColumn<Cita, String> colTelefono;
    @FXML private TableColumn<Cita, String> colVehiculo;
    @FXML private TableColumn<Cita, String> colPlacas;

    /**
     * Inicializa los componentes gráficos y las columnas de la tabla.
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
            stage.setTitle("Citas Agendadas - OmniTaller");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-citas-agendadas.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            configurarColumnas();
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configura cómo se muestran los datos de cada cita en las columnas de la tabla.
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getIdCita()));

        colFecha.setCellValueFactory(cell -> {
            LocalDate fecha = cell.getValue().getFecha();
            if (fecha == null) {
                return new SimpleStringProperty("");
            }
            if ("SEMANA".equals(pestaniaActual)) {
                return new SimpleStringProperty(fecha.format(FORMATO_DIA_SEMANA));
            }
            String textoFecha = fecha.format(FORMATO_FECHA);
            if (fecha.equals(LocalDate.now())) {
                textoFecha += " (Hoy)";
            }
            return new SimpleStringProperty(textoFecha);
        });

        // En "Todas las citas", las citas del día actual muestran su fecha en azul con "(Hoy)"
        colFecha.setCellFactory(col -> new TableCell<Cita, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("(Hoy)")) {
                        setStyle("-fx-text-fill: #0078D7; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        colHora.setCellValueFactory(cell -> {
            if (cell.getValue().getHora() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(cell.getValue().getHora().format(FORMATO_HORA_12));
        });

        colCliente.setCellValueFactory(cell -> {
            if (cell.getValue().getCliente() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(cell.getValue().getCliente().getNombre());
        });

        colTelefono.setCellValueFactory(cell -> {
            if (cell.getValue().getCliente() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(formatearTelefono(cell.getValue().getCliente().getTelefono()));
        });

        colVehiculo.setCellValueFactory(cell -> {
            Vehiculo v = cell.getValue().getVehiculo();
            if (v == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(v.getMarca() + " " + v.getModelo() + " " + v.getAnio());
        });

        colPlacas.setCellValueFactory(cell -> {
            if (cell.getValue().getVehiculo() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(cell.getValue().getVehiculo().getPlacas());
        });
    }

    /**
     * Formatea un número telefónico de 10 dígitos al formato XX-XXXX-XXXX.
     */
    private String formatearTelefono(String telefono) {
        if (telefono == null) {
            return "";
        }
        String digitos = telefono.replaceAll("[^0-9]", "");
        if (digitos.length() == 10) {
            return digitos.substring(0, 2) + "-" + digitos.substring(2, 6) + "-" + digitos.substring(6, 10);
        }
        return telefono;
    }

    /**
     * Asigna el controlador asociado a esta ventana.
     */
    public void setControlCitasAgendadas(ControlCitasAgendadas control) {
        this.control = control;
    }

    /**
     * Muestra la ventana con las citas agendadas para el día de hoy (pestaña por defecto).
     *
     * @param citasHoy Lista de citas del día actual
     */
    public void muestra(List<Cita> citasHoy) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(citasHoy));
            return;
        }

        initializeUI();
        pestaniaActual = "HOY";
        colFecha.setVisible(false);

        actualizarPestanias(btnCitasHoy, "Citas de hoy", citasHoy.size());
        lblInfoFecha.setText(LocalDate.now().format(FORMATO_HOY_LARGO));

        tablaCitas.setItems(FXCollections.observableArrayList(citasHoy));
        tablaCitas.refresh();
        stage.show();
    }

    /**
     * Muestra en la tabla las citas agendadas de la semana en curso, incorporando la columna Día / Fecha.
     *
     * @param citasSemana Lista de citas de la semana
     */
    public void muestraCitaSemana(List<Cita> citasSemana) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraCitaSemana(citasSemana));
            return;
        }

        initializeUI();
        pestaniaActual = "SEMANA";
        colFecha.setText("Día / Fecha");
        colFecha.setVisible(true);

        actualizarPestanias(btnCitasSemana, "Citas de la semana", citasSemana.size());

        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.with(DayOfWeek.MONDAY);
        LocalDate fin = hoy.with(DayOfWeek.SATURDAY);
        lblInfoFecha.setText("Semana del " + inicio.getDayOfMonth() + " al " + fin.getDayOfMonth()
                + " de " + fin.format(FORMATO_MES_ANIO));

        tablaCitas.setItems(FXCollections.observableArrayList(citasSemana));
        tablaCitas.refresh();
    }

    /**
     * Muestra en la tabla el listado completo de citas (o resultados de búsqueda), incorporando la columna Fecha.
     *
     * @param citasTotales Lista de citas a mostrar
     */
    public void muestraCitasTotales(List<Cita> citasTotales) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraCitasTotales(citasTotales));
            return;
        }

        initializeUI();
        pestaniaActual = "TODAS";
        colFecha.setText("Fecha");
        colFecha.setVisible(true);

        actualizarPestanias(btnTodasCitas, "Todas las citas", citasTotales.size());
        lblInfoFecha.setText("Historial y agenda completa");

        tablaCitas.setItems(FXCollections.observableArrayList(citasTotales));
        tablaCitas.refresh();
    }

    /**
     * Actualiza el estilo visual (punto verde y fondo ligeramente verde) y el contador de la pestaña activa.
     */
    private void actualizarPestanias(Button botonActivo, String textoBase, int total) {
        btnCitasHoy.setText("Citas de hoy");
        btnCitasHoy.setStyle(ESTILO_INACTIVO);

        btnCitasSemana.setText("Citas de la semana");
        btnCitasSemana.setStyle(ESTILO_INACTIVO);

        btnTodasCitas.setText("Todas las citas");
        btnTodasCitas.setStyle(ESTILO_INACTIVO);

        botonActivo.setText("● " + textoBase + " (" + total + ")");
        botonActivo.setStyle(ESTILO_ACTIVO);
    }

    @FXML
    private void handleCitasHoy() {
        if (control != null) {
            control.inicia();
        }
    }

    @FXML
    private void handleCitasSemanales() {
        if (control != null) {
            control.citasSemanales();
        }
    }

    @FXML
    private void handleTodasLasCitas() {
        if (control != null) {
            control.citasTotales();
        }
    }

    /**
     * Toma el atributo ingresado en cualquiera de las barras de búsqueda y solicita el filtrado al control.
     */
    @FXML
    private void handleBuscar() {
        if (control == null) {
            return;
        }

        String cliente = txtBuscarCliente.getText() != null ? txtBuscarCliente.getText().trim() : "";
        String placas = txtBuscarPlacas.getText() != null ? txtBuscarPlacas.getText().trim() : "";
        String telefono = txtBuscarTelefono.getText() != null ? txtBuscarTelefono.getText().trim() : "";

        if (!placas.isEmpty()) {
            control.citasAtributo("placas", placas);
        } else if (!telefono.isEmpty()) {
            control.citasAtributo("telefono", telefono);
        } else {
            control.citasAtributo("cliente", cliente);
        }
    }

    /**
     * Cierra la ventana para regresar al menú principal de OmniTaller.
     */
    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        }
    }
}

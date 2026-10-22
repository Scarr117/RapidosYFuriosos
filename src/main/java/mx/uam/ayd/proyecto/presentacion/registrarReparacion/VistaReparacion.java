package mx.uam.ayd.proyecto.presentacion.registrarReparacion;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.datos.CotizacionRepository.CotizacionAprobadaDTO;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.CotizacionConcepto;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;
import mx.uam.ayd.proyecto.presentacion.registrarCotizacion.ControlCotizacion;
import mx.uam.ayd.proyecto.negocio.modelo.Refaccion;
import org.springframework.stereotype.Component;

import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component
public class VistaReparacion {

    // Tabla para seleccionar la cotización
    @FXML private TableView<CotizacionAprobadaDTO> tablaCotizaciones;
    @FXML private TableColumn<CotizacionAprobadaDTO, Long> colIdCotizacion;
    @FXML private TableColumn<CotizacionAprobadaDTO, String> colNombreCliente;
    @FXML private TableColumn<CotizacionAprobadaDTO, String> colModeloVehiculo;

    // Elementos para mostrar la información detallada (solo lectura)
    @FXML private Label lblNombre;
    @FXML private Label lblTelefono;
    @FXML private Label lblMarca;
    @FXML private Label lblModelo;
    @FXML private Label lblAnio;
    @FXML private Label lblPlacas;
    @FXML private TextArea txtDiagnostico; // Text area bloqueado para mostrar el texto largo

    // Elementos para el ingreso de datos de la reparación
    @FXML private TextArea txtFallas;
    @FXML private TextField txtMesesGarantia;
    @FXML private Button btnGuardarReparacion;

    private ControlReparacion control;
    private Stage stage;

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla (los nombres deben coincidir con los getters del DTO)
        colIdCotizacion.setCellValueFactory(new PropertyValueFactory<>("idCotizacion"));
        colNombreCliente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colModeloVehiculo.setCellValueFactory(new PropertyValueFactory<>("modelo"));

        // Listener para detectar cuando el usuario hace clic en una fila de la tabla
        tablaCotizaciones.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    control.onCotizacionseleccionada(newValue.getIdCotizacion());
                }
            }
        );
    }

    // --- Métodos de configuración inicial ---
    public void iniciarVisualizacion(ControlReparacion control) {
        this.control = control;

        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-registrar-reparacion.fxml"));   
            loader.setController(this);
            
            // Crear la ventana
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Cotización");
            
            // Mostrar la ventana
            stage.show();
            
            
            bloquearEdicion();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensajeError("Error al cargar la ventana de reparacion.");
        }
    }

        public void mostrarCotizaciones(List<CotizacionAprobadaDTO> cotizaciones) {
        tablaCotizaciones.setItems(FXCollections.observableArrayList(cotizaciones));
    }

    public void mostrarInfoDetallada(String nombre, String telefono, String marca, String modelo, int anio, String placas, String diagnostico) {
        lblNombre.setText(nombre);
        lblTelefono.setText(telefono);
        lblMarca.setText(marca);
        lblModelo.setText(modelo);
        lblAnio.setText(String.valueOf(anio));
        lblPlacas.setText(placas);
        
        txtDiagnostico.setText(diagnostico);
        txtDiagnostico.setEditable(false); // Asegurar que sea solo lectura
    }

    public void permitirEdicion() {
        txtFallas.setDisable(false);
        txtMesesGarantia.setDisable(false);
        btnGuardarReparacion.setDisable(false);
    }

    public void bloquearEdicion() {
        txtFallas.setDisable(true);
        txtMesesGarantia.setDisable(true);
        btnGuardarReparacion.setDisable(true);
        
        // Limpiar campos visualmente
        txtFallas.clear();
        txtMesesGarantia.clear();
    }

    @FXML
    public void accionGuardarReparacion() {
        String fallas = txtFallas.getText();
        String garantiaStr = txtMesesGarantia.getText();

        // 1. Validar campos vacíos
        if (fallas == null || fallas.trim().isEmpty() || garantiaStr == null || garantiaStr.trim().isEmpty()) {
            mostrarMensajeError("Por favor, ingrese las fallas detectadas y los meses de garantía.");
            return;
        }

        // 2. Validar que la garantía sea un número válido
        int mesesGarantia = 0;
        try {
            mesesGarantia = Integer.parseInt(garantiaStr.trim());
            if (mesesGarantia < 0) {
                mostrarMensajeError("Los meses de garantía no pueden ser negativos.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarMensajeError("Ingrese un número válido para los meses de garantía.");
            return;
        }

        // 3. Mandar a guardar al controlador
        control.onGuardarClick(fallas, mesesGarantia);
    }

    public void mostrarMensajeExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText("La reparación se ha generado y guardado exitosamente.");
        alert.showAndWait();
        stage.close(); // Cerrar ventana tras éxito
    }

    public void mostrarMensajeError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

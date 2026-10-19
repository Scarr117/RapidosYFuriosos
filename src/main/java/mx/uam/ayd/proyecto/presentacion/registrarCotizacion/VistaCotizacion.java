package mx.uam.ayd.proyecto.presentacion.registrarCotizacion;

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
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.CotizacionConcepto;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;
import mx.uam.ayd.proyecto.negocio.modelo.Refaccion;
import org.springframework.stereotype.Component;

import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component
public class VistaCotizacion {

    
    @FXML private ComboBox<Cliente> comboClientes;
    @FXML private ComboBox<Vehiculo> comboVehiculos;
    
    // Panel de Refacciones
    @FXML private TextField txtBuscarPieza;
    @FXML private Button btnBuscarPieza;
    @FXML private TableView<Refaccion> tablaRefacciones;
    @FXML private TableColumn<Refaccion, Integer> colId;
    @FXML private TableColumn<Refaccion, String> colNombre;
    @FXML private TableColumn<Refaccion, Float> colPrecio; 
    @FXML private TableColumn<Refaccion, Integer> colExistencia;
    //@FXML private TextField txtCantidadRefaccion;
    @FXML private Label lblContador;
    @FXML private Button btnAgregarRefaccion;

    
    // Panel de Servicios
    @FXML private TextArea txtFallas;
    @FXML private TextField txtCostoManoObra;
    //@FXML private Button btnActualizarServicio;
    
    // Totales y Guardar
    @FXML private Label lblSubtotal;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;
    @FXML private Button btnGuardarCotizacion;

    private int cantidadEscogida = 0;
    private Map<Integer, Integer> piezasYaAgregadas = new HashMap<>();
    private Refaccion refaccionActual;
    private ControlCotizacion control;
    private Stage stage;

    @FXML
    public void initialize() {
        
        colId.setCellValueFactory(new PropertyValueFactory<>("idRefaccion"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));

        // 2. Formato limpio para el ComboBox de Clientes
        comboClientes.setConverter(new javafx.util.StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                return (cliente == null) ? "" : cliente.getNombre() + " " + cliente.getTelefono();
            }

            @Override
            public Cliente fromString(String string) { return null; }
        });

        // Formato limpio para el ComboBox de Vehículos
        comboVehiculos.setConverter(new javafx.util.StringConverter<Vehiculo>() {
            @Override
            public String toString(Vehiculo vehiculo) {
                return (vehiculo == null) ? "" : vehiculo.getMarca() + " " + vehiculo.getModelo()+ " " + vehiculo.getAnio();
            }

            @Override
            public Vehiculo fromString(String string) { return null; }
        });

    }

    // --- Métodos de configuración inicial ---
    public void iniciarVisualizacion(ControlCotizacion control) {
        this.control = control;

        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-cotizacion.fxml")); 
            
            
            loader.setController(this);
            
            // Crear la ventana
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Cotización");
            
            // Mostrar la ventana
            stage.show();
            
            
            bloquearEdicion();
            configurarEventos();
            
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensajeError("Error al cargar la ventana de cotización.");
        }

        
    }

    private void configurarEventos() {
        // Evento cuando el usuario elige un cliente en el ComboBox
        comboClientes.setOnAction(event -> {
            Cliente seleccionado = comboClientes.getSelectionModel().getSelectedItem();
            control.onClienteSeleccionado(seleccionado);
        });

        
        comboVehiculos.setOnAction(event -> {
            Vehiculo seleccionado = comboVehiculos.getSelectionModel().getSelectedItem();

            if (seleccionado != null) {
            control.onVehiculoSeleccionado(seleccionado);
            }
        });

        // Cálculo automático de Mano de Obra
        txtCostoManoObra.textProperty().addListener((observable, oldValue, newValue) -> {
            
            // Si el campo se queda vacío, el costo es 0
            if (newValue == null || newValue.trim().isEmpty()) {
                control.onActualizarServicio(0.0f);
                return;
            }

            // Bloquear letras: Solo permitimos números y opcionalmente un punto decimal
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtCostoManoObra.setText(oldValue); // Deshace la tecla presionada si es inválida
                return;
            }

            // Procesar el costo
            try {
                float costo = Float.parseFloat(newValue);
                control.onActualizarServicio(costo);
            } catch (NumberFormatException e) {
                // Atrapa casos temporales mientras el usuario escribe, por ejemplo, si solo teclea "."
                control.onActualizarServicio(0.0f);
            }
        });

    }

    
    public void mostrarClientes(List<Cliente> clientesDisponibles) {
        comboClientes.setItems(FXCollections.observableArrayList(clientesDisponibles));
    }

    public void mostrarVehiculos(List<Vehiculo> vehiculosDelCliente) {
        comboVehiculos.setItems(FXCollections.observableArrayList(vehiculosDelCliente));
        comboVehiculos.setDisable(false);
    }

    public void permitirEdicion() {
        // Habilita los campos tras validar la cita del vehículo
        txtBuscarPieza.setDisable(false);
        btnBuscarPieza.setDisable(false);
        tablaRefacciones.setDisable(false);
        btnAgregarRefaccion.setDisable(false);
        txtFallas.setDisable(false);
        txtCostoManoObra.setDisable(false);
        btnGuardarCotizacion.setDisable(false);
    }

    public void bloquearEdicion() {
        comboVehiculos.setDisable(true);
        txtBuscarPieza.setDisable(true);
        btnBuscarPieza.setDisable(true);
        tablaRefacciones.setDisable(true);
        btnAgregarRefaccion.setDisable(true);
        txtFallas.setDisable(true);
        txtCostoManoObra.setDisable(true);
        btnGuardarCotizacion.setDisable(true);
    }

    public void mostrarRefaccion(List<Refaccion> encontrada) {
        tablaRefacciones.setItems(FXCollections.observableArrayList(encontrada));
    }

    public void actualizarEtiquetasTotales(float subtotal, float iva, float total) {
        lblSubtotal.setText("$ " + String.format("%.2f", subtotal));
        lblIva.setText("$ " + String.format("%.2f", iva));
        lblTotal.setText("$ " + String.format("%.2f", total));
    }

    public void mostrarMensajeExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText("Cotización guardada exitosamente.");
        alert.showAndWait();

        Stage stageActual = (Stage) btnGuardarCotizacion.getScene().getWindow();
        stageActual.close();
        
    }

    public void mostrarMensajeError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /*
    FXML
     */


    @FXML
    public void accionBuscarRefaccion() {
        String textoIngresado = txtBuscarPieza.getText();
    
    // Validar que no esté vacío
        if (textoIngresado == null || textoIngresado.trim().isEmpty()) {
            return; 
        }

        try {
        // Convertir el texto a Int
            Integer idPieza = Integer.parseInt(textoIngresado.trim());
            control.onBuscarRefaccionClick(idPieza);
        
        } catch (NumberFormatException e) {
        // Si el usuario escribe letras, evitamos que la app explote
            mostrarMensajeError("Error: El ID debe ser un número válido.");
        }
    }

    @FXML
    public void accionBotonMas() {
        Refaccion seleccionada = tablaRefacciones.getSelectionModel().getSelectedItem();
        if (seleccionada != null && cantidadEscogida < seleccionada.getExistencia()) {

            //Obtenenmos cuantas piezas de esta refacción YA agregamos a la cotización 
            int yaAgregadas = piezasYaAgregadas.getOrDefault(seleccionada.getIdRefaccion(), 0);

            if ((cantidadEscogida + yaAgregadas) < seleccionada.getExistencia()){
                cantidadEscogida++;
                lblContador.setText(String.valueOf(cantidadEscogida));
            }
       
         }
    }

    @FXML
    public void accionBotonMenos() {
        if (cantidadEscogida > 0) {
            cantidadEscogida--;
            lblContador.setText(String.valueOf(cantidadEscogida));
        }
    }

    @FXML
    public void accionAgregarRefaccion() {
        Refaccion seleccionada = tablaRefacciones.getSelectionModel().getSelectedItem();

        if(seleccionada == null){
            mostrarMensajeError("Seleccione una refaccion de la tabla primero");
            return;
        }
        if (cantidadEscogida == 0) {
            mostrarMensajeError("Escoge al menos 1 pieza");
            return;
        }

        control.onAgregarRefaccion(seleccionada,cantidadEscogida);

        int yaAgregadas = piezasYaAgregadas.getOrDefault(seleccionada.getIdRefaccion(), 0);
        piezasYaAgregadas.put(seleccionada.getIdRefaccion(), yaAgregadas + cantidadEscogida);

        cantidadEscogida = 0;
        lblContador.setText("0");

    }

    @FXML
    public void accionGuardarCotizacion() {
        Vehiculo vehiculoSeleccionado = comboVehiculos.getValue();
        String diagnostico = txtFallas.getText();
        String costoTxt = txtCostoManoObra.getText();

        if (vehiculoSeleccionado == null || diagnostico == null || diagnostico.trim().isEmpty() ||
        costoTxt == null || costoTxt.trim().isEmpty()) {
        
        mostrarMensajeError("¡Atención! Debes seleccionar el vehiculo, llenar las fallas y el costo de mano de obra antes de guardar.");
        return; 
        }

        try {
            float costoManoObra = Float.parseFloat(costoTxt.trim());
            if (costoManoObra <= 0) {
                mostrarMensajeError("El costo de la mano de obra debe ser mayor a $0.00.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarMensajeError("El costo de la mano de obra debe ser un número válido.");
            return;
        }

        // 3. Procesar el guardado si las validaciones pasaron
        control.onGuardarClick(diagnostico, vehiculoSeleccionado);
    }
}
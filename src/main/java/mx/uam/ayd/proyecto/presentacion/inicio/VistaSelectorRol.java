package mx.uam.ayd.proyecto.presentacion.inicio;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class VistaSelectorRol {

    private Stage stage;
    private ControlSelectorRol control;

    public void muestra(ControlSelectorRol control) {
        this.control = control;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-selector-rol.fxml"));

            loader.setController(this);
            Parent root = loader.load();
            // Crear una nueva ventana
            stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Selección de Perfil - Rápidos y Furiosos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cierra() {
        if (stage != null) {
            stage.close();
        }
    }
    @FXML
    void ingresarComoMecanico(ActionEvent event) {
        control.ingresarComoMecanico();
    }

    @FXML
    void ingresarComoCliente(ActionEvent event) {
        control.ingresarComoCliente();
    }
}
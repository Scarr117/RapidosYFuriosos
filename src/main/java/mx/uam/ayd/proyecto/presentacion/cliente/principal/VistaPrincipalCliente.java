package mx.uam.ayd.proyecto.presentacion.cliente.principal;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class VistaPrincipalCliente {

    private Stage stage;
    private ControlPrincipalCliente control;

    public void muestra(ControlPrincipalCliente control) {
        this.control = control;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cliente/ventana-principal-cliente.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            stage = new Stage();
            stage.setScene(new Scene(root, 700, 500));
            stage.setTitle("Panel de Cliente - Rápidos y Furiosos");
            stage.setOnCloseRequest(event -> {
                event.consume();
                stage.close();
                control.regresarASelector();
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
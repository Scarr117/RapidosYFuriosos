package mx.uam.ayd.proyecto.presentacion.pedidos;

import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class VentanaPedidos {

    private Stage stage;

    /**
     * Método que carga el FXML y muestra la ventana en pantalla.
     * Recibe el controlador para enlazarse correctamente.
     */
    public void muestra(ControladorPedidos controlador) {
        try {
            // 1. Cargar el diseño FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VentanaPedidos.fxml"));
            
            // ¡OJO AQUÍ! Debes poner la ruta exacta donde guardaste tu archivo FXML
            //loader.setLocation(getClass().getResource("/fxml/VentanaPedidos.fxml"));
            
            // 2. Le decimos al FXML que use tu controlador de Spring
            loader.setController(controlador);
            
            Parent root = loader.load();

            // 3. Crear la ventana si no existe
            if (stage == null) {
                stage = new Stage();
            }

            // 4. Configurar y mostrar
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestión y Seguimiento de Pedidos (HU-30)");
            stage.show();

        } catch (Exception e) {
            System.err.println("Error al cargar la ventana de Pedidos:");
            e.printStackTrace();
        }
    }
}

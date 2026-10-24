package mx.uam.ayd.proyecto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.Grupo;
// Importamos el nuevo controlador del selector en lugar del principal
import mx.uam.ayd.proyecto.presentacion.inicio.ControlSelectorRol;

@SpringBootApplication
public class ProyectoApplication {

    private final ControlSelectorRol controlSelectorRol;

    @Autowired
    public ProyectoApplication(ControlSelectorRol controlSelectorRol) {
        this.controlSelectorRol = controlSelectorRol;
    }

    public static void main(String[] args) {
        Application.launch(JavaFXApplication.class, args);
    }

    public static class JavaFXApplication extends Application {

        private static ConfigurableApplicationContext applicationContext;

        @Override
        public void init() throws Exception {
            SpringApplicationBuilder builder = new SpringApplicationBuilder(ProyectoApplication.class);
            builder.headless(false);
            applicationContext = builder.run(getParameters().getRaw().toArray(new String[0]));
        }

        @Override
        public void start(Stage primaryStage) {
            Platform.runLater(() -> {
                applicationContext.getBean(ProyectoApplication.class).inicia();
            });
        }

        @Override
        public void stop() throws Exception {
            applicationContext.close();
            Platform.exit();
        }
    }

    public void inicia() {
        inicializaBD();

        Platform.runLater(() -> {
            // Arrancamos el selector de rol en lugar de la ventana del mecánico
            controlSelectorRol.inicia();
        });
    }

    public void inicializaBD() {
        Grupo grupoAdmin = new Grupo();
        grupoAdmin.setNombre("Administradores");

        Grupo grupoOps = new Grupo();
        grupoOps.setNombre("Operadores");
    }
}
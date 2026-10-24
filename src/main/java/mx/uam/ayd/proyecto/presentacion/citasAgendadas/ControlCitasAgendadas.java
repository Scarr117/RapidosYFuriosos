package mx.uam.ayd.proyecto.presentacion.citasAgendadas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioCitasAgendadas;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;

/**
 * Controlador de presentación para la ventana de Citas Agendadas (HU-01).
 */
@Component
public class ControlCitasAgendadas {

    @Autowired
    private ServicioCitasAgendadas servicioCitasAgendadas;

    @Autowired
    private VentanaCitasAgendadas ventanaCitasAgendadas;

    /**
     * Inicia la ventana mostrando por defecto las citas agendadas para el día de hoy.
     */
    public void inicia() {
        ventanaCitasAgendadas.setControlCitasAgendadas(this);
        List<Cita> citasHoy = servicioCitasAgendadas.obtenerCitasHoy();
        ventanaCitasAgendadas.muestra(citasHoy);
    }

    /**
     * Obtiene las citas de la semana actual y las envía a la ventana.
     */
    public void citasSemanales() {
        List<Cita> citasSemana = servicioCitasAgendadas.obtenerCitasSemanales();
        ventanaCitasAgendadas.muestraCitaSemana(citasSemana);
    }

    /**
     * Obtiene todas las citas agendadas y las envía a la ventana.
     */
    public void citasTotales() {
        List<Cita> citasTotales = servicioCitasAgendadas.obtenerCitasTotales();
        ventanaCitasAgendadas.muestraCitasTotales(citasTotales);
    }

    /**
     * Busca citas según el criterio y atributo ingresados y actualiza la tabla en la ventana.
     *
     * @param criterio Tipo de filtro (cliente, placas o telefono)
     * @param atributo Texto a buscar
     */
    public void citasAtributo(String criterio, String atributo) {
        List<Cita> citasTotales = servicioCitasAgendadas.obtenerCitasAtributo(criterio, atributo);
        ventanaCitasAgendadas.muestraCitasTotales(citasTotales);
    }
}

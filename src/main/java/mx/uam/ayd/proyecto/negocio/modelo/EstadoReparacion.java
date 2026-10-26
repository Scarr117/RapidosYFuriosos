package mx.uam.ayd.proyecto.negocio.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Estados con los que el mecánico ve las reparaciones que hay en el taller
 * (HU-14: consultar reparaciones).
 *
 * Las reparaciones ya entregadas no pertenecen a ningún estado, porque
 * ya no están en el taller.
 */
public enum EstadoReparacion {

    /** Se está trabajando en el vehículo. */
    EN_CURSO("En curso", "En reparación", "En revisión"),

    /** Hay algo pendiente que requiere revisión antes de poder entregarlo. */
    EN_ESPERA("En espera", "En espera"),

    /** Ya está reparado, pero el cliente aún no lo recoge. */
    LISTO("Listo para entrega", "Listo para entrega");

    private final String etiqueta;
    private final List<String> estatusBD;

    EstadoReparacion(String etiqueta, String... estatusBD) {
        this.etiqueta = etiqueta;
        this.estatusBD = List.of(estatusBD);
    }

    /**
     * @return texto que se muestra al mecánico
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * @return los textos de estatusServicio (tal como se guardan en la BD) que pertenecen a este estado
     */
    public List<String> getEstatusBD() {
        return estatusBD;
    }

    /**
     * Determina a qué estado pertenece un estatusServicio guardado en la BD.
     *
     * @param estatus texto de estatusServicio de una reparación
     * @return el estado correspondiente, o vacío si es nulo o no pertenece a
     *         ninguno (por ejemplo "Entregado")
     */
    public static Optional<EstadoReparacion> desdeEstatus(String estatus) {
        if (estatus == null) {
            return Optional.empty();
        }

        String limpio = estatus.trim();
        for (EstadoReparacion estado : values()) {
            for (String texto : estado.estatusBD) {
                if (texto.equalsIgnoreCase(limpio)) {
                    return Optional.of(estado);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * @return todos los textos de estatusServicio que significan "el vehículo sigue en el taller"
     */
    public static List<String> estatusEnTaller() {
        List<String> todos = new ArrayList<>();
        for (EstadoReparacion estado : values()) {
            todos.addAll(estado.estatusBD);
        }
        return todos;
    }
}

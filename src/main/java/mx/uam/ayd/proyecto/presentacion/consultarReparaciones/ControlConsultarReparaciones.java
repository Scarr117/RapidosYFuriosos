package mx.uam.ayd.proyecto.presentacion.consultarReparaciones;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioReparacion;
import mx.uam.ayd.proyecto.negocio.modelo.EstadoReparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Reparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;

/**
 * Módulo de control para la historia de usuario HU-14: el mecánico consulta
 * las reparaciones que hay actualmente en el taller (en curso, en espera y
 * listas pendientes de entrega).
 */
@Component
public class ControlConsultarReparaciones {

    /** Mensaje que se muestra cuando no hay ningún vehículo en el taller. */
    public static final String MENSAJE_TALLER_LIBRE = "El taller está libre: no hay vehículos en reparación ni pendientes de entrega.";

    /** Texto del panel de detalle mientras no hay una reparación seleccionada. */
    public static final String MENSAJE_SIN_SELECCION = "Seleccione una reparación para consultar su información.";

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ServicioReparacion servicioReparacion;
    private final VentanaConsultarReparaciones ventana;

    @Autowired
    public ControlConsultarReparaciones(ServicioReparacion servicioReparacion,
                                        VentanaConsultarReparaciones ventana) {
        this.servicioReparacion = servicioReparacion;
        this.ventana = ventana;
    }

    /**
     * Método que se ejecuta después de la construcción del bean
     * y realiza la conexión bidireccional entre el control y la ventana
     */
    @PostConstruct
    public void init() {
        ventana.setControlConsultarReparaciones(this);
    }

    /**
     * Inicia la historia de usuario: consulta las reparaciones vigentes y
     * abre la ventana con ellas.
     */
    public void inicia() {
        solicitarReparaciones();
    }

    /**
     * Consulta las reparaciones registradas, las clasifica por estado y se las
     * entrega a la ventana. Se vuelve a consultar en cada llamada, por lo que
     * siempre se muestran los estados registrados en ese momento.
     */
    public void solicitarReparaciones() {

        List<Reparacion> reparaciones = servicioReparacion.obtenerReparaciones();
        Map<EstadoReparacion, List<Reparacion>> clasificadas = clasificaPorEstado(reparaciones);

        boolean hayReparaciones = clasificadas.values().stream().anyMatch(lista -> !lista.isEmpty());

        if (!hayReparaciones) {
            ventana.muestraTallerLibre(MENSAJE_TALLER_LIBRE);
            return;
        }

        ventana.muestraReparaciones(clasificadas);
    }

    /**
     * Clasifica las reparaciones por estado. El mapa siempre trae los tres
     * estados (aunque alguno vaya vacío). Las reparaciones cuyo estatus no
     * pertenece a ningún estado (por ejemplo, ya entregadas) se descartan.
     *
     * @param reparaciones reparaciones a clasificar
     * @return mapa estado → reparaciones de ese estado
     */
    public Map<EstadoReparacion, List<Reparacion>> clasificaPorEstado(List<Reparacion> reparaciones) {

        Map<EstadoReparacion, List<Reparacion>> resultado = new EnumMap<>(EstadoReparacion.class);
        for (EstadoReparacion estado : EstadoReparacion.values()) {
            resultado.put(estado, new ArrayList<>());
        }

        if (reparaciones == null) {
            return resultado;
        }

        for (Reparacion reparacion : reparaciones) {
            EstadoReparacion.desdeEstatus(reparacion.getEstatusServicio())
                    .ifPresent(estado -> resultado.get(estado).add(reparacion));
        }

        return resultado;
    }

    /**
     * El mecánico selecciona una reparación: se muestra su información disponible.
     *
     * @param reparacion la reparación seleccionada (si es nula no se hace nada)
     */
    public void seleccionaReparacion(Reparacion reparacion) {
        if (reparacion == null) {
            return;
        }
        ventana.muestraDetalle(construyeDetalle(reparacion));
    }

    /**
     * Arma el texto con la información registrada de una reparación.
     *
     * @param reparacion la reparación
     * @return texto listo para mostrarse
     */
    public String construyeDetalle(Reparacion reparacion) {

        StringBuilder detalle = new StringBuilder();

        detalle.append("Reparación #").append(reparacion.getIdReparacion()).append('\n');
        detalle.append("Vehículo: ").append(descripcionVehiculo(reparacion)).append('\n');

        Vehiculo vehiculo = reparacion.getVehiculo();
        if (vehiculo != null && vehiculo.getCliente() != null) {
            detalle.append("Cliente: ").append(vehiculo.getCliente().getNombre()).append('\n');
        }

        detalle.append("Estado: ").append(etiquetaEstado(reparacion)).append('\n');
        detalle.append("Inicio: ").append(formateaFecha(reparacion.getFechaInicio(), "Sin fecha registrada")).append('\n');
        detalle.append("Fin: ").append(formateaFecha(reparacion.getFechaFin(), "Aún sin fecha de término")).append('\n');

        if (reparacion.getGarantiaMeses() > 0) {
            detalle.append("Garantía: ").append(reparacion.getGarantiaMeses()).append(" meses");
            if (reparacion.getCondicionesGarantia() != null && !reparacion.getCondicionesGarantia().isBlank()) {
                detalle.append(" - ").append(reparacion.getCondicionesGarantia());
            }
            detalle.append('\n');
        }

        String observaciones = reparacion.getObservacionesTecnicas();
        detalle.append("Observaciones técnicas: ");
        if (observaciones == null || observaciones.isBlank()) {
            detalle.append("Sin observaciones registradas");
        } else {
            detalle.append(observaciones);
        }

        return detalle.toString();
    }

    /**
     * @return marca, modelo y placas del vehículo de la reparación
     */
    public String descripcionVehiculo(Reparacion reparacion) {
        Vehiculo vehiculo = reparacion.getVehiculo();
        if (vehiculo == null) {
            return "Vehículo sin datos registrados";
        }
        return vehiculo.getMarca() + " " + vehiculo.getModelo() + " - Placas: " + vehiculo.getPlacas();
    }

    /**
     * @return el estado en que se muestra la reparación (o su estatus tal cual, si no pertenece a ningún estado)
     */
    public String etiquetaEstado(Reparacion reparacion) {
        return EstadoReparacion.desdeEstatus(reparacion.getEstatusServicio())
                .map(EstadoReparacion::getEtiqueta)
                .orElse(String.valueOf(reparacion.getEstatusServicio()));
    }

    /**
     * Da formato de fecha y hora para mostrarse en pantalla.
     */
    public static String formateaFecha(LocalDateTime fecha, String textoSiNula) {
        return fecha == null ? textoSiNula : fecha.format(FORMATO_FECHA);
    }
}

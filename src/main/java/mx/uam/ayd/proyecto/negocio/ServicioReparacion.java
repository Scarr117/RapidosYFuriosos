package mx.uam.ayd.proyecto.negocio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.uam.ayd.proyecto.datos.ReparacionRepository;
import mx.uam.ayd.proyecto.datos.ReparacionRepository.VehiculosPendientesDTO;
import mx.uam.ayd.proyecto.negocio.modelo.Reparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Cotizacion;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la entidad Reparacion
 * Maneja la lógica de negocio, incluyendo la validación de Control de Calidad (HU-40)
 * 
 * @author Erik LIE04
 */
@Service
public class ServicioReparacion {

    private static final Logger log = LoggerFactory.getLogger(ServicioReparacion.class);
    
    private final ReparacionRepository reparacionRepository;
    
    // Inyección de dependencias por constructor
    @Autowired
    public ServicioReparacion(ReparacionRepository reparacionRepository) {
        this.reparacionRepository = reparacionRepository;
    }

    /**
     * Recupera una reparación por su ID.
     * 
     */
    public Reparacion recuperarReparacion(int idReparacion) {
        Optional<Reparacion> opcional = reparacionRepository.findById(idReparacion);
        if(opcional.isPresent()) {
            return opcional.get();
        }
        throw new IllegalArgumentException("No se encontró la reparación con ID: " + idReparacion);
    }


    /*
    HU-06 Crear Reparacion con su fallas O
    @author Oscar Hinojosa
    */
    @Transactional
    public Reparacion crearNuevaReparacionConFallas(String codigosFalla, int mesesGarantia, Cotizacion cotizacion) {
        
        if (codigosFalla == null || codigosFalla.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar los códigos de falla detectados.");
        }
        if (cotizacion == null) {
            throw new IllegalArgumentException("Debe seleccionar una cotizacion.");
        }

        // Definimos los estatus que se consideran "Reparación Cerrada/Finalizada"
        List<String> estatusFinalizados = List.of("Entregado", "En espera", "Listo paara entrega");

        // Validamos si el carro ya tiene un proceso activo en el taller
        boolean tieneReparacionActiva = reparacionRepository.existsByCotizacionAndEstatusServicioIn(cotizacion, estatusFinalizados);

        if (tieneReparacionActiva) {
            throw new IllegalArgumentException("La cotizacion ya tiene un proceso de reparación activo en el taller");
        }

        // Crear la nueva entidad Reparacion
        Reparacion nuevaReparacion = new Reparacion();
        nuevaReparacion.setEstatusServicio("En espera");
        nuevaReparacion.setCotizacion(cotizacion);
        nuevaReparacion.setFechaInicio(LocalDateTime.now());
        nuevaReparacion.setVehiculo(cotizacion.getCita().getVehiculo());
        

        // Guardar las fallas directamente en las observaciones técnicas
        nuevaReparacion.setObservacionesTecnicas(codigosFalla);

        nuevaReparacion.setGarantiaMeses(mesesGarantia);

        // Guardar en la base de datos
        return reparacionRepository.save(nuevaReparacion);
    }

    /**
     * Escenario 1 (HU-40): Registra un escaneo limpio y avanza el estado.
     */
    public Reparacion procesarEscaneoLimpio(int idReparacion) {
        Reparacion reparacion = recuperarReparacion(idReparacion);
        
        // Modificación realizada por Erik para la HU-40 (Control de Calidad)
        // Regla de Negocio: Pasa a estado "Listo para entrega" al verificar escaneo limpio, conectando con la HU-42
        reparacion.setEstatusServicio("Listo para entrega");
        
        // Se añade nota a las observaciones para mantener un historial en la BD
        String notasActuales = reparacion.getObservacionesTecnicas() != null ? reparacion.getObservacionesTecnicas() : "";
        reparacion.setObservacionesTecnicas(notasActuales + " | [Control de Calidad: Escaneo Limpio Exitoso]");
        
        return reparacionRepository.save(reparacion);
    }

    /**
     * Escenario 2 y 3 (HU-40): Registra fallas persistentes y retrocede el estado.
     * Adaptado para persistencia en String sin entidades extra.
     */
    public Reparacion procesarFallasPersistentes(int idReparacion, String codigosFalla) {
        Reparacion reparacion = recuperarReparacion(idReparacion);
        
        if (codigosFalla == null || codigosFalla.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar los códigos de falla detectados.");
        }

        // 1. Separamos el texto ingresado por el mecánico usando comas
        String[] nuevasFallas = codigosFalla.split(",");
        StringBuilder fallasAProcesar = new StringBuilder();
        
        // 2. Limpiamos y etiquetamos cada falla nueva individualmente
        for (int i = 0; i < nuevasFallas.length; i++) {
            String fallaLimpia = nuevasFallas[i].trim();
            
            if (!fallaLimpia.isEmpty()) {
                // Etiquetamos la falla para que destaque en el CheckBox
                fallasAProcesar.append(fallaLimpia).append(" [Falla detectada en Escáner]");
                
                // Agregamos la coma separadora, excepto en el último elemento
                if (i < nuevasFallas.length - 1) {
                    fallasAProcesar.append(", ");
                }
            }
        }

        // 3. Concatenamos con las fallas anteriores usando una coma como puente
        String fallasActuales = reparacion.getObservacionesTecnicas();
        if (fallasActuales == null || fallasActuales.trim().isEmpty()) {
            reparacion.setObservacionesTecnicas(fallasAProcesar.toString());
        } else {
            // La coma aquí es vital para que la UI separe las viejas de las nuevas al usar .split(",")
            reparacion.setObservacionesTecnicas(fallasActuales + ", " + fallasAProcesar.toString());
        }

        // 4. Regla de Negocio: Bloqueo de entrega, regresa a revisión para que el mecánico lo atienda.
        reparacion.setEstatusServicio("En espera"); 
        
        return reparacionRepository.save(reparacion);
    }


    /*  
    Obtener vehiculos pendientes para la lista (HU-42)
     */
    public List<VehiculosPendientesDTO> obtenerVehiculosParaEntrega() {
        return reparacionRepository.findVehiculosActivos();
    }
    
    // Cambiar Estatus servicio de los vehiculos con 'Listo para entrega' a 'Entregado' (HU-42)
    public boolean marcarEntregado(Integer idReparacion){
        Optional<Reparacion> reparacionOpt = reparacionRepository.findById(idReparacion);

        if(reparacionOpt.isPresent()) {
            Reparacion reparacion = reparacionOpt.get();
            
            // Verificamos que ya haya pasado por el control de calidad
            if(reparacion.getEstatusServicio().equalsIgnoreCase("Listo para entrega")){
               reparacion.setEstatusServicio("Entregado");
               reparacionRepository.save(reparacion);
               return true;
            }
        }
        return false;
    }

    // INICIO Modificación realizada por Erik para la HU-34 (Notificación de Atraso)
    /**
     * Obtiene todos los vehículos en proceso que son susceptibles de sufrir un atraso.
     */
    public List<Reparacion> obtenerReparacionesParaAtraso() {
        return reparacionRepository.findReparacionesParaAtraso();
    }
    // FIN Modificación Erik HU-34
}
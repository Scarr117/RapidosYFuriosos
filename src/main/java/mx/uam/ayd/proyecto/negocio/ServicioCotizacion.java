package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uam.ayd.proyecto.datos.CotizacionRepository;
import mx.uam.ayd.proyecto.datos.CotizacionRepository.CotizacionAprobadaDTO;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Cotizacion;
import mx.uam.ayd.proyecto.negocio.modelo.CotizacionConcepto;
import mx.uam.ayd.proyecto.negocio.modelo.Refaccion;


@Service
@Scope("prototype")
public class ServicioCotizacion {

    private static final Logger log = LoggerFactory.getLogger(ServicioCotizacion.class);

    private final CotizacionRepository cotizacionRepository;

    

    private Cotizacion cotizacion;

    @Autowired
    public ServicioCotizacion(CotizacionRepository cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }


    public boolean crearCotizacionBorrador(Cita cita) {
    // 1. Validar que la cita recibida no sea nula
    if (cita == null) {
        return false;
    }

    // 2. Instanciar un nuevo objeto Cotizacion en memoria
    this.cotizacion = new Cotizacion();

    // 3. Relacionar la cotización con la cita seleccionada
    this.cotizacion.setCita(cita);

    return true;
    }


    public boolean agregarRefaccionACotizacionBorrador(Refaccion seleccionada, int cantidad) {
    // 1. Verificamos que exista un borrador activo en memoria
    if (this.cotizacion == null) {
        return false; 
    }
    
    // 2. Verificamos que los datos de entrada sean válidos
    if (seleccionada == null || cantidad <= 0) {
        return false;
    }

    // 3. Instanciamos el concepto pasándole su cotización padre (this.cotizacion)
    CotizacionConcepto nuevoConcepto = new CotizacionConcepto(seleccionada, cantidad, this.cotizacion);
    
    // 4. Lo agregamos a la lista de la cotización en memoria
    this.cotizacion.getConceptos().add(nuevoConcepto);
    
    return true;
    }

    
    public boolean capturarDatosServicio(float costoManoObra) {
        
        if (this.cotizacion == null) {
            return false;
        }

    // Usamos setters
        
    if (costoManoObra >= 0) {
        this.cotizacion.setManoObraCosto(costoManoObra);
    } else {
        this.cotizacion.setManoObraCosto(0.0f);
    }

    return true;
    }


    public boolean finalizarCotizacion(String diagnosticoInicial) {
    // Verificamos que el borrador exista
    if (this.cotizacion == null) {
        return false;
    }

    // Calcular el total de las refacciones
    float totalRefacciones = 0.0f;
    for (CotizacionConcepto concepto : this.cotizacion.getConceptos()) {
        totalRefacciones += (concepto.getRefaccion().getPrecio() * concepto.getCantidad());
    }

    // Calcular subtotal, IVA y Total
    float subtotal = totalRefacciones + this.cotizacion.getManoObraCosto();
    float iva = subtotal * 0.16f; // Asumiendo un IVA estándar del 16%
    float totalFinal = subtotal + iva;

    // Asignamos el total final a la entidad
    this.cotizacion.setCostoTotal(totalFinal);

    //Asignamos el diagnostico inicial
    this.cotizacion.setdiagnosticoInicial(diagnosticoInicial);

    //Guardae cotizacion
    cotizacionRepository.save(this.cotizacion); 

    // Limpiar el borrador en memoria (Destrucción del estado)
    this.cotizacion = null;
    
    return true;
}

//HU-06 Recuperamos la cotizacion para generar la reparacion
    public Cotizacion obtenerCotizacionporID(Long idCotizacion){
        Cotizacion cotizacion = cotizacionRepository.findCotizacionConDetalles(idCotizacion);
        
        if (cotizacion == null) {
            throw new IllegalArgumentException("La cotización no existe");
        }
        
        return cotizacion;

    }
    
    public List<CotizacionAprobadaDTO> obtenerCotizacionesAprobadas(){
       return cotizacionRepository.findCotizacionesAprobadas(); 
    }


}

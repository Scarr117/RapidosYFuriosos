package mx.uam.ayd.proyecto.presentacion.registrarReparacion;

import mx.uam.ayd.proyecto.negocio.ServicioReparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
//import mx.uam.ayd.proyecto.negocio.modelo.CotizacionConcepto;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Refaccion;
import mx.uam.ayd.proyecto.negocio.modelo.Cotizacion;
import mx.uam.ayd.proyecto.datos.CotizacionRepository.CotizacionAprobadaDTO;
import mx.uam.ayd.proyecto.negocio.ServicioCotizacion;
import mx.uam.ayd.proyecto.negocio.ServicioReparacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ControlReparacion {

    @Autowired private ServicioReparacion servicioReparacion;
    //@Autowired private ServicioCliente servicioCliente;
    //@Autowired private ServicioVehiculo servicioVehiculo;
    //@Autowired private ServicioCita servicioCita;
    //@Autowired private ServicioRefaccion servicioRefaccion;
    @Autowired private ServicioCotizacion servicioCotizacion;

    
    @Autowired private VistaReparacion vista;
    private Cotizacion cotizacionSeleccionada;


    public void inicia() {
        vista.iniciarVisualizacion(this);
        buscarCotizacionAprobada();
    }

    public void buscarCotizacionAprobada() {
        List<CotizacionAprobadaDTO> cotizaciones = servicioCotizacion.obtenerCotizacionesAprobadas();
        vista.mostrarCotizaciones(cotizaciones);
    }

    public void onCotizacionseleccionada(Long idCotizacion) {
        this.cotizacionSeleccionada = servicioCotizacion.obtenerCotizacionporID(idCotizacion);

        Cliente cliente = cotizacionSeleccionada.getCita().getVehiculo().getCliente();
        Vehiculo vehiculo = cotizacionSeleccionada.getCita().getVehiculo();

        String nombre = cliente.getNombre();
        String telefono = cliente.getTelefono();
        String marca = vehiculo.getMarca();
        String modelo = vehiculo.getModelo();
        int anio = vehiculo.getAnio();
        String placas = vehiculo.getPlacas();
        String diagnostico = cotizacionSeleccionada.getdiagnosticoInicial(); 

        vista.mostrarInfoDetallada(nombre, telefono, marca, modelo, anio, placas, diagnostico);

        vista.permitirEdicion();

    }


    public void onGuardarClick(String fallas, int mesesGarantia) {
        
            // Asegurar la mano de obra en el borrador de cotización
            if (this.cotizacionSeleccionada == null) {
                vista.mostrarMensajeError("Error al registrar la mano de obra en la cotización.");
                return;
            }

            // Crear la Reparación con sus fallas
            servicioReparacion.crearNuevaReparacionConFallas(fallas, mesesGarantia, this.cotizacionSeleccionada);
            
            vista.mostrarMensajeExito();
    }
}
            

package mx.uam.ayd.proyecto.presentacion.registrarCotizacion;

import mx.uam.ayd.proyecto.negocio.ServicioReparacion;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.CotizacionConcepto;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Refaccion;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.ServicioVehiculo;
import mx.uam.ayd.proyecto.negocio.ServicioCita;
import mx.uam.ayd.proyecto.negocio.ServicioRefaccion;
import mx.uam.ayd.proyecto.negocio.ServicioCotizacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ControlCotizacion {

    private final ServicioReparacion servicioReparacion;
    @Autowired private ServicioCliente servicioCliente;
    @Autowired private ServicioVehiculo servicioVehiculo;
    @Autowired private ServicioCita servicioCita;
    @Autowired private ServicioRefaccion servicioRefaccion;
    @Autowired private ServicioCotizacion servicioCotizacion;
    private float totalRefacciones = 0.0f;
    private float costoManoObra = 0.0f;
    
    @Autowired private VistaCotizacion vista;

    ControlCotizacion(ServicioReparacion servicioReparacion) {
        this.servicioReparacion = servicioReparacion;
    }

    public void iniciar() {
        vista.iniciarVisualizacion(this);
        buscarClientes();
    }

    public void buscarClientes() {
        List<Cliente> clientesDisponibles = servicioCliente.getClientes();
        vista.mostrarClientes(clientesDisponibles);
    }

    public void onClienteSeleccionado(Cliente cliente) {
        if (cliente != null) {
            List<Vehiculo> vehiculosDelCliente = servicioVehiculo.getVehiculosCliente(cliente.getIdCliente());
            vista.mostrarVehiculos(vehiculosDelCliente);
        }
    }

    public void onVehiculoSeleccionado(Vehiculo vehiculoSeleccionado) {
        if (vehiculoSeleccionado != null) {
            Cita cita = servicioCita.obtenerCitaPendientePorVehiculo(vehiculoSeleccionado);
            
            if (cita != null) {
                boolean exito = servicioCotizacion.crearCotizacionBorrador(cita);
                if (exito) {
                    vista.permitirEdicion();
                }
            } else {
                vista.mostrarMensajeError("El vehículo no tiene una cita pendiente.");
                vista.bloquearEdicion();
                
            }
        }
    }

    public void onBuscarRefaccionClick(Integer idPieza) {
        List<Refaccion> encontrada = servicioRefaccion.buscarRefaccion(idPieza);
        vista.mostrarRefaccion(encontrada);
    }

    public float calcularSubtotal() {
        return totalRefacciones + costoManoObra;
    }

    public float calcularIva() {
        return calcularSubtotal() * 0.16f; 
    }

    public float calcularTotal() {
        return calcularSubtotal() + calcularIva();
    }

    public void recalcularTotales() {
        float subtotal = calcularSubtotal();
        float iva = calcularIva();
        float total = calcularTotal();
        vista.actualizarEtiquetasTotales(subtotal, iva, total); 
    }

    public void onAgregarRefaccion(Refaccion seleccionada, int cantidad) {
        if (seleccionada != null) {
            boolean exito = servicioCotizacion.agregarRefaccionACotizacionBorrador(seleccionada, cantidad);
            if (exito) {
                float costoDeEstaPieza = seleccionada.getPrecio() * cantidad;
                this.totalRefacciones = this.totalRefacciones + costoDeEstaPieza;

                recalcularTotales();

            } 
        }else {
            vista.mostrarMensajeError("Seleccione una refaccion");
        }
    }

    public void onActualizarServicio(float costoManoObra) {
        boolean exito = servicioCotizacion.capturarDatosServicio(costoManoObra);
        if (exito) {

            this.costoManoObra = costoManoObra;
            recalcularTotales();
        }
    }


    public void onGuardarClick(String diagnostico, Vehiculo vehiculoSeleccionado) {
        try {
            // Asegurar la mano de obra en el borrador de cotización
            boolean manoObraRegistrada = servicioCotizacion.capturarDatosServicio(this.costoManoObra);
            if (!manoObraRegistrada) {
                vista.mostrarMensajeError("Error al registrar la mano de obra en la cotización.");
                return;
            }

            // Crear la Reparación con sus fallas
        //   servicioReparacion.crearNuevaReparacionConFallas(fallas, vehiculoSeleccionado);

            // 3. Finalizar la cotización
            boolean exitoCotizacion = servicioCotizacion.finalizarCotizacion(diagnostico);
            
            if (exitoCotizacion) {
                vista.mostrarMensajeExito();
            } else {
                vista.mostrarMensajeError("No se pudo finalizar la cotización.");
            }

        } catch (IllegalArgumentException e) {
            // Muestra la regla de negocio violada (ej. "El vehículo ya tiene una reparación activa")
            vista.mostrarMensajeError(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            vista.mostrarMensajeError("Ocurrió un error inesperado al guardar en la base de datos.");
        }
    }
}
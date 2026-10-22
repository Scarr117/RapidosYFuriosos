package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Entidad que representa la reparacion de un vehiculo en el taller
 * 
 * @author Erik LIE04
 */
@Entity
public class Reparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idReparacion;

    /** Cuando el mecanico le empieza a meter mano al carro */
    private LocalDateTime fechaInicio;
    
    /** Cuando queda listo y reparado */
    private LocalDateTime fechaFin;
    
    /** Para saber si esta en proceso pausado o terminado */
    private String estatusServicio;
    
    /** Notas que deja el mecanico sobre lo que le hizo al auto */
    // INICIO Modificación realizada por Erik para la HU-40 (Control de Calidad)
    // Se aumenta el límite de caracteres a 1000 para evitar el error "Value too long" al guardar múltiples fallas.
    @Column(length = 1000)
    private String observacionesTecnicas;
    // FIN Modificación Erik HU-40
    
    /** Cuantos meses de garantia tiene el cliente por este trabajo */
    private int garantiaMeses;
    
    /** Las letras chiquitas de lo que cubre la garantia */
    private String condicionesGarantia;

    /** 
     * Relación con el vehículo que se está reparando.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

    /*
    Relacion con la cotizacion que genero la reparacion
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cotizacion")
    private Cotizacion cotizacion;

    /** 
     * Bitácora de notificaciones de atraso enviadas al cliente.
     */
    @OneToMany(mappedBy = "reparacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistorialNotificacion> historialNotificaciones = new ArrayList<>();

    /** CONSTRUCTORES
     * Constructor vacio que nos pide Spring para armar el objeto
     */
    public Reparacion() {
    }

    /** GETTERS Y SETTERS
     * Obtiene el ID de la reparacion
     * @return idReparacion
     */
    public int getIdReparacion() {
        return idReparacion;
    }

    /**
     * Establece el ID de la reparacion
     * @param idReparacion el nuevo identificador
     */
    public void setIdReparacion(int idReparacion) {
        this.idReparacion = idReparacion;
    }

    /**
     * Obtiene la fecha en la que inicio el trabajo
     * @return fechaInicio
     */
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Asigna la fecha de inicio
     * @param fechaInicio momento en que arranco
     */
    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene la fecha en la que se termino todo
     * @return fechaFin
     */
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    /**
     * Asigna la fecha de termino
     * @param fechaFin momento en que quedo listo
     */
    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Obtiene en que estado va la reparacion
     * @return estatusServicio
     */
    public String getEstatusServicio() {
        return estatusServicio;
    }

    /**
     * Actualiza el estado de la reparacion
     * @param estatusServicio el nuevo estado
     */
    public void setEstatusServicio(String estatusServicio) {
        this.estatusServicio = estatusServicio;
    }

    /**
     * Obtiene las notas del mecanico
     * @return observacionesTecnicas
     */
    public String getObservacionesTecnicas() {
        return observacionesTecnicas;
    }

    /**
     * Guarda las notas del mecanico
     * @param observacionesTecnicas el texto con los detalles
     */
    public void setObservacionesTecnicas(String observacionesTecnicas) {
        this.observacionesTecnicas = observacionesTecnicas;
    }

    /**
     * Obtiene el tiempo de garantia
     * @return garantiaMeses
     */
    public int getGarantiaMeses() {
        return garantiaMeses;
    }

    /**
     * Asigna los meses que dura la garantia
     * @param garantiaMeses cantidad de meses
     */
    public void setGarantiaMeses(int garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }

    /**
     * Obtiene las reglas de la garantia
     * @return condicionesGarantia
     */
    public String getCondicionesGarantia() {
        return condicionesGarantia;
    }

    /**
     * Asigna las reglas para que aplique la garantia
     * @param condicionesGarantia texto explicativo
     */
    public void setCondicionesGarantia(String condicionesGarantia) {
        this.condicionesGarantia = condicionesGarantia;
    }



    /**
     * @return el vehiculo asociado a esta reparacion
     */
    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    /**
     * @param vehiculo el vehiculo a asociar
     */
    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    /**
     * @return la cotizacion asociada a esta reparacion
     */
    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    /**
     * @param cotizacion la cotizacion a asociar
     */
    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }

    /**
     * Obtiene el historial de notificaciones de atraso
     * @return historialNotificaciones
     */
    public List<HistorialNotificacion> getHistorialNotificaciones() {
        return historialNotificaciones;
    }

    /**
     * Asigna el historial de notificaciones
     * @param historialNotificaciones lista de notificaciones enviadas
     */
    public void setHistorialNotificaciones(List<HistorialNotificacion> historialNotificaciones) {
        this.historialNotificaciones = historialNotificaciones;
    }

    /**
     * Metodo de ayuda para agregar un registro al historial de notificaciones
     * y mantener la consistencia bidireccional en memoria.
     * @param notificacion el registro a agregar
     */
    public void addHistorialNotificacion(HistorialNotificacion notificacion) {
        this.historialNotificaciones.add(notificacion);
        notificacion.setReparacion(this);
    }
}
package mx.uam.ayd.proyecto.datos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Reparacion;
import org.springframework.data.repository.query.Param;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;
import mx.uam.ayd.proyecto.negocio.modelo.Cotizacion;
import java.util.List;

/**
 * Repositorio para la entidad Reparacion
 * Maneja las operaciones de persistencia en la base de datos
 * @author Erik LIE04
 */
public interface ReparacionRepository extends CrudRepository<Reparacion, Integer> {
    
    /*
     Metodo para inicializar el Objeto de Transferencia de Datos entre las entidades Reparacion, Vehiculo y CLiente
    */
    public interface VehiculosPendientesDTO {
        Integer getId();
        Long getIdCotizacion();
        String getNombre();
        String getMarca();
        String getModelo();
        String getPlacas();
        String getEstatusServicio();
    }
    // INICIO Modificación realizada por Irving para la HU-14 (Consultar reparaciones)
    @Query("SELECT r FROM Reparacion r JOIN FETCH r.vehiculo WHERE r.estatusServicio IN :estatus ORDER BY r.fechaInicio")
    List<Reparacion> findEnTallerConVehiculo(@Param("estatus") List<String> estatus);
    // FIN Modificación Irving HU-14
    
    /*
    Metodo para hacer JOIN de las tres entidades
    */
    @Query("SELECT r.idReparacion AS id, " +
           "coti.idCotizacion AS idCotizacion, " + 
           "cli.nombre AS nombre, " +
           "v.marca AS marca, " +
           "v.modelo AS modelo, " +
           "v.placas AS placas, " +
           "r.estatusServicio AS estatusServicio " +
           "FROM Reparacion r " +
           "LEFT JOIN r.cotizacion coti " +  
           "JOIN r.vehiculo v " +            
           "JOIN v.cliente cli " +      
           "WHERE r.estatusServicio IN ('En espera', 'Listo para entrega')") 

    /*
    Metodo para devolver la lista
    */
    List<VehiculosPendientesDTO> findVehiculosActivos();
    
    // INICIO Modificación realizada por Erik para la HU-34 (Notificación de Atraso)
    // Regla de negocio: Consultar los vehículos susceptibles a un atraso
    // CORRECCIÓN: Se agrega "JOIN FETCH r.vehiculo" para traer el objeto Vehículo inmediatamente
    // y evitar el error "LazyInitializationException - no Session" en la capa de vista.
    @Query("SELECT r FROM Reparacion r JOIN FETCH r.vehiculo WHERE r.estatusServicio IN ('En espera', 'En reparación', 'En revisión')")
    List<Reparacion> findReparacionesParaAtraso();
    // FIN Modificación Erik HU-34

    // Busca si existe alguna reparación activa para una cotizacion que NO esté 'Entregado' ni 'Terminado'
    //Oscar Hinojosa HU-06
    boolean existsByCotizacionAndEstatusServicioIn(Cotizacion cotizacion, List<String> estatusFinalizados);


}
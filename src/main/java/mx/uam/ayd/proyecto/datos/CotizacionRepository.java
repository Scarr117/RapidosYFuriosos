package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mx.uam.ayd.proyecto.negocio.modelo.Cotizacion;
import java.util.List;
import org.springframework.data.jpa.repository.Query;


public interface CotizacionRepository extends CrudRepository<Cotizacion, Long> {
    // Spring Boot se encarga de implementar los metodos basicos (save, findById, findAll, etc.)
    // Si despues necesitamos buscar pedidos por estatus o fecha, lo agregamos aqui

    public interface CotizacionAprobadaDTO {
        Long getIdCotizacion();
        String getNombre();
        String getModelo();
        String getEstadoAprobacion();
    }

    @Query("SELECT c.idCotizacion AS idCotizacion, " +
    "cli.nombre AS nombre, " +
    "v.modelo AS modelo, " +
    "c.estadoAprobacion AS estadoAprobacion " +
    "FROM Cotizacion c " +
    "JOIN c.cita ci " +
    "JOIN ci.vehiculo v " +
    "JOIN v.cliente cli " +
    "WHERE c.estadoAprobacion IN ('Aprobada')" )

    List<CotizacionAprobadaDTO> findCotizacionesAprobadas();

    @Query("SELECT c FROM Cotizacion c " +
           "JOIN FETCH c.cita ci " +
           "JOIN FETCH ci.vehiculo v " +
           "JOIN FETCH v.cliente cli " +
           "WHERE c.idCotizacion = :id")
    Cotizacion findCotizacionConDetalles(@Param("id") Long id);


}
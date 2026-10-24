package mx.uam.ayd.proyecto.datos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.Vehiculo;

public interface CitaRepository extends CrudRepository<Cita, Long> {
    
    public List<Cita> findAll();

    // Devuelve una lista por si el cliente tiene historial de múltiples citas
    public List<Cita> findByClienteNombre(String nombre);

    public List<Cita> findByClienteNombreContainingIgnoreCase(String nombre);

    public List<Cita> findByVehiculoPlacasContainingIgnoreCase(String placas);

    public List<Cita> findByClienteTelefonoContaining(String telefono);

    public List<Cita> findByFecha(LocalDate fecha);

    public List<Cita> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    public boolean existsByFechaAndHora(LocalDate fecha, LocalTime hora);

    Cita findByVehiculo(Vehiculo vehiculo);
}
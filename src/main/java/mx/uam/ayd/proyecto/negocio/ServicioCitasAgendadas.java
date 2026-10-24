package mx.uam.ayd.proyecto.negocio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.datos.CitaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;

/**
 * Servicio de negocio para consultar las citas agendadas (HU-01).
 */
@Service
public class ServicioCitasAgendadas {

    @Autowired
    private CitaRepository citaRepository;

    /**
     * Obtiene las citas agendadas para el día de hoy ordenadas por hora.
     *
     * @return Lista de citas de hoy
     */
    public List<Cita> obtenerCitasHoy() {
        List<Cita> citasHoy = new ArrayList<>(citaRepository.findByFecha(LocalDate.now()));
        citasHoy.sort(Comparator.comparing(Cita::getHora, Comparator.nullsLast(Comparator.naturalOrder())));
        return citasHoy;
    }

    /**
     * Obtiene las citas agendadas para la semana actual (de lunes a domingo).
     *
     * @return Lista de citas de la semana
     */
    public List<Cita> obtenerCitasSemanales() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        List<Cita> citasSemana = new ArrayList<>(citaRepository.findByFechaBetween(inicioSemana, finSemana));
        citasSemana.sort(Comparator
                .comparing(Cita::getFecha, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Cita::getHora, Comparator.nullsLast(Comparator.naturalOrder())));
        return citasSemana;
    }

    /**
     * Obtiene todas las citas registradas en el sistema.
     *
     * @return Lista con todas las citas
     */
    public List<Cita> obtenerCitasTotales() {
        List<Cita> citasTotales = new ArrayList<>(citaRepository.findAll());
        citasTotales.sort(Comparator
                .comparing(Cita::getFecha, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Cita::getHora, Comparator.nullsLast(Comparator.naturalOrder())));
        return citasTotales;
    }

    /**
     * Busca citas filtrando por un atributo (nombre del cliente, placas o teléfono).
     *
     * @param criterio Criterio indicado por la barra de búsqueda (cliente, placas o telefono)
     * @param atributo Valor de búsqueda ingresado por el mecánico
     * @return Lista de citas que coinciden con el atributo buscado
     */
    public List<Cita> obtenerCitasAtributo(String criterio, String atributo) {
        if (atributo == null || atributo.trim().isEmpty()) {
            return obtenerCitasTotales();
        }

        String valor = atributo.trim();
        String tipo = identificarTipoAtributo(criterio, valor);

        if ("PLACAS".equals(tipo)) {
            return citaRepository.findByVehiculoPlacasContainingIgnoreCase(valor);
        } else if ("TELEFONO".equals(tipo)) {
            String telefonoLimpio = valor.replace("-", "").replace(" ", "");
            return citaRepository.findByClienteTelefonoContaining(telefonoLimpio);
        } else {
            return citaRepository.findByClienteNombreContainingIgnoreCase(valor);
        }
    }

    /**
     * Identifica el tipo de atributo a buscar (NOMBRE, PLACAS o TELEFONO).
     *
     * @param criterio Campo de búsqueda utilizado
     * @param atributo Texto ingresado
     * @return Tipo de atributo identificado
     */
    private String identificarTipoAtributo(String criterio, String atributo) {
        if (criterio != null) {
            String crit = criterio.trim().toLowerCase();
            if (crit.contains("placa")) {
                return "PLACAS";
            }
            if (crit.contains("telefono") || crit.contains("teléfono")) {
                return "TELEFONO";
            }
        }

        // Si se ingresó directamente en el buscador general, identifica por formato
        if (atributo.matches("[0-9\\-\\s]+")) {
            return "TELEFONO";
        }
        if (atributo.matches(".*[A-Za-z].*") && atributo.matches(".*[0-9].*")) {
            return "PLACAS";
        }

        return "NOMBRE";
    }
}

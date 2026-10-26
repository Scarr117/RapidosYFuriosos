package mx.uam.ayd.proyecto.negocio.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del agrupamiento de estatus de reparación (HU-14).
 */
class EstadoReparacionTest {

    @Test
    @DisplayName("Los estatus de trabajo en curso se agrupan como EN_CURSO")
    void testEstatusEnCurso() {
        assertEquals(Optional.of(EstadoReparacion.EN_CURSO), EstadoReparacion.desdeEstatus("En reparación"));
        assertEquals(Optional.of(EstadoReparacion.EN_CURSO), EstadoReparacion.desdeEstatus("En revisión"));
    }

    @Test
    @DisplayName("'En espera' se agrupa como EN_ESPERA")
    void testEstatusEnEspera() {
        assertEquals(Optional.of(EstadoReparacion.EN_ESPERA), EstadoReparacion.desdeEstatus("En espera"));
    }

    @Test
    @DisplayName("'Listo para entrega' se agrupa como LISTO")
    void testEstatusListo() {
        assertEquals(Optional.of(EstadoReparacion.LISTO), EstadoReparacion.desdeEstatus("Listo para entrega"));
    }

    @Test
    @DisplayName("La comparación ignora mayúsculas y espacios sobrantes")
    void testIgnoraMayusculasYEspacios() {
        assertEquals(Optional.of(EstadoReparacion.LISTO), EstadoReparacion.desdeEstatus("  LISTO para ENTREGA  "));
    }

    @Test
    @DisplayName("Los vehículos ya entregados, estatus desconocidos o nulos no pertenecen a ningún estado")
    void testEstatusFueraDelTaller() {
        assertTrue(EstadoReparacion.desdeEstatus("Entregado").isEmpty());
        assertTrue(EstadoReparacion.desdeEstatus("Terminado").isEmpty());
        assertTrue(EstadoReparacion.desdeEstatus("cualquier cosa").isEmpty());
        assertTrue(EstadoReparacion.desdeEstatus(null).isEmpty());
    }

    @Test
    @DisplayName("estatusEnTaller incluye los estatus vigentes y excluye los ya entregados")
    void testEstatusEnTaller() {
        List<String> estatus = EstadoReparacion.estatusEnTaller();

        assertTrue(estatus.contains("En reparación"));
        assertTrue(estatus.contains("En revisión"));
        assertTrue(estatus.contains("En espera"));
        assertTrue(estatus.contains("Listo para entrega"));
        assertFalse(estatus.contains("Entregado"));
        assertFalse(estatus.contains("Terminado"));
    }
}

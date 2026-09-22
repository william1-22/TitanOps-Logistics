package com.titanops.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.dao.RutaDestinoDAO;
import com.titanops.modelo.RutaDestino;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class GestionRutasControllerTest {

    @Test
    void creaRutaPlanificadaConDatosNormalizados() {
        RutaDAODoble dao = new RutaDAODoble();
        GestionRutasController controller = new GestionRutasController(dao);

        var resultado = controller.crearRuta(
                "  Proyecto   Norte ", " San   Salvador ", " Santa Ana ", "72,50");

        assertTrue(resultado.exitoso());
        assertNotNull(dao.insertada);
        assertEquals("Proyecto Norte", dao.insertada.getNombreProyecto());
        assertEquals("San Salvador", dao.insertada.getPuntoOrigen());
        assertEquals(new BigDecimal("72.5"), dao.insertada.getDistanciaKm());
        assertEquals("PLANIFICADA", dao.insertada.getEstado());
    }

    @Test
    void permiteDistanciaOpcional() {
        RutaDAODoble dao = new RutaDAODoble();
        var resultado = new GestionRutasController(dao)
                .crearRuta("Ruta 1", "Origen", "Destino", " ");

        assertTrue(resultado.exitoso());
        assertNull(dao.insertada.getDistanciaKm());
    }

    @Test
    void rechazaDistanciaNegativaOFueraDeRango() {
        GestionRutasController controller =
                new GestionRutasController(new RutaDAODoble());

        assertFalse(controller.crearRuta("R", "O", "D", "-1").exitoso());
        assertFalse(controller.crearRuta("R", "O", "D", "1.123").exitoso());
        assertFalse(controller.crearRuta("R", "O", "D", "123456789").exitoso());
    }

    @Test
    void actualizaRutaSinAsignacionActiva() {
        RutaDAODoble dao = new RutaDAODoble();
        dao.rutaPorId = ruta(8, "PLANIFICADA");
        GestionRutasController controller = new GestionRutasController(dao);

        var resultado = controller.actualizarRuta(
                8, "Ruta editada", "A", "B", "10", "FINALIZADA");

        assertTrue(resultado.exitoso());
        assertEquals("Ruta editada", dao.actualizada.getNombreProyecto());
        assertEquals("FINALIZADA", dao.actualizada.getEstado());
    }

    @Test
    void conservaEnCursoCuandoExistenAsignacionesActivas() {
        RutaDAODoble dao = new RutaDAODoble();
        dao.rutaPorId = ruta(8, "EN_CURSO");
        dao.asignacionesEnCurso = true;
        GestionRutasController controller = new GestionRutasController(dao);

        var resultado = controller.actualizarRuta(
                8, "Ruta", "A", "B", "10", "FINALIZADA");

        assertFalse(resultado.exitoso());
        assertNull(dao.actualizada);
    }

    @Test
    void impideCancelarRutaConAsignacionEnCurso() {
        RutaDAODoble dao = new RutaDAODoble();
        dao.rutaPorId = ruta(8, "EN_CURSO");
        dao.asignacionesEnCurso = true;
        GestionRutasController controller = new GestionRutasController(dao);

        var resultado = controller.cancelarRuta(8);

        assertFalse(resultado.exitoso());
        assertFalse(dao.cancelada);
    }

    @Test
    void cancelaLogicamenteRutaSinAsignacionesActivas() {
        RutaDAODoble dao = new RutaDAODoble();
        dao.rutaPorId = ruta(8, "PLANIFICADA");
        GestionRutasController controller = new GestionRutasController(dao);

        var resultado = controller.cancelarRuta(8);

        assertTrue(resultado.exitoso());
        assertTrue(dao.cancelada);
    }

    @Test
    void filtraRutasPorEstadoYTexto() {
        RutaDAODoble dao = new RutaDAODoble();
        dao.rutas = List.of(
                new RutaDestino(1, "Hospital Norte", "San Salvador", "Apopa",
                        new BigDecimal("12"), "PLANIFICADA"),
                new RutaDestino(2, "Bodega Sur", "Soyapango", "Olocuilta",
                        new BigDecimal("30"), "FINALIZADA"));
        GestionRutasController controller = new GestionRutasController(dao);

        assertEquals(1, controller.listarRutas("hospital", "PLANIFICADA").size());
        assertEquals("PLANIFICADA", dao.ultimoEstadoListado);
        assertEquals(2, controller.listarRutas("", "TODOS").size());
    }

    private static RutaDestino ruta(int id, String estado) {
        return new RutaDestino(id, "Ruta", "A", "B", new BigDecimal("10"), estado);
    }

    private static class RutaDAODoble extends RutaDestinoDAO {
        private RutaDestino insertada;
        private RutaDestino actualizada;
        private RutaDestino rutaPorId;
        private boolean asignacionesEnCurso;
        private boolean cancelada;
        private String ultimoEstadoListado;
        private List<RutaDestino> rutas = List.of();

        @Override
        public boolean insertar(RutaDestino ruta) {
            insertada = ruta;
            ruta.setIdRuta(20);
            return true;
        }

        @Override
        public boolean actualizar(RutaDestino ruta) {
            actualizada = ruta;
            return true;
        }

        @Override
        public RutaDestino obtenerPorId(int id) {
            return rutaPorId;
        }

        @Override
        public boolean tieneAsignacionesEnCurso(int idRuta) {
            return asignacionesEnCurso;
        }

        @Override
        public boolean eliminar(int id) {
            cancelada = true;
            return true;
        }

        @Override
        public List<RutaDestino> listarTodos() {
            return rutas;
        }

        @Override
        public List<RutaDestino> listarPorEstado(String estado) {
            ultimoEstadoListado = estado;
            return rutas.stream().filter(ruta -> estado.equals(ruta.getEstado())).toList();
        }
    }
}

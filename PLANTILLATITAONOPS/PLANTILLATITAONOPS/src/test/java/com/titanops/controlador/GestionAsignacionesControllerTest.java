package com.titanops.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.controlador.GestionAsignacionesController.MaquinariaOpcion;
import com.titanops.controlador.GestionAsignacionesController.OperadorOpcion;
import com.titanops.controlador.GestionAsignacionesController.RutaOpcion;
import com.titanops.dao.AsignacionDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.dao.OperadorDAO;
import com.titanops.dao.RutaDestinoDAO;
import com.titanops.modelo.Asignacion;
import com.titanops.modelo.Maquinaria;
import com.titanops.modelo.Operador;
import com.titanops.modelo.RutaDestino;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;

class GestionAsignacionesControllerTest {

    @Test
    void creaAsignacionEnCursoConSesionYFechaFutura() {
        AsignacionDAODoble asignacionDAO = new AsignacionDAODoble();
        GestionAsignacionesController controller = controller(asignacionDAO);
        Timestamp retorno = new Timestamp(System.currentTimeMillis() + 86_400_000L);

        var resultado = controller.crearAsignacion(3, maquinariaOpcion(),
                operadorOpcion(), rutaOpcion(), retorno, "  Carga   delicada ");

        assertTrue(resultado.exitoso());
        assertNotNull(asignacionDAO.insertada);
        assertEquals(3, asignacionDAO.insertada.getIdUsuarioRegistro());
        assertEquals("EN_CURSO", asignacionDAO.insertada.getEstadoAsignacion());
        assertEquals("Carga delicada", asignacionDAO.insertada.getObservaciones());
    }

    @Test
    void rechazaCreacionSinUsuarioAutenticado() {
        AsignacionDAODoble dao = new AsignacionDAODoble();
        var resultado = controller(dao).crearAsignacion(null, maquinariaOpcion(),
                operadorOpcion(), rutaOpcion(), futuro(), null);

        assertFalse(resultado.exitoso());
        assertEquals("Inicia sesión para registrar una asignación.", resultado.mensaje());
    }

    @Test
    void rechazaRecursosNoSeleccionados() {
        GestionAsignacionesController controller = controller(new AsignacionDAODoble());

        assertFalse(controller.crearAsignacion(
                1, null, operadorOpcion(), rutaOpcion(), futuro(), null).exitoso());
        assertFalse(controller.crearAsignacion(
                1, maquinariaOpcion(), null, rutaOpcion(), futuro(), null).exitoso());
        assertFalse(controller.crearAsignacion(
                1, maquinariaOpcion(), operadorOpcion(), null, futuro(), null).exitoso());
    }

    @Test
    void rechazaFechaEstimadaPasada() {
        var resultado = controller(new AsignacionDAODoble()).crearAsignacion(
                1, maquinariaOpcion(), operadorOpcion(), rutaOpcion(),
                new Timestamp(System.currentTimeMillis() - 1_000L), null);

        assertFalse(resultado.exitoso());
    }

    @Test
    void listaSoloRecursosActivosYDisponibles() {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        maquinariaDAO.maquinaria = List.of(
                maquinaria(1, true, "DISPONIBLE"),
                maquinaria(2, false, "DISPONIBLE"),
                maquinaria(3, true, "EN_RUTA"));
        OperadorDAODoble operadorDAO = new OperadorDAODoble();
        operadorDAO.operadores = List.of(
                operador(1, true, "DISPONIBLE"),
                operador(2, true, "DESCANSO"));
        GestionAsignacionesController controller = new GestionAsignacionesController(
                new AsignacionDAODoble(), maquinariaDAO, operadorDAO,
                new RutaDAODoble());

        assertEquals(1, controller.listarMaquinariaDisponible().size());
        assertEquals(1, controller.listarOperadoresDisponibles().size());
        assertEquals("DISPONIBLE", maquinariaDAO.ultimoEstadoListado);
    }

    @Test
    void finalizaAsignacionEnCurso() {
        AsignacionDAODoble dao = new AsignacionDAODoble();
        dao.asignacionPorId = asignacion(9, "EN_CURSO");

        var resultado = controller(dao).finalizarAsignacion(9);

        assertTrue(resultado.exitoso());
        assertTrue(dao.finalizada);
        assertNotNull(dao.fechaFinalizacion);
    }

    @Test
    void noFinalizaAsignacionYaCerrada() {
        AsignacionDAODoble dao = new AsignacionDAODoble();
        dao.asignacionPorId = asignacion(9, "FINALIZADA");

        var resultado = controller(dao).finalizarAsignacion(9);

        assertFalse(resultado.exitoso());
        assertFalse(dao.finalizada);
    }

    @Test
    void cancelaAsignacionEnCurso() {
        AsignacionDAODoble dao = new AsignacionDAODoble();
        dao.asignacionPorId = asignacion(9, "EN_CURSO");

        var resultado = controller(dao).cancelarAsignacion(9);

        assertTrue(resultado.exitoso());
        assertTrue(dao.cancelada);
    }

    @Test
    void armaListadoLegibleYFiltraPorProyecto() {
        AsignacionDAODoble asignacionDAO = new AsignacionDAODoble();
        asignacionDAO.asignaciones = List.of(asignacion(9, "EN_CURSO"));
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        maquinariaDAO.maquinaria = List.of(maquinaria(1, true, "EN_RUTA"));
        OperadorDAODoble operadorDAO = new OperadorDAODoble();
        operadorDAO.operadores = List.of(operador(2, true, "EN_RUTA"));
        RutaDAODoble rutaDAO = new RutaDAODoble();
        rutaDAO.rutas = List.of(new RutaDestino(
                3, "Hospital Norte", "A", "B", BigDecimal.TEN, "EN_CURSO"));
        GestionAsignacionesController controller = new GestionAsignacionesController(
                asignacionDAO, maquinariaDAO, operadorDAO, rutaDAO);

        var filas = controller.listarAsignaciones("hospital", "EN_CURSO");

        assertEquals(1, filas.size());
        assertEquals("EQ-01", filas.getFirst().codigoMaquinaria());
        assertEquals("Ana Pérez", filas.getFirst().operador());
        assertEquals("EN_CURSO", asignacionDAO.ultimoEstadoListado);
    }

    private static GestionAsignacionesController controller(AsignacionDAODoble dao) {
        return new GestionAsignacionesController(dao, new MaquinariaDAODoble(),
                new OperadorDAODoble(), new RutaDAODoble());
    }

    private static Timestamp futuro() {
        return new Timestamp(System.currentTimeMillis() + 86_400_000L);
    }

    private static MaquinariaOpcion maquinariaOpcion() {
        return new MaquinariaOpcion(1, "EQ-01", "CAT", "320");
    }

    private static OperadorOpcion operadorOpcion() {
        return new OperadorOpcion(2, "Ana", "Pérez", "01234567-8");
    }

    private static RutaOpcion rutaOpcion() {
        return new RutaOpcion(3, "Hospital", "A", "B");
    }

    private static Asignacion asignacion(int id, String estado) {
        return new Asignacion(id, 1, 2, 3, 4,
                Timestamp.valueOf("2026-09-22 08:00:00"),
                Timestamp.valueOf("2026-09-23 08:00:00"), null, estado, "Prueba");
    }

    private static Maquinaria maquinaria(int id, boolean activa, String estado) {
        return new Maquinaria(id, 1, "EQ-0" + id, "CAT", "320",
                BigDecimal.TEN, BigDecimal.ZERO, estado, activa,
                Timestamp.valueOf("2026-09-22 08:00:00"));
    }

    private static Operador operador(int id, boolean activo, String estado) {
        return new Operador(id, "Ana", "Pérez", "01234567-8", "PESADA",
                "DIURNO", null, estado, activo,
                Timestamp.valueOf("2026-09-22 08:00:00"));
    }

    private static class AsignacionDAODoble extends AsignacionDAO {
        private Asignacion insertada;
        private Asignacion asignacionPorId;
        private List<Asignacion> asignaciones = List.of();
        private boolean finalizada;
        private boolean cancelada;
        private Timestamp fechaFinalizacion;
        private String ultimoEstadoListado;

        @Override
        public boolean insertarConReservaRecursos(Asignacion asignacion) {
            insertada = asignacion;
            asignacion.setIdAsignacion(9);
            return true;
        }

        @Override
        public Asignacion obtenerPorId(int id) {
            return asignacionPorId;
        }

        @Override
        public boolean finalizar(int id, Timestamp fecha) {
            finalizada = true;
            fechaFinalizacion = fecha;
            return true;
        }

        @Override
        public boolean eliminar(int id) {
            cancelada = true;
            return true;
        }

        @Override
        public List<Asignacion> listarTodos() {
            return asignaciones;
        }

        @Override
        public List<Asignacion> listarPorEstado(String estado) {
            ultimoEstadoListado = estado;
            return asignaciones.stream()
                    .filter(item -> estado.equals(item.getEstadoAsignacion())).toList();
        }
    }

    private static class MaquinariaDAODoble extends MaquinariaDAO {
        private List<Maquinaria> maquinaria = List.of();
        private String ultimoEstadoListado;

        @Override
        public List<Maquinaria> listarTodos() {
            return maquinaria;
        }

        @Override
        public List<Maquinaria> listarPorEstado(String estado) {
            ultimoEstadoListado = estado;
            return maquinaria.stream()
                    .filter(item -> estado.equals(item.getEstadoOperativo())).toList();
        }
    }

    private static class OperadorDAODoble extends OperadorDAO {
        private List<Operador> operadores = List.of();

        @Override
        public List<Operador> listarActivos() {
            return operadores.stream().filter(item -> Boolean.TRUE.equals(item.getActivo()))
                    .toList();
        }

        @Override
        public List<Operador> listarTodos() {
            return operadores;
        }
    }

    private static class RutaDAODoble extends RutaDestinoDAO {
        private List<RutaDestino> rutas = List.of();

        @Override
        public List<RutaDestino> listarAsignables() {
            return rutas;
        }

        @Override
        public List<RutaDestino> listarTodos() {
            return rutas;
        }
    }
}

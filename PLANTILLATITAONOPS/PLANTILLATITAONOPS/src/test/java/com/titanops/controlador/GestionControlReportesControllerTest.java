package com.titanops.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.controlador.GestionControlReportesController.MaquinariaOpcion;
import com.titanops.dao.MantenimientoDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.modelo.Mantenimiento;
import com.titanops.modelo.Maquinaria;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;

class GestionControlReportesControllerTest {

    @Test
    void iniciaMantenimientoConDatosNormalizados() {
        MantenimientoDAODoble dao = new MantenimientoDAODoble();
        GestionControlReportesController controller = controller(dao);
        Timestamp salida = futuro();

        var resultado = controller.crearMantenimiento(4, maquinariaOpcion(),
                " preventivo ", salida, "  Cambio   de aceite ", "125,50",
                " Taller   Central ");

        assertTrue(resultado.exitoso());
        assertNotNull(dao.insertado);
        assertEquals(4, dao.insertado.getIdUsuarioRegistro());
        assertEquals("PREVENTIVO", dao.insertado.getTipoMantenimiento());
        assertEquals("Cambio de aceite", dao.insertado.getDiagnostico());
        assertEquals(new BigDecimal("125.5"), dao.insertado.getCosto());
        assertEquals("Taller Central", dao.insertado.getTallerResponsable());
        assertEquals("EN_PROCESO", dao.insertado.getEstadoMantenimiento());
    }

    @Test
    void permiteDatosOpcionales() {
        MantenimientoDAODoble dao = new MantenimientoDAODoble();

        var resultado = controller(dao).crearMantenimiento(4, maquinariaOpcion(),
                "CORRECTIVO", null, " ", "", null);

        assertTrue(resultado.exitoso());
        assertNull(dao.insertado.getFechaSalidaEstimada());
        assertNull(dao.insertado.getDiagnostico());
        assertNull(dao.insertado.getCosto());
        assertNull(dao.insertado.getTallerResponsable());
    }

    @Test
    void rechazaSesionOMaquinariaFaltantes() {
        GestionControlReportesController controller =
                controller(new MantenimientoDAODoble());

        assertFalse(controller.crearMantenimiento(null, maquinariaOpcion(),
                "PREVENTIVO", futuro(), null, null, null).exitoso());
        assertFalse(controller.crearMantenimiento(4, null,
                "PREVENTIVO", futuro(), null, null, null).exitoso());
    }

    @Test
    void rechazaTipoOFechaInvalida() {
        GestionControlReportesController controller =
                controller(new MantenimientoDAODoble());

        assertFalse(controller.crearMantenimiento(4, maquinariaOpcion(),
                "OTRO", futuro(), null, null, null).exitoso());
        assertFalse(controller.crearMantenimiento(4, maquinariaOpcion(),
                "PREVENTIVO", pasado(), null, null, null).exitoso());
    }

    @Test
    void rechazaCostoNegativoOFueraDeNumericDiezDos() {
        GestionControlReportesController controller =
                controller(new MantenimientoDAODoble());

        assertFalse(controller.crearMantenimiento(4, maquinariaOpcion(),
                "PREVENTIVO", futuro(), null, "-1", null).exitoso());
        assertFalse(controller.crearMantenimiento(4, maquinariaOpcion(),
                "PREVENTIVO", futuro(), null, "1.123", null).exitoso());
        assertFalse(controller.crearMantenimiento(4, maquinariaOpcion(),
                "PREVENTIVO", futuro(), null, "123456789", null).exitoso());
    }

    @Test
    void informaFalloSiLaReservaAtomicaNoSeCompleta() {
        MantenimientoDAODoble dao = new MantenimientoDAODoble();
        dao.reservaExitosa = false;

        var resultado = controller(dao).crearMantenimiento(4, maquinariaOpcion(),
                "PREVENTIVO", futuro(), null, null, null);

        assertFalse(resultado.exitoso());
    }

    @Test
    void actualizaMantenimientoEnProcesoConservandoRelaciones() {
        MantenimientoDAODoble dao = new MantenimientoDAODoble();
        dao.mantenimientoPorId = mantenimiento(7, "EN_PROCESO");

        var resultado = controller(dao).actualizarMantenimiento(7,
                "CORRECTIVO", futuro(), "Diagnóstico nuevo", "250", "Taller B");

        assertTrue(resultado.exitoso());
        assertEquals(1, dao.actualizado.getIdMaquinaria());
        assertEquals(4, dao.actualizado.getIdUsuarioRegistro());
        assertEquals("CORRECTIVO", dao.actualizado.getTipoMantenimiento());
        assertEquals(new BigDecimal("2.5E+2"), dao.actualizado.getCosto());
    }

    @Test
    void noEditaMantenimientoCerrado() {
        MantenimientoDAODoble dao = new MantenimientoDAODoble();
        dao.mantenimientoPorId = mantenimiento(7, "FINALIZADO");

        var resultado = controller(dao).actualizarMantenimiento(7,
                "CORRECTIVO", futuro(), null, null, null);

        assertFalse(resultado.exitoso());
        assertNull(dao.actualizado);
    }

    @Test
    void finalizaMantenimientoEnProceso() {
        MantenimientoDAODoble dao = new MantenimientoDAODoble();
        dao.mantenimientoPorId = mantenimiento(7, "EN_PROCESO");

        var resultado = controller(dao).finalizarMantenimiento(7);

        assertTrue(resultado.exitoso());
        assertTrue(dao.finalizado);
        assertNotNull(dao.fechaFinalizacion);
    }

    @Test
    void cancelaMantenimientoEnProceso() {
        MantenimientoDAODoble dao = new MantenimientoDAODoble();
        dao.mantenimientoPorId = mantenimiento(7, "EN_PROCESO");

        var resultado = controller(dao).cancelarMantenimiento(7);

        assertTrue(resultado.exitoso());
        assertTrue(dao.cancelado);
    }

    @Test
    void listaMantenimientosConMaquinariaYFiltros() {
        MantenimientoDAODoble dao = new MantenimientoDAODoble();
        dao.mantenimientos = List.of(mantenimiento(7, "EN_PROCESO"));
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        maquinariaDAO.maquinaria = List.of(maquinaria(1, true, "MANTENIMIENTO"));
        GestionControlReportesController controller =
                new GestionControlReportesController(dao, maquinariaDAO);

        var filas = controller.listarMantenimientos("eq-01", "EN_PROCESO");

        assertEquals(1, filas.size());
        assertEquals("EQ-01", filas.getFirst().codigoMaquinaria());
        assertEquals("CAT 320", filas.getFirst().descripcionMaquinaria());
        assertEquals("EN_PROCESO", dao.ultimoEstadoListado);
    }

    @Test
    void consolidaEstadosYResumenDeMaquinaria() {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        maquinariaDAO.maquinaria = List.of(
                maquinaria(1, true, "DISPONIBLE"),
                maquinaria(2, true, "EN_RUTA"),
                maquinaria(3, true, "MANTENIMIENTO"),
                maquinaria(4, false, "DISPONIBLE"));
        GestionControlReportesController controller =
                new GestionControlReportesController(
                        new MantenimientoDAODoble(), maquinariaDAO);

        var resumen = controller.obtenerResumenControl();

        assertEquals(4, resumen.total());
        assertEquals(1, resumen.disponibles());
        assertEquals(1, resumen.enRuta());
        assertEquals(1, resumen.mantenimiento());
        assertEquals(1, resumen.inactivas());
        assertEquals(1,
                controller.listarEstadoMaquinaria("", "INACTIVA").size());
        assertEquals(1,
                controller.listarEstadoMaquinaria("eq-03", "TODOS").size());
    }

    private static GestionControlReportesController controller(
            MantenimientoDAODoble dao) {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        maquinariaDAO.maquinaria = List.of(maquinaria(1, true, "DISPONIBLE"));
        return new GestionControlReportesController(dao, maquinariaDAO);
    }

    private static MaquinariaOpcion maquinariaOpcion() {
        return new MaquinariaOpcion(1, "EQ-01", "CAT", "320");
    }

    private static Timestamp futuro() {
        return new Timestamp(System.currentTimeMillis() + 86_400_000L);
    }

    private static Timestamp pasado() {
        return new Timestamp(System.currentTimeMillis() - 60_000L);
    }

    private static Mantenimiento mantenimiento(int id, String estado) {
        return new Mantenimiento(id, 1, 4, "PREVENTIVO",
                Timestamp.valueOf("2026-09-22 08:00:00"),
                Timestamp.valueOf("2026-09-24 08:00:00"), null,
                "Cambio de aceite", new BigDecimal("100"), estado, "Taller A");
    }

    private static Maquinaria maquinaria(int id, boolean activa, String estado) {
        return new Maquinaria(id, 1, "EQ-0" + id, "CAT", "320",
                BigDecimal.TEN, BigDecimal.ZERO, estado, activa,
                Timestamp.valueOf("2026-09-22 08:00:00"));
    }

    private static class MantenimientoDAODoble extends MantenimientoDAO {
        private boolean reservaExitosa = true;
        private Mantenimiento insertado;
        private Mantenimiento actualizado;
        private Mantenimiento mantenimientoPorId;
        private List<Mantenimiento> mantenimientos = List.of();
        private boolean finalizado;
        private boolean cancelado;
        private Timestamp fechaFinalizacion;
        private String ultimoEstadoListado;

        @Override
        public boolean insertarConReservaMaquinaria(Mantenimiento mantenimiento) {
            insertado = mantenimiento;
            mantenimiento.setIdMantenimiento(7);
            return reservaExitosa;
        }

        @Override
        public boolean actualizar(Mantenimiento mantenimiento) {
            actualizado = mantenimiento;
            return true;
        }

        @Override
        public Mantenimiento obtenerPorId(int id) {
            return mantenimientoPorId;
        }

        @Override
        public boolean finalizar(int id, Timestamp fechaSalidaReal) {
            finalizado = true;
            fechaFinalizacion = fechaSalidaReal;
            return true;
        }

        @Override
        public boolean eliminar(int id) {
            cancelado = true;
            return true;
        }

        @Override
        public List<Mantenimiento> listarTodos() {
            return mantenimientos;
        }

        @Override
        public List<Mantenimiento> listarPorEstado(String estado) {
            ultimoEstadoListado = estado;
            return mantenimientos.stream()
                    .filter(item -> estado.equals(item.getEstadoMantenimiento()))
                    .toList();
        }
    }

    private static class MaquinariaDAODoble extends MaquinariaDAO {
        private List<Maquinaria> maquinaria = List.of();

        @Override
        public List<Maquinaria> listarTodos() {
            return maquinaria;
        }

        @Override
        public List<Maquinaria> listarPorEstado(String estado) {
            return maquinaria.stream()
                    .filter(item -> estado.equals(item.getEstadoOperativo()))
                    .toList();
        }
    }
}

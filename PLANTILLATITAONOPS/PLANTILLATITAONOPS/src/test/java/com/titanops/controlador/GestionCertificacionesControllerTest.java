package com.titanops.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.controlador.GestionCertificacionesController.CategoriaOpcion;
import com.titanops.controlador.GestionCertificacionesController.OperadorOpcion;
import com.titanops.dao.CategoriaMaquinariaDAO;
import com.titanops.dao.OperadorCertificacionDAO;
import com.titanops.dao.OperadorDAO;
import com.titanops.modelo.CategoriaMaquinaria;
import com.titanops.modelo.Operador;
import com.titanops.modelo.OperadorCertificacion;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GestionCertificacionesControllerTest {

    @Test
    void creaCertificacionConNumeroNormalizado() {
        CertificacionDAODoble certificacionDAO = new CertificacionDAODoble();
        GestionCertificacionesController controller = controller(
                certificacionDAO, operador(true), categoria(true));

        var resultado = controller.crearCertificacion(operadorOpcion(true),
                categoriaOpcion(true), "  sv - 2026   abc ",
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1));

        assertTrue(resultado.exitoso());
        assertNotNull(certificacionDAO.insertada);
        assertEquals("SV - 2026 ABC",
                certificacionDAO.insertada.getNumeroAcreditacion());
        assertEquals(Date.valueOf("2026-01-01"),
                certificacionDAO.insertada.getFechaExpedicion());
        assertEquals(20, resultado.certificacion().getIdCertificacion());
    }

    @Test
    void rechazaRelacionesOFechasFaltantes() {
        GestionCertificacionesController controller = controller(
                new CertificacionDAODoble(), operador(true), categoria(true));

        assertFalse(controller.crearCertificacion(null, categoriaOpcion(true),
                "A-1", LocalDate.now(), LocalDate.now()).exitoso());
        assertFalse(controller.crearCertificacion(operadorOpcion(true), null,
                "A-1", LocalDate.now(), LocalDate.now()).exitoso());
        assertFalse(controller.crearCertificacion(operadorOpcion(true),
                categoriaOpcion(true), "A-1", null, LocalDate.now()).exitoso());
    }

    @Test
    void rechazaVencimientoAnteriorAExpedicion() {
        GestionCertificacionesController controller = controller(
                new CertificacionDAODoble(), operador(true), categoria(true));

        var resultado = controller.crearCertificacion(operadorOpcion(true),
                categoriaOpcion(true), "A-1", LocalDate.of(2026, 5, 2),
                LocalDate.of(2026, 5, 1));

        assertFalse(resultado.exitoso());
        assertEquals("La fecha de vencimiento no puede ser anterior a la expedición.",
                resultado.mensaje());
    }

    @Test
    void rechazaNumeroDuplicado() {
        CertificacionDAODoble dao = new CertificacionDAODoble();
        dao.numeroDuplicado = true;

        var resultado = controller(dao, operador(true), categoria(true))
                .crearCertificacion(operadorOpcion(true), categoriaOpcion(true),
                        "A-1", LocalDate.now(), LocalDate.now().plusYears(1));

        assertFalse(resultado.exitoso());
        assertNull(dao.insertada);
    }

    @Test
    void rechazaOperadorOCategoriaInactivosAlCrear() {
        var operadorInactivo = controller(new CertificacionDAODoble(),
                operador(false), categoria(true)).crearCertificacion(
                        operadorOpcion(false), categoriaOpcion(true), "A-1",
                        LocalDate.now(), LocalDate.now().plusYears(1));
        var categoriaInactiva = controller(new CertificacionDAODoble(),
                operador(true), categoria(false)).crearCertificacion(
                        operadorOpcion(true), categoriaOpcion(false), "A-2",
                        LocalDate.now(), LocalDate.now().plusYears(1));

        assertFalse(operadorInactivo.exitoso());
        assertFalse(categoriaInactiva.exitoso());
    }

    @Test
    void actualizaExcluyendoElRegistroActualDelDuplicado() {
        CertificacionDAODoble dao = new CertificacionDAODoble();
        dao.certificacionPorId = certificacion(5, LocalDate.now().plusYears(1));
        GestionCertificacionesController controller = controller(
                dao, operador(true), categoria(true));

        var resultado = controller.actualizarCertificacion(5,
                operadorOpcion(true), categoriaOpcion(true), " nuevo-1 ",
                LocalDate.now(), LocalDate.now().plusYears(2));

        assertTrue(resultado.exitoso());
        assertEquals(5, dao.idExcluidoEnDuplicado);
        assertEquals("NUEVO-1", dao.actualizada.getNumeroAcreditacion());
    }

    @Test
    void permiteConservarRelacionInactivaAlEditarHistorial() {
        CertificacionDAODoble dao = new CertificacionDAODoble();
        dao.certificacionPorId = certificacion(5, LocalDate.now().plusYears(1));
        GestionCertificacionesController controller = controller(
                dao, operador(false), categoria(false));

        var resultado = controller.actualizarCertificacion(5,
                operadorOpcion(false), categoriaOpcion(false), "A-1",
                LocalDate.now(), LocalDate.now().plusYears(1));

        assertTrue(resultado.exitoso());
    }

    @Test
    void eliminaCertificacionExistente() {
        CertificacionDAODoble dao = new CertificacionDAODoble();
        dao.certificacionPorId = certificacion(5, LocalDate.now().plusYears(1));

        var resultado = controller(dao, operador(true), categoria(true))
                .eliminarCertificacion(5);

        assertTrue(resultado.exitoso());
        assertTrue(dao.eliminada);
    }

    @Test
    void calculaVigenciaYFiltraPorEstado() {
        LocalDate hoy = LocalDate.now();
        CertificacionDAODoble dao = new CertificacionDAODoble();
        dao.certificaciones = List.of(
                certificacion(1, hoy.minusDays(1)),
                certificacion(2, hoy.plusDays(15)),
                certificacion(3, hoy.plusDays(60)));
        GestionCertificacionesController controller = controller(
                dao, operador(true), categoria(true));

        assertEquals(1, controller.listarCertificaciones("", "VENCIDA").size());
        assertEquals(1,
                controller.listarCertificaciones("", "VENCE PRONTO").size());
        assertEquals(1, controller.listarCertificaciones("", "VIGENTE").size());
        assertEquals(3, controller.listarCertificaciones("", "TODAS").size());
    }

    @Test
    void filtraPorOperadorDuiCategoriaYNumero() {
        CertificacionDAODoble dao = new CertificacionDAODoble();
        dao.certificaciones = List.of(
                certificacion(1, LocalDate.now().plusYears(1)));
        GestionCertificacionesController controller = controller(
                dao, operador(true), categoria(true));

        assertEquals(1, controller.listarCertificaciones("ana", "TODAS").size());
        assertEquals(1,
                controller.listarCertificaciones("01234567-8", "TODAS").size());
        assertEquals(1,
                controller.listarCertificaciones("maquinaria", "TODAS").size());
        assertEquals(1, controller.listarCertificaciones("CERT-", "TODAS").size());
    }

    private static GestionCertificacionesController controller(
            CertificacionDAODoble certificacionDAO, Operador operador,
            CategoriaMaquinaria categoria) {
        OperadorDAODoble operadorDAO = new OperadorDAODoble(operador);
        CategoriaDAODoble categoriaDAO = new CategoriaDAODoble(categoria);
        return new GestionCertificacionesController(
                certificacionDAO, operadorDAO, categoriaDAO);
    }

    private static OperadorOpcion operadorOpcion(boolean activo) {
        return new OperadorOpcion(1, "Ana", "Pérez", "01234567-8", activo);
    }

    private static CategoriaOpcion categoriaOpcion(boolean activa) {
        return new CategoriaOpcion(2, "Maquinaria pesada", activa);
    }

    private static Operador operador(boolean activo) {
        return new Operador(1, "Ana", "Pérez", "01234567-8", "PESADA",
                "DIURNO", null, activo ? "DISPONIBLE" : "INACTIVO", activo,
                Timestamp.valueOf("2026-09-22 08:00:00"));
    }

    private static CategoriaMaquinaria categoria(boolean activa) {
        return new CategoriaMaquinaria(
                2, "Maquinaria pesada", "Prueba", activa);
    }

    private static OperadorCertificacion certificacion(
            int id, LocalDate vencimiento) {
        return new OperadorCertificacion(id, 1, 2, "CERT-" + id,
                Date.valueOf(LocalDate.now().minusYears(1)),
                Date.valueOf(vencimiento));
    }

    private static class CertificacionDAODoble extends OperadorCertificacionDAO {
        private OperadorCertificacion insertada;
        private OperadorCertificacion actualizada;
        private OperadorCertificacion certificacionPorId;
        private List<OperadorCertificacion> certificaciones = List.of();
        private boolean numeroDuplicado;
        private Integer idExcluidoEnDuplicado;
        private boolean eliminada;

        @Override
        public boolean insertar(OperadorCertificacion certificacion) {
            insertada = certificacion;
            certificacion.setIdCertificacion(20);
            return true;
        }

        @Override
        public boolean actualizar(OperadorCertificacion certificacion) {
            actualizada = certificacion;
            return true;
        }

        @Override
        public boolean eliminar(int id) {
            eliminada = true;
            return true;
        }

        @Override
        public OperadorCertificacion obtenerPorId(int id) {
            return certificacionPorId;
        }

        @Override
        public boolean existeNumero(String numero, Integer idExcluido) {
            idExcluidoEnDuplicado = idExcluido;
            return numeroDuplicado;
        }

        @Override
        public List<OperadorCertificacion> listarTodos() {
            return certificaciones;
        }
    }

    private static class OperadorDAODoble extends OperadorDAO {
        private final Operador operador;

        OperadorDAODoble(Operador operador) {
            this.operador = operador;
        }

        @Override
        public Operador obtenerPorId(int id) {
            return id == operador.getIdOperador() ? operador : null;
        }

        @Override
        public List<Operador> listarTodos() {
            return List.of(operador);
        }
    }

    private static class CategoriaDAODoble extends CategoriaMaquinariaDAO {
        private final CategoriaMaquinaria categoria;

        CategoriaDAODoble(CategoriaMaquinaria categoria) {
            this.categoria = categoria;
        }

        @Override
        public CategoriaMaquinaria obtenerPorId(int id) {
            return id == categoria.getIdCategoria() ? categoria : null;
        }

        @Override
        public List<CategoriaMaquinaria> listarTodos() {
            return List.of(categoria);
        }
    }
}

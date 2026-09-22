package com.titanops.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.dao.OperadorDAO;
import com.titanops.modelo.Operador;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;

class GestionOperadoresControllerTest {

    @Test
    void creaOperadorConDatosNormalizados() {
        OperadorDAODoble dao = new OperadorDAODoble();
        GestionOperadoresController controller = new GestionOperadoresController(dao);

        var resultado = controller.crearOperador(
                "  María   José ", " López  Pérez ", "012345678",
                " liviana ", "diurno", "71234567", "disponible");

        assertTrue(resultado.exitoso());
        assertNotNull(dao.insertado);
        assertEquals("María José", dao.insertado.getNombres());
        assertEquals("López Pérez", dao.insertado.getApellidos());
        assertEquals("01234567-8", dao.insertado.getDui());
        assertEquals("LIVIANA", dao.insertado.getLicenciaTipo());
        assertEquals("DIURNO", dao.insertado.getTurno());
        assertEquals("7123-4567", dao.insertado.getTelefono());
        assertEquals("DISPONIBLE", dao.insertado.getEstadoOperativo());
        assertTrue(dao.insertado.getActivo());
    }

    @Test
    void permiteTelefonoOpcional() {
        OperadorDAODoble dao = new OperadorDAODoble();
        GestionOperadoresController controller = new GestionOperadoresController(dao);

        var resultado = controller.crearOperador(
                "Ana", "Pérez", "01234567-8", "PESADA",
                "ROTATIVO", "  ", "DESCANSO");

        assertTrue(resultado.exitoso());
        assertNull(dao.insertado.getTelefono());
    }

    @Test
    void rechazaDuiConFormatoInvalido() {
        GestionOperadoresController controller =
                new GestionOperadoresController(new OperadorDAODoble());

        var resultado = controller.crearOperador(
                "Ana", "Pérez", "123", "PESADA",
                "DIURNO", null, "DISPONIBLE");

        assertFalse(resultado.exitoso());
        assertEquals("El DUI debe tener el formato 00000000-0.", resultado.mensaje());
    }

    @Test
    void rechazaTelefonoConFormatoInvalido() {
        GestionOperadoresController controller =
                new GestionOperadoresController(new OperadorDAODoble());

        var resultado = controller.crearOperador(
                "Ana", "Pérez", "01234567-8", "PESADA",
                "DIURNO", "12345", "DISPONIBLE");

        assertFalse(resultado.exitoso());
        assertEquals("El teléfono debe tener el formato 0000-0000 o quedar vacío.",
                resultado.mensaje());
    }

    @Test
    void rechazaDuiDuplicado() {
        OperadorDAODoble dao = new OperadorDAODoble();
        dao.duiDuplicado = true;
        GestionOperadoresController controller = new GestionOperadoresController(dao);

        var resultado = controller.crearOperador(
                "Ana", "Pérez", "01234567-8", "PESADA",
                "DIURNO", null, "DISPONIBLE");

        assertFalse(resultado.exitoso());
        assertEquals("El DUI ya está registrado.", resultado.mensaje());
    }

    @Test
    void actualizaOperadorExcluyendoSuPropioDui() {
        OperadorDAODoble dao = new OperadorDAODoble();
        dao.operadorPorId = operador(5, true, "DISPONIBLE");
        GestionOperadoresController controller = new GestionOperadoresController(dao);

        var resultado = controller.actualizarOperador(
                5, " Ana ", " Pérez ", "012345678", "pesada",
                "nocturno", "22334455", "descanso");

        assertTrue(resultado.exitoso());
        assertEquals(5, dao.idExcluidoEnDuplicado);
        assertEquals("01234567-8", dao.actualizado.getDui());
        assertEquals("2233-4455", dao.actualizado.getTelefono());
        assertEquals("DESCANSO", dao.actualizado.getEstadoOperativo());
    }

    @Test
    void conservaEstadoInactivoAlEditarOperadorDesactivado() {
        OperadorDAODoble dao = new OperadorDAODoble();
        dao.operadorPorId = operador(5, false, "INACTIVO");
        GestionOperadoresController controller = new GestionOperadoresController(dao);

        var resultado = controller.actualizarOperador(
                5, "Ana", "Pérez", "01234567-8", "PESADA",
                "DIURNO", null, "DISPONIBLE");

        assertTrue(resultado.exitoso());
        assertFalse(dao.actualizado.getActivo());
        assertEquals("INACTIVO", dao.actualizado.getEstadoOperativo());
    }

    @Test
    void filtraListadoPorNombreDuiYTelefono() {
        OperadorDAODoble dao = new OperadorDAODoble();
        Operador ana = operador(5, true, "DISPONIBLE");
        Operador mario = new Operador(6, "Mario", "Gómez", "87654321-0",
                "LIVIANA", "NOCTURNO", "7000-0000", "DESCANSO", true,
                Timestamp.valueOf("2026-09-22 08:00:00"));
        dao.operadores = List.of(ana, mario);
        GestionOperadoresController controller = new GestionOperadoresController(dao);

        assertEquals(1, controller.listarOperadores("mario").size());
        assertEquals(1, controller.listarOperadores("01234567-8").size());
        assertEquals(1, controller.listarOperadores("7000").size());
        assertEquals(2, controller.listarOperadores("").size());
    }

    @Test
    void desactivaOperadorSinEliminarloFisicamente() {
        OperadorDAODoble dao = new OperadorDAODoble();
        dao.operadorPorId = operador(5, true, "DISPONIBLE");
        GestionOperadoresController controller = new GestionOperadoresController(dao);

        var resultado = controller.cambiarEstadoOperador(5, false);

        assertTrue(resultado.exitoso());
        assertTrue(dao.desactivado);
        assertFalse(dao.reactivado);
    }

    @Test
    void reactivaOperadorInactivo() {
        OperadorDAODoble dao = new OperadorDAODoble();
        dao.operadorPorId = operador(5, false, "INACTIVO");
        GestionOperadoresController controller = new GestionOperadoresController(dao);

        var resultado = controller.cambiarEstadoOperador(5, true);

        assertTrue(resultado.exitoso());
        assertTrue(dao.reactivado);
        assertFalse(dao.desactivado);
    }

    private static Operador operador(int id, boolean activo, String estado) {
        return new Operador(id, "Ana", "Pérez", "01234567-8", "PESADA",
                "DIURNO", "2233-4455", estado, activo,
                Timestamp.valueOf("2026-09-22 08:00:00"));
    }

    private static class OperadorDAODoble extends OperadorDAO {
        private boolean duiDuplicado;
        private Integer idExcluidoEnDuplicado;
        private Operador insertado;
        private Operador actualizado;
        private Operador operadorPorId;
        private boolean desactivado;
        private boolean reactivado;
        private List<Operador> operadores = List.of();

        @Override
        public boolean existeDui(String dui, Integer idOperadorExcluido) {
            idExcluidoEnDuplicado = idOperadorExcluido;
            return duiDuplicado;
        }

        @Override
        public boolean insertar(Operador operador) {
            insertado = operador;
            operador.setIdOperador(10);
            return true;
        }

        @Override
        public boolean actualizar(Operador operador) {
            actualizado = operador;
            return true;
        }

        @Override
        public Operador obtenerPorId(int id) {
            return operadorPorId;
        }

        @Override
        public List<Operador> listarTodos() {
            return operadores;
        }

        @Override
        public boolean eliminar(int id) {
            desactivado = true;
            return true;
        }

        @Override
        public boolean reactivar(int id) {
            reactivado = true;
            return true;
        }
    }
}

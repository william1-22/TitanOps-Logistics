package com.titanops.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.dao.CategoriaMaquinariaDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.modelo.CategoriaMaquinaria;
import com.titanops.modelo.Maquinaria;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GestionMaquinariaControllerTest {

    @Test
    void creaCategoriaNecesariaParaUnaBaseVacia() {
        CategoriaDAODoble categoriaDAO = new CategoriaDAODoble(true);
        GestionMaquinariaController controller = new GestionMaquinariaController(
                new MaquinariaDAODoble(), categoriaDAO);

        var resultado = controller.crearCategoria(
                "  Excavadoras   hidráulicas ", " Equipo de excavación ");

        assertTrue(resultado.exitoso());
        assertNotNull(categoriaDAO.insertada);
        assertEquals("Excavadoras hidráulicas",
                categoriaDAO.insertada.getNombreCategoria());
        assertEquals("Equipo de excavación", categoriaDAO.insertada.getDescripcion());
        assertTrue(categoriaDAO.insertada.getActivo());
    }

    @Test
    void reactivaCategoriaExistenteEnLugarDeDuplicarla() {
        CategoriaDAODoble categoriaDAO = new CategoriaDAODoble(false);
        categoriaDAO.devolverExistente = true;
        GestionMaquinariaController controller = new GestionMaquinariaController(
                new MaquinariaDAODoble(), categoriaDAO);

        var resultado = controller.crearCategoria(
                "Maquinaria pesada", "Descripción nueva");

        assertTrue(resultado.exitoso());
        assertNotNull(categoriaDAO.actualizada);
        assertTrue(categoriaDAO.actualizada.getActivo());
        assertEquals("Descripción nueva", categoriaDAO.actualizada.getDescripcion());
        assertNull(categoriaDAO.insertada);
    }

    @Test
    void creaMaquinariaConDatosNormalizados() {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        GestionMaquinariaController controller = controller(maquinariaDAO, true);

        var resultado = controller.crearMaquinaria(
                1, " mq-001 ", "  Caterpillar  ", " 320   GC ",
                "22,50", "1250.00", "disponible");

        assertTrue(resultado.exitoso());
        assertNotNull(maquinariaDAO.insertada);
        assertEquals("MQ-001", maquinariaDAO.insertada.getCodigoInventario());
        assertEquals("Caterpillar", maquinariaDAO.insertada.getMarca());
        assertEquals("320 GC", maquinariaDAO.insertada.getModelo());
        assertEquals(new BigDecimal("22.5"), maquinariaDAO.insertada.getTonelaje());
        assertEquals(new BigDecimal("1.25E+3"), maquinariaDAO.insertada.getHorasUso());
        assertEquals("DISPONIBLE", maquinariaDAO.insertada.getEstadoOperativo());
        assertTrue(maquinariaDAO.insertada.getActivo());
    }

    @Test
    void permiteDatosTecnicosOpcionales() {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        GestionMaquinariaController controller = controller(maquinariaDAO, true);

        var resultado = controller.crearMaquinaria(
                1, "EQ-01", " ", null, "", "", "MANTENIMIENTO");

        assertTrue(resultado.exitoso());
        assertNull(maquinariaDAO.insertada.getMarca());
        assertNull(maquinariaDAO.insertada.getModelo());
        assertNull(maquinariaDAO.insertada.getTonelaje());
        assertNull(maquinariaDAO.insertada.getHorasUso());
    }

    @Test
    void rechazaCodigoDuplicado() {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        maquinariaDAO.codigoDuplicado = true;
        GestionMaquinariaController controller = controller(maquinariaDAO, true);

        var resultado = controller.crearMaquinaria(
                1, "EQ-01", "CAT", "320", "10", "100", "DISPONIBLE");

        assertFalse(resultado.exitoso());
        assertEquals("El código de inventario ya está registrado.", resultado.mensaje());
    }

    @Test
    void rechazaValoresNegativos() {
        GestionMaquinariaController controller =
                controller(new MaquinariaDAODoble(), true);

        var resultado = controller.crearMaquinaria(
                1, "EQ-01", "CAT", "320", "-1", "100", "DISPONIBLE");

        assertFalse(resultado.exitoso());
        assertEquals("El tonelaje no puede ser negativo.", resultado.mensaje());
    }

    @Test
    void rechazaNumerosFueraDeNumericDiezDos() {
        GestionMaquinariaController controller =
                controller(new MaquinariaDAODoble(), true);

        var demasiadosEnteros = controller.crearMaquinaria(
                1, "EQ-01", "CAT", "320", "123456789", "100", "DISPONIBLE");
        var demasiadosDecimales = controller.crearMaquinaria(
                1, "EQ-01", "CAT", "320", "1.123", "100", "DISPONIBLE");

        assertFalse(demasiadosEnteros.exitoso());
        assertFalse(demasiadosDecimales.exitoso());
    }

    @Test
    void rechazaCategoriaInactiva() {
        GestionMaquinariaController controller =
                controller(new MaquinariaDAODoble(), false);

        var resultado = controller.crearMaquinaria(
                1, "EQ-01", "CAT", "320", "10", "100", "DISPONIBLE");

        assertFalse(resultado.exitoso());
        assertEquals("La categoría seleccionada ya no está disponible.",
                resultado.mensaje());
    }

    @Test
    void actualizaExcluyendoElCodigoDelRegistroActual() {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        maquinariaDAO.maquinariaPorId = maquinaria(7, true, "DISPONIBLE");
        GestionMaquinariaController controller = controller(maquinariaDAO, true);

        var resultado = controller.actualizarMaquinaria(
                7, 1, "eq-01", "Komatsu", "PC200", "20", "250", "EN_RUTA");

        assertTrue(resultado.exitoso());
        assertEquals(7, maquinariaDAO.idExcluidoEnDuplicado);
        assertEquals("EQ-01", maquinariaDAO.actualizada.getCodigoInventario());
        assertEquals("EN_RUTA", maquinariaDAO.actualizada.getEstadoOperativo());
    }

    @Test
    void filtraPorEstadoYTextoIncluyendoCategoria() {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        Maquinaria excavadora = maquinaria(7, true, "DISPONIBLE");
        Maquinaria grua = new Maquinaria(8, 1, "GR-01", "Liebherr", "LTM",
                new BigDecimal("50"), new BigDecimal("400"), "MANTENIMIENTO",
                true, Timestamp.valueOf("2026-09-22 08:00:00"));
        maquinariaDAO.maquinaria = List.of(excavadora, grua);
        GestionMaquinariaController controller = controller(maquinariaDAO, true);

        assertEquals(2, controller.listarMaquinaria("maquinaria", "TODOS").size());
        assertEquals(1, controller.listarMaquinaria("GR-01", "MANTENIMIENTO").size());
        assertEquals("MANTENIMIENTO", maquinariaDAO.ultimoEstadoListado);
    }

    @Test
    void desactivaMaquinariaLogicamente() {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        maquinariaDAO.maquinariaPorId = maquinaria(7, true, "DISPONIBLE");
        GestionMaquinariaController controller = controller(maquinariaDAO, true);

        var resultado = controller.cambiarEstadoRegistro(7, false);

        assertTrue(resultado.exitoso());
        assertTrue(maquinariaDAO.desactivada);
        assertFalse(maquinariaDAO.reactivada);
    }

    @Test
    void reactivaMaquinariaInactiva() {
        MaquinariaDAODoble maquinariaDAO = new MaquinariaDAODoble();
        maquinariaDAO.maquinariaPorId = maquinaria(7, false, "MANTENIMIENTO");
        GestionMaquinariaController controller = controller(maquinariaDAO, true);

        var resultado = controller.cambiarEstadoRegistro(7, true);

        assertTrue(resultado.exitoso());
        assertTrue(maquinariaDAO.reactivada);
        assertFalse(maquinariaDAO.desactivada);
    }

    private static GestionMaquinariaController controller(
            MaquinariaDAODoble maquinariaDAO, boolean categoriaActiva) {
        return new GestionMaquinariaController(
                maquinariaDAO, new CategoriaDAODoble(categoriaActiva));
    }

    private static Maquinaria maquinaria(int id, boolean activa, String estado) {
        return new Maquinaria(id, 1, "EQ-01", "CAT", "320",
                new BigDecimal("20"), new BigDecimal("100"), estado, activa,
                Timestamp.valueOf("2026-09-22 08:00:00"));
    }

    private static class MaquinariaDAODoble extends MaquinariaDAO {
        private boolean codigoDuplicado;
        private Integer idExcluidoEnDuplicado;
        private Maquinaria insertada;
        private Maquinaria actualizada;
        private Maquinaria maquinariaPorId;
        private boolean desactivada;
        private boolean reactivada;
        private String ultimoEstadoListado;
        private List<Maquinaria> maquinaria = List.of();

        @Override
        public boolean existeCodigo(String codigo, Integer idExcluido) {
            idExcluidoEnDuplicado = idExcluido;
            return codigoDuplicado;
        }

        @Override
        public boolean insertar(Maquinaria maquinaria) {
            insertada = maquinaria;
            maquinaria.setIdMaquinaria(20);
            return true;
        }

        @Override
        public boolean actualizar(Maquinaria maquinaria) {
            actualizada = maquinaria;
            return true;
        }

        @Override
        public Maquinaria obtenerPorId(int id) {
            return maquinariaPorId;
        }

        @Override
        public List<Maquinaria> listarTodos() {
            return maquinaria;
        }

        @Override
        public List<Maquinaria> listarPorEstado(String estado) {
            ultimoEstadoListado = estado;
            return maquinaria.stream()
                    .filter(item -> estado.equals(item.getEstadoOperativo()))
                    .toList();
        }

        @Override
        public boolean eliminar(int id) {
            desactivada = true;
            return true;
        }

        @Override
        public boolean reactivar(int id) {
            reactivada = true;
            return true;
        }
    }

    private static class CategoriaDAODoble extends CategoriaMaquinariaDAO {
        private final CategoriaMaquinaria categoria;
        private boolean devolverExistente;
        private CategoriaMaquinaria insertada;
        private CategoriaMaquinaria actualizada;

        CategoriaDAODoble(boolean activa) {
            categoria = new CategoriaMaquinaria(
                    1, "Maquinaria pesada", "Prueba", activa);
        }

        @Override
        public CategoriaMaquinaria obtenerPorId(int id) {
            return id == 1 ? categoria : null;
        }

        @Override
        public List<CategoriaMaquinaria> listarTodos() {
            return List.of(categoria);
        }

        @Override
        public Optional<CategoriaMaquinaria> buscarPorNombre(String nombre) {
            return devolverExistente ? Optional.of(categoria) : Optional.empty();
        }

        @Override
        public boolean insertar(CategoriaMaquinaria nuevaCategoria) {
            insertada = nuevaCategoria;
            nuevaCategoria.setIdCategoria(2);
            return true;
        }

        @Override
        public boolean actualizar(CategoriaMaquinaria categoriaActualizada) {
            actualizada = categoriaActualizada;
            return true;
        }
    }
}

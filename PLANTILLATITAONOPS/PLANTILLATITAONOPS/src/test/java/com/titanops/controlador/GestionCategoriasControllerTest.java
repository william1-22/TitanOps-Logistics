package com.titanops.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.dao.CategoriaMaquinariaDAO;
import com.titanops.modelo.CategoriaMaquinaria;
import java.util.List;
import org.junit.jupiter.api.Test;

class GestionCategoriasControllerTest {

    @Test
    void normalizaYConstruyeUnaCategoria() {
        CategoriaDAODoble dao = new CategoriaDAODoble();
        GestionCategoriasController controller = new GestionCategoriasController(dao);

        var resultado = controller.crearCategoria(
                "  Excavadoras   hidráulicas ", " Equipo de excavación ");

        assertTrue(resultado.exitoso());
        assertNotNull(dao.insertada);
        assertEquals("Excavadoras hidráulicas", dao.insertada.getNombreCategoria());
        assertEquals("Equipo de excavación", dao.insertada.getDescripcion());
        assertTrue(dao.insertada.getActivo());
    }

    @Test
    void rechazaNombreDuplicadoAlEditarOtraCategoria() {
        CategoriaDAODoble dao = new CategoriaDAODoble();
        dao.categorias = List.of(
                new CategoriaMaquinaria(1, "Excavadoras", null, true),
                new CategoriaMaquinaria(2, "Camiones", null, true));
        GestionCategoriasController controller = new GestionCategoriasController(dao);

        var resultado = controller.actualizarCategoria(2, "Excavadoras", "", true);

        assertFalse(resultado.exitoso());
        assertEquals("La categoría ya está registrada.", resultado.mensaje());
    }

    @Test
    void cambiaEstadoSinEliminarFisicamenteLaCategoria() {
        CategoriaDAODoble dao = new CategoriaDAODoble();
        dao.categorias = List.of(new CategoriaMaquinaria(1, "Excavadoras", null, true));
        GestionCategoriasController controller = new GestionCategoriasController(dao);

        var resultado = controller.cambiarEstadoCategoria(1, false);

        assertTrue(resultado.exitoso());
        assertTrue(dao.desactivada);
        assertFalse(dao.eliminadaFisicamente);
    }

    private static class CategoriaDAODoble extends CategoriaMaquinariaDAO {
        private CategoriaMaquinaria insertada;
        private List<CategoriaMaquinaria> categorias = List.of();
        private boolean desactivada;
        private boolean eliminadaFisicamente;

        @Override
        public boolean insertar(CategoriaMaquinaria categoria) {
            categoria.setIdCategoria(10);
            insertada = categoria;
            return true;
        }

        @Override
        public CategoriaMaquinaria obtenerPorId(int id) {
            return categorias.stream()
                    .filter(categoria -> categoria.getIdCategoria() == id)
                    .findFirst().orElse(null);
        }

        @Override
        public List<CategoriaMaquinaria> listarTodos() {
            return categorias;
        }

        @Override
        public boolean actualizar(CategoriaMaquinaria categoria) {
            return true;
        }

        @Override
        public boolean eliminar(int id) {
            desactivada = true;
            return true;
        }

        @Override
        public boolean reactivar(int id) {
            return true;
        }

        @Override
        public java.util.Optional<CategoriaMaquinaria> buscarPorNombre(String nombre) {
            return categorias.stream()
                    .filter(categoria -> categoria.getNombreCategoria()
                            .equalsIgnoreCase(nombre))
                    .findFirst();
        }
    }
}

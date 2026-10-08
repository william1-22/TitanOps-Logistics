package com.titanops.controlador;

import com.titanops.dao.CategoriaMaquinariaDAO;
import com.titanops.modelo.CategoriaMaquinaria;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Valida y coordina la administración del catálogo de categorías. */
public class GestionCategoriasController {
    private final CategoriaMaquinariaDAO categoriaDAO;

    public GestionCategoriasController(CategoriaMaquinariaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public List<CategoriaFila> listarCategorias(String filtro) {
        String textoFiltro = normalizarTexto(filtro).toLowerCase(Locale.ROOT);
        return categoriaDAO.listarTodos().stream()
                .filter(categoria -> textoFiltro.isEmpty()
                        || coincide(categoria, textoFiltro))
                .map(categoria -> new CategoriaFila(
                        categoria.getIdCategoria(),
                        categoria.getNombreCategoria(),
                        categoria.getDescripcion(),
                        Boolean.TRUE.equals(categoria.getActivo())))
                .toList();
    }

    public List<CategoriaFila> listarCategorias() {
        return listarCategorias("");
    }

    public Optional<CategoriaEdicion> obtenerCategoriaEdicion(int idCategoria) {
        CategoriaMaquinaria categoria = categoriaDAO.obtenerPorId(idCategoria);
        if (categoria == null) {
            return Optional.empty();
        }
        return Optional.of(new CategoriaEdicion(
                categoria.getIdCategoria(), categoria.getNombreCategoria(),
                categoria.getDescripcion(), Boolean.TRUE.equals(categoria.getActivo())));
    }

    public ResultadoOperacion crearCategoria(String nombre, String descripcion) {
        DatosNormalizados datos = normalizarDatos(nombre, descripcion);
        if (datos.error() != null) {
            return ResultadoOperacion.error(datos.error());
        }

        Optional<CategoriaMaquinaria> existente =
                categoriaDAO.buscarPorNombre(datos.nombre());
        if (existente.isPresent()) {
            CategoriaMaquinaria categoria = existente.get();
            if (Boolean.TRUE.equals(categoria.getActivo())) {
                return ResultadoOperacion.error("La categoría ya está registrada.");
            }
            categoria.setDescripcion(datos.descripcion());
            categoria.setActivo(true);
            if (!categoriaDAO.actualizar(categoria)) {
                return ResultadoOperacion.error("No fue posible reactivar la categoría.");
            }
            return ResultadoOperacion.exito("Categoría reactivada correctamente.");
        }

        CategoriaMaquinaria categoria = new CategoriaMaquinaria(
                0, datos.nombre(), datos.descripcion(), true);
        if (!categoriaDAO.insertar(categoria)) {
            return ResultadoOperacion.error("No fue posible guardar la categoría.");
        }
        return ResultadoOperacion.exito("Categoría creada correctamente.");
    }

    public ResultadoOperacion actualizarCategoria(int idCategoria,
                                                  String nombre,
                                                  String descripcion,
                                                  boolean activo) {
        DatosNormalizados datos = normalizarDatos(nombre, descripcion);
        if (datos.error() != null) {
            return ResultadoOperacion.error(datos.error());
        }

        CategoriaMaquinaria actual = categoriaDAO.obtenerPorId(idCategoria);
        if (actual == null) {
            return ResultadoOperacion.error("La categoría seleccionada ya no existe.");
        }

        Optional<CategoriaMaquinaria> existente =
                categoriaDAO.buscarPorNombre(datos.nombre());
        if (existente.isPresent()
                && existente.get().getIdCategoria() != idCategoria) {
            return ResultadoOperacion.error("La categoría ya está registrada.");
        }

        actual.setNombreCategoria(datos.nombre());
        actual.setDescripcion(datos.descripcion());
        actual.setActivo(activo);
        if (!categoriaDAO.actualizar(actual)) {
            return ResultadoOperacion.error("No fue posible actualizar la categoría.");
        }
        return ResultadoOperacion.exito("Categoría actualizada correctamente.");
    }

    public ResultadoOperacion cambiarEstadoCategoria(int idCategoria, boolean activar) {
        CategoriaMaquinaria categoria = categoriaDAO.obtenerPorId(idCategoria);
        if (categoria == null) {
            return ResultadoOperacion.error("La categoría seleccionada ya no existe.");
        }

        boolean activa = Boolean.TRUE.equals(categoria.getActivo());
        if (activa == activar) {
            return ResultadoOperacion.exito(
                    activar ? "La categoría ya estaba activa."
                            : "La categoría ya estaba inactiva.");
        }

        boolean actualizada = activar
                ? categoriaDAO.reactivar(idCategoria)
                : categoriaDAO.eliminar(idCategoria);
        if (!actualizada) {
            return ResultadoOperacion.error(
                    activar ? "No fue posible reactivar la categoría."
                            : "No fue posible desactivar la categoría.");
        }
        return ResultadoOperacion.exito(
                activar ? "Categoría reactivada correctamente."
                        : "Categoría desactivada correctamente.");
    }

    private DatosNormalizados normalizarDatos(String nombre, String descripcion) {
        String nombreNormalizado = normalizarTexto(nombre);
        String descripcionNormalizada = normalizarTexto(descripcion);
        String error = null;
        if (nombreNormalizado.isEmpty()) {
            error = "Ingresa el nombre de la categoría.";
        } else if (nombreNormalizado.length() > 100) {
            error = "El nombre de la categoría no puede exceder 100 caracteres.";
        }
        return new DatosNormalizados(nombreNormalizado,
                descripcionNormalizada.isEmpty() ? null : descripcionNormalizada, error);
    }

    private boolean coincide(CategoriaMaquinaria categoria, String filtro) {
        String texto = (valorSeguro(categoria.getNombreCategoria()) + " "
                + valorSeguro(categoria.getDescripcion()) + " "
                + (Boolean.TRUE.equals(categoria.getActivo()) ? "activo" : "inactivo"))
                .toLowerCase(Locale.ROOT);
        return texto.contains(filtro);
    }

    private String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private record DatosNormalizados(String nombre, String descripcion, String error) {}

    public record CategoriaFila(int idCategoria, String nombre, String descripcion,
                                boolean activa) {}

    public record CategoriaEdicion(int idCategoria, String nombre, String descripcion,
                                   boolean activa) {}

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
        public static ResultadoOperacion exito(String mensaje) {
            return new ResultadoOperacion(true, mensaje);
        }

        public static ResultadoOperacion error(String mensaje) {
            return new ResultadoOperacion(false, mensaje);
        }
    }
}

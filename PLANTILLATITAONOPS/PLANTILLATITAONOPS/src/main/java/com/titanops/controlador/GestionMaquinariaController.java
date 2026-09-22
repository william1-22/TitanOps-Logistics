package com.titanops.controlador;

import com.titanops.dao.CategoriaMaquinariaDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.modelo.CategoriaMaquinaria;
import com.titanops.modelo.Maquinaria;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Valida y coordina la gestión de la flota de maquinaria. */
public class GestionMaquinariaController {
    public static final List<String> ESTADOS_OPERATIVOS =
            List.of("DISPONIBLE", "EN_RUTA", "MANTENIMIENTO");

    private final MaquinariaDAO maquinariaDAO;
    private final CategoriaMaquinariaDAO categoriaDAO;

    public GestionMaquinariaController(MaquinariaDAO maquinariaDAO,
                                       CategoriaMaquinariaDAO categoriaDAO) {
        this.maquinariaDAO = maquinariaDAO;
        this.categoriaDAO = categoriaDAO;
    }

    public List<CategoriaOpcion> listarCategorias() {
        List<CategoriaOpcion> opciones = new ArrayList<>();
        for (CategoriaMaquinaria categoria : categoriaDAO.listarTodos()) {
            opciones.add(new CategoriaOpcion(
                    categoria.getIdCategoria(), categoria.getNombreCategoria(),
                    Boolean.TRUE.equals(categoria.getActivo())));
        }
        return opciones;
    }

    public ResultadoCategoria crearCategoria(String nombre, String descripcion) {
        String nombreNormalizado = normalizarTexto(nombre);
        String descripcionNormalizada = normalizarOpcional(descripcion);
        if (nombreNormalizado.isEmpty()) {
            return ResultadoCategoria.error("Ingresa el nombre de la categoría.");
        }
        if (nombreNormalizado.length() > 100) {
            return ResultadoCategoria.error(
                    "El nombre de la categoría no puede exceder 100 caracteres.");
        }

        Optional<CategoriaMaquinaria> existente =
                categoriaDAO.buscarPorNombre(nombreNormalizado);
        if (existente.isPresent()) {
            CategoriaMaquinaria categoria = existente.get();
            if (Boolean.TRUE.equals(categoria.getActivo())) {
                return ResultadoCategoria.error("La categoría ya está registrada.");
            }
            categoria.setDescripcion(descripcionNormalizada);
            categoria.setActivo(true);
            if (!categoriaDAO.actualizar(categoria)) {
                return ResultadoCategoria.error("No fue posible reactivar la categoría.");
            }
            return ResultadoCategoria.exito(
                    categoria.getIdCategoria(), categoria.getNombreCategoria(),
                    "Categoría reactivada correctamente.");
        }

        CategoriaMaquinaria categoria = new CategoriaMaquinaria(
                0, nombreNormalizado, descripcionNormalizada, true);
        if (!categoriaDAO.insertar(categoria)) {
            return ResultadoCategoria.error("No fue posible guardar la categoría.");
        }
        return ResultadoCategoria.exito(
                categoria.getIdCategoria(), categoria.getNombreCategoria(),
                "Categoría creada correctamente.");
    }

    public ResultadoCreacion crearMaquinaria(
            Integer idCategoria, String codigoInventario, String marca, String modelo,
            String tonelaje, String horasUso, String estadoOperativo) {
        DatosNormalizados datos = normalizarDatos(idCategoria, codigoInventario,
                marca, modelo, tonelaje, horasUso, estadoOperativo);
        if (datos.error() != null) {
            return ResultadoCreacion.error(datos.error());
        }
        String errorCategoria = validarCategoria(idCategoria);
        if (errorCategoria != null) {
            return ResultadoCreacion.error(errorCategoria);
        }
        if (maquinariaDAO.existeCodigo(datos.codigoInventario(), null)) {
            return ResultadoCreacion.error("El código de inventario ya está registrado.");
        }

        Maquinaria maquinaria = construirMaquinaria(0, datos, true);
        if (!maquinariaDAO.insertar(maquinaria)) {
            return ResultadoCreacion.error("No fue posible guardar la maquinaria.");
        }
        return ResultadoCreacion.exito(maquinaria);
    }

    public ResultadoOperacion actualizarMaquinaria(
            int idMaquinaria, Integer idCategoria, String codigoInventario,
            String marca, String modelo, String tonelaje, String horasUso,
            String estadoOperativo) {
        DatosNormalizados datos = normalizarDatos(idCategoria, codigoInventario,
                marca, modelo, tonelaje, horasUso, estadoOperativo);
        if (datos.error() != null) {
            return ResultadoOperacion.error(datos.error());
        }
        Maquinaria actual = maquinariaDAO.obtenerPorId(idMaquinaria);
        if (actual == null) {
            return ResultadoOperacion.error("La maquinaria seleccionada ya no existe.");
        }
        String errorCategoria = validarCategoria(idCategoria);
        if (errorCategoria != null) {
            return ResultadoOperacion.error(errorCategoria);
        }
        if (maquinariaDAO.existeCodigo(datos.codigoInventario(), idMaquinaria)) {
            return ResultadoOperacion.error("El código de inventario ya está registrado.");
        }

        Maquinaria maquinaria = construirMaquinaria(
                idMaquinaria, datos, Boolean.TRUE.equals(actual.getActivo()));
        maquinaria.setFechaRegistro(actual.getFechaRegistro());
        if (!maquinariaDAO.actualizar(maquinaria)) {
            return ResultadoOperacion.error("No fue posible actualizar la maquinaria.");
        }
        return ResultadoOperacion.exito("Maquinaria actualizada correctamente.");
    }

    public ResultadoOperacion cambiarEstadoRegistro(int idMaquinaria, boolean activar) {
        Maquinaria maquinaria = maquinariaDAO.obtenerPorId(idMaquinaria);
        if (maquinaria == null) {
            return ResultadoOperacion.error("La maquinaria seleccionada ya no existe.");
        }
        boolean activa = Boolean.TRUE.equals(maquinaria.getActivo());
        if (activar == activa) {
            return ResultadoOperacion.exito(
                    activar ? "La maquinaria ya estaba activa."
                            : "La maquinaria ya estaba inactiva.");
        }
        boolean actualizada = activar
                ? maquinariaDAO.reactivar(idMaquinaria) : maquinariaDAO.eliminar(idMaquinaria);
        if (!actualizada) {
            return ResultadoOperacion.error(
                    activar ? "No fue posible reactivar la maquinaria."
                            : "No fue posible desactivar la maquinaria.");
        }
        return ResultadoOperacion.exito(
                activar ? "Maquinaria reactivada correctamente."
                        : "Maquinaria desactivada correctamente.");
    }

    public Optional<MaquinariaEdicion> obtenerMaquinariaEdicion(int idMaquinaria) {
        Maquinaria maquinaria = maquinariaDAO.obtenerPorId(idMaquinaria);
        if (maquinaria == null) {
            return Optional.empty();
        }
        return Optional.of(new MaquinariaEdicion(
                maquinaria.getIdMaquinaria(), maquinaria.getIdCategoria(),
                maquinaria.getCodigoInventario(), maquinaria.getMarca(),
                maquinaria.getModelo(), maquinaria.getTonelaje(),
                maquinaria.getHorasUso(), maquinaria.getEstadoOperativo(),
                Boolean.TRUE.equals(maquinaria.getActivo())));
    }

    public List<MaquinariaFila> listarMaquinaria(String filtro,
                                                  String estadoOperativo) {
        String estado = normalizarMayusculas(estadoOperativo);
        List<Maquinaria> lista = estado.isEmpty() || "TODOS".equals(estado)
                ? maquinariaDAO.listarTodos() : maquinariaDAO.listarPorEstado(estado);

        Map<Integer, String> categoriasPorId = new HashMap<>();
        for (CategoriaMaquinaria categoria : categoriaDAO.listarTodos()) {
            categoriasPorId.put(categoria.getIdCategoria(), categoria.getNombreCategoria());
        }

        String textoFiltro = normalizarTexto(filtro).toLowerCase(Locale.ROOT);
        List<MaquinariaFila> filas = new ArrayList<>();
        for (Maquinaria maquinaria : lista) {
            String categoria = categoriasPorId.getOrDefault(
                    maquinaria.getIdCategoria(), "CATEGORÍA DESCONOCIDA");
            if (textoFiltro.isEmpty()
                    || coincide(maquinaria, categoria, textoFiltro)) {
                filas.add(new MaquinariaFila(
                        maquinaria.getIdMaquinaria(), maquinaria.getIdCategoria(),
                        maquinaria.getCodigoInventario(), categoria,
                        maquinaria.getMarca(), maquinaria.getModelo(),
                        maquinaria.getTonelaje(), maquinaria.getHorasUso(),
                        maquinaria.getEstadoOperativo(),
                        Boolean.TRUE.equals(maquinaria.getActivo()),
                        maquinaria.getFechaRegistro()));
            }
        }
        return filas;
    }

    public List<MaquinariaFila> listarMaquinaria() {
        return listarMaquinaria("", "TODOS");
    }

    private String validarCategoria(Integer idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            return "Selecciona una categoría.";
        }
        CategoriaMaquinaria categoria = categoriaDAO.obtenerPorId(idCategoria);
        if (categoria == null || !Boolean.TRUE.equals(categoria.getActivo())) {
            return "La categoría seleccionada ya no está disponible.";
        }
        return null;
    }

    private DatosNormalizados normalizarDatos(
            Integer idCategoria, String codigoInventario, String marca, String modelo,
            String tonelaje, String horasUso, String estadoOperativo) {
        String codigo = normalizarMayusculas(codigoInventario);
        String marcaNormalizada = normalizarOpcional(marca);
        String modeloNormalizado = normalizarOpcional(modelo);
        String estado = normalizarMayusculas(estadoOperativo);
        ResultadoDecimal tonelajeResultado = convertirDecimal(tonelaje, "tonelaje");
        ResultadoDecimal horasResultado = convertirDecimal(horasUso, "horas de uso");

        String error = null;
        if (codigo.isEmpty()) {
            error = "Ingresa el código de inventario.";
        } else if (codigo.length() > 50) {
            error = "El código de inventario no puede exceder 50 caracteres.";
        } else if (marcaNormalizada != null && marcaNormalizada.length() > 50) {
            error = "La marca no puede exceder 50 caracteres.";
        } else if (modeloNormalizado != null && modeloNormalizado.length() > 50) {
            error = "El modelo no puede exceder 50 caracteres.";
        } else if (tonelajeResultado.error() != null) {
            error = tonelajeResultado.error();
        } else if (horasResultado.error() != null) {
            error = horasResultado.error();
        } else if (!ESTADOS_OPERATIVOS.contains(estado)) {
            error = "Selecciona un estado operativo válido.";
        }

        return new DatosNormalizados(idCategoria, codigo, marcaNormalizada,
                modeloNormalizado, tonelajeResultado.valor(), horasResultado.valor(),
                estado, error);
    }

    private ResultadoDecimal convertirDecimal(String texto, String nombreCampo) {
        String valor = normalizarTexto(texto).replace(',', '.');
        if (valor.isEmpty()) {
            return new ResultadoDecimal(null, null);
        }
        try {
            BigDecimal decimal = new BigDecimal(valor).stripTrailingZeros();
            if (decimal.signum() < 0) {
                return new ResultadoDecimal(null,
                        "El " + nombreCampo + " no puede ser negativo.");
            }
            int enteros = decimal.precision() - decimal.scale();
            if (decimal.scale() > 2 || enteros > 8) {
                return new ResultadoDecimal(null,
                        "El " + nombreCampo
                                + " admite hasta 8 enteros y 2 decimales.");
            }
            return new ResultadoDecimal(decimal, null);
        } catch (NumberFormatException exception) {
            return new ResultadoDecimal(null,
                    "Ingresa un valor numérico válido para " + nombreCampo + ".");
        }
    }

    private boolean coincide(Maquinaria maquinaria, String categoria, String filtro) {
        String texto = String.join(" ",
                valorSeguro(maquinaria.getCodigoInventario()),
                valorSeguro(maquinaria.getMarca()),
                valorSeguro(maquinaria.getModelo()),
                valorSeguro(categoria),
                valorSeguro(maquinaria.getEstadoOperativo())).toLowerCase(Locale.ROOT);
        return texto.contains(filtro);
    }

    private Maquinaria construirMaquinaria(int idMaquinaria, DatosNormalizados datos,
                                           boolean activa) {
        return new Maquinaria(idMaquinaria, datos.idCategoria(),
                datos.codigoInventario(), datos.marca(), datos.modelo(),
                datos.tonelaje(), datos.horasUso(), datos.estadoOperativo(),
                activa, null);
    }

    private String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }

    private String normalizarMayusculas(String valor) {
        return normalizarTexto(valor).toUpperCase(Locale.ROOT);
    }

    private String normalizarOpcional(String valor) {
        String normalizado = normalizarTexto(valor);
        return normalizado.isEmpty() ? null : normalizado;
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private record ResultadoDecimal(BigDecimal valor, String error) {}

    private record DatosNormalizados(Integer idCategoria, String codigoInventario,
                                     String marca, String modelo, BigDecimal tonelaje,
                                     BigDecimal horasUso, String estadoOperativo,
                                     String error) {}

    public record ResultadoCreacion(boolean exitoso, String mensaje,
                                    Maquinaria maquinaria) {
        public static ResultadoCreacion exito(Maquinaria maquinaria) {
            return new ResultadoCreacion(
                    true, "Maquinaria creada correctamente.", maquinaria);
        }

        public static ResultadoCreacion error(String mensaje) {
            return new ResultadoCreacion(false, mensaje, null);
        }
    }

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
        public static ResultadoOperacion exito(String mensaje) {
            return new ResultadoOperacion(true, mensaje);
        }

        public static ResultadoOperacion error(String mensaje) {
            return new ResultadoOperacion(false, mensaje);
        }
    }

    public record ResultadoCategoria(boolean exitoso, String mensaje,
                                     Integer idCategoria, String nombre) {
        public static ResultadoCategoria exito(int idCategoria, String nombre,
                                                String mensaje) {
            return new ResultadoCategoria(true, mensaje, idCategoria, nombre);
        }

        public static ResultadoCategoria error(String mensaje) {
            return new ResultadoCategoria(false, mensaje, null, null);
        }
    }

    public record CategoriaOpcion(int idCategoria, String nombre, boolean activa) {
        @Override
        public String toString() {
            return activa ? nombre : nombre + " (INACTIVA)";
        }
    }

    public record MaquinariaEdicion(int idMaquinaria, int idCategoria,
                                    String codigoInventario, String marca,
                                    String modelo, BigDecimal tonelaje,
                                    BigDecimal horasUso, String estadoOperativo,
                                    boolean activa) {}

    public record MaquinariaFila(int idMaquinaria, int idCategoria,
                                 String codigoInventario, String categoria,
                                 String marca, String modelo, BigDecimal tonelaje,
                                 BigDecimal horasUso, String estadoOperativo,
                                 boolean activa, Timestamp fechaRegistro) {}
}

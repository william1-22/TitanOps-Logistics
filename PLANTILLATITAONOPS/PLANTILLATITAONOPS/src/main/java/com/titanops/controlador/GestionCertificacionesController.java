package com.titanops.controlador;

import com.titanops.dao.CategoriaMaquinariaDAO;
import com.titanops.dao.OperadorCertificacionDAO;
import com.titanops.dao.OperadorDAO;
import com.titanops.modelo.CategoriaMaquinaria;
import com.titanops.modelo.Operador;
import com.titanops.modelo.OperadorCertificacion;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Valida y coordina certificaciones de operadores. */
public class GestionCertificacionesController {
    public static final List<String> ESTADOS_VIGENCIA =
            List.of("TODAS", "VIGENTE", "VENCE PRONTO", "VENCIDA");

    private final OperadorCertificacionDAO certificacionDAO;
    private final OperadorDAO operadorDAO;
    private final CategoriaMaquinariaDAO categoriaDAO;

    public GestionCertificacionesController(OperadorCertificacionDAO certificacionDAO,
                                             OperadorDAO operadorDAO,
                                             CategoriaMaquinariaDAO categoriaDAO) {
        this.certificacionDAO = certificacionDAO;
        this.operadorDAO = operadorDAO;
        this.categoriaDAO = categoriaDAO;
    }

    public List<OperadorOpcion> listarOperadores() {
        List<OperadorOpcion> opciones = new ArrayList<>();
        for (Operador operador : operadorDAO.listarTodos()) {
            opciones.add(new OperadorOpcion(operador.getIdOperador(),
                    operador.getNombres(), operador.getApellidos(), operador.getDui(),
                    Boolean.TRUE.equals(operador.getActivo())));
        }
        return opciones;
    }

    public List<CategoriaOpcion> listarCategorias() {
        List<CategoriaOpcion> opciones = new ArrayList<>();
        for (CategoriaMaquinaria categoria : categoriaDAO.listarTodos()) {
            opciones.add(new CategoriaOpcion(categoria.getIdCategoria(),
                    categoria.getNombreCategoria(),
                    Boolean.TRUE.equals(categoria.getActivo())));
        }
        return opciones;
    }

    public ResultadoCreacion crearCertificacion(
            OperadorOpcion operador, CategoriaOpcion categoria,
            String numeroAcreditacion, LocalDate fechaExpedicion,
            LocalDate fechaVencimiento) {
        DatosNormalizados datos = normalizarDatos(operador, categoria,
                numeroAcreditacion, fechaExpedicion, fechaVencimiento);
        if (datos.error() != null) {
            return ResultadoCreacion.error(datos.error());
        }
        String errorRelaciones = validarRelaciones(datos.idOperador(),
                datos.idCategoria(), null);
        if (errorRelaciones != null) {
            return ResultadoCreacion.error(errorRelaciones);
        }
        if (certificacionDAO.existeNumero(datos.numeroAcreditacion(), null)) {
            return ResultadoCreacion.error(
                    "El número de acreditación ya está registrado.");
        }

        OperadorCertificacion certificacion = construirCertificacion(0, datos);
        if (!certificacionDAO.insertar(certificacion)) {
            return ResultadoCreacion.error("No fue posible guardar la certificación.");
        }
        return ResultadoCreacion.exito(certificacion);
    }

    public ResultadoOperacion actualizarCertificacion(
            int idCertificacion, OperadorOpcion operador, CategoriaOpcion categoria,
            String numeroAcreditacion, LocalDate fechaExpedicion,
            LocalDate fechaVencimiento) {
        DatosNormalizados datos = normalizarDatos(operador, categoria,
                numeroAcreditacion, fechaExpedicion, fechaVencimiento);
        if (datos.error() != null) {
            return ResultadoOperacion.error(datos.error());
        }
        OperadorCertificacion actual = certificacionDAO.obtenerPorId(idCertificacion);
        if (actual == null) {
            return ResultadoOperacion.error(
                    "La certificación seleccionada ya no existe.");
        }
        String errorRelaciones = validarRelaciones(datos.idOperador(),
                datos.idCategoria(), actual);
        if (errorRelaciones != null) {
            return ResultadoOperacion.error(errorRelaciones);
        }
        if (certificacionDAO.existeNumero(
                datos.numeroAcreditacion(), idCertificacion)) {
            return ResultadoOperacion.error(
                    "El número de acreditación ya está registrado.");
        }
        if (!certificacionDAO.actualizar(
                construirCertificacion(idCertificacion, datos))) {
            return ResultadoOperacion.error(
                    "No fue posible actualizar la certificación.");
        }
        return ResultadoOperacion.exito("Certificación actualizada correctamente.");
    }

    public ResultadoOperacion eliminarCertificacion(int idCertificacion) {
        if (certificacionDAO.obtenerPorId(idCertificacion) == null) {
            return ResultadoOperacion.error(
                    "La certificación seleccionada ya no existe.");
        }
        if (!certificacionDAO.eliminar(idCertificacion)) {
            return ResultadoOperacion.error("No fue posible eliminar la certificación.");
        }
        return ResultadoOperacion.exito("Certificación eliminada correctamente.");
    }

    public Optional<CertificacionEdicion> obtenerCertificacionEdicion(
            int idCertificacion) {
        OperadorCertificacion certificacion =
                certificacionDAO.obtenerPorId(idCertificacion);
        if (certificacion == null) {
            return Optional.empty();
        }
        return Optional.of(new CertificacionEdicion(
                certificacion.getIdCertificacion(), certificacion.getIdOperador(),
                certificacion.getIdCategoria(), certificacion.getNumeroAcreditacion(),
                certificacion.getFechaExpedicion().toLocalDate(),
                certificacion.getFechaVencimiento().toLocalDate()));
    }

    public List<CertificacionFila> listarCertificaciones(String filtro,
                                                          String estadoVigencia) {
        Map<Integer, Operador> operadores = new HashMap<>();
        for (Operador operador : operadorDAO.listarTodos()) {
            operadores.put(operador.getIdOperador(), operador);
        }
        Map<Integer, CategoriaMaquinaria> categorias = new HashMap<>();
        for (CategoriaMaquinaria categoria : categoriaDAO.listarTodos()) {
            categorias.put(categoria.getIdCategoria(), categoria);
        }

        String textoFiltro = normalizarTexto(filtro).toLowerCase(Locale.ROOT);
        String estadoFiltro = normalizarMayusculas(estadoVigencia);
        List<CertificacionFila> filas = new ArrayList<>();
        for (OperadorCertificacion certificacion : certificacionDAO.listarTodos()) {
            Operador operador = operadores.get(certificacion.getIdOperador());
            CategoriaMaquinaria categoria =
                    categorias.get(certificacion.getIdCategoria());
            String nombreOperador = operador == null
                    ? "ID " + certificacion.getIdOperador()
                    : (operador.getNombres() + " " + operador.getApellidos()).trim();
            String dui = operador == null ? "" : valorSeguro(operador.getDui());
            String nombreCategoria = categoria == null
                    ? "ID " + certificacion.getIdCategoria()
                    : categoria.getNombreCategoria();
            String vigencia = calcularVigencia(
                    certificacion.getFechaVencimiento().toLocalDate());
            CertificacionFila fila = new CertificacionFila(
                    certificacion.getIdCertificacion(), nombreOperador, dui,
                    nombreCategoria, certificacion.getNumeroAcreditacion(),
                    certificacion.getFechaExpedicion().toLocalDate(),
                    certificacion.getFechaVencimiento().toLocalDate(), vigencia);
            if ((estadoFiltro.isEmpty() || "TODAS".equals(estadoFiltro)
                    || estadoFiltro.equals(vigencia))
                    && (textoFiltro.isEmpty() || coincide(fila, textoFiltro))) {
                filas.add(fila);
            }
        }
        return filas;
    }

    public List<CertificacionFila> listarCertificaciones() {
        return listarCertificaciones("", "TODAS");
    }

    private DatosNormalizados normalizarDatos(
            OperadorOpcion operador, CategoriaOpcion categoria,
            String numeroAcreditacion, LocalDate fechaExpedicion,
            LocalDate fechaVencimiento) {
        String numero = normalizarMayusculas(numeroAcreditacion);
        String error = null;
        if (operador == null) {
            error = "Selecciona un operador.";
        } else if (categoria == null) {
            error = "Selecciona una categoría de maquinaria.";
        } else if (numero.isEmpty()) {
            error = "Ingresa el número de acreditación.";
        } else if (numero.length() > 100) {
            error = "El número de acreditación no puede exceder 100 caracteres.";
        } else if (fechaExpedicion == null) {
            error = "Selecciona la fecha de expedición.";
        } else if (fechaVencimiento == null) {
            error = "Selecciona la fecha de vencimiento.";
        } else if (fechaVencimiento.isBefore(fechaExpedicion)) {
            error = "La fecha de vencimiento no puede ser anterior a la expedición.";
        }
        return new DatosNormalizados(
                operador == null ? null : operador.idOperador(),
                categoria == null ? null : categoria.idCategoria(), numero,
                fechaExpedicion, fechaVencimiento, error);
    }

    private String validarRelaciones(Integer idOperador, Integer idCategoria,
                                      OperadorCertificacion actual) {
        Operador operador = operadorDAO.obtenerPorId(idOperador);
        if (operador == null) {
            return "El operador seleccionado ya no existe.";
        }
        boolean conservaOperador = actual != null
                && actual.getIdOperador() == idOperador;
        if (!Boolean.TRUE.equals(operador.getActivo()) && !conservaOperador) {
            return "El operador seleccionado está inactivo.";
        }

        CategoriaMaquinaria categoria = categoriaDAO.obtenerPorId(idCategoria);
        if (categoria == null) {
            return "La categoría seleccionada ya no existe.";
        }
        boolean conservaCategoria = actual != null
                && actual.getIdCategoria() == idCategoria;
        if (!Boolean.TRUE.equals(categoria.getActivo()) && !conservaCategoria) {
            return "La categoría seleccionada está inactiva.";
        }
        return null;
    }

    private OperadorCertificacion construirCertificacion(
            int idCertificacion, DatosNormalizados datos) {
        return new OperadorCertificacion(idCertificacion, datos.idOperador(),
                datos.idCategoria(), datos.numeroAcreditacion(),
                Date.valueOf(datos.fechaExpedicion()),
                Date.valueOf(datos.fechaVencimiento()));
    }

    private String calcularVigencia(LocalDate vencimiento) {
        LocalDate hoy = LocalDate.now();
        if (vencimiento.isBefore(hoy)) {
            return "VENCIDA";
        }
        if (!vencimiento.isAfter(hoy.plusDays(30))) {
            return "VENCE PRONTO";
        }
        return "VIGENTE";
    }

    private boolean coincide(CertificacionFila fila, String filtro) {
        String texto = String.join(" ", fila.operador(), fila.dui(),
                fila.categoria(), fila.numeroAcreditacion(), fila.vigencia())
                .toLowerCase(Locale.ROOT);
        return texto.contains(filtro);
    }

    private String normalizarMayusculas(String valor) {
        return normalizarTexto(valor).toUpperCase(Locale.ROOT);
    }

    private String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private record DatosNormalizados(Integer idOperador, Integer idCategoria,
                                     String numeroAcreditacion,
                                     LocalDate fechaExpedicion,
                                     LocalDate fechaVencimiento, String error) {}

    public record OperadorOpcion(int idOperador, String nombres, String apellidos,
                                 String dui, boolean activo) {
        @Override
        public String toString() {
            return (nombres + " " + apellidos).trim() + " - " + dui
                    + (activo ? "" : " (INACTIVO)");
        }
    }

    public record CategoriaOpcion(int idCategoria, String nombre, boolean activa) {
        @Override
        public String toString() {
            return nombre + (activa ? "" : " (INACTIVA)");
        }
    }

    public record CertificacionFila(int idCertificacion, String operador,
                                    String dui, String categoria,
                                    String numeroAcreditacion,
                                    LocalDate fechaExpedicion,
                                    LocalDate fechaVencimiento, String vigencia) {}

    public record CertificacionEdicion(int idCertificacion, int idOperador,
                                       int idCategoria, String numeroAcreditacion,
                                       LocalDate fechaExpedicion,
                                       LocalDate fechaVencimiento) {}

    public record ResultadoCreacion(boolean exitoso, String mensaje,
                                    OperadorCertificacion certificacion) {
        public static ResultadoCreacion exito(
                OperadorCertificacion certificacion) {
            return new ResultadoCreacion(true,
                    "Certificación creada correctamente.", certificacion);
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
}

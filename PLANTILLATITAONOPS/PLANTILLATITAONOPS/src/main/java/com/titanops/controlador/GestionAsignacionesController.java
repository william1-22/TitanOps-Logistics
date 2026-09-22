package com.titanops.controlador;

import com.titanops.dao.AsignacionDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.dao.OperadorDAO;
import com.titanops.dao.RutaDestinoDAO;
import com.titanops.modelo.Asignacion;
import com.titanops.modelo.Maquinaria;
import com.titanops.modelo.Operador;
import com.titanops.modelo.RutaDestino;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Valida y coordina asignaciones de maquinaria y operadores a rutas. */
public class GestionAsignacionesController {
    public static final List<String> ESTADOS =
            List.of("PROGRAMADA", "EN_CURSO", "FINALIZADA", "CANCELADA");

    private final AsignacionDAO asignacionDAO;
    private final MaquinariaDAO maquinariaDAO;
    private final OperadorDAO operadorDAO;
    private final RutaDestinoDAO rutaDAO;

    public GestionAsignacionesController(AsignacionDAO asignacionDAO,
                                          MaquinariaDAO maquinariaDAO,
                                          OperadorDAO operadorDAO,
                                          RutaDestinoDAO rutaDAO) {
        this.asignacionDAO = asignacionDAO;
        this.maquinariaDAO = maquinariaDAO;
        this.operadorDAO = operadorDAO;
        this.rutaDAO = rutaDAO;
    }

    public List<MaquinariaOpcion> listarMaquinariaDisponible() {
        List<MaquinariaOpcion> opciones = new ArrayList<>();
        for (Maquinaria maquinaria : maquinariaDAO.listarPorEstado("DISPONIBLE")) {
            if (Boolean.TRUE.equals(maquinaria.getActivo())) {
                opciones.add(new MaquinariaOpcion(maquinaria.getIdMaquinaria(),
                        maquinaria.getCodigoInventario(), maquinaria.getMarca(),
                        maquinaria.getModelo()));
            }
        }
        return opciones;
    }

    public List<OperadorOpcion> listarOperadoresDisponibles() {
        List<OperadorOpcion> opciones = new ArrayList<>();
        for (Operador operador : operadorDAO.listarActivos()) {
            if ("DISPONIBLE".equals(operador.getEstadoOperativo())) {
                opciones.add(new OperadorOpcion(operador.getIdOperador(),
                        operador.getNombres(), operador.getApellidos(),
                        operador.getDui()));
            }
        }
        return opciones;
    }

    public List<RutaOpcion> listarRutasAsignables() {
        List<RutaOpcion> opciones = new ArrayList<>();
        for (RutaDestino ruta : rutaDAO.listarAsignables()) {
            opciones.add(new RutaOpcion(ruta.getIdRuta(), ruta.getNombreProyecto(),
                    ruta.getPuntoOrigen(), ruta.getPuntoDestino()));
        }
        return opciones;
    }

    public ResultadoCreacion crearAsignacion(Integer idUsuarioRegistro,
                                               MaquinariaOpcion maquinaria,
                                               OperadorOpcion operador,
                                               RutaOpcion ruta,
                                               Timestamp fechaEstimadaRetorno,
                                               String observaciones) {
        if (idUsuarioRegistro == null || idUsuarioRegistro <= 0) {
            return ResultadoCreacion.error(
                    "Inicia sesión para registrar una asignación.");
        }
        if (maquinaria == null) {
            return ResultadoCreacion.error("Selecciona una maquinaria disponible.");
        }
        if (operador == null) {
            return ResultadoCreacion.error("Selecciona un operador disponible.");
        }
        if (ruta == null) {
            return ResultadoCreacion.error("Selecciona una ruta asignable.");
        }
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        if (fechaEstimadaRetorno == null || !fechaEstimadaRetorno.after(ahora)) {
            return ResultadoCreacion.error(
                    "La fecha estimada de retorno debe ser posterior a la fecha actual.");
        }
        String observacionesNormalizadas = normalizarOpcional(observaciones);
        if (observacionesNormalizadas != null
                && observacionesNormalizadas.length() > 2000) {
            return ResultadoCreacion.error(
                    "Las observaciones no pueden exceder 2000 caracteres.");
        }

        Asignacion asignacion = new Asignacion(0, maquinaria.idMaquinaria(),
                operador.idOperador(), ruta.idRuta(), idUsuarioRegistro, ahora,
                fechaEstimadaRetorno, null, "EN_CURSO", observacionesNormalizadas);
        if (!asignacionDAO.insertarConReservaRecursos(asignacion)) {
            return ResultadoCreacion.error(
                    "No fue posible crear la asignación. Actualiza los recursos e inténtalo de nuevo.");
        }
        return ResultadoCreacion.exito(asignacion);
    }

    public ResultadoOperacion finalizarAsignacion(int idAsignacion) {
        Asignacion asignacion = asignacionDAO.obtenerPorId(idAsignacion);
        if (asignacion == null) {
            return ResultadoOperacion.error("La asignación seleccionada ya no existe.");
        }
        if (!"EN_CURSO".equals(asignacion.getEstadoAsignacion())) {
            return ResultadoOperacion.error(
                    "Solo se pueden finalizar asignaciones en curso.");
        }
        if (!asignacionDAO.finalizar(idAsignacion,
                new Timestamp(System.currentTimeMillis()))) {
            return ResultadoOperacion.error("No fue posible finalizar la asignación.");
        }
        return ResultadoOperacion.exito(
                "Asignación finalizada y recursos liberados correctamente.");
    }

    public ResultadoOperacion cancelarAsignacion(int idAsignacion) {
        Asignacion asignacion = asignacionDAO.obtenerPorId(idAsignacion);
        if (asignacion == null) {
            return ResultadoOperacion.error("La asignación seleccionada ya no existe.");
        }
        if (!"EN_CURSO".equals(asignacion.getEstadoAsignacion())) {
            return ResultadoOperacion.error(
                    "Solo se pueden cancelar asignaciones en curso.");
        }
        if (!asignacionDAO.eliminar(idAsignacion)) {
            return ResultadoOperacion.error("No fue posible cancelar la asignación.");
        }
        return ResultadoOperacion.exito(
                "Asignación cancelada y recursos liberados correctamente.");
    }

    public List<AsignacionFila> listarAsignaciones(String filtro, String estado) {
        String estadoNormalizado = normalizarMayusculas(estado);
        List<Asignacion> asignaciones = estadoNormalizado.isEmpty()
                || "TODOS".equals(estadoNormalizado)
                ? asignacionDAO.listarTodos()
                : asignacionDAO.listarPorEstado(estadoNormalizado);

        Map<Integer, Maquinaria> maquinariaPorId = indexarMaquinaria();
        Map<Integer, Operador> operadoresPorId = indexarOperadores();
        Map<Integer, RutaDestino> rutasPorId = indexarRutas();
        String filtroNormalizado = normalizarTexto(filtro).toLowerCase(Locale.ROOT);
        List<AsignacionFila> filas = new ArrayList<>();
        for (Asignacion asignacion : asignaciones) {
            Maquinaria maquinaria = maquinariaPorId.get(asignacion.getIdMaquinaria());
            Operador operador = operadoresPorId.get(asignacion.getIdOperador());
            RutaDestino ruta = rutasPorId.get(asignacion.getIdRuta());
            String codigo = maquinaria == null ? "ID " + asignacion.getIdMaquinaria()
                    : maquinaria.getCodigoInventario();
            String nombreOperador = operador == null
                    ? "ID " + asignacion.getIdOperador()
                    : (operador.getNombres() + " " + operador.getApellidos()).trim();
            String proyecto = ruta == null ? "ID " + asignacion.getIdRuta()
                    : ruta.getNombreProyecto();
            AsignacionFila fila = new AsignacionFila(asignacion.getIdAsignacion(),
                    codigo, nombreOperador, proyecto,
                    asignacion.getFechaAsignacion(),
                    asignacion.getFechaEstimadaRetorno(),
                    asignacion.getFechaRetornoReal(),
                    asignacion.getEstadoAsignacion(), asignacion.getObservaciones());
            if (filtroNormalizado.isEmpty() || coincide(fila, filtroNormalizado)) {
                filas.add(fila);
            }
        }
        return filas;
    }

    public List<AsignacionFila> listarAsignaciones() {
        return listarAsignaciones("", "TODOS");
    }

    private Map<Integer, Maquinaria> indexarMaquinaria() {
        Map<Integer, Maquinaria> indice = new HashMap<>();
        for (Maquinaria maquinaria : maquinariaDAO.listarTodos()) {
            indice.put(maquinaria.getIdMaquinaria(), maquinaria);
        }
        return indice;
    }

    private Map<Integer, Operador> indexarOperadores() {
        Map<Integer, Operador> indice = new HashMap<>();
        for (Operador operador : operadorDAO.listarTodos()) {
            indice.put(operador.getIdOperador(), operador);
        }
        return indice;
    }

    private Map<Integer, RutaDestino> indexarRutas() {
        Map<Integer, RutaDestino> indice = new HashMap<>();
        for (RutaDestino ruta : rutaDAO.listarTodos()) {
            indice.put(ruta.getIdRuta(), ruta);
        }
        return indice;
    }

    private boolean coincide(AsignacionFila fila, String filtro) {
        String texto = String.join(" ", fila.codigoMaquinaria(), fila.operador(),
                fila.proyecto(), valorSeguro(fila.estado()),
                valorSeguro(fila.observaciones())).toLowerCase(Locale.ROOT);
        return texto.contains(filtro);
    }

    private String normalizarMayusculas(String valor) {
        return normalizarTexto(valor).toUpperCase(Locale.ROOT);
    }

    private String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }

    private String normalizarOpcional(String valor) {
        String texto = normalizarTexto(valor);
        return texto.isEmpty() ? null : texto;
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    public record MaquinariaOpcion(int idMaquinaria, String codigoInventario,
                                   String marca, String modelo) {
        @Override
        public String toString() {
            String detalle = String.join(" ", valor(marca), valor(modelo)).trim();
            return detalle.isEmpty() ? codigoInventario
                    : codigoInventario + " - " + detalle;
        }
    }

    public record OperadorOpcion(int idOperador, String nombres, String apellidos,
                                 String dui) {
        @Override
        public String toString() {
            return (nombres + " " + apellidos).trim() + " - " + dui;
        }
    }

    public record RutaOpcion(int idRuta, String nombreProyecto, String puntoOrigen,
                             String puntoDestino) {
        @Override
        public String toString() {
            return nombreProyecto + " - " + puntoOrigen + " → " + puntoDestino;
        }
    }

    private static String valor(String texto) {
        return texto == null ? "" : texto;
    }

    public record AsignacionFila(int idAsignacion, String codigoMaquinaria,
                                 String operador, String proyecto,
                                 Timestamp fechaAsignacion,
                                 Timestamp fechaEstimadaRetorno,
                                 Timestamp fechaRetornoReal, String estado,
                                 String observaciones) {}

    public record ResultadoCreacion(boolean exitoso, String mensaje,
                                    Asignacion asignacion) {
        public static ResultadoCreacion exito(Asignacion asignacion) {
            return new ResultadoCreacion(true,
                    "Asignación creada y recursos reservados correctamente.", asignacion);
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

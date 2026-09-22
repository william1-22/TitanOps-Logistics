package com.titanops.controlador;

import com.titanops.dao.MantenimientoDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.modelo.Mantenimiento;
import com.titanops.modelo.Maquinaria;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Coordina el control de maquinaria y sus mantenimientos. */
public class GestionControlReportesController {
    public static final List<String> TIPOS_MANTENIMIENTO =
            List.of("PREVENTIVO", "CORRECTIVO");
    public static final List<String> ESTADOS_MANTENIMIENTO =
            List.of("TODOS", "EN_PROCESO", "FINALIZADO", "CANCELADO");
    public static final List<String> ESTADOS_MAQUINARIA =
            List.of("TODOS", "DISPONIBLE", "EN_RUTA", "MANTENIMIENTO", "INACTIVA");

    private final MantenimientoDAO mantenimientoDAO;
    private final MaquinariaDAO maquinariaDAO;

    public GestionControlReportesController(MantenimientoDAO mantenimientoDAO,
                                             MaquinariaDAO maquinariaDAO) {
        this.mantenimientoDAO = mantenimientoDAO;
        this.maquinariaDAO = maquinariaDAO;
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

    public ResultadoCreacion crearMantenimiento(
            Integer idUsuarioRegistro, MaquinariaOpcion maquinaria,
            String tipoMantenimiento, Timestamp fechaSalidaEstimada,
            String diagnostico, String costo, String tallerResponsable) {
        if (idUsuarioRegistro == null || idUsuarioRegistro <= 0) {
            return ResultadoCreacion.error(
                    "Inicia sesión para registrar un mantenimiento.");
        }
        if (maquinaria == null) {
            return ResultadoCreacion.error(
                    "Selecciona una maquinaria disponible.");
        }
        Timestamp fechaIngreso = new Timestamp(System.currentTimeMillis());
        DatosNormalizados datos = normalizarDatos(tipoMantenimiento,
                fechaIngreso, fechaSalidaEstimada, diagnostico, costo,
                tallerResponsable);
        if (datos.error() != null) {
            return ResultadoCreacion.error(datos.error());
        }

        Mantenimiento mantenimiento = new Mantenimiento(0,
                maquinaria.idMaquinaria(), idUsuarioRegistro,
                datos.tipoMantenimiento(), fechaIngreso,
                datos.fechaSalidaEstimada(), null, datos.diagnostico(),
                datos.costo(), "EN_PROCESO", datos.tallerResponsable());
        if (!mantenimientoDAO.insertarConReservaMaquinaria(mantenimiento)) {
            return ResultadoCreacion.error(
                    "No fue posible iniciar el mantenimiento. Actualiza la maquinaria e inténtalo de nuevo.");
        }
        return ResultadoCreacion.exito(mantenimiento);
    }

    public ResultadoOperacion actualizarMantenimiento(
            int idMantenimiento, String tipoMantenimiento,
            Timestamp fechaSalidaEstimada, String diagnostico, String costo,
            String tallerResponsable) {
        Mantenimiento actual = mantenimientoDAO.obtenerPorId(idMantenimiento);
        if (actual == null) {
            return ResultadoOperacion.error(
                    "El mantenimiento seleccionado ya no existe.");
        }
        if (!"EN_PROCESO".equals(actual.getEstadoMantenimiento())) {
            return ResultadoOperacion.error(
                    "Solo se pueden editar mantenimientos en proceso.");
        }
        DatosNormalizados datos = normalizarDatos(tipoMantenimiento,
                actual.getFechaIngreso(), fechaSalidaEstimada, diagnostico,
                costo, tallerResponsable);
        if (datos.error() != null) {
            return ResultadoOperacion.error(datos.error());
        }

        Mantenimiento actualizado = new Mantenimiento(idMantenimiento,
                actual.getIdMaquinaria(), actual.getIdUsuarioRegistro(),
                datos.tipoMantenimiento(), actual.getFechaIngreso(),
                datos.fechaSalidaEstimada(), actual.getFechaSalidaReal(),
                datos.diagnostico(), datos.costo(),
                actual.getEstadoMantenimiento(), datos.tallerResponsable());
        if (!mantenimientoDAO.actualizar(actualizado)) {
            return ResultadoOperacion.error(
                    "No fue posible actualizar el mantenimiento.");
        }
        return ResultadoOperacion.exito("Mantenimiento actualizado correctamente.");
    }

    public ResultadoOperacion finalizarMantenimiento(int idMantenimiento) {
        Mantenimiento mantenimiento = mantenimientoDAO.obtenerPorId(idMantenimiento);
        if (mantenimiento == null) {
            return ResultadoOperacion.error(
                    "El mantenimiento seleccionado ya no existe.");
        }
        if (!"EN_PROCESO".equals(mantenimiento.getEstadoMantenimiento())) {
            return ResultadoOperacion.error(
                    "Solo se pueden finalizar mantenimientos en proceso.");
        }
        if (!mantenimientoDAO.finalizar(idMantenimiento,
                new Timestamp(System.currentTimeMillis()))) {
            return ResultadoOperacion.error(
                    "No fue posible finalizar el mantenimiento.");
        }
        return ResultadoOperacion.exito(
                "Mantenimiento finalizado y maquinaria liberada correctamente.");
    }

    public ResultadoOperacion cancelarMantenimiento(int idMantenimiento) {
        Mantenimiento mantenimiento = mantenimientoDAO.obtenerPorId(idMantenimiento);
        if (mantenimiento == null) {
            return ResultadoOperacion.error(
                    "El mantenimiento seleccionado ya no existe.");
        }
        if (!"EN_PROCESO".equals(mantenimiento.getEstadoMantenimiento())) {
            return ResultadoOperacion.error(
                    "Solo se pueden cancelar mantenimientos en proceso.");
        }
        if (!mantenimientoDAO.eliminar(idMantenimiento)) {
            return ResultadoOperacion.error(
                    "No fue posible cancelar el mantenimiento.");
        }
        return ResultadoOperacion.exito(
                "Mantenimiento cancelado y maquinaria liberada correctamente.");
    }

    public Optional<MantenimientoEdicion> obtenerMantenimientoEdicion(
            int idMantenimiento) {
        Mantenimiento mantenimiento = mantenimientoDAO.obtenerPorId(idMantenimiento);
        if (mantenimiento == null) {
            return Optional.empty();
        }
        return Optional.of(new MantenimientoEdicion(
                mantenimiento.getIdMantenimiento(),
                mantenimiento.getTipoMantenimiento(),
                mantenimiento.getFechaSalidaEstimada(),
                mantenimiento.getDiagnostico(), mantenimiento.getCosto(),
                mantenimiento.getTallerResponsable(),
                mantenimiento.getEstadoMantenimiento()));
    }

    public List<MantenimientoFila> listarMantenimientos(String filtro,
                                                         String estado) {
        String estadoNormalizado = normalizarMayusculas(estado);
        List<Mantenimiento> mantenimientos = estadoNormalizado.isEmpty()
                || "TODOS".equals(estadoNormalizado)
                ? mantenimientoDAO.listarTodos()
                : mantenimientoDAO.listarPorEstado(estadoNormalizado);
        Map<Integer, Maquinaria> maquinariaPorId = indexarMaquinaria();
        String textoFiltro = normalizarTexto(filtro).toLowerCase(Locale.ROOT);
        List<MantenimientoFila> filas = new ArrayList<>();
        for (Mantenimiento mantenimiento : mantenimientos) {
            Maquinaria maquinaria =
                    maquinariaPorId.get(mantenimiento.getIdMaquinaria());
            String codigo = maquinaria == null
                    ? "ID " + mantenimiento.getIdMaquinaria()
                    : maquinaria.getCodigoInventario();
            String descripcion = maquinaria == null ? ""
                    : descripcionMaquinaria(maquinaria);
            MantenimientoFila fila = new MantenimientoFila(
                    mantenimiento.getIdMantenimiento(), codigo, descripcion,
                    mantenimiento.getTipoMantenimiento(),
                    mantenimiento.getFechaIngreso(),
                    mantenimiento.getFechaSalidaEstimada(),
                    mantenimiento.getFechaSalidaReal(),
                    mantenimiento.getDiagnostico(), mantenimiento.getCosto(),
                    mantenimiento.getEstadoMantenimiento(),
                    mantenimiento.getTallerResponsable());
            if (textoFiltro.isEmpty() || coincide(fila, textoFiltro)) {
                filas.add(fila);
            }
        }
        return filas;
    }

    public List<EstadoMaquinariaFila> listarEstadoMaquinaria(String filtro,
                                                              String estado) {
        String estadoNormalizado = normalizarMayusculas(estado);
        String textoFiltro = normalizarTexto(filtro).toLowerCase(Locale.ROOT);
        List<EstadoMaquinariaFila> filas = new ArrayList<>();
        for (Maquinaria maquinaria : maquinariaDAO.listarTodos()) {
            boolean activa = Boolean.TRUE.equals(maquinaria.getActivo());
            String estadoVisible = activa ? maquinaria.getEstadoOperativo() : "INACTIVA";
            EstadoMaquinariaFila fila = new EstadoMaquinariaFila(
                    maquinaria.getIdMaquinaria(), maquinaria.getCodigoInventario(),
                    descripcionMaquinaria(maquinaria), estadoVisible, activa,
                    maquinaria.getFechaRegistro());
            if ((estadoNormalizado.isEmpty() || "TODOS".equals(estadoNormalizado)
                    || estadoNormalizado.equals(estadoVisible))
                    && (textoFiltro.isEmpty()
                            || coincideEstado(fila, textoFiltro))) {
                filas.add(fila);
            }
        }
        return filas;
    }

    public ResumenControl obtenerResumenControl() {
        int disponibles = 0;
        int enRuta = 0;
        int mantenimiento = 0;
        int inactivas = 0;
        for (Maquinaria maquinaria : maquinariaDAO.listarTodos()) {
            if (!Boolean.TRUE.equals(maquinaria.getActivo())) {
                inactivas++;
            } else if ("DISPONIBLE".equals(maquinaria.getEstadoOperativo())) {
                disponibles++;
            } else if ("EN_RUTA".equals(maquinaria.getEstadoOperativo())) {
                enRuta++;
            } else if ("MANTENIMIENTO".equals(
                    maquinaria.getEstadoOperativo())) {
                mantenimiento++;
            }
        }
        return new ResumenControl(disponibles, enRuta, mantenimiento, inactivas);
    }

    private DatosNormalizados normalizarDatos(
            String tipoMantenimiento, Timestamp fechaIngreso,
            Timestamp fechaSalidaEstimada, String diagnostico, String costo,
            String tallerResponsable) {
        String tipo = normalizarMayusculas(tipoMantenimiento);
        String diagnosticoNormalizado = normalizarOpcional(diagnostico);
        String taller = normalizarOpcional(tallerResponsable);
        ResultadoDecimal costoNormalizado = convertirCosto(costo);
        String error = null;
        if (!TIPOS_MANTENIMIENTO.contains(tipo)) {
            error = "Selecciona un tipo de mantenimiento válido.";
        } else if (fechaSalidaEstimada != null && fechaIngreso != null
                && !fechaSalidaEstimada.after(fechaIngreso)) {
            error = "La salida estimada debe ser posterior al ingreso.";
        } else if (diagnosticoNormalizado != null
                && diagnosticoNormalizado.length() > 5000) {
            error = "El diagnóstico no puede exceder 5000 caracteres.";
        } else if (costoNormalizado.error() != null) {
            error = costoNormalizado.error();
        } else if (taller != null && taller.length() > 150) {
            error = "El taller responsable no puede exceder 150 caracteres.";
        }
        return new DatosNormalizados(tipo, fechaSalidaEstimada,
                diagnosticoNormalizado, costoNormalizado.valor(), taller, error);
    }

    private ResultadoDecimal convertirCosto(String texto) {
        String valor = normalizarTexto(texto).replace(',', '.');
        if (valor.isEmpty()) {
            return new ResultadoDecimal(null, null);
        }
        try {
            BigDecimal costo = new BigDecimal(valor).stripTrailingZeros();
            if (costo.signum() < 0) {
                return new ResultadoDecimal(null,
                        "El costo no puede ser negativo.");
            }
            int enteros = costo.precision() - costo.scale();
            if (costo.scale() > 2 || enteros > 8) {
                return new ResultadoDecimal(null,
                        "El costo admite hasta 8 enteros y 2 decimales.");
            }
            return new ResultadoDecimal(costo, null);
        } catch (NumberFormatException exception) {
            return new ResultadoDecimal(null, "Ingresa un costo válido.");
        }
    }

    private Map<Integer, Maquinaria> indexarMaquinaria() {
        Map<Integer, Maquinaria> indice = new HashMap<>();
        for (Maquinaria maquinaria : maquinariaDAO.listarTodos()) {
            indice.put(maquinaria.getIdMaquinaria(), maquinaria);
        }
        return indice;
    }

    private String descripcionMaquinaria(Maquinaria maquinaria) {
        return String.join(" ", valorSeguro(maquinaria.getMarca()),
                valorSeguro(maquinaria.getModelo())).trim();
    }

    private boolean coincide(MantenimientoFila fila, String filtro) {
        return String.join(" ", fila.codigoMaquinaria(), fila.descripcionMaquinaria(),
                fila.tipoMantenimiento(), valorSeguro(fila.diagnostico()),
                valorSeguro(fila.estadoMantenimiento()),
                valorSeguro(fila.tallerResponsable())).toLowerCase(Locale.ROOT)
                .contains(filtro);
    }

    private boolean coincideEstado(EstadoMaquinariaFila fila, String filtro) {
        return String.join(" ", fila.codigoInventario(), fila.descripcion(),
                fila.estado()).toLowerCase(Locale.ROOT).contains(filtro);
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

    private record DatosNormalizados(String tipoMantenimiento,
                                     Timestamp fechaSalidaEstimada,
                                     String diagnostico, BigDecimal costo,
                                     String tallerResponsable, String error) {}

    private record ResultadoDecimal(BigDecimal valor, String error) {}

    public record MaquinariaOpcion(int idMaquinaria, String codigoInventario,
                                   String marca, String modelo) {
        @Override
        public String toString() {
            String descripcion = String.join(" ", valor(marca), valor(modelo)).trim();
            return descripcion.isEmpty() ? codigoInventario
                    : codigoInventario + " - " + descripcion;
        }
    }

    private static String valor(String texto) {
        return texto == null ? "" : texto;
    }

    public record MantenimientoFila(int idMantenimiento, String codigoMaquinaria,
                                    String descripcionMaquinaria,
                                    String tipoMantenimiento,
                                    Timestamp fechaIngreso,
                                    Timestamp fechaSalidaEstimada,
                                    Timestamp fechaSalidaReal, String diagnostico,
                                    BigDecimal costo, String estadoMantenimiento,
                                    String tallerResponsable) {}

    public record EstadoMaquinariaFila(int idMaquinaria, String codigoInventario,
                                       String descripcion, String estado,
                                       boolean activa, Timestamp fechaRegistro) {}

    public record ResumenControl(int disponibles, int enRuta,
                                 int mantenimiento, int inactivas) {
        public int total() {
            return disponibles + enRuta + mantenimiento + inactivas;
        }
    }

    public record MantenimientoEdicion(int idMantenimiento,
                                       String tipoMantenimiento,
                                       Timestamp fechaSalidaEstimada,
                                       String diagnostico, BigDecimal costo,
                                       String tallerResponsable,
                                       String estadoMantenimiento) {}

    public record ResultadoCreacion(boolean exitoso, String mensaje,
                                    Mantenimiento mantenimiento) {
        public static ResultadoCreacion exito(Mantenimiento mantenimiento) {
            return new ResultadoCreacion(true,
                    "Mantenimiento iniciado y maquinaria reservada correctamente.",
                    mantenimiento);
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

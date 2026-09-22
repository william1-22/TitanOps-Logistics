package com.titanops.controlador;

import com.titanops.dao.RutaDestinoDAO;
import com.titanops.modelo.RutaDestino;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Valida y coordina la creación, edición y cancelación de rutas. */
public class GestionRutasController {
    public static final List<String> ESTADOS =
            List.of("PLANIFICADA", "EN_CURSO", "FINALIZADA", "CANCELADA");

    private final RutaDestinoDAO rutaDAO;

    public GestionRutasController(RutaDestinoDAO rutaDAO) {
        this.rutaDAO = rutaDAO;
    }

    public ResultadoCreacion crearRuta(String nombreProyecto, String puntoOrigen,
                                        String puntoDestino, String distanciaKm) {
        DatosNormalizados datos = normalizarDatos(nombreProyecto, puntoOrigen,
                puntoDestino, distanciaKm, "PLANIFICADA");
        if (datos.error() != null) {
            return ResultadoCreacion.error(datos.error());
        }

        RutaDestino ruta = construirRuta(0, datos);
        if (!rutaDAO.insertar(ruta)) {
            return ResultadoCreacion.error("No fue posible guardar la ruta.");
        }
        return ResultadoCreacion.exito(ruta);
    }

    public ResultadoOperacion actualizarRuta(int idRuta, String nombreProyecto,
                                               String puntoOrigen, String puntoDestino,
                                               String distanciaKm, String estado) {
        DatosNormalizados datos = normalizarDatos(nombreProyecto, puntoOrigen,
                puntoDestino, distanciaKm, estado);
        if (datos.error() != null) {
            return ResultadoOperacion.error(datos.error());
        }

        RutaDestino actual = rutaDAO.obtenerPorId(idRuta);
        if (actual == null) {
            return ResultadoOperacion.error("La ruta seleccionada ya no existe.");
        }
        if (rutaDAO.tieneAsignacionesEnCurso(idRuta)
                && !"EN_CURSO".equals(datos.estado())) {
            return ResultadoOperacion.error(
                    "La ruta tiene asignaciones en curso y debe conservar ese estado.");
        }

        if (!rutaDAO.actualizar(construirRuta(idRuta, datos))) {
            return ResultadoOperacion.error("No fue posible actualizar la ruta.");
        }
        return ResultadoOperacion.exito("Ruta actualizada correctamente.");
    }

    public ResultadoOperacion cancelarRuta(int idRuta) {
        RutaDestino ruta = rutaDAO.obtenerPorId(idRuta);
        if (ruta == null) {
            return ResultadoOperacion.error("La ruta seleccionada ya no existe.");
        }
        if ("CANCELADA".equals(ruta.getEstado())) {
            return ResultadoOperacion.exito("La ruta ya estaba cancelada.");
        }
        if (rutaDAO.tieneAsignacionesEnCurso(idRuta)) {
            return ResultadoOperacion.error(
                    "No se puede cancelar una ruta con asignaciones en curso.");
        }
        if (!rutaDAO.eliminar(idRuta)) {
            return ResultadoOperacion.error("No fue posible cancelar la ruta.");
        }
        return ResultadoOperacion.exito("Ruta cancelada correctamente.");
    }

    public Optional<RutaEdicion> obtenerRutaEdicion(int idRuta) {
        RutaDestino ruta = rutaDAO.obtenerPorId(idRuta);
        if (ruta == null) {
            return Optional.empty();
        }
        return Optional.of(new RutaEdicion(ruta.getIdRuta(), ruta.getNombreProyecto(),
                ruta.getPuntoOrigen(), ruta.getPuntoDestino(), ruta.getDistanciaKm(),
                ruta.getEstado()));
    }

    public List<RutaFila> listarRutas(String filtro, String estado) {
        String estadoNormalizado = normalizarMayusculas(estado);
        List<RutaDestino> rutas = estadoNormalizado.isEmpty()
                || "TODOS".equals(estadoNormalizado)
                ? rutaDAO.listarTodos() : rutaDAO.listarPorEstado(estadoNormalizado);
        String filtroNormalizado = normalizarTexto(filtro).toLowerCase(Locale.ROOT);
        List<RutaFila> filas = new ArrayList<>();
        for (RutaDestino ruta : rutas) {
            if (filtroNormalizado.isEmpty() || coincide(ruta, filtroNormalizado)) {
                filas.add(new RutaFila(ruta.getIdRuta(), ruta.getNombreProyecto(),
                        ruta.getPuntoOrigen(), ruta.getPuntoDestino(),
                        ruta.getDistanciaKm(), ruta.getEstado()));
            }
        }
        return filas;
    }

    public List<RutaFila> listarRutas() {
        return listarRutas("", "TODOS");
    }

    private DatosNormalizados normalizarDatos(String nombreProyecto,
                                               String puntoOrigen,
                                               String puntoDestino,
                                               String distanciaKm,
                                               String estado) {
        String nombre = normalizarTexto(nombreProyecto);
        String origen = normalizarTexto(puntoOrigen);
        String destino = normalizarTexto(puntoDestino);
        String estadoNormalizado = normalizarMayusculas(estado);
        ResultadoDecimal distancia = convertirDistancia(distanciaKm);

        String error = null;
        if (nombre.isEmpty()) {
            error = "Ingresa el nombre del proyecto.";
        } else if (nombre.length() > 150) {
            error = "El nombre del proyecto no puede exceder 150 caracteres.";
        } else if (origen.isEmpty()) {
            error = "Ingresa el punto de origen.";
        } else if (origen.length() > 255) {
            error = "El punto de origen no puede exceder 255 caracteres.";
        } else if (destino.isEmpty()) {
            error = "Ingresa el punto de destino.";
        } else if (destino.length() > 255) {
            error = "El punto de destino no puede exceder 255 caracteres.";
        } else if (distancia.error() != null) {
            error = distancia.error();
        } else if (!ESTADOS.contains(estadoNormalizado)) {
            error = "Selecciona un estado de ruta válido.";
        }
        return new DatosNormalizados(nombre, origen, destino, distancia.valor(),
                estadoNormalizado, error);
    }

    private ResultadoDecimal convertirDistancia(String texto) {
        String valor = normalizarTexto(texto).replace(',', '.');
        if (valor.isEmpty()) {
            return new ResultadoDecimal(null, null);
        }
        try {
            BigDecimal distancia = new BigDecimal(valor).stripTrailingZeros();
            if (distancia.signum() < 0) {
                return new ResultadoDecimal(null, "La distancia no puede ser negativa.");
            }
            int enteros = distancia.precision() - distancia.scale();
            if (distancia.scale() > 2 || enteros > 8) {
                return new ResultadoDecimal(null,
                        "La distancia admite hasta 8 enteros y 2 decimales.");
            }
            return new ResultadoDecimal(distancia, null);
        } catch (NumberFormatException exception) {
            return new ResultadoDecimal(null, "Ingresa una distancia válida.");
        }
    }

    private RutaDestino construirRuta(int idRuta, DatosNormalizados datos) {
        return new RutaDestino(idRuta, datos.nombreProyecto(), datos.puntoOrigen(),
                datos.puntoDestino(), datos.distanciaKm(), datos.estado());
    }

    private boolean coincide(RutaDestino ruta, String filtro) {
        String texto = String.join(" ", valorSeguro(ruta.getNombreProyecto()),
                valorSeguro(ruta.getPuntoOrigen()), valorSeguro(ruta.getPuntoDestino()),
                valorSeguro(ruta.getEstado())).toLowerCase(Locale.ROOT);
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

    private record DatosNormalizados(String nombreProyecto, String puntoOrigen,
                                     String puntoDestino, BigDecimal distanciaKm,
                                     String estado, String error) {}

    private record ResultadoDecimal(BigDecimal valor, String error) {}

    public record RutaFila(int idRuta, String nombreProyecto, String puntoOrigen,
                           String puntoDestino, BigDecimal distanciaKm, String estado) {}

    public record RutaEdicion(int idRuta, String nombreProyecto, String puntoOrigen,
                              String puntoDestino, BigDecimal distanciaKm,
                              String estado) {}

    public record ResultadoCreacion(boolean exitoso, String mensaje, RutaDestino ruta) {
        public static ResultadoCreacion exito(RutaDestino ruta) {
            return new ResultadoCreacion(true, "Ruta creada correctamente.", ruta);
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

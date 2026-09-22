package com.titanops.controlador;

import com.titanops.dao.OperadorDAO;
import com.titanops.modelo.Operador;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Valida y coordina las operaciones de gestión de operadores. */
public class GestionOperadoresController {
    public static final List<String> TURNOS =
            List.of("DIURNO", "NOCTURNO", "ROTATIVO");
    public static final List<String> ESTADOS_OPERATIVOS =
            List.of("DISPONIBLE", "EN_RUTA", "DESCANSO", "INACTIVO");
    public static final List<String> LICENCIAS_SUGERIDAS =
            List.of("LIVIANA", "PESADA", "MOTORISTA");

    private final OperadorDAO operadorDAO;

    public GestionOperadoresController(OperadorDAO operadorDAO) {
        this.operadorDAO = operadorDAO;
    }

    public ResultadoCreacion crearOperador(String nombres, String apellidos, String dui,
                                            String licenciaTipo, String turno,
                                            String telefono, String estadoOperativo) {
        DatosNormalizados datos = normalizarDatos(
                nombres, apellidos, dui, licenciaTipo, turno, telefono, estadoOperativo);
        if (datos.error() != null) {
            return ResultadoCreacion.error(datos.error());
        }
        if ("INACTIVO".equals(datos.estadoOperativo())) {
            return ResultadoCreacion.error(
                    "Utiliza la acción Desactivar para marcar un operador como inactivo.");
        }
        if (operadorDAO.existeDui(datos.dui(), null)) {
            return ResultadoCreacion.error("El DUI ya está registrado.");
        }

        Operador operador = construirOperador(0, datos, true);
        if (!operadorDAO.insertar(operador)) {
            return ResultadoCreacion.error("No fue posible guardar el operador.");
        }
        return ResultadoCreacion.exito(operador);
    }

    public ResultadoOperacion actualizarOperador(int idOperador, String nombres,
                                                  String apellidos, String dui,
                                                  String licenciaTipo, String turno,
                                                  String telefono,
                                                  String estadoOperativo) {
        DatosNormalizados datos = normalizarDatos(
                nombres, apellidos, dui, licenciaTipo, turno, telefono, estadoOperativo);
        if (datos.error() != null) {
            return ResultadoOperacion.error(datos.error());
        }

        Operador actual = operadorDAO.obtenerPorId(idOperador);
        if (actual == null) {
            return ResultadoOperacion.error("El operador seleccionado ya no existe.");
        }
        if (operadorDAO.existeDui(datos.dui(), idOperador)) {
            return ResultadoOperacion.error("El DUI ya está registrado.");
        }

        boolean activo = Boolean.TRUE.equals(actual.getActivo());
        if (activo && "INACTIVO".equals(datos.estadoOperativo())) {
            return ResultadoOperacion.error(
                    "Utiliza la acción Desactivar para marcar un operador como inactivo.");
        }
        DatosNormalizados datosFinales = activo ? datos : datos.conEstado("INACTIVO");
        Operador operador = construirOperador(idOperador, datosFinales, activo);
        operador.setFechaRegistro(actual.getFechaRegistro());
        if (!operadorDAO.actualizar(operador)) {
            return ResultadoOperacion.error("No fue posible actualizar el operador.");
        }
        return ResultadoOperacion.exito("Operador actualizado correctamente.");
    }

    public ResultadoOperacion cambiarEstadoOperador(int idOperador, boolean activar) {
        Operador operador = operadorDAO.obtenerPorId(idOperador);
        if (operador == null) {
            return ResultadoOperacion.error("El operador seleccionado ya no existe.");
        }

        boolean activo = Boolean.TRUE.equals(operador.getActivo());
        if (activar == activo) {
            return ResultadoOperacion.exito(
                    activar ? "El operador ya estaba activo."
                            : "El operador ya estaba inactivo.");
        }

        boolean actualizado = activar
                ? operadorDAO.reactivar(idOperador) : operadorDAO.eliminar(idOperador);
        if (!actualizado) {
            return ResultadoOperacion.error(
                    activar ? "No fue posible reactivar el operador."
                            : "No fue posible desactivar el operador.");
        }
        return ResultadoOperacion.exito(
                activar ? "Operador reactivado correctamente."
                        : "Operador desactivado correctamente.");
    }

    public Optional<OperadorEdicion> obtenerOperadorEdicion(int idOperador) {
        Operador operador = operadorDAO.obtenerPorId(idOperador);
        if (operador == null) {
            return Optional.empty();
        }
        return Optional.of(new OperadorEdicion(
                operador.getIdOperador(), operador.getNombres(), operador.getApellidos(),
                operador.getDui(), operador.getLicenciaTipo(), operador.getTurno(),
                operador.getTelefono(), operador.getEstadoOperativo(),
                Boolean.TRUE.equals(operador.getActivo())));
    }

    public List<OperadorFila> listarOperadores(String filtro) {
        String filtroNormalizado = normalizarTexto(filtro).toLowerCase(Locale.ROOT);
        List<OperadorFila> filas = new ArrayList<>();
        for (Operador operador : operadorDAO.listarTodos()) {
            if (filtroNormalizado.isEmpty() || coincide(operador, filtroNormalizado)) {
                filas.add(new OperadorFila(
                        operador.getIdOperador(), operador.getNombres(),
                        operador.getApellidos(), operador.getDui(),
                        operador.getLicenciaTipo(), operador.getTurno(),
                        operador.getTelefono(), operador.getEstadoOperativo(),
                        Boolean.TRUE.equals(operador.getActivo()),
                        operador.getFechaRegistro()));
            }
        }
        return filas;
    }

    public List<OperadorFila> listarOperadores() {
        return listarOperadores("");
    }

    private boolean coincide(Operador operador, String filtro) {
        String texto = String.join(" ",
                valorSeguro(operador.getNombres()),
                valorSeguro(operador.getApellidos()),
                valorSeguro(operador.getDui()),
                valorSeguro(operador.getTelefono()),
                valorSeguro(operador.getLicenciaTipo()),
                valorSeguro(operador.getTurno()),
                valorSeguro(operador.getEstadoOperativo())).toLowerCase(Locale.ROOT);
        return texto.contains(filtro);
    }

    private DatosNormalizados normalizarDatos(
            String nombres, String apellidos, String dui, String licenciaTipo,
            String turno, String telefono, String estadoOperativo) {
        String nombresNormalizados = normalizarTexto(nombres);
        String apellidosNormalizados = normalizarTexto(apellidos);
        String duiNormalizado = normalizarDui(dui);
        String licenciaNormalizada = normalizarMayusculas(licenciaTipo);
        String turnoNormalizado = normalizarMayusculas(turno);
        String telefonoNormalizado = normalizarTelefono(telefono);
        String estadoNormalizado = normalizarMayusculas(estadoOperativo);

        String error = validarDatos(nombresNormalizados, apellidosNormalizados,
                duiNormalizado, licenciaNormalizada, turnoNormalizado,
                telefonoNormalizado, estadoNormalizado);
        return new DatosNormalizados(nombresNormalizados, apellidosNormalizados,
                duiNormalizado, licenciaNormalizada, turnoNormalizado,
                telefonoNormalizado, estadoNormalizado, error);
    }

    private String validarDatos(String nombres, String apellidos, String dui,
                                String licenciaTipo, String turno, String telefono,
                                String estadoOperativo) {
        if (nombres.isEmpty()) {
            return "Ingresa los nombres del operador.";
        }
        if (nombres.length() > 100) {
            return "Los nombres no pueden exceder 100 caracteres.";
        }
        if (apellidos.isEmpty()) {
            return "Ingresa los apellidos del operador.";
        }
        if (apellidos.length() > 100) {
            return "Los apellidos no pueden exceder 100 caracteres.";
        }
        if (!dui.matches("\\d{8}-\\d")) {
            return "El DUI debe tener el formato 00000000-0.";
        }
        if (licenciaTipo.isEmpty()) {
            return "Selecciona o ingresa el tipo de licencia.";
        }
        if (licenciaTipo.length() > 50) {
            return "El tipo de licencia no puede exceder 50 caracteres.";
        }
        if (!TURNOS.contains(turno)) {
            return "Selecciona un turno válido.";
        }
        if (telefono != null && !telefono.matches("\\d{4}-\\d{4}")) {
            return "El teléfono debe tener el formato 0000-0000 o quedar vacío.";
        }
        if (!ESTADOS_OPERATIVOS.contains(estadoOperativo)) {
            return "Selecciona un estado operativo válido.";
        }
        return null;
    }

    private Operador construirOperador(int idOperador, DatosNormalizados datos,
                                       boolean activo) {
        Operador operador = new Operador();
        operador.setIdOperador(idOperador);
        operador.setNombres(datos.nombres());
        operador.setApellidos(datos.apellidos());
        operador.setDui(datos.dui());
        operador.setLicenciaTipo(datos.licenciaTipo());
        operador.setTurno(datos.turno());
        operador.setTelefono(datos.telefono());
        operador.setEstadoOperativo(datos.estadoOperativo());
        operador.setActivo(activo);
        return operador;
    }

    private String normalizarDui(String valor) {
        String texto = normalizarTexto(valor);
        if (texto.matches("\\d{9}")) {
            return texto.substring(0, 8) + "-" + texto.substring(8);
        }
        return texto;
    }

    private String normalizarTelefono(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String texto = normalizarTexto(valor);
        if (texto.matches("\\d{8}")) {
            return texto.substring(0, 4) + "-" + texto.substring(4);
        }
        return texto;
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

    private record DatosNormalizados(String nombres, String apellidos, String dui,
                                     String licenciaTipo, String turno, String telefono,
                                     String estadoOperativo, String error) {
        DatosNormalizados conEstado(String nuevoEstado) {
            return new DatosNormalizados(nombres, apellidos, dui, licenciaTipo,
                    turno, telefono, nuevoEstado, error);
        }
    }

    public record ResultadoCreacion(boolean exitoso, String mensaje, Operador operador) {
        public static ResultadoCreacion exito(Operador operador) {
            return new ResultadoCreacion(true, "Operador creado correctamente.", operador);
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

    public record OperadorEdicion(int idOperador, String nombres, String apellidos,
                                  String dui, String licenciaTipo, String turno,
                                  String telefono, String estadoOperativo,
                                  boolean activo) {}

    public record OperadorFila(int idOperador, String nombres, String apellidos,
                               String dui, String licenciaTipo, String turno,
                               String telefono, String estadoOperativo,
                               boolean activo, Timestamp fechaRegistro) {}
}

package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.modelo.Asignacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Acceso JDBC y transacciones de la tabla public.asignaciones. */
public class AsignacionDAO implements CrudDAO<Asignacion> {
    private static final String COLUMNAS = "id_asignacion, id_maquinaria, "
            + "id_operador, id_ruta, id_usuario_registro, fecha_asignacion, "
            + "fecha_estimada_retorno, fecha_retorno_real, estado_asignacion, "
            + "observaciones";

    @Override
    public boolean insertar(Asignacion asignacion) {
        String sql = "INSERT INTO public.asignaciones "
                + "(id_maquinaria, id_operador, id_ruta, id_usuario_registro, "
                + "fecha_asignacion, fecha_estimada_retorno, fecha_retorno_real, "
                + "estado_asignacion, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(statement, asignacion, false);
            if (statement.executeUpdate() == 0) {
                return false;
            }
            asignarLlave(statement, asignacion);
            return true;
        } catch (SQLException exception) {
            registrarError("insertar asignación", exception);
            return false;
        }
    }

    /** Crea una asignación EN_CURSO y reserva operador/maquinaria atómicamente. */
    public boolean insertarConReservaRecursos(Asignacion asignacion) {
        Connection connection = null;
        boolean administraTransaccion = false;
        try {
            connection = obtenerConexion();
            administraTransaccion = connection.getAutoCommit();
            if (administraTransaccion) {
                connection.setAutoCommit(false);
            }

            if (!recursoDisponible(connection, "maquinaria", "id_maquinaria",
                    asignacion.getIdMaquinaria())
                    || !recursoDisponible(connection, "operadores", "id_operador",
                            asignacion.getIdOperador())
                    || !rutaAsignable(connection, asignacion.getIdRuta())
                    || !usuarioActivo(connection, asignacion.getIdUsuarioRegistro())) {
                revertirSiCorresponde(connection, administraTransaccion);
                return false;
            }

            String sql = "INSERT INTO public.asignaciones "
                    + "(id_maquinaria, id_operador, id_ruta, id_usuario_registro, "
                    + "fecha_asignacion, fecha_estimada_retorno, estado_asignacion, "
                    + "observaciones) VALUES (?, ?, ?, ?, ?, ?, 'EN_CURSO', ?)";
            try (PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, asignacion.getIdMaquinaria());
                statement.setInt(2, asignacion.getIdOperador());
                statement.setInt(3, asignacion.getIdRuta());
                statement.setInt(4, asignacion.getIdUsuarioRegistro());
                statement.setTimestamp(5, asignacion.getFechaAsignacion());
                statement.setTimestamp(6, asignacion.getFechaEstimadaRetorno());
                asignarTextoOpcional(statement, 7, asignacion.getObservaciones());
                if (statement.executeUpdate() != 1) {
                    revertirSiCorresponde(connection, administraTransaccion);
                    return false;
                }
                asignarLlave(statement, asignacion);
            }

            actualizarEstadoRecurso(connection, "maquinaria", "id_maquinaria",
                    asignacion.getIdMaquinaria(), "EN_RUTA");
            actualizarEstadoRecurso(connection, "operadores", "id_operador",
                    asignacion.getIdOperador(), "EN_RUTA");
            actualizarEstadoRuta(connection, asignacion.getIdRuta(), "EN_CURSO");
            asignacion.setEstadoAsignacion("EN_CURSO");

            if (administraTransaccion) {
                connection.commit();
            }
            return true;
        } catch (SQLException exception) {
            revertirSilenciosamente(connection, administraTransaccion);
            registrarError("crear asignación y reservar recursos", exception);
            return false;
        } finally {
            restaurarAutoCommit(connection, administraTransaccion);
        }
    }

    @Override
    public boolean actualizar(Asignacion asignacion) {
        String sql = "UPDATE public.asignaciones SET id_maquinaria = ?, "
                + "id_operador = ?, id_ruta = ?, id_usuario_registro = ?, "
                + "fecha_asignacion = ?, fecha_estimada_retorno = ?, "
                + "fecha_retorno_real = ?, estado_asignacion = ?, observaciones = ? "
                + "WHERE id_asignacion = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            asignarDatos(statement, asignacion, true);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar asignación", exception);
            return false;
        }
    }

    /** Cancela la asignación y libera los recursos relacionados. */
    @Override
    public boolean eliminar(int id) {
        return cerrarAsignacion(id, "CANCELADA", new Timestamp(System.currentTimeMillis()));
    }

    public boolean finalizar(int id, Timestamp fechaRetornoReal) {
        return cerrarAsignacion(id, "FINALIZADA", fechaRetornoReal);
    }

    @Override
    public Asignacion obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.asignaciones WHERE id_asignacion = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        } catch (SQLException exception) {
            registrarError("consultar asignación por id", exception);
            return null;
        }
    }

    @Override
    public List<Asignacion> listarTodos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.asignaciones ORDER BY fecha_asignacion DESC, id_asignacion DESC";
        return ejecutarListado(sql, null);
    }

    public List<Asignacion> listarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return listarTodos();
        }
        String sql = "SELECT " + COLUMNAS
                + " FROM public.asignaciones WHERE estado_asignacion = ? "
                + "ORDER BY fecha_asignacion DESC, id_asignacion DESC";
        return ejecutarListado(sql, estado.trim());
    }

    public boolean existeEnCursoParaRuta(int idRuta) {
        String sql = "SELECT EXISTS (SELECT 1 FROM public.asignaciones "
                + "WHERE id_ruta = ? AND estado_asignacion = 'EN_CURSO')";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, idRuta);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean(1);
            }
        } catch (SQLException exception) {
            registrarError("verificar asignaciones en curso", exception);
            return true;
        }
    }

    private boolean cerrarAsignacion(int idAsignacion, String estadoFinal,
                                     Timestamp fechaRetornoReal) {
        Connection connection = null;
        boolean administraTransaccion = false;
        try {
            connection = obtenerConexion();
            administraTransaccion = connection.getAutoCommit();
            if (administraTransaccion) {
                connection.setAutoCommit(false);
            }

            Asignacion asignacion = obtenerAsignacionBloqueada(connection, idAsignacion);
            if (asignacion == null
                    || !"EN_CURSO".equals(asignacion.getEstadoAsignacion())) {
                revertirSiCorresponde(connection, administraTransaccion);
                return false;
            }

            String sql = "UPDATE public.asignaciones SET estado_asignacion = ?, "
                    + "fecha_retorno_real = ? WHERE id_asignacion = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, estadoFinal);
                statement.setTimestamp(2, fechaRetornoReal);
                statement.setInt(3, idAsignacion);
                if (statement.executeUpdate() != 1) {
                    revertirSiCorresponde(connection, administraTransaccion);
                    return false;
                }
            }

            actualizarEstadoRecurso(connection, "maquinaria", "id_maquinaria",
                    asignacion.getIdMaquinaria(), "DISPONIBLE");
            actualizarEstadoRecurso(connection, "operadores", "id_operador",
                    asignacion.getIdOperador(), "DISPONIBLE");
            if (!hayOtraAsignacionEnCurso(connection, asignacion.getIdRuta(), idAsignacion)) {
                actualizarEstadoRuta(connection, asignacion.getIdRuta(), estadoFinal);
            }

            if (administraTransaccion) {
                connection.commit();
            }
            return true;
        } catch (SQLException exception) {
            revertirSilenciosamente(connection, administraTransaccion);
            registrarError("cerrar asignación", exception);
            return false;
        } finally {
            restaurarAutoCommit(connection, administraTransaccion);
        }
    }

    private boolean recursoDisponible(Connection connection, String tabla,
                                       String columnaId, int id) throws SQLException {
        String sql = "SELECT activo, estado_operativo FROM public." + tabla
                + " WHERE " + columnaId + " = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean("activo")
                        && "DISPONIBLE".equals(result.getString("estado_operativo"));
            }
        }
    }

    private boolean rutaAsignable(Connection connection, int idRuta) throws SQLException {
        String sql = "SELECT estado FROM public.rutas_destinos "
                + "WHERE id_ruta = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idRuta);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return false;
                }
                String estado = result.getString("estado");
                return "PLANIFICADA".equals(estado) || "EN_CURSO".equals(estado);
            }
        }
    }

    private boolean usuarioActivo(Connection connection, int idUsuario) throws SQLException {
        String sql = "SELECT activo FROM public.usuarios WHERE id_usuario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idUsuario);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean("activo");
            }
        }
    }

    private void actualizarEstadoRecurso(Connection connection, String tabla,
                                         String columnaId, int id, String estado)
            throws SQLException {
        String sql = "UPDATE public." + tabla + " SET estado_operativo = ? "
                + "WHERE " + columnaId + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, estado);
            statement.setInt(2, id);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("No fue posible actualizar el recurso " + tabla + ".");
            }
        }
    }

    private void actualizarEstadoRuta(Connection connection, int idRuta, String estado)
            throws SQLException {
        String sql = "UPDATE public.rutas_destinos SET estado = ? WHERE id_ruta = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, estado);
            statement.setInt(2, idRuta);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("No fue posible actualizar el estado de la ruta.");
            }
        }
    }

    private Asignacion obtenerAsignacionBloqueada(Connection connection, int id)
            throws SQLException {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.asignaciones WHERE id_asignacion = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        }
    }

    private boolean hayOtraAsignacionEnCurso(Connection connection, int idRuta,
                                              int idExcluido) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM public.asignaciones "
                + "WHERE id_ruta = ? AND estado_asignacion = 'EN_CURSO' "
                + "AND id_asignacion <> ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idRuta);
            statement.setInt(2, idExcluido);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean(1);
            }
        }
    }

    private List<Asignacion> ejecutarListado(String sql, String estado) {
        List<Asignacion> asignaciones = new ArrayList<>();
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            if (estado != null) {
                statement.setString(1, estado);
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    asignaciones.add(mapear(result));
                }
            }
        } catch (SQLException exception) {
            registrarError("listar asignaciones", exception);
        }
        return asignaciones;
    }

    private void asignarDatos(PreparedStatement statement, Asignacion asignacion,
                              boolean incluirId) throws SQLException {
        statement.setInt(1, asignacion.getIdMaquinaria());
        statement.setInt(2, asignacion.getIdOperador());
        statement.setInt(3, asignacion.getIdRuta());
        statement.setInt(4, asignacion.getIdUsuarioRegistro());
        statement.setTimestamp(5, asignacion.getFechaAsignacion());
        statement.setTimestamp(6, asignacion.getFechaEstimadaRetorno());
        if (asignacion.getFechaRetornoReal() == null) {
            statement.setNull(7, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(7, asignacion.getFechaRetornoReal());
        }
        statement.setString(8, asignacion.getEstadoAsignacion());
        asignarTextoOpcional(statement, 9, asignacion.getObservaciones());
        if (incluirId) {
            statement.setInt(10, asignacion.getIdAsignacion());
        }
    }

    private void asignarTextoOpcional(PreparedStatement statement, int indice,
                                      String valor) throws SQLException {
        if (valor == null || valor.isBlank()) {
            statement.setNull(indice, Types.VARCHAR);
        } else {
            statement.setString(indice, valor);
        }
    }

    private void asignarLlave(PreparedStatement statement, Asignacion asignacion)
            throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                asignacion.setIdAsignacion(keys.getInt(1));
            }
        }
    }

    private Asignacion mapear(ResultSet result) throws SQLException {
        return new Asignacion(
                result.getInt("id_asignacion"),
                result.getInt("id_maquinaria"),
                result.getInt("id_operador"),
                result.getInt("id_ruta"),
                result.getInt("id_usuario_registro"),
                result.getTimestamp("fecha_asignacion"),
                result.getTimestamp("fecha_estimada_retorno"),
                result.getTimestamp("fecha_retorno_real"),
                result.getString("estado_asignacion"),
                result.getString("observaciones"));
    }

    private void revertirSiCorresponde(Connection connection, boolean administra)
            throws SQLException {
        if (administra) {
            connection.rollback();
        }
    }

    private void revertirSilenciosamente(Connection connection, boolean administra) {
        if (connection == null || !administra) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // El error original es el que se informa al llamador.
        }
    }

    private void restaurarAutoCommit(Connection connection, boolean administra) {
        if (connection == null || !administra) {
            return;
        }
        try {
            connection.setAutoCommit(true);
        } catch (SQLException exception) {
            registrarError("restaurar auto-commit", exception);
        }
    }

    private Connection obtenerConexion() throws SQLException {
        Connection connection = ConexionBD.getInstancia().getConexion();
        if (connection == null) {
            throw new SQLException("No existe una conexión disponible.");
        }
        return connection;
    }

    private void registrarError(String operacion, SQLException exception) {
        System.err.println("[ASIGNACION DAO] Error al " + operacion + ": "
                + exception.getMessage());
    }
}

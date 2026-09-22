package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.modelo.Mantenimiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Acceso JDBC y transacciones de public.mantenimientos. */
public class MantenimientoDAO implements CrudDAO<Mantenimiento> {
    private static final String COLUMNAS = "id_mantenimiento, id_maquinaria, "
            + "id_usuario_registro, tipo_mantenimiento, fecha_ingreso, "
            + "fecha_salida_estimada, fecha_salida_real, diagnostico, costo, "
            + "estado_mantenimiento, taller_responsable";

    @Override
    public boolean insertar(Mantenimiento mantenimiento) {
        String sql = "INSERT INTO public.mantenimientos "
                + "(id_maquinaria, id_usuario_registro, tipo_mantenimiento, "
                + "fecha_ingreso, fecha_salida_estimada, fecha_salida_real, "
                + "diagnostico, costo, estado_mantenimiento, taller_responsable) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatosCompletos(statement, mantenimiento);
            if (statement.executeUpdate() != 1) {
                return false;
            }
            asignarLlave(statement, mantenimiento);
            return true;
        } catch (SQLException exception) {
            registrarError("insertar mantenimiento", exception);
            return false;
        }
    }

    /** Inicia el mantenimiento y reserva la maquinaria en una transacción. */
    public boolean insertarConReservaMaquinaria(Mantenimiento mantenimiento) {
        Connection connection = null;
        boolean administraTransaccion = false;
        try {
            connection = obtenerConexion();
            administraTransaccion = connection.getAutoCommit();
            if (administraTransaccion) {
                connection.setAutoCommit(false);
            }

            if (!maquinariaDisponible(connection, mantenimiento.getIdMaquinaria())
                    || !usuarioActivo(connection,
                            mantenimiento.getIdUsuarioRegistro())
                    || existeEnProceso(connection,
                            mantenimiento.getIdMaquinaria(), null)) {
                revertirSiCorresponde(connection, administraTransaccion);
                return false;
            }

            String sql = "INSERT INTO public.mantenimientos "
                    + "(id_maquinaria, id_usuario_registro, tipo_mantenimiento, "
                    + "fecha_ingreso, fecha_salida_estimada, diagnostico, costo, "
                    + "estado_mantenimiento, taller_responsable) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, 'EN_PROCESO', ?)";
            try (PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, mantenimiento.getIdMaquinaria());
                statement.setInt(2, mantenimiento.getIdUsuarioRegistro());
                statement.setString(3, mantenimiento.getTipoMantenimiento());
                asignarTimestampOpcional(statement, 4,
                        mantenimiento.getFechaIngreso());
                asignarTimestampOpcional(statement, 5,
                        mantenimiento.getFechaSalidaEstimada());
                asignarTextoOpcional(statement, 6,
                        mantenimiento.getDiagnostico());
                asignarDecimalOpcional(statement, 7, mantenimiento.getCosto());
                asignarTextoOpcional(statement, 8,
                        mantenimiento.getTallerResponsable());
                if (statement.executeUpdate() != 1) {
                    revertirSiCorresponde(connection, administraTransaccion);
                    return false;
                }
                asignarLlave(statement, mantenimiento);
            }

            actualizarEstadoMaquinaria(connection,
                    mantenimiento.getIdMaquinaria(), "MANTENIMIENTO");
            mantenimiento.setEstadoMantenimiento("EN_PROCESO");
            if (administraTransaccion) {
                connection.commit();
            }
            return true;
        } catch (SQLException exception) {
            revertirSilenciosamente(connection, administraTransaccion);
            registrarError("iniciar mantenimiento y reservar maquinaria", exception);
            return false;
        } finally {
            restaurarAutoCommit(connection, administraTransaccion);
        }
    }

    /** Actualiza únicamente los datos editables de un mantenimiento abierto. */
    @Override
    public boolean actualizar(Mantenimiento mantenimiento) {
        String sql = "UPDATE public.mantenimientos SET tipo_mantenimiento = ?, "
                + "fecha_salida_estimada = ?, diagnostico = ?, costo = ?, "
                + "taller_responsable = ? WHERE id_mantenimiento = ? "
                + "AND estado_mantenimiento = 'EN_PROCESO'";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, mantenimiento.getTipoMantenimiento());
            asignarTimestampOpcional(statement, 2,
                    mantenimiento.getFechaSalidaEstimada());
            asignarTextoOpcional(statement, 3, mantenimiento.getDiagnostico());
            asignarDecimalOpcional(statement, 4, mantenimiento.getCosto());
            asignarTextoOpcional(statement, 5,
                    mantenimiento.getTallerResponsable());
            statement.setInt(6, mantenimiento.getIdMantenimiento());
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar mantenimiento", exception);
            return false;
        }
    }

    /** Cancela el mantenimiento y libera la maquinaria. */
    @Override
    public boolean eliminar(int id) {
        return cerrarMantenimiento(id, "CANCELADO",
                new Timestamp(System.currentTimeMillis()));
    }

    public boolean finalizar(int id, Timestamp fechaSalidaReal) {
        return cerrarMantenimiento(id, "FINALIZADO", fechaSalidaReal);
    }

    @Override
    public Mantenimiento obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.mantenimientos WHERE id_mantenimiento = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        } catch (SQLException exception) {
            registrarError("consultar mantenimiento por id", exception);
            return null;
        }
    }

    @Override
    public List<Mantenimiento> listarTodos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.mantenimientos "
                + "ORDER BY fecha_ingreso DESC, id_mantenimiento DESC";
        return ejecutarListado(sql, null);
    }

    public List<Mantenimiento> listarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return listarTodos();
        }
        String sql = "SELECT " + COLUMNAS
                + " FROM public.mantenimientos WHERE estado_mantenimiento = ? "
                + "ORDER BY fecha_ingreso DESC, id_mantenimiento DESC";
        return ejecutarListado(sql, estado.trim());
    }

    public boolean existeEnProcesoParaMaquinaria(int idMaquinaria) {
        try {
            return existeEnProceso(obtenerConexion(), idMaquinaria, null);
        } catch (SQLException exception) {
            registrarError("verificar mantenimiento en proceso", exception);
            return true;
        }
    }

    private boolean cerrarMantenimiento(int idMantenimiento, String estadoFinal,
                                        Timestamp fechaSalidaReal) {
        Connection connection = null;
        boolean administraTransaccion = false;
        try {
            connection = obtenerConexion();
            administraTransaccion = connection.getAutoCommit();
            if (administraTransaccion) {
                connection.setAutoCommit(false);
            }

            Mantenimiento mantenimiento =
                    obtenerMantenimientoBloqueado(connection, idMantenimiento);
            if (mantenimiento == null
                    || !"EN_PROCESO".equals(
                            mantenimiento.getEstadoMantenimiento())) {
                revertirSiCorresponde(connection, administraTransaccion);
                return false;
            }

            String sql = "UPDATE public.mantenimientos "
                    + "SET estado_mantenimiento = ?, fecha_salida_real = ? "
                    + "WHERE id_mantenimiento = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, estadoFinal);
                statement.setTimestamp(2, fechaSalidaReal);
                statement.setInt(3, idMantenimiento);
                if (statement.executeUpdate() != 1) {
                    revertirSiCorresponde(connection, administraTransaccion);
                    return false;
                }
            }
            actualizarEstadoMaquinaria(connection,
                    mantenimiento.getIdMaquinaria(), "DISPONIBLE");
            if (administraTransaccion) {
                connection.commit();
            }
            return true;
        } catch (SQLException exception) {
            revertirSilenciosamente(connection, administraTransaccion);
            registrarError("cerrar mantenimiento", exception);
            return false;
        } finally {
            restaurarAutoCommit(connection, administraTransaccion);
        }
    }

    private boolean maquinariaDisponible(Connection connection, int idMaquinaria)
            throws SQLException {
        String sql = "SELECT activo, estado_operativo FROM public.maquinaria "
                + "WHERE id_maquinaria = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idMaquinaria);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean("activo")
                        && "DISPONIBLE".equals(
                                result.getString("estado_operativo"));
            }
        }
    }

    private boolean usuarioActivo(Connection connection, int idUsuario)
            throws SQLException {
        String sql = "SELECT activo FROM public.usuarios WHERE id_usuario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idUsuario);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean("activo");
            }
        }
    }

    private boolean existeEnProceso(Connection connection, int idMaquinaria,
                                    Integer idExcluido) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM public.mantenimientos "
                + "WHERE id_maquinaria = ? AND estado_mantenimiento = 'EN_PROCESO' "
                + "AND (? IS NULL OR id_mantenimiento <> ?))";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idMaquinaria);
            if (idExcluido == null) {
                statement.setNull(2, Types.INTEGER);
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(2, idExcluido);
                statement.setInt(3, idExcluido);
            }
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean(1);
            }
        }
    }

    private void actualizarEstadoMaquinaria(Connection connection, int idMaquinaria,
                                             String estado) throws SQLException {
        String sql = "UPDATE public.maquinaria SET estado_operativo = ? "
                + "WHERE id_maquinaria = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, estado);
            statement.setInt(2, idMaquinaria);
            if (statement.executeUpdate() != 1) {
                throw new SQLException(
                        "No fue posible actualizar el estado de la maquinaria.");
            }
        }
    }

    private Mantenimiento obtenerMantenimientoBloqueado(
            Connection connection, int idMantenimiento) throws SQLException {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.mantenimientos WHERE id_mantenimiento = ? "
                + "FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idMantenimiento);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        }
    }

    private List<Mantenimiento> ejecutarListado(String sql, String estado) {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            if (estado != null) {
                statement.setString(1, estado);
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    mantenimientos.add(mapear(result));
                }
            }
        } catch (SQLException exception) {
            registrarError("listar mantenimientos", exception);
        }
        return mantenimientos;
    }

    private void asignarDatosCompletos(PreparedStatement statement,
                                       Mantenimiento mantenimiento)
            throws SQLException {
        statement.setInt(1, mantenimiento.getIdMaquinaria());
        statement.setInt(2, mantenimiento.getIdUsuarioRegistro());
        statement.setString(3, mantenimiento.getTipoMantenimiento());
        asignarTimestampOpcional(statement, 4, mantenimiento.getFechaIngreso());
        asignarTimestampOpcional(statement, 5,
                mantenimiento.getFechaSalidaEstimada());
        asignarTimestampOpcional(statement, 6,
                mantenimiento.getFechaSalidaReal());
        asignarTextoOpcional(statement, 7, mantenimiento.getDiagnostico());
        asignarDecimalOpcional(statement, 8, mantenimiento.getCosto());
        statement.setString(9, mantenimiento.getEstadoMantenimiento());
        asignarTextoOpcional(statement, 10,
                mantenimiento.getTallerResponsable());
    }

    private void asignarTimestampOpcional(PreparedStatement statement, int indice,
                                           Timestamp valor) throws SQLException {
        if (valor == null) {
            statement.setNull(indice, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(indice, valor);
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

    private void asignarDecimalOpcional(PreparedStatement statement, int indice,
                                        java.math.BigDecimal valor)
            throws SQLException {
        if (valor == null) {
            statement.setNull(indice, Types.NUMERIC);
        } else {
            statement.setBigDecimal(indice, valor);
        }
    }

    private void asignarLlave(PreparedStatement statement,
                              Mantenimiento mantenimiento) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (keys.next()) {
                mantenimiento.setIdMantenimiento(keys.getInt(1));
            }
        }
    }

    private Mantenimiento mapear(ResultSet result) throws SQLException {
        return new Mantenimiento(
                result.getInt("id_mantenimiento"),
                result.getInt("id_maquinaria"),
                result.getInt("id_usuario_registro"),
                result.getString("tipo_mantenimiento"),
                result.getTimestamp("fecha_ingreso"),
                result.getTimestamp("fecha_salida_estimada"),
                result.getTimestamp("fecha_salida_real"),
                result.getString("diagnostico"),
                result.getBigDecimal("costo"),
                result.getString("estado_mantenimiento"),
                result.getString("taller_responsable"));
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
            // Se conserva el error original.
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
        System.err.println("[MANTENIMIENTO DAO] Error al " + operacion + ": "
                + exception.getMessage());
    }
}

package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.modelo.Operador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso JDBC a la tabla public.operadores. */
public class OperadorDAO implements CrudDAO<Operador> {
    private static final String COLUMNAS = "id_operador, nombres, apellidos, dui, "
            + "licencia_tipo, turno, telefono, estado_operativo, activo, fecha_registro";

    @Override
    public boolean insertar(Operador operador) {
        String sql = "INSERT INTO public.operadores "
                + "(nombres, apellidos, dui, licencia_tipo, turno, telefono, "
                + "estado_operativo, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(statement, operador, false);
            if (statement.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    operador.setIdOperador(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException exception) {
            registrarError("insertar operador", exception);
            return false;
        }
    }

    @Override
    public boolean actualizar(Operador operador) {
        String sql = "UPDATE public.operadores SET nombres = ?, apellidos = ?, dui = ?, "
                + "licencia_tipo = ?, turno = ?, telefono = ?, estado_operativo = ?, "
                + "activo = ? WHERE id_operador = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            asignarDatos(statement, operador, true);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar operador", exception);
            return false;
        }
    }

    /** Conserva el registro y sus relaciones históricas. */
    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE public.operadores SET activo = FALSE, "
                + "estado_operativo = 'INACTIVO' WHERE id_operador = ?";
        return ejecutarCambioEstado(sql, id, "desactivar operador");
    }

    /** Reactiva el registro en un estado seguro para una nueva asignación. */
    public boolean reactivar(int id) {
        String sql = "UPDATE public.operadores SET activo = TRUE, "
                + "estado_operativo = 'DISPONIBLE' WHERE id_operador = ?";
        return ejecutarCambioEstado(sql, id, "reactivar operador");
    }

    @Override
    public Operador obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.operadores WHERE id_operador = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        } catch (SQLException exception) {
            registrarError("consultar operador por id", exception);
            return null;
        }
    }

    @Override
    public List<Operador> listarTodos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.operadores ORDER BY apellidos, nombres";
        return ejecutarListado(sql);
    }

    public List<Operador> listarActivos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.operadores WHERE activo = TRUE ORDER BY apellidos, nombres";
        return ejecutarListado(sql);
    }

    public Optional<Operador> buscarPorDui(String dui) {
        if (dui == null || dui.isBlank()) {
            return Optional.empty();
        }

        String sql = "SELECT " + COLUMNAS + " FROM public.operadores WHERE dui = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, dui.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            registrarError("buscar operador por DUI", exception);
            return Optional.empty();
        }
    }

    public boolean existeDui(String dui, Integer idOperadorExcluido) {
        if (dui == null || dui.isBlank()) {
            return false;
        }

        String sql = "SELECT EXISTS (SELECT 1 FROM public.operadores WHERE dui = ? "
                + "AND (? IS NULL OR id_operador <> ?))";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, dui.trim());
            if (idOperadorExcluido == null) {
                statement.setNull(2, Types.INTEGER);
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(2, idOperadorExcluido);
                statement.setInt(3, idOperadorExcluido);
            }
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean(1);
            }
        } catch (SQLException exception) {
            registrarError("verificar DUI", exception);
            return false;
        }
    }

    private boolean ejecutarCambioEstado(String sql, int id, String operacion) {
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError(operacion, exception);
            return false;
        }
    }

    private List<Operador> ejecutarListado(String sql) {
        List<Operador> operadores = new ArrayList<>();
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                operadores.add(mapear(result));
            }
        } catch (SQLException exception) {
            registrarError("listar operadores", exception);
        }
        return operadores;
    }

    private void asignarDatos(PreparedStatement statement, Operador operador,
                              boolean incluirId) throws SQLException {
        statement.setString(1, operador.getNombres());
        statement.setString(2, operador.getApellidos());
        statement.setString(3, operador.getDui());
        statement.setString(4, operador.getLicenciaTipo());
        statement.setString(5, operador.getTurno());
        if (operador.getTelefono() == null || operador.getTelefono().isBlank()) {
            statement.setNull(6, Types.VARCHAR);
        } else {
            statement.setString(6, operador.getTelefono());
        }
        statement.setString(7, operador.getEstadoOperativo());
        statement.setBoolean(8, operador.getActivo() == null || operador.getActivo());
        if (incluirId) {
            statement.setInt(9, operador.getIdOperador());
        }
    }

    private Operador mapear(ResultSet result) throws SQLException {
        return new Operador(
                result.getInt("id_operador"),
                result.getString("nombres"),
                result.getString("apellidos"),
                result.getString("dui"),
                result.getString("licencia_tipo"),
                result.getString("turno"),
                result.getString("telefono"),
                result.getString("estado_operativo"),
                (Boolean) result.getObject("activo"),
                result.getTimestamp("fecha_registro"));
    }

    private Connection obtenerConexion() throws SQLException {
        Connection connection = ConexionBD.getInstancia().getConexion();
        if (connection == null) {
            throw new SQLException("No existe una conexión disponible.");
        }
        return connection;
    }

    private void registrarError(String operacion, SQLException exception) {
        System.err.println("[OPERADOR DAO] Error al " + operacion + ": "
                + exception.getMessage());
    }
}

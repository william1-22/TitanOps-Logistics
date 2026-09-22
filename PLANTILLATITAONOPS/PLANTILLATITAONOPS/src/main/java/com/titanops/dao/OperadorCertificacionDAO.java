package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.modelo.OperadorCertificacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso JDBC a public.operadores_certificaciones. */
public class OperadorCertificacionDAO implements CrudDAO<OperadorCertificacion> {
    private static final String COLUMNAS = "id_certificacion, id_operador, "
            + "id_categoria, numero_acreditacion, fecha_expedicion, fecha_vencimiento";

    @Override
    public boolean insertar(OperadorCertificacion certificacion) {
        String sql = "INSERT INTO public.operadores_certificaciones "
                + "(id_operador, id_categoria, numero_acreditacion, "
                + "fecha_expedicion, fecha_vencimiento) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(statement, certificacion, false);
            if (statement.executeUpdate() != 1) {
                return false;
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    certificacion.setIdCertificacion(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException exception) {
            registrarError("insertar certificación", exception);
            return false;
        }
    }

    @Override
    public boolean actualizar(OperadorCertificacion certificacion) {
        String sql = "UPDATE public.operadores_certificaciones SET "
                + "id_operador = ?, id_categoria = ?, numero_acreditacion = ?, "
                + "fecha_expedicion = ?, fecha_vencimiento = ? "
                + "WHERE id_certificacion = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            asignarDatos(statement, certificacion, true);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar certificación", exception);
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM public.operadores_certificaciones "
                + "WHERE id_certificacion = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("eliminar certificación", exception);
            return false;
        }
    }

    @Override
    public OperadorCertificacion obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.operadores_certificaciones WHERE id_certificacion = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        } catch (SQLException exception) {
            registrarError("consultar certificación por id", exception);
            return null;
        }
    }

    @Override
    public List<OperadorCertificacion> listarTodos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.operadores_certificaciones "
                + "ORDER BY fecha_vencimiento, numero_acreditacion";
        return ejecutarListado(sql, null);
    }

    public List<OperadorCertificacion> listarPorOperador(int idOperador) {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.operadores_certificaciones WHERE id_operador = ? "
                + "ORDER BY fecha_vencimiento, numero_acreditacion";
        return ejecutarListado(sql, idOperador);
    }

    public Optional<OperadorCertificacion> buscarPorNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT " + COLUMNAS
                + " FROM public.operadores_certificaciones "
                + "WHERE LOWER(numero_acreditacion) = LOWER(?)";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, numero.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            registrarError("buscar certificación por número", exception);
            return Optional.empty();
        }
    }

    public boolean existeNumero(String numero, Integer idCertificacionExcluida) {
        if (numero == null || numero.isBlank()) {
            return false;
        }
        String sql = "SELECT EXISTS (SELECT 1 "
                + "FROM public.operadores_certificaciones "
                + "WHERE LOWER(numero_acreditacion) = LOWER(?) "
                + "AND (? IS NULL OR id_certificacion <> ?))";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, numero.trim());
            if (idCertificacionExcluida == null) {
                statement.setNull(2, Types.INTEGER);
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(2, idCertificacionExcluida);
                statement.setInt(3, idCertificacionExcluida);
            }
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean(1);
            }
        } catch (SQLException exception) {
            registrarError("verificar número de acreditación", exception);
            return true;
        }
    }

    private List<OperadorCertificacion> ejecutarListado(String sql, Integer idOperador) {
        List<OperadorCertificacion> certificaciones = new ArrayList<>();
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            if (idOperador != null) {
                statement.setInt(1, idOperador);
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    certificaciones.add(mapear(result));
                }
            }
        } catch (SQLException exception) {
            registrarError("listar certificaciones", exception);
        }
        return certificaciones;
    }

    private void asignarDatos(PreparedStatement statement,
                              OperadorCertificacion certificacion,
                              boolean incluirId) throws SQLException {
        statement.setInt(1, certificacion.getIdOperador());
        statement.setInt(2, certificacion.getIdCategoria());
        statement.setString(3, certificacion.getNumeroAcreditacion());
        statement.setDate(4, certificacion.getFechaExpedicion());
        statement.setDate(5, certificacion.getFechaVencimiento());
        if (incluirId) {
            statement.setInt(6, certificacion.getIdCertificacion());
        }
    }

    private OperadorCertificacion mapear(ResultSet result) throws SQLException {
        return new OperadorCertificacion(
                result.getInt("id_certificacion"),
                result.getInt("id_operador"),
                result.getInt("id_categoria"),
                result.getString("numero_acreditacion"),
                result.getDate("fecha_expedicion"),
                result.getDate("fecha_vencimiento"));
    }

    private Connection obtenerConexion() throws SQLException {
        Connection connection = ConexionBD.getInstancia().getConexion();
        if (connection == null) {
            throw new SQLException("No existe una conexión disponible.");
        }
        return connection;
    }

    private void registrarError(String operacion, SQLException exception) {
        System.err.println("[CERTIFICACION DAO] Error al " + operacion + ": "
                + exception.getMessage());
    }
}

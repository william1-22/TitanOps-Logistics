package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.modelo.Maquinaria;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso JDBC a la tabla public.maquinaria. */
public class MaquinariaDAO implements CrudDAO<Maquinaria> {
    private static final String COLUMNAS = "id_maquinaria, id_categoria, "
            + "codigo_inventario, marca, modelo, tonelaje, horas_uso, "
            + "estado_operativo, activo, fecha_registro";

    @Override
    public boolean insertar(Maquinaria maquinaria) {
        String sql = "INSERT INTO public.maquinaria "
                + "(id_categoria, codigo_inventario, marca, modelo, tonelaje, "
                + "horas_uso, estado_operativo, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(statement, maquinaria, false);
            if (statement.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    maquinaria.setIdMaquinaria(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException exception) {
            registrarError("insertar maquinaria", exception);
            return false;
        }
    }

    @Override
    public boolean actualizar(Maquinaria maquinaria) {
        String sql = "UPDATE public.maquinaria SET id_categoria = ?, "
                + "codigo_inventario = ?, marca = ?, modelo = ?, tonelaje = ?, "
                + "horas_uso = ?, estado_operativo = ?, activo = ? "
                + "WHERE id_maquinaria = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            asignarDatos(statement, maquinaria, true);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar maquinaria", exception);
            return false;
        }
    }

    /** Desactivación lógica para conservar asignaciones y mantenimientos. */
    @Override
    public boolean eliminar(int id) {
        return cambiarActivo(id, false);
    }

    public boolean reactivar(int id) {
        return cambiarActivo(id, true);
    }

    @Override
    public Maquinaria obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.maquinaria WHERE id_maquinaria = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        } catch (SQLException exception) {
            registrarError("consultar maquinaria por id", exception);
            return null;
        }
    }

    @Override
    public List<Maquinaria> listarTodos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.maquinaria ORDER BY codigo_inventario";
        return ejecutarListado(sql, null);
    }

    public List<Maquinaria> listarPorEstado(String estadoOperativo) {
        if (estadoOperativo == null || estadoOperativo.isBlank()) {
            return listarTodos();
        }
        String sql = "SELECT " + COLUMNAS
                + " FROM public.maquinaria WHERE estado_operativo = ? "
                + "ORDER BY codigo_inventario";
        return ejecutarListado(sql, estadoOperativo.trim());
    }

    public Optional<Maquinaria> buscarPorCodigo(String codigoInventario) {
        if (codigoInventario == null || codigoInventario.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT " + COLUMNAS
                + " FROM public.maquinaria WHERE LOWER(codigo_inventario) = LOWER(?)";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, codigoInventario.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            registrarError("buscar maquinaria por código", exception);
            return Optional.empty();
        }
    }

    public boolean existeCodigo(String codigoInventario, Integer idMaquinariaExcluida) {
        if (codigoInventario == null || codigoInventario.isBlank()) {
            return false;
        }
        String sql = "SELECT EXISTS (SELECT 1 FROM public.maquinaria "
                + "WHERE LOWER(codigo_inventario) = LOWER(?) "
                + "AND (? IS NULL OR id_maquinaria <> ?))";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, codigoInventario.trim());
            if (idMaquinariaExcluida == null) {
                statement.setNull(2, Types.INTEGER);
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(2, idMaquinariaExcluida);
                statement.setInt(3, idMaquinariaExcluida);
            }
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean(1);
            }
        } catch (SQLException exception) {
            registrarError("verificar código de inventario", exception);
            return false;
        }
    }

    private boolean cambiarActivo(int id, boolean activo) {
        String sql = "UPDATE public.maquinaria SET activo = ? WHERE id_maquinaria = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setBoolean(1, activo);
            statement.setInt(2, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError(activo ? "reactivar maquinaria" : "desactivar maquinaria",
                    exception);
            return false;
        }
    }

    private List<Maquinaria> ejecutarListado(String sql, String estado) {
        List<Maquinaria> maquinaria = new ArrayList<>();
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            if (estado != null) {
                statement.setString(1, estado);
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    maquinaria.add(mapear(result));
                }
            }
        } catch (SQLException exception) {
            registrarError("listar maquinaria", exception);
        }
        return maquinaria;
    }

    private void asignarDatos(PreparedStatement statement, Maquinaria maquinaria,
                              boolean incluirId) throws SQLException {
        statement.setInt(1, maquinaria.getIdCategoria());
        statement.setString(2, maquinaria.getCodigoInventario());
        asignarTextoOpcional(statement, 3, maquinaria.getMarca());
        asignarTextoOpcional(statement, 4, maquinaria.getModelo());
        asignarDecimalOpcional(statement, 5, maquinaria.getTonelaje());
        asignarDecimalOpcional(statement, 6, maquinaria.getHorasUso());
        statement.setString(7, maquinaria.getEstadoOperativo());
        statement.setBoolean(8, maquinaria.getActivo() == null || maquinaria.getActivo());
        if (incluirId) {
            statement.setInt(9, maquinaria.getIdMaquinaria());
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
                                        BigDecimal valor) throws SQLException {
        if (valor == null) {
            statement.setNull(indice, Types.NUMERIC);
        } else {
            statement.setBigDecimal(indice, valor);
        }
    }

    private Maquinaria mapear(ResultSet result) throws SQLException {
        return new Maquinaria(
                result.getInt("id_maquinaria"),
                result.getInt("id_categoria"),
                result.getString("codigo_inventario"),
                result.getString("marca"),
                result.getString("modelo"),
                result.getBigDecimal("tonelaje"),
                result.getBigDecimal("horas_uso"),
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
        System.err.println("[MAQUINARIA DAO] Error al " + operacion + ": "
                + exception.getMessage());
    }
}

package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.modelo.Rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso JDBC a la tabla public.roles. */
public class RolDAO implements CrudDAO<Rol> {
    private static final String COLUMNAS = "id_rol, nombre_rol, descripcion";

    @Override
    public boolean insertar(Rol rol) {
        String sql = "INSERT INTO public.roles (nombre_rol, descripcion) VALUES (?, ?)";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, rol.getNombreRol());
            statement.setString(2, rol.getDescripcion());

            if (statement.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    rol.setIdRol(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException exception) {
            registrarError("insertar rol", exception);
            return false;
        }
    }

    @Override
    public boolean actualizar(Rol rol) {
        String sql = "UPDATE public.roles SET nombre_rol = ?, descripcion = ? WHERE id_rol = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, rol.getNombreRol());
            statement.setString(2, rol.getDescripcion());
            statement.setInt(3, rol.getIdRol());
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar rol", exception);
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM public.roles WHERE id_rol = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("eliminar rol", exception);
            return false;
        }
    }

    @Override
    public Rol obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM public.roles WHERE id_rol = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        } catch (SQLException exception) {
            registrarError("consultar rol por id", exception);
            return null;
        }
    }

    @Override
    public List<Rol> listarTodos() {
        String sql = "SELECT " + COLUMNAS + " FROM public.roles ORDER BY nombre_rol";
        List<Rol> roles = new ArrayList<>();

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                roles.add(mapear(result));
            }
        } catch (SQLException exception) {
            registrarError("listar roles", exception);
        }
        return roles;
    }

    public Optional<Rol> buscarPorNombre(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            return Optional.empty();
        }

        String sql = "SELECT " + COLUMNAS + " FROM public.roles WHERE nombre_rol = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, nombreRol.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            registrarError("consultar rol por nombre", exception);
            return Optional.empty();
        }
    }

    private Rol mapear(ResultSet result) throws SQLException {
        return new Rol(
                result.getInt("id_rol"),
                result.getString("nombre_rol"),
                result.getString("descripcion"));
    }

    private Connection obtenerConexion() throws SQLException {
        Connection connection = ConexionBD.getInstancia().getConexion();
        if (connection == null) {
            throw new SQLException("No existe una conexión disponible.");
        }
        return connection;
    }

    private void registrarError(String operacion, SQLException exception) {
        System.err.println("[ROL DAO] Error al " + operacion + ": " + exception.getMessage());
    }
}

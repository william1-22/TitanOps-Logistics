package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.modelo.CategoriaMaquinaria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso JDBC al catálogo public.categorias_maquinaria. */
public class CategoriaMaquinariaDAO implements CrudDAO<CategoriaMaquinaria> {
    private static final String COLUMNAS =
            "id_categoria, nombre_categoria, descripcion, activo";

    @Override
    public boolean insertar(CategoriaMaquinaria categoria) {
        String sql = "INSERT INTO public.categorias_maquinaria "
                + "(nombre_categoria, descripcion, activo) VALUES (?, ?, ?)";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(statement, categoria, false);
            if (statement.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    categoria.setIdCategoria(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException exception) {
            registrarError("insertar categoría", exception);
            return false;
        }
    }

    @Override
    public boolean actualizar(CategoriaMaquinaria categoria) {
        String sql = "UPDATE public.categorias_maquinaria SET nombre_categoria = ?, "
                + "descripcion = ?, activo = ? WHERE id_categoria = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            asignarDatos(statement, categoria, true);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar categoría", exception);
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        return cambiarActivo(id, false);
    }

    public boolean reactivar(int id) {
        return cambiarActivo(id, true);
    }

    @Override
    public CategoriaMaquinaria obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.categorias_maquinaria WHERE id_categoria = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        } catch (SQLException exception) {
            registrarError("consultar categoría por id", exception);
            return null;
        }
    }

    @Override
    public List<CategoriaMaquinaria> listarTodos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.categorias_maquinaria ORDER BY nombre_categoria";
        return ejecutarListado(sql);
    }

    public List<CategoriaMaquinaria> listarActivas() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.categorias_maquinaria WHERE activo = TRUE "
                + "ORDER BY nombre_categoria";
        return ejecutarListado(sql);
    }

    public Optional<CategoriaMaquinaria> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT " + COLUMNAS
                + " FROM public.categorias_maquinaria "
                + "WHERE LOWER(nombre_categoria) = LOWER(?)";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, nombre.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            registrarError("buscar categoría por nombre", exception);
            return Optional.empty();
        }
    }

    private boolean cambiarActivo(int id, boolean activo) {
        String sql = "UPDATE public.categorias_maquinaria SET activo = ? "
                + "WHERE id_categoria = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setBoolean(1, activo);
            statement.setInt(2, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError(activo ? "reactivar categoría" : "desactivar categoría",
                    exception);
            return false;
        }
    }

    private List<CategoriaMaquinaria> ejecutarListado(String sql) {
        List<CategoriaMaquinaria> categorias = new ArrayList<>();
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                categorias.add(mapear(result));
            }
        } catch (SQLException exception) {
            registrarError("listar categorías", exception);
        }
        return categorias;
    }

    private void asignarDatos(PreparedStatement statement,
                              CategoriaMaquinaria categoria,
                              boolean incluirId) throws SQLException {
        statement.setString(1, categoria.getNombreCategoria());
        if (categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            statement.setNull(2, Types.VARCHAR);
        } else {
            statement.setString(2, categoria.getDescripcion());
        }
        statement.setBoolean(3, categoria.getActivo() == null || categoria.getActivo());
        if (incluirId) {
            statement.setInt(4, categoria.getIdCategoria());
        }
    }

    private CategoriaMaquinaria mapear(ResultSet result) throws SQLException {
        return new CategoriaMaquinaria(
                result.getInt("id_categoria"),
                result.getString("nombre_categoria"),
                result.getString("descripcion"),
                (Boolean) result.getObject("activo"));
    }

    private Connection obtenerConexion() throws SQLException {
        Connection connection = ConexionBD.getInstancia().getConexion();
        if (connection == null) {
            throw new SQLException("No existe una conexión disponible.");
        }
        return connection;
    }

    private void registrarError(String operacion, SQLException exception) {
        System.err.println("[CATEGORIA MAQUINARIA DAO] Error al " + operacion + ": "
                + exception.getMessage());
    }
}

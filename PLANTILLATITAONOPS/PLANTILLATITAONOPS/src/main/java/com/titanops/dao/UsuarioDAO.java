package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.modelo.Usuario;
import com.titanops.seguridad.PasswordHasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso JDBC a la tabla public.usuarios. */
public class UsuarioDAO implements CrudDAO<Usuario> {
    private static final String COLUMNAS = "id_usuario, id_rol, nombre_completo, "
            + "username, clave_hash, activo, fecha_creacion";

    @Override
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO public.usuarios "
                + "(id_rol, nombre_completo, username, clave_hash, activo) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, usuario.getIdRol());
            statement.setString(2, usuario.getNombreCompleto());
            statement.setString(3, usuario.getUsername());
            statement.setString(4, usuario.getClaveHash());
            statement.setBoolean(5, usuario.getActivo() == null || usuario.getActivo());

            if (statement.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setIdUsuario(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException exception) {
            registrarError("insertar usuario", exception);
            return false;
        }
    }

    public boolean insertarConClave(Usuario usuario, String clavePlana) {
        usuario.setClaveHash(PasswordHasher.generar(clavePlana));
        return insertar(usuario);
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE public.usuarios SET id_rol = ?, nombre_completo = ?, "
                + "username = ?, clave_hash = ?, activo = ? WHERE id_usuario = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, usuario.getIdRol());
            statement.setString(2, usuario.getNombreCompleto());
            statement.setString(3, usuario.getUsername());
            statement.setString(4, usuario.getClaveHash());
            statement.setBoolean(5, usuario.getActivo() == null || usuario.getActivo());
            statement.setInt(6, usuario.getIdUsuario());
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar usuario", exception);
            return false;
        }
    }

    public boolean actualizarDatos(Usuario usuario) {
        String sql = "UPDATE public.usuarios SET id_rol = ?, nombre_completo = ?, "
                + "username = ?, activo = ? WHERE id_usuario = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, usuario.getIdRol());
            statement.setString(2, usuario.getNombreCompleto());
            statement.setString(3, usuario.getUsername());
            statement.setBoolean(4, usuario.getActivo() == null || usuario.getActivo());
            statement.setInt(5, usuario.getIdUsuario());
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar datos del usuario", exception);
            return false;
        }
    }

    /** Actualiza datos y, solo si se proporciona, reemplaza también la clave. */
    public boolean actualizarDatosYClave(Usuario usuario, String nuevaClavePlana) {
        String sql = "UPDATE public.usuarios SET id_rol = ?, nombre_completo = ?, "
                + "username = ?, activo = ?, clave_hash = COALESCE(?, clave_hash) "
                + "WHERE id_usuario = ?";
        String nuevoHash = nuevaClavePlana == null || nuevaClavePlana.isBlank()
                ? null : PasswordHasher.generar(nuevaClavePlana);

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, usuario.getIdRol());
            statement.setString(2, usuario.getNombreCompleto());
            statement.setString(3, usuario.getUsername());
            statement.setBoolean(4, usuario.getActivo() == null || usuario.getActivo());
            if (nuevoHash == null) {
                statement.setNull(5, java.sql.Types.VARCHAR);
            } else {
                statement.setString(5, nuevoHash);
            }
            statement.setInt(6, usuario.getIdUsuario());
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar datos y clave del usuario", exception);
            return false;
        }
    }

    public boolean actualizarClave(int idUsuario, String nuevaClavePlana) {
        String sql = "UPDATE public.usuarios SET clave_hash = ? WHERE id_usuario = ?";
        String claveHash = PasswordHasher.generar(nuevaClavePlana);

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, claveHash);
            statement.setInt(2, idUsuario);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar clave", exception);
            return false;
        }
    }

    /** Desactiva el usuario para conservar sus relaciones históricas. */
    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE public.usuarios SET activo = FALSE WHERE id_usuario = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("desactivar usuario", exception);
            return false;
        }
    }

    public boolean reactivar(int id) {
        String sql = "UPDATE public.usuarios SET activo = TRUE WHERE id_usuario = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("reactivar usuario", exception);
            return false;
        }
    }

    @Override
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM public.usuarios WHERE id_usuario = ?";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        } catch (SQLException exception) {
            registrarError("consultar usuario por id", exception);
            return null;
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT " + COLUMNAS + " FROM public.usuarios ORDER BY nombre_completo";
        return ejecutarListado(sql);
    }

    public List<Usuario> listarActivos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.usuarios WHERE activo = TRUE ORDER BY nombre_completo";
        return ejecutarListado(sql);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return consultarPorUsername(username, false);
    }

    public Optional<Usuario> validarLogin(String username, String clavePlana) {
        if (clavePlana == null || clavePlana.isBlank()) {
            return Optional.empty();
        }

        Optional<Usuario> usuario = consultarPorUsername(username, true);
        if (usuario.isEmpty()
                || !PasswordHasher.verificar(clavePlana, usuario.get().getClaveHash())) {
            return Optional.empty();
        }
        return usuario;
    }

    public boolean existeUsername(String username, Integer idUsuarioExcluido) {
        if (username == null || username.isBlank()) {
            return false;
        }

        String sql = "SELECT EXISTS (SELECT 1 FROM public.usuarios WHERE LOWER(username) = LOWER(?) "
                + "AND (? IS NULL OR id_usuario <> ?))";

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, username.trim());
            if (idUsuarioExcluido == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, idUsuarioExcluido);
                statement.setInt(3, idUsuarioExcluido);
            }
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean(1);
            }
        } catch (SQLException exception) {
            registrarError("verificar username", exception);
            return false;
        }
    }

    private Optional<Usuario> consultarPorUsername(String username, boolean soloActivos) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        String sql = "SELECT " + COLUMNAS + " FROM public.usuarios WHERE LOWER(username) = LOWER(?)"
                + (soloActivos ? " AND activo = TRUE" : "");

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setString(1, username.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            registrarError("consultar usuario por username", exception);
            return Optional.empty();
        }
    }

    private List<Usuario> ejecutarListado(String sql) {
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                usuarios.add(mapear(result));
            }
        } catch (SQLException exception) {
            registrarError("listar usuarios", exception);
        }
        return usuarios;
    }

    private Usuario mapear(ResultSet result) throws SQLException {
        return new Usuario(
                result.getInt("id_usuario"),
                result.getInt("id_rol"),
                result.getString("nombre_completo"),
                result.getString("username"),
                result.getString("clave_hash"),
                (Boolean) result.getObject("activo"),
                result.getTimestamp("fecha_creacion"));
    }

    private Connection obtenerConexion() throws SQLException {
        Connection connection = ConexionBD.getInstancia().getConexion();
        if (connection == null) {
            throw new SQLException("No existe una conexión disponible.");
        }
        return connection;
    }

    private void registrarError(String operacion, SQLException exception) {
        System.err.println("[USUARIO DAO] Error al " + operacion + ": " + exception.getMessage());
    }
}

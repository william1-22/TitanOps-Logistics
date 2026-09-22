package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.modelo.RutaDestino;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Acceso JDBC a la tabla public.rutas_destinos. */
public class RutaDestinoDAO implements CrudDAO<RutaDestino> {
    private static final String COLUMNAS = "id_ruta, nombre_proyecto, punto_origen, "
            + "punto_destino, distancia_km, estado";

    @Override
    public boolean insertar(RutaDestino ruta) {
        String sql = "INSERT INTO public.rutas_destinos "
                + "(nombre_proyecto, punto_origen, punto_destino, distancia_km, estado) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarDatos(statement, ruta, false);
            if (statement.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    ruta.setIdRuta(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException exception) {
            registrarError("insertar ruta", exception);
            return false;
        }
    }

    @Override
    public boolean actualizar(RutaDestino ruta) {
        String sql = "UPDATE public.rutas_destinos SET nombre_proyecto = ?, "
                + "punto_origen = ?, punto_destino = ?, distancia_km = ?, estado = ? "
                + "WHERE id_ruta = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            asignarDatos(statement, ruta, true);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("actualizar ruta", exception);
            return false;
        }
    }

    /** Cancela lógicamente la ruta para conservar su historial. */
    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE public.rutas_destinos SET estado = 'CANCELADA' "
                + "WHERE id_ruta = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            registrarError("cancelar ruta", exception);
            return false;
        }
    }

    @Override
    public RutaDestino obtenerPorId(int id) {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.rutas_destinos WHERE id_ruta = ?";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        } catch (SQLException exception) {
            registrarError("consultar ruta por id", exception);
            return null;
        }
    }

    @Override
    public List<RutaDestino> listarTodos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM public.rutas_destinos ORDER BY nombre_proyecto, id_ruta";
        return ejecutarListado(sql, null);
    }

    public List<RutaDestino> listarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return listarTodos();
        }
        String sql = "SELECT " + COLUMNAS
                + " FROM public.rutas_destinos WHERE estado = ? "
                + "ORDER BY nombre_proyecto, id_ruta";
        return ejecutarListado(sql, estado.trim());
    }

    public List<RutaDestino> listarAsignables() {
        String sql = "SELECT " + COLUMNAS + " FROM public.rutas_destinos "
                + "WHERE estado IN ('PLANIFICADA', 'EN_CURSO') "
                + "ORDER BY nombre_proyecto, id_ruta";
        return ejecutarListado(sql, null);
    }

    public boolean tieneAsignacionesEnCurso(int idRuta) {
        String sql = "SELECT EXISTS (SELECT 1 FROM public.asignaciones "
                + "WHERE id_ruta = ? AND estado_asignacion = 'EN_CURSO')";
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            statement.setInt(1, idRuta);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBoolean(1);
            }
        } catch (SQLException exception) {
            registrarError("verificar asignaciones activas de la ruta", exception);
            return true;
        }
    }

    private List<RutaDestino> ejecutarListado(String sql, String estado) {
        List<RutaDestino> rutas = new ArrayList<>();
        try (PreparedStatement statement = obtenerConexion().prepareStatement(sql)) {
            if (estado != null) {
                statement.setString(1, estado);
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rutas.add(mapear(result));
                }
            }
        } catch (SQLException exception) {
            registrarError("listar rutas", exception);
        }
        return rutas;
    }

    private void asignarDatos(PreparedStatement statement, RutaDestino ruta,
                              boolean incluirId) throws SQLException {
        statement.setString(1, ruta.getNombreProyecto());
        statement.setString(2, ruta.getPuntoOrigen());
        statement.setString(3, ruta.getPuntoDestino());
        if (ruta.getDistanciaKm() == null) {
            statement.setNull(4, Types.NUMERIC);
        } else {
            statement.setBigDecimal(4, ruta.getDistanciaKm());
        }
        statement.setString(5, ruta.getEstado());
        if (incluirId) {
            statement.setInt(6, ruta.getIdRuta());
        }
    }

    private RutaDestino mapear(ResultSet result) throws SQLException {
        return new RutaDestino(
                result.getInt("id_ruta"),
                result.getString("nombre_proyecto"),
                result.getString("punto_origen"),
                result.getString("punto_destino"),
                result.getBigDecimal("distancia_km"),
                result.getString("estado"));
    }

    private Connection obtenerConexion() throws SQLException {
        Connection connection = ConexionBD.getInstancia().getConexion();
        if (connection == null) {
            throw new SQLException("No existe una conexión disponible.");
        }
        return connection;
    }

    private void registrarError(String operacion, SQLException exception) {
        System.err.println("[RUTA DAO] Error al " + operacion + ": "
                + exception.getMessage());
    }
}

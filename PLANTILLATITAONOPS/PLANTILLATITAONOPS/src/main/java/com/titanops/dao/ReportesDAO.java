package com.titanops.dao;

import com.titanops.conexion.ConexionBD;
import com.titanops.reporte.ReportesDTO.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * DAO especializado en consultas analíticas y agregaciones para los 9 reportes
 * operativos de TitanOps sobre PostgreSQL (Supabase).
 */
public class ReportesDAO {

    private final Connection conexionFija;

    /**
     * Constructor por defecto compatible con el resto de DAOs de TitanOps.
     * Obtiene la conexión activa desde el Singleton {@link ConexionBD}.
     */
    public ReportesDAO() {
        this.conexionFija = null;
    }

    /**
     * Constructor con inyección de conexión explícita (útil para pruebas o llamadas directas).
     */
    public ReportesDAO(Connection conexion) {
        this.conexionFija = conexion;
    }

    // =========================================================================
    // REPORTE 1: Ratio de Mantenimiento Preventivo vs. Correctivo
    // =========================================================================

    public List<RatioMantenimientoDTO> obtenerRatioMantenimiento(
            Date fechaInicio, Date fechaFin, Integer idMaquinaria) throws SQLException {
        return obtenerRatioMantenimiento(fechaInicio, fechaFin, idMaquinaria, null);
    }

    public List<RatioMantenimientoDTO> obtenerRatioMantenimiento(
            Date fechaInicio, Date fechaFin, Integer idMaquinaria, Integer idCategoria)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT m.id_maquinaria, m.codigo_inventario, "
                + "TRIM(CONCAT(COALESCE(m.marca, ''), ' ', COALESCE(m.modelo, ''))) AS marca_modelo, "
                + "c.nombre_categoria, mt.tipo_mantenimiento, "
                + "COUNT(mt.id_mantenimiento) AS total_intervenciones, "
                + "COALESCE(SUM(mt.costo), 0) AS costo_total, "
                + "COALESCE(SUM(EXTRACT(EPOCH FROM (COALESCE(mt.fecha_salida_real, NOW()) - mt.fecha_ingreso)) / 3600.0), 0) AS horas_taller "
                + "FROM public.mantenimientos mt "
                + "INNER JOIN public.maquinaria m ON mt.id_maquinaria = m.id_maquinaria "
                + "INNER JOIN public.categorias_maquinaria c ON m.id_categoria = c.id_categoria "
                + "WHERE mt.estado_mantenimiento != 'CANCELADO' ");

        List<ParametroSql> params = new ArrayList<>();
        if (fechaInicio != null) {
            sql.append("AND DATE(mt.fecha_ingreso) >= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaInicio));
        }
        if (fechaFin != null) {
            sql.append("AND DATE(mt.fecha_ingreso) <= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaFin));
        }
        if (idMaquinaria != null && idMaquinaria > 0) {
            sql.append("AND m.id_maquinaria = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idMaquinaria));
        }
        if (idCategoria != null && idCategoria > 0) {
            sql.append("AND m.id_categoria = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idCategoria));
        }

        sql.append("GROUP BY m.id_maquinaria, m.codigo_inventario, m.marca, m.modelo, ")
           .append("c.nombre_categoria, mt.tipo_mantenimiento ")
           .append("ORDER BY m.codigo_inventario, mt.tipo_mantenimiento");

        List<RatioMantenimientoDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new RatioMantenimientoDTO(
                        rs.getInt("id_maquinaria"),
                        rs.getString("codigo_inventario"),
                        rs.getString("marca_modelo"),
                        rs.getString("nombre_categoria"),
                        rs.getString("tipo_mantenimiento"),
                        rs.getInt("total_intervenciones"),
                        rs.getBigDecimal("costo_total"),
                        rs.getDouble("horas_taller")
                ));
            }
        }
        return lista;
    }

    // =========================================================================
    // REPORTE 2: Costo de Mantenimiento por Hora de Uso (Índice TCO Operativo)
    // =========================================================================

    public List<CostoHoraUsoDTO> obtenerCostoPorHoraUso() throws SQLException {
        return obtenerCostoPorHoraUso(null, Boolean.TRUE);
    }

    public List<CostoHoraUsoDTO> obtenerCostoPorHoraUso(
            Integer idCategoria, Boolean activo) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT m.id_maquinaria, m.codigo_inventario, "
                + "TRIM(CONCAT(COALESCE(m.marca, ''), ' ', COALESCE(m.modelo, ''))) AS marca_modelo, "
                + "c.nombre_categoria, COALESCE(m.horas_uso, 0) AS horas_uso, "
                + "COUNT(mt.id_mantenimiento) AS total_intervenciones, "
                + "COALESCE(SUM(mt.costo), 0) AS costo_acumulado, "
                + "CASE WHEN COALESCE(m.horas_uso, 0) > 0 "
                + "  THEN ROUND(COALESCE(SUM(mt.costo), 0) / m.horas_uso, 2) "
                + "  ELSE 0 END AS costo_por_hora, "
                + "CASE WHEN COUNT(mt.id_mantenimiento) > 0 "
                + "  THEN ROUND(COALESCE(SUM(mt.costo), 0) / COUNT(mt.id_mantenimiento), 2) "
                + "  ELSE 0 END AS costo_promedio_intervencion, "
                + "COALESCE(m.activo, false) AS activo "
                + "FROM public.maquinaria m "
                + "INNER JOIN public.categorias_maquinaria c ON m.id_categoria = c.id_categoria "
                + "LEFT JOIN public.mantenimientos mt ON m.id_maquinaria = mt.id_maquinaria "
                + "  AND mt.estado_mantenimiento != 'CANCELADO' "
                + "WHERE 1 = 1 ");

        List<ParametroSql> params = new ArrayList<>();
        if (idCategoria != null && idCategoria > 0) {
            sql.append("AND m.id_categoria = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idCategoria));
        }
        if (activo != null) {
            sql.append("AND COALESCE(m.activo, false) = ? ");
            params.add(ps -> ps.setBoolean(ps.nextIndex(), activo));
        }

        sql.append("GROUP BY m.id_maquinaria, m.codigo_inventario, m.marca, m.modelo, ")
           .append("c.nombre_categoria, m.horas_uso, m.activo ")
           .append("ORDER BY costo_por_hora DESC, m.codigo_inventario ASC");

        List<CostoHoraUsoDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new CostoHoraUsoDTO(
                        rs.getInt("id_maquinaria"),
                        rs.getString("codigo_inventario"),
                        rs.getString("marca_modelo"),
                        rs.getString("nombre_categoria"),
                        rs.getBigDecimal("horas_uso"),
                        rs.getInt("total_intervenciones"),
                        rs.getBigDecimal("costo_acumulado"),
                        rs.getBigDecimal("costo_por_hora"),
                        rs.getBigDecimal("costo_promedio_intervencion"),
                        rs.getBoolean("activo")
                ));
            }
        }
        return lista;
    }

    // =========================================================================
    // REPORTE 3: Cumplimiento de Tiempos de Rutas (SLA Logístico / Puntualidad)
    // =========================================================================

    public Map<String, Integer> obtenerResumenCumplimientoRutas(
            Date fechaInicio, Date fechaFin) throws SQLException {
        return obtenerResumenCumplimientoRutas(fechaInicio, fechaFin, null, null);
    }

    public Map<String, Integer> obtenerResumenCumplimientoRutas(
            Date fechaInicio, Date fechaFin, Integer idRuta, Integer idOperador)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT "
                + "CASE WHEN fecha_retorno_real <= fecha_estimada_retorno "
                + "  THEN 'A Tiempo / Adelantado' ELSE 'Con Retraso' END AS clasificacion, "
                + "COUNT(id_asignacion) AS total "
                + "FROM public.asignaciones "
                + "WHERE estado_asignacion = 'FINALIZADA' "
                + "AND fecha_retorno_real IS NOT NULL ");

        List<ParametroSql> params = new ArrayList<>();
        if (fechaInicio != null) {
            sql.append("AND DATE(fecha_asignacion) >= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaInicio));
        }
        if (fechaFin != null) {
            sql.append("AND DATE(fecha_asignacion) <= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaFin));
        }
        if (idRuta != null && idRuta > 0) {
            sql.append("AND id_ruta = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idRuta));
        }
        if (idOperador != null && idOperador > 0) {
            sql.append("AND id_operador = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idOperador));
        }
        sql.append("GROUP BY clasificacion");

        Map<String, Integer> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("clasificacion"), rs.getInt("total"));
            }
        }
        return resultado;
    }

    public List<CumplimientoRutaDTO> obtenerCumplimientoRutas(
            Date fechaInicio, Date fechaFin, Integer idRuta, Integer idOperador)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT a.id_asignacion, r.nombre_proyecto, "
                + "CONCAT(r.punto_origen, ' -> ', r.punto_destino) AS trayecto, "
                + "m.codigo_inventario, CONCAT(o.nombres, ' ', o.apellidos) AS operador, "
                + "a.fecha_asignacion, a.fecha_estimada_retorno, a.fecha_retorno_real, "
                + "EXTRACT(EPOCH FROM (a.fecha_retorno_real - a.fecha_estimada_retorno)) / 3600.0 AS desviacion_horas, "
                + "CASE WHEN a.fecha_retorno_real <= a.fecha_estimada_retorno "
                + "  THEN 'A Tiempo / Adelantado' ELSE 'Con Retraso' END AS clasificacion "
                + "FROM public.asignaciones a "
                + "INNER JOIN public.rutas_destinos r ON a.id_ruta = r.id_ruta "
                + "INNER JOIN public.maquinaria m ON a.id_maquinaria = m.id_maquinaria "
                + "INNER JOIN public.operadores o ON a.id_operador = o.id_operador "
                + "WHERE a.estado_asignacion = 'FINALIZADA' "
                + "AND a.fecha_retorno_real IS NOT NULL ");

        List<ParametroSql> params = new ArrayList<>();
        if (fechaInicio != null) {
            sql.append("AND DATE(a.fecha_asignacion) >= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaInicio));
        }
        if (fechaFin != null) {
            sql.append("AND DATE(a.fecha_asignacion) <= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaFin));
        }
        if (idRuta != null && idRuta > 0) {
            sql.append("AND a.id_ruta = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idRuta));
        }
        if (idOperador != null && idOperador > 0) {
            sql.append("AND a.id_operador = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idOperador));
        }
        sql.append("ORDER BY a.fecha_retorno_real DESC");

        List<CumplimientoRutaDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new CumplimientoRutaDTO(
                        rs.getInt("id_asignacion"),
                        rs.getString("nombre_proyecto"),
                        rs.getString("trayecto"),
                        rs.getString("codigo_inventario"),
                        rs.getString("operador"),
                        rs.getTimestamp("fecha_asignacion"),
                        rs.getTimestamp("fecha_estimada_retorno"),
                        rs.getTimestamp("fecha_retorno_real"),
                        rs.getDouble("desviacion_horas"),
                        rs.getString("clasificacion")
                ));
            }
        }
        return lista;
    }

    // =========================================================================
    // REPORTE 5: Snapshot Maestro de Maquinarias y Disponibilidad en Tiempo Real
    // =========================================================================

    public Map<String, Integer> obtenerDistribucionEstadoMaquinaria() throws SQLException {
        return obtenerDistribucionEstadoMaquinaria(null);
    }

    public Map<String, Integer> obtenerDistribucionEstadoMaquinaria(Integer idCategoria)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT estado_operativo, COUNT(id_maquinaria) AS total "
                + "FROM public.maquinaria "
                + "WHERE activo = true ");

        List<ParametroSql> params = new ArrayList<>();
        if (idCategoria != null && idCategoria > 0) {
            sql.append("AND id_categoria = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idCategoria));
        }
        sql.append("GROUP BY estado_operativo ORDER BY estado_operativo");

        Map<String, Integer> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString("estado_operativo"), rs.getInt("total"));
            }
        }
        return resultado;
    }

    public List<SnapshotMaquinariaDTO> obtenerSnapshotMaquinaria() throws SQLException {
        return obtenerSnapshotMaquinaria(null, null, null);
    }

    public List<SnapshotMaquinariaDTO> obtenerSnapshotMaquinaria(
            Boolean activo, String estadoOperativo, Integer idCategoria) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT m.id_maquinaria, m.codigo_inventario, m.marca, m.modelo, "
                + "c.nombre_categoria, COALESCE(m.tonelaje, 0) AS tonelaje, "
                + "COALESCE(m.horas_uso, 0) AS horas_uso, m.estado_operativo, "
                + "COALESCE(m.activo, false) AS activo, m.fecha_registro, "
                + "COALESCE(a_res.total_asignaciones, 0) AS total_asignaciones, "
                + "COALESCE(mt_res.total_mantenimientos, 0) AS total_mantenimientos, "
                + "a_res.ultima_asignacion "
                + "FROM public.maquinaria m "
                + "INNER JOIN public.categorias_maquinaria c ON m.id_categoria = c.id_categoria "
                + "LEFT JOIN ("
                + "  SELECT id_maquinaria, COUNT(id_asignacion) AS total_asignaciones, "
                + "         MAX(fecha_asignacion) AS ultima_asignacion "
                + "  FROM public.asignaciones GROUP BY id_maquinaria"
                + ") a_res ON m.id_maquinaria = a_res.id_maquinaria "
                + "LEFT JOIN ("
                + "  SELECT id_maquinaria, COUNT(id_mantenimiento) AS total_mantenimientos "
                + "  FROM public.mantenimientos GROUP BY id_maquinaria"
                + ") mt_res ON m.id_maquinaria = mt_res.id_maquinaria "
                + "WHERE 1 = 1 ");

        List<ParametroSql> params = new ArrayList<>();
        if (activo != null) {
            sql.append("AND COALESCE(m.activo, false) = ? ");
            params.add(ps -> ps.setBoolean(ps.nextIndex(), activo));
        }
        String estadoNorm = normalizarFiltroTexto(estadoOperativo);
        if (estadoNorm != null) {
            sql.append("AND UPPER(m.estado_operativo) = ? ");
            params.add(ps -> ps.setString(ps.nextIndex(), estadoNorm));
        }
        if (idCategoria != null && idCategoria > 0) {
            sql.append("AND m.id_categoria = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idCategoria));
        }
        sql.append("ORDER BY m.codigo_inventario ASC");

        List<SnapshotMaquinariaDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new SnapshotMaquinariaDTO(
                        rs.getInt("id_maquinaria"),
                        rs.getString("codigo_inventario"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("nombre_categoria"),
                        rs.getBigDecimal("tonelaje"),
                        rs.getBigDecimal("horas_uso"),
                        rs.getString("estado_operativo"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_registro"),
                        rs.getInt("total_asignaciones"),
                        rs.getInt("total_mantenimientos"),
                        rs.getTimestamp("ultima_asignacion")
                ));
            }
        }
        return lista;
    }

    // =========================================================================
    // REPORTE 6: Certificaciones y Vigencia Operativa (Compliance)
    // =========================================================================

    public List<CertificacionVigenciaDTO> obtenerEstadoCertificaciones() throws SQLException {
        return obtenerEstadoCertificaciones(null, null, null);
    }

    public List<CertificacionVigenciaDTO> obtenerEstadoCertificaciones(
            String estadoVigencia, Integer idCategoria, String licenciaTipo) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM ("
                + "  SELECT CONCAT(o.nombres, ' ', o.apellidos) AS operador, o.dui, "
                + "         o.licencia_tipo, o.estado_operativo, "
                + "         c.id_categoria, c.nombre_categoria, oc.numero_acreditacion, "
                + "         oc.fecha_expedicion, oc.fecha_vencimiento, "
                + "         (oc.fecha_vencimiento - CURRENT_DATE) AS dias_restantes, "
                + "         CASE "
                + "           WHEN (oc.fecha_vencimiento - CURRENT_DATE) <= 0 THEN 'VENCIDA' "
                + "           WHEN (oc.fecha_vencimiento - CURRENT_DATE) <= 30 THEN 'POR_VENCER' "
                + "           ELSE 'VIGENTE' "
                + "         END AS estado_vigencia "
                + "  FROM public.operadores_certificaciones oc "
                + "  INNER JOIN public.operadores o ON oc.id_operador = o.id_operador "
                + "  INNER JOIN public.categorias_maquinaria c ON oc.id_categoria = c.id_categoria "
                + "  WHERE o.activo = true"
                + ") cert_sub WHERE 1 = 1 ");

        List<ParametroSql> params = new ArrayList<>();
        String vigenciaNorm = normalizarFiltroTexto(estadoVigencia);
        if (vigenciaNorm != null) {
            sql.append("AND cert_sub.estado_vigencia = ? ");
            params.add(ps -> ps.setString(ps.nextIndex(), vigenciaNorm));
        }
        if (idCategoria != null && idCategoria > 0) {
            sql.append("AND cert_sub.id_categoria = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idCategoria));
        }
        String licenciaNorm = normalizarFiltroTexto(licenciaTipo);
        if (licenciaNorm != null) {
            sql.append("AND UPPER(cert_sub.licencia_tipo) = ? ");
            params.add(ps -> ps.setString(ps.nextIndex(), licenciaNorm));
        }
        sql.append("ORDER BY cert_sub.dias_restantes ASC, cert_sub.operador ASC");

        List<CertificacionVigenciaDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new CertificacionVigenciaDTO(
                        rs.getString("operador"),
                        rs.getString("dui"),
                        rs.getString("licencia_tipo"),
                        rs.getString("estado_operativo"),
                        rs.getString("nombre_categoria"),
                        rs.getString("numero_acreditacion"),
                        rs.getDate("fecha_expedicion"),
                        rs.getDate("fecha_vencimiento"),
                        rs.getInt("dias_restantes"),
                        rs.getString("estado_vigencia")
                ));
            }
        }
        return lista;
    }

    // =========================================================================
    // REPORTE 7: Auditoría y Gestión de Usuarios
    // =========================================================================

    public List<AuditoriaUsuarioDTO> obtenerAuditoriaUsuarios() throws SQLException {
        return obtenerAuditoriaUsuarios(null, null, null, null, null);
    }

    public List<AuditoriaUsuarioDTO> obtenerAuditoriaUsuarios(
            Integer idRol, Integer idUsuario, Boolean activo,
            Date fechaInicio, Date fechaFin) throws SQLException {
        List<ParametroSql> params = new ArrayList<>();

        StringBuilder subAsig = new StringBuilder(
                "SELECT id_usuario_registro, "
                + "COUNT(id_asignacion) AS total_asignaciones, "
                + "COUNT(CASE WHEN estado_asignacion = 'CANCELADA' THEN 1 END) AS asignaciones_canceladas "
                + "FROM public.asignaciones WHERE 1 = 1 ");
        if (fechaInicio != null) {
            subAsig.append("AND DATE(fecha_asignacion) >= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaInicio));
        }
        if (fechaFin != null) {
            subAsig.append("AND DATE(fecha_asignacion) <= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaFin));
        }
        subAsig.append("GROUP BY id_usuario_registro");

        StringBuilder subMant = new StringBuilder(
                "SELECT id_usuario_registro, "
                + "COUNT(id_mantenimiento) AS total_mantenimientos, "
                + "COUNT(CASE WHEN estado_mantenimiento = 'CANCELADO' THEN 1 END) AS mantenimientos_cancelados, "
                + "COALESCE(SUM(CASE WHEN estado_mantenimiento != 'CANCELADO' THEN costo ELSE 0 END), 0) AS monto_taller "
                + "FROM public.mantenimientos WHERE 1 = 1 ");
        if (fechaInicio != null) {
            subMant.append("AND DATE(fecha_ingreso) >= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaInicio));
        }
        if (fechaFin != null) {
            subMant.append("AND DATE(fecha_ingreso) <= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaFin));
        }
        subMant.append("GROUP BY id_usuario_registro");

        StringBuilder sql = new StringBuilder(
                "SELECT u.id_usuario, u.nombre_completo, u.username, r.nombre_rol, "
                + "COALESCE(u.activo, false) AS activo, u.fecha_creacion, "
                + "COALESCE(a_res.total_asignaciones, 0) AS total_asignaciones, "
                + "COALESCE(a_res.asignaciones_canceladas, 0) AS asignaciones_canceladas, "
                + "COALESCE(m_res.total_mantenimientos, 0) AS total_mantenimientos, "
                + "COALESCE(m_res.mantenimientos_cancelados, 0) AS mantenimientos_cancelados, "
                + "COALESCE(m_res.monto_taller, 0) AS monto_taller "
                + "FROM public.usuarios u "
                + "INNER JOIN public.roles r ON u.id_rol = r.id_rol "
                + "LEFT JOIN (" + subAsig + ") a_res ON u.id_usuario = a_res.id_usuario_registro "
                + "LEFT JOIN (" + subMant + ") m_res ON u.id_usuario = m_res.id_usuario_registro "
                + "WHERE 1 = 1 ");

        if (idRol != null && idRol > 0) {
            sql.append("AND u.id_rol = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idRol));
        }
        if (idUsuario != null && idUsuario > 0) {
            sql.append("AND u.id_usuario = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idUsuario));
        }
        if (activo != null) {
            sql.append("AND COALESCE(u.activo, false) = ? ");
            params.add(ps -> ps.setBoolean(ps.nextIndex(), activo));
        }
        sql.append("ORDER BY (COALESCE(a_res.total_asignaciones, 0) + COALESCE(m_res.total_mantenimientos, 0)) DESC, ")
           .append("u.nombre_completo ASC");

        List<AuditoriaUsuarioDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new AuditoriaUsuarioDTO(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_completo"),
                        rs.getString("username"),
                        rs.getString("nombre_rol"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_creacion"),
                        rs.getInt("total_asignaciones"),
                        rs.getInt("asignaciones_canceladas"),
                        rs.getInt("total_mantenimientos"),
                        rs.getInt("mantenimientos_cancelados"),
                        rs.getBigDecimal("monto_taller")
                ));
            }
        }
        return lista;
    }

    // =========================================================================
    // REPORTE 8: Utilización y Desgaste de Flota
    // =========================================================================

    public List<UtilizacionDesgasteDTO> obtenerUtilizacionDesgasteFlota() throws SQLException {
        return obtenerUtilizacionDesgasteFlota(null, null, null);
    }

    public List<UtilizacionDesgasteDTO> obtenerUtilizacionDesgasteFlota(
            Integer idCategoria, Date fechaInicio, Date fechaFin) throws SQLException {
        List<ParametroSql> params = new ArrayList<>();

        StringBuilder subRuta = new StringBuilder(
                "SELECT id_maquinaria, "
                + "COALESCE(SUM(EXTRACT(EPOCH FROM (COALESCE(fecha_retorno_real, NOW()) - fecha_asignacion)) / 3600.0), 0) AS horas_ruta "
                + "FROM public.asignaciones "
                + "WHERE estado_asignacion != 'CANCELADA' AND fecha_asignacion IS NOT NULL ");
        if (fechaInicio != null) {
            subRuta.append("AND DATE(fecha_asignacion) >= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaInicio));
        }
        if (fechaFin != null) {
            subRuta.append("AND DATE(fecha_asignacion) <= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaFin));
        }
        subRuta.append("GROUP BY id_maquinaria");

        StringBuilder subTaller = new StringBuilder(
                "SELECT id_maquinaria, "
                + "COALESCE(SUM(EXTRACT(EPOCH FROM (COALESCE(fecha_salida_real, NOW()) - fecha_ingreso)) / 3600.0), 0) AS horas_taller "
                + "FROM public.mantenimientos "
                + "WHERE estado_mantenimiento != 'CANCELADO' AND fecha_ingreso IS NOT NULL ");
        if (fechaInicio != null) {
            subTaller.append("AND DATE(fecha_ingreso) >= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaInicio));
        }
        if (fechaFin != null) {
            subTaller.append("AND DATE(fecha_ingreso) <= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaFin));
        }
        subTaller.append("GROUP BY id_maquinaria");

        StringBuilder sql = new StringBuilder(
                "SELECT m.id_maquinaria, m.codigo_inventario, "
                + "TRIM(CONCAT(COALESCE(m.marca, ''), ' ', COALESCE(m.modelo, ''))) AS marca_modelo, "
                + "c.nombre_categoria, m.fecha_registro, "
                + "GREATEST((CURRENT_DATE - DATE(COALESCE(m.fecha_registro, NOW()))), 1) AS antiguedad_dias, "
                + "COALESCE(m.horas_uso, 0) AS horas_uso, "
                + "ROUND(COALESCE(m.horas_uso, 0) / GREATEST((CURRENT_DATE - DATE(COALESCE(m.fecha_registro, NOW()))), 1), 2) AS promedio_uso_diario, "
                + "COALESCE(a_res.horas_ruta, 0) AS horas_ruta, "
                + "COALESCE(mt_res.horas_taller, 0) AS horas_taller "
                + "FROM public.maquinaria m "
                + "INNER JOIN public.categorias_maquinaria c ON m.id_categoria = c.id_categoria "
                + "LEFT JOIN (" + subRuta + ") a_res ON m.id_maquinaria = a_res.id_maquinaria "
                + "LEFT JOIN (" + subTaller + ") mt_res ON m.id_maquinaria = mt_res.id_maquinaria "
                + "WHERE m.activo = true ");

        if (idCategoria != null && idCategoria > 0) {
            sql.append("AND m.id_categoria = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idCategoria));
        }
        sql.append("ORDER BY promedio_uso_diario DESC, m.codigo_inventario ASC");

        List<UtilizacionDesgasteDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new UtilizacionDesgasteDTO(
                        rs.getInt("id_maquinaria"),
                        rs.getString("codigo_inventario"),
                        rs.getString("marca_modelo"),
                        rs.getString("nombre_categoria"),
                        rs.getTimestamp("fecha_registro"),
                        rs.getInt("antiguedad_dias"),
                        rs.getBigDecimal("horas_uso"),
                        rs.getBigDecimal("promedio_uso_diario"),
                        rs.getDouble("horas_ruta"),
                        rs.getDouble("horas_taller")
                ));
            }
        }
        return lista;
    }

    // =========================================================================
    // REPORTE 9: Historial de Rutas, Proyectos y Bitácora de Observaciones
    // =========================================================================

    public List<HistorialRutaDTO> obtenerHistorialRutasYObservaciones(Integer idRuta)
            throws SQLException {
        return obtenerHistorialRutasYObservaciones(idRuta, null, null, null);
    }

    public List<HistorialRutaDTO> obtenerHistorialRutasYObservaciones(
            Integer idRuta, String estadoRuta, String estadoAsignacion, Integer idOperador)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT a.id_asignacion, r.nombre_proyecto, "
                + "CONCAT(r.punto_origen, ' -> ', r.punto_destino) AS trayecto, "
                + "r.estado AS estado_ruta, m.codigo_inventario, "
                + "CONCAT(o.nombres, ' ', o.apellidos) AS operador, "
                + "o.turno, COALESCE(o.telefono, '') AS telefono, "
                + "a.estado_asignacion, a.fecha_asignacion, a.fecha_retorno_real, "
                + "COALESCE(a.observaciones, 'Sin observaciones') AS observaciones "
                + "FROM public.asignaciones a "
                + "INNER JOIN public.rutas_destinos r ON a.id_ruta = r.id_ruta "
                + "INNER JOIN public.maquinaria m ON a.id_maquinaria = m.id_maquinaria "
                + "INNER JOIN public.operadores o ON a.id_operador = o.id_operador "
                + "WHERE 1 = 1 ");

        List<ParametroSql> params = new ArrayList<>();
        if (idRuta != null && idRuta > 0) {
            sql.append("AND a.id_ruta = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idRuta));
        }
        String estadoRutaNorm = normalizarFiltroTexto(estadoRuta);
        if (estadoRutaNorm != null) {
            sql.append("AND UPPER(r.estado) = ? ");
            params.add(ps -> ps.setString(ps.nextIndex(), estadoRutaNorm));
        }
        String estadoAsigNorm = normalizarFiltroTexto(estadoAsignacion);
        if (estadoAsigNorm != null) {
            sql.append("AND UPPER(a.estado_asignacion) = ? ");
            params.add(ps -> ps.setString(ps.nextIndex(), estadoAsigNorm));
        }
        if (idOperador != null && idOperador > 0) {
            sql.append("AND a.id_operador = ? ");
            params.add(ps -> ps.setInt(ps.nextIndex(), idOperador));
        }
        sql.append("ORDER BY a.fecha_asignacion DESC, a.id_asignacion DESC");

        List<HistorialRutaDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new HistorialRutaDTO(
                        rs.getInt("id_asignacion"),
                        rs.getString("nombre_proyecto"),
                        rs.getString("trayecto"),
                        rs.getString("estado_ruta"),
                        rs.getString("codigo_inventario"),
                        rs.getString("operador"),
                        rs.getString("turno"),
                        rs.getString("telefono"),
                        rs.getString("estado_asignacion"),
                        rs.getTimestamp("fecha_asignacion"),
                        rs.getTimestamp("fecha_retorno_real"),
                        rs.getString("observaciones")
                ));
            }
        }
        return lista;
    }

    // =========================================================================
    // REPORTE 10: Productividad y Carga Laboral de Operadores por Turno
    // =========================================================================

    public List<Object[]> obtenerProductividadPorTurno(
            Date fechaInicio, Date fechaFin) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT o.turno, a.estado_asignacion, COUNT(a.id_asignacion) AS total_viajes "
                + "FROM public.operadores o "
                + "INNER JOIN public.asignaciones a ON o.id_operador = a.id_operador "
                + "WHERE 1 = 1 ");

        List<ParametroSql> params = new ArrayList<>();
        if (fechaInicio != null) {
            sql.append("AND DATE(a.fecha_asignacion) >= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaInicio));
        }
        if (fechaFin != null) {
            sql.append("AND DATE(a.fecha_asignacion) <= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaFin));
        }
        sql.append("GROUP BY o.turno, a.estado_asignacion ORDER BY o.turno, a.estado_asignacion");

        List<Object[]> resultados = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultados.add(new Object[]{
                    rs.getString("turno"),
                    rs.getString("estado_asignacion"),
                    rs.getInt("total_viajes")
                });
            }
        }
        return resultados;
    }

    public List<ProductividadOperadorDTO> obtenerProductividadOperadores() throws SQLException {
        return obtenerProductividadOperadores(null, null, null, null);
    }

    public List<ProductividadOperadorDTO> obtenerProductividadOperadores(
            String turno, String estadoOperativo, Date fechaInicio, Date fechaFin)
            throws SQLException {
        List<ParametroSql> params = new ArrayList<>();

        StringBuilder subAsig = new StringBuilder(
                "SELECT id_operador, "
                + "COUNT(id_asignacion) AS total_viajes, "
                + "COUNT(CASE WHEN estado_asignacion = 'FINALIZADA' THEN 1 END) AS viajes_finalizados, "
                + "COUNT(CASE WHEN estado_asignacion = 'CANCELADA' THEN 1 END) AS viajes_cancelados, "
                + "COUNT(CASE WHEN estado_asignacion = 'EN_CURSO' THEN 1 END) AS viajes_en_curso, "
                + "COALESCE(SUM(CASE WHEN estado_asignacion = 'FINALIZADA' "
                + "  AND fecha_retorno_real IS NOT NULL AND fecha_asignacion IS NOT NULL "
                + "  THEN EXTRACT(EPOCH FROM (fecha_retorno_real - fecha_asignacion)) / 3600.0 "
                + "  ELSE 0 END), 0) AS horas_conducidas "
                + "FROM public.asignaciones WHERE 1 = 1 ");
        if (fechaInicio != null) {
            subAsig.append("AND DATE(fecha_asignacion) >= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaInicio));
        }
        if (fechaFin != null) {
            subAsig.append("AND DATE(fecha_asignacion) <= ? ");
            params.add(ps -> ps.setDate(ps.nextIndex(), fechaFin));
        }
        subAsig.append("GROUP BY id_operador");

        StringBuilder sql = new StringBuilder(
                "SELECT o.id_operador, CONCAT(o.nombres, ' ', o.apellidos) AS operador, "
                + "o.dui, o.licencia_tipo, o.turno, o.estado_operativo, "
                + "COALESCE(cert_res.categorias_certificadas, 0) AS categorias_certificadas, "
                + "COALESCE(asig_res.total_viajes, 0) AS total_viajes, "
                + "COALESCE(asig_res.viajes_finalizados, 0) AS viajes_finalizados, "
                + "COALESCE(asig_res.viajes_cancelados, 0) AS viajes_cancelados, "
                + "COALESCE(asig_res.viajes_en_curso, 0) AS viajes_en_curso, "
                + "COALESCE(asig_res.horas_conducidas, 0) AS horas_conducidas "
                + "FROM public.operadores o "
                + "LEFT JOIN ("
                + "  SELECT id_operador, COUNT(DISTINCT id_categoria) AS categorias_certificadas "
                + "  FROM public.operadores_certificaciones GROUP BY id_operador"
                + ") cert_res ON o.id_operador = cert_res.id_operador "
                + "LEFT JOIN (" + subAsig + ") asig_res ON o.id_operador = asig_res.id_operador "
                + "WHERE o.activo = true ");

        String turnoNorm = normalizarFiltroTexto(turno);
        if (turnoNorm != null) {
            sql.append("AND UPPER(o.turno) = ? ");
            params.add(ps -> ps.setString(ps.nextIndex(), turnoNorm));
        }
        String estadoNorm = normalizarFiltroTexto(estadoOperativo);
        if (estadoNorm != null) {
            sql.append("AND UPPER(o.estado_operativo) = ? ");
            params.add(ps -> ps.setString(ps.nextIndex(), estadoNorm));
        }
        sql.append("ORDER BY o.turno ASC, horas_conducidas DESC, o.apellidos ASC");

        List<ProductividadOperadorDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = prepararConsulta(sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new ProductividadOperadorDTO(
                        rs.getInt("id_operador"),
                        rs.getString("operador"),
                        rs.getString("dui"),
                        rs.getString("licencia_tipo"),
                        rs.getString("turno"),
                        rs.getString("estado_operativo"),
                        rs.getInt("categorias_certificadas"),
                        rs.getInt("total_viajes"),
                        rs.getInt("viajes_finalizados"),
                        rs.getInt("viajes_cancelados"),
                        rs.getInt("viajes_en_curso"),
                        rs.getDouble("horas_conducidas")
                ));
            }
        }
        return lista;
    }

    // =========================================================================
    // Consultas de Catálogo para Selectores de Filtros en Vistas
    // =========================================================================

    public List<OpcionFiltro> listarOpcionesCategorias() throws SQLException {
        List<OpcionFiltro> opciones = new ArrayList<>();
        opciones.add(OpcionFiltro.todos("Todas las categorías"));
        String sql = "SELECT id_categoria, nombre_categoria FROM public.categorias_maquinaria "
                + "WHERE activo = true ORDER BY nombre_categoria";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                opciones.add(new OpcionFiltro(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre_categoria")));
            }
        }
        return opciones;
    }

    public List<OpcionFiltro> listarOpcionesMaquinaria() throws SQLException {
        List<OpcionFiltro> opciones = new ArrayList<>();
        opciones.add(OpcionFiltro.todos("Todas las maquinarias"));
        String sql = "SELECT id_maquinaria, codigo_inventario, marca, modelo "
                + "FROM public.maquinaria ORDER BY codigo_inventario";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String codigo = rs.getString("codigo_inventario");
                String desc = (valor(rs.getString("marca")) + " " + valor(rs.getString("modelo"))).trim();
                opciones.add(new OpcionFiltro(
                        rs.getInt("id_maquinaria"),
                        desc.isEmpty() ? codigo : codigo + " - " + desc));
            }
        }
        return opciones;
    }

    public List<OpcionFiltro> listarOpcionesRutas() throws SQLException {
        List<OpcionFiltro> opciones = new ArrayList<>();
        opciones.add(OpcionFiltro.todos("Todas las rutas / proyectos"));
        String sql = "SELECT id_ruta, nombre_proyecto, punto_origen, punto_destino "
                + "FROM public.rutas_destinos ORDER BY nombre_proyecto, id_ruta";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String etiqueta = rs.getString("nombre_proyecto") + " ("
                        + rs.getString("punto_origen") + " -> "
                        + rs.getString("punto_destino") + ")";
                opciones.add(new OpcionFiltro(rs.getInt("id_ruta"), etiqueta));
            }
        }
        return opciones;
    }

    public List<OpcionFiltro> listarOpcionesOperadores() throws SQLException {
        List<OpcionFiltro> opciones = new ArrayList<>();
        opciones.add(OpcionFiltro.todos("Todos los operadores"));
        String sql = "SELECT id_operador, nombres, apellidos, dui "
                + "FROM public.operadores ORDER BY apellidos, nombres";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String etiqueta = rs.getString("nombres") + " "
                        + rs.getString("apellidos") + " (" + rs.getString("dui") + ")";
                opciones.add(new OpcionFiltro(rs.getInt("id_operador"), etiqueta));
            }
        }
        return opciones;
    }

    public List<OpcionFiltro> listarOpcionesUsuarios() throws SQLException {
        List<OpcionFiltro> opciones = new ArrayList<>();
        opciones.add(OpcionFiltro.todos("Todos los usuarios"));
        String sql = "SELECT id_usuario, nombre_completo, username "
                + "FROM public.usuarios ORDER BY nombre_completo";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String etiqueta = rs.getString("nombre_completo") + " (@"
                        + rs.getString("username") + ")";
                opciones.add(new OpcionFiltro(rs.getInt("id_usuario"), etiqueta));
            }
        }
        return opciones;
    }

    public List<OpcionFiltro> listarOpcionesRoles() throws SQLException {
        List<OpcionFiltro> opciones = new ArrayList<>();
        opciones.add(OpcionFiltro.todos("Todos los roles"));
        String sql = "SELECT id_rol, nombre_rol FROM public.roles ORDER BY nombre_rol";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                opciones.add(new OpcionFiltro(
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol")));
            }
        }
        return opciones;
    }

    // =========================================================================
    // Infraestructura interna de conexión y preparación de consultas
    // =========================================================================

    protected Connection obtenerConexion() throws SQLException {
        if (conexionFija != null) {
            return conexionFija;
        }
        Connection connection = ConexionBD.getInstancia().getConexion();
        if (connection == null) {
            throw new SQLException("No existe una conexión disponible con la base de datos.");
        }
        return connection;
    }

    private PreparedStatement prepararConsulta(String sql, List<ParametroSql> parametros)
            throws SQLException {
        PreparedStatement statement = obtenerConexion().prepareStatement(sql);
        IndicePreparedStatement cursor = new IndicePreparedStatement(statement);
        for (ParametroSql parametro : parametros) {
            parametro.asignar(cursor);
        }
        return statement;
    }

    private static String normalizarFiltroTexto(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim().toUpperCase(Locale.ROOT);
        if (limpio.isEmpty() || "TODOS".equals(limpio) || "TODAS".equals(limpio)) {
            return null;
        }
        return limpio;
    }

    private static String valor(String texto) {
        return texto == null ? "" : texto.trim();
    }

    @FunctionalInterface
    private interface ParametroSql {
        void asignar(IndicePreparedStatement ps) throws SQLException;
    }

    private static final class IndicePreparedStatement {
        private final PreparedStatement delegate;
        private int indice = 0;

        private IndicePreparedStatement(PreparedStatement delegate) {
            this.delegate = delegate;
        }

        private int nextIndex() {
            return ++indice;
        }

        private void setDate(int idx, Date valor) throws SQLException {
            delegate.setDate(idx, valor);
        }

        private void setInt(int idx, int valor) throws SQLException {
            delegate.setInt(idx, valor);
        }

        private void setBoolean(int idx, boolean valor) throws SQLException {
            delegate.setBoolean(idx, valor);
        }

        private void setString(int idx, String valor) throws SQLException {
            delegate.setString(idx, valor);
        }
    }
}

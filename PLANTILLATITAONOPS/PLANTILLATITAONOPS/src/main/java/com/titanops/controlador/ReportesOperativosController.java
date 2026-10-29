package com.titanops.controlador;

import com.titanops.dao.ReportesDAO;
import com.titanops.reporte.GraficosReporteService;
import com.titanops.reporte.ReportesDTO.*;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.JFreeChart;

/**
 * Controlador de backend para los 9 reportes operativos del sistema TitanOps.
 * Orquesta consultas de {@link ReportesDAO}, cálculo de KPIs matemáticos y generación
 * de gráficos con {@link GraficosReporteService} sin acoplarse a vistas específicas.
 */
public class ReportesOperativosController {

    public static final List<String> FILTRO_ACTIVO_OPCIONES =
            List.of("TODOS", "ACTIVOS", "INACTIVOS");
    public static final List<String> ESTADOS_MAQUINARIA =
            List.of("TODOS", "DISPONIBLE", "EN_RUTA", "MANTENIMIENTO");
    public static final List<String> ESTADOS_VIGENCIA_CERTIFICACION =
            List.of("TODOS", "VENCIDA", "POR_VENCER", "VIGENTE");
    public static final List<String> ESTADOS_RUTA =
            List.of("TODOS", "PLANIFICADA", "EN_CURSO", "FINALIZADA", "CANCELADA");
    public static final List<String> ESTADOS_ASIGNACION =
            List.of("TODOS", "PROGRAMADA", "EN_CURSO", "FINALIZADA", "CANCELADA");
    public static final List<String> TURNOS_OPERADOR =
            List.of("TODOS", "DIURNO", "NOCTURNO", "ROTATIVO");
    public static final List<String> ESTADOS_OPERADOR =
            List.of("TODOS", "DISPONIBLE", "EN_RUTA", "DESCANSO", "INACTIVO");

    public static final String[] COLUMNAS_REPORTE_1 = {
        "CÓDIGO", "MARCA / MODELO", "CATEGORÍA", "TIPO", "INTERVENCIONES", "COSTO TOTAL ($)", "HORAS TALLER"
    };
    public static final String[] COLUMNAS_REPORTE_2 = {
        "CÓDIGO", "MARCA / MODELO", "CATEGORÍA", "HORAS USO", "INTERVENCIONES",
        "COSTO ACUMULADO ($)", "COSTO / HORA ($)", "COSTO PROM. ($)", "ESTADO"
    };
    public static final String[] COLUMNAS_REPORTE_3 = {
        "ID VIAJE", "PROYECTO", "TRAYECTO", "MAQUINARIA", "OPERADOR",
        "ASIGNACIÓN", "RETORNO EST.", "RETORNO REAL", "DESVIACIÓN (H)", "CLASIFICACIÓN SLA"
    };
    public static final String[] COLUMNAS_REPORTE_5 = {
        "CÓDIGO", "MARCA / MODELO", "CATEGORÍA", "TONELAJE", "HORAS USO",
        "ESTADO OPERATIVO", "ESTATUS", "RUTAS HIST.", "TALLERES HIST.", "ÚLTIMA RUTA", "REGISTRO"
    };
    public static final String[] COLUMNAS_REPORTE_6 = {
        "OPERADOR", "DUI", "LICENCIA", "CATEGORÍA AUTORIZADA", "N° ACREDITACIÓN",
        "EXPEDICIÓN", "VENCIMIENTO", "DÍAS RESTANTES", "VIGENCIA"
    };
    public static final String[] COLUMNAS_REPORTE_7 = {
        "ID", "NOMBRE COMPLETO", "USERNAME", "ROL", "ESTATUS",
        "DESPACHOS REG.", "DESPACHOS CANC.", "TALLERES REG.", "TALLERES CANC.", "MONTO TALLER ($)", "CREACIÓN"
    };
    public static final String[] COLUMNAS_REPORTE_8 = {
        "CÓDIGO", "MARCA / MODELO", "CATEGORÍA", "ANTIGÜEDAD (DÍAS)", "HORAS USO",
        "USO DIARIO PROM. (H)", "HORAS EN RUTA", "HORAS EN TALLER", "REGISTRO"
    };
    public static final String[] COLUMNAS_REPORTE_9 = {
        "ID VIAJE", "PROYECTO", "TRAYECTO", "ESTADO RUTA", "MAQUINARIA",
        "OPERADOR", "TURNO", "ESTADO VIAJE", "ASIGNACIÓN", "RETORNO REAL", "OBSERVACIONES"
    };
    public static final String[] COLUMNAS_REPORTE_10 = {
        "OPERADOR", "DUI", "LICENCIA", "TURNO", "ESTADO OPERATIVO",
        "CATEGORÍAS CERT.", "TOTAL VIAJES", "FINALIZADOS", "CANCELADOS", "HORAS CONDUCIDAS"
    };

    private final ReportesDAO reportesDAO;
    private final GraficosReporteService graficosService;

    public ReportesOperativosController() {
        this(new ReportesDAO(), new GraficosReporteService());
    }

    public ReportesOperativosController(ReportesDAO reportesDAO,
                                        GraficosReporteService graficosService) {
        this.reportesDAO = reportesDAO;
        this.graficosService = graficosService;
    }

    public ReportesDAO getReportesDAO() {
        return reportesDAO;
    }

    public GraficosReporteService getGraficosService() {
        return graficosService;
    }

    // =========================================================================
    // REPORTE 1: Ratio de Mantenimiento Preventivo vs. Correctivo
    // =========================================================================

    public ResultadoReporte<RatioMantenimientoDTO, KpiRatioMantenimientoDTO> generarReporte1RatioMantenimiento(
            Date fechaInicio, Date fechaFin, Integer idMaquinaria, Integer idCategoria)
            throws SQLException {
        List<RatioMantenimientoDTO> filas = reportesDAO.obtenerRatioMantenimiento(
                fechaInicio, fechaFin, idMaquinaria, idCategoria);
        KpiRatioMantenimientoDTO kpi = calcularKpiRatioMantenimiento(filas);
        JFreeChart graficoBarras = graficosService.crearGraficoRatioMantenimiento(filas);
        JFreeChart graficoPastel = graficosService.crearGraficoPastelRatioMantenimiento(filas);
        List<Object[]> filasTabla = filas.stream().map(RatioMantenimientoDTO::toTableRow).toList();
        return new ResultadoReporte<>(filas, kpi, graficoBarras, graficoPastel,
                COLUMNAS_REPORTE_1, filasTabla);
    }

    public KpiRatioMantenimientoDTO calcularKpiRatioMantenimiento(List<RatioMantenimientoDTO> filas) {
        int preventivos = 0;
        int correctivos = 0;
        BigDecimal costoPreventivo = BigDecimal.ZERO;
        BigDecimal costoCorrectivo = BigDecimal.ZERO;
        double horasTotales = 0.0;

        if (filas != null) {
            for (RatioMantenimientoDTO fila : filas) {
                horasTotales += fila.getHorasTaller();
                if ("CORRECTIVO".equalsIgnoreCase(fila.getTipoMantenimiento())) {
                    correctivos += fila.getTotalIntervenciones();
                    costoCorrectivo = costoCorrectivo.add(fila.getCostoTotal());
                } else {
                    preventivos += fila.getTotalIntervenciones();
                    costoPreventivo = costoPreventivo.add(fila.getCostoTotal());
                }
            }
        }
        int totalMantenimientos = preventivos + correctivos;
        BigDecimal costoTotal = costoPreventivo.add(costoCorrectivo);
        double pctIncidencias = totalMantenimientos > 0
                ? redondear((correctivos * 100.0) / totalMantenimientos) : 0.0;
        double pctGasto = costoTotal.compareTo(BigDecimal.ZERO) > 0
                ? redondear(costoCorrectivo.multiply(BigDecimal.valueOf(100))
                        .divide(costoTotal, 4, RoundingMode.HALF_UP).doubleValue())
                : 0.0;

        return new KpiRatioMantenimientoDTO(
                totalMantenimientos, preventivos, correctivos,
                pctIncidencias, costoPreventivo, costoCorrectivo, costoTotal,
                pctGasto, redondear(horasTotales));
    }

    // =========================================================================
    // REPORTE 2: Costo de Mantenimiento por Hora de Uso (Índice TCO Operativo)
    // =========================================================================

    public ResultadoReporte<CostoHoraUsoDTO, KpiCostoHoraUsoDTO> generarReporte2CostoHoraUso(
            Integer idCategoria, Boolean activo) throws SQLException {
        List<CostoHoraUsoDTO> filas = reportesDAO.obtenerCostoPorHoraUso(idCategoria, activo);
        KpiCostoHoraUsoDTO kpi = calcularKpiCostoHoraUso(filas);
        JFreeChart grafico = graficosService.crearGraficoCostoPorHora(filas);
        List<Object[]> filasTabla = filas.stream().map(CostoHoraUsoDTO::toTableRow).toList();
        return new ResultadoReporte<>(filas, kpi, grafico, null, COLUMNAS_REPORTE_2, filasTabla);
    }

    public KpiCostoHoraUsoDTO calcularKpiCostoHoraUso(List<CostoHoraUsoDTO> filas) {
        if (filas == null || filas.isEmpty()) {
            return new KpiCostoHoraUsoDTO(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "N/A");
        }
        BigDecimal horasTotales = BigDecimal.ZERO;
        BigDecimal costoTotal = BigDecimal.ZERO;
        CostoHoraUsoDTO mayor = filas.getFirst();

        for (CostoHoraUsoDTO fila : filas) {
            horasTotales = horasTotales.add(fila.getHorasUso());
            costoTotal = costoTotal.add(fila.getCostoAcumulado());
            if (fila.getCostoPorHora().compareTo(mayor.getCostoPorHora()) > 0) {
                mayor = fila;
            }
        }
        BigDecimal cphGlobal = horasTotales.compareTo(BigDecimal.ZERO) > 0
                ? costoTotal.divide(horasTotales, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        String etiquetaMayor = mayor.getCodigoInventario() + " ($" + mayor.getCostoPorHora() + "/h)";
        return new KpiCostoHoraUsoDTO(filas.size(), horasTotales, costoTotal, cphGlobal, etiquetaMayor);
    }

    // =========================================================================
    // REPORTE 3: Cumplimiento de Tiempos de Rutas (SLA Logístico / Puntualidad)
    // =========================================================================

    public ResultadoReporte<CumplimientoRutaDTO, KpiCumplimientoRutasDTO> generarReporte3CumplimientoRutas(
            Date fechaInicio, Date fechaFin, Integer idRuta, Integer idOperador)
            throws SQLException {
        List<CumplimientoRutaDTO> filas = reportesDAO.obtenerCumplimientoRutas(
                fechaInicio, fechaFin, idRuta, idOperador);
        KpiCumplimientoRutasDTO kpi = calcularKpiCumplimientoRutas(filas);
        JFreeChart graficoPastel = graficosService.crearGraficoCumplimientoRutas(filas);
        JFreeChart graficoDesviacion = graficosService.crearGraficoDesviacionPorProyecto(filas);
        List<Object[]> filasTabla = filas.stream().map(CumplimientoRutaDTO::toTableRow).toList();
        return new ResultadoReporte<>(filas, kpi, graficoPastel, graficoDesviacion,
                COLUMNAS_REPORTE_3, filasTabla);
    }

    public KpiCumplimientoRutasDTO calcularKpiCumplimientoRutas(List<CumplimientoRutaDTO> filas) {
        if (filas == null || filas.isEmpty()) {
            return new KpiCumplimientoRutasDTO(0, 0, 0, 0.0, 0.0);
        }
        int aTiempo = 0;
        int conRetraso = 0;
        double sumaDesviacion = 0.0;
        for (CumplimientoRutaDTO fila : filas) {
            sumaDesviacion += fila.getDesviacionHoras();
            if (fila.getDesviacionHoras() <= 0.0) {
                aTiempo++;
            } else {
                conRetraso++;
            }
        }
        int total = filas.size();
        double pctPuntualidad = redondear((aTiempo * 100.0) / total);
        double desviacionProm = redondear(sumaDesviacion / total);
        return new KpiCumplimientoRutasDTO(total, aTiempo, conRetraso, pctPuntualidad, desviacionProm);
    }

    // =========================================================================
    // REPORTE 5: Maquinarias: Datos Generales y Disponibilidad en Tiempo Real
    // =========================================================================

    public ResultadoReporte<SnapshotMaquinariaDTO, KpiDisponibilidadMaquinariaDTO> generarReporte5SnapshotMaquinaria(
            Boolean activo, String estadoOperativo, Integer idCategoria) throws SQLException {
        List<SnapshotMaquinariaDTO> filas = reportesDAO.obtenerSnapshotMaquinaria(
                activo, estadoOperativo, idCategoria);
        KpiDisponibilidadMaquinariaDTO kpi = calcularKpiDisponibilidadMaquinaria(filas);
        JFreeChart grafico = graficosService.crearGraficoSnapshotMaquinaria(filas);
        List<Object[]> filasTabla = filas.stream().map(SnapshotMaquinariaDTO::toTableRow).toList();
        return new ResultadoReporte<>(filas, kpi, grafico, null, COLUMNAS_REPORTE_5, filasTabla);
    }

    public KpiDisponibilidadMaquinariaDTO calcularKpiDisponibilidadMaquinaria(
            List<SnapshotMaquinariaDTO> filas) {
        int activas = 0;
        int disponibles = 0;
        int enRuta = 0;
        int mantenimiento = 0;
        int inactivas = 0;

        if (filas != null) {
            for (SnapshotMaquinariaDTO fila : filas) {
                if (!fila.isActivo()) {
                    inactivas++;
                } else {
                    activas++;
                    if ("DISPONIBLE".equalsIgnoreCase(fila.getEstadoOperativo())) {
                        disponibles++;
                    } else if ("EN_RUTA".equalsIgnoreCase(fila.getEstadoOperativo())) {
                        enRuta++;
                    } else if ("MANTENIMIENTO".equalsIgnoreCase(fila.getEstadoOperativo())) {
                        mantenimiento++;
                    }
                }
            }
        }
        double tasa = activas > 0 ? redondear((disponibles * 100.0) / activas) : 0.0;
        return new KpiDisponibilidadMaquinariaDTO(
                activas, disponibles, enRuta, mantenimiento, inactivas, tasa);
    }

    // =========================================================================
    // REPORTE 6: Certificaciones y Vigencia Operativa (Compliance)
    // =========================================================================

    public ResultadoReporte<CertificacionVigenciaDTO, KpiCertificacionesDTO> generarReporte6Certificaciones(
            String estadoVigencia, Integer idCategoria, String licenciaTipo) throws SQLException {
        List<CertificacionVigenciaDTO> filas = reportesDAO.obtenerEstadoCertificaciones(
                estadoVigencia, idCategoria, licenciaTipo);
        KpiCertificacionesDTO kpi = calcularKpiCertificaciones(filas);
        JFreeChart grafico = graficosService.crearGraficoCertificaciones(filas);
        List<Object[]> filasTabla = filas.stream().map(CertificacionVigenciaDTO::toTableRow).toList();
        return new ResultadoReporte<>(filas, kpi, grafico, null, COLUMNAS_REPORTE_6, filasTabla);
    }

    public KpiCertificacionesDTO calcularKpiCertificaciones(List<CertificacionVigenciaDTO> filas) {
        int vigentes = 0;
        int porVencer = 0;
        int vencidas = 0;
        if (filas != null) {
            for (CertificacionVigenciaDTO fila : filas) {
                if ("VENCIDA".equalsIgnoreCase(fila.getEstadoVigencia())) {
                    vencidas++;
                } else if ("POR_VENCER".equalsIgnoreCase(fila.getEstadoVigencia())) {
                    porVencer++;
                } else {
                    vigentes++;
                }
            }
        }
        int total = vigentes + porVencer + vencidas;
        double pctVigentes = total > 0 ? redondear((vigentes * 100.0) / total) : 0.0;
        return new KpiCertificacionesDTO(total, vigentes, porVencer, vencidas, pctVigentes);
    }

    // =========================================================================
    // REPORTE 7: Auditoría y Gestión de Usuarios
    // =========================================================================

    public ResultadoReporte<AuditoriaUsuarioDTO, KpiAuditoriaUsuariosDTO> generarReporte7AuditoriaUsuarios(
            Integer idRol, Integer idUsuario, Boolean activo,
            Date fechaInicio, Date fechaFin) throws SQLException {
        List<AuditoriaUsuarioDTO> filas = reportesDAO.obtenerAuditoriaUsuarios(
                idRol, idUsuario, activo, fechaInicio, fechaFin);
        KpiAuditoriaUsuariosDTO kpi = calcularKpiAuditoriaUsuarios(filas);
        JFreeChart grafico = graficosService.crearGraficoAuditoriaUsuarios(filas);
        List<Object[]> filasTabla = filas.stream().map(AuditoriaUsuarioDTO::toTableRow).toList();
        return new ResultadoReporte<>(filas, kpi, grafico, null, COLUMNAS_REPORTE_7, filasTabla);
    }

    public KpiAuditoriaUsuariosDTO calcularKpiAuditoriaUsuarios(List<AuditoriaUsuarioDTO> filas) {
        int despachos = 0;
        int talleres = 0;
        int cancelaciones = 0;
        BigDecimal montoTotal = BigDecimal.ZERO;
        if (filas != null) {
            for (AuditoriaUsuarioDTO fila : filas) {
                despachos += fila.getTotalAsignaciones();
                talleres += fila.getTotalMantenimientos();
                cancelaciones += fila.getAsignacionesCanceladas() + fila.getMantenimientosCancelados();
                montoTotal = montoTotal.add(fila.getMontoTallerAutorizado());
            }
        }
        int totalUsuarios = filas == null ? 0 : filas.size();
        return new KpiAuditoriaUsuariosDTO(
                totalUsuarios, despachos, talleres, cancelaciones, montoTotal);
    }

    // =========================================================================
    // REPORTE 8: Utilización y Desgaste de Flota
    // =========================================================================

    public ResultadoReporte<UtilizacionDesgasteDTO, KpiUtilizacionFlotaDTO> generarReporte8UtilizacionFlota(
            Integer idCategoria, Date fechaInicio, Date fechaFin) throws SQLException {
        List<UtilizacionDesgasteDTO> filas = reportesDAO.obtenerUtilizacionDesgasteFlota(
                idCategoria, fechaInicio, fechaFin);
        KpiUtilizacionFlotaDTO kpi = calcularKpiUtilizacionFlota(filas);
        JFreeChart grafico = graficosService.crearGraficoUtilizacionFlota(filas);
        List<Object[]> filasTabla = filas.stream().map(UtilizacionDesgasteDTO::toTableRow).toList();
        return new ResultadoReporte<>(filas, kpi, grafico, null, COLUMNAS_REPORTE_8, filasTabla);
    }

    public KpiUtilizacionFlotaDTO calcularKpiUtilizacionFlota(List<UtilizacionDesgasteDTO> filas) {
        if (filas == null || filas.isEmpty()) {
            return new KpiUtilizacionFlotaDTO(0, 0.0, 0.0, BigDecimal.ZERO);
        }
        double horasRuta = 0.0;
        double horasTaller = 0.0;
        BigDecimal sumaPromedios = BigDecimal.ZERO;
        for (UtilizacionDesgasteDTO fila : filas) {
            horasRuta += fila.getHorasEnRuta();
            horasTaller += fila.getHorasEnTaller();
            sumaPromedios = sumaPromedios.add(fila.getPromedioUsoDiario());
        }
        BigDecimal promedioGlobal = sumaPromedios.divide(
                BigDecimal.valueOf(filas.size()), 2, RoundingMode.HALF_UP);
        return new KpiUtilizacionFlotaDTO(
                filas.size(), redondear(horasRuta), redondear(horasTaller), promedioGlobal);
    }

    // =========================================================================
    // REPORTE 9: Historial de Rutas, Proyectos y Bitácora de Observaciones
    // =========================================================================

    public ResultadoReporte<HistorialRutaDTO, KpiHistorialRutasDTO> generarReporte9HistorialRutas(
            Integer idRuta, String estadoRuta, String estadoAsignacion, Integer idOperador)
            throws SQLException {
        List<HistorialRutaDTO> filas = reportesDAO.obtenerHistorialRutasYObservaciones(
                idRuta, estadoRuta, estadoAsignacion, idOperador);
        KpiHistorialRutasDTO kpi = calcularKpiHistorialRutas(filas);
        JFreeChart grafico = graficosService.crearGraficoHistorialRutasPorProyecto(filas);
        List<Object[]> filasTabla = filas.stream().map(HistorialRutaDTO::toTableRow).toList();
        return new ResultadoReporte<>(filas, kpi, grafico, null, COLUMNAS_REPORTE_9, filasTabla);
    }

    public KpiHistorialRutasDTO calcularKpiHistorialRutas(List<HistorialRutaDTO> filas) {
        int finalizados = 0;
        int enCurso = 0;
        int programados = 0;
        int cancelados = 0;
        if (filas != null) {
            for (HistorialRutaDTO fila : filas) {
                String est = fila.getEstadoAsignacion();
                if ("FINALIZADA".equalsIgnoreCase(est)) {
                    finalizados++;
                } else if ("CANCELADA".equalsIgnoreCase(est)) {
                    cancelados++;
                } else if ("EN_CURSO".equalsIgnoreCase(est)) {
                    enCurso++;
                } else {
                    programados++;
                }
            }
        }
        int total = finalizados + enCurso + programados + cancelados;
        double tasaEfectividad = total > 0 ? redondear((finalizados * 100.0) / total) : 0.0;
        double tasaCancelacion = total > 0 ? redondear((cancelados * 100.0) / total) : 0.0;
        return new KpiHistorialRutasDTO(
                total, finalizados, enCurso, programados, cancelados,
                tasaEfectividad, tasaCancelacion);
    }

    // =========================================================================
    // REPORTE 10: Productividad y Carga Laboral de Operadores por Turno
    // =========================================================================

    public ResultadoReporte<ProductividadOperadorDTO, KpiProductividadTurnoDTO> generarReporte10ProductividadTurno(
            String turno, String estadoOperativo, Date fechaInicio, Date fechaFin)
            throws SQLException {
        List<ProductividadOperadorDTO> filas = reportesDAO.obtenerProductividadOperadores(
                turno, estadoOperativo, fechaInicio, fechaFin);
        KpiProductividadTurnoDTO kpi = calcularKpiProductividadTurno(filas);
        JFreeChart grafico = graficosService.crearGraficoProductividadOperadores(filas);
        List<Object[]> filasTabla = filas.stream().map(ProductividadOperadorDTO::toTableRow).toList();
        return new ResultadoReporte<>(filas, kpi, grafico, null, COLUMNAS_REPORTE_10, filasTabla);
    }

    public KpiProductividadTurnoDTO calcularKpiProductividadTurno(
            List<ProductividadOperadorDTO> filas) {
        int finalizados = 0;
        int cancelados = 0;
        int totalViajes = 0;
        double horasConducidas = 0.0;
        if (filas != null) {
            for (ProductividadOperadorDTO fila : filas) {
                finalizados += fila.getViajesFinalizados();
                cancelados += fila.getViajesCancelados();
                totalViajes += fila.getTotalViajes();
                horasConducidas += fila.getHorasConducidas();
            }
        }
        int totalOperadores = filas == null ? 0 : filas.size();
        double cumplimiento = totalViajes > 0
                ? redondear((finalizados * 100.0) / totalViajes) : 0.0;
        return new KpiProductividadTurnoDTO(
                totalOperadores, finalizados, cancelados,
                redondear(horasConducidas), cumplimiento);
    }

    // =========================================================================
    // Métodos puente para poblar Tablas y Contenedores en el Frontend
    // =========================================================================

    /**
     * Construye un {@link DefaultTableModel} de solo lectura listo para asignarse
     * a cualquier {@code JTable} o {@code com.titanops.vista.componentes.Tabla}.
     */
    public DefaultTableModel construirModeloTabla(String[] columnas, List<Object[]> filas) {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        if (filas != null) {
            for (Object[] fila : filas) {
                modelo.addRow(fila);
            }
        }
        return modelo;
    }

    /**
     * Incrusta un {@link JFreeChart} dentro de cualquier {@link JPanel} contenedor
     * de la vista configurando automáticamente {@link BorderLayout}.
     */
    public void incrustarGraficoEnPanel(JPanel contenedor, JFreeChart grafico) {
        if (contenedor == null || grafico == null) {
            return;
        }
        if (!(contenedor.getLayout() instanceof BorderLayout)) {
            contenedor.setLayout(new BorderLayout());
        }
        JPanel chartPanel = graficosService.construirPanelGrafico(
                grafico, contenedor.getWidth(), contenedor.getHeight());
        contenedor.removeAll();
        contenedor.add(chartPanel, BorderLayout.CENTER);
        contenedor.revalidate();
        contenedor.repaint();
    }

    /**
     * Convierte la selección de texto ("TODOS", "ACTIVOS", "INACTIVOS") a {@link Boolean}.
     */
    public static Boolean interpretarFiltroActivo(String seleccion) {
        if (seleccion == null) {
            return null;
        }
        String norm = seleccion.trim().toUpperCase();
        if ("ACTIVOS".equals(norm) || "ACTIVO".equals(norm) || "ACTIVA".equals(norm) || "TRUE".equals(norm)) {
            return Boolean.TRUE;
        }
        if ("INACTIVOS".equals(norm) || "INACTIVO".equals(norm) || "INACTIVA".equals(norm) || "FALSE".equals(norm)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /**
     * Estructura inmutable devuelta al frontend por cada uno de los 9 reportes.
     */
    public record ResultadoReporte<D, K>(
            List<D> datos,
            K kpi,
            JFreeChart graficoPrincipal,
            JFreeChart graficoSecundario,
            String[] columnasTabla,
            List<Object[]> filasTabla) {
    }
}

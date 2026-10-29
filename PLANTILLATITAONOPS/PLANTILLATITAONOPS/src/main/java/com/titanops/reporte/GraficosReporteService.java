package com.titanops.reporte;

import com.titanops.reporte.ReportesDTO.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Servicio generador de gráficos JFreeChart para los 9 reportes operativos de TitanOps.
 * Totalmente desacoplado de las vistas Swing (devuelve objetos {@link JFreeChart} o
 * contenedores {@link JPanel} listos para incrustar en cualquier contenedor con BorderLayout).
 */
public class GraficosReporteService {

    private static final Color COLOR_AZUL_CORPORATIVO = new Color(37, 99, 235);
    private static final Color COLOR_ROJO_ALERTA = new Color(231, 76, 60);
    private static final Color COLOR_VERDE_EXITO = new Color(46, 204, 113);
    private static final Color COLOR_AMBAR_ADVERTENCIA = new Color(243, 156, 18);
    private static final Color COLOR_FONDO_PLOT = new Color(248, 250, 252);
    private static final Color COLOR_GRILLA = new Color(203, 213, 225);
    private static final Color COLOR_TEXTO = new Color(30, 41, 59);

    // =========================================================================
    // REPORTE 1: Ratio de Mantenimiento Preventivo vs. Correctivo
    // =========================================================================

    /**
     * Genera el gráfico de barras apiladas del Reporte 1: Preventivo vs Correctivo por unidad.
     */
    public JFreeChart crearGraficoRatioMantenimiento(List<RatioMantenimientoDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (datos != null) {
            for (RatioMantenimientoDTO fila : datos) {
                dataset.addValue(
                        fila.getTotalIntervenciones(),
                        fila.getTipoMantenimiento(),
                        fila.getCodigoInventario());
            }
        }

        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Ratio de Mantenimiento: Preventivo vs Correctivo",
                "Maquinaria (Código)",
                "Cantidad de Intervenciones",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        chart.getCategoryPlot().getRangeAxis().setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
        return chart;
    }

    /**
     * Variante circular del Reporte 1: Proporción global Preventivo vs Correctivo.
     */
    public JFreeChart crearGraficoPastelRatioMantenimiento(List<RatioMantenimientoDTO> datos) {
        Map<String, Integer> conteos = new LinkedHashMap<>();
        if (datos != null) {
            for (RatioMantenimientoDTO fila : datos) {
                conteos.merge(
                        fila.getTipoMantenimiento(),
                        fila.getTotalIntervenciones(),
                        Integer::sum);
            }
        }
        return crearGraficoPastelGenerico(
                "Proporción de Mantenimientos: Preventivo vs Correctivo", conteos);
    }

    // =========================================================================
    // REPORTE 2: Costo de Mantenimiento por Hora de Uso (Índice TCO Operativo)
    // =========================================================================

    /**
     * Genera el gráfico de barras horizontales del Reporte 2: Costo por Hora de Uso (CPH).
     */
    public JFreeChart crearGraficoCostoPorHora(List<CostoHoraUsoDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (datos != null) {
            for (CostoHoraUsoDTO fila : datos) {
                dataset.addValue(
                        fila.getCostoPorHora(),
                        "Costo / Hora ($)",
                        fila.getCodigoInventario());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Costo de Mantenimiento por Hora de Uso (Índice TCO)",
                "Maquinaria",
                "USD ($) por Hora",
                dataset,
                PlotOrientation.HORIZONTAL,
                false, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        return chart;
    }

    // =========================================================================
    // REPORTE 3: Cumplimiento de Tiempos de Rutas (SLA Logístico / Puntualidad)
    // =========================================================================

    /**
     * Genera gráfico circular de puntualidad a partir de la lista detallada del Reporte 3.
     */
    public JFreeChart crearGraficoCumplimientoRutas(List<CumplimientoRutaDTO> datos) {
        Map<String, Integer> resumen = new LinkedHashMap<>();
        if (datos != null) {
            for (CumplimientoRutaDTO fila : datos) {
                resumen.merge(fila.getClasificacion(), 1, Integer::sum);
            }
        }
        return crearGraficoPastelGenerico(
                "Cumplimiento de SLA Logístico (A Tiempo vs Con Retraso)", resumen);
    }

    /**
     * Genera gráfico de barras con la desviación promedio en horas por proyecto (Reporte 3).
     */
    public JFreeChart crearGraficoDesviacionPorProyecto(List<CumplimientoRutaDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (datos != null) {
            Map<String, double[]> acumulados = new LinkedHashMap<>();
            for (CumplimientoRutaDTO fila : datos) {
                double[] par = acumulados.computeIfAbsent(
                        fila.getNombreProyecto(), k -> new double[2]);
                par[0] += fila.getDesviacionHoras();
                par[1] += 1.0;
            }
            for (Map.Entry<String, double[]> entry : acumulados.entrySet()) {
                double promedio = entry.getValue()[1] > 0
                        ? entry.getValue()[0] / entry.getValue()[1] : 0.0;
                dataset.addValue(
                        Math.round(promedio * 100.0) / 100.0,
                        "Desviación Promedio (Horas)",
                        entry.getKey());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Desviación Promedio de Retorno por Proyecto (Horas)",
                "Proyecto / Ruta",
                "Horas de Desviación (Δt)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        chart.getCategoryPlot().getRangeAxis().setLowerMargin(0.15);
        return chart;
    }

    // =========================================================================
    // REPORTE 5: Maquinarias: Datos Generales y Disponibilidad en Tiempo Real
    // =========================================================================

    /**
     * Genera un gráfico de anillo (RingChart) mostrando la distribución operativa de maquinaria.
     */
    public JFreeChart crearGraficoAnilloEstadoMaquinaria(Map<String, Integer> datos) {
        DefaultPieDataset<String> dataset = construirPieDataset(datos);
        JFreeChart chart = ChartFactory.createRingChart(
                "Disponibilidad de Maquinaria en Tiempo Real",
                dataset,
                true, true, false
        );
        estilizarPiePlot(chart);
        if (chart.getPlot() instanceof RingPlot ringPlot) {
            ringPlot.setSectionDepth(0.35);
            ringPlot.setSeparatorsVisible(true);
            ringPlot.setSeparatorPaint(Color.WHITE);
        }
        return chart;
    }

    /**
     * Genera el gráfico de disponibilidad directamente desde la lista de SnapshotMaquinariaDTO.
     */
    public JFreeChart crearGraficoSnapshotMaquinaria(List<SnapshotMaquinariaDTO> datos) {
        Map<String, Integer> distribucion = new LinkedHashMap<>();
        if (datos != null) {
            for (SnapshotMaquinariaDTO fila : datos) {
                String clave = fila.isActivo() ? fila.getEstadoOperativo() : "INACTIVA";
                distribucion.merge(clave, 1, Integer::sum);
            }
        }
        return crearGraficoAnilloEstadoMaquinaria(distribucion);
    }

    /**
     * Genera un gráfico circular genérico (Reporte 3 o Reporte 5).
     */
    public JFreeChart crearGraficoPastelGenerico(String titulo, Map<String, Integer> datos) {
        DefaultPieDataset<String> dataset = construirPieDataset(datos);
        JFreeChart chart = ChartFactory.createPieChart(
                titulo,
                dataset,
                true, true, false
        );
        estilizarPiePlot(chart);
        return chart;
    }

    // =========================================================================
    // REPORTE 6: Certificaciones y Vigencia Operativa (Compliance)
    // =========================================================================

    /**
     * Genera el gráfico de barras apiladas del Reporte 6: Semáforo de Certificaciones por Categoría.
     */
    public JFreeChart crearGraficoCertificaciones(List<CertificacionVigenciaDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (datos != null) {
            for (CertificacionVigenciaDTO fila : datos) {
                incrementarValorCategoria(
                        dataset,
                        1.0,
                        fila.getEstadoVigencia(),
                        fila.getCategoriaAutorizada());
            }
        }

        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Vigencia de Certificaciones por Categoría de Maquinaria",
                "Categoría de Maquinaria",
                "Operadores Acreditados",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        chart.getCategoryPlot().getRangeAxis().setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
        return chart;
    }

    // =========================================================================
    // REPORTE 7: Auditoría y Gestión de Usuarios
    // =========================================================================

    /**
     * Genera el gráfico de barras agrupadas del Reporte 7: Asignaciones vs Mantenimientos por Usuario.
     */
    public JFreeChart crearGraficoAuditoriaUsuarios(List<AuditoriaUsuarioDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (datos != null) {
            for (AuditoriaUsuarioDTO fila : datos) {
                String etiquetaUsuario = fila.getUsername().isEmpty()
                        ? fila.getNombreCompleto()
                        : fila.getUsername();
                dataset.addValue(fila.getTotalAsignaciones(), "Despachos Registrados", etiquetaUsuario);
                dataset.addValue(fila.getTotalMantenimientos(), "Órdenes de Taller", etiquetaUsuario);
                int cancelaciones = fila.getAsignacionesCanceladas() + fila.getMantenimientosCancelados();
                dataset.addValue(cancelaciones, "Cancelaciones", etiquetaUsuario);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Volumen de Gestión Administrativa por Usuario",
                "Usuario",
                "Operaciones Registradas",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        chart.getCategoryPlot().getRangeAxis().setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
        return chart;
    }

    // =========================================================================
    // REPORTE 8: Utilización y Desgaste de Flota
    // =========================================================================

    /**
     * Genera el gráfico de barras dobles del Reporte 8: Horas en Ruta vs Horas en Taller por unidad.
     */
    public JFreeChart crearGraficoUtilizacionFlota(List<UtilizacionDesgasteDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (datos != null) {
            for (UtilizacionDesgasteDTO fila : datos) {
                dataset.addValue(fila.getHorasEnRuta(), "Horas Productivas en Ruta", fila.getCodigoInventario());
                dataset.addValue(fila.getHorasEnTaller(), "Horas Muertas en Taller (Downtime)", fila.getCodigoInventario());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Utilización Operativa vs Tiempo en Taller (Downtime)",
                "Maquinaria (Código)",
                "Horas Acumuladas",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        return chart;
    }

    // =========================================================================
    // REPORTE 9: Historial de Rutas, Proyectos y Bitácora de Observaciones
    // =========================================================================

    /**
     * Genera el gráfico de barras apiladas del Reporte 9: Estados de viajes por Proyecto.
     */
    public JFreeChart crearGraficoHistorialRutasPorProyecto(List<HistorialRutaDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (datos != null) {
            for (HistorialRutaDTO fila : datos) {
                incrementarValorCategoria(
                        dataset,
                        1.0,
                        fila.getEstadoAsignacion(),
                        fila.getNombreProyecto());
            }
        }

        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Efectividad y Estados de Viajes por Proyecto",
                "Proyecto",
                "Cantidad de Viajes",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        chart.getCategoryPlot().getRangeAxis().setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
        return chart;
    }

    // =========================================================================
    // REPORTE 10: Productividad y Carga Laboral de Operadores por Turno
    // =========================================================================

    /**
     * Genera el gráfico del Reporte 10 a partir del resumen agrupado por turno y estado.
     */
    public JFreeChart crearGraficoProductividadTurno(List<Object[]> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (datos != null) {
            for (Object[] fila : datos) {
                String turno = String.valueOf(fila[0]);
                String estado = String.valueOf(fila[1]);
                Number total = (Number) fila[2];
                dataset.addValue(total, estado, turno);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Rendimiento de Viajes por Turno Operativo",
                "Turno",
                "Cantidad de Asignaciones",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        chart.getCategoryPlot().getRangeAxis().setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());
        return chart;
    }

    /**
     * Genera el gráfico del Reporte 10 a partir de la lista de ProductividadOperadorDTO
     * comparando horas conducidas y viajes finalizados por turno.
     */
    public JFreeChart crearGraficoProductividadOperadores(List<ProductividadOperadorDTO> datos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (datos != null) {
            Map<String, double[]> porTurno = new LinkedHashMap<>();
            for (ProductividadOperadorDTO fila : datos) {
                double[] acum = porTurno.computeIfAbsent(fila.getTurno(), k -> new double[3]);
                acum[0] += fila.getViajesFinalizados();
                acum[1] += fila.getViajesCancelados();
                acum[2] += fila.getHorasConducidas();
            }
            for (Map.Entry<String, double[]> entry : porTurno.entrySet()) {
                String turno = entry.getKey();
                double[] valores = entry.getValue();
                dataset.addValue(valores[0], "Viajes Finalizados", turno);
                dataset.addValue(valores[1], "Viajes Cancelados", turno);
                dataset.addValue(Math.round(valores[2] * 100.0) / 100.0, "Horas Conducidas", turno);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Productividad y Carga Laboral por Turno",
                "Turno Operativo",
                "Volumen / Horas",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        aplicarEstiloCorporativoBarras(chart);
        return chart;
    }

    // =========================================================================
    // Utilidades de Integración Swing y Exportación
    // =========================================================================

    /**
     * Método puente para el Frontend: Convierte cualquier {@link JFreeChart} en un {@link JPanel}
     * listo para insertarse en un contenedor Swing con {@code BorderLayout.CENTER}.
     */
    public JPanel construirPanelGrafico(JFreeChart chart, int anchoPreferido, int altoPreferido) {
        int ancho = anchoPreferido > 0 ? anchoPreferido : 640;
        int alto = altoPreferido > 0 ? altoPreferido : 360;
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(ancho, alto));
        chartPanel.setMinimumDrawWidth(200);
        chartPanel.setMinimumDrawHeight(150);
        chartPanel.setMaximumDrawWidth(1920);
        chartPanel.setMaximumDrawHeight(1080);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setBackground(Color.WHITE);
        return chartPanel;
    }

    /**
     * Exporta cualquier gráfico a un archivo PNG sin requerir interfaz gráfica.
     */
    public void exportarGraficoAPng(JFreeChart chart, String rutaArchivo, int ancho, int alto)
            throws IOException {
        exportarGraficoAPng(chart, Path.of(rutaArchivo), ancho, alto);
    }

    public void exportarGraficoAPng(JFreeChart chart, Path destino, int ancho, int alto)
            throws IOException {
        File archivo = destino.toFile();
        ChartUtils.saveChartAsPNG(
                archivo,
                chart,
                ancho > 0 ? ancho : 800,
                alto > 0 ? alto : 500);
    }

    private DefaultPieDataset<String> construirPieDataset(Map<String, Integer> datos) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        if (datos != null) {
            for (Map.Entry<String, Integer> entry : datos.entrySet()) {
                dataset.setValue(
                        entry.getKey() + " (" + entry.getValue() + ")",
                        entry.getValue());
            }
        }
        return dataset;
    }

    private void incrementarValorCategoria(
            DefaultCategoryDataset dataset, double incremento, String serie, String categoria) {
        double actual = 0.0;
        if (dataset.getRowIndex(serie) >= 0 && dataset.getColumnIndex(categoria) >= 0) {
            Number existente = dataset.getValue(serie, categoria);
            if (existente != null) {
                actual = existente.doubleValue();
            }
        }
        dataset.setValue(actual + incremento, serie, categoria);
    }

    @SuppressWarnings("unchecked")
    private void estilizarPiePlot(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        if (chart.getTitle() != null) {
            chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 15));
            chart.getTitle().setPaint(COLOR_TEXTO);
        }
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setLabelBackgroundPaint(new Color(239, 246, 255));
        plot.setLabelOutlinePaint(new Color(125, 211, 252));
        plot.setLabelShadowPaint(null);
        plot.setNoDataMessage("Sin datos para los filtros seleccionados");
    }

    private void aplicarEstiloCorporativoBarras(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        if (chart.getTitle() != null) {
            chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 15));
            chart.getTitle().setPaint(COLOR_TEXTO);
        }
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(COLOR_FONDO_PLOT);
        plot.setRangeGridlinePaint(COLOR_GRILLA);
        plot.setOutlineVisible(false);
        plot.setNoDataMessage("Sin datos para los filtros seleccionados");
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 12));
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.getRangeAxis().setUpperMargin(0.18);

        if (plot.getRenderer() instanceof BarRenderer renderer) {
            renderer.setBarPainter(new StandardBarPainter());
            renderer.setShadowVisible(false);
            renderer.setMaximumBarWidth(0.18);
            renderer.setItemMargin(0.10);
            renderer.setDefaultItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator());
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 11));
            renderer.setDefaultItemLabelPaint(COLOR_TEXTO);
            renderer.setSeriesPaint(0, COLOR_AZUL_CORPORATIVO);
            renderer.setSeriesPaint(1, COLOR_ROJO_ALERTA);
            renderer.setSeriesPaint(2, COLOR_VERDE_EXITO);
            renderer.setSeriesPaint(3, COLOR_AMBAR_ADVERTENCIA);
        }
    }
}

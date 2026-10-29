package com.titanops.reporte;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.controlador.ReportesOperativosController;
import com.titanops.dao.ReportesDAO;
import com.titanops.reporte.ReportesDTO.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.JFreeChart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReportesOperativosBackendTest {

    @TempDir
    Path tempDir;

    @Test
    void generaLosNueveReportesCalculaKpisYConstruyeGraficos() throws Exception {
        ReportesDAODoble dao = new ReportesDAODoble();
        GraficosReporteService graficosService = new GraficosReporteService();
        ReportesOperativosController controller =
                new ReportesOperativosController(dao, graficosService);

        // Reporte 1: Ratio Preventivo vs Correctivo
        var rep1 = controller.generarReporte1RatioMantenimiento(
                Date.valueOf("2026-01-01"), Date.valueOf("2026-12-31"), null, null);
        assertEquals(2, rep1.datos().size());
        assertEquals(10, rep1.kpi().totalMantenimientos());
        assertEquals(6, rep1.kpi().conteoPreventivos());
        assertEquals(4, rep1.kpi().conteoCorrectivos());
        assertEquals(40.0, rep1.kpi().porcentajeIncidenciasCorrectivas());
        assertEquals(new BigDecimal("1000.00"), rep1.kpi().costoTotal());
        assertEquals(70.0, rep1.kpi().porcentajeGastoCorrectivo());
        assertNotNull(rep1.graficoPrincipal());
        assertNotNull(rep1.graficoSecundario());

        // Reporte 2: Costo por Hora de Uso
        var rep2 = controller.generarReporte2CostoHoraUso(null, true);
        assertEquals(1, rep2.datos().size());
        assertEquals(new BigDecimal("12.50"), rep2.kpi().costoPorHoraGlobal());
        assertNotNull(rep2.graficoPrincipal());

        // Reporte 3: Cumplimiento de Tiempos de Ruta (SLA)
        var rep3 = controller.generarReporte3CumplimientoRutas(null, null, null, null);
        assertEquals(2, rep3.datos().size());
        assertEquals(1, rep3.kpi().viajesATiempo());
        assertEquals(1, rep3.kpi().viajesConRetraso());
        assertEquals(50.0, rep3.kpi().porcentajePuntualidadGlobal());
        assertNotNull(rep3.graficoPrincipal());
        assertNotNull(rep3.graficoSecundario());

        // Reporte 5: Snapshot Maestro de Maquinarias
        var rep5 = controller.generarReporte5SnapshotMaquinaria(true, "TODOS", null);
        assertEquals(2, rep5.datos().size());
        assertEquals(50.0, rep5.kpi().tasaDisponibilidad());
        assertNotNull(rep5.graficoPrincipal());

        // Reporte 6: Certificaciones y Vigencia Operativa
        var rep6 = controller.generarReporte6Certificaciones("TODOS", null, null);
        assertEquals(3, rep6.datos().size());
        assertEquals(1, rep6.kpi().vigentes());
        assertEquals(1, rep6.kpi().porVencer());
        assertEquals(1, rep6.kpi().vencidas());
        assertNotNull(rep6.graficoPrincipal());

        // Reporte 7: Auditoría y Gestión de Usuarios
        var rep7 = controller.generarReporte7AuditoriaUsuarios(null, null, true, null, null);
        assertEquals(1, rep7.datos().size());
        assertEquals(8, rep7.kpi().totalDespachosRegistrados());
        assertEquals(3, rep7.kpi().totalMantenimientosRegistrados());
        assertEquals(2, rep7.kpi().totalCancelaciones());
        assertNotNull(rep7.graficoPrincipal());

        // Reporte 8: Utilización y Desgaste de Flota
        var rep8 = controller.generarReporte8UtilizacionFlota(null, null, null);
        assertEquals(1, rep8.datos().size());
        assertEquals(120.0, rep8.kpi().totalHorasEnRuta());
        assertEquals(15.0, rep8.kpi().totalHorasEnTaller());
        assertNotNull(rep8.graficoPrincipal());

        // Reporte 9: Historial de Rutas y Observaciones
        var rep9 = controller.generarReporte9HistorialRutas(null, null, null, null);
        assertEquals(2, rep9.datos().size());
        assertEquals(50.0, rep9.kpi().tasaEfectividad());
        assertEquals(50.0, rep9.kpi().tasaCancelacion());
        assertNotNull(rep9.graficoPrincipal());

        // Reporte 10: Productividad de Operadores por Turno
        var rep10 = controller.generarReporte10ProductividadTurno(null, null, null, null);
        assertEquals(1, rep10.datos().size());
        assertEquals(5, rep10.kpi().totalViajesFinalizados());
        assertEquals(42.5, rep10.kpi().totalHorasConducidas());
        assertNotNull(rep10.graficoPrincipal());

        // Prueba de incrustación en JPanel, DefaultTableModel y exportación a PNG
        JPanel contenedor = new JPanel();
        controller.incrustarGraficoEnPanel(contenedor, rep1.graficoPrincipal());
        assertEquals(1, contenedor.getComponentCount());

        DefaultTableModel modelo = controller.construirModeloTabla(
                rep1.columnasTabla(), rep1.filasTabla());
        assertEquals(2, modelo.getRowCount());
        assertEquals(ReportesOperativosController.COLUMNAS_REPORTE_1.length,
                modelo.getColumnCount());

        Path png = tempDir.resolve("grafico_reporte1.png");
        graficosService.exportarGraficoAPng(rep1.graficoPrincipal(), png, 640, 360);
        assertTrue(Files.exists(png));
        assertTrue(Files.size(png) > 0);

        JFreeChart chartTurno = graficosService.crearGraficoProductividadTurno(
                dao.obtenerProductividadPorTurno(null, null));
        assertNotNull(chartTurno);
    }

    private static class ReportesDAODoble extends ReportesDAO {
        @Override
        public List<RatioMantenimientoDTO> obtenerRatioMantenimiento(
                Date fechaInicio, Date fechaFin, Integer idMaquinaria, Integer idCategoria) {
            return List.of(
                    new RatioMantenimientoDTO(1, "EQ-01", "CAT 320", "Excavadora",
                            "PREVENTIVO", 6, new BigDecimal("300.00"), 12.5),
                    new RatioMantenimientoDTO(1, "EQ-01", "CAT 320", "Excavadora",
                            "CORRECTIVO", 4, new BigDecimal("700.00"), 28.0)
            );
        }

        @Override
        public List<CostoHoraUsoDTO> obtenerCostoPorHoraUso(Integer idCategoria, Boolean activo) {
            return List.of(
                    new CostoHoraUsoDTO(1, "EQ-01", "CAT 320", "Excavadora",
                            new BigDecimal("80.00"), 4, new BigDecimal("1000.00"),
                            new BigDecimal("12.50"), new BigDecimal("250.00"), true)
            );
        }

        @Override
        public List<CumplimientoRutaDTO> obtenerCumplimientoRutas(
                Date fechaInicio, Date fechaFin, Integer idRuta, Integer idOperador) {
            return List.of(
                    new CumplimientoRutaDTO(10, "Proyecto Norte", "Base -> Tramo 1", "EQ-01",
                            "Juan Pérez", Timestamp.valueOf("2026-10-01 08:00:00"),
                            Timestamp.valueOf("2026-10-01 16:00:00"),
                            Timestamp.valueOf("2026-10-01 15:00:00"),
                            -1.0, "A Tiempo / Adelantado"),
                    new CumplimientoRutaDTO(11, "Proyecto Sur", "Base -> Tramo 2", "EQ-02",
                            "Carlos Gómez", Timestamp.valueOf("2026-10-02 08:00:00"),
                            Timestamp.valueOf("2026-10-02 16:00:00"),
                            Timestamp.valueOf("2026-10-02 19:00:00"),
                            3.0, "Con Retraso")
            );
        }

        @Override
        public List<SnapshotMaquinariaDTO> obtenerSnapshotMaquinaria(
                Boolean activo, String estadoOperativo, Integer idCategoria) {
            return List.of(
                    new SnapshotMaquinariaDTO(1, "EQ-01", "CAT", "320", "Excavadora",
                            new BigDecimal("20.00"), new BigDecimal("100.00"), "DISPONIBLE",
                            true, Timestamp.valueOf("2026-01-10 08:00:00"), 5, 2,
                            Timestamp.valueOf("2026-10-01 08:00:00")),
                    new SnapshotMaquinariaDTO(2, "EQ-02", "KOMATSU", "PC200", "Excavadora",
                            new BigDecimal("22.00"), new BigDecimal("140.00"), "EN_RUTA",
                            true, Timestamp.valueOf("2026-01-15 08:00:00"), 7, 1,
                            Timestamp.valueOf("2026-10-05 08:00:00"))
            );
        }

        @Override
        public List<CertificacionVigenciaDTO> obtenerEstadoCertificaciones(
                String estadoVigencia, Integer idCategoria, String licenciaTipo) {
            return List.of(
                    new CertificacionVigenciaDTO("Juan Pérez", "01234567-8", "PESADA",
                            "DISPONIBLE", "Excavadora", "ACR-001",
                            Date.valueOf("2025-01-01"), Date.valueOf("2026-01-01"),
                            -10, "VENCIDA"),
                    new CertificacionVigenciaDTO("Carlos Gómez", "11234567-8", "PESADA",
                            "DISPONIBLE", "Excavadora", "ACR-002",
                            Date.valueOf("2026-01-01"), Date.valueOf("2026-11-10"),
                            12, "POR_VENCER"),
                    new CertificacionVigenciaDTO("Mario López", "21234567-8", "PESADA",
                            "DISPONIBLE", "Grúa", "ACR-003",
                            Date.valueOf("2026-01-01"), Date.valueOf("2027-06-01"),
                            215, "VIGENTE")
            );
        }

        @Override
        public List<AuditoriaUsuarioDTO> obtenerAuditoriaUsuarios(
                Integer idRol, Integer idUsuario, Boolean activo,
                Date fechaInicio, Date fechaFin) {
            return List.of(
                    new AuditoriaUsuarioDTO(1, "Ana Administradora", "aadmin", "ADMINISTRADOR",
                            true, Timestamp.valueOf("2026-01-01 08:00:00"),
                            8, 1, 3, 1, new BigDecimal("1450.00"))
            );
        }

        @Override
        public List<UtilizacionDesgasteDTO> obtenerUtilizacionDesgasteFlota(
                Integer idCategoria, Date fechaInicio, Date fechaFin) {
            return List.of(
                    new UtilizacionDesgasteDTO(1, "EQ-01", "CAT 320", "Excavadora",
                            Timestamp.valueOf("2026-01-01 08:00:00"), 100,
                            new BigDecimal("450.00"), new BigDecimal("4.50"),
                            120.0, 15.0)
            );
        }

        @Override
        public List<HistorialRutaDTO> obtenerHistorialRutasYObservaciones(
                Integer idRuta, String estadoRuta, String estadoAsignacion, Integer idOperador) {
            return List.of(
                    new HistorialRutaDTO(1, "Proyecto Norte", "Base -> Tramo 1", "FINALIZADA",
                            "EQ-01", "Juan Pérez", "DIURNO", "7777-8888", "FINALIZADA",
                            Timestamp.valueOf("2026-10-01 08:00:00"),
                            Timestamp.valueOf("2026-10-01 16:00:00"), "Entrega sin novedades"),
                    new HistorialRutaDTO(2, "Proyecto Norte", "Base -> Tramo 1", "FINALIZADA",
                            "EQ-02", "Carlos Gómez", "NOCTURNO", "7777-9999", "CANCELADA",
                            Timestamp.valueOf("2026-10-02 08:00:00"),
                            Timestamp.valueOf("2026-10-02 10:00:00"), "Lluvia torrencial en ruta")
            );
        }

        @Override
        public List<ProductividadOperadorDTO> obtenerProductividadOperadores(
                String turno, String estadoOperativo, Date fechaInicio, Date fechaFin) {
            return List.of(
                    new ProductividadOperadorDTO(1, "Juan Pérez", "01234567-8", "PESADA",
                            "DIURNO", "DISPONIBLE", 3, 6, 5, 1, 0, 42.5)
            );
        }

        @Override
        public List<Object[]> obtenerProductividadPorTurno(Date fechaInicio, Date fechaFin) {
            return List.of(
                    new Object[]{"DIURNO", "FINALIZADA", 5},
                    new Object[]{"DIURNO", "CANCELADA", 1}
            );
        }
    }
}

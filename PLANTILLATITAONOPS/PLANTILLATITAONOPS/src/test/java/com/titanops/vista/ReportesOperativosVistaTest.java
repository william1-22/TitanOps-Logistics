package com.titanops.vista;

import com.titanops.controlador.ReportesOperativosController;
import com.titanops.dao.ReportesDAO;
import com.titanops.reporte.GraficosReporteService;
import com.titanops.reporte.ReportesDTO.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.nio.file.*;
import javax.swing.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ReportesOperativosVistaTest {
    static { System.setProperty("java.awt.headless", "true"); }
    private static final String[] VISTAS = {"ReporteMantenimiento", "ReporteCostoHora", "ReporteCumplimiento", "ReporteDisponibilidad", "ReporteCertificaciones", "ReporteAuditoria", "ReporteUtilizacion", "ReporteHistorialRutas", "ReporteProductividad"};
    private static <T> T campo(Object vista, String nombre, Class<T> tipo) throws Exception {
        var f = vista.getClass().getDeclaredField(nombre); f.setAccessible(true); return tipo.cast(f.get(vista));
    }
    private static EnlaceReporteOperativo enlazar(JPanel vista, ReportesOperativosController controller) throws Exception {
        var m = vista.getClass().getDeclaredMethod("enlazar", ReportesOperativosController.class);
        return (EnlaceReporteOperativo) m.invoke(vista, controller);
    }
    @TestFactory Stream<DynamicTest> generaNueveReportesEnSegundoPlanoYRenderiza() {
        return Stream.of(VISTAS).map(nombre -> DynamicTest.dynamicTest(nombre, () -> {
            JPanel[] panel = new JPanel[1];
            EnlaceReporteOperativo[] enlaceRef = new EnlaceReporteOperativo[1];
            SwingUtilities.invokeAndWait(() -> {
                try {
                    panel[0] = (JPanel) Class.forName("com.titanops.vista."+nombre).getConstructor().newInstance();
                    var enlace = enlazar(panel[0], new ReportesOperativosController(new ReportesDAODoble(), new GraficosReporteService()));
                    enlaceRef[0] = enlace;
                    enlace.aplicarCatalogos(Map.of());
                    assertEquals("EXPORTAR REPORTE PDF", campo(panel[0], "btnExportar", JButton.class).getText());
                    campo(panel[0], "btnGenerar", JButton.class).doClick();
                    assertFalse(campo(panel[0], "btnGenerar", JButton.class).isEnabled());
                } catch(Exception e) { throw new RuntimeException(e); }
            });
            long limite = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(10);
            boolean[] listo = {false};
            while (!listo[0] && System.nanoTime() < limite) {
                SwingUtilities.invokeAndWait(() -> {
                    try { listo[0] = campo(panel[0], "btnGenerar", JButton.class).isEnabled(); }
                    catch(Exception e) { throw new RuntimeException(e); }
                });
                if (!listo[0]) Thread.sleep(20);
            }
            assertTrue(listo[0], "El SwingWorker debe finalizar");
            SwingUtilities.invokeAndWait(() -> {
                try {
                    JTable tabla = campo(panel[0], "tablaDatos", JTable.class);
                    assertTrue(tabla.getRowCount() > 0);
                    assertFalse(tabla.isCellEditable(0,0));
                    assertNotNull(tabla.getRowSorter());
                    assertInstanceOf(org.jfree.chart.ChartPanel.class, campo(panel[0], "panelGrafico1", JPanel.class).getComponent(0));
                    assertTrue(campo(panel[0], "btnExportar", JButton.class).isEnabled());
                    panel[0].addNotify();
                    assertNotNull(campo(panel[0], "scrollDatos", JScrollPane.class).getColumnHeader());
                    int alto = Math.max(670, panel[0].getPreferredSize().height);
                    for (int ancho : new int[]{960, 1160}) {
                        panel[0].setSize(ancho, alto); layout(panel[0]);
                        assertTrue(campo(panel[0], "scrollDatos", JScrollPane.class).getHeight() >= 100);
                        var imagen = new java.awt.image.BufferedImage(ancho, alto, java.awt.image.BufferedImage.TYPE_INT_RGB);
                        var g=imagen.createGraphics();panel[0].printAll(g);g.dispose();
                        Files.createDirectories(Path.of("target/reportes-qa"));
                        javax.imageio.ImageIO.write(imagen,"png",Path.of("target/reportes-qa/"+nombre+"-"+ancho+".png").toFile());
                    }
                    panel[0].removeNotify();
                    Path pdfOut = Path.of("target/reportes-qa/" + nombre + ".pdf");
                    enlaceRef[0].exportarPdfDirecto(pdfOut);
                    assertTrue(Files.exists(pdfOut) && Files.size(pdfOut) > 1000);
                    byte[] cabeceraPdf = Files.readAllBytes(pdfOut);
                    assertEquals("%PDF-", new String(cabeceraPdf, 0, 5, java.nio.charset.StandardCharsets.US_ASCII));
                    campo(panel[0], "btnLimpiar", JButton.class).doClick();
                    assertTrue(campo(panel[0], "lblEstado", JLabel.class).getText().contains("restablecidos"));
                } catch(Exception e) { throw new RuntimeException(e); }
            });
        }));
    }
    @Test void enviaLosIdentificadoresYFechasAlBackendFueraDelEdt() throws Exception {
        var llamada = new java.util.concurrent.CompletableFuture<List<Object>>();
        var dao = new ReportesDAODoble() {
            @Override public List<RatioMantenimientoDTO> obtenerRatioMantenimiento(Date inicio, Date fin, Integer maquinaria, Integer categoria) {
                llamada.complete(List.of(inicio, fin, maquinaria, categoria, SwingUtilities.isEventDispatchThread()));
                return super.obtenerRatioMantenimiento(inicio, fin, maquinaria, categoria);
            }
        };
        SwingUtilities.invokeAndWait(() -> {
            try {
                var vista = new ReporteMantenimiento();
                var enlace = vista.enlazar(new ReportesOperativosController(dao, new GraficosReporteService()));
                enlace.aplicarCatalogos(Map.of("cmbMaquinaria", List.of(new OpcionFiltro(44,"Equipo")), "cmbCategoria", List.of(new OpcionFiltro(55,"Categoría"))));
                campo(vista,"fechaInicio",com.toedter.calendar.JDateChooser.class).setDate(Date.valueOf("2026-10-01"));
                campo(vista,"fechaFin",com.toedter.calendar.JDateChooser.class).setDate(Date.valueOf("2026-10-29"));
                campo(vista,"btnGenerar",JButton.class).doClick();
            } catch(Exception e) {throw new RuntimeException(e);}
        });
        assertEquals(List.of(Date.valueOf("2026-10-01"),Date.valueOf("2026-10-29"),44,55,false),llamada.get(10,java.util.concurrent.TimeUnit.SECONDS));
    }
    private static void layout(java.awt.Container c) { c.doLayout(); for (var hijo:c.getComponents()) if(hijo instanceof java.awt.Container cont) layout(cont); }
    @Test void validaFechasYConservaLaVistaCuandoNoHayDatos() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                var vista = new ReporteMantenimiento();
                var controller = new ReportesOperativosController(new ReportesDAODoble(),new GraficosReporteService());
                var enlace = vista.enlazar(controller);enlace.aplicarCatalogos(Map.of());
                campo(vista,"fechaInicio",com.toedter.calendar.JDateChooser.class).setDate(Date.valueOf("2026-10-29"));
                campo(vista,"fechaFin",com.toedter.calendar.JDateChooser.class).setDate(Date.valueOf("2026-10-01"));
                campo(vista,"btnGenerar",JButton.class).doClick();
                assertTrue(campo(vista,"lblEstado",JLabel.class).getText().contains("anterior"));
                assertFalse(campo(vista,"btnExportar",JButton.class).isEnabled());
                var vacio = new ReportesOperativosController(new ReportesDAO(){
                    @Override public List<RatioMantenimientoDTO> obtenerRatioMantenimiento(Date i,Date f,Integer m,Integer c){return List.of();}
                },new GraficosReporteService()).generarReporte1RatioMantenimiento(null,null,null,null);
                campo(vista,"tablaDatos",JTable.class).getColumnModel().getColumn(0).setPreferredWidth(233);
                enlace.presentar(vacio);
                assertEquals(233,campo(vista,"tablaDatos",JTable.class).getColumnModel().getColumn(0).getPreferredWidth());
                assertEquals(0,campo(vista,"tablaDatos",JTable.class).getRowCount());
                assertTrue(campo(vista,"lblEstado",JLabel.class).getText().contains("Sin resultados"));
                assertEquals("0",campo(vista,"lblKpi1",JLabel.class).getText());
            } catch(Exception e){throw new RuntimeException(e);}
        });
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

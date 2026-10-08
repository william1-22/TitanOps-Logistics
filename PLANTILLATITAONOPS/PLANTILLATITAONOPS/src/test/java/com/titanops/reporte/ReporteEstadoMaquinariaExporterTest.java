package com.titanops.reporte;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.controlador.GestionControlReportesController.EstadoMaquinariaFila;
import com.titanops.controlador.GestionControlReportesController.ResumenControl;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReporteEstadoMaquinariaExporterTest {

    @TempDir
    Path carpetaTemporal;

    @Test
    void exportaLosTresFormatosConResumenYFilas() throws Exception {
        ReporteEstadoMaquinaria reporte = new ReporteEstadoMaquinaria(
                List.of(new EstadoMaquinariaFila(7, "EQ-07", "CAT 320",
                        "DISPONIBLE", true,
                        Timestamp.valueOf("2026-10-08 08:30:00"))),
                new ResumenControl(1, 0, 0, 0), "EQ-07", "TODOS",
                Instant.parse("2026-10-08T14:30:00Z"));
        Path csv = carpetaTemporal.resolve("reporte.csv");
        Path json = carpetaTemporal.resolve("reporte.json");
        Path pdf = carpetaTemporal.resolve("reporte.pdf");

        ReporteEstadoMaquinariaExporter.exportarCsv(csv, reporte);
        ReporteEstadoMaquinariaExporter.exportarJson(json, reporte);
        ReporteEstadoMaquinariaExporter.exportarPdf(pdf, reporte);

        String contenidoCsv = Files.readString(csv, StandardCharsets.UTF_8);
        String contenidoJson = Files.readString(json, StandardCharsets.UTF_8);
        String contenidoPdf = Files.readString(pdf, StandardCharsets.ISO_8859_1);
        assertTrue(contenidoCsv.contains("EQ-07"));
        assertTrue(contenidoCsv.contains("Disponibles;1"));
        assertTrue(contenidoJson.contains("\"codigoInventario\": \"EQ-07\""));
        assertTrue(contenidoJson.contains("\"total\": 1"));
        assertTrue(contenidoPdf.startsWith("%PDF-1.4"));
        assertTrue(contenidoPdf.contains("xref"));
        assertTrue(contenidoPdf.contains("EQ-07"));
    }
}

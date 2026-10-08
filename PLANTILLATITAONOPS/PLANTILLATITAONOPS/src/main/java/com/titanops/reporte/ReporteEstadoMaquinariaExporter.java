package com.titanops.reporte;

import com.titanops.controlador.GestionControlReportesController.EstadoMaquinariaFila;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Escribe el reporte de estado en CSV, JSON y un PDF autónomo. */
public final class ReporteEstadoMaquinariaExporter {
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    private ReporteEstadoMaquinariaExporter() {}

    public static void exportarCsv(Path destino, ReporteEstadoMaquinaria reporte)
            throws IOException {
        StringBuilder contenido = new StringBuilder();
        contenido.append("Reporte de estado de maquinaria\n");
        contenido.append("Generado;\"").append(escaparCsv(FECHA.format(reporte.generadoEn())))
                .append("\"\n");
        contenido.append("Filtro;\"").append(escaparCsv(reporte.filtro()))
                .append("\"\n");
        contenido.append("Estado;\"").append(escaparCsv(reporte.estado()))
                .append("\"\n\n");
        contenido.append("Resumen;Cantidad\n");
        contenido.append("Disponibles;").append(reporte.resumen().disponibles()).append('\n');
        contenido.append("En ruta;").append(reporte.resumen().enRuta()).append('\n');
        contenido.append("Mantenimiento;").append(reporte.resumen().mantenimiento()).append('\n');
        contenido.append("Inactivas;").append(reporte.resumen().inactivas()).append('\n');
        contenido.append("Total;").append(reporte.resumen().total()).append("\n\n");
        contenido.append("ID;Código;Descripción;Estado;Fecha de registro\n");
        for (EstadoMaquinariaFila fila : reporte.filas()) {
            contenido.append(fila.idMaquinaria()).append(';')
                    .append('"').append(escaparCsv(fila.codigoInventario())).append('"').append(';')
                    .append('"').append(escaparCsv(fila.descripcion())).append('"').append(';')
                    .append('"').append(escaparCsv(fila.estado())).append('"').append(';')
                    .append('"').append(escaparCsv(fecha(fila.fechaRegistro()))).append('"')
                    .append('\n');
        }
        Files.writeString(destino, contenido.toString(), StandardCharsets.UTF_8);
    }

    public static void exportarJson(Path destino, ReporteEstadoMaquinaria reporte)
            throws IOException {
        StringBuilder contenido = new StringBuilder("{\n");
        propiedad(contenido, "generadoEn", FECHA.format(reporte.generadoEn()), false, 1);
        propiedad(contenido, "filtro", reporte.filtro(), false, 1);
        propiedad(contenido, "estado", reporte.estado(), false, 1);
        contenido.append("  \"resumen\": {\n");
        propiedad(contenido, "disponibles", reporte.resumen().disponibles(), false, 2);
        propiedad(contenido, "enRuta", reporte.resumen().enRuta(), false, 2);
        propiedad(contenido, "mantenimiento", reporte.resumen().mantenimiento(), false, 2);
        propiedad(contenido, "inactivas", reporte.resumen().inactivas(), false, 2);
        propiedad(contenido, "total", reporte.resumen().total(), true, 2);
        contenido.append("  },\n  \"maquinaria\": [\n");
        for (int indice = 0; indice < reporte.filas().size(); indice++) {
            EstadoMaquinariaFila fila = reporte.filas().get(indice);
            contenido.append("    {\n");
            propiedad(contenido, "idMaquinaria", fila.idMaquinaria(), false, 3);
            propiedad(contenido, "codigoInventario", fila.codigoInventario(), true, 3);
            propiedad(contenido, "descripcion", fila.descripcion(), true, 3);
            propiedad(contenido, "estado", fila.estado(), true, 3);
            propiedad(contenido, "fechaRegistro", fecha(fila.fechaRegistro()), true, 3);
            contenido.append("    }").append(indice + 1 < reporte.filas().size() ? "," : "")
                    .append('\n');
        }
        contenido.append("  ]\n}\n");
        Files.writeString(destino, contenido.toString(), StandardCharsets.UTF_8);
    }

    public static void exportarPdf(Path destino, ReporteEstadoMaquinaria reporte)
            throws IOException {
        List<String> lineas = new ArrayList<>();
        lineas.add("REPORTE DE ESTADO DE MAQUINARIA");
        lineas.add("Generado: " + FECHA.format(reporte.generadoEn()));
        lineas.add("Filtro: " + visible(reporte.filtro()));
        lineas.add("Estado: " + visible(reporte.estado()));
        lineas.add("");
        lineas.add("RESUMEN");
        lineas.add("Disponibles: " + reporte.resumen().disponibles());
        lineas.add("En ruta: " + reporte.resumen().enRuta());
        lineas.add("Mantenimiento: " + reporte.resumen().mantenimiento());
        lineas.add("Inactivas: " + reporte.resumen().inactivas());
        lineas.add("Total: " + reporte.resumen().total());
        lineas.add("");
        lineas.add("ID | CODIGO | DESCRIPCION | ESTADO | FECHA DE REGISTRO");
        for (EstadoMaquinariaFila fila : reporte.filas()) {
            lineas.add(fila.idMaquinaria() + " | " + visible(fila.codigoInventario())
                    + " | " + visible(fila.descripcion()) + " | "
                    + visible(fila.estado()) + " | " + visible(fecha(fila.fechaRegistro())));
        }
        escribirPdf(destino, lineas);
    }

    private static void escribirPdf(Path destino, List<String> lineas) throws IOException {
        final int lineasPorPagina = 42;
        List<String> paginas = new ArrayList<>();
        for (int inicio = 0; inicio < lineas.size(); inicio += lineasPorPagina) {
            int fin = Math.min(inicio + lineasPorPagina, lineas.size());
            StringBuilder contenido = new StringBuilder("BT\n/F1 10 Tf\n40 760 Td\n");
            for (int indice = inicio; indice < fin; indice++) {
                if (indice > inicio) {
                    contenido.append("0 -16 Td\n");
                }
                contenido.append('(').append(escaparPdf(lineas.get(indice)))
                        .append(") Tj\n");
            }
            contenido.append("ET\n");
            paginas.add(contenido.toString());
        }
        if (paginas.isEmpty()) {
            paginas.add("BT\n/F1 10 Tf\n40 760 Td\n(Reporte sin registros) Tj\nET\n");
        }

        List<String> objetos = new ArrayList<>();
        objetos.add("<< /Type /Catalog /Pages 2 0 R >>");
        int primerObjetoPagina = 4;
        StringBuilder referencias = new StringBuilder();
        for (int indice = 0; indice < paginas.size(); indice++) {
            referencias.append(primerObjetoPagina + indice * 2).append(" 0 R ");
        }
        objetos.add("<< /Type /Pages /Kids [" + referencias
                + "] /Count " + paginas.size() + " >>");
        objetos.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
        for (String pagina : paginas) {
            int numeroPagina = objetos.size() + 1;
            int numeroContenido = numeroPagina + 1;
            objetos.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] "
                    + "/Resources << /Font << /F1 3 0 R >> >> /Contents "
                    + numeroContenido + " 0 R >>");
            byte[] contenido = pagina.getBytes(StandardCharsets.ISO_8859_1);
            objetos.add("<< /Length " + contenido.length + " >>\nstream\n"
                    + pagina + "endstream");
        }

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        escribir(salida, "%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n");
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);
        for (int indice = 0; indice < objetos.size(); indice++) {
            offsets.add(salida.size());
            escribir(salida, (indice + 1) + " 0 obj\n" + objetos.get(indice)
                    + "\nendobj\n");
        }
        int inicioXref = salida.size();
        escribir(salida, "xref\n0 " + (objetos.size() + 1) + "\n");
        escribir(salida, "0000000000 65535 f \n");
        for (int indice = 1; indice < offsets.size(); indice++) {
            escribir(salida, String.format("%010d 00000 n \n", offsets.get(indice)));
        }
        escribir(salida, "trailer\n<< /Size " + (objetos.size() + 1)
                + " /Root 1 0 R >>\nstartxref\n" + inicioXref + "\n%%EOF\n");
        Files.write(destino, salida.toByteArray());
    }

    private static void escribir(ByteArrayOutputStream salida, String texto)
            throws IOException {
        salida.write(texto.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static void propiedad(StringBuilder contenido, String nombre,
                                  String valor, boolean ultima, int nivel) {
        contenido.append("  ".repeat(nivel)).append('"').append(nombre).append("\": \"")
                .append(escaparJson(valor)).append('"').append(ultima ? "\n" : ",\n");
    }

    private static void propiedad(StringBuilder contenido, String nombre,
                                  int valor, boolean ultima, int nivel) {
        contenido.append("  ".repeat(nivel)).append('"').append(nombre).append("\": ")
                .append(valor).append(ultima ? "\n" : ",\n");
    }

    private static String escaparCsv(String valor) {
        return valor == null ? "" : valor.replace("\"", "\"\"");
    }

    private static String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private static String escaparPdf(String valor) {
        String normalizado = Normalizer.normalize(visible(valor), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalizado.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", " ").replace("\n", " ");
    }

    private static String visible(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private static String fecha(java.sql.Timestamp valor) {
        return valor == null ? "" : FECHA.format(valor.toInstant());
    }
}

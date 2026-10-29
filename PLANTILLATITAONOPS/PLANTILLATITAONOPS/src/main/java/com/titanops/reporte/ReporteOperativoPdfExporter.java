package com.titanops.reporte;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.titanops.controlador.ReportesOperativosController.ResultadoReporte;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

/**
 * Exportador de reportes operativos de TitanOps a documento PDF ejecutivo (horizontal)
 * que incluye cabecera corporativa, filtros aplicados, tarjetas KPI, gráficos JFreeChart
 * en alta resolución y tabla detallada paginada.
 */
public final class ReporteOperativoPdfExporter {

    private static final Color COLOR_AZUL_TITULO = new Color(30, 64, 175);
    private static final Color COLOR_AZUL_SUAVE = new Color(239, 246, 255);
    private static final Color COLOR_CABECERA_TABLA = new Color(224, 242, 254);
    private static final Color COLOR_BORDE_AZUL = new Color(125, 211, 252);
    private static final Color COLOR_FILA_PAR = new Color(248, 250, 252);
    private static final Color COLOR_TEXTO_OSCURO = new Color(30, 41, 59);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(71, 85, 105);

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private ReporteOperativoPdfExporter() {
    }

    /**
     * Genera un archivo PDF completo para cualquiera de los 9 reportes operativos.
     *
     * @param destino        Ruta de salida del archivo {@code .pdf}.
     * @param numeroReporte  Número identificador del reporte (1..10).
     * @param tituloReporte  Título descriptivo del reporte.
     * @param resumenFiltros Texto con los filtros activos al momento de exportar.
     * @param titulosKpi     Nombres de las tarjetas de indicadores KPI.
     * @param valoresKpi     Valores formateados de las tarjetas KPI.
     * @param resultado      Resultado generado por {@code ReportesOperativosController}.
     * @throws IOException Si ocurre un error de escritura en disco o de generación PDF.
     */
    public static void exportarPdf(
            Path destino,
            int numeroReporte,
            String tituloReporte,
            String resumenFiltros,
            String[] titulosKpi,
            String[] valoresKpi,
            ResultadoReporte<?, ?> resultado) throws IOException {
        if (destino.getParent() != null) {
            Files.createDirectories(destino.getParent());
        }
        try (OutputStream out = Files.newOutputStream(destino)) {
            Document doc = new Document(PageSize.A4.rotate(), 32f, 32f, 30f, 34f);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            String fechaGeneracion = LocalDateTime.now().format(FORMATO_FECHA_HORA);
            writer.setPageEvent(new PiePaginaCorporativo(tituloReporte, fechaGeneracion));
            doc.open();

            agregarCabeceraCorporativa(doc, numeroReporte, tituloReporte, resumenFiltros,
                    fechaGeneracion, resultado.filasTabla() == null ? 0 : resultado.filasTabla().size());
            agregarTarjetasKpi(doc, titulosKpi, valoresKpi);
            agregarSeccionGraficos(doc, resultado.graficoPrincipal(), resultado.graficoSecundario());
            agregarTablaDatos(doc, resultado.columnasTabla(), resultado.filasTabla());

            doc.close();
        } catch (DocumentException ex) {
            throw new IOException("Error al construir el documento PDF del reporte operativo", ex);
        }
    }

    private static void agregarCabeceraCorporativa(
            Document doc,
            int numeroReporte,
            String tituloReporte,
            String resumenFiltros,
            String fechaGeneracion,
            int totalFilas) throws DocumentException {
        Font fuenteSubtitulo = new Font(Font.HELVETICA, 8.5f, Font.BOLD, COLOR_TEXTO_SECUNDARIO);
        Font fuenteTitulo = new Font(Font.HELVETICA, 15f, Font.BOLD, COLOR_AZUL_TITULO);
        Font fuenteMeta = new Font(Font.HELVETICA, 9f, Font.NORMAL, COLOR_TEXTO_OSCURO);
        Font fuenteFiltros = new Font(Font.HELVETICA, 8.5f, Font.NORMAL, COLOR_TEXTO_SECUNDARIO);

        PdfPTable cabecera = new PdfPTable(new float[]{68f, 32f});
        cabecera.setWidthPercentage(100f);
        cabecera.setSpacingAfter(6f);

        PdfPCell celdaIzquierda = new PdfPCell();
        celdaIzquierda.setBorder(Rectangle.BOTTOM);
        celdaIzquierda.setBorderColor(COLOR_BORDE_AZUL);
        celdaIzquierda.setBorderWidth(1.2f);
        celdaIzquierda.setPaddingBottom(6f);
        celdaIzquierda.addElement(new Paragraph(
                "TITANOPS LOGISTICS  •  REPORTE OPERATIVO #" + numeroReporte, fuenteSubtitulo));
        celdaIzquierda.addElement(new Paragraph(
                tituloReporte != null && !tituloReporte.isBlank()
                        ? tituloReporte : "Reporte Operativo #" + numeroReporte,
                fuenteTitulo));
        cabecera.addCell(celdaIzquierda);

        PdfPCell celdaDerecha = new PdfPCell();
        celdaDerecha.setBorder(Rectangle.BOTTOM);
        celdaDerecha.setBorderColor(COLOR_BORDE_AZUL);
        celdaDerecha.setBorderWidth(1.2f);
        celdaDerecha.setPaddingBottom(6f);
        celdaDerecha.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph pFecha = new Paragraph("Generado: " + fechaGeneracion, fuenteMeta);
        pFecha.setAlignment(Element.ALIGN_RIGHT);
        Paragraph pRegistros = new Paragraph("Registros incluidos: " + totalFilas, fuenteMeta);
        pRegistros.setAlignment(Element.ALIGN_RIGHT);
        celdaDerecha.addElement(pFecha);
        celdaDerecha.addElement(pRegistros);
        cabecera.addCell(celdaDerecha);

        doc.add(cabecera);

        if (resumenFiltros != null && !resumenFiltros.isBlank()) {
            Paragraph pFiltros = new Paragraph("Filtros aplicados: " + resumenFiltros, fuenteFiltros);
            pFiltros.setSpacingAfter(8f);
            doc.add(pFiltros);
        }
    }

    private static void agregarTarjetasKpi(
            Document doc, String[] titulosKpi, String[] valoresKpi) throws DocumentException {
        if (titulosKpi == null || valoresKpi == null || titulosKpi.length == 0) {
            return;
        }
        int cantidad = Math.min(titulosKpi.length, valoresKpi.length);
        PdfPTable tablaKpi = new PdfPTable(cantidad);
        tablaKpi.setWidthPercentage(100f);
        tablaKpi.setSpacingAfter(10f);

        Font fuenteNombre = new Font(Font.HELVETICA, 8.5f, Font.NORMAL, COLOR_TEXTO_SECUNDARIO);
        Font fuenteValor = new Font(Font.HELVETICA, 13f, Font.BOLD, COLOR_AZUL_TITULO);

        for (int i = 0; i < cantidad; i++) {
            PdfPCell tarjeta = new PdfPCell();
            tarjeta.setBackgroundColor(COLOR_AZUL_SUAVE);
            tarjeta.setBorderColor(COLOR_BORDE_AZUL);
            tarjeta.setBorderWidth(0.8f);
            tarjeta.setPaddingTop(6f);
            tarjeta.setPaddingBottom(8f);
            tarjeta.setPaddingLeft(6f);
            tarjeta.setPaddingRight(6f);

            Paragraph pNombre = new Paragraph(
                    titulosKpi[i] != null ? titulosKpi[i] : ("KPI " + (i + 1)), fuenteNombre);
            pNombre.setAlignment(Element.ALIGN_CENTER);

            Paragraph pValor = new Paragraph(
                    valoresKpi[i] != null ? valoresKpi[i] : "—", fuenteValor);
            pValor.setAlignment(Element.ALIGN_CENTER);
            pValor.setSpacingBefore(3f);

            tarjeta.addElement(pNombre);
            tarjeta.addElement(pValor);
            tablaKpi.addCell(tarjeta);
        }
        doc.add(tablaKpi);
    }

    private static void agregarSeccionGraficos(
            Document doc, JFreeChart graficoPrincipal, JFreeChart graficoSecundario)
            throws DocumentException, IOException {
        if (graficoPrincipal == null) {
            return;
        }
        if (graficoSecundario == null) {
            Image imgPrincipal = crearImagenGrafico(graficoPrincipal, 1100, 400);
            imgPrincipal.scaleToFit(760f, 235f);
            imgPrincipal.setAlignment(Element.ALIGN_CENTER);
            imgPrincipal.setSpacingAfter(10f);
            doc.add(imgPrincipal);
        } else {
            Image imgPrincipal = crearImagenGrafico(graficoPrincipal, 1100, 340);
            imgPrincipal.scaleToFit(760f, 185f);
            imgPrincipal.setAlignment(Element.ALIGN_CENTER);
            imgPrincipal.setSpacingAfter(6f);
            doc.add(imgPrincipal);

            Image imgSecundario = crearImagenGrafico(graficoSecundario, 1100, 340);
            imgSecundario.scaleToFit(760f, 185f);
            imgSecundario.setAlignment(Element.ALIGN_CENTER);
            imgSecundario.setSpacingAfter(10f);
            doc.add(imgSecundario);
        }
    }

    private static Image crearImagenGrafico(JFreeChart chart, int anchoPx, int altoPx)
            throws IOException, DocumentException {
        BufferedImage buffered = chart.createBufferedImage(anchoPx, altoPx);
        byte[] bytesPng = ChartUtils.encodeAsPNG(buffered);
        return Image.getInstance(bytesPng);
    }

    private static void agregarTablaDatos(
            Document doc, String[] columnas, List<Object[]> filas) throws DocumentException {
        if (columnas == null || columnas.length == 0) {
            return;
        }
        Font fuenteSubseccion = new Font(Font.HELVETICA, 10f, Font.BOLD, COLOR_AZUL_TITULO);
        Paragraph tituloTabla = new Paragraph(
                "DETALLE DE REGISTROS (" + (filas == null ? 0 : filas.size()) + ")",
                fuenteSubseccion);
        tituloTabla.setSpacingBefore(4f);
        tituloTabla.setSpacingAfter(5f);
        doc.add(tituloTabla);

        PdfPTable tabla = new PdfPTable(columnas.length);
        tabla.setWidthPercentage(100f);
        tabla.setHeaderRows(1);

        Font fuenteCabecera = new Font(Font.HELVETICA, 8f, Font.BOLD, COLOR_AZUL_TITULO);
        Font fuenteCelda = new Font(Font.HELVETICA, 7.8f, Font.NORMAL, COLOR_TEXTO_OSCURO);

        for (String col : columnas) {
            PdfPCell th = new PdfPCell(new Phrase(col != null ? col : "", fuenteCabecera));
            th.setBackgroundColor(COLOR_CABECERA_TABLA);
            th.setBorderColor(COLOR_BORDE_AZUL);
            th.setPadding(5f);
            th.setHorizontalAlignment(Element.ALIGN_LEFT);
            th.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tabla.addCell(th);
        }

        if (filas == null || filas.isEmpty()) {
            PdfPCell vacia = new PdfPCell(new Phrase(
                    "Sin resultados para los filtros seleccionados.", fuenteCelda));
            vacia.setColspan(columnas.length);
            vacia.setHorizontalAlignment(Element.ALIGN_CENTER);
            vacia.setPadding(10f);
            vacia.setBorderColor(COLOR_BORDE_AZUL);
            tabla.addCell(vacia);
        } else {
            for (int r = 0; r < filas.size(); r++) {
                Object[] fila = filas.get(r);
                Color fondo = (r % 2 == 0) ? Color.WHITE : COLOR_FILA_PAR;
                for (int c = 0; c < columnas.length; c++) {
                    Object valor = (fila != null && c < fila.length) ? fila[c] : "";
                    String texto = valor != null ? String.valueOf(valor) : "—";
                    PdfPCell td = new PdfPCell(new Phrase(texto, fuenteCelda));
                    td.setBackgroundColor(fondo);
                    td.setBorderColor(COLOR_BORDE_AZUL);
                    td.setPadding(4.5f);
                    td.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tabla.addCell(td);
                }
            }
        }
        doc.add(tabla);
    }

    private static final class PiePaginaCorporativo extends PdfPageEventHelper {
        private final String titulo;
        private final String fecha;
        private final Font fuentePie = new Font(Font.HELVETICA, 8f, Font.NORMAL, COLOR_TEXTO_SECUNDARIO);

        PiePaginaCorporativo(String titulo, String fecha) {
            this.titulo = titulo != null ? titulo : "Reporte Operativo";
            this.fecha = fecha;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase izquierda = new Phrase("TitanOps Logistics  —  " + titulo + " (" + fecha + ")", fuentePie);
            Phrase derecha = new Phrase("Página " + writer.getPageNumber(), fuentePie);
            ColumnText.showTextAligned(
                    cb, Element.ALIGN_LEFT, izquierda,
                    document.left(), document.bottom() - 16f, 0f);
            ColumnText.showTextAligned(
                    cb, Element.ALIGN_RIGHT, derecha,
                    document.right(), document.bottom() - 16f, 0f);
        }
    }
}

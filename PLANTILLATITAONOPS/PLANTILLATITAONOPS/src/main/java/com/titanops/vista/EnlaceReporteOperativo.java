package com.titanops.vista;

import com.titanops.controlador.ReportesOperativosController;
import com.titanops.controlador.ReportesOperativosController.ResultadoReporte;
import com.titanops.reporte.ReportesDTO.*;
import com.titanops.dao.ReportesDAO;
import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import org.jfree.chart.JFreeChart;

/** Conecta los componentes del Designer con el backend; no construye la vista. */
final class EnlaceReporteOperativo {
    // ConexionBD comparte una conexión JDBC: serializar las consultas de este módulo.
    private static final Object CONSULTAS = new Object();
    private final JPanel vista;
    private final int numero;
    private final ReportesOperativosController controller;
    private final Map<String, Component> filtros;
    private final JTable tabla;
    private final JPanel principal, secundario;
    private final JLabel[] indicadores;
    private final String[] titulos;
    private final JLabel estado;
    private final JButton generar, limpiar, exportar;
    private ResultadoReporte<?, ?> resultado;
    private boolean ocupado;

    EnlaceReporteOperativo(JPanel vista, int numero, ReportesOperativosController controller,
            Map<String, Component> filtros, JTable tabla, JPanel principal, JPanel secundario,
            JLabel[] indicadores, JLabel estado, JButton generar, JButton limpiar, JButton exportar) {
        this.vista = vista;
        this.numero = numero;
        this.controller = controller;
        this.filtros = filtros;
        this.tabla = tabla;
        this.principal = principal;
        this.secundario = secundario;
        this.indicadores = indicadores;
        this.titulos = java.util.Arrays.stream(indicadores).map(JLabel::getToolTipText).toArray(String[]::new);
        this.estado = estado;
        this.generar = generar;
        this.limpiar = limpiar;
        this.exportar = exportar;
        for (var entry : filtros.entrySet()) {
            if (entry.getValue() instanceof JComboBox combo) {
                List<String> opciones = switch (entry.getKey()) {
                    case "cmbActivo" -> ReportesOperativosController.FILTRO_ACTIVO_OPCIONES;
                    case "cmbEstadoOperativo" -> ReportesOperativosController.ESTADOS_MAQUINARIA;
                    case "cmbEstadoVigencia" -> ReportesOperativosController.ESTADOS_VIGENCIA_CERTIFICACION;
                    case "cmbEstadoRuta" -> ReportesOperativosController.ESTADOS_RUTA;
                    case "cmbEstadoAsignacion" -> ReportesOperativosController.ESTADOS_ASIGNACION;
                    case "cmbTurno" -> ReportesOperativosController.TURNOS_OPERADOR;
                    case "cmbEstadoOperador" -> ReportesOperativosController.ESTADOS_OPERADOR;
                    default -> List.of();
                };
                for (String opcion : opciones) combo.addItem(opcion);
            }
        }
        actualizarTabla(controller.construirModeloTabla(columnas(), List.of()));
        generar.addActionListener(e -> generar());
        limpiar.addActionListener(e -> limpiar());
        exportar.addActionListener(e -> exportar());
        habilitar(false);
    }

    static Map<String, List<OpcionFiltro>> cargarCatalogos() throws java.sql.SQLException {
        synchronized (CONSULTAS) {
            var dao = new ReportesDAO();
            return Map.of("cmbCategoria", dao.listarOpcionesCategorias(),
                    "cmbMaquinaria", dao.listarOpcionesMaquinaria(),
                    "cmbRuta", dao.listarOpcionesRutas(), "cmbOperador", dao.listarOpcionesOperadores(),
                    "cmbUsuario", dao.listarOpcionesUsuarios(), "cmbRol", dao.listarOpcionesRoles());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void aplicarCatalogos(Map<String, List<OpcionFiltro>> catalogos) {
        for (var entry : catalogos.entrySet()) {
            if (filtros.get(entry.getKey()) instanceof JComboBox combo) {
                Object anterior = combo.getSelectedItem();
                combo.removeAllItems();
                entry.getValue().forEach(combo::addItem);
                if (anterior instanceof OpcionFiltro opcion) {
                    entry.getValue().stream().filter(o -> java.util.Objects.equals(o.id(), opcion.id()))
                            .findFirst().ifPresent(combo::setSelectedItem);
                }
            }
        }
        habilitar(true);
    }

    void habilitar(boolean disponible) {
        generar.setEnabled(disponible && !ocupado);
        limpiar.setEnabled(disponible && !ocupado);
        filtros.values().forEach(c -> c.setEnabled(disponible && !ocupado));
        exportar.setEnabled(!ocupado && resultado != null);
    }

    private String[] columnas() {
        return switch (numero) {
            case 1 -> ReportesOperativosController.COLUMNAS_REPORTE_1;
            case 2 -> ReportesOperativosController.COLUMNAS_REPORTE_2;
            case 3 -> ReportesOperativosController.COLUMNAS_REPORTE_3;
            case 5 -> ReportesOperativosController.COLUMNAS_REPORTE_5;
            case 6 -> ReportesOperativosController.COLUMNAS_REPORTE_6;
            case 7 -> ReportesOperativosController.COLUMNAS_REPORTE_7;
            case 8 -> ReportesOperativosController.COLUMNAS_REPORTE_8;
            case 9 -> ReportesOperativosController.COLUMNAS_REPORTE_9;
            case 10 -> ReportesOperativosController.COLUMNAS_REPORTE_10;
            default -> throw new IllegalArgumentException("Reporte desconocido");
        };
    }

    private Object seleccion(String nombre) {
        return filtros.get(nombre) instanceof JComboBox<?> combo ? combo.getSelectedItem() : null;
    }
    private Integer id(String nombre) {
        return seleccion(nombre) instanceof OpcionFiltro op ? op.id() : null;
    }
    private String texto(String nombre) {
        if (filtros.get(nombre) instanceof JTextField campo) return campo.getText().trim();
        return seleccion(nombre) == null ? null : seleccion(nombre).toString();
    }
    private Date fecha(String nombre) {
        if (!(filtros.get(nombre) instanceof JDateChooser chooser)) return null;
        return chooser.getDate() == null ? null : new Date(chooser.getDate().getTime());
    }

    private void generar() {
        if (ocupado) return;
        final Date inicio = fecha("fechaInicio"), fin = fecha("fechaFin");
        if (inicio != null && fin != null && inicio.after(fin)) {
            estado.setText("La fecha inicial debe ser anterior o igual a la fecha final.");
            return;
        }
        // Capturar los controles en el EDT antes de iniciar consultas.
        final Integer categoria = id("cmbCategoria"), maquinaria = id("cmbMaquinaria"),
                ruta = id("cmbRuta"), operador = id("cmbOperador"), rol = id("cmbRol"), usuario = id("cmbUsuario");
        final Boolean activo = ReportesOperativosController.interpretarFiltroActivo(texto("cmbActivo"));
        final String estadoOperativo = texto("cmbEstadoOperativo"), vigencia = texto("cmbEstadoVigencia"),
                licencia = texto("txtLicenciaTipo"), estadoRuta = texto("cmbEstadoRuta"),
                estadoViaje = texto("cmbEstadoAsignacion"), turno = texto("cmbTurno"), estadoOperador = texto("cmbEstadoOperador");
        ocupado = true;
        habilitar(true);
        estado.setText("Generando reporte…");
        new SwingWorker<ResultadoReporte<?, ?>, Void>() {
            protected ResultadoReporte<?, ?> doInBackground() throws Exception {
                synchronized (CONSULTAS) {
                    return switch (numero) {
                        case 1 -> controller.generarReporte1RatioMantenimiento(inicio, fin, maquinaria, categoria);
                        case 2 -> controller.generarReporte2CostoHoraUso(categoria, activo);
                        case 3 -> controller.generarReporte3CumplimientoRutas(inicio, fin, ruta, operador);
                        case 5 -> controller.generarReporte5SnapshotMaquinaria(activo, estadoOperativo, categoria);
                        case 6 -> controller.generarReporte6Certificaciones(vigencia, categoria, licencia);
                        case 7 -> controller.generarReporte7AuditoriaUsuarios(rol, usuario, activo, inicio, fin);
                        case 8 -> controller.generarReporte8UtilizacionFlota(categoria, inicio, fin);
                        case 9 -> controller.generarReporte9HistorialRutas(ruta, estadoRuta, estadoViaje, operador);
                        case 10 -> controller.generarReporte10ProductividadTurno(turno, estadoOperador, inicio, fin);
                        default -> throw new IllegalArgumentException("Reporte desconocido");
                    };
                }
            }
            protected void done() {
                try {
                    presentar(get());
                } catch (Exception ex) {
                    estado.setText("No se pudo generar el reporte. Revisa la conexión y vuelve a intentarlo.");
                } finally {
                    ocupado = false;
                    habilitar(true);
                }
            }
        }.execute();
    }

    void presentar(ResultadoReporte<?, ?> nuevo) {
        resultado = nuevo;
        actualizarTabla(controller.construirModeloTabla(nuevo.columnasTabla(), nuevo.filasTabla()));
        controller.incrustarGraficoEnPanel(principal, nuevo.graficoPrincipal());
        if (secundario != null) controller.incrustarGraficoEnPanel(secundario, nuevo.graficoSecundario());
        Object[] valores = valoresKpi(nuevo.kpi());
        for (int i = 0; i < indicadores.length; i++) {
            String valor = valores[i] instanceof Number n ? String.format(java.util.Locale.US, "%,.2f", n.doubleValue()) : valores[i].toString();
            if (valores[i] instanceof Integer) valor = valores[i].toString();
            indicadores[i].setText(valor);
            indicadores[i].setToolTipText(titulos[i] + ": " + valor);
        }
        estado.setText(nuevo.datos().isEmpty() ? "Sin resultados para los filtros seleccionados."
                : nuevo.datos().size() + " filas. Reporte generado; al cambiar filtros, vuelve a generar.");
        exportar.setEnabled(true);
    }

    private static Object[] valoresKpi(Object kpi) {
        return switch (kpi) {
            case KpiRatioMantenimientoDTO k -> new Object[]{k.totalMantenimientos(), k.conteoPreventivos()+" / "+k.conteoCorrectivos(), k.porcentajeIncidenciasCorrectivas(), k.porcentajeGastoCorrectivo(), k.costoTotal()};
            case KpiCostoHoraUsoDTO k -> new Object[]{k.totalMaquinasEvaluadas(), k.horasUsoTotales(), k.costoAcumuladoTotal(), k.costoPorHoraGlobal(), k.unidadMayorCostoPorHora()};
            case KpiCumplimientoRutasDTO k -> new Object[]{k.totalViajesFinalizados(), k.viajesATiempo(), k.viajesConRetraso(), k.porcentajePuntualidadGlobal(), k.desviacionPromedioHoras()};
            case KpiDisponibilidadMaquinariaDTO k -> new Object[]{k.totalMaquinasActivas(), k.disponibles()+" / "+k.enRuta(), k.enMantenimiento(), k.inactivas(), k.tasaDisponibilidad()};
            case KpiCertificacionesDTO k -> new Object[]{k.totalCertificaciones(), k.vigentes(), k.porVencer(), k.vencidas(), k.porcentajeVigentes()};
            case KpiAuditoriaUsuariosDTO k -> new Object[]{k.totalUsuariosAuditados(), k.totalDespachosRegistrados(), k.totalMantenimientosRegistrados(), k.totalCancelaciones(), k.montoTotalTallerAutorizado()};
            case KpiUtilizacionFlotaDTO k -> new Object[]{k.totalUnidades(), k.totalHorasEnRuta(), k.totalHorasEnTaller(), k.promedioUsoDiarioGlobal()};
            case KpiHistorialRutasDTO k -> new Object[]{k.totalViajes(), k.viajesFinalizados()+" / "+k.viajesEnCurso(), k.viajesProgramados()+" / "+k.viajesCancelados(), k.tasaEfectividad(), k.tasaCancelacion()};
            case KpiProductividadTurnoDTO k -> new Object[]{k.totalOperadores(), k.totalViajesFinalizados(), k.totalViajesCancelados(), k.totalHorasConducidas(), k.promedioCumplimientoPorcentaje()};
            default -> throw new IllegalArgumentException("Indicadores desconocidos");
        };
    }

    private void actualizarTabla(javax.swing.table.DefaultTableModel modelo) {
        // Conservar las columnas y medidas definidas en Design al sustituir los datos.
        var columnasDisenadas = tabla.getColumnModel();
        tabla.setColumnModel(new javax.swing.table.DefaultTableColumnModel());
        tabla.setModel(modelo);
        tabla.setColumnModel(columnasDisenadas);
    }

    private void limpiar() {
        for (Component componente : filtros.values()) {
            if (componente instanceof JDateChooser fecha) fecha.setDate(null);
            else if (componente instanceof JComboBox<?> combo && combo.getItemCount() > 0) combo.setSelectedIndex(0);
            else if (componente instanceof JTextField texto) texto.setText("");
        }
        estado.setText("Filtros restablecidos. Pulsa GENERAR REPORTE para actualizar los resultados.");
    }

    private String tituloReporte() {
        return switch (numero) {
            case 1 -> "Mantenimiento preventivo y correctivo";
            case 2 -> "Costo de mantenimiento por hora de uso";
            case 3 -> "Cumplimiento de tiempos de rutas";
            case 5 -> "Datos generales y disponibilidad de maquinaria";
            case 6 -> "Certificaciones y vigencia operativa";
            case 7 -> "Auditoría y gestión de usuarios";
            case 8 -> "Utilización y desgaste de flota";
            case 9 -> "Historial de rutas, proyectos y observaciones";
            case 10 -> "Productividad y carga laboral por turno";
            default -> "Reporte Operativo #" + numero;
        };
    }

    private String resumenFiltros() {
        var partes = new java.util.ArrayList<String>();
        var fmt = new java.text.SimpleDateFormat("dd/MM/yyyy");
        for (var entry : filtros.entrySet()) {
            String clave = entry.getKey();
            Component comp = entry.getValue();
            String valor;
            if (comp instanceof JDateChooser chooser) {
                valor = chooser.getDate() != null ? fmt.format(chooser.getDate()) : "Sin límite";
            } else if (comp instanceof JComboBox<?> combo) {
                valor = combo.getSelectedItem() != null ? combo.getSelectedItem().toString() : "TODOS";
            } else if (comp instanceof JTextField txt) {
                valor = txt.getText() != null && !txt.getText().isBlank() ? txt.getText().trim() : "TODOS";
            } else {
                continue;
            }
            partes.add(clave.replaceFirst("^(cmb|txt)", "") + ": " + valor);
        }
        return String.join("  |  ", partes);
    }

    void exportarPdfDirecto(Path destino) throws java.io.IOException {
        if (resultado == null) {
            throw new IllegalStateException("Primero debe generarse el reporte antes de exportar.");
        }
        String[] valores = java.util.Arrays.stream(indicadores).map(JLabel::getText).toArray(String[]::new);
        com.titanops.reporte.ReporteOperativoPdfExporter.exportarPdf(
                destino, numero, tituloReporte(), resumenFiltros(), titulos, valores, resultado);
    }

    private void exportar() {
        if (resultado == null || ocupado) return;
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Exportar reporte completo a PDF");
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        selector.setSelectedFile(new java.io.File("reporte-operativo-" + numero + ".pdf"));
        if (selector.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) return;
        Path elegido = selector.getSelectedFile().toPath();
        Path destino = elegido.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")
                ? elegido : Path.of(elegido + ".pdf");
        if (Files.exists(destino) && JOptionPane.showConfirmDialog(
                vista, "El archivo ya existe. ¿Deseas reemplazarlo?", "Guardar PDF",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        final String titulo = tituloReporte();
        final String filtrosActivos = resumenFiltros();
        final String[] valoresKpiActuales = java.util.Arrays.stream(indicadores).map(JLabel::getText).toArray(String[]::new);
        final ResultadoReporte<?, ?> instantanea = resultado;
        ocupado = true;
        habilitar(true);
        estado.setText("Exportando reporte PDF…");
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                com.titanops.reporte.ReporteOperativoPdfExporter.exportarPdf(
                        destino, numero, titulo, filtrosActivos, titulos, valoresKpiActuales, instantanea);
                return null;
            }
            protected void done() {
                try {
                    get();
                    estado.setText("Reporte PDF guardado en " + destino);
                } catch (Exception ex) {
                    estado.setText("No se pudo guardar el PDF. Revisa la carpeta y los permisos.");
                } finally {
                    ocupado = false;
                    habilitar(true);
                }
            }
        }.execute();
    }
}

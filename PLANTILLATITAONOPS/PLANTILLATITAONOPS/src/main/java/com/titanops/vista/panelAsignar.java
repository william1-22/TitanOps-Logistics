package com.titanops.vista;

import com.titanops.controlador.GestionAsignacionesController;
import com.titanops.controlador.GestionAsignacionesController.AsignacionFila;
import com.titanops.controlador.GestionAsignacionesController.MaquinariaOpcion;
import com.titanops.controlador.GestionAsignacionesController.OperadorOpcion;
import com.titanops.controlador.GestionAsignacionesController.ResultadoCreacion;
import com.titanops.controlador.GestionAsignacionesController.ResultadoOperacion;
import com.titanops.controlador.GestionAsignacionesController.RutaOpcion;
import com.titanops.dao.AsignacionDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.dao.OperadorDAO;
import com.titanops.dao.RutaDestinoDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/** Pantalla para asignar recursos a rutas y cerrar asignaciones activas. */
public class panelAsignar extends JPanel {
    private static final Color COLOR_FONDO = new Color(134, 137, 93);
    private static final Color COLOR_ACCION = new Color(93, 36, 23);
    private static final SimpleDateFormat FORMATO_FECHA =
            new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final Integer idUsuarioSesion;
    private final GestionAsignacionesController controller;
    private boolean cargando;

    public panelAsignar() {
        this(null, new GestionAsignacionesController(new AsignacionDAO(),
                new MaquinariaDAO(), new OperadorDAO(), new RutaDestinoDAO()));
    }

    panelAsignar(Integer idUsuarioSesion,
                 GestionAsignacionesController controller) {
        this.idUsuarioSesion = idUsuarioSesion;
        this.controller = controller;
        initComponents();
        configurarVista();
        configurarEventos();
        cargarDatos();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelSuperior = new javax.swing.JPanel();
        panelCaptura = new javax.swing.JPanel();
        panelFormulario = new javax.swing.JPanel();
        lblMaquinaria = new javax.swing.JLabel();
        cmbMaquinaria = new javax.swing.JComboBox<>();
        lblOperador = new javax.swing.JLabel();
        cmbOperador = new javax.swing.JComboBox<>();
        lblRuta = new javax.swing.JLabel();
        cmbRuta = new javax.swing.JComboBox<>();
        lblRetorno = new javax.swing.JLabel();
        spnRetorno = new javax.swing.JSpinner();
        lblObservaciones = new javax.swing.JLabel();
        scrollObservaciones = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        panelAccionCrear = new javax.swing.JPanel();
        btnAsignar = new javax.swing.JButton();
        lblRelleno = new javax.swing.JLabel();
        panelFiltros = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblFiltroEstado = new javax.swing.JLabel();
        cmbEstadoFiltro = new javax.swing.JComboBox<>();
        btnActualizar = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();
        scrollTabla = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        panelAcciones = new javax.swing.JPanel();
        btnFinalizar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setBackground(new java.awt.Color(134, 137, 93));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 18, 14, 18));
        setPreferredSize(new java.awt.Dimension(1120, 700));
        setLayout(new java.awt.BorderLayout(8, 8));

        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new java.awt.BorderLayout(0, 6));

        panelCaptura.setOpaque(false);
        panelCaptura.setLayout(new java.awt.BorderLayout());

        panelFormulario.setOpaque(false);

        lblMaquinaria.setText("Maquinaria:");

        lblOperador.setText("Operador:");

        lblRuta.setText("Ruta / proyecto:");

        lblRetorno.setText("Retorno estimado:");

        lblObservaciones.setText("Observaciones:");

        txtObservaciones.setColumns(30);
        txtObservaciones.setRows(3);
        scrollObservaciones.setViewportView(txtObservaciones);

        panelAccionCrear.setOpaque(false);

        btnAsignar.setText("INICIAR ASIGNACIÓN");
        panelAccionCrear.add(btnAsignar);

        javax.swing.GroupLayout panelFormularioLayout = new javax.swing.GroupLayout(panelFormulario);
        panelFormulario.setLayout(panelFormularioLayout);
        panelFormularioLayout.setHorizontalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormularioLayout.createSequentialGroup()
                        .addComponent(lblObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(panelAccionCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblRelleno, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(panelFormularioLayout.createSequentialGroup()
                            .addComponent(lblMaquinaria, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbMaquinaria, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblOperador)
                            .addGap(35, 35, 35)
                            .addComponent(cmbOperador, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelFormularioLayout.createSequentialGroup()
                            .addComponent(lblRuta)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbRuta, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(lblRetorno)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spnRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(1, 1, 1))
        );
        panelFormularioLayout.setVerticalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblMaquinaria, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbMaquinaria, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblOperador, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbOperador, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblRuta, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbRuta, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spnRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrollObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelAccionCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRelleno, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        panelCaptura.add(panelFormulario, java.awt.BorderLayout.CENTER);

        panelSuperior.add(panelCaptura, java.awt.BorderLayout.CENTER);

        panelFiltros.setOpaque(false);
        panelFiltros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));

        lblBuscar.setText("Buscar:");
        panelFiltros.add(lblBuscar);

        txtBuscar.setColumns(22);
        panelFiltros.add(txtBuscar);

        lblFiltroEstado.setText("Estado:");
        panelFiltros.add(lblFiltroEstado);
        panelFiltros.add(cmbEstadoFiltro);

        btnActualizar.setText("ACTUALIZAR");
        panelFiltros.add(btnActualizar);

        lblEstado.setText(" ");
        panelFiltros.add(lblEstado);

        panelSuperior.add(panelFiltros, java.awt.BorderLayout.SOUTH);

        add(panelSuperior, java.awt.BorderLayout.NORTH);

        scrollTabla.setViewportView(tabla);

        add(scrollTabla, java.awt.BorderLayout.CENTER);

        panelAcciones.setOpaque(false);
        panelAcciones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        btnFinalizar.setText("FINALIZAR");
        panelAcciones.add(btnFinalizar);

        btnCancelar.setText("CANCELAR");
        panelAcciones.add(btnCancelar);

        add(panelAcciones, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void configurarVista() {
        spnRetorno.setModel(crearModeloFecha());
        spnRetorno.setEditor(new JSpinner.DateEditor(spnRetorno, "dd/MM/yyyy HH:mm"));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        cmbEstadoFiltro.addItem("TODOS");
        for (String estado : GestionAsignacionesController.ESTADOS) {
            cmbEstadoFiltro.addItem(estado);
        }

        tabla.setModel(new DefaultTableModel(new Object[]{"ID", "MAQUINARIA",
            "OPERADOR", "PROYECTO", "ASIGNADA", "RETORNO EST.", "ESTADO",
            "OBSERVACIONES"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configurarBoton(btnAsignar);
        configurarBoton(btnActualizar);
        configurarBoton(btnFinalizar);
        configurarBoton(btnCancelar);
        actualizarBotonesSeleccion();
    }

    private void configurarEventos() {
        btnAsignar.addActionListener(event -> crearAsignacion());
        btnActualizar.addActionListener(event -> cargarDatos());
        txtBuscar.addActionListener(event -> recargarAsignaciones());
        cmbEstadoFiltro.addActionListener(event -> {
            if (!cargando) {
                recargarAsignaciones();
            }
        });
        tabla.getSelectionModel().addListSelectionListener(
                event -> actualizarBotonesSeleccion());
        btnFinalizar.addActionListener(event -> cerrarAsignacion(true));
        btnCancelar.addActionListener(event -> cerrarAsignacion(false));
    }

    public final void cargarDatos() {
        if (cargando) {
            return;
        }
        String filtro = txtBuscar.getText();
        String estado = (String) cmbEstadoFiltro.getSelectedItem();
        cambiarEstadoCarga(true, "Cargando asignaciones y recursos...");
        new SwingWorker<DatosPantalla, Void>() {
            @Override
            protected DatosPantalla doInBackground() {
                return new DatosPantalla(controller.listarMaquinariaDisponible(),
                        controller.listarOperadoresDisponibles(),
                        controller.listarRutasAsignables(),
                        controller.listarAsignaciones(filtro, estado));
            }

            @Override
            protected void done() {
                try {
                    DatosPantalla datos = get();
                    llenarCombo(cmbMaquinaria, datos.maquinaria());
                    llenarCombo(cmbOperador, datos.operadores());
                    llenarCombo(cmbRuta, datos.rutas());
                    llenarTabla(datos.asignaciones());
                    cambiarEstadoCarga(false,
                            datos.asignaciones().size() + " asignación(es) mostrada(s).");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    cambiarEstadoCarga(false, "Carga interrumpida.");
                } catch (ExecutionException exception) {
                    cambiarEstadoCarga(false, "No fue posible cargar los datos.");
                }
            }
        }.execute();
    }

    private void recargarAsignaciones() {
        if (cargando) {
            return;
        }
        String filtro = txtBuscar.getText();
        String estado = (String) cmbEstadoFiltro.getSelectedItem();
        cambiarEstadoCarga(true, "Cargando asignaciones...");
        new SwingWorker<List<AsignacionFila>, Void>() {
            @Override
            protected List<AsignacionFila> doInBackground() {
                return controller.listarAsignaciones(filtro, estado);
            }

            @Override
            protected void done() {
                try {
                    List<AsignacionFila> asignaciones = get();
                    llenarTabla(asignaciones);
                    cambiarEstadoCarga(false,
                            asignaciones.size() + " asignación(es) mostrada(s).");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    cambiarEstadoCarga(false, "Carga interrumpida.");
                } catch (ExecutionException exception) {
                    cambiarEstadoCarga(false, "No fue posible cargar las asignaciones.");
                }
            }
        }.execute();
    }

    private void crearAsignacion() {
        if (cargando) {
            return;
        }
        MaquinariaOpcion maquinaria = (MaquinariaOpcion) cmbMaquinaria.getSelectedItem();
        OperadorOpcion operador = (OperadorOpcion) cmbOperador.getSelectedItem();
        RutaOpcion ruta = (RutaOpcion) cmbRuta.getSelectedItem();
        Timestamp retorno = new Timestamp(((Date) spnRetorno.getValue()).getTime());
        String observaciones = txtObservaciones.getText();
        cambiarEstadoCarga(true, "Creando asignación...");
        new SwingWorker<ResultadoCreacion, Void>() {
            @Override
            protected ResultadoCreacion doInBackground() {
                return controller.crearAsignacion(idUsuarioSesion, maquinaria, operador,
                        ruta, retorno, observaciones);
            }

            @Override
            protected void done() {
                cambiarEstadoCarga(false, "Listo.");
                try {
                    ResultadoCreacion resultado = get();
                    if (!resultado.exitoso()) {
                        mostrarValidacion(resultado.mensaje());
                        return;
                    }
                    txtObservaciones.setText("");
                    spnRetorno.setValue(fechaInicialRetorno());
                    JOptionPane.showMessageDialog(panelAsignar.this,
                            resultado.mensaje(), "Asignaciones",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    mostrarError("Se interrumpió la creación de la asignación.");
                } catch (ExecutionException exception) {
                    mostrarError("No fue posible crear la asignación.");
                }
            }
        }.execute();
    }

    private void cerrarAsignacion(boolean finalizar) {
        Integer idAsignacion = idAsignacionSeleccionada();
        if (idAsignacion == null || cargando) {
            return;
        }
        String accion = finalizar ? "finalizar" : "cancelar";
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Deseas " + accion + " la asignación seleccionada?\n"
                        + "El operador y la maquinaria volverán a estar disponibles.",
                finalizar ? "Finalizar asignación" : "Cancelar asignación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        cambiarEstadoCarga(true, "Procesando asignación...");
        new SwingWorker<ResultadoOperacion, Void>() {
            @Override
            protected ResultadoOperacion doInBackground() {
                return finalizar ? controller.finalizarAsignacion(idAsignacion)
                        : controller.cancelarAsignacion(idAsignacion);
            }

            @Override
            protected void done() {
                cambiarEstadoCarga(false, "Listo.");
                try {
                    ResultadoOperacion resultado = get();
                    if (!resultado.exitoso()) {
                        mostrarValidacion(resultado.mensaje());
                        return;
                    }
                    JOptionPane.showMessageDialog(panelAsignar.this,
                            resultado.mensaje(), "Asignaciones",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    mostrarError("Se interrumpió la operación.");
                } catch (ExecutionException exception) {
                    mostrarError("No fue posible " + accion + " la asignación.");
                }
            }
        }.execute();
    }

    private void llenarTabla(List<AsignacionFila> asignaciones) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        tabla.clearSelection();
        modelo.setRowCount(0);
        for (AsignacionFila asignacion : asignaciones) {
            modelo.addRow(new Object[]{asignacion.idAsignacion(),
                asignacion.codigoMaquinaria(), asignacion.operador(),
                asignacion.proyecto(), fechaVisible(asignacion.fechaAsignacion()),
                fechaVisible(asignacion.fechaEstimadaRetorno()), asignacion.estado(),
                valorVisible(asignacion.observaciones())});
        }
        actualizarBotonesSeleccion();
    }

    private <T> void llenarCombo(JComboBox<T> combo, List<T> elementos) {
        combo.removeAllItems();
        for (T elemento : elementos) {
            combo.addItem(elemento);
        }
    }

    private Integer idAsignacionSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        return (Integer) tabla.getModel().getValueAt(filaModelo, 0);
    }

    private boolean seleccionEnCurso() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return false;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        return "EN_CURSO".equals(tabla.getModel().getValueAt(filaModelo, 6));
    }

    private void cambiarEstadoCarga(boolean ocupado, String mensaje) {
        cargando = ocupado;
        cmbMaquinaria.setEnabled(!ocupado);
        cmbOperador.setEnabled(!ocupado);
        cmbRuta.setEnabled(!ocupado);
        spnRetorno.setEnabled(!ocupado);
        txtObservaciones.setEnabled(!ocupado);
        txtBuscar.setEnabled(!ocupado);
        cmbEstadoFiltro.setEnabled(!ocupado);
        btnAsignar.setEnabled(!ocupado);
        btnActualizar.setEnabled(!ocupado);
        tabla.setEnabled(!ocupado);
        lblEstado.setText(mensaje);
        actualizarBotonesSeleccion();
    }

    private void actualizarBotonesSeleccion() {
        boolean habilitar = !cargando && seleccionEnCurso();
        btnFinalizar.setEnabled(habilitar);
        btnCancelar.setEnabled(habilitar);
    }

    private static SpinnerDateModel crearModeloFecha() {
        return new SpinnerDateModel(fechaInicialRetorno(), null, null,
                Calendar.MINUTE);
    }

    private static Date fechaInicialRetorno() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private static void configurarBoton(JButton boton) {
        boton.setBackground(COLOR_ACCION);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 14));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    private String fechaVisible(Timestamp fecha) {
        return fecha == null ? "" : FORMATO_FECHA.format(fecha);
    }

    private String valorVisible(String valor) {
        return valor == null ? "" : valor;
    }

    private void mostrarValidacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación",
                JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Asignaciones",
                JOptionPane.ERROR_MESSAGE);
    }

    private record DatosPantalla(List<MaquinariaOpcion> maquinaria,
                                  List<OperadorOpcion> operadores,
                                  List<RutaOpcion> rutas,
                                  List<AsignacionFila> asignaciones) {}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAsignar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JComboBox<String> cmbEstadoFiltro;
    private javax.swing.JComboBox<MaquinariaOpcion> cmbMaquinaria;
    private javax.swing.JComboBox<OperadorOpcion> cmbOperador;
    private javax.swing.JComboBox<RutaOpcion> cmbRuta;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFiltroEstado;
    private javax.swing.JLabel lblMaquinaria;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel lblOperador;
    private javax.swing.JLabel lblRelleno;
    private javax.swing.JLabel lblRetorno;
    private javax.swing.JLabel lblRuta;
    private javax.swing.JPanel panelAccionCrear;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelCaptura;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JPanel panelSuperior;
    private javax.swing.JScrollPane scrollObservaciones;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JSpinner spnRetorno;
    private javax.swing.JTable tabla;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextArea txtObservaciones;
    // End of variables declaration//GEN-END:variables
}

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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
    private final JComboBox<MaquinariaOpcion> cmbMaquinaria = new JComboBox<>();
    private final JComboBox<OperadorOpcion> cmbOperador = new JComboBox<>();
    private final JComboBox<RutaOpcion> cmbRuta = new JComboBox<>();
    private final JSpinner spnRetorno = new JSpinner(crearModeloFecha());
    private final JTextArea txtObservaciones = new JTextArea(3, 30);
    private final JTextField txtBuscar = new JTextField(22);
    private final JComboBox<String> cmbEstadoFiltro = new JComboBox<>();
    private final JButton btnAsignar = crearBoton("INICIAR ASIGNACIÓN");
    private final JButton btnActualizar = crearBoton("ACTUALIZAR");
    private final JButton btnFinalizar = crearBoton("FINALIZAR");
    private final JButton btnCancelar = crearBoton("CANCELAR");
    private final JLabel lblEstado = new JLabel(" ");
    private final JTable tabla = new JTable();
    private boolean cargando;

    public panelAsignar() {
        this(null, new GestionAsignacionesController(new AsignacionDAO(),
                new MaquinariaDAO(), new OperadorDAO(), new RutaDestinoDAO()));
    }

    panelAsignar(Integer idUsuarioSesion,
                 GestionAsignacionesController controller) {
        this.idUsuarioSesion = idUsuarioSesion;
        this.controller = controller;
        construirVista();
        configurarEventos();
        cargarDatos();
    }

    private void construirVista() {
        setLayout(new BorderLayout(8, 8));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        spnRetorno.setEditor(new JSpinner.DateEditor(spnRetorno, "dd/MM/yyyy HH:mm"));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        agregarCampo(formulario, 0, 0, "Maquinaria", cmbMaquinaria);
        agregarCampo(formulario, 2, 0, "Operador", cmbOperador);
        agregarCampo(formulario, 0, 1, "Ruta / proyecto", cmbRuta);
        agregarCampo(formulario, 2, 1, "Retorno estimado", spnRetorno);

        GridBagConstraints labelObservaciones = restricciones(0, 2);
        labelObservaciones.anchor = GridBagConstraints.FIRST_LINE_END;
        formulario.add(new JLabel("Observaciones:"), labelObservaciones);
        GridBagConstraints observaciones = restricciones(1, 2);
        observaciones.gridwidth = 3;
        observaciones.fill = GridBagConstraints.BOTH;
        observaciones.weightx = 1.0;
        formulario.add(new JScrollPane(txtObservaciones), observaciones);

        JPanel accionCrear = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        accionCrear.setOpaque(false);
        accionCrear.add(btnAsignar);
        GridBagConstraints accion = restricciones(0, 3);
        accion.gridwidth = 4;
        formulario.add(accionCrear, accion);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtros.setOpaque(false);
        cmbEstadoFiltro.addItem("TODOS");
        for (String estado : GestionAsignacionesController.ESTADOS) {
            cmbEstadoFiltro.addItem(estado);
        }
        filtros.add(new JLabel("Buscar:"));
        filtros.add(txtBuscar);
        filtros.add(new JLabel("Estado:"));
        filtros.add(cmbEstadoFiltro);
        filtros.add(btnActualizar);
        filtros.add(lblEstado);

        JPanel superior = new JPanel(new BorderLayout());
        superior.setOpaque(false);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(filtros, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);

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
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        acciones.setOpaque(false);
        acciones.add(btnFinalizar);
        acciones.add(btnCancelar);
        add(acciones, BorderLayout.SOUTH);
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

    private void agregarCampo(JPanel panel, int columna, int fila, String etiqueta,
                              java.awt.Component componente) {
        GridBagConstraints label = restricciones(columna, fila);
        label.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(etiqueta + ":"), label);
        GridBagConstraints campo = restricciones(columna + 1, fila);
        campo.fill = GridBagConstraints.HORIZONTAL;
        campo.weightx = 1.0;
        panel.add(componente, campo);
    }

    private GridBagConstraints restricciones(int columna, int fila) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = columna;
        constraints.gridy = fila;
        constraints.insets = new Insets(4, 6, 4, 6);
        return constraints;
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

    private static JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(COLOR_ACCION);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 14));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return boton;
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
}

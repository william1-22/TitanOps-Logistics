package com.titanops.vista;

import com.titanops.controlador.GestionControlReportesController;
import com.titanops.controlador.GestionControlReportesController.EstadoMaquinariaFila;
import com.titanops.controlador.GestionControlReportesController.MaquinariaOpcion;
import com.titanops.controlador.GestionControlReportesController.MantenimientoEdicion;
import com.titanops.controlador.GestionControlReportesController.MantenimientoFila;
import com.titanops.controlador.GestionControlReportesController.ResumenControl;
import com.titanops.controlador.GestionControlReportesController.ResultadoCreacion;
import com.titanops.controlador.GestionControlReportesController.ResultadoOperacion;
import com.titanops.dao.MantenimientoDAO;
import com.titanops.dao.MaquinariaDAO;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/** Control operativo de maquinaria y mantenimientos. */
public class Control_y_reportes extends JInternalFrame {
    private static final Color COLOR_FONDO = new Color(134, 137, 93);
    private static final Color COLOR_CABECERA = new Color(84, 88, 47);
    private static final Color COLOR_ACCION = new Color(93, 36, 23);
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JDesktopPane desktop;
    private final Integer idUsuarioSesion;
    private final GestionControlReportesController controller;
    private boolean cargando;

    public Control_y_reportes() {
        this(null, null);
    }

    public Control_y_reportes(JDesktopPane desktop) {
        this(desktop, null);
    }

    public Control_y_reportes(JDesktopPane desktop, Integer idUsuarioSesion) {
        this(desktop, idUsuarioSesion,
                new GestionControlReportesController(
                        new MantenimientoDAO(), new MaquinariaDAO()));
    }

    Control_y_reportes(JDesktopPane desktop, Integer idUsuarioSesion,
                       GestionControlReportesController controller) {
        this.desktop = desktop;
        this.idUsuarioSesion = idUsuarioSesion;
        this.controller = controller;
        initComponents();
        configurarVista();
        configurarEventos();
        cargarTodo();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        tabsControl = new javax.swing.JTabbedPane();
        panelEstado = new javax.swing.JPanel();
        panelEstadoSuperior = new javax.swing.JPanel();
        panelFiltrosEstado = new javax.swing.JPanel();
        lblBuscarEstado = new javax.swing.JLabel();
        txtBuscarEstado = new javax.swing.JTextField();
        lblFiltroEstadoMaquinaria = new javax.swing.JLabel();
        cmbEstadoMaquinaria = new javax.swing.JComboBox<>();
        btnBuscarEstado = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();
        panelResumen = new javax.swing.JPanel();
        lblDisponibles = new javax.swing.JLabel();
        lblEnRuta = new javax.swing.JLabel();
        lblMantenimiento = new javax.swing.JLabel();
        lblInactivas = new javax.swing.JLabel();
        scrollEstado = new javax.swing.JScrollPane();
        tablaEstado = new javax.swing.JTable();
        panelMantenimientos = new javax.swing.JPanel();
        panelMantenimientoSuperior = new javax.swing.JPanel();
        panelFormularioMantenimiento = new javax.swing.JPanel();
        lblMaquinaria = new javax.swing.JLabel();
        cmbMaquinaria = new javax.swing.JComboBox<>();
        lblTipo = new javax.swing.JLabel();
        cmbTipo = new javax.swing.JComboBox<>();
        lblSalidaEstimada = new javax.swing.JLabel();
        fechaSalidaEstimada = new com.toedter.calendar.JDateChooser();
        lblCosto = new javax.swing.JLabel();
        txtCosto = new javax.swing.JTextField();
        lblTaller = new javax.swing.JLabel();
        txtTaller = new javax.swing.JTextField();
        lblDiagnostico = new javax.swing.JLabel();
        scrollDiagnostico = new javax.swing.JScrollPane();
        txtDiagnostico = new javax.swing.JTextArea();
        panelAccionesCreacion = new javax.swing.JPanel();
        btnLimpiar = new javax.swing.JButton();
        btnIniciar = new javax.swing.JButton();
        lblRelleno1 = new javax.swing.JLabel();
        lblRelleno2 = new javax.swing.JLabel();
        lblRelleno3 = new javax.swing.JLabel();
        panelFiltrosMantenimiento = new javax.swing.JPanel();
        lblBuscarMantenimiento = new javax.swing.JLabel();
        txtBuscarMantenimiento = new javax.swing.JTextField();
        lblFiltroEstadoMantenimiento = new javax.swing.JLabel();
        cmbEstadoMantenimiento = new javax.swing.JComboBox<>();
        btnBuscarMantenimiento = new javax.swing.JButton();
        scrollMantenimientos = new javax.swing.JScrollPane();
        tablaMantenimientos = new javax.swing.JTable();
        panelAccionesMantenimiento = new javax.swing.JPanel();
        btnEditar = new javax.swing.JButton();
        btnFinalizar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        lblEstadoCarga = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Control operativo y reportes");
        setPreferredSize(new java.awt.Dimension(1160, 760));

        lblTitulo.setBackground(new java.awt.Color(84, 88, 47));
        lblTitulo.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 34)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("CONTROL OPERATIVO");
        lblTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 8, 18, 8));
        lblTitulo.setOpaque(true);
        getContentPane().add(lblTitulo, java.awt.BorderLayout.NORTH);

        panelEstado.setBackground(new java.awt.Color(134, 137, 93));
        panelEstado.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 14, 12, 14));
        panelEstado.setLayout(new java.awt.BorderLayout(8, 8));

        panelEstadoSuperior.setOpaque(false);
        panelEstadoSuperior.setLayout(new java.awt.BorderLayout(0, 6));

        panelFiltrosEstado.setOpaque(false);
        panelFiltrosEstado.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));

        lblBuscarEstado.setText("Buscar:");
        panelFiltrosEstado.add(lblBuscarEstado);

        txtBuscarEstado.setColumns(22);
        panelFiltrosEstado.add(txtBuscarEstado);

        lblFiltroEstadoMaquinaria.setText("Estado:");
        panelFiltrosEstado.add(lblFiltroEstadoMaquinaria);
        panelFiltrosEstado.add(cmbEstadoMaquinaria);

        btnBuscarEstado.setText("ACTUALIZAR");
        panelFiltrosEstado.add(btnBuscarEstado);

        btnExportar.setText("EXPORTAR");
        panelFiltrosEstado.add(btnExportar);

        panelEstadoSuperior.add(panelFiltrosEstado, java.awt.BorderLayout.NORTH);

        panelResumen.setOpaque(false);
        panelResumen.setLayout(new java.awt.GridLayout(1, 4, 8, 4));

        lblDisponibles.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDisponibles.setText("Disponibles: 0");
        panelResumen.add(lblDisponibles);

        lblEnRuta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEnRuta.setText("En ruta: 0");
        panelResumen.add(lblEnRuta);

        lblMantenimiento.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMantenimiento.setText("Mantenimiento: 0");
        panelResumen.add(lblMantenimiento);

        lblInactivas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInactivas.setText("Inactivas: 0");
        panelResumen.add(lblInactivas);

        panelEstadoSuperior.add(panelResumen, java.awt.BorderLayout.SOUTH);

        panelEstado.add(panelEstadoSuperior, java.awt.BorderLayout.NORTH);

        scrollEstado.setViewportView(tablaEstado);

        panelEstado.add(scrollEstado, java.awt.BorderLayout.CENTER);

        tabsControl.addTab("ESTADO DE MAQUINARIA", panelEstado);

        panelMantenimientos.setBackground(new java.awt.Color(134, 137, 93));
        panelMantenimientos.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 14, 10, 14));
        panelMantenimientos.setLayout(new java.awt.BorderLayout(8, 8));

        panelMantenimientoSuperior.setOpaque(false);
        panelMantenimientoSuperior.setLayout(new java.awt.BorderLayout(0, 6));

        panelFormularioMantenimiento.setOpaque(false);
        panelFormularioMantenimiento.setLayout(new java.awt.GridLayout(4, 4, 10, 6));

        lblMaquinaria.setText("Maquinaria:");
        panelFormularioMantenimiento.add(lblMaquinaria);
        panelFormularioMantenimiento.add(cmbMaquinaria);

        lblTipo.setText("Tipo:");
        panelFormularioMantenimiento.add(lblTipo);
        panelFormularioMantenimiento.add(cmbTipo);

        lblSalidaEstimada.setText("Salida estimada:");
        panelFormularioMantenimiento.add(lblSalidaEstimada);
        panelFormularioMantenimiento.add(fechaSalidaEstimada);

        lblCosto.setText("Costo:");
        panelFormularioMantenimiento.add(lblCosto);

        txtCosto.setColumns(12);
        panelFormularioMantenimiento.add(txtCosto);

        lblTaller.setText("Taller:");
        panelFormularioMantenimiento.add(lblTaller);

        txtTaller.setColumns(22);
        panelFormularioMantenimiento.add(txtTaller);

        lblDiagnostico.setText("Diagnóstico:");
        panelFormularioMantenimiento.add(lblDiagnostico);

        txtDiagnostico.setColumns(28);
        txtDiagnostico.setRows(3);
        scrollDiagnostico.setViewportView(txtDiagnostico);

        panelFormularioMantenimiento.add(scrollDiagnostico);

        panelAccionesCreacion.setOpaque(false);

        btnLimpiar.setText("LIMPIAR");
        panelAccionesCreacion.add(btnLimpiar);

        btnIniciar.setText("INICIAR MANTENIMIENTO");
        panelAccionesCreacion.add(btnIniciar);

        panelFormularioMantenimiento.add(panelAccionesCreacion);
        panelFormularioMantenimiento.add(lblRelleno1);
        panelFormularioMantenimiento.add(lblRelleno2);
        panelFormularioMantenimiento.add(lblRelleno3);

        panelMantenimientoSuperior.add(panelFormularioMantenimiento, java.awt.BorderLayout.CENTER);

        panelFiltrosMantenimiento.setOpaque(false);
        panelFiltrosMantenimiento.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 3));

        lblBuscarMantenimiento.setText("Buscar:");
        panelFiltrosMantenimiento.add(lblBuscarMantenimiento);

        txtBuscarMantenimiento.setColumns(20);
        panelFiltrosMantenimiento.add(txtBuscarMantenimiento);

        lblFiltroEstadoMantenimiento.setText("Estado:");
        panelFiltrosMantenimiento.add(lblFiltroEstadoMantenimiento);
        panelFiltrosMantenimiento.add(cmbEstadoMantenimiento);

        btnBuscarMantenimiento.setText("ACTUALIZAR");
        panelFiltrosMantenimiento.add(btnBuscarMantenimiento);

        panelMantenimientoSuperior.add(panelFiltrosMantenimiento, java.awt.BorderLayout.SOUTH);

        panelMantenimientos.add(panelMantenimientoSuperior, java.awt.BorderLayout.NORTH);

        scrollMantenimientos.setViewportView(tablaMantenimientos);

        panelMantenimientos.add(scrollMantenimientos, java.awt.BorderLayout.CENTER);

        panelAccionesMantenimiento.setOpaque(false);
        panelAccionesMantenimiento.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 3));

        btnEditar.setText("EDITAR");
        panelAccionesMantenimiento.add(btnEditar);

        btnFinalizar.setText("FINALIZAR");
        panelAccionesMantenimiento.add(btnFinalizar);

        btnCancelar.setText("CANCELAR");
        panelAccionesMantenimiento.add(btnCancelar);

        panelMantenimientos.add(panelAccionesMantenimiento, java.awt.BorderLayout.SOUTH);

        tabsControl.addTab("MANTENIMIENTOS", panelMantenimientos);

        getContentPane().add(tabsControl, java.awt.BorderLayout.CENTER);

        lblEstadoCarga.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 6, 12));
        lblEstadoCarga.setText(" ");
        getContentPane().add(lblEstadoCarga, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void configurarVista() {
        for (String estado : GestionControlReportesController.ESTADOS_MAQUINARIA) {
            cmbEstadoMaquinaria.addItem(estado);
        }
        tablaEstado.setModel(new DefaultTableModel(new Object[]{"ID", "CÓDIGO",
            "DESCRIPCIÓN", "ESTADO", "REGISTRO"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tablaEstado.setAutoCreateRowSorter(true);
        tablaEstado.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        for (String tipo : GestionControlReportesController.TIPOS_MANTENIMIENTO) {
            cmbTipo.addItem(tipo);
        }
        fechaSalidaEstimada.setDateFormatString("dd/MM/yyyy HH:mm");
        fechaSalidaEstimada.setDate(
                new Date(System.currentTimeMillis() + 86_400_000L));
        txtDiagnostico.setLineWrap(true);
        txtDiagnostico.setWrapStyleWord(true);

        for (String estado : GestionControlReportesController.ESTADOS_MANTENIMIENTO) {
            cmbEstadoMantenimiento.addItem(estado);
        }
        tablaMantenimientos.setModel(new DefaultTableModel(new Object[]{"ID",
            "MAQUINARIA", "DESCRIPCIÓN", "TIPO", "INGRESO", "SALIDA EST.",
            "SALIDA REAL", "COSTO", "ESTADO", "TALLER", "DIAGNÓSTICO"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tablaMantenimientos.setAutoCreateRowSorter(true);
        tablaMantenimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configurarBoton(btnBuscarEstado);
        configurarBoton(btnExportar);
        configurarBoton(btnIniciar);
        configurarBoton(btnLimpiar);
        configurarBoton(btnBuscarMantenimiento);
        configurarBoton(btnEditar);
        configurarBoton(btnFinalizar);
        configurarBoton(btnCancelar);
        configurarIndicador(lblDisponibles);
        configurarIndicador(lblEnRuta);
        configurarIndicador(lblMantenimiento);
        configurarIndicador(lblInactivas);
        actualizarBotonesSeleccion();
    }

    private void configurarEventos() {
        btnBuscarEstado.addActionListener(event -> recargarEstadoMaquinaria());
        txtBuscarEstado.addActionListener(event -> recargarEstadoMaquinaria());
        cmbEstadoMaquinaria.addActionListener(event -> {
            if (!cargando) {
                recargarEstadoMaquinaria();
            }
        });
        btnExportar.addActionListener(event -> abrirExportacion());
        btnIniciar.addActionListener(event -> iniciarMantenimiento());
        btnLimpiar.addActionListener(event -> limpiarFormulario());
        btnBuscarMantenimiento.addActionListener(event -> recargarMantenimientos());
        txtBuscarMantenimiento.addActionListener(event -> recargarMantenimientos());
        cmbEstadoMantenimiento.addActionListener(event -> {
            if (!cargando) {
                recargarMantenimientos();
            }
        });
        tablaMantenimientos.getSelectionModel().addListSelectionListener(
                event -> actualizarBotonesSeleccion());
        btnEditar.addActionListener(event -> cargarEdicion());
        btnFinalizar.addActionListener(event -> cerrarMantenimiento(true));
        btnCancelar.addActionListener(event -> cerrarMantenimiento(false));
    }

    public final void cargarTodo() {
        if (cargando) {
            return;
        }
        cambiarEstadoCarga(true, "Cargando control operativo...");
        String filtroEstado = txtBuscarEstado.getText();
        String estadoMaquinaria = (String) cmbEstadoMaquinaria.getSelectedItem();
        String filtroMantenimiento = txtBuscarMantenimiento.getText();
        String estadoMantenimiento =
                (String) cmbEstadoMantenimiento.getSelectedItem();
        new SwingWorker<DatosControl, Void>() {
            @Override
            protected DatosControl doInBackground() {
                return new DatosControl(controller.listarMaquinariaDisponible(),
                        controller.listarEstadoMaquinaria(
                                filtroEstado, estadoMaquinaria),
                        controller.obtenerResumenControl(),
                        controller.listarMantenimientos(
                                filtroMantenimiento, estadoMantenimiento));
            }

            @Override
            protected void done() {
                try {
                    DatosControl datos = get();
                    llenarMaquinariaDisponible(datos.maquinariaDisponible());
                    llenarTablaEstado(datos.estadoMaquinaria());
                    mostrarResumen(datos.resumen());
                    llenarTablaMantenimientos(datos.mantenimientos());
                    cambiarEstadoCarga(false, "Control operativo actualizado.");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    cambiarEstadoCarga(false, "Carga interrumpida.");
                } catch (ExecutionException exception) {
                    cambiarEstadoCarga(false,
                            "No fue posible cargar el control operativo.");
                }
            }
        }.execute();
    }

    private void recargarEstadoMaquinaria() {
        if (cargando) {
            return;
        }
        String filtro = txtBuscarEstado.getText();
        String estado = (String) cmbEstadoMaquinaria.getSelectedItem();
        cambiarEstadoCarga(true, "Actualizando estado de maquinaria...");
        new SwingWorker<DatosEstado, Void>() {
            @Override
            protected DatosEstado doInBackground() {
                return new DatosEstado(controller.listarEstadoMaquinaria(filtro, estado),
                        controller.obtenerResumenControl());
            }

            @Override
            protected void done() {
                try {
                    DatosEstado datos = get();
                    llenarTablaEstado(datos.filas());
                    mostrarResumen(datos.resumen());
                    cambiarEstadoCarga(false,
                            datos.filas().size() + " maquinaria(s) mostrada(s).");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    cambiarEstadoCarga(false, "Carga interrumpida.");
                } catch (ExecutionException exception) {
                    cambiarEstadoCarga(false,
                            "No fue posible actualizar la maquinaria.");
                }
            }
        }.execute();
    }

    private void recargarMantenimientos() {
        if (cargando) {
            return;
        }
        String filtro = txtBuscarMantenimiento.getText();
        String estado = (String) cmbEstadoMantenimiento.getSelectedItem();
        cambiarEstadoCarga(true, "Actualizando mantenimientos...");
        new SwingWorker<List<MantenimientoFila>, Void>() {
            @Override
            protected List<MantenimientoFila> doInBackground() {
                return controller.listarMantenimientos(filtro, estado);
            }

            @Override
            protected void done() {
                try {
                    List<MantenimientoFila> filas = get();
                    llenarTablaMantenimientos(filas);
                    cambiarEstadoCarga(false,
                            filas.size() + " mantenimiento(s) mostrado(s).");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    cambiarEstadoCarga(false, "Carga interrumpida.");
                } catch (ExecutionException exception) {
                    cambiarEstadoCarga(false,
                            "No fue posible actualizar los mantenimientos.");
                }
            }
        }.execute();
    }

    private void iniciarMantenimiento() {
        if (cargando) {
            return;
        }
        MaquinariaOpcion maquinaria =
                (MaquinariaOpcion) cmbMaquinaria.getSelectedItem();
        String tipo = (String) cmbTipo.getSelectedItem();
        Timestamp salida = aTimestamp(fechaSalidaEstimada.getDate());
        String diagnostico = txtDiagnostico.getText();
        String costo = txtCosto.getText();
        String taller = txtTaller.getText();
        cambiarEstadoCarga(true, "Iniciando mantenimiento...");
        new SwingWorker<ResultadoCreacion, Void>() {
            @Override
            protected ResultadoCreacion doInBackground() {
                return controller.crearMantenimiento(idUsuarioSesion, maquinaria,
                        tipo, salida, diagnostico, costo, taller);
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
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(Control_y_reportes.this,
                            resultado.mensaje(), "Mantenimientos",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarTodo();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    mostrarError("Se interrumpió el inicio del mantenimiento.");
                } catch (ExecutionException exception) {
                    mostrarError("No fue posible iniciar el mantenimiento.");
                }
            }
        }.execute();
    }

    private void cargarEdicion() {
        Integer idMantenimiento = idMantenimientoSeleccionado();
        if (idMantenimiento == null || cargando) {
            return;
        }
        cambiarEstadoCarga(true, "Cargando mantenimiento...");
        new SwingWorker<Optional<MantenimientoEdicion>, Void>() {
            @Override
            protected Optional<MantenimientoEdicion> doInBackground() {
                return controller.obtenerMantenimientoEdicion(idMantenimiento);
            }

            @Override
            protected void done() {
                cambiarEstadoCarga(false, "Listo.");
                try {
                    Optional<MantenimientoEdicion> mantenimiento = get();
                    if (mantenimiento.isEmpty()) {
                        mostrarValidacion(
                                "El mantenimiento seleccionado ya no existe.");
                        cargarTodo();
                        return;
                    }
                    if (!"EN_PROCESO".equals(
                            mantenimiento.get().estadoMantenimiento())) {
                        mostrarValidacion(
                                "Solo se pueden editar mantenimientos en proceso.");
                        return;
                    }
                    mostrarFormularioEdicion(mantenimiento.get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    mostrarError("Se interrumpió la carga del mantenimiento.");
                } catch (ExecutionException exception) {
                    mostrarError("No fue posible cargar el mantenimiento.");
                }
            }
        }.execute();
    }

    private void mostrarFormularioEdicion(MantenimientoEdicion mantenimiento) {
        JComboBox<String> tipo = new JComboBox<>(
                GestionControlReportesController.TIPOS_MANTENIMIENTO
                        .toArray(String[]::new));
        tipo.setSelectedItem(mantenimiento.tipoMantenimiento());
        JDateChooser salida = new JDateChooser();
        salida.setDateFormatString("dd/MM/yyyy HH:mm");
        salida.setDate(desdeTimestamp(mantenimiento.fechaSalidaEstimada()));
        JTextArea diagnostico = new JTextArea(
                valorVisible(mantenimiento.diagnostico()), 4, 28);
        diagnostico.setLineWrap(true);
        diagnostico.setWrapStyleWord(true);
        JTextField costo = new JTextField(
                decimalVisible(mantenimiento.costo()), 14);
        JTextField taller = new JTextField(
                valorVisible(mantenimiento.tallerResponsable()), 24);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        agregarCampo(formulario, 0, 0, "Tipo", tipo);
        agregarCampo(formulario, 0, 1, "Salida estimada", salida);
        agregarCampo(formulario, 0, 2, "Costo", costo);
        agregarCampo(formulario, 0, 3, "Taller", taller);
        GridBagConstraints labelDiagnostico = restricciones(0, 4);
        labelDiagnostico.anchor = GridBagConstraints.FIRST_LINE_END;
        formulario.add(new JLabel("Diagnóstico:"), labelDiagnostico);
        GridBagConstraints campoDiagnostico = restricciones(1, 4);
        campoDiagnostico.fill = GridBagConstraints.BOTH;
        formulario.add(new JScrollPane(diagnostico), campoDiagnostico);

        int respuesta = JOptionPane.showConfirmDialog(this, formulario,
                "Editar mantenimiento", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (respuesta == JOptionPane.OK_OPTION) {
            actualizarMantenimiento(mantenimiento.idMantenimiento(),
                    (String) tipo.getSelectedItem(), aTimestamp(salida.getDate()),
                    diagnostico.getText(), costo.getText(), taller.getText());
        }
    }

    private void actualizarMantenimiento(int idMantenimiento, String tipo,
                                         Timestamp salida, String diagnostico,
                                         String costo, String taller) {
        cambiarEstadoCarga(true, "Actualizando mantenimiento...");
        new SwingWorker<ResultadoOperacion, Void>() {
            @Override
            protected ResultadoOperacion doInBackground() {
                return controller.actualizarMantenimiento(idMantenimiento, tipo,
                        salida, diagnostico, costo, taller);
            }

            @Override
            protected void done() {
                procesarOperacion(this, "actualizar el mantenimiento");
            }
        }.execute();
    }

    private void cerrarMantenimiento(boolean finalizar) {
        Integer idMantenimiento = idMantenimientoSeleccionado();
        if (idMantenimiento == null || cargando) {
            return;
        }
        String accion = finalizar ? "finalizar" : "cancelar";
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Deseas " + accion + " el mantenimiento seleccionado?\n"
                        + "La maquinaria volverá a estar disponible.",
                finalizar ? "Finalizar mantenimiento" : "Cancelar mantenimiento",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        cambiarEstadoCarga(true, "Procesando mantenimiento...");
        new SwingWorker<ResultadoOperacion, Void>() {
            @Override
            protected ResultadoOperacion doInBackground() {
                return finalizar
                        ? controller.finalizarMantenimiento(idMantenimiento)
                        : controller.cancelarMantenimiento(idMantenimiento);
            }

            @Override
            protected void done() {
                procesarOperacion(this, accion + " el mantenimiento");
            }
        }.execute();
    }

    private void procesarOperacion(SwingWorker<ResultadoOperacion, Void> worker,
                                   String accion) {
        cambiarEstadoCarga(false, "Listo.");
        try {
            ResultadoOperacion resultado = worker.get();
            if (!resultado.exitoso()) {
                mostrarValidacion(resultado.mensaje());
                return;
            }
            JOptionPane.showMessageDialog(this, resultado.mensaje(),
                    "Mantenimientos", JOptionPane.INFORMATION_MESSAGE);
            cargarTodo();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            mostrarError("Se interrumpió la operación.");
        } catch (ExecutionException exception) {
            mostrarError("No fue posible " + accion + ".");
        }
    }

    private void abrirExportacion() {
        if (desktop == null) {
            mostrarValidacion(
                    "La exportación se completará en el siguiente bloque 5D.");
            return;
        }
        tipo_exportar ventana = new tipo_exportar();
        desktop.add(ventana);
        ventana.setSize(900, 550);
        ventana.setLocation(20, 20);
        ventana.setVisible(true);
        ventana.toFront();
    }

    private void llenarMaquinariaDisponible(List<MaquinariaOpcion> maquinaria) {
        cmbMaquinaria.removeAllItems();
        for (MaquinariaOpcion opcion : maquinaria) {
            cmbMaquinaria.addItem(opcion);
        }
    }

    private void llenarTablaEstado(List<EstadoMaquinariaFila> filas) {
        DefaultTableModel modelo = (DefaultTableModel) tablaEstado.getModel();
        modelo.setRowCount(0);
        for (EstadoMaquinariaFila fila : filas) {
            modelo.addRow(new Object[]{fila.idMaquinaria(), fila.codigoInventario(),
                fila.descripcion(), fila.estado(), fechaVisible(fila.fechaRegistro())});
        }
    }

    private void mostrarResumen(ResumenControl resumen) {
        lblDisponibles.setText("Disponibles: " + resumen.disponibles());
        lblEnRuta.setText("En ruta: " + resumen.enRuta());
        lblMantenimiento.setText("Mantenimiento: " + resumen.mantenimiento());
        lblInactivas.setText("Inactivas: " + resumen.inactivas());
    }

    private void llenarTablaMantenimientos(List<MantenimientoFila> filas) {
        DefaultTableModel modelo =
                (DefaultTableModel) tablaMantenimientos.getModel();
        tablaMantenimientos.clearSelection();
        modelo.setRowCount(0);
        for (MantenimientoFila fila : filas) {
            modelo.addRow(new Object[]{fila.idMantenimiento(),
                fila.codigoMaquinaria(), fila.descripcionMaquinaria(),
                fila.tipoMantenimiento(), fechaVisible(fila.fechaIngreso()),
                fechaVisible(fila.fechaSalidaEstimada()),
                fechaVisible(fila.fechaSalidaReal()), decimalVisible(fila.costo()),
                fila.estadoMantenimiento(), valorVisible(fila.tallerResponsable()),
                valorVisible(fila.diagnostico())});
        }
        actualizarBotonesSeleccion();
    }

    private Integer idMantenimientoSeleccionado() {
        int fila = tablaMantenimientos.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int filaModelo = tablaMantenimientos.convertRowIndexToModel(fila);
        return (Integer) tablaMantenimientos.getModel()
                .getValueAt(filaModelo, 0);
    }

    private boolean mantenimientoSeleccionadoEnProceso() {
        int fila = tablaMantenimientos.getSelectedRow();
        if (fila < 0) {
            return false;
        }
        int filaModelo = tablaMantenimientos.convertRowIndexToModel(fila);
        return "EN_PROCESO".equals(tablaMantenimientos.getModel()
                .getValueAt(filaModelo, 8));
    }

    private void cambiarEstadoCarga(boolean ocupado, String mensaje) {
        cargando = ocupado;
        txtBuscarEstado.setEnabled(!ocupado);
        cmbEstadoMaquinaria.setEnabled(!ocupado);
        btnBuscarEstado.setEnabled(!ocupado);
        btnExportar.setEnabled(!ocupado);
        tablaEstado.setEnabled(!ocupado);
        cmbMaquinaria.setEnabled(!ocupado);
        cmbTipo.setEnabled(!ocupado);
        fechaSalidaEstimada.setEnabled(!ocupado);
        txtDiagnostico.setEnabled(!ocupado);
        txtCosto.setEnabled(!ocupado);
        txtTaller.setEnabled(!ocupado);
        btnIniciar.setEnabled(!ocupado && idUsuarioSesion != null
                && cmbMaquinaria.getItemCount() > 0);
        btnLimpiar.setEnabled(!ocupado);
        txtBuscarMantenimiento.setEnabled(!ocupado);
        cmbEstadoMantenimiento.setEnabled(!ocupado);
        btnBuscarMantenimiento.setEnabled(!ocupado);
        tablaMantenimientos.setEnabled(!ocupado);
        lblEstadoCarga.setText(mensaje);
        actualizarBotonesSeleccion();
    }

    private void actualizarBotonesSeleccion() {
        boolean seleccion = !cargando && mantenimientoSeleccionadoEnProceso();
        btnEditar.setEnabled(seleccion);
        btnFinalizar.setEnabled(seleccion);
        btnCancelar.setEnabled(seleccion);
    }

    private void limpiarFormulario() {
        txtDiagnostico.setText("");
        txtCosto.setText("");
        txtTaller.setText("");
        fechaSalidaEstimada.setDate(
                new Date(System.currentTimeMillis() + 86_400_000L));
        if (cmbTipo.getItemCount() > 0) {
            cmbTipo.setSelectedIndex(0);
        }
        if (cmbMaquinaria.getItemCount() > 0) {
            cmbMaquinaria.setSelectedIndex(0);
        }
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

    private static void configurarBoton(JButton boton) {
        boton.setBackground(COLOR_ACCION);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 13));
        boton.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
    }

    private static void configurarIndicador(JLabel label) {
        label.setOpaque(true);
        label.setBackground(new Color(168, 171, 143));
        label.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private Timestamp aTimestamp(Date fecha) {
        return fecha == null ? null : new Timestamp(fecha.getTime());
    }

    private Date desdeTimestamp(Timestamp fecha) {
        return fecha == null ? null : new Date(fecha.getTime());
    }

    private String fechaVisible(Timestamp fecha) {
        return fecha == null ? ""
                : fecha.toLocalDateTime().format(FORMATO_FECHA);
    }

    private String decimalVisible(BigDecimal valor) {
        return valor == null ? "" : valor.stripTrailingZeros().toPlainString();
    }

    private String valorVisible(String valor) {
        return valor == null ? "" : valor;
    }

    private void mostrarValidacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación",
                JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Control operativo",
                JOptionPane.ERROR_MESSAGE);
    }

    private record DatosControl(List<MaquinariaOpcion> maquinariaDisponible,
                                List<EstadoMaquinariaFila> estadoMaquinaria,
                                ResumenControl resumen,
                                List<MantenimientoFila> mantenimientos) {}

    private record DatosEstado(List<EstadoMaquinariaFila> filas,
                               ResumenControl resumen) {}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarEstado;
    private javax.swing.JButton btnBuscarMantenimiento;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnFinalizar;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbEstadoMantenimiento;
    private javax.swing.JComboBox<String> cmbEstadoMaquinaria;
    private javax.swing.JComboBox<MaquinariaOpcion> cmbMaquinaria;
    private javax.swing.JComboBox<String> cmbTipo;
    private com.toedter.calendar.JDateChooser fechaSalidaEstimada;
    private javax.swing.JLabel lblBuscarEstado;
    private javax.swing.JLabel lblBuscarMantenimiento;
    private javax.swing.JLabel lblCosto;
    private javax.swing.JLabel lblDiagnostico;
    private javax.swing.JLabel lblDisponibles;
    private javax.swing.JLabel lblEnRuta;
    private javax.swing.JLabel lblEstadoCarga;
    private javax.swing.JLabel lblFiltroEstadoMantenimiento;
    private javax.swing.JLabel lblFiltroEstadoMaquinaria;
    private javax.swing.JLabel lblInactivas;
    private javax.swing.JLabel lblMantenimiento;
    private javax.swing.JLabel lblMaquinaria;
    private javax.swing.JLabel lblRelleno1;
    private javax.swing.JLabel lblRelleno2;
    private javax.swing.JLabel lblRelleno3;
    private javax.swing.JLabel lblSalidaEstimada;
    private javax.swing.JLabel lblTaller;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelAccionesCreacion;
    private javax.swing.JPanel panelAccionesMantenimiento;
    private javax.swing.JPanel panelEstado;
    private javax.swing.JPanel panelEstadoSuperior;
    private javax.swing.JPanel panelFiltrosEstado;
    private javax.swing.JPanel panelFiltrosMantenimiento;
    private javax.swing.JPanel panelFormularioMantenimiento;
    private javax.swing.JPanel panelMantenimientoSuperior;
    private javax.swing.JPanel panelMantenimientos;
    private javax.swing.JPanel panelResumen;
    private javax.swing.JScrollPane scrollDiagnostico;
    private javax.swing.JScrollPane scrollEstado;
    private javax.swing.JScrollPane scrollMantenimientos;
    private javax.swing.JTable tablaEstado;
    private javax.swing.JTable tablaMantenimientos;
    private javax.swing.JTabbedPane tabsControl;
    private javax.swing.JTextField txtBuscarEstado;
    private javax.swing.JTextField txtBuscarMantenimiento;
    private javax.swing.JTextField txtCosto;
    private javax.swing.JTextArea txtDiagnostico;
    private javax.swing.JTextField txtTaller;
    // End of variables declaration//GEN-END:variables
}

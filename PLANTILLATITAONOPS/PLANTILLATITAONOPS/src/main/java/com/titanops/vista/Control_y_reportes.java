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
import com.titanops.reporte.ReporteEstadoMaquinaria;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
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
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JDesktopPane desktop;
    private final Integer idUsuarioSesion;
    private final GestionControlReportesController controller;
    private boolean cargando;
    private List<EstadoMaquinariaFila> estadoMaquinariaActual = List.of();
    private ResumenControl resumenActual = new ResumenControl(0, 0, 0, 0);

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
        getContentPane().setLayout(new BorderLayout());
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
        txtBuscarEstado = new com.titanops.vista.componentes.CampoTexto();
        lblFiltroEstadoMaquinaria = new javax.swing.JLabel();
        cmbEstadoMaquinaria = new com.titanops.vista.componentes.Selector<>();
        btnBuscarEstado = new com.titanops.vista.componentes.Boton();
        btnExportar = new com.titanops.vista.componentes.Boton();
        panelResumen = new javax.swing.JPanel();
        lblDisponibles = new javax.swing.JLabel();
        lblEnRuta = new javax.swing.JLabel();
        lblMantenimiento = new javax.swing.JLabel();
        lblInactivas = new javax.swing.JLabel();
        scrollEstado = new javax.swing.JScrollPane();
        tablaEstado = new com.titanops.vista.componentes.Tabla();
        panelMantenimientos = new javax.swing.JPanel();
        panelMantenimientoSuperior = new javax.swing.JPanel();
        panelFormularioMantenimiento = new javax.swing.JPanel();
        lblMaquinaria = new javax.swing.JLabel();
        cmbMaquinaria = new com.titanops.vista.componentes.Selector<>();
        lblTipo = new javax.swing.JLabel();
        cmbTipo = new com.titanops.vista.componentes.Selector<>();
        lblSalidaEstimada = new javax.swing.JLabel();
        fechaSalidaEstimada = new com.toedter.calendar.JDateChooser();
        lblCosto = new javax.swing.JLabel();
        txtCosto = new com.titanops.vista.componentes.CampoTexto();
        lblTaller = new javax.swing.JLabel();
        txtTaller = new com.titanops.vista.componentes.CampoTexto();
        lblDiagnostico = new javax.swing.JLabel();
        scrollDiagnostico = new javax.swing.JScrollPane();
        txtDiagnostico = new com.titanops.vista.componentes.AreaTexto();
        panelAccionesCreacion = new javax.swing.JPanel();
        btnLimpiar = new com.titanops.vista.componentes.Boton();
        btnIniciar = new com.titanops.vista.componentes.Boton();
        lblRelleno1 = new javax.swing.JLabel();
        lblRelleno2 = new javax.swing.JLabel();
        lblRelleno3 = new javax.swing.JLabel();
        panelFiltrosMantenimiento = new javax.swing.JPanel();
        lblBuscarMantenimiento = new javax.swing.JLabel();
        txtBuscarMantenimiento = new com.titanops.vista.componentes.CampoTexto();
        lblFiltroEstadoMantenimiento = new javax.swing.JLabel();
        cmbEstadoMantenimiento = new com.titanops.vista.componentes.Selector<>();
        btnBuscarMantenimiento = new com.titanops.vista.componentes.Boton();
        scrollMantenimientos = new javax.swing.JScrollPane();
        tablaMantenimientos = new com.titanops.vista.componentes.Tabla();
        panelAccionesMantenimiento = new javax.swing.JPanel();
        btnEditar = new com.titanops.vista.componentes.Boton();
        btnFinalizar = new com.titanops.vista.componentes.Boton();
        btnCancelar = new com.titanops.vista.componentes.Boton();
        lblEstadoCarga = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Control operativo y reportes");
        setPreferredSize(new java.awt.Dimension(1160, 760));

        lblTitulo.setText("CONTROL OPERATIVO");
        lblTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 8, 18, 8));
        getContentPane().add(lblTitulo, java.awt.BorderLayout.NORTH);

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

        panelMantenimientos.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 14, 10, 14));
        panelMantenimientos.setLayout(new java.awt.BorderLayout(8, 8));

        panelMantenimientoSuperior.setOpaque(false);
        panelMantenimientoSuperior.setLayout(new java.awt.BorderLayout(0, 6));

        panelFormularioMantenimiento.setOpaque(false);

        lblMaquinaria.setText("Maquinaria:");

        lblTipo.setText("Tipo:");

        lblSalidaEstimada.setText("Salida estimada:");

        lblCosto.setText("Costo:");

        txtCosto.setColumns(12);

        lblTaller.setText("Taller:");

        txtTaller.setColumns(22);

        lblDiagnostico.setText("Diagnóstico:");

        txtDiagnostico.setColumns(28);
        txtDiagnostico.setRows(3);
        scrollDiagnostico.setViewportView(txtDiagnostico);

        panelAccionesCreacion.setOpaque(false);

        btnLimpiar.setText("LIMPIAR");
        panelAccionesCreacion.add(btnLimpiar);

        btnIniciar.setText("INICIAR MANTENIMIENTO");
        panelAccionesCreacion.add(btnIniciar);

        javax.swing.GroupLayout panelFormularioMantenimientoLayout = new javax.swing.GroupLayout(panelFormularioMantenimiento);
        panelFormularioMantenimiento.setLayout(panelFormularioMantenimientoLayout);
        panelFormularioMantenimientoLayout.setHorizontalGroup(
            panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                        .addComponent(panelAccionesCreacion, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRelleno1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblRelleno2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lblRelleno3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                        .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblMaquinaria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblTaller, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lblSalidaEstimada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbMaquinaria, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(fechaSalidaEstimada, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
                                .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(lblCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblTipo))))
                            .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(txtTaller, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblDiagnostico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrollDiagnostico, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(cmbTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCosto, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))))
                .addGap(1, 1, 1))
        );
        panelFormularioMantenimientoLayout.setVerticalGroup(
            panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblMaquinaria, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbMaquinaria, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormularioMantenimientoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSalidaEstimada, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFormularioMantenimientoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechaSalidaEstimada, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTaller, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTaller, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblDiagnostico, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrollDiagnostico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(panelFormularioMantenimientoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRelleno1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRelleno2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRelleno3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelAccionesCreacion, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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


        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        tabsControl.setForeground(new java.awt.Color(30, 41, 59));
        panelEstado.setForeground(new java.awt.Color(30, 41, 59));
        panelEstado.setBackground(new java.awt.Color(248, 250, 252));
        panelEstadoSuperior.setForeground(new java.awt.Color(30, 41, 59));
        panelEstadoSuperior.setBackground(new java.awt.Color(248, 250, 252));
        panelFiltrosEstado.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltrosEstado.setBackground(new java.awt.Color(248, 250, 252));
        panelResumen.setForeground(new java.awt.Color(30, 41, 59));
        panelResumen.setBackground(new java.awt.Color(248, 250, 252));
        scrollEstado.setForeground(new java.awt.Color(30, 41, 59));
        scrollEstado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        scrollEstado.setBackground(new java.awt.Color(255, 255, 255));
        panelMantenimientos.setForeground(new java.awt.Color(30, 41, 59));
        panelMantenimientos.setBackground(new java.awt.Color(248, 250, 252));
        panelMantenimientoSuperior.setForeground(new java.awt.Color(30, 41, 59));
        panelMantenimientoSuperior.setBackground(new java.awt.Color(248, 250, 252));
        panelFormularioMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        panelFormularioMantenimiento.setBackground(new java.awt.Color(248, 250, 252));
        scrollDiagnostico.setForeground(new java.awt.Color(30, 41, 59));
        scrollDiagnostico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        scrollDiagnostico.setBackground(new java.awt.Color(255, 255, 255));
        panelAccionesCreacion.setForeground(new java.awt.Color(30, 41, 59));
        panelAccionesCreacion.setBackground(new java.awt.Color(248, 250, 252));
        panelFiltrosMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltrosMantenimiento.setBackground(new java.awt.Color(248, 250, 252));
        scrollMantenimientos.setForeground(new java.awt.Color(30, 41, 59));
        scrollMantenimientos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        scrollMantenimientos.setBackground(new java.awt.Color(255, 255, 255));
        panelAccionesMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        panelAccionesMantenimiento.setBackground(new java.awt.Color(248, 250, 252));
        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 26));
        lblTitulo.setHorizontalAlignment(0);
        lblTitulo.setBackground(new java.awt.Color(30, 64, 175));
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setOpaque(true);
        lblBuscarEstado.setForeground(new java.awt.Color(30, 41, 59));
        lblFiltroEstadoMaquinaria.setForeground(new java.awt.Color(30, 41, 59));
        lblDisponibles.setForeground(new java.awt.Color(30, 41, 59));
        lblEnRuta.setForeground(new java.awt.Color(30, 41, 59));
        lblMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        lblInactivas.setForeground(new java.awt.Color(30, 41, 59));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        lblMaquinaria.setForeground(new java.awt.Color(30, 41, 59));
        lblTipo.setForeground(new java.awt.Color(30, 41, 59));
        lblSalidaEstimada.setForeground(new java.awt.Color(30, 41, 59));
        lblCosto.setForeground(new java.awt.Color(30, 41, 59));
        lblTaller.setForeground(new java.awt.Color(30, 41, 59));
        lblDiagnostico.setForeground(new java.awt.Color(30, 41, 59));
        lblRelleno1.setForeground(new java.awt.Color(30, 41, 59));
        lblRelleno2.setForeground(new java.awt.Color(30, 41, 59));
        lblRelleno3.setForeground(new java.awt.Color(30, 41, 59));
        lblBuscarMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        lblFiltroEstadoMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        lblEstadoCarga.setForeground(new java.awt.Color(30, 41, 59));


        lblTitulo.setText("CONTROL OPERATIVO");
        lblTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 8, 18, 8));
        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 26));
        lblTitulo.setBackground(new java.awt.Color(30, 64, 175));
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setOpaque(true);
        tabsControl.setForeground(new java.awt.Color(30, 41, 59));
        panelEstado.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 14, 12, 14));
        panelEstado.setForeground(new java.awt.Color(30, 41, 59));
        panelEstado.setBackground(new java.awt.Color(248, 250, 252));
        panelEstadoSuperior.setOpaque(false);
        panelEstadoSuperior.setForeground(new java.awt.Color(30, 41, 59));
        panelEstadoSuperior.setBackground(new java.awt.Color(248, 250, 252));
        panelFiltrosEstado.setOpaque(false);
        panelFiltrosEstado.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltrosEstado.setBackground(new java.awt.Color(248, 250, 252));
        lblBuscarEstado.setText("Buscar:");
        lblBuscarEstado.setForeground(new java.awt.Color(30, 41, 59));
        txtBuscarEstado.setBackground(new java.awt.Color(255, 255, 255));
        txtBuscarEstado.setForeground(new java.awt.Color(30, 41, 59));
        txtBuscarEstado.setColorFoco(new java.awt.Color(37, 99, 235));
        txtBuscarEstado.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtBuscarEstado.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtBuscarEstado.setPreferredSize(new java.awt.Dimension(200, 40));
        txtBuscarEstado.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtBuscarEstado.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtBuscarEstado.setCaretColor(new java.awt.Color(37, 99, 235));
        txtBuscarEstado.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtBuscarEstado.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        lblFiltroEstadoMaquinaria.setText("Estado:");
        lblFiltroEstadoMaquinaria.setForeground(new java.awt.Color(30, 41, 59));
        cmbEstadoMaquinaria.setBackground(new java.awt.Color(255, 255, 255));
        cmbEstadoMaquinaria.setForeground(new java.awt.Color(30, 41, 59));
        cmbEstadoMaquinaria.setColorFoco(new java.awt.Color(37, 99, 235));
        cmbEstadoMaquinaria.setFont(new java.awt.Font("SansSerif", 0, 13));
        cmbEstadoMaquinaria.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        cmbEstadoMaquinaria.setPreferredSize(new java.awt.Dimension(180, 30));
        btnBuscarEstado.setText("ACTUALIZAR");
        btnBuscarEstado.setBackground(new java.awt.Color(239, 246, 255));
        btnBuscarEstado.setForeground(new java.awt.Color(30, 64, 175));
        btnBuscarEstado.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnBuscarEstado.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnBuscarEstado.setColorFoco(new java.awt.Color(37, 99, 235));
        btnBuscarEstado.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnBuscarEstado.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnBuscarEstado.setRolloverEnabled(true);
        btnBuscarEstado.setContentAreaFilled(false);
        btnBuscarEstado.setOpaque(false);
        btnExportar.setText("EXPORTAR");
        btnExportar.setBackground(new java.awt.Color(239, 246, 255));
        btnExportar.setForeground(new java.awt.Color(30, 64, 175));
        btnExportar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnExportar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnExportar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnExportar.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnExportar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnExportar.setRolloverEnabled(true);
        btnExportar.setContentAreaFilled(false);
        btnExportar.setOpaque(false);
        panelResumen.setOpaque(false);
        panelResumen.setForeground(new java.awt.Color(30, 41, 59));
        panelResumen.setBackground(new java.awt.Color(248, 250, 252));
        lblDisponibles.setText("Disponibles: 0");
        lblDisponibles.setForeground(new java.awt.Color(30, 41, 59));
        lblEnRuta.setText("En ruta: 0");
        lblEnRuta.setForeground(new java.awt.Color(30, 41, 59));
        lblMantenimiento.setText("Mantenimiento: 0");
        lblMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        lblInactivas.setText("Inactivas: 0");
        lblInactivas.setForeground(new java.awt.Color(30, 41, 59));
        scrollEstado.setForeground(new java.awt.Color(30, 41, 59));
        scrollEstado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        scrollEstado.setBackground(new java.awt.Color(255, 255, 255));
        tablaEstado.setBackground(new java.awt.Color(255, 255, 255));
        tablaEstado.setForeground(new java.awt.Color(30, 41, 59));
        tablaEstado.setGridColor(new java.awt.Color(125, 211, 252));
        tablaEstado.setSelectionBackground(new java.awt.Color(219, 234, 254));
        tablaEstado.setSelectionForeground(new java.awt.Color(30, 64, 175));
        tablaEstado.setFondoCabecera(new java.awt.Color(224, 242, 254));
        tablaEstado.setTextoCabecera(new java.awt.Color(30, 64, 175));
        tablaEstado.setBordeCabecera(new java.awt.Color(125, 211, 252));
        tablaEstado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        tablaEstado.setFont(new java.awt.Font("SansSerif", 0, 13));
        tablaEstado.setRowHeight(30);
        tablaEstado.setShowVerticalLines(false);
        tablaEstado.setFillsViewportHeight(true);
        panelMantenimientos.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 14, 10, 14));
        panelMantenimientos.setForeground(new java.awt.Color(30, 41, 59));
        panelMantenimientos.setBackground(new java.awt.Color(248, 250, 252));
        panelMantenimientoSuperior.setOpaque(false);
        panelMantenimientoSuperior.setForeground(new java.awt.Color(30, 41, 59));
        panelMantenimientoSuperior.setBackground(new java.awt.Color(248, 250, 252));
        panelFormularioMantenimiento.setOpaque(false);
        panelFormularioMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        panelFormularioMantenimiento.setBackground(new java.awt.Color(248, 250, 252));
        lblMaquinaria.setText("Maquinaria:");
        lblMaquinaria.setForeground(new java.awt.Color(30, 41, 59));
        cmbMaquinaria.setBackground(new java.awt.Color(255, 255, 255));
        cmbMaquinaria.setForeground(new java.awt.Color(30, 41, 59));
        cmbMaquinaria.setColorFoco(new java.awt.Color(37, 99, 235));
        cmbMaquinaria.setFont(new java.awt.Font("SansSerif", 0, 13));
        cmbMaquinaria.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        cmbMaquinaria.setPreferredSize(new java.awt.Dimension(180, 30));
        lblTipo.setText("Tipo:");
        lblTipo.setForeground(new java.awt.Color(30, 41, 59));
        cmbTipo.setBackground(new java.awt.Color(255, 255, 255));
        cmbTipo.setForeground(new java.awt.Color(30, 41, 59));
        cmbTipo.setColorFoco(new java.awt.Color(37, 99, 235));
        cmbTipo.setFont(new java.awt.Font("SansSerif", 0, 13));
        cmbTipo.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        cmbTipo.setPreferredSize(new java.awt.Dimension(180, 30));
        lblSalidaEstimada.setText("Salida estimada:");
        lblSalidaEstimada.setForeground(new java.awt.Color(30, 41, 59));
        lblCosto.setText("Costo:");
        lblCosto.setForeground(new java.awt.Color(30, 41, 59));
        txtCosto.setBackground(new java.awt.Color(255, 255, 255));
        txtCosto.setForeground(new java.awt.Color(30, 41, 59));
        txtCosto.setColorFoco(new java.awt.Color(37, 99, 235));
        txtCosto.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtCosto.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtCosto.setPreferredSize(new java.awt.Dimension(200, 40));
        txtCosto.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtCosto.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtCosto.setCaretColor(new java.awt.Color(37, 99, 235));
        txtCosto.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtCosto.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        lblTaller.setText("Taller:");
        lblTaller.setForeground(new java.awt.Color(30, 41, 59));
        txtTaller.setBackground(new java.awt.Color(255, 255, 255));
        txtTaller.setForeground(new java.awt.Color(30, 41, 59));
        txtTaller.setColorFoco(new java.awt.Color(37, 99, 235));
        txtTaller.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtTaller.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtTaller.setPreferredSize(new java.awt.Dimension(200, 40));
        txtTaller.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtTaller.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtTaller.setCaretColor(new java.awt.Color(37, 99, 235));
        txtTaller.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtTaller.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        lblDiagnostico.setText("Diagnóstico:");
        lblDiagnostico.setForeground(new java.awt.Color(30, 41, 59));
        scrollDiagnostico.setForeground(new java.awt.Color(30, 41, 59));
        scrollDiagnostico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        scrollDiagnostico.setBackground(new java.awt.Color(255, 255, 255));
        txtDiagnostico.setBackground(new java.awt.Color(255, 255, 255));
        txtDiagnostico.setForeground(new java.awt.Color(30, 41, 59));
        txtDiagnostico.setColorFoco(new java.awt.Color(37, 99, 235));
        txtDiagnostico.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtDiagnostico.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtDiagnostico.setPreferredSize(new java.awt.Dimension(210, 82));
        txtDiagnostico.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtDiagnostico.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtDiagnostico.setCaretColor(new java.awt.Color(37, 99, 235));
        txtDiagnostico.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtDiagnostico.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        panelAccionesCreacion.setOpaque(false);
        panelAccionesCreacion.setForeground(new java.awt.Color(30, 41, 59));
        panelAccionesCreacion.setBackground(new java.awt.Color(248, 250, 252));
        btnLimpiar.setText("LIMPIAR");
        btnLimpiar.setBackground(new java.awt.Color(239, 246, 255));
        btnLimpiar.setForeground(new java.awt.Color(30, 64, 175));
        btnLimpiar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnLimpiar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnLimpiar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnLimpiar.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnLimpiar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnLimpiar.setRolloverEnabled(true);
        btnLimpiar.setContentAreaFilled(false);
        btnLimpiar.setOpaque(false);
        btnIniciar.setText("INICIAR MANTENIMIENTO");
        btnIniciar.setBackground(new java.awt.Color(239, 246, 255));
        btnIniciar.setForeground(new java.awt.Color(30, 64, 175));
        btnIniciar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnIniciar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnIniciar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnIniciar.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnIniciar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnIniciar.setRolloverEnabled(true);
        btnIniciar.setContentAreaFilled(false);
        btnIniciar.setOpaque(false);
        lblRelleno1.setForeground(new java.awt.Color(30, 41, 59));
        lblRelleno2.setForeground(new java.awt.Color(30, 41, 59));
        lblRelleno3.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltrosMantenimiento.setOpaque(false);
        panelFiltrosMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltrosMantenimiento.setBackground(new java.awt.Color(248, 250, 252));
        lblBuscarMantenimiento.setText("Buscar:");
        lblBuscarMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        txtBuscarMantenimiento.setBackground(new java.awt.Color(255, 255, 255));
        txtBuscarMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        txtBuscarMantenimiento.setColorFoco(new java.awt.Color(37, 99, 235));
        txtBuscarMantenimiento.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtBuscarMantenimiento.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtBuscarMantenimiento.setPreferredSize(new java.awt.Dimension(200, 40));
        txtBuscarMantenimiento.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtBuscarMantenimiento.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtBuscarMantenimiento.setCaretColor(new java.awt.Color(37, 99, 235));
        txtBuscarMantenimiento.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtBuscarMantenimiento.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        lblFiltroEstadoMantenimiento.setText("Estado:");
        lblFiltroEstadoMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        cmbEstadoMantenimiento.setBackground(new java.awt.Color(255, 255, 255));
        cmbEstadoMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        cmbEstadoMantenimiento.setColorFoco(new java.awt.Color(37, 99, 235));
        cmbEstadoMantenimiento.setFont(new java.awt.Font("SansSerif", 0, 13));
        cmbEstadoMantenimiento.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        cmbEstadoMantenimiento.setPreferredSize(new java.awt.Dimension(180, 30));
        btnBuscarMantenimiento.setText("ACTUALIZAR");
        btnBuscarMantenimiento.setBackground(new java.awt.Color(239, 246, 255));
        btnBuscarMantenimiento.setForeground(new java.awt.Color(30, 64, 175));
        btnBuscarMantenimiento.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnBuscarMantenimiento.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnBuscarMantenimiento.setColorFoco(new java.awt.Color(37, 99, 235));
        btnBuscarMantenimiento.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnBuscarMantenimiento.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnBuscarMantenimiento.setRolloverEnabled(true);
        btnBuscarMantenimiento.setContentAreaFilled(false);
        btnBuscarMantenimiento.setOpaque(false);
        scrollMantenimientos.setForeground(new java.awt.Color(30, 41, 59));
        scrollMantenimientos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        scrollMantenimientos.setBackground(new java.awt.Color(255, 255, 255));
        tablaMantenimientos.setBackground(new java.awt.Color(255, 255, 255));
        tablaMantenimientos.setForeground(new java.awt.Color(30, 41, 59));
        tablaMantenimientos.setGridColor(new java.awt.Color(125, 211, 252));
        tablaMantenimientos.setSelectionBackground(new java.awt.Color(219, 234, 254));
        tablaMantenimientos.setSelectionForeground(new java.awt.Color(30, 64, 175));
        tablaMantenimientos.setFondoCabecera(new java.awt.Color(224, 242, 254));
        tablaMantenimientos.setTextoCabecera(new java.awt.Color(30, 64, 175));
        tablaMantenimientos.setBordeCabecera(new java.awt.Color(125, 211, 252));
        tablaMantenimientos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        tablaMantenimientos.setFont(new java.awt.Font("SansSerif", 0, 13));
        tablaMantenimientos.setRowHeight(30);
        tablaMantenimientos.setShowVerticalLines(false);
        tablaMantenimientos.setFillsViewportHeight(true);
        panelAccionesMantenimiento.setOpaque(false);
        panelAccionesMantenimiento.setForeground(new java.awt.Color(30, 41, 59));
        panelAccionesMantenimiento.setBackground(new java.awt.Color(248, 250, 252));
        btnEditar.setText("EDITAR");
        btnEditar.setBackground(new java.awt.Color(239, 246, 255));
        btnEditar.setForeground(new java.awt.Color(30, 64, 175));
        btnEditar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnEditar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnEditar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnEditar.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnEditar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnEditar.setRolloverEnabled(true);
        btnEditar.setContentAreaFilled(false);
        btnEditar.setOpaque(false);
        btnFinalizar.setText("FINALIZAR");
        btnFinalizar.setBackground(new java.awt.Color(239, 246, 255));
        btnFinalizar.setForeground(new java.awt.Color(30, 64, 175));
        btnFinalizar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnFinalizar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnFinalizar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnFinalizar.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnFinalizar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnFinalizar.setRolloverEnabled(true);
        btnFinalizar.setContentAreaFilled(false);
        btnFinalizar.setOpaque(false);
        btnCancelar.setText("CANCELAR");
        btnCancelar.setBackground(new java.awt.Color(239, 246, 255));
        btnCancelar.setForeground(new java.awt.Color(30, 64, 175));
        btnCancelar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnCancelar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnCancelar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnCancelar.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnCancelar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnCancelar.setRolloverEnabled(true);
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setOpaque(false);
        lblEstadoCarga.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 6, 12));
        lblEstadoCarga.setText(" ");
        lblEstadoCarga.setForeground(new java.awt.Color(30, 41, 59));
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
        ReporteEstadoMaquinaria reporte = new ReporteEstadoMaquinaria(
                estadoMaquinariaActual, resumenActual, txtBuscarEstado.getText(),
                (String) cmbEstadoMaquinaria.getSelectedItem(), Instant.now());
        tipo_exportar ventana = new tipo_exportar(reporte);
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
        estadoMaquinariaActual = List.copyOf(filas);
        DefaultTableModel modelo = (DefaultTableModel) tablaEstado.getModel();
        modelo.setRowCount(0);
        for (EstadoMaquinariaFila fila : filas) {
            modelo.addRow(new Object[]{fila.idMaquinaria(), fila.codigoInventario(),
                fila.descripcion(), fila.estado(), fechaVisible(fila.fechaRegistro())});
        }
    }

    private void mostrarResumen(ResumenControl resumen) {
        resumenActual = resumen;
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
        // El color y el borde se definen desde el formulario de NetBeans.
    }

    private static void configurarIndicador(JLabel label) {
        label.setOpaque(true);
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
    private com.titanops.vista.componentes.Boton btnBuscarEstado;
    private com.titanops.vista.componentes.Boton btnBuscarMantenimiento;
    private com.titanops.vista.componentes.Boton btnCancelar;
    private com.titanops.vista.componentes.Boton btnEditar;
    private com.titanops.vista.componentes.Boton btnExportar;
    private com.titanops.vista.componentes.Boton btnFinalizar;
    private com.titanops.vista.componentes.Boton btnIniciar;
    private com.titanops.vista.componentes.Boton btnLimpiar;
    private com.titanops.vista.componentes.Selector<String> cmbEstadoMantenimiento;
    private com.titanops.vista.componentes.Selector<String> cmbEstadoMaquinaria;
    private com.titanops.vista.componentes.Selector<MaquinariaOpcion> cmbMaquinaria;
    private com.titanops.vista.componentes.Selector<String> cmbTipo;
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
    private com.titanops.vista.componentes.Tabla tablaEstado;
    private com.titanops.vista.componentes.Tabla tablaMantenimientos;
    private javax.swing.JTabbedPane tabsControl;
    private com.titanops.vista.componentes.CampoTexto txtBuscarEstado;
    private com.titanops.vista.componentes.CampoTexto txtBuscarMantenimiento;
    private com.titanops.vista.componentes.CampoTexto txtCosto;
    private com.titanops.vista.componentes.AreaTexto txtDiagnostico;
    private com.titanops.vista.componentes.CampoTexto txtTaller;
    // End of variables declaration//GEN-END:variables
}

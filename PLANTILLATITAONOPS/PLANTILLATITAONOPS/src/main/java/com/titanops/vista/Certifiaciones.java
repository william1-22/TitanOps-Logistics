package com.titanops.vista;

import com.titanops.controlador.GestionCertificacionesController;
import com.titanops.controlador.GestionCertificacionesController.CategoriaOpcion;
import com.titanops.controlador.GestionCertificacionesController.CertificacionEdicion;
import com.titanops.controlador.GestionCertificacionesController.CertificacionFila;
import com.titanops.controlador.GestionCertificacionesController.OperadorOpcion;
import com.titanops.controlador.GestionCertificacionesController.ResultadoCreacion;
import com.titanops.controlador.GestionCertificacionesController.ResultadoOperacion;
import com.titanops.dao.CategoriaMaquinariaDAO;
import com.titanops.dao.OperadorCertificacionDAO;
import com.titanops.dao.OperadorDAO;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/** Gestión de certificaciones asociadas a operadores y categorías. */
public class Certifiaciones extends JPanel {
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final GestionCertificacionesController controller;
    private List<OperadorOpcion> operadores = List.of();
    private List<CategoriaOpcion> categorias = List.of();
    private boolean cargando;

    public Certifiaciones() {
        this(new GestionCertificacionesController(new OperadorCertificacionDAO(),
                new OperadorDAO(), new CategoriaMaquinariaDAO()));
    }

    Certifiaciones(GestionCertificacionesController controller) {
        this.controller = controller;
        initComponents();
        configurarVista();
        configurarEventos();

        cargarDatos();
    }

    /**
     * Inicializa los componentes administrados por NetBeans GUI Builder.
     * La lógica y las consultas permanecen fuera de este bloque protegido.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtBuscar = new com.titanops.vista.componentes.CampoTexto();
        panelSuperior = new javax.swing.JPanel();
        panelFormulario = new javax.swing.JPanel();
        lblOperador = new javax.swing.JLabel();
        cmbOperador = new com.titanops.vista.componentes.Selector<>();
        lblCategoria = new javax.swing.JLabel();
        cmbCategoria = new com.titanops.vista.componentes.Selector<>();
        lblNumero = new javax.swing.JLabel();
        txtNumero = new com.titanops.vista.componentes.CampoTexto();
        lblExpedicion = new javax.swing.JLabel();
        fechaExpedicion = new com.toedter.calendar.JDateChooser();
        lblVencimiento = new javax.swing.JLabel();
        fechaVencimiento = new com.toedter.calendar.JDateChooser();
        panelAccionesCreacion = new javax.swing.JPanel();
        btnGuardar = new com.titanops.vista.componentes.Boton();
        lblRelleno = new javax.swing.JLabel();
        panelFiltros = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        lblVigencia = new javax.swing.JLabel();
        cmbVigencia = new com.titanops.vista.componentes.Selector<>();
        btnBuscar = new com.titanops.vista.componentes.Boton();
        lblEstado = new javax.swing.JLabel();
        btnLimpiar = new com.titanops.vista.componentes.Boton();
        scrollTabla = new javax.swing.JScrollPane();
        tabla = new com.titanops.vista.componentes.Tabla();
        panelAccionesTabla = new javax.swing.JPanel();
        btnEditar = new com.titanops.vista.componentes.Boton();
        btnEliminar = new com.titanops.vista.componentes.Boton();

        txtBuscar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtBuscar.setColumns(22);
        txtBuscar.setForeground(new java.awt.Color(30, 41, 59));
        txtBuscar.setCaretColor(new java.awt.Color(37, 99, 235));
        txtBuscar.setColorFoco(new java.awt.Color(37, 99, 235));
        txtBuscar.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        txtBuscar.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtBuscar.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        txtBuscar.setPreferredSize(new java.awt.Dimension(200, 40));
        txtBuscar.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtBuscar.setSelectionColor(new java.awt.Color(191, 219, 254));

        setBackground(new java.awt.Color(239, 246, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 18, 14, 18));
        setForeground(new java.awt.Color(30, 41, 59));
        setPreferredSize(new java.awt.Dimension(900, 540));
        setLayout(new java.awt.BorderLayout(8, 8));

        panelSuperior.setOpaque(false);
        panelSuperior.setForeground(new java.awt.Color(30, 41, 59));
        panelSuperior.setBackground(new java.awt.Color(248, 250, 252));
        panelSuperior.setLayout(new java.awt.BorderLayout(0, 6));

        panelFormulario.setOpaque(false);
        panelFormulario.setForeground(new java.awt.Color(30, 41, 59));
        panelFormulario.setBackground(new java.awt.Color(248, 250, 252));
        panelFormulario.setLayout(new java.awt.GridLayout(3, 4, 10, 8));

        lblOperador.setText("Operador:");
        lblOperador.setForeground(new java.awt.Color(30, 41, 59));
        panelFormulario.add(lblOperador);

        cmbOperador.setForeground(new java.awt.Color(30, 41, 59));
        cmbOperador.setColorFoco(new java.awt.Color(37, 99, 235));
        cmbOperador.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        cmbOperador.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        cmbOperador.setPreferredSize(new java.awt.Dimension(180, 30));
        panelFormulario.add(cmbOperador);

        lblCategoria.setText("Categoría:");
        lblCategoria.setForeground(new java.awt.Color(30, 41, 59));
        panelFormulario.add(lblCategoria);

        cmbCategoria.setForeground(new java.awt.Color(30, 41, 59));
        cmbCategoria.setColorFoco(new java.awt.Color(37, 99, 235));
        cmbCategoria.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        cmbCategoria.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        cmbCategoria.setPreferredSize(new java.awt.Dimension(180, 30));
        panelFormulario.add(cmbCategoria);

        lblNumero.setText("N.º acreditación:");
        lblNumero.setForeground(new java.awt.Color(30, 41, 59));
        panelFormulario.add(lblNumero);

        txtNumero.setColumns(18);
        txtNumero.setForeground(new java.awt.Color(30, 41, 59));
        txtNumero.setColorFoco(new java.awt.Color(37, 99, 235));
        txtNumero.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        txtNumero.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtNumero.setPreferredSize(new java.awt.Dimension(200, 40));
        txtNumero.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtNumero.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtNumero.setCaretColor(new java.awt.Color(37, 99, 235));
        txtNumero.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtNumero.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        panelFormulario.add(txtNumero);

        lblExpedicion.setText("Expedición:");
        lblExpedicion.setForeground(new java.awt.Color(30, 41, 59));
        panelFormulario.add(lblExpedicion);
        panelFormulario.add(fechaExpedicion);

        lblVencimiento.setText("Vencimiento:");
        lblVencimiento.setForeground(new java.awt.Color(30, 41, 59));
        panelFormulario.add(lblVencimiento);
        panelFormulario.add(fechaVencimiento);

        panelAccionesCreacion.setOpaque(false);
        panelAccionesCreacion.setForeground(new java.awt.Color(30, 41, 59));
        panelAccionesCreacion.setBackground(new java.awt.Color(248, 250, 252));

        btnGuardar.setText("GUARDAR CERTIFICACIÓN");
        btnGuardar.setBackground(new java.awt.Color(239, 246, 255));
        btnGuardar.setForeground(new java.awt.Color(30, 64, 175));
        btnGuardar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnGuardar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnGuardar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnGuardar.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnGuardar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnGuardar.setContentAreaFilled(false);
        panelAccionesCreacion.add(btnGuardar);

        panelFormulario.add(panelAccionesCreacion);

        lblRelleno.setForeground(new java.awt.Color(30, 41, 59));
        panelFormulario.add(lblRelleno);

        panelSuperior.add(panelFormulario, java.awt.BorderLayout.CENTER);

        panelFiltros.setOpaque(false);
        panelFiltros.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltros.setBackground(new java.awt.Color(248, 250, 252));
        panelFiltros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));

        lblBuscar.setText("Buscar:");
        lblBuscar.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltros.add(lblBuscar);

        lblVigencia.setText("Vigencia:");
        lblVigencia.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltros.add(lblVigencia);

        cmbVigencia.setForeground(new java.awt.Color(30, 41, 59));
        cmbVigencia.setColorFoco(new java.awt.Color(37, 99, 235));
        cmbVigencia.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        cmbVigencia.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        cmbVigencia.setPreferredSize(new java.awt.Dimension(180, 30));
        panelFiltros.add(cmbVigencia);

        btnBuscar.setText("BUSCAR");
        btnBuscar.setBackground(new java.awt.Color(239, 246, 255));
        btnBuscar.setForeground(new java.awt.Color(30, 64, 175));
        btnBuscar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnBuscar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnBuscar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnBuscar.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnBuscar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnBuscar.setContentAreaFilled(false);
        panelFiltros.add(btnBuscar);

        lblEstado.setText(" ");
        lblEstado.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltros.add(lblEstado);

        btnLimpiar.setText("LIMPIAR");
        btnLimpiar.setBackground(new java.awt.Color(239, 246, 255));
        btnLimpiar.setForeground(new java.awt.Color(30, 64, 175));
        btnLimpiar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnLimpiar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnLimpiar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnLimpiar.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnLimpiar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnLimpiar.setContentAreaFilled(false);
        panelFiltros.add(btnLimpiar);

        panelSuperior.add(panelFiltros, java.awt.BorderLayout.SOUTH);

        add(panelSuperior, java.awt.BorderLayout.NORTH);

        scrollTabla.setForeground(new java.awt.Color(30, 41, 59));
        scrollTabla.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)));
        scrollTabla.setBackground(new java.awt.Color(255, 255, 255));

        tabla.setForeground(new java.awt.Color(30, 41, 59));
        tabla.setGridColor(new java.awt.Color(125, 211, 252));
        tabla.setSelectionBackground(new java.awt.Color(219, 234, 254));
        tabla.setSelectionForeground(new java.awt.Color(30, 64, 175));
        tabla.setFondoCabecera(new java.awt.Color(224, 242, 254));
        tabla.setTextoCabecera(new java.awt.Color(30, 64, 175));
        tabla.setBordeCabecera(new java.awt.Color(125, 211, 252));
        tabla.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)));
        tabla.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        tabla.setRowHeight(30);
        tabla.setFillsViewportHeight(true);
        scrollTabla.setViewportView(tabla);

        add(scrollTabla, java.awt.BorderLayout.CENTER);

        panelAccionesTabla.setOpaque(false);
        panelAccionesTabla.setForeground(new java.awt.Color(30, 41, 59));
        panelAccionesTabla.setBackground(new java.awt.Color(248, 250, 252));
        panelAccionesTabla.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 4));

        btnEditar.setText("EDITAR");
        btnEditar.setBackground(new java.awt.Color(239, 246, 255));
        btnEditar.setForeground(new java.awt.Color(30, 64, 175));
        btnEditar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnEditar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnEditar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnEditar.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnEditar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnEditar.setContentAreaFilled(false);
        panelAccionesTabla.add(btnEditar);

        btnEliminar.setText("ELIMINAR");
        btnEliminar.setBackground(new java.awt.Color(239, 246, 255));
        btnEliminar.setForeground(new java.awt.Color(30, 64, 175));
        btnEliminar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnEliminar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnEliminar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnEliminar.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnEliminar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnEliminar.setContentAreaFilled(false);
        panelAccionesTabla.add(btnEliminar);

        add(panelAccionesTabla, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void configurarVista() {

        configurarFecha(fechaExpedicion);
        configurarFecha(fechaVencimiento);
        fechaExpedicion.setDate(desdeLocalDate(LocalDate.now()));
        fechaVencimiento.setDate(desdeLocalDate(LocalDate.now().plusYears(1)));

        for (String estado : GestionCertificacionesController.ESTADOS_VIGENCIA) {
            cmbVigencia.addItem(estado);
        }

        tabla.setModel(new DefaultTableModel(new Object[]{"ID", "OPERADOR", "DUI",
            "CATEGORÍA", "ACREDITACIÓN", "EXPEDICIÓN", "VENCIMIENTO", "VIGENCIA"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configurarBoton(btnGuardar);
        configurarBoton(btnLimpiar);
        configurarBoton(btnBuscar);
        configurarBoton(btnEditar);
        configurarBoton(btnEliminar);
        actualizarBotonesSeleccion();
    }

    private void configurarEventos() {
        btnGuardar.addActionListener(event -> crearCertificacion());
        btnLimpiar.addActionListener(event -> limpiarFormulario());
        btnBuscar.addActionListener(event -> recargarCertificaciones());
        txtBuscar.addActionListener(event -> recargarCertificaciones());
        cmbVigencia.addActionListener(event -> {
            if (!cargando) {
                recargarCertificaciones();
            }
        });
        tabla.getSelectionModel().addListSelectionListener(
                event -> actualizarBotonesSeleccion());
        btnEditar.addActionListener(event -> cargarEdicion());
        btnEliminar.addActionListener(event -> eliminarCertificacion());
    }

    public final void cargarDatos() {
        if (cargando) {
            return;
        }
        String filtro = txtBuscar.getText();
        String vigencia = (String) cmbVigencia.getSelectedItem();
        cambiarEstadoCarga(true, "Cargando certificaciones...");
        new SwingWorker<DatosPantalla, Void>() {
            @Override
            protected DatosPantalla doInBackground() {
                return new DatosPantalla(controller.listarOperadores(),
                        controller.listarCategorias(),
                        controller.listarCertificaciones(filtro, vigencia));
            }

            @Override
            protected void done() {
                try {
                    DatosPantalla datos = get();
                    operadores = datos.operadores();
                    categorias = datos.categorias();
                    llenarOpcionesCreacion();
                    llenarTabla(datos.certificaciones());
                    cambiarEstadoCarga(false, datos.certificaciones().size()
                            + " certificación(es) mostrada(s).");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    cambiarEstadoCarga(false, "Carga interrumpida.");
                } catch (ExecutionException exception) {
                    cambiarEstadoCarga(false, "No fue posible cargar los datos.");
                }
            }
        }.execute();
    }

    private void recargarCertificaciones() {
        if (cargando) {
            return;
        }
        String filtro = txtBuscar.getText();
        String vigencia = (String) cmbVigencia.getSelectedItem();
        cambiarEstadoCarga(true, "Cargando certificaciones...");
        new SwingWorker<List<CertificacionFila>, Void>() {
            @Override
            protected List<CertificacionFila> doInBackground() {
                return controller.listarCertificaciones(filtro, vigencia);
            }

            @Override
            protected void done() {
                try {
                    List<CertificacionFila> filas = get();
                    llenarTabla(filas);
                    cambiarEstadoCarga(false,
                            filas.size() + " certificación(es) mostrada(s).");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    cambiarEstadoCarga(false, "Carga interrumpida.");
                } catch (ExecutionException exception) {
                    cambiarEstadoCarga(false,
                            "No fue posible cargar las certificaciones.");
                }
            }
        }.execute();
    }

    private void crearCertificacion() {
        if (cargando) {
            return;
        }
        OperadorOpcion operador = (OperadorOpcion) cmbOperador.getSelectedItem();
        CategoriaOpcion categoria = (CategoriaOpcion) cmbCategoria.getSelectedItem();
        String numero = txtNumero.getText();
        LocalDate expedicion = aLocalDate(fechaExpedicion.getDate());
        LocalDate vencimiento = aLocalDate(fechaVencimiento.getDate());
        cambiarEstadoCarga(true, "Guardando certificación...");
        new SwingWorker<ResultadoCreacion, Void>() {
            @Override
            protected ResultadoCreacion doInBackground() {
                return controller.crearCertificacion(operador, categoria, numero,
                        expedicion, vencimiento);
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
                    JOptionPane.showMessageDialog(Certifiaciones.this,
                            resultado.mensaje(), "Certificaciones",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    mostrarError("Se interrumpió la creación de la certificación.");
                } catch (ExecutionException exception) {
                    mostrarError("No fue posible crear la certificación.");
                }
            }
        }.execute();
    }

    private void cargarEdicion() {
        Integer idCertificacion = idCertificacionSeleccionada();
        if (idCertificacion == null || cargando) {
            return;
        }
        cambiarEstadoCarga(true, "Cargando certificación...");
        new SwingWorker<Optional<CertificacionEdicion>, Void>() {
            @Override
            protected Optional<CertificacionEdicion> doInBackground() {
                return controller.obtenerCertificacionEdicion(idCertificacion);
            }

            @Override
            protected void done() {
                cambiarEstadoCarga(false, "Listo.");
                try {
                    Optional<CertificacionEdicion> certificacion = get();
                    if (certificacion.isEmpty()) {
                        mostrarValidacion(
                                "La certificación seleccionada ya no existe.");
                        cargarDatos();
                        return;
                    }
                    mostrarFormularioEdicion(certificacion.get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    mostrarError("Se interrumpió la carga de la certificación.");
                } catch (ExecutionException exception) {
                    mostrarError("No fue posible cargar la certificación.");
                }
            }
        }.execute();
    }

    private void mostrarFormularioEdicion(CertificacionEdicion certificacion) {
        JComboBox<OperadorOpcion> operador = new JComboBox<>();
        llenarCombo(operador, operadores);
        seleccionarOperador(operador, certificacion.idOperador());
        JComboBox<CategoriaOpcion> categoria = new JComboBox<>();
        llenarCombo(categoria, categorias);
        seleccionarCategoria(categoria, certificacion.idCategoria());
        JTextField numero = new JTextField(certificacion.numeroAcreditacion(), 20);
        JDateChooser expedicion = new JDateChooser(
                desdeLocalDate(certificacion.fechaExpedicion()));
        JDateChooser vencimiento = new JDateChooser(
                desdeLocalDate(certificacion.fechaVencimiento()));
        configurarFecha(expedicion);
        configurarFecha(vencimiento);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        agregarCampo(formulario, 0, 0, "Operador", operador);
        agregarCampo(formulario, 0, 1, "Categoría", categoria);
        agregarCampo(formulario, 0, 2, "N.º acreditación", numero);
        agregarCampo(formulario, 0, 3, "Expedición", expedicion);
        agregarCampo(formulario, 0, 4, "Vencimiento", vencimiento);
        int respuesta = JOptionPane.showConfirmDialog(this, formulario,
                "Editar certificación", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (respuesta == JOptionPane.OK_OPTION) {
            actualizarCertificacion(certificacion.idCertificacion(),
                    (OperadorOpcion) operador.getSelectedItem(),
                    (CategoriaOpcion) categoria.getSelectedItem(), numero.getText(),
                    aLocalDate(expedicion.getDate()),
                    aLocalDate(vencimiento.getDate()));
        }
    }

    private void actualizarCertificacion(
            int idCertificacion, OperadorOpcion operador, CategoriaOpcion categoria,
            String numero, LocalDate expedicion, LocalDate vencimiento) {
        cambiarEstadoCarga(true, "Actualizando certificación...");
        new SwingWorker<ResultadoOperacion, Void>() {
            @Override
            protected ResultadoOperacion doInBackground() {
                return controller.actualizarCertificacion(idCertificacion, operador,
                        categoria, numero, expedicion, vencimiento);
            }

            @Override
            protected void done() {
                procesarOperacion(this, "actualizar la certificación");
            }
        }.execute();
    }

    private void eliminarCertificacion() {
        Integer idCertificacion = idCertificacionSeleccionada();
        if (idCertificacion == null || cargando) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar la certificación seleccionada?",
                "Eliminar certificación", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        cambiarEstadoCarga(true, "Eliminando certificación...");
        new SwingWorker<ResultadoOperacion, Void>() {
            @Override
            protected ResultadoOperacion doInBackground() {
                return controller.eliminarCertificacion(idCertificacion);
            }

            @Override
            protected void done() {
                procesarOperacion(this, "eliminar la certificación");
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
                    "Certificaciones", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            mostrarError("Se interrumpió la operación.");
        } catch (ExecutionException exception) {
            mostrarError("No fue posible " + accion + ".");
        }
    }

    private void llenarOpcionesCreacion() {
        cmbOperador.removeAllItems();
        for (OperadorOpcion operador : operadores) {
            if (operador.activo()) {
                cmbOperador.addItem(operador);
            }
        }
        cmbCategoria.removeAllItems();
        for (CategoriaOpcion categoria : categorias) {
            if (categoria.activa()) {
                cmbCategoria.addItem(categoria);
            }
        }
    }

    private void llenarTabla(List<CertificacionFila> certificaciones) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        tabla.clearSelection();
        modelo.setRowCount(0);
        for (CertificacionFila certificacion : certificaciones) {
            modelo.addRow(new Object[]{certificacion.idCertificacion(),
                certificacion.operador(), certificacion.dui(),
                certificacion.categoria(), certificacion.numeroAcreditacion(),
                certificacion.fechaExpedicion().format(FORMATO_FECHA),
                certificacion.fechaVencimiento().format(FORMATO_FECHA),
                certificacion.vigencia()});
        }
        actualizarBotonesSeleccion();
    }

    private <T> void llenarCombo(JComboBox<T> combo, List<T> elementos) {
        for (T elemento : elementos) {
            combo.addItem(elemento);
        }
    }

    private void seleccionarOperador(JComboBox<OperadorOpcion> combo, int id) {
        for (int indice = 0; indice < combo.getItemCount(); indice++) {
            if (combo.getItemAt(indice).idOperador() == id) {
                combo.setSelectedIndex(indice);
                return;
            }
        }
    }

    private void seleccionarCategoria(JComboBox<CategoriaOpcion> combo, int id) {
        for (int indice = 0; indice < combo.getItemCount(); indice++) {
            if (combo.getItemAt(indice).idCategoria() == id) {
                combo.setSelectedIndex(indice);
                return;
            }
        }
    }

    private Integer idCertificacionSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        return (Integer) tabla.getModel().getValueAt(filaModelo, 0);
    }

    private void cambiarEstadoCarga(boolean ocupado, String mensaje) {
        cargando = ocupado;
        cmbOperador.setEnabled(!ocupado);
        cmbCategoria.setEnabled(!ocupado);
        txtNumero.setEnabled(!ocupado);
        fechaExpedicion.setEnabled(!ocupado);
        fechaVencimiento.setEnabled(!ocupado);
        txtBuscar.setEnabled(!ocupado);
        cmbVigencia.setEnabled(!ocupado);
        btnGuardar.setEnabled(!ocupado && cmbOperador.getItemCount() > 0
                && cmbCategoria.getItemCount() > 0);
        btnLimpiar.setEnabled(!ocupado);
        btnBuscar.setEnabled(!ocupado);
        tabla.setEnabled(!ocupado);
        lblEstado.setText(mensaje);
        actualizarBotonesSeleccion();
    }

    private void actualizarBotonesSeleccion() {
        boolean seleccion = tabla.getSelectedRow() >= 0 && !cargando;
        btnEditar.setEnabled(seleccion);
        btnEliminar.setEnabled(seleccion);
    }

    private void limpiarFormulario() {
        txtNumero.setText("");
        fechaExpedicion.setDate(desdeLocalDate(LocalDate.now()));
        fechaVencimiento.setDate(desdeLocalDate(LocalDate.now().plusYears(1)));
        if (cmbOperador.getItemCount() > 0) {
            cmbOperador.setSelectedIndex(0);
        }
        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }
        txtNumero.requestFocusInWindow();
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

    private void configurarFecha(JDateChooser selector) {
        selector.setDateFormatString("dd/MM/yyyy");
    }

    private LocalDate aLocalDate(Date fecha) {
        return fecha == null ? null : fecha.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date desdeLocalDate(LocalDate fecha) {
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void mostrarValidacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación",
                JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Certificaciones",
                JOptionPane.ERROR_MESSAGE);
    }

    private record DatosPantalla(List<OperadorOpcion> operadores,
                                  List<CategoriaOpcion> categorias,
                                  List<CertificacionFila> certificaciones) {}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.titanops.vista.componentes.Boton btnBuscar;
    private com.titanops.vista.componentes.Boton btnEditar;
    private com.titanops.vista.componentes.Boton btnEliminar;
    private com.titanops.vista.componentes.Boton btnGuardar;
    private com.titanops.vista.componentes.Boton btnLimpiar;
    private com.titanops.vista.componentes.Selector<CategoriaOpcion> cmbCategoria;
    private com.titanops.vista.componentes.Selector<OperadorOpcion> cmbOperador;
    private com.titanops.vista.componentes.Selector<String> cmbVigencia;
    private com.toedter.calendar.JDateChooser fechaExpedicion;
    private com.toedter.calendar.JDateChooser fechaVencimiento;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCategoria;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblExpedicion;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblOperador;
    private javax.swing.JLabel lblRelleno;
    private javax.swing.JLabel lblVencimiento;
    private javax.swing.JLabel lblVigencia;
    private javax.swing.JPanel panelAccionesCreacion;
    private javax.swing.JPanel panelAccionesTabla;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JPanel panelSuperior;
    private javax.swing.JScrollPane scrollTabla;
    private com.titanops.vista.componentes.Tabla tabla;
    private com.titanops.vista.componentes.CampoTexto txtBuscar;
    private com.titanops.vista.componentes.CampoTexto txtNumero;
    // End of variables declaration//GEN-END:variables
}

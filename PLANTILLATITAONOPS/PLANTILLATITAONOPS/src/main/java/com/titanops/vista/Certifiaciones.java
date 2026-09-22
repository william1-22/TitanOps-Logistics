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
import java.awt.Color;
import java.awt.FlowLayout;
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
    private static final Color COLOR_FONDO = new Color(134, 137, 93);
    private static final Color COLOR_ACCION = new Color(93, 36, 23);
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final GestionCertificacionesController controller;
    private final JComboBox<OperadorOpcion> cmbOperador = new JComboBox<>();
    private final JComboBox<CategoriaOpcion> cmbCategoria = new JComboBox<>();
    private final JTextField txtNumero = new JTextField(18);
    private final JDateChooser fechaExpedicion = new JDateChooser();
    private final JDateChooser fechaVencimiento = new JDateChooser();
    private final JTextField txtBuscar = new JTextField(22);
    private final JComboBox<String> cmbVigencia = new JComboBox<>();
    private final JButton btnGuardar = crearBoton("GUARDAR CERTIFICACIÓN");
    private final JButton btnLimpiar = crearBoton("LIMPIAR");
    private final JButton btnBuscar = crearBoton("BUSCAR");
    private final JButton btnEditar = crearBoton("EDITAR");
    private final JButton btnEliminar = crearBoton("ELIMINAR");
    private final JLabel lblEstado = new JLabel(" ");
    private final JTable tabla = new JTable();
    private List<OperadorOpcion> operadores = List.of();
    private List<CategoriaOpcion> categorias = List.of();
    private boolean cargando;

    public Certifiaciones() {
        this(new GestionCertificacionesController(new OperadorCertificacionDAO(),
                new OperadorDAO(), new CategoriaMaquinariaDAO()));
    }

    Certifiaciones(GestionCertificacionesController controller) {
        this.controller = controller;
        construirVista();
        configurarEventos();
        cargarDatos();
    }

    private void construirVista() {
        setLayout(new BorderLayout(8, 8));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        configurarFecha(fechaExpedicion);
        configurarFecha(fechaVencimiento);
        fechaExpedicion.setDate(desdeLocalDate(LocalDate.now()));
        fechaVencimiento.setDate(desdeLocalDate(LocalDate.now().plusYears(1)));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        agregarCampo(formulario, 0, 0, "Operador", cmbOperador);
        agregarCampo(formulario, 2, 0, "Categoría", cmbCategoria);
        agregarCampo(formulario, 0, 1, "N.º acreditación", txtNumero);
        agregarCampo(formulario, 2, 1, "Expedición", fechaExpedicion);
        agregarCampo(formulario, 0, 2, "Vencimiento", fechaVencimiento);

        JPanel accionesCreacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        accionesCreacion.setOpaque(false);
        accionesCreacion.add(btnGuardar);
        accionesCreacion.add(btnLimpiar);
        GridBagConstraints acciones = restricciones(2, 2);
        acciones.gridwidth = 2;
        formulario.add(accionesCreacion, acciones);

        for (String estado : GestionCertificacionesController.ESTADOS_VIGENCIA) {
            cmbVigencia.addItem(estado);
        }
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Buscar:"));
        filtros.add(txtBuscar);
        filtros.add(new JLabel("Vigencia:"));
        filtros.add(cmbVigencia);
        filtros.add(btnBuscar);
        filtros.add(lblEstado);

        JPanel superior = new JPanel(new BorderLayout());
        superior.setOpaque(false);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(filtros, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);

        tabla.setModel(new DefaultTableModel(new Object[]{"ID", "OPERADOR", "DUI",
            "CATEGORÍA", "ACREDITACIÓN", "EXPEDICIÓN", "VENCIMIENTO", "VIGENCIA"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel accionesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        accionesTabla.setOpaque(false);
        accionesTabla.add(btnEditar);
        accionesTabla.add(btnEliminar);
        add(accionesTabla, BorderLayout.SOUTH);
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

    private static JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(COLOR_ACCION);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 14));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return boton;
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
}

package com.titanops.vista;

import com.titanops.controlador.GestionRutasController;
import com.titanops.controlador.GestionRutasController.ResultadoCreacion;
import com.titanops.controlador.GestionRutasController.RutaFila;
import com.titanops.dao.RutaDestinoDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/** Pantalla para crear rutas y consultar el catálogo actual. */
public class panelCrear extends JPanel {
    private static final Color COLOR_FONDO = new Color(134, 137, 93);
    private static final Color COLOR_ACCION = new Color(93, 36, 23);

    private final GestionRutasController controller;
    private final JTextField txtProyecto = new JTextField(24);
    private final JTextField txtOrigen = new JTextField(24);
    private final JTextField txtDestino = new JTextField(24);
    private final JTextField txtDistancia = new JTextField(12);
    private final JTextField txtBuscar = new JTextField(28);
    private final JButton btnGuardar = crearBoton("GUARDAR RUTA");
    private final JButton btnLimpiar = crearBoton("LIMPIAR");
    private final JButton btnBuscar = crearBoton("BUSCAR");
    private final JLabel lblEstado = new JLabel(" ");
    private final JTable tabla = new JTable();
    private boolean cargando;

    public panelCrear() {
        this(new GestionRutasController(new RutaDestinoDAO()));
    }

    panelCrear(GestionRutasController controller) {
        this.controller = controller;
        construirVista();
        configurarEventos();
        recargarRutas();
    }

    private void construirVista() {
        setLayout(new BorderLayout(8, 8));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        agregarCampo(formulario, 0, 0, "Proyecto", txtProyecto);
        agregarCampo(formulario, 2, 0, "Distancia (km)", txtDistancia);
        agregarCampo(formulario, 0, 1, "Origen", txtOrigen);
        agregarCampo(formulario, 2, 1, "Destino", txtDestino);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        acciones.setOpaque(false);
        acciones.add(btnGuardar);
        acciones.add(btnLimpiar);
        GridBagConstraints accionesConstraint = restricciones(0, 2);
        accionesConstraint.gridwidth = 4;
        accionesConstraint.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(acciones, accionesConstraint);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        busqueda.setOpaque(false);
        busqueda.add(new JLabel("Buscar:"));
        busqueda.add(txtBuscar);
        busqueda.add(btnBuscar);
        busqueda.add(lblEstado);

        JPanel superior = new JPanel(new BorderLayout());
        superior.setOpaque(false);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(busqueda, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);

        tabla.setModel(new DefaultTableModel(
                new Object[]{"ID", "PROYECTO", "ORIGEN", "DESTINO", "KM", "ESTADO"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void configurarEventos() {
        btnGuardar.addActionListener(event -> crearRuta());
        btnLimpiar.addActionListener(event -> limpiarFormulario());
        btnBuscar.addActionListener(event -> recargarRutas());
        txtBuscar.addActionListener(event -> recargarRutas());
    }

    private void crearRuta() {
        if (cargando) {
            return;
        }
        String proyecto = txtProyecto.getText();
        String origen = txtOrigen.getText();
        String destino = txtDestino.getText();
        String distancia = txtDistancia.getText();
        cambiarEstadoCarga(true, "Guardando ruta...");
        new SwingWorker<ResultadoCreacion, Void>() {
            @Override
            protected ResultadoCreacion doInBackground() {
                return controller.crearRuta(proyecto, origen, destino, distancia);
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
                    JOptionPane.showMessageDialog(panelCrear.this, resultado.mensaje(),
                            "Rutas", JOptionPane.INFORMATION_MESSAGE);
                    recargarRutas();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    mostrarError("Se interrumpió la creación de la ruta.");
                } catch (ExecutionException exception) {
                    mostrarError("No fue posible crear la ruta.");
                }
            }
        }.execute();
    }

    public final void recargarRutas() {
        if (cargando) {
            return;
        }
        String filtro = txtBuscar.getText();
        cambiarEstadoCarga(true, "Cargando rutas...");
        new SwingWorker<List<RutaFila>, Void>() {
            @Override
            protected List<RutaFila> doInBackground() {
                return controller.listarRutas(filtro, "TODOS");
            }

            @Override
            protected void done() {
                try {
                    List<RutaFila> rutas = get();
                    llenarTabla(rutas);
                    cambiarEstadoCarga(false,
                            rutas.size() + " ruta(s) mostrada(s).");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    cambiarEstadoCarga(false, "Carga interrumpida.");
                } catch (ExecutionException exception) {
                    cambiarEstadoCarga(false, "No fue posible cargar las rutas.");
                }
            }
        }.execute();
    }

    private void llenarTabla(List<RutaFila> rutas) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        for (RutaFila ruta : rutas) {
            modelo.addRow(new Object[]{ruta.idRuta(), ruta.nombreProyecto(),
                ruta.puntoOrigen(), ruta.puntoDestino(), decimalVisible(ruta.distanciaKm()),
                ruta.estado()});
        }
    }

    private void cambiarEstadoCarga(boolean ocupado, String mensaje) {
        cargando = ocupado;
        txtProyecto.setEnabled(!ocupado);
        txtOrigen.setEnabled(!ocupado);
        txtDestino.setEnabled(!ocupado);
        txtDistancia.setEnabled(!ocupado);
        txtBuscar.setEnabled(!ocupado);
        btnGuardar.setEnabled(!ocupado);
        btnLimpiar.setEnabled(!ocupado);
        btnBuscar.setEnabled(!ocupado);
        tabla.setEnabled(!ocupado);
        lblEstado.setText(mensaje);
    }

    private void limpiarFormulario() {
        txtProyecto.setText("");
        txtOrigen.setText("");
        txtDestino.setText("");
        txtDistancia.setText("");
        txtProyecto.requestFocusInWindow();
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
        constraints.insets = new Insets(5, 6, 5, 6);
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

    private String decimalVisible(BigDecimal valor) {
        return valor == null ? "" : valor.stripTrailingZeros().toPlainString();
    }

    private void mostrarValidacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación",
                JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Rutas",
                JOptionPane.ERROR_MESSAGE);
    }
}

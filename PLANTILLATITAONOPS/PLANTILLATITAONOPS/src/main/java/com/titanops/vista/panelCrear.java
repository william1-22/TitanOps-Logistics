package com.titanops.vista;

import com.titanops.controlador.GestionRutasController;
import com.titanops.controlador.GestionRutasController.ResultadoCreacion;
import com.titanops.controlador.GestionRutasController.RutaFila;
import com.titanops.dao.RutaDestinoDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
    private boolean cargando;

    public panelCrear() {
        this(new GestionRutasController(new RutaDestinoDAO()));
    }

    panelCrear(GestionRutasController controller) {
        this.controller = controller;
        initComponents();
        configurarVista();
        configurarEventos();
        recargarRutas();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelSuperior = new javax.swing.JPanel();
        panelCaptura = new javax.swing.JPanel();
        panelFormulario = new javax.swing.JPanel();
        lblProyecto = new javax.swing.JLabel();
        txtProyecto = new javax.swing.JTextField();
        lblDistancia = new javax.swing.JLabel();
        txtDistancia = new javax.swing.JTextField();
        lblOrigen = new javax.swing.JLabel();
        txtOrigen = new javax.swing.JTextField();
        lblDestino = new javax.swing.JLabel();
        txtDestino = new javax.swing.JTextField();
        panelAcciones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        panelBusqueda = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();
        scrollTabla = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();

        setBackground(new java.awt.Color(134, 137, 93));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 22, 18, 22));
        setPreferredSize(new java.awt.Dimension(1120, 700));
        setLayout(new java.awt.BorderLayout(8, 8));

        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new java.awt.BorderLayout(0, 6));

        panelCaptura.setOpaque(false);
        panelCaptura.setLayout(new java.awt.BorderLayout());

        panelFormulario.setOpaque(false);
        panelFormulario.setLayout(new java.awt.GridLayout(2, 4, 10, 8));

        lblProyecto.setText("Proyecto:");
        panelFormulario.add(lblProyecto);

        txtProyecto.setColumns(24);
        panelFormulario.add(txtProyecto);

        lblDistancia.setText("Distancia (km):");
        panelFormulario.add(lblDistancia);

        txtDistancia.setColumns(12);
        panelFormulario.add(txtDistancia);

        lblOrigen.setText("Origen:");
        panelFormulario.add(lblOrigen);

        txtOrigen.setColumns(24);
        panelFormulario.add(txtOrigen);

        lblDestino.setText("Destino:");
        panelFormulario.add(lblDestino);

        txtDestino.setColumns(24);
        panelFormulario.add(txtDestino);

        panelCaptura.add(panelFormulario, java.awt.BorderLayout.CENTER);

        panelAcciones.setOpaque(false);
        panelAcciones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 8));

        btnGuardar.setText("GUARDAR RUTA");
        panelAcciones.add(btnGuardar);

        btnLimpiar.setText("LIMPIAR");
        panelAcciones.add(btnLimpiar);

        panelCaptura.add(panelAcciones, java.awt.BorderLayout.SOUTH);

        panelSuperior.add(panelCaptura, java.awt.BorderLayout.CENTER);

        panelBusqueda.setOpaque(false);
        panelBusqueda.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));

        lblBuscar.setText("Buscar:");
        panelBusqueda.add(lblBuscar);

        txtBuscar.setColumns(28);
        panelBusqueda.add(txtBuscar);

        btnBuscar.setText("BUSCAR");
        panelBusqueda.add(btnBuscar);

        lblEstado.setText(" ");
        panelBusqueda.add(lblEstado);

        panelSuperior.add(panelBusqueda, java.awt.BorderLayout.SOUTH);

        add(panelSuperior, java.awt.BorderLayout.NORTH);

        scrollTabla.setViewportView(tabla);

        add(scrollTabla, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void configurarVista() {
        tabla.setModel(new DefaultTableModel(
                new Object[]{"ID", "PROYECTO", "ORIGEN", "DESTINO", "KM", "ESTADO"}, 0) {
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

    private static void configurarBoton(JButton boton) {
        boton.setBackground(COLOR_ACCION);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 14));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblDestino;
    private javax.swing.JLabel lblDistancia;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblOrigen;
    private javax.swing.JLabel lblProyecto;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelBusqueda;
    private javax.swing.JPanel panelCaptura;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JPanel panelSuperior;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tabla;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDestino;
    private javax.swing.JTextField txtDistancia;
    private javax.swing.JTextField txtOrigen;
    private javax.swing.JTextField txtProyecto;
    // End of variables declaration//GEN-END:variables
}

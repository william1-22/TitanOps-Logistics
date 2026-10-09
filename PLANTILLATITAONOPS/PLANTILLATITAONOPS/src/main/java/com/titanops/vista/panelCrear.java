package com.titanops.vista;

import com.titanops.controlador.GestionRutasController;
import com.titanops.controlador.GestionRutasController.ResultadoCreacion;
import com.titanops.controlador.GestionRutasController.RutaFila;
import com.titanops.dao.RutaDestinoDAO;
import java.awt.BorderLayout;
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

    private final GestionRutasController controller;
    private boolean cargando;

    public panelCrear() {
        this(new GestionRutasController(new RutaDestinoDAO()), false);
    }

    panelCrear(GestionRutasController controller) {
        this(controller, true);
    }

    private panelCrear(GestionRutasController controller, boolean cargarRutas) {
        this.controller = controller;
        initComponents();
        configurarVista();
        configurarEventos();

        if (cargarRutas) {
            recargarRutas();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelSuperior = new javax.swing.JPanel();
        panelCaptura = new javax.swing.JPanel();
        panelFormulario = new javax.swing.JPanel();
        lblProyecto = new javax.swing.JLabel();
        txtProyecto = new com.titanops.vista.componentes.CampoTexto();
        lblDistancia = new javax.swing.JLabel();
        txtDistancia = new com.titanops.vista.componentes.CampoTexto();
        lblOrigen = new javax.swing.JLabel();
        txtOrigen = new com.titanops.vista.componentes.CampoTexto();
        lblDestino = new javax.swing.JLabel();
        txtDestino = new com.titanops.vista.componentes.CampoTexto();
        panelAcciones = new javax.swing.JPanel();
        btnGuardar = new com.titanops.vista.componentes.Boton();
        btnLimpiar = new com.titanops.vista.componentes.Boton();
        panelBusqueda = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new com.titanops.vista.componentes.CampoTexto();
        btnBuscar = new com.titanops.vista.componentes.Boton();
        lblEstado = new javax.swing.JLabel();
        scrollTabla = new javax.swing.JScrollPane();
        tabla = new com.titanops.vista.componentes.Tabla();

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

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"ID", "PROYECTO", "ORIGEN", "DESTINO", "KM", "ESTADO"}
        ) {
            Class[] types = new Class [] {java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class};
            boolean[] canEdit = new boolean [] {false, false, false, false, false, false};

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollTabla.setViewportView(tabla);

        add(scrollTabla, java.awt.BorderLayout.CENTER);

        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        panelSuperior.setForeground(new java.awt.Color(30, 41, 59));
        panelSuperior.setBackground(new java.awt.Color(248, 250, 252));
        panelCaptura.setForeground(new java.awt.Color(30, 41, 59));
        panelCaptura.setBackground(new java.awt.Color(248, 250, 252));
        panelFormulario.setForeground(new java.awt.Color(30, 41, 59));
        panelFormulario.setBackground(new java.awt.Color(248, 250, 252));
        panelAcciones.setForeground(new java.awt.Color(30, 41, 59));
        panelAcciones.setBackground(new java.awt.Color(248, 250, 252));
        panelBusqueda.setForeground(new java.awt.Color(30, 41, 59));
        panelBusqueda.setBackground(new java.awt.Color(248, 250, 252));
        scrollTabla.setForeground(new java.awt.Color(30, 41, 59));
        scrollTabla.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        scrollTabla.setBackground(new java.awt.Color(255, 255, 255));
        lblProyecto.setForeground(new java.awt.Color(30, 41, 59));
        lblDistancia.setForeground(new java.awt.Color(30, 41, 59));
        lblOrigen.setForeground(new java.awt.Color(30, 41, 59));
        lblDestino.setForeground(new java.awt.Color(30, 41, 59));
        lblBuscar.setForeground(new java.awt.Color(30, 41, 59));
        lblEstado.setForeground(new java.awt.Color(30, 41, 59));


        panelSuperior.setOpaque(false);
        panelSuperior.setForeground(new java.awt.Color(30, 41, 59));
        panelSuperior.setBackground(new java.awt.Color(248, 250, 252));
        panelCaptura.setOpaque(false);
        panelCaptura.setForeground(new java.awt.Color(30, 41, 59));
        panelCaptura.setBackground(new java.awt.Color(248, 250, 252));
        panelFormulario.setOpaque(false);
        panelFormulario.setForeground(new java.awt.Color(30, 41, 59));
        panelFormulario.setBackground(new java.awt.Color(248, 250, 252));
        lblProyecto.setText("Proyecto:");
        lblProyecto.setForeground(new java.awt.Color(30, 41, 59));
        txtProyecto.setBackground(new java.awt.Color(255, 255, 255));
        txtProyecto.setForeground(new java.awt.Color(30, 41, 59));
        txtProyecto.setColorFoco(new java.awt.Color(37, 99, 235));
        txtProyecto.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtProyecto.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtProyecto.setPreferredSize(new java.awt.Dimension(200, 40));
        txtProyecto.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtProyecto.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtProyecto.setCaretColor(new java.awt.Color(37, 99, 235));
        txtProyecto.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtProyecto.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        lblDistancia.setText("Distancia (km):");
        lblDistancia.setForeground(new java.awt.Color(30, 41, 59));
        txtDistancia.setBackground(new java.awt.Color(255, 255, 255));
        txtDistancia.setForeground(new java.awt.Color(30, 41, 59));
        txtDistancia.setColorFoco(new java.awt.Color(37, 99, 235));
        txtDistancia.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtDistancia.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtDistancia.setPreferredSize(new java.awt.Dimension(200, 40));
        txtDistancia.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtDistancia.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtDistancia.setCaretColor(new java.awt.Color(37, 99, 235));
        txtDistancia.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtDistancia.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        lblOrigen.setText("Origen:");
        lblOrigen.setForeground(new java.awt.Color(30, 41, 59));
        txtOrigen.setBackground(new java.awt.Color(255, 255, 255));
        txtOrigen.setForeground(new java.awt.Color(30, 41, 59));
        txtOrigen.setColorFoco(new java.awt.Color(37, 99, 235));
        txtOrigen.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtOrigen.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtOrigen.setPreferredSize(new java.awt.Dimension(200, 40));
        txtOrigen.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtOrigen.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtOrigen.setCaretColor(new java.awt.Color(37, 99, 235));
        txtOrigen.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtOrigen.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        lblDestino.setText("Destino:");
        lblDestino.setForeground(new java.awt.Color(30, 41, 59));
        txtDestino.setBackground(new java.awt.Color(255, 255, 255));
        txtDestino.setForeground(new java.awt.Color(30, 41, 59));
        txtDestino.setColorFoco(new java.awt.Color(37, 99, 235));
        txtDestino.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtDestino.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtDestino.setPreferredSize(new java.awt.Dimension(200, 40));
        txtDestino.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtDestino.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtDestino.setCaretColor(new java.awt.Color(37, 99, 235));
        txtDestino.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtDestino.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        panelAcciones.setOpaque(false);
        panelAcciones.setForeground(new java.awt.Color(30, 41, 59));
        panelAcciones.setBackground(new java.awt.Color(248, 250, 252));
        btnGuardar.setText("GUARDAR RUTA");
        btnGuardar.setBackground(new java.awt.Color(239, 246, 255));
        btnGuardar.setForeground(new java.awt.Color(30, 64, 175));
        btnGuardar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnGuardar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnGuardar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnGuardar.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnGuardar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnGuardar.setRolloverEnabled(true);
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setOpaque(false);
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
        panelBusqueda.setOpaque(false);
        panelBusqueda.setForeground(new java.awt.Color(30, 41, 59));
        panelBusqueda.setBackground(new java.awt.Color(248, 250, 252));
        lblBuscar.setText("Buscar:");
        lblBuscar.setForeground(new java.awt.Color(30, 41, 59));
        txtBuscar.setBackground(new java.awt.Color(255, 255, 255));
        txtBuscar.setForeground(new java.awt.Color(30, 41, 59));
        txtBuscar.setColorFoco(new java.awt.Color(37, 99, 235));
        txtBuscar.setFont(new java.awt.Font("SansSerif", 0, 13));
        txtBuscar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        txtBuscar.setPreferredSize(new java.awt.Dimension(200, 40));
        txtBuscar.setSelectionColor(new java.awt.Color(191, 219, 254));
        txtBuscar.setSelectedTextColor(new java.awt.Color(30, 41, 59));
        txtBuscar.setCaretColor(new java.awt.Color(37, 99, 235));
        txtBuscar.setDisabledTextColor(new java.awt.Color(100, 116, 139));
        txtBuscar.setColorPlaceholder(new java.awt.Color(100, 116, 139));
        btnBuscar.setText("BUSCAR");
        btnBuscar.setBackground(new java.awt.Color(239, 246, 255));
        btnBuscar.setForeground(new java.awt.Color(30, 64, 175));
        btnBuscar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnBuscar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnBuscar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnBuscar.setFont(new java.awt.Font("SansSerif", 1, 13));
        btnBuscar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        btnBuscar.setRolloverEnabled(true);
        btnBuscar.setContentAreaFilled(false);
        btnBuscar.setOpaque(false);
        lblEstado.setText(" ");
        lblEstado.setForeground(new java.awt.Color(30, 41, 59));
        scrollTabla.setForeground(new java.awt.Color(30, 41, 59));
        scrollTabla.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        scrollTabla.setBackground(new java.awt.Color(255, 255, 255));
        tabla.setBackground(new java.awt.Color(255, 255, 255));
        tabla.setForeground(new java.awt.Color(30, 41, 59));
        tabla.setGridColor(new java.awt.Color(125, 211, 252));
        tabla.setSelectionBackground(new java.awt.Color(219, 234, 254));
        tabla.setSelectionForeground(new java.awt.Color(30, 64, 175));
        tabla.setFondoCabecera(new java.awt.Color(224, 242, 254));
        tabla.setTextoCabecera(new java.awt.Color(30, 64, 175));
        tabla.setBordeCabecera(new java.awt.Color(125, 211, 252));
        tabla.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        tabla.setFont(new java.awt.Font("SansSerif", 0, 13));
        tabla.setRowHeight(30);
        tabla.setShowVerticalLines(false);
        tabla.setFillsViewportHeight(true);
    }// </editor-fold>//GEN-END:initComponents

    private void configurarVista() {
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
        // El color y el borde se definen desde el formulario de NetBeans.
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
    private com.titanops.vista.componentes.Boton btnBuscar;
    private com.titanops.vista.componentes.Boton btnGuardar;
    private com.titanops.vista.componentes.Boton btnLimpiar;
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
    private com.titanops.vista.componentes.Tabla tabla;
    private com.titanops.vista.componentes.CampoTexto txtBuscar;
    private com.titanops.vista.componentes.CampoTexto txtDestino;
    private com.titanops.vista.componentes.CampoTexto txtDistancia;
    private com.titanops.vista.componentes.CampoTexto txtOrigen;
    private com.titanops.vista.componentes.CampoTexto txtProyecto;
    // End of variables declaration//GEN-END:variables
}

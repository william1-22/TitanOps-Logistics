package com.titanops.vista;

import com.titanops.controlador.GestionRutasController;
import com.titanops.controlador.GestionRutasController.ResultadoOperacion;
import com.titanops.controlador.GestionRutasController.RutaEdicion;
import com.titanops.controlador.GestionRutasController.RutaFila;
import com.titanops.dao.RutaDestinoDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
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

/** Pantalla para consultar, editar y cancelar rutas existentes. */
public class panelEditar extends JPanel {

    private final GestionRutasController controller;
    private boolean cargando;

    public panelEditar() {
        this(new GestionRutasController(new RutaDestinoDAO()));
    }

    panelEditar(GestionRutasController controller) {
        this.controller = controller;
        initComponents();
        configurarVista();
        configurarEventos();

        recargarRutas();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFiltros = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new com.titanops.vista.componentes.CampoTexto();
        lblFiltroEstado = new javax.swing.JLabel();
        cmbEstado = new com.titanops.vista.componentes.Selector<>();
        btnBuscar = new com.titanops.vista.componentes.Boton();
        lblEstado = new javax.swing.JLabel();
        scrollTabla = new javax.swing.JScrollPane();
        tabla = new com.titanops.vista.componentes.Tabla();
        panelAcciones = new javax.swing.JPanel();
        btnEditar = new com.titanops.vista.componentes.Boton();
        btnCancelar = new com.titanops.vista.componentes.Boton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 22, 18, 22));
        setPreferredSize(new java.awt.Dimension(1120, 700));
        setLayout(new java.awt.BorderLayout(8, 8));

        panelFiltros.setOpaque(false);
        panelFiltros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));

        lblBuscar.setText("Buscar:");
        panelFiltros.add(lblBuscar);

        txtBuscar.setColumns(28);
        panelFiltros.add(txtBuscar);

        lblFiltroEstado.setText("Estado:");
        panelFiltros.add(lblFiltroEstado);
        panelFiltros.add(cmbEstado);

        btnBuscar.setText("BUSCAR");
        panelFiltros.add(btnBuscar);

        lblEstado.setText(" ");
        panelFiltros.add(lblEstado);

        add(panelFiltros, java.awt.BorderLayout.NORTH);

        scrollTabla.setViewportView(tabla);

        add(scrollTabla, java.awt.BorderLayout.CENTER);

        panelAcciones.setOpaque(false);
        panelAcciones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        btnEditar.setText("EDITAR");
        panelAcciones.add(btnEditar);

        btnCancelar.setText("CANCELAR RUTA");
        panelAcciones.add(btnCancelar);

        add(panelAcciones, java.awt.BorderLayout.SOUTH);

        setForeground(new java.awt.Color(30, 41, 59));
        setBackground(new java.awt.Color(239, 246, 255));
        panelFiltros.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltros.setBackground(new java.awt.Color(248, 250, 252));
        scrollTabla.setForeground(new java.awt.Color(30, 41, 59));
        scrollTabla.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1));
        scrollTabla.setBackground(new java.awt.Color(255, 255, 255));
        panelAcciones.setForeground(new java.awt.Color(30, 41, 59));
        panelAcciones.setBackground(new java.awt.Color(248, 250, 252));
        lblBuscar.setForeground(new java.awt.Color(30, 41, 59));
        lblFiltroEstado.setForeground(new java.awt.Color(30, 41, 59));
        lblEstado.setForeground(new java.awt.Color(30, 41, 59));


        panelFiltros.setOpaque(false);
        panelFiltros.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltros.setBackground(new java.awt.Color(248, 250, 252));
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
        lblFiltroEstado.setText("Estado:");
        lblFiltroEstado.setForeground(new java.awt.Color(30, 41, 59));
        cmbEstado.setBackground(new java.awt.Color(255, 255, 255));
        cmbEstado.setForeground(new java.awt.Color(30, 41, 59));
        cmbEstado.setColorFoco(new java.awt.Color(37, 99, 235));
        cmbEstado.setFont(new java.awt.Font("SansSerif", 0, 13));
        cmbEstado.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252), 1), javax.swing.BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        cmbEstado.setPreferredSize(new java.awt.Dimension(180, 30));
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
        panelAcciones.setOpaque(false);
        panelAcciones.setForeground(new java.awt.Color(30, 41, 59));
        panelAcciones.setBackground(new java.awt.Color(248, 250, 252));
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
        btnCancelar.setText("CANCELAR RUTA");
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
    }// </editor-fold>//GEN-END:initComponents

    private void configurarVista() {
        cmbEstado.addItem("TODOS");
        for (String estado : GestionRutasController.ESTADOS) {
            cmbEstado.addItem(estado);
        }

        tabla.setModel(new DefaultTableModel(
                new Object[]{"ID", "PROYECTO", "ORIGEN", "DESTINO", "KM", "ESTADO"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configurarBoton(btnBuscar);
        configurarBoton(btnEditar);
        configurarBoton(btnCancelar);
        actualizarBotonesSeleccion();
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(event -> recargarRutas());
        txtBuscar.addActionListener(event -> recargarRutas());
        cmbEstado.addActionListener(event -> {
            if (!cargando) {
                recargarRutas();
            }
        });
        tabla.getSelectionModel().addListSelectionListener(
                event -> actualizarBotonesSeleccion());
        btnEditar.addActionListener(event -> cargarEdicion());
        btnCancelar.addActionListener(event -> cancelarRuta());
    }

    public final void recargarRutas() {
        if (cargando) {
            return;
        }
        String filtro = txtBuscar.getText();
        String estado = (String) cmbEstado.getSelectedItem();
        cambiarEstadoCarga(true, "Cargando rutas...");
        new SwingWorker<List<RutaFila>, Void>() {
            @Override
            protected List<RutaFila> doInBackground() {
                return controller.listarRutas(filtro, estado);
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

    private void cargarEdicion() {
        Integer idRuta = idRutaSeleccionada();
        if (idRuta == null || cargando) {
            return;
        }
        cambiarEstadoCarga(true, "Cargando ruta...");
        new SwingWorker<Optional<RutaEdicion>, Void>() {
            @Override
            protected Optional<RutaEdicion> doInBackground() {
                return controller.obtenerRutaEdicion(idRuta);
            }

            @Override
            protected void done() {
                cambiarEstadoCarga(false, "Listo.");
                try {
                    Optional<RutaEdicion> ruta = get();
                    if (ruta.isEmpty()) {
                        mostrarValidacion("La ruta seleccionada ya no existe.");
                        recargarRutas();
                        return;
                    }
                    mostrarFormularioEdicion(ruta.get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    mostrarError("Se interrumpió la carga de la ruta.");
                } catch (ExecutionException exception) {
                    mostrarError("No fue posible cargar la ruta.");
                }
            }
        }.execute();
    }

    private void mostrarFormularioEdicion(RutaEdicion ruta) {
        JTextField proyecto = new JTextField(ruta.nombreProyecto(), 24);
        JTextField origen = new JTextField(ruta.puntoOrigen(), 24);
        JTextField destino = new JTextField(ruta.puntoDestino(), 24);
        JTextField distancia = new JTextField(decimalVisible(ruta.distanciaKm()), 12);
        JComboBox<String> estado = new JComboBox<>(
                GestionRutasController.ESTADOS.toArray(String[]::new));
        estado.setSelectedItem(ruta.estado());

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        agregarCampo(formulario, 0, "Proyecto", proyecto);
        agregarCampo(formulario, 1, "Origen", origen);
        agregarCampo(formulario, 2, "Destino", destino);
        agregarCampo(formulario, 3, "Distancia (km)", distancia);
        agregarCampo(formulario, 4, "Estado", estado);
        int respuesta = JOptionPane.showConfirmDialog(this, formulario,
                "Editar ruta", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (respuesta == JOptionPane.OK_OPTION) {
            actualizarRuta(ruta.idRuta(), proyecto.getText(), origen.getText(),
                    destino.getText(), distancia.getText(),
                    (String) estado.getSelectedItem());
        }
    }

    private void actualizarRuta(int idRuta, String proyecto, String origen,
                                String destino, String distancia, String estado) {
        cambiarEstadoCarga(true, "Actualizando ruta...");
        new SwingWorker<ResultadoOperacion, Void>() {
            @Override
            protected ResultadoOperacion doInBackground() {
                return controller.actualizarRuta(idRuta, proyecto, origen, destino,
                        distancia, estado);
            }

            @Override
            protected void done() {
                procesarOperacion(this, "actualizar la ruta");
            }
        }.execute();
    }

    private void cancelarRuta() {
        Integer idRuta = idRutaSeleccionada();
        if (idRuta == null || cargando) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Deseas cancelar la ruta seleccionada?",
                "Cancelar ruta", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        cambiarEstadoCarga(true, "Cancelando ruta...");
        new SwingWorker<ResultadoOperacion, Void>() {
            @Override
            protected ResultadoOperacion doInBackground() {
                return controller.cancelarRuta(idRuta);
            }

            @Override
            protected void done() {
                procesarOperacion(this, "cancelar la ruta");
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
            JOptionPane.showMessageDialog(this, resultado.mensaje(), "Rutas",
                    JOptionPane.INFORMATION_MESSAGE);
            recargarRutas();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            mostrarError("Se interrumpió la operación.");
        } catch (ExecutionException exception) {
            mostrarError("No fue posible " + accion + ".");
        }
    }

    private void llenarTabla(List<RutaFila> rutas) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        tabla.clearSelection();
        modelo.setRowCount(0);
        for (RutaFila ruta : rutas) {
            modelo.addRow(new Object[]{ruta.idRuta(), ruta.nombreProyecto(),
                ruta.puntoOrigen(), ruta.puntoDestino(), decimalVisible(ruta.distanciaKm()),
                ruta.estado()});
        }
        actualizarBotonesSeleccion();
    }

    private Integer idRutaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        return (Integer) tabla.getModel().getValueAt(filaModelo, 0);
    }

    private void cambiarEstadoCarga(boolean ocupado, String mensaje) {
        cargando = ocupado;
        txtBuscar.setEnabled(!ocupado);
        cmbEstado.setEnabled(!ocupado);
        btnBuscar.setEnabled(!ocupado);
        tabla.setEnabled(!ocupado);
        lblEstado.setText(mensaje);
        actualizarBotonesSeleccion();
    }

    private void actualizarBotonesSeleccion() {
        boolean seleccion = tabla.getSelectedRow() >= 0 && !cargando;
        btnEditar.setEnabled(seleccion);
        btnCancelar.setEnabled(seleccion);
    }

    private void agregarCampo(JPanel panel, int fila, String etiqueta,
                              java.awt.Component componente) {
        GridBagConstraints label = restricciones(0, fila);
        label.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(etiqueta + ":"), label);
        GridBagConstraints campo = restricciones(1, fila);
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
    private com.titanops.vista.componentes.Boton btnCancelar;
    private com.titanops.vista.componentes.Boton btnEditar;
    private com.titanops.vista.componentes.Selector<String> cmbEstado;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblFiltroEstado;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JScrollPane scrollTabla;
    private com.titanops.vista.componentes.Tabla tabla;
    private com.titanops.vista.componentes.CampoTexto txtBuscar;
    // End of variables declaration//GEN-END:variables
}

package com.titanops.vista;

/** Vista editable en la pestaña Design de NetBeans. */
public class ReporteProductividad extends javax.swing.JPanel {
    public ReporteProductividad() { initComponents(); }

    EnlaceReporteOperativo enlazar(com.titanops.controlador.ReportesOperativosController controller) {
        return new EnlaceReporteOperativo(this, 10, controller,
                java.util.Map.of("cmbTurno", cmbTurno, "cmbEstadoOperador", cmbEstadoOperador, "fechaInicio", fechaInicio, "fechaFin", fechaFin), tablaDatos, panelGrafico1,
                null, new javax.swing.JLabel[] {lblKpi1, lblKpi2, lblKpi3, lblKpi4, lblKpi5},
                lblEstado, btnGenerar, btnLimpiar, btnExportar);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelSuperior = new javax.swing.JPanel();
        panelCabecera = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        panelFiltros = new javax.swing.JPanel();
        filtrocmbTurno = new javax.swing.JPanel();
        lblcmbTurno = new javax.swing.JLabel();
        cmbTurno = new com.titanops.vista.componentes.Selector<>();
        filtrocmbEstadoOperador = new javax.swing.JPanel();
        lblcmbEstadoOperador = new javax.swing.JLabel();
        cmbEstadoOperador = new com.titanops.vista.componentes.Selector<>();
        filtrofechaInicio = new javax.swing.JPanel();
        lblfechaInicio = new javax.swing.JLabel();
        fechaInicio = new com.toedter.calendar.JDateChooser();
        filtrofechaFin = new javax.swing.JPanel();
        lblfechaFin = new javax.swing.JLabel();
        fechaFin = new com.toedter.calendar.JDateChooser();
        panelAcciones = new javax.swing.JPanel();
        btnGenerar = new com.titanops.vista.componentes.Boton();
        btnLimpiar = new com.titanops.vista.componentes.Boton();
        btnExportar = new com.titanops.vista.componentes.Boton();
        panelResumen = new javax.swing.JPanel();
        tarjetaKpi1 = new javax.swing.JPanel();
        lblNombreKpi1 = new javax.swing.JLabel();
        lblKpi1 = new javax.swing.JLabel();
        tarjetaKpi2 = new javax.swing.JPanel();
        lblNombreKpi2 = new javax.swing.JLabel();
        lblKpi2 = new javax.swing.JLabel();
        tarjetaKpi3 = new javax.swing.JPanel();
        lblNombreKpi3 = new javax.swing.JLabel();
        lblKpi3 = new javax.swing.JLabel();
        tarjetaKpi4 = new javax.swing.JPanel();
        lblNombreKpi4 = new javax.swing.JLabel();
        lblKpi4 = new javax.swing.JLabel();
        tarjetaKpi5 = new javax.swing.JPanel();
        lblNombreKpi5 = new javax.swing.JLabel();
        lblKpi5 = new javax.swing.JLabel();
        divisionResultados = new javax.swing.JSplitPane();
        panelGraficos = new javax.swing.JPanel();
        panelGrafico1 = new javax.swing.JPanel();
        lblGrafico1 = new javax.swing.JLabel();
        scrollDatos = new javax.swing.JScrollPane();
        tablaDatos = new com.titanops.vista.componentes.Tabla();
        lblEstado = new javax.swing.JLabel();

        setBackground(new java.awt.Color(248, 250, 252));
        setForeground(new java.awt.Color(30, 41, 59));
        setPreferredSize(new java.awt.Dimension(1050, 920));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setLayout(new java.awt.BorderLayout(8, 14));

        panelSuperior.setBackground(new java.awt.Color(248, 250, 252));
        panelSuperior.setForeground(new java.awt.Color(30, 41, 59));
        panelSuperior.setLayout(new java.awt.BorderLayout(8, 14));

        panelCabecera.setBackground(new java.awt.Color(248, 250, 252));
        panelCabecera.setForeground(new java.awt.Color(30, 41, 59));
        panelCabecera.setLayout(new java.awt.BorderLayout(8, 10));

        lblTitulo.setText("Productividad y carga laboral por turno");
        lblTitulo.setForeground(new java.awt.Color(30, 64, 175));
        lblTitulo.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        panelCabecera.add(lblTitulo, java.awt.BorderLayout.NORTH);

        panelFiltros.setBackground(new java.awt.Color(248, 250, 252));
        panelFiltros.setForeground(new java.awt.Color(30, 41, 59));
        panelFiltros.setLayout(new java.awt.GridLayout(1, 4, 8, 4));

        filtrocmbTurno.setBackground(new java.awt.Color(248, 250, 252));
        filtrocmbTurno.setForeground(new java.awt.Color(30, 41, 59));
        filtrocmbTurno.setLayout(new java.awt.BorderLayout(0, 3));

        lblcmbTurno.setText("Turno");
        lblcmbTurno.setForeground(new java.awt.Color(30, 41, 59));
        lblcmbTurno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        filtrocmbTurno.add(lblcmbTurno, java.awt.BorderLayout.NORTH);

        cmbTurno.setPreferredSize(new java.awt.Dimension(165, 30));
        cmbTurno.setForeground(new java.awt.Color(30, 41, 59));
        cmbTurno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        cmbTurno.setColorFoco(new java.awt.Color(30, 64, 175));
        filtrocmbTurno.add(cmbTurno, java.awt.BorderLayout.CENTER);

        panelFiltros.add(filtrocmbTurno);

        filtrocmbEstadoOperador.setBackground(new java.awt.Color(248, 250, 252));
        filtrocmbEstadoOperador.setForeground(new java.awt.Color(30, 41, 59));
        filtrocmbEstadoOperador.setLayout(new java.awt.BorderLayout(0, 3));

        lblcmbEstadoOperador.setText("Estado operativo");
        lblcmbEstadoOperador.setForeground(new java.awt.Color(30, 41, 59));
        lblcmbEstadoOperador.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        filtrocmbEstadoOperador.add(lblcmbEstadoOperador, java.awt.BorderLayout.NORTH);

        cmbEstadoOperador.setPreferredSize(new java.awt.Dimension(165, 30));
        cmbEstadoOperador.setForeground(new java.awt.Color(30, 41, 59));
        cmbEstadoOperador.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        cmbEstadoOperador.setColorFoco(new java.awt.Color(30, 64, 175));
        filtrocmbEstadoOperador.add(cmbEstadoOperador, java.awt.BorderLayout.CENTER);

        panelFiltros.add(filtrocmbEstadoOperador);

        filtrofechaInicio.setBackground(new java.awt.Color(248, 250, 252));
        filtrofechaInicio.setForeground(new java.awt.Color(30, 41, 59));
        filtrofechaInicio.setLayout(new java.awt.BorderLayout(0, 3));

        lblfechaInicio.setText("Desde");
        lblfechaInicio.setForeground(new java.awt.Color(30, 41, 59));
        lblfechaInicio.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        filtrofechaInicio.add(lblfechaInicio, java.awt.BorderLayout.NORTH);

        fechaInicio.setPreferredSize(new java.awt.Dimension(165, 30));
        fechaInicio.setBackground(new java.awt.Color(255, 255, 255));
        fechaInicio.setForeground(new java.awt.Color(30, 41, 59));
        fechaInicio.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fechaInicio.setDateFormatString("dd/MM/yyyy");
        filtrofechaInicio.add(fechaInicio, java.awt.BorderLayout.CENTER);

        panelFiltros.add(filtrofechaInicio);

        filtrofechaFin.setBackground(new java.awt.Color(248, 250, 252));
        filtrofechaFin.setForeground(new java.awt.Color(30, 41, 59));
        filtrofechaFin.setLayout(new java.awt.BorderLayout(0, 3));

        lblfechaFin.setText("Hasta");
        lblfechaFin.setForeground(new java.awt.Color(30, 41, 59));
        lblfechaFin.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        filtrofechaFin.add(lblfechaFin, java.awt.BorderLayout.NORTH);

        fechaFin.setPreferredSize(new java.awt.Dimension(165, 30));
        fechaFin.setBackground(new java.awt.Color(255, 255, 255));
        fechaFin.setForeground(new java.awt.Color(30, 41, 59));
        fechaFin.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fechaFin.setDateFormatString("dd/MM/yyyy");
        filtrofechaFin.add(fechaFin, java.awt.BorderLayout.CENTER);

        panelFiltros.add(filtrofechaFin);

        panelCabecera.add(panelFiltros, java.awt.BorderLayout.CENTER);

        panelAcciones.setBackground(new java.awt.Color(248, 250, 252));
        panelAcciones.setForeground(new java.awt.Color(30, 41, 59));
        panelAcciones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));

        btnGenerar.setText("GENERAR REPORTE");
        btnGenerar.setBackground(new java.awt.Color(239, 246, 255));
        btnGenerar.setForeground(new java.awt.Color(30, 64, 175));
        btnGenerar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnGenerar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnGenerar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnGenerar.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnGenerar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        panelAcciones.add(btnGenerar);

        btnLimpiar.setText("LIMPIAR FILTROS");
        btnLimpiar.setBackground(new java.awt.Color(239, 246, 255));
        btnLimpiar.setForeground(new java.awt.Color(30, 64, 175));
        btnLimpiar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnLimpiar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnLimpiar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnLimpiar.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnLimpiar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        panelAcciones.add(btnLimpiar);

        btnExportar.setText("EXPORTAR REPORTE PDF");
        btnExportar.setBackground(new java.awt.Color(239, 246, 255));
        btnExportar.setForeground(new java.awt.Color(30, 64, 175));
        btnExportar.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnExportar.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnExportar.setColorFoco(new java.awt.Color(37, 99, 235));
        btnExportar.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnExportar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        btnExportar.setEnabled(false);
        panelAcciones.add(btnExportar);

        panelCabecera.add(panelAcciones, java.awt.BorderLayout.SOUTH);

        panelSuperior.add(panelCabecera, java.awt.BorderLayout.NORTH);

        panelResumen.setBackground(new java.awt.Color(248, 250, 252));
        panelResumen.setForeground(new java.awt.Color(30, 41, 59));
        panelResumen.setLayout(new java.awt.GridLayout(1, 5, 12, 4));

        tarjetaKpi1.setBackground(new java.awt.Color(239, 246, 255));
        tarjetaKpi1.setForeground(new java.awt.Color(30, 41, 59));
        tarjetaKpi1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 8, 10, 8));
        tarjetaKpi1.setLayout(new java.awt.BorderLayout(0, 5));

        lblNombreKpi1.setText("Operadores");
        lblNombreKpi1.setForeground(new java.awt.Color(30, 41, 59));
        lblNombreKpi1.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblNombreKpi1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tarjetaKpi1.add(lblNombreKpi1, java.awt.BorderLayout.NORTH);

        lblKpi1.setText("—");
        lblKpi1.setForeground(new java.awt.Color(30, 64, 175));
        lblKpi1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblKpi1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblKpi1.setToolTipText("Operadores");
        tarjetaKpi1.add(lblKpi1, java.awt.BorderLayout.CENTER);

        panelResumen.add(tarjetaKpi1);

        tarjetaKpi2.setBackground(new java.awt.Color(239, 246, 255));
        tarjetaKpi2.setForeground(new java.awt.Color(30, 41, 59));
        tarjetaKpi2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 8, 10, 8));
        tarjetaKpi2.setLayout(new java.awt.BorderLayout(0, 5));

        lblNombreKpi2.setText("Viajes finalizados");
        lblNombreKpi2.setForeground(new java.awt.Color(30, 41, 59));
        lblNombreKpi2.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblNombreKpi2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tarjetaKpi2.add(lblNombreKpi2, java.awt.BorderLayout.NORTH);

        lblKpi2.setText("—");
        lblKpi2.setForeground(new java.awt.Color(30, 64, 175));
        lblKpi2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblKpi2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblKpi2.setToolTipText("Viajes finalizados");
        tarjetaKpi2.add(lblKpi2, java.awt.BorderLayout.CENTER);

        panelResumen.add(tarjetaKpi2);

        tarjetaKpi3.setBackground(new java.awt.Color(239, 246, 255));
        tarjetaKpi3.setForeground(new java.awt.Color(30, 41, 59));
        tarjetaKpi3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 8, 10, 8));
        tarjetaKpi3.setLayout(new java.awt.BorderLayout(0, 5));

        lblNombreKpi3.setText("Viajes cancelados");
        lblNombreKpi3.setForeground(new java.awt.Color(30, 41, 59));
        lblNombreKpi3.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblNombreKpi3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tarjetaKpi3.add(lblNombreKpi3, java.awt.BorderLayout.NORTH);

        lblKpi3.setText("—");
        lblKpi3.setForeground(new java.awt.Color(30, 64, 175));
        lblKpi3.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblKpi3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblKpi3.setToolTipText("Viajes cancelados");
        tarjetaKpi3.add(lblKpi3, java.awt.BorderLayout.CENTER);

        panelResumen.add(tarjetaKpi3);

        tarjetaKpi4.setBackground(new java.awt.Color(239, 246, 255));
        tarjetaKpi4.setForeground(new java.awt.Color(30, 41, 59));
        tarjetaKpi4.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 8, 10, 8));
        tarjetaKpi4.setLayout(new java.awt.BorderLayout(0, 5));

        lblNombreKpi4.setText("Horas conducidas");
        lblNombreKpi4.setForeground(new java.awt.Color(30, 41, 59));
        lblNombreKpi4.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblNombreKpi4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tarjetaKpi4.add(lblNombreKpi4, java.awt.BorderLayout.NORTH);

        lblKpi4.setText("—");
        lblKpi4.setForeground(new java.awt.Color(30, 64, 175));
        lblKpi4.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblKpi4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblKpi4.setToolTipText("Horas conducidas");
        tarjetaKpi4.add(lblKpi4, java.awt.BorderLayout.CENTER);

        panelResumen.add(tarjetaKpi4);

        tarjetaKpi5.setBackground(new java.awt.Color(239, 246, 255));
        tarjetaKpi5.setForeground(new java.awt.Color(30, 41, 59));
        tarjetaKpi5.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 8, 10, 8));
        tarjetaKpi5.setLayout(new java.awt.BorderLayout(0, 5));

        lblNombreKpi5.setText("Cumplimiento (%)");
        lblNombreKpi5.setForeground(new java.awt.Color(30, 41, 59));
        lblNombreKpi5.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblNombreKpi5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tarjetaKpi5.add(lblNombreKpi5, java.awt.BorderLayout.NORTH);

        lblKpi5.setText("—");
        lblKpi5.setForeground(new java.awt.Color(30, 64, 175));
        lblKpi5.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblKpi5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblKpi5.setToolTipText("Cumplimiento (%)");
        tarjetaKpi5.add(lblKpi5, java.awt.BorderLayout.CENTER);

        panelResumen.add(tarjetaKpi5);

        panelSuperior.add(panelResumen, java.awt.BorderLayout.SOUTH);

        add(panelSuperior, java.awt.BorderLayout.NORTH);

        divisionResultados.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        divisionResultados.setResizeWeight(0.6);
        divisionResultados.setDividerLocation(420);
        divisionResultados.setDividerSize(10);
        divisionResultados.setOneTouchExpandable(true);
        divisionResultados.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        panelGraficos.setBackground(new java.awt.Color(248, 250, 252));
        panelGraficos.setForeground(new java.awt.Color(30, 41, 59));
        panelGraficos.setMinimumSize(new java.awt.Dimension(300, 150));
        panelGraficos.setLayout(new java.awt.GridLayout(1, 1, 8, 4));

        panelGrafico1.setBackground(new java.awt.Color(248, 250, 252));
        panelGrafico1.setForeground(new java.awt.Color(30, 41, 59));
        panelGrafico1.setMinimumSize(new java.awt.Dimension(150, 120));
        panelGrafico1.setLayout(new java.awt.BorderLayout(0, 0));

        lblGrafico1.setText("Genera el reporte para visualizar el gráfico");
        lblGrafico1.setForeground(new java.awt.Color(30, 41, 59));
        lblGrafico1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblGrafico1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelGrafico1.add(lblGrafico1, java.awt.BorderLayout.CENTER);

        panelGraficos.add(panelGrafico1);

        divisionResultados.setTopComponent(panelGraficos);

        scrollDatos.setMinimumSize(new java.awt.Dimension(300, 100));

        tablaDatos.setAutoCreateRowSorter(true);
        tablaDatos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tablaDatos.setRowHeight(28);
        tablaDatos.setForeground(new java.awt.Color(30, 41, 59));
        tablaDatos.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        tablaDatos.setGridColor(new java.awt.Color(125, 211, 252));
        tablaDatos.setFondoCabecera(new java.awt.Color(224, 242, 254));
        tablaDatos.setTextoCabecera(new java.awt.Color(30, 64, 175));
        tablaDatos.setBordeCabecera(new java.awt.Color(125, 211, 252));
        tablaDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "OPERADOR", "DUI", "LICENCIA", "TURNO", "ESTADO OPERATIVO", "CATEGORÍAS CERT.", "TOTAL VIAJES", "FINALIZADOS", "CANCELADOS", "HORAS CONDUCIDAS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollDatos.setViewportView(tablaDatos);
        if (tablaDatos.getColumnModel().getColumnCount() > 0) {
            tablaDatos.getColumnModel().getColumn(0).setPreferredWidth(140);
            tablaDatos.getColumnModel().getColumn(1).setPreferredWidth(140);
            tablaDatos.getColumnModel().getColumn(2).setPreferredWidth(140);
            tablaDatos.getColumnModel().getColumn(3).setPreferredWidth(140);
            tablaDatos.getColumnModel().getColumn(4).setPreferredWidth(144);
            tablaDatos.getColumnModel().getColumn(5).setPreferredWidth(144);
            tablaDatos.getColumnModel().getColumn(6).setPreferredWidth(140);
            tablaDatos.getColumnModel().getColumn(7).setPreferredWidth(140);
            tablaDatos.getColumnModel().getColumn(8).setPreferredWidth(140);
            tablaDatos.getColumnModel().getColumn(9).setPreferredWidth(144);
        }

        divisionResultados.setBottomComponent(scrollDatos);

        add(divisionResultados, java.awt.BorderLayout.CENTER);

        lblEstado.setText("Selecciona los filtros y genera el reporte.");
        lblEstado.setForeground(new java.awt.Color(30, 41, 59));
        lblEstado.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        add(lblEstado, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.titanops.vista.componentes.Boton btnExportar;
    private com.titanops.vista.componentes.Boton btnGenerar;
    private com.titanops.vista.componentes.Boton btnLimpiar;
    private com.titanops.vista.componentes.Selector<String> cmbEstadoOperador;
    private com.titanops.vista.componentes.Selector<String> cmbTurno;
    private javax.swing.JSplitPane divisionResultados;
    private com.toedter.calendar.JDateChooser fechaFin;
    private com.toedter.calendar.JDateChooser fechaInicio;
    private javax.swing.JPanel filtrocmbEstadoOperador;
    private javax.swing.JPanel filtrocmbTurno;
    private javax.swing.JPanel filtrofechaFin;
    private javax.swing.JPanel filtrofechaInicio;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblGrafico1;
    private javax.swing.JLabel lblKpi1;
    private javax.swing.JLabel lblKpi2;
    private javax.swing.JLabel lblKpi3;
    private javax.swing.JLabel lblKpi4;
    private javax.swing.JLabel lblKpi5;
    private javax.swing.JLabel lblNombreKpi1;
    private javax.swing.JLabel lblNombreKpi2;
    private javax.swing.JLabel lblNombreKpi3;
    private javax.swing.JLabel lblNombreKpi4;
    private javax.swing.JLabel lblNombreKpi5;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblcmbEstadoOperador;
    private javax.swing.JLabel lblcmbTurno;
    private javax.swing.JLabel lblfechaFin;
    private javax.swing.JLabel lblfechaInicio;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelCabecera;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelGrafico1;
    private javax.swing.JPanel panelGraficos;
    private javax.swing.JPanel panelResumen;
    private javax.swing.JPanel panelSuperior;
    private javax.swing.JScrollPane scrollDatos;
    private com.titanops.vista.componentes.Tabla tablaDatos;
    private javax.swing.JPanel tarjetaKpi1;
    private javax.swing.JPanel tarjetaKpi2;
    private javax.swing.JPanel tarjetaKpi3;
    private javax.swing.JPanel tarjetaKpi4;
    private javax.swing.JPanel tarjetaKpi5;
    // End of variables declaration//GEN-END:variables
}

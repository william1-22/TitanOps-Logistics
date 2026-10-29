package com.titanops.vista;

/** Vista editable en la pestaña Design de NetBeans. */
public class ReportesAnaliticos extends javax.swing.JPanel {
    private final java.util.List<EnlaceReporteOperativo> enlaces = new java.util.ArrayList<>();
    private boolean iniciada;

    public ReportesAnaliticos() {
        initComponents();
        for (var sp : new javax.swing.JScrollPane[]{
                scrollVista1, scrollVista2, scrollVista3, scrollVista5,
                scrollVista6, scrollVista7, scrollVista8, scrollVista9, scrollVista10}) {
            sp.getVerticalScrollBar().setUnitIncrement(18);
            sp.getHorizontalScrollBar().setUnitIncrement(18);
        }
        if (!java.beans.Beans.isDesignTime()) {
            addAncestorListener(new javax.swing.event.AncestorListener() {
                public void ancestorAdded(javax.swing.event.AncestorEvent e) { iniciar(); }
                public void ancestorRemoved(javax.swing.event.AncestorEvent e) { }
                public void ancestorMoved(javax.swing.event.AncestorEvent e) { }
            });
        }
    }

    void iniciar() {
        if (iniciada) return;
        iniciada = true;
        var controller = new com.titanops.controlador.ReportesOperativosController();
        enlaces.add(vista1.enlazar(controller));
        enlaces.add(vista2.enlazar(controller));
        enlaces.add(vista3.enlazar(controller));
        enlaces.add(vista5.enlazar(controller));
        enlaces.add(vista6.enlazar(controller));
        enlaces.add(vista7.enlazar(controller));
        enlaces.add(vista8.enlazar(controller));
        enlaces.add(vista9.enlazar(controller));
        enlaces.add(vista10.enlazar(controller));
        btnCatalogos.addActionListener(e -> cargarCatalogos());
        cargarCatalogos();
    }

    private void cargarCatalogos() {
        btnCatalogos.setEnabled(false);
        lblCatalogos.setText("Cargando catálogos…");
        enlaces.forEach(e -> e.habilitar(false));
        new javax.swing.SwingWorker<java.util.Map<String, java.util.List<com.titanops.reporte.ReportesDTO.OpcionFiltro>>, Void>() {
            protected java.util.Map<String, java.util.List<com.titanops.reporte.ReportesDTO.OpcionFiltro>> doInBackground() throws Exception {
                return EnlaceReporteOperativo.cargarCatalogos();
            }
            protected void done() {
                try {
                    var catalogos = get();
                    enlaces.forEach(e -> e.aplicarCatalogos(catalogos));
                    lblCatalogos.setText("Catálogos actualizados. Fechas vacías: sin límite de fecha.");
                } catch (Exception ex) {
                    lblCatalogos.setText("No se pudieron cargar los catálogos. Pulsa ACTUALIZAR CATÁLOGOS para reintentar.");
                } finally { btnCatalogos.setEnabled(true); }
            }
        }.execute();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCatalogos = new javax.swing.JPanel();
        btnCatalogos = new com.titanops.vista.componentes.Boton();
        lblCatalogos = new javax.swing.JLabel();
        tabsReportes = new javax.swing.JTabbedPane();
        scrollVista1 = new javax.swing.JScrollPane();
        vista1 = new com.titanops.vista.ReporteMantenimiento();
        scrollVista2 = new javax.swing.JScrollPane();
        vista2 = new com.titanops.vista.ReporteCostoHora();
        scrollVista3 = new javax.swing.JScrollPane();
        vista3 = new com.titanops.vista.ReporteCumplimiento();
        scrollVista5 = new javax.swing.JScrollPane();
        vista5 = new com.titanops.vista.ReporteDisponibilidad();
        scrollVista6 = new javax.swing.JScrollPane();
        vista6 = new com.titanops.vista.ReporteCertificaciones();
        scrollVista7 = new javax.swing.JScrollPane();
        vista7 = new com.titanops.vista.ReporteAuditoria();
        scrollVista8 = new javax.swing.JScrollPane();
        vista8 = new com.titanops.vista.ReporteUtilizacion();
        scrollVista9 = new javax.swing.JScrollPane();
        vista9 = new com.titanops.vista.ReporteHistorialRutas();
        scrollVista10 = new javax.swing.JScrollPane();
        vista10 = new com.titanops.vista.ReporteProductividad();

        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setPreferredSize(new java.awt.Dimension(960, 560));
        setLayout(new java.awt.BorderLayout(8, 8));

        panelCatalogos.setBackground(new java.awt.Color(248, 250, 252));
        panelCatalogos.setForeground(new java.awt.Color(30, 41, 59));
        panelCatalogos.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));

        btnCatalogos.setText("ACTUALIZAR CATÁLOGOS");
        btnCatalogos.setBackground(new java.awt.Color(239, 246, 255));
        btnCatalogos.setForeground(new java.awt.Color(30, 64, 175));
        btnCatalogos.setFondoActivo(new java.awt.Color(37, 99, 235));
        btnCatalogos.setTextoActivo(new java.awt.Color(255, 255, 255));
        btnCatalogos.setColorFoco(new java.awt.Color(37, 99, 235));
        btnCatalogos.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        btnCatalogos.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        panelCatalogos.add(btnCatalogos);

        lblCatalogos.setText("Catálogos pendientes de carga");
        lblCatalogos.setForeground(new java.awt.Color(30, 41, 59));
        lblCatalogos.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        panelCatalogos.add(lblCatalogos);

        add(panelCatalogos, java.awt.BorderLayout.NORTH);

        tabsReportes.setBackground(new java.awt.Color(239, 246, 255));
        tabsReportes.setForeground(new java.awt.Color(30, 64, 175));
        tabsReportes.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabsReportes.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N

        scrollVista1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollVista1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollVista1.setViewportView(vista1);

        tabsReportes.addTab("1. Mantenimiento", scrollVista1);

        scrollVista2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollVista2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollVista2.setViewportView(vista2);

        tabsReportes.addTab("2. Costo por hora", scrollVista2);

        scrollVista3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollVista3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollVista3.setViewportView(vista3);

        tabsReportes.addTab("3. Puntualidad", scrollVista3);

        scrollVista5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollVista5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollVista5.setViewportView(vista5);

        tabsReportes.addTab("5. Disponibilidad", scrollVista5);

        scrollVista6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollVista6.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollVista6.setViewportView(vista6);

        tabsReportes.addTab("6. Certificaciones", scrollVista6);

        scrollVista7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollVista7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollVista7.setViewportView(vista7);

        tabsReportes.addTab("7. Auditoría", scrollVista7);

        scrollVista8.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollVista8.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollVista8.setViewportView(vista8);

        tabsReportes.addTab("8. Utilización", scrollVista8);

        scrollVista9.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollVista9.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollVista9.setViewportView(vista9);

        tabsReportes.addTab("9. Historial de rutas", scrollVista9);

        scrollVista10.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollVista10.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollVista10.setViewportView(vista10);

        tabsReportes.addTab("10. Productividad", scrollVista10);

        add(tabsReportes, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.titanops.vista.componentes.Boton btnCatalogos;
    private javax.swing.JLabel lblCatalogos;
    private javax.swing.JPanel panelCatalogos;
    private javax.swing.JScrollPane scrollVista1;
    private javax.swing.JScrollPane scrollVista10;
    private javax.swing.JScrollPane scrollVista2;
    private javax.swing.JScrollPane scrollVista3;
    private javax.swing.JScrollPane scrollVista5;
    private javax.swing.JScrollPane scrollVista6;
    private javax.swing.JScrollPane scrollVista7;
    private javax.swing.JScrollPane scrollVista8;
    private javax.swing.JScrollPane scrollVista9;
    private javax.swing.JTabbedPane tabsReportes;
    private com.titanops.vista.ReporteMantenimiento vista1;
    private com.titanops.vista.ReporteProductividad vista10;
    private com.titanops.vista.ReporteCostoHora vista2;
    private com.titanops.vista.ReporteCumplimiento vista3;
    private com.titanops.vista.ReporteDisponibilidad vista5;
    private com.titanops.vista.ReporteCertificaciones vista6;
    private com.titanops.vista.ReporteAuditoria vista7;
    private com.titanops.vista.ReporteUtilizacion vista8;
    private com.titanops.vista.ReporteHistorialRutas vista9;
    // End of variables declaration//GEN-END:variables
}

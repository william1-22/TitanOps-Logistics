package com.titanops.vista;

/** Vista editable en la pestaña Design de NetBeans. */
public class ReportesAnaliticos extends javax.swing.JPanel {
    private final java.util.List<EnlaceReporteOperativo> enlaces = new java.util.ArrayList<>();
    private boolean iniciada;

    public ReportesAnaliticos() {
        initComponents();
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
        vista1 = new com.titanops.vista.ReporteMantenimiento();
        vista2 = new com.titanops.vista.ReporteCostoHora();
        vista3 = new com.titanops.vista.ReporteCumplimiento();
        vista5 = new com.titanops.vista.ReporteDisponibilidad();
        vista6 = new com.titanops.vista.ReporteCertificaciones();
        vista7 = new com.titanops.vista.ReporteAuditoria();
        vista8 = new com.titanops.vista.ReporteUtilizacion();
        vista9 = new com.titanops.vista.ReporteHistorialRutas();
        vista10 = new com.titanops.vista.ReporteProductividad();

        setBackground(new java.awt.Color(239, 246, 255));
        setForeground(new java.awt.Color(30, 41, 59));
        setPreferredSize(new java.awt.Dimension(1160, 760));
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
        btnCatalogos.setFont(new java.awt.Font("SansSerif", 1, 12));
        btnCatalogos.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(125, 211, 252)), javax.swing.BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        btnCatalogos.setEnabled(true);
        panelCatalogos.add(btnCatalogos);
        lblCatalogos.setText("Catálogos pendientes de carga");
        lblCatalogos.setForeground(new java.awt.Color(30, 41, 59));
        lblCatalogos.setFont(new java.awt.Font("SansSerif", 0, 12));
        panelCatalogos.add(lblCatalogos);
        add(panelCatalogos, java.awt.BorderLayout.NORTH);
        tabsReportes.setTabLayoutPolicy(1);
        tabsReportes.setBackground(new java.awt.Color(239, 246, 255));
        tabsReportes.setForeground(new java.awt.Color(30, 64, 175));
        tabsReportes.setFont(new java.awt.Font("SansSerif", 1, 12));
        tabsReportes.addTab("1. Mantenimiento", vista1);
        tabsReportes.addTab("2. Costo por hora", vista2);
        tabsReportes.addTab("3. Puntualidad", vista3);
        tabsReportes.addTab("5. Disponibilidad", vista5);
        tabsReportes.addTab("6. Certificaciones", vista6);
        tabsReportes.addTab("7. Auditoría", vista7);
        tabsReportes.addTab("8. Utilización", vista8);
        tabsReportes.addTab("9. Historial de rutas", vista9);
        tabsReportes.addTab("10. Productividad", vista10);
        add(tabsReportes, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panelCatalogos;
    private com.titanops.vista.componentes.Boton btnCatalogos;
    private javax.swing.JLabel lblCatalogos;
    private javax.swing.JTabbedPane tabsReportes;
    private com.titanops.vista.ReporteMantenimiento vista1;
    private com.titanops.vista.ReporteCostoHora vista2;
    private com.titanops.vista.ReporteCumplimiento vista3;
    private com.titanops.vista.ReporteDisponibilidad vista5;
    private com.titanops.vista.ReporteCertificaciones vista6;
    private com.titanops.vista.ReporteAuditoria vista7;
    private com.titanops.vista.ReporteUtilizacion vista8;
    private com.titanops.vista.ReporteHistorialRutas vista9;
    private com.titanops.vista.ReporteProductividad vista10;
    // End of variables declaration//GEN-END:variables
}

package com.titanops.vista;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.titanops.controlador.GestionControlReportesController;
import com.titanops.dao.MantenimientoDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.modelo.Mantenimiento;
import com.titanops.modelo.Maquinaria;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class ControlReportesTest {

    static {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void construyeYRenderizaLaVistaSinConsultarLaBaseReal() {
        assertDoesNotThrow(() -> SwingUtilities.invokeAndWait(() -> {
            GestionControlReportesController controller =
                    new GestionControlReportesController(
                            new MantenimientoDAOVacio(), new MaquinariaDAOVacia());
            Control_y_reportes vista =
                    new Control_y_reportes(null, 1, controller);
            vista.setSize(1160, 760);
            vista.doLayout();

            BufferedImage imagen = new BufferedImage(
                    1160, 760, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = imagen.createGraphics();
            try {
                vista.printAll(graphics);
            } finally {
                graphics.dispose();
            }

            assertEquals("Control operativo y reportes", vista.getTitle());
            assertEquals(BorderLayout.class,
                    vista.getContentPane().getLayout().getClass());
        }));
    }

    private static class MantenimientoDAOVacio extends MantenimientoDAO {
        @Override
        public List<Mantenimiento> listarTodos() {
            return List.of();
        }
    }

    private static class MaquinariaDAOVacia extends MaquinariaDAO {
        @Override
        public List<Maquinaria> listarTodos() {
            return List.of();
        }

        @Override
        public List<Maquinaria> listarPorEstado(String estado) {
            return List.of();
        }
    }
}

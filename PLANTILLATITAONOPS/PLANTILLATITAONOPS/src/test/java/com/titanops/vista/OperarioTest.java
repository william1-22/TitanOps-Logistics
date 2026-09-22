package com.titanops.vista;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.titanops.controlador.GestionOperadoresController;
import com.titanops.dao.OperadorDAO;
import com.titanops.modelo.Operador;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class OperarioTest {

    static {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void construyeYRenderizaLaVistaSinConsultarLaBaseReal() {
        assertDoesNotThrow(() -> SwingUtilities.invokeAndWait(() -> {
            var controller = new GestionOperadoresController(new OperadorDAOVacio());
            Operario vista = new Operario(controller);
            vista.setSize(900, 650);
            vista.doLayout();

            BufferedImage imagen = new BufferedImage(
                    900, 650, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = imagen.createGraphics();
            try {
                vista.printAll(graphics);
            } finally {
                graphics.dispose();
            }

            assertEquals(BorderLayout.class, vista.getLayout().getClass());
        }));
    }

    private static class OperadorDAOVacio extends OperadorDAO {
        @Override
        public List<Operador> listarTodos() {
            return List.of();
        }
    }
}

package com.titanops.vista;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.titanops.controlador.GestionMaquinariaController;
import com.titanops.dao.CategoriaMaquinariaDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.modelo.CategoriaMaquinaria;
import com.titanops.modelo.Maquinaria;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class MaquinariaGestionTest {

    static {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void construyeYRenderizaLaVistaSinConsultarLaBaseReal() {
        assertDoesNotThrow(() -> SwingUtilities.invokeAndWait(() -> {
            var controller = new GestionMaquinariaController(
                    new MaquinariaDAOVacio(), new CategoriaDAOFalsa());
            MAQUINARIAGESTION vista = new MAQUINARIAGESTION(null, controller);
            vista.setSize(1120, 700);
            vista.doLayout();

            BufferedImage imagen = new BufferedImage(
                    1120, 700, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = imagen.createGraphics();
            try {
                vista.printAll(graphics);
            } finally {
                graphics.dispose();
            }

            assertEquals(BorderLayout.class,
                    vista.getContentPane().getLayout().getClass());
        }));
    }

    private static class MaquinariaDAOVacio extends MaquinariaDAO {
        @Override
        public List<Maquinaria> listarTodos() {
            return List.of();
        }
    }

    private static class CategoriaDAOFalsa extends CategoriaMaquinariaDAO {
        @Override
        public List<CategoriaMaquinaria> listarTodos() {
            return List.of(new CategoriaMaquinaria(
                    1, "Maquinaria pesada", "Prueba", true));
        }
    }
}

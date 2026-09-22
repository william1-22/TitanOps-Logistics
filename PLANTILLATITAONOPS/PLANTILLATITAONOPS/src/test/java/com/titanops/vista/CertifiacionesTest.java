package com.titanops.vista;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.titanops.controlador.GestionCertificacionesController;
import com.titanops.dao.CategoriaMaquinariaDAO;
import com.titanops.dao.OperadorCertificacionDAO;
import com.titanops.dao.OperadorDAO;
import com.titanops.modelo.CategoriaMaquinaria;
import com.titanops.modelo.Operador;
import com.titanops.modelo.OperadorCertificacion;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.util.List;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class CertifiacionesTest {

    static {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void construyeYRenderizaLaVistaSinConsultarLaBaseReal() {
        assertDoesNotThrow(() -> SwingUtilities.invokeAndWait(() -> {
            GestionCertificacionesController controller =
                    new GestionCertificacionesController(
                            new CertificacionDAOVacio(), new OperadorDAOFalso(),
                            new CategoriaDAOFalsa());
            Certifiaciones vista = new Certifiaciones(controller);
            vista.setSize(980, 650);
            vista.doLayout();

            BufferedImage imagen = new BufferedImage(
                    980, 650, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = imagen.createGraphics();
            try {
                vista.printAll(graphics);
            } finally {
                graphics.dispose();
            }

            assertEquals(BorderLayout.class, vista.getLayout().getClass());
        }));
    }

    private static class CertificacionDAOVacio extends OperadorCertificacionDAO {
        @Override
        public List<OperadorCertificacion> listarTodos() {
            return List.of();
        }
    }

    private static class OperadorDAOFalso extends OperadorDAO {
        @Override
        public List<Operador> listarTodos() {
            return List.of(new Operador(1, "Ana", "Pérez", "01234567-8",
                    "PESADA", "DIURNO", null, "DISPONIBLE", true,
                    Timestamp.valueOf("2026-09-22 08:00:00")));
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

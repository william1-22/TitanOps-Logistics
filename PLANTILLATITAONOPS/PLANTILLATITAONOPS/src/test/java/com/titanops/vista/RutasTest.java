package com.titanops.vista;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.titanops.controlador.GestionAsignacionesController;
import com.titanops.controlador.GestionRutasController;
import com.titanops.dao.AsignacionDAO;
import com.titanops.dao.MaquinariaDAO;
import com.titanops.dao.OperadorDAO;
import com.titanops.dao.RutaDestinoDAO;
import com.titanops.modelo.Asignacion;
import com.titanops.modelo.Maquinaria;
import com.titanops.modelo.Operador;
import com.titanops.modelo.RutaDestino;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class RutasTest {

    static {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void construyeYRenderizaLasVistasSinConsultarLaBaseReal() {
        assertDoesNotThrow(() -> SwingUtilities.invokeAndWait(() -> {
            RutaDAOVacio rutaDAO = new RutaDAOVacio();
            GestionRutasController rutasController =
                    new GestionRutasController(rutaDAO);
            GestionAsignacionesController asignacionesController =
                    new GestionAsignacionesController(new AsignacionDAOVacio(),
                            new MaquinariaDAOVacia(), new OperadorDAOVacio(), rutaDAO);

            Rutas vista = new Rutas(1, rutasController, asignacionesController);
            vista.setSize(1120, 760);
            renderizar(vista);
            assertEquals("Gestión de rutas y asignaciones", vista.getTitle());

            renderizar(new panelCrear(rutasController));
            renderizar(new panelEditar(rutasController));
            renderizar(new panelAsignar(1, asignacionesController));
        }));
    }

    private static void renderizar(java.awt.Component componente) {
        componente.setSize(1120, 700);
        if (componente instanceof JPanel panel) {
            panel.doLayout();
        }
        BufferedImage imagen = new BufferedImage(1120, 700,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = imagen.createGraphics();
        try {
            componente.printAll(graphics);
        } finally {
            graphics.dispose();
        }
    }

    private static class RutaDAOVacio extends RutaDestinoDAO {
        @Override
        public List<RutaDestino> listarTodos() {
            return List.of();
        }

        @Override
        public List<RutaDestino> listarAsignables() {
            return List.of();
        }
    }

    private static class AsignacionDAOVacio extends AsignacionDAO {
        @Override
        public List<Asignacion> listarTodos() {
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

    private static class OperadorDAOVacio extends OperadorDAO {
        @Override
        public List<Operador> listarTodos() {
            return List.of();
        }

        @Override
        public List<Operador> listarActivos() {
            return List.of();
        }
    }
}

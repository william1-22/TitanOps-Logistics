package com.titanops.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.titanops.dao.RolDAO;
import com.titanops.dao.UsuarioDAO;
import com.titanops.modelo.Rol;
import com.titanops.modelo.Usuario;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GestionUsuariosControllerTest {

    @Test
    void creaUsuarioConDatosNormalizadosYRolReal() {
        UsuarioDAODoble usuarioDAO = new UsuarioDAODoble();
        RolDAODoble rolDAO = new RolDAODoble();
        GestionUsuariosController controller =
                new GestionUsuariosController(usuarioDAO, rolDAO);

        var resultado = controller.crearUsuario(
                "  Ana   Pérez  ", " Ana.Admin ", "segura123".toCharArray(),
                "ADMINISTRADOR");

        assertTrue(resultado.exitoso());
        assertNotNull(usuarioDAO.insertado);
        assertEquals("Ana Pérez", usuarioDAO.insertado.getNombreCompleto());
        assertEquals("ana.admin", usuarioDAO.insertado.getUsername());
        assertEquals(1, usuarioDAO.insertado.getIdRol());
        assertTrue(usuarioDAO.insertado.getActivo());
    }

    @Test
    void rechazaUsernameDuplicado() {
        UsuarioDAODoble usuarioDAO = new UsuarioDAODoble();
        usuarioDAO.duplicado = true;
        GestionUsuariosController controller =
                new GestionUsuariosController(usuarioDAO, new RolDAODoble());

        var resultado = controller.crearUsuario(
                "Ana Pérez", "ana", "segura123".toCharArray(), "ADMINISTRADOR");

        assertFalse(resultado.exitoso());
        assertEquals("El nombre de usuario ya está registrado.", resultado.mensaje());
    }

    @Test
    void listaUsuariosConElNombreDelRolSinExponerLaClave() {
        UsuarioDAODoble usuarioDAO = new UsuarioDAODoble();
        usuarioDAO.usuarios = List.of(new Usuario(
                7, 1, "Ana Pérez", "ana", "hash-no-visible", true,
                Timestamp.valueOf("2026-09-21 10:15:00")));
        GestionUsuariosController controller =
                new GestionUsuariosController(usuarioDAO, new RolDAODoble());

        var filas = controller.listarUsuarios();

        assertEquals(1, filas.size());
        assertEquals("ADMINISTRADOR", filas.getFirst().rol());
        assertEquals("ana", filas.getFirst().username());
    }

    @Test
    void actualizaDatosSinCambiarLaClaveCuandoQuedaVacia() {
        UsuarioDAODoble usuarioDAO = new UsuarioDAODoble();
        usuarioDAO.usuarioPorId = usuario(7, 1, true);
        GestionUsuariosController controller =
                new GestionUsuariosController(usuarioDAO, new RolDAODoble());

        var resultado = controller.actualizarUsuario(
                7, "Ana Actualizada", "ANA.NUEVA", new char[0],
                "ADMINISTRADOR", 7);

        assertTrue(resultado.exitoso());
        assertEquals("ana.nueva", usuarioDAO.actualizado.getUsername());
        assertEquals(null, usuarioDAO.nuevaClave);
        assertEquals(7, usuarioDAO.idExcluidoEnDuplicado);
    }

    @Test
    void impideDesactivarLaPropiaSesion() {
        UsuarioDAODoble usuarioDAO = new UsuarioDAODoble();
        usuarioDAO.usuarioPorId = usuario(7, 1, true);
        GestionUsuariosController controller =
                new GestionUsuariosController(usuarioDAO, new RolDAODoble());

        var resultado = controller.cambiarEstadoUsuario(7, false, 7);

        assertFalse(resultado.exitoso());
        assertFalse(usuarioDAO.desactivado);
    }

    @Test
    void impideDesactivarAlUltimoAdministradorActivo() {
        UsuarioDAODoble usuarioDAO = new UsuarioDAODoble();
        usuarioDAO.usuarioPorId = usuario(7, 1, true);
        usuarioDAO.usuariosActivos = List.of(usuarioDAO.usuarioPorId);
        GestionUsuariosController controller =
                new GestionUsuariosController(usuarioDAO, new RolDAODoble());

        var resultado = controller.cambiarEstadoUsuario(7, false, 99);

        assertFalse(resultado.exitoso());
        assertEquals("Debe permanecer al menos un administrador activo.",
                resultado.mensaje());
        assertFalse(usuarioDAO.desactivado);
    }

    @Test
    void reactivaUnUsuarioInactivo() {
        UsuarioDAODoble usuarioDAO = new UsuarioDAODoble();
        usuarioDAO.usuarioPorId = usuario(8, 2, false);
        GestionUsuariosController controller =
                new GestionUsuariosController(usuarioDAO, new RolDAODoble());

        var resultado = controller.cambiarEstadoUsuario(8, true, 7);

        assertTrue(resultado.exitoso());
        assertTrue(usuarioDAO.reactivado);
    }

    private static Usuario usuario(int idUsuario, int idRol, boolean activo) {
        return new Usuario(idUsuario, idRol, "Ana Pérez", "ana", "hash", activo,
                Timestamp.valueOf("2026-09-21 10:15:00"));
    }

    private static class UsuarioDAODoble extends UsuarioDAO {
        private boolean duplicado;
        private Usuario insertado;
        private Usuario actualizado;
        private Usuario usuarioPorId;
        private String nuevaClave;
        private Integer idExcluidoEnDuplicado;
        private boolean desactivado;
        private boolean reactivado;
        private List<Usuario> usuarios = List.of();
        private List<Usuario> usuariosActivos = List.of();

        @Override
        public boolean existeUsername(String username, Integer idUsuarioExcluido) {
            idExcluidoEnDuplicado = idUsuarioExcluido;
            return duplicado;
        }

        @Override
        public boolean insertarConClave(Usuario usuario, String clavePlana) {
            insertado = usuario;
            usuario.setIdUsuario(10);
            return true;
        }

        @Override
        public List<Usuario> listarTodos() {
            return usuarios;
        }

        @Override
        public Usuario obtenerPorId(int id) {
            return usuarioPorId;
        }

        @Override
        public boolean actualizarDatosYClave(Usuario usuario, String nuevaClavePlana) {
            actualizado = usuario;
            nuevaClave = nuevaClavePlana;
            return true;
        }

        @Override
        public List<Usuario> listarActivos() {
            return usuariosActivos;
        }

        @Override
        public boolean eliminar(int id) {
            desactivado = true;
            return true;
        }

        @Override
        public boolean reactivar(int id) {
            reactivado = true;
            return true;
        }
    }

    private static class RolDAODoble extends RolDAO {
        private final Rol administrador =
                new Rol(1, "ADMINISTRADOR", "Acceso total");
        private final Rol operador =
                new Rol(2, "OPERADOR_DESPACHO", "Acceso operativo");

        @Override
        public Optional<Rol> buscarPorNombre(String nombreRol) {
            if ("ADMINISTRADOR".equals(nombreRol)) {
                return Optional.of(administrador);
            }
            if ("OPERADOR_DESPACHO".equals(nombreRol)) {
                return Optional.of(operador);
            }
            return Optional.empty();
        }

        @Override
        public List<Rol> listarTodos() {
            return List.of(administrador, operador);
        }

        @Override
        public Rol obtenerPorId(int id) {
            return id == 1 ? administrador : id == 2 ? operador : null;
        }
    }
}

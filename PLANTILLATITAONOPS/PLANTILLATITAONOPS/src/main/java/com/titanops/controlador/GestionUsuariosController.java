package com.titanops.controlador;

import com.titanops.dao.RolDAO;
import com.titanops.dao.UsuarioDAO;
import com.titanops.modelo.Rol;
import com.titanops.modelo.Usuario;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Coordina las operaciones de las pantallas de gestión de usuarios. */
public class GestionUsuariosController {
    private final UsuarioDAO usuarioDAO;
    private final RolDAO rolDAO;

    public GestionUsuariosController(UsuarioDAO usuarioDAO, RolDAO rolDAO) {
        this.usuarioDAO = usuarioDAO;
        this.rolDAO = rolDAO;
    }

    public List<Rol> listarRoles() {
        return rolDAO.listarTodos();
    }

    public ResultadoCreacion crearUsuario(String nombreCompleto, String username,
                                           char[] clave, String nombreRol) {
        String nombreNormalizado = normalizarTexto(nombreCompleto);
        String usernameNormalizado = normalizarUsername(username);
        String errorDatos = validarDatos(nombreNormalizado, usernameNormalizado, nombreRol);

        if (errorDatos != null) {
            return ResultadoCreacion.error(errorDatos);
        }
        if (!claveValida(clave, false)) {
            return ResultadoCreacion.error("La contraseña debe tener al menos 8 caracteres.");
        }

        Optional<Rol> rol = rolDAO.buscarPorNombre(nombreRol.trim());
        if (rol.isEmpty()) {
            return ResultadoCreacion.error("El rol seleccionado ya no está disponible.");
        }
        if (usuarioDAO.existeUsername(usernameNormalizado, null)) {
            return ResultadoCreacion.error("El nombre de usuario ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setIdRol(rol.get().getIdRol());
        usuario.setNombreCompleto(nombreNormalizado);
        usuario.setUsername(usernameNormalizado);
        usuario.setActivo(true);

        String clavePlana = new String(clave);
        try {
            if (!usuarioDAO.insertarConClave(usuario, clavePlana)) {
                return ResultadoCreacion.error("No fue posible guardar el usuario.");
            }
        } finally {
            clavePlana = null;
        }

        return ResultadoCreacion.exito(usuario);
    }

    public Optional<UsuarioEdicion> obtenerUsuarioEdicion(int idUsuario) {
        Usuario usuario = usuarioDAO.obtenerPorId(idUsuario);
        if (usuario == null) {
            return Optional.empty();
        }
        Rol rol = rolDAO.obtenerPorId(usuario.getIdRol());
        if (rol == null) {
            return Optional.empty();
        }
        return Optional.of(new UsuarioEdicion(
                usuario.getIdUsuario(), usuario.getNombreCompleto(), usuario.getUsername(),
                rol.getNombreRol(), Boolean.TRUE.equals(usuario.getActivo())));
    }

    public ResultadoOperacion actualizarUsuario(int idUsuario, String nombreCompleto,
                                                 String username, char[] nuevaClave,
                                                 String nombreRol,
                                                 Integer idUsuarioSesion) {
        String nombreNormalizado = normalizarTexto(nombreCompleto);
        String usernameNormalizado = normalizarUsername(username);
        String errorDatos = validarDatos(nombreNormalizado, usernameNormalizado, nombreRol);
        if (errorDatos != null) {
            return ResultadoOperacion.error(errorDatos);
        }
        if (nuevaClave != null && nuevaClave.length > 0 && !claveValida(nuevaClave, true)) {
            return ResultadoOperacion.error(
                    "La nueva contraseña debe tener al menos 8 caracteres.");
        }

        Usuario usuarioActual = usuarioDAO.obtenerPorId(idUsuario);
        if (usuarioActual == null) {
            return ResultadoOperacion.error("El usuario seleccionado ya no existe.");
        }
        Optional<Rol> nuevoRol = rolDAO.buscarPorNombre(nombreRol.trim());
        if (nuevoRol.isEmpty()) {
            return ResultadoOperacion.error("El rol seleccionado ya no está disponible.");
        }
        if (usuarioDAO.existeUsername(usernameNormalizado, idUsuario)) {
            return ResultadoOperacion.error("El nombre de usuario ya está registrado.");
        }
        if (idUsuarioSesion != null && idUsuarioSesion == idUsuario
                && usuarioActual.getIdRol() != nuevoRol.get().getIdRol()) {
            return ResultadoOperacion.error("No puedes cambiar el rol de tu propia sesión.");
        }
        if (Boolean.TRUE.equals(usuarioActual.getActivo())
                && esAdministrador(usuarioActual.getIdRol())
                && !esAdministrador(nuevoRol.get().getIdRol())
                && esUltimoAdministradorActivo()) {
            return ResultadoOperacion.error(
                    "Debe permanecer al menos un administrador activo.");
        }

        Usuario actualizado = new Usuario();
        actualizado.setIdUsuario(idUsuario);
        actualizado.setIdRol(nuevoRol.get().getIdRol());
        actualizado.setNombreCompleto(nombreNormalizado);
        actualizado.setUsername(usernameNormalizado);
        actualizado.setActivo(usuarioActual.getActivo());

        String clavePlana = nuevaClave == null || nuevaClave.length == 0
                ? null : new String(nuevaClave);
        try {
            if (!usuarioDAO.actualizarDatosYClave(actualizado, clavePlana)) {
                return ResultadoOperacion.error("No fue posible actualizar el usuario.");
            }
        } finally {
            clavePlana = null;
        }
        return ResultadoOperacion.exito("Usuario actualizado correctamente.");
    }

    public ResultadoOperacion cambiarEstadoUsuario(int idUsuario, boolean activar,
                                                    Integer idUsuarioSesion) {
        Usuario usuario = usuarioDAO.obtenerPorId(idUsuario);
        if (usuario == null) {
            return ResultadoOperacion.error("El usuario seleccionado ya no existe.");
        }

        boolean activo = Boolean.TRUE.equals(usuario.getActivo());
        if (activar && activo) {
            return ResultadoOperacion.exito("El usuario ya estaba activo.");
        }
        if (!activar && !activo) {
            return ResultadoOperacion.exito("El usuario ya estaba inactivo.");
        }
        if (!activar && idUsuarioSesion != null && idUsuarioSesion == idUsuario) {
            return ResultadoOperacion.error("No puedes desactivar tu propia sesión.");
        }
        if (!activar && esAdministrador(usuario.getIdRol())
                && esUltimoAdministradorActivo()) {
            return ResultadoOperacion.error(
                    "Debe permanecer al menos un administrador activo.");
        }

        boolean actualizado = activar
                ? usuarioDAO.reactivar(idUsuario) : usuarioDAO.eliminar(idUsuario);
        if (!actualizado) {
            return ResultadoOperacion.error(
                    activar ? "No fue posible reactivar el usuario."
                            : "No fue posible desactivar el usuario.");
        }
        return ResultadoOperacion.exito(
                activar ? "Usuario reactivado correctamente."
                        : "Usuario desactivado correctamente.");
    }

    public List<UsuarioFila> listarUsuarios() {
        Map<Integer, String> rolesPorId = new HashMap<>();
        for (Rol rol : rolDAO.listarTodos()) {
            rolesPorId.put(rol.getIdRol(), rol.getNombreRol());
        }

        List<UsuarioFila> filas = new ArrayList<>();
        for (Usuario usuario : usuarioDAO.listarTodos()) {
            filas.add(new UsuarioFila(
                    usuario.getIdUsuario(),
                    usuario.getNombreCompleto(),
                    usuario.getUsername(),
                    rolesPorId.getOrDefault(usuario.getIdRol(), "ROL DESCONOCIDO"),
                    Boolean.TRUE.equals(usuario.getActivo()),
                    usuario.getFechaCreacion()));
        }
        return filas;
    }

    private String validarDatos(String nombreCompleto, String username, String nombreRol) {
        if (nombreCompleto.isEmpty()) {
            return "Ingresa el nombre completo.";
        }
        if (username.length() < 3) {
            return "El usuario debe tener al menos 3 caracteres.";
        }
        if (!username.matches("[a-z0-9._-]+")) {
            return "El usuario solo puede contener letras, números, punto, guion o guion bajo.";
        }
        if (nombreRol == null || nombreRol.isBlank()) {
            return "Selecciona un rol.";
        }
        return null;
    }

    private boolean claveValida(char[] clave, boolean permiteVacia) {
        if (clave == null || clave.length == 0) {
            return permiteVacia;
        }
        if (clave.length < 8) {
            return false;
        }
        for (char caracter : clave) {
            if (!Character.isWhitespace(caracter)) {
                return true;
            }
        }
        return false;
    }

    private boolean esAdministrador(int idRol) {
        Optional<Rol> administrador = rolDAO.buscarPorNombre("ADMINISTRADOR");
        return administrador.isPresent() && administrador.get().getIdRol() == idRol;
    }

    private boolean esUltimoAdministradorActivo() {
        Optional<Rol> administrador = rolDAO.buscarPorNombre("ADMINISTRADOR");
        if (administrador.isEmpty()) {
            return true;
        }
        int idRolAdministrador = administrador.get().getIdRol();
        long administradoresActivos = usuarioDAO.listarActivos().stream()
                .filter(usuario -> usuario.getIdRol() == idRolAdministrador)
                .count();
        return administradoresActivos <= 1;
    }

    private String normalizarTexto(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }

    private String normalizarUsername(String valor) {
        return normalizarTexto(valor).toLowerCase(Locale.ROOT);
    }

    public record ResultadoCreacion(boolean exitoso, String mensaje, Usuario usuario) {
        public static ResultadoCreacion exito(Usuario usuario) {
            return new ResultadoCreacion(true, "Usuario creado correctamente.", usuario);
        }

        public static ResultadoCreacion error(String mensaje) {
            return new ResultadoCreacion(false, mensaje, null);
        }
    }

    public record ResultadoOperacion(boolean exitoso, String mensaje) {
        public static ResultadoOperacion exito(String mensaje) {
            return new ResultadoOperacion(true, mensaje);
        }

        public static ResultadoOperacion error(String mensaje) {
            return new ResultadoOperacion(false, mensaje);
        }
    }

    public record UsuarioEdicion(int idUsuario, String nombreCompleto, String username,
                                 String rol, boolean activo) {}

    public record UsuarioFila(int idUsuario, String nombreCompleto, String username,
                              String rol, boolean activo, Timestamp fechaCreacion) {}
}

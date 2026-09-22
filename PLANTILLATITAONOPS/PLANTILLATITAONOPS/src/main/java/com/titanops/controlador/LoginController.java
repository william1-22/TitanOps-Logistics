package com.titanops.controlador;

import com.titanops.dao.RolDAO;
import com.titanops.dao.UsuarioDAO;
import com.titanops.modelo.Rol;
import com.titanops.modelo.Usuario;
import com.titanops.vista.LOGIN;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import javax.swing.SwingWorker;

/** Coordina la vista de login con los DAO de usuarios y roles. */
public class LoginController {
    private final LOGIN vista;
    private final UsuarioDAO usuarioDAO;
    private final RolDAO rolDAO;
    private final BiConsumer<Usuario, Rol> alAutenticar;

    public LoginController(LOGIN vista, UsuarioDAO usuarioDAO, RolDAO rolDAO,
                           BiConsumer<Usuario, Rol> alAutenticar) {
        this.vista = vista;
        this.usuarioDAO = usuarioDAO;
        this.rolDAO = rolDAO;
        this.alAutenticar = alAutenticar;
        conectarEventos();
    }

    private void conectarEventos() {
        vista.alIniciarSesion(event -> autenticar());
        vista.alCancelar(event -> vista.cerrar());
    }

    private void autenticar() {
        String username = vista.obtenerUsername().trim();
        char[] claveCaracteres = vista.obtenerClave();

        if (username.isBlank() || claveCaracteres.length == 0) {
            Arrays.fill(claveCaracteres, '\0');
            vista.mostrarError("Ingresa el usuario y la contraseña.");
            return;
        }

        String clave = new String(claveCaracteres);
        Arrays.fill(claveCaracteres, '\0');
        vista.establecerProcesando(true);

        SwingWorker<Optional<SesionAutenticada>, Void> worker = new SwingWorker<>() {
            @Override
            protected Optional<SesionAutenticada> doInBackground() {
                Optional<Usuario> usuario = usuarioDAO.validarLogin(username, clave);
                if (usuario.isEmpty()) {
                    return Optional.empty();
                }

                Rol rol = rolDAO.obtenerPorId(usuario.get().getIdRol());
                return rol == null
                        ? Optional.empty()
                        : Optional.of(new SesionAutenticada(usuario.get(), rol));
            }

            @Override
            protected void done() {
                vista.establecerProcesando(false);
                try {
                    Optional<SesionAutenticada> sesion = get();
                    if (sesion.isEmpty()) {
                        vista.limpiarClave();
                        vista.mostrarError("Usuario o contraseña incorrectos, o usuario inactivo.");
                        return;
                    }

                    SesionAutenticada autenticada = sesion.get();
                    alAutenticar.accept(autenticada.usuario(), autenticada.rol());
                    vista.cerrar();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    vista.mostrarError("La autenticación fue interrumpida.");
                } catch (ExecutionException exception) {
                    vista.mostrarError("No fue posible validar las credenciales.");
                }
            }
        };

        worker.execute();
    }

    private record SesionAutenticada(Usuario usuario, Rol rol) {}
}

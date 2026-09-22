package com.titanops.seguridad;

import java.nio.charset.StandardCharsets;
import org.mindrot.jbcrypt.BCrypt;

/** Centraliza la generación y verificación de contraseñas con BCrypt. */
public final class PasswordHasher {
    private static final int COSTO_BCRYPT = 12;
    private static final int MAXIMO_BYTES_BCRYPT = 72;

    private PasswordHasher() {}

    public static String generar(String clavePlana) {
        validarClave(clavePlana);
        return BCrypt.hashpw(clavePlana, BCrypt.gensalt(COSTO_BCRYPT));
    }

    public static boolean verificar(String clavePlana, String claveHash) {
        if (!esClaveValida(clavePlana) || claveHash == null || claveHash.isBlank()) {
            return false;
        }

        try {
            return BCrypt.checkpw(clavePlana, claveHash);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static void validarClave(String clavePlana) {
        if (!esClaveValida(clavePlana)) {
            throw new IllegalArgumentException(
                    "La clave debe contener entre 1 y 72 bytes en UTF-8.");
        }
    }

    private static boolean esClaveValida(String clavePlana) {
        return clavePlana != null
                && !clavePlana.isBlank()
                && clavePlana.getBytes(StandardCharsets.UTF_8).length <= MAXIMO_BYTES_BCRYPT;
    }
}

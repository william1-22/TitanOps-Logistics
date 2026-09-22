package com.titanops.seguridad;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {

    @Test
    void generaHashesDistintosYVerificables() {
        String clave = "TitanOps-2026";

        String primerHash = PasswordHasher.generar(clave);
        String segundoHash = PasswordHasher.generar(clave);

        assertNotEquals(primerHash, segundoHash);
        assertTrue(PasswordHasher.verificar(clave, primerHash));
        assertTrue(PasswordHasher.verificar(clave, segundoHash));
    }

    @Test
    void rechazaUnaClaveIncorrectaOUnHashInvalido() {
        String hash = PasswordHasher.generar("clave-correcta");

        assertFalse(PasswordHasher.verificar("clave-incorrecta", hash));
        assertFalse(PasswordHasher.verificar(null, hash));
        assertFalse(PasswordHasher.verificar("clave-correcta", "hash-invalido"));
    }

    @Test
    void noPermiteClavesVaciasONulas() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.generar(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.generar("   "));
    }
}

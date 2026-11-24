package edu.dosw.styleradar.usuarios.domain;

import edu.dosw.styleradar.usuarios.domain.model.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void debeCrearEmailValido() {
        Email email = new Email("usuario@gmail.com");

        assertEquals("usuario@gmail.com", email.valor());
    }

    @Test
    void debeConvertirEmailAMinusculas() {
        Email email = new Email("Usuario@GMAIL.COM");

        assertEquals("usuario@gmail.com", email.valor());
    }

    @Test
    void debeEliminarEspaciosDelEmail() {
        Email email = new Email(" usuario@gmail.com ");

        assertEquals("usuario@gmail.com", email.valor());
    }

    @Test
    void noDebeAceptarEmailVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Email(""));
    }

    @Test
    void noDebeAceptarEmailNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Email(null));
    }

    @Test
    void noDebeAceptarEmailInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new Email("usuario-sin-arroba"));
    }
}

package edu.dosw.styleradar.usuarios.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PublicProfileFormatterTest {

    @ParameterizedTest
    @CsvSource({
            "'Laura Gómez Ruiz', 'Laura G.'",
            "'sofía lópez', 'Sofía L.'",
            "'MARÍA CAMILA PÉREZ', 'María C.'",
            "'Juan', 'Juan'",
            "'  Carlos    Mendoza   ', 'Carlos M.'",
            "'Andrés de la Vega', 'Andrés V.'",
            "'María del Carmen', 'María C.'",
            "'Ángela Gómez', 'Ángela G.'",
            "'óscar lópez', 'Óscar L.'",
            "'Juan de', 'Juan'"
    })
    void debeFormatearNombrePublicoCorrectamente(String input, String expected) {
        assertEquals(expected, PublicProfileFormatter.formatDisplayName(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n", "\u0000\u0007"})
    void debeDevolverNullParaEntradasVaciasONulas(String input) {
        assertNull(PublicProfileFormatter.formatDisplayName(input));
    }

    @Test
    void debeManejarUnSoloCaracter() {
        assertEquals("A", PublicProfileFormatter.formatDisplayName("a"));
    }

    @Test
    void debeSanitizarCaracteresDeControl() {
        assertEquals("Sofía L.", PublicProfileFormatter.formatDisplayName("Sofía\r\nLópez"));
    }
}

package edu.dosw.styleradar.usuarios.domain;

import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TallaTest {

    @Test
    void debeAceptarTallasAlfabeticasParaPrendasSuperiores() {
        assertTrue(Talla.esValidaPara(TipoPrenda.SUPERIOR, "XS"));
        assertTrue(Talla.esValidaPara(TipoPrenda.SUPERIOR, "S"));
        assertTrue(Talla.esValidaPara(TipoPrenda.SUPERIOR, "M"));
        assertTrue(Talla.esValidaPara(TipoPrenda.SUPERIOR, "L"));
        assertTrue(Talla.esValidaPara(TipoPrenda.SUPERIOR, "XL"));
    }

    @Test
    void debeRechazarTallasNoValidasParaPrendasSuperiores() {
        assertFalse(Talla.esValidaPara(TipoPrenda.SUPERIOR, "XXL"));
        assertFalse(Talla.esValidaPara(TipoPrenda.SUPERIOR, "32"));
    }

    @Test
    void debeAceptarTallasNumericasParaPrendasInferiores() {
        assertTrue(Talla.esValidaPara(TipoPrenda.INFERIOR, "26"));
        assertTrue(Talla.esValidaPara(TipoPrenda.INFERIOR, "32"));
        assertTrue(Talla.esValidaPara(TipoPrenda.INFERIOR, "36"));
    }

    @Test
    void debeRechazarTallasFueraDelRangoParaPrendasInferiores() {
        assertFalse(Talla.esValidaPara(TipoPrenda.INFERIOR, "25"));
        assertFalse(Talla.esValidaPara(TipoPrenda.INFERIOR, "37"));
        assertFalse(Talla.esValidaPara(TipoPrenda.INFERIOR, "L"));
    }

    @Test
    void debeAceptarTallasNumericasParaCalzado() {
        assertTrue(Talla.esValidaPara(TipoPrenda.CALZADO, "35"));
        assertTrue(Talla.esValidaPara(TipoPrenda.CALZADO, "40"));
        assertTrue(Talla.esValidaPara(TipoPrenda.CALZADO, "45"));
    }

    @Test
    void debeRechazarTallasFueraDelRangoParaCalzado() {
        assertFalse(Talla.esValidaPara(TipoPrenda.CALZADO, "34"));
        assertFalse(Talla.esValidaPara(TipoPrenda.CALZADO, "46"));
        assertFalse(Talla.esValidaPara(TipoPrenda.CALZADO, "50"));
    }

    @Test
    void debeRechazarTipoDePrendaOTallaNulos() {
        assertFalse(Talla.esValidaPara(null, "M"));
        assertFalse(Talla.esValidaPara(TipoPrenda.SUPERIOR, null));
    }
}
